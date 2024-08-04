package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface Drawable {
   void draw(SpriteBatch var1, float var2, float var3, float var4, float var5);

   float getLeftWidth();

   void setLeftWidth(float var1);

   float getRightWidth();

   void setRightWidth(float var1);

   float getTopHeight();

   void setTopHeight(float var1);

   float getBottomHeight();

   void setBottomHeight(float var1);

   float getMinWidth();

   void setMinWidth(float var1);

   float getMinHeight();

   void setMinHeight(float var1);
}
