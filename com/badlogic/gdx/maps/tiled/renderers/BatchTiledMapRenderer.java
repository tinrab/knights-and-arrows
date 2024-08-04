package com.badlogic.gdx.maps.tiled.renderers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import java.util.Iterator;

public abstract class BatchTiledMapRenderer implements TiledMapRenderer, Disposable {
   protected TiledMap map;
   protected float unitScale;
   protected SpriteBatch spriteBatch;
   protected Rectangle viewBounds;
   protected boolean ownsSpriteBatch;

   public TiledMap getMap() {
      return this.map;
   }

   public void setMap(TiledMap map) {
      this.map = map;
   }

   public float getUnitScale() {
      return this.unitScale;
   }

   public SpriteBatch getSpriteBatch() {
      return this.spriteBatch;
   }

   public Rectangle getViewBounds() {
      return this.viewBounds;
   }

   public BatchTiledMapRenderer(TiledMap map) {
      this(map, 1.0F);
   }

   public BatchTiledMapRenderer(TiledMap map, float unitScale) {
      this.map = map;
      this.unitScale = unitScale;
      this.viewBounds = new Rectangle();
      this.spriteBatch = new SpriteBatch();
      this.ownsSpriteBatch = true;
   }

   public BatchTiledMapRenderer(TiledMap map, SpriteBatch spriteBatch) {
      this(map, 1.0F, spriteBatch);
   }

   public BatchTiledMapRenderer(TiledMap map, float unitScale, SpriteBatch spriteBatch) {
      this.map = map;
      this.unitScale = unitScale;
      this.viewBounds = new Rectangle();
      this.spriteBatch = spriteBatch;
      this.ownsSpriteBatch = false;
   }

   public void setView(OrthographicCamera camera) {
      this.spriteBatch.setProjectionMatrix(camera.combined);
      float width = camera.viewportWidth * camera.zoom;
      float height = camera.viewportHeight * camera.zoom;
      this.viewBounds.set(camera.position.x - width / 2.0F, camera.position.y - height / 2.0F, width, height);
   }

   public void setView(Matrix4 projection, float x, float y, float width, float height) {
      this.spriteBatch.setProjectionMatrix(projection);
      this.viewBounds.set(x, y, width, height);
   }

   public void render() {
      AnimatedTiledMapTile.updateAnimationBaseTime();
      this.spriteBatch.begin();
      Iterator var2 = this.map.getLayers().iterator();

      while(true) {
         while(true) {
            MapLayer layer;
            do {
               if (!var2.hasNext()) {
                  this.spriteBatch.end();
                  return;
               }

               layer = (MapLayer)var2.next();
            } while(!layer.isVisible());

            if (layer instanceof TiledMapTileLayer) {
               this.renderTileLayer((TiledMapTileLayer)layer);
            } else {
               Iterator var4 = layer.getObjects().iterator();

               while(var4.hasNext()) {
                  MapObject object = (MapObject)var4.next();
                  this.renderObject(object);
               }
            }
         }
      }
   }

   public void render(int[] layers) {
      AnimatedTiledMapTile.updateAnimationBaseTime();
      this.spriteBatch.begin();
      int[] var5 = layers;
      int var4 = layers.length;

      for(int var3 = 0; var3 < var4; ++var3) {
         int layerIdx = var5[var3];
         MapLayer layer = this.map.getLayers().get(layerIdx);
         if (layer.isVisible()) {
            if (layer instanceof TiledMapTileLayer) {
               this.renderTileLayer((TiledMapTileLayer)layer);
            } else {
               Iterator var8 = layer.getObjects().iterator();

               while(var8.hasNext()) {
                  MapObject object = (MapObject)var8.next();
                  this.renderObject(object);
               }
            }
         }
      }

      this.spriteBatch.end();
   }

   public void dispose() {
      if (this.ownsSpriteBatch) {
         this.spriteBatch.dispose();
      }

   }
}
