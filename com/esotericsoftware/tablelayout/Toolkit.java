package com.esotericsoftware.tablelayout;

public abstract class Toolkit<C, T extends C, L extends BaseTableLayout> {
   public static Toolkit instance;

   public abstract Cell obtainCell(L var1);

   public abstract void freeCell(Cell var1);

   public abstract void addChild(C var1, C var2);

   public abstract void removeChild(C var1, C var2);

   public abstract float getMinWidth(C var1);

   public abstract float getMinHeight(C var1);

   public abstract float getPrefWidth(C var1);

   public abstract float getPrefHeight(C var1);

   public abstract float getMaxWidth(C var1);

   public abstract float getMaxHeight(C var1);

   public abstract float getWidth(C var1);

   public abstract float getHeight(C var1);

   public abstract void clearDebugRectangles(L var1);

   public abstract void addDebugRectangle(L var1, BaseTableLayout.Debug var2, float var3, float var4, float var5, float var6);

   public void setWidget(L layout, Cell cell, C widget) {
      if (cell.widget != widget) {
         this.removeChild(layout.table, cell.widget);
         cell.widget = widget;
         if (widget != null) {
            this.addChild(layout.table, widget);
         }

      }
   }

   public float width(float value) {
      return value;
   }

   public float height(float value) {
      return value;
   }
}
