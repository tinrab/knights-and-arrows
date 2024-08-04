package com.badlogic.gdx.math;

import java.io.Serializable;

public class Quaternion implements Serializable {
   private static final long serialVersionUID = -7661875440774897168L;
   private static final float NORMALIZATION_TOLERANCE = 1.0E-5F;
   private static Quaternion tmp1 = new Quaternion(0.0F, 0.0F, 0.0F, 0.0F);
   private static Quaternion tmp2 = new Quaternion(0.0F, 0.0F, 0.0F, 0.0F);
   public float x;
   public float y;
   public float z;
   public float w;

   public Quaternion(float x, float y, float z, float w) {
      this.set(x, y, z, w);
   }

   public Quaternion() {
      this.idt();
   }

   public Quaternion(Quaternion quaternion) {
      this.set(quaternion);
   }

   public Quaternion(Vector3 axis, float angle) {
      this.set(axis, angle);
   }

   public Quaternion set(float x, float y, float z, float w) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.w = w;
      return this;
   }

   public Quaternion set(Quaternion quaternion) {
      return this.set(quaternion.x, quaternion.y, quaternion.z, quaternion.w);
   }

   public Quaternion set(Vector3 axis, float angle) {
      return this.setFromAxis(axis.x, axis.y, axis.z, angle);
   }

   public Quaternion cpy() {
      return new Quaternion(this);
   }

   public float len() {
      return (float)Math.sqrt((double)(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w));
   }

   public String toString() {
      return "[" + this.x + "|" + this.y + "|" + this.z + "|" + this.w + "]";
   }

   public Quaternion setEulerAngles(float yaw, float pitch, float roll) {
      yaw = (float)Math.toRadians((double)yaw);
      pitch = (float)Math.toRadians((double)pitch);
      roll = (float)Math.toRadians((double)roll);
      float num9 = roll * 0.5F;
      float num6 = (float)Math.sin((double)num9);
      float num5 = (float)Math.cos((double)num9);
      float num8 = pitch * 0.5F;
      float num4 = (float)Math.sin((double)num8);
      float num3 = (float)Math.cos((double)num8);
      float num7 = yaw * 0.5F;
      float num2 = (float)Math.sin((double)num7);
      float num = (float)Math.cos((double)num7);
      float f1 = num * num4;
      float f2 = num2 * num3;
      float f3 = num * num3;
      float f4 = num2 * num4;
      this.x = f1 * num5 + f2 * num6;
      this.y = f2 * num5 - f1 * num6;
      this.z = f3 * num6 - f4 * num5;
      this.w = f3 * num5 + f4 * num6;
      return this;
   }

   public float len2() {
      return this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
   }

   public Quaternion nor() {
      float len = this.len2();
      if (len != 0.0F && Math.abs(len - 1.0F) > 1.0E-5F) {
         len = (float)Math.sqrt((double)len);
         this.w /= len;
         this.x /= len;
         this.y /= len;
         this.z /= len;
      }

      return this;
   }

   public Quaternion conjugate() {
      this.x = -this.x;
      this.y = -this.y;
      this.z = -this.z;
      return this;
   }

   public Vector3 transform(Vector3 v) {
      tmp2.set(this);
      tmp2.conjugate();
      tmp2.mulLeft(tmp1.set(v.x, v.y, v.z, 0.0F)).mulLeft(this);
      v.x = tmp2.x;
      v.y = tmp2.y;
      v.z = tmp2.z;
      return v;
   }

   public Quaternion mul(Quaternion q) {
      float newX = this.w * q.x + this.x * q.w + this.y * q.z - this.z * q.y;
      float newY = this.w * q.y + this.y * q.w + this.z * q.x - this.x * q.z;
      float newZ = this.w * q.z + this.z * q.w + this.x * q.y - this.y * q.x;
      float newW = this.w * q.w - this.x * q.x - this.y * q.y - this.z * q.z;
      this.x = newX;
      this.y = newY;
      this.z = newZ;
      this.w = newW;
      return this;
   }

   public Quaternion mulLeft(Quaternion q) {
      float newX = q.w * this.x + q.x * this.w + q.y * this.z - q.z * this.y;
      float newY = q.w * this.y + q.y * this.w + q.z * this.x - q.x * this.z;
      float newZ = q.w * this.z + q.z * this.w + q.x * this.y - q.y * this.x;
      float newW = q.w * this.w - q.x * this.x - q.y * this.y - q.z * this.z;
      this.x = newX;
      this.y = newY;
      this.z = newZ;
      this.w = newW;
      return this;
   }

   public void toMatrix(float[] matrix) {
      float xx = this.x * this.x;
      float xy = this.x * this.y;
      float xz = this.x * this.z;
      float xw = this.x * this.w;
      float yy = this.y * this.y;
      float yz = this.y * this.z;
      float yw = this.y * this.w;
      float zz = this.z * this.z;
      float zw = this.z * this.w;
      matrix[0] = 1.0F - 2.0F * (yy + zz);
      matrix[4] = 2.0F * (xy - zw);
      matrix[8] = 2.0F * (xz + yw);
      matrix[12] = 0.0F;
      matrix[1] = 2.0F * (xy + zw);
      matrix[5] = 1.0F - 2.0F * (xx + zz);
      matrix[9] = 2.0F * (yz - xw);
      matrix[13] = 0.0F;
      matrix[2] = 2.0F * (xz - yw);
      matrix[6] = 2.0F * (yz + xw);
      matrix[10] = 1.0F - 2.0F * (xx + yy);
      matrix[14] = 0.0F;
      matrix[3] = 0.0F;
      matrix[7] = 0.0F;
      matrix[11] = 0.0F;
      matrix[15] = 1.0F;
   }

   public Quaternion idt() {
      return this.set(0.0F, 0.0F, 0.0F, 1.0F);
   }

   public Quaternion setFromAxis(Vector3 axis, float angle) {
      return this.setFromAxis(axis.x, axis.y, axis.z, angle);
   }

   public Quaternion setFromAxis(float x, float y, float z, float angle) {
      float d = Vector3.len(x, y, z);
      if (d == 0.0F) {
         return this.idt();
      } else {
         d = 1.0F / d;
         float l_ang = angle * 0.017453292F;
         float l_sin = (float)Math.sin((double)(l_ang / 2.0F));
         float l_cos = (float)Math.cos((double)(l_ang / 2.0F));
         return this.set(d * x * l_sin, d * y * l_sin, d * z * l_sin, l_cos).nor();
      }
   }

   public Quaternion setFromMatrix(Matrix4 matrix) {
      return this.setFromAxes(matrix.val[0], matrix.val[4], matrix.val[8], matrix.val[1], matrix.val[5], matrix.val[9], matrix.val[2], matrix.val[6], matrix.val[10]);
   }

   public Quaternion setFromAxes(float xx, float xy, float xz, float yx, float yy, float yz, float zx, float zy, float zz) {
      float t = xx + yy + zz;
      double x;
      double y;
      double z;
      double w;
      double s;
      if (t >= 0.0F) {
         s = Math.sqrt((double)(t + 1.0F));
         w = 0.5D * s;
         s = 0.5D / s;
         x = (double)(zy - yz) * s;
         y = (double)(xz - zx) * s;
         z = (double)(yx - xy) * s;
      } else if (xx > yy && xx > zz) {
         s = Math.sqrt(1.0D + (double)xx - (double)yy - (double)zz);
         x = s * 0.5D;
         s = 0.5D / s;
         y = (double)(yx + xy) * s;
         z = (double)(xz + zx) * s;
         w = (double)(zy - yz) * s;
      } else if (yy > zz) {
         s = Math.sqrt(1.0D + (double)yy - (double)xx - (double)zz);
         y = s * 0.5D;
         s = 0.5D / s;
         x = (double)(yx + xy) * s;
         z = (double)(zy + yz) * s;
         w = (double)(xz - zx) * s;
      } else {
         s = Math.sqrt(1.0D + (double)zz - (double)xx - (double)yy);
         z = s * 0.5D;
         s = 0.5D / s;
         x = (double)(xz + zx) * s;
         y = (double)(zy + yz) * s;
         w = (double)(yx - xy) * s;
      }

      return this.set((float)x, (float)y, (float)z, (float)w);
   }

   public Quaternion setFromCross(Vector3 v1, Vector3 v2) {
      float dot = MathUtils.clamp(v1.dot(v2), -1.0F, 1.0F);
      float angle = (float)Math.acos((double)dot) * 57.295776F;
      return this.setFromAxis(v1.y * v2.z - v1.z * v2.y, v1.z * v2.x - v1.x * v2.z, v1.x * v2.y - v1.y * v2.x, angle);
   }

   public Quaternion setFromCross(float x1, float y1, float z1, float x2, float y2, float z2) {
      float dot = MathUtils.clamp(Vector3.dot(x1, y1, z1, x2, y2, z2), -1.0F, 1.0F);
      float angle = (float)Math.acos((double)dot) * 57.295776F;
      return this.setFromAxis(y1 * z2 - z1 * y2, z1 * x2 - x1 * z2, x1 * y2 - y1 * x2, angle);
   }

   public Quaternion slerp(Quaternion end, float alpha) {
      if (this.equals(end)) {
         return this;
      } else {
         float result = this.dot(end);
         if ((double)result < 0.0D) {
            end.mul(-1.0F);
            result = -result;
         }

         float scale0 = 1.0F - alpha;
         float scale1 = alpha;
         if ((double)(1.0F - result) > 0.1D) {
            double theta = Math.acos((double)result);
            double invSinTheta = 1.0D / Math.sin(theta);
            scale0 = (float)(Math.sin((double)(1.0F - alpha) * theta) * invSinTheta);
            scale1 = (float)(Math.sin((double)alpha * theta) * invSinTheta);
         }

         float x = scale0 * this.x + scale1 * end.x;
         float y = scale0 * this.y + scale1 * end.y;
         float z = scale0 * this.z + scale1 * end.z;
         float w = scale0 * this.w + scale1 * end.w;
         this.set(x, y, z, w);
         return this;
      }
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof Quaternion)) {
         return false;
      } else {
         Quaternion comp = (Quaternion)o;
         return this.x == comp.x && this.y == comp.y && this.z == comp.z && this.w == comp.w;
      }
   }

   public float dot(Quaternion other) {
      return this.x * other.x + this.y * other.y + this.z * other.z + this.w * other.w;
   }

   public Quaternion mul(float scalar) {
      this.x *= scalar;
      this.y *= scalar;
      this.z *= scalar;
      this.w *= scalar;
      return this;
   }
}
