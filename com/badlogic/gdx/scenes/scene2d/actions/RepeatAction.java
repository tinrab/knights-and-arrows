package com.badlogic.gdx.scenes.scene2d.actions;

public class RepeatAction extends DelegateAction {
   public static final int FOREVER = -1;
   private int repeatCount;
   private int executedCount;
   private boolean finished;

   protected boolean delegate(float delta) {
      if (this.executedCount == this.repeatCount) {
         return true;
      } else {
         if (this.action.act(delta)) {
            if (this.finished) {
               return true;
            }

            if (this.repeatCount > 0) {
               ++this.executedCount;
            }

            if (this.executedCount == this.repeatCount) {
               return true;
            }

            if (this.action != null) {
               this.action.restart();
            }
         }

         return false;
      }
   }

   public void finish() {
      this.finished = true;
   }

   public void restart() {
      super.restart();
      this.executedCount = 0;
      this.finished = false;
   }

   public void setCount(int count) {
      this.repeatCount = count;
   }

   public int getCount() {
      return this.repeatCount;
   }
}
