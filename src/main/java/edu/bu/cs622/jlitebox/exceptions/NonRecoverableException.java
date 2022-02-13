package edu.bu.cs622.jlitebox.exceptions;

/**
 * Non-recoverable exceptions are exceptions where the error can be ignored and
 * will not require confirmation/interaction from the user
 *
 * @author dlegaspi@bu.edu
 */
public class NonRecoverableException extends JLiteBoxException {
    public NonRecoverableException(String s, boolean interact) {
        super(s, interact);
    }

    public NonRecoverableException(boolean interact) {
        super(interact);
    }

    public NonRecoverableException(String s, Throwable t, boolean interact) {
        super(s, t, interact);
    }
}
