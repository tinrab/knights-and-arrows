package com.minild44.www.util;

public class Animation {
   private int frames;
   private float state;
   private float frameDuration;

   public Animation(int frames, float frameDuration) {
      this.frames = frames;
      this.frameDuration = frameDuration;
   }

   public void setFrameDuration(float f) {
      this.frameDuration = f;
   }

   public void update(float delta) {
      this.state += delta;
      if (this.state / this.frameDuration >= (float)this.frames) {
         this.state = 0.0F;
      }

   }

   public int getCurrentFrame() {
      return (int)(this.state / this.frameDuration);
   }
}
