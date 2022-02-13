package edu.bu.cs622.jlitebox.image;

import edu.bu.cs622.jlitebox.config.ImageCatalogConfiguration;
import edu.bu.cs622.jlitebox.exceptions.ImageImportException;
import edu.bu.cs622.jlitebox.exceptions.JLiteBoxException;
import edu.bu.cs622.jlitebox.filter.FilteredImageContentCollection;
import edu.bu.cs622.jlitebox.filter.ImageContentFilter;
import edu.bu.cs622.jlitebox.image.metadata.ImageMetadataExtractor;
import edu.bu.cs622.jlitebox.image.preview.ImagePreviewGenerator;
import edu.bu.cs622.jlitebox.storage.ImageMetadataStorage;
import edu.bu.cs622.jlitebox.storage.ImageStorage;
import edu.bu.cs622.jlitebox.utils.ImageDownloader;
import io.vavr.Function3;
import io.vavr.Function4;
import io.vavr.control.Try;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * The image catalog
 *
 * @author dlegaspi@bu.edu
 */
public class BasicImageCatalog implements ImageCatalog {
    private static Logger logger = LoggerFactory.getLogger(BasicImageCatalog.class);

    private final ImageStorage imageStorage;
    private final ImageMetadataStorage imageMetadataStorage;
    private final ImageCatalogConfiguration config;
    private final ImageDownloader downloader;
    private final ImageMetadataExtractor metadataExtractor;
    private final ImagePreviewGenerator previewGenerator;

    private ExecutorService threadPool;

    @Inject
    public BasicImageCatalog(ImageStorage imageStorage,
                    ImageMetadataStorage imageMetadataStorage,
                    ImageDownloader downloader,
                    ImageMetadataExtractor metadataExtractor,
                    ImagePreviewGenerator previewGenerator,
                    ImageCatalogConfiguration config) {
        this.imageMetadataStorage = imageMetadataStorage;
        this.imageStorage = imageStorage;
        this.config = config;
        this.downloader = downloader;
        this.metadataExtractor = metadataExtractor;
        this.previewGenerator = previewGenerator;
        this.threadPool = Executors.newFixedThreadPool(config.getImageImportThreads());
    }

    private List<Image> images = Collections.synchronizedList(new ArrayList<>());

    @Override
    public Collection<Image> getImages() {
        return images;
    }

    @Override
    public Collection<Image> getImages(ImageContentFilter filter) {
        return new FilteredImageContentCollection(images).filterWith(filter);
    }

    @Override
    public ImageStorage getStorage() {
        return imageStorage;
    }

    @Override
    public ImageMetadataStorage getMetadataStorage() {
        return imageMetadataStorage;
    }

    @Override
    public ImagePreviewGenerator getPreviewGenerator() {
        return previewGenerator;
    }

    private Image generatePreviewForImage(Image i) throws JLiteBoxException {
        if (i instanceof RawImage)
            ((RawImage) i).generateImagePreview(getPreviewGenerator());
        else if (i instanceof JpegImage)
            ((JpegImage) i).resizeImagePreview(getPreviewGenerator());

        return i;
    }

    protected int addImages(List<Image> newImages,
                    ImageImportOptions options,
                    Function4<Image, String, Integer, Integer, Void> importCallback) throws JLiteBoxException {
        this.images.addAll(newImages);

        // list the types
        newImages.forEach(i -> {
            logger.info("{} is a {}", i.getName(), i.getType());
        });

        // Atomic integer is thread-safe and simple and allows us to use an incrementer
        // without having to deal manually with mutexes, critical sections nor even
        // deal explicitly with synchronized keyword. Also note that each thread here
        // on separate files so there is no need to "lock" resources being mutated.
        AtomicInteger imported = new AtomicInteger(0);
        AtomicInteger metadataExtracted = new AtomicInteger(0);
        AtomicInteger previewGenerated = new AtomicInteger(0);
        try {
            imageStorage.save(newImages);

            // We are using threads here to execute the processing of each file in parallel
            // this benefits parallelism because each generate preview and read metadata
            // operation operates on a different file and each process is expensive and
            // heavy in I/O and having them execute in parallel allows us to be able
            // to efficiently use multiple threads of execution and the import process
            // finishes quicker.
            CompletableFuture.allOf(newImages.stream().map(i -> CompletableFuture
                            // Note that each thread of execution is further broken
                            // down in to 3 threads of execution (one for creating preview, one for reading
                            // metadata, and one to update GUI status bar to allow yielding thread of
                            // execution automagically without having to specify Thread::yield
                            .supplyAsync(() -> Try.of(() -> generatePreviewForImage(i))
                                            .onSuccess(t -> {
                                                if (importCallback != null) {
                                                    importCallback.apply(i, "preview generated",
                                                            previewGenerated.incrementAndGet(), newImages.size());
                                                }
                                            })
                                            .onFailure(t -> logger.warn("Failed to generate preview for {}",
                                                            i.getName())),
                                            threadPool)
                            .thenApplyAsync(ti -> ti.mapTry(metadataExtractor::parse)
                                            .onSuccess(t -> {
                                                if (importCallback != null) {
                                                    importCallback.apply(i, "metadata extracted",
                                                            metadataExtracted.incrementAndGet(), newImages.size());
                                                }
                                            })
                                            .onFailure(t -> logger.warn("Failed to extract metadata for {}",
                                                            i.getName()))
                                            .andThen(mo -> mo.ifPresent(i::setMetadata)),
                                            threadPool)
                            .thenRun(() -> {
                                // this is executed in another thread for updating the UI
                                // but not block the next image to be processed.
                                if (importCallback != null)
                                    importCallback.apply(i, "imported", imported.incrementAndGet(), newImages.size());
                            })).toArray(CompletableFuture[]::new)).join(); // wait for all threads to finish

            // this saves all metadata in the database at this point. SQLite operations are
            // serialized into single thread even if we explicitly multi-thread it so we're
            // not getting any performance advantage to using threads here.
            imageMetadataStorage.saveMetadata(newImages);
        } catch (JLiteBoxException e) {
            if (!options.isIgnoreErrors())
                throw e;
        }

        logger.info("Catalog has {} images", newImages.size());
        return newImages.size();
    }

    @Override
    public int addImagesFromDirectory(String srcDir, ImageImportOptions options,
                    Function4<Image, String, Integer, Integer, Void> importCallback) throws JLiteBoxException {
        logger.info("Adding images from dir {}", srcDir);
        var dir = new File(srcDir);
        var fileFilter = new SuffixFileFilter(config.supportedFileExtensions(), IOCase.INSENSITIVE);
        var newImages = FileUtils.listFiles(dir, fileFilter, null).stream()
                        .map(f -> ImageFactory.fromFile(f.getAbsolutePath()))
                        .collect(Collectors.toList());

        return addImages(newImages, options, importCallback);
    }

    @Override
    public int addImagesFromFile(String path, ImageImportOptions options, Predicate<String> onErrorCallback)
                    throws JLiteBoxException {
        List<Image> newImages = new ArrayList<>();
        int addedImages = 0;
        try {
            var urls = Files.readAllLines(Paths.get(path));
            logger.info("there are {} urls in file {}", urls.size(), path);

            for (var url : urls) {
                try {
                    // pull the image from the url and save it to the temp directory
                    var tmpFilePath = downloader.download(url);
                    newImages.add(ImageFactory.fromFile(tmpFilePath));
                } catch (JLiteBoxException e) {
                    if (!onErrorCallback.test(url))
                        throw e;
                }
            }

            addedImages = addImages(newImages, options, null);
            downloader.cleanup();
        } catch (IOException e) {
            throw new ImageImportException("Error reading the image list file " + path, e);
        } catch (JLiteBoxException e) {
            if (!options.isIgnoreErrors())
                throw new ImageImportException("Error in adding images from " + path, e);
        }

        return addedImages;
    }

    @Override
    public Collection<Image> loadImages(Function3<Image, Integer, Integer, Void> loadCallback)
                    throws JLiteBoxException {
        var names = getMetadataStorage().getImageNames();
        AtomicInteger counter = new AtomicInteger(0);

        // this actually does NOT benefit from parallelism because the data
        // at this point is loaded from the SQLite db and its JDBC default
        // behavior is to serialize all operations in a single thread anyway
        // and the multi-threading is wasted and will actually make this slower
        // because of the context-switching.
        this.images = names.stream().map(name -> Try.of(() -> this.getMetadataStorage().loadImage(name))
                        .onSuccess(mi -> {
                            if (loadCallback != null) {
                                mi.ifPresent(i -> loadCallback.apply(i, counter.incrementAndGet(), names.size()));
                            }
                        }))
                        .filter(Try::isSuccess)
                        .map(Try::get)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList());

        return images;
    }
}
