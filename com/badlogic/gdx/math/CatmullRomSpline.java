package com.badlogic.gdx.math;

public class CatmullRomSpline<T extends Vector<T>> implements Path<T> {
   public T[] controlPoints;
   public boolean continuous;
   public int spanCount;
   private T tmp;
   private T tmp2;

   public static <T extends Vector<T>> T calculate(T out, float t, T[] points, boolean continuous, T tmp) {
      int n = continuous ? points.length : points.length - 3;
      float u = t * (float)n;
      int i = t >= 1.0F ? n - 1 : (int)u;
      u -= (float)i;
      return calculate(out, i, u, points, continuous, tmp);
   }

   public static <T extends Vector<T>> T calculate(T out, int i, float u, T[] points, boolean continuous, T tmp) {
      int n = points.length;
      float u2 = u * u;
      float u3 = u2 * u;
      out.set(points[i]).scl(1.5F * u3 - 2.5F * u2 + 1.0F);
      if (continuous || i > 0) {
         out.add(tmp.set(points[(n + i - 1) % n]).scl(-0.5F * u3 + u2 - 0.5F * u));
      }

      if (continuous || i < n - 1) {
         out.add(tmp.set(points[(i + 1) % n]).scl(-1.5F * u3 + 2.0F * u2 + 0.5F * u));
      }

      if (continuous || i < n - 2) {
         out.add(tmp.set(points[(i + 2) % n]).scl(0.5F * u3 - 0.5F * u2));
      }

      return out;
   }

   public CatmullRomSpline() {
   }

   public CatmullRomSpline(T[] controlPoints, boolean continuous) {
      this.set(controlPoints, continuous);
   }

   public CatmullRomSpline set(T[] controlPoints, boolean continuous) {
      if (this.tmp == null) {
         this.tmp = controlPoints[0].cpy();
      }

      if (this.tmp2 == null) {
         this.tmp2 = controlPoints[0].cpy();
      }

      this.controlPoints = controlPoints;
      this.continuous = continuous;
      this.spanCount = continuous ? controlPoints.length : controlPoints.length - 3;
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
      return calculate(out, this.continuous ? span : span + 1, u, this.controlPoints, this.continuous, this.tmp);
   }

   public int nearest(T in) {
      return this.nearest(in, 0, this.spanCount);
   }

   public int nearest(T in, int start, int count) {
      while(start < 0) {
         start += this.spanCount;
      }

      int result = start % this.spanCount;
      float dst = in.dst2(this.controlPoints[result]);

      for(int i = 1; i < count; ++i) {
         int idx = (start + i) % this.spanCount;
         float d = in.dst2(this.controlPoints[idx]);
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
      T nearest = this.controlPoints[near];
      T previous = this.controlPoints[near > 0 ? near - 1 : this.spanCount - 1];
      T next = this.controlPoints[(near + 1) % this.spanCount];
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
