package com.badlogic.gdx.utils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.reflect.ArrayReflection;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Constructor;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Json {
   private static final boolean debug = false;
   private JsonWriter writer;
   private String typeName = "class";
   private boolean usePrototypes = true;
   private JsonWriter.OutputType outputType;
   private final ObjectMap<Class, ObjectMap<String, Json.FieldMetadata>> typeToFields = new ObjectMap();
   private final ObjectMap<String, Class> tagToClass = new ObjectMap();
   private final ObjectMap<Class, String> classToTag = new ObjectMap();
   private final ObjectMap<Class, Json.Serializer> classToSerializer = new ObjectMap();
   private final ObjectMap<Class, Object[]> classToDefaultValues = new ObjectMap();
   private boolean ignoreUnknownFields;

   public Json() {
      this.outputType = JsonWriter.OutputType.minimal;
   }

   public Json(JsonWriter.OutputType outputType) {
      this.outputType = outputType;
   }

   public void setIgnoreUnknownFields(boolean ignoreUnknownFields) {
      this.ignoreUnknownFields = ignoreUnknownFields;
   }

   public void setOutputType(JsonWriter.OutputType outputType) {
      this.outputType = outputType;
   }

   public void addClassTag(String tag, Class type) {
      this.tagToClass.put(tag, type);
      this.classToTag.put(type, tag);
   }

   public Class getClass(String tag) {
      Class type = (Class)this.tagToClass.get(tag);
      if (type != null) {
         return type;
      } else {
         try {
            return ClassReflection.forName(tag);
         } catch (ReflectionException var4) {
            throw new SerializationException(var4);
         }
      }
   }

   public String getTag(Class type) {
      String tag = (String)this.classToTag.get(type);
      return tag != null ? tag : type.getName();
   }

   public void setTypeName(String typeName) {
      this.typeName = typeName;
   }

   public <T> void setSerializer(Class<T> type, Json.Serializer<T> serializer) {
      this.classToSerializer.put(type, serializer);
   }

   public <T> Json.Serializer<T> getSerializer(Class<T> type) {
      return (Json.Serializer)this.classToSerializer.get(type);
   }

   public void setUsePrototypes(boolean usePrototypes) {
      this.usePrototypes = usePrototypes;
   }

   public void setElementType(Class type, String fieldName, Class elementType) {
      ObjectMap<String, Json.FieldMetadata> fields = (ObjectMap)this.typeToFields.get(type);
      if (fields == null) {
         fields = this.cacheFields(type);
      }

      Json.FieldMetadata metadata = (Json.FieldMetadata)fields.get(fieldName);
      if (metadata == null) {
         throw new SerializationException("Field not found: " + fieldName + " (" + type.getName() + ")");
      } else {
         metadata.elementType = elementType;
      }
   }

   private ObjectMap<String, Json.FieldMetadata> cacheFields(Class type) {
      ArrayList<Field> allFields = new ArrayList();

      for(Class nextClass = type; nextClass != Object.class; nextClass = nextClass.getSuperclass()) {
         Collections.addAll(allFields, ClassReflection.getDeclaredFields(nextClass));
      }

      ObjectMap<String, Json.FieldMetadata> nameToField = new ObjectMap();
      int i = 0;

      for(int n = allFields.size(); i < n; ++i) {
         Field field = (Field)allFields.get(i);
         if (!field.isTransient() && !field.isStatic() && !field.isSynthetic()) {
            if (!field.isAccessible()) {
               try {
                  field.setAccessible(true);
               } catch (AccessControlException var9) {
                  continue;
               }
            }

            nameToField.put(field.getName(), new Json.FieldMetadata(field));
         }
      }

      this.typeToFields.put(type, nameToField);
      return nameToField;
   }

   public String toJson(Object object) {
      return this.toJson(object, object == null ? null : object.getClass(), (Class)null);
   }

   public String toJson(Object object, Class knownType) {
      return this.toJson(object, knownType, (Class)null);
   }

   public String toJson(Object object, Class knownType, Class elementType) {
      StringWriter buffer = new StringWriter();
      this.toJson(object, knownType, elementType, (Writer)buffer);
      return buffer.toString();
   }

   public void toJson(Object object, FileHandle file) {
      this.toJson(object, object == null ? null : object.getClass(), (Class)null, (FileHandle)file);
   }

   public void toJson(Object object, Class knownType, FileHandle file) {
      this.toJson(object, knownType, (Class)null, (FileHandle)file);
   }

   public void toJson(Object object, Class knownType, Class elementType, FileHandle file) {
      Writer writer = null;

      try {
         writer = file.writer(false);
         this.toJson(object, knownType, elementType, writer);
      } catch (Exception var14) {
         throw new SerializationException("Error writing file: " + file, var14);
      } finally {
         try {
            if (writer != null) {
               writer.close();
            }
         } catch (IOException var13) {
         }

      }

   }

   public void toJson(Object object, Writer writer) {
      this.toJson(object, object == null ? null : object.getClass(), (Class)null, (Writer)writer);
   }

   public void toJson(Object object, Class knownType, Writer writer) {
      this.toJson(object, knownType, (Class)null, (Writer)writer);
   }

   public void toJson(Object object, Class knownType, Class elementType, Writer writer) {
      this.setWriter(writer);

      try {
         this.writeValue(object, knownType, elementType);
      } finally {
         try {
            this.writer.close();
         } catch (Exception var10) {
         }

         this.writer = null;
      }

   }

   public void setWriter(Writer writer) {
      if (!(writer instanceof JsonWriter)) {
         writer = new JsonWriter((Writer)writer);
      }

      this.writer = (JsonWriter)writer;
      this.writer.setOutputType(this.outputType);
   }

   public JsonWriter getWriter() {
      return this.writer;
   }

   public void writeFields(Object object) {
      Class type = object.getClass();
      Object[] defaultValues = this.getDefaultValues(type);
      ObjectMap<String, Json.FieldMetadata> fields = (ObjectMap)this.typeToFields.get(type);
      if (fields == null) {
         fields = this.cacheFields(type);
      }

      int i = 0;
      Iterator var7 = (new ObjectMap.Values(fields)).iterator();

      while(var7.hasNext()) {
         Json.FieldMetadata metadata = (Json.FieldMetadata)var7.next();
         Field field = metadata.field;

         try {
            Object value = field.get(object);
            if (defaultValues != null) {
               Object defaultValue = defaultValues[i++];
               if (value == null && defaultValue == null || value != null && defaultValue != null && value.equals(defaultValue)) {
                  continue;
               }
            }

            this.writer.name(field.getName());
            this.writeValue(value, field.getType(), metadata.elementType);
         } catch (ReflectionException var11) {
            throw new SerializationException("Error accessing field: " + field.getName() + " (" + type.getName() + ")", var11);
         } catch (SerializationException var12) {
            var12.addTrace(field + " (" + type.getName() + ")");
            throw var12;
         } catch (Exception var13) {
            SerializationException ex = new SerializationException(var13);
            ex.addTrace(field + " (" + type.getName() + ")");
            throw ex;
         }
      }

   }

   private Object[] getDefaultValues(Class type) {
      if (!this.usePrototypes) {
         return null;
      } else if (this.classToDefaultValues.containsKey(type)) {
         return (Object[])this.classToDefaultValues.get(type);
      } else {
         Object object;
         try {
            object = this.newInstance(type);
         } catch (Exception var14) {
            this.classToDefaultValues.put(type, (Object)null);
            return null;
         }

         ObjectMap<String, Json.FieldMetadata> fields = (ObjectMap)this.typeToFields.get(type);
         if (fields == null) {
            fields = this.cacheFields(type);
         }

         Object[] values = new Object[fields.size];
         this.classToDefaultValues.put(type, values);
         int i = 0;
         Iterator var7 = fields.values().iterator();

         while(var7.hasNext()) {
            Json.FieldMetadata metadata = (Json.FieldMetadata)var7.next();
            Field field = metadata.field;

            try {
               values[i++] = field.get(object);
            } catch (ReflectionException var11) {
               throw new SerializationException("Error accessing field: " + field.getName() + " (" + type.getName() + ")", var11);
            } catch (SerializationException var12) {
               var12.addTrace(field + " (" + type.getName() + ")");
               throw var12;
            } catch (RuntimeException var13) {
               SerializationException ex = new SerializationException(var13);
               ex.addTrace(field + " (" + type.getName() + ")");
               throw ex;
            }
         }

         return values;
      }
   }

   public void writeField(Object object, String name) {
      this.writeField(object, name, name, (Class)null);
   }

   public void writeField(Object object, String name, Class elementType) {
      this.writeField(object, name, name, elementType);
   }

   public void writeField(Object object, String fieldName, String jsonName) {
      this.writeField(object, fieldName, jsonName, (Class)null);
   }

   public void writeField(Object object, String fieldName, String jsonName, Class elementType) {
      Class type = object.getClass();
      ObjectMap<String, Json.FieldMetadata> fields = (ObjectMap)this.typeToFields.get(type);
      if (fields == null) {
         fields = this.cacheFields(type);
      }

      Json.FieldMetadata metadata = (Json.FieldMetadata)fields.get(fieldName);
      if (metadata == null) {
         throw new SerializationException("Field not found: " + fieldName + " (" + type.getName() + ")");
      } else {
         Field field = metadata.field;
         if (elementType == null) {
            elementType = metadata.elementType;
         }

         try {
            this.writer.name(jsonName);
            this.writeValue(field.get(object), field.getType(), elementType);
         } catch (ReflectionException var11) {
            throw new SerializationException("Error accessing field: " + field.getName() + " (" + type.getName() + ")", var11);
         } catch (SerializationException var12) {
            var12.addTrace(field + " (" + type.getName() + ")");
            throw var12;
         } catch (Exception var13) {
            SerializationException ex = new SerializationException(var13);
            ex.addTrace(field + " (" + type.getName() + ")");
            throw ex;
         }
      }
   }

   public void writeValue(String name, Object value) {
      try {
         this.writer.name(name);
      } catch (IOException var4) {
         throw new SerializationException(var4);
      }

      if (value == null) {
         this.writeValue((Object)value, (Class)null, (Class)null);
      } else {
         this.writeValue((Object)value, (Class)value.getClass(), (Class)null);
      }

   }

   public void writeValue(String name, Object value, Class knownType) {
      try {
         this.writer.name(name);
      } catch (IOException var5) {
         throw new SerializationException(var5);
      }

      this.writeValue((Object)value, (Class)knownType, (Class)null);
   }

   public void writeValue(String name, Object value, Class knownType, Class elementType) {
      try {
         this.writer.name(name);
      } catch (IOException var6) {
         throw new SerializationException(var6);
      }

      this.writeValue(value, knownType, elementType);
   }

   public void writeValue(Object value) {
      if (value == null) {
         this.writeValue((Object)value, (Class)null, (Class)null);
      } else {
         this.writeValue((Object)value, (Class)value.getClass(), (Class)null);
      }

   }

   public void writeValue(Object value, Class knownType) {
      this.writeValue((Object)value, (Class)knownType, (Class)null);
   }

   public void writeValue(Object value, Class knownType, Class elementType) {
      try {
         if (value == null) {
            this.writer.value((Object)null);
         } else if ((knownType == null || !knownType.isPrimitive()) && knownType != String.class && knownType != Integer.class && knownType != Boolean.class && knownType != Float.class && knownType != Long.class && knownType != Double.class && knownType != Short.class && knownType != Byte.class && knownType != Character.class) {
            Class actualType = value.getClass();
            if (!actualType.isPrimitive() && actualType != String.class && actualType != Integer.class && actualType != Boolean.class && actualType != Float.class && actualType != Long.class && actualType != Double.class && actualType != Short.class && actualType != Byte.class && actualType != Character.class) {
               if (value instanceof Json.Serializable) {
                  this.writeObjectStart(actualType, knownType);
                  ((Json.Serializable)value).write(this);
                  this.writeObjectEnd();
               } else {
                  Json.Serializer serializer = (Json.Serializer)this.classToSerializer.get(actualType);
                  if (serializer != null) {
                     serializer.write(this, value, knownType);
                  } else {
                     int n;
                     int i;
                     if (value instanceof Array) {
                        if (knownType != null && actualType != knownType && actualType != Array.class) {
                           throw new SerializationException("Serialization of an Array other than the known type is not supported.\nKnown type: " + knownType + "\nActual type: " + actualType);
                        } else {
                           this.writeArrayStart();
                           Array array = (Array)value;
                           i = 0;

                           for(n = array.size; i < n; ++i) {
                              this.writeValue((Object)array.get(i), (Class)elementType, (Class)null);
                           }

                           this.writeArrayEnd();
                        }
                     } else {
                        Iterator var7;
                        if (value instanceof Collection) {
                           if (knownType != null && actualType != knownType && actualType != ArrayList.class) {
                              throw new SerializationException("Serialization of a Collection other than the known type is not supported.\nKnown type: " + knownType + "\nActual type: " + actualType);
                           } else {
                              this.writeArrayStart();
                              var7 = ((Collection)value).iterator();

                              while(var7.hasNext()) {
                                 Object item = var7.next();
                                 this.writeValue((Object)item, (Class)elementType, (Class)null);
                              }

                              this.writeArrayEnd();
                           }
                        } else if (actualType.isArray()) {
                           if (elementType == null) {
                              elementType = actualType.getComponentType();
                           }

                           int length = ArrayReflection.getLength(value);
                           this.writeArrayStart();

                           for(i = 0; i < length; ++i) {
                              this.writeValue((Object)ArrayReflection.get(value, i), (Class)elementType, (Class)null);
                           }

                           this.writeArrayEnd();
                        } else if (value instanceof OrderedMap) {
                           if (knownType == null) {
                              knownType = OrderedMap.class;
                           }

                           this.writeObjectStart(actualType, knownType);
                           OrderedMap map = (OrderedMap)value;
                           Iterator var17 = map.orderedKeys().iterator();

                           while(var17.hasNext()) {
                              Object key = var17.next();
                              this.writer.name(this.convertToString(key));
                              this.writeValue((Object)map.get(key), (Class)elementType, (Class)null);
                           }

                           this.writeObjectEnd();
                        } else if (value instanceof ArrayMap) {
                           if (knownType == null) {
                              knownType = ArrayMap.class;
                           }

                           this.writeObjectStart(actualType, knownType);
                           ArrayMap map = (ArrayMap)value;
                           i = 0;

                           for(n = map.size; i < n; ++i) {
                              this.writer.name(this.convertToString(map.keys[i]));
                              this.writeValue((Object)map.values[i], (Class)elementType, (Class)null);
                           }

                           this.writeObjectEnd();
                        } else if (value instanceof ObjectMap) {
                           if (knownType == null) {
                              knownType = OrderedMap.class;
                           }

                           this.writeObjectStart(actualType, knownType);
                           var7 = ((ObjectMap)value).entries().iterator();

                           while(var7.hasNext()) {
                              ObjectMap.Entry entry = (ObjectMap.Entry)var7.next();
                              this.writer.name(this.convertToString(entry.key));
                              this.writeValue((Object)entry.value, (Class)elementType, (Class)null);
                           }

                           this.writeObjectEnd();
                        } else if (!(value instanceof Map)) {
                           if (ClassReflection.isAssignableFrom(Enum.class, actualType)) {
                              this.writer.value(value);
                           } else {
                              this.writeObjectStart(actualType, knownType);
                              this.writeFields(value);
                              this.writeObjectEnd();
                           }
                        } else {
                           if (knownType == null) {
                              knownType = HashMap.class;
                           }

                           this.writeObjectStart(actualType, knownType);
                           var7 = ((Map)value).entrySet().iterator();

                           while(var7.hasNext()) {
                              Entry entry = (Entry)var7.next();
                              this.writer.name(this.convertToString(entry.getKey()));
                              this.writeValue((Object)entry.getValue(), (Class)elementType, (Class)null);
                           }

                           this.writeObjectEnd();
                        }
                     }
                  }
               }
            } else {
               this.writeObjectStart(actualType, (Class)null);
               this.writeValue("value", value);
               this.writeObjectEnd();
            }
         } else {
            this.writer.value(value);
         }
      } catch (IOException var9) {
         throw new SerializationException(var9);
      }
   }

   public void writeObjectStart(String name) {
      try {
         this.writer.name(name);
      } catch (IOException var3) {
         throw new SerializationException(var3);
      }

      this.writeObjectStart();
   }

   public void writeObjectStart(String name, Class actualType, Class knownType) {
      try {
         this.writer.name(name);
      } catch (IOException var5) {
         throw new SerializationException(var5);
      }

      this.writeObjectStart(actualType, knownType);
   }

   public void writeObjectStart() {
      try {
         this.writer.object();
      } catch (IOException var2) {
         throw new SerializationException(var2);
      }
   }

   public void writeObjectStart(Class actualType, Class knownType) {
      try {
         this.writer.object();
      } catch (IOException var4) {
         throw new SerializationException(var4);
      }

      if (knownType == null || knownType != actualType) {
         this.writeType(actualType);
      }

   }

   public void writeObjectEnd() {
      try {
         this.writer.pop();
      } catch (IOException var2) {
         throw new SerializationException(var2);
      }
   }

   public void writeArrayStart(String name) {
      try {
         this.writer.name(name);
         this.writer.array();
      } catch (IOException var3) {
         throw new SerializationException(var3);
      }
   }

   public void writeArrayStart() {
      try {
         this.writer.array();
      } catch (IOException var2) {
         throw new SerializationException(var2);
      }
   }

   public void writeArrayEnd() {
      try {
         this.writer.pop();
      } catch (IOException var2) {
         throw new SerializationException(var2);
      }
   }

   public void writeType(Class type) {
      if (this.typeName != null) {
         String className = (String)this.classToTag.get(type);
         if (className == null) {
            className = type.getName();
         }

         try {
            this.writer.set(this.typeName, className);
         } catch (IOException var4) {
            throw new SerializationException(var4);
         }
      }
   }

   public <T> T fromJson(Class<T> type, Reader reader) {
      return this.readValue((Class)type, (Class)null, (new JsonReader()).parse(reader));
   }

   public <T> T fromJson(Class<T> type, Class elementType, Reader reader) {
      return this.readValue(type, elementType, (new JsonReader()).parse(reader));
   }

   public <T> T fromJson(Class<T> type, InputStream input) {
      return this.readValue((Class)type, (Class)null, (new JsonReader()).parse(input));
   }

   public <T> T fromJson(Class<T> type, Class elementType, InputStream input) {
      return this.readValue(type, elementType, (new JsonReader()).parse(input));
   }

   public <T> T fromJson(Class<T> type, FileHandle file) {
      try {
         return this.readValue((Class)type, (Class)null, (new JsonReader()).parse(file));
      } catch (Exception var4) {
         throw new SerializationException("Error reading file: " + file, var4);
      }
   }

   public <T> T fromJson(Class<T> type, Class elementType, FileHandle file) {
      try {
         return this.readValue(type, elementType, (new JsonReader()).parse(file));
      } catch (Exception var5) {
         throw new SerializationException("Error reading file: " + file, var5);
      }
   }

   public <T> T fromJson(Class<T> type, char[] data, int offset, int length) {
      return this.readValue((Class)type, (Class)null, (new JsonReader()).parse(data, offset, length));
   }

   public <T> T fromJson(Class<T> type, Class elementType, char[] data, int offset, int length) {
      return this.readValue(type, elementType, (new JsonReader()).parse(data, offset, length));
   }

   public <T> T fromJson(Class<T> type, String json) {
      return this.readValue((Class)type, (Class)null, (new JsonReader()).parse(json));
   }

   public <T> T fromJson(Class<T> type, Class elementType, String json) {
      return this.readValue(type, elementType, (new JsonReader()).parse(json));
   }

   public void readField(Object object, String name, JsonValue jsonData) {
      this.readField(object, name, name, (Class)null, jsonData);
   }

   public void readField(Object object, String name, Class elementType, JsonValue jsonData) {
      this.readField(object, name, name, elementType, jsonData);
   }

   public void readField(Object object, String fieldName, String jsonName, JsonValue jsonData) {
      this.readField(object, fieldName, jsonName, (Class)null, jsonData);
   }

   public void readField(Object object, String fieldName, String jsonName, Class elementType, JsonValue jsonMap) {
      Class type = object.getClass();
      ObjectMap<String, Json.FieldMetadata> fields = (ObjectMap)this.typeToFields.get(type);
      if (fields == null) {
         fields = this.cacheFields(type);
      }

      Json.FieldMetadata metadata = (Json.FieldMetadata)fields.get(fieldName);
      if (metadata == null) {
         throw new SerializationException("Field not found: " + fieldName + " (" + type.getName() + ")");
      } else {
         Field field = metadata.field;
         JsonValue jsonValue = jsonMap.get(jsonName);
         if (jsonValue != null) {
            if (elementType == null) {
               elementType = metadata.elementType;
            }

            try {
               field.set(object, this.readValue(field.getType(), elementType, jsonValue));
            } catch (ReflectionException var13) {
               throw new SerializationException("Error accessing field: " + field.getName() + " (" + type.getName() + ")", var13);
            } catch (SerializationException var14) {
               var14.addTrace(field.getName() + " (" + type.getName() + ")");
               throw var14;
            } catch (RuntimeException var15) {
               SerializationException ex = new SerializationException(var15);
               ex.addTrace(field.getName() + " (" + type.getName() + ")");
               throw ex;
            }
         }
      }
   }

   public void readFields(Object object, JsonValue jsonMap) {
      Class type = object.getClass();
      ObjectMap<String, Json.FieldMetadata> fields = (ObjectMap)this.typeToFields.get(type);
      if (fields == null) {
         fields = this.cacheFields(type);
      }

      for(JsonValue child = jsonMap.child(); child != null; child = child.next()) {
         Json.FieldMetadata metadata = (Json.FieldMetadata)fields.get(child.name());
         if (metadata == null) {
            if (!this.ignoreUnknownFields) {
               throw new SerializationException("Field not found: " + child.name() + " (" + type.getName() + ")");
            }
         } else {
            Field field = metadata.field;

            try {
               field.set(object, this.readValue(field.getType(), metadata.elementType, child));
            } catch (ReflectionException var10) {
               throw new SerializationException("Error accessing field: " + field.getName() + " (" + type.getName() + ")", var10);
            } catch (SerializationException var11) {
               var11.addTrace(field.getName() + " (" + type.getName() + ")");
               throw var11;
            } catch (RuntimeException var12) {
               SerializationException ex = new SerializationException(var12);
               ex.addTrace(field.getName() + " (" + type.getName() + ")");
               throw ex;
            }
         }
      }

   }

   public <T> T readValue(String name, Class<T> type, JsonValue jsonMap) {
      return this.readValue((Class)type, (Class)null, jsonMap.get(name));
   }

   public <T> T readValue(String name, Class<T> type, T defaultValue, JsonValue jsonMap) {
      JsonValue jsonValue = jsonMap.get(name);
      return jsonValue == null ? defaultValue : this.readValue((Class)type, (Class)null, jsonValue);
   }

   public <T> T readValue(String name, Class<T> type, Class elementType, JsonValue jsonMap) {
      return this.readValue(type, elementType, jsonMap.get(name));
   }

   public <T> T readValue(String name, Class<T> type, Class elementType, T defaultValue, JsonValue jsonMap) {
      JsonValue jsonValue = jsonMap.get(name);
      return jsonValue == null ? defaultValue : this.readValue(type, elementType, jsonValue);
   }

   public <T> T readValue(Class<T> type, Class elementType, T defaultValue, JsonValue jsonData) {
      return this.readValue(type, elementType, jsonData);
   }

   public <T> T readValue(Class<T> type, JsonValue jsonData) {
      return this.readValue((Class)type, (Class)null, jsonData);
   }

   public <T> T readValue(Class<T> type, Class elementType, JsonValue jsonData) {
      if (jsonData == null) {
         return null;
      } else {
         String string;
         Object newArray;
         JsonValue child;
         if (jsonData.isObject()) {
            string = this.typeName == null ? null : jsonData.getString(this.typeName, (String)null);
            if (string != null) {
               jsonData.remove(this.typeName);

               try {
                  type = ClassReflection.forName(string);
               } catch (ReflectionException var9) {
                  type = (Class)this.tagToClass.get(string);
                  if (type == null) {
                     throw new SerializationException(var9);
                  }
               }
            }

            if (type != String.class && type != Integer.class && type != Boolean.class && type != Float.class && type != Long.class && type != Double.class && type != Short.class && type != Byte.class && type != Character.class) {
               if (type == null) {
                  return jsonData;
               } else {
                  Json.Serializer serializer = (Json.Serializer)this.classToSerializer.get(type);
                  if (serializer != null) {
                     return serializer.read(this, jsonData, type);
                  } else {
                     newArray = this.newInstance(type);
                     if (newArray instanceof Json.Serializable) {
                        ((Json.Serializable)newArray).read(this, jsonData);
                        return newArray;
                     } else if (newArray instanceof HashMap) {
                        HashMap result = (HashMap)newArray;

                        for(JsonValue child = jsonData.child(); child != null; child = child.next()) {
                           result.put(child.name(), this.readValue((Class)elementType, (Class)null, child));
                        }

                        return result;
                     } else if (!(newArray instanceof ObjectMap)) {
                        this.readFields(newArray, jsonData);
                        return newArray;
                     } else {
                        ObjectMap result = (ObjectMap)newArray;

                        for(child = jsonData.child(); child != null; child = child.next()) {
                           result.put(child.name(), this.readValue((Class)elementType, (Class)null, child));
                        }

                        return result;
                     }
                  }
               }
            } else {
               return this.readValue("value", type, jsonData);
            }
         } else {
            if (type != null) {
               Json.Serializer serializer = (Json.Serializer)this.classToSerializer.get(type);
               if (serializer != null) {
                  return serializer.read(this, jsonData, type);
               }
            }

            int i;
            if (jsonData.isArray()) {
               JsonValue child;
               if (type != null && !ClassReflection.isAssignableFrom(Array.class, type)) {
                  if (ClassReflection.isAssignableFrom(List.class, type)) {
                     List newArray = type == null ? new ArrayList() : (List)this.newInstance(type);

                     for(child = jsonData.child(); child != null; child = child.next()) {
                        ((List)newArray).add(this.readValue((Class)elementType, (Class)null, child));
                     }

                     return newArray;
                  } else if (!type.isArray()) {
                     throw new SerializationException("Unable to convert value to required type: " + jsonData + " (" + type.getName() + ")");
                  } else {
                     Class componentType = type.getComponentType();
                     if (elementType == null) {
                        elementType = componentType;
                     }

                     newArray = ArrayReflection.newInstance(componentType, jsonData.size());
                     i = 0;

                     for(child = jsonData.child(); child != null; child = child.next()) {
                        ArrayReflection.set(newArray, i++, this.readValue((Class)elementType, (Class)null, child));
                     }

                     return newArray;
                  }
               } else {
                  Array newArray = type == null ? new Array() : (Array)this.newInstance(type);

                  for(child = jsonData.child(); child != null; child = child.next()) {
                     newArray.add(this.readValue((Class)elementType, (Class)null, child));
                  }

                  return newArray;
               }
            } else {
               if (jsonData.isNumber()) {
                  try {
                     label336: {
                        if (type != null && type != Float.TYPE && type != Float.class) {
                           if (type != Integer.TYPE && type != Integer.class) {
                              if (type != Long.TYPE && type != Long.class) {
                                 if (type != Double.TYPE && type != Double.class) {
                                    if (type == String.class) {
                                       return Float.toString(jsonData.asFloat());
                                    }

                                    if (type != Short.TYPE && type != Short.class) {
                                       if (type != Byte.TYPE && type != Byte.class) {
                                          break label336;
                                       }

                                       return (byte)jsonData.asInt();
                                    }

                                    return (short)jsonData.asInt();
                                 }

                                 return (double)jsonData.asFloat();
                              }

                              return jsonData.asLong();
                           }

                           return jsonData.asInt();
                        }

                        return jsonData.asFloat();
                     }
                  } catch (NumberFormatException var12) {
                  }

                  jsonData = new JsonValue(jsonData.asString());
               }

               if (jsonData.isBoolean()) {
                  try {
                     if (type == null || type == Boolean.TYPE || type == Boolean.class) {
                        return jsonData.asBoolean();
                     }
                  } catch (NumberFormatException var11) {
                  }

                  jsonData = new JsonValue(jsonData.asString());
               }

               if (!jsonData.isString()) {
                  return null;
               } else {
                  string = jsonData.asString();
                  if (type != null && type != String.class) {
                     try {
                        label385: {
                           if (type != Integer.TYPE && type != Integer.class) {
                              if (type != Float.TYPE && type != Float.class) {
                                 if (type != Long.TYPE && type != Long.class) {
                                    if (type != Double.TYPE && type != Double.class) {
                                       if (type == Short.TYPE || type == Short.class) {
                                          return Short.valueOf(string);
                                       }

                                       if (type == Byte.TYPE || type == Byte.class) {
                                          return Byte.valueOf(string);
                                       }
                                       break label385;
                                    }

                                    return Double.valueOf(string);
                                 }

                                 return Long.valueOf(string);
                              }

                              return Float.valueOf(string);
                           }

                           return Integer.valueOf(string);
                        }
                     } catch (NumberFormatException var10) {
                     }

                     if (type != Boolean.TYPE && type != Boolean.class) {
                        if (type != Character.TYPE && type != Character.class) {
                           if (ClassReflection.isAssignableFrom(Enum.class, type)) {
                              Object[] constants = type.getEnumConstants();
                              i = 0;

                              for(int n = constants.length; i < n; ++i) {
                                 if (string.equals(constants[i].toString())) {
                                    return constants[i];
                                 }
                              }
                           }

                           if (type == CharSequence.class) {
                              return string;
                           } else {
                              throw new SerializationException("Unable to convert value to required type: " + jsonData + " (" + type.getName() + ")");
                           }
                        } else {
                           return string.charAt(0);
                        }
                     } else {
                        return Boolean.valueOf(string);
                     }
                  } else {
                     return string;
                  }
               }
            }
         }
      }
   }

   private String convertToString(Object object) {
      return object instanceof Class ? ((Class)object).getName() : String.valueOf(object);
   }

   private Object newInstance(Class type) {
      try {
         return ClassReflection.newInstance(type);
      } catch (Exception var7) {
         Exception ex = var7;

         try {
            Constructor constructor = ClassReflection.getDeclaredConstructor(type);
            constructor.setAccessible(true);
            return constructor.newInstance();
         } catch (SecurityException var4) {
         } catch (ReflectionException var5) {
            if (type.isArray()) {
               throw new SerializationException("Encountered JSON object when expected array of type: " + type.getName(), var7);
            }

            if (ClassReflection.isMemberClass(type) && !ClassReflection.isStaticClass(type)) {
               throw new SerializationException("Class cannot be created (non-static member class): " + type.getName(), var7);
            }

            throw new SerializationException("Class cannot be created (missing no-arg constructor): " + type.getName(), var7);
         } catch (Exception var6) {
            ex = var6;
         }

         throw new SerializationException("Error constructing instance of class: " + type.getName(), ex);
      }
   }

   public String prettyPrint(Object object) {
      return this.prettyPrint((Object)object, 0);
   }

   public String prettyPrint(String json) {
      return this.prettyPrint((String)json, 0);
   }

   public String prettyPrint(Object object, int singleLineColumns) {
      return this.prettyPrint(this.toJson(object), singleLineColumns);
   }

   public String prettyPrint(String json, int singleLineColumns) {
      return (new JsonReader()).parse(json).prettyPrint(this.outputType, singleLineColumns);
   }

   private static class FieldMetadata {
      Field field;
      Class elementType;

      public FieldMetadata(Field field) {
         this.field = field;
         this.elementType = field.getElementType();
      }
   }

   public abstract static class ReadOnlySerializer<T> implements Json.Serializer<T> {
      public void write(Json json, T object, Class knownType) {
      }

      public abstract T read(Json var1, JsonValue var2, Class var3);
   }

   public interface Serializable {
      void write(Json var1);

      void read(Json var1, JsonValue var2);
   }

   public interface Serializer<T> {
      void write(Json var1, T var2, Class var3);

      T read(Json var1, JsonValue var2, Class var3);
   }
}
