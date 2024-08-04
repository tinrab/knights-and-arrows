package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.utils.Timer;

public class DragScrollListener extends DragListener {
   private ScrollPane scroll;
   private Timer.Task scrollUp;
   private Timer.Task scrollDown;
   Interpolation interpolation;
   float minSpeed;
   float maxSpeed;
   float tickSecs;
   long startTime;
   long rampTime;

   public DragScrollListener(final ScrollPane scroll) {
      this.interpolation = Interpolation.exp5In;
      this.minSpeed = 15.0F;
      this.maxSpeed = 75.0F;
      this.tickSecs = 0.05F;
      this.rampTime = 1750L;
      this.scroll = scroll;
      this.scrollUp = new Timer.Task() {
         public void run() {
            scroll.setScrollY(scroll.getScrollY() - DragScrollListener.this.getScrollPixels());
         }
      };
      this.scrollDown = new Timer.Task() {
         public void run() {
            scroll.setScrollY(scroll.getScrollY() + DragScrollListener.this.getScrollPixels());
         }
      };
   }

   public void setup(float minSpeedPixels, float maxSpeedPixels, float tickSecs, float rampSecs) {
      this.minSpeed = minSpeedPixels;
      this.maxSpeed = maxSpeedPixels;
      this.tickSecs = tickSecs;
      this.rampTime = (long)(rampSecs * 1000.0F);
   }

   float getScrollPixels() {
      return this.interpolation.apply(this.minSpeed, this.maxSpeed, Math.min(1.0F, (float)(System.currentTimeMillis() - this.startTime) / (float)this.rampTime));
   }

   public void drag(InputEvent event, float x, float y, int pointer) {
      if (x >= 0.0F && x < this.scroll.getWidth()) {
         if (y >= this.scroll.getHeight()) {
            this.scrollDown.cancel();
            if (!this.scrollUp.isScheduled()) {
               this.startTime = System.currentTimeMillis();
               Timer.schedule(this.scrollUp, this.tickSecs, this.tickSecs);
            }

            return;
         }

         if (y < 0.0F) {
            this.scrollUp.cancel();
            if (!this.scrollDown.isScheduled()) {
               this.startTime = System.currentTimeMillis();
               Timer.schedule(this.scrollDown, this.tickSecs, this.tickSecs);
            }

            return;
         }
      }

      this.scrollUp.cancel();
      this.scrollDown.cancel();
   }

   public void dragStop(InputEvent event, float x, float y, int pointer) {
      this.scrollUp.cancel();
      this.scrollDown.cancel();
   }
}
