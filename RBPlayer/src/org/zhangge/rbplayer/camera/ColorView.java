package org.zhangge.rbplayer.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ColorView extends ImageView {

	private Paint gPaint = null;
	private Bitmap gBitmap1 = null;
	private Bitmap gBitmap2 = null;
	private ColorMatrix gRedColorMatrix = null;
	private ColorMatrix gBlueGreenMatrix = null;
	private float[] gRedColor = {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0};
	private float[] gBlueGreenColor = {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0.5f, 0};
	private ColorMatrixColorFilter gRedFilter;
	private ColorMatrixColorFilter gBlueGreenFilter;
	private int gOffset;

	public ColorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public ColorView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public ColorView(Context context) {
		super(context);
		init();
	}

	private void init() {
		gPaint = new Paint();
		
		gRedColorMatrix = new ColorMatrix();
		gRedColorMatrix.set(gRedColor);
		gRedFilter = new ColorMatrixColorFilter(gRedColorMatrix);
		
		gBlueGreenMatrix = new ColorMatrix();
		gBlueGreenMatrix.set(gBlueGreenColor);
		gBlueGreenFilter = new ColorMatrixColorFilter(gBlueGreenMatrix);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		gPaint.setColorFilter(gRedFilter);
		canvas.drawBitmap(gBitmap1, 0, 0, gPaint);
		
		gPaint.setColorFilter(gBlueGreenFilter);
		canvas.drawBitmap(gBitmap2, gOffset, 0, gPaint);
		
	}

	public void setColorArray(float[] colorArray) {
		this.gRedColor = colorArray;
	}
	
	public void setOffset(int offset) {
		gOffset = offset;
		invalidate();
	}

	public void setBitmap(Bitmap bitmap1, Bitmap bitmap2) {
		this.gBitmap1 = bitmap1;
		this.gBitmap2 = bitmap2;
		invalidate();
	}
}