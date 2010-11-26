package org.andnav2.ui.camera;

import java.lang.reflect.Method;

import org.andnav2.R;
import org.andnav2.ui.common.CommonDialogFactory;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

public class CameraFavorite extends Activity{

	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	Camera camera;
	Button buttonClick;
    SurfaceHolder previewHolder;
    String name;

	// ===========================================================
	// Constructors
	// ===========================================================

	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.camerafavorite);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        name = this.getIntent().getStringExtra(CommonDialogFactory.class.getName());

        SurfaceView preview=(SurfaceView)findViewById(R.id.cameraPreview);
        previewHolder=preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		buttonClick = (Button) findViewById(R.id.cameraButtonClick);
		buttonClick.setOnClickListener( new OnClickListener() {
			public void onClick(View v) {
				camera.takePicture(shutterCallback, rawCallback, jpegCallback);
			}
		});
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

    SurfaceHolder.Callback surfaceCallback=new SurfaceHolder.Callback() {
            public void surfaceCreated(SurfaceHolder holder) {
                camera=Camera.open();

                try {
                    camera.setPreviewDisplay(previewHolder);
                }
                catch (Throwable t) {
                    Toast
                        .makeText(CameraFavorite.this, t.getMessage(), Toast.LENGTH_LONG)
                        .show();
                }
            }

            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Camera.Parameters parameters=camera.getParameters();

                parameters.setPreviewSize(width, height);
                parameters.setPictureFormat(PixelFormat.JPEG);
                try {
                    String flash = (String) Camera.Parameters.class.getField("FLASH_MODE_AUTO").get(parameters);
                    String focus = (String) Camera.Parameters.class.getField("FOCUS_MODE_AUTO").get(parameters);
                    Method setFlash = Camera.Parameters.class.getMethod("setFlashMode", new Class[] { String.class });
                    Method setFocus = Camera.Parameters.class.getMethod("setFocusMode", new Class[] { String.class });
                } catch (final Exception e) {}
                camera.setParameters(parameters);
                camera.startPreview();
            }

            public void surfaceDestroyed(SurfaceHolder holder) {
                camera.stopPreview();
                camera.release();
                camera=null;
            }
        };

	// ===========================================================
	// Methods
	// ===========================================================

	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
		}
	};

	/** Handles data for raw picture */
	PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
		}
	};

	/** Handles data for jpeg picture */
	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
            Intent d = new Intent();
            d.putExtra(CommonDialogFactory.class.getName(), name);
            d.putExtra(CameraFavorite.class.getName(), data);
            CameraFavorite.this.setResult(Activity.RESULT_OK, d);
            CameraFavorite.this.finish();
		}
	};

}
