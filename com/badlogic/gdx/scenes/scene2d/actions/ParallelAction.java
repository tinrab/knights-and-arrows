package com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class ParallelAction extends Action {
   Array<Action> actions = new Array(4);
   private boolean complete;

   public ParallelAction() {
   }

   public ParallelAction(Action action1) {
      this.addAction(action1);
   }

   public ParallelAction(Action action1, Action action2) {
      this.addAction(action1);
      this.addAction(action2);
   }

   public ParallelAction(Action action1, Action action2, Action action3) {
      this.addAction(action1);
      this.addAction(action2);
      this.addAction(action3);
   }

   public ParallelAction(Action action1, Action action2, Action action3, Action action4) {
      this.addAction(action1);
      this.addAction(action2);
      this.addAction(action3);
      this.addAction(action4);
   }

   public ParallelAction(Action action1, Action action2, Action action3, Action action4, Action action5) {
      this.addAction(action1);
      this.addAction(action2);
      this.addAction(action3);
      this.addAction(action4);
      this.addAction(action5);
   }

   public boolean act(float delta) {
      if (this.complete) {
         return true;
      } else {
         this.complete = true;
         Pool pool = this.getPool();
         this.setPool((Pool)null);

         try {
            Array<Action> actions = this.actions;
            int i = 0;

            for(int n = actions.size; i < n && this.actor != null; ++i) {
               if (!((Action)actions.get(i)).act(delta)) {
                  this.complete = false;
               }

               if (this.actor == null) {
                  return true;
               }
            }

            boolean var7 = this.complete;
            return var7;
         } finally {
            this.setPool(pool);
         }
      }
   }

   public void restart() {
      this.complete = false;
      Array<Action> actions = this.actions;
      int i = 0;

      for(int n = actions.size; i < n; ++i) {
         ((Action)actions.get(i)).restart();
      }

   }

   public void reset() {
      super.reset();
      this.actions.clear();
   }

   public void addAction(Action action) {
      this.actions.add(action);
      if (this.actor != null) {
         action.setActor(this.actor);
      }

   }

   public void setActor(Actor actor) {
      Array<Action> actions = this.actions;
      int i = 0;

      for(int n = actions.size; i < n; ++i) {
         ((Action)actions.get(i)).setActor(actor);
      }

      super.setActor(actor);
   }

   public Array<Action> getActions() {
      return this.actions;
   }

   public String toString() {
      StringBuilder buffer = new StringBuilder(64);
      buffer.append(super.toString());
      buffer.append('(');
      Array<Action> actions = this.actions;
      int i = 0;

      for(int n = actions.size; i < n; ++i) {
         if (i > 0) {
            buffer.append(", ");
         }

         buffer.append(actions.get(i));
      }

      buffer.append(')');
      return buffer.toString();
   }
}
