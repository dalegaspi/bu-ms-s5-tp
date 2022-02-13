package edu.bu.cs622.jlitebox.controller;

import edu.bu.cs622.jlitebox.exceptions.JLiteBoxException;
import edu.bu.cs622.jlitebox.image.ImageCatalog;
import edu.bu.cs622.jlitebox.image.ImageFactory;
import edu.bu.cs622.jlitebox.view.ImageImporterView;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ControllerTests {

    @Mock
    ImageCatalog imageCatalog;

    @Mock
    ImageImporterView imageImporterView;

    @BeforeEach
    public void beforeEach() throws JLiteBoxException {
        MockitoAnnotations.openMocks(this);
        when(imageCatalog.addImagesFromFile(anyString(), any(), any())).thenReturn(10);
        when(imageCatalog.addImagesFromDirectory(anyString(), any(), any())).thenReturn(15);
    }

    @Test
    public void testImportController() throws JLiteBoxException {
        var controller = new ConsoleFileImportController(imageImporterView, imageCatalog);

        var totalFromDir = controller.importImagesFromDirectory("/tmp", ImageFactory.withDefaultImportOptions());
        var totalFromFile = controller.importImagesFromFile("./tmp.txt", ImageFactory.withDefaultImportOptions());

        assertEquals(totalFromFile, 10);
        assertEquals(totalFromDir, 15);
    }
}
