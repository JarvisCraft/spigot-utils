package ru.progrm_jarvis.minecraft.commons.util.chat;

import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.gson.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Type;

/**
 * Utility for functions related to chat-components.
 */
@UtilityClass
public class ChatComponentUtil {

    /**
     * Gets an instance of {@link WrappedChatComponentGsonSerializer}
     * capable of serializing and deserializing {@link WrappedChatComponent}
     * which may be used in {@link GsonBuilder#registerTypeAdapter(Type, Object)}.
     *
     * @return instance of {@link WrappedChatComponentGsonSerializer} for {@link WrappedChatComponent} (de)serialization
     */
    public static WrappedChatComponentGsonSerializer wrappedChatComponentGsonSerializer() {
        return WrappedChatComponentGsonSerializer.INSTANCE;
    }

    /**
     * {@link JsonSerializer} and {@link JsonDeserializer}
     * for use with {@link GsonBuilder#registerTypeAdapter(Type, Object)}.
     */
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class WrappedChatComponentGsonSerializer
            implements JsonSerializer<WrappedChatComponent>, JsonDeserializer<WrappedChatComponent> {

        /**
         * Singleton instance of {@link WrappedChatComponentGsonSerializer}.
         */
        private static final WrappedChatComponentGsonSerializer INSTANCE = new WrappedChatComponentGsonSerializer();

        @Override
        public WrappedChatComponent deserialize(final JsonElement json, final Type typeOfT,
                                                final JsonDeserializationContext context)
                throws JsonParseException {
            if (json.isJsonObject()) return WrappedChatComponent.fromJson(json.toString());
            return WrappedChatComponent.fromText(json.getAsString());
        }

        @Override
        public JsonElement serialize(final WrappedChatComponent src, final Type typeOfSrc,
                                     final JsonSerializationContext context) {
            return context.serialize(context.serialize(src.getJson()));
        }
    }
}
