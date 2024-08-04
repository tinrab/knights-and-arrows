package com.badlogic.gdx.physics.box2d.joints;

import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;

public class DistanceJoint extends Joint {
   public DistanceJoint(World world, long addr) {
      super(world, addr);
   }

   public void setLength(float length) {
      this.jniSetLength(this.addr, length);
   }

   private native void jniSetLength(long var1, float var3);

   public float getLength() {
      return this.jniGetLength(this.addr);
   }

   private native float jniGetLength(long var1);

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
