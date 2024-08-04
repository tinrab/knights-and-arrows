package com.badlogic.gdx.maps.tiled;

import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import java.util.Iterator;

public class TiledMap extends Map {
   private TiledMapTileSets tilesets = new TiledMapTileSets();
   private Array<? extends Disposable> ownedResources;

   public TiledMapTileSets getTileSets() {
      return this.tilesets;
   }

   public void setOwnedResources(Array<? extends Disposable> resources) {
      this.ownedResources = resources;
   }

   public void dispose() {
      if (this.ownedResources != null) {
         Iterator var2 = this.ownedResources.iterator();

         while(var2.hasNext()) {
            Disposable resource = (Disposable)var2.next();
            resource.dispose();
         }
      }

   }
}
