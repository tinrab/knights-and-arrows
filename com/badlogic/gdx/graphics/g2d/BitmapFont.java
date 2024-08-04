package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class BitmapFont implements Disposable {
   private static final int LOG2_PAGE_SIZE = 9;
   private static final int PAGE_SIZE = 512;
   private static final int PAGES = 128;
   public static final char[] xChars = new char[]{'x', 'e', 'a', 'o', 'n', 's', 'r', 'c', 'u', 'm', 'v', 'w', 'z'};
   public static final char[] capChars = new char[]{'M', 'N', 'B', 'D', 'C', 'E', 'F', 'K', 'A', 'G', 'H', 'I', 'J', 'L', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
   final BitmapFont.BitmapFontData data;
   TextureRegion region;
   private final BitmapFontCache cache;
   private boolean flipped;
   private boolean integer;
   private boolean ownsTexture;

   public BitmapFont() {
      this(Gdx.files.classpath("com/badlogic/gdx/utils/arial-15.fnt"), Gdx.files.classpath("com/badlogic/gdx/utils/arial-15.png"), false, true);
   }

   public BitmapFont(boolean flip) {
      this(Gdx.files.classpath("com/badlogic/gdx/utils/arial-15.fnt"), Gdx.files.classpath("com/badlogic/gdx/utils/arial-15.png"), flip, true);
   }

   public BitmapFont(FileHandle fontFile, TextureRegion region, boolean flip) {
      this(new BitmapFont.BitmapFontData(fontFile, flip), region, true);
   }

   public BitmapFont(FileHandle fontFile, boolean flip) {
      this((BitmapFont.BitmapFontData)(new BitmapFont.BitmapFontData(fontFile, flip)), (TextureRegion)null, true);
   }

   public BitmapFont(FileHandle fontFile, FileHandle imageFile, boolean flip) {
      this(fontFile, imageFile, flip, true);
   }

   public BitmapFont(FileHandle fontFile, FileHandle imageFile, boolean flip, boolean integer) {
      this(new BitmapFont.BitmapFontData(fontFile, flip), new TextureRegion(new Texture(imageFile, false)), integer);
      this.ownsTexture = true;
   }

   public BitmapFont(BitmapFont.BitmapFontData data, TextureRegion region, boolean integer) {
      this.cache = new BitmapFontCache(this);
      this.region = region == null ? new TextureRegion(new Texture(Gdx.files.internal(data.imagePath), false)) : region;
      this.flipped = data.flipped;
      this.data = data;
      this.integer = integer;
      this.cache.setUseIntegerPositions(integer);
      this.load(data);
      this.ownsTexture = region == null;
   }

   private void load(BitmapFont.BitmapFontData data) {
      float invTexWidth = 1.0F / (float)this.region.getTexture().getWidth();
      float invTexHeight = 1.0F / (float)this.region.getTexture().getHeight();
      float u = this.region.u;
      float v = this.region.v;
      float offsetX = 0.0F;
      float offsetY = 0.0F;
      float regionWidth = (float)this.region.getRegionWidth();
      float regionHeight = (float)this.region.getRegionHeight();
      if (this.region instanceof TextureAtlas.AtlasRegion) {
         TextureAtlas.AtlasRegion atlasRegion = (TextureAtlas.AtlasRegion)this.region;
         offsetX = atlasRegion.offsetX;
         offsetY = (float)(atlasRegion.originalHeight - atlasRegion.packedHeight) - atlasRegion.offsetY;
      }

      BitmapFont.Glyph[][] var13;
      int var12 = (var13 = data.glyphs).length;

      for(int var11 = 0; var11 < var12; ++var11) {
         BitmapFont.Glyph[] page = var13[var11];
         if (page != null) {
            BitmapFont.Glyph[] var17 = page;
            int var16 = page.length;

            for(int var15 = 0; var15 < var16; ++var15) {
               BitmapFont.Glyph glyph = var17[var15];
               if (glyph != null) {
                  float x = (float)glyph.srcX;
                  float x2 = (float)(glyph.srcX + glyph.width);
                  float y = (float)glyph.srcY;
                  float y2 = (float)(glyph.srcY + glyph.height);
                  if (offsetX > 0.0F) {
                     x -= offsetX;
                     if (x < 0.0F) {
                        glyph.width = (int)((float)glyph.width + x);
                        glyph.xoffset = (int)((float)glyph.xoffset - x);
                        x = 0.0F;
                     }

                     x2 -= offsetX;
                     if (x2 > regionWidth) {
                        glyph.width = (int)((float)glyph.width - (x2 - regionWidth));
                        x2 = regionWidth;
                     }
                  }

                  if (offsetY > 0.0F) {
                     y -= offsetY;
                     if (y < 0.0F) {
                        glyph.height = (int)((float)glyph.height + y);
                        y = 0.0F;
                     }

                     y2 -= offsetY;
                     if (y2 > regionHeight) {
                        float amount = y2 - regionHeight;
                        glyph.height = (int)((float)glyph.height - amount);
                        glyph.yoffset = (int)((float)glyph.yoffset + amount);
                        y2 = regionHeight;
                     }
                  }

                  glyph.u = u + x * invTexWidth;
                  glyph.u2 = u + x2 * invTexWidth;
                  if (data.flipped) {
                     glyph.v = v + y * invTexHeight;
                     glyph.v2 = v + y2 * invTexHeight;
                  } else {
                     glyph.v2 = v + y * invTexHeight;
                     glyph.v = v + y2 * invTexHeight;
                  }
               }
            }
         }
      }

   }

   public BitmapFont.TextBounds draw(SpriteBatch spriteBatch, CharSequence str, float x, float y) {
      this.cache.clear();
      BitmapFont.TextBounds bounds = this.cache.addText(str, x, y, 0, str.length());
      this.cache.draw(spriteBatch);
      return bounds;
   }

   public BitmapFont.TextBounds draw(SpriteBatch spriteBatch, CharSequence str, float x, float y, int start, int end) {
      this.cache.clear();
      BitmapFont.TextBounds bounds = this.cache.addText(str, x, y, start, end);
      this.cache.draw(spriteBatch);
      return bounds;
   }

   public BitmapFont.TextBounds drawMultiLine(SpriteBatch spriteBatch, CharSequence str, float x, float y) {
      this.cache.clear();
      BitmapFont.TextBounds bounds = this.cache.addMultiLineText(str, x, y, 0.0F, BitmapFont.HAlignment.LEFT);
      this.cache.draw(spriteBatch);
      return bounds;
   }

   public BitmapFont.TextBounds drawMultiLine(SpriteBatch spriteBatch, CharSequence str, float x, float y, float alignmentWidth, BitmapFont.HAlignment alignment) {
      this.cache.clear();
      BitmapFont.TextBounds bounds = this.cache.addMultiLineText(str, x, y, alignmentWidth, alignment);
      this.cache.draw(spriteBatch);
      return bounds;
   }

   public BitmapFont.TextBounds drawWrapped(SpriteBatch spriteBatch, CharSequence str, float x, float y, float wrapWidth) {
      this.cache.clear();
      BitmapFont.TextBounds bounds = this.cache.addWrappedText(str, x, y, wrapWidth, BitmapFont.HAlignment.LEFT);
      this.cache.draw(spriteBatch);
      return bounds;
   }

   public BitmapFont.TextBounds drawWrapped(SpriteBatch spriteBatch, CharSequence str, float x, float y, float wrapWidth, BitmapFont.HAlignment alignment) {
      this.cache.clear();
      BitmapFont.TextBounds bounds = this.cache.addWrappedText(str, x, y, wrapWidth, alignment);
      this.cache.draw(spriteBatch);
      return bounds;
   }

   public BitmapFont.TextBounds getBounds(CharSequence str) {
      return this.getBounds(str, 0, str.length());
   }

   public BitmapFont.TextBounds getBounds(CharSequence str, BitmapFont.TextBounds textBounds) {
      return this.getBounds(str, 0, str.length(), textBounds);
   }

   public BitmapFont.TextBounds getBounds(CharSequence str, int start, int end) {
      return this.getBounds(str, start, end, this.cache.getBounds());
   }

   public BitmapFont.TextBounds getBounds(CharSequence str, int start, int end, BitmapFont.TextBounds textBounds) {
      BitmapFont.BitmapFontData data = this.data;
      int width = 0;
      BitmapFont.Glyph lastGlyph = null;

      while(start < end) {
         lastGlyph = data.getGlyph(str.charAt(start++));
         if (lastGlyph != null) {
            width = lastGlyph.xadvance;
            break;
         }
      }

      while(start < end) {
         char ch = str.charAt(start++);
         BitmapFont.Glyph g = data.getGlyph(ch);
         if (g != null) {
            width += lastGlyph.getKerning(ch);
            lastGlyph = g;
            width += g.xadvance;
         }
      }

      textBounds.width = (float)width * data.scaleX;
      textBounds.height = data.capHeight;
      return textBounds;
   }

   public BitmapFont.TextBounds getMultiLineBounds(CharSequence str) {
      return this.getMultiLineBounds(str, this.cache.getBounds());
   }

   public BitmapFont.TextBounds getMultiLineBounds(CharSequence str, BitmapFont.TextBounds textBounds) {
      int start = 0;
      float maxWidth = 0.0F;
      int numLines = 0;

      for(int length = str.length(); start < length; ++numLines) {
         int lineEnd = indexOf(str, '\n', start);
         float lineWidth = this.getBounds(str, start, lineEnd).width;
         maxWidth = Math.max(maxWidth, lineWidth);
         start = lineEnd + 1;
      }

      textBounds.width = maxWidth;
      textBounds.height = this.data.capHeight + (float)(numLines - 1) * this.data.lineHeight;
      return textBounds;
   }

   public BitmapFont.TextBounds getWrappedBounds(CharSequence str, float wrapWidth) {
      return this.getWrappedBounds(str, wrapWidth, this.cache.getBounds());
   }

   public BitmapFont.TextBounds getWrappedBounds(CharSequence str, float wrapWidth, BitmapFont.TextBounds textBounds) {
      if (wrapWidth <= 0.0F) {
         wrapWidth = 2.14748365E9F;
      }

      float down = this.data.down;
      int start = 0;
      int numLines = 0;
      int length = str.length();

      float maxWidth;
      for(maxWidth = 0.0F; start < length; ++numLines) {
         int newLine;
         for(newLine = indexOf(str, '\n', start); start < newLine && isWhitespace(str.charAt(start)); ++start) {
         }

         int lineEnd = start + this.computeVisibleGlyphs(str, start, newLine, wrapWidth);
         int nextStart = lineEnd + 1;
         if (lineEnd < newLine) {
            while(lineEnd > start && !isWhitespace(str.charAt(lineEnd))) {
               --lineEnd;
            }

            if (lineEnd == start) {
               if (nextStart > start + 1) {
                  --nextStart;
               }

               lineEnd = nextStart;
            } else {
               for(nextStart = lineEnd; lineEnd > start && isWhitespace(str.charAt(lineEnd - 1)); --lineEnd) {
               }
            }
         }

         if (lineEnd > start) {
            float lineWidth = this.getBounds(str, start, lineEnd).width;
            maxWidth = Math.max(maxWidth, lineWidth);
         }

         start = nextStart;
      }

      textBounds.width = maxWidth;
      textBounds.height = this.data.capHeight + (float)(numLines - 1) * this.data.lineHeight;
      return textBounds;
   }

   public void computeGlyphAdvancesAndPositions(CharSequence str, FloatArray glyphAdvances, FloatArray glyphPositions) {
      glyphAdvances.clear();
      glyphPositions.clear();
      int index = 0;
      int end = str.length();
      float width = 0.0F;
      BitmapFont.Glyph lastGlyph = null;
      BitmapFont.BitmapFontData data = this.data;
      if (data.scaleX != 1.0F) {
         for(float scaleX = this.data.scaleX; index < end; ++index) {
            char ch = str.charAt(index);
            BitmapFont.Glyph g = data.getGlyph(ch);
            if (g != null) {
               if (lastGlyph != null) {
                  width += (float)lastGlyph.getKerning(ch) * scaleX;
               }

               lastGlyph = g;
               float xadvance = (float)g.xadvance * scaleX;
               glyphAdvances.add(xadvance);
               glyphPositions.add(width);
               width += xadvance;
            }
         }

         glyphAdvances.add(0.0F);
         glyphPositions.add(width);
      } else {
         while(true) {
            if (index >= end) {
               glyphAdvances.add(0.0F);
               glyphPositions.add(width);
               break;
            }

            char ch = str.charAt(index);
            BitmapFont.Glyph g = data.getGlyph(ch);
            if (g != null) {
               if (lastGlyph != null) {
                  width += (float)lastGlyph.getKerning(ch);
               }

               lastGlyph = g;
               glyphAdvances.add((float)g.xadvance);
               glyphPositions.add(width);
               width += (float)g.xadvance;
            }

            ++index;
         }
      }

   }

   public int computeVisibleGlyphs(CharSequence str, int start, int end, float availableWidth) {
      BitmapFont.BitmapFontData data = this.data;
      int index = start;
      float width = 0.0F;
      BitmapFont.Glyph lastGlyph = null;
      if (data.scaleX == 1.0F) {
         for(; index < end; ++index) {
            char ch = str.charAt(index);
            BitmapFont.Glyph g = data.getGlyph(ch);
            if (g != null) {
               if (lastGlyph != null) {
                  width += (float)lastGlyph.getKerning(ch);
               }

               if (width + (float)g.xadvance - availableWidth > 0.001F) {
                  break;
               }

               width += (float)g.xadvance;
               lastGlyph = g;
            }
         }
      } else {
         for(float scaleX = this.data.scaleX; index < end; ++index) {
            char ch = str.charAt(index);
            BitmapFont.Glyph g = data.getGlyph(ch);
            if (g != null) {
               if (lastGlyph != null) {
                  width += (float)lastGlyph.getKerning(ch) * scaleX;
               }

               float xadvance = (float)g.xadvance * scaleX;
               if (width + xadvance - availableWidth > 0.001F) {
                  break;
               }

               width += xadvance;
               lastGlyph = g;
            }
         }
      }

      return index - start;
   }

   public void setColor(float color) {
      this.cache.setColor(color);
   }

   public void setColor(Color color) {
      this.cache.setColor(color);
   }

   public void setColor(float r, float g, float b, float a) {
      this.cache.setColor(r, g, b, a);
   }

   public Color getColor() {
      return this.cache.getColor();
   }

   public void setScale(float scaleX, float scaleY) {
      BitmapFont.BitmapFontData data = this.data;
      float x = scaleX / data.scaleX;
      float y = scaleY / data.scaleY;
      data.lineHeight *= y;
      data.spaceWidth *= x;
      data.xHeight *= y;
      data.capHeight *= y;
      data.ascent *= y;
      data.descent *= y;
      data.down *= y;
      data.scaleX = scaleX;
      data.scaleY = scaleY;
   }

   public void setScale(float scaleXY) {
      this.setScale(scaleXY, scaleXY);
   }

   public void scale(float amount) {
      this.setScale(this.data.scaleX + amount, this.data.scaleY + amount);
   }

   public float getScaleX() {
      return this.data.scaleX;
   }

   public float getScaleY() {
      return this.data.scaleY;
   }

   public TextureRegion getRegion() {
      return this.region;
   }

   public float getLineHeight() {
      return this.data.lineHeight;
   }

   public float getSpaceWidth() {
      return this.data.spaceWidth;
   }

   public float getXHeight() {
      return this.data.xHeight;
   }

   public float getCapHeight() {
      return this.data.capHeight;
   }

   public float getAscent() {
      return this.data.ascent;
   }

   public float getDescent() {
      return this.data.descent;
   }

   public boolean isFlipped() {
      return this.flipped;
   }

   public void dispose() {
      if (this.ownsTexture) {
         this.region.getTexture().dispose();
      }

   }

   public void setFixedWidthGlyphs(CharSequence glyphs) {
      BitmapFont.BitmapFontData data = this.data;
      int maxAdvance = 0;
      int index = 0;

      int end;
      BitmapFont.Glyph g;
      for(end = glyphs.length(); index < end; ++index) {
         g = data.getGlyph(glyphs.charAt(index));
         if (g != null && g.xadvance > maxAdvance) {
            maxAdvance = g.xadvance;
         }
      }

      index = 0;

      for(end = glyphs.length(); index < end; ++index) {
         g = data.getGlyph(glyphs.charAt(index));
         if (g != null) {
            g.xoffset += (maxAdvance - g.xadvance) / 2;
            g.xadvance = maxAdvance;
            g.kerning = null;
         }
      }

   }

   public boolean containsCharacter(char character) {
      return this.data.getGlyph(character) != null;
   }

   public void setUseIntegerPositions(boolean integer) {
      this.integer = integer;
      this.cache.setUseIntegerPositions(integer);
   }

   public boolean usesIntegerPositions() {
      return this.integer;
   }

   public BitmapFont.BitmapFontData getData() {
      return this.data;
   }

   public boolean ownsTexture() {
      return this.ownsTexture;
   }

   public void setOwnsTexture(boolean ownsTexture) {
      this.ownsTexture = ownsTexture;
   }

   static int indexOf(CharSequence text, char ch, int start) {
      int n;
      for(n = text.length(); start < n; ++start) {
         if (text.charAt(start) == ch) {
            return start;
         }
      }

      return n;
   }

   static boolean isWhitespace(char c) {
      switch(c) {
      case '\t':
      case '\n':
      case '\r':
      case ' ':
         return true;
      default:
         return false;
      }
   }

   public static class BitmapFontData {
      public String imagePath;
      public FileHandle fontFile;
      public boolean flipped;
      public float lineHeight;
      public float capHeight = 1.0F;
      public float ascent;
      public float descent;
      public float down;
      public float scaleX = 1.0F;
      public float scaleY = 1.0F;
      public final BitmapFont.Glyph[][] glyphs = new BitmapFont.Glyph[128][];
      public float spaceWidth;
      public float xHeight = 1.0F;

      public BitmapFontData() {
      }

      public BitmapFontData(FileHandle fontFile, boolean flip) {
         this.fontFile = fontFile;
         this.flipped = flip;
         BufferedReader reader = new BufferedReader(new InputStreamReader(fontFile.read()), 512);

         try {
            reader.readLine();
            String line = reader.readLine();
            if (line == null) {
               throw new GdxRuntimeException("Invalid font file: " + fontFile);
            } else {
               String[] common = line.split(" ", 4);
               if (common.length < 4) {
                  throw new GdxRuntimeException("Invalid font file: " + fontFile);
               } else if (!common[1].startsWith("lineHeight=")) {
                  throw new GdxRuntimeException("Invalid font file: " + fontFile);
               } else {
                  this.lineHeight = (float)Integer.parseInt(common[1].substring(11));
                  if (!common[2].startsWith("base=")) {
                     throw new GdxRuntimeException("Invalid font file: " + fontFile);
                  } else {
                     int baseLine = Integer.parseInt(common[2].substring(5));
                     line = reader.readLine();
                     if (line == null) {
                        throw new GdxRuntimeException("Invalid font file: " + fontFile);
                     } else {
                        String[] pageLine = line.split(" ", 4);
                        if (!pageLine[2].startsWith("file=")) {
                           throw new GdxRuntimeException("Invalid font file: " + fontFile);
                        } else {
                           String imgFilename = null;
                           if (pageLine[2].endsWith("\"")) {
                              imgFilename = pageLine[2].substring(6, pageLine[2].length() - 1);
                           } else {
                              imgFilename = pageLine[2].substring(5, pageLine[2].length());
                           }

                           this.imagePath = fontFile.parent().child(imgFilename).path().replaceAll("\\\\", "/");
                           this.descent = 0.0F;

                           while(true) {
                              line = reader.readLine();
                              BitmapFont.Glyph glyph;
                              int i;
                              if (line == null || line.startsWith("kernings ")) {
                                 while(true) {
                                    line = reader.readLine();
                                    int amount;
                                    if (line == null || !line.startsWith("kerning ")) {
                                       glyph = this.getGlyph(' ');
                                       BitmapFont.Glyph xGlyph;
                                       if (glyph == null) {
                                          glyph = new BitmapFont.Glyph();
                                          xGlyph = this.getGlyph('l');
                                          if (xGlyph == null) {
                                             xGlyph = this.getFirstGlyph();
                                          }

                                          glyph.xadvance = xGlyph.xadvance;
                                          this.setGlyph(32, glyph);
                                       }

                                       this.spaceWidth = (float)(glyph != null ? glyph.xadvance + glyph.width : 1);
                                       xGlyph = null;
                                       i = 0;

                                       while(true) {
                                          if (i < BitmapFont.xChars.length) {
                                             xGlyph = this.getGlyph(BitmapFont.xChars[i]);
                                             if (xGlyph == null) {
                                                ++i;
                                                continue;
                                             }
                                          }

                                          if (xGlyph == null) {
                                             xGlyph = this.getFirstGlyph();
                                          }

                                          this.xHeight = (float)xGlyph.height;
                                          BitmapFont.Glyph capGlyph = null;
                                          int i = 0;

                                          while(true) {
                                             if (i < BitmapFont.capChars.length) {
                                                capGlyph = this.getGlyph(BitmapFont.capChars[i]);
                                                if (capGlyph == null) {
                                                   ++i;
                                                   continue;
                                                }
                                             }

                                             if (capGlyph == null) {
                                                BitmapFont.Glyph[][] var15;
                                                int var14 = (var15 = this.glyphs).length;

                                                for(amount = 0; amount < var14; ++amount) {
                                                   BitmapFont.Glyph[] page = var15[amount];
                                                   if (page != null) {
                                                      BitmapFont.Glyph[] var19 = page;
                                                      int var18 = page.length;

                                                      for(int var17 = 0; var17 < var18; ++var17) {
                                                         BitmapFont.Glyph glyph = var19[var17];
                                                         if (glyph != null && glyph.height != 0 && glyph.width != 0) {
                                                            this.capHeight = Math.max(this.capHeight, (float)glyph.height);
                                                         }
                                                      }
                                                   }
                                                }
                                             } else {
                                                this.capHeight = (float)capGlyph.height;
                                             }

                                             this.ascent = (float)baseLine - this.capHeight;
                                             this.down = -this.lineHeight;
                                             if (flip) {
                                                this.ascent = -this.ascent;
                                                this.down = -this.down;
                                             }

                                             return;
                                          }
                                       }
                                    }

                                    StringTokenizer tokens = new StringTokenizer(line, " =");
                                    tokens.nextToken();
                                    tokens.nextToken();
                                    int first = Integer.parseInt(tokens.nextToken());
                                    tokens.nextToken();
                                    i = Integer.parseInt(tokens.nextToken());
                                    if (first >= 0 && first <= 65535 && i >= 0 && i <= 65535) {
                                       BitmapFont.Glyph glyph = this.getGlyph((char)first);
                                       tokens.nextToken();
                                       amount = Integer.parseInt(tokens.nextToken());
                                       glyph.setKerning(i, amount);
                                    }
                                 }
                              }

                              if (line.startsWith("char ")) {
                                 glyph = new BitmapFont.Glyph();
                                 StringTokenizer tokens = new StringTokenizer(line, " =");
                                 tokens.nextToken();
                                 tokens.nextToken();
                                 i = Integer.parseInt(tokens.nextToken());
                                 if (i <= 65535) {
                                    this.setGlyph(i, glyph);
                                    tokens.nextToken();
                                    glyph.srcX = Integer.parseInt(tokens.nextToken());
                                    tokens.nextToken();
                                    glyph.srcY = Integer.parseInt(tokens.nextToken());
                                    tokens.nextToken();
                                    glyph.width = Integer.parseInt(tokens.nextToken());
                                    tokens.nextToken();
                                    glyph.height = Integer.parseInt(tokens.nextToken());
                                    tokens.nextToken();
                                    glyph.xoffset = Integer.parseInt(tokens.nextToken());
                                    tokens.nextToken();
                                    if (flip) {
                                       glyph.yoffset = Integer.parseInt(tokens.nextToken());
                                    } else {
                                       glyph.yoffset = -(glyph.height + Integer.parseInt(tokens.nextToken()));
                                    }

                                    tokens.nextToken();
                                    glyph.xadvance = Integer.parseInt(tokens.nextToken());
                                    if (glyph.width > 0 && glyph.height > 0) {
                                       this.descent = Math.min((float)(baseLine + glyph.yoffset), this.descent);
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         } catch (Exception var27) {
            throw new GdxRuntimeException("Error loading font file: " + fontFile, var27);
         } finally {
            try {
               reader.close();
            } catch (IOException var26) {
            }

         }
      }

      public void setGlyph(int ch, BitmapFont.Glyph glyph) {
         BitmapFont.Glyph[] page = this.glyphs[ch / 512];
         if (page == null) {
            this.glyphs[ch / 512] = page = new BitmapFont.Glyph[512];
         }

         page[ch & 511] = glyph;
      }

      public BitmapFont.Glyph getFirstGlyph() {
         BitmapFont.Glyph[][] var4;
         int var3 = (var4 = this.glyphs).length;

         for(int var2 = 0; var2 < var3; ++var2) {
            BitmapFont.Glyph[] page = var4[var2];
            if (page != null) {
               BitmapFont.Glyph[] var8 = page;
               int var7 = page.length;

               for(int var6 = 0; var6 < var7; ++var6) {
                  BitmapFont.Glyph glyph = var8[var6];
                  if (glyph != null && glyph.height != 0 && glyph.width != 0) {
                     return glyph;
                  }
               }
            }
         }

         throw new GdxRuntimeException("No glyphs found!");
      }

      public BitmapFont.Glyph getGlyph(char ch) {
         BitmapFont.Glyph[] page = this.glyphs[ch / 512];
         return page != null ? page[ch & 511] : null;
      }

      public String getImagePath() {
         return this.imagePath;
      }

      public FileHandle getFontFile() {
         return this.fontFile;
      }
   }

   public static class Glyph {
      public int srcX;
      public int srcY;
      public int width;
      public int height;
      public float u;
      public float v;
      public float u2;
      public float v2;
      public int xoffset;
      public int yoffset;
      public int xadvance;
      public byte[][] kerning;

      public int getKerning(char ch) {
         if (this.kerning != null) {
            byte[] page = this.kerning[ch >>> 9];
            if (page != null) {
               return page[ch & 511];
            }
         }

         return 0;
      }

      public void setKerning(int ch, int value) {
         if (this.kerning == null) {
            this.kerning = new byte[128][];
         }

         byte[] page = this.kerning[ch >>> 9];
         if (page == null) {
            this.kerning[ch >>> 9] = page = new byte[512];
         }

         page[ch & 511] = (byte)value;
      }
   }

   public static enum HAlignment {
      LEFT,
      CENTER,
      RIGHT;
   }

   public static class TextBounds {
      public float width;
      public float height;

      public TextBounds() {
      }

      public TextBounds(BitmapFont.TextBounds bounds) {
         this.set(bounds);
      }

      public void set(BitmapFont.TextBounds bounds) {
         this.width = bounds.width;
         this.height = bounds.height;
      }
   }
}
