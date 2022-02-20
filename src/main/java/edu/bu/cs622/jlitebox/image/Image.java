package edu.bu.cs622.jlitebox.image;

import edu.bu.cs622.jlitebox.App;
import edu.bu.cs622.jlitebox.image.metadata.ImageMetadata;
import edu.bu.cs622.jlitebox.utils.AppUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Map;
import java.util.Optional;

/**
 * Abstract image class
 *
 * @author dlegaspi@bu.edu
 */
public abstract class Image {
    protected ImageMetadata metadata = new ImageMetadata(Map.of());
    private final String filename;
    private final String originalSrcPath;
    protected javafx.scene.image.Image preview;

    /**
     * the filename
     *
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Image metadata
     *
     * @return image metadata
     */
    public ImageMetadata getMetadata() {
        return metadata;
    }

    /**
     * Set image metadata
     *
     * @param metadata the metadata
     */
    public void setMetadata(ImageMetadata metadata) {
        this.metadata = metadata;
    }

    /**
     * Original Source Path on import
     *
     * @return the original source path
     */
    public String getOriginalSrcPath() {
        return originalSrcPath;
    }

    Image(@NonNull String originalSrcPath) {
        this.filename = AppUtils.getFilename(originalSrcPath);
        this.originalSrcPath = originalSrcPath;
    }

    /**
     * Convenience method to see if the image has equipment info
     *
     * @return true if it has equipment information
     */
    public boolean hasEquipmentInfo() {
        return getMetadata() != null && getMetadata().getEquipmentUsed() != null
                        && getMetadata().getEquipmentUsed().size() > 0;
    }

    /**
     * default name derived from filename
     *
     * @return the name
     */
    public String getName() {
        return AppUtils.getBaseFilename(getFilename());
    }

    public abstract String getType();

    /**
     * the preview image
     *
     * @return preview image
     */
    public Optional<javafx.scene.image.Image> getPreview() {
        return Optional.ofNullable(preview);
    }

    /**
     * set preview
     *
     * @param preview the new preview
     */
    public void setPreview(javafx.scene.image.Image preview) {
        this.preview = preview;
    }

    /**
     * Get the file name
     *
     * @return the filename
     */
    public String toFilename() {
        return AppUtils.getFilename(getOriginalSrcPath());
    }
}
