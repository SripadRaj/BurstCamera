package com.myapp.burst;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


class Preview extends SurfaceView implements SurfaceHolder.Callback {
	private static final String TAG = "Preview";

    SurfaceHolder mHolder;
    public Camera camera;
	ImagePicker imagePicker;
	private Camera.Parameters mParameters;
	private Camera.Size mPreviewSize;
	private Bitmap mBitmap;
	private Context mContext;
	private Camera.CameraInfo cameraInfo;


	Preview(Context context) {
        super(context);

		mContext =context;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        camera = openFrontFacingCamera();
        camera.setDisplayOrientation(90);

		cameraInfo = new Camera.CameraInfo();
		Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK,cameraInfo);

		mParameters = camera.getParameters();
		mParameters.setRotation(90);

		List<Camera.Size> cameraSize = mParameters.getSupportedPreviewSizes();
		mPreviewSize = cameraSize.get(0);

		for (Camera.Size s : cameraSize) {
			if ((s.width * s.height) > (mPreviewSize.width * mPreviewSize.height)) {
				mPreviewSize = s;
			}
		}

		camera.setParameters(mParameters);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
		if(camera == null){
			camera = openFrontFacingCamera();
			camera.setDisplayOrientation(90);

			mParameters = camera.getParameters();

			List<Camera.Size> cameraSize = mParameters.getSupportedPreviewSizes();
			mPreviewSize = cameraSize.get(0);
			mParameters.setRotation(90);

			for (Camera.Size s : cameraSize) {
				if ((s.width * s.height) > (mPreviewSize.width * mPreviewSize.height)) {
					mPreviewSize = s;
				}
			}

			mParameters.setPreviewSize(mPreviewSize.width,mPreviewSize.height);
			camera.setParameters(mParameters);
		}

        try {
			camera.setPreviewDisplay(holder);

			camera.setPreviewCallback(new PreviewCallback() {

				public void onPreviewFrame(byte[] data, Camera arg1) {
					/*FileOutputStream outStream = null;
					try {
						File f = imagePicker.createFile(null,null,null);
						outStream = new FileOutputStream(f);
						outStream.write(data);
						outStream.close();
						Log.d(TAG, "onPreviewFrame - wrote bytes: " + data.length);
					} catch (FileNotFoundException e) {
//						e.printStackTrace();
					} catch (IOException e) {
//						e.printStackTrace();
					} finally {
					}*/

//					CameraDemo.bitmaps.add(getBitmap(data));
						Preview.this.invalidate();

				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        // Because the CameraDevice object is not a shared resource, it's very
        // important to release it when the activity is paused.
        camera.stopPreview();
        camera = null;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        camera.startPreview();
    }

    private Camera openFrontFacingCamera() {
		int cameraCount = 0;
		Camera cam = null;
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		cameraCount = Camera.getNumberOfCameras();
		for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
			Camera.getCameraInfo(camIdx, cameraInfo);
			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
				try {
					cam = Camera.open(camIdx);
				} catch (RuntimeException e) {
					Log.e(TAG,
							"Camera failed to open: " + e.getLocalizedMessage());
				}
			}
		}

		return cam;
	}


    @Override
    public void draw(Canvas canvas) {
    		super.draw(canvas);
    		Paint p= new Paint(Color.RED);
    		Log.d(TAG,"draw");
    		canvas.drawText("PREVIEW", canvas.getWidth()/2, canvas.getHeight()/2, p );
    }


	public Camera.Parameters getmParameters() {
		return mParameters;
	}

	public Camera.Size getmPreviewSize() {
		return mPreviewSize;
	}


	public int getCorrectCameraOrientation(Camera.CameraInfo info, Camera camera) {

		int rotation = ((Activity)mContext).getWindowManager().getDefaultDisplay().getRotation();
		int degrees = 0;

		switch(rotation){
			case Surface.ROTATION_0:
				degrees = 0;
				break;

			case Surface.ROTATION_90:
				degrees = 90;
				break;

			case Surface.ROTATION_180:
				degrees = 180;
				break;

			case Surface.ROTATION_270:
				degrees = 270;
				break;

		}

		int result;
		if(info.facing==Camera.CameraInfo.CAMERA_FACING_FRONT){
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360;
		}else{
			result = (info.orientation - degrees + 360) % 360;
		}

		return result;
	}
}