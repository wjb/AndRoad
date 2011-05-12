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

public class CircleOverlay extends Overlay {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected final List<CircleItem> mCIs;

	// ===========================================================
	// Constructors
	// ===========================================================

	public CircleOverlay(final Context ctx){
		this(ctx, new ArrayList<CircleItem>());
	}

	public CircleOverlay(final Context ctx, final CircleItem pItem){
        super(ctx);
		this.mCIs = new ArrayList<CircleItem>();
		this.mCIs.add(pItem);
	}

	public CircleOverlay(final Context ctx, final ArrayList<CircleItem> pItems){
        super(ctx);
		Assert.assertNotNull(pItems);
		this.mCIs = pItems;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public List<CircleItem> getCircleItems(){
		return this.mCIs;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected void onDraw(final Canvas c, final MapView osmv) {
		final Projection pj = osmv.getProjection();

		final int limit = this.mCIs.size();
		for(int i = 0; i < limit; i++){
			final CircleItem a = this.mCIs.get(i);
			a.drawToCanvas(c, pj);
		}
	}

	@Override
	protected void onDrawFinished(final Canvas c, final MapView osmv) {
		// Nothing
	}

    @Override
    public boolean onSingleTapUp(final MotionEvent e, final MapView MapView) {
        final Projection pj = MapView.getProjection();

        final int limit = this.mCIs.size();
        for(int i = 0; i < limit; i++){
            final CircleItem a = this.mCIs.get(i);
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
