package org.androad.osm.views.util;

import org.osmdroid.ResourceProxy;
import org.osmdroid.ResourceProxy.string;
import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;

public class OSMMapYahooRenderer extends OnlineTileSourceBase {

	public OSMMapYahooRenderer(String aName, final string aResourceId, int aZoomMinLevel,
			int aZoomMaxLevel, int aMaptileZoom, String aImageFilenameEnding,
			String ...aBaseUrl) {
		super(aName, aResourceId, aZoomMinLevel, aZoomMaxLevel, aMaptileZoom, aImageFilenameEnding, aBaseUrl);
	}

	@Override
	public String localizedName(ResourceProxy proxy) {
		return name();
	}

	@Override
	public String getTileURLString(MapTile aTile) {
        int zoom = this.getMaximumZoomLevel() - aTile.getZoomLevel();
        int x = aTile.getX();
        int y = (((1 << (this.getMaximumZoomLevel() - zoom)) >> 1) - 1 - aTile.getY());
        int z = zoom + 1;
		return new StringBuilder().append(getBaseUrl()).append("x=").append(x)
            .append("&y=").append(y).append("&z=").append(z)
				.toString();
	}

}
