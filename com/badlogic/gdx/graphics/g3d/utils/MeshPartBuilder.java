package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;

public interface MeshPartBuilder {
   MeshPart getMeshPart();

   VertexAttributes getAttributes();

   void setColor(Color var1);

   void setColor(float var1, float var2, float var3, float var4);

   short vertex(float[] var1);

   short vertex(Vector3 var1, Vector3 var2, Color var3, Vector2 var4);

   short vertex(MeshPartBuilder.VertexInfo var1);

   void index(short var1);

   void index(short var1, short var2);

   void index(short var1, short var2, short var3);

   void index(short var1, short var2, short var3, short var4);

   void index(short var1, short var2, short var3, short var4, short var5, short var6);

   void index(short var1, short var2, short var3, short var4, short var5, short var6, short var7, short var8);

   void line(short var1, short var2);

   void line(MeshPartBuilder.VertexInfo var1, MeshPartBuilder.VertexInfo var2);

   void line(Vector3 var1, Vector3 var2);

   void line(float var1, float var2, float var3, float var4, float var5, float var6);

   void line(Vector3 var1, Color var2, Vector3 var3, Color var4);

   void triangle(short var1, short var2, short var3);

   void triangle(MeshPartBuilder.VertexInfo var1, MeshPartBuilder.VertexInfo var2, MeshPartBuilder.VertexInfo var3);

   void triangle(Vector3 var1, Vector3 var2, Vector3 var3);

   void triangle(Vector3 var1, Color var2, Vector3 var3, Color var4, Vector3 var5, Color var6);

   void rect(short var1, short var2, short var3, short var4);

   void rect(MeshPartBuilder.VertexInfo var1, MeshPartBuilder.VertexInfo var2, MeshPartBuilder.VertexInfo var3, MeshPartBuilder.VertexInfo var4);

   void rect(Vector3 var1, Vector3 var2, Vector3 var3, Vector3 var4, Vector3 var5);

   void rect(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, float var12, float var13, float var14, float var15);

   void box(MeshPartBuilder.VertexInfo var1, MeshPartBuilder.VertexInfo var2, MeshPartBuilder.VertexInfo var3, MeshPartBuilder.VertexInfo var4, MeshPartBuilder.VertexInfo var5, MeshPartBuilder.VertexInfo var6, MeshPartBuilder.VertexInfo var7, MeshPartBuilder.VertexInfo var8);

   void box(Vector3 var1, Vector3 var2, Vector3 var3, Vector3 var4, Vector3 var5, Vector3 var6, Vector3 var7, Vector3 var8);

   void box(Matrix4 var1);

   void box(float var1, float var2, float var3);

   void box(float var1, float var2, float var3, float var4, float var5, float var6);

   void cylinder(float var1, float var2, float var3, int var4);

   void cone(float var1, float var2, float var3, int var4);

   void sphere(float var1, float var2, float var3, int var4, int var5);

   public static class VertexInfo implements Pool.Poolable {
      public final Vector3 position = new Vector3();
      public boolean hasPosition;
      public final Vector3 normal = new Vector3(0.0F, 1.0F, 0.0F);
      public boolean hasNormal;
      public final Color color = new Color(1.0F, 1.0F, 1.0F, 1.0F);
      public boolean hasColor;
      public final Vector2 uv = new Vector2();
      public boolean hasUV;

      public void reset() {
         this.position.set(0.0F, 0.0F, 0.0F);
         this.normal.set(0.0F, 1.0F, 0.0F);
         this.color.set(1.0F, 1.0F, 1.0F, 1.0F);
         this.uv.set(0.0F, 0.0F);
      }

      public MeshPartBuilder.VertexInfo set(Vector3 pos, Vector3 nor, Color col, Vector2 uv) {
         this.reset();
         if (this.hasPosition = pos != null) {
            this.position.set(pos);
         }

         if (this.hasNormal = nor != null) {
            this.normal.set(nor);
         }

         if (this.hasColor = col != null) {
            this.color.set(col);
         }

         if (this.hasUV = uv != null) {
            this.uv.set(uv);
         }

         return this;
      }

      public MeshPartBuilder.VertexInfo setPos(float x, float y, float z) {
         this.position.set(x, y, z);
         this.hasPosition = true;
         return this;
      }

      public MeshPartBuilder.VertexInfo setPos(Vector3 pos) {
         if (this.hasPosition = pos != null) {
            this.position.set(pos);
         }

         return this;
      }

      public MeshPartBuilder.VertexInfo setNor(float x, float y, float z) {
         this.normal.set(x, y, z);
         this.hasNormal = true;
         return this;
      }

      public MeshPartBuilder.VertexInfo setNor(Vector3 nor) {
         if (this.hasNormal = nor != null) {
            this.normal.set(nor);
         }

         return this;
      }

      public MeshPartBuilder.VertexInfo setCol(float r, float g, float b, float a) {
         this.color.set(r, g, b, a);
         this.hasColor = true;
         return this;
      }

      public MeshPartBuilder.VertexInfo setCol(Color col) {
         if (this.hasColor = col != null) {
            this.color.set(col);
         }

         return this;
      }

      public MeshPartBuilder.VertexInfo setUV(float u, float v) {
         this.uv.set(u, v);
         this.hasUV = true;
         return this;
      }

      public MeshPartBuilder.VertexInfo setUV(Vector2 uv) {
         if (this.hasUV = uv != null) {
            this.uv.set(uv);
         }

         return this;
      }
   }
}
