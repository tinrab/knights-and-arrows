package com.badlogic.gdx.physics.box2d.joints;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.JointDef;

public class PulleyJointDef extends JointDef {
   private static final float minPulleyLength = 2.0F;
   public final Vector2 groundAnchorA = new Vector2(-1.0F, 1.0F);
   public final Vector2 groundAnchorB = new Vector2(1.0F, 1.0F);
   public final Vector2 localAnchorA = new Vector2(-1.0F, 0.0F);
   public final Vector2 localAnchorB = new Vector2(1.0F, 0.0F);
   public float lengthA = 0.0F;
   public float lengthB = 0.0F;
   public float ratio = 1.0F;

   public PulleyJointDef() {
      this.type = JointDef.JointType.PulleyJoint;
      this.collideConnected = true;
   }

   public void initialize(Body bodyA, Body bodyB, Vector2 groundAnchorA, Vector2 groundAnchorB, Vector2 anchorA, Vector2 anchorB, float ratio) {
      this.bodyA = bodyA;
      this.bodyB = bodyB;
      this.groundAnchorA.set(groundAnchorA);
      this.groundAnchorB.set(groundAnchorB);
      this.localAnchorA.set(bodyA.getLocalPoint(anchorA));
      this.localAnchorB.set(bodyB.getLocalPoint(anchorB));
      this.lengthA = anchorA.dst(groundAnchorA);
      this.lengthB = anchorB.dst(groundAnchorB);
      this.ratio = ratio;
      float var10000 = this.lengthA + ratio * this.lengthB;
   }
}
