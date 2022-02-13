package edu.bu.cs622.jlitebox.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.bu.cs622.jlitebox.config.ImageCatalogConfiguration;
import edu.bu.cs622.jlitebox.equipment.Camera;
import edu.bu.cs622.jlitebox.equipment.Lens;
import edu.bu.cs622.jlitebox.exceptions.ImageImportException;
import edu.bu.cs622.jlitebox.exceptions.ImageOperationException;
import edu.bu.cs622.jlitebox.image.Image;
import edu.bu.cs622.jlitebox.image.ImageUtils;
import edu.bu.cs622.jlitebox.image.JpegImage;
import edu.bu.cs622.jlitebox.image.RawImage;
import edu.bu.cs622.jlitebox.image.metadata.ImageMetadata;
import edu.bu.cs622.jlitebox.image.preview.ImagePreviewGenerator;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

/**
 * Metadata/Preview storage stored in SQLite
 *
 * @see ImageMetadataStorage
 * @author dlegaspi@bu.edu
 */
public final class DatabaseImageMetadataStorage implements ImageMetadataStorage, Closeable {
    private static Logger logger = LoggerFactory.getLogger(DatabaseImageMetadataStorage.class);

    // the Jackson serializer
    private final ObjectMapper mapper;
    private final ImagePreviewGenerator previewGenerator;

    Connection conn;

    @Inject
    public DatabaseImageMetadataStorage(ImageCatalogConfiguration config, ImagePreviewGenerator previewGenerator)
                    throws SQLException {
        this.mapper = new ObjectMapper();
        this.previewGenerator = previewGenerator;
        logger.info("Connecting to {}", config.getDatabaseUrl());
        this.conn = DriverManager.getConnection(config.getDatabaseUrl());
    }

    private String toJson(ImageMetadata metadata) {
        return Try.of(() -> mapper.writeValueAsString(metadata)).getOrNull();
    }

    private byte[] toByteArray(javafx.scene.image.Image preview) {
        return Try.of(() -> previewGenerator.convertPreviewToByteArray(preview)).getOrNull();
    }

    // this is the "upsert" statement; if the image name already exists in the
    // database, we just update the metadata hence the ON CONFLICT(name) clause
    static final String UPSERT_PSTATEMENT = "insert into image_metadata(name, src_path, image_type, camera_brand, " +
                    "camera_model, camera_autofocus," +
                    "lens_brand, lens_model, lens_focal_length, shutter_speed, " +
                    "capture_date, iso, image_preview, raw_metadata) " +
                    "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) on conflict(name) do update " +
                    "set src_path = excluded.src_path, " +
                    "image_type = excluded.image_type, " +
                    "camera_brand = excluded.camera_brand, " +
                    "camera_model = excluded.camera_model, " +
                    "camera_autofocus = excluded.camera_autofocus, " +
                    "lens_brand = excluded.lens_brand, " +
                    "lens_model = excluded.lens_model, " +
                    "lens_focal_length = excluded.lens_focal_length, " +
                    "shutter_speed = excluded.shutter_speed, " +
                    "capture_date = excluded.capture_date, " +
                    "iso = excluded.iso, " +
                    "image_preview = excluded.image_preview, " +
                    "raw_metadata = excluded.raw_metadata";

    /**
     * save to database
     *
     * @param image        the image
     * @param previewBytes the byte array for the preview
     * @param jsonMetadata metadata in json format
     * @return return false if failed
     */
    private boolean saveToDatabase(Image image, byte[] previewBytes, String jsonMetadata) {
        try (var pstmt = conn.prepareStatement(UPSERT_PSTATEMENT)) {
            var meta = image.getMetadata();
            pstmt.setString(1, image.getName());
            pstmt.setString(2, image.getOriginalSrcPath());
            pstmt.setString(3, image.getType());
            pstmt.setString(4, meta.getCamera().map(Camera::getBrand).orElse(null));
            pstmt.setString(5, meta.getCamera().map(Camera::getModel).orElse(null));
            pstmt.setInt(6, meta.getCamera().map(c -> c.isAutofocus() ? 1 : 0).orElse(0));
            pstmt.setString(7, meta.getLens().map(Lens::getBrand).orElse(null));
            pstmt.setString(8, meta.getLens().map(Lens::getModel).orElse(null));
            pstmt.setFloat(9, meta.getLens().map(Lens::getFocalLength).orElse((float) 0));
            pstmt.setFloat(10, meta.getShutterSpeed());
            pstmt.setInt(11, meta.getCaptureDate() != null ? (int) meta.getCaptureDate().getTime()
                            : (int) (new Date()).getTime());
            pstmt.setInt(12, meta.getIso());
            pstmt.setBytes(13, previewBytes);
            pstmt.setString(14, jsonMetadata);
            logger.info("{} metadata and preview successfully saved to database", image.getName());
            pstmt.execute();
        } catch (SQLException e) {
            logger.error("Error in saving metadata for {}: {}", image.getName(), e.getMessage(), e);
        }

        return true;
    }

    @Override
    public boolean saveMetadata(Collection<Image> images) {
        var allSaved = images.stream().allMatch(i -> {
            logger.info("Saving metadata for {}", i.getName());

            var previewBytes = i.getPreview().map(this::toByteArray).orElse(null);
            var json = toJson(i.getMetadata());
            logger.info("JSON for {}: {}", i.getName(), json);

            return saveToDatabase(i, previewBytes, json);
        });

        return allSaved;
    }

    /**
     * Be a good citizen and close the db connection.
     *
     * @throws IOException I/O exception
     */
    @Override
    public void close() throws IOException {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            logger.error("Exception on close(): {}", e.getMessage(), e);
        }
    }

    /**
     * internal class for loading the data from database
     */
    static class DatabaseRecord {
        byte[] previewBytes;
        String jsonMetadata;
        Image image;

        DatabaseRecord(String srcPath, String imgType, byte[] previewBytes, String jsonMetadata) {
            this.image = !imgType.equalsIgnoreCase(ImageUtils.JPG) ? new RawImage(srcPath) : new JpegImage(srcPath);
            this.jsonMetadata = jsonMetadata;
            this.previewBytes = previewBytes;
        }
    }

    static final String QUERY_ONE_IMAGE_PSTATEMENT = "select src_path, image_type, raw_metadata, image_preview from image_metadata where name = ?";

    /**
     * Load from the database. Returns the Image with (partial) metadata along with
     * the preview bytes and json metadata
     *
     * @param name the name to look up
     * @return image metadata record from db
     */
    private Optional<DatabaseRecord> loadFromDatabase(String name) {
        try (var stmt = conn.prepareStatement(QUERY_ONE_IMAGE_PSTATEMENT)) {
            stmt.setString(1, name);
            var rs = stmt.executeQuery();

            if (rs.next()) {
                // pull the columns
                var src = rs.getString(1);
                var itype = rs.getString(2);
                var jsonMetadata = rs.getString(3);
                var previewBytes = rs.getBinaryStream(4).readAllBytes();
                var rec = new DatabaseRecord(src, itype, previewBytes, jsonMetadata);
                return Optional.of(rec);
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.error("Unable to retrieve {} from db due to SQL error: {}", name, e.getMessage(), e);
            return Optional.empty();
        } catch (IOException e) {
            logger.error("Unable to retrieve {} from db due to I/O error: {}", name, e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Image> loadImage(String name) {
        var rec = loadFromDatabase(name).map(r -> {
            try {
                // deserialize the metadata from JSON to ImageMetadata class using Jackson
                var metadata = mapper.readValue(r.jsonMetadata, ImageMetadata.class);

                // create the preview image from the bytes from blob in record
                var preview = previewGenerator.createPreviewFromByteArray(r.previewBytes);
                r.image.setMetadata(metadata);
                r.image.setPreview(preview);
                return r.image;
            } catch (JsonProcessingException e) {
                logger.error("Error loading JSON metadata for {}", name, e);
                return null;
            } catch (ImageImportException e) {
                logger.error("Error loading preview for {}", name, e);
                return null;
            }
        });

        logger.info("Successfully loaded from db: {}", name);
        return rec;
    }

    static final String QUERY_GET_ALL_NAMES = "select name from image_metadata";

    @Override
    public List<String> getImageNames() throws ImageOperationException {
        var names = new ArrayList<String>();
        try (var stmt = conn.createStatement()) {
            var rs = stmt.executeQuery(QUERY_GET_ALL_NAMES);
            while (rs.next()) {
                names.add(rs.getString(1));
            }
        } catch (SQLException e) {
            logger.error("Query exception for names: {}", e.getMessage(), e);
            throw new ImageOperationException("Unable to get the image names due to database error", e);
        }

        logger.info("There are {} images in the database", names.size());
        return names;
    }
}
