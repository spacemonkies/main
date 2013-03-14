package com.spacemonkeys.buildphase.ui.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.spacemonkeys.buildphase.R;

public class ButtonFragment extends Fragment {

	/*
	 *
	 * @Author: MDRS
	 * @Date: Mar 11, 2013
	 *
	 */

	// ===========================================================
	// Constants
	// ===========================================================

	private static int CAMERA_REQUEST = 0;
	private static int RESULT_LOAD_IMAGE = 1;

	// ===========================================================
	// Fields
	// ===========================================================

	Button mCam, mGal;
	LinearLayout mLay;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		final View v =inflater.inflate(R.layout.button_fragment_container, container, true);

		mCam = (Button) v.findViewById(R.id.camera_button);
		mCam.setText("Camera");
		mCam.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				final Intent c = new Intent("android.media.action.IMAGE_CAPTURE");
				getActivity().startActivityForResult(c, CAMERA_REQUEST);
			}

		});


		mGal = (Button) v.findViewById(R.id.gallery_button);
		mGal.setText("Gallery");
		mGal.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				final Intent g = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				getActivity().startActivityForResult(g, RESULT_LOAD_IMAGE);

			}

		});

		return v;

    }

	// ===========================================================
	// Methods
	// ===========================================================
}
