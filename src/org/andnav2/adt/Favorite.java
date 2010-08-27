// Created by plusminus on 14:31:57 - 15.02.2008
package org.andnav2.adt;

import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.osm.util.constants.OSMConstants;

public class Favorite extends GeoPoint{

	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

    protected final long id;
	protected final String mName;
	protected final int mUses;

	// ===========================================================
	// Constructors
	// ===========================================================

	public Favorite(final long id, final String aName, final int aLatitude, final int aLongitude, final int aUses) {
		super(aLatitude, aLongitude);
        this.id = id;
		this.mName = aName;
		this.mUses = aUses;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public long getId() {
		return this.id;
	}

	public String getName() {
		return this.mName;
	}

    public String getPhotoFilename() {
        final String favoriteFolderPath = org.andnav2.osm.util.Util.getAndNavExternalStoragePath() + OSMConstants.SDCARD_SAVEDFAVORITES_PATH;
        return favoriteFolderPath + this.id + ".jpg";
    }

	public String getFullString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("Id: ");
		sb.append(this.id);
		sb.append("    Name: ");
		sb.append(this.mName);
		sb.append("    Lat: ");
		sb.append(this.mLatitudeE6);
		sb.append("  Lng: ");
		sb.append(this.mLongitudeE6);
		return sb.toString();
	}


	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public String toString() {
		return new StringBuilder()
		.append(this.mName)
		.append(" (#")
		.append(this.mUses)
		.append(")")
		.toString();
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
