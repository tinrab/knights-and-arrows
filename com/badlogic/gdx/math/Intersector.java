package com.badlogic.gdx.math;

import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import java.util.Arrays;
import java.util.List;

public final class Intersector {
   private static final Vector3 v0 = new Vector3();
   private static final Vector3 v1 = new Vector3();
   private static final Vector3 v2 = new Vector3();
   private static final Plane p = new Plane(new Vector3(), 0.0F);
   private static final Vector3 i = new Vector3();
   private static final Vector3 dir = new Vector3();
   private static final Vector3 start = new Vector3();
   static Vector3 tmp = new Vector3();
   static Vector3 best = new Vector3();
   static Vector3 tmp1 = new Vector3();
   static Vector3 tmp2 = new Vector3();
   static Vector3 tmp3 = new Vector3();
   static Vector3 intersection = new Vector3();

   public static float getLowestPositiveRoot(float a, float b, float c) {
      float det = b * b - 4.0F * a * c;
      if (det < 0.0F) {
         return Float.NaN;
      } else {
         float sqrtD = (float)Math.sqrt((double)det);
         float invA = 1.0F / (2.0F * a);
         float r1 = (-b - sqrtD) * invA;
         float r2 = (-b + sqrtD) * invA;
         if (r1 > r2) {
            float tmp = r2;
            r2 = r1;
            r1 = tmp;
         }

         if (r1 > 0.0F) {
            return r1;
         } else {
            return r2 > 0.0F ? r2 : Float.NaN;
         }
      }
   }

   public static boolean isPointInTriangle(Vector3 point, Vector3 t1, Vector3 t2, Vector3 t3) {
      v0.set(t1).sub(point);
      v1.set(t2).sub(point);
      v2.set(t3).sub(point);
      float ab = v0.dot(v1);
      float ac = v0.dot(v2);
      float bc = v1.dot(v2);
      float cc = v2.dot(v2);
      if (bc * ac - cc * ab < 0.0F) {
         return false;
      } else {
         float bb = v1.dot(v1);
         return !(ab * bc - ac * bb < 0.0F);
      }
   }

   public static boolean intersectSegmentPlane(Vector3 start, Vector3 end, Plane plane, Vector3 intersection) {
      Vector3 dir = end.tmp().sub(start);
      float denom = dir.dot(plane.getNormal());
      float t = -(start.dot(plane.getNormal()) + plane.getD()) / denom;
      if (!(t < 0.0F) && !(t > 1.0F)) {
         intersection.set(start).add(dir.scl(t));
         return true;
      } else {
         return false;
      }
   }

   public static int pointLineSide(Vector2 linePoint1, Vector2 linePoint2, Vector2 point) {
      return (int)Math.signum((linePoint2.x - linePoint1.x) * (point.y - linePoint1.y) - (linePoint2.y - linePoint1.y) * (point.x - linePoint1.x));
   }

   public static int pointLineSide(float linePoint1X, float linePoint1Y, float linePoint2X, float linePoint2Y, float pointX, float pointY) {
      return (int)Math.signum((linePoint2X - linePoint1X) * (pointY - linePoint1Y) - (linePoint2Y - linePoint1Y) * (pointX - linePoint1X));
   }

   public static boolean isPointInPolygon(List<Vector2> polygon, Vector2 point) {
      int j = polygon.size() - 1;
      boolean oddNodes = false;

      for(int i = 0; i < polygon.size(); j = i++) {
         if ((((Vector2)polygon.get(i)).y < point.y && ((Vector2)polygon.get(j)).y >= point.y || ((Vector2)polygon.get(j)).y < point.y && ((Vector2)polygon.get(i)).y >= point.y) && ((Vector2)polygon.get(i)).x + (point.y - ((Vector2)polygon.get(i)).y) / (((Vector2)polygon.get(j)).y - ((Vector2)polygon.get(i)).y) * (((Vector2)polygon.get(j)).x - ((Vector2)polygon.get(i)).x) < point.x) {
            oddNodes = !oddNodes;
         }
      }

      return oddNodes;
   }

   public static float distanceLinePoint(Vector2 start, Vector2 end, Vector2 point) {
      tmp.set(end.x, end.y, 0.0F);
      float l2 = tmp.sub(start.x, start.y, 0.0F).len2();
      if (l2 == 0.0F) {
         return point.dst(start);
      } else {
         tmp.set(point.x, point.y, 0.0F);
         tmp.sub(start.x, start.y, 0.0F);
         tmp2.set(end.x, end.y, 0.0F);
         tmp2.sub(start.x, start.y, 0.0F);
         float t = tmp.dot(tmp2) / l2;
         if (t < 0.0F) {
            return point.dst(start);
         } else if (t > 1.0F) {
            return point.dst(end);
         } else {
            tmp.set(end.x, end.y, 0.0F);
            tmp.sub(start.x, start.y, 0.0F).scl(t).add(start.x, start.y, 0.0F);
            return tmp2.set(point.x, point.y, 0.0F).dst(tmp);
         }
      }
   }

   public static float distanceLinePoint(float startX, float startY, float endX, float endY, float pointX, float pointY) {
      float normalLength = (float)Math.sqrt((double)((endX - startX) * (endX - startX) + (endY - startY) * (endY - startY)));
      return Math.abs((pointX - startX) * (endY - startY) - (pointY - startY) * (endX - startX)) / normalLength;
   }

   public static boolean intersectSegmentCircle(Vector2 start, Vector2 end, Vector2 center, float squareRadius) {
      tmp.set(end.x - start.x, end.y - start.y, 0.0F);
      tmp1.set(center.x - start.x, center.y - start.y, 0.0F);
      float l = tmp.len();
      float u = tmp1.dot(tmp.nor());
      if (u <= 0.0F) {
         tmp2.set(start.x, start.y, 0.0F);
      } else if (u >= l) {
         tmp2.set(end.x, end.y, 0.0F);
      } else {
         tmp3.set(tmp.scl(u));
         tmp2.set(tmp3.x + start.x, tmp3.y + start.y, 0.0F);
      }

      float x = center.x - tmp2.x;
      float y = center.y - tmp2.y;
      return x * x + y * y <= squareRadius;
   }

   public static float intersectSegmentCircleDisplace(Vector2 start, Vector2 end, Vector2 point, float radius, Vector2 displacement) {
      float u = (point.x - start.x) * (end.x - start.x) + (point.y - start.y) * (end.y - start.y);
      float d = start.dst(end);
      u /= d * d;
      if (!(u < 0.0F) && !(u > 1.0F)) {
         tmp.set(end.x, end.y, 0.0F).sub(start.x, start.y, 0.0F);
         tmp2.set(start.x, start.y, 0.0F).add(tmp.scl(u));
         d = tmp2.dst(point.x, point.y, 0.0F);
         if (d < radius) {
            displacement.set(point).sub(tmp2.x, tmp2.y).nor();
            return d;
         } else {
            return Float.POSITIVE_INFINITY;
         }
      } else {
         return Float.POSITIVE_INFINITY;
      }
   }

   public static boolean intersectRayPlane(Ray ray, Plane plane, Vector3 intersection) {
      float denom = ray.direction.dot(plane.getNormal());
      if (denom != 0.0F) {
         float t = -(ray.origin.dot(plane.getNormal()) + plane.getD()) / denom;
         if (t < 0.0F) {
            return false;
         } else {
            if (intersection != null) {
               intersection.set(ray.origin).add(ray.direction.tmp().scl(t));
            }

            return true;
         }
      } else if (plane.testPoint(ray.origin) == Plane.PlaneSide.OnPlane) {
         if (intersection != null) {
            intersection.set(ray.origin);
         }

         return true;
      } else {
         return false;
      }
   }

   public static float intersectLinePlane(float x, float y, float z, float x2, float y2, float z2, Plane plane, Vector3 intersection) {
      Vector3 direction = tmp.set(x2, y2, z2).sub(x, y, z);
      Vector3 origin = tmp2.set(x, y, z);
      float denom = direction.dot(plane.getNormal());
      if (denom != 0.0F) {
         float t = -(origin.dot(plane.getNormal()) + plane.getD()) / denom;
         if (t >= 0.0F && t <= 1.0F && intersection != null) {
            intersection.set(origin).add(direction.scl(t));
         }

         return t;
      } else if (plane.testPoint(origin) == Plane.PlaneSide.OnPlane) {
         if (intersection != null) {
            intersection.set(origin);
         }

         return 0.0F;
      } else {
         return -1.0F;
      }
   }

   public static boolean intersectRayTriangle(Ray ray, Vector3 t1, Vector3 t2, Vector3 t3, Vector3 intersection) {
      p.set(t1, t2, t3);
      if (!intersectRayPlane(ray, p, i)) {
         return false;
      } else {
         v0.set(t3).sub(t1);
         v1.set(t2).sub(t1);
         v2.set(i).sub(t1);
         float dot00 = v0.dot(v0);
         float dot01 = v0.dot(v1);
         float dot02 = v0.dot(v2);
         float dot11 = v1.dot(v1);
         float dot12 = v1.dot(v2);
         float denom = dot00 * dot11 - dot01 * dot01;
         if (denom == 0.0F) {
            return false;
         } else {
            float u = (dot11 * dot02 - dot01 * dot12) / denom;
            float v = (dot00 * dot12 - dot01 * dot02) / denom;
            if (u >= 0.0F && v >= 0.0F && u + v <= 1.0F) {
               if (intersection != null) {
                  intersection.set(i);
               }

               return true;
            } else {
               return false;
            }
         }
      }
   }

   public static boolean intersectRaySphere(Ray ray, Vector3 center, float radius, Vector3 intersection) {
      dir.set(ray.direction).nor();
      start.set(ray.origin);
      float b = 2.0F * dir.dot(start.tmp().sub(center));
      float c = start.dst2(center) - radius * radius;
      float disc = b * b - 4.0F * c;
      if (disc < 0.0F) {
         return false;
      } else {
         float distSqrt = (float)Math.sqrt((double)disc);
         float q;
         if (b < 0.0F) {
            q = (-b - distSqrt) / 2.0F;
         } else {
            q = (-b + distSqrt) / 2.0F;
         }

         float t0 = q / 1.0F;
         float t1 = c / q;
         if (t0 > t1) {
            float temp = t0;
            t0 = t1;
            t1 = temp;
         }

         if (t1 < 0.0F) {
            return false;
         } else if (t0 < 0.0F) {
            if (intersection != null) {
               intersection.set(start).add(dir.tmp().scl(t1));
            }

            return true;
         } else {
            if (intersection != null) {
               intersection.set(start).add(dir.tmp().scl(t0));
            }

            return true;
         }
      }
   }

   public static boolean intersectRayBounds(Ray ray, BoundingBox box, Vector3 intersection) {
      Vector3.tmp.set(ray.origin);
      Vector3.tmp2.set(ray.origin);
      Vector3.tmp.sub(box.min);
      Vector3.tmp2.sub(box.max);
      if (Vector3.tmp.x > 0.0F && Vector3.tmp.y > 0.0F && Vector3.tmp.z > 0.0F && Vector3.tmp2.x < 0.0F && Vector3.tmp2.y < 0.0F && Vector3.tmp2.z < 0.0F) {
         return true;
      } else {
         float lowest = 0.0F;
         boolean hit = false;
         float t;
         if (ray.origin.x <= box.min.x && ray.direction.x > 0.0F) {
            t = (box.min.x - ray.origin.x) / ray.direction.x;
            if (t >= 0.0F) {
               Vector3.tmp3.set(ray.direction).scl(t).add(ray.origin);
               if (Vector3.tmp3.y >= box.min.y && Vector3.tmp3.y <= box.max.y && Vector3.tmp3.z >= box.min.z && Vector3.tmp3.z <= box.max.z && (!hit || t < lowest)) {
                  hit = true;
                  lowest = t;
               }
            }
         }

         if (ray.origin.x >= box.max.x && ray.direction.x < 0.0F) {
            t = (box.max.x - ray.origin.x) / ray.direction.x;
            if (t >= 0.0F) {
               Vector3.tmp3.set(ray.direction).scl(t).add(ray.origin);
               if (Vector3.tmp3.y >= box.min.y && Vector3.tmp3.y <= box.max.y && Vector3.tmp3.z >= box.min.z && Vector3.tmp3.z <= box.max.z && (!hit || t < lowest)) {
                  hit = true;
                  lowest = t;
               }
            }
         }

         if (ray.origin.y <= box.min.y && ray.direction.y > 0.0F) {
            t = (box.min.y - ray.origin.y) / ray.direction.y;
            if (t >= 0.0F) {
               Vector3.tmp3.set(ray.direction).scl(t).add(ray.origin);
               if (Vector3.tmp3.x >= box.min.x && Vector3.tmp3.x <= box.max.x && Vector3.tmp3.z >= box.min.z && Vector3.tmp3.z <= box.max.z && (!hit || t < lowest)) {
                  hit = true;
                  lowest = t;
               }
            }
         }

         if (ray.origin.y >= box.max.y && ray.direction.y < 0.0F) {
            t = (box.max.y - ray.origin.y) / ray.direction.y;
            if (t >= 0.0F) {
               Vector3.tmp3.set(ray.direction).scl(t).add(ray.origin);
               if (Vector3.tmp3.x >= box.min.x && Vector3.tmp3.x <= box.max.x && Vector3.tmp3.z >= box.min.z && Vector3.tmp3.z <= box.max.z && (!hit || t < lowest)) {
                  hit = true;
                  lowest = t;
               }
            }
         }

         if (ray.origin.z <= box.min.y && ray.direction.z > 0.0F) {
            t = (box.min.z - ray.origin.z) / ray.direction.z;
            if (t >= 0.0F) {
               Vector3.tmp3.set(ray.direction).scl(t).add(ray.origin);
               if (Vector3.tmp3.x >= box.min.x && Vector3.tmp3.x <= box.max.x && Vector3.tmp3.y >= box.min.y && Vector3.tmp3.y <= box.max.y && (!hit || t < lowest)) {
                  hit = true;
                  lowest = t;
               }
            }
         }

         if (ray.origin.z >= box.max.z && ray.direction.z < 0.0F) {
            t = (box.max.z - ray.origin.z) / ray.direction.z;
            if (t >= 0.0F) {
               Vector3.tmp3.set(ray.direction).scl(t).add(ray.origin);
               if (Vector3.tmp3.x >= box.min.x && Vector3.tmp3.x <= box.max.x && Vector3.tmp3.y >= box.min.y && Vector3.tmp3.y <= box.max.y && (!hit || t < lowest)) {
                  hit = true;
                  lowest = t;
               }
            }
         }

         if (hit && intersection != null) {
            intersection.set(ray.direction).scl(lowest).add(ray.origin);
         }

         return hit;
      }
   }

   public static boolean intersectRayBoundsFast(Ray ray, BoundingBox box) {
      float divX = 1.0F / ray.direction.x;
      float divY = 1.0F / ray.direction.y;
      float divZ = 1.0F / ray.direction.z;
      float a = (box.min.x - ray.origin.x) * divX;
      float b = (box.max.x - ray.origin.x) * divX;
      float min;
      float max;
      if (a < b) {
         min = a;
         max = b;
      } else {
         min = b;
         max = a;
      }

      a = (box.min.y - ray.origin.y) * divY;
      b = (box.max.y - ray.origin.y) * divY;
      float t;
      if (a > b) {
         t = a;
         a = b;
         b = t;
      }

      if (a > min) {
         min = a;
      }

      if (b < max) {
         max = b;
      }

      a = (box.min.z - ray.origin.z) * divZ;
      b = (box.max.z - ray.origin.z) * divZ;
      if (a > b) {
         t = a;
         a = b;
         b = t;
      }

      if (a > min) {
         min = a;
      }

      if (b < max) {
         max = b;
      }

      return max >= 0.0F && max >= min;
   }

   public static boolean intersectRayTriangles(Ray ray, float[] triangles, Vector3 intersection) {
      float min_dist = Float.MAX_VALUE;
      boolean hit = false;
      if (triangles.length / 3 % 3 != 0) {
         throw new RuntimeException("triangle list size is not a multiple of 3");
      } else {
         for(int i = 0; i < triangles.length - 6; i += 9) {
            boolean result = intersectRayTriangle(ray, tmp1.set(triangles[i], triangles[i + 1], triangles[i + 2]), tmp2.set(triangles[i + 3], triangles[i + 4], triangles[i + 5]), tmp3.set(triangles[i + 6], triangles[i + 7], triangles[i + 8]), tmp);
            if (result) {
               float dist = ray.origin.tmp().sub(tmp).len2();
               if (dist < min_dist) {
                  min_dist = dist;
                  best.set(tmp);
                  hit = true;
               }
            }
         }

         if (!hit) {
            return false;
         } else {
            if (intersection != null) {
               intersection.set(best);
            }

            return true;
         }
      }
   }

   public static boolean intersectRayTriangles(Ray ray, float[] vertices, short[] indices, int vertexSize, Vector3 intersection) {
      float min_dist = Float.MAX_VALUE;
      boolean hit = false;
      if (indices.length % 3 != 0) {
         throw new RuntimeException("triangle list size is not a multiple of 3");
      } else {
         for(int i = 0; i < indices.length; i += 3) {
            int i1 = indices[i] * vertexSize;
            int i2 = indices[i + 1] * vertexSize;
            int i3 = indices[i + 2] * vertexSize;
            boolean result = intersectRayTriangle(ray, tmp1.set(vertices[i1], vertices[i1 + 1], vertices[i1 + 2]), tmp2.set(vertices[i2], vertices[i2 + 1], vertices[i2 + 2]), tmp3.set(vertices[i3], vertices[i3 + 1], vertices[i3 + 2]), tmp);
            if (result) {
               float dist = ray.origin.tmp().sub(tmp).len2();
               if (dist < min_dist) {
                  min_dist = dist;
                  best.set(tmp);
                  hit = true;
               }
            }
         }

         if (!hit) {
            return false;
         } else {
            if (intersection != null) {
               intersection.set(best);
            }

            return true;
         }
      }
   }

   public static boolean intersectRayTriangles(Ray ray, List<Vector3> triangles, Vector3 intersection) {
      float min_dist = Float.MAX_VALUE;
      boolean hit = false;
      if (triangles.size() % 3 != 0) {
         throw new RuntimeException("triangle list size is not a multiple of 3");
      } else {
         for(int i = 0; i < triangles.size() - 2; i += 3) {
            boolean result = intersectRayTriangle(ray, (Vector3)triangles.get(i), (Vector3)triangles.get(i + 1), (Vector3)triangles.get(i + 2), tmp);
            if (result) {
               float dist = ray.origin.tmp().sub(tmp).len2();
               if (dist < min_dist) {
                  min_dist = dist;
                  best.set(tmp);
                  hit = true;
               }
            }
         }

         if (!hit) {
            return false;
         } else {
            if (intersection != null) {
               intersection.set(best);
            }

            return true;
         }
      }
   }

   public static boolean intersectLines(Vector2 p1, Vector2 p2, Vector2 p3, Vector2 p4, Vector2 intersection) {
      float x1 = p1.x;
      float y1 = p1.y;
      float x2 = p2.x;
      float y2 = p2.y;
      float x3 = p3.x;
      float y3 = p3.y;
      float x4 = p4.x;
      float y4 = p4.y;
      float det1 = det(x1, y1, x2, y2);
      float det2 = det(x3, y3, x4, y4);
      float det3 = det(x1 - x2, y1 - y2, x3 - x4, y3 - y4);
      float x = det(det1, x1 - x2, det2, x3 - x4) / det3;
      float y = det(det1, y1 - y2, det2, y3 - y4) / det3;
      intersection.x = x;
      intersection.y = y;
      return true;
   }

   public static boolean intersectLinePolygon(Vector2 p1, Vector2 p2, Polygon polygon) {
      float[] vertices = polygon.getTransformedVertices();
      int i = 0;
      float x1 = p1.x;
      float y1 = p1.y;
      float x2 = p2.x;
      float y2 = p2.y;

      for(float det1 = det(x1, y1, x2, y2); i < vertices.length - 2; i += 2) {
         float x3 = vertices[i];
         float y3 = vertices[i + 1];
         float x4 = vertices[i + 2];
         float y4 = vertices[i + 3];
         float det2 = det(x3, y3, x4, y4);
         float det3 = det(x1 - x2, y1 - y2, x3 - x4, y3 - y4);
         float x = det(det1, x1 - x2, det2, x3 - x4) / det3;
         float y = det(det1, y1 - y2, det2, y3 - y4) / det3;
         if ((x >= x3 && x <= x4 || x >= x4 && x <= x3) && (y >= y3 && y <= y4 || y >= y4 && y <= y3)) {
            return true;
         }
      }

      return false;
   }

   public static boolean intersectSegments(Vector2 p1, Vector2 p2, Vector2 p3, Vector2 p4, Vector2 intersection) {
      float x1 = p1.x;
      float y1 = p1.y;
      float x2 = p2.x;
      float y2 = p2.y;
      float x3 = p3.x;
      float y3 = p3.y;
      float x4 = p4.x;
      float y4 = p4.y;
      float d = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
      if (d == 0.0F) {
         return false;
      } else {
         float ua = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3)) / d;
         float ub = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3)) / d;
         if (!(ua < 0.0F) && !(ua > 1.0F)) {
            if (!(ub < 0.0F) && !(ub > 1.0F)) {
               if (intersection != null) {
                  intersection.set(x1 + (x2 - x1) * ua, y1 + (y2 - y1) * ua);
               }

               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   static float det(float a, float b, float c, float d) {
      return a * d - b * c;
   }

   static double detd(double a, double b, double c, double d) {
      return a * d - b * c;
   }

   public static boolean overlaps(Circle c1, Circle c2) {
      return c1.overlaps(c2);
   }

   public static boolean overlaps(Rectangle r1, Rectangle r2) {
      return r1.overlaps(r2);
   }

   public static boolean overlaps(Circle c, Rectangle r) {
      float closestX = c.x;
      float closestY = c.y;
      if (c.x < r.x) {
         closestX = r.x;
      } else if (c.x > r.x + r.width) {
         closestX = r.x + r.width;
      }

      if (c.y < r.y) {
         closestY = r.y;
      } else if (c.y > r.y + r.height) {
         closestY = r.y + r.height;
      }

      closestX -= c.x;
      closestX *= closestX;
      closestY -= c.y;
      closestY *= closestY;
      return closestX + closestY < c.radius * c.radius;
   }

   public static boolean overlapConvexPolygons(Polygon p1, Polygon p2) {
      return overlapConvexPolygons((Polygon)p1, (Polygon)p2, (Intersector.MinimumTranslationVector)null);
   }

   public static boolean overlapConvexPolygons(Polygon p1, Polygon p2, Intersector.MinimumTranslationVector mtv) {
      return overlapConvexPolygons(p1.getTransformedVertices(), p2.getTransformedVertices(), mtv);
   }

   public static boolean overlapConvexPolygons(float[] verts1, float[] verts2, Intersector.MinimumTranslationVector mtv) {
      float overlap = Float.MAX_VALUE;
      float smallestAxisX = 0.0F;
      float smallestAxisY = 0.0F;
      int numAxes1 = verts1.length;

      int numAxes2;
      float x1;
      float y1;
      float x2;
      float axisX;
      float axisX;
      float axisY;
      float min1;
      float min1;
      float min2;
      float o;
      float mins;
      float max1;
      float max2;
      for(numAxes2 = 0; numAxes2 < numAxes1; numAxes2 += 2) {
         float x1 = verts1[numAxes2];
         x1 = verts1[numAxes2 + 1];
         y1 = verts1[(numAxes2 + 2) % numAxes1];
         x2 = verts1[(numAxes2 + 3) % numAxes1];
         axisX = x1 - x2;
         axisX = -(x1 - y1);
         axisY = (float)Math.sqrt((double)(axisX * axisX + axisX * axisX));
         axisX /= axisY;
         axisX /= axisY;
         min1 = axisX * verts1[0] + axisX * verts1[1];
         min1 = min1;

         for(int j = 2; j < verts1.length; j += 2) {
            min2 = axisX * verts1[j] + axisX * verts1[j + 1];
            if (min2 < min1) {
               min1 = min2;
            } else if (min2 > min1) {
               min1 = min2;
            }
         }

         max1 = axisX * verts2[0] + axisX * verts2[1];
         min2 = max1;

         for(int j = 2; j < verts2.length; j += 2) {
            o = axisX * verts2[j] + axisX * verts2[j + 1];
            if (o < max1) {
               max1 = o;
            } else if (o > min2) {
               min2 = o;
            }
         }

         if ((!(min1 <= max1) || !(min1 >= max1)) && (!(max1 <= min1) || !(min2 >= min1))) {
            return false;
         }

         max2 = Math.min(min1, min2) - Math.max(min1, max1);
         if (min1 < max1 && min1 > min2 || max1 < min1 && min2 > min1) {
            o = Math.abs(min1 - max1);
            mins = Math.abs(min1 - min2);
            if (o < mins) {
               axisX = -axisX;
               axisX = -axisX;
               max2 += o;
            } else {
               max2 += mins;
            }
         }

         if (max2 < overlap) {
            overlap = max2;
            smallestAxisX = axisX;
            smallestAxisY = axisX;
         }
      }

      numAxes2 = verts2.length;

      for(int i = 0; i < numAxes2; i += 2) {
         x1 = verts2[i];
         y1 = verts2[i + 1];
         x2 = verts2[(i + 2) % numAxes2];
         axisX = verts2[(i + 3) % numAxes2];
         axisX = y1 - axisX;
         axisY = -(x1 - x2);
         min1 = (float)Math.sqrt((double)(axisX * axisX + axisY * axisY));
         axisX /= min1;
         axisY /= min1;
         min1 = axisX * verts1[0] + axisY * verts1[1];
         max1 = min1;

         for(int j = 2; j < verts1.length; j += 2) {
            max2 = axisX * verts1[j] + axisY * verts1[j + 1];
            if (max2 < min1) {
               min1 = max2;
            } else if (max2 > max1) {
               max1 = max2;
            }
         }

         min2 = axisX * verts2[0] + axisY * verts2[1];
         max2 = min2;

         for(int j = 2; j < verts2.length; j += 2) {
            mins = axisX * verts2[j] + axisY * verts2[j + 1];
            if (mins < min2) {
               min2 = mins;
            } else if (mins > max2) {
               max2 = mins;
            }
         }

         if ((!(min1 <= min2) || !(max1 >= min2)) && (!(min2 <= min1) || !(max2 >= min1))) {
            return false;
         }

         o = Math.min(max1, max2) - Math.max(min1, min2);
         if (min1 < min2 && max1 > max2 || min2 < min1 && max2 > max1) {
            mins = Math.abs(min1 - min2);
            float maxs = Math.abs(max1 - max2);
            if (mins < maxs) {
               axisX = -axisX;
               axisY = -axisY;
               o += mins;
            } else {
               o += maxs;
            }
         }

         if (o < overlap) {
            overlap = o;
            smallestAxisX = axisX;
            smallestAxisY = axisY;
         }
      }

      if (mtv != null) {
         mtv.normal.set(smallestAxisX, smallestAxisY);
         mtv.depth = overlap;
      }

      return true;
   }

   public static void splitTriangle(float[] triangle, Plane plane, Intersector.SplitTriangle split) {
      int stride = triangle.length / 3;
      boolean r1 = plane.testPoint(triangle[0], triangle[1], triangle[2]) == Plane.PlaneSide.Back;
      boolean r2 = plane.testPoint(triangle[0 + stride], triangle[1 + stride], triangle[2 + stride]) == Plane.PlaneSide.Back;
      boolean r3 = plane.testPoint(triangle[0 + stride * 2], triangle[1 + stride * 2], triangle[2 + stride * 2]) == Plane.PlaneSide.Back;
      split.reset();
      if (r1 == r2 && r2 == r3) {
         split.total = 1;
         if (r1) {
            split.numBack = 1;
            System.arraycopy(triangle, 0, split.back, 0, triangle.length);
         } else {
            split.numFront = 1;
            System.arraycopy(triangle, 0, split.front, 0, triangle.length);
         }

      } else {
         split.total = 3;
         split.numFront = (r1 ? 1 : 0) + (r2 ? 1 : 0) + (r3 ? 1 : 0);
         split.numBack = split.total - split.numFront;
         split.setSide(r1);
         int first = 0;
         if (r1 != r2) {
            splitEdge(triangle, first, stride, stride, plane, split.edgeSplit, 0);
            split.add(triangle, first, stride);
            split.add(split.edgeSplit, 0, stride);
            split.setSide(!split.getSide());
            split.add(split.edgeSplit, 0, stride);
         } else {
            split.add(triangle, first, stride);
         }

         int second = stride + stride;
         if (r2 != r3) {
            splitEdge(triangle, stride, second, stride, plane, split.edgeSplit, 0);
            split.add(triangle, stride, stride);
            split.add(split.edgeSplit, 0, stride);
            split.setSide(!split.getSide());
            split.add(split.edgeSplit, 0, stride);
         } else {
            split.add(triangle, stride, stride);
         }

         int first = stride + stride;
         int second = 0;
         if (r3 != r1) {
            splitEdge(triangle, first, second, stride, plane, split.edgeSplit, 0);
            split.add(triangle, first, stride);
            split.add(split.edgeSplit, 0, stride);
            split.setSide(!split.getSide());
            split.add(split.edgeSplit, 0, stride);
         } else {
            split.add(triangle, first, stride);
         }

         if (split.numFront == 2) {
            System.arraycopy(split.front, stride * 2, split.front, stride * 3, stride * 2);
            System.arraycopy(split.front, 0, split.front, stride * 5, stride);
         } else {
            System.arraycopy(split.back, stride * 2, split.back, stride * 3, stride * 2);
            System.arraycopy(split.back, 0, split.back, stride * 5, stride);
         }

      }
   }

   private static void splitEdge(float[] vertices, int s, int e, int stride, Plane plane, float[] split, int offset) {
      float t = intersectLinePlane(vertices[s], vertices[s + 1], vertices[s + 2], vertices[e], vertices[e + 1], vertices[e + 2], plane, intersection);
      split[offset + 0] = intersection.x;
      split[offset + 1] = intersection.y;
      split[offset + 2] = intersection.z;

      for(int i = 3; i < stride; ++i) {
         float a = vertices[s + i];
         float b = vertices[e + i];
         split[offset + i] = a + t * (b - a);
      }

   }

   public static void main(String[] args) {
      Plane plane = new Plane(new Vector3(1.0F, 0.0F, 0.0F), 0.0F);
      Intersector.SplitTriangle split = new Intersector.SplitTriangle(3);
      float[] fTriangle = new float[]{-10.0F, 0.0F, 10.0F, -1.0F, 0.0F, 0.0F, -10.0F, 0.0F, 10.0F};
      splitTriangle(fTriangle, plane, split);
      System.out.println(split);
      float[] triangle = new float[]{-10.0F, 0.0F, 10.0F, 10.0F, 0.0F, 0.0F, -10.0F, 0.0F, -10.0F};
      splitTriangle(triangle, plane, split);
      System.out.println(split);
      Circle c1 = new Circle(0.0F, 0.0F, 1.0F);
      Circle c2 = new Circle(0.0F, 0.0F, 1.0F);
      Circle c3 = new Circle(2.0F, 0.0F, 1.0F);
      Circle c4 = new Circle(0.0F, 0.0F, 2.0F);
      System.out.println("Circle test cases");
      System.out.println(c1.overlaps(c1));
      System.out.println(c1.overlaps(c2));
      System.out.println(c1.overlaps(c3));
      System.out.println(c1.overlaps(c4));
      System.out.println(c4.overlaps(c1));
      System.out.println(c1.contains(0.0F, 1.0F));
      System.out.println(c1.contains(0.0F, 2.0F));
      System.out.println(c1.contains(c1));
      System.out.println(c1.contains(c4));
      System.out.println(c4.contains(c1));
      System.out.println("Rectangle test cases");
      Rectangle r1 = new Rectangle(0.0F, 0.0F, 1.0F, 1.0F);
      Rectangle r2 = new Rectangle(1.0F, 0.0F, 2.0F, 1.0F);
      System.out.println(r1.overlaps(r1));
      System.out.println(r1.overlaps(r2));
      System.out.println(r1.contains(0.0F, 0.0F));
      System.out.println("BoundingBox test cases");
      BoundingBox b1 = new BoundingBox(Vector3.Zero, new Vector3(1.0F, 1.0F, 1.0F));
      BoundingBox b2 = new BoundingBox(new Vector3(1.0F, 1.0F, 1.0F), new Vector3(2.0F, 2.0F, 2.0F));
      System.out.println(b1.contains(Vector3.Zero));
      System.out.println(b1.contains(b1));
      System.out.println(b1.contains(b2));
   }

   public static class MinimumTranslationVector {
      public Vector2 normal = new Vector2();
      public float depth = 0.0F;
   }

   public static class SplitTriangle {
      public float[] front;
      public float[] back;
      float[] edgeSplit;
      public int numFront;
      public int numBack;
      public int total;
      boolean frontCurrent = false;
      int frontOffset = 0;
      int backOffset = 0;

      public SplitTriangle(int numAttributes) {
         this.front = new float[numAttributes * 3 * 2];
         this.back = new float[numAttributes * 3 * 2];
         this.edgeSplit = new float[numAttributes];
      }

      public String toString() {
         return "SplitTriangle [front=" + Arrays.toString(this.front) + ", back=" + Arrays.toString(this.back) + ", numFront=" + this.numFront + ", numBack=" + this.numBack + ", total=" + this.total + "]";
      }

      void setSide(boolean front) {
         this.frontCurrent = front;
      }

      boolean getSide() {
         return this.frontCurrent;
      }

      void add(float[] vertex, int offset, int stride) {
         if (this.frontCurrent) {
            System.arraycopy(vertex, offset, this.front, this.frontOffset, stride);
            this.frontOffset += stride;
         } else {
            System.arraycopy(vertex, offset, this.back, this.backOffset, stride);
            this.backOffset += stride;
         }

      }

      void reset() {
         this.frontCurrent = false;
         this.frontOffset = 0;
         this.backOffset = 0;
         this.numFront = 0;
         this.numBack = 0;
         this.total = 0;
      }
   }
}
