package edu.bu.cs622.jlitebox.image;

import edu.bu.cs622.jlitebox.exceptions.JLiteBoxException;
import edu.bu.cs622.jlitebox.filter.ImageContentFilter;
import edu.bu.cs622.jlitebox.image.preview.ImagePreviewGenerator;
import edu.bu.cs622.jlitebox.storage.ImageMetadataStorage;
import edu.bu.cs622.jlitebox.storage.ImageStorage;
import io.vavr.Function3;
import io.vavr.Function4;

import java.util.Collection;
import java.util.function.Predicate;

/**
 * Image catalog
 *
 * @author dlegaspi@bu.edu
 */
public interface ImageCatalog {
    /**
     * Logical operator
     */
    enum LogicalOperator {
        OR, AND
    }

    /**
     * get the images
     *
     * @return the images collection
     */
    Collection<Image> getImages();

    /**
     * get the images based on a filter
     *
     * @param filter filter
     * @return images matching the specified filter
     */
    Collection<Image> getImages(ImageContentFilter filter);

    /**
     * Image storage
     *
     * @return the image storage
     */
    ImageStorage getStorage();

    /**
     * Metadata storage
     *
     * @return the image metadata storage
     */
    ImageMetadataStorage getMetadataStorage();

    /**
     * Preview generator
     *
     * @return the preview generator
     */
    ImagePreviewGenerator getPreviewGenerator();

    /**
     * Import images from the specified source directory
     *
     * @param srcDir  the directory
     * @param options import options
     * @param importCallback callback for updating UI while importing
     * @return number of imported images
     * @throws JLiteBoxException for any errors
     */
    int addImagesFromDirectory(String srcDir, ImageImportOptions options, Function4<Image, String, Integer, Integer, Void> importCallback) throws JLiteBoxException;

    /**
     * Import images list from specified file
     *
     * @param path            the file that contains list
     * @param options         options
     * @param onErrorCallback callback that if returns true continue even with error
     * @return number of import images
     * @throws JLiteBoxException for any errors
     */
    int addImagesFromFile(String path, ImageImportOptions options, Predicate<String> onErrorCallback)
                    throws JLiteBoxException;

    /**
     * Import options
     */
    final class ImageImportOptions {
        private LogicalOperator operator;
        private boolean overwrite;
        private boolean ignoreErrors;

        ImageImportOptions(LogicalOperator operation, boolean overwrite, boolean ignoreErrors) {
            this.operator = operation;
            this.overwrite = overwrite;
            this.ignoreErrors = ignoreErrors;
        }

        public boolean isOverwrite() {
            return overwrite;
        }

        public boolean isAndOperation() {
            return this.operator == LogicalOperator.AND;
        }

        public boolean isIgnoreErrors() {
            return ignoreErrors;
        }
    }

    /**
     * load images from catalog
     *
     * @param loadCallback load callback for updating UI while loading
     * @return number of images loaded from catalog
     */
    Collection<Image> loadImages(Function3<Image, Integer, Integer, Void> loadCallback) throws JLiteBoxException;
}
