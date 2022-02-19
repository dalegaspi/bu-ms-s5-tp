package edu.bu.cs622.jlitebox.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.bu.cs622.jlitebox.config.BasicImageCatalogConfiguration;
import edu.bu.cs622.jlitebox.config.ImageCatalogConfiguration;
import edu.bu.cs622.jlitebox.exceptions.ImageImportException;
import edu.bu.cs622.jlitebox.image.Image;
import edu.bu.cs622.jlitebox.image.ImageFactory;
import edu.bu.cs622.jlitebox.image.JpegImage;
import edu.bu.cs622.jlitebox.image.RawImage;
import edu.bu.cs622.jlitebox.image.metadata.ImageMetadata;
import edu.bu.cs622.jlitebox.image.preview.ImagePreviewGenerator;
import edu.bu.cs622.jlitebox.image.preview.LibRawImagePreviewGenerator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.librawfx.RAWImageLoaderFactory;
import org.testfx.framework.junit5.ApplicationTest;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

/**
 * SQLite tests with image blobs
 *
 * @author dlegaspi@bu.edu
 */
public class SqliteWithBlobTests extends ApplicationTest {

    static ImagePreviewGenerator previewGenerator;
    static ImageCatalogConfiguration config;
    static Connection conn;

    @AfterAll
    public static void tearDown() throws SQLException {
        conn.close();
    }

    @BeforeAll
    public static void setUp() throws Exception {
        RAWImageLoaderFactory.install();
        config = new BasicImageCatalogConfiguration();
        previewGenerator = new LibRawImagePreviewGenerator(config);
        var url = "jdbc:sqlite:db/jlitebox-test.sqlite";
        conn = DriverManager.getConnection(url);
    }

    private void assertReadWrite(Image image, javafx.scene.image.Image preview)
                    throws SQLException, ImageImportException, IOException {
        byte[] writeBytes = previewGenerator.convertPreviewToByteArray(preview);
        Assertions.assertTrue(writeBytes.length > 0);
        var q = "insert into image(name, src_path, image_type, image_preview) " +
                        "values (?, ?, ?, ?) on conflict(name) do update " +
                        "set src_path = excluded.src_path, " +
                        "image_type = excluded.image_type, " +
                        "image_preview = excluded.image_preview";
        var pstmt = conn.prepareStatement(q);
        pstmt.setString(1, image.getName());
        pstmt.setString(2, image.getOriginalSrcPath());
        pstmt.setString(3, image.getType());
        pstmt.setBytes(4, writeBytes);
        pstmt.execute();

        var q2 = "select image_preview from image where name = ?";
        var pstmt2 = conn.prepareStatement(q2);
        pstmt2.setString(1, image.getName());
        var rs = pstmt2.executeQuery();
        Assertions.assertTrue(rs.next());

        byte[] readBytes = rs.getBinaryStream(1).readAllBytes();
        Assertions.assertTrue(readBytes.length > 0);

        var dimage = previewGenerator.createPreviewFromByteArray(readBytes);
        Assertions.assertNotNull(dimage);
    }

    @Test
    public void testConnectAndInsertRawImagePreviewBlob() throws SQLException, ImageImportException, IOException {
        var path = getClass().getClassLoader().getResource("images/DSC_2304.NEF").getPath();
        var image = ImageFactory.fromFile(path);
        var preview = previewGenerator.generatePreview((RawImage) image);

        assertReadWrite(image, preview);
    }

    @Test
    public void testConnectAndInsertJpegImagePreviewBlob() throws SQLException, ImageImportException, IOException {
        var path = getClass().getClassLoader().getResource("images/IMAGE-01.JPG").getPath();
        var image = ImageFactory.fromFile(path);
        var preview = previewGenerator.resizePreview((JpegImage) image);

        assertReadWrite(image, preview);
    }
}
