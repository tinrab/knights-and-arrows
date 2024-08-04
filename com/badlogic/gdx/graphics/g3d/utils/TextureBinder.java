package com.badlogic.gdx.graphics.g3d.utils;

public interface TextureBinder {
   void begin();

   void end();

   int bind(TextureDescriptor var1);

   int getBindCount();

   int getReuseCount();

   void resetCounts();
}
