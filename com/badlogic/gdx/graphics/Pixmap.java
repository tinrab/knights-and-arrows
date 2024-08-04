package com.badlogic.gdx.graphics;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Pixmap implements Disposable {
   private static Pixmap.Blending blending;
   final Gdx2DPixmap pixmap;
   int color = 0;
   private boolean disposed;

   static {
      blending = Pixmap.Blending.SourceOver;
   }

   public static void setBlending(Pixmap.Blending blending) {
      Pixmap.blending = blending;
      Gdx2DPixmap.setBlend(blending == Pixmap.Blending.None ? 0 : 1);
   }

   public static void setFilter(Pixmap.Filter filter) {
      Gdx2DPixmap.setScale(filter == Pixmap.Filter.NearestNeighbour ? 0 : 1);
   }

   public Pixmap(int width, int height, Pixmap.Format format) {
      this.pixmap = new Gdx2DPixmap(width, height, Pixmap.Format.toGdx2DPixmapFormat(format));
      this.setColor(0.0F, 0.0F, 0.0F, 0.0F);
      this.fill();
   }

   public Pixmap(byte[] encodedData, int offset, int len) {
      try {
         this.pixmap = new Gdx2DPixmap(encodedData, offset, len, 0);
      } catch (IOException var5) {
         throw new GdxRuntimeException("Couldn't load pixmap from image data", var5);
      }
   }

   public Pixmap(FileHandle file) {
      try {
         byte[] bytes = file.readBytes();
         this.pixmap = new Gdx2DPixmap(bytes, 0, bytes.length, 0);
      } catch (Exception var3) {
         throw new GdxRuntimeException("Couldn't load file: " + file, var3);
      }
   }

   public Pixmap(Gdx2DPixmap pixmap) {
      this.pixmap = pixmap;
   }

   public void setColor(int color) {
      this.color = color;
   }

   public void setColor(float r, float g, float b, float a) {
      this.color = Color.rgba8888(r, g, b, a);
   }

   public void setColor(Color color) {
      this.color = Color.rgba8888(color.r, color.g, color.b, color.a);
   }

   public void fill() {
      this.pixmap.clear(this.color);
   }

   public void drawLine(int x, int y, int x2, int y2) {
      this.pixmap.drawLine(x, y, x2, y2, this.color);
   }

   public void drawRectangle(int x, int y, int width, int height) {
      this.pixmap.drawRect(x, y, width, height, this.color);
   }

   public void drawPixmap(Pixmap pixmap, int x, int y) {
      this.drawPixmap(pixmap, x, y, 0, 0, pixmap.getWidth(), pixmap.getHeight());
   }

   public void drawPixmap(Pixmap pixmap, int x, int y, int srcx, int srcy, int srcWidth, int srcHeight) {
      this.pixmap.drawPixmap(pixmap.pixmap, srcx, srcy, x, y, srcWidth, srcHeight);
   }

   public void drawPixmap(Pixmap pixmap, int srcx, int srcy, int srcWidth, int srcHeight, int dstx, int dsty, int dstWidth, int dstHeight) {
      this.pixmap.drawPixmap(pixmap.pixmap, srcx, srcy, srcWidth, srcHeight, dstx, dsty, dstWidth, dstHeight);
   }

   public void fillRectangle(int x, int y, int width, int height) {
      this.pixmap.fillRect(x, y, width, height, this.color);
   }

   public void drawCircle(int x, int y, int radius) {
      this.pixmap.drawCircle(x, y, radius, this.color);
   }

   public void fillCircle(int x, int y, int radius) {
      this.pixmap.fillCircle(x, y, radius, this.color);
   }

   public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3) {
      this.pixmap.fillTriangle(x1, y1, x2, y2, x3, y3, this.color);
   }

   public int getPixel(int x, int y) {
      return this.pixmap.getPixel(x, y);
   }

   public int getWidth() {
      return this.pixmap.getWidth();
   }

   public int getHeight() {
      return this.pixmap.getHeight();
   }

   public void dispose() {
      if (this.disposed) {
         throw new GdxRuntimeException("Pixmap already disposed!");
      } else {
         this.pixmap.dispose();
         this.disposed = true;
      }
   }

   public void drawPixel(int x, int y) {
      this.pixmap.setPixel(x, y, this.color);
   }

   public void drawPixel(int x, int y, int color) {
      this.pixmap.setPixel(x, y, color);
   }

   public int getGLFormat() {
      return this.pixmap.getGLFormat();
   }

   public int getGLInternalFormat() {
      return this.pixmap.getGLInternalFormat();
   }

   public int getGLType() {
      return this.pixmap.getGLType();
   }

   public ByteBuffer getPixels() {
      if (this.disposed) {
         throw new GdxRuntimeException("Pixmap already disposed");
      } else {
         return this.pixmap.getPixels();
      }
   }

   public Pixmap.Format getFormat() {
      return Pixmap.Format.fromGdx2DPixmapFormat(this.pixmap.getFormat());
   }

   public static Pixmap.Blending getBlending() {
      return blending;
   }

   public static enum Blending {
      None,
      SourceOver;
   }

   public static enum Filter {
      NearestNeighbour,
      BiLinear;
   }

   public static enum Format {
      Alpha,
      Intensity,
      LuminanceAlpha,
      RGB565,
      RGBA4444,
      RGB888,
      RGBA8888;

      public static int toGdx2DPixmapFormat(Pixmap.Format format) {
         if (format == Alpha) {
            return 1;
         } else if (format == Intensity) {
            return 1;
         } else if (format == LuminanceAlpha) {
            return 2;
         } else if (format == RGB565) {
            return 5;
         } else if (format == RGBA4444) {
            return 6;
         } else if (format == RGB888) {
            return 3;
         } else if (format == RGBA8888) {
            return 4;
         } else {
            throw new GdxRuntimeException("Unknown Format: " + format);
         }
      }

      public static Pixmap.Format fromGdx2DPixmapFormat(int format) {
         if (format == 1) {
            return Alpha;
         } else if (format == 2) {
            return LuminanceAlpha;
         } else if (format == 5) {
            return RGB565;
         } else if (format == 6) {
            return RGBA4444;
         } else if (format == 3) {
            return RGB888;
         } else if (format == 4) {
            return RGBA8888;
         } else {
            throw new GdxRuntimeException("Unknown Gdx2DPixmap Format: " + format);
         }
      }
   }
}
