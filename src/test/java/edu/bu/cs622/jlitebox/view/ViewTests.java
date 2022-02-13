package edu.bu.cs622.jlitebox.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.when;

/**
 * Testing the view objects
 *
 * @author dlegaspi@bu.edu
 */
@ExtendWith(MockitoExtension.class)
public class ViewTests {

    @Mock
    private UserInputSource inputSource;

    @BeforeEach
    public void beforeEach() {
        MockitoAnnotations.openMocks(this);
        when(inputSource.getInput()).thenReturn("y");
    }

    @Test
    public void testImageImporterConsoleView() {
        var view = new ImageImporterConsoleView(inputSource);
        view.emit("hello, world!");

        var response = view.select("input", List.of("y", "n"));
        assertEquals("y", response);
        assertNotNull(view);
    }
}
