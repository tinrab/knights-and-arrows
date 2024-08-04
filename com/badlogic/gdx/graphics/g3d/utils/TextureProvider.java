package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public interface TextureProvider {
   Texture load(String var1);

   public static class AssetTextureProvider implements TextureProvider {
      public final AssetManager assetManager;

      public AssetTextureProvider(AssetManager assetManager) {
         this.assetManager = assetManager;
      }

      public Texture load(String fileName) {
         return (Texture)this.assetManager.get(fileName, Texture.class);
      }
   }

   public static class FileTextureProvider implements TextureProvider {
      public Texture load(String fileName) {
         return new Texture(Gdx.files.internal(fileName));
      }
   }
}
