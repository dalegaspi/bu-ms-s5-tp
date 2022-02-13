package edu.bu.cs622.jlitebox.image;

import edu.bu.cs622.jlitebox.config.BasicImageCatalogConfiguration;
import edu.bu.cs622.jlitebox.config.ImageCatalogConfiguration;
import edu.bu.cs622.jlitebox.exceptions.JLiteBoxException;
import edu.bu.cs622.jlitebox.image.preview.ImagePreviewGenerator;
import edu.bu.cs622.jlitebox.image.preview.LibRawImagePreviewGenerator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.librawfx.RAWImageLoaderFactory;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Image preview generation tests
 *
 * @author dlegaspi@bu.edu
 */
public class ImagePreviewTests extends ApplicationTest {
    static ImagePreviewGenerator previewGenerator;
    static ImageCatalogConfiguration config;

    @BeforeAll
    public static void setUp() throws Exception {
        RAWImageLoaderFactory.install();
        config = new BasicImageCatalogConfiguration();
        previewGenerator = new LibRawImagePreviewGenerator(config);
    }

    @Test
    public void testImagePreviewGenerator() throws JLiteBoxException {
        var path = getClass().getClassLoader().getResource("images/DSC_2304.NEF").getPath();
        var image = ImageFactory.fromFile(path);

        var preview = previewGenerator.generatePreview((RawImage) image);
        assertNotNull(preview);
    }

    @Test
    public void testImageResize() throws JLiteBoxException {
        var path = getClass().getClassLoader().getResource("images/IMAGE-01.JPG").getPath();
        var image = ImageFactory.fromFile(path);

        var preview = previewGenerator.resizePreview((JpegImage) image);
        assertNotNull(preview);
    }
}
