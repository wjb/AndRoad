// Created by plusminus on 20:07:18 - 21.05.2008
package org.androad.ui.map;

import java.util.List;

import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapView.Projection;
import org.osmdroid.views.MapController.AnimationType;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayManager;
import org.osmdroid.views.overlay.SimpleLocationOverlay;

import org.androad.R;
import org.androad.adt.AndNavLocation;
import org.androad.preferences.Preferences;
import org.androad.sys.ors.views.overlay.BitmapItem;
import org.androad.sys.ors.views.overlay.BitmapOverlay;
import org.androad.ui.common.OnClickOnFocusChangedListenerAdapter;
import org.androad.util.constants.Constants;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
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
	protected BitmapItem mSetHomeItem;
	protected BitmapOverlay mSetHomeOverlay;
	protected SimpleLocationOverlay mMyLocationOverlay;

	protected GeoPoint mHomeLocation;

	protected boolean doAutoCenter = false;

	// ===========================================================
	// Constructors
	// ===========================================================

	@Override
	protected void onSetupContentView() {
		this.setContentView(R.layout.sethome_map);
		super.mOSMapView = (MapView)findViewById(R.id.map_sethome);
		super.mOSMapView.setTileSource(Preferences.getMapViewProviderInfoWhereAmI(this));
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle);

        this.mSetHomeItem = new BitmapItem(null, this, R.drawable.home_set, null, new Point(0,0));
        this.mSetHomeOverlay = new BitmapOverlay(this, this.mSetHomeItem);

		/* Add a new instance of our fancy Overlay-Class to the MapView. */
		final OverlayManager overlaymanager = this.mOSMapView.getOverlayManager();
		overlaymanager.add(this.mSetHomeOverlay);
		overlaymanager.add(this.mMyLocationOverlay = new SimpleLocationOverlay(this));

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

		GeoPoint mp = Preferences.getHomeGeoPoint(this);
		if(mp != null){
			this.mHomeLocation = mp;
			this.mSetHomeItem.setCenter(mp);
			this.mOSMapView.getController().animateTo(mp, AnimationType.MIDDLEPEAKSPEED);
			this.mOSMapView.invalidate();
		}else{
            mp = this.mMyLocationOverlay.getMyLocation();
			this.mHomeLocation = mp;
			this.mSetHomeItem.setCenter(mp);
			this.mOSMapView.getController().animateTo(mp, AnimationType.MIDDLEPEAKSPEED);
			this.mOSMapView.invalidate();
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
				final ITileSource[] providers = TileSourceFactory.getTileSources().toArray(new ITileSource[0]);

				final SpannableString[] renderersNames = new SpannableString[providers.length];

				for(int j = 0; j < providers.length; j ++){
					final SpannableString itemTitle = new SpannableString(providers[j].name());
					itemTitle.setSpan(new StyleSpan(Typeface.ITALIC), providers[j].name().length(), itemTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					itemTitle.setSpan(new RelativeSizeSpan(0.5f), providers[j].name().length(), itemTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

					renderersNames[j] = itemTitle;
				}

				final int curRendererIndex = SetHomeMap.this.mOSMapView.getTileProvider().getTileSource().ordinal();

				new AlertDialog.Builder(SetHomeMap.this)
				.setTitle(R.string.maps_menu_submenu_renderers)
				.setSingleChoiceItems(renderersNames, curRendererIndex , new DialogInterface.OnClickListener(){
					@Override
					public void onClick(final DialogInterface d, final int which) {
						changeProviderInfo(providers[which]);
						d.dismiss();
					}
				}).create().show();
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
				final MapView mapView = SetHomeMap.super.mOSMapView; // Drag to local field
				final Projection pj = mapView.getProjection();
				final GeoPoint mp = pj.fromPixels((int)mv.getX(), (int)mv.getY());

				SetHomeMap.this.mHomeLocation = mp;
				SetHomeMap.this.mSetHomeItem.setCenter(mp);
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
	private void changeProviderInfo(final ITileSource aProviderInfo) {
		/* Remember changes to the provider to start the next time with the same provider. */
		Preferences.saveMapViewProviderInfoWhereAmI(this, aProviderInfo);

		/* Check if Auto-Follow has to be disabled. */
        super.mOSMapView.setTileSource(aProviderInfo);
	}

}
