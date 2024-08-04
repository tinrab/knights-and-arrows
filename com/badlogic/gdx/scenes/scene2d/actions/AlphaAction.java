package com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.graphics.Color;

public class AlphaAction extends TemporalAction {
   private float start;
   private float end;
   private Color color;

   protected void begin() {
      if (this.color == null) {
         this.color = this.actor.getColor();
      }

      this.start = this.color.a;
   }

   protected void update(float percent) {
      this.color.a = this.start + (this.end - this.start) * percent;
   }

   public void reset() {
      super.reset();
      this.color = null;
   }

   public Color getColor() {
      return this.color;
   }

   public void setColor(Color color) {
      this.color = color;
   }

   public float getAlpha() {
      return this.end;
   }

   public void setAlpha(float alpha) {
      this.end = alpha;
   }
}
