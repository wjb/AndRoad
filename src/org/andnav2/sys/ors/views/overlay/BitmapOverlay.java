package org.andnav2.sys.ors.views.overlay;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.andnav2.osm.views.OSMMapView;
import org.andnav2.osm.views.OSMMapView.OSMMapViewProjection;
import org.andnav2.osm.views.overlay.OSMMapViewOverlay;

import android.graphics.Canvas;
import android.view.MotionEvent;

public class BitmapOverlay extends OSMMapViewOverlay{
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected final List<BitmapItem> mBIs;

	// ===========================================================
	// Constructors
	// ===========================================================

	public BitmapOverlay(){
		this(new ArrayList<BitmapItem>());
	}

	public BitmapOverlay(final BitmapItem pItem){
		this.mBIs = new ArrayList<BitmapItem>();
		this.mBIs.add(pItem);
	}

	public BitmapOverlay(final ArrayList<BitmapItem> pItems){
		Assert.assertNotNull(pItems);
		this.mBIs = pItems;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public List<BitmapItem> getBitmapItems(){
		return this.mBIs;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void release() {
		this.mBIs.clear();
	}

	@Override
	protected void onDraw(final Canvas c, final OSMMapView osmv) {
		final OSMMapViewProjection pj = osmv.getProjection();

		final int limit = this.mBIs.size();
		for(int i = 0; i < limit; i++){
			final BitmapItem a = this.mBIs.get(i);
			a.drawToCanvas(c, pj);
		}
	}

	@Override
	protected void onDrawFinished(final Canvas c, final OSMMapView osmv) {
		// Nothing
	}

    @Override
    public boolean onSingleTapUp(final MotionEvent e, final OSMMapView openStreetMapView) {
        final OSMMapViewProjection pj = openStreetMapView.getProjection();

        final int limit = this.mBIs.size();
        for(int i = 0; i < limit; i++){
            final BitmapItem a = this.mBIs.get(i);
            if (a.onSingleTapUp(e, pj))
                return true;
        }

        return false;
    }

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
