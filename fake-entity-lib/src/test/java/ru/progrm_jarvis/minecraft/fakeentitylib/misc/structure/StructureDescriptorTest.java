package ru.progrm_jarvis.minecraft.fakeentitylib.misc.structure;

import com.google.gson.Gson;
import lombok.NonNull;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StructureDescriptorTest {

    private static Gson gson;

    @BeforeAll
    static void setUp() {
        gson = new Gson();

        val server = mock(Server.class);
        val logger = mock(Logger.class);
        val itemFactory = mock(ItemFactory.class);

        when(itemFactory.getItemMeta(notNull()))
                .thenReturn(mock(ItemMeta.class, Mockito.withSettings().extraInterfaces(Damageable.class)));
        when(server.getItemFactory()).thenReturn(itemFactory);
        when(server.getLogger()).thenReturn(logger);

        Bukkit.setServer(server);
    }

    @Test
    void testGson() throws IOException {
        final StructureDescriptor.JsonRepresentation jsonStructureDescriptor;
        try (val reader = new BufferedReader(new FileReader(getFile("/entity_descriptor_1.json")))) {
            jsonStructureDescriptor = gson.fromJson(reader, StructureDescriptor.JsonRepresentation.class);
        }

        assertEquals(7, jsonStructureDescriptor.getObjects().length);
        assertEquals(1, jsonStructureDescriptor.getKeyframes().length);
        assertEquals(7, jsonStructureDescriptor.getKeyframes()[0].getObjects().length);
    }

    @Test
    void testFromJson() {
        val structureDescriptor = StructureDescriptor.fromJson(getFile("/entity_descriptor_1.json"));

        assertEquals(7, structureDescriptor.getElements().size());
        assertEquals(1, structureDescriptor.getKeyframes().size());
    }

    private File getFile(final @NonNull String fileName) {
        return new File(getClass().getResource(fileName).getFile());
    }
}