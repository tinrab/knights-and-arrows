package com.badlogic.gdx.physics.box2d;

import com.badlogic.gdx.math.Vector2;

public class Manifold {
   long addr;
   final Manifold.ManifoldPoint[] points = new Manifold.ManifoldPoint[]{new Manifold.ManifoldPoint(), new Manifold.ManifoldPoint()};
   final Vector2 localNormal = new Vector2();
   final Vector2 localPoint = new Vector2();
   final int[] tmpInt = new int[2];
   final float[] tmpFloat = new float[4];

   protected Manifold(long addr) {
      this.addr = addr;
   }

   public Manifold.ManifoldType getType() {
      int type = this.jniGetType(this.addr);
      if (type == 0) {
         return Manifold.ManifoldType.Circle;
      } else if (type == 1) {
         return Manifold.ManifoldType.FaceA;
      } else {
         return type == 2 ? Manifold.ManifoldType.FaceB : Manifold.ManifoldType.Circle;
      }
   }

   private native int jniGetType(long var1);

   public int getPointCount() {
      return this.jniGetPointCount(this.addr);
   }

   private native int jniGetPointCount(long var1);

   public Vector2 getLocalNormal() {
      this.jniGetLocalNormal(this.addr, this.tmpFloat);
      this.localNormal.set(this.tmpFloat[0], this.tmpFloat[1]);
      return this.localNormal;
   }

   private native void jniGetLocalNormal(long var1, float[] var3);

   public Vector2 getLocalPoint() {
      this.jniGetLocalPoint(this.addr, this.tmpFloat);
      this.localPoint.set(this.tmpFloat[0], this.tmpFloat[1]);
      return this.localPoint;
   }

   private native void jniGetLocalPoint(long var1, float[] var3);

   public Manifold.ManifoldPoint[] getPoints() {
      int count = this.jniGetPointCount(this.addr);

      for(int i = 0; i < count; ++i) {
         int contactID = this.jniGetPoint(this.addr, this.tmpFloat, i);
         Manifold.ManifoldPoint point = this.points[i];
         point.contactID = contactID;
         point.localPoint.set(this.tmpFloat[0], this.tmpFloat[1]);
         point.normalImpulse = this.tmpFloat[2];
         point.tangentImpulse = this.tmpFloat[3];
      }

      return this.points;
   }

   private native int jniGetPoint(long var1, float[] var3, int var4);

   public class ManifoldPoint {
      public final Vector2 localPoint = new Vector2();
      public float normalImpulse;
      public float tangentImpulse;
      public int contactID = 0;

      public String toString() {
         return "id: " + this.contactID + ", " + this.localPoint + ", " + this.normalImpulse + ", " + this.tangentImpulse;
      }
   }

   public static enum ManifoldType {
      Circle,
      FaceA,
      FaceB;
   }
}
