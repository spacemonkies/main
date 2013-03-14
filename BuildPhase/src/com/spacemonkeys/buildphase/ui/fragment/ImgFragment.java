package com.spacemonkeys.buildphase.ui.fragment;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.spacemonkeys.buildphase.R;

public class ImgFragment extends Fragment {
	/*
	 *
	 * @Author: MDRS
	 * @Date: Mar 12, 2013
	 *
	 */

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private ImageView mBgnd;
	private LinearLayout mLay;

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
		final View v = inflater.inflate(R.layout.image_fragment_container, container, true);
		mBgnd = (ImageView) v.findViewById(R.id.image_view);
		return v;
    }

	// ===========================================================
	// Methods
	// ===========================================================

	public void onSetBackground(final Drawable d) {
		mBgnd.setImageDrawable(d);
	}

	public LinearLayout onGetLayout() {
		return mLay;
	}

}
