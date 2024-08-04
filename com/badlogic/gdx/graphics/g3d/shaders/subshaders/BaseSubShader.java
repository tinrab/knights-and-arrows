package com.badlogic.gdx.graphics.g3d.shaders.subshaders;

import com.badlogic.gdx.utils.Array;

public abstract class BaseSubShader implements SubShader {
   protected Array<String> vertexVars = new Array(new String[0]);
   protected Array<String> vertexCode = new Array(new String[0]);
   protected Array<String> fragmentVars = new Array(new String[0]);
   protected Array<String> fragmentCode = new Array(new String[0]);

   public String[] getVertexShaderVars() {
      return (String[])this.vertexVars.toArray();
   }

   public String[] getVertexShaderCode() {
      return (String[])this.vertexCode.toArray();
   }

   public String[] getFragmentShaderVars() {
      return (String[])this.fragmentVars.toArray();
   }

   public String[] getFragmentShaderCode() {
      return (String[])this.fragmentCode.toArray();
   }
}
