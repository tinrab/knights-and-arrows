package com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;

public class AddListenerAction extends Action {
   private Actor targetActor;
   private EventListener listener;
   private boolean capture;

   public boolean act(float delta) {
      Actor var10000;
      if (this.targetActor != null) {
         var10000 = this.targetActor;
      } else {
         var10000 = this.actor;
      }

      if (this.capture) {
         this.targetActor.addCaptureListener(this.listener);
      } else {
         this.targetActor.addListener(this.listener);
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
