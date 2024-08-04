package com.badlogic.gdx.graphics.g3d.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.lights.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.lights.Lights;
import com.badlogic.gdx.graphics.g3d.lights.PointLight;
import com.badlogic.gdx.graphics.g3d.materials.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.materials.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.materials.IntAttribute;
import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.graphics.g3d.materials.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.util.Iterator;

public class GLES10Shader implements Shader {
   private Camera camera;
   private RenderContext context;
   private Matrix4 currentTransform;
   private Material currentMaterial;
   private Texture currentTexture0;
   private Mesh currentMesh;
   public static int defaultCullFace = 1029;
   private final float[] lightVal = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
   private final float[] zeroVal4 = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
   private final float[] oneVal4 = new float[]{1.0F, 1.0F, 1.0F, 1.0F};

   public GLES10Shader() {
      if (Gdx.gl10 == null) {
         throw new GdxRuntimeException("This shader requires OpenGL ES 1.x");
      }
   }

   public void init() {
   }

   public boolean canRender(Renderable renderable) {
      return true;
   }

   public int compareTo(Shader other) {
      return 0;
   }

   public boolean equals(Object obj) {
      return obj instanceof GLES10Shader ? this.equals((GLES10Shader)obj) : false;
   }

   public boolean equals(GLES10Shader obj) {
      return obj == this;
   }

   public void begin(Camera camera, RenderContext context) {
      this.context = context;
      this.camera = camera;
      context.setDepthTest(true, 515);
      Gdx.gl10.glMatrixMode(5889);
      Gdx.gl10.glLoadMatrixf(camera.combined.val, 0);
      Gdx.gl10.glMatrixMode(5888);
   }

   private void bindLights(Lights lights) {
      if (lights == null) {
         Gdx.gl10.glDisable(2896);
      } else {
         Gdx.gl10.glEnable(2896);
         Gdx.gl10.glLightModelfv(2899, getValues(this.lightVal, lights.ambientLight), 0);
         Gdx.gl10.glLightfv(16384, 4609, this.zeroVal4, 0);
         int idx = 0;
         Gdx.gl10.glPushMatrix();
         Gdx.gl10.glLoadIdentity();

         int i;
         for(i = 0; i < lights.directionalLights.size && idx < 8; ++i) {
            DirectionalLight light = (DirectionalLight)lights.directionalLights.get(i);
            Gdx.gl10.glEnable(16384 + idx);
            Gdx.gl10.glLightfv(16384 + idx, 4609, getValues(this.lightVal, light.color), 0);
            Gdx.gl10.glLightfv(16384 + idx, 4611, getValues(this.lightVal, -light.direction.x, -light.direction.y, -light.direction.z, 0.0F), 0);
            Gdx.gl10.glLightf(16384 + idx, 4614, 180.0F);
            Gdx.gl10.glLightf(16384 + idx, 4615, 1.0F);
            Gdx.gl10.glLightf(16384 + idx, 4616, 0.0F);
            Gdx.gl10.glLightf(16384 + idx, 4617, 0.0F);
            ++idx;
         }

         for(i = 0; i < lights.pointLights.size && idx < 8; ++i) {
            Gdx.gl10.glEnable(16384 + idx);
            PointLight light = (PointLight)lights.pointLights.get(i);
            Gdx.gl10.glLightfv(16384 + idx, 4609, getValues(this.lightVal, light.color), 0);
            Gdx.gl10.glLightfv(16384 + idx, 4611, getValues(this.lightVal, light.position.x, light.position.y, light.position.z, 1.0F), 0);
            Gdx.gl10.glLightf(16384 + idx, 4614, 180.0F);
            Gdx.gl10.glLightf(16384 + idx, 4615, 0.0F);
            Gdx.gl10.glLightf(16384 + idx, 4616, 0.0F);
            Gdx.gl10.glLightf(16384 + idx, 4617, 1.0F / light.intensity);
            ++idx;
         }

         while(idx < 8) {
            Gdx.gl10.glDisable(16384 + idx++);
         }

         Gdx.gl10.glPopMatrix();
      }
   }

   private static final float[] getValues(float[] out, float v0, float v1, float v2, float v3) {
      out[0] = v0;
      out[1] = v1;
      out[2] = v2;
      out[3] = v3;
      return out;
   }

   private static final float[] getValues(float[] out, Color color) {
      return getValues(out, color.r, color.g, color.b, color.a);
   }

   public void render(Renderable renderable) {
      if (this.currentMaterial != renderable.material) {
         this.currentMaterial = renderable.material;
         if (!this.currentMaterial.has(BlendingAttribute.Type)) {
            this.context.setBlending(false, 770, 771);
         }

         if (!this.currentMaterial.has(ColorAttribute.Diffuse)) {
            Gdx.gl10.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            if (renderable.lights != null) {
               Gdx.gl10.glDisable(2903);
            }
         }

         if (!this.currentMaterial.has(TextureAttribute.Diffuse)) {
            Gdx.gl10.glDisable(3553);
         }

         int cullFace = defaultCullFace;
         Iterator var4 = this.currentMaterial.iterator();

         while(var4.hasNext()) {
            Material.Attribute attribute = (Material.Attribute)var4.next();
            if (attribute.type == BlendingAttribute.Type) {
               this.context.setBlending(true, ((BlendingAttribute)attribute).sourceFunction, ((BlendingAttribute)attribute).destFunction);
            } else if (attribute.type == ColorAttribute.Diffuse) {
               Gdx.gl10.glColor4f(((ColorAttribute)attribute).color.r, ((ColorAttribute)attribute).color.g, ((ColorAttribute)attribute).color.b, ((ColorAttribute)attribute).color.a);
               if (renderable.lights != null) {
                  Gdx.gl10.glEnable(2903);
                  Gdx.gl10.glMaterialfv(1032, 4608, this.zeroVal4, 0);
                  Gdx.gl10.glMaterialfv(1032, 4609, getValues(this.lightVal, ((ColorAttribute)attribute).color), 0);
               }
            } else if (attribute.type == TextureAttribute.Diffuse) {
               TextureDescriptor textureDesc = ((TextureAttribute)attribute).textureDescription;
               if (this.currentTexture0 != textureDesc.texture) {
                  (this.currentTexture0 = textureDesc.texture).bind(0);
               }

               Gdx.gl.glTexParameterf(3553, 10241, (float)textureDesc.minFilter);
               Gdx.gl.glTexParameterf(3553, 10240, (float)textureDesc.magFilter);
               Gdx.gl.glTexParameterf(3553, 10242, (float)textureDesc.uWrap);
               Gdx.gl.glTexParameterf(3553, 10243, (float)textureDesc.vWrap);
               Gdx.gl10.glEnable(3553);
            } else if ((attribute.type & IntAttribute.CullFace) == IntAttribute.CullFace) {
               cullFace = ((IntAttribute)attribute).value;
            }
         }

         this.context.setCullFace(cullFace);
      }

      if (this.currentTransform != renderable.worldTransform) {
         if (this.currentTransform != null) {
            Gdx.gl10.glPopMatrix();
         }

         this.currentTransform = renderable.worldTransform;
         Gdx.gl10.glPushMatrix();
         Gdx.gl10.glLoadMatrixf(this.currentTransform.val, 0);
      }

      this.bindLights(renderable.lights);
      if (this.currentMesh != renderable.mesh) {
         if (this.currentMesh != null) {
            this.currentMesh.unbind();
         }

         (this.currentMesh = renderable.mesh).bind();
      }

      renderable.mesh.render(renderable.primitiveType, renderable.meshPartOffset, renderable.meshPartSize);
   }

   public void end() {
      if (this.currentMesh != null) {
         this.currentMesh.unbind();
      }

      this.currentMesh = null;
      if (this.currentTransform != null) {
         Gdx.gl10.glPopMatrix();
      }

      this.currentTransform = null;
      this.currentTexture0 = null;
      this.currentMaterial = null;
      Gdx.gl10.glDisable(2896);
   }

   public void dispose() {
   }
}
