package edu.bu.cs622.jlitebox.utils;

import edu.bu.cs622.jlitebox.config.ImageCatalogConfiguration;
import edu.bu.cs622.jlitebox.exceptions.CleanupException;
import edu.bu.cs622.jlitebox.exceptions.ImageImportException;
import edu.bu.cs622.jlitebox.exceptions.JLiteBoxException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * The ImageDownloader implementation that relies on Apache Commons to download
 * files from URL and copy to a temp destination file
 *
 * @author dlegaspi@bu.edu
 * @see ImageDownloader
 */
public class BasicImageDownloader implements ImageDownloader {
    private static final Logger logger = LoggerFactory.getLogger(BasicImageDownloader.class);

    private final ImageCatalogConfiguration config;
    private final String workingDir;
    static final String TEMP_SUB_PATH = "images";

    @Inject
    public BasicImageDownloader(ImageCatalogConfiguration configuration) {
        this.config = configuration;

        // the working dir is the temp_path + "/images"
        this.workingDir = AppUtils.createPath(config.getTempDirectory(), TEMP_SUB_PATH);
    }

    @Override
    public String download(String url) throws JLiteBoxException {
        var fileName = AppUtils.getFilename(url);
        var fullTmpDestPath = AppUtils.createPath(workingDir, fileName);

        try {
            // pull the image from the url and save it to the temp directory
            logger.info("Saving {} to {}", url, fullTmpDestPath);

            var u = new URL(url);
            var conn = u.openConnection();
            var is = new BufferedInputStream(conn.getInputStream());
            var file = new File(fullTmpDestPath);

            // this does all-inclusive copying of the input stream of file downloaded
            // to final destination file
            FileUtils.copyInputStreamToFile(is, file);
            return fullTmpDestPath;
        } catch (IOException e) {
            throw new ImageImportException(e.getMessage(), e);
        }
    }

    @Override
    public void cleanup() throws CleanupException {
        try {
            // delete the working temp directory of files that were used/created
            FileUtils.cleanDirectory(new File(workingDir));
        } catch (Exception e) {
            throw new CleanupException(e.getMessage(), e);
        }
    }
}
