package com.badlogic.gdx.physics.box2d;

import com.badlogic.gdx.math.Vector2;

public class CircleShape extends Shape {
   private final float[] tmp = new float[2];
   private final Vector2 position = new Vector2();

   public CircleShape() {
      this.addr = this.newCircleShape();
   }

   private native long newCircleShape();

   protected CircleShape(long addr) {
      this.addr = addr;
   }

   public Shape.Type getType() {
      return Shape.Type.Circle;
   }

   public Vector2 getPosition() {
      this.jniGetPosition(this.addr, this.tmp);
      this.position.x = this.tmp[0];
      this.position.y = this.tmp[1];
      return this.position;
   }

   private native void jniGetPosition(long var1, float[] var3);

   public void setPosition(Vector2 position) {
      this.jniSetPosition(this.addr, position.x, position.y);
   }

   private native void jniSetPosition(long var1, float var3, float var4);
}
