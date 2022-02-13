package edu.bu.cs622.jlitebox.exceptions;

/**
 * Image import exception
 *
 * @author dlegaspi@bu.edu
 */
public class ImageOperationException extends RecoverableException {
    public ImageOperationException() {
        super(true);
    }

    public ImageOperationException(String s) {
        super(s, true);
    }

    public ImageOperationException(String s, Throwable t) {
        super(s, t, true);
    }
}
