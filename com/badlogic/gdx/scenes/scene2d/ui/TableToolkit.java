package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.esotericsoftware.tablelayout.BaseTableLayout;
import com.esotericsoftware.tablelayout.Cell;
import com.esotericsoftware.tablelayout.Toolkit;

public class TableToolkit extends Toolkit<Actor, Table, TableLayout> {
   static boolean drawDebug;
   static Pool<Cell> cellPool = new Pool() {
      protected Cell newObject() {
         return new Cell();
      }
   };

   public Cell obtainCell(TableLayout layout) {
      Cell cell = (Cell)cellPool.obtain();
      cell.setLayout(layout);
      return cell;
   }

   public void freeCell(Cell cell) {
      cell.free();
      cellPool.free(cell);
   }

   public void addChild(Actor parent, Actor child) {
      child.remove();
      ((Group)parent).addActor(child);
   }

   public void removeChild(Actor parent, Actor child) {
      ((Group)parent).removeActor(child);
   }

   public float getMinWidth(Actor actor) {
      return actor instanceof Layout ? ((Layout)actor).getMinWidth() : actor.getWidth();
   }

   public float getMinHeight(Actor actor) {
      return actor instanceof Layout ? ((Layout)actor).getMinHeight() : actor.getHeight();
   }

   public float getPrefWidth(Actor actor) {
      return actor instanceof Layout ? ((Layout)actor).getPrefWidth() : actor.getWidth();
   }

   public float getPrefHeight(Actor actor) {
      return actor instanceof Layout ? ((Layout)actor).getPrefHeight() : actor.getHeight();
   }

   public float getMaxWidth(Actor actor) {
      return actor instanceof Layout ? ((Layout)actor).getMaxWidth() : 0.0F;
   }

   public float getMaxHeight(Actor actor) {
      return actor instanceof Layout ? ((Layout)actor).getMaxHeight() : 0.0F;
   }

   public float getWidth(Actor widget) {
      return widget.getWidth();
   }

   public float getHeight(Actor widget) {
      return widget.getHeight();
   }

   public void clearDebugRectangles(TableLayout layout) {
      if (layout.debugRects != null) {
         layout.debugRects.clear();
      }

   }

   public void addDebugRectangle(TableLayout layout, BaseTableLayout.Debug type, float x, float y, float w, float h) {
      drawDebug = true;
      if (layout.debugRects == null) {
         layout.debugRects = new Array();
      }

      layout.debugRects.add(new TableToolkit.DebugRect(type, x, ((Table)layout.getTable()).getHeight() - y, w, h));
   }

   static class DebugRect extends Rectangle {
      final BaseTableLayout.Debug type;

      public DebugRect(BaseTableLayout.Debug type, float x, float y, float width, float height) {
         super(x, y, width, height);
         this.type = type;
      }
   }
}
