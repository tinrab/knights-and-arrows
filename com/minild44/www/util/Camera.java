package com.minild44.www.util;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Camera extends OrthographicCamera {
   private Vector2 target = new Vector2();
   public Vector2 tmpPos = new Vector2();
   private float screenShakeAmplitude;
   private float screenShakeAnim;
   private float offx;
   private float offy;

   public Camera(int width, int height) {
      super((float)width, (float)height);
   }

   public void transform(float delta) {
      this.screenShakeAnim += 0.005F;
      this.screenShakeAmplitude -= this.screenShakeAmplitude * 0.05F;
      this.offx = (float)(Math.cos((double)((float)((double)this.screenShakeAnim * 3.141592653589793D * 20.0D))) * (double)this.screenShakeAmplitude);
      this.offy = (float)(Math.sin((double)((float)((double)this.screenShakeAnim * 3.141592653589793D * 6.0D))) * (double)this.screenShakeAmplitude * 0.699999988079071D);
      this.position.set(this.position.x + this.offx, this.position.y + this.offy, 0.0F);
      this.tmpPos.x = MathUtils.lerp(this.tmpPos.x, this.target.x, 6.0F * delta);
      this.tmpPos.y = MathUtils.lerp(this.tmpPos.y, this.target.y, 6.0F * delta);
      this.tmpPos.add(this.offx, this.offy);
      this.position.set((float)((int)this.tmpPos.x), (float)((int)this.tmpPos.y), 0.0F);
   }

   public void update() {
      this.screenShakeAnim += 0.005F;
      this.screenShakeAmplitude -= this.screenShakeAmplitude * 0.05F;
      super.update();
   }

   public void addShake(float amount) {
      this.screenShakeAmplitude += amount;
   }

   public void setTarget(float x, float y) {
      this.target.set(x, y);
   }

   public void translate(float dx, float dy) {
      this.target.add(dx, dy);
   }

   public void clampTo(float x1, float y1, float x2, float y2) {
      if (this.target.x - this.viewportWidth / 2.0F < x1) {
         this.target.x = x1 + this.viewportWidth / 2.0F;
      } else if (this.target.x + this.viewportWidth / 2.0F > x2) {
         this.target.x = x2 - this.viewportWidth / 2.0F;
      }

      if (this.target.y - this.viewportHeight / 2.0F < y1) {
         this.target.y = y1 + this.viewportHeight / 2.0F;
      } else if (this.target.y + this.viewportHeight / 2.0F > y2) {
         this.target.y = y2 - this.viewportHeight / 2.0F;
      }

   }

   public Vector2 unproject(Vector2 v) {
      Vector3 v3 = new Vector3(v.x, v.y, 0.0F);
      this.unproject(v3);
      return new Vector2(v3.x, v3.y);
   }

   public Vector2 project(Vector2 v) {
      Vector3 v3 = new Vector3(v.x, v.y, 0.0F);
      this.project(v3);
      return new Vector2(v3.x, v3.y);
   }
}
