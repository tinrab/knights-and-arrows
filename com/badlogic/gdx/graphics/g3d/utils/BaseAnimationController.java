package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Animation;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodeAnimation;
import com.badlogic.gdx.graphics.g3d.model.NodeKeyframe;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;
import java.util.Iterator;

public class BaseAnimationController {
   private final Pool<BaseAnimationController.Transform> transformPool = new Pool<BaseAnimationController.Transform>() {
      protected BaseAnimationController.Transform newObject() {
         return new BaseAnimationController.Transform();
      }
   };
   private static final ObjectMap<Node, BaseAnimationController.Transform> transforms = new ObjectMap();
   private boolean applying = false;
   public final ModelInstance target;
   private static final BaseAnimationController.Transform tmpT = new BaseAnimationController.Transform();

   public BaseAnimationController(ModelInstance target) {
      this.target = target;
   }

   protected void begin() {
      if (this.applying) {
         throw new GdxRuntimeException("You must call end() after each call to being()");
      } else {
         this.applying = true;
      }
   }

   protected void apply(Animation animation, float time, float weight) {
      if (!this.applying) {
         throw new GdxRuntimeException("You must call begin() before adding an animation");
      } else {
         applyAnimation(transforms, this.transformPool, weight, animation, time);
      }
   }

   protected void end() {
      if (!this.applying) {
         throw new GdxRuntimeException("You must call begin() first");
      } else {
         Iterator var2 = transforms.entries().iterator();

         while(var2.hasNext()) {
            ObjectMap.Entry<Node, BaseAnimationController.Transform> entry = (ObjectMap.Entry)var2.next();
            ((BaseAnimationController.Transform)entry.value).toMatrix4(((Node)entry.key).localTransform);
            this.transformPool.free((BaseAnimationController.Transform)entry.value);
         }

         transforms.clear();
         this.target.calculateTransforms();
         this.applying = false;
      }
   }

   protected void applyAnimation(Animation animation, float time) {
      if (this.applying) {
         throw new GdxRuntimeException("Call end() first");
      } else {
         applyAnimation((ObjectMap)null, (Pool)null, 1.0F, animation, time);
         this.target.calculateTransforms();
      }
   }

   protected void applyAnimations(Animation anim1, float time1, Animation anim2, float time2, float weight) {
      if (anim2 != null && weight != 0.0F) {
         if (anim1 != null && weight != 1.0F) {
            if (this.applying) {
               throw new GdxRuntimeException("Call end() first");
            }

            this.begin();
            this.apply(anim1, time1, 1.0F);
            this.apply(anim2, time2, weight);
            this.end();
         } else {
            this.applyAnimation(anim2, time2);
         }
      } else {
         this.applyAnimation(anim1, time1);
      }

   }

   protected static void applyAnimation(ObjectMap<Node, BaseAnimationController.Transform> out, Pool<BaseAnimationController.Transform> pool, float alpha, Animation animation, float time) {
      Iterator var6 = animation.nodeAnimations.iterator();

      while(var6.hasNext()) {
         NodeAnimation nodeAnim = (NodeAnimation)var6.next();
         Node node = nodeAnim.node;
         node.isAnimated = true;
         int n = nodeAnim.keyframes.size - 1;
         int first = 0;
         int second = -1;

         for(int i = 0; i < n; ++i) {
            if (time >= ((NodeKeyframe)nodeAnim.keyframes.get(i)).keytime && time <= ((NodeKeyframe)nodeAnim.keyframes.get(i + 1)).keytime) {
               first = i;
               second = i + 1;
               break;
            }
         }

         BaseAnimationController.Transform transform = tmpT;
         NodeKeyframe firstKeyframe = (NodeKeyframe)nodeAnim.keyframes.get(first);
         transform.set(firstKeyframe.translation, firstKeyframe.rotation, firstKeyframe.scale);
         if (second > first) {
            NodeKeyframe secondKeyframe = (NodeKeyframe)nodeAnim.keyframes.get(second);
            float t = (time - firstKeyframe.keytime) / (secondKeyframe.keytime - firstKeyframe.keytime);
            transform.lerp(secondKeyframe.translation, secondKeyframe.rotation, secondKeyframe.scale, t);
         }

         if (out == null) {
            transform.toMatrix4(node.localTransform);
         } else if (out.containsKey(node)) {
            if (alpha == 1.0F) {
               ((BaseAnimationController.Transform)out.get(node)).set(transform);
            } else {
               ((BaseAnimationController.Transform)out.get(node)).lerp(transform, alpha);
            }
         } else {
            out.put(node, ((BaseAnimationController.Transform)pool.obtain()).set(transform));
         }
      }

   }

   public static final class Transform implements Pool.Poolable {
      public final Vector3 translation = new Vector3();
      public final Quaternion rotation = new Quaternion();
      public final Vector3 scale = new Vector3(1.0F, 1.0F, 1.0F);

      public BaseAnimationController.Transform idt() {
         this.translation.set(0.0F, 0.0F, 0.0F);
         this.rotation.idt();
         this.scale.set(1.0F, 1.0F, 1.0F);
         return this;
      }

      public BaseAnimationController.Transform set(Vector3 t, Quaternion r, Vector3 s) {
         this.translation.set(t);
         this.rotation.set(r);
         this.scale.set(s);
         return this;
      }

      public BaseAnimationController.Transform set(BaseAnimationController.Transform other) {
         return this.set(other.translation, other.rotation, other.scale);
      }

      public BaseAnimationController.Transform lerp(BaseAnimationController.Transform target, float alpha) {
         return this.lerp(target.translation, target.rotation, target.scale, alpha);
      }

      public BaseAnimationController.Transform lerp(Vector3 targetT, Quaternion targetR, Vector3 targetS, float alpha) {
         this.translation.lerp(targetT, alpha);
         this.rotation.slerp(targetR, alpha);
         this.scale.lerp(targetS, alpha);
         return this;
      }

      public Matrix4 toMatrix4(Matrix4 out) {
         out.idt();
         out.translate(this.translation);
         out.rotate(this.rotation);
         out.scale(this.scale.x, this.scale.y, this.scale.z);
         return out;
      }

      public void reset() {
         this.idt();
      }
   }
}
