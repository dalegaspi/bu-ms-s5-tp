package edu.bu.cs622.jlitebox.image.preview;

import edu.bu.cs622.jlitebox.exceptions.ImageImportException;
import edu.bu.cs622.jlitebox.image.JpegImage;
import edu.bu.cs622.jlitebox.image.RawImage;
import javafx.scene.image.Image;

/**
 * JPEG Image preview Generator
 */
public interface ImagePreviewGenerator {

    /**
     * Generate a preview
     *
     * @param image
     * @return a image preview object of Type Image
     * @see javafx.scene.image.Image
     */
    javafx.scene.image.Image generatePreview(RawImage image) throws ImageImportException;

    /**
     * Resize a preview for JPEG images because technically you don't generate one
     * because an image preview is already a JPEG.
     *
     * @param image
     * @return a image preview object of Type Image
     * @see javafx.scene.image.Image
     */
    javafx.scene.image.Image resizePreview(JpegImage image) throws ImageImportException;

    /**
     * Create byte array from Image Preview for storage
     *
     * @param image image to serialize
     * @return the byte array
     * @throws ImageImportException I/O errors
     */
    byte[] convertPreviewToByteArray(javafx.scene.image.Image image) throws ImageImportException;

    /**
     * Craete a preview from Byte array from storage for display
     *
     * @param bytes the bytes
     * @return the image
     * @throws ImageImportException I/O errors
     */
    Image createPreviewFromByteArray(byte[] bytes) throws ImageImportException;
}
