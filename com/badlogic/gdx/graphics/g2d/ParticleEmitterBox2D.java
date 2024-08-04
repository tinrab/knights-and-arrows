package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import java.io.BufferedReader;
import java.io.IOException;

public class ParticleEmitterBox2D extends ParticleEmitter {
   final World world;
   final Vector2 startPoint = new Vector2();
   final Vector2 endPoint = new Vector2();
   boolean particleCollided;
   float normalAngle;
   private static final float EPSILON = 0.001F;
   final RayCastCallback rayCallBack = new RayCastCallback() {
      public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
         ParticleEmitterBox2D.this.particleCollided = true;
         ParticleEmitterBox2D.this.normalAngle = MathUtils.atan2(normal.y, normal.x) * 57.295776F;
         return fraction;
      }
   };

   public ParticleEmitterBox2D(World world) {
      this.world = world;
   }

   public ParticleEmitterBox2D(World world, BufferedReader reader) throws IOException {
      super(reader);
      this.world = world;
   }

   public ParticleEmitterBox2D(World world, ParticleEmitter emitter) {
      super(emitter);
      this.world = world;
   }

   protected ParticleEmitter.Particle newParticle(Sprite sprite) {
      return new ParticleEmitterBox2D.ParticleBox2D(sprite);
   }

   private class ParticleBox2D extends ParticleEmitter.Particle {
      public ParticleBox2D(Sprite sprite) {
         super(sprite);
      }

      public void translate(float velocityX, float velocityY) {
         if (!(velocityX * velocityX + velocityY * velocityY < 0.001F)) {
            float x = this.getX() + this.getWidth() / 2.0F;
            float y = this.getY() + this.getHeight() / 2.0F;
            ParticleEmitterBox2D.this.particleCollided = false;
            ParticleEmitterBox2D.this.startPoint.set(x, y);
            ParticleEmitterBox2D.this.endPoint.set(x + velocityX, y + velocityY);
            if (ParticleEmitterBox2D.this.world != null) {
               ParticleEmitterBox2D.this.world.rayCast(ParticleEmitterBox2D.this.rayCallBack, ParticleEmitterBox2D.this.startPoint, ParticleEmitterBox2D.this.endPoint);
            }

            if (ParticleEmitterBox2D.this.particleCollided) {
               this.angle = 2.0F * ParticleEmitterBox2D.this.normalAngle - this.angle - 180.0F;
               this.angleCos = MathUtils.cosDeg(this.angle);
               this.angleSin = MathUtils.sinDeg(this.angle);
               velocityX = this.velocity * this.angleCos;
               velocityY = this.velocity * this.angleSin;
            }

            super.translate(velocityX, velocityY);
         }
      }
   }
}
