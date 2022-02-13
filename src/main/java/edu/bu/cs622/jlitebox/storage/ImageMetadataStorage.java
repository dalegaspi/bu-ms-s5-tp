package edu.bu.cs622.jlitebox.storage;

import edu.bu.cs622.jlitebox.exceptions.ImageImportException;
import edu.bu.cs622.jlitebox.exceptions.ImageOperationException;
import edu.bu.cs622.jlitebox.image.Image;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Image metadata storage (database)
 *
 * @author dlegaspi@bu.edu
 */
public interface ImageMetadataStorage {

    /**
     * Save metadata for list of images
     *
     * @param images the images
     * @return true if successful
     */
    boolean saveMetadata(Collection<Image> images);

    /**
     * save the metadata of this image
     * 
     * @param image the image
     * @return true if successful
     */
    default boolean saveMetadata(Image image) {
        return saveMetadata(List.of(image));
    }

    /**
     * load image of specified name
     *
     * @param name the name
     * @return the corresponding
     */
    Optional<Image> loadImage(String name);

    /**
     * get all the image names
     *
     * @return the image name list
     */
    List<String> getImageNames() throws ImageOperationException;
}
