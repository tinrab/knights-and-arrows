package com.badlogic.gdx.utils;

public class Bits {
   long[] bits = new long[1];

   public boolean get(int index) {
      int word = index >>> 6;
      if (word >= this.bits.length) {
         return false;
      } else {
         return (this.bits[word] & 1L << (index & 63)) != 0L;
      }
   }

   public void set(int index) {
      int word = index >>> 6;
      this.checkCapacity(word);
      long[] var10000 = this.bits;
      var10000[word] |= 1L << (index & 63);
   }

   public void flip(int index) {
      int word = index >>> 6;
      this.checkCapacity(word);
      long[] var10000 = this.bits;
      var10000[word] ^= 1L << (index & 63);
   }

   private void checkCapacity(int len) {
      if (len > this.bits.length) {
         long[] newBits = new long[len + 1];
         System.arraycopy(this.bits, 0, newBits, 0, this.bits.length);
         this.bits = newBits;
      }

   }

   public void clear(int index) {
      int word = index >>> 6;
      if (word < this.bits.length) {
         long[] var10000 = this.bits;
         var10000[word] &= ~(1L << (index & 63));
      }
   }

   public void clear() {
      int length = this.bits.length;

      for(int i = 0; i < length; ++i) {
         this.bits[i] = 0L;
      }

   }

   public int numBits() {
      return this.bits.length << 6;
   }
}
