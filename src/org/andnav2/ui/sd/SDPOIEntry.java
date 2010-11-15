// Created by plusminus on 00:45:35 - 25.10.2008
package org.andnav2.ui.sd;

import java.util.ArrayList;
import java.util.List;

import org.andnav2.R;
import org.andnav2.adt.DBPOI;
import org.andnav2.db.DBManager;
import org.andnav2.db.DataBaseException;
import org.andnav2.preferences.Preferences;
import org.andnav2.ui.AndNavBaseActivity;
import org.andnav2.ui.common.InlineAutoCompleterConstant;
import org.andnav2.ui.common.OnClickOnFocusChangedListenerAdapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.method.TextKeyListener;
import android.text.method.TextKeyListener.Capitalize;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;


public class SDPOIEntry extends AndNavBaseActivity{
	// ===========================================================
	// Final Fields
	// ===========================================================

	/* REQUEST-CODES for SubActivities. */
	protected static final int REQUESTCODE_SD_POISEARCHLIST = 0;

	protected static final int MENU_WIPE_ID = Menu.FIRST;

	// ===========================================================
	// Fields
	// ===========================================================

	protected EditText poiEditText;
	protected Bundle bundleCreatedWith;

	private InlineAutoCompleterConstant mAutoCompleter;

	// ===========================================================
	// Constructors
	// ===========================================================

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle, true); // We need DataState-Info
		Preferences.applySharedSettings(this);
		this.setContentView(R.layout.sd_poi_entry);

		/* Save the Extras Bundle of the Intent this Activity
		 * was created with, because it contains the Information,
		 * that will finally be used for a GeoCode API. */
		this.bundleCreatedWith = this.getIntent().getExtras();

		this.poiEditText = (EditText)findViewById(R.id.et_sd_poi_entry_query);
		this.poiEditText.setKeyListener(new TextKeyListener(Capitalize.WORDS,false));
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(this.poiEditText, InputMethodManager.SHOW_FORCED);

		this.applyTopMenuButtonListeners();
		this.applyOkButtonListener();
		this.applyAutoCompleteListeners();

		if(super.mMenuVoiceEnabled) {
			MediaPlayer.create(this, R.raw.enter_a_streetname).start();
		}
	}

    @Override
    protected void onDestroy() {
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(this.poiEditText.getWindowToken(), 0);
        super.onDestroy();
    }

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		menu.add(0, MENU_WIPE_ID, Menu.NONE, R.string.menu_sd_poi_wipe).setIcon(R.drawable.wipe).setAlphabeticShortcut('w');
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
		switch(item.getItemId()){
			case MENU_WIPE_ID:
				try {
					DBManager.clearPOIHistory(this);
					this.mAutoCompleter.clearStatic();
				} catch (final DataBaseException e) {
					Log.e(DEBUGTAG, "DBError", e);
				}
				return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

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
		if(this.poiEditText.getText().length() == 0){
			Toast.makeText(this, R.string.toast_sd_poi_empty, Toast.LENGTH_SHORT).show();
			return;
		}

		// TODO REMOVE COMENTS, when local search works.

		//		final UnitSystem us = Preferences.getUnitSystem(this);
		//
		//		final int[] valDist;
		//		switch(us){
		//			case IMPERIAL:
		//				valDist = getResources().getIntArray(R.array.poi_search_radius_ors_imperial);
		//				break;
		//			case METRIC:
		//			default:
		//				valDist = getResources().getIntArray(R.array.poi_search_radius_ors_metric);
		//		}
		//		final String[] valStr = new String[valDist.length];
		//
		//		for (int i = 0; i < valDist.length; i++){
		//			final int cur = valDist[i];
		//			if(cur == SDPOISearchList.POISEARCH_RADIUS_GLOBAL){
		//				valStr[i] = getString(R.string.poi_search_radius_global);
		//			} else {
		//				final String[] distStringParts = us.getDistanceString(cur, null);
		//				valStr[i] = distStringParts[UnitSystem.DISTSTRINGS_DIST_ID] + distStringParts[UnitSystem.DISTSTRINGS_UNIT_ID];
		//			}
		//		}
		//
		//		new AlertDialog.Builder(this)
		//			.setTitle(R.string.choose_search_radius)
		//			.setSingleChoiceItems(valStr, 3, new DialogInterface.OnClickListener(){
		//				@Override
		//				public void onClick(final DialogInterface d, final int which) {
		//					d.dismiss();

		final Intent favIntent = new Intent(SDPOIEntry.this, SDPOISearchList.class);
		// ........................................ valDist[which]);
		SDPOIEntry.this.bundleCreatedWith.putInt(SDPOISearchList.EXTRAS_POISEARCH_RADIUS, SDPOISearchList.POISEARCH_RADIUS_GLOBAL);
		SDPOIEntry.this.bundleCreatedWith.putInt(SDPOISearchList.EXTRAS_POISEARCH_MODE, SDPOISearchList.EXTRAS_POISEARCH_MODE_GOOGLE_FREEFORM_SEARCH);
		SDPOIEntry.this.bundleCreatedWith.putString(SDPOISearchList.EXTRAS_POISEARCH_QUERY, SDPOIEntry.this.poiEditText.getText().toString());
		favIntent.putExtras(SDPOIEntry.this.bundleCreatedWith);
		SDPOIEntry.this.startActivityForResult(favIntent, REQUESTCODE_SD_POISEARCHLIST);
		//				}
		//		}).create().show();
	}

	protected void applyAutoCompleteListeners() {
		try{
			final List<DBPOI> usedPOIs = DBManager.getPOIHistory(this);

			final ArrayList<String> usedPOIStrings = new ArrayList<String>(usedPOIs.size());

			for(final DBPOI poi : usedPOIs) {
				usedPOIStrings.add(poi.getName());
			}

			this.mAutoCompleter = new InlineAutoCompleterConstant(this.poiEditText, usedPOIStrings, false){
				@Override
				public boolean onEnter() {
					SDPOIEntry.this.advanceToNextScreen();
					return true;
				}
			};
		} catch (final DataBaseException e) {
			//			Log.e(DEBUGTAG, "Error on loading POIs", e);
		}
	}

	protected void applyOkButtonListener() {
		/* Set Listener for OK-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.btn_sd_poi_entry_ok)){
			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused && SDPOIEntry.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDPOIEntry.this, R.raw.ok).start();
				}
			}

			@Override
			public void onClicked(final View me) {
				SDPOIEntry.this.advanceToNextScreen();
			}
		};
	}

	protected void applyTopMenuButtonListeners() {
		/* Set Listener for Back-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_poi_entry_back)){
			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused && SDPOIEntry.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDPOIEntry.this, R.raw.close).start();
				}
			}

			@Override
			public void onClicked(final View me) {
				/* Back one level. */
				SDPOIEntry.this.setResult(SUBACTIVITY_RESULTCODE_UP_ONE_LEVEL);
				SDPOIEntry.this.finish();
			}
		};

		/* Set Listener for Close-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_poi_entry_close)){
			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused && SDPOIEntry.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDPOIEntry.this, R.raw.close).start();
				}
			}

			@Override
			public void onClicked(final View me) {
				/* Set RsultCode that the calling
				 * activity knows that we want
				 * to go back to the Base-Menu */
				SDPOIEntry.this.setResult(SUBACTIVITY_RESULTCODE_CHAINCLOSE_QUITTED);
				SDPOIEntry.this.finish();
			}
		};
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
