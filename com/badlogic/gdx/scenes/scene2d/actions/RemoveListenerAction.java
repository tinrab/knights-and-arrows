package com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;

public class RemoveListenerAction extends Action {
   private Actor targetActor;
   private EventListener listener;
   private boolean capture;

   public boolean act(float delta) {
      Actor actor = this.targetActor != null ? this.targetActor : this.actor;
      if (this.capture) {
         actor.removeCaptureListener(this.listener);
      } else {
         actor.removeListener(this.listener);
      }

      return true;
   }

   public Actor getTargetActor() {
      return this.targetActor;
   }

   public void setTargetActor(Actor actor) {
      this.targetActor = actor;
   }

   public EventListener getListener() {
      return this.listener;
   }

   public void setListener(EventListener listener) {
      this.listener = listener;
   }

   public boolean getCapture() {
      return this.capture;
   }

   public void setCapture(boolean capture) {
      this.capture = capture;
   }

   public void reset() {
      super.reset();
      this.targetActor = null;
      this.listener = null;
   }
}
