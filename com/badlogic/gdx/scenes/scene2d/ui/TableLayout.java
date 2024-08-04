package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer10;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.tablelayout.BaseTableLayout;
import com.esotericsoftware.tablelayout.Cell;
import com.esotericsoftware.tablelayout.Toolkit;

class TableLayout extends BaseTableLayout<Actor, Table, TableLayout, TableToolkit> {
   Array<TableToolkit.DebugRect> debugRects;
   private ImmediateModeRenderer debugRenderer;
   boolean round = true;

   public TableLayout() {
      super((TableToolkit)Toolkit.instance);
   }

   public void layout() {
      Table table = (Table)this.getTable();
      float width = table.getWidth();
      float height = table.getHeight();
      super.layout(0.0F, 0.0F, width, height);
      java.util.List<Cell> cells = this.getCells();
      int i;
      int i;
      Cell c;
      float widgetWidth;
      float widgetHeight;
      float widgetX;
      float widgetY;
      Actor actor;
      if (this.round) {
         i = 0;

         for(i = cells.size(); i < i; ++i) {
            c = (Cell)cells.get(i);
            if (!c.getIgnore()) {
               widgetWidth = (float)Math.round(c.getWidgetWidth());
               widgetHeight = (float)Math.round(c.getWidgetHeight());
               widgetX = (float)Math.round(c.getWidgetX());
               widgetY = height - (float)Math.round(c.getWidgetY()) - widgetHeight;
               c.setWidgetX(widgetX);
               c.setWidgetY(widgetY);
               c.setWidgetWidth(widgetWidth);
               c.setWidgetHeight(widgetHeight);
               actor = (Actor)c.getWidget();
               if (actor != null) {
                  actor.setX(widgetX);
                  actor.setY(widgetY);
                  if (actor.getWidth() != widgetWidth || actor.getHeight() != widgetHeight) {
                     actor.setWidth(widgetWidth);
                     actor.setHeight(widgetHeight);
                     if (actor instanceof Layout) {
                        ((Layout)actor).invalidate();
                     }
                  }
               }
            }
         }
      } else {
         i = 0;

         for(i = cells.size(); i < i; ++i) {
            c = (Cell)cells.get(i);
            if (!c.getIgnore()) {
               widgetWidth = c.getWidgetWidth();
               widgetHeight = c.getWidgetHeight();
               widgetX = c.getWidgetX();
               widgetY = height - c.getWidgetY() - widgetHeight;
               c.setWidgetX(widgetX);
               c.setWidgetY(widgetY);
               c.setWidgetWidth(widgetWidth);
               c.setWidgetHeight(widgetHeight);
               actor = (Actor)c.getWidget();
               if (actor != null) {
                  actor.setX(widgetX);
                  actor.setY(widgetY);
                  if (actor.getWidth() != widgetWidth || actor.getHeight() != widgetHeight) {
                     actor.setWidth(widgetWidth);
                     actor.setHeight(widgetHeight);
                     if (actor instanceof Layout) {
                        ((Layout)actor).invalidate();
                     }
                  }
               }
            }
         }
      }

      Array<Actor> children = table.getChildren();
      i = 0;

      for(int n = children.size; i < n; ++i) {
         Actor child = (Actor)children.get(i);
         if (child instanceof Layout) {
            ((Layout)child).validate();
         }
      }

   }

   public void invalidateHierarchy() {
      super.invalidate();
      ((Table)this.getTable()).invalidateHierarchy();
   }

   private void toStageCoordinates(Actor actor, Vector2 point) {
      point.x += actor.getX();
      point.y += actor.getY();
      this.toStageCoordinates(actor.getParent(), point);
   }

   public void drawDebug(SpriteBatch batch) {
      if (this.getDebug() != BaseTableLayout.Debug.none && this.debugRects != null) {
         if (this.debugRenderer == null) {
            if (Gdx.graphics.isGL20Available()) {
               this.debugRenderer = new ImmediateModeRenderer20(64, false, true, 0);
            } else {
               this.debugRenderer = new ImmediateModeRenderer10(64);
            }
         }

         float x = 0.0F;
         float y = 0.0F;

         for(Object parent = (Actor)this.getTable(); parent != null; parent = ((Actor)parent).getParent()) {
            if (parent instanceof Group) {
               x += ((Actor)parent).getX();
               y += ((Actor)parent).getY();
            }
         }

         this.debugRenderer.begin(batch.getProjectionMatrix(), 1);
         int i = 0;

         for(int n = this.debugRects.size; i < n; ++i) {
            TableToolkit.DebugRect rect = (TableToolkit.DebugRect)this.debugRects.get(i);
            float x1 = x + rect.x;
            float y1 = y + rect.y - rect.height;
            float x2 = x1 + rect.width;
            float y2 = y1 + rect.height;
            float r = (float)(rect.type == BaseTableLayout.Debug.cell ? 1 : 0);
            float g = (float)(rect.type == BaseTableLayout.Debug.widget ? 1 : 0);
            float b = (float)(rect.type == BaseTableLayout.Debug.table ? 1 : 0);
            this.debugRenderer.color(r, g, b, 1.0F);
            this.debugRenderer.vertex(x1, y1, 0.0F);
            this.debugRenderer.color(r, g, b, 1.0F);
            this.debugRenderer.vertex(x1, y2, 0.0F);
            this.debugRenderer.color(r, g, b, 1.0F);
            this.debugRenderer.vertex(x1, y2, 0.0F);
            this.debugRenderer.color(r, g, b, 1.0F);
            this.debugRenderer.vertex(x2, y2, 0.0F);
            this.debugRenderer.color(r, g, b, 1.0F);
            this.debugRenderer.vertex(x2, y2, 0.0F);
            this.debugRenderer.color(r, g, b, 1.0F);
            this.debugRenderer.vertex(x2, y1, 0.0F);
            this.debugRenderer.color(r, g, b, 1.0F);
            this.debugRenderer.vertex(x2, y1, 0.0F);
            this.debugRenderer.color(r, g, b, 1.0F);
            this.debugRenderer.vertex(x1, y1, 0.0F);
            if (this.debugRenderer.getNumVertices() == 64) {
               this.debugRenderer.end();
               this.debugRenderer.begin(batch.getProjectionMatrix(), 1);
            }
         }

         this.debugRenderer.end();
      }
   }
}
