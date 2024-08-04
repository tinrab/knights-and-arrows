package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TextureRegionDrawable extends BaseDrawable {
   private TextureRegion region;

   public TextureRegionDrawable() {
   }

   public TextureRegionDrawable(TextureRegion region) {
      this.setRegion(region);
   }

   public TextureRegionDrawable(TextureRegionDrawable drawable) {
      super(drawable);
      this.setRegion(drawable.region);
   }

   public void draw(SpriteBatch batch, float x, float y, float width, float height) {
      batch.draw(this.region, x, y, width, height);
   }

   public void setRegion(TextureRegion region) {
      this.region = region;
      this.setMinWidth((float)region.getRegionWidth());
      this.setMinHeight((float)region.getRegionHeight());
   }

   public TextureRegion getRegion() {
      return this.region;
   }
}
