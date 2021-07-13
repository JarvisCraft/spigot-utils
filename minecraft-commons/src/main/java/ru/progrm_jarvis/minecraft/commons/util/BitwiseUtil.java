package ru.progrm_jarvis.minecraft.commons.util;

import lombok.experimental.UtilityClass;
import lombok.val;

/**
 * Utility for bitwise operations.
 */
@UtilityClass
public class BitwiseUtil {

    /**
     * Number of bits in a {@code byte}
     */
    private static final int BYTE_BITS = 8,
    /**
     * Number of bits in a {@code short}
     */
    SHORT_BITS = 16,
    /**
     * Number of bits in a {@code int}
     */
    INT_BITS = 32,
    /**
     * Number of bits in a {@code long}
     */
    LONG_BITS = 64,
    /**
     * Number of bits in a {@code char}
     */
    CHAR_BITS = 16;

    private int bit(byte num, int index) {
        return num >> (BYTE_BITS - index + 1) & 0x1;
    }

    private int bit(short num, int index) {
        return num >> (SHORT_BITS - index + 1) & 0x1;
    }

    private int bit(int num, int index) {
        return num >> (INT_BITS - index + 1) & 0x1;
    }

    private int bit(long num, int index) {
        return (int) (num >> (LONG_BITS - index + 1) & 0x1);
    }

    private int bit(char num, int index) {
        return num >> (CHAR_BITS - index + 1) & 0x1;
    }

    private int bit(byte num, int index, int bit) {
        val mask = bit << (BYTE_BITS - index + 1);
        return num & ~mask & mask;
    }

    private int bit(short num, int index, int bit) {
        val mask = bit << (SHORT_BITS - index + 1);
        return num & ~mask & mask;
    }

    private int bit(int num, int index, int bit) {
        val mask = bit << (INT_BITS - index + 1);
        return num & ~mask & mask;
    }

    private int bit(long num, int index, int bit) {
        val mask = (long) bit << (LONG_BITS - index + 1);
        return (int) (num & ~mask & mask);
    }

    private int bit(char num, int index, int bit) {
        val mask = bit << (CHAR_BITS - index + 1);
        return num & ~mask & mask;
    }

    public String getBits(final byte num, final int bitsCount) {
        val bits = new char[bitsCount];

        for (int i = 0, index = bitsCount - 1; i < bitsCount; i++, index--) bits[index]
                = (num >> i & 0x1) == 1 ? '1' : '0';

        return new String(bits);
    }

    public String getBits(final short num, final int bitsCount) {
        val bits = new char[bitsCount];

        for (int i = 0, index = bitsCount - 1; i < bitsCount; i++, index--) bits[index]
                = (num >> i & 0x1) == 1 ? '1' : '0';

        return new String(bits);
    }

    public String getBits(final int num, final int bitsCount) {
        val bits = new char[bitsCount];

        for (int i = 0, index = bitsCount - 1; i < bitsCount; i++, index--) bits[index]
                = (num >> i & 0x1) == 1 ? '1' : '0';

        return new String(bits);
    }

    public String getBits(final long num, final int bitsCount) {
        val bits = new char[bitsCount];

        for (int i = 0, index = bitsCount - 1; i < bitsCount; i++, index--) bits[index]
                = (num >> i & 0x1) == 1 ? '1' : '0';

        return new String(bits);
    }

    public String getBits(final char num, final int bitsCount) {
        val bits = new char[bitsCount];

        for (int i = 0, index = bitsCount - 1; i < bitsCount; i++, index--) bits[index]
                = (num >> i & 0x1) == 1 ? '1' : '0';

        return new String(bits);
    }

    public String getBits(final byte num) {
        return getBits(num, BYTE_BITS);
    }

    public String getBits(final short num) {
        return getBits(num, SHORT_BITS);
    }

    public String getBits(final int num) {
        return getBits(num, INT_BITS);
    }

    public String getBits(final long num) {
        return getBits(num, LONG_BITS);
    }

    public String getBits(final char num) {
        return getBits(num, CHAR_BITS);
    }

    public int impl(final int bit1, final int bit) {
        return ~bit1 | bit;
    }

    public byte implicate(byte num1, final byte num2) {
        for (int i = 0; i < BYTE_BITS; i++) num1 = (byte) bit(num1, i, impl(bit(num1, i), bit(num2, i)));

        return num1;
    }

    public short implicate(short num1, final short num2) {
        for (int i = 0; i < SHORT_BITS; i++) num1 = (short) bit(num1, i, impl(bit(num1, i), bit(num2, i)));

        return num1;
    }

    public int implicate(int num1, final int num2) {
        for (int i = 0; i < INT_BITS; i++) num1 = bit(num1, i, impl(bit(num1, i), bit(num2, i)));

        return num1;
    }

    public long implicate(long num1, final long num2) {
        for (int i = 0; i < LONG_BITS; i++) num1 = bit(num1, i, impl(bit(num1, i), bit(num2, i)));

        return num1;
    }

    public char implicate(char num1, final char num2) {
        for (int i = 0; i < INT_BITS; i++) num1 = (char) bit(num1, i, impl(bit(num1, i), bit(num2, i)));

        return num1;
    }

    /**
     * Converts the unsigned {@code int} to a 8-bit ({@code byte}) representation.
     *
     * @param unsignedInt integer whose least significant 8 bits are to be stored in a byte
     * @return unsigned integer's least significant 8 bits in a single byte
     */
    public byte unsignedIntToByte(final int unsignedInt) {
        return (byte) unsignedInt;
    }

    /**
     * Converts the 8-bit ({@code byte}) value to an unsigned int representation.
     *
     * @param byteValue byte value whose bits will be used as the trailing bits of the resulting integer
     * @return an integer value consisting of 24 foremost <code>0</code>s and 8 bits of the specified byte
     */
    public int byteToUnsignedInt(final byte byteValue) {
        return byteValue & 0xFF;
    }
}
