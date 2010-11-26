//Created by plusminus on 13:59:14 - 12.02.2008
package org.andnav2.ui.sd;

import org.andnav2.R;
import org.andnav2.adt.voice.Voice;
import org.andnav2.preferences.Preferences;
import org.andnav2.ui.AndNavBaseActivity;
import org.andnav2.ui.common.OnClickOnFocusChangedListenerAdapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class SDStreetNumber extends AndNavBaseActivity {
	// ===========================================================
	// Final Fields
	// ===========================================================

	/* REQUEST-CODES for SubActivities. */
	protected static final int REQUESTCODE_RESOLVER = 0x1737;

	// ===========================================================
	// Fields
	// ===========================================================

	protected EditText streetNumberEditText;
	protected Bundle bundleCreatedWith;

	// ===========================================================
	// Constructors
	// ===========================================================

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle);
		Preferences.applySharedSettings(this);
		setContentView(R.layout.sd_streetnumber);

		/* Save the Extras Bundle of the Intent this Activity
		 * was created with, because it contains the Information,
		 * that will finally be used for a GeoCode API. */
		this.bundleCreatedWith = this.getIntent().getExtras();

		this.streetNumberEditText = (EditText)findViewById(R.id.et_sd_streetnumber_numberentered);
        this.streetNumberEditText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(this.streetNumberEditText, InputMethodManager.SHOW_FORCED);

		this.applyTopButtonListeners();

		if(super.mMenuVoiceEnabled) {
			MediaPlayer.create(this, R.raw.enter_a_streetnumber).start();
		}
	}

    @Override
    protected void onDestroy() {
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(this.streetNumberEditText.getWindowToken(), 0);
        super.onDestroy();
    }

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		switch(resultCode){
			case SUBACTIVITY_RESULTCODE_CHAINCLOSE_SUCCESS:
				this.setResult(SUBACTIVITY_RESULTCODE_CHAINCLOSE_SUCCESS, data);
				this.finish();
				break;
			case SUBACTIVITY_RESULTCODE_CHAINCLOSE_QUITTED:
				this.setResult(SUBACTIVITY_RESULTCODE_CHAINCLOSE_QUITTED, data);
				this.finish();
				break;
		}
		/* Finally call the super()-method. */
		super.onActivityResult(requestCode, resultCode, data);
	}

	// ===========================================================
	// Methods
	// ===========================================================


	private void advanceToNextScreen(final boolean skipThis) {
		if(!skipThis && this.streetNumberEditText.getText().length() == 0){
			Toast.makeText(this, R.string.toast_sd_streetnumber_empty, Toast.LENGTH_SHORT).show();
		}else{
			final String streetNumber = SDStreetNumber.this.streetNumberEditText.getText().toString();

			if(skipThis){
				/* Remove Info from the bundle that may be included in it. */
				SDStreetNumber.this.bundleCreatedWith.remove(EXTRAS_STREETNUMBER_ID);
			}else{
				if(streetNumber.length() == 0){
					Toast.makeText(this, R.string.toast_sd_streetnumber_empty, Toast.LENGTH_SHORT).show();
					return;
				}
				if (SDStreetNumber.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDStreetNumber.this, R.raw.ok).start();
				}

				/* Add the StreetNumber to the Bundle to be passed further. */
				SDStreetNumber.this.bundleCreatedWith.putString(EXTRAS_STREETNUMBER_ID, streetNumber);
			}

			/* Create an Intent to open the Map as a SubActivity. */
			final Intent resolveIntent = new Intent(SDStreetNumber.this, SDResolver.class);

			/* Pass the Bundle this Activity was created with further. */
			resolveIntent.putExtras(SDStreetNumber.this.bundleCreatedWith);

			startActivityForResult(resolveIntent, REQUESTCODE_RESOLVER);
		}
	}

	protected void applyTopButtonListeners() {
		this.streetNumberEditText.setOnKeyListener(new OnKeyListener(){
			@Override
			public boolean onKey(final View arg0, final int arg1, final KeyEvent ke) {
				if(ke.getAction() == KeyEvent.ACTION_DOWN){
					if(ke.getKeyCode() == KeyEvent.KEYCODE_ENTER || ke.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER){
						if(SDStreetNumber.super.mMenuVoiceEnabled) {
							MediaPlayer.create(SDStreetNumber.this, R.raw.ok).start();
						}

						advanceToNextScreen(false);
						return true;
					}
				}
				return false;
			}
		});

		/* Set OnClickListener for Skip-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_streetnumber_skip)){
			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused && SDStreetNumber.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDStreetNumber.this, R.raw.skip).start();
				}
			}

			@Override
			public void onClicked(final View me) {
				advanceToNextScreen(true);
			}
		};

		/* Set OnClickListener for Back-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_streetnumber_back)){
			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused && SDStreetNumber.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDStreetNumber.this, R.raw.close).start();
				}
			}

			@Override
			public void onClicked(final View me) {
				/* Back one level. */
				SDStreetNumber.this.setResult(SUBACTIVITY_RESULTCODE_UP_ONE_LEVEL);
				SDStreetNumber.this.finish();
			}
		};

		/* Set OnClickListener for Close-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_streetnumber_close)){
			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused && SDStreetNumber.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDStreetNumber.this, R.raw.close).start();
				}
			}

			@Override
			public void onClicked(final View me) {
				/* Set ResultCode that the calling
				 * activity knows that we want
				 * to go back to the Base-Menu */
				SDStreetNumber.this.setResult(SUBACTIVITY_RESULTCODE_CHAINCLOSE_QUITTED);
				SDStreetNumber.this.finish();
			}
		};
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}