// Created by plusminus on 00:52:21 - 12.11.2008
package org.androad.sys.ors.views.overlay;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.andnav.osm.views.OpenStreetMapView;
import org.andnav.osm.views.OpenStreetMapView.OpenStreetMapViewProjection;
import org.andnav.osm.views.overlay.OpenStreetMapViewOverlay;

import org.androad.sys.ors.adt.aoi.AreaOfInterest;

import android.content.Context;
import android.graphics.Canvas;

public class AreaOfInterestOverlay extends OpenStreetMapViewOverlay {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected final List<AreaOfInterest> mAOIs;
	protected int mDrawnAreasLimit = Integer.MAX_VALUE;

	// ===========================================================
	// Constructors
	// ===========================================================

	public AreaOfInterestOverlay(final Context ctx){
		this(ctx, new ArrayList<AreaOfInterest>());
	}

	public AreaOfInterestOverlay(final Context ctx, final AreaOfInterest pAOI){
        super(ctx);
		this.mAOIs = new ArrayList<AreaOfInterest>();
		this.mAOIs.add(pAOI);
	}

	public AreaOfInterestOverlay(final Context ctx, final ArrayList<AreaOfInterest> pAOIs){
        super(ctx);
		Assert.assertNotNull(pAOIs);
		this.mAOIs = pAOIs;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public List<AreaOfInterest> getAreasOfInterest(){
		return this.mAOIs;
	}

	public int getDrawnAreasLimit() {
		return this.mDrawnAreasLimit;
	}

	public void setDrawnAreasLimit(final int aLimit) {
		this.mDrawnAreasLimit = aLimit;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	public void release() {
		this.mAOIs.clear();
	}

	@Override
	protected void onDraw(final Canvas c, final OpenStreetMapView osmv) {
		final OpenStreetMapViewProjection pj = osmv.getProjection();

		final int limit = Math.min(this.mDrawnAreasLimit, this.mAOIs.size());
		for(int i = 0; i < limit; i++){
			final AreaOfInterest a = this.mAOIs.get(i);
			a.drawToCanvas(c, pj);
		}
	}

	@Override
	protected void onDrawFinished(final Canvas c, final OpenStreetMapView osmv) {
		// Nothing
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
