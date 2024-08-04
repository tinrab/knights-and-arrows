package com.badlogic.gdx.maps.tiled;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapRenderer;

public interface TiledMapRenderer extends MapRenderer {
   void renderObject(MapObject var1);

   void renderTileLayer(TiledMapTileLayer var1);
}
