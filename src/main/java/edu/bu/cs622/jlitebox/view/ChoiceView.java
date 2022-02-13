package edu.bu.cs622.jlitebox.view;

import java.util.List;

/**
 * Choices
 *
 * @author dlegaspi@bu.edu
 */
public interface ChoiceView {

    /**
     * Select a choice
     *
     * @param message message
     * @param choices list of choices
     * @return selected choice
     */
    String select(String message, List<String> choices);
}
