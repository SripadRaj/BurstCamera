package com.myapp.burst;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Deflater;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class CameraDemo extends Activity {
	private static final String TAG = "FrontCamera";
	Camera camera;
	Preview preview;
	ImageView buttonClick, doneButton;
	int stillCount = 0;
	ImagePicker imagePicker;
	TextView counter;
	ArrayList<Uri> uris;

	private Camera.Parameters mParameters;
	private Camera.Size mPreviewSize;
	private Bitmap mBitmap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		uris = new ArrayList<>();

		preview = new Preview(this);
		imagePicker = ImagePicker.getInstance(getApplicationContext());
		((FrameLayout) findViewById(R.id.preview)).addView(preview);

		buttonClick = (ImageView) findViewById(R.id.buttonClick);
		buttonClick.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				preview.camera.takePicture(shutterCallback, rawCallback,
						jpegCallback);
			}
		});

		doneButton = (ImageView) findViewById(R.id.done);
		counter = (TextView) findViewById(R.id.counter);

		counter.setText("" + stillCount);

		mPreviewSize = preview.getmPreviewSize();
		mParameters = preview.getmParameters();

		doneButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {


				Bundle bundle = new Bundle();
//				bundle.putParcelableArrayList("images",bitmaps);

				Intent intent = new Intent();
				intent.putExtras(bundle);
				setResult(RESULT_OK, intent);
				finish();

			}
		});

		Log.d(TAG, "onCreate'd");
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (camera != null) {
			camera.release();
		}
	}

	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			Log.d(TAG, "onShutter'd");
		}
	};

	PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.d(TAG, "onPictureTaken - raw with data = " + ((data != null) ? data.length : " NULL"));
		}
	};

	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			FileOutputStream outStream = null;
			try {
				// write to local sandbox file system
				// outStream =
				// CameraDemo.this.openFileOutput(String.format("%d.jpg",
				// System.currentTimeMillis()), 0);
				// Or write to sdcard

//				Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);

				File f = imagePicker.createFile(null,null,null);
				outStream = new FileOutputStream(f);
//				bmp.compress(Bitmap.CompressFormat.JPEG,30,outStream);
				outStream.write(data);
				outStream.close();

				Bitmap cBmp = imagePicker.processCameraImageWithCompression(CameraDemo.this,f,30);
				File f2 = imagePicker.createFile(null,null,null);
				outStream = new FileOutputStream(f2);
				cBmp.compress(Bitmap.CompressFormat.JPEG,80,outStream);
				outStream.flush();
				outStream.close();
				f.delete();


                uris.add(Uri.parse(f2.getAbsolutePath()));
				GridViewActivity.filesList.add(f2);
				GridViewActivity.byteArrayList.add(Uri.fromFile(f2));

				/*ByteArrayOutputStream blob = new ByteArrayOutputStream();
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inMutable = true;
				Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length, options);
				bmp.compress(Bitmap.CompressFormat.JPEG,50,blob);*/


				/*if (data != null) {
//					byte[] b = CompressionUtils.compress(data);

					File outputDir = getApplicationContext().getCacheDir(); // context being the Activity pointer
					File outputFile = File.createTempFile("temp", ".jpeg", outputDir);

					outStream = new FileOutputStream(outputFile);
					outStream.write(data);
					outStream.close();

					ExifInterface exif;
					try {
						exif = new ExifInterface(outputFile.getAbsolutePath());
						int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

						int rotationInDegrees = exifToDegrees(rotation);

						Matrix matrix = new Matrix();
						if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}

						Bitmap adjustedBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);


						ByteArrayOutputStream blob2 = new ByteArrayOutputStream();
						BitmapFactory.Options options2 = new BitmapFactory.Options();
						options2.inMutable = true;
						adjustedBitmap.compress(Bitmap.CompressFormat.JPEG,100,blob2);
						data = blob.toByteArray();

						GridViewActivity.byteArrayList.add(data);

					} catch (IOException e) {
						e.printStackTrace();
					}

				} else {
					Log.e("DATA", "NULL");
				}*/

				Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {

			}
			Log.d(TAG, "onPictureTaken - jpeg");

			try {
				stillCount++;
				counter.setText("" + stillCount);
				camera.startPreview();
				/*preview.camera.takePicture(shutterCallback, rawCallback,
						jpegCallback);*/
				/*if (stillCount < 10) {
					preview.camera.takePicture(shutterCallback, rawCallback,
							jpegCallback);
				} else {
					stillCount = 0;
					buttonClick.setEnabled(true);
				}*/
			} catch (Exception e) {
				Log.d(TAG, "Error starting preview: " + e.toString());
			}
		}
	};


	public int exifToDegrees(int exifOrientation) {
		if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
		else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
		else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
		return 0;
	}
}

