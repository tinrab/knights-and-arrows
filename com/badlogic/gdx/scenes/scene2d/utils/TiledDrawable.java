package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TiledDrawable extends TextureRegionDrawable {
   public TiledDrawable() {
   }

   public TiledDrawable(TextureRegion region) {
      super(region);
   }

   public TiledDrawable(TextureRegionDrawable drawable) {
      super(drawable);
   }

   public void draw(SpriteBatch batch, float x, float y, float width, float height) {
      TextureRegion region = this.getRegion();
      float regionWidth = (float)region.getRegionWidth();
      float regionHeight = (float)region.getRegionHeight();
      float remainingX = width % regionWidth;
      float remainingY = height % regionHeight;
      float startX = x;
      float startY = y;
      float endX = x + width - remainingX;

      float endY;
      for(endY = y + height - remainingY; x < endX; x += regionWidth) {
         for(y = startY; y < endY; y += regionHeight) {
            batch.draw(region, x, y, regionWidth, regionHeight);
         }
      }

      Texture texture = region.getTexture();
      float u = region.getU();
      float v2 = region.getV2();
      float u2;
      float v;
      if (remainingX > 0.0F) {
         u2 = u + remainingX / (float)texture.getWidth();
         v = region.getV();

         for(y = startY; y < endY; y += regionHeight) {
            batch.draw(texture, x, y, remainingX, regionHeight, u, v2, u2, v);
         }

         if (remainingY > 0.0F) {
            v = v2 - remainingY / (float)texture.getHeight();
            batch.draw(texture, x, y, remainingX, remainingY, u, v2, u2, v);
         }
      }

      if (remainingY > 0.0F) {
         u2 = region.getU2();
         v = v2 - remainingY / (float)texture.getHeight();

         for(x = startX; x < endX; x += regionWidth) {
            batch.draw(texture, x, y, regionWidth, remainingY, u, v2, u2, v);
         }
      }

   }
}
