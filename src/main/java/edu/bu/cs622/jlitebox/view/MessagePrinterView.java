package edu.bu.cs622.jlitebox.view;

/**
 * Prints message
 *
 * @author dlegaspi@bu.edu
 */
public interface MessagePrinterView {

    /**
     * Prints something
     *
     * @param format the format
     * @param args   params
     */
    void emit(String format, Object... args);
}
