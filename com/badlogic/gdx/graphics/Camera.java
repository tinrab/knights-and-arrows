package com.badlogic.gdx.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

public abstract class Camera {
   public final Vector3 position = new Vector3();
   public final Vector3 direction = new Vector3(0.0F, 0.0F, -1.0F);
   public final Vector3 up = new Vector3(0.0F, 1.0F, 0.0F);
   public final Matrix4 projection = new Matrix4();
   public final Matrix4 view = new Matrix4();
   public final Matrix4 combined = new Matrix4();
   public final Matrix4 invProjectionView = new Matrix4();
   public float near = 1.0F;
   public float far = 100.0F;
   public float viewportWidth = 0.0F;
   public float viewportHeight = 0.0F;
   public final Frustum frustum = new Frustum();
   private final Vector3 tmpVec = new Vector3();
   final Ray ray = new Ray(new Vector3(), new Vector3());

   public abstract void update();

   public abstract void update(boolean var1);

   public void apply(GL10 gl) {
      gl.glMatrixMode(5889);
      gl.glLoadMatrixf(this.projection.val, 0);
      gl.glMatrixMode(5888);
      gl.glLoadMatrixf(this.view.val, 0);
   }

   public void lookAt(float x, float y, float z) {
      this.direction.set(x, y, z).sub(this.position).nor();
      this.normalizeUp();
   }

   public void lookAt(Vector3 target) {
      this.direction.set(target).sub(this.position).nor();
      this.normalizeUp();
   }

   public void normalizeUp() {
      this.tmpVec.set(this.direction).crs(this.up).nor();
      this.up.set(this.tmpVec).crs(this.direction).nor();
   }

   public void rotate(float angle, float axisX, float axisY, float axisZ) {
      this.direction.rotate(angle, axisX, axisY, axisZ);
      this.up.rotate(angle, axisX, axisY, axisZ);
   }

   public void rotate(Vector3 axis, float angle) {
      this.direction.rotate(axis, angle);
      this.up.rotate(axis, angle);
   }

   public void rotate(Matrix4 transform) {
      this.direction.rot(transform);
      this.up.rot(transform);
   }

   public void rotate(Quaternion quat) {
      quat.transform(this.direction);
      quat.transform(this.up);
   }

   public void rotateAround(Vector3 point, Vector3 axis, float angle) {
      this.tmpVec.set(point);
      this.tmpVec.sub(this.position);
      this.translate(this.tmpVec);
      this.rotate(axis, angle);
      this.tmpVec.rotate(axis, angle);
      this.translate(-this.tmpVec.x, -this.tmpVec.y, -this.tmpVec.z);
   }

   public void transform(Matrix4 transform) {
      this.position.mul(transform);
      this.rotate(transform);
   }

   public void translate(float x, float y, float z) {
      this.position.add(x, y, z);
   }

   public void translate(Vector3 vec) {
      this.position.add(vec);
   }

   public void unproject(Vector3 vec, float viewportX, float viewportY, float viewportWidth, float viewportHeight) {
      float x = vec.x;
      float y = vec.y;
      x -= viewportX;
      y = (float)Gdx.graphics.getHeight() - y - 1.0F;
      y -= viewportY;
      vec.x = 2.0F * x / viewportWidth - 1.0F;
      vec.y = 2.0F * y / viewportHeight - 1.0F;
      vec.z = 2.0F * vec.z - 1.0F;
      vec.prj(this.invProjectionView);
   }

   public void unproject(Vector3 vec) {
      this.unproject(vec, 0.0F, 0.0F, (float)Gdx.graphics.getWidth(), (float)Gdx.graphics.getHeight());
   }

   public void project(Vector3 vec) {
      this.project(vec, 0.0F, 0.0F, (float)Gdx.graphics.getWidth(), (float)Gdx.graphics.getHeight());
   }

   public void project(Vector3 vec, float viewportX, float viewportY, float viewportWidth, float viewportHeight) {
      vec.prj(this.combined);
      vec.x = viewportWidth * (vec.x + 1.0F) / 2.0F + viewportX;
      vec.y = viewportHeight * (vec.y + 1.0F) / 2.0F + viewportY;
      vec.z = (vec.z + 1.0F) / 2.0F;
   }

   public Ray getPickRay(float x, float y, float viewportX, float viewportY, float viewportWidth, float viewportHeight) {
      this.unproject(this.ray.origin.set(x, y, 0.0F), viewportX, viewportY, viewportWidth, viewportHeight);
      this.unproject(this.ray.direction.set(x, y, 1.0F), viewportX, viewportY, viewportWidth, viewportHeight);
      this.ray.direction.sub(this.ray.origin).nor();
      return this.ray;
   }

   public Ray getPickRay(float x, float y) {
      return this.getPickRay(x, y, 0.0F, 0.0F, (float)Gdx.graphics.getWidth(), (float)Gdx.graphics.getHeight());
   }
}
