package com.badlogic.gdx.physics.box2d.joints;

import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;

public class GearJoint extends Joint {
   public GearJoint(World world, long addr) {
      super(world, addr);
   }

   public void setRatio(float ratio) {
      this.jniSetRatio(this.addr, ratio);
   }

   private native void jniSetRatio(long var1, float var3);

   public float getRatio() {
      return this.jniGetRatio(this.addr);
   }

   private native float jniGetRatio(long var1);
}
