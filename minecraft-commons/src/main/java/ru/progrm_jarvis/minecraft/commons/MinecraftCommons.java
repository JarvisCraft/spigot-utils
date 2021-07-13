package ru.progrm_jarvis.minecraft.commons;

import lombok.experimental.UtilityClass;
import ru.progrm_jarvis.minecraft.commons.util.SystemPropertyUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * Utility for accessing general configurations of Minecraft Commons.
 */
@UtilityClass
public class MinecraftCommons {

    /**
     * Root folder of minecraft-commons shared files
     */
    public final File ROOT_DIRECTORY = new File(SystemPropertyUtil.getSystemProperty(
            MinecraftCommons.class.getCanonicalName() + ".root-directory", Function.identity(),
            "plugins/minecraft_commons/"
    ));

    private final boolean CREATE_README_FILE = SystemPropertyUtil.getSystemPropertyBoolean(
            MinecraftCommons.class.getCanonicalName() + ".create-readme-file", true
    );

    /**
     * Content of {@code README.txt} file created in {@link #ROOT_DIRECTORY} if this option is not disabled.
     */
    public final List<String> README_CONTENT = Arrays.asList(
            "This is an internal folder of minecraft-commons library.",
            "It is most likely used by one or more of your plugins and should not be removed "
                    + "as it may store some sensitive data",
            "",
            "minecraft-commons is part of minecraft-utils open-source project "
                    + "and is distributed under Apache 2.0 license",
            "Source code is available at: https://github.com/JarvisCraft/minecraft-utils",
            "",
            "minecraft-commons and minecraft-utils development is not related to Mojang AB, Microsoft or Minecraft",
            "",
            "                   ~ PROgrm_JARvis#");

    static {
        if (!ROOT_DIRECTORY.isFile()) try {
            Files.createDirectories(ROOT_DIRECTORY.toPath());
        } catch (final IOException e) {
            throw new RuntimeException("Unable to create minecraft-commons root directory", e);
        }
        // create readme file if its generation is not disabled
        if (CREATE_README_FILE) try {
            Files.write(new File(ROOT_DIRECTORY, "README.txt").toPath(),  README_CONTENT);
        } catch (final IOException e) {
            throw new RuntimeException("Unable to create README.txt for minecraft-commons", e);
        }
    }
}
