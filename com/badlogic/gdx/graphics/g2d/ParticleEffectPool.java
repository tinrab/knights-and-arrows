package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.utils.Pool;

public class ParticleEffectPool extends Pool<ParticleEffectPool.PooledEffect> {
   private final ParticleEffect effect;

   public ParticleEffectPool(ParticleEffect effect, int initialCapacity, int max) {
      super(initialCapacity, max);
      this.effect = effect;
   }

   protected ParticleEffectPool.PooledEffect newObject() {
      return new ParticleEffectPool.PooledEffect(this.effect);
   }

   public ParticleEffectPool.PooledEffect obtain() {
      ParticleEffectPool.PooledEffect effect = (ParticleEffectPool.PooledEffect)super.obtain();
      effect.reset();
      return effect;
   }

   public class PooledEffect extends ParticleEffect {
      PooledEffect(ParticleEffect effect) {
         super(effect);
      }

      public void reset() {
         super.reset();
      }

      public void free() {
         ParticleEffectPool.this.free(this);
      }
   }
}
