package edu.bu.cs622.jlitebox.image;

import edu.bu.cs622.jlitebox.image.ImageCatalog.ImageImportOptions;

import static edu.bu.cs622.jlitebox.image.ImageCatalog.LogicalOperator.AND;
import static edu.bu.cs622.jlitebox.image.ImageCatalog.LogicalOperator.OR;

/**
 * Creates instance(s) of Image
 *
 * @see Image
 * @author dlegaspi@bu.edu
 */
public final class ImageFactory {

    /**
     * Create an Image from file
     *
     * @param path file path
     * @return an instance of Image (RawImage or JpegImage)
     */
    public static Image fromFile(String path) {
        return ImageUtils.isJpegImage(path) ? new JpegImage(path) : new RawImage(path);
    }

    /**
     * create import options
     *
     * @see ImageImportOptions
     * @param isAndOperation is AND
     * @param isOverwrite    is overwrite
     * @return ImageImportOptions instance
     */
    public static ImageImportOptions withImportOptions(boolean isAndOperation, boolean isOverwrite,
                    boolean ignoreErrors) {
        return new ImageImportOptions(isAndOperation ? AND : OR, isOverwrite, ignoreErrors);
    }

    /**
     * default import options
     *
     * @return default import options
     */
    public static ImageImportOptions withDefaultImportOptions() {
        return withImportOptions(true, true, false);
    }
}
