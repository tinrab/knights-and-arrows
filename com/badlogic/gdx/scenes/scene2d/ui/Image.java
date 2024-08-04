package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;

public class Image extends Widget {
   private Scaling scaling;
   private int align;
   private float imageX;
   private float imageY;
   private float imageWidth;
   private float imageHeight;
   private Drawable drawable;

   public Image() {
      this((Drawable)null);
   }

   public Image(NinePatch patch) {
      this(new NinePatchDrawable(patch), Scaling.stretch, 1);
   }

   public Image(TextureRegion region) {
      this(new TextureRegionDrawable(region), Scaling.stretch, 1);
   }

   public Image(Texture texture) {
      this((Drawable)(new TextureRegionDrawable(new TextureRegion(texture))));
   }

   public Image(Skin skin, String drawableName) {
      this(skin.getDrawable(drawableName), Scaling.stretch, 1);
   }

   public Image(Drawable drawable) {
      this(drawable, Scaling.stretch, 1);
   }

   public Image(Drawable drawable, Scaling scaling) {
      this(drawable, scaling, 1);
   }

   public Image(Drawable drawable, Scaling scaling, int align) {
      this.align = 1;
      this.setDrawable(drawable);
      this.scaling = scaling;
      this.align = align;
      this.setWidth(this.getPrefWidth());
      this.setHeight(this.getPrefHeight());
   }

   public void layout() {
      if (this.drawable != null) {
         float regionWidth = this.drawable.getMinWidth();
         float regionHeight = this.drawable.getMinHeight();
         float width = this.getWidth();
         float height = this.getHeight();
         Vector2 size = this.scaling.apply(regionWidth, regionHeight, width, height);
         this.imageWidth = size.x;
         this.imageHeight = size.y;
         if ((this.align & 8) != 0) {
            this.imageX = 0.0F;
         } else if ((this.align & 16) != 0) {
            this.imageX = (float)((int)(width - this.imageWidth));
         } else {
            this.imageX = (float)((int)(width / 2.0F - this.imageWidth / 2.0F));
         }

         if ((this.align & 2) != 0) {
            this.imageY = (float)((int)(height - this.imageHeight));
         } else if ((this.align & 4) != 0) {
            this.imageY = 0.0F;
         } else {
            this.imageY = (float)((int)(height / 2.0F - this.imageHeight / 2.0F));
         }

      }
   }

   public void draw(SpriteBatch batch, float parentAlpha) {
      this.validate();
      Color color = this.getColor();
      batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
      float x = this.getX();
      float y = this.getY();
      float scaleX = this.getScaleX();
      float scaleY = this.getScaleY();
      if (this.drawable != null) {
         if (this.drawable.getClass() == TextureRegionDrawable.class) {
            TextureRegion region = ((TextureRegionDrawable)this.drawable).getRegion();
            float rotation = this.getRotation();
            if (scaleX == 1.0F && scaleY == 1.0F && rotation == 0.0F) {
               batch.draw(region, x + this.imageX, y + this.imageY, this.imageWidth, this.imageHeight);
            } else {
               batch.draw(region, x + this.imageX, y + this.imageY, this.getOriginX() - this.imageX, this.getOriginY() - this.imageY, this.imageWidth, this.imageHeight, scaleX, scaleY, rotation);
            }
         } else {
            this.drawable.draw(batch, x + this.imageX, y + this.imageY, this.imageWidth * scaleX, this.imageHeight * scaleY);
         }
      }

   }

   public void setDrawable(Drawable drawable) {
      if (drawable != null) {
         if (this.drawable == drawable) {
            return;
         }

         if (this.getPrefWidth() != drawable.getMinWidth() || this.getPrefHeight() != drawable.getMinHeight()) {
            this.invalidateHierarchy();
         }
      } else if (this.getPrefWidth() != 0.0F || this.getPrefHeight() != 0.0F) {
         this.invalidateHierarchy();
      }

      this.drawable = drawable;
   }

   public Drawable getDrawable() {
      return this.drawable;
   }

   public void setScaling(Scaling scaling) {
      if (scaling == null) {
         throw new IllegalArgumentException("scaling cannot be null.");
      } else {
         this.scaling = scaling;
      }
   }

   public void setAlign(int align) {
      this.align = align;
   }

   public float getMinWidth() {
      return 0.0F;
   }

   public float getMinHeight() {
      return 0.0F;
   }

   public float getPrefWidth() {
      return this.drawable != null ? this.drawable.getMinWidth() : 0.0F;
   }

   public float getPrefHeight() {
      return this.drawable != null ? this.drawable.getMinHeight() : 0.0F;
   }

   public float getImageX() {
      return this.imageX;
   }

   public float getImageY() {
      return this.imageY;
   }

   public float getImageWidth() {
      return this.imageWidth;
   }

   public float getImageHeight() {
      return this.imageHeight;
   }
}
