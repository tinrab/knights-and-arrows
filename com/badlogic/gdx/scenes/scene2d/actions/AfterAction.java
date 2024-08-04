package com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

public class AfterAction extends DelegateAction {
   private Array<Action> waitForActions = new Array(false, 4);

   public void setActor(Actor actor) {
      if (actor != null) {
         this.waitForActions.addAll(actor.getActions());
      }

      super.setActor(actor);
   }

   public void restart() {
      super.restart();
      this.waitForActions.clear();
   }

   protected boolean delegate(float delta) {
      Array<Action> currentActions = this.actor.getActions();
      if (currentActions.size == 1) {
         this.waitForActions.clear();
      }

      for(int i = this.waitForActions.size - 1; i >= 0; --i) {
         Action action = (Action)this.waitForActions.get(i);
         int index = currentActions.indexOf(action, true);
         if (index == -1) {
            this.waitForActions.removeIndex(i);
         }
      }

      if (this.waitForActions.size > 0) {
         return false;
      } else {
         return this.action.act(delta);
      }
   }
}
