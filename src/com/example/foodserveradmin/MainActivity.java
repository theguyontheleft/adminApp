package com.example.foodserveradmin;

import java.io.File;
import java.util.List;
import java.util.jar.Attributes.Name;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Miguel Suarez
 * @author Carl Barbee
 * @author James Dagres
 * @author Matt Luckham
 * 
 *         The administrator logins in by taking a picture which is checked to
 *         make sure it is a valid person's face. The admin is directed to the
 *         orders activity which displays all the current orders on the server.
 * 
 */

public class MainActivity extends Activity {

	protected EditText nameEdit;
	protected static String name;
	protected static String IPandPort;
	protected Asyncserver myActivity;
	protected TextView status;
	protected Button takePic;
	private File sdImageMainDirectory;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		takePic = (Button) findViewById(R.id.signIn);
		status = (TextView) findViewById(R.id.connectionStatus);
		nameEdit = (EditText) findViewById(R.id.admin_name);

		// Administer signs in if valid face is detected from picture.
		takeAPicture();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Once picture is taken, admin can try to connect to the server.
	 */
	public void loginToServer() {

		// admin can't log in twice
		takePic.setEnabled(false);
		// get values from Text edit and tries to connect to the
		// server
		myActivity = new Asyncserver(MainActivity.this);
		String stringPort = "8080";
		String stringIP = "54.201.86.103";
	  IPandPort = stringIP + ":" + stringPort;
		// start AsyncTask
		myActivity.execute(IPandPort);
	}

	/**
	 * Starts the camera, admin must take the picture in order to access the
	 * server.
	 */
	public void takeAPicture() {

		takePic.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				File root = new File(Environment.getExternalStorageDirectory()
						+ File.separator + "myDir" + File.separator);
				root.mkdirs();
				sdImageMainDirectory = new File(root, "AdminName");

				// Update administrator's picture.
				if (sdImageMainDirectory.exists()) {
					sdImageMainDirectory.delete();
					sdImageMainDirectory = new File(root, "Admin.png");
				}

				Uri outputFileUri = Uri.fromFile(sdImageMainDirectory);
				Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
				intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
				startActivityForResult(intent, 0);
			}
		});
	}

	/**
	 * Check for a valid admin picture taken then start listening for admin login
	 * button press.
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 0 && resultCode == RESULT_OK) {

			// Check if picture file was created.
			if (sdImageMainDirectory.exists()) {
				BitmapFactory.Options BitmapFactoryOptionsbfo = new BitmapFactory.Options();
				BitmapFactoryOptionsbfo.inPreferredConfig = Bitmap.Config.RGB_565;
				Bitmap myBitmap = BitmapFactory.decodeFile(
						sdImageMainDirectory.getAbsolutePath(), BitmapFactoryOptionsbfo);
				FaceDetector.Face[] myFace = new FaceDetector.Face[1];
				FaceDetector myFaceDetect = new FaceDetector(myBitmap.getWidth(),
						myBitmap.getHeight(), 1);
				myFaceDetect.findFaces(myBitmap, myFace);

				if (myFace[0] != null && myFace[0].confidence() >= .3) {
					Toast.makeText(getApplicationContext(), "Valid Admin!",
							Toast.LENGTH_SHORT).show();
					// Start listening for admin login button.
					loginToServer();
				}
				else {
					Toast.makeText(getApplicationContext(), "Invalid Admin!",
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
}
