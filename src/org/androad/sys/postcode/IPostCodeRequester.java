// Created by plusminus on 19:27:39 - 23.11.2008
package org.androad.sys.postcode;

import java.io.IOException;

import org.androad.sys.ors.adt.GeocodedAddress;
import org.androad.sys.ors.exceptions.ORSException;


public interface IPostCodeRequester {
	// ===========================================================
	// Final Fields
	// ===========================================================

	public GeocodedAddress request(final String aPostcode) throws IOException, ORSException;

	// ===========================================================
	// Methods
	// ===========================================================
}
