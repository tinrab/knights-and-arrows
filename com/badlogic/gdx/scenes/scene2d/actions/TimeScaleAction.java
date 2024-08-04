package com.badlogic.gdx.scenes.scene2d.actions;

public class TimeScaleAction extends DelegateAction {
   private float scale;

   protected boolean delegate(float delta) {
      return this.action == null ? true : this.action.act(delta * this.scale);
   }

   public float getScale() {
      return this.scale;
   }

   public void setScale(float scale) {
      this.scale = scale;
   }
}
