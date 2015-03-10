package org.zhangge.rbplayer.camera;

import android.graphics.Bitmap;

public class ImageHandler {
	
	public static Bitmap rightMove(Bitmap src, int offset) {
		int srcWidth = src.getWidth();
		int srcHeight = src.getHeight();
		int[] srcPixels0 = new int[(srcWidth - offset) * srcHeight];
		int[] srcPixels1 = new int[offset * srcHeight];
		src.getPixels(srcPixels0, 0, srcWidth - offset, 0, 0, srcWidth - offset, srcHeight);
		src.getPixels(srcPixels1, 0, offset, 0, 0, offset, srcHeight);
		
		for (int i = 0; i < srcPixels1.length; i++) {
			srcPixels1[i] = 0;
		}
		Bitmap dst = Bitmap.createBitmap(srcWidth, srcHeight, src.getConfig());
		dst.setPixels(srcPixels1, 0, offset, 0, 0, offset, srcHeight);
		dst.setPixels(srcPixels0, 0, srcWidth - offset, offset, 0, srcWidth - offset, srcHeight);
		return dst;
	}
	
	public static Bitmap commonCombinePicture(Bitmap srcPic, int offset) {

		int width = srcPic.getWidth();
		int height = srcPic.getHeight();
		int[] srcPixels = new int[width * height];

		srcPic.getPixels(srcPixels, 0, width, 0, 0, width, height);

		int[] dstPixels = new int[width * height];
		Bitmap dstPic = Bitmap.createBitmap(width, height, srcPic.getConfig());
		int x, y;
		int lineSize = width;
		int rpOffset = offset >= 0 ? 1 : ((-offset) * 2 + 1);
		int rpPlusIndex = offset * 2;
		int lp, rp, row;
		for (y = 0; y < height; y++) {
			lp = 0;
			rp = rpOffset;
			row = y * lineSize;
			for (x = 0; x < width; x++) {
				if ((x % 2) == 0) {
					dstPixels[row + x] = srcPixels[row + lp];// srcPixels[row+lbit];
					lp += 2;
				} else {
					if (rp > width - 1)
						rp = width - 1;
					dstPixels[row + x] = srcPixels[row + rp];// srcPixels[row+lbit];
					if (x > rpPlusIndex)
						rp += 2;
				}
			}
		}
		dstPic.setPixels(dstPixels, 0, width, 0, 0, width, height);
		return dstPic;
	}

	public static Bitmap leftRightCombineTwoPicture(Bitmap leftPic,
			Bitmap rightPic, int offset) {
		Bitmap dstPic = null;
		int lpWidth = leftPic.getWidth();
		int lpHeight = leftPic.getHeight();
		int rpWidth = rightPic.getWidth();
		int rpHeight = rightPic.getHeight();

		if (lpWidth != rpWidth || lpHeight != rpHeight) {
			return null;
		}
		dstPic = Bitmap.createBitmap(lpWidth, lpHeight, leftPic.getConfig());
		int totalSize = lpWidth * lpHeight;
		int[] srcPicBufleft = new int[totalSize];
		int[] srcPicBufright = new int[totalSize];
		int[] dstPicBuf = new int[totalSize];
		leftPic.getPixels(srcPicBufleft, 0, lpWidth, 0, 0, lpWidth, lpHeight);
		rightPic.getPixels(srcPicBufright, 0, lpWidth, 0, 0, lpWidth, lpHeight);
		int base;
		int rightOffset = 1;
		int rightPlusIndex = 1;
		if (offset > 0) {
			rightOffset = 1;
			rightPlusIndex = offset * 2;
		} else {
			rightOffset = (-offset) * 2 + 1;
		}

		for (int i = 0; i < lpHeight; i++) {
			base = i * lpWidth;
			for (int j = 0, m = 0, n = rightOffset; j < lpWidth; j++) {
				if (j % 2 == 0) {
					dstPicBuf[base + j] = srcPicBufleft[base + m];
					m = m + 2;
					if (m > lpWidth - 1)
						m = lpWidth - 1;

				} else {
					dstPicBuf[base + j] = srcPicBufright[base + n];
					if (j > rightPlusIndex) {
						n += 2;
						if (n > lpWidth - 2)
							n = lpWidth - 2;
					}
				}
			}
		}
		dstPic.setPixels(dstPicBuf, 0, lpWidth, 0, 0, rpWidth, lpHeight);
		return dstPic;
	}
	
	public static Bitmap common2CombinePicture(Bitmap srcPic, int offset) {

		int width = srcPic.getWidth();
		int height = srcPic.getHeight();
		int[] srcPixels = new int[width * height];

		srcPic.getPixels(srcPixels, 0, width, 0, 0, width, height);

		int[] dstPixels = new int[width * height];
		Bitmap dstPic = Bitmap.createBitmap(width, height, srcPic.getConfig());
		int x, y;
		int lineSize = width;
		int rpOffset = offset >= 0 ? 2 : ((-offset) * 4 + 2);
		int rpPlusIndex = offset * 4;
		int lp, rp, row;
		for (y = 0; y < height; y++) {
			lp = 0;
			rp = rpOffset;
			row = y * lineSize;
			for (x = 0; x < width; x+=2) {
				if (((x/2) % 2) == 0) {
					dstPixels[row + x] = srcPixels[row + lp];// srcPixels[row+lbit];
					dstPixels[row + x+1] = srcPixels[row + lp+1];// srcPixels[row+lbit];
					lp += 4;
				} else {
					if (rp > width - 2)
						rp = width - 2;
					dstPixels[row + x] = srcPixels[row + rp];// srcPixels[row+lbit];
					dstPixels[row + x+1] = srcPixels[row + rp+1];// srcPixels[row+lbit];
					if (x > rpPlusIndex)
						rp += 4;
				}
			}
		}
		dstPic.setPixels(dstPixels, 0, width, 0, 0, width, height);
		return dstPic;
	}

	public static Bitmap leftRight2CombineTwoPicture(Bitmap leftPic,
			Bitmap rightPic, int offset) {
		Bitmap dstPic = null;
		int lpWidth = leftPic.getWidth();
		int lpHeight = leftPic.getHeight();
		int rpWidth = rightPic.getWidth();
		int rpHeight = rightPic.getHeight();

		if (lpWidth != rpWidth || lpHeight != rpHeight) {
			return null;
		}
		dstPic = Bitmap.createBitmap(lpWidth, lpHeight, leftPic.getConfig());
		int totalSize = lpWidth * lpHeight;
		int[] srcPicBufleft = new int[totalSize];
		int[] srcPicBufright = new int[totalSize];
		int[] dstPicBuf = new int[totalSize];
		leftPic.getPixels(srcPicBufleft, 0, lpWidth, 0, 0, lpWidth, lpHeight);
		rightPic.getPixels(srcPicBufright, 0, lpWidth, 0, 0, lpWidth, lpHeight);
		int base;
		int rightOffset = 2;
		int rightPlusIndex = 1;
		if (offset > 0) {
			rightOffset = 2;
			rightPlusIndex = offset * 4;
		} else {
			rightOffset = (-offset) * 4 + 2;
		}

		for (int i = 0; i < lpHeight; i++) {
			base = i * lpWidth;
			for (int j = 0, m = 0, n = rightOffset; j < lpWidth; j+=2) {
				if ((j/2 % 2) == 0) {
					dstPicBuf[base + j] = srcPicBufleft[base + m];
					dstPicBuf[base + j+1] = srcPicBufleft[base + m+1];
					m += 4;
					if (m > lpWidth - 2)
						m = lpWidth - 2;

				} else {
					dstPicBuf[base + j] = srcPicBufright[base + n];
					dstPicBuf[base + j + 1] = srcPicBufright[base + n +1];
					if (j > rightPlusIndex) {
						n += 4;
						if (n > lpWidth - 2)
							n = lpWidth - 2;
					}
				}
			}
		}
		dstPic.setPixels(dstPicBuf, 0, lpWidth, 0, 0, rpWidth, lpHeight);
		return dstPic;
	}
}
