package com.badlogic.gdx.maps.objects;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Circle;

public class CircleMapObject extends MapObject {
   private Circle circle;

   public Circle getCircle() {
      return this.circle;
   }

   public CircleMapObject() {
      this(0.0F, 0.0F, 1.0F);
   }

   public CircleMapObject(float x, float y, float radius) {
      this.circle = new Circle(x, y, radius);
   }
}
