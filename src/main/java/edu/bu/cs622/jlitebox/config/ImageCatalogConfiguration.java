package edu.bu.cs622.jlitebox.config;

import java.util.List;

/**
 * Catalog configuration
 *
 * @author dlegaspi@bu.edu
 */
public interface ImageCatalogConfiguration {
    /**
     * root directory
     *
     * @return the root directory
     */
    String getRootDirectory();

    /**
     * Supported image file extensions
     *
     * @return list of supported extensions;
     */
    public List<String> supportedFileExtensions();

    /**
     * Get a temporary directory
     *
     * @return a temporary directory
     */
    String getTempDirectory();

    /**
     * image preview height
     *
     * @return the height in pixels
     */
    int getImagePreviewHeight();

    /**
     * image preview length
     *
     * @return the height in pixels
     */
    int getImagePreviewWidth();

    /**
     * the database url for metadata
     *
     * @return JDBC url
     */
    String getDatabaseUrl();

    /**
     * Number of threads to import images for parallelism
     *
     * @return number of threads
     */
    default int getImageImportThreads() {
        return 8;
    }

    /**
     * Number of threads to load images for parallelism
     *
     * @return number of threads
     */
    default int getImageLoadThreads() {
        return getImageImportThreads();
    }
}
