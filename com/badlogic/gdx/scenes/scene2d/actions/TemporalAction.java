package com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.utils.Pool;

public abstract class TemporalAction extends Action {
   private float duration;
   private float time;
   private Interpolation interpolation;
   private boolean reverse;
   private boolean complete;

   public TemporalAction() {
   }

   public TemporalAction(float duration) {
      this.duration = duration;
   }

   public TemporalAction(float duration, Interpolation interpolation) {
      this.duration = duration;
      this.interpolation = interpolation;
   }

   public boolean act(float delta) {
      if (this.complete) {
         return true;
      } else {
         Pool pool = this.getPool();
         this.setPool((Pool)null);

         boolean var5;
         try {
            if (this.time == 0.0F) {
               this.begin();
            }

            this.time += delta;
            this.complete = this.time >= this.duration;
            float percent;
            if (this.complete) {
               percent = 1.0F;
            } else {
               percent = this.time / this.duration;
               if (this.interpolation != null) {
                  percent = this.interpolation.apply(percent);
               }
            }

            this.update(this.reverse ? 1.0F - percent : percent);
            if (this.complete) {
               this.end();
            }

            var5 = this.complete;
         } finally {
            this.setPool(pool);
         }

         return var5;
      }
   }

   protected void begin() {
   }

   protected void end() {
   }

   protected abstract void update(float var1);

   public void finish() {
      this.time = this.duration;
   }

   public void restart() {
      this.time = 0.0F;
      this.complete = false;
   }

   public void reset() {
      super.reset();
      this.reverse = false;
      this.interpolation = null;
   }

   public float getTime() {
      return this.time;
   }

   public void setTime(float time) {
      this.time = time;
   }

   public float getDuration() {
      return this.duration;
   }

   public void setDuration(float duration) {
      this.duration = duration;
   }

   public Interpolation getInterpolation() {
      return this.interpolation;
   }

   public void setInterpolation(Interpolation interpolation) {
      this.interpolation = interpolation;
   }

   public boolean isReverse() {
      return this.reverse;
   }

   public void setReverse(boolean reverse) {
      this.reverse = reverse;
   }
}
