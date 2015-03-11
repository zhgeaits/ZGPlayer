package org.zhangge.rbplayer.camera;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zhangge.almightyzgbox_android.log.ZGLog;
import org.zhangge.almightyzgbox_android.utils.CommonUtils;
import org.zhangge.rbplayerpro.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint({ "SimpleDateFormat", "InlinedApi" })
public class CameraActivity extends Activity {

	public static String KEY_PIC_PATH = "key_pic_path";
	private Preview preview;
	private TextView text;
	public static Bitmap firstCameraBitmap = null;
	public static Bitmap secondCameraBitmap = null;
	private String picturePath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		if(getIntent().getExtras() != null) {
			picturePath = getIntent().getExtras().getString(KEY_PIC_PATH);
		}
		preview = new Preview(this);
		setContentView(preview);
		text = new TextView(this);
		CommonUtils.hideSystemUI(preview);
		Toast.makeText(this, getString(R.string.camera_one), Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			preview.takePicture();
		}
		return super.onTouchEvent(event);
	}

	private class Preview extends SurfaceView implements SurfaceHolder.Callback {

		private SurfaceHolder holder;
		private Camera camera;
		private int[] wh;
		private int rightOffset;
		private List<Bitmap> willRecycle = new ArrayList<Bitmap>();
		
		private PictureCallback pictureCallback = new PictureCallback() {

			private Bitmap fixBitmap(Bitmap src) {
				willRecycle.add(src);
				wh = CommonUtils.getDisplayPixelsZG(CameraActivity.this);
				int dstWidth, dstHeight;
				if (src.getWidth() >= wh[0]) {
					dstWidth = wh[0];
				} else {
					dstWidth = src.getWidth();
				}
				dstHeight = dstWidth * src.getHeight() / src.getWidth();
				Bitmap dst = null;
				if (dstWidth == src.getWidth() && dstHeight == src.getHeight()) {
					dst = src;
				} else {
					dst = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
					willRecycle.add(dst);
				}

				int startX = 0, startY = 0;
				int cutWidth, cutHeight;
				if (dstWidth > wh[0]) {
					startX = (dst.getWidth() - wh[0]) / 2;
					cutWidth = wh[0];
				} else {
					cutWidth = dstWidth;
				}
				if (dstHeight > wh[1]) {
					startY = (dst.getHeight() - wh[1]) / 2;
					cutHeight = wh[1];
				} else {
					cutHeight = dstHeight;
				}

				Bitmap b0 = null;
				if (startX == 0 && startY == 0 && cutWidth == dstWidth && cutHeight == dstHeight) {
					b0 = dst;
				} else {
					b0 = Bitmap.createBitmap(dst, startX, startY, cutWidth, cutHeight);
					willRecycle.add(b0);
				}

				return b0;
			}

			@SuppressWarnings("deprecation")
			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				wh = CommonUtils.getDisplayPixelsZG(CameraActivity.this);
				Options origOpts = CommonUtils.getBitmapOptions(null, data);
				BitmapFactory.Options opts = new BitmapFactory.Options();
				opts.inPreferredConfig = Bitmap.Config.RGB_565;
				opts.inJustDecodeBounds = false;
				opts.inPurgeable = true;
				opts.inJustDecodeBounds = false;
				opts.inSampleSize = CommonUtils.calculateInSampleSize(origOpts, wh[0], wh[1]);
				if (firstCameraBitmap == null) {
					firstCameraBitmap = this.fixBitmap(BitmapFactory.decodeByteArray(data, 0, data.length, opts));
					willRecycle.add(firstCameraBitmap);

					rightOffset = 40;

					Bitmap toShowBitmap = ImageHandler.rightMove(firstCameraBitmap, rightOffset);
					willRecycle.add(toShowBitmap);

					Drawable draw = new BitmapDrawable(toShowBitmap);
					// ByteArrayInputStream bais = new
					// ByteArrayInputStream(data);
					// Drawable d = BitmapDrawable.createFromStream(bais,
					// firstPicture);
					Preview.this.setBackgroundDrawable(draw);
					Preview.this.getBackground().setAlpha(100);
					camera.startPreview();

					LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
					text.setVisibility(VISIBLE);
					text.setGravity(Gravity.CENTER);
					text.setTextSize((int) ((30 / (double) 1920) * CommonUtils.getDisplayPixelsZG(CameraActivity.this)[0]));
					text.setTextColor(Color.RED);
					text.setText(getString(R.string.camera_two));
					CameraActivity.this.addContentView(text, layoutParams);
				} else {
					secondCameraBitmap = this.fixBitmap(BitmapFactory.decodeByteArray(data, 0, data.length, opts));
					SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_hhmmss");
					String pictureName = format.format(new Date());
					// 保存两张图片,拍的第一张图片是右眼的，拍第二张图片需要左移动，是左眼的
					File file1 = new File(picturePath + "/RBPlayer_" + pictureName + "-2.jpg");
					File file2 = new File(picturePath + "/RBPlayer_" + pictureName + ".jpg");
					try {
						BufferedOutputStream bos1 = new BufferedOutputStream(new FileOutputStream(file1));
						BufferedOutputStream bos2 = new BufferedOutputStream(new FileOutputStream(file2));
						firstCameraBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos1);
						secondCameraBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos2);
						bos1.flush();
						bos1.close();
						bos2.flush();
						bos2.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

					camera.stopPreview();
					camera = null;
					firstCameraBitmap = null;
					secondCameraBitmap = null;
					finish();
					Preview.this.setBackgroundDrawable(null);
					CameraActivity.this.setContentView(preview);
					CommonUtils.recycleBitmap(willRecycle);
					// PicActivity.startMe(CameraActivity.this, 0,
					// file2.getAbsolutePath(), null);
				}
			}
		};

		public Preview(Context context) {
			super(context);
			holder = getHolder();
			holder.addCallback(this);
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			if (width < height) {// 修正横屏拍照
				width += height;
				height = width - height;
				width = width - height;
			}
			Camera.Parameters parameters = camera.getParameters();// 获得相机参数
			Size pictureSize = findBestResolution(width, height, parameters.getSupportedPictureSizes(), 0);
			Size previewSize = findBestResolution(width, height, parameters.getSupportedPreviewSizes(), pictureSize.width
					/ (double) pictureSize.height);
			this.setLayoutParams(new FrameLayout.LayoutParams(previewSize.width, previewSize.height));
			parameters.setPreviewSize(previewSize.width, previewSize.height); // 设置预览图像大小
			parameters.setPictureSize(pictureSize.width, pictureSize.height); // 设置拍照后的照片大小
			parameters.setPictureFormat(ImageFormat.JPEG); // 设置照片格式
			String focusMode = parameters.getFocusMode();
			try {
				parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 连续对焦
				camera.setParameters(parameters);// 设置相机参数
			} catch (Exception e) {
				ZGLog.error(this, "奇葩手机不支持连续对焦");
				parameters.setFocusMode(focusMode);
				camera.setParameters(parameters);// 设置相机参数
			}
			camera.autoFocus(new AutoFocusCallback() {
				@Override
				public void onAutoFocus(boolean success, Camera camera) {
					if (success) {
						camera.cancelAutoFocus();// 只有加上了这一句，才会自动对焦。
					}
				}
			});
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			camera = Camera.open();
			try {
				camera.setPreviewDisplay(holder);
				camera.startPreview();
			} catch (IOException e) {
				camera.release();
				camera = null;
			}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			camera.release();
			camera = null;
		}

		private Size findBestResolution(int pWidth, int pHeight, List<Size> lSizes, double rate) {
			Size lSelectedSize = camera.new Size(0, 0);
			for (Size lSize : lSizes) {
				double lRate = lSize.width / (double) lSize.height;
				if (rate == 0 || (lRate - rate) == 0) {
					if ((Math.abs(pWidth - lSize.width) < Math.abs(pWidth - lSelectedSize.width))) {
						lSelectedSize = lSize;
					}
				}
			}
			return lSelectedSize;
		}

		public void takePicture() {
			if (camera != null) {
				camera.takePicture(null, null, pictureCallback);
			}
		}
	}
}
