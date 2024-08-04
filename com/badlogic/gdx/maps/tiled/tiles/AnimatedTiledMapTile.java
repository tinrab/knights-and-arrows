package com.badlogic.gdx.maps.tiled.tiles;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class AnimatedTiledMapTile implements TiledMapTile {
   private static long lastTiledMapRenderTime = 0L;
   private int id;
   private TiledMapTile.BlendMode blendMode;
   private MapProperties properties;
   private Array<StaticTiledMapTile> frameTiles;
   private float animationInterval;
   private long frameCount;
   private static final long initialTimeOffset = TimeUtils.millis();

   public int getId() {
      return this.id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public TiledMapTile.BlendMode getBlendMode() {
      return this.blendMode;
   }

   public void setBlendMode(TiledMapTile.BlendMode blendMode) {
      this.blendMode = blendMode;
   }

   public TextureRegion getTextureRegion() {
      long currentFrame = lastTiledMapRenderTime / (long)(this.animationInterval * 1000.0F) % this.frameCount;
      return ((StaticTiledMapTile)this.frameTiles.get((int)currentFrame)).getTextureRegion();
   }

   public MapProperties getProperties() {
      if (this.properties == null) {
         this.properties = new MapProperties();
      }

      return this.properties;
   }

   public static void updateAnimationBaseTime() {
      lastTiledMapRenderTime = TimeUtils.millis() - initialTimeOffset;
   }

   public AnimatedTiledMapTile(float interval, Array<StaticTiledMapTile> frameTiles) {
      this.blendMode = TiledMapTile.BlendMode.ALPHA;
      this.frameCount = 0L;
      this.frameTiles = frameTiles;
      this.animationInterval = interval;
      this.frameCount = (long)frameTiles.size;
   }
}
