package com.badlogic.gdx.physics.box2d.joints;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.JointDef;

public class RopeJointDef extends JointDef {
   public final Vector2 localAnchorA = new Vector2(-1.0F, 0.0F);
   public final Vector2 localAnchorB = new Vector2(1.0F, 0.0F);
   public float maxLength = 0.0F;

   public RopeJointDef() {
      this.type = JointDef.JointType.RopeJoint;
   }
}
