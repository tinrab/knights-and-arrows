package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;

public class Button extends Table {
   private Button.ButtonStyle style;
   boolean isChecked;
   boolean isDisabled;
   ButtonGroup buttonGroup;
   private ClickListener clickListener;

   public Button(Skin skin) {
      super(skin);
      this.initialize();
      this.setStyle((Button.ButtonStyle)skin.get(Button.ButtonStyle.class));
      this.setWidth(this.getPrefWidth());
      this.setHeight(this.getPrefHeight());
   }

   public Button(Skin skin, String styleName) {
      super(skin);
      this.initialize();
      this.setStyle((Button.ButtonStyle)skin.get(styleName, Button.ButtonStyle.class));
      this.setWidth(this.getPrefWidth());
      this.setHeight(this.getPrefHeight());
   }

   public Button(Actor child, Skin skin, String styleName) {
      this(child, (Button.ButtonStyle)skin.get(styleName, Button.ButtonStyle.class));
   }

   public Button(Actor child, Button.ButtonStyle style) {
      this.initialize();
      this.add(child);
      this.setStyle(style);
      this.setWidth(this.getPrefWidth());
      this.setHeight(this.getPrefHeight());
   }

   public Button(Button.ButtonStyle style) {
      this.initialize();
      this.setStyle(style);
   }

   private void initialize() {
      this.setTouchable(Touchable.enabled);
      this.addListener(this.clickListener = new ClickListener() {
         public void clicked(InputEvent event, float x, float y) {
            if (!Button.this.isDisabled) {
               boolean wasChecked = Button.this.isChecked;
               Button.this.setChecked(!Button.this.isChecked);
            }
         }
      });
   }

   public Button(Drawable up) {
      this(new Button.ButtonStyle(up, (Drawable)null, (Drawable)null));
   }

   public Button(Drawable up, Drawable down) {
      this(new Button.ButtonStyle(up, down, (Drawable)null));
   }

   public Button(Drawable up, Drawable down, Drawable checked) {
      this(new Button.ButtonStyle(up, down, checked));
   }

   public Button(Actor child, Skin skin) {
      this(child, (Button.ButtonStyle)skin.get(Button.ButtonStyle.class));
   }

   public void setChecked(boolean isChecked) {
      if (this.isChecked != isChecked) {
         if (this.buttonGroup == null || this.buttonGroup.canCheck(this, isChecked)) {
            this.isChecked = isChecked;
            if (!this.isDisabled) {
               ChangeListener.ChangeEvent changeEvent = (ChangeListener.ChangeEvent)Pools.obtain(ChangeListener.ChangeEvent.class);
               if (this.fire(changeEvent)) {
                  this.isChecked = !isChecked;
               }

               Pools.free(changeEvent);
            }

         }
      }
   }

   public void toggle() {
      this.setChecked(!this.isChecked);
   }

   public boolean isChecked() {
      return this.isChecked;
   }

   public boolean isPressed() {
      return this.clickListener.isPressed();
   }

   public boolean isOver() {
      return this.clickListener.isOver();
   }

   public ClickListener getClickListener() {
      return this.clickListener;
   }

   public boolean isDisabled() {
      return this.isDisabled;
   }

   public void setDisabled(boolean isDisabled) {
      this.isDisabled = isDisabled;
   }

   public void setStyle(Button.ButtonStyle style) {
      if (style == null) {
         throw new IllegalArgumentException("style cannot be null.");
      } else {
         this.style = style;
         Drawable background = style.up;
         if (background == null) {
            background = style.down;
            if (background == null) {
               background = style.checked;
            }
         }

         if (background != null) {
            this.padBottom(background.getBottomHeight());
            this.padTop(background.getTopHeight());
            this.padLeft(background.getLeftWidth());
            this.padRight(background.getRightWidth());
         }

         this.invalidateHierarchy();
      }
   }

   public Button.ButtonStyle getStyle() {
      return this.style;
   }

   public void draw(SpriteBatch batch, float parentAlpha) {
      this.validate();
      Drawable background = null;
      float offsetX = 0.0F;
      float offsetY = 0.0F;
      if (this.isPressed() && !this.isDisabled) {
         background = this.style.down == null ? this.style.up : this.style.down;
         offsetX = this.style.pressedOffsetX;
         offsetY = this.style.pressedOffsetY;
      } else {
         if (this.isDisabled && this.style.disabled != null) {
            background = this.style.disabled;
         } else if (this.isChecked && this.style.checked != null) {
            background = this.isOver() && this.style.checkedOver != null ? this.style.checkedOver : this.style.checked;
         } else if (this.isOver() && this.style.over != null) {
            background = this.style.over;
         } else {
            background = this.style.up;
         }

         offsetX = this.style.unpressedOffsetX;
         offsetY = this.style.unpressedOffsetY;
      }

      if (background != null) {
         Color color = this.getColor();
         batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
         background.draw(batch, this.getX(), this.getY(), this.getWidth(), this.getHeight());
      }

      Array<Actor> children = this.getChildren();

      int i;
      for(i = 0; i < children.size; ++i) {
         ((Actor)children.get(i)).translate(offsetX, offsetY);
      }

      super.draw(batch, parentAlpha);

      for(i = 0; i < children.size; ++i) {
         ((Actor)children.get(i)).translate(-offsetX, -offsetY);
      }

   }

   protected void drawBackground(SpriteBatch batch, float parentAlpha) {
   }

   public float getPrefWidth() {
      float width = super.getPrefWidth();
      if (this.style.up != null) {
         width = Math.max(width, this.style.up.getMinWidth());
      }

      if (this.style.down != null) {
         width = Math.max(width, this.style.down.getMinWidth());
      }

      if (this.style.checked != null) {
         width = Math.max(width, this.style.checked.getMinWidth());
      }

      return width;
   }

   public float getPrefHeight() {
      float height = super.getPrefHeight();
      if (this.style.up != null) {
         height = Math.max(height, this.style.up.getMinHeight());
      }

      if (this.style.down != null) {
         height = Math.max(height, this.style.down.getMinHeight());
      }

      if (this.style.checked != null) {
         height = Math.max(height, this.style.checked.getMinHeight());
      }

      return height;
   }

   public float getMinWidth() {
      return this.getPrefWidth();
   }

   public float getMinHeight() {
      return this.getPrefHeight();
   }

   public static class ButtonStyle {
      public Drawable up;
      public Drawable down;
      public Drawable over;
      public Drawable checked;
      public Drawable checkedOver;
      public Drawable disabled;
      public float pressedOffsetX;
      public float pressedOffsetY;
      public float unpressedOffsetX;
      public float unpressedOffsetY;

      public ButtonStyle() {
      }

      public ButtonStyle(Drawable up, Drawable down, Drawable checked) {
         this.up = up;
         this.down = down;
         this.checked = checked;
      }

      public ButtonStyle(Button.ButtonStyle style) {
         this.up = style.up;
         this.down = style.down;
         this.over = style.over;
         this.checked = style.checked;
         this.checkedOver = style.checkedOver;
         this.disabled = style.disabled;
         this.pressedOffsetX = style.pressedOffsetX;
         this.pressedOffsetY = style.pressedOffsetY;
         this.unpressedOffsetX = style.unpressedOffsetX;
         this.unpressedOffsetY = style.unpressedOffsetY;
      }
   }
}
