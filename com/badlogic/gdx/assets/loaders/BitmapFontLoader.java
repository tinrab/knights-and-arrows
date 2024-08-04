package com.badlogic.gdx.assets.loaders;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class BitmapFontLoader extends AsynchronousAssetLoader<BitmapFont, BitmapFontLoader.BitmapFontParameter> {
   BitmapFont.BitmapFontData data;

   public BitmapFontLoader(FileHandleResolver resolver) {
      super(resolver);
   }

   public Array<AssetDescriptor> getDependencies(String fileName, BitmapFontLoader.BitmapFontParameter parameter) {
      Array<AssetDescriptor> deps = new Array();
      if (parameter != null && parameter.bitmapFontData != null) {
         this.data = parameter.bitmapFontData;
         return deps;
      } else {
         FileHandle handle = this.resolve(fileName);
         this.data = new BitmapFont.BitmapFontData(handle, parameter != null ? parameter.flip : false);
         deps.add(new AssetDescriptor(this.data.getImagePath(), Texture.class));
         return deps;
      }
   }

   public void loadAsync(AssetManager manager, String fileName, BitmapFontLoader.BitmapFontParameter parameter) {
   }

   public BitmapFont loadSync(AssetManager manager, String fileName, BitmapFontLoader.BitmapFontParameter parameter) {
      this.resolve(fileName);
      TextureRegion region = new TextureRegion((Texture)manager.get(this.data.getImagePath(), Texture.class));
      if (parameter != null) {
         region.getTexture().setFilter(parameter.minFitler, parameter.maxFilter);
      }

      return new BitmapFont(this.data, region, true);
   }

   public static class BitmapFontParameter extends AssetLoaderParameters<BitmapFont> {
      public boolean flip = false;
      public Texture.TextureFilter minFitler;
      public Texture.TextureFilter maxFilter;
      public BitmapFont.BitmapFontData bitmapFontData;

      public BitmapFontParameter() {
         this.minFitler = Texture.TextureFilter.Nearest;
         this.maxFilter = Texture.TextureFilter.Nearest;
         this.bitmapFontData = null;
      }
   }
}
