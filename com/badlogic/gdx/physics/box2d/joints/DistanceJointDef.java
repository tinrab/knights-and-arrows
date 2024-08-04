package com.badlogic.gdx.physics.box2d.joints;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.JointDef;

public class DistanceJointDef extends JointDef {
   public final Vector2 localAnchorA = new Vector2();
   public final Vector2 localAnchorB = new Vector2();
   public float length = 1.0F;
   public float frequencyHz = 0.0F;
   public float dampingRatio = 0.0F;

   public DistanceJointDef() {
      this.type = JointDef.JointType.DistanceJoint;
   }

   public void initialize(Body bodyA, Body bodyB, Vector2 anchorA, Vector2 anchorB) {
      this.bodyA = bodyA;
      this.bodyB = bodyB;
      this.localAnchorA.set(bodyA.getLocalPoint(anchorA));
      this.localAnchorB.set(bodyB.getLocalPoint(anchorB));
      this.length = anchorA.dst(anchorB);
   }
}
