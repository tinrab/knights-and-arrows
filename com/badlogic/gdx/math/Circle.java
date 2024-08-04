package com.badlogic.gdx.math;

import java.io.Serializable;

public class Circle implements Serializable {
   public float x;
   public float y;
   public float radius;

   public Circle() {
   }

   public Circle(float x, float y, float radius) {
      this.x = x;
      this.y = y;
      this.radius = radius;
   }

   public Circle(Vector2 position, float radius) {
      this.x = position.x;
      this.y = position.y;
      this.radius = radius;
   }

   public void set(float x, float y, float radius) {
      this.x = x;
      this.y = y;
      this.radius = radius;
   }

   public void set(Circle circle) {
      this.x = circle.x;
      this.y = circle.y;
      this.radius = circle.radius;
   }

   public void setPosition(Vector2 position) {
      this.x = position.x;
      this.y = position.y;
   }

   public void setPosition(float x, float y) {
      this.x = x;
      this.y = y;
   }

   public void setRadius(float radius) {
      this.radius = radius;
   }

   public boolean contains(float x, float y) {
      x = this.x - x;
      y = this.y - y;
      return x * x + y * y <= this.radius * this.radius;
   }

   public boolean contains(Vector2 point) {
      float dx = this.x - point.x;
      float dy = this.y - point.y;
      return dx * dx + dy * dy <= this.radius * this.radius;
   }

   public boolean contains(Circle c) {
      float dx = this.x - c.x;
      float dy = this.y - c.y;
      float maxDistanceSqrd = dx * dx + dy * dy + c.radius * c.radius;
      return maxDistanceSqrd <= this.radius * this.radius;
   }

   public boolean overlaps(Circle c) {
      float dx = this.x - c.x;
      float dy = this.y - c.y;
      float distance = dx * dx + dy * dy;
      float radiusSum = this.radius + c.radius;
      return distance < radiusSum * radiusSum;
   }

   public String toString() {
      return this.x + "," + this.y + "," + this.radius;
   }
}
