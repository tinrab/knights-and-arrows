package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Pools;

public class SelectBox extends Widget {
   static final Vector2 tmpCoords = new Vector2();
   SelectBox.SelectBoxStyle style;
   String[] items;
   int selectedIndex;
   private final BitmapFont.TextBounds bounds;
   SelectBox.SelectList list;
   private float prefWidth;
   private float prefHeight;
   private ClickListener clickListener;
   int maxListCount;

   public SelectBox(Object[] items, Skin skin) {
      this(items, (SelectBox.SelectBoxStyle)skin.get(SelectBox.SelectBoxStyle.class));
   }

   public SelectBox(Object[] items, Skin skin, String styleName) {
      this(items, (SelectBox.SelectBoxStyle)skin.get(styleName, SelectBox.SelectBoxStyle.class));
   }

   public SelectBox(Object[] items, SelectBox.SelectBoxStyle style) {
      this.selectedIndex = 0;
      this.bounds = new BitmapFont.TextBounds();
      this.setStyle(style);
      this.setItems(items);
      this.setWidth(this.getPrefWidth());
      this.setHeight(this.getPrefHeight());
      this.addListener(this.clickListener = new ClickListener() {
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            if (pointer == 0 && button != 0) {
               return false;
            } else {
               Stage stage = SelectBox.this.getStage();
               if (SelectBox.this.list == null) {
                  SelectBox.this.list = SelectBox.this.new SelectList();
               }

               SelectBox.this.list.show(stage);
               return true;
            }
         }
      });
   }

   public void setMaxListCount(int maxListCount) {
      this.maxListCount = maxListCount;
   }

   public int getMaxListCount() {
      return this.maxListCount;
   }

   public void setStyle(SelectBox.SelectBoxStyle style) {
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

   public SelectBox.SelectBoxStyle getStyle() {
      return this.style;
   }

   public void setItems(Object[] objects) {
      if (objects == null) {
         throw new IllegalArgumentException("items cannot be null.");
      } else {
         if (!(objects instanceof String[])) {
            String[] strings = new String[((Object[])objects).length];
            int i = 0;

            for(int n = ((Object[])objects).length; i < n; ++i) {
               strings[i] = String.valueOf(((Object[])objects)[i]);
            }

            objects = strings;
         }

         this.items = (String[])objects;
         this.selectedIndex = 0;
         Drawable bg = this.style.background;
         BitmapFont font = this.style.font;
         this.prefHeight = Math.max(bg.getTopHeight() + bg.getBottomHeight() + font.getCapHeight() - font.getDescent() * 2.0F, bg.getMinHeight());
         float max = 0.0F;

         for(int i = 0; i < this.items.length; ++i) {
            max = Math.max(font.getBounds(this.items[i]).width, max);
         }

         this.prefWidth = bg.getLeftWidth() + bg.getRightWidth() + max;
         this.prefWidth = Math.max(this.prefWidth, max + this.style.listBackground.getLeftWidth() + this.style.listBackground.getRightWidth() + this.style.listSelection.getLeftWidth() + this.style.listSelection.getRightWidth());
         if (this.items.length > 0) {
            ChangeListener.ChangeEvent changeEvent = (ChangeListener.ChangeEvent)Pools.obtain(ChangeListener.ChangeEvent.class);
            this.fire(changeEvent);
            Pools.free(changeEvent);
         }

         this.invalidateHierarchy();
      }
   }

   public String[] getItems() {
      return this.items;
   }

   public void draw(SpriteBatch batch, float parentAlpha) {
      Drawable background;
      if (this.list != null && this.list.getParent() != null && this.style.backgroundOpen != null) {
         background = this.style.backgroundOpen;
      } else if (this.clickListener.isOver() && this.style.backgroundOver != null) {
         background = this.style.backgroundOver;
      } else {
         background = this.style.background;
      }

      BitmapFont font = this.style.font;
      Color fontColor = this.style.fontColor;
      Color color = this.getColor();
      float x = this.getX();
      float y = this.getY();
      float width = this.getWidth();
      float height = this.getHeight();
      batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
      background.draw(batch, x, y, width, height);
      if (this.items.length > 0) {
         float availableWidth = width - background.getLeftWidth() - background.getRightWidth();
         int numGlyphs = font.computeVisibleGlyphs(this.items[this.selectedIndex], 0, this.items[this.selectedIndex].length(), availableWidth);
         this.bounds.set(font.getBounds(this.items[this.selectedIndex]));
         height -= background.getBottomHeight() + background.getTopHeight();
         float textY = (float)((int)(height / 2.0F + background.getBottomHeight() + this.bounds.height / 2.0F));
         font.setColor(fontColor.r, fontColor.g, fontColor.b, fontColor.a * parentAlpha);
         font.draw(batch, this.items[this.selectedIndex], x + background.getLeftWidth(), y + textY, 0, numGlyphs);
      }

   }

   public void setSelection(int selection) {
      this.selectedIndex = selection;
   }

   public void setSelection(String item) {
      for(int i = 0; i < this.items.length; ++i) {
         if (this.items[i].equals(item)) {
            this.selectedIndex = i;
         }
      }

   }

   public int getSelectionIndex() {
      return this.selectedIndex;
   }

   public String getSelection() {
      return this.items[this.selectedIndex];
   }

   public float getPrefWidth() {
      return this.prefWidth;
   }

   public float getPrefHeight() {
      return this.prefHeight;
   }

   public void hideList() {
      if (this.list != null && this.list.getParent() != null) {
         this.list.addAction(Actions.sequence(Actions.fadeOut(0.15F, Interpolation.fade), Actions.removeActor()));
      }
   }

   public static class SelectBoxStyle {
      public Drawable background;
      public Drawable backgroundOver;
      public Drawable backgroundOpen;
      public Drawable listBackground;
      public Drawable listSelection;
      public BitmapFont font;
      public Color fontColor = new Color(1.0F, 1.0F, 1.0F, 1.0F);

      public SelectBoxStyle() {
      }

      public SelectBoxStyle(BitmapFont font, Color fontColor, Drawable background, Drawable listBackground, Drawable listSelection) {
         this.background = background;
         this.listBackground = listBackground;
         this.listSelection = listSelection;
         this.font = font;
         this.fontColor.set(fontColor);
      }

      public SelectBoxStyle(SelectBox.SelectBoxStyle style) {
         this.background = style.background;
         this.listBackground = style.listBackground;
         this.listSelection = style.listSelection;
         this.font = style.font;
         this.fontColor.set(style.fontColor);
      }
   }

   class SelectList extends ScrollPane {
      final List list;
      final Vector2 screenCoords = new Vector2();

      public SelectList() {
         super((Actor)null);
         this.getStyle().background = SelectBox.this.style.listBackground;
         this.setOverscroll(false, false);
         List.ListStyle listStyle = new List.ListStyle();
         listStyle.font = SelectBox.this.style.font;
         listStyle.fontColorSelected = SelectBox.this.style.fontColor;
         listStyle.fontColorUnselected = SelectBox.this.style.fontColor;
         listStyle.selection = SelectBox.this.style.listSelection;
         this.list = new List(new Object[0], listStyle);
         this.setWidget(this.list);
         this.list.addListener(new InputListener() {
            public boolean mouseMoved(InputEvent event, float x, float y) {
               SelectList.this.list.setSelectedIndex(Math.min(SelectBox.this.items.length - 1, (int)((SelectList.this.list.getHeight() - y) / SelectList.this.list.getItemHeight())));
               return true;
            }
         });
         this.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
               if (event.getTarget() == SelectList.this.list) {
                  return true;
               } else {
                  SelectBox.this.hideList();
                  return false;
               }
            }

            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
               if (SelectList.this.hit(x, y, true) == SelectList.this.list) {
                  SelectBox.this.setSelection(SelectList.this.list.getSelectedIndex());
                  ChangeListener.ChangeEvent changeEvent = (ChangeListener.ChangeEvent)Pools.obtain(ChangeListener.ChangeEvent.class);
                  SelectBox.this.fire(changeEvent);
                  Pools.free(changeEvent);
                  SelectBox.this.hideList();
               }

            }
         });
      }

      public void show(Stage stage) {
         stage.addActor(this);
         SelectBox.this.localToStageCoordinates(SelectBox.tmpCoords.set(0.0F, 0.0F));
         this.screenCoords.set(SelectBox.tmpCoords);
         this.list.setItems(SelectBox.this.items);
         this.list.setSelectedIndex(SelectBox.this.selectedIndex);
         float itemHeight = this.list.getItemHeight();
         float height = itemHeight * (float)(SelectBox.this.maxListCount <= 0 ? SelectBox.this.items.length : Math.min(SelectBox.this.maxListCount, SelectBox.this.items.length));
         Drawable background = this.getStyle().background;
         if (background != null) {
            height += background.getTopHeight() + background.getBottomHeight();
         }

         float heightBelow = SelectBox.tmpCoords.y;
         float heightAbove = stage.getCamera().viewportHeight - SelectBox.tmpCoords.y - SelectBox.this.getHeight();
         boolean below = true;
         if (height > heightBelow) {
            if (heightAbove > heightBelow) {
               below = false;
               height = Math.min(height, heightAbove);
            } else {
               height = heightBelow;
            }
         }

         if (below) {
            this.setY(SelectBox.tmpCoords.y - height);
         } else {
            this.setY(SelectBox.tmpCoords.y + SelectBox.this.getHeight());
         }

         this.setX(SelectBox.tmpCoords.x);
         this.setWidth(SelectBox.this.getWidth());
         this.setHeight(height);
         this.scrollToCenter(0.0F, this.list.getHeight() - (float)SelectBox.this.selectedIndex * itemHeight - itemHeight / 2.0F, 0.0F, 0.0F);
         this.updateVisualScroll();
         this.getColor().a = 0.0F;
         this.addAction(Actions.fadeIn(0.3F, Interpolation.fade));
      }

      public Actor hit(float x, float y, boolean touchable) {
         Actor actor = super.hit(x, y, touchable);
         return (Actor)(actor != null ? actor : this);
      }

      public void act(float delta) {
         super.act(delta);
         SelectBox.this.localToStageCoordinates(SelectBox.tmpCoords.set(0.0F, 0.0F));
         if (SelectBox.tmpCoords.x != this.screenCoords.x || SelectBox.tmpCoords.y != this.screenCoords.y) {
            SelectBox.this.hideList();
         }

      }
   }
}
