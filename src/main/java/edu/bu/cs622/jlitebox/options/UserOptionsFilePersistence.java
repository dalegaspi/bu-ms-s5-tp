package edu.bu.cs622.jlitebox.options;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.bu.cs622.jlitebox.config.ImageCatalogConfiguration;
import io.vavr.control.Try;
import org.apache.commons.io.FileUtils;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

public class UserOptionsFilePersistence implements ObjectPersistence<UserOptions> {
    private final ObjectMapper objectMapper;

    private final String filePath;

    public static final String OPTIONS_FILE = "options.json";

    private Optional<UserOptions> currentOptions = Optional.empty();

    @Inject
    public UserOptionsFilePersistence(ImageCatalogConfiguration configuration) throws IOException {
        objectMapper = new ObjectMapper();

        FileUtils.createParentDirectories(new File(configuration.getFileOptionsBaseDir()));
        filePath = Paths.get(configuration.getFileOptionsBaseDir(), OPTIONS_FILE).toString();
    }

    @Override
    public Optional<UserOptions> load() {
        currentOptions = Try.of(() -> objectMapper.readValue(new File(filePath), UserOptions.class)).toJavaOptional();
        return currentOptions;
    }

    @Override
    public synchronized boolean save(UserOptions newOptions) {
        if (currentOptions.map(userOptions -> !userOptions.equals(newOptions)).orElse(true)) {
            return Try.run(() -> objectMapper.writeValue(new File(filePath), newOptions)).isSuccess();
        } else {
            return true;
        }
    }
}
