package edu.bu.cs622.jlitebox.controller.gui;

import com.google.inject.Injector;
import com.jfoenix.controls.JFXChipView;
import com.jfoenix.controls.JFXToggleButton;
import edu.bu.cs622.jlitebox.App;
import edu.bu.cs622.jlitebox.filter.ImageContentFilter;
import edu.bu.cs622.jlitebox.image.Image;
import edu.bu.cs622.jlitebox.image.ImageCatalog;
import edu.bu.cs622.jlitebox.image.ImageFactory;
import edu.bu.cs622.jlitebox.image.metadata.ImageMetadata;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Try;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.StatusBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

/**
 * Main view controller for the GUI. Using Guice for DI
 * (https://stackoverflow.com/a/23471463/918858)
 *
 * @author dlegaspi@bu.edu
 */
public class MainController implements Initializable {
    private static Logger logger = LoggerFactory.getLogger(MainController.class);

    private ImageCatalog catalog;
    private AboutController aboutController;
    private Injector injector;

    /**
     * Constructure with Guice dependency injection
     *
     * @param catalog the image catalog (a.k.a. data layer)
     */
    @Inject
    public MainController(ImageCatalog catalog, AboutController aboutController) {
        logger.info("Launching main GUI controller...");
        this.catalog = catalog;
        this.aboutController = aboutController;
    }

    public void setInjector(Injector injector) {
        this.injector = injector;
    }

    private int theCurrentImageIndex = 0;

    @FXML
    private Label catalogStatistics;

    @FXML
    private Label theCurrentImageLabel;

    @FXML
    private ImageView theCurrentImage;

    @FXML
    private JFXChipView<String> filterStrings;

    @FXML
    private SplitPane mainSplitViewContainer;

    @FXML
    private JFXToggleButton toggleGridView;

    @FXML
    private Slider previewSizeSlider;

    @FXML
    private CheckMenuItem menuToggleShowMetadata;

    @FXML
    private CheckMenuItem menuToggleExifMetaHover;

    @FXML
    private JFXToggleButton toggleExifOnHover;

    @FXML
    private JFXToggleButton toggleShowExif;

    @FXML
    private Pane leftToolBarPane;

    @FXML
    private BorderPane mainViewPane;

    @FXML
    private VBox mainGridViewContainer;

    @FXML
    protected void handleFileExitAction(ActionEvent event) {
        logger.info("Exiting application...");
        Platform.exit();
        System.exit(0);
    }

    @FXML
    protected void handleAboutDialog(ActionEvent event) throws IOException {
        logger.info("About dialog box...");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/about.fxml"));

        loader.setControllerFactory(injector::getInstance);
        Parent root = loader.load();
        Scene scene = new Scene(root, 340, 200);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.setTitle("About " + App.APP_NAME);
        stage.setScene(scene);

        // Run the dialog box as a modal
        stage.showAndWait();
    }

    /**
     * Updates status bar text
     *
     * @param text the text
     */
    private void updateStatusBarText(String text) {
        Platform.runLater(() -> {
            logger.info("Refreshing status bar with text [{}]", text);
            statusBar.setText(text);
        });
    }

    static String statusBarDefaultText = "Ready.";

    @FXML
    protected void handleFileImportAction(ActionEvent event) {
        logger.info("Import...");
        DirectoryChooser importDirDialog = new DirectoryChooser();
        importDirDialog.setTitle("Import Images");
        var selectedDirectory = importDirDialog
                        .showDialog(((MenuItem) event.getTarget()).getParentPopup().getOwnerWindow());

        if (selectedDirectory != null) {
            // runAsync to not block the GUI; JavaFx is single-threaded and only allows
            // updated in its own thread (it doesn't allow update of GUI from another thread
            // this is why there is reference to that use of Platform::runLater which
            // allows asynchronous import _but still_ be able to update the GUI during the
            // progress of import
            statusBar.setText(String.format("Importing images from directory [%s]...", selectedDirectory.getPath()));
            CompletableFuture.runAsync(() -> Try.of(() -> catalog.addImagesFromDirectory(
                            selectedDirectory.getAbsolutePath(),
                            ImageFactory.withDefaultImportOptions(),
                            (currentImage, status, currentIndex, all) -> {
                                logger.info("Image {} of {} {}}.", currentIndex, all, status);
                                // this allows to update the status bar in the UI on the
                                // status of import _asynchronously_
                                updateStatusBarText(String.format("Image %d of %d %s.", currentIndex, all, status));
                                return null;
                            }))).thenRun(() -> Platform.runLater(() -> {
                                // once everything is finished, refresh the main view
                                logger.info("Refreshing view...");
                                initializeImageCollectionView(true);
                                statusBar.setText(statusBarDefaultText);

                                Notifications.create()
                                                .title("JLiteBox Import Directory")
                                                .text(String.format("Import from directory [%s] completed.",
                                                                selectedDirectory.getPath()))
                                                .showInformation();
                            }));
        }
    }

    @FXML
    private StatusBar statusBar;

    @FXML
    private TilePane gridContainer;

    private VBox createImageVBox(Image image) {
        var vbox = new VBox();

        String normalCss = "-fx-border-color: gray;\n" +
                        "-fx-background-radius: 10;\n" +
                        "-fx-background-color: white;\n" +
                        "-fx-border-radius: 10;\n" +
                        "-fx-border-width: 1;\n" +
                        "-fx-border-style: solid;\n";

        String hoverCss = "-fx-border-color: #D9631E;\n" +
                        "-fx-background-radius: 10;\n" +
                        "-fx-background-color: white;\n" +
                        "-fx-border-radius: 10;\n" +
                        "-fx-border-width: 1;\n" +
                        "-fx-border-style: solid;\n";

        vbox.setStyle(normalCss);

        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setPadding(new Insets(12.5));
        vbox.setPrefWidth(120);
        vbox.setPrefHeight(ImageBox.getPreferredHeight(showMetadata));
        vbox.setSpacing(10);

        vbox.getProperties().put("image", image);

        // create tooltip based on meta
        if (exifOnHover) {
            var tooltip = new Tooltip(image.getMetadata().getFormattedRawMetadata());
            tooltip.setFont(Font.font("SF Mono", FontWeight.NORMAL, 10));
            tooltip.setShowDuration(Duration.seconds(30));
            Tooltip.install(vbox, tooltip);
        }

        vbox.hoverProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> value, Boolean oldValue, Boolean newValue) {
                logger.debug("observable value = {} old = {} new = {}", value, oldValue, newValue);
                vbox.setStyle(value.getValue() ? hoverCss : normalCss);
            }
        });

        return vbox;
    }

    private double imagePreviewSizeMultiplier = 1.0;

    private double applyMultiplier(double value) {
        return value * imagePreviewSizeMultiplier;
    }

    private ImageView createImageViewFor(Image image, javafx.scene.image.Image preview) {
        var imageView = new ImageView();
        logger.info("Creating/resizing preview for {}...", image.getName());

        if (preview.getHeight() < preview.getWidth())
            imageView.setFitWidth(applyMultiplier(120));
        else
            imageView.setFitWidth(applyMultiplier(80));
        imageView.setPreserveRatio(true);
        imageView.setImage(preview);
        // apply a shadow effect.
        var effect = new InnerShadow(5, Color.DARKGRAY);
        effect.setBlurType(BlurType.GAUSSIAN);
        imageView.setEffect(new InnerShadow(5, Color.DARKGRAY));
        return imageView;
    }

    private static void setImageTitleProperties(double labelWidth, Label label, boolean showMetadata) {
        label.setMinWidth(labelWidth);
        label.setMaxWidth(labelWidth);
        label.setAlignment(showMetadata ? Pos.TOP_LEFT : Pos.TOP_CENTER);
        label.setFont(Font.font("SF Mono",
                        showMetadata ? FontWeight.NORMAL : FontWeight.BOLD,
                        ImageBox.getPreferredLabelFontSize(showMetadata)));
    }

    private Label createImageTitle(Image image, String titleText) {
        var imageTitle = new Label();

        logger.info("Creating title label for {} length {}", image.getName(), titleText.length());
        setImageTitleProperties(applyMultiplier(120), imageTitle, showMetadata);

        imageTitle.setText(titleText);
        return imageTitle;
    }

    /**
     * This creates the Image Preview Layer with the Image Type (JPG/RAW) that
     * appears in the upper left corner
     *
     * @param image   the image
     * @param preview the preview
     * @return Pane/layer with banner
     */
    private Tuple2<Pane, Pane> createImageViewWithBannerPane(Image image, javafx.scene.image.Image preview) {
        var imageBannerPane = new AnchorPane();
        var banner = new Label(image.getType().toUpperCase());
        banner.setTextFill(Color.LIGHTGRAY);
        banner.setPadding(new Insets(4));
        banner.setBackground(Background.fill(Color.valueOf("#D9631E")));
        banner.setStyle("-fx-opacity: 0.75");
        banner.setFont(Font.font("SF Compact", FontWeight.BOLD, 10));

        var imageView = createImageViewFor(image, preview);

        var imageViewPane = new StackPane();
        imageViewPane.setAlignment(Pos.TOP_CENTER);
        imageViewPane.getChildren().add(imageView);
        banner.setLayoutX(imageView.getLayoutX());
        banner.setLayoutY(imageView.getLayoutY());

        imageBannerPane.getChildren().add(banner);

        imageViewPane.getChildren().add(imageBannerPane);

        return Tuple.of(imageViewPane, imageBannerPane);
    }

    private boolean showMetadata = true;

    @FXML
    public void handleToggleShowMetadata(ActionEvent event) {
        if (event.getSource() instanceof CheckMenuItem) {
            var cmi = (CheckMenuItem) event.getSource();
            showMetadata = cmi.isSelected();
            toggleShowExif.setSelected(showMetadata);
            imageBoxGrid.values().forEach(b -> b.refresh(showMetadata, applyMultiplier(120)));
        }
    }

    private boolean exifOnHover = true;

    @FXML
    public void handleToggleExifMetaHover(ActionEvent event) {
        if (event.getSource() instanceof CheckMenuItem) {
            var cmi = (CheckMenuItem) event.getSource();
            exifOnHover = cmi.isSelected();
            toggleExifOnHover.setSelected(exifOnHover);
            initializeImageCollectionView(false);
        }
    }

    @FXML
    public void reloadImages(ActionEvent event) {
        logger.info("Reloading from catalog...");
        updateStatusBarText("Reloading images from catalog...");
        CompletableFuture.runAsync(() -> initializeImageCollectionView(true))
                        .thenRun(() -> updateStatusBarText(statusBarDefaultText));
    }

    @FXML
    public void handleToggleShowToolbar(ActionEvent event) {
        if (event.getSource() instanceof CheckMenuItem) {
            var cmi = (CheckMenuItem) event.getSource();
            toggleToolBarVisibility(cmi.isSelected());
        }
    }

    private void toggleToolBarVisibility(boolean visibleToggle) {
        leftToolBarPane.setVisible(visibleToggle);
        leftToolBarPane.setPrefWidth(visibleToggle ? USE_COMPUTED_SIZE : 0);
    }

    public void handlePreviousImageButton(ActionEvent event) {
        theCurrentImageIndex--;
        if (theCurrentImageIndex <= 0)
            theCurrentImageIndex = images.size() - 1;

        renderTheImageInSplitView();
    }

    public void handleNextImageButton(ActionEvent event) {
        theCurrentImageIndex++;
        if (theCurrentImageIndex >= images.size() - 1)
            theCurrentImageIndex = 0;

        renderTheImageInSplitView();
    }

    static class ImageBox {
        VBox vbox;
        Pane imageView;
        Pane imageBanner;
        Label caption;
        Image image;
        ImageMetadata metadata;

        ImageBox(Image image, ImageMetadata metadata, VBox vbox, Pane imageView, Label caption, Pane imageBanner) {
            this.image = image;
            this.vbox = vbox;
            this.imageView = imageView;
            this.caption = caption;
            this.metadata = metadata;
            this.imageBanner = imageBanner;
        }

        static int getPreferredHeight(boolean showMetadata) {
            return showMetadata ? 220 : 160;
        }

        static int getPreferredLabelFontSize(boolean showMetadata) {
            return showMetadata ? 10 : 12;
        }

        void refresh(boolean showMetadata, double labelWidth) {
            Platform.runLater(() -> {
                this.vbox.setPrefHeight(getPreferredHeight(showMetadata));
                setImageTitleProperties(labelWidth, this.caption, showMetadata);
                this.caption.setText(getLabelText(showMetadata, image, metadata));
                this.imageBanner.setVisible(showMetadata);
            });
        }

        static String getLabelText(boolean showMetadata, Image image, ImageMetadata metadata) {
            return showMetadata && metadata != null
                            ? String.format("%s\n%s", image.getName(), metadata.getStringForLabelDisplay())
                            : image.getName();
        }
    }

    private Map<String, ImageBox> imageBoxGrid = FXCollections.observableHashMap();

    private void initializeImageView() {
        // clear the grid then redraw
        Platform.runLater(() -> {
            gridContainer.setPadding(new Insets(10));
            gridContainer.getChildren().clear();
            imageBoxGrid.clear();
        });

        AtomicInteger loaded = new AtomicInteger(0);
        // convert each image in the catalog into a JavaFx object asynchronously
        var tasks = images.stream()
                        .map(image -> CompletableFuture.supplyAsync(() -> {
                            var preview = Optional.of(image).flatMap(Image::getPreview).orElse(null);
                            var metadata = Optional.of(image).map(Image::getMetadata);
                            var titleText = metadata.map(m -> ImageBox.getLabelText(showMetadata, image, m))
                                            .orElse(image.getName());

                            var vbox = createImageVBox(image);
                            Platform.runLater(() -> {
                                logger.info("Constructing VBox for {}...", image.getName());

                                var imageTitle = createImageTitle(image, titleText);
                                var imageViewPanes = createImageViewWithBannerPane(image, preview);
                                var imageViewPane = imageViewPanes._1;
                                var imageBannerPane = imageViewPanes._2;

                                logger.info("Adding Image and Text of {} to its Vbox...", image.getName());
                                imageBoxGrid.put(image.getName(),
                                                new ImageBox(image,
                                                                metadata.orElse(new ImageMetadata(Map.of())),
                                                                vbox,
                                                                imageViewPane,
                                                                imageTitle,
                                                                imageBannerPane));
                                vbox.getChildren().addAll(imageViewPane, imageTitle);
                                updateStatusBarText(String.format("Image %d of %d loaded.", loaded.incrementAndGet(),
                                                images.size()));
                            });

                            return vbox;
                        }).thenApply(vb -> {
                            logger.info("Add Image vbox for {} to grid...", image.getName());
                            Platform.runLater(() -> {
                                gridContainer.getChildren().add(vb);
                                FadeTransition ft = new FadeTransition(Duration.millis(1500), vb);
                                ft.setFromValue(0.0);
                                ft.setToValue(1.0);
                                ft.play();
                            });

                            return vb;
                        })).toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(tasks).thenRun(() -> {
            Try.run(() -> Thread.sleep(3000));
            updateStatusBarText(statusBarDefaultText);
        });
    }

    private Collection<Image> images;

    private void loadImagesFromCatalog() {
        images = Try.of(() -> catalog.loadImages((currentImage, currentIndex, imageCount) -> {
            logger.info("Loading {} of {} [{}] into UI...", currentIndex, imageCount, currentImage.getName());
            return null;
        })).getOrElse(List.of());
    }

    private void initializeImageCollectionView(boolean reloadFromCatalog) {
        if (images == null || images.isEmpty() || reloadFromCatalog)
            loadImagesFromCatalog();
        initializeImageView();
        updateStatusBarText(statusBarDefaultText);
    }

    private void toggleMainView(boolean isGrid) {
        Platform.runLater(() -> {
            if (isGrid) {
                logger.info("Switching to grid view.");
                mainViewPane.setCenter(mainGridViewContainer);
            } else {
                logger.info("Switching to split view.");

                theCurrentImageIndex = 0;
                renderTheImageInSplitView();
                mainSplitViewContainer.getItems().remove(1);
                mainSplitViewContainer.getItems().add(mainGridViewContainer);
                mainViewPane.setCenter(mainSplitViewContainer);
            }
        });
    }

    private void renderTheImageInSplitView() {
        var imagesAsList = new ArrayList<>(images);

        if (theCurrentImageIndex < 0 || theCurrentImageIndex > images.size() - 1)
            theCurrentImageIndex = 0;

        var current = imagesAsList.get(theCurrentImageIndex);
        current.getPreview().ifPresent(p -> {
            theCurrentImage.setImage(p);
        });

        theCurrentImageLabel.setText(
                        String.format("%s\n%s", current.getName(), current.getMetadata().getStringForLabelDisplay()));
    }

    private void initializeToggleHandlers() {
        toggleShowExif.selectedProperty().addListener((observable, oldValue, newValue) -> {
            showMetadata = observable.getValue();
            menuToggleShowMetadata.setSelected(showMetadata);
            imageBoxGrid.values().forEach(b -> b.refresh(showMetadata, applyMultiplier(120)));
        });

        toggleExifOnHover.selectedProperty().addListener((observable, oldValue, newValue) -> {
            exifOnHover = observable.getValue();
            menuToggleExifMetaHover.setSelected(exifOnHover);
            initializeImageCollectionView(false);
        });

        toggleGridView.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                toggleMainView(newValue);
            }
        });
    }

    private void initializePreviewSizeSlider() {
        previewSizeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                imagePreviewSizeMultiplier = 1.0 + observable.getValue().doubleValue() * 0.2;
                initializeImageCollectionView(false);
            }
        });
    }

    private void initializeFilterTextBox() {
        this.filterStrings.getChips().addListener((ListChangeListener<String>) change -> {
            logger.info("Current filters: {}", filterStrings.getChips());

            this.images = catalog.getImages(ImageContentFilter.fromFilterStringList(filterStrings.getChips()));
            initializeImageCollectionView(false);
        });
    }

    private void initializeCatalogStatistics() {
        this.catalogStatistics.setText(catalog.getMetadataStorage().getFormattedStatistics());
    }

    /**
     * initialization of the component wired to this controller
     *
     * @param url            url
     * @param resourceBundle resource bundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Initialization...");

        mainViewPane.setCenter(mainGridViewContainer);
        images = catalog.getImages();
        initializeImageCollectionView(false);
        initializeToggleHandlers();
        initializePreviewSizeSlider();
        initializeFilterTextBox();
        initializeCatalogStatistics();
    }
}
