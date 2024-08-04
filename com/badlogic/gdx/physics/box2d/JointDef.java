package com.badlogic.gdx.physics.box2d;

public class JointDef {
   public JointDef.JointType type;
   public Body bodyA;
   public Body bodyB;
   public boolean collideConnected;

   public JointDef() {
      this.type = JointDef.JointType.Unknown;
      this.bodyA = null;
      this.bodyB = null;
      this.collideConnected = false;
   }

   public static enum JointType {
      Unknown(0),
      RevoluteJoint(1),
      PrismaticJoint(2),
      DistanceJoint(3),
      PulleyJoint(4),
      MouseJoint(5),
      GearJoint(6),
      WheelJoint(7),
      WeldJoint(8),
      FrictionJoint(9),
      RopeJoint(10);

      public static JointDef.JointType[] valueTypes = new JointDef.JointType[]{Unknown, RevoluteJoint, PrismaticJoint, DistanceJoint, PulleyJoint, MouseJoint, GearJoint, WheelJoint, WeldJoint, FrictionJoint, RopeJoint};
      private int value;

      private JointType(int value) {
         this.value = value;
      }

      public int getValue() {
         return this.value;
      }
   }
}
