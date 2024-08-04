package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class NinePatch {
   public static final int TOP_LEFT = 0;
   public static final int TOP_CENTER = 1;
   public static final int TOP_RIGHT = 2;
   public static final int MIDDLE_LEFT = 3;
   public static final int MIDDLE_CENTER = 4;
   public static final int MIDDLE_RIGHT = 5;
   public static final int BOTTOM_LEFT = 6;
   public static final int BOTTOM_CENTER = 7;
   public static final int BOTTOM_RIGHT = 8;
   private static final Color tmpDrawColor = new Color();
   private Texture texture;
   private int bottomLeft;
   private int bottomCenter;
   private int bottomRight;
   private int middleLeft;
   private int middleCenter;
   private int middleRight;
   private int topLeft;
   private int topCenter;
   private int topRight;
   private float leftWidth;
   private float rightWidth;
   private float middleWidth;
   private float middleHeight;
   private float topHeight;
   private float bottomHeight;
   private float[] vertices;
   private int idx;
   private final Color color;
   private int padLeft;
   private int padRight;
   private int padTop;
   private int padBottom;

   public NinePatch(Texture texture, int left, int right, int top, int bottom) {
      this(new TextureRegion(texture), left, right, top, bottom);
   }

   public NinePatch(TextureRegion region, int left, int right, int top, int bottom) {
      this.bottomLeft = -1;
      this.bottomCenter = -1;
      this.bottomRight = -1;
      this.middleLeft = -1;
      this.middleCenter = -1;
      this.middleRight = -1;
      this.topLeft = -1;
      this.topCenter = -1;
      this.topRight = -1;
      this.vertices = new float[180];
      this.color = new Color(Color.WHITE);
      this.padLeft = -1;
      this.padRight = -1;
      this.padTop = -1;
      this.padBottom = -1;
      if (region == null) {
         throw new IllegalArgumentException("region cannot be null.");
      } else {
         int middleWidth = region.getRegionWidth() - left - right;
         int middleHeight = region.getRegionHeight() - top - bottom;
         TextureRegion[] patches = new TextureRegion[9];
         if (top > 0) {
            if (left > 0) {
               patches[0] = new TextureRegion(region, 0, 0, left, top);
            }

            if (middleWidth > 0) {
               patches[1] = new TextureRegion(region, left, 0, middleWidth, top);
            }

            if (right > 0) {
               patches[2] = new TextureRegion(region, left + middleWidth, 0, right, top);
            }
         }

         if (middleHeight > 0) {
            if (left > 0) {
               patches[3] = new TextureRegion(region, 0, top, left, middleHeight);
            }

            if (middleWidth > 0) {
               patches[4] = new TextureRegion(region, left, top, middleWidth, middleHeight);
            }

            if (right > 0) {
               patches[5] = new TextureRegion(region, left + middleWidth, top, right, middleHeight);
            }
         }

         if (bottom > 0) {
            if (left > 0) {
               patches[6] = new TextureRegion(region, 0, top + middleHeight, left, bottom);
            }

            if (middleWidth > 0) {
               patches[7] = new TextureRegion(region, left, top + middleHeight, middleWidth, bottom);
            }

            if (right > 0) {
               patches[8] = new TextureRegion(region, left + middleWidth, top + middleHeight, right, bottom);
            }
         }

         if (left == 0 && middleWidth == 0) {
            patches[1] = patches[2];
            patches[4] = patches[5];
            patches[7] = patches[8];
            patches[2] = null;
            patches[5] = null;
            patches[8] = null;
         }

         if (top == 0 && middleHeight == 0) {
            patches[3] = patches[6];
            patches[4] = patches[7];
            patches[5] = patches[8];
            patches[6] = null;
            patches[7] = null;
            patches[8] = null;
         }

         this.load(patches);
      }
   }

   public NinePatch(Texture texture, Color color) {
      this(texture);
      this.setColor(color);
   }

   public NinePatch(Texture texture) {
      this(new TextureRegion(texture));
   }

   public NinePatch(TextureRegion region, Color color) {
      this(region);
      this.setColor(color);
   }

   public NinePatch(TextureRegion region) {
      this.bottomLeft = -1;
      this.bottomCenter = -1;
      this.bottomRight = -1;
      this.middleLeft = -1;
      this.middleCenter = -1;
      this.middleRight = -1;
      this.topLeft = -1;
      this.topCenter = -1;
      this.topRight = -1;
      this.vertices = new float[180];
      this.color = new Color(Color.WHITE);
      this.padLeft = -1;
      this.padRight = -1;
      this.padTop = -1;
      this.padBottom = -1;
      this.load(new TextureRegion[]{null, null, null, null, region, null, null, null, null});
   }

   public NinePatch(TextureRegion... patches) {
      this.bottomLeft = -1;
      this.bottomCenter = -1;
      this.bottomRight = -1;
      this.middleLeft = -1;
      this.middleCenter = -1;
      this.middleRight = -1;
      this.topLeft = -1;
      this.topCenter = -1;
      this.topRight = -1;
      this.vertices = new float[180];
      this.color = new Color(Color.WHITE);
      this.padLeft = -1;
      this.padRight = -1;
      this.padTop = -1;
      this.padBottom = -1;
      if (patches != null && patches.length == 9) {
         this.load(patches);
         float leftWidth = this.getLeftWidth();
         if ((patches[0] == null || (float)patches[0].getRegionWidth() == leftWidth) && (patches[3] == null || (float)patches[3].getRegionWidth() == leftWidth) && (patches[6] == null || (float)patches[6].getRegionWidth() == leftWidth)) {
            float rightWidth = this.getRightWidth();
            if ((patches[2] == null || (float)patches[2].getRegionWidth() == rightWidth) && (patches[5] == null || (float)patches[5].getRegionWidth() == rightWidth) && (patches[8] == null || (float)patches[8].getRegionWidth() == rightWidth)) {
               float bottomHeight = this.getBottomHeight();
               if (patches[6] != null && (float)patches[6].getRegionHeight() != bottomHeight || patches[7] != null && (float)patches[7].getRegionHeight() != bottomHeight || patches[8] != null && (float)patches[8].getRegionHeight() != bottomHeight) {
                  throw new GdxRuntimeException("Bottom side patches must have the same height");
               } else {
                  float topHeight = this.getTopHeight();
                  if (patches[0] != null && (float)patches[0].getRegionHeight() != topHeight || patches[1] != null && (float)patches[1].getRegionHeight() != topHeight || patches[2] != null && (float)patches[2].getRegionHeight() != topHeight) {
                     throw new GdxRuntimeException("Top side patches must have the same height");
                  }
               }
            } else {
               throw new GdxRuntimeException("Right side patches must have the same width");
            }
         } else {
            throw new GdxRuntimeException("Left side patches must have the same width");
         }
      } else {
         throw new IllegalArgumentException("NinePatch needs nine TextureRegions");
      }
   }

   public NinePatch(NinePatch ninePatch) {
      this(ninePatch, ninePatch.color);
   }

   public NinePatch(NinePatch ninePatch, Color color) {
      this.bottomLeft = -1;
      this.bottomCenter = -1;
      this.bottomRight = -1;
      this.middleLeft = -1;
      this.middleCenter = -1;
      this.middleRight = -1;
      this.topLeft = -1;
      this.topCenter = -1;
      this.topRight = -1;
      this.vertices = new float[180];
      this.color = new Color(Color.WHITE);
      this.padLeft = -1;
      this.padRight = -1;
      this.padTop = -1;
      this.padBottom = -1;
      this.texture = ninePatch.texture;
      this.bottomLeft = ninePatch.bottomLeft;
      this.bottomCenter = ninePatch.bottomCenter;
      this.bottomRight = ninePatch.bottomRight;
      this.middleLeft = ninePatch.middleLeft;
      this.middleCenter = ninePatch.middleCenter;
      this.middleRight = ninePatch.middleRight;
      this.topLeft = ninePatch.topLeft;
      this.topCenter = ninePatch.topCenter;
      this.topRight = ninePatch.topRight;
      this.leftWidth = ninePatch.leftWidth;
      this.rightWidth = ninePatch.rightWidth;
      this.middleWidth = ninePatch.middleWidth;
      this.middleHeight = ninePatch.middleHeight;
      this.topHeight = ninePatch.topHeight;
      this.bottomHeight = ninePatch.bottomHeight;
      this.vertices = new float[ninePatch.vertices.length];
      System.arraycopy(ninePatch.vertices, 0, this.vertices, 0, ninePatch.vertices.length);
      this.idx = ninePatch.idx;
      this.color.set(color);
   }

   private void load(TextureRegion[] patches) {
      float color = Color.WHITE.toFloatBits();
      if (patches[6] != null) {
         this.bottomLeft = this.add(patches[6], color);
         this.leftWidth = (float)patches[6].getRegionWidth();
         this.bottomHeight = (float)patches[6].getRegionHeight();
      }

      if (patches[7] != null) {
         this.bottomCenter = this.add(patches[7], color);
         this.middleWidth = Math.max(this.middleWidth, (float)patches[7].getRegionWidth());
         this.bottomHeight = Math.max(this.bottomHeight, (float)patches[7].getRegionHeight());
      }

      if (patches[8] != null) {
         this.bottomRight = this.add(patches[8], color);
         this.rightWidth = Math.max(this.rightWidth, (float)patches[8].getRegionWidth());
         this.bottomHeight = Math.max(this.bottomHeight, (float)patches[8].getRegionHeight());
      }

      if (patches[3] != null) {
         this.middleLeft = this.add(patches[3], color);
         this.leftWidth = Math.max(this.leftWidth, (float)patches[3].getRegionWidth());
         this.middleHeight = Math.max(this.middleHeight, (float)patches[3].getRegionHeight());
      }

      if (patches[4] != null) {
         this.middleCenter = this.add(patches[4], color);
         this.middleWidth = Math.max(this.middleWidth, (float)patches[4].getRegionWidth());
         this.middleHeight = Math.max(this.middleHeight, (float)patches[4].getRegionHeight());
      }

      if (patches[5] != null) {
         this.middleRight = this.add(patches[5], color);
         this.rightWidth = Math.max(this.rightWidth, (float)patches[5].getRegionWidth());
         this.middleHeight = Math.max(this.middleHeight, (float)patches[5].getRegionHeight());
      }

      if (patches[0] != null) {
         this.topLeft = this.add(patches[0], color);
         this.leftWidth = Math.max(this.leftWidth, (float)patches[0].getRegionWidth());
         this.topHeight = Math.max(this.topHeight, (float)patches[0].getRegionHeight());
      }

      if (patches[1] != null) {
         this.topCenter = this.add(patches[1], color);
         this.middleWidth = Math.max(this.middleWidth, (float)patches[1].getRegionWidth());
         this.topHeight = Math.max(this.topHeight, (float)patches[1].getRegionHeight());
      }

      if (patches[2] != null) {
         this.topRight = this.add(patches[2], color);
         this.rightWidth = Math.max(this.rightWidth, (float)patches[2].getRegionWidth());
         this.topHeight = Math.max(this.topHeight, (float)patches[2].getRegionHeight());
      }

      if (this.idx < this.vertices.length) {
         float[] newVertices = new float[this.idx];
         System.arraycopy(this.vertices, 0, newVertices, 0, this.idx);
         this.vertices = newVertices;
      }

   }

   private int add(TextureRegion region, float color) {
      if (this.texture == null) {
         this.texture = region.getTexture();
      } else if (this.texture != region.getTexture()) {
         throw new IllegalArgumentException("All regions must be from the same texture.");
      }

      float u = region.u;
      float v = region.v2;
      float u2 = region.u2;
      float v2 = region.v;
      float[] vertices = this.vertices;
      this.idx += 2;
      vertices[this.idx++] = color;
      vertices[this.idx++] = u;
      vertices[this.idx] = v;
      this.idx += 3;
      vertices[this.idx++] = color;
      vertices[this.idx++] = u;
      vertices[this.idx] = v2;
      this.idx += 3;
      vertices[this.idx++] = color;
      vertices[this.idx++] = u2;
      vertices[this.idx] = v2;
      this.idx += 3;
      vertices[this.idx++] = color;
      vertices[this.idx++] = u2;
      vertices[this.idx++] = v;
      return this.idx - 20;
   }

   private void set(int idx, float x, float y, float width, float height, float color) {
      float fx2 = x + width;
      float fy2 = y + height;
      float[] vertices = this.vertices;
      vertices[idx++] = x;
      vertices[idx++] = y;
      vertices[idx] = color;
      idx += 3;
      vertices[idx++] = x;
      vertices[idx++] = fy2;
      vertices[idx] = color;
      idx += 3;
      vertices[idx++] = fx2;
      vertices[idx++] = fy2;
      vertices[idx] = color;
      idx += 3;
      vertices[idx++] = fx2;
      vertices[idx++] = y;
      vertices[idx] = color;
   }

   public void draw(SpriteBatch batch, float x, float y, float width, float height) {
      float centerColumnX = x + this.leftWidth;
      float rightColumnX = x + width - this.rightWidth;
      float middleRowY = y + this.bottomHeight;
      float topRowY = y + height - this.topHeight;
      float c = tmpDrawColor.set(this.color).mul(batch.getColor()).toFloatBits();
      if (this.bottomLeft != -1) {
         this.set(this.bottomLeft, x, y, centerColumnX - x, middleRowY - y, c);
      }

      if (this.bottomCenter != -1) {
         this.set(this.bottomCenter, centerColumnX, y, rightColumnX - centerColumnX, middleRowY - y, c);
      }

      if (this.bottomRight != -1) {
         this.set(this.bottomRight, rightColumnX, y, x + width - rightColumnX, middleRowY - y, c);
      }

      if (this.middleLeft != -1) {
         this.set(this.middleLeft, x, middleRowY, centerColumnX - x, topRowY - middleRowY, c);
      }

      if (this.middleCenter != -1) {
         this.set(this.middleCenter, centerColumnX, middleRowY, rightColumnX - centerColumnX, topRowY - middleRowY, c);
      }

      if (this.middleRight != -1) {
         this.set(this.middleRight, rightColumnX, middleRowY, x + width - rightColumnX, topRowY - middleRowY, c);
      }

      if (this.topLeft != -1) {
         this.set(this.topLeft, x, topRowY, centerColumnX - x, y + height - topRowY, c);
      }

      if (this.topCenter != -1) {
         this.set(this.topCenter, centerColumnX, topRowY, rightColumnX - centerColumnX, y + height - topRowY, c);
      }

      if (this.topRight != -1) {
         this.set(this.topRight, rightColumnX, topRowY, x + width - rightColumnX, y + height - topRowY, c);
      }

      batch.draw(this.texture, this.vertices, 0, this.idx);
   }

   public void setColor(Color color) {
      this.color.set(color);
   }

   public Color getColor() {
      return this.color;
   }

   public float getLeftWidth() {
      return this.leftWidth;
   }

   public void setLeftWidth(float leftWidth) {
      this.leftWidth = leftWidth;
   }

   public float getRightWidth() {
      return this.rightWidth;
   }

   public void setRightWidth(float rightWidth) {
      this.rightWidth = rightWidth;
   }

   public float getTopHeight() {
      return this.topHeight;
   }

   public void setTopHeight(float topHeight) {
      this.topHeight = topHeight;
   }

   public float getBottomHeight() {
      return this.bottomHeight;
   }

   public void setBottomHeight(float bottomHeight) {
      this.bottomHeight = bottomHeight;
   }

   public float getMiddleWidth() {
      return this.middleWidth;
   }

   public void setMiddleWidth(float middleWidth) {
      this.middleWidth = middleWidth;
   }

   public float getMiddleHeight() {
      return this.middleHeight;
   }

   public void setMiddleHeight(float middleHeight) {
      this.middleHeight = middleHeight;
   }

   public float getTotalWidth() {
      return this.leftWidth + this.middleWidth + this.rightWidth;
   }

   public float getTotalHeight() {
      return this.topHeight + this.middleHeight + this.bottomHeight;
   }

   public void setPadding(int left, int right, int top, int bottom) {
      this.padLeft = left;
      this.padRight = right;
      this.padTop = top;
      this.padBottom = bottom;
   }

   public float getPadLeft() {
      return this.padLeft == -1 ? this.getLeftWidth() : (float)this.padLeft;
   }

   public void setPadLeft(int left) {
      this.padLeft = left;
   }

   public float getPadRight() {
      return this.padRight == -1 ? this.getRightWidth() : (float)this.padRight;
   }

   public void setPadRight(int right) {
      this.padRight = right;
   }

   public float getPadTop() {
      return this.padTop == -1 ? this.getTopHeight() : (float)this.padTop;
   }

   public void setPadTop(int top) {
      this.padTop = top;
   }

   public float getPadBottom() {
      return this.padBottom == -1 ? this.getBottomHeight() : (float)this.padBottom;
   }

   public void setPadBottom(int bottom) {
      this.padBottom = bottom;
   }

   public Texture getTexture() {
      return this.texture;
   }
}
