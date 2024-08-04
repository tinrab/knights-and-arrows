package com.badlogic.gdx.scenes.scene2d.actions;

public class FloatAction extends TemporalAction {
   private float start;
   private float end;
   private float value;

   public FloatAction() {
      this.start = 0.0F;
      this.end = 1.0F;
   }

   public FloatAction(float start, float end) {
      this.start = start;
      this.end = end;
   }

   protected void begin() {
      this.value = this.start;
   }

   protected void update(float percent) {
      this.value = this.start + (this.end - this.start) * percent;
   }

   public float getValue() {
      return this.value;
   }

   public void setValue(float value) {
      this.value = value;
   }

   public float getStart() {
      return this.start;
   }

   public void setStart(float start) {
      this.start = start;
   }

   public float getEnd() {
      return this.end;
   }

   public void setEnd(float end) {
      this.end = end;
   }
}
