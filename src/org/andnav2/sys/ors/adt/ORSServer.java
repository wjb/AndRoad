// Created by plusminus on 9:30:48 PM - Feb 28, 2009
package org.andnav2.sys.ors.adt;

import java.io.IOException;

import org.andnav2.sys.ors.adt.lus.Country;
import org.andnav2.util.ping.IPingMethod;
import org.andnav2.util.ping.PingResult;

import org.andnav2.sys.ors.ds.DSRequester;
import org.andnav2.sys.ors.ds.openrouteservice.OpenRouteServiceDSRequester;
import org.andnav2.sys.ors.ds.yahoo.YahooDSRequester;
import org.andnav2.sys.ors.lus.LUSRequester;
import org.andnav2.sys.ors.lus.openrouteservice.OpenRouteServiceLUSRequester;
import org.andnav2.sys.ors.rs.RSRequester;
import org.andnav2.sys.ors.rs.openrouteservice.OpenRouteServiceRSRequester;
import org.andnav2.sys.ors.rs.yahoo.YahooRSRequester;

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
              new OpenRouteServiceRSRequester("http://openls.geog.uni-heidelberg.de/route/andnav"),
              new OpenRouteServiceDSRequester("http://openls.geog.uni-heidelberg.de/directory/andnav"),
              new OpenRouteServiceLUSRequester("http://openls.geog.uni-heidelberg.de/geocode/andnav"),
              new IPingMethod.HostNamePing("openls.geog.uni-heidelberg.de")),
	YAHOO("Yahoo Navigation",
          "This server is the yahoo navigation. You are not authorised to use it through AndNav. This is just a test.",
          "USA",
          Country.USA,
          Country.USA,
          new YahooRSRequester(),
          new YahooDSRequester(),
          null,
          new IPingMethod.HostNamePing("maps.yahoo.com"));

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
	public final RSRequester ROUTESERVICE;
	public final DSRequester DIRECTORYSERVICE;
	public final LUSRequester LOCATIONUTILITYSERVICE;
	public final IPingMethod PING;

	// ===========================================================
	// Constructors
	// ===========================================================

	private ORSServer(final String pServerName, final String pServerDescription, final String pLocationName, final Country pLocation, final Country pCoverage, final RSRequester pRouteService, final DSRequester pDirectoryService, final LUSRequester pLocationUtilityService, final IPingMethod pPingMethod) {
		this.ROUTESERVICE = pRouteService;
		this.DIRECTORYSERVICE = pDirectoryService;
		this.LOCATIONUTILITYSERVICE = pLocationUtilityService;
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
