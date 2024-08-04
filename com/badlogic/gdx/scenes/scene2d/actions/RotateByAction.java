package com.badlogic.gdx.scenes.scene2d.actions;

public class RotateByAction extends RelativeTemporalAction {
   private float amount;

   protected void updateRelative(float percentDelta) {
      this.actor.rotate(this.amount * percentDelta);
   }

   public float getAmount() {
      return this.amount;
   }

   public void setAmount(float rotationAmount) {
      this.amount = rotationAmount;
   }
}
