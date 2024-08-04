package com.badlogic.gdx.physics.box2d;

public interface ContactListener {
   void beginContact(Contact var1);

   void endContact(Contact var1);

   void preSolve(Contact var1, Manifold var2);

   void postSolve(Contact var1, ContactImpulse var2);
}
