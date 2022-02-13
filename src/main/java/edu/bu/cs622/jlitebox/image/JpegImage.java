package edu.bu.cs622.jlitebox.image;

import edu.bu.cs622.jlitebox.exceptions.ImageImportException;
import edu.bu.cs622.jlitebox.image.preview.ImagePreviewGenerator;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A JPEG Image
 *
 * @author dlegaspi@bu.edu
 */
public class JpegImage extends Image {
    private static Logger logger = LoggerFactory.getLogger(JpegImage.class);

    public JpegImage(@NonNull String originalSrcPath) {
        super(originalSrcPath);
    }

    /**
     * Resize preview for JPG images
     */
    public void resizeImagePreview(@NonNull ImagePreviewGenerator previewGenerator) throws ImageImportException {
        logger.info("Resizing Image Preview for {}", getName());
        this.preview = previewGenerator.resizePreview(this);
    }

    @Override
    public String getType() {
        return "JPG";
    }
}
