package com.badlogic.gdx.maps.objects;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Ellipse;

public class EllipseMapObject extends MapObject {
   private Ellipse ellipse;

   public Ellipse getEllipse() {
      return this.ellipse;
   }

   public EllipseMapObject() {
      this(0.0F, 0.0F, 1.0F, 1.0F);
   }

   public EllipseMapObject(float x, float y, float width, float height) {
      this.ellipse = new Ellipse(x, y, width, height);
   }
}
