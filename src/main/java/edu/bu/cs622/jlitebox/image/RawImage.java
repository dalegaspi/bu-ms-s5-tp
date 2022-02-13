package edu.bu.cs622.jlitebox.image;

import edu.bu.cs622.jlitebox.exceptions.ImageImportException;
import edu.bu.cs622.jlitebox.image.preview.ImagePreviewGenerator;
import edu.bu.cs622.jlitebox.storage.ImageStorage;
import edu.bu.cs622.jlitebox.utils.AppUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RAW image object.
 *
 * @see Image
 * @author dlegaspi@bu.edu
 */
public class RawImage extends Image {
    private static Logger logger = LoggerFactory.getLogger(RawImage.class);

    public RawImage(@NonNull String originalSrcPath) {
        super(originalSrcPath);
    }

    /**
     * Generate Preview only applies to RAW images
     */
    public void generateImagePreview(@NonNull ImagePreviewGenerator previewGenerator) throws ImageImportException {
        logger.info("Generating Image Preview for {}", getName());
        this.preview = previewGenerator.generatePreview(this);
    }

    @Override
    public String getType() {
        return AppUtils.getFilenameExt(getOriginalSrcPath());
    }
}
