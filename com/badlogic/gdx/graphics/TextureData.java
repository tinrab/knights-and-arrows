package com.badlogic.gdx.graphics;

public interface TextureData {
   TextureData.TextureDataType getType();

   boolean isPrepared();

   void prepare();

   Pixmap consumePixmap();

   boolean disposePixmap();

   void consumeCompressedData();

   int getWidth();

   int getHeight();

   Pixmap.Format getFormat();

   boolean useMipMaps();

   boolean isManaged();

   public static enum TextureDataType {
      Pixmap,
      Compressed,
      Float;
   }
}
