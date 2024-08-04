package com.badlogic.gdx.assets;

import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.AsyncResult;
import com.badlogic.gdx.utils.async.AsyncTask;

class AssetLoadingTask implements AsyncTask<Void> {
   AssetManager manager;
   final AssetDescriptor assetDesc;
   final AssetLoader loader;
   final AsyncExecutor executor;
   final long startTime;
   volatile boolean asyncDone = false;
   volatile boolean dependenciesLoaded = false;
   volatile Array<AssetDescriptor> dependencies;
   volatile AsyncResult<Void> depsFuture = null;
   volatile AsyncResult<Void> loadFuture = null;
   volatile Object asset = null;
   int ticks = 0;
   volatile boolean cancel = false;

   public AssetLoadingTask(AssetManager manager, AssetDescriptor assetDesc, AssetLoader loader, AsyncExecutor threadPool) {
      this.manager = manager;
      this.assetDesc = assetDesc;
      this.loader = loader;
      this.executor = threadPool;
      this.startTime = manager.log.getLevel() == 3 ? TimeUtils.nanoTime() : 0L;
   }

   public Void call() throws Exception {
      AsynchronousAssetLoader asyncLoader = (AsynchronousAssetLoader)this.loader;
      if (!this.dependenciesLoaded) {
         this.dependencies = asyncLoader.getDependencies(this.assetDesc.fileName, this.assetDesc.params);
         if (this.dependencies != null) {
            this.manager.injectDependencies(this.assetDesc.fileName, this.dependencies);
         } else {
            asyncLoader.loadAsync(this.manager, this.assetDesc.fileName, this.assetDesc.params);
            this.asyncDone = true;
         }
      } else {
         asyncLoader.loadAsync(this.manager, this.assetDesc.fileName, this.assetDesc.params);
      }

      return null;
   }

   public boolean update() {
      ++this.ticks;
      if (this.loader instanceof SynchronousAssetLoader) {
         this.handleSyncLoader();
      } else {
         this.handleAsyncLoader();
      }

      return this.asset != null;
   }

   private void handleSyncLoader() {
      SynchronousAssetLoader syncLoader = (SynchronousAssetLoader)this.loader;
      if (!this.dependenciesLoaded) {
         this.dependenciesLoaded = true;
         this.dependencies = syncLoader.getDependencies(this.assetDesc.fileName, this.assetDesc.params);
         if (this.dependencies == null) {
            this.asset = syncLoader.load(this.manager, this.assetDesc.fileName, this.assetDesc.params);
            return;
         }

         this.manager.injectDependencies(this.assetDesc.fileName, this.dependencies);
      } else {
         this.asset = syncLoader.load(this.manager, this.assetDesc.fileName, this.assetDesc.params);
      }

   }

   private void handleAsyncLoader() {
      AsynchronousAssetLoader asyncLoader = (AsynchronousAssetLoader)this.loader;
      if (!this.dependenciesLoaded) {
         if (this.depsFuture == null) {
            this.depsFuture = this.executor.submit(this);
         } else if (this.depsFuture.isDone()) {
            try {
               this.depsFuture.get();
            } catch (Exception var4) {
               throw new GdxRuntimeException("Couldn't load dependencies of asset '" + this.assetDesc.fileName + "'", var4);
            }

            this.dependenciesLoaded = true;
            if (this.asyncDone) {
               this.asset = asyncLoader.loadSync(this.manager, this.assetDesc.fileName, this.assetDesc.params);
            }
         }
      } else if (this.loadFuture == null && !this.asyncDone) {
         this.loadFuture = this.executor.submit(this);
      } else if (this.asyncDone) {
         this.asset = asyncLoader.loadSync(this.manager, this.assetDesc.fileName, this.assetDesc.params);
      } else if (this.loadFuture.isDone()) {
         try {
            this.loadFuture.get();
         } catch (Exception var3) {
            throw new GdxRuntimeException("Couldn't load asset '" + this.assetDesc.fileName + "'", var3);
         }

         this.asset = asyncLoader.loadSync(this.manager, this.assetDesc.fileName, this.assetDesc.params);
      }

   }

   public Object getAsset() {
      return this.asset;
   }
}
