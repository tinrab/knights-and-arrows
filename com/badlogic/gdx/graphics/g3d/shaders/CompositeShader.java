package com.badlogic.gdx.graphics.g3d.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.subshaders.DiffuseColorTextureShader;
import com.badlogic.gdx.graphics.g3d.shaders.subshaders.SubShader;
import com.badlogic.gdx.graphics.g3d.shaders.subshaders.TransformShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.util.Iterator;

public class CompositeShader implements Shader {
   private Camera camera;
   private RenderContext context;
   private ShaderProgram program;
   private final Array<SubShader> subShaders = new Array();
   private long attributesMask;
   private long materialMask;
   private boolean lightingEnabled;
   private final Matrix3 normalMatrix = new Matrix3();

   public CompositeShader(Renderable renderable) {
      this.subShaders.add(new TransformShader());
      this.subShaders.add(new DiffuseColorTextureShader());
      this.init(renderable);
   }

   public CompositeShader(Renderable renderable, Array<SubShader> shaders) {
      this.subShaders.addAll(shaders);
      this.init(renderable);
   }

   private void init(Renderable renderable) {
      this.materialMask = renderable.material.getMask();
      this.attributesMask = renderable.mesh.getVertexAttributes().getMask();
      this.lightingEnabled = renderable.lights != null;
      Iterator var3 = this.subShaders.iterator();

      while(var3.hasNext()) {
         SubShader subShader = (SubShader)var3.next();
         subShader.init(renderable);
      }

      StringBuffer vertexShader = new StringBuffer();
      StringBuffer fragmentShader = new StringBuffer();
      fragmentShader.append("#ifdef GL_ES\n  #define LOWP lowp\n  #define MED mediump\n  #define HIGH highp\n  precision mediump float;\n#else\n  #define MED\n  #define LOWP\n  #define HIGH\n#endif\n\n");
      vertexShader.append("uniform mat4 u_projTrans;\n");
      vertexShader.append("uniform vec3 u_cameraPosition;\n");
      vertexShader.append("uniform vec3 u_cameraDirection;\n");
      vertexShader.append("uniform vec3 u_cameraUp;\n");
      vertexShader.append("uniform mat3 u_normalMatrix;\n");
      Iterator var5 = this.subShaders.iterator();

      SubShader subShader;
      String line;
      int var7;
      int var8;
      String[] var9;
      while(var5.hasNext()) {
         subShader = (SubShader)var5.next();
         var8 = (var9 = subShader.getVertexShaderVars()).length;

         for(var7 = 0; var7 < var8; ++var7) {
            line = var9[var7];
            vertexShader.append(line);
            vertexShader.append("\n");
         }

         vertexShader.append("\n");
         var8 = (var9 = subShader.getFragmentShaderVars()).length;

         for(var7 = 0; var7 < var8; ++var7) {
            line = var9[var7];
            fragmentShader.append(line);
            fragmentShader.append("\n");
         }

         fragmentShader.append("\n");
      }

      vertexShader.append("void main() {\n");
      var5 = this.subShaders.iterator();

      while(var5.hasNext()) {
         subShader = (SubShader)var5.next();
         var8 = (var9 = subShader.getVertexShaderCode()).length;

         for(var7 = 0; var7 < var8; ++var7) {
            line = var9[var7];
            vertexShader.append("  ");
            vertexShader.append(line);
            vertexShader.append("\n");
         }
      }

      vertexShader.append("  gl_Position = position;\n");
      vertexShader.append("}");
      fragmentShader.append("void main() {\n");
      var5 = this.subShaders.iterator();

      while(var5.hasNext()) {
         subShader = (SubShader)var5.next();
         var8 = (var9 = subShader.getFragmentShaderCode()).length;

         for(var7 = 0; var7 < var8; ++var7) {
            line = var9[var7];
            fragmentShader.append("  ");
            fragmentShader.append(line);
            fragmentShader.append("\n");
         }
      }

      fragmentShader.append("  gl_FragColor = color;\n");
      fragmentShader.append("}");
      this.program = new ShaderProgram(vertexShader.toString(), fragmentShader.toString());
      if (!this.program.isCompiled()) {
         throw new GdxRuntimeException("Couldn't compile composite shader\n------ vertex shader ------\n" + vertexShader + "\n" + "------ fragment shader ------\n" + fragmentShader + "\n" + "------ error log ------\n" + this.program.getLog());
      } else {
         Gdx.app.log("CompositeShader", "\n------ vertex shader ------\n" + vertexShader + "\n" + "------ fragment shader ------\n" + fragmentShader + "\n" + "------ error log ------\n" + this.program.getLog());
      }
   }

   public void init() {
   }

   public int compareTo(Shader other) {
      return 0;
   }

   public boolean canRender(Renderable renderable) {
      return this.materialMask == renderable.material.getMask() && this.attributesMask == renderable.mesh.getVertexAttributes().getMask() && renderable.lights != null == this.lightingEnabled;
   }

   public void begin(Camera camera, RenderContext context) {
      this.program.begin();
      this.camera = camera;
      this.context = context;
      context.setDepthTest(true, 515);
   }

   public void render(Renderable renderable) {
      ShaderProgram.pedantic = false;
      this.program.setUniformMatrix("u_projTrans", this.camera.combined);
      this.program.setUniformf("u_cameraPosition", this.camera.position);
      this.program.setUniformf("u_cameraDirection", this.camera.direction);
      this.program.setUniformf("u_cameraUp", this.camera.up);
      this.program.setUniformMatrix("u_normalMatrix", this.normalMatrix.set(this.camera.combined));
      Iterator var3 = this.subShaders.iterator();

      while(var3.hasNext()) {
         SubShader shader = (SubShader)var3.next();
         shader.apply(this.program, this.context, this.camera, renderable);
      }

      renderable.mesh.render(this.program, renderable.primitiveType, renderable.meshPartOffset, renderable.meshPartSize);
   }

   public void end() {
      this.program.end();
   }

   public void dispose() {
      this.program.dispose();
   }
}
