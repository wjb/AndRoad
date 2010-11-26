// Created by plusminus on 23:42:39 - 17.12.2008
package org.androad.sound.tts;

import org.androad.sound.tts.speechimprovement.countries.SpeechImproverDE;
import org.androad.sound.tts.speechimprovement.countries.SpeechImproverUS;
import org.androad.sys.ors.adt.lus.Country;


/**
 * @author Nicolas Gramlich
 *
 */
public class SpeechImprover {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public static String improve(final String pTurnDescription, final Country pRouteCountry){
		switch(pRouteCountry){
			case USA:
				return SpeechImproverUS.improve(pTurnDescription);
			case GERMANY:
				return SpeechImproverDE.improve(pTurnDescription);
			default:
				return pTurnDescription;
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
