package info.androidhive.androidvideostreaming;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import net.majorkernelpanic.streaming.Session;
import net.majorkernelpanic.streaming.SessionBuilder;
import net.majorkernelpanic.streaming.audio.AudioQuality;
import net.majorkernelpanic.streaming.gl.SurfaceView;
import net.majorkernelpanic.streaming.rtsp.RtspClient;
import net.majorkernelpanic.streaming.video.VideoQuality;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends Activity implements RtspClient.Callback,
		Session.Callback, SurfaceHolder.Callback
{
	// log tag
	public final static String TAG = MainActivity.class.getSimpleName();

	// surfaceview
	private static SurfaceView mSurfaceView;
	private static RtspClient mClient;
	// Rtsp session
	private Session mSession;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		requestPermissions(new String[]{Manifest.permission.CAMERA,
				Manifest.permission.WRITE_EXTERNAL_STORAGE,
				Manifest.permission.RECORD_AUDIO,
				Manifest.permission.INTERNET
		}, 1);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		// getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_main);

		mSurfaceView = findViewById(R.id.surface);

		mSurfaceView.getHolder().addCallback(this);

		// Initialize RTSP client
		initRtspClient();

	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String permissions[], int[] grantResults) {
		switch (requestCode) {
			case 1: {

				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {


				} else {

					// permission denied, boo! Disable the
					// functionality that depends on this permission.
					Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
				}
				return;
			}

			// other 'case' lines to check for other
			// permissions this app might request
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		toggleStreaming();
	}

	@Override
	protected void onPause()
	{
		super.onPause();

		toggleStreaming();
	}

	private void initRtspClient()
	{
		// Configures the SessionBuilder
		mSession = SessionBuilder.getInstance()
				.setContext(getApplicationContext())
				.setAudioEncoder(SessionBuilder.AUDIO_NONE)
				//.setAudioQuality(new AudioQuality(8000, 16000))
				.setVideoEncoder(SessionBuilder.VIDEO_H264)
				.setSurfaceView(mSurfaceView).setPreviewOrientation(0)
				.setCallback(this).build();

		// Configures the RTSP client
		mClient = new RtspClient();
		mClient.setSession(mSession);
		mClient.setCallback(this);
		mSurfaceView.setAspectRatioMode(SurfaceView.ASPECT_RATIO_PREVIEW);
		String ip, port, path;

		// We parse the URI written in the Editext
		Pattern uri = Pattern.compile("rtsp://(.+):(\\d+)/(.+)");
		Matcher m = uri.matcher(AppConfig.STREAM_URL);
		m.find();
		ip = m.group(1);
		port = m.group(2);
		path = m.group(3);

		/*mClient.setCredentials(AppConfig.PUBLISHER_USERNAME,
				AppConfig.PUBLISHER_PASSWORD);*/
		mClient.setServerAddress(ip, Integer.parseInt(port));
		mClient.setStreamPath("/" + path);
	}

	private void toggleStreaming()
	{
		if (!mClient.isStreaming())
		{
			// Start camera preview
			mSession.startPreview();

			// Start video stream
			mClient.startStream();
		}
		else
		{
			// already streaming, stop streaming
			// stop camera preview
			mSession.stopPreview();

			// stop streaming
			mClient.stopStream();
		}
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		mClient.release();
		mSession.release();
		mSurfaceView.getHolder().removeCallback(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onSessionError(int reason, int streamType, Exception e)
	{
		switch (reason)
		{
			case Session.ERROR_CAMERA_ALREADY_IN_USE:
				break;
			case Session.ERROR_CAMERA_HAS_NO_FLASH:
				break;
			case Session.ERROR_INVALID_SURFACE:
				break;
			case Session.ERROR_STORAGE_NOT_READY:
				break;
			case Session.ERROR_CONFIGURATION_NOT_SUPPORTED:
				break;
			case Session.ERROR_OTHER:
				break;
		}

		if (e != null)
		{
			alertError(e.getMessage());
			e.printStackTrace();
		}
	}

	private void alertError(final String msg)
	{
		final String error = (msg == null) ? "Unknown error: " : msg;
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setMessage(error).setPositiveButton("Ok",
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
					}
				});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	@Override
	public void onRtspUpdate(int message, Exception exception)
	{
		switch (message)
		{
			case RtspClient.ERROR_CONNECTION_FAILED:
			case RtspClient.ERROR_WRONG_CREDENTIALS:
				alertError(exception.getMessage());
				exception.printStackTrace();
				break;
		}
	}

	@Override
	public void onPreviewStarted()
	{
	}

	@Override
	public void onSessionConfigured()
	{
	}

	@Override
	public void onSessionStarted()
	{
	}

	@Override
	public void onSessionStopped()
	{
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3)
	{
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
	}

	@Override
	public void onBitrareUpdate(long bitrate)
	{
	}

}
