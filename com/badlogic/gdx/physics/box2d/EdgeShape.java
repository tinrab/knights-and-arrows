package com.badlogic.gdx.physics.box2d;

import com.badlogic.gdx.math.Vector2;

public class EdgeShape extends Shape {
   static final float[] vertex = new float[2];

   public EdgeShape() {
      this.addr = this.newEdgeShape();
   }

   private native long newEdgeShape();

   EdgeShape(long addr) {
      this.addr = addr;
   }

   public void set(Vector2 v1, Vector2 v2) {
      this.set(v1.x, v1.y, v2.x, v2.y);
   }

   public void set(float v1X, float v1Y, float v2X, float v2Y) {
      this.jniSet(this.addr, v1X, v1Y, v2X, v2Y);
   }

   private native void jniSet(long var1, float var3, float var4, float var5, float var6);

   public void getVertex1(Vector2 vec) {
      this.jniGetVertex1(this.addr, vertex);
      vec.x = vertex[0];
      vec.y = vertex[1];
   }

   private native void jniGetVertex1(long var1, float[] var3);

   public void getVertex2(Vector2 vec) {
      this.jniGetVertex2(this.addr, vertex);
      vec.x = vertex[0];
      vec.y = vertex[1];
   }

   private native void jniGetVertex2(long var1, float[] var3);

   public Shape.Type getType() {
      return Shape.Type.Edge;
   }
}
