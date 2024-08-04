package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.esotericsoftware.tablelayout.Cell;

public class TextButton extends Button {
   private final Label label;
   private TextButton.TextButtonStyle style;

   public TextButton(String text, Skin skin) {
      this(text, (TextButton.TextButtonStyle)skin.get(TextButton.TextButtonStyle.class));
      this.setSkin(skin);
   }

   public TextButton(String text, Skin skin, String styleName) {
      this(text, (TextButton.TextButtonStyle)skin.get(styleName, TextButton.TextButtonStyle.class));
      this.setSkin(skin);
   }

   public TextButton(String text, TextButton.TextButtonStyle style) {
      super((Button.ButtonStyle)style);
      this.style = style;
      this.label = new Label(text, new Label.LabelStyle(style.font, style.fontColor));
      this.label.setAlignment(1);
      this.add(this.label).expand().fill();
      this.setWidth(this.getPrefWidth());
      this.setHeight(this.getPrefHeight());
   }

   public void setStyle(Button.ButtonStyle style) {
      if (!(style instanceof TextButton.TextButtonStyle)) {
         throw new IllegalArgumentException("style must be a TextButtonStyle.");
      } else {
         super.setStyle(style);
         this.style = (TextButton.TextButtonStyle)style;
         if (this.label != null) {
            TextButton.TextButtonStyle textButtonStyle = (TextButton.TextButtonStyle)style;
            Label.LabelStyle labelStyle = this.label.getStyle();
            labelStyle.font = textButtonStyle.font;
            labelStyle.fontColor = textButtonStyle.fontColor;
            this.label.setStyle(labelStyle);
         }

      }
   }

   public TextButton.TextButtonStyle getStyle() {
      return this.style;
   }

   public void draw(SpriteBatch batch, float parentAlpha) {
      Color fontColor;
      if (this.isDisabled && this.style.disabledFontColor != null) {
         fontColor = this.style.disabledFontColor;
      } else if (this.isPressed() && this.style.downFontColor != null) {
         fontColor = this.style.downFontColor;
      } else if (this.isChecked && this.style.checkedFontColor != null) {
         fontColor = this.isOver() && this.style.checkedOverFontColor != null ? this.style.checkedOverFontColor : this.style.checkedFontColor;
      } else if (this.isOver() && this.style.overFontColor != null) {
         fontColor = this.style.overFontColor;
      } else {
         fontColor = this.style.fontColor;
      }

      if (fontColor != null) {
         this.label.getStyle().fontColor = fontColor;
      }

      super.draw(batch, parentAlpha);
   }

   public Label getLabel() {
      return this.label;
   }

   public Cell getLabelCell() {
      return this.getCell(this.label);
   }

   public void setText(String text) {
      this.label.setText(text);
   }

   public CharSequence getText() {
      return this.label.getText();
   }

   public static class TextButtonStyle extends Button.ButtonStyle {
      public BitmapFont font;
      public Color fontColor;
      public Color downFontColor;
      public Color overFontColor;
      public Color checkedFontColor;
      public Color checkedOverFontColor;
      public Color disabledFontColor;

      public TextButtonStyle() {
      }

      public TextButtonStyle(Drawable up, Drawable down, Drawable checked, BitmapFont font) {
         super(up, down, checked);
         this.font = font;
      }

      public TextButtonStyle(TextButton.TextButtonStyle style) {
         super(style);
         this.font = style.font;
         if (style.fontColor != null) {
            this.fontColor = new Color(style.fontColor);
         }

         if (style.downFontColor != null) {
            this.downFontColor = new Color(style.downFontColor);
         }

         if (style.overFontColor != null) {
            this.overFontColor = new Color(style.overFontColor);
         }

         if (style.checkedFontColor != null) {
            this.checkedFontColor = new Color(style.checkedFontColor);
         }

         if (style.checkedOverFontColor != null) {
            this.checkedFontColor = new Color(style.checkedOverFontColor);
         }

         if (style.disabledFontColor != null) {
            this.disabledFontColor = new Color(style.disabledFontColor);
         }

      }
   }
}
