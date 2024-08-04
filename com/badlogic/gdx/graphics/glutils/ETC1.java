package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.ByteBuffer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ETC1 {
   public static int PKM_HEADER_SIZE = 16;
   public static int ETC1_RGB8_OES = 36196;

   private static int getPixelSize(Pixmap.Format format) {
      if (format == Pixmap.Format.RGB565) {
         return 2;
      } else if (format == Pixmap.Format.RGB888) {
         return 3;
      } else {
         throw new GdxRuntimeException("Can only handle RGB565 or RGB888 images");
      }
   }

   public static ETC1.ETC1Data encodeImage(Pixmap pixmap) {
      int pixelSize = getPixelSize(pixmap.getFormat());
      ByteBuffer compressedData = encodeImage(pixmap.getPixels(), 0, pixmap.getWidth(), pixmap.getHeight(), pixelSize);
      BufferUtils.newUnsafeByteBuffer(compressedData);
      return new ETC1.ETC1Data(pixmap.getWidth(), pixmap.getHeight(), compressedData, 0);
   }

   public static ETC1.ETC1Data encodeImagePKM(Pixmap pixmap) {
      int pixelSize = getPixelSize(pixmap.getFormat());
      ByteBuffer compressedData = encodeImagePKM(pixmap.getPixels(), 0, pixmap.getWidth(), pixmap.getHeight(), pixelSize);
      BufferUtils.newUnsafeByteBuffer(compressedData);
      return new ETC1.ETC1Data(pixmap.getWidth(), pixmap.getHeight(), compressedData, 16);
   }

   public static Pixmap decodeImage(ETC1.ETC1Data etc1Data, Pixmap.Format format) {
      int dataOffset = false;
      int width = false;
      int height = false;
      byte dataOffset;
      int width;
      int height;
      if (etc1Data.hasPKMHeader()) {
         dataOffset = 16;
         width = getWidthPKM(etc1Data.compressedData, 0);
         height = getHeightPKM(etc1Data.compressedData, 0);
      } else {
         dataOffset = 0;
         width = etc1Data.width;
         height = etc1Data.height;
      }

      int pixelSize = getPixelSize(format);
      Pixmap pixmap = new Pixmap(width, height, format);
      decodeImage(etc1Data.compressedData, dataOffset, pixmap.getPixels(), 0, width, height, pixelSize);
      return pixmap;
   }

   public static native int getCompressedDataSize(int var0, int var1);

   public static native void formatHeader(ByteBuffer var0, int var1, int var2, int var3);

   static native int getWidthPKM(ByteBuffer var0, int var1);

   static native int getHeightPKM(ByteBuffer var0, int var1);

   static native boolean isValidPKM(ByteBuffer var0, int var1);

   private static native void decodeImage(ByteBuffer var0, int var1, ByteBuffer var2, int var3, int var4, int var5, int var6);

   private static native ByteBuffer encodeImage(ByteBuffer var0, int var1, int var2, int var3, int var4);

   private static native ByteBuffer encodeImagePKM(ByteBuffer var0, int var1, int var2, int var3, int var4);

   public static final class ETC1Data implements Disposable {
      public final int width;
      public final int height;
      public final ByteBuffer compressedData;
      public final int dataOffset;

      public ETC1Data(int width, int height, ByteBuffer compressedData, int dataOffset) {
         this.width = width;
         this.height = height;
         this.compressedData = compressedData;
         this.dataOffset = dataOffset;
         this.checkNPOT();
      }

      public ETC1Data(FileHandle pkmFile) {
         byte[] buffer = new byte[10240];
         DataInputStream in = null;

         try {
            in = new DataInputStream(new BufferedInputStream(new GZIPInputStream(pkmFile.read())));
            int fileSize = in.readInt();
            this.compressedData = BufferUtils.newUnsafeByteBuffer(fileSize);
            boolean var5 = false;

            while(true) {
               int readBytes;
               if ((readBytes = in.read(buffer)) == -1) {
                  this.compressedData.position(0);
                  this.compressedData.limit(this.compressedData.capacity());
                  break;
               }

               this.compressedData.put(buffer, 0, readBytes);
            }
         } catch (Exception var13) {
            throw new GdxRuntimeException("Couldn't load pkm file '" + pkmFile + "'", var13);
         } finally {
            if (in != null) {
               try {
                  in.close();
               } catch (Exception var12) {
               }
            }

         }

         this.width = ETC1.getWidthPKM(this.compressedData, 0);
         this.height = ETC1.getHeightPKM(this.compressedData, 0);
         this.dataOffset = ETC1.PKM_HEADER_SIZE;
         this.compressedData.position(this.dataOffset);
         this.checkNPOT();
      }

      private void checkNPOT() {
         if (!MathUtils.isPowerOfTwo(this.width) || !MathUtils.isPowerOfTwo(this.height)) {
            Gdx.app.debug("ETC1Data", "warning: non-power-of-two ETC1 textures may crash the driver of PowerVR GPUs");
         }

      }

      public boolean hasPKMHeader() {
         return this.dataOffset == 16;
      }

      public void write(FileHandle file) {
         DataOutputStream write = null;
         byte[] buffer = new byte[10240];
         int writtenBytes = 0;
         this.compressedData.position(0);
         this.compressedData.limit(this.compressedData.capacity());

         try {
            write = new DataOutputStream(new GZIPOutputStream(file.write(false)));
            write.writeInt(this.compressedData.capacity());

            while(writtenBytes != this.compressedData.capacity()) {
               int bytesToWrite = Math.min(this.compressedData.remaining(), buffer.length);
               this.compressedData.get(buffer, 0, bytesToWrite);
               write.write(buffer, 0, bytesToWrite);
               writtenBytes += bytesToWrite;
            }
         } catch (Exception var13) {
            throw new GdxRuntimeException("Couldn't write PKM file to '" + file + "'", var13);
         } finally {
            if (write != null) {
               try {
                  write.close();
               } catch (Exception var12) {
               }
            }

         }

         this.compressedData.position(this.dataOffset);
         this.compressedData.limit(this.compressedData.capacity());
      }

      public void dispose() {
         BufferUtils.disposeUnsafeByteBuffer(this.compressedData);
      }

      public String toString() {
         return this.hasPKMHeader() ? (ETC1.isValidPKM(this.compressedData, 0) ? "valid" : "invalid") + " pkm [" + ETC1.getWidthPKM(this.compressedData, 0) + "x" + ETC1.getHeightPKM(this.compressedData, 0) + "], compressed: " + (this.compressedData.capacity() - ETC1.PKM_HEADER_SIZE) : "raw [" + this.width + "x" + this.height + "], compressed: " + (this.compressedData.capacity() - ETC1.PKM_HEADER_SIZE);
      }
   }
}
