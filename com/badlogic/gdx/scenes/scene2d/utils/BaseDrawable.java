package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class BaseDrawable implements Drawable {
   private float leftWidth;
   private float rightWidth;
   private float topHeight;
   private float bottomHeight;
   private float minWidth;
   private float minHeight;

   public BaseDrawable() {
   }

   public BaseDrawable(Drawable drawable) {
      this.leftWidth = drawable.getLeftWidth();
      this.rightWidth = drawable.getRightWidth();
      this.topHeight = drawable.getTopHeight();
      this.bottomHeight = drawable.getBottomHeight();
      this.minWidth = drawable.getMinWidth();
      this.minHeight = drawable.getMinHeight();
   }

   public void draw(SpriteBatch batch, float x, float y, float width, float height) {
   }

   public float getLeftWidth() {
      return this.leftWidth;
   }

   public void setLeftWidth(float leftWidth) {
      this.leftWidth = leftWidth;
   }

   public float getRightWidth() {
      return this.rightWidth;
   }

   public void setRightWidth(float rightWidth) {
      this.rightWidth = rightWidth;
   }

   public float getTopHeight() {
      return this.topHeight;
   }

   public void setTopHeight(float topHeight) {
      this.topHeight = topHeight;
   }

   public float getBottomHeight() {
      return this.bottomHeight;
   }

   public void setBottomHeight(float bottomHeight) {
      this.bottomHeight = bottomHeight;
   }

   public float getMinWidth() {
      return this.minWidth;
   }

   public void setMinWidth(float minWidth) {
      this.minWidth = minWidth;
   }

   public float getMinHeight() {
      return this.minHeight;
   }

   public void setMinHeight(float minHeight) {
      this.minHeight = minHeight;
   }
}
