package ru.progrm_jarvis.minecraft.commons.util;

import lombok.var;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BitwiseUtilTest {

    @Test
    void testUnsignedIntByteConversions() {
        for (var i = 0; i < 255; i++) assertEquals(i, BitwiseUtil.byteToUnsignedInt(BitwiseUtil.unsignedIntToByte(i)));
    }
}