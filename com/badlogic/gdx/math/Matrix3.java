package com.badlogic.gdx.math;

import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.Serializable;

public class Matrix3 implements Serializable {
   private static final long serialVersionUID = 7907569533774959788L;
   private static final float DEGREE_TO_RAD = 0.017453292F;
   public static final int M00 = 0;
   public static final int M01 = 3;
   public static final int M02 = 6;
   public static final int M10 = 1;
   public static final int M11 = 4;
   public static final int M12 = 7;
   public static final int M20 = 2;
   public static final int M21 = 5;
   public static final int M22 = 8;
   public float[] val = new float[9];
   private float[] tmp = new float[9];

   public Matrix3() {
      this.idt();
   }

   public Matrix3(Matrix3 matrix) {
      this.set(matrix);
   }

   public Matrix3 idt() {
      this.val[0] = 1.0F;
      this.val[1] = 0.0F;
      this.val[2] = 0.0F;
      this.val[3] = 0.0F;
      this.val[4] = 1.0F;
      this.val[5] = 0.0F;
      this.val[6] = 0.0F;
      this.val[7] = 0.0F;
      this.val[8] = 1.0F;
      return this;
   }

   public Matrix3 mul(Matrix3 m) {
      float v00 = this.val[0] * m.val[0] + this.val[3] * m.val[1] + this.val[6] * m.val[2];
      float v01 = this.val[0] * m.val[3] + this.val[3] * m.val[4] + this.val[6] * m.val[5];
      float v02 = this.val[0] * m.val[6] + this.val[3] * m.val[7] + this.val[6] * m.val[8];
      float v10 = this.val[1] * m.val[0] + this.val[4] * m.val[1] + this.val[7] * m.val[2];
      float v11 = this.val[1] * m.val[3] + this.val[4] * m.val[4] + this.val[7] * m.val[5];
      float v12 = this.val[1] * m.val[6] + this.val[4] * m.val[7] + this.val[7] * m.val[8];
      float v20 = this.val[2] * m.val[0] + this.val[5] * m.val[1] + this.val[8] * m.val[2];
      float v21 = this.val[2] * m.val[3] + this.val[5] * m.val[4] + this.val[8] * m.val[5];
      float v22 = this.val[2] * m.val[6] + this.val[5] * m.val[7] + this.val[8] * m.val[8];
      this.val[0] = v00;
      this.val[1] = v10;
      this.val[2] = v20;
      this.val[3] = v01;
      this.val[4] = v11;
      this.val[5] = v21;
      this.val[6] = v02;
      this.val[7] = v12;
      this.val[8] = v22;
      return this;
   }

   public Matrix3 setToRotation(float degrees) {
      float angle = 0.017453292F * degrees;
      float cos = (float)Math.cos((double)angle);
      float sin = (float)Math.sin((double)angle);
      this.val[0] = cos;
      this.val[1] = sin;
      this.val[2] = 0.0F;
      this.val[3] = -sin;
      this.val[4] = cos;
      this.val[5] = 0.0F;
      this.val[6] = 0.0F;
      this.val[7] = 0.0F;
      this.val[8] = 1.0F;
      return this;
   }

   public Matrix3 setToTranslation(float x, float y) {
      this.val[0] = 1.0F;
      this.val[1] = 0.0F;
      this.val[2] = 0.0F;
      this.val[3] = 0.0F;
      this.val[4] = 1.0F;
      this.val[5] = 0.0F;
      this.val[6] = x;
      this.val[7] = y;
      this.val[8] = 1.0F;
      return this;
   }

   public Matrix3 setToTranslation(Vector2 translation) {
      this.val[0] = 1.0F;
      this.val[1] = 0.0F;
      this.val[2] = 0.0F;
      this.val[3] = 0.0F;
      this.val[4] = 1.0F;
      this.val[5] = 0.0F;
      this.val[6] = translation.x;
      this.val[7] = translation.y;
      this.val[8] = 1.0F;
      return this;
   }

   public Matrix3 setToScaling(float scaleX, float scaleY) {
      this.val[0] = scaleX;
      this.val[1] = 0.0F;
      this.val[2] = 0.0F;
      this.val[3] = 0.0F;
      this.val[4] = scaleY;
      this.val[5] = 0.0F;
      this.val[6] = 0.0F;
      this.val[7] = 0.0F;
      this.val[8] = 1.0F;
      return this;
   }

   public String toString() {
      return "[" + this.val[0] + "|" + this.val[3] + "|" + this.val[6] + "]\n" + "[" + this.val[1] + "|" + this.val[4] + "|" + this.val[7] + "]\n" + "[" + this.val[2] + "|" + this.val[5] + "|" + this.val[8] + "]";
   }

   public float det() {
      return this.val[0] * this.val[4] * this.val[8] + this.val[3] * this.val[7] * this.val[2] + this.val[6] * this.val[1] * this.val[5] - this.val[0] * this.val[7] * this.val[5] - this.val[3] * this.val[1] * this.val[8] - this.val[6] * this.val[4] * this.val[2];
   }

   public Matrix3 inv() {
      float det = this.det();
      if (det == 0.0F) {
         throw new GdxRuntimeException("Can't invert a singular matrix");
      } else {
         float inv_det = 1.0F / det;
         this.tmp[0] = this.val[4] * this.val[8] - this.val[5] * this.val[7];
         this.tmp[1] = this.val[2] * this.val[7] - this.val[1] * this.val[8];
         this.tmp[2] = this.val[1] * this.val[5] - this.val[2] * this.val[4];
         this.tmp[3] = this.val[5] * this.val[6] - this.val[3] * this.val[8];
         this.tmp[4] = this.val[0] * this.val[8] - this.val[2] * this.val[6];
         this.tmp[5] = this.val[2] * this.val[3] - this.val[0] * this.val[5];
         this.tmp[6] = this.val[3] * this.val[7] - this.val[4] * this.val[6];
         this.tmp[7] = this.val[1] * this.val[6] - this.val[0] * this.val[7];
         this.tmp[8] = this.val[0] * this.val[4] - this.val[1] * this.val[3];
         this.val[0] = inv_det * this.tmp[0];
         this.val[1] = inv_det * this.tmp[1];
         this.val[2] = inv_det * this.tmp[2];
         this.val[3] = inv_det * this.tmp[3];
         this.val[4] = inv_det * this.tmp[4];
         this.val[5] = inv_det * this.tmp[5];
         this.val[6] = inv_det * this.tmp[6];
         this.val[7] = inv_det * this.tmp[7];
         this.val[8] = inv_det * this.tmp[8];
         return this;
      }
   }

   public Matrix3 set(Matrix3 mat) {
      System.arraycopy(mat.val, 0, this.val, 0, this.val.length);
      return this;
   }

   public Matrix3 set(Matrix4 mat) {
      this.val[0] = mat.val[0];
      this.val[1] = mat.val[1];
      this.val[2] = mat.val[2];
      this.val[3] = mat.val[4];
      this.val[4] = mat.val[5];
      this.val[5] = mat.val[6];
      this.val[6] = mat.val[8];
      this.val[7] = mat.val[9];
      this.val[8] = mat.val[10];
      return this;
   }

   public Matrix3 trn(Vector2 vector) {
      float[] var10000 = this.val;
      var10000[6] += vector.x;
      var10000 = this.val;
      var10000[7] += vector.y;
      return this;
   }

   public Matrix3 trn(float x, float y) {
      float[] var10000 = this.val;
      var10000[6] += x;
      var10000 = this.val;
      var10000[7] += y;
      return this;
   }

   public Matrix3 trn(Vector3 vector) {
      float[] var10000 = this.val;
      var10000[6] += vector.x;
      var10000 = this.val;
      var10000[7] += vector.y;
      return this;
   }

   public Matrix3 translate(float x, float y) {
      this.tmp[0] = 1.0F;
      this.tmp[1] = 0.0F;
      this.tmp[2] = 0.0F;
      this.tmp[3] = 0.0F;
      this.tmp[4] = 1.0F;
      this.tmp[5] = 0.0F;
      this.tmp[6] = x;
      this.tmp[7] = y;
      this.tmp[8] = 1.0F;
      mul(this.val, this.tmp);
      return this;
   }

   public Matrix3 translate(Vector2 translation) {
      this.tmp[0] = 1.0F;
      this.tmp[1] = 0.0F;
      this.tmp[2] = 0.0F;
      this.tmp[3] = 0.0F;
      this.tmp[4] = 1.0F;
      this.tmp[5] = 0.0F;
      this.tmp[6] = translation.x;
      this.tmp[7] = translation.y;
      this.tmp[8] = 1.0F;
      mul(this.val, this.tmp);
      return this;
   }

   public Matrix3 rotate(float angle) {
      if (angle == 0.0F) {
         return this;
      } else {
         angle *= 0.017453292F;
         float cos = (float)Math.cos((double)angle);
         float sin = (float)Math.sin((double)angle);
         this.tmp[0] = cos;
         this.tmp[1] = sin;
         this.tmp[2] = 0.0F;
         this.tmp[3] = -sin;
         this.tmp[4] = cos;
         this.tmp[5] = 0.0F;
         this.tmp[6] = 0.0F;
         this.tmp[7] = 0.0F;
         this.tmp[8] = 1.0F;
         mul(this.val, this.tmp);
         return this;
      }
   }

   public Matrix3 scale(float scaleX, float scaleY) {
      this.tmp[0] = scaleX;
      this.tmp[1] = 0.0F;
      this.tmp[2] = 0.0F;
      this.tmp[3] = 0.0F;
      this.tmp[4] = scaleY;
      this.tmp[5] = 0.0F;
      this.tmp[6] = 0.0F;
      this.tmp[7] = 0.0F;
      this.tmp[8] = 1.0F;
      mul(this.val, this.tmp);
      return this;
   }

   public Matrix3 scale(Vector2 scale) {
      this.tmp[0] = scale.x;
      this.tmp[1] = 0.0F;
      this.tmp[2] = 0.0F;
      this.tmp[3] = 0.0F;
      this.tmp[4] = scale.y;
      this.tmp[5] = 0.0F;
      this.tmp[6] = 0.0F;
      this.tmp[7] = 0.0F;
      this.tmp[8] = 1.0F;
      mul(this.val, this.tmp);
      return this;
   }

   public float[] getValues() {
      return this.val;
   }

   public Matrix3 scl(float scale) {
      float[] var10000 = this.val;
      var10000[0] *= scale;
      var10000 = this.val;
      var10000[4] *= scale;
      return this;
   }

   public Matrix3 scl(Vector2 scale) {
      float[] var10000 = this.val;
      var10000[0] *= scale.x;
      var10000 = this.val;
      var10000[4] *= scale.y;
      return this;
   }

   public Matrix3 scl(Vector3 scale) {
      float[] var10000 = this.val;
      var10000[0] *= scale.x;
      var10000 = this.val;
      var10000[4] *= scale.y;
      return this;
   }

   public Matrix3 transpose() {
      float v01 = this.val[1];
      float v02 = this.val[2];
      float v10 = this.val[3];
      float v12 = this.val[5];
      float v20 = this.val[6];
      float v21 = this.val[7];
      this.val[3] = v01;
      this.val[6] = v02;
      this.val[1] = v10;
      this.val[7] = v12;
      this.val[2] = v20;
      this.val[5] = v21;
      return this;
   }

   private static void mul(float[] mata, float[] matb) {
      float v00 = mata[0] * matb[0] + mata[3] * matb[1] + mata[6] * matb[2];
      float v01 = mata[0] * matb[3] + mata[3] * matb[4] + mata[6] * matb[5];
      float v02 = mata[0] * matb[6] + mata[3] * matb[7] + mata[6] * matb[8];
      float v10 = mata[1] * matb[0] + mata[4] * matb[1] + mata[7] * matb[2];
      float v11 = mata[1] * matb[3] + mata[4] * matb[4] + mata[7] * matb[5];
      float v12 = mata[1] * matb[6] + mata[4] * matb[7] + mata[7] * matb[8];
      float v20 = mata[2] * matb[0] + mata[5] * matb[1] + mata[8] * matb[2];
      float v21 = mata[2] * matb[3] + mata[5] * matb[4] + mata[8] * matb[5];
      float v22 = mata[2] * matb[6] + mata[5] * matb[7] + mata[8] * matb[8];
      mata[0] = v00;
      mata[1] = v10;
      mata[2] = v20;
      mata[3] = v01;
      mata[4] = v11;
      mata[5] = v21;
      mata[6] = v02;
      mata[7] = v12;
      mata[8] = v22;
   }
}
