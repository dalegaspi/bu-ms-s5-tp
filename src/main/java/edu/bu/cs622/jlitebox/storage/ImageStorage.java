package edu.bu.cs622.jlitebox.storage;

import edu.bu.cs622.jlitebox.exceptions.JLiteBoxException;
import edu.bu.cs622.jlitebox.image.Image;

import java.util.Collection;
import java.util.List;

/**
 * @author dlegaspi@bu.edu
 */
public interface ImageStorage {
    /**
     * Save images to a catalog
     *
     * @param images
     * @return true if
     */
    boolean save(Collection<Image> images) throws JLiteBoxException;

    /**
     * save an image
     *
     * @param image the image to save
     */
    default boolean save(Image image) throws JLiteBoxException {
        return save(List.of(image));
    }

    /**
     * The root directory
     *
     * @return the root directory
     */
    String getRootDirectory();
}
