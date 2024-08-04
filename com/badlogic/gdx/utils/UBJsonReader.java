package com.badlogic.gdx.utils;

import com.badlogic.gdx.files.FileHandle;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class UBJsonReader implements BaseJsonReader {
   public JsonValue parse(InputStream input) {
      try {
         return this.parse(new DataInputStream(input));
      } catch (IOException var3) {
         throw new SerializationException(var3);
      }
   }

   public JsonValue parse(FileHandle file) {
      try {
         return this.parse(file.read());
      } catch (Exception var3) {
         throw new SerializationException("Error parsing file: " + file, var3);
      }
   }

   public JsonValue parse(DataInputStream din) throws IOException {
      return this.parse(din, din.readByte());
   }

   protected JsonValue parse(DataInputStream din, byte type) throws IOException {
      if (type == 91) {
         return this.parseArray(din);
      } else if (type == 123) {
         return this.parseObject(din);
      } else if (type == 90) {
         return new JsonValue(JsonValue.ValueType.nullValue);
      } else if (type == 84) {
         return new JsonValue(true);
      } else if (type == 70) {
         return new JsonValue(false);
      } else if (type == 66) {
         return new JsonValue((long)this.readUChar(din));
      } else if (type == 105) {
         return new JsonValue((long)din.readShort());
      } else if (type == 73) {
         return new JsonValue((long)din.readInt());
      } else if (type == 76) {
         return new JsonValue(din.readLong());
      } else if (type == 100) {
         return new JsonValue((double)din.readFloat());
      } else if (type == 68) {
         return new JsonValue(din.readDouble());
      } else if (type != 115 && type != 83) {
         if (type != 97 && type != 65) {
            throw new GdxRuntimeException("Unrecognized data type");
         } else {
            return this.parseData(din, type);
         }
      } else {
         return new JsonValue(this.parseString(din, type));
      }
   }

   protected JsonValue parseArray(DataInputStream din) throws IOException {
      JsonValue result = new JsonValue(JsonValue.ValueType.array);
      byte type = din.readByte();

      for(JsonValue prev = null; din.available() > 0 && type != 93; type = din.readByte()) {
         JsonValue val = this.parse(din, type);
         if (prev != null) {
            prev.next = val;
            ++result.size;
         } else {
            result.child = val;
            result.size = 1;
         }

         prev = val;
      }

      return result;
   }

   protected JsonValue parseObject(DataInputStream din) throws IOException {
      JsonValue result = new JsonValue(JsonValue.ValueType.object);
      byte type = din.readByte();

      for(JsonValue prev = null; din.available() > 0 && type != 125; type = din.readByte()) {
         if (type != 115 && type != 83) {
            throw new GdxRuntimeException("Only string key are currently supported");
         }

         String key = this.parseString(din, type);
         JsonValue child = this.parse(din);
         child.setName(key);
         if (prev != null) {
            prev.next = child;
            ++result.size;
         } else {
            result.child = child;
            result.size = 1;
         }

         prev = child;
      }

      return result;
   }

   protected JsonValue parseData(DataInputStream din, byte blockType) throws IOException {
      byte dataType = din.readByte();
      long size = blockType == 65 ? this.readUInt(din) : (long)this.readUChar(din);
      JsonValue result = new JsonValue(JsonValue.ValueType.array);
      JsonValue prev = null;

      for(long i = 0L; i < size; ++i) {
         JsonValue val = this.parse(din, dataType);
         if (prev != null) {
            prev.next = val;
            ++result.size;
         } else {
            result.child = val;
            result.size = 1;
         }

         prev = val;
      }

      return result;
   }

   protected String parseString(DataInputStream din, byte type) throws IOException {
      return this.readString(din, type == 115 ? (long)this.readUChar(din) : this.readUInt(din));
   }

   protected short readUChar(DataInputStream din) throws IOException {
      return (short)((short)din.readByte() & 255);
   }

   protected long readUInt(DataInputStream din) throws IOException {
      return (long)din.readInt() & -1L;
   }

   protected String readString(DataInputStream din, long size) throws IOException {
      byte[] data = new byte[(int)size];
      din.readFully(data);
      return new String(data, "UTF-8");
   }
}
