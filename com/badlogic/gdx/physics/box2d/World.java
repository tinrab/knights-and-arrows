package com.badlogic.gdx.physics.box2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.FrictionJoint;
import com.badlogic.gdx.physics.box2d.joints.FrictionJointDef;
import com.badlogic.gdx.physics.box2d.joints.GearJoint;
import com.badlogic.gdx.physics.box2d.joints.GearJointDef;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.physics.box2d.joints.PulleyJoint;
import com.badlogic.gdx.physics.box2d.joints.PulleyJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.RopeJoint;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJoint;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.badlogic.gdx.physics.box2d.joints.WheelJoint;
import com.badlogic.gdx.physics.box2d.joints.WheelJointDef;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.LongMap;
import com.badlogic.gdx.utils.Pool;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class World implements Disposable {
   protected final Pool<Body> freeBodies = new Pool<Body>(100, 200) {
      protected Body newObject() {
         return new Body(World.this, 0L);
      }
   };
   protected final Pool<Fixture> freeFixtures = new Pool<Fixture>(100, 200) {
      protected Fixture newObject() {
         return new Fixture((Body)null, 0L);
      }
   };
   private final long addr;
   protected final LongMap<Body> bodies = new LongMap(100);
   protected final LongMap<Fixture> fixtures = new LongMap(100);
   protected final LongMap<Joint> joints = new LongMap(100);
   protected ContactFilter contactFilter = null;
   protected ContactListener contactListener = null;
   final float[] tmpGravity = new float[2];
   final Vector2 gravity = new Vector2();
   private QueryCallback queryCallback = null;
   private long[] contactAddrs = new long[200];
   private final ArrayList<Contact> contacts = new ArrayList();
   private final ArrayList<Contact> freeContacts = new ArrayList();
   private final Contact contact = new Contact(this, 0L);
   private final Manifold manifold = new Manifold(0L);
   private final ContactImpulse impulse = new ContactImpulse(this, 0L);
   private RayCastCallback rayCastCallback = null;
   private Vector2 rayPoint = new Vector2();
   private Vector2 rayNormal = new Vector2();

   public World(Vector2 gravity, boolean doSleep) {
      this.addr = this.newWorld(gravity.x, gravity.y, doSleep);
      this.contacts.ensureCapacity(this.contactAddrs.length);
      this.freeContacts.ensureCapacity(this.contactAddrs.length);

      for(int i = 0; i < this.contactAddrs.length; ++i) {
         this.freeContacts.add(new Contact(this, 0L));
      }

   }

   private native long newWorld(float var1, float var2, boolean var3);

   public void setDestructionListener(DestructionListener listener) {
   }

   public void setContactFilter(ContactFilter filter) {
      this.contactFilter = filter;
      this.setUseDefaultContactFilter(filter == null);
   }

   private native void setUseDefaultContactFilter(boolean var1);

   public void setContactListener(ContactListener listener) {
      this.contactListener = listener;
   }

   public Body createBody(BodyDef def) {
      long bodyAddr = this.jniCreateBody(this.addr, def.type.getValue(), def.position.x, def.position.y, def.angle, def.linearVelocity.x, def.linearVelocity.y, def.angularVelocity, def.linearDamping, def.angularDamping, def.allowSleep, def.awake, def.fixedRotation, def.bullet, def.active, def.gravityScale);
      Body body = (Body)this.freeBodies.obtain();
      body.reset(bodyAddr);
      this.bodies.put(body.addr, body);
      return body;
   }

   private native long jniCreateBody(long var1, int var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, boolean var12, boolean var13, boolean var14, boolean var15, boolean var16, float var17);

   public void destroyBody(Body body) {
      body.setUserData((Object)null);
      this.bodies.remove(body.addr);
      ArrayList fixtureList = body.getFixtureList();

      while(!fixtureList.isEmpty()) {
         ((Fixture)this.fixtures.remove(((Fixture)fixtureList.remove(0)).addr)).setUserData((Object)null);
      }

      ArrayList jointList = body.getJointList();

      while(!jointList.isEmpty()) {
         this.destroyJoint(((JointEdge)body.getJointList().get(0)).joint);
      }

      this.jniDestroyBody(this.addr, body.addr);
      this.freeBodies.free(body);
   }

   private native void jniDestroyBody(long var1, long var3);

   public Joint createJoint(JointDef def) {
      long jointAddr = this.createProperJoint(def);
      Joint joint = null;
      if (def.type == JointDef.JointType.DistanceJoint) {
         joint = new DistanceJoint(this, jointAddr);
      }

      if (def.type == JointDef.JointType.FrictionJoint) {
         joint = new FrictionJoint(this, jointAddr);
      }

      if (def.type == JointDef.JointType.GearJoint) {
         joint = new GearJoint(this, jointAddr);
      }

      if (def.type == JointDef.JointType.MouseJoint) {
         joint = new MouseJoint(this, jointAddr);
      }

      if (def.type == JointDef.JointType.PrismaticJoint) {
         joint = new PrismaticJoint(this, jointAddr);
      }

      if (def.type == JointDef.JointType.PulleyJoint) {
         joint = new PulleyJoint(this, jointAddr);
      }

      if (def.type == JointDef.JointType.RevoluteJoint) {
         joint = new RevoluteJoint(this, jointAddr);
      }

      if (def.type == JointDef.JointType.WeldJoint) {
         joint = new WeldJoint(this, jointAddr);
      }

      if (def.type == JointDef.JointType.RopeJoint) {
         joint = new RopeJoint(this, jointAddr);
      }

      if (def.type == JointDef.JointType.WheelJoint) {
         joint = new WheelJoint(this, jointAddr);
      }

      if (joint != null) {
         this.joints.put(((Joint)joint).addr, joint);
      }

      JointEdge jointEdgeA = new JointEdge(def.bodyB, (Joint)joint);
      JointEdge jointEdgeB = new JointEdge(def.bodyA, (Joint)joint);
      ((Joint)joint).jointEdgeA = jointEdgeA;
      ((Joint)joint).jointEdgeB = jointEdgeB;
      def.bodyA.joints.add(jointEdgeA);
      def.bodyB.joints.add(jointEdgeB);
      return (Joint)joint;
   }

   private long createProperJoint(JointDef def) {
      if (def.type == JointDef.JointType.DistanceJoint) {
         DistanceJointDef d = (DistanceJointDef)def;
         return this.jniCreateDistanceJoint(this.addr, d.bodyA.addr, d.bodyB.addr, d.collideConnected, d.localAnchorA.x, d.localAnchorA.y, d.localAnchorB.x, d.localAnchorB.y, d.length, d.frequencyHz, d.dampingRatio);
      } else if (def.type == JointDef.JointType.FrictionJoint) {
         FrictionJointDef d = (FrictionJointDef)def;
         return this.jniCreateFrictionJoint(this.addr, d.bodyA.addr, d.bodyB.addr, d.collideConnected, d.localAnchorA.x, d.localAnchorA.y, d.localAnchorB.x, d.localAnchorB.y, d.maxForce, d.maxTorque);
      } else if (def.type == JointDef.JointType.GearJoint) {
         GearJointDef d = (GearJointDef)def;
         return this.jniCreateGearJoint(this.addr, d.bodyA.addr, d.bodyB.addr, d.collideConnected, d.joint1.addr, d.joint2.addr, d.ratio);
      } else if (def.type == JointDef.JointType.MouseJoint) {
         MouseJointDef d = (MouseJointDef)def;
         return this.jniCreateMouseJoint(this.addr, d.bodyA.addr, d.bodyB.addr, d.collideConnected, d.target.x, d.target.y, d.maxForce, d.frequencyHz, d.dampingRatio);
      } else if (def.type == JointDef.JointType.PrismaticJoint) {
         PrismaticJointDef d = (PrismaticJointDef)def;
         return this.jniCreatePrismaticJoint(this.addr, d.bodyA.addr, d.bodyB.addr, d.collideConnected, d.localAnchorA.x, d.localAnchorA.y, d.localAnchorB.x, d.localAnchorB.y, d.localAxisA.x, d.localAxisA.y, d.referenceAngle, d.enableLimit, d.lowerTranslation, d.upperTranslation, d.enableMotor, d.maxMotorForce, d.motorSpeed);
      } else if (def.type == JointDef.JointType.PulleyJoint) {
         PulleyJointDef d = (PulleyJointDef)def;
         return this.jniCreatePulleyJoint(this.addr, d.bodyA.addr, d.bodyB.addr, d.collideConnected, d.groundAnchorA.x, d.groundAnchorA.y, d.groundAnchorB.x, d.groundAnchorB.y, d.localAnchorA.x, d.localAnchorA.y, d.localAnchorB.x, d.localAnchorB.y, d.lengthA, d.lengthB, d.ratio);
      } else if (def.type == JointDef.JointType.RevoluteJoint) {
         RevoluteJointDef d = (RevoluteJointDef)def;
         return this.jniCreateRevoluteJoint(this.addr, d.bodyA.addr, d.bodyB.addr, d.collideConnected, d.localAnchorA.x, d.localAnchorA.y, d.localAnchorB.x, d.localAnchorB.y, d.referenceAngle, d.enableLimit, d.lowerAngle, d.upperAngle, d.enableMotor, d.motorSpeed, d.maxMotorTorque);
      } else if (def.type == JointDef.JointType.WeldJoint) {
         WeldJointDef d = (WeldJointDef)def;
         return this.jniCreateWeldJoint(this.addr, d.bodyA.addr, d.bodyB.addr, d.collideConnected, d.localAnchorA.x, d.localAnchorA.y, d.localAnchorB.x, d.localAnchorB.y, d.referenceAngle);
      } else if (def.type == JointDef.JointType.RopeJoint) {
         RopeJointDef d = (RopeJointDef)def;
         return this.jniCreateRopeJoint(this.addr, d.bodyA.addr, d.bodyB.addr, d.collideConnected, d.localAnchorA.x, d.localAnchorA.y, d.localAnchorB.x, d.localAnchorB.y, d.maxLength);
      } else if (def.type == JointDef.JointType.WheelJoint) {
         WheelJointDef d = (WheelJointDef)def;
         return this.jniCreateWheelJoint(this.addr, d.bodyA.addr, d.bodyB.addr, d.collideConnected, d.localAnchorA.x, d.localAnchorA.y, d.localAnchorB.x, d.localAnchorB.y, d.localAxisA.x, d.localAxisA.y, d.enableMotor, d.maxMotorTorque, d.motorSpeed, d.frequencyHz, d.dampingRatio);
      } else {
         return 0L;
      }
   }

   private native long jniCreateWheelJoint(long var1, long var3, long var5, boolean var7, float var8, float var9, float var10, float var11, float var12, float var13, boolean var14, float var15, float var16, float var17, float var18);

   private native long jniCreateRopeJoint(long var1, long var3, long var5, boolean var7, float var8, float var9, float var10, float var11, float var12);

   private native long jniCreateDistanceJoint(long var1, long var3, long var5, boolean var7, float var8, float var9, float var10, float var11, float var12, float var13, float var14);

   private native long jniCreateFrictionJoint(long var1, long var3, long var5, boolean var7, float var8, float var9, float var10, float var11, float var12, float var13);

   private native long jniCreateGearJoint(long var1, long var3, long var5, boolean var7, long var8, long var10, float var12);

   private native long jniCreateMouseJoint(long var1, long var3, long var5, boolean var7, float var8, float var9, float var10, float var11, float var12);

   private native long jniCreatePrismaticJoint(long var1, long var3, long var5, boolean var7, float var8, float var9, float var10, float var11, float var12, float var13, float var14, boolean var15, float var16, float var17, boolean var18, float var19, float var20);

   private native long jniCreatePulleyJoint(long var1, long var3, long var5, boolean var7, float var8, float var9, float var10, float var11, float var12, float var13, float var14, float var15, float var16, float var17, float var18);

   private native long jniCreateRevoluteJoint(long var1, long var3, long var5, boolean var7, float var8, float var9, float var10, float var11, float var12, boolean var13, float var14, float var15, boolean var16, float var17, float var18);

   private native long jniCreateWeldJoint(long var1, long var3, long var5, boolean var7, float var8, float var9, float var10, float var11, float var12);

   public void destroyJoint(Joint joint) {
      joint.setUserData((Object)null);
      this.joints.remove(joint.addr);
      joint.jointEdgeA.other.joints.remove(joint.jointEdgeB);
      joint.jointEdgeB.other.joints.remove(joint.jointEdgeA);
      this.jniDestroyJoint(this.addr, joint.addr);
   }

   private native void jniDestroyJoint(long var1, long var3);

   public void step(float timeStep, int velocityIterations, int positionIterations) {
      this.jniStep(this.addr, timeStep, velocityIterations, positionIterations);
   }

   private native void jniStep(long var1, float var3, int var4, int var5);

   public void clearForces() {
      this.jniClearForces(this.addr);
   }

   private native void jniClearForces(long var1);

   public void setWarmStarting(boolean flag) {
      this.jniSetWarmStarting(this.addr, flag);
   }

   private native void jniSetWarmStarting(long var1, boolean var3);

   public void setContinuousPhysics(boolean flag) {
      this.jniSetContiousPhysics(this.addr, flag);
   }

   private native void jniSetContiousPhysics(long var1, boolean var3);

   public int getProxyCount() {
      return this.jniGetProxyCount(this.addr);
   }

   private native int jniGetProxyCount(long var1);

   public int getBodyCount() {
      return this.jniGetBodyCount(this.addr);
   }

   private native int jniGetBodyCount(long var1);

   public int getJointCount() {
      return this.jniGetJointcount(this.addr);
   }

   private native int jniGetJointcount(long var1);

   public int getContactCount() {
      return this.jniGetContactCount(this.addr);
   }

   private native int jniGetContactCount(long var1);

   public void setGravity(Vector2 gravity) {
      this.jniSetGravity(this.addr, gravity.x, gravity.y);
   }

   private native void jniSetGravity(long var1, float var3, float var4);

   public Vector2 getGravity() {
      this.jniGetGravity(this.addr, this.tmpGravity);
      this.gravity.x = this.tmpGravity[0];
      this.gravity.y = this.tmpGravity[1];
      return this.gravity;
   }

   private native void jniGetGravity(long var1, float[] var3);

   public boolean isLocked() {
      return this.jniIsLocked(this.addr);
   }

   private native boolean jniIsLocked(long var1);

   public void setAutoClearForces(boolean flag) {
      this.jniSetAutoClearForces(this.addr, flag);
   }

   private native void jniSetAutoClearForces(long var1, boolean var3);

   public boolean getAutoClearForces() {
      return this.jniGetAutoClearForces(this.addr);
   }

   private native boolean jniGetAutoClearForces(long var1);

   public void QueryAABB(QueryCallback callback, float lowerX, float lowerY, float upperX, float upperY) {
      this.queryCallback = callback;
      this.jniQueryAABB(this.addr, lowerX, lowerY, upperX, upperY);
   }

   private native void jniQueryAABB(long var1, float var3, float var4, float var5, float var6);

   public List<Contact> getContactList() {
      int numContacts = this.getContactCount();
      int i;
      if (numContacts > this.contactAddrs.length) {
         i = 2 * numContacts;
         this.contactAddrs = new long[i];
         this.contacts.ensureCapacity(i);
         this.freeContacts.ensureCapacity(i);
      }

      if (numContacts > this.freeContacts.size()) {
         i = this.freeContacts.size();

         for(int i = 0; i < numContacts - i; ++i) {
            this.freeContacts.add(new Contact(this, 0L));
         }
      }

      this.jniGetContactList(this.addr, this.contactAddrs);
      this.contacts.clear();

      for(i = 0; i < numContacts; ++i) {
         Contact contact = (Contact)this.freeContacts.get(i);
         contact.addr = this.contactAddrs[i];
         this.contacts.add(contact);
      }

      return this.contacts;
   }

   public Iterator<Body> getBodies() {
      return this.bodies.values();
   }

   public Iterator<Joint> getJoints() {
      return this.joints.values();
   }

   private native void jniGetContactList(long var1, long[] var3);

   public void dispose() {
      this.jniDispose(this.addr);
   }

   private native void jniDispose(long var1);

   private boolean contactFilter(long fixtureA, long fixtureB) {
      if (this.contactFilter != null) {
         return this.contactFilter.shouldCollide((Fixture)this.fixtures.get(fixtureA), (Fixture)this.fixtures.get(fixtureB));
      } else {
         Filter filterA = ((Fixture)this.fixtures.get(fixtureA)).getFilterData();
         Filter filterB = ((Fixture)this.fixtures.get(fixtureB)).getFilterData();
         if (filterA.groupIndex == filterB.groupIndex && filterA.groupIndex != 0) {
            return filterA.groupIndex > 0;
         } else {
            boolean collide = (filterA.maskBits & filterB.categoryBits) != 0 && (filterA.categoryBits & filterB.maskBits) != 0;
            return collide;
         }
      }
   }

   private void beginContact(long contactAddr) {
      this.contact.addr = contactAddr;
      if (this.contactListener != null) {
         this.contactListener.beginContact(this.contact);
      }

   }

   private void endContact(long contactAddr) {
      this.contact.addr = contactAddr;
      if (this.contactListener != null) {
         this.contactListener.endContact(this.contact);
      }

   }

   private void preSolve(long contactAddr, long manifoldAddr) {
      this.contact.addr = contactAddr;
      this.manifold.addr = manifoldAddr;
      if (this.contactListener != null) {
         this.contactListener.preSolve(this.contact, this.manifold);
      }

   }

   private void postSolve(long contactAddr, long impulseAddr) {
      this.contact.addr = contactAddr;
      this.impulse.addr = impulseAddr;
      if (this.contactListener != null) {
         this.contactListener.postSolve(this.contact, this.impulse);
      }

   }

   private boolean reportFixture(long addr) {
      return this.queryCallback != null ? this.queryCallback.reportFixture((Fixture)this.fixtures.get(addr)) : false;
   }

   public static native void setVelocityThreshold(float var0);

   public static native float getVelocityThreshold();

   public void rayCast(RayCastCallback callback, Vector2 point1, Vector2 point2) {
      this.rayCastCallback = callback;
      this.jniRayCast(this.addr, point1.x, point1.y, point2.x, point2.y);
   }

   private native void jniRayCast(long var1, float var3, float var4, float var5, float var6);

   private float reportRayFixture(long addr, float pX, float pY, float nX, float nY, float fraction) {
      if (this.rayCastCallback != null) {
         this.rayPoint.x = pX;
         this.rayPoint.y = pY;
         this.rayNormal.x = nX;
         this.rayNormal.y = nY;
         return this.rayCastCallback.reportRayFixture((Fixture)this.fixtures.get(addr), this.rayPoint, this.rayNormal, fraction);
      } else {
         return 0.0F;
      }
   }
}
