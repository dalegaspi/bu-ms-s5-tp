package edu.bu.cs622.jlitebox.storage;

import edu.bu.cs622.jlitebox.config.ImageCatalogConfiguration;
import edu.bu.cs622.jlitebox.exceptions.JLiteBoxException;
import edu.bu.cs622.jlitebox.filter.ImageContentFilter;
import edu.bu.cs622.jlitebox.image.*;
import edu.bu.cs622.jlitebox.image.metadata.ImageMetadata;
import edu.bu.cs622.jlitebox.image.metadata.ImageMetadataExtractor;
import edu.bu.cs622.jlitebox.image.preview.ImagePreviewGenerator;
import edu.bu.cs622.jlitebox.utils.ImageDownloader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;

/**
 * Image catalog tests
 *
 * @author dlegaspi@bu.edu
 */
@ExtendWith(MockitoExtension.class)
public class ImageCatalogTests {

    @Mock
    private ImageStorage imageStorage;

    @Mock
    private ImageMetadataStorage imageMetadataStorage;

    @Mock
    private ImageDownloader downloader;

    @Mock
    private ImageMetadataExtractor metadataExtractor;

    @Mock
    private ImageCatalogConfiguration config;

    @Mock
    private ImagePreviewGenerator previewGenerator;

    @BeforeEach
    public void beforeEach() throws JLiteBoxException {
        MockitoAnnotations.openMocks(this);
        lenient().when(config.supportedFileExtensions()).thenReturn(List.of("JPG", "DNG"));
        lenient().when(config.getImageImportThreads()).thenReturn(4);
        lenient().when(downloader.download(anyString())).thenReturn("/tmp/hello.jpg");
        lenient().when(metadataExtractor.parse(any())).thenReturn(Optional.of(new ImageMetadata(Map.of())));
        lenient().when(previewGenerator.generatePreview(any())).thenReturn(null);
        lenient().when(previewGenerator.resizePreview(any())).thenReturn(null);
    }

    @Test
    public void testBasicImageCatalogFromDir() throws JLiteBoxException {
        var c = new BasicImageCatalog(imageStorage, imageMetadataStorage, downloader, metadataExtractor, previewGenerator, config);
        var imageDir = getClass().getClassLoader().getResource("images/").getPath();

        assertNotNull(c);
        assertNotNull(c.getStorage());
        assertNotNull(c.getMetadataStorage());
        int count = c.addImagesFromDirectory(imageDir, ImageFactory.withDefaultImportOptions(), (w, x, y, z) -> { return null; });
        assertTrue(count > 0);

        var images = c.getImages();
        assertTrue(images.size() > 0);

        var filter = new ImageContentFilter.ImageContentFilterBuilder(true).withName("IMAGE").build();
        var filteredImages = c.getImages(filter);
        assertTrue(filteredImages.size() > 0);
        assertNotNull(filteredImages.iterator());
    }

    @Test
    public void testBasicImageCatalogFromFile() throws JLiteBoxException {
        var c = new BasicImageCatalog(imageStorage, imageMetadataStorage, downloader, metadataExtractor, previewGenerator, config);
        var imageFile = getClass().getClassLoader().getResource("import.txt").getPath();

        assertNotNull(c);
        assertNotNull(c.getStorage());
        assertNotNull(c.getMetadataStorage());
        int count = c.addImagesFromFile(imageFile, ImageFactory.withDefaultImportOptions(), i -> true);
        assertTrue(count > 0);

        var images = c.getImages();
        assertTrue(images.size() > 0);

        var filter = new ImageContentFilter.ImageContentFilterBuilder(false).withName("hello").build();
        var filteredImages = c.getImages(filter);
        assertTrue(filteredImages.size() > 0);
        assertNotNull(filteredImages.iterator());
    }
}
