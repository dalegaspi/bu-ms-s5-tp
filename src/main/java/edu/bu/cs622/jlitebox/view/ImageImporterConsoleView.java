package edu.bu.cs622.jlitebox.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

/**
 * Console-based importer view
 *
 * @author dlegaspi@bu.edu
 */
public class ImageImporterConsoleView implements ImageImporterView {
    private static Logger logger = LoggerFactory.getLogger(ImageImporterConsoleView.class);

    private final UserInputSource inputSource;

    public static Optional<String> choiceSelected(String choice, List<String> choices) {
        return choices.stream().filter(c -> c.equalsIgnoreCase(choice)).findAny();
    }

    @Inject
    ImageImporterConsoleView(UserInputSource inputSource) {
        this.inputSource = inputSource;
    }

    @Override
    public String select(String message, List<String> choices) {
        String c = "";
        var choicesAsString = String.join(", ", choices);

        // prompts the user for input while the choice is not in the list of choices
        // specified by parameter
        while (choiceSelected(c, choices).isEmpty()) {
            System.out.print(message + " [" + choicesAsString + "]? ");
            c = inputSource.getInput();
        }

        return c;
    }

    @Override
    public void emit(String format, Object... args) {
        logger.info(format, args);
    }
}
