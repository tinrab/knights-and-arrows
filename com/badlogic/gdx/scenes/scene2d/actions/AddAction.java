package com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class AddAction extends Action {
   private Actor targetActor;
   private Action action;

   public boolean act(float delta) {
      (this.targetActor != null ? this.targetActor : this.actor).addAction(this.action);
      return true;
   }

   public Actor getTargetActor() {
      return this.targetActor;
   }

   public void setTargetActor(Actor actor) {
      this.targetActor = actor;
   }

   public Action getAction() {
      return this.action;
   }

   public void setAction(Action action) {
      this.action = action;
   }

   public void restart() {
      if (this.action != null) {
         this.action.restart();
      }

   }

   public void reset() {
      super.reset();
      this.targetActor = null;
      this.action = null;
   }
}
