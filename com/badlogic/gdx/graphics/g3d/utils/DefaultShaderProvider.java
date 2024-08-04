package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.shaders.GLES10Shader;

public class DefaultShaderProvider extends BaseShaderProvider {
   public String vertexShader;
   public String fragmentShader;

   public DefaultShaderProvider(String vertexShader, String fragmentShader) {
      this.vertexShader = vertexShader;
      this.fragmentShader = fragmentShader;
   }

   public DefaultShaderProvider(FileHandle vertexShader, FileHandle fragmentShader) {
      this(vertexShader.readString(), fragmentShader.readString());
   }

   public DefaultShaderProvider() {
      this(DefaultShader.getDefaultVertexShader(), DefaultShader.getDefaultFragmentShader());
   }

   protected Shader createShader(Renderable renderable) {
      Gdx.app.log("DefaultShaderProvider", "Creating new shader");
      return (Shader)(Gdx.graphics.isGL20Available() ? new DefaultShader(this.vertexShader, this.fragmentShader, renderable.material, renderable.mesh.getVertexAttributes(), renderable.lights != null, renderable.lights != null && renderable.lights.fog != null, 2, 5, 3, renderable.bones == null ? 0 : 12) : new GLES10Shader());
   }
}
