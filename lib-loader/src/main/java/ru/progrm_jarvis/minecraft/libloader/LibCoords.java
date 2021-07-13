package ru.progrm_jarvis.minecraft.libloader;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

/**
 * Coords of a library with dependencies.
 */
@FunctionalInterface
public interface LibCoords {

    String MAVEN_CENTRAL_REPO_URL = "https://repo1.maven.org/maven2/";

    String SONATYPE_OSS_SNAPSHOTS_REPO_URL = "https://oss.sonatype.org/content/repositories/snapshots/";

    /**
     * Opens stream for accessing library artifact.
     *
     * @return URL of the artifact containing dependency classes
     * @throws IOException if an IO-error occurs while opening stream
     *
     * @apiNote returned stream <b>must</b> be manually closed
     * @apiNote should call{@link #assureIsRefreshed()} before all logic
     */
    InputStream openArtifactStream() throws IOException;

    /**
     * Computes the hashcode of the library not to repeat loading of the same library (if possible).
     *
     * @return optional containing hash of the artifact or empty optional if not available
     *
     * @apiNote this may be a time-consuming operation
     * @apiNote should call{@link #assureIsRefreshed()} before all logic
     */
    default Optional<String> computeHash() {
        return Optional.empty();
    }

    /**
     * Refreshes the coordinate so that it is actualized.
     */
    default void refresh() {}

    /**
     * Checks whether this lib coords have been refreshed at least once.
     *
     * @return {@code true} if {@link #refresh()} have been called at least once or doesn't requre refreshing
     * and {@code false} otherwise.
     */
    default boolean isRefreshed() {
        return true;
    }

    /**
     * Refreshes the lib coords if they haven't been yet.
     */
    default void assureIsRefreshed() {
        if (!isRefreshed()) refresh();
    }

    /**
     * Gets the root URL for the repository of specified parameters.
     *
     * @param repositoryUrl URL of the repository of the artifact
     * @param groupId artifact's {@code groupId}
     * @param artifactId artifact's {@code artifactId}
     * @param version artifact's version
     * @return URl to the artifacts root ended with '/'
     */
    static String getMavenArtifactsRootUrl(final @NonNull String repositoryUrl,
                                           final @NonNull String groupId, final @NonNull String artifactId,
                                           final @NonNull String version) {
        return (repositoryUrl.lastIndexOf('/') == repositoryUrl.length() - 1 ? repositoryUrl : repositoryUrl + '/')
                + groupId.replace('.', '/') + '/' + artifactId + '/' + version + '/';
    }

    @SneakyThrows(MalformedURLException.class)
    static LibCoords fromMavenRepo(final @NonNull String repositoryUrl,
                                   final @NonNull String groupId, final @NonNull String artifactId,
                                   final @NonNull String version) {
        val jarUrl = getMavenArtifactsRootUrl(repositoryUrl, groupId, artifactId, version)
                + artifactId + '-' + version + ".jar";

        return new MavenRepoLibCoords(new URL(jarUrl), new URL(jarUrl + ".sha1"), new URL(jarUrl + ".md5"));
    }

    static LibCoords fromMavenCentralRepo(final @NonNull String groupId, final @NonNull String artifactId,
                                          final @NonNull String version) {
        return fromMavenRepo(MAVEN_CENTRAL_REPO_URL, groupId, artifactId, version);
    }

    @SneakyThrows(MalformedURLException.class)
    static LibCoords fromSonatypeNexusRepo(final @NonNull String repositoryUrl,
                                           final @NonNull String groupId, final @NonNull String artifactId,
                                           final @NonNull String version, final @NonNull String metadataFileName) {
        val rootUrl = getMavenArtifactsRootUrl(repositoryUrl, groupId, artifactId, version);

        return new SonatypeNexusRepoLibCoords(new URL(rootUrl + metadataFileName), rootUrl);
    }

    static LibCoords fromSonatypeNexusRepo(final @NonNull String repositoryUrl,
                                           final @NonNull String groupId, final @NonNull String artifactId,
                                           final @NonNull String version) {
        return fromSonatypeNexusRepo(
                repositoryUrl, groupId, artifactId, version, SonatypeNexusRepoLibCoords.METADATA_FILE_NAME
        );
    }

    static LibCoords fromSonatypeOssSnapshotsRepo(final @NonNull String groupId, final @NonNull String artifactId,
                                                  final @NonNull String version) {
        return fromSonatypeNexusRepo(
                SONATYPE_OSS_SNAPSHOTS_REPO_URL, groupId, artifactId,
                version, SonatypeNexusRepoLibCoords.METADATA_FILE_NAME
        );
    }

    /**
     * Gets the name of the artifact described in the specified {@code maven-metadata.xml} of Sonatype Nexus.
     *
     * @param documentElement {@code maven-metadata.xml} standard for Sonatype Nexus snapshots repository.
     * @return name of the artifact specified in given {@code maven-metadata.xml} document element
     *
     * @see Document#getDocumentElement() to create {@link Element} from {@link Document}
     */
    static String getLatestNexusArtifactName(final @NonNull Element documentElement) {
        val version = documentElement.getElementsByTagName("version").item(0).getFirstChild().getTextContent();
        val snapshot = (Element) ((Element) documentElement.getElementsByTagName("versioning").item(0))
                .getElementsByTagName("snapshot").item(0);

        return documentElement.getElementsByTagName("artifactId").item(0).getFirstChild().getTextContent()
                + '-' + (version.endsWith("-SNAPSHOT") ? version.substring(0, version.length() - 9) : version)
                + '-' + snapshot.getElementsByTagName("timestamp").item(0).getFirstChild().getTextContent()
                + '-' + snapshot.getElementsByTagName("buildNumber").item(0).getFirstChild().getTextContent();
    }

    /**
     * Lib coords of an artifact normally contained in non-SNAPSHOT maven repository.
     * Its artifact is accessed by direct URL.
     * The hash is computed from their URL requests' contents concatenated using {@code '_'}.
     */
    @Value
    @FieldDefaults(level = AccessLevel.PRIVATE)
    final class MavenRepoLibCoords implements LibCoords {

        /**
         * URL of artifact
         */
        @NonNull URL artifactUrl;
        /**
         * URL to be used (when self is non-null) to get the first hash of an artifact.
         */
        @Nullable URL hash1Url,
        /**
         * URL to be used (when self and {@link #hash1Url} are non-null) to get the second hash of an artifact.
         */
        hash2Url;

        public MavenRepoLibCoords(final @NonNull URL artifactUrl,
                                  final @Nullable URL hash1Url, final @Nullable URL hash2Url) {
            this.artifactUrl = artifactUrl;
            this.hash1Url = hash1Url;
            this.hash2Url = hash2Url;
        }

        public MavenRepoLibCoords(final @NonNull URL jarUrl, final URL hash1Url) {
            this(jarUrl, hash1Url, null);
        }

        public MavenRepoLibCoords(final @NonNull URL jarUrl) {
            this(jarUrl, null, null);
        }

        @Override
        public InputStream openArtifactStream() throws IOException {
            assureIsRefreshed();

            return artifactUrl.openStream();
        }

        @Override
        public Optional<String> computeHash() {
            assureIsRefreshed();

            if (hash1Url == null) return Optional.empty();
            try {
                if (hash2Url == null) return Optional.ofNullable(LibLoader.readLineFromUrl(hash1Url));
                else {
                    val hash1 = LibLoader.readLineFromUrl(hash1Url);
                    if (hash1 == null) return Optional.empty();

                    val hash2 = LibLoader.readLineFromUrl(hash2Url);
                    if (hash2 == null) return Optional.empty();

                    return Optional.of(hash1 + '_' + hash2);
                }
            } catch (final IOException e) {
                return Optional.empty();
            }
        }
    }

    @ToString
    @EqualsAndHashCode
    @RequiredArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    final class SonatypeNexusRepoLibCoords implements LibCoords {

        public static final String METADATA_FILE_NAME = "maven-metadata.xml";

        /**
         * Document builder factory singleton for internal usage.
         */
        private static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();

        /**
         * URL of a metadata file to get the latest version of artifact.
         */
        final @NonNull URL mavenMetadataUrl;
        final @NonNull String artifactsRootUrl;

        /**
         * Whether or not this lib coords were refreshed.
         */
        @Getter boolean refreshed;
        String latestVersion;
        MavenRepoLibCoords mavenRepoLibCoords;

        @Override
        @SneakyThrows({ParserConfigurationException.class, MalformedURLException.class})
        public void refresh() {
            refreshed = true;

            final Document metadataDocument;
            try {
                val mavenMetadata = String.join(System.lineSeparator(), LibLoader.readLinesFromUrl(mavenMetadataUrl));
                if (mavenMetadata.isEmpty()) throw new RuntimeException(
                        "Could not read Maven metadata from " + mavenMetadataUrl + " as it's content is empty"
                );
                metadataDocument = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder()
                        .parse(new InputSource(new StringReader(mavenMetadata)));
            } catch (final IOException | SAXException e) {
                throw new RuntimeException(
                        "Could not read Maven metadata from " + mavenMetadataUrl, e
                );
            }

            val latestVersion = getLatestNexusArtifactName(metadataDocument.getDocumentElement());
            val jarUrl = artifactsRootUrl + latestVersion + ".jar";

            if (!latestVersion.equals(this.latestVersion)) {
                mavenRepoLibCoords = new MavenRepoLibCoords(
                        new URL(jarUrl), new URL(jarUrl + ".sha1"), new URL(jarUrl + ".md5")
                );
                this.latestVersion = latestVersion;
            }
        }

        @Override
        public InputStream openArtifactStream() throws IOException {
            assureIsRefreshed();

            return mavenRepoLibCoords.openArtifactStream();
        }

        @Override
        public Optional<String> computeHash() {
            assureIsRefreshed();

            return mavenRepoLibCoords.computeHash();
        }
    }

}
