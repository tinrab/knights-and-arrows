package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.NumberUtils;
import java.nio.FloatBuffer;
import java.util.ArrayList;

public class SpriteCache implements Disposable {
   private static final float[] tempVertices = new float[30];
   private final Mesh mesh;
   private boolean drawing;
   private final Matrix4 transformMatrix;
   private final Matrix4 projectionMatrix;
   private ArrayList<SpriteCache.Cache> caches;
   private final Matrix4 combinedMatrix;
   private final ShaderProgram shader;
   private SpriteCache.Cache currentCache;
   private final ArrayList<Texture> textures;
   private final ArrayList<Integer> counts;
   private float color;
   private Color tempColor;
   private ShaderProgram customShader;

   public SpriteCache() {
      this(1000, false);
   }

   public SpriteCache(int size, boolean useIndices) {
      this(size, createDefaultShader(), useIndices);
   }

   public SpriteCache(int size, ShaderProgram shader, boolean useIndices) {
      this.transformMatrix = new Matrix4();
      this.projectionMatrix = new Matrix4();
      this.caches = new ArrayList();
      this.combinedMatrix = new Matrix4();
      this.textures = new ArrayList(8);
      this.counts = new ArrayList(8);
      this.color = Color.WHITE.toFloatBits();
      this.tempColor = new Color(1.0F, 1.0F, 1.0F, 1.0F);
      this.customShader = null;
      this.shader = shader;
      this.mesh = new Mesh(true, size * (useIndices ? 4 : 6), useIndices ? size * 6 : 0, new VertexAttribute[]{new VertexAttribute(1, 2, "a_position"), new VertexAttribute(4, 4, "a_color"), new VertexAttribute(16, 2, "a_texCoord0")});
      this.mesh.setAutoBind(false);
      if (useIndices) {
         int length = size * 6;
         short[] indices = new short[length];
         short j = 0;

         for(int i = 0; i < length; j = (short)(j + 4)) {
            indices[i + 0] = j;
            indices[i + 1] = (short)(j + 1);
            indices[i + 2] = (short)(j + 2);
            indices[i + 3] = (short)(j + 2);
            indices[i + 4] = (short)(j + 3);
            indices[i + 5] = j;
            i += 6;
         }

         this.mesh.setIndices(indices);
      }

      this.projectionMatrix.setToOrtho2D(0.0F, 0.0F, (float)Gdx.graphics.getWidth(), (float)Gdx.graphics.getHeight());
   }

   public void setColor(Color tint) {
      this.color = tint.toFloatBits();
   }

   public void setColor(float r, float g, float b, float a) {
      int intBits = (int)(255.0F * a) << 24 | (int)(255.0F * b) << 16 | (int)(255.0F * g) << 8 | (int)(255.0F * r);
      this.color = NumberUtils.intToFloatColor(intBits);
   }

   public void setColor(float color) {
      this.color = color;
   }

   public Color getColor() {
      int intBits = NumberUtils.floatToIntColor(this.color);
      Color color = this.tempColor;
      color.r = (float)(intBits & 255) / 255.0F;
      color.g = (float)(intBits >>> 8 & 255) / 255.0F;
      color.b = (float)(intBits >>> 16 & 255) / 255.0F;
      color.a = (float)(intBits >>> 24 & 255) / 255.0F;
      return color;
   }

   public void beginCache() {
      if (this.currentCache != null) {
         throw new IllegalStateException("endCache must be called before begin.");
      } else {
         int verticesPerImage = this.mesh.getNumIndices() > 0 ? true : true;
         this.currentCache = new SpriteCache.Cache(this.caches.size(), this.mesh.getVerticesBuffer().limit());
         this.caches.add(this.currentCache);
         this.mesh.getVerticesBuffer().compact();
      }
   }

   public void beginCache(int cacheID) {
      if (this.currentCache != null) {
         throw new IllegalStateException("endCache must be called before begin.");
      } else if (cacheID == this.caches.size() - 1) {
         SpriteCache.Cache oldCache = (SpriteCache.Cache)this.caches.remove(cacheID);
         this.mesh.getVerticesBuffer().limit(oldCache.offset);
         this.beginCache();
      } else {
         this.currentCache = (SpriteCache.Cache)this.caches.get(cacheID);
         this.mesh.getVerticesBuffer().position(this.currentCache.offset);
      }
   }

   public int endCache() {
      if (this.currentCache == null) {
         throw new IllegalStateException("beginCache must be called before endCache.");
      } else {
         SpriteCache.Cache cache = this.currentCache;
         int cacheCount = this.mesh.getVerticesBuffer().position() - cache.offset;
         int i;
         int n;
         if (cache.textures == null) {
            cache.maxCount = cacheCount;
            cache.textureCount = this.textures.size();
            cache.textures = (Texture[])this.textures.toArray(new Texture[cache.textureCount]);
            cache.counts = new int[cache.textureCount];
            i = 0;

            for(n = this.counts.size(); i < n; ++i) {
               cache.counts[i] = (Integer)this.counts.get(i);
            }

            this.mesh.getVerticesBuffer().flip();
         } else {
            if (cacheCount > cache.maxCount) {
               throw new GdxRuntimeException("If a cache is not the last created, it cannot be redefined with more entries than when it was first created: " + cacheCount + " (" + cache.maxCount + " max)");
            }

            cache.textureCount = this.textures.size();
            if (cache.textures.length < cache.textureCount) {
               cache.textures = new Texture[cache.textureCount];
            }

            i = 0;

            for(n = cache.textureCount; i < n; ++i) {
               cache.textures[i] = (Texture)this.textures.get(i);
            }

            if (cache.counts.length < cache.textureCount) {
               cache.counts = new int[cache.textureCount];
            }

            i = 0;

            for(n = cache.textureCount; i < n; ++i) {
               cache.counts[i] = (Integer)this.counts.get(i);
            }

            FloatBuffer vertices = this.mesh.getVerticesBuffer();
            vertices.position(0);
            SpriteCache.Cache lastCache = (SpriteCache.Cache)this.caches.get(this.caches.size() - 1);
            vertices.limit(lastCache.offset + lastCache.maxCount);
         }

         this.currentCache = null;
         this.textures.clear();
         this.counts.clear();
         return cache.id;
      }
   }

   public void clear() {
      this.caches.clear();
      this.mesh.getVerticesBuffer().clear().flip();
   }

   public void add(Texture texture, float[] vertices, int offset, int length) {
      if (this.currentCache == null) {
         throw new IllegalStateException("beginCache must be called before add.");
      } else {
         int verticesPerImage = this.mesh.getNumIndices() > 0 ? 4 : 6;
         int count = length / (verticesPerImage * 5) * 6;
         int lastIndex = this.textures.size() - 1;
         if (lastIndex >= 0 && this.textures.get(lastIndex) == texture) {
            this.counts.set(lastIndex, (Integer)this.counts.get(lastIndex) + count);
         } else {
            this.textures.add(texture);
            this.counts.add(count);
         }

         this.mesh.getVerticesBuffer().put(vertices, offset, length);
      }
   }

   public void add(Texture texture, float x, float y) {
      float fx2 = x + (float)texture.getWidth();
      float fy2 = y + (float)texture.getHeight();
      tempVertices[0] = x;
      tempVertices[1] = y;
      tempVertices[2] = this.color;
      tempVertices[3] = 0.0F;
      tempVertices[4] = 1.0F;
      tempVertices[5] = x;
      tempVertices[6] = fy2;
      tempVertices[7] = this.color;
      tempVertices[8] = 0.0F;
      tempVertices[9] = 0.0F;
      tempVertices[10] = fx2;
      tempVertices[11] = fy2;
      tempVertices[12] = this.color;
      tempVertices[13] = 1.0F;
      tempVertices[14] = 0.0F;
      if (this.mesh.getNumIndices() > 0) {
         tempVertices[15] = fx2;
         tempVertices[16] = y;
         tempVertices[17] = this.color;
         tempVertices[18] = 1.0F;
         tempVertices[19] = 1.0F;
         this.add(texture, tempVertices, 0, 20);
      } else {
         tempVertices[15] = fx2;
         tempVertices[16] = fy2;
         tempVertices[17] = this.color;
         tempVertices[18] = 1.0F;
         tempVertices[19] = 0.0F;
         tempVertices[20] = fx2;
         tempVertices[21] = y;
         tempVertices[22] = this.color;
         tempVertices[23] = 1.0F;
         tempVertices[24] = 1.0F;
         tempVertices[25] = x;
         tempVertices[26] = y;
         tempVertices[27] = this.color;
         tempVertices[28] = 0.0F;
         tempVertices[29] = 1.0F;
         this.add(texture, tempVertices, 0, 30);
      }

   }

   public void add(Texture texture, float x, float y, int srcWidth, int srcHeight, float u, float v, float u2, float v2, float color) {
      float fx2 = x + (float)srcWidth;
      float fy2 = y + (float)srcHeight;
      tempVertices[0] = x;
      tempVertices[1] = y;
      tempVertices[2] = color;
      tempVertices[3] = u;
      tempVertices[4] = v;
      tempVertices[5] = x;
      tempVertices[6] = fy2;
      tempVertices[7] = color;
      tempVertices[8] = u;
      tempVertices[9] = v2;
      tempVertices[10] = fx2;
      tempVertices[11] = fy2;
      tempVertices[12] = color;
      tempVertices[13] = u2;
      tempVertices[14] = v2;
      if (this.mesh.getNumIndices() > 0) {
         tempVertices[15] = fx2;
         tempVertices[16] = y;
         tempVertices[17] = color;
         tempVertices[18] = u2;
         tempVertices[19] = v;
         this.add(texture, tempVertices, 0, 20);
      } else {
         tempVertices[15] = fx2;
         tempVertices[16] = fy2;
         tempVertices[17] = color;
         tempVertices[18] = u2;
         tempVertices[19] = v2;
         tempVertices[20] = fx2;
         tempVertices[21] = y;
         tempVertices[22] = color;
         tempVertices[23] = u2;
         tempVertices[24] = v;
         tempVertices[25] = x;
         tempVertices[26] = y;
         tempVertices[27] = color;
         tempVertices[28] = u;
         tempVertices[29] = v;
         this.add(texture, tempVertices, 0, 30);
      }

   }

   public void add(Texture texture, float x, float y, int srcX, int srcY, int srcWidth, int srcHeight) {
      float invTexWidth = 1.0F / (float)texture.getWidth();
      float invTexHeight = 1.0F / (float)texture.getHeight();
      float u = (float)srcX * invTexWidth;
      float v = (float)(srcY + srcHeight) * invTexHeight;
      float u2 = (float)(srcX + srcWidth) * invTexWidth;
      float v2 = (float)srcY * invTexHeight;
      float fx2 = x + (float)srcWidth;
      float fy2 = y + (float)srcHeight;
      tempVertices[0] = x;
      tempVertices[1] = y;
      tempVertices[2] = this.color;
      tempVertices[3] = u;
      tempVertices[4] = v;
      tempVertices[5] = x;
      tempVertices[6] = fy2;
      tempVertices[7] = this.color;
      tempVertices[8] = u;
      tempVertices[9] = v2;
      tempVertices[10] = fx2;
      tempVertices[11] = fy2;
      tempVertices[12] = this.color;
      tempVertices[13] = u2;
      tempVertices[14] = v2;
      if (this.mesh.getNumIndices() > 0) {
         tempVertices[15] = fx2;
         tempVertices[16] = y;
         tempVertices[17] = this.color;
         tempVertices[18] = u2;
         tempVertices[19] = v;
         this.add(texture, tempVertices, 0, 20);
      } else {
         tempVertices[15] = fx2;
         tempVertices[16] = fy2;
         tempVertices[17] = this.color;
         tempVertices[18] = u2;
         tempVertices[19] = v2;
         tempVertices[20] = fx2;
         tempVertices[21] = y;
         tempVertices[22] = this.color;
         tempVertices[23] = u2;
         tempVertices[24] = v;
         tempVertices[25] = x;
         tempVertices[26] = y;
         tempVertices[27] = this.color;
         tempVertices[28] = u;
         tempVertices[29] = v;
         this.add(texture, tempVertices, 0, 30);
      }

   }

   public void add(Texture texture, float x, float y, float width, float height, int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY) {
      float invTexWidth = 1.0F / (float)texture.getWidth();
      float invTexHeight = 1.0F / (float)texture.getHeight();
      float u = (float)srcX * invTexWidth;
      float v = (float)(srcY + srcHeight) * invTexHeight;
      float u2 = (float)(srcX + srcWidth) * invTexWidth;
      float v2 = (float)srcY * invTexHeight;
      float fx2 = x + width;
      float fy2 = y + height;
      float tmp;
      if (flipX) {
         tmp = u;
         u = u2;
         u2 = tmp;
      }

      if (flipY) {
         tmp = v;
         v = v2;
         v2 = tmp;
      }

      tempVertices[0] = x;
      tempVertices[1] = y;
      tempVertices[2] = this.color;
      tempVertices[3] = u;
      tempVertices[4] = v;
      tempVertices[5] = x;
      tempVertices[6] = fy2;
      tempVertices[7] = this.color;
      tempVertices[8] = u;
      tempVertices[9] = v2;
      tempVertices[10] = fx2;
      tempVertices[11] = fy2;
      tempVertices[12] = this.color;
      tempVertices[13] = u2;
      tempVertices[14] = v2;
      if (this.mesh.getNumIndices() > 0) {
         tempVertices[15] = fx2;
         tempVertices[16] = y;
         tempVertices[17] = this.color;
         tempVertices[18] = u2;
         tempVertices[19] = v;
         this.add(texture, tempVertices, 0, 20);
      } else {
         tempVertices[15] = fx2;
         tempVertices[16] = fy2;
         tempVertices[17] = this.color;
         tempVertices[18] = u2;
         tempVertices[19] = v2;
         tempVertices[20] = fx2;
         tempVertices[21] = y;
         tempVertices[22] = this.color;
         tempVertices[23] = u2;
         tempVertices[24] = v;
         tempVertices[25] = x;
         tempVertices[26] = y;
         tempVertices[27] = this.color;
         tempVertices[28] = u;
         tempVertices[29] = v;
         this.add(texture, tempVertices, 0, 30);
      }

   }

   public void add(Texture texture, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation, int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY) {
      float worldOriginX = x + originX;
      float worldOriginY = y + originY;
      float fx = -originX;
      float fy = -originY;
      float fx2 = width - originX;
      float fy2 = height - originY;
      if (scaleX != 1.0F || scaleY != 1.0F) {
         fx *= scaleX;
         fy *= scaleY;
         fx2 *= scaleX;
         fy2 *= scaleY;
      }

      float x1;
      float y1;
      float x2;
      float y2;
      float x3;
      float y3;
      float x4;
      float y4;
      float invTexWidth;
      float invTexHeight;
      if (rotation != 0.0F) {
         invTexWidth = MathUtils.cosDeg(rotation);
         invTexHeight = MathUtils.sinDeg(rotation);
         x1 = invTexWidth * fx - invTexHeight * fy;
         y1 = invTexHeight * fx + invTexWidth * fy;
         x2 = invTexWidth * fx - invTexHeight * fy2;
         y2 = invTexHeight * fx + invTexWidth * fy2;
         x3 = invTexWidth * fx2 - invTexHeight * fy2;
         y3 = invTexHeight * fx2 + invTexWidth * fy2;
         x4 = x1 + (x3 - x2);
         y4 = y3 - (y2 - y1);
      } else {
         x1 = fx;
         y1 = fy;
         x2 = fx;
         y2 = fy2;
         x3 = fx2;
         y3 = fy2;
         x4 = fx2;
         y4 = fy;
      }

      x1 += worldOriginX;
      y1 += worldOriginY;
      x2 += worldOriginX;
      y2 += worldOriginY;
      x3 += worldOriginX;
      y3 += worldOriginY;
      x4 += worldOriginX;
      y4 += worldOriginY;
      invTexWidth = 1.0F / (float)texture.getWidth();
      invTexHeight = 1.0F / (float)texture.getHeight();
      float u = (float)srcX * invTexWidth;
      float v = (float)(srcY + srcHeight) * invTexHeight;
      float u2 = (float)(srcX + srcWidth) * invTexWidth;
      float v2 = (float)srcY * invTexHeight;
      float tmp;
      if (flipX) {
         tmp = u;
         u = u2;
         u2 = tmp;
      }

      if (flipY) {
         tmp = v;
         v = v2;
         v2 = tmp;
      }

      tempVertices[0] = x1;
      tempVertices[1] = y1;
      tempVertices[2] = this.color;
      tempVertices[3] = u;
      tempVertices[4] = v;
      tempVertices[5] = x2;
      tempVertices[6] = y2;
      tempVertices[7] = this.color;
      tempVertices[8] = u;
      tempVertices[9] = v2;
      tempVertices[10] = x3;
      tempVertices[11] = y3;
      tempVertices[12] = this.color;
      tempVertices[13] = u2;
      tempVertices[14] = v2;
      if (this.mesh.getNumIndices() > 0) {
         tempVertices[15] = x4;
         tempVertices[16] = y4;
         tempVertices[17] = this.color;
         tempVertices[18] = u2;
         tempVertices[19] = v;
         this.add(texture, tempVertices, 0, 20);
      } else {
         tempVertices[15] = x3;
         tempVertices[16] = y3;
         tempVertices[17] = this.color;
         tempVertices[18] = u2;
         tempVertices[19] = v2;
         tempVertices[20] = x4;
         tempVertices[21] = y4;
         tempVertices[22] = this.color;
         tempVertices[23] = u2;
         tempVertices[24] = v;
         tempVertices[25] = x1;
         tempVertices[26] = y1;
         tempVertices[27] = this.color;
         tempVertices[28] = u;
         tempVertices[29] = v;
         this.add(texture, tempVertices, 0, 30);
      }

   }

   public void add(TextureRegion region, float x, float y) {
      this.add(region, x, y, (float)region.getRegionWidth(), (float)region.getRegionHeight());
   }

   public void add(TextureRegion region, float x, float y, float width, float height) {
      float fx2 = x + width;
      float fy2 = y + height;
      float u = region.u;
      float v = region.v2;
      float u2 = region.u2;
      float v2 = region.v;
      tempVertices[0] = x;
      tempVertices[1] = y;
      tempVertices[2] = this.color;
      tempVertices[3] = u;
      tempVertices[4] = v;
      tempVertices[5] = x;
      tempVertices[6] = fy2;
      tempVertices[7] = this.color;
      tempVertices[8] = u;
      tempVertices[9] = v2;
      tempVertices[10] = fx2;
      tempVertices[11] = fy2;
      tempVertices[12] = this.color;
      tempVertices[13] = u2;
      tempVertices[14] = v2;
      if (this.mesh.getNumIndices() > 0) {
         tempVertices[15] = fx2;
         tempVertices[16] = y;
         tempVertices[17] = this.color;
         tempVertices[18] = u2;
         tempVertices[19] = v;
         this.add(region.texture, tempVertices, 0, 20);
      } else {
         tempVertices[15] = fx2;
         tempVertices[16] = fy2;
         tempVertices[17] = this.color;
         tempVertices[18] = u2;
         tempVertices[19] = v2;
         tempVertices[20] = fx2;
         tempVertices[21] = y;
         tempVertices[22] = this.color;
         tempVertices[23] = u2;
         tempVertices[24] = v;
         tempVertices[25] = x;
         tempVertices[26] = y;
         tempVertices[27] = this.color;
         tempVertices[28] = u;
         tempVertices[29] = v;
         this.add(region.texture, tempVertices, 0, 30);
      }

   }

   public void add(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation) {
      float worldOriginX = x + originX;
      float worldOriginY = y + originY;
      float fx = -originX;
      float fy = -originY;
      float fx2 = width - originX;
      float fy2 = height - originY;
      if (scaleX != 1.0F || scaleY != 1.0F) {
         fx *= scaleX;
         fy *= scaleY;
         fx2 *= scaleX;
         fy2 *= scaleY;
      }

      float x1;
      float y1;
      float x2;
      float y2;
      float x3;
      float y3;
      float x4;
      float y4;
      float u;
      float v;
      if (rotation != 0.0F) {
         u = MathUtils.cosDeg(rotation);
         v = MathUtils.sinDeg(rotation);
         x1 = u * fx - v * fy;
         y1 = v * fx + u * fy;
         x2 = u * fx - v * fy2;
         y2 = v * fx + u * fy2;
         x3 = u * fx2 - v * fy2;
         y3 = v * fx2 + u * fy2;
         x4 = x1 + (x3 - x2);
         y4 = y3 - (y2 - y1);
      } else {
         x1 = fx;
         y1 = fy;
         x2 = fx;
         y2 = fy2;
         x3 = fx2;
         y3 = fy2;
         x4 = fx2;
         y4 = fy;
      }

      x1 += worldOriginX;
      y1 += worldOriginY;
      x2 += worldOriginX;
      y2 += worldOriginY;
      x3 += worldOriginX;
      y3 += worldOriginY;
      x4 += worldOriginX;
      y4 += worldOriginY;
      u = region.u;
      v = region.v2;
      float u2 = region.u2;
      float v2 = region.v;
      tempVertices[0] = x1;
      tempVertices[1] = y1;
      tempVertices[2] = this.color;
      tempVertices[3] = u;
      tempVertices[4] = v;
      tempVertices[5] = x2;
      tempVertices[6] = y2;
      tempVertices[7] = this.color;
      tempVertices[8] = u;
      tempVertices[9] = v2;
      tempVertices[10] = x3;
      tempVertices[11] = y3;
      tempVertices[12] = this.color;
      tempVertices[13] = u2;
      tempVertices[14] = v2;
      if (this.mesh.getNumIndices() > 0) {
         tempVertices[15] = x4;
         tempVertices[16] = y4;
         tempVertices[17] = this.color;
         tempVertices[18] = u2;
         tempVertices[19] = v;
         this.add(region.texture, tempVertices, 0, 20);
      } else {
         tempVertices[15] = x3;
         tempVertices[16] = y3;
         tempVertices[17] = this.color;
         tempVertices[18] = u2;
         tempVertices[19] = v2;
         tempVertices[20] = x4;
         tempVertices[21] = y4;
         tempVertices[22] = this.color;
         tempVertices[23] = u2;
         tempVertices[24] = v;
         tempVertices[25] = x1;
         tempVertices[26] = y1;
         tempVertices[27] = this.color;
         tempVertices[28] = u;
         tempVertices[29] = v;
         this.add(region.texture, tempVertices, 0, 30);
      }

   }

   public void add(Sprite sprite) {
      if (this.mesh.getNumIndices() > 0) {
         this.add(sprite.getTexture(), sprite.getVertices(), 0, 20);
      } else {
         float[] spriteVertices = sprite.getVertices();
         System.arraycopy(spriteVertices, 0, tempVertices, 0, 15);
         System.arraycopy(spriteVertices, 10, tempVertices, 15, 5);
         System.arraycopy(spriteVertices, 15, tempVertices, 20, 5);
         System.arraycopy(spriteVertices, 0, tempVertices, 25, 5);
         this.add(sprite.getTexture(), tempVertices, 0, 30);
      }
   }

   public void begin() {
      if (this.drawing) {
         throw new IllegalStateException("end must be called before begin.");
      } else {
         if (!Gdx.graphics.isGL20Available()) {
            GL10 gl = Gdx.gl10;
            gl.glDepthMask(false);
            gl.glEnable(3553);
            gl.glMatrixMode(5889);
            gl.glLoadMatrixf(this.projectionMatrix.val, 0);
            gl.glMatrixMode(5888);
            gl.glLoadMatrixf(this.transformMatrix.val, 0);
            this.mesh.bind();
         } else {
            this.combinedMatrix.set(this.projectionMatrix).mul(this.transformMatrix);
            GL20 gl = Gdx.gl20;
            gl.glDepthMask(false);
            if (this.customShader != null) {
               this.customShader.begin();
               this.customShader.setUniformMatrix("u_proj", this.projectionMatrix);
               this.customShader.setUniformMatrix("u_trans", this.transformMatrix);
               this.customShader.setUniformMatrix("u_projTrans", this.combinedMatrix);
               this.customShader.setUniformi("u_texture", 0);
               this.mesh.bind(this.customShader);
            } else {
               this.shader.begin();
               this.shader.setUniformMatrix("u_projectionViewMatrix", this.combinedMatrix);
               this.shader.setUniformi("u_texture", 0);
               this.mesh.bind(this.shader);
            }
         }

         this.drawing = true;
      }
   }

   public void end() {
      if (!this.drawing) {
         throw new IllegalStateException("begin must be called before end.");
      } else {
         this.drawing = false;
         if (!Gdx.graphics.isGL20Available()) {
            GL10 gl = Gdx.gl10;
            gl.glDepthMask(true);
            gl.glDisable(3553);
            this.mesh.unbind();
         } else {
            this.shader.end();
            GL20 gl = Gdx.gl20;
            gl.glDepthMask(true);
            if (this.customShader != null) {
               this.mesh.unbind(this.customShader);
            } else {
               this.mesh.unbind(this.shader);
            }
         }

      }
   }

   public void draw(int cacheID) {
      if (!this.drawing) {
         throw new IllegalStateException("SpriteCache.begin must be called before draw.");
      } else {
         SpriteCache.Cache cache = (SpriteCache.Cache)this.caches.get(cacheID);
         int verticesPerImage = this.mesh.getNumIndices() > 0 ? 4 : 6;
         int offset = cache.offset / (verticesPerImage * 5) * 6;
         Texture[] textures = cache.textures;
         int[] counts = cache.counts;
         int i;
         int n;
         int count;
         if (Gdx.graphics.isGL20Available()) {
            i = 0;

            for(n = cache.textureCount; i < n; ++i) {
               count = counts[i];
               textures[i].bind();
               if (this.customShader != null) {
                  this.mesh.render(this.customShader, 4, offset, count);
               } else {
                  this.mesh.render(this.shader, 4, offset, count);
               }

               offset += count;
            }
         } else {
            i = 0;

            for(n = cache.textureCount; i < n; ++i) {
               count = counts[i];
               textures[i].bind();
               this.mesh.render(4, offset, count);
               offset += count;
            }
         }

      }
   }

   public void draw(int cacheID, int offset, int length) {
      if (!this.drawing) {
         throw new IllegalStateException("SpriteCache.begin must be called before draw.");
      } else {
         SpriteCache.Cache cache = (SpriteCache.Cache)this.caches.get(cacheID);
         offset = offset * 6 + cache.offset;
         length *= 6;
         Texture[] textures = cache.textures;
         int[] counts = cache.counts;
         int i;
         int n;
         int count;
         if (Gdx.graphics.isGL20Available()) {
            i = 0;

            for(n = cache.textureCount; i < n; ++i) {
               textures[i].bind();
               count = counts[i];
               if (count > length) {
                  i = n;
                  count = length;
               } else {
                  length -= count;
               }

               if (this.customShader != null) {
                  this.mesh.render(this.customShader, 4, offset, count);
               } else {
                  this.mesh.render(this.shader, 4, offset, count);
               }

               offset += count;
            }
         } else {
            i = 0;

            for(n = cache.textureCount; i < n; ++i) {
               textures[i].bind();
               count = counts[i];
               if (count > length) {
                  i = n;
                  count = length;
               } else {
                  length -= count;
               }

               this.mesh.render(4, offset, count);
               offset += count;
            }
         }

      }
   }

   public void dispose() {
      this.mesh.dispose();
      if (this.shader != null) {
         this.shader.dispose();
      }

   }

   public Matrix4 getProjectionMatrix() {
      return this.projectionMatrix;
   }

   public void setProjectionMatrix(Matrix4 projection) {
      if (this.drawing) {
         throw new IllegalStateException("Can't set the matrix within begin/end.");
      } else {
         this.projectionMatrix.set(projection);
      }
   }

   public Matrix4 getTransformMatrix() {
      return this.transformMatrix;
   }

   public void setTransformMatrix(Matrix4 transform) {
      if (this.drawing) {
         throw new IllegalStateException("Can't set the matrix within begin/end.");
      } else {
         this.transformMatrix.set(transform);
      }
   }

   static ShaderProgram createDefaultShader() {
      if (!Gdx.graphics.isGL20Available()) {
         return null;
      } else {
         String vertexShader = "attribute vec4 a_position;\nattribute vec4 a_color;\nattribute vec2 a_texCoord0;\nuniform mat4 u_projectionViewMatrix;\nvarying vec4 v_color;\nvarying vec2 v_texCoords;\n\nvoid main()\n{\n   v_color = a_color;\n   v_texCoords = a_texCoord0;\n   gl_Position =  u_projectionViewMatrix * a_position;\n}\n";
         String fragmentShader = "#ifdef GL_ES\nprecision mediump float;\n#endif\nvarying vec4 v_color;\nvarying vec2 v_texCoords;\nuniform sampler2D u_texture;\nvoid main()\n{\n  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n}";
         ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
         if (!shader.isCompiled()) {
            throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());
         } else {
            return shader;
         }
      }
   }

   public void setShader(ShaderProgram shader) {
      this.customShader = shader;
   }

   private static class Cache {
      final int id;
      final int offset;
      int maxCount;
      int textureCount;
      Texture[] textures;
      int[] counts;

      public Cache(int id, int offset) {
         this.id = id;
         this.offset = offset;
      }
   }
}
