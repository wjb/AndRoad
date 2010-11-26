package org.androad.sys.ors.views.overlay;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.andnav.osm.views.OpenStreetMapView;
import org.andnav.osm.views.OpenStreetMapView.OpenStreetMapViewProjection;
import org.andnav.osm.views.overlay.OpenStreetMapViewOverlay;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;

public class BitmapOverlay extends OpenStreetMapViewOverlay {
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

	public BitmapOverlay(final Context ctx){
		this(ctx, new ArrayList<BitmapItem>());
	}

	public BitmapOverlay(final Context ctx, final BitmapItem pItem){
        super(ctx);
		this.mBIs = new ArrayList<BitmapItem>();
		this.mBIs.add(pItem);
	}

	public BitmapOverlay(final Context ctx, final ArrayList<BitmapItem> pItems){
        super(ctx);
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

	public void release() {
		this.mBIs.clear();
	}

	@Override
	protected void onDraw(final Canvas c, final OpenStreetMapView osmv) {
		final OpenStreetMapViewProjection pj = osmv.getProjection();

		final int limit = this.mBIs.size();
		for(int i = 0; i < limit; i++){
			final BitmapItem a = this.mBIs.get(i);
			a.drawToCanvas(c, pj);
		}
	}

	@Override
	protected void onDrawFinished(final Canvas c, final OpenStreetMapView osmv) {
		// Nothing
	}

    @Override
    public boolean onSingleTapUp(final MotionEvent e, final OpenStreetMapView openStreetMapView) {
        final OpenStreetMapViewProjection pj = openStreetMapView.getProjection();

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
