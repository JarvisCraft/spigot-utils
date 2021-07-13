package ru.progrm_jarvis.minecraft.commons.util.chat;

import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.gson.GsonBuilder;
import lombok.val;
import org.junit.jupiter.api.Test;
import ru.progrm_jarvis.mcunit.annotation.EnabledIfNms;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChatComponentUtilTest {

    @Test
    @EnabledIfNms
    void testWrappedChatComponentGsonSerializer() {
        val gson = new GsonBuilder()
                .registerTypeAdapter(WrappedChatComponent.class, ChatComponentUtil.wrappedChatComponentGsonSerializer())
                .create();

        assertEquals(
                WrappedChatComponent.fromJson("{\"text\": \"Hello world!\"}"),
                gson.fromJson("{\"text\": \"Hello world!\"}", WrappedChatComponent.class)
        );

        assertEquals(
                WrappedChatComponent.fromText("Hello world"),
                gson.fromJson("Hello world", WrappedChatComponent.class)
        );
    }
}