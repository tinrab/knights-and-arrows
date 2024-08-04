package com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.utils.Pool;

public class RunnableAction extends Action {
   private Runnable runnable;
   private boolean ran;

   public boolean act(float delta) {
      if (!this.ran) {
         this.ran = true;
         this.run();
      }

      return true;
   }

   public void run() {
      Pool pool = this.getPool();
      this.setPool((Pool)null);

      try {
         this.runnable.run();
      } finally {
         this.setPool(pool);
      }

   }

   public void restart() {
      this.ran = false;
   }

   public void reset() {
      super.reset();
      this.runnable = null;
   }

   public Runnable getRunnable() {
      return this.runnable;
   }

   public void setRunnable(Runnable runnable) {
      this.runnable = runnable;
   }
}
