// Created by plusminus on 00:09:18 - 04.12.2008
package org.androad.ui.map.overlay;

import org.andnav.osm.views.OpenStreetMapView;
import org.andnav.osm.views.overlay.OpenStreetMapViewOverlay;

import org.androad.preferences.PreferenceConstants;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff.Mode;


public class ColorSchemeOverlay extends OpenStreetMapViewOverlay implements PreferenceConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private int mColorScheme = PREF_THEME_DEFAULT_RESID;
	private final Paint mPaint = new Paint();

	// ===========================================================
	// Constructors
	// ===========================================================
	public ColorSchemeOverlay(final Context ctx) {
        super(ctx);

	}

	public ColorSchemeOverlay(final Context ctx, final int aColorScheme) {
        super(ctx);
		setColorScheme(aColorScheme);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public int getColorScheme() {
		return this.mColorScheme;
	}

	public void setColorScheme(final int aColorScheme) {
		if(aColorScheme == this.mColorScheme) {
			return;
		}

		this.mColorScheme = aColorScheme;

		switch(this.mColorScheme){
			case PREF_THEME_DAY_RESID:
				this.mPaint.setARGB(100, 255, 255, 255);
				this.mPaint.setXfermode(new PorterDuffXfermode(Mode.DARKEN));
				break;
			case PREF_THEME_NIGHT_RESID:
				this.mPaint.setARGB(75, 0, 0, 0);
				this.mPaint.setXfermode(new PorterDuffXfermode(Mode.DARKEN));
				break;
			case PREF_THEME_DEFAULT_RESID:
			default:
				return;
		}
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected void onDraw(final Canvas c, final OpenStreetMapView osmv) {
		switch(this.mColorScheme){
			case PREF_THEME_DAY_RESID:
			case PREF_THEME_NIGHT_RESID:
				c.drawRect(0, 0,700, 700, this.mPaint); // TODO Find getter for these hardcoded values.
				break;
			case PREF_THEME_DEFAULT_RESID:
			default:
				return;
		}
	}

	@Override
	protected void onDrawFinished(final Canvas c, final OpenStreetMapView osmv) {
		return;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
