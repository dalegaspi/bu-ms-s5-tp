package edu.bu.cs622.jlitebox.controller;

import edu.bu.cs622.jlitebox.exceptions.JLiteBoxException;
import edu.bu.cs622.jlitebox.image.ImageCatalog;

/**
 * Image Import Controller
 *
 * @author dlegaspi@bu.edu
 */
public interface ImageImportController {

    /**
     * import a list of images from a file. The list will be a list of file paths or
     * urls
     *
     * @param filename the file containing the list
     * @return the number of files imported
     */
    int importImagesFromFile(String filename, ImageCatalog.ImageImportOptions options) throws JLiteBoxException;

    /**
     * import a list of images from a directory.
     *
     * @param path    the root dir
     * @param options options
     * @return number of images imported
     */
    int importImagesFromDirectory(String path, ImageCatalog.ImageImportOptions options) throws JLiteBoxException;
}
