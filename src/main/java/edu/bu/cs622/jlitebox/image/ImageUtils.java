package edu.bu.cs622.jlitebox.image;

import com.google.common.base.Splitter;
import edu.bu.cs622.jlitebox.utils.AppUtils;

public final class ImageUtils {
    public static final String JPG = "JPG";
    public static final String JPEG = "JPEG";

    /**
     * Is this a JPEG image?
     *
     * @param path path
     * @return true if JPG
     */
    public static boolean isJpegImage(String path) {
        return AppUtils.getFilenameExt(path).equalsIgnoreCase(JPEG)
                        || AppUtils.getFilenameExt(path).equalsIgnoreCase(JPG);
    }

    /**
     * Focal length to int. The focal length string is usually in the form
     * "${digits} mm" and we return the ${digits} part as float.
     *
     * @param focalLength the focal length
     * @return float equivalent of FL
     */
    public static float normalizeFocalLength(String focalLength) {
        try {
            if (focalLength.contains("mm")) {
                return Splitter.on(" ")
                                .splitToList(focalLength)
                                .stream()
                                .findFirst()
                                .map(Float::parseFloat)
                                .orElse((float) 0);
            } else {
                return Float.parseFloat(focalLength);
            }
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Aperture to float. The aperture is usually in the form "f/${digits}" and we
     * return the ${digits} part as float.
     *
     * @param aperture string
     * @return the aperture
     */
    public static float normalizeAperture(String aperture) {
        try {
            if (aperture.startsWith("f/")) {
                return Float.parseFloat(Splitter.on("/").splitToList(aperture).get(1));
            } else {
                return Float.parseFloat(aperture);
            }
        } catch (Exception e) {
            return 0;
        }
    }
}
