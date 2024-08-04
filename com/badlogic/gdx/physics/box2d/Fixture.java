package com.badlogic.gdx.physics.box2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class Fixture {
   private Body body;
   protected long addr;
   protected Shape shape;
   protected Object userData;
   private final short[] tmp = new short[3];
   private final Filter filter = new Filter();

   protected Fixture(Body body, long addr) {
      this.body = body;
      this.addr = addr;
   }

   protected void reset(Body body, long addr) {
      this.body = body;
      this.addr = addr;
      this.shape = null;
      this.userData = null;
   }

   public Shape.Type getType() {
      int type = this.jniGetType(this.addr);
      switch(type) {
      case 0:
         return Shape.Type.Circle;
      case 1:
         return Shape.Type.Edge;
      case 2:
         return Shape.Type.Polygon;
      case 3:
         return Shape.Type.Chain;
      default:
         throw new GdxRuntimeException("Unknown shape type!");
      }
   }

   private native int jniGetType(long var1);

   public Shape getShape() {
      if (this.shape == null) {
         long shapeAddr = this.jniGetShape(this.addr);
         if (shapeAddr == 0L) {
            throw new GdxRuntimeException("Null shape address!");
         }

         int type = Shape.jniGetType(shapeAddr);
         switch(type) {
         case 0:
            this.shape = new CircleShape(shapeAddr);
            break;
         case 1:
            this.shape = new EdgeShape(shapeAddr);
            break;
         case 2:
            this.shape = new PolygonShape(shapeAddr);
            break;
         case 3:
            this.shape = new ChainShape(shapeAddr);
            break;
         default:
            throw new GdxRuntimeException("Unknown shape type!");
         }
      }

      return this.shape;
   }

   private native long jniGetShape(long var1);

   public void setSensor(boolean sensor) {
      this.jniSetSensor(this.addr, sensor);
   }

   private native void jniSetSensor(long var1, boolean var3);

   public boolean isSensor() {
      return this.jniIsSensor(this.addr);
   }

   private native boolean jniIsSensor(long var1);

   public void setFilterData(Filter filter) {
      this.jniSetFilterData(this.addr, filter.categoryBits, filter.maskBits, filter.groupIndex);
   }

   private native void jniSetFilterData(long var1, short var3, short var4, short var5);

   public Filter getFilterData() {
      this.jniGetFilterData(this.addr, this.tmp);
      this.filter.maskBits = this.tmp[0];
      this.filter.categoryBits = this.tmp[1];
      this.filter.groupIndex = this.tmp[2];
      return this.filter;
   }

   private native void jniGetFilterData(long var1, short[] var3);

   public void refilter() {
      this.jniRefilter(this.addr);
   }

   private native void jniRefilter(long var1);

   public Body getBody() {
      return this.body;
   }

   public boolean testPoint(Vector2 p) {
      return this.jniTestPoint(this.addr, p.x, p.y);
   }

   public boolean testPoint(float x, float y) {
      return this.jniTestPoint(this.addr, x, y);
   }

   private native boolean jniTestPoint(long var1, float var3, float var4);

   public void setDensity(float density) {
      this.jniSetDensity(this.addr, density);
   }

   private native void jniSetDensity(long var1, float var3);

   public float getDensity() {
      return this.jniGetDensity(this.addr);
   }

   private native float jniGetDensity(long var1);

   public float getFriction() {
      return this.jniGetFriction(this.addr);
   }

   private native float jniGetFriction(long var1);

   public void setFriction(float friction) {
      this.jniSetFriction(this.addr, friction);
   }

   private native void jniSetFriction(long var1, float var3);

   public float getRestitution() {
      return this.jniGetRestitution(this.addr);
   }

   private native float jniGetRestitution(long var1);

   public void setRestitution(float restitution) {
      this.jniSetRestitution(this.addr, restitution);
   }

   private native void jniSetRestitution(long var1, float var3);

   public void setUserData(Object userData) {
      this.userData = userData;
   }

   public Object getUserData() {
      return this.userData;
   }
}
