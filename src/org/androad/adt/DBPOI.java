// Created by plusminus on 22:00:47 - 30.10.2008
package org.androad.adt;

import org.osmdroid.util.GeoPoint;

public class DBPOI extends GeoPoint {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

    protected long mId;
	protected String mName;
	protected String mEnName;
	protected String mType;
	protected String mSubType;
	protected String mOpeningHours;
	protected String mPhone;
	protected String mSite;

	// ===========================================================
	// Constructors
	// ===========================================================

	public DBPOI(final String pName, final GeoPoint pGeoPoint){
		super(pGeoPoint);
		this.mName = pName;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public long getId() {
		return this.mId;
	}

	public String getName() {
		return this.mName;
	}

	public String getEnName() {
		return this.mEnName;
	}

	public String getType() {
		return this.mType;
	}

	public String getSubType() {
		return this.mSubType;
	}

	public String getOpeningHours() {
		return this.mOpeningHours;
	}

	public String getPhone() {
		return this.mPhone;
	}

	public String getSite() {
		return this.mSite;
	}

	public void setId(long l) {
		this.mId = l;
	}

	public void setName(String s) {
		this.mName = s;
	}

	public void setEnName(String s) {
		this.mEnName = s;
	}

	public void setType(String s) {
		this.mType = s;
	}

	public void setSubType(String s) {
		this.mSubType = s;
	}

	public void setOpeningHours(String s) {
		this.mOpeningHours = s;
	}

	public void setPhone(String s) {
		this.mPhone = s;
	}

	public void setSite(String s) {
		this.mSite = s;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
