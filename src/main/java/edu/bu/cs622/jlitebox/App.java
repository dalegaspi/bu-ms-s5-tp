package edu.bu.cs622.jlitebox;

import java.io.IOException;

/**
 * Application entry point
 *
 * The heart of the app is in AppFx.  We are doing this because JavaFx abides by the Java 9 modularity
 * https://edencoding.com/runtime-components-error/
 *
 * @see AppFx
 * @author dlegaspi@bu.edu
 */
public class App {
    public static String APP_NAME = "JLiteBox";

    public static void main(String[] args) throws IOException {
        AppFx.main(args);
    }
}
