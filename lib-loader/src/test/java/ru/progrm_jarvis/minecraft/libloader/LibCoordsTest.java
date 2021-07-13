package ru.progrm_jarvis.minecraft.libloader;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class LibCoordsTest {

    @Test
    void testGetLatestNexusArtifactName()
            throws ParserConfigurationException, IOException, SAXException {
        assertEquals(
                "minecraft-commons-0.1.0-20190109.213531-6",
                LibCoords.getLatestNexusArtifactName(DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder().parse(new File(getClass().getResource("/maven-metadata.xml").getFile()))
                        .getDocumentElement()
                )
        );
    }

    @Test
    void testGetMavenArtifactsRootUrl() {
        assertEquals(
                "https://oss.sonatype.org/content/repositories/snapshots/"
                        + "ru/progrm-jarvis/minecraft/minecraft-commons/0.1.0-SNAPSHOT/",
                LibCoords.getMavenArtifactsRootUrl(
                        "https://oss.sonatype.org/content/repositories/snapshots/",
                        "ru.progrm-jarvis.minecraft", "minecraft-commons", "0.1.0-SNAPSHOT")
        );

        assertEquals(
                "https://oss.sonatype.org/content/repositories/snapshots/"
                        + "ru/progrm-jarvis/minecraft/minecraft-commons/0.1.0-SNAPSHOT/",
                LibCoords.getMavenArtifactsRootUrl(
                        "https://oss.sonatype.org/content/repositories/snapshots",
                        "ru.progrm-jarvis.minecraft", "minecraft-commons", "0.1.0-SNAPSHOT")
        );
    }
}