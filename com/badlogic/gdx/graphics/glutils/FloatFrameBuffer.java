package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class FloatFrameBuffer extends FrameBuffer {
   public FloatFrameBuffer(int width, int height, boolean hasDepth) {
      super((Pixmap.Format)null, width, height, hasDepth);
   }

   protected void setupTexture() {
      FloatTextureData data = new FloatTextureData(this.width, this.height);
      this.colorTexture = new Texture(data);
      if (Gdx.app.getType() != Application.ApplicationType.Desktop && Gdx.app.getType() != Application.ApplicationType.Applet) {
         this.colorTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
      } else {
         this.colorTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
      }

      this.colorTexture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
   }
}
