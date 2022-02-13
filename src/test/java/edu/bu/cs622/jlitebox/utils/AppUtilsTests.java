package edu.bu.cs622.jlitebox.utils;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for AppUtils
 *
 * @author dlegaspi@bu.edu
 */
class AppUtilsTests {

    @Test
    void testGetBaseFilename() {
        var path = "/tmp/IMAGE-01.JPG";

        var base = AppUtils.getBaseFilename(path);
        assertEquals(base, "IMAGE-01");
    }

    @Test
    void testGetFilenameExt() {
        var path = "/tmp/IMAGE-01.JPG";

        var ext = AppUtils.getFilenameExt(path);
        assertEquals(ext, "JPG");
    }

    @Test
    void testCreatePath() {
        var path = AppUtils.createPath("/tmp/", "hello", "world.jpg");
        assertNotNull(path);
    }

    @Test
    void getFilenameTest() {
        var url = "https://live.staticflickr.com/65535/51815575646_3ec1d92c83_k.jpg";
        assertEquals("51815575646_3ec1d92c83_k.jpg", AppUtils.getFilename(url));
    }

    @Test
    void testOptionalNull() {
        Optional<String> s = Optional.of("test");

        var n = s.map(ss -> null);
        assertTrue(n.isEmpty());
    }
}