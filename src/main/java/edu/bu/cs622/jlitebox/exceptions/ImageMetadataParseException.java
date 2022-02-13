package edu.bu.cs622.jlitebox.exceptions;

/**
 * Image metadata parsing exception
 *
 * @author dlegaspi@bu.edu
 */
public class ImageMetadataParseException extends NonRecoverableException {
    public ImageMetadataParseException() {
        super(false);
    }

    public ImageMetadataParseException(String s) {
        super(s, false);
    }

    public ImageMetadataParseException(String s, Throwable t) {
        super(s, t, false);
    }
}
