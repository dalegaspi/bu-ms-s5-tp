package edu.bu.cs622.jlitebox.storage;

import edu.bu.cs622.jlitebox.config.ImageCatalogConfiguration;
import edu.bu.cs622.jlitebox.exceptions.JLiteBoxException;
import edu.bu.cs622.jlitebox.image.ImageFactory;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Image storage tests
 *
 * @author dlegaspi@bu.edu
 */
@ExtendWith(MockitoExtension.class)
public class ImageStorageTests {
    @Mock
    private ImageCatalogConfiguration config;

    @BeforeEach
    public void beforeEach() {
        MockitoAnnotations.openMocks(this);
        when(config.getRootDirectory()).thenReturn(FileUtils.getTempDirectoryPath());
    }

    @Test
    void testFileImageStorage() throws JLiteBoxException {
        var imagePath = getClass().getClassLoader().getResource("images/IMAGE-01.JPG").getPath();
        var image = ImageFactory.fromFile(imagePath);
        var storage = new FileImageStorage(config);

        assertTrue(storage.save(image));
    }
}
