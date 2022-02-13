package edu.bu.cs622.jlitebox.image;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Image model tests
 *
 * @author dlegaspi@bu.edu
 */
public class ImageModelTests {
    @Test
    public void imageBasicTest() {
        var image = ImageFactory.fromFile("./hello.jpg");

        assertTrue(image.getFilename().contains("hello.jpg"));
        assertEquals(image.getName(), "hello");
        assertNotNull(image.getMetadata());
        assertFalse(image.hasEquipmentInfo());
        assertNotNull(image);
    }

    @Test
    public void createJpegImageTest() {
        var image = ImageFactory.fromFile("./hello.jpg");

        assertNotNull(image);
        assertInstanceOf(JpegImage.class, image);
        assertTrue(image.getType().toLowerCase().contains("jpg"));
    }

    @Test
    public void createRawImageTest() {
        var image = ImageFactory.fromFile("./hello.dng");

        assertNotNull(image);
        assertTrue(image instanceof RawImage);
        assertTrue(image.getType().toLowerCase().contains("dng"));
    }

    @Test
    public void importOptionsTest() {
        var options = ImageFactory.withDefaultImportOptions();

        assertNotNull(options);
        assertTrue(options.isAndOperation());
        assertTrue(options.isOverwrite());
        assertFalse(options.isIgnoreErrors());
    }
}
