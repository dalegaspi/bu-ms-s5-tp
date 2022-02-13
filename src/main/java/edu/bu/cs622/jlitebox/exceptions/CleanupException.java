package edu.bu.cs622.jlitebox.exceptions;

/**
 * Clean-up exception usually something to do with minor I/O issues.
 *
 * @author dlegaspi@bu.edu
 */
public class CleanupException extends NonRecoverableException {
    public CleanupException() {
        super(false);
    }

    public CleanupException(String s) {
        super(s, false);
    }

    public CleanupException(String s, Throwable t) {
        super(s, t, false);
    }
}
