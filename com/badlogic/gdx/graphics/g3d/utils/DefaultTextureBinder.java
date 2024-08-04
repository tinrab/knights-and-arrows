package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.nio.IntBuffer;

public final class DefaultTextureBinder implements TextureBinder {
   public static final int ROUNDROBIN = 0;
   public static final int WEIGHTED = 1;
   public static final int MAX_GLES_UNITS = 32;
   private final int offset;
   private final int count;
   private final int reuseWeight;
   private final TextureDescriptor[] textures;
   private final int[] weights;
   private final int method;
   private boolean reused;
   private int reuseCount;
   private int bindCount;
   private int currentTexture;

   public DefaultTextureBinder(int method) {
      this(method, 0);
   }

   public DefaultTextureBinder(int method, int offset) {
      this(method, offset, Math.min(getMaxTextureUnits(), 32) - offset);
   }

   public DefaultTextureBinder(int method, int offset, int count) {
      this(method, offset, count, 10);
   }

   public DefaultTextureBinder(int method, int offset, int count, int reuseWeight) {
      this.reuseCount = 0;
      this.bindCount = 0;
      this.currentTexture = 0;
      int max = Math.min(getMaxTextureUnits(), 32);
      if (offset >= 0 && count >= 0 && offset + count <= max && reuseWeight >= 1) {
         this.method = method;
         this.offset = offset;
         this.count = count;
         this.textures = new TextureDescriptor[count];

         for(int i = 0; i < count; ++i) {
            this.textures[i] = new TextureDescriptor();
         }

         this.reuseWeight = reuseWeight;
         this.weights = method == 1 ? new int[count] : null;
      } else {
         throw new GdxRuntimeException("Illegal arguments");
      }
   }

   private static int getMaxTextureUnits() {
      IntBuffer buffer = BufferUtils.newIntBuffer(16);
      if (Gdx.graphics.isGL20Available()) {
         Gdx.gl.glGetIntegerv(34930, buffer);
      } else {
         Gdx.gl.glGetIntegerv(34018, buffer);
      }

      return buffer.get(0);
   }

   public void begin() {
      for(int i = 0; i < this.count; ++i) {
         this.textures[i].texture = null;
         if (this.weights != null) {
            this.weights[i] = 0;
         }
      }

   }

   public void end() {
      for(int i = 0; i < this.count; ++i) {
         if (this.textures[i].texture != null) {
            Gdx.gl.glActiveTexture('蓀' + this.offset + i);
            Gdx.gl.glBindTexture(3553, 0);
            this.textures[i].texture = null;
         }
      }

      Gdx.gl.glActiveTexture(33984);
   }

   public final int bind(TextureDescriptor textureDesc) {
      return this.bindTexture(textureDesc, false);
   }

   private final int bindTexture(TextureDescriptor textureDesc, boolean rebind) {
      this.reused = false;
      int idx;
      int result;
      switch(this.method) {
      case 0:
         result = this.offset + (idx = this.bindTextureRoundRobin(textureDesc.texture));
         break;
      case 1:
         result = this.offset + (idx = this.bindTextureWeighted(textureDesc.texture));
         break;
      default:
         return -1;
      }

      if (this.reused) {
         ++this.reuseCount;
         if (rebind) {
            textureDesc.texture.bind(result);
         } else {
            Gdx.gl.glActiveTexture('蓀' + result);
         }
      } else {
         ++this.bindCount;
      }

      if (textureDesc.minFilter != 1281 && textureDesc.minFilter != this.textures[idx].minFilter) {
         Gdx.gl.glTexParameterf(3553, 10241, (float)(this.textures[idx].minFilter = textureDesc.minFilter));
      }

      if (textureDesc.magFilter != 1281 && textureDesc.magFilter != this.textures[idx].magFilter) {
         Gdx.gl.glTexParameterf(3553, 10240, (float)(this.textures[idx].magFilter = textureDesc.magFilter));
      }

      if (textureDesc.uWrap != 1281 && textureDesc.uWrap != this.textures[idx].uWrap) {
         Gdx.gl.glTexParameterf(3553, 10242, (float)(this.textures[idx].uWrap = textureDesc.uWrap));
      }

      if (textureDesc.vWrap != 1281 && textureDesc.vWrap != this.textures[idx].vWrap) {
         Gdx.gl.glTexParameterf(3553, 10243, (float)(this.textures[idx].vWrap = textureDesc.vWrap));
      }

      return result;
   }

   private final int bindTextureRoundRobin(Texture texture) {
      for(int i = 0; i < this.count; ++i) {
         int idx = (this.currentTexture + i) % this.count;
         if (this.textures[idx].texture == texture) {
            this.reused = true;
            return idx;
         }
      }

      this.currentTexture = (this.currentTexture + 1) % this.count;
      this.textures[this.currentTexture].texture = texture;
      texture.bind(this.offset + this.currentTexture);
      return this.currentTexture;
   }

   private final int bindTextureWeighted(Texture texture) {
      int result = -1;
      int weight = this.weights[0];
      int windex = 0;

      for(int i = 0; i < this.count; ++i) {
         if (this.textures[i].texture == texture) {
            result = i;
            int[] var10000 = this.weights;
            var10000[i] += this.reuseWeight;
         } else if (this.weights[i] < 0 || --this.weights[i] < weight) {
            weight = this.weights[i];
            windex = i;
         }
      }

      if (result < 0) {
         this.textures[windex].texture = texture;
         this.weights[windex] = 100;
         result = windex;
         texture.bind(this.offset + windex);
      } else {
         this.reused = true;
      }

      return result;
   }

   public final int getBindCount() {
      return this.bindCount;
   }

   public final int getReuseCount() {
      return this.reuseCount;
   }

   public final void resetCounts() {
      this.bindCount = this.reuseCount = 0;
   }
}
