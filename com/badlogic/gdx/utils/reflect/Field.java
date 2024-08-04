package com.badlogic.gdx.utils.reflect;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public final class Field {
   private final java.lang.reflect.Field field;

   Field(java.lang.reflect.Field field) {
      this.field = field;
   }

   public String getName() {
      return this.field.getName();
   }

   public Class getType() {
      return this.field.getType();
   }

   public Class getDeclaringClass() {
      return this.field.getDeclaringClass();
   }

   public boolean isAccessible() {
      return this.field.isAccessible();
   }

   public void setAccessible(boolean accessible) {
      this.field.setAccessible(accessible);
   }

   public boolean isDefaultAccess() {
      return !this.isPrivate() && !this.isProtected() && !this.isPublic();
   }

   public boolean isFinal() {
      return Modifier.isFinal(this.field.getModifiers());
   }

   public boolean isPrivate() {
      return Modifier.isPrivate(this.field.getModifiers());
   }

   public boolean isProtected() {
      return Modifier.isProtected(this.field.getModifiers());
   }

   public boolean isPublic() {
      return Modifier.isPublic(this.field.getModifiers());
   }

   public boolean isStatic() {
      return Modifier.isStatic(this.field.getModifiers());
   }

   public boolean isTransient() {
      return Modifier.isTransient(this.field.getModifiers());
   }

   public boolean isVolatile() {
      return Modifier.isVolatile(this.field.getModifiers());
   }

   public boolean isSynthetic() {
      return this.field.isSynthetic();
   }

   public Class getElementType() {
      Type genericType = this.field.getGenericType();
      if (genericType instanceof ParameterizedType) {
         Type[] actualTypes = ((ParameterizedType)genericType).getActualTypeArguments();
         if (actualTypes.length == 1) {
            Type actualType = actualTypes[0];
            if (actualType instanceof Class) {
               return (Class)actualType;
            }

            if (actualType instanceof ParameterizedType) {
               return (Class)((ParameterizedType)actualType).getRawType();
            }
         }
      }

      return null;
   }

   public Object get(Object obj) throws ReflectionException {
      try {
         return this.field.get(obj);
      } catch (IllegalArgumentException var3) {
         throw new ReflectionException("Object is not an instance of " + this.getDeclaringClass(), var3);
      } catch (IllegalAccessException var4) {
         throw new ReflectionException("Illegal access to field: " + this.getName(), var4);
      }
   }

   public void set(Object obj, Object value) throws ReflectionException {
      try {
         this.field.set(obj, value);
      } catch (IllegalArgumentException var4) {
         throw new ReflectionException("Argument not valid for field: " + this.getName(), var4);
      } catch (IllegalAccessException var5) {
         throw new ReflectionException("Illegal access to field: " + this.getName(), var5);
      }
   }
}
