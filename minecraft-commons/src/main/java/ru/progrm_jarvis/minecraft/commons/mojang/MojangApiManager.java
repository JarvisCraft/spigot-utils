package ru.progrm_jarvis.minecraft.commons.mojang;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.*;
import lombok.Builder.Default;
import lombok.experimental.FieldDefaults;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.progrm_jarvis.javacommons.lazy.Lazy;
import ru.progrm_jarvis.minecraft.commons.annotation.AsyncExpected;
import ru.progrm_jarvis.minecraft.commons.async.AsyncRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@ToString
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class MojangApiManager implements AutoCloseable {

    ///////////////////////////////////////////////////////////////////////////
    // URIs
    ///////////////////////////////////////////////////////////////////////////

    public static final String USERNAME_TO_UUID_AT_TIME_URI_PREFIX
            = "https://api.mojang.com/users/profiles/minecraft/";

    public static final String UUID_TO_PROFILE_URI_PREFIX
            = "https://sessionserver.mojang.com/session/minecraft/profile/";

    protected static final JsonParser jsonParser = new JsonParser();

    Lazy<HttpClientConnectionManager> httpConnectionManager;
    Lazy<HttpClient> httpClient;

    Lazy<AsyncRunner> asyncRunner;

    Lazy<Cache<String, UUID>> uuidsCache;
    Lazy<Cache<UUID, GameProfile>> profilesCache;

    public MojangApiManager(final Configuration configuration) {
        httpConnectionManager = Lazy.createThreadSafe(configuration.httpConnectionManager);
        {
            val httpClientFunction = configuration.httpClient;
            httpClient = Lazy.createThreadSafe(() -> httpClientFunction.apply(httpConnectionManager.get()));
        }

        asyncRunner = Lazy.createThreadSafe(configuration.asyncRunner);

        uuidsCache = Lazy.createThreadSafe(configuration.uuidsCache);
        profilesCache = Lazy.createThreadSafe(configuration.profilesCache);
    }

    @Override
    public void close() {
        if (httpConnectionManager.isInitialized()) httpConnectionManager.get().shutdown();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Inner utilities
    ///////////////////////////////////////////////////////////////////////////

    @AsyncExpected
    protected static JsonElement readJson(final @NonNull InputStream inputStream) {
        return jsonParser.parse(new BufferedReader(new InputStreamReader(inputStream)));
    }

    @AsyncExpected
    protected JsonElement httpGetJson(final @NonNull String uri) throws IOException {
        return readJson(httpClient.get().execute(new HttpGet(uri)).getEntity().getContent());
    }

    @AsyncExpected
    protected JsonElement httpGetJson(final @NonNull String uri, final @Nullable Map<String, String> parameters)
            throws IOException {
        if (parameters == null || parameters.isEmpty()) return httpGetJson(uri);

        val uriBuilder = new StringBuilder(uri)
                .append('?');

        val params = parameters.entrySet();
        val size = parameters.size();
        var i = 1;
        for (val param : params) {
            // append parameter
            uriBuilder
                    .append(param.getKey())
                    .append('=')
                    .append(param.getValue());
            // if not last param then append '&'
            if (i != size) {
                uriBuilder.append('&');
                i++; // micro-optimization: increment only if needed
            }
        }

        return httpGetJson(uriBuilder.toString());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Username --> UUID
    ///////////////////////////////////////////////////////////////////////////

    @AsyncExpected
    public @NotNull UUID readUuid(final @NonNull String userName) throws IOException {
        return MojangUtil.fromMojangUuid(httpGetJson(USERNAME_TO_UUID_AT_TIME_URI_PREFIX + userName).getAsJsonObject()
                .get("id").getAsString()
        );
    }

    public void readUuid(final @NonNull String userName,
                         final @NonNull Consumer<UUID> callback) {
        asyncRunner.get().runAsynchronously(() -> readUuidUnchecked(userName), callback);
    }

    @AsyncExpected
    @SneakyThrows(IOException.class)
    public @NotNull UUID readUuidUnchecked(final @NonNull String userName) {
        return readUuid(userName);
    }

    @SneakyThrows
    @AsyncExpected
    public @NotNull UUID getUuid(final @NonNull String userName) {
        return uuidsCache.get()
                .get(userName.toLowerCase(), () -> readUuidUnchecked(userName));
    }

    public void getUuid(final @NonNull String userName, final @NonNull Consumer<UUID> callback) {
        asyncRunner.get().runAsynchronously(() -> getUuid(userName), callback);
    }

    ///////////////////////////////////////////////////////////////////////////
    // UUID --> GameProfile
    ///////////////////////////////////////////////////////////////////////////

    @AsyncExpected
    public @NotNull GameProfile readProfile(final @NonNull UUID uuid, final boolean signed) throws IOException {
        val data = httpGetJson(UUID_TO_PROFILE_URI_PREFIX + MojangUtil.toMojangUuid(uuid) + "?unsigned=" + !signed)
                .getAsJsonObject();

        val profile = new GameProfile(uuid, data.get("name").getAsString());

        val profileProperties = profile.getProperties();

        val properties = data.getAsJsonArray("properties");
        for (val property : properties) {
            val jsonProperty = property.getAsJsonObject();
            val propertyName = jsonProperty.get("name").getAsString();

            profileProperties.put(propertyName, new Property(
                    propertyName,
                    jsonProperty.get("value").getAsString(),
                    signed ? jsonProperty.get("signature").getAsString() : null
            ));
        }

        return profile;
    }

    public void readProfile(final @NonNull UUID uuid, final boolean signed,
                            final @NonNull Consumer<GameProfile> callback) {
        asyncRunner.get().runAsynchronously(() -> readProfileUnchecked(uuid, signed), callback);
    }

    @AsyncExpected
    @SneakyThrows(IOException.class)
    public @NotNull GameProfile readProfileUnchecked(final @NonNull UUID uuid, final boolean signed) {
        return readProfile(uuid, signed);
    }

    @SneakyThrows
    @AsyncExpected
    public @NotNull GameProfile getProfile(final @NonNull UUID uuid, final boolean signed) {
        val cache = profilesCache.get();
        var cachedProfile = cache.getIfPresent(uuid);
        // get profile using Mojang API if there is no cached one or it is unsigned but has to be
        if (cachedProfile == null || (signed && !MojangUtil.isSigned(cachedProfile))) cache
                .put(uuid, cachedProfile = readProfile(uuid, signed));

        return cachedProfile;
    }

    public void getProfile(final @NonNull UUID uuid, final boolean signed,
                           final @NonNull Consumer<GameProfile> callback) {
        asyncRunner.get().runAsynchronously(() -> getProfile(uuid, signed), callback);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Configuration class
    ///////////////////////////////////////////////////////////////////////////

    @Builder
    @FieldDefaults(level = AccessLevel.PROTECTED)
    public static class Configuration {

        @Default Supplier<HttpClientConnectionManager> httpConnectionManager
                = PoolingHttpClientConnectionManager::new;

        @Default Function<HttpClientConnectionManager, HttpClient> httpClient = manager -> HttpClients
                .custom()
                .setConnectionManager(manager)
                .build();

        @Default Supplier<Cache<String, UUID>> uuidsCache
                = () -> CacheBuilder.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build();

        @Default Supplier<Cache<UUID, GameProfile>> profilesCache
                = () -> CacheBuilder.newBuilder()
                .expireAfterWrite(5, TimeUnit.SECONDS)
                .build();

        @Default Supplier<AsyncRunner> asyncRunner = () -> {
            throw new IllegalStateException("Async tasks are not available in this MojangApiManager");
        };

        public static Configuration getDefault() {
            return builder().build();
        }
    }
}
