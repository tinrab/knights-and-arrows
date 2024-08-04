package com.badlogic.gdx.physics.box2d;

public class FixtureDef {
   public Shape shape;
   public float friction = 0.2F;
   public float restitution = 0.0F;
   public float density = 0.0F;
   public boolean isSensor = false;
   public final Filter filter = new Filter();
}
