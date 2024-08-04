package com.badlogic.gdx.physics.box2d.joints;

import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;

public class RevoluteJoint extends Joint {
   public RevoluteJoint(World world, long addr) {
      super(world, addr);
   }

   public float getJointAngle() {
      return this.jniGetJointAngle(this.addr);
   }

   private native float jniGetJointAngle(long var1);

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

   public void setMaxMotorTorque(float torque) {
      this.jniSetMaxMotorTorque(this.addr, torque);
   }

   private native void jniSetMaxMotorTorque(long var1, float var3);

   public float getMotorTorque(float invDt) {
      return this.jniGetMotorTorque(this.addr, invDt);
   }

   private native float jniGetMotorTorque(long var1, float var3);
}
