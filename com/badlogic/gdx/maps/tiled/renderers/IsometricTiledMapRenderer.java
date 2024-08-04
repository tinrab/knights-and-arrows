package com.badlogic.gdx.maps.tiled.renderers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

public class IsometricTiledMapRenderer extends BatchTiledMapRenderer {
   private TiledMap map;
   private float[] vertices = new float[20];

   public IsometricTiledMapRenderer(TiledMap map) {
      super(map);
   }

   public IsometricTiledMapRenderer(TiledMap map, SpriteBatch spriteBatch) {
      super(map, spriteBatch);
   }

   public IsometricTiledMapRenderer(TiledMap map, float unitScale) {
      super(map, unitScale);
   }

   public IsometricTiledMapRenderer(TiledMap map, float unitScale, SpriteBatch spriteBatch) {
      super(map, unitScale, spriteBatch);
   }

   public void renderObject(MapObject object) {
   }

   public void renderTileLayer(TiledMapTileLayer layer) {
      Color batchColor = this.spriteBatch.getColor();
      float color = Color.toFloatBits(batchColor.r, batchColor.g, batchColor.b, batchColor.a * layer.getOpacity());
      int col1 = 0;
      int col2 = layer.getWidth() - 1;
      int row1 = 0;
      int row2 = layer.getHeight() - 1;
      float tileWidth = layer.getTileWidth() * this.unitScale;
      float tileHeight = layer.getTileHeight() * this.unitScale;
      float halfTileWidth = tileWidth * 0.5F;
      float halfTileHeight = tileHeight * 0.5F;

      for(int row = row2; row >= row1; --row) {
         for(int col = col1; col <= col2; ++col) {
            float x = (float)col * halfTileWidth + (float)row * halfTileWidth;
            float y = (float)row * halfTileHeight - (float)col * halfTileHeight;
            TiledMapTileLayer.Cell cell = layer.getCell(col, row);
            if (cell != null) {
               TiledMapTile tile = cell.getTile();
               if (tile != null) {
                  boolean flipX = cell.getFlipHorizontally();
                  boolean flipY = cell.getFlipVertically();
                  int rotations = cell.getRotation();
                  TextureRegion region = tile.getTextureRegion();
                  float x2 = x + (float)region.getRegionWidth() * this.unitScale;
                  float y2 = y + (float)region.getRegionHeight() * this.unitScale;
                  float u1 = region.getU();
                  float v1 = region.getV2();
                  float u2 = region.getU2();
                  float v2 = region.getV();
                  this.vertices[0] = x;
                  this.vertices[1] = y;
                  this.vertices[2] = color;
                  this.vertices[3] = u1;
                  this.vertices[4] = v1;
                  this.vertices[5] = x;
                  this.vertices[6] = y2;
                  this.vertices[7] = color;
                  this.vertices[8] = u1;
                  this.vertices[9] = v2;
                  this.vertices[10] = x2;
                  this.vertices[11] = y2;
                  this.vertices[12] = color;
                  this.vertices[13] = u2;
                  this.vertices[14] = v2;
                  this.vertices[15] = x2;
                  this.vertices[16] = y;
                  this.vertices[17] = color;
                  this.vertices[18] = u2;
                  this.vertices[19] = v1;
                  float tempV;
                  if (flipX) {
                     tempV = this.vertices[3];
                     this.vertices[3] = this.vertices[13];
                     this.vertices[13] = tempV;
                     tempV = this.vertices[8];
                     this.vertices[8] = this.vertices[18];
                     this.vertices[18] = tempV;
                  }

                  if (flipY) {
                     tempV = this.vertices[4];
                     this.vertices[4] = this.vertices[14];
                     this.vertices[14] = tempV;
                     tempV = this.vertices[9];
                     this.vertices[9] = this.vertices[19];
                     this.vertices[19] = tempV;
                  }

                  if (rotations != 0) {
                     float tempU;
                     switch(rotations) {
                     case 1:
                        tempV = this.vertices[4];
                        this.vertices[4] = this.vertices[9];
                        this.vertices[9] = this.vertices[14];
                        this.vertices[14] = this.vertices[19];
                        this.vertices[19] = tempV;
                        tempU = this.vertices[3];
                        this.vertices[3] = this.vertices[8];
                        this.vertices[8] = this.vertices[13];
                        this.vertices[13] = this.vertices[18];
                        this.vertices[18] = tempU;
                        break;
                     case 2:
                        tempV = this.vertices[3];
                        this.vertices[3] = this.vertices[13];
                        this.vertices[13] = tempV;
                        tempV = this.vertices[8];
                        this.vertices[8] = this.vertices[18];
                        this.vertices[18] = tempV;
                        tempU = this.vertices[4];
                        this.vertices[4] = this.vertices[14];
                        this.vertices[14] = tempU;
                        tempU = this.vertices[9];
                        this.vertices[9] = this.vertices[19];
                        this.vertices[19] = tempU;
                        break;
                     case 3:
                        tempV = this.vertices[4];
                        this.vertices[4] = this.vertices[19];
                        this.vertices[19] = this.vertices[14];
                        this.vertices[14] = this.vertices[9];
                        this.vertices[9] = tempV;
                        tempU = this.vertices[3];
                        this.vertices[3] = this.vertices[18];
                        this.vertices[18] = this.vertices[13];
                        this.vertices[13] = this.vertices[8];
                        this.vertices[8] = tempU;
                     }
                  }

                  this.spriteBatch.draw(region.getTexture(), this.vertices, 0, 20);
               }
            }
         }
      }

   }
}
