package com.badlogic.gdx.physics.box2d;

import com.badlogic.gdx.math.Vector2;

public class BodyDef {
   public BodyDef.BodyType type;
   public final Vector2 position;
   public float angle;
   public final Vector2 linearVelocity;
   public float angularVelocity;
   public float linearDamping;
   public float angularDamping;
   public boolean allowSleep;
   public boolean awake;
   public boolean fixedRotation;
   public boolean bullet;
   public boolean active;
   public float gravityScale;

   public BodyDef() {
      this.type = BodyDef.BodyType.StaticBody;
      this.position = new Vector2();
      this.angle = 0.0F;
      this.linearVelocity = new Vector2();
      this.angularVelocity = 0.0F;
      this.linearDamping = 0.0F;
      this.angularDamping = 0.0F;
      this.allowSleep = true;
      this.awake = true;
      this.fixedRotation = false;
      this.bullet = false;
      this.active = true;
      this.gravityScale = 1.0F;
   }

   public static enum BodyType {
      StaticBody(0),
      KinematicBody(1),
      DynamicBody(2);

      private int value;

      private BodyType(int value) {
         this.value = value;
      }

      public int getValue() {
         return this.value;
      }
   }
}
