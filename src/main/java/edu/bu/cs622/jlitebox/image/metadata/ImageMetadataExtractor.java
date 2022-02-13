package edu.bu.cs622.jlitebox.image.metadata;

import edu.bu.cs622.jlitebox.exceptions.JLiteBoxException;
import edu.bu.cs622.jlitebox.image.Image;

import java.util.Optional;

/**
 * Image metadata extractor
 *
 * @author dlegaspi@bu.edu
 */
public interface ImageMetadataExtractor {
    /**
     * get the metadata from the image
     *
     * @param image the image
     * @return the image metadata
     */
    Optional<ImageMetadata> parse(Image image) throws JLiteBoxException;
}
