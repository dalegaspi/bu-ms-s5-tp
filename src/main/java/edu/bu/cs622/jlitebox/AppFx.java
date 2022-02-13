package edu.bu.cs622.jlitebox;

import com.google.inject.Guice;
import edu.bu.cs622.jlitebox.config.ConfigurationManager;
import edu.bu.cs622.jlitebox.controller.ImageImportController;
import edu.bu.cs622.jlitebox.controller.gui.MainController;
import edu.bu.cs622.jlitebox.controller.gui.SplashController;
import edu.bu.cs622.jlitebox.exceptions.JLiteBoxException;
import edu.bu.cs622.jlitebox.image.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.cli.*;
import org.librawfx.RAWImageLoaderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main JavaFx entry point class
 *
 * @author dlegaspi@bu.edu
 */
public class AppFx extends Application {
    private static Logger logger = LoggerFactory.getLogger(AppFx.class);
    private static CommandLine parsedArgs;

    /**
     * Command line options
     *
     * @return the options
     */
    private static Options getCommandOptions() {
        var options = new Options();
        options.addOption(new Option("o", "overwrite", false, "Overwrite destination"));
        options.addOption(new Option("i", "ignore-errors", false, "Ignore errors"));
        options.addOption(new Option("f", "file", true, "Path to file that contains list of files to be imported."));
        options.addOption(new Option("d", "dir", true, "Directory of images to be imported"));
        return options;
    }

    private Image getAppIcon() {
        var appIconStream = getClass().getResourceAsStream("/logo.png");

        return new Image(appIconStream);
    }

    @Override
    public void start(Stage stage) {
        var injector = Guice.createInjector(new JLiteBoxModule());
        var isConsole = parsedArgs.hasOption("f") || parsedArgs.hasOption("d");
        if (isConsole) {
            try {
                launchAsConsole(parsedArgs);
            } catch (JLiteBoxException e) {
                e.printStackTrace();
            }
        } else {
            try {
                // Load FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/splash.fxml"));

                // Set the controller and set the controller factory to use Guice
                // see https://stackoverflow.com/a/23471463/918858
                loader.setControllerFactory(injector::getInstance);
                Parent root = loader.load();
                Scene scene = new Scene(root, 600, 400);

                // set window icon and title
                stage.getIcons().add(getAppIcon());
                stage.setTitle(App.APP_NAME);
                stage.setScene(scene);
                stage.initStyle(StageStyle.TRANSPARENT);

                // set the injector in the main controller
                ((SplashController) loader.getController()).setInjector(injector);

                // show the main GUI
                stage.show();
            } catch (Exception e) {
                logger.error("Exception: {}", e.getMessage(), e);
            }
        }
    }

    private static void launchAsConsole(CommandLine parsedArgs) throws JLiteBoxException {
        // DI injector initialization by Guice initiated
        var injector = Guice.createInjector(new JLiteBoxModule());

        var controller = injector.getInstance(ImageImportController.class);
        var importOptions = ImageFactory.withImportOptions(true, parsedArgs.hasOption("overwrite"),
                        parsedArgs.hasOption("ignore-errors"));

        if (parsedArgs.hasOption("f")) {
            // import images by list that's in a file
            var file = parsedArgs.getOptionValue("file");
            logger.info("Importing from file {}", file);
            controller.importImagesFromFile(file, importOptions);
            logger.info("Import from file complete.");
        } else if (parsedArgs.hasOption("d")) {
            // import images by directory
            var dir = parsedArgs.getOptionValue("dir");
            logger.info("Importing from directory {}", dir);
            controller.importImagesFromDirectory(dir, importOptions);
            logger.info("Import from directory complete");
        }

        Platform.exit();
    }

    public static void main(String[] args) {
        // activate the LibRawFx library
        RAWImageLoaderFactory.install();

        // log version information if it exists
        ConfigurationManager.getVersionInfo().ifPresent(v -> {
            logger.info("JLiteBox {}", v);
        });

        // parsing of command-line arguments
        var cmdLineParser = new DefaultParser();
        var helpFormatter = new HelpFormatter();
        var options = getCommandOptions();
        try {
            parsedArgs = cmdLineParser.parse(options, args);

            if (parsedArgs.hasOption("f") || parsedArgs.hasOption("d")) {
                logger.info("Launching app with console...");
            } else {
                logger.info("Launching app with GUI...");
            }

            launch(args);
        } catch (ParseException exception) {
            logger.error(exception.getMessage());
            helpFormatter.printHelp("java -jar jlitebox-{version}-SNAPSHOT-jar-with-dependencies.jar", options);
            System.exit(1);
        }
    }
}