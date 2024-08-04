package com.badlogic.gdx.utils;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

public class BufferUtils {
   static Array<ByteBuffer> unsafeBuffers = new Array();
   static int allocatedUnsafe = 0;

   public static void copy(float[] src, Buffer dst, int numFloats, int offset) {
      copyJni(src, dst, numFloats, offset);
      dst.position(0);
      if (dst instanceof ByteBuffer) {
         dst.limit(numFloats << 2);
      } else if (dst instanceof FloatBuffer) {
         dst.limit(numFloats);
      }

   }

   public static void copy(byte[] src, int srcOffset, Buffer dst, int numElements) {
      copyJni(src, srcOffset, dst, positionInBytes(dst), numElements);
      dst.limit(dst.position() + bytesToElements(dst, numElements));
   }

   public static void copy(short[] src, int srcOffset, Buffer dst, int numElements) {
      copyJni(src, srcOffset << 1, dst, positionInBytes(dst), numElements << 1);
      dst.limit(dst.position() + bytesToElements(dst, numElements << 1));
   }

   public static void copy(char[] src, int srcOffset, Buffer dst, int numElements) {
      copyJni(src, srcOffset << 1, dst, positionInBytes(dst), numElements << 1);
      dst.limit(dst.position() + bytesToElements(dst, numElements << 1));
   }

   public static void copy(int[] src, int srcOffset, Buffer dst, int numElements) {
      copyJni(src, srcOffset << 2, dst, positionInBytes(dst), numElements << 2);
      dst.limit(dst.position() + bytesToElements(dst, numElements << 2));
   }

   public static void copy(long[] src, int srcOffset, Buffer dst, int numElements) {
      copyJni(src, srcOffset << 3, dst, positionInBytes(dst), numElements << 3);
      dst.limit(dst.position() + bytesToElements(dst, numElements << 3));
   }

   public static void copy(float[] src, int srcOffset, Buffer dst, int numElements) {
      copyJni(src, srcOffset << 2, dst, positionInBytes(dst), numElements << 2);
      dst.limit(dst.position() + bytesToElements(dst, numElements << 2));
   }

   public static void copy(double[] src, int srcOffset, Buffer dst, int numElements) {
      copyJni(src, srcOffset << 3, dst, positionInBytes(dst), numElements << 3);
      dst.limit(dst.position() + bytesToElements(dst, numElements << 3));
   }

   public static void copy(Buffer src, Buffer dst, int numElements) {
      int numBytes = elementsToBytes(src, numElements);
      copyJni(src, positionInBytes(src), dst, positionInBytes(dst), numBytes);
      dst.limit(dst.position() + bytesToElements(dst, numBytes));
   }

   private static int positionInBytes(Buffer dst) {
      if (dst instanceof ByteBuffer) {
         return dst.position();
      } else if (dst instanceof ShortBuffer) {
         return dst.position() << 1;
      } else if (dst instanceof CharBuffer) {
         return dst.position() << 1;
      } else if (dst instanceof IntBuffer) {
         return dst.position() << 2;
      } else if (dst instanceof LongBuffer) {
         return dst.position() << 3;
      } else if (dst instanceof FloatBuffer) {
         return dst.position() << 2;
      } else if (dst instanceof DoubleBuffer) {
         return dst.position() << 3;
      } else {
         throw new GdxRuntimeException("Can't copy to a " + dst.getClass().getName() + " instance");
      }
   }

   private static int bytesToElements(Buffer dst, int bytes) {
      if (dst instanceof ByteBuffer) {
         return bytes;
      } else if (dst instanceof ShortBuffer) {
         return bytes >>> 1;
      } else if (dst instanceof CharBuffer) {
         return bytes >>> 1;
      } else if (dst instanceof IntBuffer) {
         return bytes >>> 2;
      } else if (dst instanceof LongBuffer) {
         return bytes >>> 3;
      } else if (dst instanceof FloatBuffer) {
         return bytes >>> 2;
      } else if (dst instanceof DoubleBuffer) {
         return bytes >>> 3;
      } else {
         throw new GdxRuntimeException("Can't copy to a " + dst.getClass().getName() + " instance");
      }
   }

   private static int elementsToBytes(Buffer dst, int elements) {
      if (dst instanceof ByteBuffer) {
         return elements;
      } else if (dst instanceof ShortBuffer) {
         return elements << 1;
      } else if (dst instanceof CharBuffer) {
         return elements << 1;
      } else if (dst instanceof IntBuffer) {
         return elements << 2;
      } else if (dst instanceof LongBuffer) {
         return elements << 3;
      } else if (dst instanceof FloatBuffer) {
         return elements << 2;
      } else if (dst instanceof DoubleBuffer) {
         return elements << 3;
      } else {
         throw new GdxRuntimeException("Can't copy to a " + dst.getClass().getName() + " instance");
      }
   }

   public static FloatBuffer newFloatBuffer(int numFloats) {
      ByteBuffer buffer = ByteBuffer.allocateDirect(numFloats * 4);
      buffer.order(ByteOrder.nativeOrder());
      return buffer.asFloatBuffer();
   }

   public static DoubleBuffer newDoubleBuffer(int numDoubles) {
      ByteBuffer buffer = ByteBuffer.allocateDirect(numDoubles * 8);
      buffer.order(ByteOrder.nativeOrder());
      return buffer.asDoubleBuffer();
   }

   public static ByteBuffer newByteBuffer(int numBytes) {
      ByteBuffer buffer = ByteBuffer.allocateDirect(numBytes);
      buffer.order(ByteOrder.nativeOrder());
      return buffer;
   }

   public static ShortBuffer newShortBuffer(int numShorts) {
      ByteBuffer buffer = ByteBuffer.allocateDirect(numShorts * 2);
      buffer.order(ByteOrder.nativeOrder());
      return buffer.asShortBuffer();
   }

   public static CharBuffer newCharBuffer(int numChars) {
      ByteBuffer buffer = ByteBuffer.allocateDirect(numChars * 2);
      buffer.order(ByteOrder.nativeOrder());
      return buffer.asCharBuffer();
   }

   public static IntBuffer newIntBuffer(int numInts) {
      ByteBuffer buffer = ByteBuffer.allocateDirect(numInts * 4);
      buffer.order(ByteOrder.nativeOrder());
      return buffer.asIntBuffer();
   }

   public static LongBuffer newLongBuffer(int numLongs) {
      ByteBuffer buffer = ByteBuffer.allocateDirect(numLongs * 8);
      buffer.order(ByteOrder.nativeOrder());
      return buffer.asLongBuffer();
   }

   public static void disposeUnsafeByteBuffer(ByteBuffer buffer) {
      int size = buffer.capacity();
      synchronized(unsafeBuffers) {
         if (!unsafeBuffers.removeValue(buffer, true)) {
            throw new IllegalArgumentException("buffer not allocated with newUnsafeByteBuffer or already disposed");
         }
      }

      allocatedUnsafe -= size;
      freeMemory(buffer);
   }

   public static ByteBuffer newUnsafeByteBuffer(int numBytes) {
      ByteBuffer buffer = newDisposableByteBuffer(numBytes);
      buffer.order(ByteOrder.nativeOrder());
      allocatedUnsafe += numBytes;
      synchronized(unsafeBuffers) {
         unsafeBuffers.add(buffer);
         return buffer;
      }
   }

   public static long getUnsafeBufferAddress(Buffer buffer) {
      return getBufferAddress(buffer) + (long)buffer.position();
   }

   public static ByteBuffer newUnsafeByteBuffer(ByteBuffer buffer) {
      allocatedUnsafe += buffer.capacity();
      synchronized(unsafeBuffers) {
         unsafeBuffers.add(buffer);
         return buffer;
      }
   }

   public static int getAllocatedBytesUnsafe() {
      return allocatedUnsafe;
   }

   private static native void freeMemory(ByteBuffer var0);

   private static native ByteBuffer newDisposableByteBuffer(int var0);

   private static native long getBufferAddress(Buffer var0);

   public static native void clear(ByteBuffer var0, int var1);

   private static native void copyJni(float[] var0, Buffer var1, int var2, int var3);

   private static native void copyJni(byte[] var0, int var1, Buffer var2, int var3, int var4);

   private static native void copyJni(char[] var0, int var1, Buffer var2, int var3, int var4);

   private static native void copyJni(short[] var0, int var1, Buffer var2, int var3, int var4);

   private static native void copyJni(int[] var0, int var1, Buffer var2, int var3, int var4);

   private static native void copyJni(long[] var0, int var1, Buffer var2, int var3, int var4);

   private static native void copyJni(float[] var0, int var1, Buffer var2, int var3, int var4);

   private static native void copyJni(double[] var0, int var1, Buffer var2, int var3, int var4);

   private static native void copyJni(Buffer var0, int var1, Buffer var2, int var3, int var4);
}
