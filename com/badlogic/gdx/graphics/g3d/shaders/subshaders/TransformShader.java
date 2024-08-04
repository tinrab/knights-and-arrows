package com.badlogic.gdx.graphics.g3d.shaders.subshaders;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;

public class TransformShader extends BaseSubShader {
   private int NUM_BONES = 12;
   private boolean skinned;
   private float[] bones;
   private Matrix4 idtMatrix;

   public TransformShader() {
      this.bones = new float[this.NUM_BONES * 16];
      this.idtMatrix = new Matrix4();
      this.vertexVars.addAll((Object[])(new String[]{"uniform mat4 u_worldTrans;"}));
      this.vertexCode.addAll((Object[])(new String[]{"#ifdef skinnedFlag", "  mat4 skinning = mat4(0.0);", "  #ifdef boneWeight0Flag", "    skinning += (a_boneWeight0.y) * u_bones[int(a_boneWeight0.x)];", "  #endif", "  #ifdef boneWeight1Flag", "    skinning += (a_boneWeight1.y) * u_bones[int(a_boneWeight1.x)];", "  #endif", "  #ifdef boneWeight2Flag", "    skinning += (a_boneWeight2.y) * u_bones[int(a_boneWeight2.x)];", "  #endif", "  #ifdef boneWeight3Flag", "    skinning += (a_boneWeight3.y) * u_bones[int(a_boneWeight3.x)];", "  #endif", "  #ifdef boneWeight4Flag", "    skinning += (a_boneWeight4.y) * u_bones[int(a_boneWeight4.x)];", "  #endif", "  #ifdef boneWeight5Flag", "    skinning += (a_boneWeight5.y) * u_bones[int(a_boneWeight5.x)];", "  #endif", "  #ifdef boneWeight6Flag", "    skinning += (a_boneWeight6.y) * u_bones[int(a_boneWeight6.x)];", "  #endif", "  #ifdef boneWeight7Flag", "    skinning += (a_boneWeight7.y) * u_bones[int(a_boneWeight7.x)];", "  #endif", "  vec4 position = u_worldTrans * skinning * vec4(a_position, 1.0);", "#else", "  vec4 position = u_worldTrans * vec4(a_position, 1);", "#endif", "position = u_projTrans * position;"}));
      this.fragmentCode.addAll((Object[])(new String[]{"vec4 color = vec4(1.0);"}));
   }

   public void init(Renderable renderable) {
      VertexAttributes attributes = renderable.mesh.getVertexAttributes();
      int boneWeightsPerVertex = 0;

      for(int i = 0; i < attributes.size(); ++i) {
         VertexAttribute attr = attributes.get(i);
         if (attr.usage == 1) {
            this.vertexVars.add("attribute vec3 " + attr.alias + ";");
         }

         if (attr.usage == 8) {
            this.vertexVars.add("attribute vec3 " + attr.alias + ";");
         }

         if (attr.usage == 256) {
            this.vertexVars.add("attribute vec3 " + attr.alias + ";");
         }

         if (attr.usage == 128) {
            this.vertexVars.add("attribute vec3 " + attr.alias + ";");
         }

         if (attr.usage == 64) {
            this.vertexVars.add("#define boneWeight" + boneWeightsPerVertex + "Flag");
            this.vertexVars.add("attribute vec2 " + attr.alias + ";");
            ++boneWeightsPerVertex;
         }
      }

      if (boneWeightsPerVertex > 0) {
         this.skinned = true;
         this.vertexVars.add("#define skinnedFlag");
         this.vertexVars.add("#define numBones " + this.NUM_BONES);
         this.vertexVars.add("uniform mat4 u_bones[numBones];");
      }

   }

   public void apply(ShaderProgram program, RenderContext context, Camera camera, Renderable renderable) {
      program.setUniformMatrix("u_worldTrans", renderable.worldTransform);
      if (this.skinned) {
         int i = 0;

         for(int offset = 0; i < this.NUM_BONES; offset += 16) {
            Matrix4 mat = null;
            if (renderable.bones != null && i < renderable.bones.length && renderable.bones[i] != null) {
               mat = renderable.bones[i];
            } else {
               mat = this.idtMatrix;
            }

            System.arraycopy(mat.val, 0, this.bones, offset, 16);
            ++i;
         }

         program.setUniformMatrix4fv("u_bones", this.bones, 0, this.bones.length);
      }

   }
}
