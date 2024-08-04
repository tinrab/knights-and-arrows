package com.badlogic.gdx.physics.box2d.joints;

import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;

public class WheelJoint extends Joint {
   public WheelJoint(World world, long addr) {
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

   private boolean isMotorEnabled() {
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

   public float getMaxMotorTorque() {
      return this.jniGetMaxMotorTorque(this.addr);
   }

   private native float jniGetMaxMotorTorque(long var1);

   public float getMotorTorque(float invDt) {
      return this.jniGetMotorTorque(this.addr, invDt);
   }

   private native float jniGetMotorTorque(long var1, float var3);

   public void setSpringFrequencyHz(float hz) {
      this.jniSetSpringFrequencyHz(this.addr, hz);
   }

   private native void jniSetSpringFrequencyHz(long var1, float var3);

   public float getSpringFrequencyHz() {
      return this.jniGetSpringFrequencyHz(this.addr);
   }

   private native float jniGetSpringFrequencyHz(long var1);

   public void setSpringDampingRatio(float ratio) {
      this.jniSetSpringDampingRatio(this.addr, ratio);
   }

   private native void jniSetSpringDampingRatio(long var1, float var3);

   public float getSpringDampingRatio() {
      return this.jniGetSpringDampingRatio(this.addr);
   }

   private native float jniGetSpringDampingRatio(long var1);
}
