package edu.bu.cs622.jlitebox.filter;

import edu.bu.cs622.jlitebox.image.Image;
import edu.bu.cs622.jlitebox.image.ImageCatalog;

import java.util.Optional;
import java.util.function.Function;

/**
 * Image content filter for filtering images. Uses builder pattern.
 *
 * @author dlegaspi@bu.edu
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class ImageContentFilter implements ContentFilter<Image> {
    private final ImageCatalog.LogicalOperator operator;

    // usually it's frowned upon to use Optionals in fields
    // but the reason we're doing this is to short-circuit the
    // testing faster; if the field is Optional.empty() we don't
    // even do a .map(...) to test.
    private final Optional<String> name;
    private final Optional<String> fileExt;
    private final Optional<String> cameraName;
    private final Optional<String> lensName;
    private final Optional<Float> aperture;
    private final Optional<Float> focalLength;
    private final Optional<Integer> iso;
    private final Optional<Float> shutterSpeed;

    /**
     * Builder for the ImageContentFilter
     */
    public static class ImageContentFilterBuilder {
        private final boolean isAnd;
        private String name;
        private String fileExt;
        private String cameraName;
        private String lensName;
        private float aperture;
        private int focalLength;
        private int iso;
        private float shutterSpeed;

        public ImageContentFilterBuilder(boolean isAndOperator) {
            this.isAnd = isAndOperator;
        }

        public ImageContentFilterBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public ImageContentFilterBuilder withFileExt(String fileExt) {
            this.fileExt = fileExt;
            return this;
        }

        public ImageContentFilterBuilder withCameraName(String cameraName) {
            this.cameraName = cameraName;
            return this;
        }

        public ImageContentFilterBuilder withLensName(String lensName) {
            this.lensName = lensName;
            return this;
        }

        public ImageContentFilterBuilder withAperture(float aperture) {
            this.aperture = aperture;
            return this;
        }

        public ImageContentFilterBuilder withFocalLength(int focalLength) {
            this.focalLength = focalLength;
            return this;
        }

        public ImageContentFilterBuilder withIso(int iso) {
            this.iso = iso;
            return this;
        }

        public ImageContentFilterBuilder withShutterSpeed(float shutterSpeed) {
            this.shutterSpeed = shutterSpeed;
            return this;
        }

        public ImageContentFilter build() {
            return new ImageContentFilter(isAnd,
                            name,
                            fileExt,
                            cameraName,
                            lensName,
                            aperture,
                            focalLength,
                            iso,
                            shutterSpeed);
        }
    }

    /**
     * Filter creation based on the criteria. This is not what is used to create it;
     * see the builder that's why it's a private contstructor
     *
     * @see ImageContentFilterBuilder
     * @param isAnd        is logical AND operation
     * @param name         image name
     * @param fileExt      file extension
     * @param cameraName   camera name
     * @param lensName     lens name
     * @param aperture     aperture
     * @param focalLength  focal length
     * @param iso          iso
     * @param shutterSpeed shutter speed
     */
    private ImageContentFilter(boolean isAnd,
                    String name,
                    String fileExt,
                    String cameraName,
                    String lensName,
                    float aperture,
                    float focalLength,
                    int iso, float shutterSpeed) {
        this.operator = isAnd ? ImageCatalog.LogicalOperator.AND : ImageCatalog.LogicalOperator.OR;
        this.name = Optional.ofNullable(name);
        this.fileExt = Optional.ofNullable(fileExt);
        this.cameraName = Optional.ofNullable(cameraName);
        this.lensName = Optional.ofNullable(lensName);
        this.focalLength = focalLength == 0 ? Optional.empty() : Optional.of(focalLength);
        this.iso = iso == 0 ? Optional.empty() : Optional.of(iso);
        this.aperture = aperture == 0 ? Optional.empty() : Optional.of(aperture);
        this.shutterSpeed = shutterSpeed == 0 ? Optional.empty() : Optional.of(shutterSpeed);
    }

    public boolean isAndOperation() {
        return this.operator == ImageCatalog.LogicalOperator.AND;
    }

    @Override
    public boolean test(Image image) {
        // the idea here is that image metadata is tested against the filter where the
        // filter field is not Optional.empty()
        // if the filter field exists and matches it will return true
        var meta = image.getMetadata();

        // matching of fields; we are using lambdas here to be more explicit
        // what fields we are matching and to avoid repetition of code
        // in the next section; also for readability
        Function<CharSequence, Boolean> nameMatch = v -> image.getName().contains(v);
        Function<CharSequence, Boolean> fileExtMatch = v -> image.getFilename().contains(v);
        Function<CharSequence, Boolean> cameraMatch = v -> meta.getCamera()
                        .map(c -> c.getBrand().contains(v))
                        .orElse(false);
        Function<CharSequence, Boolean> lensMatch = v -> meta.getLens()
                        .map(l -> l.getBrand().contains(v))
                        .orElse(false);
        Function<Float, Boolean> focalLengthMatch = v -> meta.getLens()
                        .map(l -> l.getFocalLength() == v)
                        .orElse(false);
        Function<Float, Boolean> shutterSpeedMatch = v -> meta.getShutterSpeed() == v;
        Function<Float, Boolean> apertureMatch = v -> meta.getAperture() == v;
        Function<Integer, Boolean> isoMatch = v -> meta.getIso() == v;

        if (isAndOperation()) {
            // all match

            return name.map(nameMatch).orElse(true)
                            && fileExt.map(fileExtMatch).orElse(true)
                            && cameraName.map(cameraMatch).orElse(true)
                            && lensName.map(lensMatch).orElse(true)
                            && focalLength.map(focalLengthMatch).orElse(true)
                            && shutterSpeed.map(shutterSpeedMatch).orElse(true)
                            && aperture.map(apertureMatch).orElse(true)
                            && iso.map(isoMatch).orElse(true);
        } else {
            // any match

            // since Java 9 .or() is composable and this is better than in previous
            // versions of Java otherwise we will have to use the construct above
            // similar to AND
            return name.map(nameMatch)
                            .or(() -> fileExt.map(fileExtMatch))
                            .or(() -> cameraName.map(cameraMatch))
                            .or(() -> lensName.map(lensMatch))
                            .or(() -> focalLength.map(focalLengthMatch))
                            .or(() -> shutterSpeed.map(shutterSpeedMatch))
                            .or(() -> aperture.map(apertureMatch))
                            .or(() -> iso.map(isoMatch))
                            .orElse(false);
        }
    }
}
