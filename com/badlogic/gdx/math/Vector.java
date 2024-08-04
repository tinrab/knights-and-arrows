package com.badlogic.gdx.math;

public interface Vector<T extends Vector> {
   T cpy();

   float len();

   float len2();

   T limit(float var1);

   T clamp(float var1, float var2);

   T set(T var1);

   T sub(T var1);

   T nor();

   T add(T var1);

   float dot(T var1);

   T scl(float var1);

   T scl(T var1);

   float dst(T var1);

   float dst2(T var1);

   T lerp(T var1, float var2);
}
