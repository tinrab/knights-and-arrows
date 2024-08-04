package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;

public interface ShaderProvider {
   Shader getShader(Renderable var1);

   void dispose();
}
