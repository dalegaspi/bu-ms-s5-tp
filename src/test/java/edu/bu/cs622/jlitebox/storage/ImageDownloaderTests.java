package edu.bu.cs622.jlitebox.storage;

import edu.bu.cs622.jlitebox.config.ImageCatalogConfiguration;
import edu.bu.cs622.jlitebox.exceptions.JLiteBoxException;
import edu.bu.cs622.jlitebox.utils.BasicImageDownloader;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Image downloader tests
 *
 * @author dlegaspi@bu.edu
 */
@ExtendWith(MockitoExtension.class)
public class ImageDownloaderTests {
    @Mock
    private ImageCatalogConfiguration config;

    @BeforeEach
    public void beforeEach() throws JLiteBoxException {
        MockitoAnnotations.openMocks(this);
        when(config.getTempDirectory()).thenReturn(FileUtils.getTempDirectoryPath());
    }

    @Test
    public void testBasicDownloader() throws JLiteBoxException {
        var d = new BasicImageDownloader(config);

        var file = d.download("https://live.staticflickr.com/65535/51836490494_3fa5339ee8_k.jpg");
        assertNotNull(file);

        d.cleanup();
    }
}
