// Copyright © 2016 Shawn Baker using the MIT License.
package ca.frozen.rpicameraviewer.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import ca.frozen.rpicameraviewer.classes.Camera;
import ca.frozen.rpicameraviewer.classes.Utils;
import ca.frozen.rpicameraviewer.R;

public class VideoActivity extends AppCompatActivity implements VideoFragment.OnFadeListener
{
	// public constants
	public final static String CAMERA_MONO = "CAMERA_MONO";
	public final static String CAMERA_LEFT = "CAMERA_LEFT";
	public final static String CAMERA_RIGHT = "CAMERA_RIGHT";

	public final static String CAMERA_TYPE = "CAMERA_TYPE";
	public final static String MONO = "MONO";
	public final static String STEREO = "STEREO";

	// local constants
	private final static String TAG = "VideoActivity";

	// instance variables
	private Camera cameraMono;
	private Camera cameraLeft;
	private Camera cameraRight;

	private FrameLayout monoFrameLayout;
	private VideoFragment monoVideoFragment;

	private FrameLayout leftFrameLayout;
	private VideoFragment leftVideoFragment;

	private FrameLayout rightFrameLayout;
	private VideoFragment rightVideoFragment;

	private LinearLayout videoWrapper;

	//******************************************************************************
	// onCreate
	//******************************************************************************
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// configure the activity
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video);
		//Log.d(TAG, "onCreate");

		// load the settings and cameras
		Utils.loadData();

		videoWrapper = (LinearLayout) findViewById(R.id.video_wrapper);

		// set full screen layout
		int visibility = videoWrapper.getSystemUiVisibility();
		visibility |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;


		// get the camera object
		Bundle data = getIntent().getExtras();
		switch (data.getString(CAMERA_TYPE)){
			case MONO:
				cameraMono = data.getParcelable(CAMERA_MONO);
				// get the frame layout, handle system visibility changes
				monoFrameLayout = (FrameLayout) findViewById(R.id.video);
				videoWrapper.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
				{
					@Override
					public void onSystemUiVisibilityChange(int visibility)
					{
						if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
						{
							monoVideoFragment.startFadeIn();
						}
					}
				});

				videoWrapper.setSystemUiVisibility(visibility);

				// create the video fragment
				monoVideoFragment = monoVideoFragment.newInstance(cameraMono, true);
				FragmentTransaction fragTran = getSupportFragmentManager().beginTransaction();
				fragTran.add(R.id.video, monoVideoFragment);
				fragTran.commit();
				break;
			case STEREO:
				cameraLeft = data.getParcelable(CAMERA_LEFT);
				// get the frame layout, handle system visibility changes
				leftFrameLayout = (FrameLayout) findViewById(R.id.video_left);
				videoWrapper.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
				{
					@Override
					public void onSystemUiVisibilityChange(int visibility)
					{
						if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
						{
							leftVideoFragment.startFadeIn();
						}
					}
				});

				cameraRight = data.getParcelable(CAMERA_RIGHT);
				// get the frame layout, handle system visibility changes
				rightFrameLayout = (FrameLayout) findViewById(R.id.video_right);
				videoWrapper.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
				{
					@Override
					public void onSystemUiVisibilityChange(int visibility)
					{
						if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
						{
							leftVideoFragment.startFadeIn();
							rightVideoFragment.startFadeIn();
						}
					}
				});

				videoWrapper.setSystemUiVisibility(visibility);

				// create the video fragment
				leftVideoFragment = VideoFragment.newInstance(cameraLeft, true);
				rightVideoFragment = VideoFragment.newInstance(cameraRight, true);

				FragmentTransaction fragTranLeft = getSupportFragmentManager().beginTransaction();
				fragTranLeft.add(R.id.video_left, leftVideoFragment);
				fragTranLeft.commit();

				FragmentTransaction fragTranRight = getSupportFragmentManager().beginTransaction();
				fragTranRight.add(R.id.video_right, rightVideoFragment);
				fragTranRight.commit();

				break;
		}
	}

	//******************************************************************************
	// onStartFadeIn
	//******************************************************************************
	@Override
	public void onStartFadeIn()
	{
	}

	//******************************************************************************
	// onStartFadeOut
	//******************************************************************************
	@Override
	public void onStartFadeOut()
	{
		// hide the status and navigation bars
		int leftVisibility = leftFrameLayout.getSystemUiVisibility();
		leftVisibility |= View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
		leftFrameLayout.setSystemUiVisibility(leftVisibility);
	}

	//******************************************************************************
	// onRequestPermissionsResult
	//******************************************************************************
	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
	{
		leftVideoFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}
}
