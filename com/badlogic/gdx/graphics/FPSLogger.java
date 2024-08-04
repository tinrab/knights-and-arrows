package com.badlogic.gdx.graphics;

import com.badlogic.gdx.Gdx;

public class FPSLogger {
   long startTime = System.currentTimeMillis();

   public void log() {
      if (System.currentTimeMillis() - this.startTime > 1000L) {
         Gdx.app.log("FPSLogger", "fps: " + Gdx.graphics.getFramesPerSecond());
         this.startTime = System.currentTimeMillis();
      }

   }
}
