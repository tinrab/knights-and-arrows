package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.StringBuilder;

public class Label extends Widget {
   private Label.LabelStyle style;
   private final BitmapFont.TextBounds bounds;
   private final StringBuilder text;
   private BitmapFontCache cache;
   private int labelAlign;
   private BitmapFont.HAlignment lineAlign;
   private boolean wrap;
   private float lastPrefHeight;
   private boolean sizeInvalid;
   private float fontScaleX;
   private float fontScaleY;

   public Label(CharSequence text, Skin skin) {
      this(text, (Label.LabelStyle)skin.get(Label.LabelStyle.class));
   }

   public Label(CharSequence text, Skin skin, String styleName) {
      this(text, (Label.LabelStyle)skin.get(styleName, Label.LabelStyle.class));
   }

   public Label(CharSequence text, Skin skin, String fontName, Color color) {
      this(text, new Label.LabelStyle(skin.getFont(fontName), color));
   }

   public Label(CharSequence text, Skin skin, String fontName, String colorName) {
      this(text, new Label.LabelStyle(skin.getFont(fontName), skin.getColor(colorName)));
   }

   public Label(CharSequence text, Label.LabelStyle style) {
      this.bounds = new BitmapFont.TextBounds();
      this.text = new StringBuilder();
      this.labelAlign = 8;
      this.lineAlign = BitmapFont.HAlignment.LEFT;
      this.sizeInvalid = true;
      this.fontScaleX = 1.0F;
      this.fontScaleY = 1.0F;
      if (text != null) {
         this.text.append(text);
      }

      this.setStyle(style);
      this.setWidth(this.getPrefWidth());
      this.setHeight(this.getPrefHeight());
   }

   public void setStyle(Label.LabelStyle style) {
      if (style == null) {
         throw new IllegalArgumentException("style cannot be null.");
      } else if (style.font == null) {
         throw new IllegalArgumentException("Missing LabelStyle font.");
      } else {
         this.style = style;
         this.cache = new BitmapFontCache(style.font, style.font.usesIntegerPositions());
         this.invalidateHierarchy();
      }
   }

   public Label.LabelStyle getStyle() {
      return this.style;
   }

   public void setText(CharSequence newText) {
      if (newText instanceof StringBuilder) {
         if (this.text.equals(newText)) {
            return;
         }

         this.text.setLength(0);
         this.text.append((StringBuilder)newText);
      } else {
         if (newText == null) {
            newText = "";
         }

         if (this.textEquals((CharSequence)newText)) {
            return;
         }

         this.text.setLength(0);
         this.text.append((CharSequence)newText);
      }

      this.invalidateHierarchy();
   }

   private boolean textEquals(CharSequence other) {
      int length = this.text.length;
      char[] chars = this.text.chars;
      if (length != other.length()) {
         return false;
      } else {
         for(int i = 0; i < length; ++i) {
            if (chars[i] != other.charAt(i)) {
               return false;
            }
         }

         return true;
      }
   }

   public CharSequence getText() {
      return this.text;
   }

   public void invalidate() {
      super.invalidate();
      this.sizeInvalid = true;
   }

   private void computeSize() {
      this.sizeInvalid = false;
      if (this.wrap) {
         float width = this.getWidth();
         if (this.style.background != null) {
            width -= this.style.background.getLeftWidth() + this.style.background.getRightWidth();
         }

         this.bounds.set(this.cache.getFont().getWrappedBounds(this.text, width));
      } else {
         this.bounds.set(this.cache.getFont().getMultiLineBounds(this.text));
      }

      BitmapFont.TextBounds var10000 = this.bounds;
      var10000.width *= this.fontScaleX;
      var10000 = this.bounds;
      var10000.height *= this.fontScaleY;
   }

   public void layout() {
      if (this.sizeInvalid) {
         this.computeSize();
      }

      if (this.wrap) {
         float prefHeight = this.getPrefHeight();
         if (prefHeight != this.lastPrefHeight) {
            this.lastPrefHeight = prefHeight;
            this.invalidateHierarchy();
         }
      }

      BitmapFont font = this.cache.getFont();
      float oldScaleX = font.getScaleX();
      float oldScaleY = font.getScaleY();
      if (this.fontScaleX != 1.0F || this.fontScaleY != 1.0F) {
         font.setScale(this.fontScaleX, this.fontScaleY);
      }

      Drawable background = this.style.background;
      float width = this.getWidth();
      float height = this.getHeight();
      float x = 0.0F;
      float y = 0.0F;
      if (background != null) {
         x = background.getLeftWidth();
         y = background.getBottomHeight();
         width -= background.getLeftWidth() + background.getRightWidth();
         height -= background.getBottomHeight() + background.getTopHeight();
      }

      if ((this.labelAlign & 2) != 0) {
         y += this.cache.getFont().isFlipped() ? 0.0F : height - this.bounds.height;
         y += this.style.font.getDescent();
      } else if ((this.labelAlign & 4) != 0) {
         y += this.cache.getFont().isFlipped() ? height - this.bounds.height : 0.0F;
         y -= this.style.font.getDescent();
      } else {
         y += (float)((int)((height - this.bounds.height) / 2.0F));
      }

      if (!this.cache.getFont().isFlipped()) {
         y += this.bounds.height;
      }

      if ((this.labelAlign & 8) == 0) {
         if ((this.labelAlign & 16) != 0) {
            x += width - this.bounds.width;
         } else {
            x += (float)((int)((width - this.bounds.width) / 2.0F));
         }
      }

      if (this.wrap) {
         this.cache.setWrappedText(this.text, x, y, this.bounds.width, this.lineAlign);
      } else {
         this.cache.setMultiLineText(this.text, x, y, this.bounds.width, this.lineAlign);
      }

      if (this.fontScaleX != 1.0F || this.fontScaleY != 1.0F) {
         font.setScale(oldScaleX, oldScaleY);
      }

   }

   public void draw(SpriteBatch batch, float parentAlpha) {
      this.validate();
      Color color = this.getColor();
      if (this.style.background != null) {
         batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
         this.style.background.draw(batch, this.getX(), this.getY(), this.getWidth(), this.getHeight());
      }

      this.cache.setColor(this.style.fontColor == null ? color : Color.tmp.set(color).mul(this.style.fontColor));
      this.cache.setPosition(this.getX(), this.getY());
      this.cache.draw(batch, color.a * parentAlpha);
   }

   public float getPrefWidth() {
      if (this.wrap) {
         return 0.0F;
      } else {
         if (this.sizeInvalid) {
            this.computeSize();
         }

         float width = this.bounds.width;
         Drawable background = this.style.background;
         if (background != null) {
            width += background.getLeftWidth() + background.getRightWidth();
         }

         return width;
      }
   }

   public float getPrefHeight() {
      if (this.sizeInvalid) {
         this.computeSize();
      }

      float height = this.bounds.height - this.style.font.getDescent() * 2.0F;
      Drawable background = this.style.background;
      if (background != null) {
         height += background.getTopHeight() + background.getBottomHeight();
      }

      return height;
   }

   public BitmapFont.TextBounds getTextBounds() {
      if (this.sizeInvalid) {
         this.computeSize();
      }

      return this.bounds;
   }

   public void setWrap(boolean wrap) {
      this.wrap = wrap;
      this.invalidateHierarchy();
   }

   public void setAlignment(int wrapAlign) {
      this.setAlignment(wrapAlign, wrapAlign);
   }

   public void setAlignment(int labelAlign, int lineAlign) {
      this.labelAlign = labelAlign;
      if ((lineAlign & 8) != 0) {
         this.lineAlign = BitmapFont.HAlignment.LEFT;
      } else if ((lineAlign & 16) != 0) {
         this.lineAlign = BitmapFont.HAlignment.RIGHT;
      } else {
         this.lineAlign = BitmapFont.HAlignment.CENTER;
      }

      this.invalidate();
   }

   public void setFontScale(float fontScale) {
      this.fontScaleX = fontScale;
      this.fontScaleY = fontScale;
      this.invalidateHierarchy();
   }

   public void setFontScale(float fontScaleX, float fontScaleY) {
      this.fontScaleX = fontScaleX;
      this.fontScaleY = fontScaleY;
      this.invalidateHierarchy();
   }

   public float getFontScaleX() {
      return this.fontScaleX;
   }

   public void setFontScaleX(float fontScaleX) {
      this.fontScaleX = fontScaleX;
      this.invalidateHierarchy();
   }

   public float getFontScaleY() {
      return this.fontScaleY;
   }

   public void setFontScaleY(float fontScaleY) {
      this.fontScaleY = fontScaleY;
      this.invalidateHierarchy();
   }

   public static class LabelStyle {
      public BitmapFont font;
      public Color fontColor;
      public Drawable background;

      public LabelStyle() {
      }

      public LabelStyle(BitmapFont font, Color fontColor) {
         this.font = font;
         this.fontColor = fontColor;
      }

      public LabelStyle(Label.LabelStyle style) {
         this.font = style.font;
         if (style.fontColor != null) {
            this.fontColor = new Color(style.fontColor);
         }

      }
   }
}
