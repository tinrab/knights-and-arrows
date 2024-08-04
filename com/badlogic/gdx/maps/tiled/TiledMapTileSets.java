package com.badlogic.gdx.maps.tiled;

import com.badlogic.gdx.utils.Array;
import java.util.Iterator;

public class TiledMapTileSets implements Iterable<TiledMapTileSet> {
   private Array<TiledMapTileSet> tilesets = new Array();

   public TiledMapTileSet getTileSet(int index) {
      return (TiledMapTileSet)this.tilesets.get(index);
   }

   public TiledMapTileSet getTileSet(String name) {
      Iterator var3 = this.tilesets.iterator();

      while(var3.hasNext()) {
         TiledMapTileSet tileset = (TiledMapTileSet)var3.next();
         if (name.equals(tileset.getName())) {
            return tileset;
         }
      }

      return null;
   }

   public void addTileSet(TiledMapTileSet tileset) {
      this.tilesets.add(tileset);
   }

   public void removeTileSet(int index) {
      this.tilesets.removeIndex(index);
   }

   public void removeTileSet(TiledMapTileSet tileset) {
      this.tilesets.removeValue(tileset, true);
   }

   public TiledMapTile getTile(int id) {
      Iterator var3 = this.tilesets.iterator();

      while(var3.hasNext()) {
         TiledMapTileSet tileset = (TiledMapTileSet)var3.next();
         TiledMapTile tile = tileset.getTile(id);
         if (tile != null) {
            return tile;
         }
      }

      return null;
   }

   public Iterator<TiledMapTileSet> iterator() {
      return this.tilesets.iterator();
   }
}
