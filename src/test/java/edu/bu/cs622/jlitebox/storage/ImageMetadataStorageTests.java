package edu.bu.cs622.jlitebox.storage;

import edu.bu.cs622.jlitebox.config.ImageCatalogConfiguration;
import edu.bu.cs622.jlitebox.image.ImageFactory;
import edu.bu.cs622.jlitebox.image.preview.ImagePreviewGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Image metadata storage tests
 *
 * @author dlegaspi@bu.edu
 */
@ExtendWith(MockitoExtension.class)
public class ImageMetadataStorageTests {
    @Mock
    private ImageCatalogConfiguration config;

    @Mock
    private ImagePreviewGenerator previewGenerator;

    @BeforeEach
    public void beforeEach() {
        MockitoAnnotations.openMocks(this);
        when(config.getDatabaseUrl()).thenReturn("jdbc:sqlite:db/jlitebox-test.sqlite");
    }

    @Test
    public void testImageMetadataStorage() throws SQLException {
        var storage = new DatabaseImageMetadataStorage(config, previewGenerator);
        var imagePath = getClass().getClassLoader().getResource("images/IMAGE-01.JPG").getPath();
        var image = ImageFactory.fromFile(imagePath);

        assertTrue(storage.saveMetadata(image));
    }
}
