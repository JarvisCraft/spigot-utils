package ru.progrm_jarvis.minecraft.commons.util.io;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;

import java.io.File;
import java.nio.file.Files;

/**
 * Utilities for common
 */
@UtilityClass
public class FileUtil {

    /**
     * Creates the file if it doesn't exists.
     *
     * @param file file to make exist if possible
     * @return the file specified
     *
     * @apiNote this attempts to create a file and its parent directory only when needed
     */
    @SneakyThrows
    public File makeSureExists(final @NonNull File file) {
        if (!file.isFile()) {
            val parent = file.getParentFile();
            if (parent != null && !parent.isDirectory()) Files.createDirectories(parent.toPath());

            Files.createFile(file.toPath());
        }

        return file;
    }
}
