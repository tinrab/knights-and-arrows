package com.badlogic.gdx.utils;

import java.io.IOException;
import java.io.Writer;
import java.util.regex.Pattern;

public class JsonWriter extends Writer {
   final Writer writer;
   private final Array<JsonWriter.JsonObject> stack = new Array();
   private JsonWriter.JsonObject current;
   private boolean named;
   private JsonWriter.OutputType outputType;

   public JsonWriter(Writer writer) {
      this.outputType = JsonWriter.OutputType.json;
      this.writer = writer;
   }

   public Writer getWriter() {
      return this.writer;
   }

   public void setOutputType(JsonWriter.OutputType outputType) {
      this.outputType = outputType;
   }

   public JsonWriter name(String name) throws IOException {
      if (this.current != null && !this.current.array) {
         if (!this.current.needsComma) {
            this.current.needsComma = true;
         } else {
            this.writer.write(44);
         }

         this.writer.write(this.outputType.quoteName(name));
         this.writer.write(58);
         this.named = true;
         return this;
      } else {
         throw new IllegalStateException("Current item must be an object.");
      }
   }

   public JsonWriter object() throws IOException {
      if (this.current != null) {
         if (this.current.array) {
            if (!this.current.needsComma) {
               this.current.needsComma = true;
            } else {
               this.writer.write(44);
            }
         } else {
            if (!this.named && !this.current.array) {
               throw new IllegalStateException("Name must be set.");
            }

            this.named = false;
         }
      }

      this.stack.add(this.current = new JsonWriter.JsonObject(false));
      return this;
   }

   public JsonWriter array() throws IOException {
      if (this.current != null) {
         if (this.current.array) {
            if (!this.current.needsComma) {
               this.current.needsComma = true;
            } else {
               this.writer.write(44);
            }
         } else {
            if (!this.named && !this.current.array) {
               throw new IllegalStateException("Name must be set.");
            }

            this.named = false;
         }
      }

      this.stack.add(this.current = new JsonWriter.JsonObject(true));
      return this;
   }

   public JsonWriter value(Object value) throws IOException {
      if (value instanceof Number) {
         Number number = (Number)value;
         long longValue = number.longValue();
         if (number.doubleValue() == (double)longValue) {
            value = longValue;
         }
      }

      if (this.current != null) {
         if (this.current.array) {
            if (!this.current.needsComma) {
               this.current.needsComma = true;
            } else {
               this.writer.write(44);
            }
         } else {
            if (!this.named) {
               throw new IllegalStateException("Name must be set.");
            }

            this.named = false;
         }
      }

      this.writer.write(this.outputType.quoteValue(value));
      return this;
   }

   public JsonWriter object(String name) throws IOException {
      return this.name(name).object();
   }

   public JsonWriter array(String name) throws IOException {
      return this.name(name).array();
   }

   public JsonWriter set(String name, Object value) throws IOException {
      return this.name(name).value(value);
   }

   public JsonWriter pop() throws IOException {
      if (this.named) {
         throw new IllegalStateException("Expected an object, array, or value since a name was set.");
      } else {
         ((JsonWriter.JsonObject)this.stack.pop()).close();
         this.current = this.stack.size == 0 ? null : (JsonWriter.JsonObject)this.stack.peek();
         return this;
      }
   }

   public void write(char[] cbuf, int off, int len) throws IOException {
      this.writer.write(cbuf, off, len);
   }

   public void flush() throws IOException {
      this.writer.flush();
   }

   public void close() throws IOException {
      while(this.stack.size > 0) {
         this.pop();
      }

      this.writer.close();
   }

   private class JsonObject {
      final boolean array;
      boolean needsComma;

      JsonObject(boolean array) throws IOException {
         this.array = array;
         JsonWriter.this.writer.write(array ? 91 : 123);
      }

      void close() throws IOException {
         JsonWriter.this.writer.write(this.array ? 93 : 125);
      }
   }

   public static enum OutputType {
      json,
      javascript,
      minimal;

      private static Pattern javascriptPattern = Pattern.compile("[a-zA-Z_$][a-zA-Z_$0-9]*");
      private static Pattern minimalValuePattern = Pattern.compile("[a-zA-Z_$][^:}\\], ]*");
      private static Pattern minimalNamePattern = Pattern.compile("[a-zA-Z0-9_$][^:}\\], ]*");
      // $FF: synthetic field
      private static int[] $SWITCH_TABLE$com$badlogic$gdx$utils$JsonWriter$OutputType;

      public String quoteValue(Object value) {
         if (value != null && !(value instanceof Number) && !(value instanceof Boolean)) {
            String string = String.valueOf(value).replace("\\", "\\\\");
            return this == minimal && !string.equals("true") && !string.equals("false") && !string.equals("null") && minimalValuePattern.matcher(string).matches() ? string : '"' + string.replace("\"", "\\\"") + '"';
         } else {
            return String.valueOf(value);
         }
      }

      public String quoteName(String value) {
         value = value.replace("\\", "\\\\");
         switch($SWITCH_TABLE$com$badlogic$gdx$utils$JsonWriter$OutputType()[this.ordinal()]) {
         case 2:
            if (javascriptPattern.matcher(value).matches()) {
               return value;
            }

            return '"' + value.replace("\"", "\\\"") + '"';
         case 3:
            if (minimalNamePattern.matcher(value).matches()) {
               return value;
            }

            return '"' + value.replace("\"", "\\\"") + '"';
         default:
            return '"' + value.replace("\"", "\\\"") + '"';
         }
      }

      // $FF: synthetic method
      static int[] $SWITCH_TABLE$com$badlogic$gdx$utils$JsonWriter$OutputType() {
         int[] var10000 = $SWITCH_TABLE$com$badlogic$gdx$utils$JsonWriter$OutputType;
         if (var10000 != null) {
            return var10000;
         } else {
            int[] var0 = new int[values().length];

            try {
               var0[javascript.ordinal()] = 2;
            } catch (NoSuchFieldError var3) {
            }

            try {
               var0[json.ordinal()] = 1;
            } catch (NoSuchFieldError var2) {
            }

            try {
               var0[minimal.ordinal()] = 3;
            } catch (NoSuchFieldError var1) {
            }

            $SWITCH_TABLE$com$badlogic$gdx$utils$JsonWriter$OutputType = var0;
            return var0;
         }
      }
   }
}
