// Created by plusminus on 21:38:10 - 16.10.2008
package org.androad.sys.ors.adt;


public enum AvoidFeature {
	HIGHWAY("Highway"), TOLLWAY("Tollway");

	public final String mDefiniton;
	private AvoidFeature(final String s){
		this.mDefiniton = s;
	}
}
