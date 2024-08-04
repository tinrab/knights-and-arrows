package com.badlogic.gdx.graphics.g3d;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.lights.Lights;
import com.badlogic.gdx.graphics.g3d.utils.DefaultRenderableSorter;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.g3d.utils.RenderableSorter;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import java.util.Iterator;

public class ModelBatch implements Disposable {
   protected Camera camera;
   protected final Pool<Renderable> renderablesPool;
   protected final Array<Renderable> renderables;
   protected final Array<Renderable> reuseableRenderables;
   protected final RenderContext context;
   protected final ShaderProvider shaderProvider;
   protected final RenderableSorter sorter;

   public ModelBatch(RenderContext context, ShaderProvider shaderProvider, RenderableSorter sorter) {
      this.renderablesPool = new Pool<Renderable>() {
         protected Renderable newObject() {
            return new Renderable();
         }

         public Renderable obtain() {
            Renderable renderable = (Renderable)super.obtain();
            renderable.lights = null;
            renderable.material = null;
            renderable.mesh = null;
            renderable.shader = null;
            return renderable;
         }
      };
      this.renderables = new Array();
      this.reuseableRenderables = new Array();
      this.context = context;
      this.shaderProvider = shaderProvider;
      this.sorter = sorter;
   }

   public ModelBatch(ShaderProvider shaderProvider) {
      this(new RenderContext(new DefaultTextureBinder(0, 1)), shaderProvider, new DefaultRenderableSorter());
   }

   public ModelBatch(FileHandle vertexShader, FileHandle fragmentShader) {
      this(new DefaultShaderProvider(vertexShader, fragmentShader));
   }

   public ModelBatch(String vertexShader, String fragmentShader) {
      this(new DefaultShaderProvider(vertexShader, fragmentShader));
   }

   public ModelBatch() {
      this(new RenderContext(new DefaultTextureBinder(0, 1)), new DefaultShaderProvider(), new DefaultRenderableSorter());
   }

   public void begin(Camera cam) {
      this.camera = cam;
   }

   public void end() {
      this.sorter.sort(this.camera, this.renderables);
      this.context.begin();
      Shader currentShader = null;

      for(int i = 0; i < this.renderables.size; ++i) {
         Renderable renderable = (Renderable)this.renderables.get(i);
         if (currentShader != renderable.shader) {
            if (currentShader != null) {
               currentShader.end();
            }

            currentShader = renderable.shader;
            currentShader.begin(this.camera, this.context);
         }

         currentShader.render(renderable);
      }

      if (currentShader != null) {
         currentShader.end();
      }

      this.context.end();
      this.renderablesPool.freeAll(this.reuseableRenderables);
      this.reuseableRenderables.clear();
      this.renderables.clear();
      this.camera = null;
   }

   public void render(Renderable renderable) {
      renderable.shader = this.shaderProvider.getShader(renderable);
      renderable.mesh.setAutoBind(false);
      this.renderables.add(renderable);
   }

   public void render(RenderableProvider renderableProvider) {
      this.render((RenderableProvider)renderableProvider, (Lights)null, (Shader)null);
   }

   public <T extends RenderableProvider> void render(Iterable<T> renderableProviders) {
      this.render((Iterable)renderableProviders, (Lights)null, (Shader)null);
   }

   public void render(RenderableProvider renderableProvider, Lights lights) {
      this.render((RenderableProvider)renderableProvider, lights, (Shader)null);
   }

   public <T extends RenderableProvider> void render(Iterable<T> renderableProviders, Lights lights) {
      this.render((Iterable)renderableProviders, lights, (Shader)null);
   }

   public void render(RenderableProvider renderableProvider, Shader shader) {
      this.render((RenderableProvider)renderableProvider, (Lights)null, shader);
   }

   public <T extends RenderableProvider> void render(Iterable<T> renderableProviders, Shader shader) {
      this.render((Iterable)renderableProviders, (Lights)null, shader);
   }

   public void render(RenderableProvider renderableProvider, Lights lights, Shader shader) {
      int offset = this.renderables.size;
      renderableProvider.getRenderables(this.renderables, this.renderablesPool);

      for(int i = offset; i < this.renderables.size; ++i) {
         Renderable renderable = (Renderable)this.renderables.get(i);
         renderable.lights = lights;
         renderable.shader = shader;
         renderable.shader = this.shaderProvider.getShader(renderable);
         this.reuseableRenderables.add(renderable);
      }

   }

   public <T extends RenderableProvider> void render(Iterable<T> renderableProviders, Lights lights, Shader shader) {
      Iterator var5 = renderableProviders.iterator();

      while(var5.hasNext()) {
         RenderableProvider renderableProvider = (RenderableProvider)var5.next();
         this.render(renderableProvider, lights, shader);
      }

   }

   public void dispose() {
      this.shaderProvider.dispose();
   }
}
