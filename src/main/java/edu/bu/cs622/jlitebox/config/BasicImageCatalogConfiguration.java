package edu.bu.cs622.jlitebox.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import edu.bu.cs622.jlitebox.filter.ContentFilter;
import edu.bu.cs622.jlitebox.filter.FilteredContentCollection;

import java.util.List;

/**
 * This is the configuration based on Typesafe configuration
 * (https://github.com/lightbend/config).
 *
 * The configuration can be updated in the /resources/application.conf
 *
 * see README file for explanation of the configuration
 *
 * @author dlegaspi@bu.edu
 */
public class BasicImageCatalogConfiguration implements ImageCatalogConfiguration {
    private final Config config = ConfigFactory.load();

    @Override
    public String getRootDirectory() {
        return config.getString("app.storage.root-dir");
    }

    @Override
    public List<String> supportedFileExtensions() {

        return config.getStringList("app.images.supported-exts");
    }

    @Override
    public String getTempDirectory() {
        return config.getString("app.import.temp-dir");
    }

    @Override
    public int getImagePreviewHeight() {
        return config.getInt("app.preview.height");
    }

    @Override
    public int getImagePreviewWidth() {
        return config.getInt("app.preview.width");
    }

    @Override
    public String getDatabaseUrl() {
        return config.getString("app.metadata.db-url");
    }
}
