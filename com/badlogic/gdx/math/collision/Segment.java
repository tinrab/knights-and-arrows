package com.badlogic.gdx.math.collision;

import com.badlogic.gdx.math.Vector3;
import java.io.Serializable;

public class Segment implements Serializable {
   private static final long serialVersionUID = 2739667069736519602L;
   public final Vector3 a = new Vector3();
   public final Vector3 b = new Vector3();

   public Segment(Vector3 a, Vector3 b) {
      this.a.set(a);
      this.b.set(b);
   }

   public Segment(float aX, float aY, float aZ, float bX, float bY, float bZ) {
      this.a.set(aX, aY, aZ);
      this.b.set(bX, bY, bZ);
   }
}
