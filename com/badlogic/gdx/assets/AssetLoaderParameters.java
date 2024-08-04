package com.badlogic.gdx.assets;

public class AssetLoaderParameters<T> {
   public AssetLoaderParameters.LoadedCallback loadedCallback;

   public interface LoadedCallback {
      void finishedLoading(AssetManager var1, String var2, Class var3);
   }
}
