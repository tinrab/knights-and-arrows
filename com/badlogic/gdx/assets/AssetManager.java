package com.badlogic.gdx.assets;

import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.MusicLoader;
import com.badlogic.gdx.assets.loaders.PixmapLoader;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.assets.loaders.SoundLoader;
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.UBJsonReader;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.ThreadUtils;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import java.util.Iterator;
import java.util.Stack;

public class AssetManager implements Disposable {
   final ObjectMap<Class, ObjectMap<String, RefCountedContainer>> assets;
   final ObjectMap<String, Class> assetTypes;
   final ObjectMap<String, Array<String>> assetDependencies;
   final ObjectMap<Class, ObjectMap<String, AssetLoader>> loaders;
   final Array<AssetDescriptor> loadQueue;
   final AsyncExecutor executor;
   Stack<AssetLoadingTask> tasks;
   AssetErrorListener listener;
   int loaded;
   int toLoad;
   Logger log;

   public AssetManager() {
      this(new InternalFileHandleResolver());
   }

   public AssetManager(FileHandleResolver resolver) {
      this.assets = new ObjectMap();
      this.assetTypes = new ObjectMap();
      this.assetDependencies = new ObjectMap();
      this.loaders = new ObjectMap();
      this.loadQueue = new Array();
      this.tasks = new Stack();
      this.listener = null;
      this.loaded = 0;
      this.toLoad = 0;
      this.log = new Logger("AssetManager", 0);
      this.setLoader(BitmapFont.class, new BitmapFontLoader(resolver));
      this.setLoader(Music.class, new MusicLoader(resolver));
      this.setLoader(Pixmap.class, new PixmapLoader(resolver));
      this.setLoader(Sound.class, new SoundLoader(resolver));
      this.setLoader(TextureAtlas.class, new TextureAtlasLoader(resolver));
      this.setLoader(Texture.class, new TextureLoader(resolver));
      this.setLoader(Skin.class, new SkinLoader(resolver));
      this.setLoader(Model.class, ".g3dj", new G3dModelLoader(new JsonReader(), resolver));
      this.setLoader(Model.class, ".g3db", new G3dModelLoader(new UBJsonReader(), resolver));
      this.setLoader(Model.class, ".obj", new ObjLoader(resolver));
      this.executor = new AsyncExecutor(1);
   }

   public synchronized <T> T get(String fileName) {
      Class<T> type = (Class)this.assetTypes.get(fileName);
      ObjectMap<String, RefCountedContainer> assetsByType = (ObjectMap)this.assets.get(type);
      if (assetsByType == null) {
         throw new GdxRuntimeException("Asset not loaded: " + fileName);
      } else {
         RefCountedContainer assetContainer = (RefCountedContainer)assetsByType.get(fileName);
         if (assetContainer == null) {
            throw new GdxRuntimeException("Asset not loaded: " + fileName);
         } else {
            T asset = assetContainer.getObject(type);
            if (asset == null) {
               throw new GdxRuntimeException("Asset not loaded: " + fileName);
            } else {
               return asset;
            }
         }
      }
   }

   public synchronized <T> T get(String fileName, Class<T> type) {
      ObjectMap<String, RefCountedContainer> assetsByType = (ObjectMap)this.assets.get(type);
      if (assetsByType == null) {
         throw new GdxRuntimeException("Asset not loaded: " + fileName);
      } else {
         RefCountedContainer assetContainer = (RefCountedContainer)assetsByType.get(fileName);
         if (assetContainer == null) {
            throw new GdxRuntimeException("Asset not loaded: " + fileName);
         } else {
            T asset = assetContainer.getObject(type);
            if (asset == null) {
               throw new GdxRuntimeException("Asset not loaded: " + fileName);
            } else {
               return asset;
            }
         }
      }
   }

   public synchronized void unload(String fileName) {
      int foundIndex = -1;

      for(int i = 0; i < this.loadQueue.size; ++i) {
         if (((AssetDescriptor)this.loadQueue.get(i)).fileName.equals(fileName)) {
            foundIndex = i;
            break;
         }
      }

      if (foundIndex != -1) {
         this.loadQueue.removeIndex(foundIndex);
         this.log.debug("Unload (from queue): " + fileName);
      } else {
         if (this.tasks.size() > 0) {
            AssetLoadingTask currAsset = (AssetLoadingTask)this.tasks.firstElement();
            if (currAsset.assetDesc.fileName.equals(fileName)) {
               currAsset.cancel = true;
               this.log.debug("Unload (from tasks): " + fileName);
               return;
            }
         }

         Class type = (Class)this.assetTypes.get(fileName);
         if (type == null) {
            throw new GdxRuntimeException("Asset not loaded: " + fileName);
         } else {
            RefCountedContainer assetRef = (RefCountedContainer)((ObjectMap)this.assets.get(type)).get(fileName);
            assetRef.decRefCount();
            if (assetRef.getRefCount() <= 0) {
               this.log.debug("Unload (dispose): " + fileName);
               if (assetRef.getObject(Object.class) instanceof Disposable) {
                  ((Disposable)assetRef.getObject(Object.class)).dispose();
               }

               this.assetTypes.remove(fileName);
               ((ObjectMap)this.assets.get(type)).remove(fileName);
            } else {
               this.log.debug("Unload (decrement): " + fileName);
            }

            Array<String> dependencies = (Array)this.assetDependencies.get(fileName);
            if (dependencies != null) {
               Iterator var7 = dependencies.iterator();

               while(var7.hasNext()) {
                  String dependency = (String)var7.next();
                  this.unload(dependency);
               }
            }

            if (assetRef.getRefCount() <= 0) {
               this.assetDependencies.remove(fileName);
            }

         }
      }
   }

   public synchronized <T> boolean containsAsset(T asset) {
      ObjectMap<String, RefCountedContainer> typedAssets = (ObjectMap)this.assets.get(asset.getClass());
      if (typedAssets == null) {
         return false;
      } else {
         Iterator var4 = typedAssets.keys().iterator();

         Object otherAsset;
         do {
            if (!var4.hasNext()) {
               return false;
            }

            String fileName = (String)var4.next();
            otherAsset = ((RefCountedContainer)typedAssets.get(fileName)).getObject(Object.class);
         } while(otherAsset != asset && !asset.equals(otherAsset));

         return true;
      }
   }

   public synchronized <T> String getAssetFileName(T asset) {
      Iterator var3 = this.assets.keys().iterator();

      while(var3.hasNext()) {
         Class assetType = (Class)var3.next();
         ObjectMap<String, RefCountedContainer> typedAssets = (ObjectMap)this.assets.get(assetType);
         Iterator var6 = typedAssets.keys().iterator();

         while(var6.hasNext()) {
            String fileName = (String)var6.next();
            T otherAsset = ((RefCountedContainer)typedAssets.get(fileName)).getObject(Object.class);
            if (otherAsset == asset || asset.equals(otherAsset)) {
               return fileName;
            }
         }
      }

      return null;
   }

   public synchronized boolean isLoaded(String fileName) {
      return fileName == null ? false : this.assetTypes.containsKey(fileName);
   }

   public synchronized boolean isLoaded(String fileName, Class type) {
      ObjectMap<String, RefCountedContainer> assetsByType = (ObjectMap)this.assets.get(type);
      if (assetsByType == null) {
         return false;
      } else {
         RefCountedContainer assetContainer = (RefCountedContainer)assetsByType.get(fileName);
         if (assetContainer == null) {
            return false;
         } else {
            return assetContainer.getObject(type) != null;
         }
      }
   }

   public <T> AssetLoader getLoader(Class<T> type) {
      return this.getLoader(type, (String)null);
   }

   public <T> AssetLoader getLoader(Class<T> type, String fileName) {
      ObjectMap<String, AssetLoader> loaders = (ObjectMap)this.loaders.get(type);
      if (loaders != null && loaders.size >= 1) {
         if (fileName == null) {
            return (AssetLoader)loaders.get("");
         } else {
            AssetLoader result = null;
            int l = -1;
            Iterator var7 = loaders.entries().iterator();

            while(var7.hasNext()) {
               ObjectMap.Entry<String, AssetLoader> entry = (ObjectMap.Entry)var7.next();
               if (((String)entry.key).length() > l && fileName.endsWith((String)entry.key)) {
                  result = (AssetLoader)entry.value;
                  l = ((String)entry.key).length();
               }
            }

            return result;
         }
      } else {
         return null;
      }
   }

   public synchronized <T> void load(String fileName, Class<T> type) {
      this.load(fileName, type, (AssetLoaderParameters)null);
   }

   public synchronized <T> void load(String fileName, Class<T> type, AssetLoaderParameters<T> parameter) {
      AssetLoader loader = this.getLoader(type, fileName);
      if (loader == null) {
         throw new GdxRuntimeException("No loader for type: " + ClassReflection.getSimpleName(type));
      } else {
         if (this.loadQueue.size == 0) {
            this.loaded = 0;
            this.toLoad = 0;
         }

         int i;
         AssetDescriptor assetDesc;
         for(i = 0; i < this.loadQueue.size; ++i) {
            assetDesc = (AssetDescriptor)this.loadQueue.get(i);
            if (assetDesc.fileName.equals(fileName) && !assetDesc.type.equals(type)) {
               throw new GdxRuntimeException("Asset with name '" + fileName + "' already in preload queue, but has different type (expected: " + ClassReflection.getSimpleName(type) + ", found: " + ClassReflection.getSimpleName(assetDesc.type) + ")");
            }
         }

         for(i = 0; i < this.tasks.size(); ++i) {
            assetDesc = ((AssetLoadingTask)this.tasks.get(i)).assetDesc;
            if (assetDesc.fileName.equals(fileName) && !assetDesc.type.equals(type)) {
               throw new GdxRuntimeException("Asset with name '" + fileName + "' already in task list, but has different type (expected: " + ClassReflection.getSimpleName(type) + ", found: " + ClassReflection.getSimpleName(assetDesc.type) + ")");
            }
         }

         Class otherType = (Class)this.assetTypes.get(fileName);
         if (otherType != null && !otherType.equals(type)) {
            throw new GdxRuntimeException("Asset with name '" + fileName + "' already loaded, but has different type (expected: " + ClassReflection.getSimpleName(type) + ", found: " + ClassReflection.getSimpleName(otherType) + ")");
         } else {
            ++this.toLoad;
            assetDesc = new AssetDescriptor(fileName, type, parameter);
            this.loadQueue.add(assetDesc);
            this.log.debug("Queued: " + assetDesc);
         }
      }
   }

   public synchronized void load(AssetDescriptor desc) {
      this.load(desc.fileName, desc.type, desc.params);
   }

   private void disposeDependencies(String fileName) {
      Array<String> dependencies = (Array)this.assetDependencies.get(fileName);
      if (dependencies != null) {
         Iterator var4 = dependencies.iterator();

         while(var4.hasNext()) {
            String dependency = (String)var4.next();
            this.disposeDependencies(dependency);
         }
      }

      Class type = (Class)this.assetTypes.get(fileName);
      Object asset = ((RefCountedContainer)((ObjectMap)this.assets.get(type)).get(fileName)).getObject(Object.class);
      if (asset instanceof Disposable) {
         ((Disposable)asset).dispose();
      }

   }

   public synchronized boolean update() {
      try {
         if (this.tasks.size() == 0) {
            while(true) {
               if (this.loadQueue.size == 0 || this.tasks.size() != 0) {
                  if (this.tasks.size() == 0) {
                     return true;
                  }
                  break;
               }

               this.nextTask();
            }
         }

         return this.updateTask() && this.loadQueue.size == 0 && this.tasks.size() == 0;
      } catch (Throwable var2) {
         this.handleTaskError(var2);
         return this.loadQueue.size == 0;
      }
   }

   public synchronized boolean update(int millis) {
      long endTime = System.currentTimeMillis() + (long)millis;

      while(true) {
         boolean done = this.update();
         if (done || System.currentTimeMillis() > endTime) {
            return done;
         }

         ThreadUtils.yield();
      }
   }

   public void finishLoading() {
      this.log.debug("Waiting for loading to complete...");

      while(!this.update()) {
         ThreadUtils.yield();
      }

      this.log.debug("Loading complete.");
   }

   synchronized void injectDependencies(String parentAssetFilename, Array<AssetDescriptor> dependendAssetDescs) {
      Iterator var4 = dependendAssetDescs.iterator();

      while(var4.hasNext()) {
         AssetDescriptor desc = (AssetDescriptor)var4.next();
         this.injectDependency(parentAssetFilename, desc);
      }

   }

   private synchronized void injectDependency(String parentAssetFilename, AssetDescriptor dependendAssetDesc) {
      Array<String> dependencies = (Array)this.assetDependencies.get(parentAssetFilename);
      if (dependencies == null) {
         dependencies = new Array();
         this.assetDependencies.put(parentAssetFilename, dependencies);
      }

      dependencies.add(dependendAssetDesc.fileName);
      if (this.isLoaded(dependendAssetDesc.fileName)) {
         this.log.debug("Dependency already loaded: " + dependendAssetDesc);
         Class type = (Class)this.assetTypes.get(dependendAssetDesc.fileName);
         RefCountedContainer assetRef = (RefCountedContainer)((ObjectMap)this.assets.get(type)).get(dependendAssetDesc.fileName);
         assetRef.incRefCount();
         this.incrementRefCountedDependencies(dependendAssetDesc.fileName);
      } else {
         this.log.info("Loading dependency: " + dependendAssetDesc);
         this.addTask(dependendAssetDesc);
      }

   }

   private void nextTask() {
      AssetDescriptor assetDesc = (AssetDescriptor)this.loadQueue.removeIndex(0);
      if (this.isLoaded(assetDesc.fileName)) {
         this.log.debug("Already loaded: " + assetDesc);
         Class type = (Class)this.assetTypes.get(assetDesc.fileName);
         RefCountedContainer assetRef = (RefCountedContainer)((ObjectMap)this.assets.get(type)).get(assetDesc.fileName);
         assetRef.incRefCount();
         this.incrementRefCountedDependencies(assetDesc.fileName);
         ++this.loaded;
      } else {
         this.log.info("Loading: " + assetDesc);
         this.addTask(assetDesc);
      }

   }

   private void addTask(AssetDescriptor assetDesc) {
      AssetLoader loader = this.getLoader(assetDesc.type, assetDesc.fileName);
      if (loader == null) {
         throw new GdxRuntimeException("No loader for type: " + ClassReflection.getSimpleName(assetDesc.type));
      } else {
         this.tasks.push(new AssetLoadingTask(this, assetDesc, loader, this.executor));
      }
   }

   protected <T> void addAsset(String fileName, Class<T> type, T asset) {
      this.assetTypes.put(fileName, type);
      ObjectMap<String, RefCountedContainer> typeToAssets = (ObjectMap)this.assets.get(type);
      if (typeToAssets == null) {
         typeToAssets = new ObjectMap();
         this.assets.put(type, typeToAssets);
      }

      typeToAssets.put(fileName, new RefCountedContainer(asset));
   }

   private boolean updateTask() {
      AssetLoadingTask task = (AssetLoadingTask)this.tasks.peek();
      if (task.update()) {
         this.addAsset(task.assetDesc.fileName, task.assetDesc.type, task.getAsset());
         if (this.tasks.size() == 1) {
            ++this.loaded;
         }

         this.tasks.pop();
         if (task.cancel) {
            this.unload(task.assetDesc.fileName);
         } else {
            if (task.assetDesc.params != null && task.assetDesc.params.loadedCallback != null) {
               task.assetDesc.params.loadedCallback.finishedLoading(this, task.assetDesc.fileName, task.assetDesc.type);
            }

            long endTime = TimeUtils.nanoTime();
            this.log.debug("Loaded: " + (float)(endTime - task.startTime) / 1000000.0F + "ms " + task.assetDesc);
         }

         return true;
      } else {
         return false;
      }
   }

   private void incrementRefCountedDependencies(String parent) {
      Array<String> dependencies = (Array)this.assetDependencies.get(parent);
      if (dependencies != null) {
         Iterator var4 = dependencies.iterator();

         while(var4.hasNext()) {
            String dependency = (String)var4.next();
            Class type = (Class)this.assetTypes.get(dependency);
            RefCountedContainer assetRef = (RefCountedContainer)((ObjectMap)this.assets.get(type)).get(dependency);
            assetRef.incRefCount();
            this.incrementRefCountedDependencies(dependency);
         }

      }
   }

   private void handleTaskError(Throwable t) {
      this.log.error("Error loading asset.", t);
      if (this.tasks.isEmpty()) {
         throw new GdxRuntimeException(t);
      } else {
         AssetLoadingTask task = (AssetLoadingTask)this.tasks.pop();
         AssetDescriptor assetDesc = task.assetDesc;
         if (task.dependenciesLoaded && task.dependencies != null) {
            Iterator var5 = task.dependencies.iterator();

            while(var5.hasNext()) {
               AssetDescriptor desc = (AssetDescriptor)var5.next();
               this.unload(desc.fileName);
            }
         }

         this.tasks.clear();
         if (this.listener != null) {
            this.listener.error(assetDesc.fileName, assetDesc.type, t);
         } else {
            throw new GdxRuntimeException(t);
         }
      }
   }

   public synchronized <T, P extends AssetLoaderParameters<T>> void setLoader(Class<T> type, AssetLoader<T, P> loader) {
      this.setLoader(type, (String)null, loader);
   }

   public synchronized <T, P extends AssetLoaderParameters<T>> void setLoader(Class<T> type, String suffix, AssetLoader<T, P> loader) {
      if (type == null) {
         throw new IllegalArgumentException("type cannot be null.");
      } else if (loader == null) {
         throw new IllegalArgumentException("loader cannot be null.");
      } else {
         this.log.debug("Loader set: " + ClassReflection.getSimpleName(type) + " -> " + ClassReflection.getSimpleName(loader.getClass()));
         ObjectMap<String, AssetLoader> loaders = (ObjectMap)this.loaders.get(type);
         if (loaders == null) {
            this.loaders.put(type, loaders = new ObjectMap());
         }

         loaders.put(suffix == null ? "" : suffix, loader);
      }
   }

   public synchronized int getLoadedAssets() {
      return this.assetTypes.size;
   }

   public synchronized int getQueuedAssets() {
      return this.loadQueue.size + this.tasks.size();
   }

   public synchronized float getProgress() {
      return this.toLoad == 0 ? 1.0F : Math.min(1.0F, (float)this.loaded / (float)this.toLoad);
   }

   public synchronized void setErrorListener(AssetErrorListener listener) {
      this.listener = listener;
   }

   public synchronized void dispose() {
      this.log.debug("Disposing.");
      this.clear();
      this.executor.dispose();
   }

   public synchronized void clear() {
      this.loadQueue.clear();

      while(!this.update()) {
      }

      ObjectIntMap dependencyCount = new ObjectIntMap();

      label52:
      while(this.assetTypes.size > 0) {
         dependencyCount.clear();
         Array<String> assets = this.assetTypes.keys().toArray();
         Iterator var4 = assets.iterator();

         String asset;
         while(var4.hasNext()) {
            asset = (String)var4.next();
            dependencyCount.put(asset, 0);
         }

         var4 = assets.iterator();

         while(true) {
            Array dependencies;
            do {
               if (!var4.hasNext()) {
                  var4 = assets.iterator();

                  while(var4.hasNext()) {
                     asset = (String)var4.next();
                     if (dependencyCount.get(asset, 0) == 0) {
                        this.unload(asset);
                     }
                  }
                  continue label52;
               }

               asset = (String)var4.next();
               dependencies = (Array)this.assetDependencies.get(asset);
            } while(dependencies == null);

            Iterator var7 = dependencies.iterator();

            while(var7.hasNext()) {
               String dependency = (String)var7.next();
               int count = dependencyCount.get(dependency, 0);
               ++count;
               dependencyCount.put(dependency, count);
            }
         }
      }

      this.assets.clear();
      this.assetTypes.clear();
      this.assetDependencies.clear();
      this.loaded = 0;
      this.toLoad = 0;
      this.loadQueue.clear();
      this.tasks.clear();
   }

   public Logger getLogger() {
      return this.log;
   }

   public synchronized int getReferenceCount(String fileName) {
      Class type = (Class)this.assetTypes.get(fileName);
      if (type == null) {
         throw new GdxRuntimeException("Asset not loaded: " + fileName);
      } else {
         return ((RefCountedContainer)((ObjectMap)this.assets.get(type)).get(fileName)).getRefCount();
      }
   }

   public synchronized void setReferenceCount(String fileName, int refCount) {
      Class type = (Class)this.assetTypes.get(fileName);
      if (type == null) {
         throw new GdxRuntimeException("Asset not loaded: " + fileName);
      } else {
         ((RefCountedContainer)((ObjectMap)this.assets.get(type)).get(fileName)).setRefCount(refCount);
      }
   }

   public synchronized String getDiagnostics() {
      StringBuffer buffer = new StringBuffer();

      for(Iterator var3 = this.assetTypes.keys().iterator(); var3.hasNext(); buffer.append("\n")) {
         String fileName = (String)var3.next();
         buffer.append(fileName);
         buffer.append(", ");
         Class type = (Class)this.assetTypes.get(fileName);
         RefCountedContainer assetRef = (RefCountedContainer)((ObjectMap)this.assets.get(type)).get(fileName);
         Array<String> dependencies = (Array)this.assetDependencies.get(fileName);
         buffer.append(ClassReflection.getSimpleName(type));
         buffer.append(", refs: ");
         buffer.append(assetRef.getRefCount());
         if (dependencies != null) {
            buffer.append(", deps: [");
            Iterator var8 = dependencies.iterator();

            while(var8.hasNext()) {
               String dep = (String)var8.next();
               buffer.append(dep);
               buffer.append(",");
            }

            buffer.append("]");
         }
      }

      return buffer.toString();
   }

   public synchronized Array<String> getAssetNames() {
      return this.assetTypes.keys().toArray();
   }

   public synchronized Array<String> getDependencies(String fileName) {
      return (Array)this.assetDependencies.get(fileName);
   }

   public synchronized Class getAssetType(String fileName) {
      return (Class)this.assetTypes.get(fileName);
   }
}
