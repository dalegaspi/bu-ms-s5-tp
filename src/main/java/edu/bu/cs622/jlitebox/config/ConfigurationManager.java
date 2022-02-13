package edu.bu.cs622.jlitebox.config;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

/**
 * Configuration manager
 *
 * @see ImageCatalogConfiguration
 * @author dlegaspi@bu.edu
 */
public class ConfigurationManager {

    /**
     * get version info from git.properties
     * 
     * @return version Properties
     */
    public static Optional<Properties> getVersionProperties() {
        Properties properties = new Properties();
        try {
            properties.load(ConfigurationManager.class.getClassLoader().getResourceAsStream("git.properties"));
            return Optional.of(properties);
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    /**
     * version info
     *
     * @return version info
     */
    public static Optional<String> getVersionInfo() {
        return getVersionProperties().map(p -> String.format("%s.%s", p.getProperty("git.build.version"),
                        p.getProperty("git.commit.id.abbrev")));
    }
}
