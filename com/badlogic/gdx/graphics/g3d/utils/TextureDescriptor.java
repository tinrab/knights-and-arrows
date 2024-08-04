package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.graphics.Texture;

public class TextureDescriptor {
   public Texture texture = null;
   public int minFilter = 1281;
   public int magFilter = 1281;
   public int uWrap = 1281;
   public int vWrap = 1281;

   public TextureDescriptor(Texture texture, int minFilter, int magFilter, int uWrap, int vWrap) {
      this.set(texture, minFilter, magFilter, uWrap, vWrap);
   }

   public TextureDescriptor(Texture texture) {
      this.texture = texture;
   }

   public TextureDescriptor() {
   }

   public void set(Texture texture, int minFilter, int magFilter, int uWrap, int vWrap) {
      this.texture = texture;
      this.minFilter = minFilter;
      this.magFilter = magFilter;
      this.uWrap = uWrap;
      this.vWrap = vWrap;
   }

   public void set(TextureDescriptor other) {
      this.texture = other.texture;
      this.minFilter = other.minFilter;
      this.magFilter = other.magFilter;
      this.uWrap = other.uWrap;
      this.vWrap = other.vWrap;
   }

   public void reset() {
      this.texture = null;
      this.minFilter = 1281;
      this.magFilter = 1281;
      this.uWrap = 1281;
      this.vWrap = 1281;
   }

   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else if (obj == this) {
         return true;
      } else if (!(obj instanceof TextureDescriptor)) {
         return false;
      } else {
         TextureDescriptor other = (TextureDescriptor)obj;
         return other.texture == this.texture && other.minFilter == this.minFilter && other.magFilter == this.magFilter && other.uWrap == this.uWrap && other.vWrap == this.vWrap;
      }
   }
}
