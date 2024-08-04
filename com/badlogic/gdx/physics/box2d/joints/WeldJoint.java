package com.badlogic.gdx.physics.box2d.joints;

import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;

public class WeldJoint extends Joint {
   public WeldJoint(World world, long addr) {
      super(world, addr);
   }

   public float getReferenceAngle() {
      return this.jniGetReferenceAngle(this.addr);
   }

   private native float jniGetReferenceAngle(long var1);
}
