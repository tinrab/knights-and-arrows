package com.badlogic.gdx.physics.box2d;

public interface ContactFilter {
   boolean shouldCollide(Fixture var1, Fixture var2);
}
