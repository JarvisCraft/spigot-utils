package ru.progrm_jarvis.minecraft.commons.mojang;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.val;
import org.apache.http.client.methods.HttpGet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.progrm_jarvis.mcunit.io.http.HttpClientMocks;

import java.util.Base64;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static ru.progrm_jarvis.minecraft.commons.mojang.MojangApiManager.USERNAME_TO_UUID_AT_TIME_URI_PREFIX;
import static ru.progrm_jarvis.minecraft.commons.mojang.MojangApiManager.UUID_TO_PROFILE_URI_PREFIX;

class MojangApiManagerTest {

    private static final UUID
            UUID_1 = UUID.fromString("29be10b1-b2d1-4130-b8f2-4fd14d3ccb62");
    private static final String
            MOJANG_UUID_1 = "29be10b1b2d14130b8f24fd14d3ccb62",
            USERNAME_1 = "PROgrm_JARvis",
            TEXTURES_PROPERTY_NAME = "textures",
            PROFILE_PROPERTY_1_VALUE_1 = "eyJ0aW1lc3RhbXAiOjE1NDU0MDcyMDUwNjksInByb2ZpbGVJZCI6IjI5YmUxMGIxYjJkMTQxMzBiO"
                    + "GYyNGZkMTRkM2NjYjYyIiwicHJvZmlsZU5hbWUiOiJQUk9ncm1fSkFSdmlzIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsI"
                    + "nRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9lYTU0MTdhM"
                    + "zBlZDU3OTQ3YjFlZDliMmUzMWNkZWJlZDJhYzE5YzlhODkyZGU2NDY4NjNhMTA4YTNkOTE0MzE5In19fQ==",
            PROFILE_PROPERTY_1_SIGNATURE_1 = "P3QNNsvMIx9lNpz9Is1gc9eeUlz7zj0KWLBIgwhswschofE3X1NjQVoykKy1SAA8Ru6SmXmPs"
                    + "W1VtWdLzjCdtsCR0h/1mLTpruu5VFuBB6hwILHCeKabLIdSRzKKLXUHNNSd6kLe+Qis/QSOlGESQT8NYuxxSp2NlT0/YD6ot"
                    + "8pd/KxXyVRJrU/VPYfYJnxv9KeN4PFb67F82dZ33dlZsIvRN2LlFPluQeQ4xrdSojVyUVTefTQ5HGQBibIFv0dcbKScPHqYA"
                    + "4qKT61oMmsJEvzZ94HQKANTgEAO8D1KEwt+2Q3mzvr/lCqQq3MzmWQRympCbP3XVYe06hx2kk4awNkZ9OT+o9mOGOR7T7Rsz"
                    + "E4I0ghIg2wgo30cGK+YSjRGpvoX3BJafATzyuGssN22y1hmUMqTzK+0jkQpTYxK+9pxslQ/LIPp9zTKJyd5HwE25pyFFPOj+"
                    + "fzYWQmb0Vf92SVDWeE2c6oE0NFyDmyRLu40m1FRWn0AW+6v0G/38Zbc6QESgT6YOrUvUjTgqqbC95rSLjRCPMj6bE6QXPDqH"
                    + "yJQnGrDsws+QO9BafajpQatd8njHFASYIgs675NJy9dnEBjw1FLzpED5hIS0dLRUeEzAhWL9j+89tTPflysTrJxu5HR41Ydd"
                    + "dd7CQRmVHOFO59HjSpHXo/Xp47uJPKcIyA=";
    private static final GameProfile PROFILE_1 = new GameProfile(UUID_1, USERNAME_1);

    static {
        PROFILE_1.getProperties().put(
                TEXTURES_PROPERTY_NAME,
                new Property(TEXTURES_PROPERTY_NAME, PROFILE_PROPERTY_1_VALUE_1, PROFILE_PROPERTY_1_SIGNATURE_1)
        );
    }

    @BeforeAll
    static void validateBase64s() {
        val decoder = Base64.getDecoder();

        assertNotNull(decoder.decode(PROFILE_PROPERTY_1_VALUE_1));
        assertNotNull(decoder.decode(PROFILE_PROPERTY_1_SIGNATURE_1));
    }

    @Test
    void testGetUuid() {
        try (val mojangApiManager = new MojangApiManager(MojangApiManager.Configuration.builder()
                .httpClient(manager -> HttpClientMocks.mockHttpClient()
                        .responding(
                                new HttpGet(USERNAME_TO_UUID_AT_TIME_URI_PREFIX + USERNAME_1),
                                "{\"id\":\"" + MOJANG_UUID_1 +"\",\"name\":\"" + USERNAME_1 + "\"}"
                        ))
                .build())) {
            assertEquals(UUID_1, mojangApiManager.getUuid(USERNAME_1));
        }
    }

    @Test
    void testGetProfile() {
        try (val mojangApiManager = new MojangApiManager(MojangApiManager.Configuration.builder()
                .httpClient(manager -> HttpClientMocks.mockHttpClient()
                        .responding(
                                new HttpGet(UUID_TO_PROFILE_URI_PREFIX + MOJANG_UUID_1 + "?unsigned=false"),
                                "{\"id\":\"" + MOJANG_UUID_1 + "\",\"name\":\"" + USERNAME_1 + "\",\"properties\":"
                                        + "[{\"name\":\"textures\",\"value\":\"" + PROFILE_PROPERTY_1_VALUE_1 + "\","
                                        + "\"signature\":\"" + PROFILE_PROPERTY_1_VALUE_1 + "\"}]}"
                        ))
                .build())) {

            assertEquals(PROFILE_1, mojangApiManager.getProfile(UUID_1, true));
        }
    }
}