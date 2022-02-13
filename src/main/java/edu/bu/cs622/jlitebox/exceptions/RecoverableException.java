package edu.bu.cs622.jlitebox.exceptions;

/**
 * Recoverable exceptions allows the system to propagate to the UI for
 * confirmation from the user (e.g., modal dialog box) to recover from an error
 * or abort the operation altogether
 *
 * @author dlegapi@bu.edu
 */
public class RecoverableException extends JLiteBoxException {
    public RecoverableException(String s, boolean interact) {
        super(s, interact);
    }

    public RecoverableException(boolean interact) {
        super(interact);
    }

    public RecoverableException(String s, Throwable t, boolean interact) {
        super(s, t, interact);
    }
}
