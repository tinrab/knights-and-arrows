package com.badlogic.gdx.assets.loaders;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import java.util.Iterator;

public class TextureAtlasLoader extends SynchronousAssetLoader<TextureAtlas, TextureAtlasLoader.TextureAtlasParameter> {
   TextureAtlas.TextureAtlasData data;

   public TextureAtlasLoader(FileHandleResolver resolver) {
      super(resolver);
   }

   public TextureAtlas load(AssetManager assetManager, String fileName, TextureAtlasLoader.TextureAtlasParameter parameter) {
      TextureAtlas.TextureAtlasData.Page page;
      Texture texture;
      for(Iterator var5 = this.data.getPages().iterator(); var5.hasNext(); page.texture = texture) {
         page = (TextureAtlas.TextureAtlasData.Page)var5.next();
         texture = (Texture)assetManager.get(page.textureFile.path().replaceAll("\\\\", "/"), Texture.class);
      }

      return new TextureAtlas(this.data);
   }

   public Array<AssetDescriptor> getDependencies(String fileName, TextureAtlasLoader.TextureAtlasParameter parameter) {
      FileHandle atlasFile = this.resolve(fileName);
      FileHandle imgDir = atlasFile.parent();
      if (parameter != null) {
         this.data = new TextureAtlas.TextureAtlasData(atlasFile, imgDir, parameter.flip);
      } else {
         this.data = new TextureAtlas.TextureAtlasData(atlasFile, imgDir, false);
      }

      Array<AssetDescriptor> dependencies = new Array();
      Iterator var7 = this.data.getPages().iterator();

      while(var7.hasNext()) {
         TextureAtlas.TextureAtlasData.Page page = (TextureAtlas.TextureAtlasData.Page)var7.next();
         FileHandle handle = this.resolve(page.textureFile.path());
         TextureLoader.TextureParameter params = new TextureLoader.TextureParameter();
         params.format = page.format;
         params.genMipMaps = page.useMipMaps;
         params.minFilter = page.minFilter;
         params.magFilter = page.magFilter;
         dependencies.add(new AssetDescriptor(handle.path().replaceAll("\\\\", "/"), Texture.class, params));
      }

      return dependencies;
   }

   public static class TextureAtlasParameter extends AssetLoaderParameters<TextureAtlas> {
      public boolean flip = false;

      public TextureAtlasParameter() {
      }

      public TextureAtlasParameter(boolean flip) {
         this.flip = flip;
      }
   }
}
