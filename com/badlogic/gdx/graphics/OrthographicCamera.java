package com.badlogic.gdx.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class OrthographicCamera extends Camera {
   public float zoom = 1.0F;
   private final Vector3 tmp = new Vector3();

   public OrthographicCamera() {
      this.near = 0.0F;
   }

   public OrthographicCamera(float viewportWidth, float viewportHeight) {
      this.viewportWidth = viewportWidth;
      this.viewportHeight = viewportHeight;
      this.near = 0.0F;
      this.update();
   }

   public OrthographicCamera(float viewportWidth, float viewportHeight, float diamondAngle) {
      this.viewportWidth = viewportWidth;
      this.viewportHeight = viewportHeight;
      this.near = 0.0F;
      this.findDirectionForIsoView(diamondAngle, 1.0E-8F, 20);
      this.update();
   }

   public void findDirectionForIsoView(float targetAngle, float epsilon, int maxIterations) {
      float start = targetAngle - 5.0F;
      float end = targetAngle + 5.0F;
      float mid = targetAngle;
      int iterations = 0;

      for(float aMid = 0.0F; Math.abs(targetAngle - aMid) > epsilon && iterations++ < maxIterations; mid = start + (end - start) / 2.0F) {
         aMid = this.calculateAngle(mid);
         if (targetAngle < aMid) {
            end = mid;
         } else {
            start = mid;
         }
      }

      this.position.set(this.calculateDirection(mid));
      this.position.y = -this.position.y;
      this.lookAt(0.0F, 0.0F, 0.0F);
      this.normalizeUp();
   }

   private float calculateAngle(float a) {
      Vector3 camPos = this.calculateDirection(a);
      this.position.set(camPos.scl(30.0F));
      this.lookAt(0.0F, 0.0F, 0.0F);
      this.normalizeUp();
      this.update();
      Vector3 orig = new Vector3(0.0F, 0.0F, 0.0F);
      Vector3 vec = new Vector3(1.0F, 0.0F, 0.0F);
      this.project(orig);
      this.project(vec);
      Vector2 d = new Vector2(vec.x - orig.x, -(vec.y - orig.y));
      return d.angle();
   }

   private Vector3 calculateDirection(float angle) {
      Matrix4 transform = new Matrix4();
      Vector3 dir = (new Vector3(-1.0F, 0.0F, 1.0F)).nor();
      transform.setToRotation((new Vector3(1.0F, 0.0F, 1.0F)).nor(), angle);
      dir.mul(transform).nor();
      return dir;
   }

   public void update() {
      this.update(true);
   }

   public void update(boolean updateFrustum) {
      this.projection.setToOrtho(this.zoom * -this.viewportWidth / 2.0F, this.zoom * this.viewportWidth / 2.0F, this.zoom * -this.viewportHeight / 2.0F, this.zoom * this.viewportHeight / 2.0F, Math.abs(this.near), Math.abs(this.far));
      this.view.setToLookAt(this.position, this.tmp.set(this.position).add(this.direction), this.up);
      this.combined.set(this.projection);
      Matrix4.mul(this.combined.val, this.view.val);
      if (updateFrustum) {
         this.invProjectionView.set(this.combined);
         Matrix4.inv(this.invProjectionView.val);
         this.frustum.update(this.invProjectionView);
      }

   }

   public void setToOrtho(boolean yDown) {
      this.setToOrtho(yDown, (float)Gdx.graphics.getWidth(), (float)Gdx.graphics.getHeight());
   }

   public void setToOrtho(boolean yDown, float viewportWidth, float viewportHeight) {
      if (yDown) {
         this.up.set(0.0F, -1.0F, 0.0F);
         this.direction.set(0.0F, 0.0F, 1.0F);
      }

      this.position.set(this.zoom * viewportWidth / 2.0F, this.zoom * viewportHeight / 2.0F, 0.0F);
      this.viewportWidth = viewportWidth;
      this.viewportHeight = viewportHeight;
      this.update();
   }

   public void rotate(float angle) {
      this.rotate(this.direction, angle);
   }

   public float getXYAngle() {
      return MathUtils.atan2(this.up.x, this.up.y);
   }

   public void translate(float x, float y) {
      this.translate(x, y, 0.0F);
   }

   public void translate(Vector2 vec) {
      this.translate(vec.x, vec.y, 0.0F);
   }
}
