package com.badlogic.gdx.maps.tiled;

import com.badlogic.gdx.maps.MapLayer;

public class TiledMapTileLayer extends MapLayer {
   private int width;
   private int height;
   private float tileWidth;
   private float tileHeight;
   private TiledMapTileLayer.Cell[][] cells;

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public float getTileWidth() {
      return this.tileWidth;
   }

   public float getTileHeight() {
      return this.tileHeight;
   }

   public TiledMapTileLayer(int width, int height, int tileWidth, int tileHeight) {
      this.width = width;
      this.height = height;
      this.tileWidth = (float)tileWidth;
      this.tileHeight = (float)tileHeight;
      this.cells = new TiledMapTileLayer.Cell[width][height];
   }

   public TiledMapTileLayer.Cell getCell(int x, int y) {
      if (x >= 0 && x < this.width) {
         return y >= 0 && y < this.height ? this.cells[x][y] : null;
      } else {
         return null;
      }
   }

   public void setCell(int x, int y, TiledMapTileLayer.Cell cell) {
      if (x >= 0 && x < this.width) {
         if (y >= 0 && y < this.height) {
            this.cells[x][y] = cell;
         }
      }
   }

   public static class Cell {
      private TiledMapTile tile;
      private boolean flipHorizontally;
      private boolean flipVertically;
      private int rotation;
      public static final int ROTATE_0 = 0;
      public static final int ROTATE_90 = 1;
      public static final int ROTATE_180 = 2;
      public static final int ROTATE_270 = 3;

      public TiledMapTile getTile() {
         return this.tile;
      }

      public void setTile(TiledMapTile tile) {
         this.tile = tile;
      }

      public boolean getFlipHorizontally() {
         return this.flipHorizontally;
      }

      public void setFlipHorizontally(boolean flipHorizontally) {
         this.flipHorizontally = flipHorizontally;
      }

      public boolean getFlipVertically() {
         return this.flipVertically;
      }

      public void setFlipVertically(boolean flipVertically) {
         this.flipVertically = flipVertically;
      }

      public int getRotation() {
         return this.rotation;
      }

      public void setRotation(int rotation) {
         this.rotation = rotation;
      }
   }
}
