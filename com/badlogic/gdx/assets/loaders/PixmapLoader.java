package com.badlogic.gdx.assets.loaders;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Array;

public class PixmapLoader extends AsynchronousAssetLoader<Pixmap, PixmapLoader.PixmapParameter> {
   Pixmap pixmap;

   public PixmapLoader(FileHandleResolver resolver) {
      super(resolver);
   }

   public void loadAsync(AssetManager manager, String fileName, PixmapLoader.PixmapParameter parameter) {
      this.pixmap = null;
      this.pixmap = new Pixmap(this.resolve(fileName));
   }

   public Pixmap loadSync(AssetManager manager, String fileName, PixmapLoader.PixmapParameter parameter) {
      return this.pixmap;
   }

   public Array<AssetDescriptor> getDependencies(String fileName, PixmapLoader.PixmapParameter parameter) {
      return null;
   }

   public static class PixmapParameter extends AssetLoaderParameters<Pixmap> {
   }
}
