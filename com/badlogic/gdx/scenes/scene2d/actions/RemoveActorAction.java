package com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class RemoveActorAction extends Action {
   private Actor removeActor;
   private boolean removed;

   public boolean act(float delta) {
      if (!this.removed) {
         this.removed = true;
         (this.removeActor != null ? this.removeActor : this.actor).remove();
      }

      return true;
   }

   public void restart() {
      this.removed = false;
   }

   public void reset() {
      super.reset();
      this.removeActor = null;
   }

   public Actor getRemoveActor() {
      return this.removeActor;
   }

   public void setRemoveActor(Actor removeActor) {
      this.removeActor = removeActor;
   }
}
