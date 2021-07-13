package ru.progrm_jarvis.minecraft.commons.mapimage;

import it.unimi.dsi.fastutil.ints.Int2ByteMap;
import it.unimi.dsi.fastutil.ints.Int2ByteMaps;
import it.unimi.dsi.fastutil.ints.Int2ByteOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.experimental.UtilityClass;
import lombok.val;
import ru.progrm_jarvis.minecraft.commons.util.image.ColorUtil;

import static ru.progrm_jarvis.minecraft.commons.mapimage.MapImageColor.NO_COLOR_CODE;

/**
 * Utilities related to Minecraft color codes for a map.
 */
@UtilityClass
public class MapImageMinecraftColors {

    /**
     * All available colors available in Minecraft associated with their {@code byte}-codes
     * @see <a href="https://minecraft.gamepedia.com/Map_item_format#1.12_Color_Table"> Taken from Minecraft Wiki</a>
     */
    public final Int2ByteMap MINECRAFT_RGB_COLOR_CODES;

    /**
     * All available colors available in Minecraft
     * <p>
     * @see <a href="https://minecraft.gamepedia.com/Map_item_format#1.12_Color_Table"> Taken from Minecraft Wiki</a>
     */
    private final IntSet MINECRAFT_RGB_COLORS;

    static {
        //<editor-fold desc="Minecraft colors registration" defaultstate="collapsed">
        val minecraftColors = new Int2ByteOpenHashMap(NO_COLOR_CODE);
        minecraftColors.put(ColorUtil.toArgb((byte) 89, (byte) 125, (byte) 39), (byte) 4);
        minecraftColors.put(ColorUtil.toArgb((byte) 109, (byte) 153, (byte) 48), (byte) 5);
        minecraftColors.put(ColorUtil.toArgb((byte) 127, (byte) 178, (byte) 56), (byte) 6);
        minecraftColors.put(ColorUtil.toArgb((byte) 67, (byte) 94, (byte) 29), (byte) 7);
        minecraftColors.put(ColorUtil.toArgb((byte) 174, (byte) 164, (byte) 115), (byte) 8);
        minecraftColors.put(ColorUtil.toArgb((byte) 213, (byte) 201, (byte) 140), (byte) 9);
        minecraftColors.put(ColorUtil.toArgb((byte) 247, (byte) 233, (byte) 163), (byte) 10);
        minecraftColors.put(ColorUtil.toArgb((byte) 130, (byte) 123, (byte) 86), (byte) 11);
        minecraftColors.put(ColorUtil.toArgb((byte) 140, (byte) 140, (byte) 140), (byte) 12);
        minecraftColors.put(ColorUtil.toArgb((byte) 171, (byte) 171, (byte) 171), (byte) 13);
        minecraftColors.put(ColorUtil.toArgb((byte) 199, (byte) 199, (byte) 199), (byte) 14);
        minecraftColors.put(ColorUtil.toArgb((byte) 105, (byte) 105, (byte) 105), (byte) 15);
        minecraftColors.put(ColorUtil.toArgb((byte) 180, (byte) 0, (byte) 0), (byte) 16);
        minecraftColors.put(ColorUtil.toArgb((byte) 220, (byte) 0, (byte) 0), (byte) 17);
        minecraftColors.put(ColorUtil.toArgb((byte) 255, (byte) 0, (byte) 0), (byte) 18);
        minecraftColors.put(ColorUtil.toArgb((byte) 135, (byte) 0, (byte) 0), (byte) 19);
        minecraftColors.put(ColorUtil.toArgb((byte) 112, (byte) 112, (byte) 180), (byte) 20);
        minecraftColors.put(ColorUtil.toArgb((byte) 138, (byte) 138, (byte) 220), (byte) 21);
        minecraftColors.put(ColorUtil.toArgb((byte) 160, (byte) 160, (byte) 255), (byte) 22);
        minecraftColors.put(ColorUtil.toArgb((byte) 84, (byte) 84, (byte) 135), (byte) 23);
        minecraftColors.put(ColorUtil.toArgb((byte) 117, (byte) 117, (byte) 117), (byte) 24);
        minecraftColors.put(ColorUtil.toArgb((byte) 144, (byte) 144, (byte) 144), (byte) 25);
        minecraftColors.put(ColorUtil.toArgb((byte) 167, (byte) 167, (byte) 167), (byte) 26);
        minecraftColors.put(ColorUtil.toArgb((byte) 88, (byte) 88, (byte) 88), (byte) 27);
        minecraftColors.put(ColorUtil.toArgb((byte) 0, (byte) 87, (byte) 0), (byte) 28);
        minecraftColors.put(ColorUtil.toArgb((byte) 0, (byte) 106, (byte) 0), (byte) 29);
        minecraftColors.put(ColorUtil.toArgb((byte) 0, (byte) 124, (byte) 0), (byte) 30);
        minecraftColors.put(ColorUtil.toArgb((byte) 0, (byte) 65, (byte) 0), (byte) 31);
        minecraftColors.put(ColorUtil.toArgb((byte) 180, (byte) 180, (byte) 180), (byte) 32);
        minecraftColors.put(ColorUtil.toArgb((byte) 220, (byte) 220, (byte) 220), (byte) 33);
        minecraftColors.put(ColorUtil.toArgb((byte) 255, (byte) 255, (byte) 255), (byte) 34);
        minecraftColors.put(ColorUtil.toArgb((byte) 135, (byte) 135, (byte) 135), (byte) 35);
        minecraftColors.put(ColorUtil.toArgb((byte) 115, (byte) 118, (byte) 129), (byte) 36);
        minecraftColors.put(ColorUtil.toArgb((byte) 141, (byte) 144, (byte) 158), (byte) 37);
        minecraftColors.put(ColorUtil.toArgb((byte) 164, (byte) 168, (byte) 184), (byte) 38);
        minecraftColors.put(ColorUtil.toArgb((byte) 86, (byte) 88, (byte) 97), (byte) 39);
        minecraftColors.put(ColorUtil.toArgb((byte) 106, (byte) 76, (byte) 54), (byte) 40);
        minecraftColors.put(ColorUtil.toArgb((byte) 130, (byte) 94, (byte) 66), (byte) 41);
        minecraftColors.put(ColorUtil.toArgb((byte) 151, (byte) 109, (byte) 77), (byte) 42);
        minecraftColors.put(ColorUtil.toArgb((byte) 79, (byte) 57, (byte) 40), (byte) 43);
        minecraftColors.put(ColorUtil.toArgb((byte) 79, (byte) 79, (byte) 79), (byte) 44);
        minecraftColors.put(ColorUtil.toArgb((byte) 96, (byte) 96, (byte) 96), (byte) 45);
        minecraftColors.put(ColorUtil.toArgb((byte) 112, (byte) 112, (byte) 112), (byte) 46);
        minecraftColors.put(ColorUtil.toArgb((byte) 59, (byte) 59, (byte) 59), (byte) 47);
        minecraftColors.put(ColorUtil.toArgb((byte) 45, (byte) 45, (byte) 180), (byte) 48);
        minecraftColors.put(ColorUtil.toArgb((byte) 55, (byte) 55, (byte) 220), (byte) 49);
        minecraftColors.put(ColorUtil.toArgb((byte) 64, (byte) 64, (byte) 255), (byte) 50);
        minecraftColors.put(ColorUtil.toArgb((byte) 33, (byte) 33, (byte) 135), (byte) 51);
        minecraftColors.put(ColorUtil.toArgb((byte) 100, (byte) 84, (byte) 50), (byte) 52);
        minecraftColors.put(ColorUtil.toArgb((byte) 123, (byte) 102, (byte) 62), (byte) 53);
        minecraftColors.put(ColorUtil.toArgb((byte) 143, (byte) 119, (byte) 72), (byte) 54);
        minecraftColors.put(ColorUtil.toArgb((byte) 75, (byte) 63, (byte) 38), (byte) 55);
        minecraftColors.put(ColorUtil.toArgb((byte) 180, (byte) 177, (byte) 172), (byte) 56);
        minecraftColors.put(ColorUtil.toArgb((byte) 220, (byte) 217, (byte) 211), (byte) 57);
        minecraftColors.put(ColorUtil.toArgb((byte) 255, (byte) 252, (byte) 245), (byte) 58);
        minecraftColors.put(ColorUtil.toArgb((byte) 135, (byte) 133, (byte) 129), (byte) 59);
        minecraftColors.put(ColorUtil.toArgb((byte) 152, (byte) 89, (byte) 36), (byte) 60);
        minecraftColors.put(ColorUtil.toArgb((byte) 186, (byte) 109, (byte) 44), (byte) 61);
        minecraftColors.put(ColorUtil.toArgb((byte) 216, (byte) 127, (byte) 51), (byte) 62);
        minecraftColors.put(ColorUtil.toArgb((byte) 114, (byte) 67, (byte) 27), (byte) 63);
        minecraftColors.put(ColorUtil.toArgb((byte) 125, (byte) 53, (byte) 152), (byte) 64);
        minecraftColors.put(ColorUtil.toArgb((byte) 153, (byte) 65, (byte) 186), (byte) 65);
        minecraftColors.put(ColorUtil.toArgb((byte) 178, (byte) 76, (byte) 216), (byte) 66);
        minecraftColors.put(ColorUtil.toArgb((byte) 94, (byte) 40, (byte) 114), (byte) 67);
        minecraftColors.put(ColorUtil.toArgb((byte) 72, (byte) 108, (byte) 152), (byte) 68);
        minecraftColors.put(ColorUtil.toArgb((byte) 88, (byte) 132, (byte) 186), (byte) 69);
        minecraftColors.put(ColorUtil.toArgb((byte) 102, (byte) 153, (byte) 216), (byte) 70);
        minecraftColors.put(ColorUtil.toArgb((byte) 54, (byte) 81, (byte) 114), (byte) 71);
        minecraftColors.put(ColorUtil.toArgb((byte) 161, (byte) 161, (byte) 36), (byte) 72);
        minecraftColors.put(ColorUtil.toArgb((byte) 197, (byte) 197, (byte) 44), (byte) 73);
        minecraftColors.put(ColorUtil.toArgb((byte) 229, (byte) 229, (byte) 51), (byte) 74);
        minecraftColors.put(ColorUtil.toArgb((byte) 121, (byte) 121, (byte) 27), (byte) 75);
        minecraftColors.put(ColorUtil.toArgb((byte) 89, (byte) 144, (byte) 17), (byte) 76);
        minecraftColors.put(ColorUtil.toArgb((byte) 109, (byte) 176, (byte) 21), (byte) 77);
        minecraftColors.put(ColorUtil.toArgb((byte) 127, (byte) 204, (byte) 25), (byte) 78);
        minecraftColors.put(ColorUtil.toArgb((byte) 67, (byte) 108, (byte) 13), (byte) 79);
        minecraftColors.put(ColorUtil.toArgb((byte) 170, (byte) 89, (byte) 116), (byte) 80);
        minecraftColors.put(ColorUtil.toArgb((byte) 208, (byte) 109, (byte) 142), (byte) 81);
        minecraftColors.put(ColorUtil.toArgb((byte) 242, (byte) 127, (byte) 165), (byte) 82);
        minecraftColors.put(ColorUtil.toArgb((byte) 128, (byte) 67, (byte) 87), (byte) 83);
        minecraftColors.put(ColorUtil.toArgb((byte) 53, (byte) 53, (byte) 53), (byte) 84);
        minecraftColors.put(ColorUtil.toArgb((byte) 65, (byte) 65, (byte) 65), (byte) 85);
        minecraftColors.put(ColorUtil.toArgb((byte) 76, (byte) 76, (byte) 76), (byte) 86);
        minecraftColors.put(ColorUtil.toArgb((byte) 40, (byte) 40, (byte) 40), (byte) 87);
        minecraftColors.put(ColorUtil.toArgb((byte) 108, (byte) 108, (byte) 108), (byte) 88);
        minecraftColors.put(ColorUtil.toArgb((byte) 132, (byte) 132, (byte) 132), (byte) 89);
        minecraftColors.put(ColorUtil.toArgb((byte) 153, (byte) 153, (byte) 153), (byte) 90);
        minecraftColors.put(ColorUtil.toArgb((byte) 81, (byte) 81, (byte) 81), (byte) 91);
        minecraftColors.put(ColorUtil.toArgb((byte) 53, (byte) 89, (byte) 108), (byte) 92);
        minecraftColors.put(ColorUtil.toArgb((byte) 65, (byte) 109, (byte) 132), (byte) 93);
        minecraftColors.put(ColorUtil.toArgb((byte) 76, (byte) 127, (byte) 153), (byte) 94);
        minecraftColors.put(ColorUtil.toArgb((byte) 40, (byte) 67, (byte) 81), (byte) 95);
        minecraftColors.put(ColorUtil.toArgb((byte) 89, (byte) 44, (byte) 125), (byte) 96);
        minecraftColors.put(ColorUtil.toArgb((byte) 109, (byte) 54, (byte) 153), (byte) 97);
        minecraftColors.put(ColorUtil.toArgb((byte) 127, (byte) 63, (byte) 178), (byte) 98);
        minecraftColors.put(ColorUtil.toArgb((byte) 67, (byte) 33, (byte) 94), (byte) 99);
        minecraftColors.put(ColorUtil.toArgb((byte) 36, (byte) 53, (byte) 125), (byte) 100);
        minecraftColors.put(ColorUtil.toArgb((byte) 44, (byte) 65, (byte) 153), (byte) 101);
        minecraftColors.put(ColorUtil.toArgb((byte) 51, (byte) 76, (byte) 178), (byte) 102);
        minecraftColors.put(ColorUtil.toArgb((byte) 27, (byte) 40, (byte) 94), (byte) 103);
        minecraftColors.put(ColorUtil.toArgb((byte) 72, (byte) 53, (byte) 36), (byte) 104);
        minecraftColors.put(ColorUtil.toArgb((byte) 88, (byte) 65, (byte) 44), (byte) 105);
        minecraftColors.put(ColorUtil.toArgb((byte) 102, (byte) 76, (byte) 51), (byte) 106);
        minecraftColors.put(ColorUtil.toArgb((byte) 54, (byte) 40, (byte) 27), (byte) 107);
        minecraftColors.put(ColorUtil.toArgb((byte) 72, (byte) 89, (byte) 36), (byte) 108);
        minecraftColors.put(ColorUtil.toArgb((byte) 88, (byte) 109, (byte) 44), (byte) 109);
        minecraftColors.put(ColorUtil.toArgb((byte) 102, (byte) 127, (byte) 51), (byte) 110);
        minecraftColors.put(ColorUtil.toArgb((byte) 54, (byte) 67, (byte) 27), (byte) 111);
        minecraftColors.put(ColorUtil.toArgb((byte) 108, (byte) 36, (byte) 36), (byte) 112);
        minecraftColors.put(ColorUtil.toArgb((byte) 132, (byte) 44, (byte) 44), (byte) 113);
        minecraftColors.put(ColorUtil.toArgb((byte) 153, (byte) 51, (byte) 51), (byte) 114);
        minecraftColors.put(ColorUtil.toArgb((byte) 81, (byte) 27, (byte) 27), (byte) 115);
        minecraftColors.put(ColorUtil.toArgb((byte) 17, (byte) 17, (byte) 17), (byte) 116);
        minecraftColors.put(ColorUtil.toArgb((byte) 21, (byte) 21, (byte) 21), (byte) 117);
        minecraftColors.put(ColorUtil.toArgb((byte) 25, (byte) 25, (byte) 25), (byte) 118);
        minecraftColors.put(ColorUtil.toArgb((byte) 13, (byte) 13, (byte) 13), (byte) 119);
        minecraftColors.put(ColorUtil.toArgb((byte) 176, (byte) 168, (byte) 54), (byte) 120);
        minecraftColors.put(ColorUtil.toArgb((byte) 215, (byte) 205, (byte) 66), (byte) 121);
        minecraftColors.put(ColorUtil.toArgb((byte) 250, (byte) 238, (byte) 77), (byte) 122);
        minecraftColors.put(ColorUtil.toArgb((byte) 132, (byte) 126, (byte) 40), (byte) 123);
        minecraftColors.put(ColorUtil.toArgb((byte) 64, (byte) 154, (byte) 150), (byte) 124);
        minecraftColors.put(ColorUtil.toArgb((byte) 79, (byte) 188, (byte) 183), (byte) 125);
        minecraftColors.put(ColorUtil.toArgb((byte) 92, (byte) 219, (byte) 213), (byte) 126);
        minecraftColors.put(ColorUtil.toArgb((byte) 48, (byte) 115, (byte) 112), (byte) 127);
        minecraftColors.put(ColorUtil.toArgb((byte) 52, (byte) 90, (byte) 180), (byte) 128);
        minecraftColors.put(ColorUtil.toArgb((byte) 63, (byte) 110, (byte) 220), (byte) 129);
        minecraftColors.put(ColorUtil.toArgb((byte) 74, (byte) 128, (byte) 255), (byte) 130);
        minecraftColors.put(ColorUtil.toArgb((byte) 39, (byte) 67, (byte) 135), (byte) 131);
        minecraftColors.put(ColorUtil.toArgb((byte) 0, (byte) 153, (byte) 40), (byte) 132);
        minecraftColors.put(ColorUtil.toArgb((byte) 0, (byte) 187, (byte) 50), (byte) 133);
        minecraftColors.put(ColorUtil.toArgb((byte) 0, (byte) 217, (byte) 58), (byte) 134);
        minecraftColors.put(ColorUtil.toArgb((byte) 0, (byte) 114, (byte) 30), (byte) 135);
        minecraftColors.put(ColorUtil.toArgb((byte) 91, (byte) 60, (byte) 34), (byte) 136);
        minecraftColors.put(ColorUtil.toArgb((byte) 111, (byte) 74, (byte) 42), (byte) 137);
        minecraftColors.put(ColorUtil.toArgb((byte) 129, (byte) 86, (byte) 49), (byte) 138);
        minecraftColors.put(ColorUtil.toArgb((byte) 68, (byte) 45, (byte) 25), (byte) 139);
        minecraftColors.put(ColorUtil.toArgb((byte) 79, (byte) 1, (byte) 0), (byte) 140);
        minecraftColors.put(ColorUtil.toArgb((byte) 96, (byte) 1, (byte) 0), (byte) 141);
        minecraftColors.put(ColorUtil.toArgb((byte) 112, (byte) 2, (byte) 0), (byte) 142);
        minecraftColors.put(ColorUtil.toArgb((byte) 59, (byte) 1, (byte) 0), (byte) 143);
        minecraftColors.put(ColorUtil.toArgb((byte) 147, (byte) 124, (byte) 113), (byte) 144);
        minecraftColors.put(ColorUtil.toArgb((byte) 180, (byte) 152, (byte) 138), (byte) 145);
        minecraftColors.put(ColorUtil.toArgb((byte) 209, (byte) 177, (byte) 161), (byte) 146);
        minecraftColors.put(ColorUtil.toArgb((byte) 110, (byte) 93, (byte) 85), (byte) 147);
        minecraftColors.put(ColorUtil.toArgb((byte) 112, (byte) 57, (byte) 25), (byte) 148);
        minecraftColors.put(ColorUtil.toArgb((byte) 137, (byte) 70, (byte) 31), (byte) 149);
        minecraftColors.put(ColorUtil.toArgb((byte) 159, (byte) 82, (byte) 36), (byte) 150);
        minecraftColors.put(ColorUtil.toArgb((byte) 84, (byte) 43, (byte) 19), (byte) 151);
        minecraftColors.put(ColorUtil.toArgb((byte) 105, (byte) 61, (byte) 76), (byte) 152);
        minecraftColors.put(ColorUtil.toArgb((byte) 128, (byte) 75, (byte) 93), (byte) 153);
        minecraftColors.put(ColorUtil.toArgb((byte) 149, (byte) 87, (byte) 108), (byte) 154);
        minecraftColors.put(ColorUtil.toArgb((byte) 78, (byte) 46, (byte) 57), (byte) 155);
        minecraftColors.put(ColorUtil.toArgb((byte) 79, (byte) 76, (byte) 97), (byte) 156);
        minecraftColors.put(ColorUtil.toArgb((byte) 96, (byte) 93, (byte) 119), (byte) 157);
        minecraftColors.put(ColorUtil.toArgb((byte) 112, (byte) 108, (byte) 138), (byte) 158);
        minecraftColors.put(ColorUtil.toArgb((byte) 59, (byte) 57, (byte) 73), (byte) 159);
        minecraftColors.put(ColorUtil.toArgb((byte) 131, (byte) 93, (byte) 25), (byte) 160);
        minecraftColors.put(ColorUtil.toArgb((byte) 160, (byte) 114, (byte) 31), (byte) 161);
        minecraftColors.put(ColorUtil.toArgb((byte) 186, (byte) 133, (byte) 36), (byte) 162);
        minecraftColors.put(ColorUtil.toArgb((byte) 98, (byte) 70, (byte) 19), (byte) 163);
        minecraftColors.put(ColorUtil.toArgb((byte) 72, (byte) 82, (byte) 37), (byte) 164);
        minecraftColors.put(ColorUtil.toArgb((byte) 88, (byte) 100, (byte) 45), (byte) 165);
        minecraftColors.put(ColorUtil.toArgb((byte) 103, (byte) 117, (byte) 53), (byte) 166);
        minecraftColors.put(ColorUtil.toArgb((byte) 54, (byte) 61, (byte) 28), (byte) 167);
        minecraftColors.put(ColorUtil.toArgb((byte) 112, (byte) 54, (byte) 55), (byte) 168);
        minecraftColors.put(ColorUtil.toArgb((byte) 138, (byte) 66, (byte) 67), (byte) 169);
        minecraftColors.put(ColorUtil.toArgb((byte) 160, (byte) 77, (byte) 78), (byte) 170);
        minecraftColors.put(ColorUtil.toArgb((byte) 84, (byte) 40, (byte) 41), (byte) 171);
        minecraftColors.put(ColorUtil.toArgb((byte) 40, (byte) 28, (byte) 24), (byte) 172);
        minecraftColors.put(ColorUtil.toArgb((byte) 49, (byte) 35, (byte) 30), (byte) 173);
        minecraftColors.put(ColorUtil.toArgb((byte) 57, (byte) 41, (byte) 35), (byte) 174);
        minecraftColors.put(ColorUtil.toArgb((byte) 30, (byte) 21, (byte) 18), (byte) 175);
        minecraftColors.put(ColorUtil.toArgb((byte) 95, (byte) 75, (byte) 69), (byte) 176);
        minecraftColors.put(ColorUtil.toArgb((byte) 116, (byte) 92, (byte) 84), (byte) 177);
        minecraftColors.put(ColorUtil.toArgb((byte) 135, (byte) 107, (byte) 98), (byte) 178);
        minecraftColors.put(ColorUtil.toArgb((byte) 71, (byte) 56, (byte) 51), (byte) 179);
        minecraftColors.put(ColorUtil.toArgb((byte) 61, (byte) 64, (byte) 64), (byte) 180);
        minecraftColors.put(ColorUtil.toArgb((byte) 75, (byte) 79, (byte) 79), (byte) 181);
        minecraftColors.put(ColorUtil.toArgb((byte) 87, (byte) 92, (byte) 92), (byte) 182);
        minecraftColors.put(ColorUtil.toArgb((byte) 46, (byte) 48, (byte) 48), (byte) 183);
        minecraftColors.put(ColorUtil.toArgb((byte) 86, (byte) 51, (byte) 62), (byte) 184);
        minecraftColors.put(ColorUtil.toArgb((byte) 105, (byte) 62, (byte) 75), (byte) 185);
        minecraftColors.put(ColorUtil.toArgb((byte) 122, (byte) 73, (byte) 88), (byte) 186);
        minecraftColors.put(ColorUtil.toArgb((byte) 64, (byte) 38, (byte) 46), (byte) 187);
        minecraftColors.put(ColorUtil.toArgb((byte) 53, (byte) 43, (byte) 64), (byte) 188);
        minecraftColors.put(ColorUtil.toArgb((byte) 65, (byte) 53, (byte) 79), (byte) 189);
        minecraftColors.put(ColorUtil.toArgb((byte) 76, (byte) 62, (byte) 92), (byte) 190);
        minecraftColors.put(ColorUtil.toArgb((byte) 40, (byte) 32, (byte) 48), (byte) 191);
        minecraftColors.put(ColorUtil.toArgb((byte) 53, (byte) 35, (byte) 24), (byte) 192);
        minecraftColors.put(ColorUtil.toArgb((byte) 65, (byte) 43, (byte) 30), (byte) 193);
        minecraftColors.put(ColorUtil.toArgb((byte) 76, (byte) 50, (byte) 35), (byte) 194);
        minecraftColors.put(ColorUtil.toArgb((byte) 40, (byte) 26, (byte) 18), (byte) 195);
        minecraftColors.put(ColorUtil.toArgb((byte) 53, (byte) 57, (byte) 29), (byte) 196);
        minecraftColors.put(ColorUtil.toArgb((byte) 65, (byte) 70, (byte) 36), (byte) 197);
        minecraftColors.put(ColorUtil.toArgb((byte) 76, (byte) 82, (byte) 42), (byte) 198);
        minecraftColors.put(ColorUtil.toArgb((byte) 40, (byte) 43, (byte) 22), (byte) 199);
        minecraftColors.put(ColorUtil.toArgb((byte) 100, (byte) 42, (byte) 32), (byte) 200);
        minecraftColors.put(ColorUtil.toArgb((byte) 122, (byte) 51, (byte) 39), (byte) 201);
        minecraftColors.put(ColorUtil.toArgb((byte) 142, (byte) 60, (byte) 46), (byte) 202);
        minecraftColors.put(ColorUtil.toArgb((byte) 75, (byte) 31, (byte) 24), (byte) 203);
        minecraftColors.put(ColorUtil.toArgb((byte) 26, (byte) 15, (byte) 11), (byte) 204);
        minecraftColors.put(ColorUtil.toArgb((byte) 31, (byte) 18, (byte) 13), (byte) 205);
        minecraftColors.put(ColorUtil.toArgb((byte) 37, (byte) 22, (byte) 16), (byte) 206);
        minecraftColors.put(ColorUtil.toArgb((byte) 19, (byte) 11, (byte) 8), (byte) 207);

        MINECRAFT_RGB_COLOR_CODES = Int2ByteMaps.unmodifiable(minecraftColors);
        MINECRAFT_RGB_COLORS = MINECRAFT_RGB_COLOR_CODES.keySet();
        //</editor-fold>
    }

    /**
     * Creates a new primitive map
     * with keys being an {@code int} representation of an RGB color in Minecraft map
     * and value being its code in Minecraft.
     *
     * @return primitive map of available map color codes in minecraft by their RGB colors
     */
    public Int2ByteMap asNewPrimitiveMap() {
        return new Int2ByteOpenHashMap(MINECRAFT_RGB_COLOR_CODES);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Minecraft color conversions
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Checks whether the specified color can be shown on a map without any distortion.
     *
     * @param rgb RGB color as {@code int}
     * @return {@code true} if this color can be shown on an in-game map without distortion anf {@code false} otherwise
     */
    public boolean isMinecraftColor(final int rgb) {
        for (val minecraftRgbColor : MINECRAFT_RGB_COLORS) if (rgb == minecraftRgbColor) return true;
        return false;
    }

    /**
     * Checks whether the specified color can be shown on a map without any distortion.
     *
     * @param red red channel of the RGB color
     * @param green green channel of the RGB color
     * @param blue blue channel of the RGB color
     * @return {@code true} if this color can be shown on an in-game map without distortion anf {@code false} otherwise
     */
    public boolean isMinecraftColor(final byte red, final byte green, final byte blue) {
        return isMinecraftColor(ColorUtil.toArgb(red, green, blue));
    }

    /**
     * Gets Minecraft map color code for the specified color.
     *
     * @param rgb RGB color as {@code int}
     * @return non-zero value being the found minecraft code for the color or {@code 0} if none was found
     */
    public byte getMinecraftColorCode(final int rgb) {
        return MINECRAFT_RGB_COLOR_CODES.get(rgb);
    }

    /**
     * Gets Minecraft map color code for the specified color.
     *
     * @param red red channel of the RGB color
     * @param green green channel of the RGB color
     * @param blue blue channel of the RGB color
     * @return value being the found minecraft code for the color
     * or {@link MapImageColor#NO_COLOR_CODE} if none was found
     */
    public byte getMinecraftColorCode(final byte red, final byte green, final byte blue) {
        return MINECRAFT_RGB_COLOR_CODES.get(ColorUtil.toArgb(red, green, blue));
    }
}
