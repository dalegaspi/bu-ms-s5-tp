package edu.bu.cs622.jlitebox;

import com.google.inject.AbstractModule;
import edu.bu.cs622.jlitebox.config.BasicImageCatalogConfiguration;
import edu.bu.cs622.jlitebox.config.ImageCatalogConfiguration;
import edu.bu.cs622.jlitebox.controller.ConsoleFileImportController;
import edu.bu.cs622.jlitebox.controller.ImageImportController;
import edu.bu.cs622.jlitebox.image.*;
import edu.bu.cs622.jlitebox.image.metadata.ImageMetadataExtractor;
import edu.bu.cs622.jlitebox.image.metadata.LibRawMetadataExtractor;
import edu.bu.cs622.jlitebox.image.preview.ImagePreviewGenerator;
import edu.bu.cs622.jlitebox.image.preview.LibRawImagePreviewGenerator;
import edu.bu.cs622.jlitebox.storage.DatabaseImageMetadataStorage;
import edu.bu.cs622.jlitebox.storage.FileImageStorage;
import edu.bu.cs622.jlitebox.storage.ImageMetadataStorage;
import edu.bu.cs622.jlitebox.storage.ImageStorage;
import edu.bu.cs622.jlitebox.utils.BasicImageDownloader;
import edu.bu.cs622.jlitebox.utils.ImageDownloader;
import edu.bu.cs622.jlitebox.view.ImageImporterConsoleView;
import edu.bu.cs622.jlitebox.view.ImageImporterView;
import edu.bu.cs622.jlitebox.view.UserInputSource;
import edu.bu.cs622.jlitebox.view.UserKeyboardInputSource;

/**
 * Google Guice module for Dependency Injection
 *
 * @author dlegaspi@bu.edu
 */
public class JLiteBoxModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ImageCatalogConfiguration.class).to(BasicImageCatalogConfiguration.class);
        bind(ImageImportController.class).to(ConsoleFileImportController.class);
        bind(ImageImporterView.class).to(ImageImporterConsoleView.class);
        bind(ImageCatalog.class).to(BasicImageCatalog.class);
        bind(ImageMetadataStorage.class).to(DatabaseImageMetadataStorage.class);
        bind(ImageStorage.class).to(FileImageStorage.class);
        bind(ImageMetadataStorage.class).to(DatabaseImageMetadataStorage.class);
        bind(UserInputSource.class).to(UserKeyboardInputSource.class);
        bind(ImageDownloader.class).to(BasicImageDownloader.class);
        bind(ImageMetadataExtractor.class).to(LibRawMetadataExtractor.class);
        bind(ImagePreviewGenerator.class).to(LibRawImagePreviewGenerator.class);
    }
}
