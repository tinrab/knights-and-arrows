package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Animation;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pool;

public class AnimationController extends BaseAnimationController {
   protected final Pool<AnimationController.AnimationDesc> animationPool = new Pool<AnimationController.AnimationDesc>() {
      protected AnimationController.AnimationDesc newObject() {
         return new AnimationController.AnimationDesc();
      }
   };
   public AnimationController.AnimationDesc current;
   public AnimationController.AnimationDesc queued;
   public float queuedTransitionTime;
   public AnimationController.AnimationDesc previous;
   public float transitionCurrentTime;
   public float transitionTargetTime;
   public boolean inAction;
   private boolean updating;

   public AnimationController(ModelInstance target) {
      super(target);
   }

   private AnimationController.AnimationDesc obtain(Animation anim, int loopCount, float speed, AnimationController.AnimationListener listener) {
      AnimationController.AnimationDesc result = (AnimationController.AnimationDesc)this.animationPool.obtain();
      result.animation = anim;
      result.listener = listener;
      result.loopCount = loopCount;
      result.speed = speed;
      result.time = speed < 0.0F ? anim.duration : 0.0F;
      return result;
   }

   private AnimationController.AnimationDesc obtain(String id, int loopCount, float speed, AnimationController.AnimationListener listener) {
      Animation anim = this.target.getAnimation(id);
      if (anim == null) {
         throw new GdxRuntimeException("Unknown animation: " + id);
      } else {
         return this.obtain(anim, loopCount, speed, listener);
      }
   }

   private AnimationController.AnimationDesc obtain(AnimationController.AnimationDesc anim) {
      return this.obtain(anim.animation, anim.loopCount, anim.speed, anim.listener);
   }

   public void update(float delta) {
      if (this.current != null && this.current.loopCount != 0 && this.current.animation != null) {
         this.updating = true;
         float remain = this.current.update(delta);
         if (remain != 0.0F && this.queued != null) {
            this.inAction = false;
            this.animate(this.queued, this.queuedTransitionTime);
            this.queued = null;
            this.updating = false;
            this.update(remain);
         } else {
            if (this.previous != null && (this.transitionCurrentTime += delta) >= this.transitionTargetTime) {
               this.animationPool.free(this.previous);
               this.previous = null;
            }

            if (this.previous != null) {
               this.applyAnimations(this.previous.animation, this.previous.time, this.current.animation, this.current.time, this.transitionCurrentTime / this.transitionTargetTime);
            } else {
               this.applyAnimation(this.current.animation, this.current.time);
            }

            this.updating = false;
         }
      }
   }

   public void setAnimation(String id, int loopCount, float speed, AnimationController.AnimationListener listener) {
      this.setAnimation(this.obtain(id, loopCount, speed, listener));
   }

   protected void setAnimation(Animation anim, int loopCount, float speed, AnimationController.AnimationListener listener) {
      this.setAnimation(this.obtain(anim, loopCount, speed, listener));
   }

   protected void setAnimation(AnimationController.AnimationDesc anim) {
      if (this.updating) {
         throw new GdxRuntimeException("Cannot change animation during update");
      } else {
         if (this.current == null) {
            this.current = anim;
         } else {
            if (this.current.animation == anim.animation) {
               anim.time = this.current.time;
            }

            this.animationPool.free(this.current);
            this.current = anim;
         }

      }
   }

   public void animate(String id, int loopCount, float speed, AnimationController.AnimationListener listener, float transitionTime) {
      this.animate(this.obtain(id, loopCount, speed, listener), transitionTime);
   }

   protected void animate(Animation anim, int loopCount, float speed, AnimationController.AnimationListener listener, float transitionTime) {
      this.animate(this.obtain(anim, loopCount, speed, listener), transitionTime);
   }

   protected void animate(AnimationController.AnimationDesc anim, float transitionTime) {
      if (this.current == null) {
         this.current = anim;
      } else if (this.inAction) {
         this.queue(anim, transitionTime);
      } else if (this.current.animation == anim.animation) {
         anim.time = this.current.time;
         this.animationPool.free(this.current);
         this.current = anim;
      } else {
         if (this.previous != null) {
            this.animationPool.free(this.previous);
         }

         this.previous = this.current;
         this.current = anim;
         this.transitionCurrentTime = 0.0F;
         this.transitionTargetTime = transitionTime;
      }

   }

   public void queue(String id, int loopCount, float speed, AnimationController.AnimationListener listener, float transitionTime) {
      this.queue(this.obtain(id, loopCount, speed, listener), transitionTime);
   }

   protected void queue(Animation anim, int loopCount, float speed, AnimationController.AnimationListener listener, float transitionTime) {
      this.queue(this.obtain(anim, loopCount, speed, listener), transitionTime);
   }

   protected void queue(AnimationController.AnimationDesc anim, float transitionTime) {
      if (this.current != null && this.current.loopCount != 0) {
         if (this.queued != null) {
            this.animationPool.free(this.queued);
         }

         this.queued = anim;
         this.queuedTransitionTime = transitionTime;
         if (this.current.loopCount < 0) {
            this.current.loopCount = 1;
         }
      } else {
         this.animate(anim, transitionTime);
      }

   }

   public void action(String id, int loopCount, float speed, AnimationController.AnimationListener listener, float transitionTime) {
      this.action(this.obtain(id, loopCount, speed, listener), transitionTime);
   }

   protected void action(Animation anim, int loopCount, float speed, AnimationController.AnimationListener listener, float transitionTime) {
      this.action(this.obtain(anim, loopCount, speed, listener), transitionTime);
   }

   protected void action(AnimationController.AnimationDesc anim, float transitionTime) {
      if (anim.loopCount < 0) {
         throw new GdxRuntimeException("An action cannot be continuous");
      } else {
         if (this.current != null && this.current.loopCount != 0) {
            AnimationController.AnimationDesc toQueue = this.inAction ? null : this.obtain(this.current);
            this.inAction = false;
            this.animate(anim, transitionTime);
            this.inAction = true;
            if (toQueue != null) {
               this.queue(toQueue, transitionTime);
            }
         } else {
            this.animate(anim, transitionTime);
         }

      }
   }

   public static class AnimationDesc {
      public AnimationController.AnimationListener listener;
      public Animation animation;
      public float speed;
      public float time;
      public int loopCount;

      public float update(float delta) {
         if (this.loopCount != 0 && this.animation != null) {
            float duration = this.animation.duration;
            float diff = this.speed * delta;
            this.time += diff;
            int loops = (int)Math.abs(this.time / duration);
            if (this.time < 0.0F) {
               ++loops;

               while(this.time < 0.0F) {
                  this.time += duration;
               }
            }

            this.time = Math.abs(this.time % duration);

            for(int i = 0; i < loops; ++i) {
               if (this.loopCount > 0) {
                  --this.loopCount;
               }

               if (this.listener != null) {
                  this.listener.onLoop(this);
               }

               if (this.loopCount == 0) {
                  float result = (float)(loops - 1 - i) * duration + (diff < 0.0F ? duration - this.time : this.time);
                  this.time = diff < 0.0F ? duration : 0.0F;
                  if (this.listener != null) {
                     this.listener.onEnd(this);
                  }

                  return result;
               }
            }

            return 0.0F;
         } else {
            return delta;
         }
      }
   }

   public interface AnimationListener {
      void onEnd(AnimationController.AnimationDesc var1);

      void onLoop(AnimationController.AnimationDesc var1);
   }
}
