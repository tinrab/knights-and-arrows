package com.badlogic.gdx.math.collision;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public class BoundingBox implements Serializable {
   private static final long serialVersionUID = -1286036817192127343L;
   final Vector3[] crn = new Vector3[8];
   public final Vector3 min = new Vector3();
   public final Vector3 max = new Vector3();
   final Vector3 cnt = new Vector3();
   final Vector3 dim = new Vector3();
   boolean crn_dirty = true;

   public Vector3 getCenter() {
      return this.cnt;
   }

   protected void updateCorners() {
      if (this.crn_dirty) {
         this.crn[0].set(this.min.x, this.min.y, this.min.z);
         this.crn[1].set(this.max.x, this.min.y, this.min.z);
         this.crn[2].set(this.max.x, this.max.y, this.min.z);
         this.crn[3].set(this.min.x, this.max.y, this.min.z);
         this.crn[4].set(this.min.x, this.min.y, this.max.z);
         this.crn[5].set(this.max.x, this.min.y, this.max.z);
         this.crn[6].set(this.max.x, this.max.y, this.max.z);
         this.crn[7].set(this.min.x, this.max.y, this.max.z);
         this.crn_dirty = false;
      }
   }

   public Vector3[] getCorners() {
      this.updateCorners();
      return this.crn;
   }

   public Vector3 getDimensions() {
      return this.dim;
   }

   public Vector3 getMin() {
      return this.min;
   }

   public synchronized Vector3 getMax() {
      return this.max;
   }

   public BoundingBox() {
      this.crn_dirty = true;

      for(int l_idx = 0; l_idx < 8; ++l_idx) {
         this.crn[l_idx] = new Vector3();
      }

      this.clr();
   }

   public BoundingBox(BoundingBox bounds) {
      this.crn_dirty = true;

      for(int l_idx = 0; l_idx < 8; ++l_idx) {
         this.crn[l_idx] = new Vector3();
      }

      this.set(bounds);
   }

   public BoundingBox(Vector3 minimum, Vector3 maximum) {
      this.crn_dirty = true;

      for(int l_idx = 0; l_idx < 8; ++l_idx) {
         this.crn[l_idx] = new Vector3();
      }

      this.set(minimum, maximum);
   }

   public BoundingBox set(BoundingBox bounds) {
      this.crn_dirty = true;
      return this.set(bounds.min, bounds.max);
   }

   public BoundingBox set(Vector3 minimum, Vector3 maximum) {
      this.min.set(minimum.x < maximum.x ? minimum.x : maximum.x, minimum.y < maximum.y ? minimum.y : maximum.y, minimum.z < maximum.z ? minimum.z : maximum.z);
      this.max.set(minimum.x > maximum.x ? minimum.x : maximum.x, minimum.y > maximum.y ? minimum.y : maximum.y, minimum.z > maximum.z ? minimum.z : maximum.z);
      this.cnt.set(this.min).add(this.max).scl(0.5F);
      this.dim.set(this.max).sub(this.min);
      this.crn_dirty = true;
      return this;
   }

   public BoundingBox set(Vector3[] points) {
      this.inf();
      Vector3[] var5 = points;
      int var4 = points.length;

      for(int var3 = 0; var3 < var4; ++var3) {
         Vector3 l_point = var5[var3];
         this.ext(l_point);
      }

      this.crn_dirty = true;
      return this;
   }

   public BoundingBox set(List<Vector3> points) {
      this.inf();
      Iterator var3 = points.iterator();

      while(var3.hasNext()) {
         Vector3 l_point = (Vector3)var3.next();
         this.ext(l_point);
      }

      this.crn_dirty = true;
      return this;
   }

   public BoundingBox inf() {
      this.min.set(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
      this.max.set(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
      this.cnt.set(0.0F, 0.0F, 0.0F);
      this.dim.set(0.0F, 0.0F, 0.0F);
      this.crn_dirty = true;
      return this;
   }

   public BoundingBox ext(Vector3 point) {
      this.crn_dirty = true;
      return this.set(this.min.set(min(this.min.x, point.x), min(this.min.y, point.y), min(this.min.z, point.z)), this.max.set(Math.max(this.max.x, point.x), Math.max(this.max.y, point.y), Math.max(this.max.z, point.z)));
   }

   public BoundingBox clr() {
      this.crn_dirty = true;
      return this.set(this.min.set(0.0F, 0.0F, 0.0F), this.max.set(0.0F, 0.0F, 0.0F));
   }

   public boolean isValid() {
      return this.min.x < this.max.x && this.min.y < this.max.y && this.min.z < this.max.z;
   }

   public BoundingBox ext(BoundingBox a_bounds) {
      this.crn_dirty = true;
      return this.set(this.min.set(min(this.min.x, a_bounds.min.x), min(this.min.y, a_bounds.min.y), min(this.min.z, a_bounds.min.z)), this.max.set(max(this.max.x, a_bounds.max.x), max(this.max.y, a_bounds.max.y), max(this.max.z, a_bounds.max.z)));
   }

   public BoundingBox mul(Matrix4 matrix) {
      this.updateCorners();
      this.inf();
      Vector3[] var5;
      int var4 = (var5 = this.crn).length;

      for(int var3 = 0; var3 < var4; ++var3) {
         Vector3 l_pnt = var5[var3];
         l_pnt.mul(matrix);
         this.min.set(min(this.min.x, l_pnt.x), min(this.min.y, l_pnt.y), min(this.min.z, l_pnt.z));
         this.max.set(max(this.max.x, l_pnt.x), max(this.max.y, l_pnt.y), max(this.max.z, l_pnt.z));
      }

      this.crn_dirty = true;
      return this.set(this.min, this.max);
   }

   public boolean contains(BoundingBox b) {
      return !this.isValid() || this.min.x <= b.min.x && this.min.y <= b.min.y && this.min.z <= b.min.z && this.max.x >= b.max.x && this.max.y >= b.max.y && this.max.z >= b.max.z;
   }

   public boolean contains(Vector3 v) {
      return this.min.x <= v.x && this.max.x >= v.x && this.min.y <= v.y && this.max.y >= v.y && this.min.z <= v.z && this.max.z >= v.z;
   }

   public String toString() {
      return "[" + this.min + "|" + this.max + "]";
   }

   public BoundingBox ext(float x, float y, float z) {
      this.crn_dirty = true;
      return this.set(this.min.set(min(this.min.x, x), min(this.min.y, y), min(this.min.z, z)), this.max.set(max(this.max.x, x), max(this.max.y, y), max(this.max.z, z)));
   }

   static float min(float a, float b) {
      return a > b ? b : a;
   }

   static float max(float a, float b) {
      return a > b ? a : b;
   }
}
