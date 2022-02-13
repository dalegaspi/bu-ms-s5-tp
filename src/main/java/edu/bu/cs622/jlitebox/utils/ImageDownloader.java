package edu.bu.cs622.jlitebox.utils;

import edu.bu.cs622.jlitebox.exceptions.CleanupException;
import edu.bu.cs622.jlitebox.exceptions.JLiteBoxException;

/**
 * Downloads an image from a remote location
 *
 * @author dlegaspi@bu.edu
 */
public interface ImageDownloader {
    /**
     * download image to a local file
     *
     * @param url the url
     * @throws JLiteBoxException download exceptions
     * @return the location of the file where downloaded file is written to
     */
    String download(String url) throws JLiteBoxException;

    /**
     * cleanup
     */
    void cleanup() throws CleanupException;
}
