package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class ScissorStack {
   private static Array<Rectangle> scissors = new Array();
   static Vector3 tmp = new Vector3();
   static final Rectangle viewport = new Rectangle();

   public static boolean pushScissors(Rectangle scissor) {
      fix(scissor);
      if (scissors.size == 0) {
         if (scissor.width < 1.0F || scissor.height < 1.0F) {
            return false;
         }

         Gdx.gl.glEnable(3089);
      } else {
         Rectangle parent = (Rectangle)scissors.get(scissors.size - 1);
         float minX = Math.max(parent.x, scissor.x);
         float maxX = Math.min(parent.x + parent.width, scissor.x + scissor.width);
         if (maxX - minX < 1.0F) {
            return false;
         }

         float minY = Math.max(parent.y, scissor.y);
         float maxY = Math.min(parent.y + parent.height, scissor.y + scissor.height);
         if (maxY - minY < 1.0F) {
            return false;
         }

         scissor.x = minX;
         scissor.y = minY;
         scissor.width = maxX - minX;
         scissor.height = Math.max(1.0F, maxY - minY);
      }

      scissors.add(scissor);
      Gdx.gl.glScissor((int)scissor.x, (int)scissor.y, (int)scissor.width, (int)scissor.height);
      return true;
   }

   public static Rectangle popScissors() {
      Rectangle old = (Rectangle)scissors.pop();
      if (scissors.size == 0) {
         Gdx.gl.glDisable(3089);
      } else {
         Rectangle scissor = (Rectangle)scissors.peek();
         Gdx.gl.glScissor((int)scissor.x, (int)scissor.y, (int)scissor.width, (int)scissor.height);
      }

      return old;
   }

   private static void fix(Rectangle rect) {
      rect.x = (float)Math.round(rect.x);
      rect.y = (float)Math.round(rect.y);
      rect.width = (float)Math.round(rect.width);
      rect.height = (float)Math.round(rect.height);
      if (rect.width < 0.0F) {
         rect.width = -rect.width;
         rect.x -= rect.width;
      }

      if (rect.height < 0.0F) {
         rect.height = -rect.height;
         rect.y -= rect.height;
      }

   }

   public static void calculateScissors(Camera camera, Matrix4 batchTransform, Rectangle area, Rectangle scissor) {
      tmp.set(area.x, area.y, 0.0F);
      tmp.mul(batchTransform);
      camera.project(tmp);
      scissor.x = tmp.x;
      scissor.y = tmp.y;
      tmp.set(area.x + area.width, area.y + area.height, 0.0F);
      tmp.mul(batchTransform);
      camera.project(tmp);
      scissor.width = tmp.x - scissor.x;
      scissor.height = tmp.y - scissor.y;
   }

   public static Rectangle getViewport() {
      if (scissors.size == 0) {
         viewport.set(0.0F, 0.0F, (float)Gdx.graphics.getWidth(), (float)Gdx.graphics.getHeight());
         return viewport;
      } else {
         Rectangle scissor = (Rectangle)scissors.peek();
         viewport.set(scissor);
         return viewport;
      }
   }

   public static void toWindowCoordinates(Camera camera, Matrix4 transformMatrix, Vector2 point) {
      tmp.set(point.x, point.y, 0.0F);
      tmp.mul(transformMatrix);
      camera.project(tmp);
      tmp.y = (float)Gdx.graphics.getHeight() - tmp.y;
      point.x = tmp.x;
      point.y = tmp.y;
   }
}
