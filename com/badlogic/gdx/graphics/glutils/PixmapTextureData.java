package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class PixmapTextureData implements TextureData {
   final Pixmap pixmap;
   final Pixmap.Format format;
   final boolean useMipMaps;
   final boolean disposePixmap;

   public PixmapTextureData(Pixmap pixmap, Pixmap.Format format, boolean useMipMaps, boolean disposePixmap) {
      this.pixmap = pixmap;
      this.format = format == null ? pixmap.getFormat() : format;
      this.useMipMaps = useMipMaps;
      this.disposePixmap = disposePixmap;
   }

   public boolean disposePixmap() {
      return this.disposePixmap;
   }

   public Pixmap consumePixmap() {
      return this.pixmap;
   }

   public int getWidth() {
      return this.pixmap.getWidth();
   }

   public int getHeight() {
      return this.pixmap.getHeight();
   }

   public Pixmap.Format getFormat() {
      return this.format;
   }

   public boolean useMipMaps() {
      return this.useMipMaps;
   }

   public boolean isManaged() {
      return false;
   }

   public TextureData.TextureDataType getType() {
      return TextureData.TextureDataType.Pixmap;
   }

   public void consumeCompressedData() {
      throw new GdxRuntimeException("This TextureData implementation does not upload data itself");
   }

   public boolean isPrepared() {
      return true;
   }

   public void prepare() {
      throw new GdxRuntimeException("prepare() must not be called on a PixmapTextureData instance as it is already prepared.");
   }
}
