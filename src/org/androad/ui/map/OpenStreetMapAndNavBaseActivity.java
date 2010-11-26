// Created by plusminus on 01:08:10 - 12.10.2008
package org.androad.ui.map;

import java.util.List;

import org.andnav.osm.views.overlay.OpenStreetMapViewOverlay;
import org.andnav.osm.views.util.OpenStreetMapRendererFactory;

import org.androad.R;
import org.androad.osm.OpenStreetMapActivity;
import org.androad.osm.views.overlay.OSMMapViewSimpleTraceOverlay;
import org.androad.osm.views.util.OSMMapGoogleRenderer;
import org.androad.osm.views.util.OSMMapMicrosoftRenderer;
import org.androad.osm.views.util.OSMMapYahooRenderer;
import org.androad.preferences.PreferenceConstants;
import org.androad.preferences.Preferences;
import org.androad.ui.map.overlay.ColorSchemeOverlay;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;


public abstract class OpenStreetMapAndNavBaseActivity extends OpenStreetMapActivity {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int MENU_SHOWTRACE_ID = 100;
	private static final int MENU_SUBMITTRACE_ID = MENU_SHOWTRACE_ID + 1;

	// ===========================================================
	// Fields
	// ===========================================================

	protected boolean mMenuVoiceEnabled = false;
	//
	//	protected boolean mIsLandScapeMode = false;

	protected ColorSchemeOverlay mColorSchemeOverlay;
	protected OSMMapViewSimpleTraceOverlay mSimpleTraceOverlay;

	// ===========================================================
	// Constructors
	// ===========================================================

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		this.onCreate(savedInstanceState, true, true);
	}

	/** Called when the activity is first created.
	 * @param savedInstanceState
	 * @param pDoGPSRecordingAndContributing If <code>true</code>, it automatically contributes to the OpenStreetMap Project in the background.
	 * @param pShowTitleBarInMap <code>true</code> if the title bar should remain visible.
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState, final boolean pDoGPSRecordingAndContributing, final boolean pShowTitleBarInMap) {
		super.onCreate(savedInstanceState, pDoGPSRecordingAndContributing, pShowTitleBarInMap);

		final boolean showTitleBarInMap = Preferences.getShowTitleBarInMap(this);
		/* The app-title-bar just takes some pixels away without being useful. */
		if(!showTitleBarInMap) {
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		}

        // Add google maps
        OpenStreetMapRendererFactory.addRenderer(
                   new OSMMapGoogleRenderer("Google Maps", 0, 19, 8, ".png",
                   "http://mt0.google.com/vt/lyrs=m@127&"));
        OpenStreetMapRendererFactory.addRenderer(
                   new OSMMapGoogleRenderer("Google Maps Satellite", 0, 19, 8, ".jpg",
                   "http://mt0.google.com/vt/lyrs=s@127,h@127&"));
        OpenStreetMapRendererFactory.addRenderer(
                   new OSMMapGoogleRenderer("Google Maps Terrain", 0, 15, 8, ".jpg",
                   "http://mt0.google.com/vt/lyrs=t@127,r@127&"));

        // Add yahoo maps
        OpenStreetMapRendererFactory.addRenderer(
                   new OSMMapYahooRenderer("Yahoo Maps", 0, 17, 8, ".jpg",
                   "http://maps.yimg.com/hw/tile?"));
        OpenStreetMapRendererFactory.addRenderer(
                   new OSMMapYahooRenderer("Yahoo Maps Satellite", 0, 17, 8, ".jpg",
                   "http://maps.yimg.com/ae/ximg?"));

        // Add microsoft maps
        OpenStreetMapRendererFactory.addRenderer(
                   new OSMMapMicrosoftRenderer("Microsoft Maps", 0, 19, 8, ".png",
                   "http://r0.ortho.tiles.virtualearth.net/tiles/r"));
        OpenStreetMapRendererFactory.addRenderer(
                   new OSMMapMicrosoftRenderer("Microsoft Earth", 0, 19, 8, ".jpg",
                   "http://a0.ortho.tiles.virtualearth.net/tiles/a"));
        OpenStreetMapRendererFactory.addRenderer(
                   new OSMMapMicrosoftRenderer("Microsoft Hybrid", 0, 19, 8, ".jpg",
                   "http://h0.ortho.tiles.virtualearth.net/tiles/h"));

		this.onSetupContentView();

        // Add multi touch zoom
        this.mOSMapView.setBuiltInZoomControls(false);
        this.mOSMapView.setMultiTouchControls(true);

		final List<OpenStreetMapViewOverlay> overlays = this.mOSMapView.getOverlays();

		this.mSimpleTraceOverlay = new OSMMapViewSimpleTraceOverlay(this, super.getRouteRecorder().getRecordedGeoPoints(), PreferenceConstants.PREF_DISPLAYQUALITY_HIGH);
		this.mSimpleTraceOverlay.setVisible(false);
		overlays.add(this.mSimpleTraceOverlay);

		this.mColorSchemeOverlay = new ColorSchemeOverlay(this);
		overlays.add(this.mColorSchemeOverlay);

		this.mMenuVoiceEnabled = Preferences.getMenuVoiceEnabled(this);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected void onResume() {
		this.mColorSchemeOverlay.setColorScheme(Preferences.getSharedThemeID(this));
		this.setRequestedOrientation(Preferences.getRequestedScreenOrientation(this));
		super.onResume();
	}

	protected abstract void onSetupContentView();

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		int pos = 100;
		{ // Trace-Item
			menu.add(pos, MENU_SHOWTRACE_ID, pos, getString(R.string.maps_menu_trace_show))
			.setIcon(R.drawable.trace)
			.setAlphabeticShortcut('t');
			pos++;
		}

		{ // Submit-Item
			menu.add(pos, MENU_SUBMITTRACE_ID, pos, getString(R.string.maps_menu_trace_submit))
			.setIcon(R.drawable.trace_submit)
			.setAlphabeticShortcut('s');
			pos++;
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
		switch(item.getItemId()){
			case MENU_SHOWTRACE_ID:
				this.mSimpleTraceOverlay.setVisible(!this.mSimpleTraceOverlay.isVisible());
				return true;
			case MENU_SUBMITTRACE_ID:
				disableDoGPSRecordingAndContributing(true);
				enableDoGPSRecordingAndContributing();
				return true;
			default:
				return super.onMenuItemSelected(featureId, item);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(final Menu menu) {
		/* Refresh Show/Hide Trace item. */
		menu.findItem(MENU_SHOWTRACE_ID).setTitle((this.mSimpleTraceOverlay.isVisible()) ? R.string.maps_menu_trace_hide : R.string.maps_menu_trace_show);

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void disableDoGPSRecordingAndContributing(final boolean pContributdeCurrentRoute) {
		super.disableDoGPSRecordingAndContributing(pContributdeCurrentRoute);

		/* Make the Overlay use the new Polyline. */
		this.mSimpleTraceOverlay.setPolyline(super.getRouteRecorder().getRecordedGeoPoints());
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
