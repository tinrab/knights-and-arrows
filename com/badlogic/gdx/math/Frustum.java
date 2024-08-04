package com.badlogic.gdx.math;

import com.badlogic.gdx.math.collision.BoundingBox;

public class Frustum {
   protected static final Vector3[] clipSpacePlanePoints = new Vector3[]{new Vector3(-1.0F, -1.0F, -1.0F), new Vector3(1.0F, -1.0F, -1.0F), new Vector3(1.0F, 1.0F, -1.0F), new Vector3(-1.0F, 1.0F, -1.0F), new Vector3(-1.0F, -1.0F, 1.0F), new Vector3(1.0F, -1.0F, 1.0F), new Vector3(1.0F, 1.0F, 1.0F), new Vector3(-1.0F, 1.0F, 1.0F)};
   protected static final float[] clipSpacePlanePointsArray = new float[24];
   public final Plane[] planes = new Plane[6];
   public final Vector3[] planePoints = new Vector3[]{new Vector3(), new Vector3(), new Vector3(), new Vector3(), new Vector3(), new Vector3(), new Vector3(), new Vector3()};
   protected final float[] planePointsArray = new float[24];

   static {
      int j = 0;
      Vector3[] var4;
      int var3 = (var4 = clipSpacePlanePoints).length;

      for(int var2 = 0; var2 < var3; ++var2) {
         Vector3 v = var4[var2];
         clipSpacePlanePointsArray[j++] = v.x;
         clipSpacePlanePointsArray[j++] = v.y;
         clipSpacePlanePointsArray[j++] = v.z;
      }

   }

   public Frustum() {
      for(int i = 0; i < 6; ++i) {
         this.planes[i] = new Plane(new Vector3(), 0.0F);
      }

   }

   public void update(Matrix4 inverseProjectionView) {
      System.arraycopy(clipSpacePlanePointsArray, 0, this.planePointsArray, 0, clipSpacePlanePointsArray.length);
      Matrix4.prj(inverseProjectionView.val, this.planePointsArray, 0, 8, 3);
      int i = 0;

      for(int var3 = 0; i < 8; ++i) {
         Vector3 v = this.planePoints[i];
         v.x = this.planePointsArray[var3++];
         v.y = this.planePointsArray[var3++];
         v.z = this.planePointsArray[var3++];
      }

      this.planes[0].set(this.planePoints[1], this.planePoints[0], this.planePoints[2]);
      this.planes[1].set(this.planePoints[4], this.planePoints[5], this.planePoints[7]);
      this.planes[2].set(this.planePoints[0], this.planePoints[4], this.planePoints[3]);
      this.planes[3].set(this.planePoints[5], this.planePoints[1], this.planePoints[6]);
      this.planes[4].set(this.planePoints[2], this.planePoints[3], this.planePoints[6]);
      this.planes[5].set(this.planePoints[4], this.planePoints[0], this.planePoints[1]);
   }

   public boolean pointInFrustum(Vector3 point) {
      for(int i = 0; i < this.planes.length; ++i) {
         Plane.PlaneSide result = this.planes[i].testPoint(point);
         if (result == Plane.PlaneSide.Back) {
            return false;
         }
      }

      return true;
   }

   public boolean sphereInFrustum(Vector3 center, float radius) {
      for(int i = 0; i < 6; ++i) {
         if (this.planes[i].normal.x * center.x + this.planes[i].normal.y * center.y + this.planes[i].normal.z * center.z < -radius - this.planes[i].d) {
            return false;
         }
      }

      return true;
   }

   public boolean sphereInFrustumWithoutNearFar(Vector3 center, float radius) {
      for(int i = 2; i < 6; ++i) {
         if (this.planes[i].normal.x * center.x + this.planes[i].normal.y * center.y + this.planes[i].normal.z * center.z < -radius - this.planes[i].d) {
            return false;
         }
      }

      return true;
   }

   public boolean boundsInFrustum(BoundingBox bounds) {
      Vector3[] corners = bounds.getCorners();
      int len = corners.length;
      int i = 0;

      for(int len2 = this.planes.length; i < len2; ++i) {
         int out = 0;

         for(int j = 0; j < len; ++j) {
            if (this.planes[i].testPoint(corners[j]) == Plane.PlaneSide.Back) {
               ++out;
            }
         }

         if (out == 8) {
            return false;
         }
      }

      return true;
   }

   public boolean rectInFrustrum(Rectangle rect) {
      float x0 = rect.x;
      float x1 = rect.x + rect.width;
      float y0 = rect.y;
      float y1 = rect.y + rect.height;
      return this.pointInFrustum(new Vector3(x0, y0, 0.0F)) || this.pointInFrustum(new Vector3(x1, y0, 0.0F)) || this.pointInFrustum(new Vector3(x1, y1, 0.0F)) || this.pointInFrustum(new Vector3(x0, y1, 0.0F));
   }
}
