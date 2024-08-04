package com.badlogic.gdx.physics.box2d.joints;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;

public class PulleyJoint extends Joint {
   private final float[] tmp = new float[2];
   private final Vector2 groundAnchorA = new Vector2();
   private final Vector2 groundAnchorB = new Vector2();

   public PulleyJoint(World world, long addr) {
      super(world, addr);
   }

   public Vector2 getGroundAnchorA() {
      this.jniGetGroundAnchorA(this.addr, this.tmp);
      this.groundAnchorA.set(this.tmp[0], this.tmp[1]);
      return this.groundAnchorA;
   }

   private native void jniGetGroundAnchorA(long var1, float[] var3);

   public Vector2 getGroundAnchorB() {
      this.jniGetGroundAnchorB(this.addr, this.tmp);
      this.groundAnchorB.set(this.tmp[0], this.tmp[1]);
      return this.groundAnchorB;
   }

   private native void jniGetGroundAnchorB(long var1, float[] var3);

   public float getLength1() {
      return this.jniGetLength1(this.addr);
   }

   private native float jniGetLength1(long var1);

   public float getLength2() {
      return this.jniGetLength2(this.addr);
   }

   private native float jniGetLength2(long var1);

   public float getRatio() {
      return this.jniGetRatio(this.addr);
   }

   private native float jniGetRatio(long var1);
}
