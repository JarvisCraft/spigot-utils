package ru.progrm_jarvis.minecraft.commons.nms;

import com.comphenix.packetwrapper.AbstractPacket;
import com.google.common.reflect.ClassPath;
import lombok.val;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import ru.progrm_jarvis.minecraft.commons.nms.protocol.misc.PacketWrapperUtil;
import ru.progrm_jarvis.reflector.wrapper.invoke.InvokeConstructorWrapper;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.reflect.ClassPath.from;
import static java.util.regex.Pattern.compile;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PacketWrapperUtilTest {

    private static final Pattern
            PACKET_WRAPPER_CLASS_NAME_PATTERN = compile("Wrapper[A-Z][a-z]+(?:Client|Server)[A-Z][A-Za-z]+"),
            PACKET_WRAPPER_TO_STRING_PATTERN = compile("Wrapper[A-Z][a-z]+(?:Client|Server)[A-Z][A-Za-z]+\\{.*}");

    @Test
    void getterNameToStringTest() {
        assertEquals("lel", PacketWrapperUtil.getterNameToString("lel"));
        assertEquals("foo", PacketWrapperUtil.getterNameToString("getFoo"));
        assertEquals("barBaz", PacketWrapperUtil.getterNameToString("getBarBaz"));
        assertEquals("get", PacketWrapperUtil.getterNameToString("get"));
    }

    @Test
    @Disabled("Requires PortocolManager singleton at runtime")
    @SuppressWarnings({"UnstableApiUsage", "unchecked"})
    void defaultPacketsTest() throws IOException {
        val classLoader = AbstractPacket.class.getClassLoader();

        from(classLoader)
                .getTopLevelClasses(AbstractPacket.class.getPackage().getName())
                .stream()
                .filter(classInfo -> PACKET_WRAPPER_CLASS_NAME_PATTERN.matcher(classInfo.getSimpleName()).matches())
                .map(ClassPath.ClassInfo::getName)
                .map((Function<String, Class<? extends AbstractPacket>>) className
                        -> {
                    try {
                        return (Class<? extends AbstractPacket>) classLoader.loadClass(className);
                    } catch (final ClassNotFoundException e) {
                        throw new IllegalStateException("Could not find class by name \"" + className + '"');
                    }
                })
                .map((Function<Class<? extends AbstractPacket>, Constructor<? extends AbstractPacket>>) clazz -> {
                    try {
                        return clazz.getDeclaredConstructor();
                    } catch (final NoSuchMethodException e) {
                        throw new IllegalStateException("Could not find empty constructor of class " + clazz);
                    }
                })
                .map(InvokeConstructorWrapper::from)
                .collect(Collectors.toSet())
                .forEach(constructor -> {
                    System.out.println("Testing: " + constructor);
                    for (int i = 0; i < 3 + RandomUtils.nextInt(3); i++)
                        assertDoesNotThrow(
                                () -> PacketWrapperUtil.toString(constructor.invoke())
                        );
                });
    }
}