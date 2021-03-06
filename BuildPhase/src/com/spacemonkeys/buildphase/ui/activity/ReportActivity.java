package com.spacemonkeys.buildphase.ui.activity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.util.Date;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.spacemonkeys.buildphase.R;
import com.spacemonkeys.buildphase.ui.fragment.ButtonFragment;
import com.spacemonkeys.buildphase.ui.fragment.ImgFragment;
import com.spacemonkeys.util.MSLLocationManager;

/*
 * launch choose activity to get URI of pic to upload
 * upload pic via POST in async task
 * connect to DB and submit text data from form
 * close all remote connections
 */

public class ReportActivity extends Activity implements OnClickListener {

	private static int CAMERA_REQUEST = 0;
	private static int RESULT_LOAD_IMAGE = 1;
	private static String TAG = "TAG";
	//private ImageView mImg;
    private Button mSubmit, mGetLoc;
    private TextView mAdd;
    private String picturePath, assetPath, assetName;
    private Bitmap mBitmapOut;
    private ButtonFragment mButnFrag;
    private ImgFragment mImgFrag;
    private MSLLocationManager mLoc;
    private File mFile;

	@Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_activity);

        mSubmit = (Button) findViewById(R.id.submit);
        mSubmit.setOnClickListener(this);

        mGetLoc = (Button) findViewById(R.id.get_location);
        mGetLoc.setOnClickListener(this);

        final TextView date = (TextView)findViewById(R.id.date);
        mAdd = (TextView)findViewById(R.id.address);

        final Date d = new Date();
        final CharSequence s  = DateFormat.format("MMMM d, yyyy ", d.getTime());
        date.setText(s);

        mLoc = new MSLLocationManager(this);
        Log.e("@REPORT_ACTIVITY", "mLOC equals " + mLoc);
        Log.e("@REPORT_ACTIVITY", "location is " + mLoc.getAddress(mLoc.getLocation(false)));
        final String add = mLoc.getAddress(mLoc.getLocation(false));

        mAdd.setText(add);

    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mImgFrag = (ImgFragment) this.getFragmentManager().findFragmentById(R.id.image_fragment);

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null) {

        	mImgFrag.onSetBackground(new BitmapDrawable(getBaseContext().getResources(), (Bitmap) data.getExtras().get("data")));
        }

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            final Uri selectedImage = data.getData();
            final String[] filePathColumn = { MediaStore.Images.Media.DATA };

            final Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            final int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();

            mBitmapOut = decodeSampledRotatedBitmapFromFile(512, 512, picturePath);
            mImgFrag.onSetBackground(new BitmapDrawable(getBaseContext().getResources(), mBitmapOut));

        	assetName = "transport" + picturePath.substring(49, picturePath.length());

        	//TODO: save file to temporary app directory for transport, not back in device's gallery

        	Log.w(TAG, "About to start writing file");

			try {
				Log.i(TAG, "Writing file");
				Log.w("@REPORT_ACTIVITY.onActivityResult()", "File path " + getBaseContext().getFilesDir() + assetName);
				mFile = new File(getBaseContext().getFilesDir(), assetName);
		        final FileOutputStream outStream = new FileOutputStream(mFile);
		        mBitmapOut.compress(Bitmap.CompressFormat.JPEG, 50, outStream);
				outStream.close();
				Log.i(TAG, "Finished writing file");
			} catch (final Exception e) {
				e.printStackTrace();
				Log.w(TAG, "File write failed");
			}
        }

        onSwitchFragments();

    }

    @Override
    public void onClick(final View arg0) {
    	Log.w(TAG, "onClick BEGIN");
    	if (arg0.equals(mSubmit)) onTransportImg();

    	if (arg0.equals(mGetLoc)) {
    		mAdd.setText("Please Wait..." );
    		mAdd.setText(mLoc.getAddress(mLoc.getLocation(false)));
    	}

    	Log.w(TAG, "onClick END");
    }

	public void onTransportImg() {
    	Log.w(TAG, "onTransportImg BEGIN");
    	 new ImageUpload().execute();
    	Log.w(TAG, "onTrasnportImg END");
    }

	private class ImageUpload extends AsyncTask<String, Void, String> {

        final FTPClient ftpClient = new FTPClient();

        @Override
        protected String doInBackground(final String... params) {
        	Log.w(TAG, "Upload Started");
            	try {
            		ftpClient.connect(InetAddress.getByName("ftp.31stcenturydesigns.com"));
              	    ftpClient.login("mimclaughlin", "C0d3F3st");
              	    ftpClient.changeWorkingDirectory("/31stcenturydesigns.com/images/");
              	    ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

              	    BufferedInputStream buffIn = null;
              	    Log.w("@ReportActivity.ftpClient.doInBackground()", "FileName " + mFile.getAbsolutePath());
              		buffIn = new BufferedInputStream(new FileInputStream(mFile));
              		ftpClient.enterLocalPassiveMode();
              	    ftpClient.storeFile(assetName, buffIn);
              	    buffIn.close();
              	    ftpClient.logout();
              	    ftpClient.disconnect();
              	    Log.w(TAG, "Upload Successful");

                  	} catch (final Exception e) {
              			e.printStackTrace();
              			Log.w(TAG, "Upload Failed");
              		}

              return "Executed";
        }

        @Override
        protected void onPostExecute(final String result) {

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(final Void... values) {
        }
    }

	public static int calculateInSampleSize(final BitmapFactory.Options options, final int reqWidth, final int reqHeight) {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

        // Calculate ratios of height and width to requested height and width
        final int heightRatio = Math.round((float) height / (float) reqHeight);
        final int widthRatio = Math.round((float) width / (float) reqWidth);

        // Choose the smallest ratio as inSampleSize value, this will guarantee
        // a final image with both dimensions larger than or equal to the
        // requested height and width.
        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
    }

    return inSampleSize;
}

	public Bitmap decodeSampledRotatedBitmapFromFile(final int reqWidth, final int reqHeight, final String pPath) {
	    // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options op = new BitmapFactory.Options();
        op.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(pPath, op);

	    // Calculate inSampleSize
	    op.inSampleSize = calculateInSampleSize(op, reqWidth, reqHeight);
	    // Decode bitmap with inSampleSize set
	    op.inJustDecodeBounds = false;
	    final Bitmap pulledImg = BitmapFactory.decodeFile(pPath, op);

        final Bitmap canvasBitmap = pulledImg.copy(Bitmap.Config.ARGB_8888, true);
        final Canvas canvas = new Canvas(canvasBitmap);
        final Matrix matrix = new Matrix();
        matrix.setRotate(90 , pulledImg.getWidth()/2, pulledImg.getHeight()/2);
        canvas.drawBitmap(pulledImg, matrix, new Paint());

        final Bitmap bmd = Bitmap.createBitmap(pulledImg, 0, 0, reqWidth, reqHeight, matrix, true);

        return bmd;

	}

	protected void onSwitchFragments() {
        final android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.hide(this.getFragmentManager().findFragmentById(R.id.button_fragment));
        ft.show(this.getFragmentManager().findFragmentById(R.id.image_fragment));
        ft.commit();
	}

}