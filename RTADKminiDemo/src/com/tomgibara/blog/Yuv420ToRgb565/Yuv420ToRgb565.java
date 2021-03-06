package com.tomgibara.blog.Yuv420ToRgb565;

// This code was from http://blog.tomgibara.com/post/132956174/yuv420-to-rgb565-conversion-in-android
// Many thanks to Tom Gibara.

public class Yuv420ToRgb565 {
	/**
	 * Converts semi-planar YUV420 as generated for camera preview into RGB565
	 * format for use as an OpenGL ES texture. It assumes that both the input
	 * and output data are contiguous and start at zero.
	 * 
	 * @param yuvs
	 *            the array of YUV420 semi-planar data
	 * @param rgbs
	 *            an array into which the RGB565 data will be written
	 * @param width
	 *            the number of pixels horizontally
	 * @param height
	 *            the number of pixels vertically
	 */

	// we tackle the conversion two pixels at a time for greater speed
	public static void toRGB565(byte[] yuvs, int width, int height, int[] rgbs) {
		// the end of the luminance data
		final int lumEnd = width * height;
		// points to the next luminance value pair
		int lumPtr = 0;
		// points to the next chromiance value pair
		int chrPtr = lumEnd;
		// points to the next byte output pair of RGB565 value
		int outPtr = 0;
		// the end of the current luminance scanline
		int lineEnd = width;

		while (true) {

			// skip back to the start of the chromiance values when necessary
			if (lumPtr == lineEnd) {
				if (lumPtr == lumEnd)
					break; // we've reached the end
				// division here is a bit expensive, but's only done once per
				// scanline
				chrPtr = lumEnd + ((lumPtr >> 1) / width) * width;
				lineEnd += width;
			}

			// read the luminance and chromiance values
			final int Y1 = yuvs[lumPtr++] & 0xff;
			final int Y2 = yuvs[lumPtr++] & 0xff;
			final int Cr = (yuvs[chrPtr++] & 0xff) - 128;
			final int Cb = (yuvs[chrPtr++] & 0xff) - 128;
			int R, G, B;

			// generate first RGB components
			B = Y1 + ((454 * Cb) >> 8);
			if (B < 0)
				B = 0;
			else if (B > 255)
				B = 255;
			G = Y1 - ((88 * Cb + 183 * Cr) >> 8);
			if (G < 0)
				G = 0;
			else if (G > 255)
				G = 255;
			R = Y1 + ((359 * Cr) >> 8);
			if (R < 0)
				R = 0;
			else if (R > 255)
				R = 255;
			// NOTE: this assume little-endian encoding

			// rgbs[outPtr++] = (byte) (((G & 0x3c) << 3) | (B >> 3));
			// rgbs[outPtr++] = (byte) ((R & 0xf8) | (G >> 5));
			final byte first_low_byte = (byte) (((G & 0x3c) << 3) | (B >> 3));
			final byte first_high_byte = (byte) ((R & 0xf8) | (G >> 5));

			// generate second RGB components
			B = Y2 + ((454 * Cb) >> 8);
			if (B < 0)
				B = 0;
			else if (B > 255)
				B = 255;
			G = Y2 - ((88 * Cb + 183 * Cr) >> 8);
			if (G < 0)
				G = 0;
			else if (G > 255)
				G = 255;
			R = Y2 + ((359 * Cr) >> 8);
			if (R < 0)
				R = 0;
			else if (R > 255)
				R = 255;
			// NOTE: this assume little-endian encoding
			// rgbs[outPtr++] = (byte) (((G & 0x3c) << 3) | (B >> 3));
			// rgbs[outPtr++] = (byte) ((R & 0xf8) | (G >> 5));
			final byte second_low_byte = (byte) (((G & 0x3c) << 3) | (B >> 3));
			final byte second_high_byte = (byte) ((R & 0xf8) | (G >> 5));

			rgbs[outPtr++] = (first_low_byte & 0xff) << 24
					| (first_high_byte & 0xff) << 16
					| (second_low_byte & 0xff) << 8 | (second_high_byte & 0xff);
		}// while
		assert outPtr == rgbs.length;
	}// toRGB565
}// Yuv420ToRgb565
