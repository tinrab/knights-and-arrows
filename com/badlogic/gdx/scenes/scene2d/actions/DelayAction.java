package com.badlogic.gdx.scenes.scene2d.actions;

public class DelayAction extends DelegateAction {
   private float duration;
   private float time;

   public DelayAction() {
   }

   public DelayAction(float duration) {
      this.duration = duration;
   }

   protected boolean delegate(float delta) {
      if (this.time < this.duration) {
         this.time += delta;
         if (this.time < this.duration) {
            return false;
         }

         delta = this.time - this.duration;
      }

      return this.action == null ? true : this.action.act(delta);
   }

   public void finish() {
      this.time = this.duration;
   }

   public void restart() {
      super.restart();
      this.time = 0.0F;
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
}
