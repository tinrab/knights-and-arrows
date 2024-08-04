package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.utils.Disposable;
import java.nio.ShortBuffer;

public interface IndexData extends Disposable {
   int getNumIndices();

   int getNumMaxIndices();

   void setIndices(short[] var1, int var2, int var3);

   ShortBuffer getBuffer();

   void bind();

   void unbind();

   void invalidate();

   void dispose();
}
