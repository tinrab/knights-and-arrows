package com.minild44.www.pathfinding;

import java.io.Serializable;

public class Step implements Serializable {
   public float x;
   public float y;

   public Step(float x, float y) {
      this.x = x;
      this.y = y;
   }

   public float getX() {
      return this.x;
   }

   public float getY() {
      return this.y;
   }

   public int hashCode() {
      return (int)(this.x * this.y);
   }

   public boolean equals(Object other) {
      if (other instanceof Step) {
         Step o = (Step)other;
         return o.x == this.x && o.y == this.y;
      } else {
         return false;
      }
   }
}
