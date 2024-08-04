package com.badlogic.gdx.utils;

import com.badlogic.gdx.math.Vector2;

public enum Scaling {
   fit,
   fill,
   fillX,
   fillY,
   stretch,
   stretchX,
   stretchY,
   none;

   private static final Vector2 temp = new Vector2();
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$com$badlogic$gdx$utils$Scaling;

   public Vector2 apply(float sourceWidth, float sourceHeight, float targetWidth, float targetHeight) {
      float var10000;
      float targetRatio;
      float sourceRatio;
      float scale;
      switch($SWITCH_TABLE$com$badlogic$gdx$utils$Scaling()[this.ordinal()]) {
      case 1:
         targetRatio = targetHeight / targetWidth;
         sourceRatio = sourceHeight / sourceWidth;
         scale = targetRatio > sourceRatio ? targetWidth / sourceWidth : targetHeight / sourceHeight;
         temp.x = sourceWidth * scale;
         temp.y = sourceHeight * scale;
         break;
      case 2:
         targetRatio = targetHeight / targetWidth;
         sourceRatio = sourceHeight / sourceWidth;
         scale = targetRatio < sourceRatio ? targetWidth / sourceWidth : targetHeight / sourceHeight;
         temp.x = sourceWidth * scale;
         temp.y = sourceHeight * scale;
         break;
      case 3:
         var10000 = targetHeight / targetWidth;
         var10000 = sourceHeight / sourceWidth;
         scale = targetWidth / sourceWidth;
         temp.x = sourceWidth * scale;
         temp.y = sourceHeight * scale;
         break;
      case 4:
         var10000 = targetHeight / targetWidth;
         var10000 = sourceHeight / sourceWidth;
         scale = targetHeight / sourceHeight;
         temp.x = sourceWidth * scale;
         temp.y = sourceHeight * scale;
         break;
      case 5:
         temp.x = targetWidth;
         temp.y = targetHeight;
         break;
      case 6:
         temp.x = targetWidth;
         temp.y = sourceHeight;
         break;
      case 7:
         temp.x = sourceWidth;
         temp.y = targetHeight;
         break;
      case 8:
         temp.x = sourceWidth;
         temp.y = sourceHeight;
      }

      return temp;
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$com$badlogic$gdx$utils$Scaling() {
      int[] var10000 = $SWITCH_TABLE$com$badlogic$gdx$utils$Scaling;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[values().length];

         try {
            var0[fill.ordinal()] = 2;
         } catch (NoSuchFieldError var8) {
         }

         try {
            var0[fillX.ordinal()] = 3;
         } catch (NoSuchFieldError var7) {
         }

         try {
            var0[fillY.ordinal()] = 4;
         } catch (NoSuchFieldError var6) {
         }

         try {
            var0[fit.ordinal()] = 1;
         } catch (NoSuchFieldError var5) {
         }

         try {
            var0[none.ordinal()] = 8;
         } catch (NoSuchFieldError var4) {
         }

         try {
            var0[stretch.ordinal()] = 5;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[stretchX.ordinal()] = 6;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[stretchY.ordinal()] = 7;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$com$badlogic$gdx$utils$Scaling = var0;
         return var0;
      }
   }
}
