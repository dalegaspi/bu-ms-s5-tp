package edu.bu.cs622.jlitebox.view;

import java.util.Scanner;

/**
 * User keyboard input
 *
 * @author dlegaspi@bu.edu
 */
public class UserKeyboardInputSource implements UserInputSource {
    Scanner scanner = new Scanner(System.in);

    @Override
    public String getInput() {
        return scanner.next();
    }
}
