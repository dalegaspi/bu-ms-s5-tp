package edu.bu.cs622.jlitebox.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Configuration tests
 *
 * @author dlegaspi@bu.edu
 */
public class ConfigTests {

    @Test
    public void testBasicConfig() {
        var version = ConfigurationManager.getVersionInfo();

        assertTrue(version.isPresent());
    }

    @Test
    public void testCatalogConfig() {
        var config = new BasicImageCatalogConfiguration();

        assertNotNull(config);
        assertNotNull(config.getRootDirectory());
        assertNotNull(config.getTempDirectory());
        assertNotNull(config.supportedFileExtensions());
        assertTrue(config.supportedFileExtensions().size() > 0);
    }
}
