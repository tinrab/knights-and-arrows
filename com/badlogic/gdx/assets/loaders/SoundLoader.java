package com.badlogic.gdx.assets.loaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;

public class SoundLoader extends SynchronousAssetLoader<Sound, SoundLoader.SoundParameter> {
   public SoundLoader(FileHandleResolver resolver) {
      super(resolver);
   }

   public Sound load(AssetManager assetManager, String fileName, SoundLoader.SoundParameter parameter) {
      return Gdx.audio.newSound(this.resolve(fileName));
   }

   public Array<AssetDescriptor> getDependencies(String fileName, SoundLoader.SoundParameter parameter) {
      return null;
   }

   public static class SoundParameter extends AssetLoaderParameters<Sound> {
   }
}
