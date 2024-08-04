package com.badlogic.gdx.physics.box2d;

import com.badlogic.gdx.math.Vector2;

public abstract class Joint {
   protected long addr;
   private final World world;
   private final float[] tmp = new float[2];
   private Object userData;
   protected JointEdge jointEdgeA;
   protected JointEdge jointEdgeB;
   private final Vector2 anchorA = new Vector2();
   private final Vector2 anchorB = new Vector2();
   private final Vector2 reactionForce = new Vector2();

   protected Joint(World world, long addr) {
      this.world = world;
      this.addr = addr;
   }

   public JointDef.JointType getType() {
      int type = this.jniGetType(this.addr);
      return type > 0 && type < JointDef.JointType.valueTypes.length ? JointDef.JointType.valueTypes[type] : JointDef.JointType.Unknown;
   }

   private native int jniGetType(long var1);

   public Body getBodyA() {
      return (Body)this.world.bodies.get(this.jniGetBodyA(this.addr));
   }

   private native long jniGetBodyA(long var1);

   public Body getBodyB() {
      return (Body)this.world.bodies.get(this.jniGetBodyB(this.addr));
   }

   private native long jniGetBodyB(long var1);

   public Vector2 getAnchorA() {
      this.jniGetAnchorA(this.addr, this.tmp);
      this.anchorA.x = this.tmp[0];
      this.anchorA.y = this.tmp[1];
      return this.anchorA;
   }

   private native void jniGetAnchorA(long var1, float[] var3);

   public Vector2 getAnchorB() {
      this.jniGetAnchorB(this.addr, this.tmp);
      this.anchorB.x = this.tmp[0];
      this.anchorB.y = this.tmp[1];
      return this.anchorB;
   }

   private native void jniGetAnchorB(long var1, float[] var3);

   public Vector2 getReactionForce(float inv_dt) {
      this.jniGetReactionForce(this.addr, inv_dt, this.tmp);
      this.reactionForce.x = this.tmp[0];
      this.reactionForce.y = this.tmp[1];
      return this.reactionForce;
   }

   private native void jniGetReactionForce(long var1, float var3, float[] var4);

   public float getReactionTorque(float inv_dt) {
      return this.jniGetReactionTorque(this.addr, inv_dt);
   }

   private native float jniGetReactionTorque(long var1, float var3);

   public Object getUserData() {
      return this.userData;
   }

   public void setUserData(Object userData) {
      this.userData = userData;
   }

   public boolean isActive() {
      return this.jniIsActive(this.addr);
   }

   private native boolean jniIsActive(long var1);
}
