package org.androad.sys.ors.views.overlay;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.osmdroid.views.MapView;
import org.osmdroid.views.MapView.Projection;
import org.osmdroid.views.overlay.Overlay;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;

public class BitmapOverlay extends Overlay {
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
        for (BitmapItem bi : this.mBIs) {
            bi.release();
        }
		this.mBIs.clear();
	}

	@Override
	protected void draw(final Canvas c, final MapView osmv, final boolean shadow) {
		final Projection pj = osmv.getProjection();

		final int limit = this.mBIs.size();
		for(int i = 0; i < limit; i++){
			final BitmapItem a = this.mBIs.get(i);
			a.drawToCanvas(c, pj);
		}
	}

    @Override
    public boolean onSingleTapUp(final MotionEvent e, final MapView MapView) {
        final Projection pj = MapView.getProjection();

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
