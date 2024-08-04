package com.badlogic.gdx.utils;

public class PauseableThread extends Thread {
   final Runnable runnable;
   boolean paused = false;
   boolean exit = false;

   public PauseableThread(Runnable runnable) {
      this.runnable = runnable;
   }

   public void run() {
      while(true) {
         synchronized(this) {
            try {
               while(this.paused) {
                  this.wait();
               }
            } catch (InterruptedException var3) {
               var3.printStackTrace();
            }
         }

         if (this.exit) {
            return;
         }

         this.runnable.run();
      }
   }

   public void onPause() {
      this.paused = true;
   }

   public void onResume() {
      synchronized(this) {
         this.paused = false;
         this.notifyAll();
      }
   }

   public boolean isPaused() {
      return this.paused;
   }

   public void stopThread() {
      this.exit = true;
      if (this.paused) {
         this.onResume();
      }

   }
}
