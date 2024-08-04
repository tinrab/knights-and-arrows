package com.badlogic.gdx.assets.loaders;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.glutils.ETC1TextureData;
import com.badlogic.gdx.graphics.glutils.FileTextureData;
import com.badlogic.gdx.utils.Array;

public class TextureLoader extends AsynchronousAssetLoader<Texture, TextureLoader.TextureParameter> {
   TextureLoader.TextureLoaderInfo info = new TextureLoader.TextureLoaderInfo();

   public TextureLoader(FileHandleResolver resolver) {
      super(resolver);
   }

   public void loadAsync(AssetManager manager, String fileName, TextureLoader.TextureParameter parameter) {
      this.info.filename = fileName;
      if (parameter == null || parameter != null && parameter.textureData == null) {
         Pixmap pixmap = null;
         Pixmap.Format format = null;
         boolean genMipMaps = false;
         this.info.texture = null;
         if (parameter != null) {
            format = parameter.format;
            genMipMaps = parameter.genMipMaps;
            this.info.texture = parameter.texture;
         }

         FileHandle handle = this.resolve(fileName);
         if (!fileName.contains(".etc1")) {
            if (fileName.contains(".cim")) {
               pixmap = PixmapIO.readCIM(handle);
            } else {
               pixmap = new Pixmap(handle);
            }

            this.info.data = new FileTextureData(handle, pixmap, format, genMipMaps);
         } else {
            this.info.data = new ETC1TextureData(handle, genMipMaps);
         }
      } else {
         this.info.data = parameter.textureData;
         if (!this.info.data.isPrepared()) {
            this.info.data.prepare();
         }

         this.info.texture = parameter.texture;
      }

   }

   public Texture loadSync(AssetManager manager, String fileName, TextureLoader.TextureParameter parameter) {
      if (this.info == null) {
         return null;
      } else {
         Texture texture = this.info.texture;
         if (texture != null) {
            texture.load(this.info.data);
         } else {
            texture = new Texture(this.info.data);
         }

         if (parameter != null) {
            texture.setFilter(parameter.minFilter, parameter.magFilter);
            texture.setWrap(parameter.wrapU, parameter.wrapV);
         }

         return texture;
      }
   }

   public Array<AssetDescriptor> getDependencies(String fileName, TextureLoader.TextureParameter parameter) {
      return null;
   }

   public static class TextureLoaderInfo {
      String filename;
      TextureData data;
      Texture texture;
   }

   public static class TextureParameter extends AssetLoaderParameters<Texture> {
      public Pixmap.Format format = null;
      public boolean genMipMaps = false;
      public Texture texture = null;
      public TextureData textureData = null;
      public Texture.TextureFilter minFilter;
      public Texture.TextureFilter magFilter;
      public Texture.TextureWrap wrapU;
      public Texture.TextureWrap wrapV;

      public TextureParameter() {
         this.minFilter = Texture.TextureFilter.Nearest;
         this.magFilter = Texture.TextureFilter.Nearest;
         this.wrapU = Texture.TextureWrap.ClampToEdge;
         this.wrapV = Texture.TextureWrap.ClampToEdge;
      }
   }
}
