package edu.bu.cs622.jlitebox.image.metadata;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.bu.cs622.jlitebox.equipment.Camera;
import edu.bu.cs622.jlitebox.equipment.ImagingEquipment;
import edu.bu.cs622.jlitebox.equipment.Lens;
import edu.bu.cs622.jlitebox.image.ImageUtils;
import edu.bu.cs622.jlitebox.utils.AppUtils;
import io.vavr.control.Try;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Image Metadata class
 *
 * @author dlegaspi@bu.edu
 */
public final class ImageMetadata implements Serializable {

    private final List<ImagingEquipment> equipmentList = new ArrayList<>();
    private Map<String, String> rawData;
    private float aperture;
    private float shutterSpeed;
    private Date captureDate;
    private int iso;

    private Optional<String> getValue(String key) {
        return AppUtils.getValue(rawData, key);
    }

    private Optional<Camera> identifyCamera() {
        var brand = getValue("CameraMaker");
        var model = getValue("CameraModel");

        // we set it as auto-focus camera unless it's a Leica
        var isAutoFocus = brand.map(b -> !b.equalsIgnoreCase("Leica")).orElse(false);

        return brand.map(b -> b.isBlank() ? null : new Camera(b, model.orElse(""), isAutoFocus));
    }

    private Optional<Lens> identifyLens() {
        var brand = getValue("LensMake");
        var model = getValue("Lens");
        var focalLength = getValue("Focal length")
                        .map(ImageUtils::normalizeFocalLength)
                        .orElse((float) 0);

        return brand.map(b -> b.isBlank() ? null : new Lens(b, model.orElse(""), focalLength));
    }

    @JsonCreator
    public ImageMetadata(@JsonProperty("rawData") Map<String, String> rawData) {
        this.rawData = rawData;

        // identify the camera and/or lens if it's in the metadata map
        identifyCamera().ifPresent(camera -> equipmentList.add(camera));
        identifyLens().ifPresent(lens -> equipmentList.add(lens));

        // map the values in the metadata map to the corresponding fields of the image
        // matadat object
        getValue("Aperture")
                        .ifPresent(a -> aperture = ImageUtils.normalizeAperture(a));
        getValue("Shutter")
                        .ifPresent(s -> shutterSpeed = Try.of(() -> Float.parseFloat(s)).getOrElse((float) 0));
        getValue("ISO speed")
                        .ifPresent(s -> iso = Try.of(() -> (int) Float.parseFloat(s)).getOrElse(0));
        getValue("Timestamp (EpocheSec)")
                        .ifPresent(t -> captureDate = Try.of(() -> new Date(Long.parseLong(t) * 1000))
                                        .getOrElse(new Date()));
    }

    /**
     * Equipment used to capture the image
     *
     * @return collection of equipment
     */
    @JsonIgnore
    public List<ImagingEquipment> getEquipmentUsed() {
        return equipmentList;
    }

    /**
     * Return the camera used in this image if exists
     *
     * @return the camera as optional
     */
    @JsonIgnore
    public Optional<Camera> getCamera() {
        return equipmentList.stream()
                        .filter(e -> e instanceof Camera)
                        .map(e -> (Camera) e)
                        .findFirst();
    }

    /**
     * Returns true if it has camera information
     *
     * @return true if it has camera information
     */
    @JsonIgnore
    public boolean hasCamera() {
        return getCamera().isPresent();
    }

    /**
     * Return the lens used in this image if exists
     *
     * @return the lens as optional
     */
    @JsonIgnore
    public Optional<Lens> getLens() {
        return equipmentList.stream()
                        .filter(e -> e instanceof Lens)
                        .map(e -> (Lens) e)
                        .findFirst();
    }

    /**
     * Returns true if it has lens information
     *
     * @return true if it has lens information
     */
    @JsonIgnore
    public boolean hasLens() {
        return getLens().isPresent();
    }

    /**
     * Raw data
     *
     * @return the raw data
     */
    public Map<String, String> getRawData() {
        return rawData;
    }

    public float getAperture() {
        return aperture;
    }

    public float getShutterSpeed() {
        return shutterSpeed;
    }

    public Date getCaptureDate() {
        return captureDate;
    }

    public int getIso() {
        return iso;
    }

    @JsonIgnore
    public String getStringForLabelDisplay() {
        return String.format("Aperture: f/%.1f\nShutter Speed: 1/%ds\nCamera: %s\nLens: %s",
                        getAperture(),
                        (int) getShutterSpeed(),
                        getCamera().map(Camera::toString).orElse("None"),
                        getLens().map(Lens::toString).orElse("None"));
    }

    @JsonIgnore
    public String getFormattedRawMetadata() {
        return rawData.entrySet().stream()
                .filter(e -> !e.getKey().equalsIgnoreCase("XMP"))
                .map(e -> String.format("%s: %s", e.getKey(), e.getValue()))
                .collect(Collectors.joining("\n"));
    }
}
