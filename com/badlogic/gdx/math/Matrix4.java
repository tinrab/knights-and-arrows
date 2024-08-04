package com.badlogic.gdx.math;

import java.io.Serializable;

public class Matrix4 implements Serializable {
   private static final long serialVersionUID = -2717655254359579617L;
   public static final int M00 = 0;
   public static final int M01 = 4;
   public static final int M02 = 8;
   public static final int M03 = 12;
   public static final int M10 = 1;
   public static final int M11 = 5;
   public static final int M12 = 9;
   public static final int M13 = 13;
   public static final int M20 = 2;
   public static final int M21 = 6;
   public static final int M22 = 10;
   public static final int M23 = 14;
   public static final int M30 = 3;
   public static final int M31 = 7;
   public static final int M32 = 11;
   public static final int M33 = 15;
   public final float[] tmp = new float[16];
   public final float[] val = new float[16];
   static Quaternion quat = new Quaternion();
   static final Vector3 l_vez = new Vector3();
   static final Vector3 l_vex = new Vector3();
   static final Vector3 l_vey = new Vector3();
   static final Vector3 tmpVec = new Vector3();
   static final Matrix4 tmpMat = new Matrix4();
   static final Vector3 right = new Vector3();
   static final Vector3 tmpForward = new Vector3();
   static final Vector3 tmpUp = new Vector3();

   public Matrix4() {
      this.val[0] = 1.0F;
      this.val[5] = 1.0F;
      this.val[10] = 1.0F;
      this.val[15] = 1.0F;
   }

   public Matrix4(Matrix4 matrix) {
      this.set(matrix);
   }

   public Matrix4(float[] values) {
      this.set(values);
   }

   public Matrix4(Quaternion quaternion) {
      this.set(quaternion);
   }

   public Matrix4 set(Matrix4 matrix) {
      return this.set(matrix.val);
   }

   public Matrix4 set(float[] values) {
      System.arraycopy(values, 0, this.val, 0, this.val.length);
      return this;
   }

   public Matrix4 set(Quaternion quaternion) {
      return this.set(quaternion.x, quaternion.y, quaternion.z, quaternion.w);
   }

   public Matrix4 set(float x, float y, float z, float w) {
      float l_xx = x * x;
      float l_xy = x * y;
      float l_xz = x * z;
      float l_xw = x * w;
      float l_yy = y * y;
      float l_yz = y * z;
      float l_yw = y * w;
      float l_zz = z * z;
      float l_zw = z * w;
      this.val[0] = 1.0F - 2.0F * (l_yy + l_zz);
      this.val[4] = 2.0F * (l_xy - l_zw);
      this.val[8] = 2.0F * (l_xz + l_yw);
      this.val[12] = 0.0F;
      this.val[1] = 2.0F * (l_xy + l_zw);
      this.val[5] = 1.0F - 2.0F * (l_xx + l_zz);
      this.val[9] = 2.0F * (l_yz - l_xw);
      this.val[13] = 0.0F;
      this.val[2] = 2.0F * (l_xz - l_yw);
      this.val[6] = 2.0F * (l_yz + l_xw);
      this.val[10] = 1.0F - 2.0F * (l_xx + l_yy);
      this.val[14] = 0.0F;
      this.val[3] = 0.0F;
      this.val[7] = 0.0F;
      this.val[11] = 0.0F;
      this.val[15] = 1.0F;
      return this;
   }

   public Matrix4 set(Vector3 xAxis, Vector3 yAxis, Vector3 zAxis, Vector3 pos) {
      this.val[0] = xAxis.x;
      this.val[4] = xAxis.y;
      this.val[8] = xAxis.z;
      this.val[1] = yAxis.x;
      this.val[5] = yAxis.y;
      this.val[9] = yAxis.z;
      this.val[2] = -zAxis.x;
      this.val[6] = -zAxis.y;
      this.val[10] = -zAxis.z;
      this.val[12] = pos.x;
      this.val[13] = pos.y;
      this.val[14] = pos.z;
      this.val[3] = 0.0F;
      this.val[7] = 0.0F;
      this.val[11] = 0.0F;
      this.val[15] = 1.0F;
      return this;
   }

   public Matrix4 cpy() {
      return new Matrix4(this);
   }

   public Matrix4 trn(Vector3 vector) {
      float[] var10000 = this.val;
      var10000[12] += vector.x;
      var10000 = this.val;
      var10000[13] += vector.y;
      var10000 = this.val;
      var10000[14] += vector.z;
      return this;
   }

   public Matrix4 trn(float x, float y, float z) {
      float[] var10000 = this.val;
      var10000[12] += x;
      var10000 = this.val;
      var10000[13] += y;
      var10000 = this.val;
      var10000[14] += z;
      return this;
   }

   public float[] getValues() {
      return this.val;
   }

   public Matrix4 mul(Matrix4 matrix) {
      mul(this.val, matrix.val);
      return this;
   }

   public Matrix4 tra() {
      this.tmp[0] = this.val[0];
      this.tmp[4] = this.val[1];
      this.tmp[8] = this.val[2];
      this.tmp[12] = this.val[3];
      this.tmp[1] = this.val[4];
      this.tmp[5] = this.val[5];
      this.tmp[9] = this.val[6];
      this.tmp[13] = this.val[7];
      this.tmp[2] = this.val[8];
      this.tmp[6] = this.val[9];
      this.tmp[10] = this.val[10];
      this.tmp[14] = this.val[11];
      this.tmp[3] = this.val[12];
      this.tmp[7] = this.val[13];
      this.tmp[11] = this.val[14];
      this.tmp[15] = this.val[15];
      return this.set(this.tmp);
   }

   public Matrix4 idt() {
      this.val[0] = 1.0F;
      this.val[4] = 0.0F;
      this.val[8] = 0.0F;
      this.val[12] = 0.0F;
      this.val[1] = 0.0F;
      this.val[5] = 1.0F;
      this.val[9] = 0.0F;
      this.val[13] = 0.0F;
      this.val[2] = 0.0F;
      this.val[6] = 0.0F;
      this.val[10] = 1.0F;
      this.val[14] = 0.0F;
      this.val[3] = 0.0F;
      this.val[7] = 0.0F;
      this.val[11] = 0.0F;
      this.val[15] = 1.0F;
      return this;
   }

   public Matrix4 inv() {
      float l_det = this.val[3] * this.val[6] * this.val[9] * this.val[12] - this.val[2] * this.val[7] * this.val[9] * this.val[12] - this.val[3] * this.val[5] * this.val[10] * this.val[12] + this.val[1] * this.val[7] * this.val[10] * this.val[12] + this.val[2] * this.val[5] * this.val[11] * this.val[12] - this.val[1] * this.val[6] * this.val[11] * this.val[12] - this.val[3] * this.val[6] * this.val[8] * this.val[13] + this.val[2] * this.val[7] * this.val[8] * this.val[13] + this.val[3] * this.val[4] * this.val[10] * this.val[13] - this.val[0] * this.val[7] * this.val[10] * this.val[13] - this.val[2] * this.val[4] * this.val[11] * this.val[13] + this.val[0] * this.val[6] * this.val[11] * this.val[13] + this.val[3] * this.val[5] * this.val[8] * this.val[14] - this.val[1] * this.val[7] * this.val[8] * this.val[14] - this.val[3] * this.val[4] * this.val[9] * this.val[14] + this.val[0] * this.val[7] * this.val[9] * this.val[14] + this.val[1] * this.val[4] * this.val[11] * this.val[14] - this.val[0] * this.val[5] * this.val[11] * this.val[14] - this.val[2] * this.val[5] * this.val[8] * this.val[15] + this.val[1] * this.val[6] * this.val[8] * this.val[15] + this.val[2] * this.val[4] * this.val[9] * this.val[15] - this.val[0] * this.val[6] * this.val[9] * this.val[15] - this.val[1] * this.val[4] * this.val[10] * this.val[15] + this.val[0] * this.val[5] * this.val[10] * this.val[15];
      if (l_det == 0.0F) {
         throw new RuntimeException("non-invertible matrix");
      } else {
         float inv_det = 1.0F / l_det;
         this.tmp[0] = this.val[9] * this.val[14] * this.val[7] - this.val[13] * this.val[10] * this.val[7] + this.val[13] * this.val[6] * this.val[11] - this.val[5] * this.val[14] * this.val[11] - this.val[9] * this.val[6] * this.val[15] + this.val[5] * this.val[10] * this.val[15];
         this.tmp[4] = this.val[12] * this.val[10] * this.val[7] - this.val[8] * this.val[14] * this.val[7] - this.val[12] * this.val[6] * this.val[11] + this.val[4] * this.val[14] * this.val[11] + this.val[8] * this.val[6] * this.val[15] - this.val[4] * this.val[10] * this.val[15];
         this.tmp[8] = this.val[8] * this.val[13] * this.val[7] - this.val[12] * this.val[9] * this.val[7] + this.val[12] * this.val[5] * this.val[11] - this.val[4] * this.val[13] * this.val[11] - this.val[8] * this.val[5] * this.val[15] + this.val[4] * this.val[9] * this.val[15];
         this.tmp[12] = this.val[12] * this.val[9] * this.val[6] - this.val[8] * this.val[13] * this.val[6] - this.val[12] * this.val[5] * this.val[10] + this.val[4] * this.val[13] * this.val[10] + this.val[8] * this.val[5] * this.val[14] - this.val[4] * this.val[9] * this.val[14];
         this.tmp[1] = this.val[13] * this.val[10] * this.val[3] - this.val[9] * this.val[14] * this.val[3] - this.val[13] * this.val[2] * this.val[11] + this.val[1] * this.val[14] * this.val[11] + this.val[9] * this.val[2] * this.val[15] - this.val[1] * this.val[10] * this.val[15];
         this.tmp[5] = this.val[8] * this.val[14] * this.val[3] - this.val[12] * this.val[10] * this.val[3] + this.val[12] * this.val[2] * this.val[11] - this.val[0] * this.val[14] * this.val[11] - this.val[8] * this.val[2] * this.val[15] + this.val[0] * this.val[10] * this.val[15];
         this.tmp[9] = this.val[12] * this.val[9] * this.val[3] - this.val[8] * this.val[13] * this.val[3] - this.val[12] * this.val[1] * this.val[11] + this.val[0] * this.val[13] * this.val[11] + this.val[8] * this.val[1] * this.val[15] - this.val[0] * this.val[9] * this.val[15];
         this.tmp[13] = this.val[8] * this.val[13] * this.val[2] - this.val[12] * this.val[9] * this.val[2] + this.val[12] * this.val[1] * this.val[10] - this.val[0] * this.val[13] * this.val[10] - this.val[8] * this.val[1] * this.val[14] + this.val[0] * this.val[9] * this.val[14];
         this.tmp[2] = this.val[5] * this.val[14] * this.val[3] - this.val[13] * this.val[6] * this.val[3] + this.val[13] * this.val[2] * this.val[7] - this.val[1] * this.val[14] * this.val[7] - this.val[5] * this.val[2] * this.val[15] + this.val[1] * this.val[6] * this.val[15];
         this.tmp[6] = this.val[12] * this.val[6] * this.val[3] - this.val[4] * this.val[14] * this.val[3] - this.val[12] * this.val[2] * this.val[7] + this.val[0] * this.val[14] * this.val[7] + this.val[4] * this.val[2] * this.val[15] - this.val[0] * this.val[6] * this.val[15];
         this.tmp[10] = this.val[4] * this.val[13] * this.val[3] - this.val[12] * this.val[5] * this.val[3] + this.val[12] * this.val[1] * this.val[7] - this.val[0] * this.val[13] * this.val[7] - this.val[4] * this.val[1] * this.val[15] + this.val[0] * this.val[5] * this.val[15];
         this.tmp[14] = this.val[12] * this.val[5] * this.val[2] - this.val[4] * this.val[13] * this.val[2] - this.val[12] * this.val[1] * this.val[6] + this.val[0] * this.val[13] * this.val[6] + this.val[4] * this.val[1] * this.val[14] - this.val[0] * this.val[5] * this.val[14];
         this.tmp[3] = this.val[9] * this.val[6] * this.val[3] - this.val[5] * this.val[10] * this.val[3] - this.val[9] * this.val[2] * this.val[7] + this.val[1] * this.val[10] * this.val[7] + this.val[5] * this.val[2] * this.val[11] - this.val[1] * this.val[6] * this.val[11];
         this.tmp[7] = this.val[4] * this.val[10] * this.val[3] - this.val[8] * this.val[6] * this.val[3] + this.val[8] * this.val[2] * this.val[7] - this.val[0] * this.val[10] * this.val[7] - this.val[4] * this.val[2] * this.val[11] + this.val[0] * this.val[6] * this.val[11];
         this.tmp[11] = this.val[8] * this.val[5] * this.val[3] - this.val[4] * this.val[9] * this.val[3] - this.val[8] * this.val[1] * this.val[7] + this.val[0] * this.val[9] * this.val[7] + this.val[4] * this.val[1] * this.val[11] - this.val[0] * this.val[5] * this.val[11];
         this.tmp[15] = this.val[4] * this.val[9] * this.val[2] - this.val[8] * this.val[5] * this.val[2] + this.val[8] * this.val[1] * this.val[6] - this.val[0] * this.val[9] * this.val[6] - this.val[4] * this.val[1] * this.val[10] + this.val[0] * this.val[5] * this.val[10];
         this.val[0] = this.tmp[0] * inv_det;
         this.val[4] = this.tmp[4] * inv_det;
         this.val[8] = this.tmp[8] * inv_det;
         this.val[12] = this.tmp[12] * inv_det;
         this.val[1] = this.tmp[1] * inv_det;
         this.val[5] = this.tmp[5] * inv_det;
         this.val[9] = this.tmp[9] * inv_det;
         this.val[13] = this.tmp[13] * inv_det;
         this.val[2] = this.tmp[2] * inv_det;
         this.val[6] = this.tmp[6] * inv_det;
         this.val[10] = this.tmp[10] * inv_det;
         this.val[14] = this.tmp[14] * inv_det;
         this.val[3] = this.tmp[3] * inv_det;
         this.val[7] = this.tmp[7] * inv_det;
         this.val[11] = this.tmp[11] * inv_det;
         this.val[15] = this.tmp[15] * inv_det;
         return this;
      }
   }

   public float det() {
      return this.val[3] * this.val[6] * this.val[9] * this.val[12] - this.val[2] * this.val[7] * this.val[9] * this.val[12] - this.val[3] * this.val[5] * this.val[10] * this.val[12] + this.val[1] * this.val[7] * this.val[10] * this.val[12] + this.val[2] * this.val[5] * this.val[11] * this.val[12] - this.val[1] * this.val[6] * this.val[11] * this.val[12] - this.val[3] * this.val[6] * this.val[8] * this.val[13] + this.val[2] * this.val[7] * this.val[8] * this.val[13] + this.val[3] * this.val[4] * this.val[10] * this.val[13] - this.val[0] * this.val[7] * this.val[10] * this.val[13] - this.val[2] * this.val[4] * this.val[11] * this.val[13] + this.val[0] * this.val[6] * this.val[11] * this.val[13] + this.val[3] * this.val[5] * this.val[8] * this.val[14] - this.val[1] * this.val[7] * this.val[8] * this.val[14] - this.val[3] * this.val[4] * this.val[9] * this.val[14] + this.val[0] * this.val[7] * this.val[9] * this.val[14] + this.val[1] * this.val[4] * this.val[11] * this.val[14] - this.val[0] * this.val[5] * this.val[11] * this.val[14] - this.val[2] * this.val[5] * this.val[8] * this.val[15] + this.val[1] * this.val[6] * this.val[8] * this.val[15] + this.val[2] * this.val[4] * this.val[9] * this.val[15] - this.val[0] * this.val[6] * this.val[9] * this.val[15] - this.val[1] * this.val[4] * this.val[10] * this.val[15] + this.val[0] * this.val[5] * this.val[10] * this.val[15];
   }

   public Matrix4 setToProjection(float near, float far, float fov, float aspectRatio) {
      this.idt();
      float l_fd = (float)(1.0D / Math.tan((double)fov * 0.017453292519943295D / 2.0D));
      float l_a1 = (far + near) / (near - far);
      float l_a2 = 2.0F * far * near / (near - far);
      this.val[0] = l_fd / aspectRatio;
      this.val[1] = 0.0F;
      this.val[2] = 0.0F;
      this.val[3] = 0.0F;
      this.val[4] = 0.0F;
      this.val[5] = l_fd;
      this.val[6] = 0.0F;
      this.val[7] = 0.0F;
      this.val[8] = 0.0F;
      this.val[9] = 0.0F;
      this.val[10] = l_a1;
      this.val[11] = -1.0F;
      this.val[12] = 0.0F;
      this.val[13] = 0.0F;
      this.val[14] = l_a2;
      this.val[15] = 0.0F;
      return this;
   }

   public Matrix4 setToOrtho2D(float x, float y, float width, float height) {
      this.setToOrtho(x, x + width, y, y + height, 0.0F, 1.0F);
      return this;
   }

   public Matrix4 setToOrtho2D(float x, float y, float width, float height, float near, float far) {
      this.setToOrtho(x, x + width, y, y + height, near, far);
      return this;
   }

   public Matrix4 setToOrtho(float left, float right, float bottom, float top, float near, float far) {
      this.idt();
      float x_orth = 2.0F / (right - left);
      float y_orth = 2.0F / (top - bottom);
      float z_orth = -2.0F / (far - near);
      float tx = -(right + left) / (right - left);
      float ty = -(top + bottom) / (top - bottom);
      float tz = -(far + near) / (far - near);
      this.val[0] = x_orth;
      this.val[1] = 0.0F;
      this.val[2] = 0.0F;
      this.val[3] = 0.0F;
      this.val[4] = 0.0F;
      this.val[5] = y_orth;
      this.val[6] = 0.0F;
      this.val[7] = 0.0F;
      this.val[8] = 0.0F;
      this.val[9] = 0.0F;
      this.val[10] = z_orth;
      this.val[11] = 0.0F;
      this.val[12] = tx;
      this.val[13] = ty;
      this.val[14] = tz;
      this.val[15] = 1.0F;
      return this;
   }

   public Matrix4 setTranslation(Vector3 vector) {
      this.val[12] = vector.x;
      this.val[13] = vector.y;
      this.val[14] = vector.z;
      return this;
   }

   public Matrix4 setToTranslation(Vector3 vector) {
      this.idt();
      this.val[12] = vector.x;
      this.val[13] = vector.y;
      this.val[14] = vector.z;
      return this;
   }

   public Matrix4 setToTranslation(float x, float y, float z) {
      this.idt();
      this.val[12] = x;
      this.val[13] = y;
      this.val[14] = z;
      return this;
   }

   public Matrix4 setToTranslationAndScaling(Vector3 translation, Vector3 scaling) {
      this.idt();
      this.val[12] = translation.x;
      this.val[13] = translation.y;
      this.val[14] = translation.z;
      this.val[0] = scaling.x;
      this.val[5] = scaling.y;
      this.val[10] = scaling.z;
      return this;
   }

   public Matrix4 setToTranslationAndScaling(float translationX, float translationY, float translationZ, float scalingX, float scalingY, float scalingZ) {
      this.idt();
      this.val[12] = translationX;
      this.val[13] = translationY;
      this.val[14] = translationZ;
      this.val[0] = scalingX;
      this.val[5] = scalingY;
      this.val[10] = scalingZ;
      return this;
   }

   public Matrix4 setToRotation(Vector3 axis, float angle) {
      if (angle == 0.0F) {
         this.idt();
         return this;
      } else {
         return this.set(quat.set(axis, angle));
      }
   }

   public Matrix4 setToRotation(float axisX, float axisY, float axisZ, float angle) {
      if (angle == 0.0F) {
         this.idt();
         return this;
      } else {
         return this.set(quat.setFromAxis(axisX, axisY, axisZ, angle));
      }
   }

   public Matrix4 setToRotation(Vector3 v1, Vector3 v2) {
      return this.set(quat.setFromCross(v1, v2));
   }

   public Matrix4 setToRotation(float x1, float y1, float z1, float x2, float y2, float z2) {
      return this.set(quat.setFromCross(x1, y1, z1, x2, y2, z2));
   }

   public Matrix4 setFromEulerAngles(float yaw, float pitch, float roll) {
      quat.setEulerAngles(yaw, pitch, roll);
      return this.set(quat);
   }

   public Matrix4 setToScaling(Vector3 vector) {
      this.idt();
      this.val[0] = vector.x;
      this.val[5] = vector.y;
      this.val[10] = vector.z;
      return this;
   }

   public Matrix4 setToScaling(float x, float y, float z) {
      this.idt();
      this.val[0] = x;
      this.val[5] = y;
      this.val[10] = z;
      return this;
   }

   public Matrix4 setToLookAt(Vector3 direction, Vector3 up) {
      l_vez.set(direction).nor();
      l_vex.set(direction).nor();
      l_vex.crs(up).nor();
      l_vey.set(l_vex).crs(l_vez).nor();
      this.idt();
      this.val[0] = l_vex.x;
      this.val[4] = l_vex.y;
      this.val[8] = l_vex.z;
      this.val[1] = l_vey.x;
      this.val[5] = l_vey.y;
      this.val[9] = l_vey.z;
      this.val[2] = -l_vez.x;
      this.val[6] = -l_vez.y;
      this.val[10] = -l_vez.z;
      return this;
   }

   public Matrix4 setToLookAt(Vector3 position, Vector3 target, Vector3 up) {
      tmpVec.set(target).sub(position);
      this.setToLookAt(tmpVec, up);
      this.mul(tmpMat.setToTranslation(position.tmp().scl(-1.0F)));
      return this;
   }

   public Matrix4 setToWorld(Vector3 position, Vector3 forward, Vector3 up) {
      tmpForward.set(forward).nor();
      right.set(tmpForward).crs(up).nor();
      tmpUp.set(right).crs(tmpForward).nor();
      this.set(right, tmpUp, tmpForward, position);
      return this;
   }

   public String toString() {
      return "[" + this.val[0] + "|" + this.val[4] + "|" + this.val[8] + "|" + this.val[12] + "]\n" + "[" + this.val[1] + "|" + this.val[5] + "|" + this.val[9] + "|" + this.val[13] + "]\n" + "[" + this.val[2] + "|" + this.val[6] + "|" + this.val[10] + "|" + this.val[14] + "]\n" + "[" + this.val[3] + "|" + this.val[7] + "|" + this.val[11] + "|" + this.val[15] + "]\n";
   }

   public void lerp(Matrix4 matrix, float alpha) {
      for(int i = 0; i < 16; ++i) {
         this.val[i] = this.val[i] * (1.0F - alpha) + matrix.val[i] * alpha;
      }

   }

   public Matrix4 set(Matrix3 mat) {
      this.val[0] = mat.val[0];
      this.val[1] = mat.val[1];
      this.val[2] = mat.val[2];
      this.val[3] = 0.0F;
      this.val[4] = mat.val[3];
      this.val[5] = mat.val[4];
      this.val[6] = mat.val[5];
      this.val[7] = 0.0F;
      this.val[8] = 0.0F;
      this.val[9] = 0.0F;
      this.val[10] = 1.0F;
      this.val[11] = 0.0F;
      this.val[12] = mat.val[6];
      this.val[13] = mat.val[7];
      this.val[14] = 0.0F;
      this.val[15] = mat.val[8];
      return this;
   }

   public Matrix4 scl(Vector3 scale) {
      float[] var10000 = this.val;
      var10000[0] *= scale.x;
      var10000 = this.val;
      var10000[5] *= scale.y;
      var10000 = this.val;
      var10000[10] *= scale.z;
      return this;
   }

   public Matrix4 scl(float x, float y, float z) {
      float[] var10000 = this.val;
      var10000[0] *= x;
      var10000 = this.val;
      var10000[5] *= y;
      var10000 = this.val;
      var10000[10] *= z;
      return this;
   }

   public Matrix4 scl(float scale) {
      float[] var10000 = this.val;
      var10000[0] *= scale;
      var10000 = this.val;
      var10000[5] *= scale;
      var10000 = this.val;
      var10000[10] *= scale;
      return this;
   }

   public void getTranslation(Vector3 position) {
      position.x = this.val[12];
      position.y = this.val[13];
      position.z = this.val[14];
   }

   public void getRotation(Quaternion rotation) {
      rotation.setFromMatrix(this);
   }

   public Matrix4 toNormalMatrix() {
      this.val[12] = 0.0F;
      this.val[13] = 0.0F;
      this.val[14] = 0.0F;
      return this.inv().tra();
   }

   public static native void mul(float[] var0, float[] var1);

   public static native void mulVec(float[] var0, float[] var1);

   public static native void mulVec(float[] var0, float[] var1, int var2, int var3, int var4);

   public static native void prj(float[] var0, float[] var1);

   public static native void prj(float[] var0, float[] var1, int var2, int var3, int var4);

   public static native void rot(float[] var0, float[] var1);

   public static native void rot(float[] var0, float[] var1, int var2, int var3, int var4);

   public static native boolean inv(float[] var0);

   public static native float det(float[] var0);

   public Matrix4 translate(Vector3 translation) {
      return this.translate(translation.x, translation.y, translation.z);
   }

   public Matrix4 translate(float x, float y, float z) {
      this.tmp[0] = 1.0F;
      this.tmp[4] = 0.0F;
      this.tmp[8] = 0.0F;
      this.tmp[12] = x;
      this.tmp[1] = 0.0F;
      this.tmp[5] = 1.0F;
      this.tmp[9] = 0.0F;
      this.tmp[13] = y;
      this.tmp[2] = 0.0F;
      this.tmp[6] = 0.0F;
      this.tmp[10] = 1.0F;
      this.tmp[14] = z;
      this.tmp[3] = 0.0F;
      this.tmp[7] = 0.0F;
      this.tmp[11] = 0.0F;
      this.tmp[15] = 1.0F;
      mul(this.val, this.tmp);
      return this;
   }

   public Matrix4 rotate(Vector3 axis, float angle) {
      if (angle == 0.0F) {
         return this;
      } else {
         quat.set(axis, angle);
         return this.rotate(quat);
      }
   }

   public Matrix4 rotate(float axisX, float axisY, float axisZ, float angle) {
      if (angle == 0.0F) {
         return this;
      } else {
         quat.setFromAxis(axisX, axisY, axisZ, angle);
         return this.rotate(quat);
      }
   }

   public Matrix4 rotate(Quaternion rotation) {
      rotation.toMatrix(this.tmp);
      mul(this.val, this.tmp);
      return this;
   }

   public Matrix4 rotate(Vector3 v1, Vector3 v2) {
      return this.rotate(quat.setFromCross(v1, v2));
   }

   public Matrix4 scale(float scaleX, float scaleY, float scaleZ) {
      this.tmp[0] = scaleX;
      this.tmp[4] = 0.0F;
      this.tmp[8] = 0.0F;
      this.tmp[12] = 0.0F;
      this.tmp[1] = 0.0F;
      this.tmp[5] = scaleY;
      this.tmp[9] = 0.0F;
      this.tmp[13] = 0.0F;
      this.tmp[2] = 0.0F;
      this.tmp[6] = 0.0F;
      this.tmp[10] = scaleZ;
      this.tmp[14] = 0.0F;
      this.tmp[3] = 0.0F;
      this.tmp[7] = 0.0F;
      this.tmp[11] = 0.0F;
      this.tmp[15] = 1.0F;
      mul(this.val, this.tmp);
      return this;
   }
}
