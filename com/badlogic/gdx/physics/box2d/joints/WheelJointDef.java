package com.badlogic.gdx.physics.box2d.joints;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.JointDef;

public class WheelJointDef extends JointDef {
   public final Vector2 localAnchorA = new Vector2();
   public final Vector2 localAnchorB = new Vector2();
   public final Vector2 localAxisA = new Vector2(1.0F, 0.0F);
   public boolean enableMotor = false;
   public float maxMotorTorque = 0.0F;
   public float motorSpeed = 0.0F;
   public float frequencyHz = 2.0F;
   public float dampingRatio = 0.7F;

   public WheelJointDef() {
      this.type = JointDef.JointType.WheelJoint;
   }

   public void initialize(Body bodyA, Body bodyB, Vector2 anchor, Vector2 axis) {
      this.bodyA = bodyA;
      this.bodyB = bodyB;
      this.localAnchorA.set(bodyA.getLocalPoint(anchor));
      this.localAnchorB.set(bodyB.getLocalPoint(anchor));
      this.localAxisA.set(bodyA.getLocalVector(axis));
   }
}
