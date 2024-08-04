package com.badlogic.gdx.physics.box2d.joints;

import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;

public class PrismaticJoint extends Joint {
   public PrismaticJoint(World world, long addr) {
      super(world, addr);
   }

   public float getJointTranslation() {
      return this.jniGetJointTranslation(this.addr);
   }

   private native float jniGetJointTranslation(long var1);

   public float getJointSpeed() {
      return this.jniGetJointSpeed(this.addr);
   }

   private native float jniGetJointSpeed(long var1);

   public boolean isLimitEnabled() {
      return this.jniIsLimitEnabled(this.addr);
   }

   private native boolean jniIsLimitEnabled(long var1);

   public void enableLimit(boolean flag) {
      this.jniEnableLimit(this.addr, flag);
   }

   private native void jniEnableLimit(long var1, boolean var3);

   public float getLowerLimit() {
      return this.jniGetLowerLimit(this.addr);
   }

   private native float jniGetLowerLimit(long var1);

   public float getUpperLimit() {
      return this.jniGetUpperLimit(this.addr);
   }

   private native float jniGetUpperLimit(long var1);

   public void setLimits(float lower, float upper) {
      this.jniSetLimits(this.addr, lower, upper);
   }

   private native void jniSetLimits(long var1, float var3, float var4);

   public boolean isMotorEnabled() {
      return this.jniIsMotorEnabled(this.addr);
   }

   private native boolean jniIsMotorEnabled(long var1);

   public void enableMotor(boolean flag) {
      this.jniEnableMotor(this.addr, flag);
   }

   private native void jniEnableMotor(long var1, boolean var3);

   public void setMotorSpeed(float speed) {
      this.jniSetMotorSpeed(this.addr, speed);
   }

   private native void jniSetMotorSpeed(long var1, float var3);

   public float getMotorSpeed() {
      return this.jniGetMotorSpeed(this.addr);
   }

   private native float jniGetMotorSpeed(long var1);

   public void setMaxMotorForce(float force) {
      this.jniSetMaxMotorForce(this.addr, force);
   }

   private native void jniSetMaxMotorForce(long var1, float var3);

   public float getMotorForce(float invDt) {
      return this.jniGetMotorForce(this.addr, invDt);
   }

   private native float jniGetMotorForce(long var1, float var3);
}
