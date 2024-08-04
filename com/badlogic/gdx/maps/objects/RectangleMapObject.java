package com.badlogic.gdx.maps.objects;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Rectangle;

public class RectangleMapObject extends MapObject {
   private Rectangle rectangle;

   public Rectangle getRectangle() {
      return this.rectangle;
   }

   public RectangleMapObject() {
      this(0.0F, 0.0F, 1.0F, 1.0F);
   }

   public RectangleMapObject(float x, float y, float width, float height) {
      this.rectangle = new Rectangle(x, y, width, height);
   }
}
