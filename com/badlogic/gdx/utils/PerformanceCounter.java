package com.badlogic.gdx.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.FloatCounter;

public class PerformanceCounter {
   private static final float nano2seconds = 1.0E-9F;
   private long startTime;
   private long lastTick;
   public final FloatCounter time;
   public final FloatCounter load;
   public final String name;
   public float current;
   public boolean valid;

   public PerformanceCounter(String name) {
      this(name, 5);
   }

   public PerformanceCounter(String name, int windowSize) {
      this.startTime = 0L;
      this.lastTick = 0L;
      this.current = 0.0F;
      this.valid = false;
      this.name = name;
      this.time = new FloatCounter(windowSize);
      this.load = new FloatCounter(1);
   }

   public void tick() {
      long t = TimeUtils.nanoTime();
      if (this.lastTick > 0L) {
         this.tick((float)(t - this.lastTick) * 1.0E-9F);
      }

      this.lastTick = t;
   }

   public void tick(float delta) {
      if (!this.valid) {
         Gdx.app.error("PerformanceCounter", "Invalid data, check if you called PerformanceCounter#stop()");
      } else {
         this.time.put(this.current);
         float currentLoad = delta == 0.0F ? 0.0F : this.current / delta;
         this.load.put(delta > 1.0F ? currentLoad : delta * currentLoad + (1.0F - delta) * this.load.latest);
         this.current = 0.0F;
         this.valid = false;
      }
   }

   public void start() {
      this.startTime = TimeUtils.nanoTime();
      this.valid = false;
   }

   public void stop() {
      if (this.startTime > 0L) {
         this.current += (float)(TimeUtils.nanoTime() - this.startTime) * 1.0E-9F;
         this.startTime = 0L;
         this.valid = true;
      }

   }

   public void reset() {
      this.time.reset();
      this.load.reset();
      this.startTime = 0L;
      this.lastTick = 0L;
      this.current = 0.0F;
      this.valid = false;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      return this.toString(sb).toString();
   }

   public StringBuilder toString(StringBuilder sb) {
      sb.append(this.name).append(": [time: ").append(this.time.value).append(", load: ").append(this.load.value).append("]");
      return sb;
   }
}
