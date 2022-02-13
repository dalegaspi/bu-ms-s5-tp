package edu.bu.cs622.jlitebox.controller.gui;

import edu.bu.cs622.jlitebox.image.ImageCatalog;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * About Dialog Controller
 *
 * @author dlegaspi@bu.edu
 */
public class AboutController {
    private static Logger logger = LoggerFactory.getLogger(AboutController.class);

    private ImageCatalog catalog;

    @Inject
    public AboutController(ImageCatalog catalog) {
        this.catalog = catalog;
    }

    @FXML
    protected void handleAboutOk(ActionEvent event) {
        logger.info("Close About Dialog...");

        // https://riptutorial.com/javafx/example/28033/creating-custom-dialog
        var source = (Node) event.getSource();
        var stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}
