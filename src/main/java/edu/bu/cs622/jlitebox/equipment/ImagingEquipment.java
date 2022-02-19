package edu.bu.cs622.jlitebox.equipment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import edu.bu.cs622.jlitebox.image.metadata.ImageMetadata;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Optional;

/**
 * base abstract class for ImagingEquipment
 *
 * @author dlegaspi@bu.edu
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
                @JsonSubTypes.Type(value = Camera.class, name = "Camera"),
                @JsonSubTypes.Type(value = Lens.class, name = "Lens") })
public abstract class ImagingEquipment {
    private String brand;
    private String model;
    private String id;

    public String getId() {
        return id;
    }

    private static String createId(String brand, String model) {
        var hash = DigestUtils.sha1Hex(Optional.ofNullable(brand).orElse("None") +
                        Optional.ofNullable(model).orElse("None"));

        return hash;
    }

    /**
     * Constructor
     *
     * @param brand the name of the imaging equipment
     * @param model description
     */
    public ImagingEquipment(String brand, String model) {
        this.brand = brand;
        this.model = model;
        this.id = createId(brand, model);
    }

    /**
     * Returns unique name
     *
     * @return name
     */
    public String getBrand() {
        return brand;
    }

    /**
     * Description of the equipment
     *
     * @return
     */
    public String getModel() {
        return model;
    }
}
