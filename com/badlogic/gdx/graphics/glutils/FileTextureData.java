package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class FileTextureData implements TextureData {
   public static boolean copyToPOT;
   final FileHandle file;
   int width = 0;
   int height = 0;
   Pixmap.Format format;
   Pixmap pixmap;
   boolean useMipMaps;
   boolean isPrepared = false;

   public FileTextureData(FileHandle file, Pixmap preloadedPixmap, Pixmap.Format format, boolean useMipMaps) {
      this.file = file;
      this.pixmap = preloadedPixmap;
      this.format = format;
      this.useMipMaps = useMipMaps;
      if (this.pixmap != null) {
         this.pixmap = this.ensurePot(this.pixmap);
         this.width = this.pixmap.getWidth();
         this.height = this.pixmap.getHeight();
         if (format == null) {
            this.format = this.pixmap.getFormat();
         }
      }

   }

   public boolean isPrepared() {
      return this.isPrepared;
   }

   public void prepare() {
      if (this.isPrepared) {
         throw new GdxRuntimeException("Already prepared");
      } else {
         if (this.pixmap == null) {
            if (this.file.extension().equals("cim")) {
               this.pixmap = PixmapIO.readCIM(this.file);
            } else {
               this.pixmap = this.ensurePot(new Pixmap(this.file));
            }

            this.width = this.pixmap.getWidth();
            this.height = this.pixmap.getHeight();
            if (this.format == null) {
               this.format = this.pixmap.getFormat();
            }
         }

         this.isPrepared = true;
      }
   }

   private Pixmap ensurePot(Pixmap pixmap) {
      if (Gdx.gl20 == null && copyToPOT) {
         int pixmapWidth = pixmap.getWidth();
         int pixmapHeight = pixmap.getHeight();
         int potWidth = MathUtils.nextPowerOfTwo(pixmapWidth);
         int potHeight = MathUtils.nextPowerOfTwo(pixmapHeight);
         if (pixmapWidth != potWidth || pixmapHeight != potHeight) {
            Pixmap tmp = new Pixmap(potWidth, potHeight, pixmap.getFormat());
            tmp.drawPixmap(pixmap, 0, 0, 0, 0, pixmapWidth, pixmapHeight);
            pixmap.dispose();
            return tmp;
         }
      }

      return pixmap;
   }

   public Pixmap consumePixmap() {
      if (!this.isPrepared) {
         throw new GdxRuntimeException("Call prepare() before calling getPixmap()");
      } else {
         this.isPrepared = false;
         Pixmap pixmap = this.pixmap;
         this.pixmap = null;
         return pixmap;
      }
   }

   public boolean disposePixmap() {
      return true;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public Pixmap.Format getFormat() {
      return this.format;
   }

   public boolean useMipMaps() {
      return this.useMipMaps;
   }

   public boolean isManaged() {
      return true;
   }

   public FileHandle getFileHandle() {
      return this.file;
   }

   public TextureData.TextureDataType getType() {
      return TextureData.TextureDataType.Pixmap;
   }

   public void consumeCompressedData() {
      throw new GdxRuntimeException("This TextureData implementation does not upload data itself");
   }
}
