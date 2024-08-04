package com.badlogic.gdx.physics.box2d.joints;

import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;

public class FrictionJoint extends Joint {
   public FrictionJoint(World world, long addr) {
      super(world, addr);
   }

   public void setMaxForce(float force) {
      this.jniSetMaxForce(this.addr, force);
   }

   private native void jniSetMaxForce(long var1, float var3);

   public float getMaxForce() {
      return this.jniGetMaxForce(this.addr);
   }

   private native float jniGetMaxForce(long var1);

   public void setMaxTorque(float torque) {
      this.jniSetMaxTorque(this.addr, torque);
   }

   private native void jniSetMaxTorque(long var1, float var3);

   public float getMaxTorque() {
      return this.jniGetMaxTorque(this.addr);
   }

   private native float jniGetMaxTorque(long var1);
}
