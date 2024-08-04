package com.bitfire.utils;

public final class Hash {
   private Hash() {
   }

   public static long RSHash(String str) {
      int b = 378551;
      int a = 63689;
      long hash = 0L;

      for(int i = 0; i < str.length(); ++i) {
         hash = hash * (long)a + (long)str.charAt(i);
         a *= b;
      }

      return hash;
   }

   public static long JSHash(String str) {
      long hash = 1315423911L;

      for(int i = 0; i < str.length(); ++i) {
         hash ^= (hash << 5) + (long)str.charAt(i) + (hash >> 2);
      }

      return hash;
   }

   public static long PJWHash(String str) {
      long BitsInUnsignedInt = 32L;
      long ThreeQuarters = BitsInUnsignedInt * 3L / 4L;
      long OneEighth = BitsInUnsignedInt / 8L;
      long HighBits = -1L << (int)(BitsInUnsignedInt - OneEighth);
      long hash = 0L;
      long test = 0L;

      for(int i = 0; i < str.length(); ++i) {
         hash = (hash << (int)OneEighth) + (long)str.charAt(i);
         test = hash & HighBits;
         if (test != 0L) {
            hash = (hash ^ test >> (int)ThreeQuarters) & ~HighBits;
         }
      }

      return hash;
   }

   public static long ELFHash(String str) {
      long hash = 0L;
      long x = 0L;

      for(int i = 0; i < str.length(); ++i) {
         hash = (hash << 4) + (long)str.charAt(i);
         x = hash & 4026531840L;
         if (x != 0L) {
            hash ^= x >> 24;
         }

         hash &= ~x;
      }

      return hash;
   }

   public static long BKDRHash(String str) {
      long seed = 131L;
      long hash = 0L;

      for(int i = 0; i < str.length(); ++i) {
         hash = hash * seed + (long)str.charAt(i);
      }

      return hash;
   }

   public static long SDBMHash(String str) {
      long hash = 0L;

      for(int i = 0; i < str.length(); ++i) {
         hash = (long)str.charAt(i) + (hash << 6) + (hash << 16) - hash;
      }

      return hash;
   }

   public static long DJBHash(String str) {
      long hash = 5381L;

      for(int i = 0; i < str.length(); ++i) {
         hash = (hash << 5) + hash + (long)str.charAt(i);
      }

      return hash;
   }

   public static long DEKHash(String str) {
      long hash = (long)str.length();

      for(int i = 0; i < str.length(); ++i) {
         hash = hash << 5 ^ hash >> 27 ^ (long)str.charAt(i);
      }

      return hash;
   }

   public static long BPHash(String str) {
      long hash = 0L;

      for(int i = 0; i < str.length(); ++i) {
         hash = hash << 7 ^ (long)str.charAt(i);
      }

      return hash;
   }

   public static long FNVHash(String str) {
      long fnv_prime = -2128831035L;
      long hash = 0L;

      for(int i = 0; i < str.length(); ++i) {
         hash *= fnv_prime;
         hash ^= (long)str.charAt(i);
      }

      return hash;
   }

   public static long APHash(String str) {
      long hash = -1431655766L;

      for(int i = 0; i < str.length(); ++i) {
         if ((i & 1) == 0) {
            hash ^= hash << 7 ^ (long)str.charAt(i) * (hash >> 3);
         } else {
            hash ^= ~((hash << 11) + (long)str.charAt(i) ^ hash >> 5);
         }
      }

      return hash;
   }
}
