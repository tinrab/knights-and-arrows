package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.graphics.Texture;

public class TextureRegion {
   Texture texture;
   float u;
   float v;
   float u2;
   float v2;
   int regionWidth;
   int regionHeight;

   public TextureRegion() {
   }

   public TextureRegion(Texture texture) {
      if (texture == null) {
         throw new IllegalArgumentException("texture cannot be null.");
      } else {
         this.texture = texture;
         this.setRegion(0, 0, texture.getWidth(), texture.getHeight());
      }
   }

   public TextureRegion(Texture texture, int width, int height) {
      this.texture = texture;
      this.setRegion(0, 0, width, height);
   }

   public TextureRegion(Texture texture, int x, int y, int width, int height) {
      this.texture = texture;
      this.setRegion(x, y, width, height);
   }

   public TextureRegion(Texture texture, float u, float v, float u2, float v2) {
      this.texture = texture;
      this.setRegion(u, v, u2, v2);
   }

   public TextureRegion(TextureRegion region) {
      this.setRegion(region);
   }

   public TextureRegion(TextureRegion region, int x, int y, int width, int height) {
      this.setRegion(region, x, y, width, height);
   }

   public void setRegion(Texture texture) {
      this.texture = texture;
      this.setRegion(0, 0, texture.getWidth(), texture.getHeight());
   }

   public void setRegion(int x, int y, int width, int height) {
      float invTexWidth = 1.0F / (float)this.texture.getWidth();
      float invTexHeight = 1.0F / (float)this.texture.getHeight();
      this.setRegion((float)x * invTexWidth, (float)y * invTexHeight, (float)(x + width) * invTexWidth, (float)(y + height) * invTexHeight);
      this.regionWidth = Math.abs(width);
      this.regionHeight = Math.abs(height);
   }

   public void setRegion(float u, float v, float u2, float v2) {
      this.u = u;
      this.v = v;
      this.u2 = u2;
      this.v2 = v2;
      this.regionWidth = Math.round(Math.abs(u2 - u) * (float)this.texture.getWidth());
      this.regionHeight = Math.round(Math.abs(v2 - v) * (float)this.texture.getHeight());
   }

   public void setRegion(TextureRegion region) {
      this.texture = region.texture;
      this.setRegion(region.u, region.v, region.u2, region.v2);
   }

   public void setRegion(TextureRegion region, int x, int y, int width, int height) {
      this.texture = region.texture;
      this.setRegion(region.getRegionX() + x, region.getRegionY() + y, width, height);
   }

   public Texture getTexture() {
      return this.texture;
   }

   public void setTexture(Texture texture) {
      this.texture = texture;
   }

   public float getU() {
      return this.u;
   }

   public void setU(float u) {
      this.u = u;
      this.regionWidth = Math.round(Math.abs(this.u2 - u) * (float)this.texture.getWidth());
   }

   public float getV() {
      return this.v;
   }

   public void setV(float v) {
      this.v = v;
      this.regionHeight = Math.round(Math.abs(this.v2 - v) * (float)this.texture.getHeight());
   }

   public float getU2() {
      return this.u2;
   }

   public void setU2(float u2) {
      this.u2 = u2;
      this.regionWidth = Math.round(Math.abs(u2 - this.u) * (float)this.texture.getWidth());
   }

   public float getV2() {
      return this.v2;
   }

   public void setV2(float v2) {
      this.v2 = v2;
      this.regionHeight = Math.round(Math.abs(v2 - this.v) * (float)this.texture.getHeight());
   }

   public int getRegionX() {
      return Math.round(this.u * (float)this.texture.getWidth());
   }

   public void setRegionX(int x) {
      this.setU((float)x / (float)this.texture.getWidth());
   }

   public int getRegionY() {
      return Math.round(this.v * (float)this.texture.getHeight());
   }

   public void setRegionY(int y) {
      this.setV((float)y / (float)this.texture.getHeight());
   }

   public int getRegionWidth() {
      return this.regionWidth;
   }

   public void setRegionWidth(int width) {
      this.setU2(this.u + (float)width / (float)this.texture.getWidth());
   }

   public int getRegionHeight() {
      return this.regionHeight;
   }

   public void setRegionHeight(int height) {
      this.setV2(this.v + (float)height / (float)this.texture.getHeight());
   }

   public void flip(boolean x, boolean y) {
      float temp;
      if (x) {
         temp = this.u;
         this.u = this.u2;
         this.u2 = temp;
      }

      if (y) {
         temp = this.v;
         this.v = this.v2;
         this.v2 = temp;
      }

   }

   public boolean isFlipX() {
      return this.u > this.u2;
   }

   public boolean isFlipY() {
      return this.v > this.v2;
   }

   public void scroll(float xAmount, float yAmount) {
      float height;
      if (xAmount != 0.0F) {
         height = (this.u2 - this.u) * (float)this.texture.getWidth();
         this.u = (this.u + xAmount) % 1.0F;
         this.u2 = this.u + height / (float)this.texture.getWidth();
      }

      if (yAmount != 0.0F) {
         height = (this.v2 - this.v) * (float)this.texture.getHeight();
         this.v = (this.v + yAmount) % 1.0F;
         this.v2 = this.v + height / (float)this.texture.getHeight();
      }

   }

   public TextureRegion[][] split(int tileWidth, int tileHeight) {
      int x = this.getRegionX();
      int y = this.getRegionY();
      int width = this.regionWidth;
      int height = this.regionHeight;
      int rows = height / tileHeight;
      int cols = width / tileWidth;
      int startX = x;
      TextureRegion[][] tiles = new TextureRegion[rows][cols];

      for(int row = 0; row < rows; y += tileHeight) {
         x = startX;

         for(int col = 0; col < cols; x += tileWidth) {
            tiles[row][col] = new TextureRegion(this.texture, x, y, tileWidth, tileHeight);
            ++col;
         }

         ++row;
      }

      return tiles;
   }

   public static TextureRegion[][] split(Texture texture, int tileWidth, int tileHeight) {
      TextureRegion region = new TextureRegion(texture);
      return region.split(tileWidth, tileHeight);
   }
}