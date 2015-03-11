package org.zhangge.rbplayer.camera;

import android.graphics.Bitmap;
import android.graphics.Color;

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
		for (int i = 0; i < lpHeight; i++) {
			base = i * lpWidth;
			if(offset == 0) {
				for (int j = 0; j < lpWidth; j++) {
					dstPicBuf[base + j] = combineARGB(srcPicBufleft[base + j], srcPicBufright[base + j]);
				}
			}
			if(offset < 0) {
				for (int j = 0; j < lpWidth + offset; j++) {
					dstPicBuf[base + j] = combineARGB(srcPicBufleft[base + j], srcPicBufright[base + j - offset]);
				}
				for (int j = lpWidth + offset; j < lpWidth; j++) {
					dstPicBuf[base + j] = srcPicBufleft[base + j];
				}
			}
			if(offset > 0) {
				for (int j = 0; j < offset; j++) {
					dstPicBuf[base + j] = srcPicBufleft[base + j];
				}
				for (int j = offset; j < lpWidth; j++) {
					dstPicBuf[base + j] = combineARGB(srcPicBufleft[base + j], srcPicBufright[base + j - offset]);
				}
			}
		}
		
		dstPic.setPixels(dstPicBuf, 0, lpWidth, 0, 0, rpWidth, lpHeight);
		return dstPic;
	}
	
	private static int combineARGB(int pixel1, int pixel2) {
		int a1 = Color.alpha(pixel1);
		int r1 = Color.red(pixel1);  
		int a2 = Color.alpha(pixel2);
        int g2 = Color.green(pixel2);  
        int b2 = Color.blue(pixel2);
        
        int avgA = (a1>>1) + (a2>>1);
        
        int result = Color.argb(avgA, r1, g2, b2);
		return result;
	}
	
}
