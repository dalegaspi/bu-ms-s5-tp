package edu.bu.cs622.jlitebox.storage;

import edu.bu.cs622.jlitebox.config.ImageCatalogConfiguration;
import edu.bu.cs622.jlitebox.exceptions.ImageImportException;
import edu.bu.cs622.jlitebox.exceptions.JLiteBoxException;
import edu.bu.cs622.jlitebox.image.Image;
import io.vavr.control.Try;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * A mock image storage
 *
 * @see ImageStorage
 * @author dlegaspi@bu.edu
 */
public final class FileImageStorage implements ImageStorage {
    private static final Logger logger = LoggerFactory.getLogger(FileImageStorage.class);

    private final String rootDir;

    /**
     * Constructor
     *
     * @param config the configuration
     */
    @Inject
    public FileImageStorage(ImageCatalogConfiguration config) {
        this.rootDir = config.getRootDirectory();
    }

    @Override
    public boolean save(Collection<Image> images) throws JLiteBoxException {
        logger.info("Destination directory is {}", this.rootDir);
        var destDir = new File(this.rootDir);

        try {
            var oneErrorResult = images.parallelStream().map(image -> {
                logger.info("Copying image {}", image.getOriginalSrcPath());
                var originSrcPath = new File(image.getOriginalSrcPath());
                return Try.run(() -> FileUtils.copyFileToDirectory(originSrcPath, destDir));
            }).filter(Try::isFailure).findAny();

            if (oneErrorResult.isPresent()) {
                throw oneErrorResult.get().getCause();
            }
        } catch (Throwable e) {
            throw new ImageImportException("Error saving images", e);
        }

        return true;
    }

    @Override
    public String getRootDirectory() {
        return rootDir;
    }


}
