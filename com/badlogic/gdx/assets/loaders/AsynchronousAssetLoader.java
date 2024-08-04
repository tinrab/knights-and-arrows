package com.badlogic.gdx.assets.loaders;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;

public abstract class AsynchronousAssetLoader<T, P extends AssetLoaderParameters<T>> extends AssetLoader<T, P> {
   public AsynchronousAssetLoader(FileHandleResolver resolver) {
      super(resolver);
   }

   public abstract void loadAsync(AssetManager var1, String var2, P var3);

   public abstract T loadSync(AssetManager var1, String var2, P var3);
}
