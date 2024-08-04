package com.badlogic.gdx.utils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DataOutput extends DataOutputStream {
   public DataOutput(OutputStream out) {
      super(out);
   }

   public int writeInt(int value, boolean optimizePositive) throws IOException {
      if (!optimizePositive) {
         value = value << 1 ^ value >> 31;
      }

      if (value >>> 7 == 0) {
         this.write((byte)value);
         return 1;
      } else {
         this.write((byte)(value & 127 | 128));
         if (value >>> 14 == 0) {
            this.write((byte)(value >>> 7));
            return 2;
         } else {
            this.write((byte)(value >>> 7 | 128));
            if (value >>> 21 == 0) {
               this.write((byte)(value >>> 14));
               return 3;
            } else {
               this.write((byte)(value >>> 14 | 128));
               if (value >>> 28 == 0) {
                  this.write((byte)(value >>> 21));
                  return 4;
               } else {
                  this.write((byte)(value >>> 21 | 128));
                  this.write((byte)(value >>> 28));
                  return 5;
               }
            }
         }
      }
   }

   public void writeString(String value) throws IOException {
      if (value == null) {
         this.write(0);
      } else {
         int charCount = value.length();
         if (charCount == 0) {
            this.writeByte(1);
         } else {
            this.writeInt(charCount + 1, true);

            int charIndex;
            for(charIndex = 0; charIndex < charCount; ++charIndex) {
               int c = value.charAt(charIndex);
               if (c > 127) {
                  break;
               }

               this.write((byte)c);
            }

            if (charIndex < charCount) {
               this.writeString_slow(value, charCount, charIndex);
            }

         }
      }
   }

   private void writeString_slow(String value, int charCount, int charIndex) throws IOException {
      for(; charIndex < charCount; ++charIndex) {
         int c = value.charAt(charIndex);
         if (c <= 127) {
            this.write((byte)c);
         } else if (c > 2047) {
            this.write((byte)(224 | c >> 12 & 15));
            this.write((byte)(128 | c >> 6 & 63));
            this.write((byte)(128 | c & 63));
         } else {
            this.write((byte)(192 | c >> 6 & 31));
            this.write((byte)(128 | c & 63));
         }
      }

   }
}
