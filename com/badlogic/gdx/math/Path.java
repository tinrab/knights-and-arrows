package com.badlogic.gdx.math;

public interface Path<T> {
   T valueAt(T var1, float var2);

   float approximate(T var1);

   float locate(T var1);
}
