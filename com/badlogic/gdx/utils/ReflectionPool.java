package com.badlogic.gdx.utils;

import java.lang.reflect.Constructor;

public class ReflectionPool<T> extends Pool<T> {
   private final Class<T> type;

   public ReflectionPool(Class<T> type) {
      this.type = type;
   }

   public ReflectionPool(Class<T> type, int initialCapacity, int max) {
      super(initialCapacity, max);
      this.type = type;
   }

   public ReflectionPool(Class<T> type, int initialCapacity) {
      super(initialCapacity);
      this.type = type;
   }

   protected T newObject() {
      try {
         return this.type.newInstance();
      } catch (Exception var8) {
         Constructor ctor;
         try {
            ctor = this.type.getConstructor((Class[])null);
         } catch (Exception var7) {
            try {
               ctor = this.type.getDeclaredConstructor((Class[])null);
               ctor.setAccessible(true);
            } catch (NoSuchMethodException var6) {
               throw new RuntimeException("Class cannot be created (missing no-arg constructor): " + this.type.getName());
            }
         }

         try {
            return ctor.newInstance();
         } catch (Exception var5) {
            throw new GdxRuntimeException("Unable to create new instance: " + this.type.getName(), var8);
         }
      }
   }
}
