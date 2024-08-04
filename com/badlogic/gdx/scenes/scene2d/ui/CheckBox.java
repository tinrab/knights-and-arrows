package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class CheckBox extends TextButton {
   private Image image;
   private CheckBox.CheckBoxStyle style;

   public CheckBox(String text, Skin skin) {
      this(text, (CheckBox.CheckBoxStyle)skin.get(CheckBox.CheckBoxStyle.class));
   }

   public CheckBox(String text, Skin skin, String styleName) {
      this(text, (CheckBox.CheckBoxStyle)skin.get(styleName, CheckBox.CheckBoxStyle.class));
   }

   public CheckBox(String text, CheckBox.CheckBoxStyle style) {
      super(text, (TextButton.TextButtonStyle)style);
      this.clearChildren();
      this.add(this.image = new Image(style.checkboxOff));
      Label label = this.getLabel();
      this.add(label);
      label.setAlignment(8);
      this.setWidth(this.getPrefWidth());
      this.setHeight(this.getPrefHeight());
   }

   public void setStyle(Button.ButtonStyle style) {
      if (!(style instanceof CheckBox.CheckBoxStyle)) {
         throw new IllegalArgumentException("style must be a CheckBoxStyle.");
      } else {
         super.setStyle(style);
         this.style = (CheckBox.CheckBoxStyle)style;
      }
   }

   public CheckBox.CheckBoxStyle getStyle() {
      return this.style;
   }

   public void draw(SpriteBatch batch, float parentAlpha) {
      Drawable checkbox;
      if (this.isChecked && this.style.checkboxOn != null) {
         checkbox = this.style.checkboxOn;
      } else if (this.isOver() && this.style.checkboxOver != null) {
         checkbox = this.style.checkboxOver;
      } else {
         checkbox = this.style.checkboxOff;
      }

      this.image.setDrawable(checkbox);
      super.draw(batch, parentAlpha);
   }

   public Image getImage() {
      return this.image;
   }

   public static class CheckBoxStyle extends TextButton.TextButtonStyle {
      public Drawable checkboxOn;
      public Drawable checkboxOff;
      public Drawable checkboxOver;

      public CheckBoxStyle() {
      }

      public CheckBoxStyle(Drawable checkboxOff, Drawable checkboxOn, BitmapFont font, Color fontColor) {
         this.checkboxOff = checkboxOff;
         this.checkboxOn = checkboxOn;
         this.font = font;
         this.fontColor = fontColor;
      }

      public CheckBoxStyle(CheckBox.CheckBoxStyle style) {
         this.checkboxOff = style.checkboxOff;
         this.checkboxOn = style.checkboxOn;
         this.font = style.font;
         this.fontColor = new Color(style.fontColor);
      }
   }
}
