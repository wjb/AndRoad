// Created by plusminus on 20:07:18 - 21.05.2008
package org.androad.ui.map;

import java.util.List;

import org.andnav.osm.util.GeoPoint;
import org.andnav.osm.views.OpenStreetMapView;
import org.andnav.osm.views.OpenStreetMapView.OpenStreetMapViewProjection;
import org.andnav.osm.views.OpenStreetMapViewController.AnimationType;
import org.andnav.osm.views.overlay.OpenStreetMapViewOverlay;
import org.andnav.osm.views.overlay.OpenStreetMapViewSimpleLocationOverlay;

import org.androad.R;
import org.androad.adt.AndNavLocation;
import org.androad.osm.views.overlay.OSMMapViewSingleIconOverlay;
import org.androad.preferences.Preferences;
import org.androad.ui.common.OnClickOnFocusChangedListenerAdapter;
import org.androad.util.constants.Constants;

import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.Toast;


public class SetHomeMap extends OpenStreetMapAndNavBaseActivity {
	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	protected ImageButton ibtnCenter;
	protected ImageButton ibtnToggleSatellite;
	protected ImageButton ibtnClose;
	protected ImageButton ibtnSetHome;
	protected OSMMapViewSingleIconOverlay mSetHomeOverlay;
	protected OpenStreetMapViewSimpleLocationOverlay mMyLocationOverlay;

	protected GeoPoint mHomeLocation;

	protected boolean doAutoCenter = false;

	// ===========================================================
	// Constructors
	// ===========================================================

	@Override
	protected void onSetupContentView() {
		this.setContentView(R.layout.sethome_map);
		super.mOSMapView = (OpenStreetMapView)findViewById(R.id.map_sethome);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle);

		/* Add a new instance of our fancy Overlay-Class to the MapView. */
		final List<OpenStreetMapViewOverlay> overlays = this.mOSMapView.getOverlays();
		overlays.add(this.mSetHomeOverlay = new OSMMapViewSingleIconOverlay(this, R.drawable.home_set, new Point(0,0)));
		overlays.add(this.mMyLocationOverlay = new OpenStreetMapViewSimpleLocationOverlay(this));

		// Load the animation from XML (XML file is res/anim/move_animation.xml).
		final Animation anim = AnimationUtils.loadAnimation(this, R.anim.button_beat);
		anim.setRepeatCount(0);

		this.ibtnCenter = (ImageButton)this.findViewById(R.id.ibtn_sethome_center);
		this.ibtnClose = (ImageButton)this.findViewById(R.id.ibtn_sethome_cancel);
		this.ibtnSetHome = (ImageButton)this.findViewById(R.id.ibtn_sethome_set);
		this.ibtnToggleSatellite = (ImageButton)this.findViewById(R.id.ibtn_sethome_toggle_sattelite);

		this.ibtnSetHome.startAnimation(anim);

		this.applyQuickButtonListeners();
		this.applyZoomButtonListeners();
		this.applyMapViewLongPressListener();

		this.mOSMapView.getController().setZoom(15);

		final GeoPoint mp = Preferences.getHomeGeoPoint(this);
		if(mp != null){
			this.mHomeLocation = mp;
			this.mSetHomeOverlay.setLocation(mp);
			this.mOSMapView.getController().animateTo(mp, AnimationType.MIDDLEPEAKSPEED);
			this.mOSMapView.invalidate();
		}else{
			this.ibtnCenter.startAnimation(anim);
			this.doAutoCenter = true;
			this.ibtnCenter.setImageResource(R.drawable.person_focused_small);
		}
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void release(){
		// Nothing
	}

	@Override
	public void onResume() {
		// TODO onSaveRestoreInstanceState mit evtl gesetztem HOME
		this.setRequestedOrientation(Preferences.getRequestedScreenOrientation(this));
		super.onResume();
	}

	@Override
	public void onLocationLost(final AndNavLocation pLocation) {
		// TODO anzeigen...
	}

	@Override
	public void onLocationChanged(final AndNavLocation pLocation) {
		if(this.mMyLocationOverlay != null){
			this.mMyLocationOverlay.setLocation(pLocation);

			if(this.doAutoCenter){
				this.mOSMapView.getController().animateTo(pLocation, AnimationType.LINEAR);
			}
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================


	protected void applyZoomButtonListeners(){
		this.findViewById(R.id.iv_sethome_map_zoomin).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				SetHomeMap.this.mOSMapView.getController().zoomIn();
			}
		});
		this.findViewById(R.id.iv_sethome_map_zoomout).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				SetHomeMap.this.mOSMapView.getController().zoomOut();
			}
		});
	}

	protected void applyQuickButtonListeners() {
		/* Left side. */
		new OnClickOnFocusChangedListenerAdapter(this.ibtnClose){
			@Override
			public void onClicked(final View arg0) {
				if(SetHomeMap.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SetHomeMap.this, R.raw.close).start();
				}

				SetHomeMap.this.setResult(Constants.SUBACTIVITY_RESULTCODE_UP_ONE_LEVEL);
				SetHomeMap.this.finish();
			}
		};

		/* Right side. */
		new OnClickOnFocusChangedListenerAdapter(this.ibtnToggleSatellite){
			@Override
			public void onClicked(final View arg0) {
				// TODO Show Layers-Menu
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.ibtnSetHome){
			@Override
			public void onClicked(final View arg0) {
				if(SetHomeMap.this.mHomeLocation == null){
					Toast.makeText(SetHomeMap.this, R.string.toast_settings_sethome_via_map_howto, Toast.LENGTH_LONG).show();
				}else{
					if(SetHomeMap.super.mMenuVoiceEnabled) {
						MediaPlayer.create(SetHomeMap.this, R.raw.save).start();
					}

					Preferences.saveHomeGeoPoint(SetHomeMap.this, SetHomeMap.this.mHomeLocation);
					SetHomeMap.this.setResult(Constants.SUBACTIVITY_RESULTCODE_SUCCESS);
					SetHomeMap.this.finish();
				}
			}
		};

		new OnClickOnFocusChangedListenerAdapter(this.ibtnCenter){
			@Override
			public void onClick(final View arg0) {
				SetHomeMap.this.doAutoCenter = !SetHomeMap.this.doAutoCenter;
				if(SetHomeMap.this.doAutoCenter){
					SetHomeMap.this.ibtnCenter.setImageResource(R.drawable.person_focused_small);
					Toast.makeText(SetHomeMap.this, R.string.toast_autofollow_enabled, Toast.LENGTH_SHORT).show();
				}else{
					SetHomeMap.this.ibtnCenter.setImageResource(R.drawable.person_small);
					Toast.makeText(SetHomeMap.this, R.string.toast_autofollow_disabled, Toast.LENGTH_SHORT).show();
				}
				/* Invalidate map. */
				SetHomeMap.this.mOSMapView.invalidate();
			}
		};
	}

	protected void applyMapViewLongPressListener() {

		final GestureDetector gd = new GestureDetector(new GestureDetector.SimpleOnGestureListener(){
			@Override
			public void onLongPress(final MotionEvent mv) {
				final OpenStreetMapView mapView = SetHomeMap.super.mOSMapView; // Drag to local field
				final OpenStreetMapViewProjection pj = mapView.getProjection();
				final GeoPoint mp = pj.fromPixels((int)mv.getX(), (int)mv.getY());

				SetHomeMap.this.mHomeLocation = mp;
				SetHomeMap.this.mSetHomeOverlay.setLocation(mp);
				SetHomeMap.this.mOSMapView.invalidate();
			}
		});
		this.mOSMapView.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(final View v, final MotionEvent ev) {
				return gd.onTouchEvent(ev);
			}
		});
	}

	@Override
	public void onDataStateChanged(final int strength) {
		// TODO ??
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
