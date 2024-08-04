package com.badlogic.gdx.physics.box2d;

public class ContactImpulse {
   final World world;
   long addr;
   float[] tmp = new float[2];
   final float[] normalImpulses = new float[2];
   final float[] tangentImpulses = new float[2];

   protected ContactImpulse(World world, long addr) {
      this.world = world;
      this.addr = addr;
   }

   public float[] getNormalImpulses() {
      this.jniGetNormalImpulses(this.addr, this.normalImpulses);
      return this.normalImpulses;
   }

   private native void jniGetNormalImpulses(long var1, float[] var3);

   public float[] getTangentImpulses() {
      this.jniGetTangentImpulses(this.addr, this.tangentImpulses);
      return this.tangentImpulses;
   }

   private native void jniGetTangentImpulses(long var1, float[] var3);

   public int getCount() {
      return this.jniGetCount(this.addr);
   }

   private native int jniGetCount(long var1);
}
