package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.math.Matrix4;

public interface ImmediateModeRenderer {
   void begin(Matrix4 var1, int var2);

   void color(float var1, float var2, float var3, float var4);

   void texCoord(float var1, float var2);

   void normal(float var1, float var2, float var3);

   void vertex(float var1, float var2, float var3);

   void end();

   int getNumVertices();

   int getMaxVertices();

   void dispose();
}
