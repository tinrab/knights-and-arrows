package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.materials.BlendingAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import java.util.Comparator;

public class DefaultRenderableSorter implements RenderableSorter, Comparator<Renderable> {
   private Camera camera;
   private final Vector3 tmpV1 = new Vector3();
   private final Vector3 tmpV2 = new Vector3();

   public void sort(Camera camera, Array<Renderable> renderables) {
      this.camera = camera;
      renderables.sort(this);
   }

   public int compare(Renderable o1, Renderable o2) {
      boolean b1 = o1.material.has(BlendingAttribute.Type);
      boolean b2 = o2.material.has(BlendingAttribute.Type);
      if (b1 != b2) {
         return b1 ? 1 : -1;
      } else {
         o1.worldTransform.getTranslation(this.tmpV1);
         o2.worldTransform.getTranslation(this.tmpV2);
         float dst = this.camera.position.dst2(this.tmpV1) - this.camera.position.dst2(this.tmpV2);
         return dst < 0.0F ? -1 : (dst > 0.0F ? 1 : 0);
      }
   }
}
