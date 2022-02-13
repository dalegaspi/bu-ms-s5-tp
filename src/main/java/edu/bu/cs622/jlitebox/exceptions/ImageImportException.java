package edu.bu.cs622.jlitebox.exceptions;

/**
 * Image import exception
 *
 * @author dlegaspi@bu.edu
 */
public class ImageImportException extends RecoverableException {
    public ImageImportException() {
        super(true);
    }

    public ImageImportException(String s) {
        super(s, true);
    }

    public ImageImportException(String s, Throwable t) {
        super(s, t, true);
    }
}
