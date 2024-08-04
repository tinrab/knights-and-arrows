package com.badlogic.gdx.scenes.scene2d.utils;

public interface Layout {
   void layout();

   void invalidate();

   void invalidateHierarchy();

   void validate();

   void pack();

   void setFillParent(boolean var1);

   void setLayoutEnabled(boolean var1);

   float getMinWidth();

   float getMinHeight();

   float getPrefWidth();

   float getPrefHeight();

   float getMaxWidth();

   float getMaxHeight();
}
