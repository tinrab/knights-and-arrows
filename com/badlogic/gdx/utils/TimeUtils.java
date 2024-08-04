package com.badlogic.gdx.utils;

public class TimeUtils {
   public static long nanoTime() {
      return System.nanoTime();
   }

   public static long millis() {
      return System.currentTimeMillis();
   }
}
