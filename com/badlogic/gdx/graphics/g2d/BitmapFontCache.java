package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.NumberUtils;

public class BitmapFontCache {
   private final BitmapFont font;
   private float[] vertices;
   private int idx;
   private float x;
   private float y;
   private float color;
   private final Color tempColor;
   private final BitmapFont.TextBounds textBounds;
   private boolean integer;

   public BitmapFontCache(BitmapFont font) {
      this(font, font.usesIntegerPositions());
   }

   public BitmapFontCache(BitmapFont font, boolean integer) {
      this.vertices = new float[0];
      this.color = Color.WHITE.toFloatBits();
      this.tempColor = new Color(Color.WHITE);
      this.textBounds = new BitmapFont.TextBounds();
      this.integer = true;
      this.font = font;
      this.integer = integer;
   }

   public void setPosition(float x, float y) {
      this.translate(x - this.x, y - this.y);
   }

   public void translate(float xAmount, float yAmount) {
      if (xAmount != 0.0F || yAmount != 0.0F) {
         if (this.integer) {
            xAmount = (float)Math.round(xAmount);
            yAmount = (float)Math.round(yAmount);
         }

         this.x += xAmount;
         this.y += yAmount;
         float[] vertices = this.vertices;
         int i = 0;

         for(int n = this.idx; i < n; i += 5) {
            vertices[i] += xAmount;
            vertices[i + 1] += yAmount;
         }

      }
   }

   public void setColor(float color) {
      if (color != this.color) {
         this.color = color;
         float[] vertices = this.vertices;
         int i = 2;

         for(int n = this.idx; i < n; i += 5) {
            vertices[i] = color;
         }

      }
   }

   public void setColor(Color tint) {
      float color = tint.toFloatBits();
      if (color != this.color) {
         this.color = color;
         float[] vertices = this.vertices;
         int i = 2;

         for(int n = this.idx; i < n; i += 5) {
            vertices[i] = color;
         }

      }
   }

   public void setColor(float r, float g, float b, float a) {
      int intBits = (int)(255.0F * a) << 24 | (int)(255.0F * b) << 16 | (int)(255.0F * g) << 8 | (int)(255.0F * r);
      float color = NumberUtils.intToFloatColor(intBits);
      if (color != this.color) {
         this.color = color;
         float[] vertices = this.vertices;
         int i = 2;

         for(int n = this.idx; i < n; i += 5) {
            vertices[i] = color;
         }

      }
   }

   public void setColor(Color tint, int start, int end) {
      float color = tint.toFloatBits();
      float[] vertices = this.vertices;
      int i = start * 20 + 2;

      for(int n = end * 20; i < n; i += 5) {
         vertices[i] = color;
      }

   }

   public void draw(SpriteBatch spriteBatch) {
      spriteBatch.draw(this.font.getRegion().getTexture(), this.vertices, 0, this.idx);
   }

   public void draw(SpriteBatch spriteBatch, int start, int end) {
      spriteBatch.draw(this.font.getRegion().getTexture(), this.vertices, start * 20, end * 20);
   }

   public void draw(SpriteBatch spriteBatch, float alphaModulation) {
      if (alphaModulation == 1.0F) {
         this.draw(spriteBatch);
      } else {
         Color color = this.getColor();
         float oldAlpha = color.a;
         color.a *= alphaModulation;
         this.setColor(color);
         this.draw(spriteBatch);
         color.a = oldAlpha;
         this.setColor(color);
      }
   }

   public Color getColor() {
      float floatBits = this.color;
      int intBits = NumberUtils.floatToIntColor(this.color);
      Color color = this.tempColor;
      color.r = (float)(intBits & 255) / 255.0F;
      color.g = (float)(intBits >>> 8 & 255) / 255.0F;
      color.b = (float)(intBits >>> 16 & 255) / 255.0F;
      color.a = (float)(intBits >>> 24 & 255) / 255.0F;
      return color;
   }

   public void clear() {
      this.x = 0.0F;
      this.y = 0.0F;
      this.idx = 0;
   }

   private void require(int glyphCount) {
      int vertexCount = this.idx + glyphCount * 20;
      if (this.vertices == null || this.vertices.length < vertexCount) {
         float[] newVertices = new float[vertexCount];
         System.arraycopy(this.vertices, 0, newVertices, 0, this.idx);
         this.vertices = newVertices;
      }

   }

   private float addToCache(CharSequence str, float x, float y, int start, int end) {
      float startX = x;
      BitmapFont font = this.font;
      BitmapFont.Glyph lastGlyph = null;
      BitmapFont.BitmapFontData data = font.data;
      if (data.scaleX == 1.0F && data.scaleY == 1.0F) {
         while(true) {
            if (start < end) {
               lastGlyph = data.getGlyph(str.charAt(start++));
               if (lastGlyph == null) {
                  continue;
               }

               this.addGlyph(lastGlyph, x + (float)lastGlyph.xoffset, y + (float)lastGlyph.yoffset, (float)lastGlyph.width, (float)lastGlyph.height);
               x += (float)lastGlyph.xadvance;
            }

            while(start < end) {
               char ch = str.charAt(start++);
               BitmapFont.Glyph g = data.getGlyph(ch);
               if (g != null) {
                  x += (float)lastGlyph.getKerning(ch);
                  lastGlyph = g;
                  this.addGlyph(g, x + (float)g.xoffset, y + (float)g.yoffset, (float)g.width, (float)g.height);
                  x += (float)g.xadvance;
               }
            }

            return x - startX;
         }
      } else {
         float scaleX = data.scaleX;
         float scaleY = data.scaleY;

         while(start < end) {
            lastGlyph = data.getGlyph(str.charAt(start++));
            if (lastGlyph != null) {
               this.addGlyph(lastGlyph, x + (float)lastGlyph.xoffset * scaleX, y + (float)lastGlyph.yoffset * scaleY, (float)lastGlyph.width * scaleX, (float)lastGlyph.height * scaleY);
               x += (float)lastGlyph.xadvance * scaleX;
               break;
            }
         }

         while(start < end) {
            char ch = str.charAt(start++);
            BitmapFont.Glyph g = data.getGlyph(ch);
            if (g != null) {
               x += (float)lastGlyph.getKerning(ch) * scaleX;
               lastGlyph = g;
               this.addGlyph(g, x + (float)g.xoffset * scaleX, y + (float)g.yoffset * scaleY, (float)g.width * scaleX, (float)g.height * scaleY);
               x += (float)g.xadvance * scaleX;
            }
         }

         return x - startX;
      }
   }

   private void addGlyph(BitmapFont.Glyph glyph, float x, float y, float width, float height) {
      float x2 = x + width;
      float y2 = y + height;
      float u = glyph.u;
      float u2 = glyph.u2;
      float v = glyph.v;
      float v2 = glyph.v2;
      float[] vertices = this.vertices;
      if (this.integer) {
         x = (float)Math.round(x);
         y = (float)Math.round(y);
         x2 = (float)Math.round(x2);
         y2 = (float)Math.round(y2);
      }

      int idx = this.idx;
      this.idx += 20;
      vertices[idx++] = x;
      vertices[idx++] = y;
      vertices[idx++] = this.color;
      vertices[idx++] = u;
      vertices[idx++] = v;
      vertices[idx++] = x;
      vertices[idx++] = y2;
      vertices[idx++] = this.color;
      vertices[idx++] = u;
      vertices[idx++] = v2;
      vertices[idx++] = x2;
      vertices[idx++] = y2;
      vertices[idx++] = this.color;
      vertices[idx++] = u2;
      vertices[idx++] = v2;
      vertices[idx++] = x2;
      vertices[idx++] = y;
      vertices[idx++] = this.color;
      vertices[idx++] = u2;
      vertices[idx] = v;
   }

   public BitmapFont.TextBounds setText(CharSequence str, float x, float y) {
      this.clear();
      return this.addText(str, x, y, 0, str.length());
   }

   public BitmapFont.TextBounds setText(CharSequence str, float x, float y, int start, int end) {
      this.clear();
      return this.addText(str, x, y, start, end);
   }

   public BitmapFont.TextBounds addText(CharSequence str, float x, float y) {
      return this.addText(str, x, y, 0, str.length());
   }

   public BitmapFont.TextBounds addText(CharSequence str, float x, float y, int start, int end) {
      this.require(end - start);
      y += this.font.data.ascent;
      this.textBounds.width = this.addToCache(str, x, y, start, end);
      this.textBounds.height = this.font.data.capHeight;
      return this.textBounds;
   }

   public BitmapFont.TextBounds setMultiLineText(CharSequence str, float x, float y) {
      this.clear();
      return this.addMultiLineText(str, x, y, 0.0F, BitmapFont.HAlignment.LEFT);
   }

   public BitmapFont.TextBounds setMultiLineText(CharSequence str, float x, float y, float alignmentWidth, BitmapFont.HAlignment alignment) {
      this.clear();
      return this.addMultiLineText(str, x, y, alignmentWidth, alignment);
   }

   public BitmapFont.TextBounds addMultiLineText(CharSequence str, float x, float y) {
      return this.addMultiLineText(str, x, y, 0.0F, BitmapFont.HAlignment.LEFT);
   }

   public BitmapFont.TextBounds addMultiLineText(CharSequence str, float x, float y, float alignmentWidth, BitmapFont.HAlignment alignment) {
      BitmapFont font = this.font;
      int length = str.length();
      this.require(length);
      y += font.data.ascent;
      float down = font.data.down;
      float maxWidth = 0.0F;
      int start = 0;

      int numLines;
      for(numLines = 0; start < length; ++numLines) {
         int lineEnd = BitmapFont.indexOf(str, '\n', start);
         float xOffset = 0.0F;
         float lineWidth;
         if (alignment != BitmapFont.HAlignment.LEFT) {
            lineWidth = font.getBounds(str, start, lineEnd).width;
            xOffset = alignmentWidth - lineWidth;
            if (alignment == BitmapFont.HAlignment.CENTER) {
               xOffset /= 2.0F;
            }
         }

         lineWidth = this.addToCache(str, x + xOffset, y, start, lineEnd);
         maxWidth = Math.max(maxWidth, lineWidth);
         start = lineEnd + 1;
         y += down;
      }

      this.textBounds.width = maxWidth;
      this.textBounds.height = font.data.capHeight + (float)(numLines - 1) * font.data.lineHeight;
      return this.textBounds;
   }

   public BitmapFont.TextBounds setWrappedText(CharSequence str, float x, float y, float wrapWidth) {
      this.clear();
      return this.addWrappedText(str, x, y, wrapWidth, BitmapFont.HAlignment.LEFT);
   }

   public BitmapFont.TextBounds setWrappedText(CharSequence str, float x, float y, float wrapWidth, BitmapFont.HAlignment alignment) {
      this.clear();
      return this.addWrappedText(str, x, y, wrapWidth, alignment);
   }

   public BitmapFont.TextBounds addWrappedText(CharSequence str, float x, float y, float wrapWidth) {
      return this.addWrappedText(str, x, y, wrapWidth, BitmapFont.HAlignment.LEFT);
   }

   public BitmapFont.TextBounds addWrappedText(CharSequence str, float x, float y, float wrapWidth, BitmapFont.HAlignment alignment) {
      BitmapFont font = this.font;
      int length = str.length();
      this.require(length);
      y += font.data.ascent;
      float down = font.data.down;
      if (wrapWidth <= 0.0F) {
         wrapWidth = 2.14748365E9F;
      }

      float maxWidth = 0.0F;
      int start = 0;

      int numLines;
      for(numLines = 0; start < length; ++numLines) {
         int newLine;
         for(newLine = BitmapFont.indexOf(str, '\n', start); start < newLine && BitmapFont.isWhitespace(str.charAt(start)); ++start) {
         }

         int lineEnd = start + font.computeVisibleGlyphs(str, start, newLine, wrapWidth);
         int nextStart = lineEnd + 1;
         if (lineEnd < newLine) {
            while(lineEnd > start && !BitmapFont.isWhitespace(str.charAt(lineEnd))) {
               --lineEnd;
            }

            if (lineEnd == start) {
               if (nextStart > start + 1) {
                  --nextStart;
               }

               lineEnd = nextStart;
            } else {
               for(nextStart = lineEnd; lineEnd > start && BitmapFont.isWhitespace(str.charAt(lineEnd - 1)); --lineEnd) {
               }
            }
         }

         if (lineEnd > start) {
            float xOffset = 0.0F;
            float lineWidth;
            if (alignment != BitmapFont.HAlignment.LEFT) {
               lineWidth = font.getBounds(str, start, lineEnd).width;
               xOffset = wrapWidth - lineWidth;
               if (alignment == BitmapFont.HAlignment.CENTER) {
                  xOffset /= 2.0F;
               }
            }

            lineWidth = this.addToCache(str, x + xOffset, y, start, lineEnd);
            maxWidth = Math.max(maxWidth, lineWidth);
         }

         start = nextStart;
         y += down;
      }

      this.textBounds.width = maxWidth;
      this.textBounds.height = font.data.capHeight + (float)(numLines - 1) * font.data.lineHeight;
      return this.textBounds;
   }

   public BitmapFont.TextBounds getBounds() {
      return this.textBounds;
   }

   public float getX() {
      return this.x;
   }

   public float getY() {
      return this.y;
   }

   public BitmapFont getFont() {
      return this.font;
   }

   public void setUseIntegerPositions(boolean use) {
      this.integer = use;
   }

   public boolean usesIntegerPositions() {
      return this.integer;
   }

   public float[] getVertices() {
      return this.vertices;
   }
}
