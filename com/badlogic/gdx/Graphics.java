package com.badlogic.gdx;

import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GLCommon;

public interface Graphics {
   boolean isGL11Available();

   boolean isGL20Available();

   GLCommon getGLCommon();

   GL10 getGL10();

   GL11 getGL11();

   GL20 getGL20();

   int getWidth();

   int getHeight();

   float getDeltaTime();

   float getRawDeltaTime();

   int getFramesPerSecond();

   Graphics.GraphicsType getType();

   float getPpiX();

   float getPpiY();

   float getPpcX();

   float getPpcY();

   float getDensity();

   boolean supportsDisplayModeChange();

   Graphics.DisplayMode[] getDisplayModes();

   Graphics.DisplayMode getDesktopDisplayMode();

   boolean setDisplayMode(Graphics.DisplayMode var1);

   boolean setDisplayMode(int var1, int var2, boolean var3);

   void setTitle(String var1);

   void setVSync(boolean var1);

   Graphics.BufferFormat getBufferFormat();

   boolean supportsExtension(String var1);

   void setContinuousRendering(boolean var1);

   boolean isContinuousRendering();

   void requestRendering();

   boolean isFullscreen();

   public static class BufferFormat {
      public final int r;
      public final int g;
      public final int b;
      public final int a;
      public final int depth;
      public final int stencil;
      public final int samples;
      public final boolean coverageSampling;

      public BufferFormat(int r, int g, int b, int a, int depth, int stencil, int samples, boolean coverageSampling) {
         this.r = r;
         this.g = g;
         this.b = b;
         this.a = a;
         this.depth = depth;
         this.stencil = stencil;
         this.samples = samples;
         this.coverageSampling = coverageSampling;
      }

      public String toString() {
         return "r: " + this.r + ", g: " + this.g + ", b: " + this.b + ", a: " + this.a + ", depth: " + this.depth + ", stencil: " + this.stencil + ", num samples: " + this.samples + ", coverage sampling: " + this.coverageSampling;
      }
   }

   public static class DisplayMode {
      public final int width;
      public final int height;
      public final int refreshRate;
      public final int bitsPerPixel;

      protected DisplayMode(int width, int height, int refreshRate, int bitsPerPixel) {
         this.width = width;
         this.height = height;
         this.refreshRate = refreshRate;
         this.bitsPerPixel = bitsPerPixel;
      }

      public String toString() {
         return this.width + "x" + this.height + ", bpp: " + this.bitsPerPixel + ", hz: " + this.refreshRate;
      }
   }

   public static enum GraphicsType {
      AndroidGL,
      LWJGL,
      Angle,
      WebGL,
      iOSGL,
      JGLFW;
   }
}
