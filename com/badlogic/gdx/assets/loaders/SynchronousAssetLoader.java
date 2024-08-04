package com.badlogic.gdx.assets.loaders;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;

public abstract class SynchronousAssetLoader<T, P extends AssetLoaderParameters<T>> extends AssetLoader<T, P> {
   public SynchronousAssetLoader(FileHandleResolver resolver) {
      super(resolver);
   }

   public abstract T load(AssetManager var1, String var2, P var3);
}
