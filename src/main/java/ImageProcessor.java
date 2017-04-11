import javax.imageio.ImageIO; //For outputting the final image
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import magick.*;


public class ImageProcessor {

    //This is basically a port of:
    // https://github.com/au5ton/codered-steganography/blob/master/stego.js

    //TODO Extract RGBA values from pixel int
    //implemented in Pixel.java

    public static void embedData(MagickImage img, byte[] mySecret) throws MagickException {

        //Max size of image is 2147483639 bytes (2.1 GB) (Integer.MAX_VALUE - 8)

        PixelPacket[] pixelArray = reformatToPixelArray(img);

        if(pixelArray.length < mySecret.length + 4) {
            System.out.println("Not enough space available in image for the data you requested.");
            System.exit(1);
        }

        //if(img.)

        //necessary for decoding: describes the size of the data at the front of the file
        byte[] header = ByteBuffer.allocate(4).putInt(mySecret.length).array(); // mySecret.length -> byte[]

        int n = 0;
        for(int i = 0; i < header.length+mySecret.length; i++) {
            if(i < 4) {
                pixelArray[i] = encodeByteInPixel(pixelArray[i], header[i]);
            }
            else {
                pixelArray[i] = encodeByteInPixel(pixelArray[i], mySecret[n]);
                n++;
            }
        }

        int j = 0;
        for(int x = 0; x < img.getXResolution(); x++) {
            for(int y = 0; y < img.getYResolution(); y++) {
                img.getOnePixel(x,y).setRed(pixelArray[j].getRed());
                img.getOnePixel(x,y).setGreen(pixelArray[j].getGreen());
                img.getOnePixel(x,y).setBlue(pixelArray[j].getBlue());
                img.getOnePixel(x,y).setOpacity(pixelArray[j].getOpacity());
                j++;
            }
        }

    }

    public static PixelPacket[] reformatToPixelArray(MagickImage img) throws MagickException {
        PixelPacket[] pix = new PixelPacket[((int)img.getXResolution())*((int)img.getYResolution())];
        int i = 0;
        for(int x = 0; x < (int)img.getXResolution(); x++) {
            for(int y = 0; y < (int)img.getYResolution(); y++) {
                pix[i] = img.getOnePixel(x,y);
                i++;
            }
        }
        return pix;
    }

    public static PixelPacket encodeByteInPixel(PixelPacket pixel, byte b) throws MagickException {
        int bit;
        for(int i = 0; i < 8; i++) {
            // get value of bit
            if ((b & (1 << 7-i)) == 0) {
                bit = 1;
            }
            else {
                bit = 0;
            }

            // encode bit in appropriate position in appropriate channel
            if (i == 0 || i == 1) {
                pixel = encodeBitInChannel(pixel, 'r', bit, i);
            }
            else if (i == 2 || i == 3) {
                pixel = encodeBitInChannel(pixel, 'g', bit, i % 2);
            }
            else if (i == 4 || i == 5) {
                pixel = encodeBitInChannel(pixel, 'b', bit, i % 2);
            }
            else {
                // position-1 to do LSBs 2 and 1 instead of 1 and 0
                pixel = encodeBitInChannel(pixel, 'a', bit, (i % 2)-1);
            }
        }
        return pixel;
    }

    public static PixelPacket encodeBitInChannel(PixelPacket pixel, char channel, int bit, int position) throws MagickException {
        if (bit == 1) {
            // set bit to 1
            if(channel == 'r') {
                pixel.setRed(pixel.getRed() | (1 << 1-position));
            }
            else if(channel == 'g') {
                pixel.setGreen(pixel.getGreen() | (1 << 1-position));
            }
            else if(channel == 'b') {
                pixel.setBlue(pixel.getBlue() | (1 << 1-position));
            }
            else if(channel == 'a') {
                pixel.setOpacity(pixel.getOpacity() | (1 << 1-position));
            }
        }
        else {
            // set bit to 0
            //pixel[channel] &= ~(1 << 1-position);
            if(channel == 'r') {
                pixel.setRed(pixel.getRed() & ~(1 << 1-position));
            }
            else if(channel == 'g') {
                pixel.setGreen(pixel.getGreen() & ~(1 << 1-position));
            }
            else if(channel == 'b') {
                pixel.setBlue(pixel.getBlue() & ~(1 << 1-position));
            }
            else if(channel == 'a') {
                pixel.setOpacity(pixel.getOpacity() & ~(1 << 1-position));
            }
        }
        return pixel;
    }

    //TODO ✅ Get pixel matrix from imagebuffer

    //TODO ✅ Reformat pixel matrix to buffer data

    //TODO ✅ Encode data (steganography) into pixel matrix

    //TODO ✅ Encode byte in pixel

    //TODO ✅ Encode bit in channel

    //TODO Decode data from pixel matrix

    //TODO Decode byte from pixel

    //TODO Decode bit from channel

}
