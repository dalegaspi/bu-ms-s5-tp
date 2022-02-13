package edu.bu.cs622.jlitebox.utils;

import org.apache.commons.io.FilenameUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

/**
 * Helper functions
 *
 * @author dlegaspi@bu.edu
 */
public final class AppUtils {
    /**
     * get the base file name form path
     *
     * @param path the path
     * @return base file name
     */
    public static String getBaseFilename(@NonNull String path) {
        return FilenameUtils.getBaseName(path);
    }

    /**
     * get the file ext from path
     *
     * @param path
     * @return the extension
     */
    public static String getFilenameExt(@NonNull String path) {
        return FilenameUtils.getExtension(path);
    }

    /**
     * get filename with ext from path
     *
     * @param path path
     * @return the filename
     */
    public static String getFilename(@NonNull String path) {
        return FilenameUtils.getName(path);
    }

    /**
     * Create a normalized path
     *
     * @param first    the root
     * @param subPaths sub paths
     * @return the generated path
     */
    public static String createPath(String first, String... subPaths) {
        return Paths.get(first, subPaths).toString();
    }

    /**
     * Convenience function for getting value from Map string as optional
     *
     * @param map the map
     * @param key the key
     * @return optional
     */
    public static Optional<String> getValue(Map<String, String> map, String key) {
        if (map.containsKey(key)) {
            var val = map.get(key);
            return val.isBlank() ? Optional.empty() : Optional.of(val);
        } else {
            return Optional.empty();
        }
    }
}
