package com.badlogic.gdx.math;

import java.io.Serializable;

public class Ellipse implements Serializable {
   public float x;
   public float y;
   public float width;
   public float height;
   private static final long serialVersionUID = 7381533206532032099L;

   public Ellipse() {
   }

   public Ellipse(float x, float y, float width, float height) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
   }

   public Ellipse(Vector2 position, float width, float height) {
      this.x = position.x;
      this.y = position.y;
      this.width = width;
      this.height = height;
   }

   public boolean contains(float x, float y) {
      x -= this.x;
      y -= this.y;
      return x * x / (this.width * 0.5F * this.width * 0.5F) + y * y / (this.height * 0.5F * this.height * 0.5F) <= 1.0F;
   }

   public boolean contains(Vector2 point) {
      return this.contains(point.x, point.y);
   }

   public void set(float x, float y, float width, float height) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
   }

   public void set(Ellipse ellipse) {
      this.x = ellipse.x;
      this.y = ellipse.y;
      this.width = ellipse.width;
      this.height = ellipse.height;
   }
}
