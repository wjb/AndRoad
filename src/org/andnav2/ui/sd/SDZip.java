//Created by plusminus on 23:19:02 - 03.02.2008
package org.andnav2.ui.sd;

import java.util.List;

import org.andnav2.R;
import org.andnav2.adt.voice.Voice;
import org.andnav2.db.DBManager;
import org.andnav2.db.DataBaseException;
import org.andnav2.preferences.Preferences;
import org.andnav2.sys.ors.adt.lus.Country;
import org.andnav2.ui.AndNavBaseActivity;
import org.andnav2.ui.common.InlineAutoCompleterConstant;
import org.andnav2.ui.common.OnClickOnFocusChangedListenerAdapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;


public class SDZip extends AndNavBaseActivity {
	// ===========================================================
	// Final Fields
	// ===========================================================

	/* REQUEST-CODES for SubActivities. */
	protected static final int REQUESTCODE_SD_STREET = 0x1537;

	// ===========================================================
	// Fields
	// ===========================================================

	protected EditText zipCodeEditText;
	protected Bundle bundleCreatedWith;

	// ===========================================================
	// Constructors
	// ===========================================================

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle);
		Preferences.applySharedSettings(this);
		setContentView(R.layout.sd_zip);

		/* Save the Extras Bundle of the Intent this Activity
		 * was created with, because it contains the Information,
		 * that will finally be used for the Yahoo GeoCode API. */
		this.bundleCreatedWith = this.getIntent().getExtras();

		this.zipCodeEditText = (EditText)findViewById(R.id.et_sd_zip_zipentered);
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(this.zipCodeEditText, InputMethodManager.SHOW_FORCED);

		this.applyTopButtonListeners();
		this.applyAutoCompleteListeners();
		this.applyOkButtonListener();

		if(super.mMenuVoiceEnabled) {
			MediaPlayer.create(this, R.raw.enter_a_zipcode).start();
		}
	}

    @Override
    protected void onDestroy() {
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(this.zipCodeEditText.getWindowToken(), 0);
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

	protected void applyAutoCompleteListeners() {
		try {
			final Country nat = this.bundleCreatedWith.getParcelable(EXTRAS_COUNTRY_ID);
			final List<String> usedZipCodes = DBManager.getZipCodes(this, nat.COUNTRYCODE);

			new InlineAutoCompleterConstant(this.zipCodeEditText, usedZipCodes, false){
				@Override
				public boolean onEnter() {
					SDZip.this.advanceToNextScreen();
					return true;
				}
			};
		} catch (final DataBaseException e) {
			Log.e(DEBUGTAG, "Error on loading ZipCodes", e);
		}
	}

	protected void advanceToNextScreen() {
		if(this.zipCodeEditText.getText().length() == 0){
			Toast.makeText(this, R.string.toast_sd_zipcode_empty, Toast.LENGTH_SHORT).show();
		}else{
			final String zipCode = SDZip.this.zipCodeEditText.getText().toString();
			try {
				final Country nat = this.bundleCreatedWith.getParcelable(EXTRAS_COUNTRY_ID);
				DBManager.addZipCode(this, zipCode, nat.COUNTRYCODE);
			} catch (final DataBaseException e) {
				Log.e(DEBUGTAG, "Error on inserting ZipCode", e);
			}

			/* Then create an Intent to open the SubActivity. */
			final Intent sdStreetSearchIntent = new Intent(SDZip.this, SDStreet.class);
			/* Add the ZipCode to the Bundle to be passed further. */
			SDZip.this.bundleCreatedWith.putString(EXTRAS_ZIPCODE_ID, zipCode);
			/* Pass the Bundle this Activity was created with further. */
			sdStreetSearchIntent.putExtras(SDZip.this.bundleCreatedWith);

			startActivityForResult(sdStreetSearchIntent, REQUESTCODE_SD_STREET);
		}
	}

	protected void applyOkButtonListener() {
		/* Set OnClickListener for OK-Button. */
		findViewById(R.id.btn_sd_zip_ok).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				SDZip.this.advanceToNextScreen();
			}
		});
	}

	protected void applyTopButtonListeners() {

		this.zipCodeEditText.setOnKeyListener(new OnKeyListener(){
			@Override
			public boolean onKey(final View arg0, final int arg1, final KeyEvent ke) {
				if(ke.getAction() == KeyEvent.ACTION_DOWN){
					if(SDZip.super.mMenuVoiceEnabled){
						final int resID = Voice.getNumberVoiceFromKeyCode(ke.getKeyCode());
						Log.d(DEBUGTAG, "" + ke.getKeyCode());
						if(resID != NOT_SET) {
							MediaPlayer.create(SDZip.this, resID).start();
						}
					}
					if(ke.getKeyCode() == KeyEvent.KEYCODE_ENTER || ke.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER){
						if(SDZip.super.mMenuVoiceEnabled) {
							MediaPlayer.create(SDZip.this, R.raw.ok).start();
						}

						advanceToNextScreen();
						return true;
					}
				}
				return false;
			}
		});

		/* Set OnClickListener for Back-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_zip_back)){
			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused && SDZip.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDZip.this, R.raw.close).start();
				}
			}

			@Override
			public void onClicked(final View me) {
				/* Back one level. */
				SDZip.this.setResult(SUBACTIVITY_RESULTCODE_UP_ONE_LEVEL);
				SDZip.this.finish();
			}
		};

		/* Set OnClickListener for Close-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_zip_close)){
			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused && SDZip.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDZip.this, R.raw.close).start();
				}
			}

			@Override
			public void onClicked(final View me) {
				/* Set ResultCode that the calling
				 * activity knows that we want
				 * to go back to the Base-Menu */
				SDZip.this.setResult(SUBACTIVITY_RESULTCODE_CHAINCLOSE_QUITTED);
				SDZip.this.finish();
			}
		};
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}