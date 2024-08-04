package com.badlogic.gdx.scenes.scene2d.actions;

public class SizeToAction extends TemporalAction {
   private float startWidth;
   private float startHeight;
   private float endWidth;
   private float endHeight;

   protected void begin() {
      this.startWidth = this.actor.getWidth();
      this.startHeight = this.actor.getHeight();
   }

   protected void update(float percent) {
      this.actor.setSize(this.startWidth + (this.endWidth - this.startWidth) * percent, this.startHeight + (this.endHeight - this.startHeight) * percent);
   }

   public void setSize(float width, float height) {
      this.endWidth = width;
      this.endHeight = height;
   }

   public float getWidth() {
      return this.endWidth;
   }

   public void setWidth(float width) {
      this.endWidth = width;
   }

   public float getHeight() {
      return this.endHeight;
   }

   public void setHeight(float height) {
      this.endHeight = height;
   }
}
