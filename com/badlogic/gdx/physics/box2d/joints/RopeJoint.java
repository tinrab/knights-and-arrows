package com.badlogic.gdx.physics.box2d.joints;

import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;

public class RopeJoint extends Joint {
   public RopeJoint(World world, long addr) {
      super(world, addr);
   }

   public float getMaxLength() {
      return this.jniGetMaxLength(this.addr);
   }

   private native float jniGetMaxLength(long var1);

   public void setMaxLength(float length) {
      this.jniSetMaxLength(this.addr, length);
   }

   private native float jniSetMaxLength(long var1, float var3);
}
