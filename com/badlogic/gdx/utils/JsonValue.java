package com.badlogic.gdx.utils;

public class JsonValue {
   private JsonValue.ValueType type;
   private String stringValue;
   private double doubleValue;
   private long longValue;
   public String name;
   public JsonValue child;
   public JsonValue next;
   public JsonValue prev;
   public int size;
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$com$badlogic$gdx$utils$JsonValue$ValueType;

   public JsonValue(JsonValue.ValueType type) {
      this.type = type;
   }

   public JsonValue(String value) {
      this.set(value);
   }

   public JsonValue(double value) {
      this.set(value);
   }

   public JsonValue(long value) {
      this.set(value);
   }

   public JsonValue(boolean value) {
      this.set(value);
   }

   public JsonValue get(int index) {
      JsonValue current;
      for(current = this.child; current != null && index > 0; current = current.next) {
         --index;
      }

      return current;
   }

   public JsonValue get(String name) {
      JsonValue current;
      for(current = this.child; current != null && !current.name.equalsIgnoreCase(name); current = current.next) {
      }

      return current;
   }

   public JsonValue require(int index) {
      JsonValue current;
      for(current = this.child; current != null && index > 0; current = current.next) {
         --index;
      }

      if (current == null) {
         throw new IllegalArgumentException("Child not found with index: " + index);
      } else {
         return current;
      }
   }

   public JsonValue require(String name) {
      JsonValue current;
      for(current = this.child; current != null && !current.name.equalsIgnoreCase(name); current = current.next) {
      }

      if (current == null) {
         throw new IllegalArgumentException("Child not found with name: " + name);
      } else {
         return current;
      }
   }

   public JsonValue remove(int index) {
      JsonValue child = this.get(index);
      if (child == null) {
         return null;
      } else {
         if (child.prev == null) {
            this.child = child.next;
            if (this.child != null) {
               this.child.prev = null;
            }
         } else {
            child.prev.next = child.next;
            if (child.next != null) {
               child.next.prev = child.prev;
            }
         }

         --this.size;
         return child;
      }
   }

   public JsonValue remove(String name) {
      JsonValue child = this.get(name);
      if (child == null) {
         return null;
      } else {
         if (child.prev == null) {
            this.child = child.next;
            if (this.child != null) {
               this.child.prev = null;
            }
         } else {
            child.prev.next = child.next;
            if (child.next != null) {
               child.next.prev = child.prev;
            }
         }

         --this.size;
         return child;
      }
   }

   /** @deprecated */
   public int size() {
      return this.size;
   }

   public String asString() {
      switch($SWITCH_TABLE$com$badlogic$gdx$utils$JsonValue$ValueType()[this.type.ordinal()]) {
      case 3:
         return this.stringValue;
      case 4:
         return Double.toString(this.doubleValue);
      case 5:
         return Long.toString(this.longValue);
      case 6:
         return this.longValue != 0L ? "true" : "false";
      case 7:
         return null;
      default:
         throw new IllegalStateException("Value cannot be converted to string: " + this.type);
      }
   }

   public float asFloat() {
      switch($SWITCH_TABLE$com$badlogic$gdx$utils$JsonValue$ValueType()[this.type.ordinal()]) {
      case 3:
         return Float.parseFloat(this.stringValue);
      case 4:
         return (float)this.doubleValue;
      case 5:
         return (float)this.longValue;
      case 6:
         return (float)(this.longValue != 0L ? 1 : 0);
      default:
         throw new IllegalStateException("Value cannot be converted to float: " + this.type);
      }
   }

   public double asDouble() {
      switch($SWITCH_TABLE$com$badlogic$gdx$utils$JsonValue$ValueType()[this.type.ordinal()]) {
      case 3:
         return Double.parseDouble(this.stringValue);
      case 4:
         return this.doubleValue;
      case 5:
         return (double)this.longValue;
      case 6:
         return (double)(this.longValue != 0L ? 1 : 0);
      default:
         throw new IllegalStateException("Value cannot be converted to double: " + this.type);
      }
   }

   public long asLong() {
      switch($SWITCH_TABLE$com$badlogic$gdx$utils$JsonValue$ValueType()[this.type.ordinal()]) {
      case 3:
         return Long.parseLong(this.stringValue);
      case 4:
         return (long)this.doubleValue;
      case 5:
         return this.longValue;
      case 6:
         return (long)(this.longValue != 0L ? 1 : 0);
      default:
         throw new IllegalStateException("Value cannot be converted to long: " + this.type);
      }
   }

   public int asInt() {
      switch($SWITCH_TABLE$com$badlogic$gdx$utils$JsonValue$ValueType()[this.type.ordinal()]) {
      case 3:
         return Integer.parseInt(this.stringValue);
      case 4:
         return (int)this.doubleValue;
      case 5:
         return (int)this.longValue;
      case 6:
         return this.longValue != 0L ? 1 : 0;
      default:
         throw new IllegalStateException("Value cannot be converted to int: " + this.type);
      }
   }

   public boolean asBoolean() {
      switch($SWITCH_TABLE$com$badlogic$gdx$utils$JsonValue$ValueType()[this.type.ordinal()]) {
      case 3:
         return this.stringValue.equalsIgnoreCase("true");
      case 4:
         if (this.doubleValue == 0.0D) {
            return true;
         }

         return false;
      case 5:
         if (this.longValue == 0L) {
            return true;
         }

         return false;
      case 6:
         if (this.longValue != 0L) {
            return true;
         }

         return false;
      default:
         throw new IllegalStateException("Value cannot be converted to boolean: " + this.type);
      }
   }

   public JsonValue getChild(String name) {
      JsonValue child = this.get(name);
      return child == null ? null : child.child;
   }

   public String getString(String name, String defaultValue) {
      JsonValue child = this.get(name);
      return child != null && child.isValue() && !child.isNull() ? child.asString() : defaultValue;
   }

   public float getFloat(String name, float defaultValue) {
      JsonValue child = this.get(name);
      return child != null && child.isValue() ? child.asFloat() : defaultValue;
   }

   public double getDouble(String name, double defaultValue) {
      JsonValue child = this.get(name);
      return child != null && child.isValue() ? child.asDouble() : defaultValue;
   }

   public long getLong(String name, long defaultValue) {
      JsonValue child = this.get(name);
      return child != null && child.isValue() ? child.asLong() : defaultValue;
   }

   public int getInt(String name, int defaultValue) {
      JsonValue child = this.get(name);
      return child != null && child.isValue() ? child.asInt() : defaultValue;
   }

   public boolean getBoolean(String name, boolean defaultValue) {
      JsonValue child = this.get(name);
      return child != null && child.isValue() ? child.asBoolean() : defaultValue;
   }

   public String getString(String name) {
      JsonValue child = this.get(name);
      if (child == null) {
         throw new IllegalArgumentException("Named value not found: " + name);
      } else {
         return child.asString();
      }
   }

   public float getFloat(String name) {
      JsonValue child = this.get(name);
      if (child == null) {
         throw new IllegalArgumentException("Named value not found: " + name);
      } else {
         return child.asFloat();
      }
   }

   public double getDouble(String name) {
      JsonValue child = this.get(name);
      if (child == null) {
         throw new IllegalArgumentException("Named value not found: " + name);
      } else {
         return child.asDouble();
      }
   }

   public long getLong(String name) {
      JsonValue child = this.get(name);
      if (child == null) {
         throw new IllegalArgumentException("Named value not found: " + name);
      } else {
         return child.asLong();
      }
   }

   public int getInt(String name) {
      JsonValue child = this.get(name);
      if (child == null) {
         throw new IllegalArgumentException("Named value not found: " + name);
      } else {
         return child.asInt();
      }
   }

   public boolean getBoolean(String name) {
      JsonValue child = this.get(name);
      if (child == null) {
         throw new IllegalArgumentException("Named value not found: " + name);
      } else {
         return child.asBoolean();
      }
   }

   public String getString(int index) {
      JsonValue child = this.get(index);
      if (child == null) {
         throw new IllegalArgumentException("Indexed value not found: " + this.name);
      } else {
         return child.asString();
      }
   }

   public float getFloat(int index) {
      JsonValue child = this.get(index);
      if (child == null) {
         throw new IllegalArgumentException("Indexed value not found: " + this.name);
      } else {
         return child.asFloat();
      }
   }

   public double getDouble(int index) {
      JsonValue child = this.get(index);
      if (child == null) {
         throw new IllegalArgumentException("Indexed value not found: " + this.name);
      } else {
         return child.asDouble();
      }
   }

   public long getLong(int index) {
      JsonValue child = this.get(index);
      if (child == null) {
         throw new IllegalArgumentException("Indexed value not found: " + this.name);
      } else {
         return child.asLong();
      }
   }

   public int getInt(int index) {
      JsonValue child = this.get(index);
      if (child == null) {
         throw new IllegalArgumentException("Indexed value not found: " + this.name);
      } else {
         return child.asInt();
      }
   }

   public boolean getBoolean(int index) {
      JsonValue child = this.get(index);
      if (child == null) {
         throw new IllegalArgumentException("Indexed value not found: " + this.name);
      } else {
         return child.asBoolean();
      }
   }

   public JsonValue.ValueType type() {
      return this.type;
   }

   public void setType(JsonValue.ValueType type) {
      if (type == null) {
         throw new IllegalArgumentException("type cannot be null.");
      } else {
         this.type = type;
      }
   }

   public boolean isArray() {
      return this.type == JsonValue.ValueType.array;
   }

   public boolean isObject() {
      return this.type == JsonValue.ValueType.object;
   }

   public boolean isString() {
      return this.type == JsonValue.ValueType.stringValue;
   }

   public boolean isNumber() {
      return this.type == JsonValue.ValueType.doubleValue || this.type == JsonValue.ValueType.longValue;
   }

   public boolean isDouble() {
      return this.type == JsonValue.ValueType.doubleValue;
   }

   public boolean isLong() {
      return this.type == JsonValue.ValueType.longValue;
   }

   public boolean isBoolean() {
      return this.type == JsonValue.ValueType.booleanValue;
   }

   public boolean isNull() {
      return this.type == JsonValue.ValueType.nullValue;
   }

   public boolean isValue() {
      switch($SWITCH_TABLE$com$badlogic$gdx$utils$JsonValue$ValueType()[this.type.ordinal()]) {
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
         return true;
      default:
         return false;
      }
   }

   public String name() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public JsonValue child() {
      return this.child;
   }

   public JsonValue next() {
      return this.next;
   }

   public void setNext(JsonValue next) {
      this.next = next;
   }

   public JsonValue prev() {
      return this.prev;
   }

   public void setPrev(JsonValue prev) {
      this.prev = prev;
   }

   public void set(String value) {
      this.stringValue = value;
      this.type = value == null ? JsonValue.ValueType.nullValue : JsonValue.ValueType.stringValue;
   }

   public void set(double value) {
      this.doubleValue = value;
      this.longValue = (long)value;
      this.type = JsonValue.ValueType.doubleValue;
   }

   public void set(long value) {
      this.longValue = value;
      this.doubleValue = (double)value;
      this.type = JsonValue.ValueType.longValue;
   }

   public void set(boolean value) {
      this.longValue = (long)(value ? 1 : 0);
      this.type = JsonValue.ValueType.booleanValue;
   }

   public String toString() {
      if (this.isValue()) {
         return this.name == null ? this.asString() : this.name + ": " + this.asString();
      } else {
         return this.prettyPrint(JsonWriter.OutputType.minimal, 0);
      }
   }

   public String prettyPrint(JsonWriter.OutputType outputType, int singleLineColumns) {
      StringBuilder buffer = new StringBuilder(512);
      this.prettyPrint(this, buffer, outputType, 0, singleLineColumns);
      return buffer.toString();
   }

   private void prettyPrint(JsonValue object, StringBuilder buffer, JsonWriter.OutputType outputType, int indent, int singleLineColumns) {
      boolean newLines;
      int start;
      if (object.isObject()) {
         if (object.child() == null) {
            buffer.append("{}");
         } else {
            newLines = !isFlat(object);
            start = buffer.length();

            label109:
            while(true) {
               buffer.append(newLines ? "{\n" : "{ ");
               int i = false;

               for(JsonValue child = object.child(); child != null; child = child.next()) {
                  if (newLines) {
                     indent(indent, buffer);
                  }

                  buffer.append(outputType.quoteName(child.name()));
                  buffer.append(": ");
                  this.prettyPrint(child, buffer, outputType, indent + 1, singleLineColumns);
                  if (child.next() != null) {
                     buffer.append(",");
                  }

                  buffer.append((char)(newLines ? '\n' : ' '));
                  if (!newLines && buffer.length() - start > singleLineColumns) {
                     buffer.setLength(start);
                     newLines = true;
                     continue label109;
                  }
               }

               if (newLines) {
                  indent(indent - 1, buffer);
               }

               buffer.append('}');
               break;
            }
         }
      } else if (object.isArray()) {
         if (object.child() == null) {
            buffer.append("[]");
         } else {
            newLines = !isFlat(object);
            start = buffer.length();

            label127:
            while(true) {
               buffer.append(newLines ? "[\n" : "[ ");

               for(JsonValue child = object.child(); child != null; child = child.next()) {
                  if (newLines) {
                     indent(indent, buffer);
                  }

                  this.prettyPrint(child, buffer, outputType, indent + 1, singleLineColumns);
                  if (child.next() != null) {
                     buffer.append(",");
                  }

                  buffer.append((char)(newLines ? '\n' : ' '));
                  if (!newLines && buffer.length() - start > singleLineColumns) {
                     buffer.setLength(start);
                     newLines = true;
                     continue label127;
                  }
               }

               if (newLines) {
                  indent(indent - 1, buffer);
               }

               buffer.append(']');
               break;
            }
         }
      } else if (object.isString()) {
         buffer.append(outputType.quoteValue(object.asString()));
      } else if (object.isDouble()) {
         double doubleValue = object.asDouble();
         long longValue = object.asLong();
         buffer.append(doubleValue == (double)longValue ? (double)longValue : doubleValue);
      } else if (object.isLong()) {
         buffer.append(object.asLong());
      } else if (object.isBoolean()) {
         buffer.append(object.asBoolean());
      } else {
         if (!object.isNull()) {
            throw new SerializationException("Unknown object type: " + object);
         }

         buffer.append("null");
      }

   }

   private static boolean isFlat(JsonValue object) {
      for(JsonValue child = object.child(); child != null; child = child.next()) {
         if (child.isObject() || child.isArray()) {
            return false;
         }
      }

      return true;
   }

   private static void indent(int count, StringBuilder buffer) {
      for(int i = 0; i < count; ++i) {
         buffer.append('\t');
      }

   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$com$badlogic$gdx$utils$JsonValue$ValueType() {
      int[] var10000 = $SWITCH_TABLE$com$badlogic$gdx$utils$JsonValue$ValueType;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[JsonValue.ValueType.values().length];

         try {
            var0[JsonValue.ValueType.array.ordinal()] = 2;
         } catch (NoSuchFieldError var7) {
         }

         try {
            var0[JsonValue.ValueType.booleanValue.ordinal()] = 6;
         } catch (NoSuchFieldError var6) {
         }

         try {
            var0[JsonValue.ValueType.doubleValue.ordinal()] = 4;
         } catch (NoSuchFieldError var5) {
         }

         try {
            var0[JsonValue.ValueType.longValue.ordinal()] = 5;
         } catch (NoSuchFieldError var4) {
         }

         try {
            var0[JsonValue.ValueType.nullValue.ordinal()] = 7;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[JsonValue.ValueType.object.ordinal()] = 1;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[JsonValue.ValueType.stringValue.ordinal()] = 3;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$com$badlogic$gdx$utils$JsonValue$ValueType = var0;
         return var0;
      }
   }

   public static enum ValueType {
      object,
      array,
      stringValue,
      doubleValue,
      longValue,
      booleanValue,
      nullValue;
   }
}
