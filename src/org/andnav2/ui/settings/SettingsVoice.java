//Created by plusminus on 19:01:29 - 20.05.2008
package org.andnav2.ui.settings;

import org.andnav2.R;
import org.andnav2.preferences.Preferences;
import org.andnav2.ui.AndNavBaseActivity;
import org.andnav2.ui.common.OnClickOnFocusChangedListenerAdapter;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;


public class SettingsVoice extends AndNavBaseActivity {
	// ===========================================================
	// Final Fields
	// ===========================================================

	protected static final int ADVANCED_REQUESTCODE = 0x1337;

	// ===========================================================
	// Fields
	// ===========================================================

	protected CheckBox chkMenuVoice;
	protected CheckBox chkDirectionVoice;
	protected TextView tvQuickInfo;

	// ===========================================================
	// Constructors
	// ===========================================================

	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle);
		Preferences.applySharedSettings(this);
		this.setContentView(R.layout.settings_voice);

		this.tvQuickInfo = (TextView)this.findViewById(R.id.tv_settings_voice_quickinfo);

		this.chkMenuVoice = (CheckBox)this.findViewById(R.id.chk_settings_voice_menuvoice);
		this.chkMenuVoice.setChecked(Preferences.getMenuVoiceEnabled(this));
		this.chkDirectionVoice = (CheckBox)this.findViewById(R.id.chk_settings_voice_directionvoice);
		this.chkDirectionVoice.setChecked(Preferences.getDirectionVoiceEnabled(this));

		this.applyTopButtonListeners();
		this.applyCheckBoxListeners();
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	private void applyTopButtonListeners() {
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_voice_goadvanced)){
			@Override
			public void onClicked(final View me) {
				// TODO No voice-file found for "advanced" or "details"
				//				if(SettingsVoice.super.mMenuVoiceEnabled)
				//					MediaPlayer.create(SettingsVoice.this, R.raw.close).start();

				final Intent goAdvancedIntent = new Intent(SettingsVoice.this, SettingsDirectionVoice.class);

				SettingsVoice.this.startActivityForResult(goAdvancedIntent, ADVANCED_REQUESTCODE);
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_voice_tts)){
			private TextToSpeech mTTS;
			private boolean mTTSAvailable = false;

			@Override
			public void onClicked(final View me) {
				final String test_message = getString(R.string.settings_voice_tts_working);
				if(this.mTTS == null){
					this.mTTS = new TextToSpeech(SettingsVoice.this, new TextToSpeech.OnInitListener(){
						@Override
						public void onInit(final int version) {
							mTTSAvailable = true;
							//							mTTS.setLanguage(Preferences.getDrivingDirectionsLanguage(SettingsDirectionVoice.this).getIETFLAnguageTag());
							mTTS.speak(test_message, 0, null);
						}
					});
				}else{
					if(this.mTTSAvailable) {
						this.mTTS.speak(test_message, 0, null);
					}
				}
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_voice_close)){
			@Override
			public void onClicked(final View me) {
				if(SettingsVoice.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SettingsVoice.this, R.raw.close).start();
				}

				SettingsVoice.this.finish();
			}
		};
	}

	private void applyCheckBoxListeners() {
		this.chkMenuVoice.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(final CompoundButton me, final boolean checked) {
				SettingsVoice.super.mMenuVoiceEnabled = SettingsVoice.this.chkMenuVoice.isChecked();

				if(SettingsVoice.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SettingsVoice.this, R.raw.save).start();
				}
				Preferences.saveMenuVoiceEnabled(SettingsVoice.this, SettingsVoice.this.chkMenuVoice.isChecked());
			}
		});
		new OnClickOnFocusChangedListenerAdapter(this.chkMenuVoice){

			@Override
			public void onBoth(final View me, final boolean focused) {
				SettingsVoice.this.tvQuickInfo.setText(R.string.tv_settings_voice_quickinfo_menuvoice_description);
			}
		};

		this.chkDirectionVoice.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(final CompoundButton me, final boolean checked) {
				if(SettingsVoice.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SettingsVoice.this, R.raw.save).start();
				}

				Preferences.saveDirectionVoiceEnabled(SettingsVoice.this, checked);
			}
		});
		new OnClickOnFocusChangedListenerAdapter(this.chkDirectionVoice){
			@Override
			public void onBoth(final View me, final boolean focused) {
				SettingsVoice.this.tvQuickInfo.setText(R.string.tv_settings_voice_quickinfo_directionvoice_description);
			}
		};
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
