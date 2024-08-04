package com.badlogic.gdx.graphics;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class PixmapIO {
   public static void writeCIM(FileHandle file, Pixmap pixmap) {
      PixmapIO.CIM.write(file, pixmap);
   }

   public static Pixmap readCIM(FileHandle file) {
      return PixmapIO.CIM.read(file);
   }

   public static void writePNG(FileHandle file, Pixmap pixmap) {
      try {
         file.writeBytes(PixmapIO.PNG.write(pixmap), false);
      } catch (IOException var3) {
         throw new GdxRuntimeException("Error writing PNG: " + file, var3);
      }
   }

   private static class CIM {
      private static final int BUFFER_SIZE = 32000;
      private static final byte[] writeBuffer = new byte[32000];
      private static final byte[] readBuffer = new byte[32000];

      public static void write(FileHandle file, Pixmap pixmap) {
         DataOutputStream out = null;

         try {
            DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(file.write(false));
            out = new DataOutputStream(deflaterOutputStream);
            out.writeInt(pixmap.getWidth());
            out.writeInt(pixmap.getHeight());
            out.writeInt(Pixmap.Format.toGdx2DPixmapFormat(pixmap.getFormat()));
            ByteBuffer pixelBuf = pixmap.getPixels();
            pixelBuf.position(0);
            pixelBuf.limit(pixelBuf.capacity());
            int remainingBytes = pixelBuf.capacity() % 32000;
            int iterations = pixelBuf.capacity() / 32000;
            synchronized(writeBuffer) {
               for(int i = 0; i < iterations; ++i) {
                  pixelBuf.get(writeBuffer);
                  out.write(writeBuffer);
               }

               pixelBuf.get(writeBuffer, 0, remainingBytes);
               out.write(writeBuffer, 0, remainingBytes);
            }

            pixelBuf.position(0);
            pixelBuf.limit(pixelBuf.capacity());
         } catch (Exception var18) {
            throw new GdxRuntimeException("Couldn't write Pixmap to file '" + file + "'", var18);
         } finally {
            if (out != null) {
               try {
                  out.close();
               } catch (Exception var16) {
               }
            }

         }

      }

      public static Pixmap read(FileHandle file) {
         DataInputStream in = null;

         Pixmap var10;
         try {
            in = new DataInputStream(new InflaterInputStream(new BufferedInputStream(file.read())));
            int width = in.readInt();
            int height = in.readInt();
            Pixmap.Format format = Pixmap.Format.fromGdx2DPixmapFormat(in.readInt());
            Pixmap pixmap = new Pixmap(width, height, format);
            ByteBuffer pixelBuf = pixmap.getPixels();
            pixelBuf.position(0);
            pixelBuf.limit(pixelBuf.capacity());
            synchronized(readBuffer) {
               boolean var8 = false;

               int readBytes;
               while((readBytes = in.read(readBuffer)) > 0) {
                  pixelBuf.put(readBuffer, 0, readBytes);
               }
            }

            pixelBuf.position(0);
            pixelBuf.limit(pixelBuf.capacity());
            var10 = pixmap;
         } catch (Exception var19) {
            throw new GdxRuntimeException("Couldn't read Pixmap from file '" + file + "'", var19);
         } finally {
            if (in != null) {
               try {
                  in.close();
               } catch (Exception var17) {
               }
            }

         }

         return var10;
      }
   }

   private static class PNG {
      static int[] crcTable;
      static final int ZLIB_BLOCK_SIZE = 32000;

      static byte[] write(Pixmap pixmap) throws IOException {
         byte[] signature = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10};
         byte[] header = createHeaderChunk(pixmap.getWidth(), pixmap.getHeight());
         byte[] data = createDataChunk(pixmap);
         byte[] trailer = createTrailerChunk();
         ByteArrayOutputStream png = new ByteArrayOutputStream(signature.length + header.length + data.length + trailer.length);
         png.write(signature);
         png.write(header);
         png.write(data);
         png.write(trailer);
         return png.toByteArray();
      }

      private static byte[] createHeaderChunk(int width, int height) throws IOException {
         ByteArrayOutputStream baos = new ByteArrayOutputStream(13);
         DataOutputStream chunk = new DataOutputStream(baos);
         chunk.writeInt(width);
         chunk.writeInt(height);
         chunk.writeByte(8);
         chunk.writeByte(6);
         chunk.writeByte(0);
         chunk.writeByte(0);
         chunk.writeByte(0);
         return toChunk("IHDR", baos.toByteArray());
      }

      private static byte[] createDataChunk(Pixmap pixmap) throws IOException {
         int width = pixmap.getWidth();
         int height = pixmap.getHeight();
         int dest = 0;
         byte[] raw = new byte[4 * width * height + height];

         for(int y = 0; y < height; ++y) {
            raw[dest++] = 0;

            for(int x = 0; x < width; ++x) {
               int pixel = pixmap.getPixel(x, y);
               int mask = pixel & -1;
               int rr = mask >> 24 & 255;
               int gg = mask >> 16 & 255;
               int bb = mask >> 8 & 255;
               int aa = mask & 255;
               raw[dest++] = (byte)rr;
               raw[dest++] = (byte)gg;
               raw[dest++] = (byte)bb;
               raw[dest++] = (byte)aa;
            }
         }

         return toChunk("IDAT", toZLIB(raw));
      }

      private static byte[] createTrailerChunk() throws IOException {
         return toChunk("IEND", new byte[0]);
      }

      private static byte[] toChunk(String id, byte[] raw) throws IOException {
         ByteArrayOutputStream baos = new ByteArrayOutputStream(raw.length + 12);
         DataOutputStream chunk = new DataOutputStream(baos);
         chunk.writeInt(raw.length);
         byte[] bid = new byte[4];

         int crc;
         for(crc = 0; crc < 4; ++crc) {
            bid[crc] = (byte)id.charAt(crc);
         }

         chunk.write(bid);
         chunk.write(raw);
         int crc = -1;
         crc = updateCRC(crc, bid);
         crc = updateCRC(crc, raw);
         chunk.writeInt(~crc);
         return baos.toByteArray();
      }

      private static void createCRCTable() {
         crcTable = new int[256];

         for(int i = 0; i < 256; ++i) {
            int c = i;

            for(int k = 0; k < 8; ++k) {
               c = (c & 1) > 0 ? -306674912 ^ c >>> 1 : c >>> 1;
            }

            crcTable[i] = c;
         }

      }

      private static int updateCRC(int crc, byte[] raw) {
         if (crcTable == null) {
            createCRCTable();
         }

         byte[] var5 = raw;
         int var4 = raw.length;

         for(int var3 = 0; var3 < var4; ++var3) {
            byte element = var5[var3];
            crc = crcTable[(crc ^ element) & 255] ^ crc >>> 8;
         }

         return crc;
      }

      private static byte[] toZLIB(byte[] raw) throws IOException {
         ByteArrayOutputStream baos = new ByteArrayOutputStream(raw.length + 6 + raw.length / 32000 * 5);
         DataOutputStream zlib = new DataOutputStream(baos);
         byte tmp = 8;
         zlib.writeByte(tmp);
         zlib.writeByte((31 - (tmp << 8) % 31) % 31);

         int pos;
         for(pos = 0; raw.length - pos > 32000; pos += 32000) {
            writeUncompressedDeflateBlock(zlib, false, raw, pos, '紀');
         }

         writeUncompressedDeflateBlock(zlib, true, raw, pos, (char)(raw.length - pos));
         zlib.writeInt(calcADLER32(raw));
         return baos.toByteArray();
      }

      private static void writeUncompressedDeflateBlock(DataOutputStream zlib, boolean last, byte[] raw, int off, char len) throws IOException {
         zlib.writeByte((byte)(last ? 1 : 0));
         zlib.writeByte((byte)(len & 255));
         zlib.writeByte((byte)((len & '\uff00') >> 8));
         zlib.writeByte((byte)(~len & 255));
         zlib.writeByte((byte)((~len & '\uff00') >> 8));
         zlib.write(raw, off, len);
      }

      private static int calcADLER32(byte[] raw) {
         int s1 = 1;
         int s2 = 0;

         for(int i = 0; i < raw.length; ++i) {
            int abs = raw[i] >= 0 ? raw[i] : raw[i] + 256;
            s1 = (s1 + abs) % '\ufff1';
            s2 = (s2 + s1) % '\ufff1';
         }

         return (s2 << 16) + s1;
      }
   }
}
