package edu.bu.cs622.jlitebox.controller;

import edu.bu.cs622.jlitebox.exceptions.JLiteBoxException;
import edu.bu.cs622.jlitebox.filter.ImageContentFilter;
import edu.bu.cs622.jlitebox.image.ImageCatalog;
import edu.bu.cs622.jlitebox.view.ImageImporterView;

import javax.inject.Inject;
import java.util.List;

/**
 * The implementation of the file import controller via console
 *
 * @author dlegaspi@bu.edu
 */
public class ConsoleFileImportController implements ImageImportController {
    private final ImageImporterView view;
    private final ImageCatalog catalog;

    @Inject
    ConsoleFileImportController(ImageImporterView view, ImageCatalog catalog) {
        this.view = view;
        this.catalog = catalog;
    }

    @Override
    public int importImagesFromFile(String filename, ImageCatalog.ImageImportOptions options) throws JLiteBoxException {
        var imported = catalog.addImagesFromFile(filename, options, s -> {
            // this lambda prompts the view to accept a keyboard input from
            // the user to confirm if they want to continue importing
            // or abort the process altogether
            String choice = view.select("Error processing " + s + " Continue?", List.of("y", "n"));
            view.emit("Choice selected: {}", choice);
            return choice.equalsIgnoreCase("y");
        });

        return imported;
    }

    @Override
    public int importImagesFromDirectory(String path, ImageCatalog.ImageImportOptions imageImportOptions)
                    throws JLiteBoxException {
        view.emit("Importing from path: {}", path);

        var imported = catalog.addImagesFromDirectory(path, imageImportOptions, (currentImage, status, currentTotal, allTotal) -> {
            view.emit("Image {} {} of {} {}.", currentImage.getName(), currentTotal, allTotal, status);
            return null;
        });

        return imported;
    }
}
