package ru.progrm_jarvis.minecraft.libloader;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.extern.java.Log;

import java.io.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A tiny loader of JAR dependencies to runtime.
 */
@Log
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LibLoader {

    /**
     * Method handle of {@code URLClassLoader.addURL(URL)}
     */
    @NonNull private static final MethodHandle URL_CLASS_LOADER__ADD_URL_METHOD;

    /**
     * Current class loader used by this lib loader
     */
    @Getter @NonNull URLClassLoader classLoader;

    /**
     * Directory to store library artifacts and hashes in
     */
    @NonNull private final File rootDirectory;

    final @NonNull Map<String, File> loadedLibs;

    // creates a MethodHandle object for URLClassLoader#addUrl(URL) method
    static {
        final Method addUrlMethod;
        try {
            addUrlMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        } catch (final NoSuchMethodException e) {
            throw new ExceptionInInitializerError(
                    "Unable to initialize LibLoader as " + URLClassLoader.class.getCanonicalName()
                            + " is missing addURL(URL) method"
            );
        }

        val accessible = addUrlMethod.isAccessible();
        addUrlMethod.setAccessible(true);
        try {
            URL_CLASS_LOADER__ADD_URL_METHOD = MethodHandles.lookup().unreflect(addUrlMethod);
        } catch (final IllegalAccessException e) {
            throw new ExceptionInInitializerError(
                    "Unable to initialize LibLoader as " + URLClassLoader.class.getCanonicalName()
                            + " cannot be unreflected to MethodHandle"
            );
        } finally {
            addUrlMethod.setAccessible(accessible);
        }
    }

    /**
     * Tries to find an available {@link URLClassLoader} un current context.
     *
     * @return optional containing found {@link URLClassLoader} or empty if none was found in current context
     *
     * @implNote this checks current thread's class loader and system class loader
     */
    public static Optional<URLClassLoader> getAvailableUrlClassLoader() {
        var classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader instanceof URLClassLoader) return Optional.of((URLClassLoader) classLoader);

        classLoader = ClassLoader.getSystemClassLoader();
        if (classLoader instanceof URLClassLoader) return Optional.of((URLClassLoader) classLoader);

        return Optional.empty();
    }

    /**
     * Constructs new lib loader. This performs type-check of {@code classLoader}
     * in order to guarantee that it is instance of {@link URLClassLoader}.
     *
     * @param classLoader class loader to use for loading of libraries,
     * should normally be {@link URLClassLoader}
     * @param rootDirectory directory to store library artifacts and hashes in
     *
     * @throws IllegalArgumentException if the {@code classLoader} is not {@link URLClassLoader}
     */
    public LibLoader(final @NonNull ClassLoader classLoader, final @NonNull File rootDirectory) {
        if (!(classLoader instanceof URLClassLoader)) throw new IllegalArgumentException(
                classLoader + " is not instance of URLClassLoader"
        );

        loadedLibs = new ConcurrentHashMap<>();
        this.classLoader = (URLClassLoader) classLoader;
        this.rootDirectory = rootDirectory;
    }

    /**
     * Constructs new lib loader using default root directory.
     *
     * @param urlClassLoader class loader to use for loading of libraries
     */
    public LibLoader(final @NonNull URLClassLoader urlClassLoader) {
        this(urlClassLoader, new File("libs/artifacts/"));
    }

    /**
     * Constructs new lib loader using default root directory. This performs type-check of {@code classLoader}
     *      * in order to guarantee that it is instance of {@link URLClassLoader}.
     *
     * @param classLoader class loader to use for loading of libraries,
     * should normally be {@link URLClassLoader}
     *
     * @throws IllegalArgumentException if the {@code classLoader} is not {@link URLClassLoader}
     */
    public LibLoader(final @NonNull ClassLoader classLoader) {
        this(classLoader, new File("libs/artifacts/"));
    }

    /**
     * Constructs new lib loader. This uses class loader got from {@link #getAvailableUrlClassLoader()}.
     *
     * should normally be {@link URLClassLoader}
     * @param rootDirectory directory to store library artifacts and hashes in
     */
    public LibLoader(final @NonNull File rootDirectory) {
        this(getAvailableUrlClassLoader().orElseThrow(
                () -> new IllegalStateException("Cannot find any available URLClassLoader in current context")
                ), rootDirectory
        );
    }

    /**
     * Sets this lib loader's class loader.
     *
     * @param classLoader class loader to be used by this lib loader
     * @return self for chaining
     *
     * @see #setClassLoader(ClassLoader) variant performing checks of any classloader
     */
    public LibLoader setClassLoader(final @NonNull URLClassLoader classLoader) {
        this.classLoader = classLoader;

        return this;
    }

    /**
     * Sets this lib loader's class loader throwing an exception if it is not {@link URLClassLoader}.
     *
     * @param classLoader class loader to be used by this lib loader
     * @throws IllegalArgumentException if the {@code classLoader} is not {@link URLClassLoader}
     * @return self for chaining
     *
     * @see #setClassLoader(URLClassLoader) compile-time type-safe variant
     */
    public LibLoader setClassLoader(final ClassLoader classLoader) {
        if (!(classLoader instanceof URLClassLoader)) throw new IllegalArgumentException(
                classLoader + " is not instance of URLClassLoader"
        );

        setClassLoader((URLClassLoader) classLoader);

        return this;
    }

    /**
     * Checks whether the lib by specified name was loaded or not.
     *
     * @param name name of the lib
     * @return {@code true} if the lib was loaded by the specified name anf {@code false} otherwise
     */
    public boolean isLibLoaded(final @NonNull String name) {
        return loadedLibs.containsKey(name);
    }

    /**
     * Gets the loaded lib's artifact file if it was loaded.
     *
     * @param name name of the lib
     * @return optional containing file og lib's artifact
     * if it was loaded by the specified name or empty optional otherwise
     */
    public Optional<File> getLoadedLibArtifact(final @NonNull String name) {
        return Optional.ofNullable(loadedLibs.get(name));
    }

    /**
     * Call to this method guarantees that after it {@link #rootDirectory} will be a valid directory.
     */
    @SneakyThrows
    protected void assureRootDirectoryExists() {
        if (!rootDirectory.isFile()) Files.createDirectories(rootDirectory.toPath());
    }

    /**
     * Loads a library by its coords.
     * Loading should happen if:
     * <ul>
     *     <li>The library is missing</li>
     *     <li>The hash is not the same</li>
     *     <li>Hash is empty anywhere</li>
     * </ul>
     *
     * @param name name to store this library by
     * @param libCoords coords of a library artifact
     * @param addToClasspath whether or not the library loaded should be added to classpath
     *
     * @return file of created artifact
     */
    @SneakyThrows
    public File loadLib(final @NonNull String name, final @NonNull LibCoords libCoords, final boolean addToClasspath) {
        if (isLibLoaded(name)) throw new IllegalStateException("Library " + name + " is a;ready loaded by LibLoader");
        assureRootDirectoryExists();

        log.info("Loading library " + name);

        val artifactFile = new File(rootDirectory, name + ".jar");
        val hashFile = new File(rootDirectory, name + ".hash");

        libCoords.refresh();

        val hash = libCoords.computeHash();

        if (!artifactFile.isFile() || !hashFile.isFile() || !Files.lines(hashFile.toPath()).findFirst().equals(hash)) {
            {
                log.info("Downloading library " + name);
                try (val stream = libCoords.openArtifactStream()) {
                    loadFromInputStream(stream, artifactFile);
                }
            }
            Files.write(hashFile.toPath(), hash.orElse("").getBytes(StandardCharsets.UTF_8));
        }

        if (addToClasspath) addUrlToClasspath(classLoader, artifactFile.toURI().toURL());

        return artifactFile;
    }

    /**
     * Loads a library by its coords adding it to classpath.
     * Loading should happen if:
     * <ul>
     *     <li>The library is missing</li>
     *     <li>The hash is not the same</li>
     *     <li>Hash is empty anywhere</li>
     * </ul>
     *
     * @param name name to store this library by
     * @param libCoords coords of a library artifact
     *
     * @return file of created artifact
     *
     * @see #loadLib(String, LibCoords, boolean) called with {@code addToClasspath} set to {@code true}
     */
    @SneakyThrows
    public File loadLib(final @NonNull String name, final @NonNull LibCoords libCoords) {
        return loadLib(name, libCoords, true);
    }

    /**
     * Adds the specified URL to classpath of class loader.
     *
     * @param classLoader class loader to add the url to
     * @param url url to add to classpath of class loader
     */
    @SneakyThrows
    public static void addUrlToClasspath(final @NonNull URLClassLoader classLoader, final @NonNull URL url) {
        URL_CLASS_LOADER__ADD_URL_METHOD.invokeExact(classLoader, url);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Network utility methods
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Reads content of a URL as a list of lines.
     *
     * @param url url to read data from
     * @return lines read from URL
     * @throws IOException if an exception occurs while reading
     */
    public static List<String> readLinesFromUrl(final URL url) throws IOException {
        try (val reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            val lines = new ArrayList<String>();

            String line;
            while ((line = reader.readLine()) != null) lines.add(line);

            return lines;
        }
    }

    /**
     * Reads content of a URL as a single line.
     *
     * @param url url to read data from
     * @return first line read from URL
     * @throws IOException if an exception occurs while reading
     */
    public static String readLineFromUrl(final URL url) throws IOException {
        try (val reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            return reader.readLine();
        }
    }

    /**
     * Loads a file from input stream to file specified.
     *
     * @param inputStream input stream from which to get the file
     * @param file file to which to read read data
     * @throws IOException if an exception occurs in an I/O operation
     */
    public static void loadFromInputStream(final InputStream inputStream, final File file) throws IOException {
        Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
}
