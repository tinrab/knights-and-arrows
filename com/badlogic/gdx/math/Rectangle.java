package com.badlogic.gdx.math;

import java.io.Serializable;

public class Rectangle implements Serializable {
   public static final Rectangle tmp = new Rectangle();
   public static final Rectangle tmp2 = new Rectangle();
   private static final long serialVersionUID = 5733252015138115702L;
   public float x;
   public float y;
   public float width;
   public float height;

   public Rectangle() {
   }

   public Rectangle(float x, float y, float width, float height) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
   }

   public Rectangle(Rectangle rect) {
      this.x = rect.x;
      this.y = rect.y;
      this.width = rect.width;
      this.height = rect.height;
   }

   public void set(float x, float y, float width, float height) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
   }

   public float getX() {
      return this.x;
   }

   public void setX(float x) {
      this.x = x;
   }

   public float getY() {
      return this.y;
   }

   public void setY(float y) {
      this.y = y;
   }

   public float getWidth() {
      return this.width;
   }

   public void setWidth(float width) {
      this.width = width;
   }

   public float getHeight() {
      return this.height;
   }

   public void setHeight(float height) {
      this.height = height;
   }

   public void setPosition(Vector2 position) {
      this.x = position.x;
      this.y = position.y;
   }

   public void setPosition(float x, float y) {
      this.x = x;
      this.y = y;
   }

   public void setSize(float width, float height) {
      this.width = width;
      this.height = height;
   }

   public void setSize(float sizeXY) {
      this.width = sizeXY;
      this.height = sizeXY;
   }

   public boolean contains(float x, float y) {
      return this.x <= x && this.x + this.width >= x && this.y <= y && this.y + this.height >= y;
   }

   public boolean contains(Vector2 vector) {
      return this.contains(vector.x, vector.y);
   }

   public boolean contains(Rectangle rectangle) {
      float xmin = rectangle.x;
      float xmax = xmin + rectangle.width;
      float ymin = rectangle.y;
      float ymax = ymin + rectangle.height;
      return xmin > this.x && xmin < this.x + this.width && xmax > this.x && xmax < this.x + this.width && ymin > this.y && ymin < this.y + this.height && ymax > this.y && ymax < this.y + this.height;
   }

   public boolean overlaps(Rectangle r) {
      return this.x < r.x + r.width && this.x + this.width > r.x && this.y < r.y + r.height && this.y + this.height > r.y;
   }

   public void set(Rectangle rect) {
      this.x = rect.x;
      this.y = rect.y;
      this.width = rect.width;
      this.height = rect.height;
   }

   public void merge(Rectangle rect) {
      float minX = Math.min(this.x, rect.x);
      float maxX = Math.max(this.x + this.width, rect.x + rect.width);
      this.x = minX;
      this.width = maxX - minX;
      float minY = Math.min(this.y, rect.y);
      float maxY = Math.max(this.y + this.height, rect.y + rect.height);
      this.y = minY;
      this.height = maxY - minY;
   }

   public String toString() {
      return this.x + "," + this.y + "," + this.width + "," + this.height;
   }

   public void translate(float dx, float dy) {
      this.x += dx;
      this.y += dy;
   }

   public Vector2 getCenter() {
      return new Vector2(this.x + this.width / 2.0F, this.y + this.height / 2.0F);
   }

   public void fixNegativeSize() {
      if (this.width < 0.0F) {
         this.x += this.width;
         this.width = -this.width;
      }

      if (this.height < 0.0F) {
         this.y += this.height;
         this.height = -this.height;
      }

   }
}
