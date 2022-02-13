package edu.bu.cs622.jlitebox.equipment;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the equipment models
 *
 * @author dlegaspi@bu.edu
 */
public class ImagingEquipmentModelTests {

    @Test
    public void testImagingEquipment() {
        var camera = new Camera("Nikon Z6", "A Nikon Z6 Camera", false);
        var lens = new Lens("Nikon Z 70-200", "A Nikon Lens", 200);

        assertTrue(camera.getModel().length() > 0 && camera.getBrand().length() > 0);
        assertTrue(lens.getModel().length() > 0 && camera.getBrand().length() > 0);
        assertNotNull(lens.toString());
        assertNotNull(camera.toString());

        assertEquals(lens.getFocalLength(), 200);
        assertFalse(camera.isAutofocus());
    }
}
