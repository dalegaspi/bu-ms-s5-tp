package edu.bu.cs622.jlitebox.image.preview;

import edu.bu.cs622.jlitebox.config.ImageCatalogConfiguration;
import edu.bu.cs622.jlitebox.exceptions.ImageImportException;
import edu.bu.cs622.jlitebox.image.JpegImage;
import edu.bu.cs622.jlitebox.image.RawImage;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Image Preview Generator using LibRaw
 *
 * @see ImagePreviewGenerator
 * @author dlegaspi@bu.edu
 */
public class LibRawImagePreviewGenerator implements ImagePreviewGenerator {
    private static Logger logger = LoggerFactory.getLogger(LibRawImagePreviewGenerator.class);

    private final ImageCatalogConfiguration config;

    @Inject
    public LibRawImagePreviewGenerator(ImageCatalogConfiguration config) {
        this.config = config;
    }

    @Override
    public Image generatePreview(@NonNull RawImage image) throws ImageImportException {
        return loadAndResizeImage(image);
    }

    private Image loadAndResizeImage(edu.bu.cs622.jlitebox.image.Image image) throws ImageImportException {
        return generatePreview(new File(image.getOriginalSrcPath()), config.getImagePreviewWidth(),
                        config.getImagePreviewHeight());
    }

    private Image generatePreview(File fullPath, int width, int height) throws ImageImportException {
        try {
            var path = fullPath.toURI().toURL().toString();
            var preview = new Image(path, width, height, true, true);
            logger.info("Preview generated width: {}, height: {}", preview.getWidth(), preview.getHeight());

            return preview;
        } catch (MalformedURLException e) {
            throw new ImageImportException(e.getMessage(), e);
        }
    }

    @Override
    public Image generatePreviewForDisplay(edu.bu.cs622.jlitebox.image.Image image, int width, int height)
                    throws ImageImportException {
        return generatePreview(new File(config.getRootDirectory(), image.toFilename()), width, height);
    }

    @Override
    public Image resizePreview(@NonNull JpegImage image) throws ImageImportException {
        return loadAndResizeImage(image);
    }

    @Override
    public byte[] convertPreviewToByteArray(Image image) throws ImageImportException {
        BufferedImage bi = SwingFXUtils.fromFXImage(image, null);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            ImageIO.write(bi, "jpg", output);
        } catch (IOException e) {
            throw new ImageImportException(e.getMessage(), e);
        }
        return output.toByteArray();
    }

    @Override
    public Image createPreviewFromByteArray(byte[] bytes) throws ImageImportException {
        var input = new ByteArrayInputStream(bytes);
        try {
            BufferedImage bi = ImageIO.read(input);
            return SwingFXUtils.toFXImage(bi, null);
        } catch (IOException e) {
            throw new ImageImportException(e.getMessage(), e);
        }
    }
}
