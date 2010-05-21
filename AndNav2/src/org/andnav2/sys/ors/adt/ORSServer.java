// Created by plusminus on 9:30:48 PM - Feb 28, 2009
package org.andnav2.sys.ors.adt;

import java.io.IOException;

import org.andnav2.sys.ors.adt.lus.Country;
import org.andnav2.util.ping.IPingMethod;
import org.andnav2.util.ping.PingResult;

import android.os.Parcel;
import android.os.Parcelable;


public enum ORSServer implements Parcelable {
	// ===========================================================
	// Elements
	// ===========================================================

	UNIHEIDEL("University of Heidelberg",
			"This server is hosted by the University of Heidelberg, covering whole Europe, with Routing, POIs and Geocoding.",
			"Heidelberg, Germany",
			Country.GERMANY,
			Country.EUROPEANUNION,
			"http://openls.geog.uni-heidelberg.de/route/andnav",
			"http://openls.geog.uni-heidelberg.de/geocode/andnav",
			"http://openls.geog.uni-heidelberg.de/directory/andnav",
			new IPingMethod.HostNamePing("openrouteservice.org"));

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	public final String SERVERNAME;
	public final String SERVERDESCRIPTION;
	public final String LOCATIONNAME;
	public final Country LOCATION;
	public final Country COVERAGE;
	public final String URL_ROUTESERVICE;
	public final String URL_DIRECTORYSERVICE;
	public final String URL_LOCATIONUTILITYSERVICE;
	public final IPingMethod PING;

	// ===========================================================
	// Constructors
	// ===========================================================

	private ORSServer(final String pServerName, final String pServerDescription, final String pLocationName, final Country pLocation, final Country pCoverage, final String pURLRouteService, final String pURLDirectoryService, final String pURLLocationUtilityService, final IPingMethod pPingMethod) {
		this.URL_ROUTESERVICE = pURLRouteService;
		this.URL_DIRECTORYSERVICE = pURLDirectoryService;
		this.URL_LOCATIONUTILITYSERVICE = pURLLocationUtilityService;
		this.LOCATION = pLocation;
		this.LOCATIONNAME = pLocationName;
		this.SERVERNAME = pServerName;
		this.SERVERDESCRIPTION = pServerDescription;
		this.COVERAGE = pCoverage;
		this.PING = pPingMethod;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public static ORSServer getDefault() {
		return UNIHEIDEL;
	}

	public static ORSServer fromName(final String i, final boolean pDefaultFallback) {
		final ORSServer[] servers = values();
		for(final ORSServer s : servers) {
			if(s.name().equals(i)) {
				return s;
			}
		}

		return (pDefaultFallback) ? getDefault() : null;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public PingResult ping() throws IOException {
		return this.PING.ping();
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	public static enum ServerStatus{
		ONLINE, OFFLINE, UNKNOWN;
	}

	// ===========================================================
	// Parcelable
	// ===========================================================

	public static final Parcelable.Creator<ORSServer> CREATOR = new Parcelable.Creator<ORSServer>() {
		public ORSServer createFromParcel(final Parcel in) {
			return readFromParcel(in);
		}

		public ORSServer[] newArray(final int size) {
			return new ORSServer[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel out, final int flags) {
		out.writeInt(this.ordinal());
	}

	private static ORSServer readFromParcel(final Parcel in){
		return values()[in.readInt()];
	}
}
