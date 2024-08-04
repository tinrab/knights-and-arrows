package com.badlogic.gdx.utils;

public class Pools {
   private static final ObjectMap<Class, ReflectionPool> typePools = new ObjectMap();

   public static <T> Pool<T> get(Class<T> type) {
      ReflectionPool pool = (ReflectionPool)typePools.get(type);
      if (pool == null) {
         pool = new ReflectionPool(type, 4, 100);
         typePools.put(type, pool);
      }

      return pool;
   }

   public static <T> T obtain(Class<T> type) {
      return get(type).obtain();
   }

   public static void free(Object object) {
      if (object == null) {
         throw new IllegalArgumentException("object cannot be null.");
      } else {
         ReflectionPool pool = (ReflectionPool)typePools.get(object.getClass());
         if (pool != null) {
            pool.free(object);
         }
      }
   }

   public static void freeAll(Array objects) {
      if (objects == null) {
         throw new IllegalArgumentException("objects cannot be null.");
      } else {
         int i = 0;

         for(int n = objects.size; i < n; ++i) {
            Object object = objects.get(i);
            ReflectionPool pool = (ReflectionPool)typePools.get(object.getClass());
            if (pool == null) {
               return;
            }

            pool.free(object);
         }

      }
   }

   private Pools() {
   }
}
