package com.badlogic.gdx.physics.box2d;

import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;

public class Body {
   protected long addr;
   private final float[] tmp = new float[4];
   private final World world;
   private ArrayList<Fixture> fixtures = new ArrayList(2);
   protected ArrayList<JointEdge> joints = new ArrayList(2);
   private Object userData;
   private final Transform transform = new Transform();
   private final Vector2 position = new Vector2();
   private final Vector2 worldCenter = new Vector2();
   private final Vector2 localCenter = new Vector2();
   private final Vector2 linearVelocity = new Vector2();
   private final MassData massData = new MassData();
   private final Vector2 localPoint = new Vector2();
   private final Vector2 worldVector = new Vector2();
   public final Vector2 localPoint2 = new Vector2();
   public final Vector2 localVector = new Vector2();
   public final Vector2 linVelWorld = new Vector2();
   public final Vector2 linVelLoc = new Vector2();

   protected Body(World world, long addr) {
      this.world = world;
      this.addr = addr;
   }

   protected void reset(long addr) {
      this.addr = addr;
      this.userData = null;

      for(int i = 0; i < this.fixtures.size(); ++i) {
         this.world.freeFixtures.free((Fixture)this.fixtures.get(i));
      }

      this.fixtures.clear();
      this.joints.clear();
   }

   public Fixture createFixture(FixtureDef def) {
      long fixtureAddr = this.jniCreateFixture(this.addr, def.shape.addr, def.friction, def.restitution, def.density, def.isSensor, def.filter.categoryBits, def.filter.maskBits, def.filter.groupIndex);
      Fixture fixture = (Fixture)this.world.freeFixtures.obtain();
      fixture.reset(this, fixtureAddr);
      this.world.fixtures.put(fixture.addr, fixture);
      this.fixtures.add(fixture);
      return fixture;
   }

   private native long jniCreateFixture(long var1, long var3, float var5, float var6, float var7, boolean var8, short var9, short var10, short var11);

   public Fixture createFixture(Shape shape, float density) {
      long fixtureAddr = this.jniCreateFixture(this.addr, shape.addr, density);
      Fixture fixture = (Fixture)this.world.freeFixtures.obtain();
      fixture.reset(this, fixtureAddr);
      this.world.fixtures.put(fixture.addr, fixture);
      this.fixtures.add(fixture);
      return fixture;
   }

   private native long jniCreateFixture(long var1, long var3, float var5);

   public void destroyFixture(Fixture fixture) {
      this.jniDestroyFixture(this.addr, fixture.addr);
      this.world.fixtures.remove(fixture.addr);
      this.fixtures.remove(fixture);
      this.world.freeFixtures.free(fixture);
   }

   private native void jniDestroyFixture(long var1, long var3);

   public void setTransform(Vector2 position, float angle) {
      this.jniSetTransform(this.addr, position.x, position.y, angle);
   }

   public void setTransform(float x, float y, float angle) {
      this.jniSetTransform(this.addr, x, y, angle);
   }

   private native void jniSetTransform(long var1, float var3, float var4, float var5);

   public Transform getTransform() {
      this.jniGetTransform(this.addr, this.transform.vals);
      return this.transform;
   }

   private native void jniGetTransform(long var1, float[] var3);

   public Vector2 getPosition() {
      this.jniGetPosition(this.addr, this.tmp);
      this.position.x = this.tmp[0];
      this.position.y = this.tmp[1];
      return this.position;
   }

   private native void jniGetPosition(long var1, float[] var3);

   public float getAngle() {
      return this.jniGetAngle(this.addr);
   }

   private native float jniGetAngle(long var1);

   public Vector2 getWorldCenter() {
      this.jniGetWorldCenter(this.addr, this.tmp);
      this.worldCenter.x = this.tmp[0];
      this.worldCenter.y = this.tmp[1];
      return this.worldCenter;
   }

   private native void jniGetWorldCenter(long var1, float[] var3);

   public Vector2 getLocalCenter() {
      this.jniGetLocalCenter(this.addr, this.tmp);
      this.localCenter.x = this.tmp[0];
      this.localCenter.y = this.tmp[1];
      return this.localCenter;
   }

   private native void jniGetLocalCenter(long var1, float[] var3);

   public void setLinearVelocity(Vector2 v) {
      this.jniSetLinearVelocity(this.addr, v.x, v.y);
   }

   public void setLinearVelocity(float vX, float vY) {
      this.jniSetLinearVelocity(this.addr, vX, vY);
   }

   private native void jniSetLinearVelocity(long var1, float var3, float var4);

   public Vector2 getLinearVelocity() {
      this.jniGetLinearVelocity(this.addr, this.tmp);
      this.linearVelocity.x = this.tmp[0];
      this.linearVelocity.y = this.tmp[1];
      return this.linearVelocity;
   }

   private native void jniGetLinearVelocity(long var1, float[] var3);

   public void setAngularVelocity(float omega) {
      this.jniSetAngularVelocity(this.addr, omega);
   }

   private native void jniSetAngularVelocity(long var1, float var3);

   public float getAngularVelocity() {
      return this.jniGetAngularVelocity(this.addr);
   }

   private native float jniGetAngularVelocity(long var1);

   public void applyForce(Vector2 force, Vector2 point, boolean wake) {
      this.jniApplyForce(this.addr, force.x, force.y, point.x, point.y, wake);
   }

   public void applyForce(float forceX, float forceY, float pointX, float pointY, boolean wake) {
      this.jniApplyForce(this.addr, forceX, forceY, pointX, pointY, wake);
   }

   private native void jniApplyForce(long var1, float var3, float var4, float var5, float var6, boolean var7);

   public void applyForceToCenter(Vector2 force, boolean wake) {
      this.jniApplyForceToCenter(this.addr, force.x, force.y, wake);
   }

   public void applyForceToCenter(float forceX, float forceY, boolean wake) {
      this.jniApplyForceToCenter(this.addr, forceX, forceY, wake);
   }

   private native void jniApplyForceToCenter(long var1, float var3, float var4, boolean var5);

   public void applyTorque(float torque, boolean wake) {
      this.jniApplyTorque(this.addr, torque, wake);
   }

   private native void jniApplyTorque(long var1, float var3, boolean var4);

   public void applyLinearImpulse(Vector2 impulse, Vector2 point, boolean wake) {
      this.jniApplyLinearImpulse(this.addr, impulse.x, impulse.y, point.x, point.y, wake);
   }

   public void applyLinearImpulse(float impulseX, float impulseY, float pointX, float pointY, boolean wake) {
      this.jniApplyLinearImpulse(this.addr, impulseX, impulseY, pointX, pointY, wake);
   }

   private native void jniApplyLinearImpulse(long var1, float var3, float var4, float var5, float var6, boolean var7);

   public void applyAngularImpulse(float impulse, boolean wake) {
      this.jniApplyAngularImpulse(this.addr, impulse, wake);
   }

   private native void jniApplyAngularImpulse(long var1, float var3, boolean var4);

   public float getMass() {
      return this.jniGetMass(this.addr);
   }

   private native float jniGetMass(long var1);

   public float getInertia() {
      return this.jniGetInertia(this.addr);
   }

   private native float jniGetInertia(long var1);

   public MassData getMassData() {
      this.jniGetMassData(this.addr, this.tmp);
      this.massData.mass = this.tmp[0];
      this.massData.center.x = this.tmp[1];
      this.massData.center.y = this.tmp[2];
      this.massData.I = this.tmp[3];
      return this.massData;
   }

   private native void jniGetMassData(long var1, float[] var3);

   public void setMassData(MassData data) {
      this.jniSetMassData(this.addr, data.mass, data.center.x, data.center.y, data.I);
   }

   private native void jniSetMassData(long var1, float var3, float var4, float var5, float var6);

   public void resetMassData() {
      this.jniResetMassData(this.addr);
   }

   private native void jniResetMassData(long var1);

   public Vector2 getWorldPoint(Vector2 localPoint) {
      this.jniGetWorldPoint(this.addr, localPoint.x, localPoint.y, this.tmp);
      this.localPoint.x = this.tmp[0];
      this.localPoint.y = this.tmp[1];
      return this.localPoint;
   }

   private native void jniGetWorldPoint(long var1, float var3, float var4, float[] var5);

   public Vector2 getWorldVector(Vector2 localVector) {
      this.jniGetWorldVector(this.addr, localVector.x, localVector.y, this.tmp);
      this.worldVector.x = this.tmp[0];
      this.worldVector.y = this.tmp[1];
      return this.worldVector;
   }

   private native void jniGetWorldVector(long var1, float var3, float var4, float[] var5);

   public Vector2 getLocalPoint(Vector2 worldPoint) {
      this.jniGetLocalPoint(this.addr, worldPoint.x, worldPoint.y, this.tmp);
      this.localPoint2.x = this.tmp[0];
      this.localPoint2.y = this.tmp[1];
      return this.localPoint2;
   }

   private native void jniGetLocalPoint(long var1, float var3, float var4, float[] var5);

   public Vector2 getLocalVector(Vector2 worldVector) {
      this.jniGetLocalVector(this.addr, worldVector.x, worldVector.y, this.tmp);
      this.localVector.x = this.tmp[0];
      this.localVector.y = this.tmp[1];
      return this.localVector;
   }

   private native void jniGetLocalVector(long var1, float var3, float var4, float[] var5);

   public Vector2 getLinearVelocityFromWorldPoint(Vector2 worldPoint) {
      this.jniGetLinearVelocityFromWorldPoint(this.addr, worldPoint.x, worldPoint.y, this.tmp);
      this.linVelWorld.x = this.tmp[0];
      this.linVelWorld.y = this.tmp[1];
      return this.linVelWorld;
   }

   private native void jniGetLinearVelocityFromWorldPoint(long var1, float var3, float var4, float[] var5);

   public Vector2 getLinearVelocityFromLocalPoint(Vector2 localPoint) {
      this.jniGetLinearVelocityFromLocalPoint(this.addr, localPoint.x, localPoint.y, this.tmp);
      this.linVelLoc.x = this.tmp[0];
      this.linVelLoc.y = this.tmp[1];
      return this.linVelLoc;
   }

   private native void jniGetLinearVelocityFromLocalPoint(long var1, float var3, float var4, float[] var5);

   public float getLinearDamping() {
      return this.jniGetLinearDamping(this.addr);
   }

   private native float jniGetLinearDamping(long var1);

   public void setLinearDamping(float linearDamping) {
      this.jniSetLinearDamping(this.addr, linearDamping);
   }

   private native void jniSetLinearDamping(long var1, float var3);

   public float getAngularDamping() {
      return this.jniGetAngularDamping(this.addr);
   }

   private native float jniGetAngularDamping(long var1);

   public void setAngularDamping(float angularDamping) {
      this.jniSetAngularDamping(this.addr, angularDamping);
   }

   private native void jniSetAngularDamping(long var1, float var3);

   public void setType(BodyDef.BodyType type) {
      this.jniSetType(this.addr, type.getValue());
   }

   private native void jniSetType(long var1, int var3);

   public BodyDef.BodyType getType() {
      int type = this.jniGetType(this.addr);
      if (type == 0) {
         return BodyDef.BodyType.StaticBody;
      } else if (type == 1) {
         return BodyDef.BodyType.KinematicBody;
      } else {
         return type == 2 ? BodyDef.BodyType.DynamicBody : BodyDef.BodyType.StaticBody;
      }
   }

   private native int jniGetType(long var1);

   public void setBullet(boolean flag) {
      this.jniSetBullet(this.addr, flag);
   }

   private native void jniSetBullet(long var1, boolean var3);

   public boolean isBullet() {
      return this.jniIsBullet(this.addr);
   }

   private native boolean jniIsBullet(long var1);

   public void setSleepingAllowed(boolean flag) {
      this.jniSetSleepingAllowed(this.addr, flag);
   }

   private native void jniSetSleepingAllowed(long var1, boolean var3);

   public boolean isSleepingAllowed() {
      return this.jniIsSleepingAllowed(this.addr);
   }

   private native boolean jniIsSleepingAllowed(long var1);

   public void setAwake(boolean flag) {
      this.jniSetAwake(this.addr, flag);
   }

   private native void jniSetAwake(long var1, boolean var3);

   public boolean isAwake() {
      return this.jniIsAwake(this.addr);
   }

   private native boolean jniIsAwake(long var1);

   public void setActive(boolean flag) {
      this.jniSetActive(this.addr, flag);
   }

   private native void jniSetActive(long var1, boolean var3);

   public boolean isActive() {
      return this.jniIsActive(this.addr);
   }

   private native boolean jniIsActive(long var1);

   public void setFixedRotation(boolean flag) {
      this.jniSetFixedRotation(this.addr, flag);
   }

   private native void jniSetFixedRotation(long var1, boolean var3);

   public boolean isFixedRotation() {
      return this.jniIsFixedRotation(this.addr);
   }

   private native boolean jniIsFixedRotation(long var1);

   public ArrayList<Fixture> getFixtureList() {
      return this.fixtures;
   }

   public ArrayList<JointEdge> getJointList() {
      return this.joints;
   }

   public float getGravityScale() {
      return this.jniGetGravityScale(this.addr);
   }

   private native float jniGetGravityScale(long var1);

   public void setGravityScale(float scale) {
      this.jniSetGravityScale(this.addr, scale);
   }

   private native void jniSetGravityScale(long var1, float var3);

   public World getWorld() {
      return this.world;
   }

   public Object getUserData() {
      return this.userData;
   }

   public void setUserData(Object userData) {
      this.userData = userData;
   }
}
