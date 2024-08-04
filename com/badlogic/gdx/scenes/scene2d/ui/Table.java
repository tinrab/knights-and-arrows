package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.tablelayout.BaseTableLayout;
import com.esotericsoftware.tablelayout.Cell;
import com.esotericsoftware.tablelayout.Toolkit;
import com.esotericsoftware.tablelayout.Value;

public class Table extends WidgetGroup {
   private final TableLayout layout;
   private Drawable background;
   private boolean clip;
   private Skin skin;

   static {
      if (Toolkit.instance == null) {
         Toolkit.instance = new TableToolkit();
      }

   }

   public Table() {
      this((Skin)null);
   }

   public Table(Skin skin) {
      this.skin = skin;
      this.layout = new TableLayout();
      this.layout.setTable(this);
      this.setTransform(false);
      this.setTouchable(Touchable.childrenOnly);
   }

   public void draw(SpriteBatch batch, float parentAlpha) {
      this.validate();
      this.drawBackground(batch, parentAlpha);
      if (this.isTransform()) {
         this.applyTransform(batch, this.computeTransform());
         if (this.clip) {
            boolean draw = this.background == null ? this.clipBegin(0.0F, 0.0F, this.getWidth(), this.getHeight()) : this.clipBegin(this.layout.getPadLeft(), this.layout.getPadBottom(), this.getWidth() - this.layout.getPadLeft() - this.layout.getPadRight(), this.getHeight() - this.layout.getPadBottom() - this.layout.getPadTop());
            if (draw) {
               this.drawChildren(batch, parentAlpha);
               this.clipEnd();
            }
         } else {
            this.drawChildren(batch, parentAlpha);
         }

         this.resetTransform(batch);
      } else {
         super.draw(batch, parentAlpha);
      }

   }

   protected void drawBackground(SpriteBatch batch, float parentAlpha) {
      if (this.background != null) {
         Color color = this.getColor();
         batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
         this.background.draw(batch, this.getX(), this.getY(), this.getWidth(), this.getHeight());
      }

   }

   public void invalidate() {
      this.layout.invalidate();
      super.invalidate();
   }

   public float getPrefWidth() {
      return this.background != null ? Math.max(this.layout.getPrefWidth(), this.background.getMinWidth()) : this.layout.getPrefWidth();
   }

   public float getPrefHeight() {
      return this.background != null ? Math.max(this.layout.getPrefHeight(), this.background.getMinHeight()) : this.layout.getPrefHeight();
   }

   public float getMinWidth() {
      return this.layout.getMinWidth();
   }

   public float getMinHeight() {
      return this.layout.getMinHeight();
   }

   public void setBackground(String drawableName) {
      this.setBackground(this.skin.getDrawable(drawableName));
   }

   public void setBackground(Drawable background) {
      if (this.background != background) {
         this.background = background;
         if (background == null) {
            this.pad((Value)null);
         } else {
            this.padBottom(background.getBottomHeight());
            this.padTop(background.getTopHeight());
            this.padLeft(background.getLeftWidth());
            this.padRight(background.getRightWidth());
            this.invalidate();
         }

      }
   }

   public Drawable getBackground() {
      return this.background;
   }

   public Actor hit(float x, float y, boolean touchable) {
      if (this.clip) {
         if (touchable && this.getTouchable() == Touchable.disabled) {
            return null;
         }

         if (x < 0.0F || x >= this.getWidth() || y < 0.0F || y >= this.getHeight()) {
            return null;
         }
      }

      return super.hit(x, y, touchable);
   }

   public void setClip(boolean enabled) {
      this.clip = enabled;
      this.setTransform(enabled);
      this.invalidate();
   }

   public int getRow(float y) {
      return this.layout.getRow(y);
   }

   public void clearChildren() {
      super.clearChildren();
      this.layout.clear();
      this.invalidate();
   }

   public Cell add(String text) {
      if (this.skin == null) {
         throw new IllegalStateException("Table must have a skin set to use this method.");
      } else {
         return this.add((Actor)(new Label(text, this.skin)));
      }
   }

   public Cell add(String text, String labelStyleName) {
      if (this.skin == null) {
         throw new IllegalStateException("Table must have a skin set to use this method.");
      } else {
         return this.add((Actor)(new Label(text, (Label.LabelStyle)this.skin.get(labelStyleName, Label.LabelStyle.class))));
      }
   }

   public Cell add(String text, String fontName, Color color) {
      if (this.skin == null) {
         throw new IllegalStateException("Table must have a skin set to use this method.");
      } else {
         return this.add((Actor)(new Label(text, new Label.LabelStyle(this.skin.getFont(fontName), color))));
      }
   }

   public Cell add(String text, String fontName, String colorName) {
      if (this.skin == null) {
         throw new IllegalStateException("Table must have a skin set to use this method.");
      } else {
         return this.add((Actor)(new Label(text, new Label.LabelStyle(this.skin.getFont(fontName), this.skin.getColor(colorName)))));
      }
   }

   public Cell add() {
      return this.layout.add((Object)null);
   }

   public Cell add(Actor actor) {
      return this.layout.add(actor);
   }

   public boolean removeActor(Actor actor) {
      if (!super.removeActor(actor)) {
         return false;
      } else {
         Cell cell = this.getCell(actor);
         if (cell != null) {
            cell.setWidget((Object)null);
         }

         return true;
      }
   }

   public Cell stack(Actor... actors) {
      Stack stack = new Stack();
      if (actors != null) {
         int i = 0;

         for(int n = actors.length; i < n; ++i) {
            stack.addActor(actors[i]);
         }
      }

      return this.add((Actor)stack);
   }

   public Cell row() {
      return this.layout.row();
   }

   public Cell columnDefaults(int column) {
      return this.layout.columnDefaults(column);
   }

   public Cell defaults() {
      return this.layout.defaults();
   }

   public void layout() {
      this.layout.layout();
   }

   public void reset() {
      this.layout.reset();
   }

   public Cell getCell(Actor actor) {
      return this.layout.getCell(actor);
   }

   public java.util.List<Cell> getCells() {
      return this.layout.getCells();
   }

   public Table pad(Value pad) {
      this.layout.pad(pad);
      return this;
   }

   public Table pad(Value top, Value left, Value bottom, Value right) {
      this.layout.pad(top, left, bottom, right);
      return this;
   }

   public Table padTop(Value padTop) {
      this.layout.padTop(padTop);
      return this;
   }

   public Table padLeft(Value padLeft) {
      this.layout.padLeft(padLeft);
      return this;
   }

   public Table padBottom(Value padBottom) {
      this.layout.padBottom(padBottom);
      return this;
   }

   public Table padRight(Value padRight) {
      this.layout.padRight(padRight);
      return this;
   }

   public Table pad(float pad) {
      this.layout.pad(pad);
      return this;
   }

   public Table pad(float top, float left, float bottom, float right) {
      this.layout.pad(top, left, bottom, right);
      return this;
   }

   public Table padTop(float padTop) {
      this.layout.padTop(padTop);
      return this;
   }

   public Table padLeft(float padLeft) {
      this.layout.padLeft(padLeft);
      return this;
   }

   public Table padBottom(float padBottom) {
      this.layout.padBottom(padBottom);
      return this;
   }

   public Table padRight(float padRight) {
      this.layout.padRight(padRight);
      return this;
   }

   public Table align(int align) {
      this.layout.align(align);
      return this;
   }

   public Table center() {
      this.layout.center();
      return this;
   }

   public Table top() {
      this.layout.top();
      return this;
   }

   public Table left() {
      this.layout.left();
      return this;
   }

   public Table bottom() {
      this.layout.bottom();
      return this;
   }

   public Table right() {
      this.layout.right();
      return this;
   }

   public Table debug() {
      this.layout.debug();
      return this;
   }

   public Table debugTable() {
      this.layout.debugTable();
      return this;
   }

   public Table debugCell() {
      this.layout.debugCell();
      return this;
   }

   public Table debugWidget() {
      this.layout.debugWidget();
      return this;
   }

   public Table debug(BaseTableLayout.Debug debug) {
      this.layout.debug(debug);
      return this;
   }

   public BaseTableLayout.Debug getDebug() {
      return this.layout.getDebug();
   }

   public Value getPadTopValue() {
      return this.layout.getPadTopValue();
   }

   public float getPadTop() {
      return this.layout.getPadTop();
   }

   public Value getPadLeftValue() {
      return this.layout.getPadLeftValue();
   }

   public float getPadLeft() {
      return this.layout.getPadLeft();
   }

   public Value getPadBottomValue() {
      return this.layout.getPadBottomValue();
   }

   public float getPadBottom() {
      return this.layout.getPadBottom();
   }

   public Value getPadRightValue() {
      return this.layout.getPadRightValue();
   }

   public float getPadRight() {
      return this.layout.getPadRight();
   }

   public float getPadX() {
      return this.layout.getPadLeft() + this.layout.getPadRight();
   }

   public float getPadY() {
      return this.layout.getPadTop() + this.layout.getPadBottom();
   }

   public int getAlign() {
      return this.layout.getAlign();
   }

   public void setSkin(Skin skin) {
      this.skin = skin;
   }

   public void setRound(boolean round) {
      this.layout.round = round;
   }

   public static void drawDebug(Stage stage) {
      if (TableToolkit.drawDebug) {
         drawDebug(stage.getActors(), stage.getSpriteBatch());
      }
   }

   private static void drawDebug(Array<Actor> actors, SpriteBatch batch) {
      int i = 0;

      for(int n = actors.size; i < n; ++i) {
         Actor actor = (Actor)actors.get(i);
         if (actor.isVisible()) {
            if (actor instanceof Table) {
               ((Table)actor).layout.drawDebug(batch);
            }

            if (actor instanceof Group) {
               drawDebug(((Group)actor).getChildren(), batch);
            }
         }
      }

   }
}
