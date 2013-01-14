package info.justoneplanet.android.camera;

/**
 * ユーティリティ
 * 
 * @author justoneplanet This code comes from
 *         http://blog.justoneplanet.info/2009
 *         /11/03/%E3%82%82%E3%81%A3%E3%81%A8android
 *         %E3%81%A7%E3%82%AB%E3%83%A1%E3%83%A9%E3%82%92%E4%BD%BF%E3%81%86/ .
 *         Many thanks to the author(s).
 */
public class Util {
	/**
	 * YUV形式から色の配列に変換する
	 * 
	 * @param data
	 * @param width
	 * @param height
	 * @return
	 * @throws NullPointerException
	 * @throws IllegalArgumentException
	 */
	public static int[] decodeYUV(byte[] data, int width, int height)
			throws NullPointerException, IllegalArgumentException {
		int size = width * height;
		if (data == null) {
			throw new NullPointerException("buffer data is null");
		}
		if (data.length < size) {
			throw new IllegalArgumentException("buffer data is illegal");
		}

		int[] out = new int[size];

		int Y, Cr = 0, Cb = 0;
		for (int i = 0; i < height; i++) {

			int index = i * width;
			int jDiv2 = i >> 1;

			for (int i2 = 0; i2 < width; i2++) {
				Y = data[index];
				if (Y < 0) {
					Y += 255;
				}
				if ((i2 & 0x1) != 1) {
					int c0ff = size + jDiv2 * width + (i2 >> 1) * 2;
					Cb = data[c0ff];
					if (Cb < 0) {
						Cb += 127;
					} else {
						Cb -= 128;
					}
					Cr = data[c0ff + 1];
					if (Cr < 0) {
						Cr += 127;
					} else {
						Cr -= 128;
					}
				}
				// red
				int R = Y + Cr + (Cr >> 2) + (Cr >> 3) + (Cr >> 5);
				if (R < 0) {
					R = 0;
				} else if (R > 255) {
					R = 255;
				}

				// green
				int G = Y - (Cb >> 2) + (Cb >> 4) + (Cb >> 5) - (Cr >> 1)
						+ (Cr >> 3) + (Cr >> 4) + (Cr >> 5);
				if (G < 0) {
					G = 0;
				} else if (G > 255) {
					G = 255;
				}

				int B = Y + Cb + (Cb >> 1) + (Cb >> 2) + (Cb >> 6);
				if (B < 0) {
					B = 0;
				} else if (B > 255) {
					B = 255;
				}
				out[index] = 0xff000000 + (B << 16) + (G << 8) + R;
				index++;
			}
		}
		return out;
	}
}