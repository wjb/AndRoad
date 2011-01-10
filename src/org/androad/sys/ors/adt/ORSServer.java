// Created by plusminus on 9:30:48 PM - Feb 28, 2009
package org.androad.sys.ors.adt;

import java.io.IOException;

import org.androad.sys.ors.adt.lus.Country;
import org.androad.util.ping.IPingMethod;
import org.androad.util.ping.PingResult;

import org.androad.sys.ors.ds.DSRequester;
import org.androad.sys.ors.ds.google.GoogleDSRequester;
import org.androad.sys.ors.ds.openrouteservice.OpenRouteServiceDSRequester;
import org.androad.sys.ors.ds.yahoo.YahooDSRequester;
import org.androad.sys.ors.ds.yournavigation.YourNavigationDSRequester;
import org.androad.sys.ors.lus.LUSRequester;
import org.androad.sys.ors.lus.google.GoogleLUSRequester;
import org.androad.sys.ors.lus.openrouteservice.OpenRouteServiceLUSRequester;
import org.androad.sys.ors.lus.yahoo.YahooLUSRequester;
import org.androad.sys.ors.lus.yournavigation.YourNavigationLUSRequester;
import org.androad.sys.ors.rs.RSRequester;
import org.androad.sys.ors.rs.google.GoogleRSRequester;
import org.androad.sys.ors.rs.openrouteservice.OpenRouteServiceRSRequester;
import org.androad.sys.ors.rs.yahoo.YahooRSRequester;
import org.androad.sys.ors.rs.yournavigation.YourNavigationRSRequester;

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
	GOOGLE("Google Navigation",
          "This server is the google navigation. You are not authorised to use it through AndRoad. This is just a test.",
          "USA",
          Country.USA,
          Country.USA,
          new GoogleRSRequester(),
          new GoogleDSRequester(),
          new GoogleLUSRequester(),
          new IPingMethod.HostNamePing("google.com")),
	YAHOO("Yahoo Navigation",
          "This server is the yahoo navigation. You are not authorised to use it through AndRoad. This is just a test.",
          "USA",
          Country.USA,
          Country.USA,
          new YahooRSRequester(),
          new YahooDSRequester(),
          new YahooLUSRequester(),
          new IPingMethod.HostNamePing("maps.yahoo.com")),
	YOURNAVIGATION("Your Navigation",
          "This server is the your navigation.",
          "USA",
          Country.USA,
          Country.USA,
          new YourNavigationRSRequester(),
          new YourNavigationDSRequester(),
          new YourNavigationLUSRequester(),
          new IPingMethod.HostNamePing("yournavigation.org"));

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
