package edu.bu.cs622.jlitebox.exceptions;

/**
 * Base exception for the app.
 *
 * @author dlegaspi@bu.edu
 */
public class JLiteBoxException extends Exception {
    boolean interact = false;

    public JLiteBoxException(boolean interact) {
        super();
        this.interact = interact;
    }

    public JLiteBoxException(String s, boolean interact) {
        super(s);
        this.interact = interact;
    }

    public JLiteBoxException(String s, Throwable t, boolean interact) {
        super(s, t);
        this.interact = interact;
    }

    /**
     * This is a boolean to indicate if the exception requires
     * interaction/confirmation from the user to continue the operation where this
     * exception was thrown
     *
     * @return true if it requires interaction
     */
    public boolean requiresInteraction() {
        return this.interact;
    }
}
