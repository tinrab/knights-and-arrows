package com.minild44.www.util;

import com.badlogic.gdx.graphics.Color;

public class Light {
   private static final float STEP = 0.3926991F;
   private float x;
   private float y;
   public Color color;
   public float fraction = 1.0F;
   public float flickerSpeed;
   public float flickerSize;
   private boolean active = true;
   private boolean removed;
   public boolean castShadows = true;
   public boolean temp;
   public int mask;
   private float duration;

   public Light(float x, float y, Color color) {
      this.x = x;
      this.y = y;
      this.color = color;
   }

   public void update(float delta) {
      if (this.temp) {
         this.duration -= delta;
         if (this.duration <= 0.0F) {
            this.active = false;
         }
      }

      if (this.flickerSize > 0.0F) {
         this.fraction = 1.0F - this.flickerSize + (float)Math.sin((double)((float)System.nanoTime() * 2.0E-8F * this.flickerSpeed + 0.3926991F * this.x * this.y * 0.001F)) * this.flickerSize;
      }

   }

   public void setActive(boolean b) {
      this.active = b;
   }

   public boolean isActive() {
      return this.active;
   }

   public void remove() {
      this.removed = true;
   }

   public boolean wasRemoved() {
      return this.removed;
   }

   public float getX() {
      return this.x;
   }

   public float getY() {
      return this.y;
   }

   public void set(float x, float y) {
      this.x = x;
      this.y = y;
   }

   public void setActive(float time) {
      this.active = true;
      this.duration = time;
   }
}
