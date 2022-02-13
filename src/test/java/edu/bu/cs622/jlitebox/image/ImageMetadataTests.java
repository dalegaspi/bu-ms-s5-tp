package edu.bu.cs622.jlitebox.image;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.bu.cs622.jlitebox.exceptions.JLiteBoxException;
import edu.bu.cs622.jlitebox.image.metadata.ImageMetadata;
import edu.bu.cs622.jlitebox.image.metadata.LibRawMetadataExtractor;
import io.vavr.control.Try;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.librawfx.RAWImageLoaderFactory;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Image metadata tests
 *
 * @author dlegaspi@bu.edu
 */
public class ImageMetadataTests extends ApplicationTest {
    @BeforeEach
    public void setUp() throws Exception {
        RAWImageLoaderFactory.install();
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private void assertMetadata(Optional<ImageMetadata> metadata) {
        assertTrue(metadata.isPresent());
        assertTrue(metadata.map(ImageMetadata::getEquipmentUsed).isPresent());
        assertTrue(metadata.map(ImageMetadata::getRawData).isPresent());
        assertNotNull(metadata.map(ImageMetadata::hasLens));
        assertNotNull(metadata.map(ImageMetadata::hasCamera));
        assertNotNull(metadata.map(ImageMetadata::hasLens));
        assertNotNull(metadata.map(ImageMetadata::getAperture));
        assertNotNull(metadata.map(ImageMetadata::getIso));
        assertNotNull(metadata.map(ImageMetadata::getCaptureDate));
        assertNotNull(metadata.map(ImageMetadata::getShutterSpeed));
    }

    @Test
    public void testLibRawMetadataExtractor() throws JLiteBoxException {
        var path = getClass().getClassLoader().getResource("images/L1009981.DNG").getPath();
        var image = ImageFactory.fromFile(path);
        var extractor = new LibRawMetadataExtractor();

        var metadata = extractor.parse(image);
        assertMetadata(metadata);
    }

    @Test
    public void testLibRawMetadataExtractor2() throws JLiteBoxException {
        var path = getClass().getClassLoader().getResource("images/R0000978.DNG").getPath();
        var image = ImageFactory.fromFile(path);
        var extractor = new LibRawMetadataExtractor();

        var metadata = extractor.parse(image);
        assertMetadata(metadata);
    }

    @Test
    public void testLibRawMetadataExtractor3() throws JLiteBoxException {
        var path = getClass().getClassLoader().getResource("images/DSC_2304.NEF").getPath();
        var image = ImageFactory.fromFile(path);
        var extractor = new LibRawMetadataExtractor();

        var metadata = extractor.parse(image);
        assertMetadata(metadata);
    }

    @Test
    public void testMetadataSerialization() throws  JLiteBoxException {
        var path = getClass().getClassLoader().getResource("images/DSC_2304.NEF").getPath();
        var image = ImageFactory.fromFile(path);
        var extractor = new LibRawMetadataExtractor();

        var metadata = extractor.parse(image);
        assertMetadata(metadata);

        ObjectMapper mapper = new ObjectMapper();
        var json = metadata.map(m -> Try.of(() -> mapper.writeValueAsString(m)).getOrNull());

        assertTrue(json.isPresent());
    }
}
