package com.badlogic.gdx.graphics.g3d.shaders.subshaders;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.materials.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.materials.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class DiffuseColorTextureShader extends BaseSubShader {
   private boolean useVertexColor;
   private boolean useDiffuseColor;
   private boolean useDiffuseTexture;

   public void init(Renderable renderable) {
      String value = "";
      if (renderable.mesh.getVertexAttribute(2) != null || renderable.mesh.getVertexAttribute(4) != null) {
         this.vertexVars.addAll((Object[])(new String[]{"attribute vec4 a_color;", "varying vec4 v_diffuseColor;"}));
         this.vertexCode.add("v_diffuseColor = a_color;");
         this.fragmentVars.add("varying LOWP vec4 v_diffuseColor;");
         this.useVertexColor = true;
         value = "v_diffuseColor";
      }

      if (renderable.material.has(ColorAttribute.Diffuse)) {
         this.fragmentVars.addAll((Object[])(new String[]{"uniform LOWP vec4 u_diffuseColor;"}));
         this.useDiffuseColor = true;
         value = value + (this.useVertexColor ? " * " : "");
         value = value + "u_diffuseColor";
      }

      if (renderable.material.has(TextureAttribute.Diffuse)) {
         this.vertexVars.addAll((Object[])(new String[]{"attribute vec2 a_texCoord0;", "varying vec2 v_texCoords0;"}));
         this.vertexCode.addAll((Object[])(new String[]{"v_texCoords0 = a_texCoord0;"}));
         this.fragmentVars.addAll((Object[])(new String[]{"uniform sampler2D u_diffuseTexture;", "varying MED vec2 v_texCoords0;"}));
         this.useDiffuseTexture = true;
         value = value + (!this.useVertexColor && !this.useDiffuseColor ? "" : " * ");
         value = value + "texture2D(u_diffuseTexture, v_texCoords0)";
      }

      this.fragmentCode.add("color = color * " + value + ";");
   }

   public void apply(ShaderProgram program, RenderContext context, Camera camera, Renderable renderable) {
      if (this.useDiffuseColor) {
         ColorAttribute attribute = (ColorAttribute)renderable.material.get(ColorAttribute.Diffuse);
         program.setUniformf("u_diffuseColor", attribute.color);
      }

      if (this.useDiffuseTexture) {
         TextureAttribute attribute = (TextureAttribute)renderable.material.get(TextureAttribute.Diffuse);
         int unit = context.textureBinder.bind(attribute.textureDescription);
         program.setUniformi("u_diffuseTexture", unit);
      }

   }
}
