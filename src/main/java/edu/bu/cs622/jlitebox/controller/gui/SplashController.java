package edu.bu.cs622.jlitebox.controller.gui;

import com.google.inject.Injector;
import edu.bu.cs622.jlitebox.App;
import edu.bu.cs622.jlitebox.config.ConfigurationManager;
import edu.bu.cs622.jlitebox.image.ImageCatalog;
import io.vavr.control.Try;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class SplashController implements Initializable {
    private static Logger logger = LoggerFactory.getLogger(SplashController.class);
    public AnchorPane mainSplashPane;
    public Label statusText;
    public Label buildInfo;

    private ImageCatalog catalog;

    private Injector injector;


    @Inject
    public SplashController(ImageCatalog catalog) {
        logger.info("Launching main GUI controller...");
        this.catalog = catalog;
    }

    public void setInjector(Injector injector) {
        this.injector = injector;
    }

    private Image getAppIcon() {
        var appIconStream = getClass().getResourceAsStream("/logo.png");

        return new Image(appIconStream);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        CompletableFuture.runAsync(() -> {
            Try.run(() -> Thread.sleep(1000));
            Try.run(() -> catalog.loadImages((image, current, count) ->{
                Platform.runLater(() -> {
                    buildInfo.setText("Build v" + ConfigurationManager.getVersionInfo().orElse("1.0"));
                    statusText.setText(String.format("Loading %d of %d images...", current, count));
                });
                return null;
            }));

            Platform.runLater(() -> {
                try {
                Stage stage = new Stage();
                // Load FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));

                // Set the controller and set the controller factory to use Guice
                // see https://stackoverflow.com/a/23471463/918858
                loader.setControllerFactory(injector::getInstance);
                Parent root = null;
                root = loader.load();
                Scene scene = new Scene(root, 1200, 800);
                stage.getIcons().add(getAppIcon());
                stage.setTitle(App.APP_NAME);
                stage.setScene(scene);

                // set the injector in the main controller
                ((MainController) loader.getController()).setInjector(injector);

                // show the main GUI
                stage.show();

                // hide the splash window
                mainSplashPane.getScene().getWindow().hide();
                } catch (IOException e) {
                    logger.error("Cannot load main screen: " + e.getMessage(), e);
                }
            });
        });
    }
}
