package edu.bu.cs622.jlitebox.equipment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;

/**
 * Camera used to capture the image
 *
 * @see ImagingEquipment
 * @author dlegaspi@bu.edu
 */
public class Camera extends ImagingEquipment {
    private final boolean isAutofocus;

    /**
     * Camera
     *
     * @param brand camera name
     * @param model description
     */
    @JsonCreator
    public Camera(@JsonProperty("brand") String brand,
                    @JsonProperty("model") String model,
                    @JsonProperty("autofocus") boolean isAutofocus) {
        super(brand, model);
        this.isAutofocus = isAutofocus;
    }

    @Override
    public String toString() {
        if (Strings.isNullOrEmpty(this.getBrand())) {
            return "Unknown Camera";
        } else if (Strings.isNullOrEmpty(this.getModel())) {
            return this.getBrand() + " Camera";
        } else
            return this.getBrand() + " " + this.getModel();
    }

    public boolean isAutofocus() {
        return isAutofocus;
    }
}
