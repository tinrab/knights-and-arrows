package com.badlogic.gdx.physics.box2d.joints;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;

public class MouseJoint extends Joint {
   final float[] tmp = new float[2];
   private final Vector2 target = new Vector2();

   public MouseJoint(World world, long addr) {
      super(world, addr);
   }

   public void setTarget(Vector2 target) {
      this.jniSetTarget(this.addr, target.x, target.y);
   }

   private native void jniSetTarget(long var1, float var3, float var4);

   public Vector2 getTarget() {
      this.jniGetTarget(this.addr, this.tmp);
      this.target.x = this.tmp[0];
      this.target.y = this.tmp[1];
      return this.target;
   }

   private native void jniGetTarget(long var1, float[] var3);

   public void setMaxForce(float force) {
      this.jniSetMaxForce(this.addr, force);
   }

   private native void jniSetMaxForce(long var1, float var3);

   public float getMaxForce() {
      return this.jniGetMaxForce(this.addr);
   }

   private native float jniGetMaxForce(long var1);

   public void setFrequency(float hz) {
      this.jniSetFrequency(this.addr, hz);
   }

   private native void jniSetFrequency(long var1, float var3);

   public float getFrequency() {
      return this.jniGetFrequency(this.addr);
   }

   private native float jniGetFrequency(long var1);

   public void setDampingRatio(float ratio) {
      this.jniSetDampingRatio(this.addr, ratio);
   }

   private native void jniSetDampingRatio(long var1, float var3);

   public float getDampingRatio() {
      return this.jniGetDampingRatio(this.addr);
   }

   private native float jniGetDampingRatio(long var1);
}
