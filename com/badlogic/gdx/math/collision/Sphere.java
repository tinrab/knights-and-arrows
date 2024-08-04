package com.badlogic.gdx.math.collision;

import com.badlogic.gdx.math.Vector3;
import java.io.Serializable;

public class Sphere implements Serializable {
   private static final long serialVersionUID = -6487336868908521596L;
   public float radius;
   public final Vector3 center;

   public Sphere(Vector3 center, float radius) {
      this.center = new Vector3(center);
      this.radius = radius;
   }

   public boolean overlaps(Sphere sphere) {
      return this.center.dst2(sphere.center) < (this.radius + sphere.radius) * (this.radius + sphere.radius);
   }
}
