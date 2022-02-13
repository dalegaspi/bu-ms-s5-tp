package edu.bu.cs622.jlitebox.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.bu.cs622.jlitebox.config.BasicImageCatalogConfiguration;
import edu.bu.cs622.jlitebox.config.ImageCatalogConfiguration;
import edu.bu.cs622.jlitebox.image.metadata.ImageMetadata;
import edu.bu.cs622.jlitebox.image.preview.ImagePreviewGenerator;
import edu.bu.cs622.jlitebox.image.preview.LibRawImagePreviewGenerator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.librawfx.RAWImageLoaderFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

/**
 * SQLite tests
 *
 * @author dlegaspi@bu.edu
 */
public class SqliteTests {

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

    /**
     * Query test with deserialization
     */
    @Test
    public void testConnectAndQuery() throws SQLException, IOException {
        var q = "select raw_metadata from image_metadata";
        var s = conn.createStatement();
        var rs = s.executeQuery(q);

        ObjectMapper mapper = new ObjectMapper();
        Assertions.assertTrue(rs.next());
        var metadata = mapper.readValue(new BufferedInputStream(rs.getBinaryStream(1)), ImageMetadata.class);
        Assertions.assertNotNull(metadata);
    }

    /**
     * Test insertion of JSON using I/O objects
     *
     * Unfortunately SQLite doesn't support setting a String/CHAR datatype column
     * using setBinaryInputStream (well, it can but it stores it as a string-ified
     * hexadecimal byte array instead of JSON) so we ultimately need to convert it
     * to String first before writing. So this is not what is done in the app this
     * is just mostly an experimental exercise on what JDBC options SQLite supports
     *
     * @throws SQLException SQL error
     * @throws IOException  Bin I/O error
     */
    @Test
    public void testConnectAndInsertJson() throws SQLException, IOException {
        PipedOutputStream pipedOutputStream = new PipedOutputStream();
        PipedInputStream pipedInputStream = new PipedInputStream();
        pipedInputStream.connect(pipedOutputStream);

        ObjectMapper mapper = new ObjectMapper();
        var meta = new ImageMetadata(Map.of());
        var q = "insert into image_metadata(name, src_path, image_type, raw_metadata) " +
                        "values (?, ?, ?, ?) on conflict(name) do update " +
                        "set src_path = excluded.src_path, " +
                        "image_type = excluded.image_type, " +
                        "raw_metadata = excluded.raw_metadata";
        var pstmt = conn.prepareStatement(q);
        pstmt.setString(1, "TEST-001");
        pstmt.setString(2, "/tmp/TEST-001.JPG");
        pstmt.setString(3, "JPG");
        mapper.writeValue(pipedOutputStream, meta);

        pstmt.setString(4, new String(pipedInputStream.readAllBytes()));
        pstmt.execute();
    }

    /**
     * Inserting JSON metadata as simple string
     *
     * @throws SQLException SQL error
     * @throws IOException  Bin I/O error
     */
    @Test
    public void testConnectAndInsertJsonSimple() throws SQLException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        var meta = new ImageMetadata(Map.of());
        var q = "insert into image_metadata(name, src_path, image_type, raw_metadata) " +
                        "values (?, ?, ?, ?) on conflict(name) do update " +
                        "set src_path = excluded.src_path, " +
                        "image_type = excluded.image_type, " +
                        "raw_metadata = excluded.raw_metadata";
        var pstmt = conn.prepareStatement(q);
        pstmt.setString(1, "TEST-002");
        pstmt.setString(2, "/tmp/TEST-002.JPG");
        pstmt.setString(3, "JPG");
        var json = mapper.writeValueAsString(meta);

        pstmt.setString(4, json);
        pstmt.execute();
    }
}
