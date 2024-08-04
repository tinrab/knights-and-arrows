package com.badlogic.gdx.graphics;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.NumberUtils;

public class Color {
   public static final Color CLEAR = new Color(0.0F, 0.0F, 0.0F, 0.0F);
   public static final Color WHITE = new Color(1.0F, 1.0F, 1.0F, 1.0F);
   public static final Color BLACK = new Color(0.0F, 0.0F, 0.0F, 1.0F);
   public static final Color RED = new Color(1.0F, 0.0F, 0.0F, 1.0F);
   public static final Color DARK_RED = new Color(2130706687);
   public static final Color GREEN = new Color(0.0F, 1.0F, 0.0F, 1.0F);
   public static final Color DARK_GREEN = new Color(8323327);
   public static final Color BLUE = new Color(0.0F, 0.0F, 1.0F, 1.0F);
   public static final Color LIGHT_GRAY = new Color(0.75F, 0.75F, 0.75F, 1.0F);
   public static final Color GRAY = new Color(0.5F, 0.5F, 0.5F, 1.0F);
   public static final Color DARK_GRAY = new Color(0.25F, 0.25F, 0.25F, 1.0F);
   public static final Color PINK = new Color(1.0F, 0.68F, 0.68F, 1.0F);
   public static final Color ORANGE = new Color(1.0F, 0.78F, 0.0F, 1.0F);
   public static final Color YELLOW = new Color(1.0F, 1.0F, 0.0F, 1.0F);
   public static final Color MAGENTA = new Color(1.0F, 0.0F, 1.0F, 1.0F);
   public static final Color CYAN = new Color(0.0F, 1.0F, 1.0F, 1.0F);
   /** @deprecated */
   @Deprecated
   public static Color tmp = new Color();
   public float r;
   public float g;
   public float b;
   public float a;

   public Color() {
   }

   public Color(int rgba8888) {
      rgba8888ToColor(this, rgba8888);
   }

   public Color(float r, float g, float b, float a) {
      this.r = r;
      this.g = g;
      this.b = b;
      this.a = a;
      this.clamp();
   }

   public Color(Color color) {
      this.set(color);
   }

   public Color set(Color color) {
      this.r = color.r;
      this.g = color.g;
      this.b = color.b;
      this.a = color.a;
      return this;
   }

   public Color mul(Color color) {
      this.r *= color.r;
      this.g *= color.g;
      this.b *= color.b;
      this.a *= color.a;
      return this.clamp();
   }

   public Color mul(float value) {
      this.r *= value;
      this.g *= value;
      this.b *= value;
      this.a *= value;
      return this.clamp();
   }

   public Color add(Color color) {
      this.r += color.r;
      this.g += color.g;
      this.b += color.b;
      this.a += color.a;
      return this.clamp();
   }

   public Color sub(Color color) {
      this.r -= color.r;
      this.g -= color.g;
      this.b -= color.b;
      this.a -= color.a;
      return this.clamp();
   }

   public Color clamp() {
      if (this.r < 0.0F) {
         this.r = 0.0F;
      } else if (this.r > 1.0F) {
         this.r = 1.0F;
      }

      if (this.g < 0.0F) {
         this.g = 0.0F;
      } else if (this.g > 1.0F) {
         this.g = 1.0F;
      }

      if (this.b < 0.0F) {
         this.b = 0.0F;
      } else if (this.b > 1.0F) {
         this.b = 1.0F;
      }

      if (this.a < 0.0F) {
         this.a = 0.0F;
      } else if (this.a > 1.0F) {
         this.a = 1.0F;
      }

      return this;
   }

   public Color set(float r, float g, float b, float a) {
      this.r = r;
      this.g = g;
      this.b = b;
      this.a = a;
      return this.clamp();
   }

   public Color set(int rgba) {
      rgba8888ToColor(this, rgba);
      return this;
   }

   public Color add(float r, float g, float b, float a) {
      this.r += r;
      this.g += g;
      this.b += b;
      this.a += a;
      return this.clamp();
   }

   public Color sub(float r, float g, float b, float a) {
      this.r -= r;
      this.g -= g;
      this.b -= b;
      this.a -= a;
      return this.clamp();
   }

   public Color mul(float r, float g, float b, float a) {
      this.r *= r;
      this.g *= g;
      this.b *= b;
      this.a *= a;
      return this.clamp();
   }

   public Color lerp(Color target, float t) {
      this.r += t * (target.r - this.r);
      this.g += t * (target.g - this.g);
      this.b += t * (target.b - this.b);
      this.a += t * (target.a - this.a);
      return this.clamp();
   }

   public Color lerp(float r, float g, float b, float a, float t) {
      this.r += t * (r - this.r);
      this.g += t * (g - this.g);
      this.b += t * (b - this.b);
      this.a += t * (a - this.a);
      return this.clamp();
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Color color = (Color)o;
         return this.toIntBits() == color.toIntBits();
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.r != 0.0F ? NumberUtils.floatToIntBits(this.r) : 0;
      result = 31 * result + (this.g != 0.0F ? NumberUtils.floatToIntBits(this.g) : 0);
      result = 31 * result + (this.b != 0.0F ? NumberUtils.floatToIntBits(this.b) : 0);
      result = 31 * result + (this.a != 0.0F ? NumberUtils.floatToIntBits(this.a) : 0);
      return result;
   }

   public float toFloatBits() {
      int color = (int)(255.0F * this.a) << 24 | (int)(255.0F * this.b) << 16 | (int)(255.0F * this.g) << 8 | (int)(255.0F * this.r);
      return NumberUtils.intToFloatColor(color);
   }

   public int toIntBits() {
      int color = (int)(255.0F * this.a) << 24 | (int)(255.0F * this.b) << 16 | (int)(255.0F * this.g) << 8 | (int)(255.0F * this.r);
      return color;
   }

   public String toString() {
      String value;
      for(value = Integer.toHexString((int)(255.0F * this.r) << 24 | (int)(255.0F * this.g) << 16 | (int)(255.0F * this.b) << 8 | (int)(255.0F * this.a)); value.length() < 8; value = "0" + value) {
      }

      return value;
   }

   public static Color valueOf(String hex) {
      int r = Integer.valueOf(hex.substring(0, 2), 16);
      int g = Integer.valueOf(hex.substring(2, 4), 16);
      int b = Integer.valueOf(hex.substring(4, 6), 16);
      int a = hex.length() != 8 ? 255 : Integer.valueOf(hex.substring(6, 8), 16);
      return new Color((float)r / 255.0F, (float)g / 255.0F, (float)b / 255.0F, (float)a / 255.0F);
   }

   public static float toFloatBits(int r, int g, int b, int a) {
      int color = a << 24 | b << 16 | g << 8 | r;
      float floatColor = NumberUtils.intToFloatColor(color);
      return floatColor;
   }

   public static float toFloatBits(float r, float g, float b, float a) {
      int color = (int)(255.0F * a) << 24 | (int)(255.0F * b) << 16 | (int)(255.0F * g) << 8 | (int)(255.0F * r);
      return NumberUtils.intToFloatColor(color);
   }

   public static int toIntBits(int r, int g, int b, int a) {
      return a << 24 | b << 16 | g << 8 | r;
   }

   public static int alpha(float alpha) {
      return (int)(alpha * 255.0F);
   }

   public static int luminanceAlpha(float luminance, float alpha) {
      return (int)(luminance * 255.0F) << 8 | (int)(alpha * 255.0F);
   }

   public static int rgb565(float r, float g, float b) {
      return (int)(r * 31.0F) << 11 | (int)(g * 63.0F) << 5 | (int)(b * 31.0F);
   }

   public static int rgba4444(float r, float g, float b, float a) {
      return (int)(r * 15.0F) << 12 | (int)(g * 15.0F) << 8 | (int)(b * 15.0F) << 4 | (int)(a * 15.0F);
   }

   public static int rgb888(float r, float g, float b) {
      return (int)(r * 255.0F) << 16 | (int)(g * 255.0F) << 8 | (int)(b * 255.0F);
   }

   public static int rgba8888(float r, float g, float b, float a) {
      return (int)(r * 255.0F) << 24 | (int)(g * 255.0F) << 16 | (int)(b * 255.0F) << 8 | (int)(a * 255.0F);
   }

   public static int rgb565(Color color) {
      return (int)(color.r * 31.0F) << 11 | (int)(color.g * 63.0F) << 5 | (int)(color.b * 31.0F);
   }

   public static int rgba4444(Color color) {
      return (int)(color.r * 15.0F) << 12 | (int)(color.g * 15.0F) << 8 | (int)(color.b * 15.0F) << 4 | (int)(color.a * 15.0F);
   }

   public static int rgb888(Color color) {
      return (int)(color.r * 255.0F) << 16 | (int)(color.g * 255.0F) << 8 | (int)(color.b * 255.0F);
   }

   public static int rgba8888(Color color) {
      return (int)(color.r * 255.0F) << 24 | (int)(color.g * 255.0F) << 16 | (int)(color.b * 255.0F) << 8 | (int)(color.a * 255.0F);
   }

   public static void rgb565ToColor(Color color, int value) {
      color.r = (float)((value & '\uf800') >>> 11) / 31.0F;
      color.g = (float)((value & 2016) >>> 5) / 63.0F;
      color.b = (float)((value & 31) >>> 0) / 31.0F;
   }

   public static void rgba4444ToColor(Color color, int value) {
      color.r = (float)((value & '\uf000') >>> 12) / 15.0F;
      color.g = (float)((value & 3840) >>> 8) / 15.0F;
      color.b = (float)((value & 240) >>> 4) / 15.0F;
      color.a = (float)(value & 15) / 15.0F;
   }

   public static void rgb888ToColor(Color color, int value) {
      color.r = (float)((value & 16711680) >>> 16) / 255.0F;
      color.g = (float)((value & '\uff00') >>> 8) / 255.0F;
      color.b = (float)(value & 255) / 255.0F;
   }

   public static void rgba8888ToColor(Color color, int value) {
      color.r = (float)((value & -16777216) >>> 24) / 255.0F;
      color.g = (float)((value & 16711680) >>> 16) / 255.0F;
      color.b = (float)((value & '\uff00') >>> 8) / 255.0F;
      color.a = (float)(value & 255) / 255.0F;
   }

   public Color tmp() {
      return tmp.set(this);
   }

   public Color cpy() {
      return new Color(this);
   }

   public static Color random() {
      return new Color((int)(Math.random() * 2.147483647E9D));
   }

   public void clamp(float min, float max) {
      this.r = MathUtils.clamp(this.r, min, max);
      this.g = MathUtils.clamp(this.g, min, max);
      this.b = MathUtils.clamp(this.b, min, max);
      this.a = MathUtils.clamp(this.a, min, max);
   }
}
