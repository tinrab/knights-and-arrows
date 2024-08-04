package com.badlogic.gdx.physics.box2d;

import com.badlogic.gdx.math.Vector2;

public class PolygonShape extends Shape {
   private static float[] verts = new float[2];

   public PolygonShape() {
      this.addr = this.newPolygonShape();
   }

   protected PolygonShape(long addr) {
      this.addr = addr;
   }

   private native long newPolygonShape();

   public Shape.Type getType() {
      return Shape.Type.Polygon;
   }

   public void set(Vector2[] vertices) {
      float[] verts = new float[vertices.length * 2];
      int i = 0;

      for(int j = 0; i < vertices.length * 2; ++j) {
         verts[i] = vertices[j].x;
         verts[i + 1] = vertices[j].y;
         i += 2;
      }

      this.jniSet(this.addr, verts, 0, verts.length);
   }

   public void set(float[] vertices) {
      this.jniSet(this.addr, vertices, 0, vertices.length);
   }

   public void set(float[] vertices, int offset, int len) {
      this.jniSet(this.addr, vertices, offset, len);
   }

   private native void jniSet(long var1, float[] var3, int var4, int var5);

   public void setAsBox(float hx, float hy) {
      this.jniSetAsBox(this.addr, hx, hy);
   }

   private native void jniSetAsBox(long var1, float var3, float var4);

   public void setAsBox(float hx, float hy, Vector2 center, float angle) {
      this.jniSetAsBox(this.addr, hx, hy, center.x, center.y, angle);
   }

   private native void jniSetAsBox(long var1, float var3, float var4, float var5, float var6, float var7);

   public int getVertexCount() {
      return this.jniGetVertexCount(this.addr);
   }

   private native int jniGetVertexCount(long var1);

   public void getVertex(int index, Vector2 vertex) {
      this.jniGetVertex(this.addr, index, verts);
      vertex.x = verts[0];
      vertex.y = verts[1];
   }

   private native void jniGetVertex(long var1, int var3, float[] var4);
}
