package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;

public class Widget extends Actor implements Layout {
   private boolean needsLayout = true;
   private boolean fillParent;
   private boolean layoutEnabled = true;

   public float getMinWidth() {
      return this.getPrefWidth();
   }

   public float getMinHeight() {
      return this.getPrefHeight();
   }

   public float getPrefWidth() {
      return 0.0F;
   }

   public float getPrefHeight() {
      return 0.0F;
   }

   public float getMaxWidth() {
      return 0.0F;
   }

   public float getMaxHeight() {
      return 0.0F;
   }

   public void setLayoutEnabled(boolean enabled) {
      this.layoutEnabled = enabled;
      if (enabled) {
         this.invalidateHierarchy();
      }

   }

   public void validate() {
      if (this.layoutEnabled) {
         Group parent = this.getParent();
         if (this.fillParent && parent != null) {
            Stage stage = this.getStage();
            float parentWidth;
            float parentHeight;
            if (stage != null && parent == stage.getRoot()) {
               parentWidth = stage.getWidth();
               parentHeight = stage.getHeight();
            } else {
               parentWidth = parent.getWidth();
               parentHeight = parent.getHeight();
            }

            if (this.getWidth() != parentWidth || this.getHeight() != parentHeight) {
               this.setWidth(parentWidth);
               this.setHeight(parentHeight);
               this.invalidate();
            }
         }

         if (this.needsLayout) {
            this.needsLayout = false;
            this.layout();
         }
      }
   }

   public boolean needsLayout() {
      return this.needsLayout;
   }

   public void invalidate() {
      this.needsLayout = true;
   }

   public void invalidateHierarchy() {
      if (this.layoutEnabled) {
         this.invalidate();
         Group parent = this.getParent();
         if (parent instanceof Layout) {
            ((Layout)parent).invalidateHierarchy();
         }

      }
   }

   public void pack() {
      float newWidth = this.getPrefWidth();
      float newHeight = this.getPrefHeight();
      if (newWidth != this.getWidth() || newHeight != this.getHeight()) {
         this.setWidth(newWidth);
         this.setHeight(newHeight);
         this.invalidate();
      }

      this.validate();
   }

   public void setFillParent(boolean fillParent) {
      this.fillParent = fillParent;
   }

   public void draw(SpriteBatch batch, float parentAlpha) {
      this.validate();
   }

   public void layout() {
   }
}
