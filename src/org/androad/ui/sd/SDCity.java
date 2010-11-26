// Created by plusminus on 21:30:10 - 15.05.2008
package org.androad.ui.sd;

import java.util.ArrayList;
import java.util.List;

import org.androad.R;
import org.androad.db.DBManager;
import org.androad.db.DataBaseException;
import org.androad.preferences.Preferences;
import org.androad.sys.ors.adt.GeocodedAddress;
import org.androad.sys.ors.adt.lus.Country;
import org.androad.sys.ors.exceptions.ORSException;
import org.androad.sys.ors.lus.LUSRequester;
import org.androad.ui.AndNavBaseActivity;
import org.androad.ui.common.InlineAutoCompleterCombined;
import org.androad.ui.common.OnClickOnFocusChangedListenerAdapter;
import org.androad.util.constants.Constants;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.method.TextKeyListener;
import android.text.method.TextKeyListener.Capitalize;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class SDCity extends AndNavBaseActivity {
	// ===========================================================
	// Final Fields
	// ===========================================================

	/* REQUEST-CODES for SubActivities. */
	protected static final int REQUESTCODE_SD_STREET = 0x1537;

	// ===========================================================
	// Fields
	// ===========================================================

	protected EditText cityNameEditText;
	protected Bundle bundleCreatedWith;
	protected String acItem;

	// ===========================================================
	// Constructors
	// ===========================================================

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle, true); // We need DataState-Info
		Preferences.applySharedSettings(this);
		this.setContentView(R.layout.sd_city);

		/* Save the Extras Bundle of the Intent this Activity
		 * was created with, because it contains the Information,
		 * that will finally be used for a GeoCode API. */
		this.bundleCreatedWith = this.getIntent().getExtras();

		this.cityNameEditText = (EditText)findViewById(R.id.et_sd_city_cityentered);
		this.cityNameEditText.setKeyListener(new TextKeyListener(Capitalize.WORDS, false)); // TODO Possible in XML !?!?!
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(this.cityNameEditText, InputMethodManager.SHOW_FORCED);

		this.applyTopMenuButtonListeners();
		this.applyOkButtonListener();
		this.applyAutoCompleteListeners();

		if(super.mMenuVoiceEnabled) {
			MediaPlayer.create(this, R.raw.enter_a_cityname).start();
		}
	}

    @Override
    protected void onDestroy() {
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(this.cityNameEditText.getWindowToken(), 0);
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

	protected void advanceToNextScreen() {
		if(this.cityNameEditText.getText().length() == 0){
			Toast.makeText(this, R.string.toast_sd_streetname_empty, Toast.LENGTH_SHORT).show();
		}else{
			final String cityName = SDCity.this.cityNameEditText.getText().toString();
			try {
				final Country nat = this.bundleCreatedWith.getParcelable(EXTRAS_COUNTRY_ID);
				DBManager.addCityName(this, cityName, nat.COUNTRYCODE);
			} catch (final DataBaseException e) {
				Log.e(DEBUGTAG, "Error on inserting CityName", e);
			}

			/* Then create an Intent to open the SubActivity. */
			final Intent sdStreetSearchIntent = new Intent(SDCity.this, SDStreet.class);
			/* Add the ZipCode to the Bundle to be passed further. */
			SDCity.this.bundleCreatedWith.putString(EXTRAS_CITYNAME_ID, SDCity.this.cityNameEditText.getText().toString());
			/* Pass the Bundle this Activity was created with further. */
			sdStreetSearchIntent.putExtras(SDCity.this.bundleCreatedWith);

			SDCity.this.startActivityForResult(sdStreetSearchIntent, REQUESTCODE_SD_STREET);
		}
	}

	protected void applyAutoCompleteListeners() {
		try {
			final Country nat = this.bundleCreatedWith.getParcelable(EXTRAS_COUNTRY_ID);
			final String countrycode = nat.COUNTRYCODE;
			final List<String> usedCityNames = DBManager.getCityNames(this, countrycode);

			new InlineAutoCompleterCombined(this.cityNameEditText, usedCityNames, false){
				@Override
				public boolean onEnter() {
					SDCity.this.advanceToNextScreen();
					return true;
				}

				@Override
				public ArrayList<String> onGetDynamic() {
					//					if(SDCity.super.getDataConnectionStrengh() == 0)
					//						return null;
					final String cityname = SDCity.this.cityNameEditText.getText().toString();

					if(cityname.length() < 7) {
						return null;
					}

					List<GeocodedAddress> addresses;
					try {
                        final LUSRequester lus = Preferences.getORSServer(SDCity.this).LOCATIONUTILITYSERVICE;
						addresses = lus.requestFreeformAddress(SDCity.this, Country.fromAbbreviation(countrycode), cityname);

						if(addresses == null) {
							return null;
						}

						final ArrayList<String> out = new ArrayList<String>();
						for (final GeocodedAddress a : addresses) {
							final String locality = a.getMunicipality();
							Log.d(Constants.DEBUGTAG, "Found locality: " + locality);
							if(locality != null) {
								out.add(locality);
							}
						}

						return out;
					} catch (final ORSException e) {
						runOnUiThread(new Runnable(){
							@Override
							public void run() {
								Toast.makeText(SDCity.this, e.getErrors().get(0).toUserString(), Toast.LENGTH_SHORT).show();
							}
						});
						Log.e(DEBUGTAG, "Geocoding-Error", e);
						return null;
					} catch (final Exception e) {
						Log.e(DEBUGTAG, "Geocoding-Error", e);
						return null;
					}
				}
			};
		} catch (final DataBaseException e) {
			Log.e(DEBUGTAG, "Error on loading CityNames", e);
		}
	}

	protected void applyOkButtonListener() {
		/* Set OnClickListener for OK-Button. */
		findViewById(R.id.btn_sd_city_ok).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				SDCity.this.advanceToNextScreen();
			}
		});
	}

	protected void applyTopMenuButtonListeners() {
		/* Set Listener for Back-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_city_back)){
			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused && SDCity.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDCity.this, R.raw.close).start();
				}
			}

			@Override
			public void onClicked(final View v) {
				/* Back one level. */
				SDCity.this.setResult(SUBACTIVITY_RESULTCODE_UP_ONE_LEVEL);
				SDCity.this.finish();
			}
		};

		/* Set Listener for Close-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_city_close)){
			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused && SDCity.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDCity.this, R.raw.close).start();
				}
			}

			@Override
			public void onClicked(final View me) {
				/* Set RsultCode that the calling
				 * activity knows that we want
				 * to go back to the Base-Menu */
				SDCity.this.setResult(SUBACTIVITY_RESULTCODE_CHAINCLOSE_QUITTED);
				SDCity.this.finish();
			}
		};
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}