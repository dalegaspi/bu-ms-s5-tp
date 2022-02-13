package edu.bu.cs622.jlitebox.equipment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;

/**
 * Lens used for capturing image
 *
 * @see ImagingEquipment
 * @author dlegaspi@bu.edu
 */
public class Lens extends ImagingEquipment {
    private final float focalLength;

    /**
     * Lens
     *
     * @param brand name
     * @param model description
     */
    @JsonCreator
    public Lens(@JsonProperty("brand") String brand,
                    @JsonProperty("model") String model,
                    @JsonProperty("focalLength") float focalLength) {
        super(brand, model);
        this.focalLength = focalLength;
    }

    @Override
    public String toString() {
        if (Strings.isNullOrEmpty(this.getBrand())) {
            return "Unknown Lens";
        } else if (Strings.isNullOrEmpty(this.getModel())) {
            return this.getBrand() + " Lens";
        } else
            return this.getBrand() + " " + this.getModel();
    }

    /**
     * return the focal length
     *
     * @return focal length
     */
    public float getFocalLength() {
        return focalLength;
    }
}
