package edu.bu.cs622.jlitebox.image.metadata;

import edu.bu.cs622.jlitebox.exceptions.ImageMetadataParseException;
import edu.bu.cs622.jlitebox.exceptions.JLiteBoxException;
import edu.bu.cs622.jlitebox.image.Image;
import org.librawfx.LibrawImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

/**
 * Metadata extractor using LibRaw
 *
 * @author dlegaspi@bu.edu
 */
public class LibRawMetadataExtractor implements ImageMetadataExtractor {
    private static final Logger logger = LoggerFactory.getLogger(LibRawMetadataExtractor.class);

    @Override
    public Optional<ImageMetadata> parse(Image image) throws JLiteBoxException {
        try {
            var metadata = new LibrawImage(image.getOriginalSrcPath()).getMetaData();

            logger.info("Metadata successfully extracted from {}; it has {} metadata tags", image.getName(),
                            metadata.size());

            return Optional.of(new ImageMetadata(metadata));
        } catch (IOException e) {
            throw new ImageMetadataParseException(e.getMessage(), e);
        }
    }
}
