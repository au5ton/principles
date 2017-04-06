public class Pixel {
    public byte r, g, b;
    public Pixel(int r, int g, int b) {
        int r_temp = r & 0x0FF;
        this.r = (byte)r_temp;
        this.g = ((byte)g) & 0x0FF;
        this.b = ((byte)b) & 0x0FF;
    }
    public Pixel(int rgb) {
        this.r = getBytesFromInt(rgb)[0];
        this.g = getBytesFromInt(rgb)[1];
        this.b = getBytesFromInt(rgb)[2];
    }

    public static int getIntFromByte(byte r, byte g, byte b) {

        // n gets bitwise AND'd with 0x0ff to limit the value to be a maximum of 255 (0x0ff),
        // basically to get the unsigned byte
        // then each gets shifted a certain amount so each byte will fit in a Java 4-byte integer
        // the bitwise OR basically concatenates all 3 separate bytes into one

        /*

        r << 16: 00000000 11111111 00000000 00000000
        g << 8:  00000000 00000000 11111111 00000000
        b << 0:  00000000 00000000 00000000 11111111

        bitwise OR
        |
        \/

                 00000000 11111111 11111111 11111111



        */

        return ((r&0x0ff)<<16)|((g&0x0ff)<<8)|(b&0x0ff);
    }
    public static byte[] getBytesFromInt(int rgb) {

        // int rgb has 3 separate bytes embedded in order in it's 4-byte size
        // to extract each, we shift the bits by the size of the other 2 bytes
        // sort of like String.substring()

        return new byte[]{
            ((byte)(rgb>>16)) & 0x0ff,
            ((byte)(rgb>>8)) & 0x0ff,
            ((byte)(rgb)) & 0x0ff
        };
    }
}
