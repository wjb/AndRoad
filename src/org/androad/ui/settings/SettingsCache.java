// Created by plusminus on 18:41:23 - 10.04.2008
package org.androad.ui.settings;

import org.andnav.osm.tileprovider.OpenStreetMapTileFilesystemProvider;
import org.andnav.osm.tileprovider.constants.OpenStreetMapTileProviderConstants;

import org.androad.R;
import org.androad.ui.AndNavBaseActivity;
import org.androad.ui.common.OnClickOnFocusChangedListenerAdapter;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class SettingsCache extends AndNavBaseActivity {
	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected TextView mTvCurrentCacheSize;

	// ===========================================================
	// Constructors
	// ===========================================================

	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle);
		this.setContentView(R.layout.settings_cache);

		this.mTvCurrentCacheSize = (TextView)this.findViewById(R.id.tv_settings_cache_currentsize);

		this.applyButtonListeners();

		this.updateCurrentSizeText();
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

	private void updateCurrentSizeText() {
		final float usedCacheSize = OpenStreetMapTileFilesystemProvider.getUsedCacheSpace() / (1024 * 1024);
		final float maxCacheSize = OpenStreetMapTileProviderConstants.TILE_MAX_CACHE_SIZE_BYTES / (1024 * 1024);

		this.mTvCurrentCacheSize.setText(String.format(getString(R.string.tv_settings_cache_current_size), usedCacheSize, maxCacheSize));

		if(maxCacheSize < usedCacheSize) {
			this.mTvCurrentCacheSize.setTextColor(Color.rgb(255, 150, 0));
		} else {
			this.mTvCurrentCacheSize.setTextColor(Color.GREEN);
		}
	}

	protected void applyButtonListeners() {
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_settings_cache_close)){
			@Override
			public void onBoth(final View me, final boolean focused) {
				if(focused && SettingsCache.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SettingsCache.this, R.raw.close).start();
				}
                SettingsCache.this.finish();
			}

			@Override
			public void onClicked(final View me) {
			}
		};

	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
