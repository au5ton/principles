#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <strings.h>
#include <MagickWand/MagickWand.h>

int main(int argc, char **argv) {
	init_context();

	FILE *infile;
	FILE *outfile;

	infile = fopen(argv[1], "r");
	if (infile == NULL) {
		fprintf(stderr, "No such file or directory: %s\n", argv[1]);
		return 1;
	}

	outfile = fopen(argv[2], "wb");
	if (outfile == NULL) {
		fprintf(stderr, "Error opening file: %s\n", argv[2]);
		return 1;
	}

	MagickWandGenesis();
	MagickWand *image = NewMagickWand();

	MagickBooleanType mwstatus = MagickReadImageFile(image, infile);
	if (mwstatus == MagickFalse) {
		ExceptionType type;
		fprintf(stderr, "Error reading image file: %s\n", argv[1]);
		fprintf(stderr, "ImageMagick says: %s\n", MagickGetException(image, &type));
		return 1;
	}

	unsigned long height = MagickGetImageHeight(image);
	unsigned long width = MagickGetImageWidth(image);

	PixelIterator *iter = NewPixelIterator(image);
	PixelWand **row;

	unsigned long x, y;
	for (y = 0; y < height; y++) {
		row = PixelGetNextIteratorRow(iter, &width);
		for (x = 0; x < width; x++) {
			double r = PixelGetRed(&row);
			double g = PixelGetGreen(&row);
			double b = PixelGetBlue(&row);
			double a = PixelGetAlpha(&row);

			// do your thing here

			PixelSetRed(r);
			PixelSetGreen(g);
			PixelSetBlue(b);
			PixelSetAlpha(a);
		}
	}

	mwstatus = MagickWriteImageFile(image, outfile);
	if (mwstatus == MagickFalse) {
		ExceptionType type;
		fprintf(stderr, "Error writing image file: %s\n", argv[2]);
		fprintf(stderr, "ImageMagick says: %s\n", MagickGetException(image, &type));
		return 1;
	}

	iter = DestroyPixelIterator(image);
	image = DestroyMagickWand(image);
	MagickWandTerminus();
	fclose(outfile);

	return 0;
}
