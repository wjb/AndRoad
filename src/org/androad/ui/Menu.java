//Created by plusminus on 19:47:09 - 01.02.2008
package org.androad.ui;

import org.osmdroid.util.GeoPoint;

import org.androad.R;
import org.androad.preferences.Preferences;
import org.androad.sys.ors.adt.ds.POIType;
import org.androad.ui.common.CommonCallbackAdapter;
import org.androad.ui.common.CommonDialogFactory;
import org.androad.ui.common.OnClickOnFocusChangedListenerAdapter;
import org.androad.ui.map.OpenStreetDDMap;
import org.androad.ui.map.WhereAmIMap;
import org.androad.ui.sd.SDMainChoose;
import org.androad.ui.sd.SDPOISearchList;
import org.androad.ui.settings.SettingsMenu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

public class Menu extends AndNavGPSActivity {
	// ===========================================================
	// Final Fields
	// ===========================================================

	/* REQUEST-CODES for SubActivities. */
	private static final int REQUESTCODE_SETTINGS = 0x1337;
	private static final int REQUESTCODE_SD_MAINCHOOSE = REQUESTCODE_SETTINGS + 1;
	private static final int REQUESTCODE_WHEREAMI = REQUESTCODE_SD_MAINCHOOSE + 1;
	private static final int REQUESTCODE_ABOUT = REQUESTCODE_WHEREAMI + 1;
	private static final int REQUESTCODE_TTS_DATA_CHECK_CODE = REQUESTCODE_ABOUT + 1;

	private static final int MENU_ABOUT_ID = android.view.Menu.FIRST;
	private static final int MENU_VERSIONINFO_ID = MENU_ABOUT_ID + 1;
	
	private static final int DIALOG_SHOW_VERSIONINFO = 0;
	private static final int DIALOG_SHOW_TTS_INSTALL = DIALOG_SHOW_VERSIONINFO + 1;

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Preferences.applySharedSettings(this);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		this.setContentView(R.layout.menu);

		this.findViewById(R.id.ibtn_whereami).requestFocus();

		this.applyMenuButtonListeners();

		findViewById(R.id.ibtn_whereami).setSoundEffectsEnabled(true);
		findViewById(R.id.ibtn_searchdestination).setSoundEffectsEnabled(false);

		/* Check if TTS is installed and the dialog was not permanently dismissed. */
		if(Preferences.showTTSNotInstalledInfo(this)){
			try{
				Intent checkIntent = new Intent();
				checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
				startActivityForResult(checkIntent, REQUESTCODE_TTS_DATA_CHECK_CODE);
			}catch(Throwable t){
				
			}
		}
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	private void applyMenuButtonListeners() {
		/* Set OnClickListener for Where-am-I-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_whereami)){
			@Override
			public void onBoth(final View v, final boolean justGotFocus) {
				if(justGotFocus){
					if(Menu.super.mMenuVoiceEnabled) {
						MediaPlayer.create(Menu.this, R.raw.where_am_i).start();
					}
				}
			}

			@Override
			public void onClicked(final View v) {
				startWhereAmIActivity();
			}
		};

		/* Set OnClickListener for Search-Destination-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_searchdestination)){
			@Override
			public void onBoth(final View v, final boolean justGotFocus) {
				if(justGotFocus){
					if(Menu.super.mMenuVoiceEnabled) {
						MediaPlayer.create(Menu.this, R.raw.search_destination).start();
					}
				}
			}

			@Override
			public void onClicked(final View v) {
				startSearchDestinationActivity();
			}
		};

		/* Set OnClickListener for Settings-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings)){
			@Override
			public void onBoth(final View v, final boolean justGotFocus) {
				if(justGotFocus){
					if(Menu.super.mMenuVoiceEnabled) {
						MediaPlayer.create(Menu.this, R.raw.settings).start();
					}
				}
			}

			@Override
			public void onClicked(final View v) {
				startSettingsActivity();
			}
		};

		/* Set OnClickListener for Exit-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_quit)){
			@Override
			public void onBoth(final View v, final boolean justGotFocus) {
				if(justGotFocus){
					if(Menu.super.mMenuVoiceEnabled) {
						MediaPlayer.create(Menu.this, R.raw.quit).start();
					}
				}
			}

			@Override
			public void onClicked(final View v) {
				Menu.this.finish();
			}
		};
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		switch(keyCode){
			case KeyEvent.KEYCODE_W: // Where am I
				startWhereAmIActivity();
				break;
			case KeyEvent.KEYCODE_S: // Search destination
				startSearchDestinationActivity();
				break;
			case KeyEvent.KEYCODE_C: // Config (aka Settings)
				startSettingsActivity();
				break;
			case KeyEvent.KEYCODE_Q: // Quit
				this.finish();
				break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		switch(requestCode){
			case REQUESTCODE_TTS_DATA_CHECK_CODE:
				if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
					// success, TTS is available
				} else {
					showDialog(DIALOG_SHOW_TTS_INSTALL);

				}
				break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(final android.view.Menu menu) {
		menu.add(0, MENU_VERSIONINFO_ID, android.view.Menu.NONE, getString(R.string.versioninfo)).setIcon(R.drawable.hardhat);
		menu.add(1, MENU_ABOUT_ID, android.view.Menu.NONE, getString(R.string.about)).setIcon(R.drawable.questionmark_small);
		return true;
	}



	@Override
	protected Dialog onCreateDialog(final int id) {
		switch(id){
			case DIALOG_SHOW_TTS_INSTALL:
				return new AlertDialog.Builder(this)
				.setMessage(R.string.tts_not_installed_message)
				.setTitle(R.string.tts_not_installed_title)
				.setIcon(R.drawable.information)
				.setPositiveButton(R.string.install, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface pDialog, int pWhich) {
						// missing data, install it
						Intent installIntent = new Intent();
						installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
						startActivity(installIntent);
					}
				})
				.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener(){
					@Override
					public void onClick(final DialogInterface d, final int which) {
						d.dismiss();
					}
				})
				.setNegativeButton(R.string.nevershowagain, new DialogInterface.OnClickListener(){
					@Override
					public void onClick(final DialogInterface d, final int which) {
						d.dismiss();
						Preferences.saveShowTTSNotInstalledInfo(Menu.this, false);
					}
				}).create();
			case DIALOG_SHOW_VERSIONINFO:
				return CommonDialogFactory.createVersionInfoDialog(this, new CommonCallbackAdapter<Void>(){
					@Override
					public void onSuccess(final Void result) {
						// Nothing
					}
				});
			default:
				return null;
		}
	}

	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
		switch (item.getItemId()) {
			case MENU_ABOUT_ID:
				final Intent aboutIntent = new Intent(this, About.class);
				startActivityForResult(aboutIntent, REQUESTCODE_ABOUT);
				return true;
			case MENU_VERSIONINFO_ID:
				showDialog(DIALOG_SHOW_VERSIONINFO);
				return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private void startSearchDestinationActivity() {
		/* Load SDMainChoose-Activity. */
		final Intent sdCountryIntent = new Intent(Menu.this, SDMainChoose.class);

		final Bundle b = new Bundle();
		b.putInt(MODE_SD, MODE_SD_DESTINATION);

		sdCountryIntent.putExtras(b);
		Menu.this.startActivityForResult(sdCountryIntent, REQUESTCODE_SD_MAINCHOOSE);
	}

	private void startSettingsActivity() {
		/* Load Settings-Activity. */
		final Intent settingsIntent = new Intent(Menu.this, SettingsMenu.class);
		Menu.this.startActivityForResult(settingsIntent, REQUESTCODE_SETTINGS);
	}

	private void startWhereAmIActivity() {
		/* Load WhereAmI-MapActivity. */
		final Intent whereAmIIntent = new Intent(Menu.this, WhereAmIMap.class);
		Menu.this.startActivityForResult(whereAmIIntent, REQUESTCODE_WHEREAMI);
	}

	@Override
	protected void onLocationChanged() {
		// Nothing, we just want GPS enabled :)
	}

	@Override
	protected void onLocationLost() {
		// Nothing, we just want GPS enabled :)
	}
}
