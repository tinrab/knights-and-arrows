package com.badlogic.gdx.assets.loaders;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMaterial;
import com.badlogic.gdx.graphics.g3d.model.data.ModelTexture;
import com.badlogic.gdx.graphics.g3d.utils.TextureProvider;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import java.util.Iterator;

public abstract class ModelLoader<P extends AssetLoaderParameters<Model>> extends AsynchronousAssetLoader<Model, P> {
   protected Array<ObjectMap.Entry<String, ModelData>> items = new Array();

   public ModelLoader(FileHandleResolver resolver) {
      super(resolver);
   }

   public abstract ModelData loadModelData(FileHandle var1, P var2);

   public ModelData loadModelData(FileHandle fileHandle) {
      return this.loadModelData(fileHandle, (AssetLoaderParameters)null);
   }

   public Model loadModel(FileHandle fileHandle, TextureProvider textureProvider, P parameters) {
      ModelData data = this.loadModelData(fileHandle, parameters);
      return data == null ? null : new Model(data, textureProvider);
   }

   public Model loadModel(FileHandle fileHandle, P parameters) {
      return this.loadModel(fileHandle, new TextureProvider.FileTextureProvider(), parameters);
   }

   public Model loadModel(FileHandle fileHandle, TextureProvider textureProvider) {
      return this.loadModel(fileHandle, textureProvider, (AssetLoaderParameters)null);
   }

   public Model loadModel(FileHandle fileHandle) {
      return this.loadModel(fileHandle, new TextureProvider.FileTextureProvider(), (AssetLoaderParameters)null);
   }

   public Array<AssetDescriptor> getDependencies(String fileName, P parameters) {
      Array<AssetDescriptor> deps = new Array();
      ModelData data = this.loadModelData(this.resolve(fileName), parameters);
      if (data == null) {
         return deps;
      } else {
         ObjectMap.Entry<String, ModelData> item = new ObjectMap.Entry();
         item.key = fileName;
         item.value = data;
         synchronized(this.items) {
            this.items.add(item);
         }

         Iterator var7 = data.materials.iterator();

         while(true) {
            ModelMaterial modelMaterial;
            do {
               if (!var7.hasNext()) {
                  return deps;
               }

               modelMaterial = (ModelMaterial)var7.next();
            } while(modelMaterial.textures == null);

            Iterator var9 = modelMaterial.textures.iterator();

            while(var9.hasNext()) {
               ModelTexture modelTexture = (ModelTexture)var9.next();
               deps.add(new AssetDescriptor(modelTexture.fileName, Texture.class));
            }
         }
      }
   }

   public void loadAsync(AssetManager manager, String fileName, P parameters) {
   }

   public Model loadSync(AssetManager manager, String fileName, P parameters) {
      ModelData data = null;
      synchronized(this.items) {
         for(int i = 0; i < this.items.size; ++i) {
            if (((String)((ObjectMap.Entry)this.items.get(i)).key).equals(fileName)) {
               data = (ModelData)((ObjectMap.Entry)this.items.get(i)).value;
               this.items.removeIndex(i);
            }
         }
      }

      if (data == null) {
         return null;
      } else {
         Model result = new Model(data, new TextureProvider.AssetTextureProvider(manager));
         Iterator disposables = result.getManagedDisposables().iterator();

         while(disposables.hasNext()) {
            Disposable disposable = (Disposable)disposables.next();
            if (disposable instanceof Texture) {
               disposables.remove();
            }
         }

         data = null;
         return result;
      }
   }
}
