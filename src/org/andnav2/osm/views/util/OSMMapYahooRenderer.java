package org.andnav2.osm.views.util;

import org.andnav.osm.ResourceProxy;
import org.andnav.osm.tileprovider.CloudmadeException;
import org.andnav.osm.tileprovider.IOpenStreetMapTileProviderCallback;
import org.andnav.osm.tileprovider.IOpenStreetMapTileProviderCloudmadeTokenCallback;
import org.andnav.osm.tileprovider.OpenStreetMapTile;
import org.andnav.osm.views.util.OpenStreetMapRendererBase;

public class OSMMapYahooRenderer extends OpenStreetMapRendererBase {

	public OSMMapYahooRenderer(String aName, int aZoomMinLevel,
			int aZoomMaxLevel, int aMaptileZoom, String aImageFilenameEnding,
			String ...aBaseUrl) {
		super(aName, aZoomMinLevel, aZoomMaxLevel, aMaptileZoom, aImageFilenameEnding, aBaseUrl);
	}

	@Override
	public String localizedName(ResourceProxy proxy) {
		return name();
	}

	@Override
	public String getTileURLString(
			OpenStreetMapTile aTile,
			IOpenStreetMapTileProviderCallback aMCallback,
			IOpenStreetMapTileProviderCloudmadeTokenCallback aCloudmadeTokenCallback)
			throws CloudmadeException {
        int zoom = this.zoomMaxLevel() - aTile.getZoomLevel();
        int x = aTile.getX();
        int y = (((1 << (this.zoomMaxLevel() - zoom)) >> 1) - 1 - aTile.getY());
        int z = zoom + 1;
		return new StringBuilder().append(getBaseUrl()).append("x=").append(x)
            .append("&y=").append(y).append("&z=").append(z)
				.toString();
	}

}
