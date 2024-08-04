package com.badlogic.gdx.math;

import com.badlogic.gdx.utils.NumberUtils;
import java.io.Serializable;

public class Vector3 implements Serializable, Vector<Vector3> {
   private static final long serialVersionUID = 3840054589595372522L;
   public float x;
   public float y;
   public float z;
   /** @deprecated */
   public static final Vector3 tmp = new Vector3();
   /** @deprecated */
   public static final Vector3 tmp2 = new Vector3();
   /** @deprecated */
   public static final Vector3 tmp3 = new Vector3();
   public static final Vector3 X = new Vector3(1.0F, 0.0F, 0.0F);
   public static final Vector3 Y = new Vector3(0.0F, 1.0F, 0.0F);
   public static final Vector3 Z = new Vector3(0.0F, 0.0F, 1.0F);
   public static final Vector3 Zero = new Vector3(0.0F, 0.0F, 0.0F);
   private static final Matrix4 tmpMat = new Matrix4();

   public Vector3() {
   }

   public Vector3(float x, float y, float z) {
      this.set(x, y, z);
   }

   public Vector3(Vector3 vector) {
      this.set(vector);
   }

   public Vector3(float[] values) {
      this.set(values[0], values[1], values[2]);
   }

   public Vector3 set(float x, float y, float z) {
      this.x = x;
      this.y = y;
      this.z = z;
      return this;
   }

   public Vector3 set(Vector3 vector) {
      return this.set(vector.x, vector.y, vector.z);
   }

   public Vector3 set(float[] values) {
      return this.set(values[0], values[1], values[2]);
   }

   public Vector3 cpy() {
      return new Vector3(this);
   }

   /** @deprecated */
   public Vector3 tmp() {
      return tmp.set(this);
   }

   /** @deprecated */
   public Vector3 tmp2() {
      return tmp2.set(this);
   }

   /** @deprecated */
   Vector3 tmp3() {
      return tmp3.set(this);
   }

   public Vector3 add(Vector3 vector) {
      return this.add(vector.x, vector.y, vector.z);
   }

   public Vector3 add(float x, float y, float z) {
      return this.set(this.x + x, this.y + y, this.z + z);
   }

   public Vector3 add(float values) {
      return this.set(this.x + values, this.y + values, this.z + values);
   }

   public Vector3 sub(Vector3 a_vec) {
      return this.sub(a_vec.x, a_vec.y, a_vec.z);
   }

   public Vector3 sub(float x, float y, float z) {
      return this.set(this.x - x, this.y - y, this.z - z);
   }

   public Vector3 sub(float value) {
      return this.set(this.x - value, this.y - value, this.z - value);
   }

   public Vector3 scl(float value) {
      return this.set(this.x * value, this.y * value, this.z * value);
   }

   /** @deprecated */
   public Vector3 mul(float value) {
      return this.scl(value);
   }

   public Vector3 scl(Vector3 other) {
      return this.set(this.x * other.x, this.y * other.y, this.z * other.z);
   }

   /** @deprecated */
   public Vector3 mul(Vector3 other) {
      return this.scl(other);
   }

   public Vector3 scl(float vx, float vy, float vz) {
      return this.set(this.x * vx, this.y * vy, this.z * vz);
   }

   /** @deprecated */
   public Vector3 mul(float vx, float vy, float vz) {
      return this.scl(vx, vy, vz);
   }

   /** @deprecated */
   public Vector3 scale(float scalarX, float scalarY, float scalarZ) {
      return this.scl(scalarX, scalarY, scalarZ);
   }

   /** @deprecated */
   public Vector3 div(float value) {
      return this.scl(1.0F / value);
   }

   /** @deprecated */
   public Vector3 div(float vx, float vy, float vz) {
      return this.set(this.x / vx, this.y / vy, this.z / vz);
   }

   /** @deprecated */
   public Vector3 div(Vector3 other) {
      return this.set(this.x / other.x, this.y / other.y, this.z / other.z);
   }

   public static float len(float x, float y, float z) {
      return (float)Math.sqrt((double)(x * x + y * y + z * z));
   }

   public float len() {
      return (float)Math.sqrt((double)(this.x * this.x + this.y * this.y + this.z * this.z));
   }

   public static float len2(float x, float y, float z) {
      return x * x + y * y + z * z;
   }

   public float len2() {
      return this.x * this.x + this.y * this.y + this.z * this.z;
   }

   public boolean idt(Vector3 vector) {
      return this.x == vector.x && this.y == vector.y && this.z == vector.z;
   }

   public static float dst(float x1, float y1, float z1, float x2, float y2, float z2) {
      float a = x2 - x1;
      float b = y2 - y1;
      float c = z2 - z1;
      return (float)Math.sqrt((double)(a * a + b * b + c * c));
   }

   public float dst(Vector3 vector) {
      float a = vector.x - this.x;
      float b = vector.y - this.y;
      float c = vector.z - this.z;
      return (float)Math.sqrt((double)(a * a + b * b + c * c));
   }

   public float dst(float x, float y, float z) {
      float a = x - this.x;
      float b = y - this.y;
      float c = z - this.z;
      return (float)Math.sqrt((double)(a * a + b * b + c * c));
   }

   public static float dst2(float x1, float y1, float z1, float x2, float y2, float z2) {
      float a = x2 - x1;
      float b = y2 - y1;
      float c = z2 - z1;
      return a * a + b * b + c * c;
   }

   public float dst2(Vector3 point) {
      float a = point.x - this.x;
      float b = point.y - this.y;
      float c = point.z - this.z;
      return a * a + b * b + c * c;
   }

   public float dst2(float x, float y, float z) {
      float a = x - this.x;
      float b = y - this.y;
      float c = z - this.z;
      return a * a + b * b + c * c;
   }

   public Vector3 nor() {
      float len2 = this.len2();
      return len2 != 0.0F && len2 != 1.0F ? this.scl(1.0F / (float)Math.sqrt((double)len2)) : this;
   }

   public static float dot(float x1, float y1, float z1, float x2, float y2, float z2) {
      return x1 * x2 + y1 * y2 + z1 * z2;
   }

   public float dot(Vector3 vector) {
      return this.x * vector.x + this.y * vector.y + this.z * vector.z;
   }

   public float dot(float x, float y, float z) {
      return this.x * x + this.y * y + this.z * z;
   }

   public Vector3 crs(Vector3 vector) {
      return this.set(this.y * vector.z - this.z * vector.y, this.z * vector.x - this.x * vector.z, this.x * vector.y - this.y * vector.x);
   }

   public Vector3 crs(float x, float y, float z) {
      return this.set(this.y * z - this.z * y, this.z * x - this.x * z, this.x * y - this.y * x);
   }

   public Vector3 mul(Matrix4 matrix) {
      float[] l_mat = matrix.val;
      return this.set(this.x * l_mat[0] + this.y * l_mat[4] + this.z * l_mat[8] + l_mat[12], this.x * l_mat[1] + this.y * l_mat[5] + this.z * l_mat[9] + l_mat[13], this.x * l_mat[2] + this.y * l_mat[6] + this.z * l_mat[10] + l_mat[14]);
   }

   public Vector3 mul(Quaternion quat) {
      return quat.transform(this);
   }

   public Vector3 prj(Matrix4 matrix) {
      float[] l_mat = matrix.val;
      float l_w = 1.0F / (this.x * l_mat[3] + this.y * l_mat[7] + this.z * l_mat[11] + l_mat[15]);
      return this.set((this.x * l_mat[0] + this.y * l_mat[4] + this.z * l_mat[8] + l_mat[12]) * l_w, (this.x * l_mat[1] + this.y * l_mat[5] + this.z * l_mat[9] + l_mat[13]) * l_w, (this.x * l_mat[2] + this.y * l_mat[6] + this.z * l_mat[10] + l_mat[14]) * l_w);
   }

   public Vector3 rot(Matrix4 matrix) {
      float[] l_mat = matrix.val;
      return this.set(this.x * l_mat[0] + this.y * l_mat[4] + this.z * l_mat[8], this.x * l_mat[1] + this.y * l_mat[5] + this.z * l_mat[9], this.x * l_mat[2] + this.y * l_mat[6] + this.z * l_mat[10]);
   }

   public Vector3 rotate(float angle, float axisX, float axisY, float axisZ) {
      return this.mul(tmpMat.setToRotation(axisX, axisY, axisZ, angle));
   }

   public Vector3 rotate(Vector3 axis, float angle) {
      tmpMat.setToRotation(axis, angle);
      return this.mul(tmpMat);
   }

   public boolean isUnit() {
      return this.isUnit(1.0E-9F);
   }

   public boolean isUnit(float margin) {
      return Math.abs(this.len2() - 1.0F) < margin * margin;
   }

   public boolean isZero() {
      return this.x == 0.0F && this.y == 0.0F && this.z == 0.0F;
   }

   public boolean isZero(float margin) {
      return this.len2() < margin * margin;
   }

   public Vector3 lerp(Vector3 target, float alpha) {
      this.scl(1.0F - alpha);
      this.add(target.x * alpha, target.y * alpha, target.z * alpha);
      return this;
   }

   public Vector3 slerp(Vector3 target, float alpha) {
      float dot = this.dot(target);
      if (!((double)dot > 0.9995D) && !((double)dot < -0.9995D)) {
         float theta0 = (float)Math.acos((double)dot);
         float theta = theta0 * alpha;
         float st = (float)Math.sin((double)theta);
         float tx = target.x - this.x * dot;
         float ty = target.y - this.y * dot;
         float tz = target.z - this.z * dot;
         float l2 = tx * tx + ty * ty + tz * tz;
         float dl = st * (l2 < 1.0E-4F ? 1.0F : 1.0F / (float)Math.sqrt((double)l2));
         return this.scl((float)Math.cos((double)theta)).add(tx * dl, ty * dl, tz * dl).nor();
      } else {
         return this.lerp(target, alpha);
      }
   }

   public String toString() {
      return this.x + "," + this.y + "," + this.z;
   }

   public Vector3 limit(float limit) {
      if (this.len2() > limit * limit) {
         this.nor().scl(limit);
      }

      return this;
   }

   public Vector3 clamp(float min, float max) {
      float l2 = this.len2();
      if (l2 == 0.0F) {
         return this;
      } else if (l2 > max * max) {
         return this.nor().scl(max);
      } else {
         return l2 < min * min ? this.nor().scl(min) : this;
      }
   }

   public int hashCode() {
      int prime = true;
      int result = 1;
      int result = 31 * result + NumberUtils.floatToIntBits(this.x);
      result = 31 * result + NumberUtils.floatToIntBits(this.y);
      result = 31 * result + NumberUtils.floatToIntBits(this.z);
      return result;
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         Vector3 other = (Vector3)obj;
         if (NumberUtils.floatToIntBits(this.x) != NumberUtils.floatToIntBits(other.x)) {
            return false;
         } else if (NumberUtils.floatToIntBits(this.y) != NumberUtils.floatToIntBits(other.y)) {
            return false;
         } else {
            return NumberUtils.floatToIntBits(this.z) == NumberUtils.floatToIntBits(other.z);
         }
      }
   }

   public boolean epsilonEquals(Vector3 obj, float epsilon) {
      if (obj == null) {
         return false;
      } else if (Math.abs(obj.x - this.x) > epsilon) {
         return false;
      } else if (Math.abs(obj.y - this.y) > epsilon) {
         return false;
      } else {
         return !(Math.abs(obj.z - this.z) > epsilon);
      }
   }

   public boolean epsilonEquals(float x, float y, float z, float epsilon) {
      if (Math.abs(x - this.x) > epsilon) {
         return false;
      } else if (Math.abs(y - this.y) > epsilon) {
         return false;
      } else {
         return !(Math.abs(z - this.z) > epsilon);
      }
   }

   public void toUnit() {
      this.x = (float)MathUtils.round(this.x);
      this.y = (float)MathUtils.round(this.y);
      this.z = (float)MathUtils.round(this.z);
   }
}
