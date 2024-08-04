package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.CompositeShader;

public class CompositeShaderProvider extends BaseShaderProvider {
   protected Shader createShader(Renderable renderable) {
      Gdx.app.log("CompositeShaderProvider", "Creating new shader");
      return new CompositeShader(renderable);
   }
}
