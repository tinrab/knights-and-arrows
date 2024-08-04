package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.utils.Array;
import java.util.Iterator;

public abstract class BaseShaderProvider implements ShaderProvider {
   protected Array<Shader> shaders = new Array();

   public Shader getShader(Renderable renderable) {
      Shader suggestedShader = renderable.shader;
      if (suggestedShader != null && suggestedShader.canRender(renderable)) {
         return suggestedShader;
      } else {
         Iterator var4 = this.shaders.iterator();

         Shader shader;
         while(var4.hasNext()) {
            shader = (Shader)var4.next();
            if (shader.canRender(renderable)) {
               return shader;
            }
         }

         shader = this.createShader(renderable);
         shader.init();
         this.shaders.add(shader);
         return shader;
      }
   }

   protected abstract Shader createShader(Renderable var1);

   public void dispose() {
      Iterator var2 = this.shaders.iterator();

      while(var2.hasNext()) {
         Shader shader = (Shader)var2.next();
         shader.dispose();
      }

   }
}
