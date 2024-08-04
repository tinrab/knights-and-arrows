package com.badlogic.gdx.utils.reflect;

import java.lang.reflect.Modifier;

public final class ClassReflection {
   public static Class forName(String name) throws ReflectionException {
      try {
         return Class.forName(name);
      } catch (ClassNotFoundException var2) {
         throw new ReflectionException("Class not found: " + name, var2);
      }
   }

   public static String getSimpleName(Class c) {
      return c.getSimpleName();
   }

   public static boolean isInstance(Class c, Object obj) {
      return c.isInstance(obj);
   }

   public static boolean isAssignableFrom(Class c1, Class c2) {
      return c1.isAssignableFrom(c2);
   }

   public static boolean isMemberClass(Class c) {
      return c.isMemberClass();
   }

   public static boolean isStaticClass(Class c) {
      return Modifier.isStatic(c.getModifiers());
   }

   public static <T> T newInstance(Class<T> c) throws ReflectionException {
      try {
         return c.newInstance();
      } catch (InstantiationException var2) {
         throw new ReflectionException("Could not instantiate instance of class: " + c.getName(), var2);
      } catch (IllegalAccessException var3) {
         throw new ReflectionException("Could not instantiate instance of class: " + c.getName(), var3);
      }
   }

   public static Constructor[] getConstructors(Class c) {
      java.lang.reflect.Constructor[] constructors = c.getConstructors();
      Constructor[] result = new Constructor[constructors.length];
      int i = 0;

      for(int j = constructors.length; i < j; ++i) {
         result[i] = new Constructor(constructors[i]);
      }

      return result;
   }

   public static Constructor getConstructor(Class c, Class... parameterTypes) throws ReflectionException {
      try {
         return new Constructor(c.getConstructor(parameterTypes));
      } catch (SecurityException var3) {
         throw new ReflectionException("Security violation occurred while getting constructor for class: '" + c.getName() + "'.", var3);
      } catch (NoSuchMethodException var4) {
         throw new ReflectionException("Constructor not found for class: " + c.getName(), var4);
      }
   }

   public static Constructor getDeclaredConstructor(Class c, Class... parameterTypes) throws ReflectionException {
      try {
         return new Constructor(c.getDeclaredConstructor(parameterTypes));
      } catch (SecurityException var3) {
         throw new ReflectionException("Security violation while getting constructor for class: " + c.getName(), var3);
      } catch (NoSuchMethodException var4) {
         throw new ReflectionException("Constructor not found for class: " + c.getName(), var4);
      }
   }

   public static Method[] getMethods(Class c) {
      java.lang.reflect.Method[] methods = c.getMethods();
      Method[] result = new Method[methods.length];
      int i = 0;

      for(int j = methods.length; i < j; ++i) {
         result[i] = new Method(methods[i]);
      }

      return result;
   }

   public static Method getMethod(Class c, String name, Class... parameterTypes) throws ReflectionException {
      try {
         return new Method(c.getMethod(name, parameterTypes));
      } catch (SecurityException var4) {
         throw new ReflectionException("Security violation while getting method: " + name + ", for class: " + c.getName(), var4);
      } catch (NoSuchMethodException var5) {
         throw new ReflectionException("Method not found: " + name + ", for class: " + c.getName(), var5);
      }
   }

   public static Method[] getDeclaredMethods(Class c) {
      java.lang.reflect.Method[] methods = c.getDeclaredMethods();
      Method[] result = new Method[methods.length];
      int i = 0;

      for(int j = methods.length; i < j; ++i) {
         result[i] = new Method(methods[i]);
      }

      return result;
   }

   public static Method getDeclaredMethod(Class c, String name, Class... parameterTypes) throws ReflectionException {
      try {
         return new Method(c.getDeclaredMethod(name, parameterTypes));
      } catch (SecurityException var4) {
         throw new ReflectionException("Security violation while getting method: " + name + ", for class: " + c.getName(), var4);
      } catch (NoSuchMethodException var5) {
         throw new ReflectionException("Method not found: " + name + ", for class: " + c.getName(), var5);
      }
   }

   public static Field[] getFields(Class c) {
      java.lang.reflect.Field[] fields = c.getFields();
      Field[] result = new Field[fields.length];
      int i = 0;

      for(int j = fields.length; i < j; ++i) {
         result[i] = new Field(fields[i]);
      }

      return result;
   }

   public static Field getField(Class c, String name) throws ReflectionException {
      try {
         return new Field(c.getField(name));
      } catch (SecurityException var3) {
         throw new ReflectionException("Security violation while getting field: " + name + ", for class: " + c.getName(), var3);
      } catch (NoSuchFieldException var4) {
         throw new ReflectionException("Field not found: " + name + ", for class: " + c.getName(), var4);
      }
   }

   public static Field[] getDeclaredFields(Class c) {
      java.lang.reflect.Field[] fields = c.getDeclaredFields();
      Field[] result = new Field[fields.length];
      int i = 0;

      for(int j = fields.length; i < j; ++i) {
         result[i] = new Field(fields[i]);
      }

      return result;
   }

   public static Field getDeclaredField(Class c, String name) throws ReflectionException {
      try {
         return new Field(c.getDeclaredField(name));
      } catch (SecurityException var3) {
         throw new ReflectionException("Security violation while getting field: " + name + ", for class: " + c.getName(), var3);
      } catch (NoSuchFieldException var4) {
         throw new ReflectionException("Field not found: " + name + ", for class: " + c.getName(), var4);
      }
   }
}
