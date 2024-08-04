package com.badlogic.gdx.physics.box2d;

import com.badlogic.gdx.math.Vector2;

public class Contact {
   protected long addr;
   protected World world;
   protected final WorldManifold worldManifold = new WorldManifold();
   private final float[] tmp = new float[6];

   protected Contact(World world, long addr) {
      this.addr = addr;
      this.world = world;
   }

   public WorldManifold getWorldManifold() {
      int numContactPoints = this.jniGetWorldManifold(this.addr, this.tmp);
      this.worldManifold.numContactPoints = numContactPoints;
      this.worldManifold.normal.set(this.tmp[0], this.tmp[1]);

      for(int i = 0; i < numContactPoints; ++i) {
         Vector2 point = this.worldManifold.points[i];
         point.x = this.tmp[2 + i * 2];
         point.y = this.tmp[2 + i * 2 + 1];
      }

      return this.worldManifold;
   }

   private native int jniGetWorldManifold(long var1, float[] var3);

   public boolean isTouching() {
      return this.jniIsTouching(this.addr);
   }

   private native boolean jniIsTouching(long var1);

   public void setEnabled(boolean flag) {
      this.jniSetEnabled(this.addr, flag);
   }

   private native void jniSetEnabled(long var1, boolean var3);

   public boolean isEnabled() {
      return this.jniIsEnabled(this.addr);
   }

   private native boolean jniIsEnabled(long var1);

   public Fixture getFixtureA() {
      return (Fixture)this.world.fixtures.get(this.jniGetFixtureA(this.addr));
   }

   private native long jniGetFixtureA(long var1);

   public Fixture getFixtureB() {
      return (Fixture)this.world.fixtures.get(this.jniGetFixtureB(this.addr));
   }

   private native long jniGetFixtureB(long var1);

   public int getChildIndexA() {
      return this.jniGetChildIndexA(this.addr);
   }

   private native int jniGetChildIndexA(long var1);

   public int getChildIndexB() {
      return this.jniGetChildIndexB(this.addr);
   }

   private native int jniGetChildIndexB(long var1);

   public void setFriction(float friction) {
      this.jniSetFriction(this.addr, friction);
   }

   private native void jniSetFriction(long var1, float var3);

   public float getFriction() {
      return this.jniGetFriction(this.addr);
   }

   private native float jniGetFriction(long var1);

   public void resetFriction() {
      this.jniResetFriction(this.addr);
   }

   private native void jniResetFriction(long var1);

   public void setRestitution(float restitution) {
      this.jniSetRestitution(this.addr, restitution);
   }

   private native void jniSetRestitution(long var1, float var3);

   public float getRestitution() {
      return this.jniGetRestitution(this.addr);
   }

   private native float jniGetRestitution(long var1);

   public void ResetRestitution() {
      this.jniResetRestitution(this.addr);
   }

   private native void jniResetRestitution(long var1);

   public float getTangentSpeed() {
      return this.jniGetTangentSpeed(this.addr);
   }

   private native float jniGetTangentSpeed(long var1);

   public void setTangentSpeed(float speed) {
      this.jniSetTangentSpeed(this.addr, speed);
   }

   private native void jniSetTangentSpeed(long var1, float var3);
}
