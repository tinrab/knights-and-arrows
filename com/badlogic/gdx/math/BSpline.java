package com.badlogic.gdx.math;

import com.badlogic.gdx.utils.Array;

public class BSpline<T extends Vector<T>> implements Path<T> {
   private static final float d6 = 0.16666667F;
   public T[] controlPoints;
   public Array<T> knots;
   public int degree;
   public boolean continuous;
   public int spanCount;
   private T tmp;

   public static <T extends Vector<T>> T cubic(T out, float t, T[] points, boolean continuous, T tmp) {
      int n = continuous ? points.length : points.length - 3;
      float u = t * (float)n;
      int i = t >= 1.0F ? n - 1 : (int)u;
      u -= (float)i;
      return cubic(out, i, u, points, continuous, tmp);
   }

   public static <T extends Vector<T>> T cubic(T out, int i, float u, T[] points, boolean continuous, T tmp) {
      int n = points.length;
      float dt = 1.0F - u;
      float t2 = u * u;
      float t3 = t2 * u;
      out.set(points[i]).scl((3.0F * t3 - 6.0F * t2 + 4.0F) * 0.16666667F);
      if (continuous || i > 0) {
         out.add(tmp.set(points[(n + i - 1) % n]).scl(dt * dt * dt * 0.16666667F));
      }

      if (continuous || i < n - 1) {
         out.add(tmp.set(points[(i + 1) % n]).scl((-3.0F * t3 + 3.0F * t2 + 3.0F * u + 1.0F) * 0.16666667F));
      }

      if (continuous || i < n - 2) {
         out.add(tmp.set(points[(i + 2) % n]).scl(t3 * 0.16666667F));
      }

      return out;
   }

   public static <T extends Vector<T>> T calculate(T out, float t, T[] points, int degree, boolean continuous, T tmp) {
      int n = continuous ? points.length : points.length - degree;
      float u = t * (float)n;
      int i = t >= 1.0F ? n - 1 : (int)u;
      u -= (float)i;
      return calculate(out, i, u, points, degree, continuous, tmp);
   }

   public static <T extends Vector<T>> T calculate(T out, int i, float u, T[] points, int degree, boolean continuous, T tmp) {
      switch(degree) {
      case 3:
         return cubic(out, i, u, points, continuous, tmp);
      default:
         return out;
      }
   }

   public BSpline() {
   }

   public BSpline(T[] controlPoints, int degree, boolean continuous) {
      this.set(controlPoints, degree, continuous);
   }

   public BSpline set(T[] controlPoints, int degree, boolean continuous) {
      if (this.tmp == null) {
         this.tmp = controlPoints[0].cpy();
      }

      this.controlPoints = controlPoints;
      this.degree = degree;
      this.continuous = continuous;
      this.spanCount = continuous ? controlPoints.length : controlPoints.length - degree;
      if (this.knots == null) {
         this.knots = new Array(this.spanCount);
      } else {
         this.knots.clear();
         this.knots.ensureCapacity(this.spanCount);
      }

      for(int i = 0; i < this.spanCount; ++i) {
         this.knots.add(calculate(controlPoints[0].cpy(), continuous ? i : (int)((float)i + 0.5F * (float)degree), 0.0F, controlPoints, degree, continuous, this.tmp));
      }

      return this;
   }

   public T valueAt(T out, float t) {
      int n = this.spanCount;
      float u = t * (float)n;
      int i = t >= 1.0F ? n - 1 : (int)u;
      u -= (float)i;
      return this.valueAt(out, i, u);
   }

   public T valueAt(T out, int span, float u) {
      return calculate(out, this.continuous ? span : span + (int)((float)this.degree * 0.5F), u, this.controlPoints, this.degree, this.continuous, this.tmp);
   }

   public int nearest(T in) {
      return this.nearest(in, 0, this.spanCount);
   }

   public int nearest(T in, int start, int count) {
      while(start < 0) {
         start += this.spanCount;
      }

      int result = start % this.spanCount;
      float dst = in.dst2((Vector)this.knots.get(result));

      for(int i = 1; i < count; ++i) {
         int idx = (start + i) % this.spanCount;
         float d = in.dst2((Vector)this.knots.get(idx));
         if (d < dst) {
            dst = d;
            result = idx;
         }
      }

      return result;
   }

   public float approximate(T v) {
      return this.approximate(v, this.nearest(v));
   }

   public float approximate(T in, int start, int count) {
      return this.approximate(in, this.nearest(in, start, count));
   }

   public float approximate(T in, int near) {
      int n = near;
      T nearest = (Vector)this.knots.get(near);
      T previous = (Vector)this.knots.get(near > 0 ? near - 1 : this.spanCount - 1);
      T next = (Vector)this.knots.get((near + 1) % this.spanCount);
      float dstPrev2 = in.dst2(previous);
      float dstNext2 = in.dst2(next);
      Vector P1;
      Vector P2;
      Vector P3;
      if (dstNext2 < dstPrev2) {
         P1 = nearest;
         P2 = next;
         P3 = in;
      } else {
         P1 = previous;
         P2 = nearest;
         P3 = in;
         n = near > 0 ? near - 1 : this.spanCount - 1;
      }

      float L1 = P1.dst(P2);
      float L2 = P3.dst(P2);
      float L3 = P3.dst(P1);
      float s = (L2 * L2 + L1 * L1 - L3 * L3) / (2.0F * L1);
      float u = MathUtils.clamp((L1 - s) / L1, 0.0F, 1.0F);
      return ((float)n + u) / (float)this.spanCount;
   }

   public float locate(T v) {
      return this.approximate(v);
   }
}
