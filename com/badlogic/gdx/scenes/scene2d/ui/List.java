package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Cullable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pools;

public class List extends Widget implements Cullable {
   private List.ListStyle style;
   private String[] items;
   private int selectedIndex;
   private Rectangle cullingArea;
   private float prefWidth;
   private float prefHeight;
   private float itemHeight;
   private float textOffsetX;
   private float textOffsetY;
   private boolean selectable;

   public List(Object[] items, Skin skin) {
      this(items, (List.ListStyle)skin.get(List.ListStyle.class));
   }

   public List(Object[] items, Skin skin, String styleName) {
      this(items, (List.ListStyle)skin.get(styleName, List.ListStyle.class));
   }

   public List(Object[] items, List.ListStyle style) {
      this.selectable = true;
      this.setStyle(style);
      this.setItems(items);
      this.setWidth(this.getPrefWidth());
      this.setHeight(this.getPrefHeight());
      this.addListener(new InputListener() {
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            if (pointer == 0 && button != 0) {
               return false;
            } else if (!List.this.isSelectable()) {
               return false;
            } else {
               List.this.touchDown(y);
               return true;
            }
         }
      });
   }

   public void setSelectable(boolean selectable) {
      this.selectable = selectable;
   }

   public boolean isSelectable() {
      return this.selectable;
   }

   void touchDown(float y) {
      int oldIndex = this.selectedIndex;
      this.selectedIndex = (int)((this.getHeight() - y) / this.itemHeight);
      this.selectedIndex = Math.max(0, this.selectedIndex);
      this.selectedIndex = Math.min(this.items.length - 1, this.selectedIndex);
      if (oldIndex != this.selectedIndex) {
         ChangeListener.ChangeEvent changeEvent = (ChangeListener.ChangeEvent)Pools.obtain(ChangeListener.ChangeEvent.class);
         if (this.fire(changeEvent)) {
            this.selectedIndex = oldIndex;
         }

         Pools.free(changeEvent);
      }

   }

   public void setStyle(List.ListStyle style) {
      if (style == null) {
         throw new IllegalArgumentException("style cannot be null.");
      } else {
         this.style = style;
         if (this.items != null) {
            this.setItems(this.items);
         } else {
            this.invalidateHierarchy();
         }

      }
   }

   public List.ListStyle getStyle() {
      return this.style;
   }

   public void draw(SpriteBatch batch, float parentAlpha) {
      BitmapFont font = this.style.font;
      Drawable selectedDrawable = this.style.selection;
      Color fontColorSelected = this.style.fontColorSelected;
      Color fontColorUnselected = this.style.fontColorUnselected;
      Color color = this.getColor();
      batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
      float x = this.getX();
      float y = this.getY();
      font.setColor(fontColorUnselected.r, fontColorUnselected.g, fontColorUnselected.b, fontColorUnselected.a * parentAlpha);
      float itemY = this.getHeight();

      for(int i = 0; i < this.items.length; ++i) {
         if (this.cullingArea != null && (!(itemY - this.itemHeight <= this.cullingArea.y + this.cullingArea.height) || !(itemY >= this.cullingArea.y))) {
            if (itemY < this.cullingArea.y) {
               break;
            }
         } else {
            if (this.selectedIndex == i) {
               selectedDrawable.draw(batch, x, y + itemY - this.itemHeight, this.getWidth(), this.itemHeight);
               font.setColor(fontColorSelected.r, fontColorSelected.g, fontColorSelected.b, fontColorSelected.a * parentAlpha);
            }

            font.draw(batch, this.items[i], x + this.textOffsetX, y + itemY - this.textOffsetY);
            if (this.selectedIndex == i) {
               font.setColor(fontColorUnselected.r, fontColorUnselected.g, fontColorUnselected.b, fontColorUnselected.a * parentAlpha);
            }
         }

         itemY -= this.itemHeight;
      }

   }

   public int getSelectedIndex() {
      return this.selectedIndex;
   }

   public void setSelectedIndex(int index) {
      if (index >= -1 && index < this.items.length) {
         this.selectedIndex = index;
      } else {
         throw new GdxRuntimeException("index must be >= -1 and < " + this.items.length + ": " + index);
      }
   }

   public String getSelection() {
      return this.items.length != 0 && this.selectedIndex != -1 ? this.items[this.selectedIndex] : null;
   }

   public int setSelection(String item) {
      this.selectedIndex = -1;
      int i = 0;

      for(int n = this.items.length; i < n; ++i) {
         if (this.items[i].equals(item)) {
            this.selectedIndex = i;
            break;
         }
      }

      return this.selectedIndex;
   }

   public void setItems(Object[] objects) {
      if (objects == null) {
         throw new IllegalArgumentException("items cannot be null.");
      } else {
         int i;
         if (!(objects instanceof String[])) {
            String[] strings = new String[objects.length];
            int i = 0;

            for(i = objects.length; i < i; ++i) {
               strings[i] = String.valueOf(objects[i]);
            }

            this.items = strings;
         } else {
            this.items = (String[])objects;
         }

         this.selectedIndex = 0;
         BitmapFont font = this.style.font;
         Drawable selectedDrawable = this.style.selection;
         this.itemHeight = font.getCapHeight() - font.getDescent() * 2.0F;
         this.itemHeight += selectedDrawable.getTopHeight() + selectedDrawable.getBottomHeight();
         this.textOffsetX = selectedDrawable.getLeftWidth();
         this.textOffsetY = selectedDrawable.getTopHeight() - font.getDescent();
         this.prefWidth = 0.0F;

         for(i = 0; i < this.items.length; ++i) {
            BitmapFont.TextBounds bounds = font.getBounds(this.items[i]);
            this.prefWidth = Math.max(bounds.width, this.prefWidth);
         }

         this.prefWidth += selectedDrawable.getLeftWidth() + selectedDrawable.getRightWidth();
         this.prefHeight = (float)this.items.length * this.itemHeight;
         this.invalidateHierarchy();
      }
   }

   public String[] getItems() {
      return this.items;
   }

   public float getItemHeight() {
      return this.itemHeight;
   }

   public float getPrefWidth() {
      return this.prefWidth;
   }

   public float getPrefHeight() {
      return this.prefHeight;
   }

   public void setCullingArea(Rectangle cullingArea) {
      this.cullingArea = cullingArea;
   }

   public static class ListStyle {
      public BitmapFont font;
      public Color fontColorSelected = new Color(1.0F, 1.0F, 1.0F, 1.0F);
      public Color fontColorUnselected = new Color(1.0F, 1.0F, 1.0F, 1.0F);
      public Drawable selection;

      public ListStyle() {
      }

      public ListStyle(BitmapFont font, Color fontColorSelected, Color fontColorUnselected, Drawable selection) {
         this.font = font;
         this.fontColorSelected.set(fontColorSelected);
         this.fontColorUnselected.set(fontColorUnselected);
         this.selection = selection;
      }

      public ListStyle(List.ListStyle style) {
         this.font = style.font;
         this.fontColorSelected.set(style.fontColorSelected);
         this.fontColorUnselected.set(style.fontColorUnselected);
         this.selection = style.selection;
      }
   }
}
