package com.badlogic.gdx.graphics.g3d.shaders.subshaders;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public interface SubShader {
   void init(Renderable var1);

   String[] getVertexShaderVars();

   String[] getVertexShaderCode();

   String[] getFragmentShaderVars();

   String[] getFragmentShaderCode();

   void apply(ShaderProgram var1, RenderContext var2, Camera var3, Renderable var4);
}
