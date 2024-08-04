package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class Gdx2DPixmap implements Disposable {
   public static final int GDX2D_FORMAT_ALPHA = 1;
   public static final int GDX2D_FORMAT_LUMINANCE_ALPHA = 2;
   public static final int GDX2D_FORMAT_RGB888 = 3;
   public static final int GDX2D_FORMAT_RGBA8888 = 4;
   public static final int GDX2D_FORMAT_RGB565 = 5;
   public static final int GDX2D_FORMAT_RGBA4444 = 6;
   public static final int GDX2D_SCALE_NEAREST = 0;
   public static final int GDX2D_SCALE_LINEAR = 1;
   public static final int GDX2D_BLEND_NONE = 0;
   public static final int GDX2D_BLEND_SRC_OVER = 1;
   final long basePtr;
   final int width;
   final int height;
   final int format;
   final ByteBuffer pixelPtr;
   final long[] nativeData = new long[4];

   static {
      setBlend(1);
      setScale(1);
   }

   public Gdx2DPixmap(byte[] encodedData, int offset, int len, int requestedFormat) throws IOException {
      this.pixelPtr = load(this.nativeData, encodedData, offset, len, requestedFormat);
      if (this.pixelPtr == null) {
         throw new IOException("couldn't load pixmap " + getFailureReason());
      } else {
         this.basePtr = this.nativeData[0];
         this.width = (int)this.nativeData[1];
         this.height = (int)this.nativeData[2];
         this.format = (int)this.nativeData[3];
      }
   }

   public Gdx2DPixmap(InputStream in, int requestedFormat) throws IOException {
      ByteArrayOutputStream bytes = new ByteArrayOutputStream(1024);
      byte[] buffer = new byte[1024];
      boolean var5 = false;

      int readBytes;
      while((readBytes = in.read(buffer)) != -1) {
         bytes.write(buffer, 0, readBytes);
      }

      buffer = bytes.toByteArray();
      this.pixelPtr = load(this.nativeData, buffer, 0, buffer.length, requestedFormat);
      if (this.pixelPtr == null) {
         throw new IOException("couldn't load pixmap " + getFailureReason());
      } else {
         this.basePtr = this.nativeData[0];
         this.width = (int)this.nativeData[1];
         this.height = (int)this.nativeData[2];
         this.format = (int)this.nativeData[3];
      }
   }

   public Gdx2DPixmap(int width, int height, int format) throws GdxRuntimeException {
      this.pixelPtr = newPixmap(this.nativeData, width, height, format);
      if (this.pixelPtr == null) {
         throw new GdxRuntimeException("couldn't load pixmap");
      } else {
         this.basePtr = this.nativeData[0];
         this.width = (int)this.nativeData[1];
         this.height = (int)this.nativeData[2];
         this.format = (int)this.nativeData[3];
      }
   }

   public Gdx2DPixmap(ByteBuffer pixelPtr, long[] nativeData) {
      this.pixelPtr = pixelPtr;
      this.basePtr = nativeData[0];
      this.width = (int)nativeData[1];
      this.height = (int)nativeData[2];
      this.format = (int)nativeData[3];
   }

   public void dispose() {
      free(this.basePtr);
   }

   public void clear(int color) {
      clear(this.basePtr, color);
   }

   public void setPixel(int x, int y, int color) {
      setPixel(this.basePtr, x, y, color);
   }

   public int getPixel(int x, int y) {
      return getPixel(this.basePtr, x, y);
   }

   public void drawLine(int x, int y, int x2, int y2, int color) {
      drawLine(this.basePtr, x, y, x2, y2, color);
   }

   public void drawRect(int x, int y, int width, int height, int color) {
      drawRect(this.basePtr, x, y, width, height, color);
   }

   public void drawCircle(int x, int y, int radius, int color) {
      drawCircle(this.basePtr, x, y, radius, color);
   }

   public void fillRect(int x, int y, int width, int height, int color) {
      fillRect(this.basePtr, x, y, width, height, color);
   }

   public void fillCircle(int x, int y, int radius, int color) {
      fillCircle(this.basePtr, x, y, radius, color);
   }

   public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3, int color) {
      fillTriangle(this.basePtr, x1, y1, x2, y2, x3, y3, color);
   }

   public void drawPixmap(Gdx2DPixmap src, int srcX, int srcY, int dstX, int dstY, int width, int height) {
      drawPixmap(src.basePtr, this.basePtr, srcX, srcY, width, height, dstX, dstY, width, height);
   }

   public void drawPixmap(Gdx2DPixmap src, int srcX, int srcY, int srcWidth, int srcHeight, int dstX, int dstY, int dstWidth, int dstHeight) {
      drawPixmap(src.basePtr, this.basePtr, srcX, srcY, srcWidth, srcHeight, dstX, dstY, dstWidth, dstHeight);
   }

   public static Gdx2DPixmap newPixmap(InputStream in, int requestedFormat) {
      try {
         return new Gdx2DPixmap(in, requestedFormat);
      } catch (IOException var3) {
         return null;
      }
   }

   public static Gdx2DPixmap newPixmap(int width, int height, int format) {
      try {
         return new Gdx2DPixmap(width, height, format);
      } catch (IllegalArgumentException var4) {
         return null;
      }
   }

   public ByteBuffer getPixels() {
      return this.pixelPtr;
   }

   public int getHeight() {
      return this.height;
   }

   public int getWidth() {
      return this.width;
   }

   public int getFormat() {
      return this.format;
   }

   public int getGLInternalFormat() {
      switch(this.format) {
      case 1:
         return 6406;
      case 2:
         return 6410;
      case 3:
      case 5:
         return 6407;
      case 4:
      case 6:
         return 6408;
      default:
         throw new GdxRuntimeException("unknown format: " + this.format);
      }
   }

   public int getGLFormat() {
      return this.getGLInternalFormat();
   }

   public int getGLType() {
      switch(this.format) {
      case 1:
      case 2:
      case 3:
      case 4:
         return 5121;
      case 5:
         return 33635;
      case 6:
         return 32819;
      default:
         throw new GdxRuntimeException("unknown format: " + this.format);
      }
   }

   public String getFormatString() {
      switch(this.format) {
      case 1:
         return "alpha";
      case 2:
         return "luminance alpha";
      case 3:
         return "rgb888";
      case 4:
         return "rgba8888";
      case 5:
         return "rgb565";
      case 6:
         return "rgba4444";
      default:
         return "unknown";
      }
   }

   private static native ByteBuffer load(long[] var0, byte[] var1, int var2, int var3, int var4);

   private static native ByteBuffer newPixmap(long[] var0, int var1, int var2, int var3);

   private static native void free(long var0);

   private static native void clear(long var0, int var2);

   private static native void setPixel(long var0, int var2, int var3, int var4);

   private static native int getPixel(long var0, int var2, int var3);

   private static native void drawLine(long var0, int var2, int var3, int var4, int var5, int var6);

   private static native void drawRect(long var0, int var2, int var3, int var4, int var5, int var6);

   private static native void drawCircle(long var0, int var2, int var3, int var4, int var5);

   private static native void fillRect(long var0, int var2, int var3, int var4, int var5, int var6);

   private static native void fillCircle(long var0, int var2, int var3, int var4, int var5);

   private static native void fillTriangle(long var0, int var2, int var3, int var4, int var5, int var6, int var7, int var8);

   private static native void drawPixmap(long var0, long var2, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11);

   public static native void setBlend(int var0);

   public static native void setScale(int var0);

   public static native String getFailureReason();
}
