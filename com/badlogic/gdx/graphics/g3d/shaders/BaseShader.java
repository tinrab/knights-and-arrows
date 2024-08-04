package com.badlogic.gdx.graphics.g3d.shaders;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.util.Iterator;

public abstract class BaseShader implements Shader {
   public static final int VERTEX_ATTRIBUTE = 1;
   public static final int GLOBAL_UNIFORM = 2;
   public static final int LOCAL_UNIFORM = 3;
   private final Array<BaseShader.Input> inputs = new Array();
   public final Array<BaseShader.Input> vertexAttributes = new Array();
   public final Array<BaseShader.Input> globalUniforms = new Array();
   public final Array<BaseShader.Input> localUniforms = new Array();
   public ShaderProgram program;
   public RenderContext context;
   public Camera camera;

   public BaseShader.Input register(BaseShader.Input input) {
      if (this.program != null) {
         throw new GdxRuntimeException("Cannot register input after initialization");
      } else {
         BaseShader.Input existing = this.getInput(input.name);
         if (existing != null) {
            if (existing.scope != input.scope) {
               throw new GdxRuntimeException(input.name + ": An input with the same name but different scope is already registered.");
            } else {
               return existing;
            }
         } else {
            this.inputs.add(input);
            return input;
         }
      }
   }

   public Iterable<BaseShader.Input> getInputs() {
      return this.inputs;
   }

   public BaseShader.Input getInput(String alias) {
      Iterator var3 = this.inputs.iterator();

      while(var3.hasNext()) {
         BaseShader.Input input = (BaseShader.Input)var3.next();
         if (alias.equals(input.name)) {
            return input;
         }
      }

      return null;
   }

   public void init(ShaderProgram program, long materialMask, long vertexMask, long userMask) {
      if (this.program != null) {
         throw new GdxRuntimeException("Already initialized");
      } else if (!program.isCompiled()) {
         throw new GdxRuntimeException(program.getLog());
      } else {
         this.program = program;
         Iterator var9 = this.inputs.iterator();

         while(var9.hasNext()) {
            BaseShader.Input input = (BaseShader.Input)var9.next();
            if (input.compare(materialMask, vertexMask, userMask)) {
               if (input.scope == 2) {
                  input.location = program.fetchUniformLocation(input.name, false);
                  if (input.location >= 0 && input.setter != null) {
                     this.globalUniforms.add(input);
                  }
               } else if (input.scope == 3) {
                  input.location = program.fetchUniformLocation(input.name, false);
                  if (input.location >= 0 && input.setter != null) {
                     this.localUniforms.add(input);
                  }
               } else if (input.scope == 1) {
                  input.location = program.getAttributeLocation(input.name);
                  if (input.location >= 0) {
                     this.vertexAttributes.add(input);
                  }
               } else {
                  input.location = -1;
               }
            } else {
               input.location = -1;
            }
         }

      }
   }

   public void begin(Camera camera, RenderContext context) {
      this.camera = camera;
      this.context = context;
      this.program.begin();
      Iterator var4 = this.globalUniforms.iterator();

      while(var4.hasNext()) {
         BaseShader.Input input = (BaseShader.Input)var4.next();
         input.setter.set(this, this.program, input, camera, context, (Renderable)null);
      }

   }

   public void render(Renderable renderable) {
      Iterator var3 = this.localUniforms.iterator();

      while(var3.hasNext()) {
         BaseShader.Input input = (BaseShader.Input)var3.next();
         input.setter.set(this, this.program, input, this.camera, this.context, renderable);
      }

      renderable.mesh.render(this.program, renderable.primitiveType, renderable.meshPartOffset, renderable.meshPartSize);
   }

   public void end() {
      this.program.end();
   }

   public void dispose() {
      this.program = null;
      this.inputs.clear();
      this.vertexAttributes.clear();
      this.localUniforms.clear();
      this.globalUniforms.clear();
   }

   public final boolean has(BaseShader.Input input) {
      return input.location >= 0;
   }

   public final boolean set(BaseShader.Input uniform, Matrix4 value) {
      if (uniform.location < 0) {
         return false;
      } else {
         this.program.setUniformMatrix(uniform.location, value);
         return true;
      }
   }

   public final boolean set(BaseShader.Input uniform, Matrix3 value) {
      if (uniform.location < 0) {
         return false;
      } else {
         this.program.setUniformMatrix(uniform.location, value);
         return true;
      }
   }

   public final boolean set(BaseShader.Input uniform, Vector3 value) {
      if (uniform.location < 0) {
         return false;
      } else {
         this.program.setUniformf(uniform.location, value);
         return true;
      }
   }

   public final boolean set(BaseShader.Input uniform, Vector2 value) {
      if (uniform.location < 0) {
         return false;
      } else {
         this.program.setUniformf(uniform.location, value);
         return true;
      }
   }

   public final boolean set(BaseShader.Input uniform, Color value) {
      if (uniform.location < 0) {
         return false;
      } else {
         this.program.setUniformf(uniform.location, value);
         return true;
      }
   }

   public final boolean set(BaseShader.Input uniform, float value) {
      if (uniform.location < 0) {
         return false;
      } else {
         this.program.setUniformf(uniform.location, value);
         return true;
      }
   }

   public final boolean set(BaseShader.Input uniform, float v1, float v2) {
      if (uniform.location < 0) {
         return false;
      } else {
         this.program.setUniformf(uniform.location, v1, v2);
         return true;
      }
   }

   public final boolean set(BaseShader.Input uniform, float v1, float v2, float v3) {
      if (uniform.location < 0) {
         return false;
      } else {
         this.program.setUniformf(uniform.location, v1, v2, v3);
         return true;
      }
   }

   public final boolean set(BaseShader.Input uniform, float v1, float v2, float v3, float v4) {
      if (uniform.location < 0) {
         return false;
      } else {
         this.program.setUniformf(uniform.location, v1, v2, v3, v4);
         return true;
      }
   }

   public final boolean set(BaseShader.Input uniform, int value) {
      if (uniform.location < 0) {
         return false;
      } else {
         this.program.setUniformi(uniform.location, value);
         return true;
      }
   }

   public final boolean set(BaseShader.Input uniform, int v1, int v2) {
      if (uniform.location < 0) {
         return false;
      } else {
         this.program.setUniformi(uniform.location, v1, v2);
         return true;
      }
   }

   public final boolean set(BaseShader.Input uniform, int v1, int v2, int v3) {
      if (uniform.location < 0) {
         return false;
      } else {
         this.program.setUniformi(uniform.location, v1, v2, v3);
         return true;
      }
   }

   public final boolean set(BaseShader.Input uniform, int v1, int v2, int v3, int v4) {
      if (uniform.location < 0) {
         return false;
      } else {
         this.program.setUniformi(uniform.location, v1, v2, v3, v4);
         return true;
      }
   }

   public static class Input {
      public final int scope;
      public final String name;
      public final long materialFlags;
      public final long vertexFlags;
      public final long userFlags;
      public final BaseShader.Input.Setter setter;
      public int location;

      public boolean compare(long materialMask, long vertexMask, long userMask) {
         return (materialMask & this.materialFlags) == this.materialFlags && (vertexMask & this.vertexFlags) == this.vertexFlags && (userMask & this.userFlags) == this.userFlags;
      }

      public Input(int scope, String name, long materialFlags, long vertexFlags, long userFlags, BaseShader.Input.Setter setter) {
         this.location = -1;
         this.scope = scope;
         this.name = name;
         this.materialFlags = materialFlags;
         this.vertexFlags = vertexFlags;
         this.userFlags = userFlags;
         this.setter = setter;
      }

      public Input(int scope, String name, long materialFlags, long vertexFlags, long userFlags) {
         this(scope, name, materialFlags, vertexFlags, userFlags, (BaseShader.Input.Setter)null);
      }

      public Input(int scope, String name, long materialFlags, long vertexFlags, BaseShader.Input.Setter setter) {
         this(scope, name, materialFlags, vertexFlags, 0L, setter);
      }

      public Input(int scope, String name, long materialFlags, long vertexFlags) {
         this(scope, name, materialFlags, vertexFlags, 0L);
      }

      public Input(int scope, String name, long materialFlags, BaseShader.Input.Setter setter) {
         this(scope, name, materialFlags, 0L, 0L, setter);
      }

      public Input(int scope, String name, long materialFlags) {
         this(scope, name, materialFlags, 0L, 0L);
      }

      public Input(int scope, String name, BaseShader.Input.Setter setter) {
         this(scope, name, 0L, 0L, 0L, setter);
      }

      public Input(int scope, String name) {
         this(scope, name, 0L, 0L, 0L);
      }

      public interface Setter {
         void set(BaseShader var1, ShaderProgram var2, BaseShader.Input var3, Camera var4, RenderContext var5, Renderable var6);
      }
   }
}
