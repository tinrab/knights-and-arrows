package com.badlogic.gdx.physics.box2d;

import com.badlogic.gdx.math.Vector2;

public class ChainShape extends Shape {
   boolean isLooped = false;
   private static float[] verts = new float[2];

   public ChainShape() {
      this.addr = this.newChainShape();
   }

   private native long newChainShape();

   ChainShape(long addr) {
      this.addr = addr;
   }

   public Shape.Type getType() {
      return Shape.Type.Chain;
   }

   public void createLoop(Vector2[] vertices) {
      float[] verts = new float[vertices.length * 2];
      int i = 0;

      for(int j = 0; i < vertices.length * 2; ++j) {
         verts[i] = vertices[j].x;
         verts[i + 1] = vertices[j].y;
         i += 2;
      }

      this.jniCreateLoop(this.addr, verts, verts.length / 2);
      this.isLooped = true;
   }

   private native void jniCreateLoop(long var1, float[] var3, int var4);

   public void createChain(float[] vertices) {
      this.jniCreateChain(this.addr, vertices, vertices.length / 2);
      this.isLooped = false;
   }

   public void createChain(Vector2[] vertices) {
      float[] verts = new float[vertices.length * 2];
      int i = 0;

      for(int j = 0; i < vertices.length * 2; ++j) {
         verts[i] = vertices[j].x;
         verts[i + 1] = vertices[j].y;
         i += 2;
      }

      this.createChain(verts);
   }

   private native void jniCreateChain(long var1, float[] var3, int var4);

   public void setPrevVertex(Vector2 prevVertex) {
      this.setPrevVertex(prevVertex.x, prevVertex.y);
   }

   public void setPrevVertex(float prevVertexX, float prevVertexY) {
      this.jniSetPrevVertex(this.addr, prevVertexX, prevVertexY);
   }

   private native void jniSetPrevVertex(long var1, float var3, float var4);

   public void setNextVertex(Vector2 nextVertex) {
      this.setNextVertex(nextVertex.x, nextVertex.y);
   }

   public void setNextVertex(float nextVertexX, float nextVertexY) {
      this.jniSetNextVertex(this.addr, nextVertexX, nextVertexY);
   }

   private native void jniSetNextVertex(long var1, float var3, float var4);

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

   public boolean isLooped() {
      return this.isLooped;
   }
}
