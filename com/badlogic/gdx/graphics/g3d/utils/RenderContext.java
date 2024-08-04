package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.Gdx;

public class RenderContext {
   public final TextureBinder textureBinder;
   private boolean blending;
   private int blendSFactor;
   private int blendDFactor;
   private boolean depthTest;
   private int depthFunc;
   private int cullFace;

   public RenderContext(TextureBinder textures) {
      this.textureBinder = textures;
   }

   public final void begin() {
      Gdx.gl.glDisable(2929);
      this.depthTest = false;
      Gdx.gl.glDisable(3042);
      this.blending = false;
      Gdx.gl.glDisable(2884);
      this.cullFace = this.blendSFactor = this.blendDFactor = this.depthFunc = 0;
      this.textureBinder.begin();
   }

   public final void end() {
      if (this.depthTest) {
         Gdx.gl.glDisable(2929);
      }

      if (this.blending) {
         Gdx.gl.glDisable(3042);
      }

      if (this.cullFace > 0) {
         Gdx.gl.glDisable(2884);
      }

      this.textureBinder.end();
   }

   public final void setDepthTest(boolean enabled, int depthFunction) {
      if (enabled != this.depthTest) {
         this.depthTest = enabled;
         if (enabled) {
            Gdx.gl.glEnable(2929);
         } else {
            Gdx.gl.glDisable(2929);
         }
      }

      if (enabled && this.depthFunc != depthFunction) {
         Gdx.gl.glDepthFunc(depthFunction);
         this.depthFunc = depthFunction;
      }

   }

   public final void setBlending(boolean enabled, int sFactor, int dFactor) {
      if (enabled != this.blending) {
         this.blending = enabled;
         if (enabled) {
            Gdx.gl.glEnable(3042);
         } else {
            Gdx.gl.glDisable(3042);
         }
      }

      if (enabled && (this.blendSFactor != sFactor || this.blendDFactor != dFactor)) {
         Gdx.gl.glBlendFunc(sFactor, dFactor);
         this.blendSFactor = sFactor;
         this.blendDFactor = dFactor;
      }

   }

   public final void setCullFace(int face) {
      if (face != this.cullFace) {
         this.cullFace = face;
         if (face != 1028 && face != 1029 && face != 1032) {
            Gdx.gl.glDisable(2884);
         } else {
            Gdx.gl.glEnable(2884);
            Gdx.gl.glCullFace(face);
         }
      }

   }
}
