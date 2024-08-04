package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GLCommon;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.NumberUtils;

public class SpriteBatch implements Disposable {
   private Mesh mesh;
   private Mesh[] buffers;
   private Texture lastTexture;
   private float invTexWidth;
   private float invTexHeight;
   private int idx;
   private int currBufferIdx;
   private final float[] vertices;
   private final Matrix4 transformMatrix;
   private final Matrix4 projectionMatrix;
   private final Matrix4 combinedMatrix;
   private boolean drawing;
   private boolean blendingDisabled;
   private int blendSrcFunc;
   private int blendDstFunc;
   private final ShaderProgram shader;
   private boolean ownsShader;
   float color;
   private Color tempColor;
   public int renderCalls;
   public int totalRenderCalls;
   public int maxSpritesInBatch;
   private ShaderProgram customShader;
   public static final int X1 = 0;
   public static final int Y1 = 1;
   public static final int C1 = 2;
   public static final int U1 = 3;
   public static final int V1 = 4;
   public static final int X2 = 5;
   public static final int Y2 = 6;
   public static final int C2 = 7;
   public static final int U2 = 8;
   public static final int V2 = 9;
   public static final int X3 = 10;
   public static final int Y3 = 11;
   public static final int C3 = 12;
   public static final int U3 = 13;
   public static final int V3 = 14;
   public static final int X4 = 15;
   public static final int Y4 = 16;
   public static final int C4 = 17;
   public static final int U4 = 18;
   public static final int V4 = 19;

   public SpriteBatch() {
      this(1000);
   }

   public SpriteBatch(int size) {
      this(size, (ShaderProgram)null);
   }

   public SpriteBatch(int size, ShaderProgram defaultShader) {
      this(size, 1, defaultShader);
   }

   public SpriteBatch(int size, int buffers) {
      this(size, buffers, (ShaderProgram)null);
   }

   public SpriteBatch(int size, int buffers, ShaderProgram defaultShader) {
      this.lastTexture = null;
      this.invTexWidth = 0.0F;
      this.invTexHeight = 0.0F;
      this.idx = 0;
      this.currBufferIdx = 0;
      this.transformMatrix = new Matrix4();
      this.projectionMatrix = new Matrix4();
      this.combinedMatrix = new Matrix4();
      this.drawing = false;
      this.blendingDisabled = false;
      this.blendSrcFunc = 770;
      this.blendDstFunc = 771;
      this.color = Color.WHITE.toFloatBits();
      this.tempColor = new Color(1.0F, 1.0F, 1.0F, 1.0F);
      this.renderCalls = 0;
      this.totalRenderCalls = 0;
      this.maxSpritesInBatch = 0;
      this.customShader = null;
      if (size > 5460) {
         throw new GdxRuntimeException("Can't have more than 5460 sprites per batch");
      } else {
         this.buffers = new Mesh[buffers];

         int len;
         for(len = 0; len < buffers; ++len) {
            this.buffers[len] = new Mesh(Mesh.VertexDataType.VertexArray, false, size * 4, size * 6, new VertexAttribute[]{new VertexAttribute(1, 2, "a_position"), new VertexAttribute(4, 4, "a_color"), new VertexAttribute(16, 2, "a_texCoord0")});
         }

         this.projectionMatrix.setToOrtho2D(0.0F, 0.0F, (float)Gdx.graphics.getWidth(), (float)Gdx.graphics.getHeight());
         this.vertices = new float[size * 20];
         len = size * 6;
         short[] indices = new short[len];
         short j = 0;

         int i;
         for(i = 0; i < len; j = (short)(j + 4)) {
            indices[i + 0] = (short)(j + 0);
            indices[i + 1] = (short)(j + 1);
            indices[i + 2] = (short)(j + 2);
            indices[i + 3] = (short)(j + 2);
            indices[i + 4] = (short)(j + 3);
            indices[i + 5] = (short)(j + 0);
            i += 6;
         }

         for(i = 0; i < buffers; ++i) {
            this.buffers[i].setIndices(indices);
         }

         this.mesh = this.buffers[0];
         if (Gdx.graphics.isGL20Available() && defaultShader == null) {
            this.shader = createDefaultShader();
            this.ownsShader = true;
         } else {
            this.shader = defaultShader;
         }

      }
   }

   public static ShaderProgram createDefaultShader() {
      String vertexShader = "attribute vec4 a_position;\nattribute vec4 a_color;\nattribute vec2 a_texCoord0;\nuniform mat4 u_projTrans;\nvarying vec4 v_color;\nvarying vec2 v_texCoords;\n\nvoid main()\n{\n   v_color = a_color;\n   v_texCoords = a_texCoord0;\n   gl_Position =  u_projTrans * a_position;\n}\n";
      String fragmentShader = "#ifdef GL_ES\n#define LOWP lowp\nprecision mediump float;\n#else\n#define LOWP \n#endif\nvarying LOWP vec4 v_color;\nvarying vec2 v_texCoords;\nuniform sampler2D u_texture;\nvoid main()\n{\n  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n}";
      ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
      if (!shader.isCompiled()) {
         throw new IllegalArgumentException("couldn't compile shader: " + shader.getLog());
      } else {
         return shader;
      }
   }

   public void begin() {
      if (this.drawing) {
         throw new IllegalStateException("you have to call SpriteBatch.end() first");
      } else {
         this.renderCalls = 0;
         Gdx.gl.glDepthMask(false);
         if (Gdx.graphics.isGL20Available()) {
            if (this.customShader != null) {
               this.customShader.begin();
            } else {
               this.shader.begin();
            }
         } else {
            Gdx.gl.glEnable(3553);
         }

         this.setupMatrices();
         this.idx = 0;
         this.lastTexture = null;
         this.drawing = true;
      }
   }

   public void end() {
      if (!this.drawing) {
         throw new IllegalStateException("SpriteBatch.begin must be called before end.");
      } else {
         if (this.idx > 0) {
            this.renderMesh();
         }

         this.lastTexture = null;
         this.idx = 0;
         this.drawing = false;
         GLCommon gl = Gdx.gl;
         gl.glDepthMask(true);
         if (this.isBlendingEnabled()) {
            gl.glDisable(3042);
         }

         if (Gdx.graphics.isGL20Available()) {
            if (this.customShader != null) {
               this.customShader.end();
            } else {
               this.shader.end();
            }
         } else {
            gl.glDisable(3553);
         }

      }
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

   public void bindTexture(Texture texture) {
      this.lastTexture = texture;
   }

   public void draw(Texture texture, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation, int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY) {
      if (!this.drawing) {
         throw new IllegalStateException("SpriteBatch.begin must be called before draw.");
      } else {
         if (texture != this.lastTexture) {
            this.switchTexture(texture);
         } else if (this.idx == this.vertices.length) {
            this.renderMesh();
         }

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
         u = (float)srcX * this.invTexWidth;
         v = (float)(srcY + srcHeight) * this.invTexHeight;
         float u2 = (float)(srcX + srcWidth) * this.invTexWidth;
         float v2 = (float)srcY * this.invTexHeight;
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

         this.vertices[this.idx++] = x1;
         this.vertices[this.idx++] = y1;
         this.vertices[this.idx++] = this.color;
         this.vertices[this.idx++] = u;
         this.vertices[this.idx++] = v;
         this.vertices[this.idx++] = x2;
         this.vertices[this.idx++] = y2;
         this.vertices[this.idx++] = this.color;
         this.vertices[this.idx++] = u;
         this.vertices[this.idx++] = v2;
         this.vertices[this.idx++] = x3;
         this.vertices[this.idx++] = y3;
         this.vertices[this.idx++] = this.color;
         this.vertices[this.idx++] = u2;
         this.vertices[this.idx++] = v2;
         this.vertices[this.idx++] = x4;
         this.vertices[this.idx++] = y4;
         this.vertices[this.idx++] = this.color;
         this.vertices[this.idx++] = u2;
         this.vertices[this.idx++] = v;
      }
   }

   public void draw(Texture texture, float x, float y, float width, float height, int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY) {
      if (!this.drawing) {
         throw new IllegalStateException("SpriteBatch.begin must be called before draw.");
      } else {
         if (texture != this.lastTexture) {
            this.switchTexture(texture);
         } else if (this.idx == this.vertices.length) {
            this.renderMesh();
         }

         float u = (float)srcX * this.invTexWidth;
         float v = (float)(srcY + srcHeight) * this.invTexHeight;
         float u2 = (float)(srcX + srcWidth) * this.invTexWidth;
         float v2 = (float)srcY * this.invTexHeight;
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

         this.vertices[this.idx++] = x;
         this.vertices[this.idx++] = y;
         this.vertices[this.idx++] = this.color;
         this.vertices[this.idx++] = u;
         this.vertices[this.idx++] = v;
         this.vertices[this.idx++] = x;
         this.vertices[this.idx++] = fy2;
         this.vertices[this.idx++] = this.color;
         this.vertices[this.idx++] = u;
         this.vertices[this.idx++] = v2;
         this.vertices[this.idx++] = fx2;
         this.vertices[this.idx++] = fy2;
         this.vertices[this.idx++] = this.color;
         this.vertices[this.idx++] = u2;
         this.vertices[this.idx++] = v2;
         this.vertices[this.idx++] = fx2;
         this.vertices[this.idx++] = y;
         this.vertices[this.idx++] = this.color;
         this.vertices[this.idx++] = u2;
         this.vertices[this.idx++] = v;
      }
   }

   public void draw(Texture texture, float x, float y, int srcX, int srcY, int srcWidth, int srcHeight) {
      if (!this.drawing) {
         throw new IllegalStateException("SpriteBatch.begin must be called before draw.");
      } else {
         if (texture != this.lastTexture) {
            this.switchTexture(texture);
         } else if (this.idx == this.vertices.length) {
            this.renderMesh();
         }

         float u = (float)srcX * this.invTexWidth;
         float v = (float)(srcY + srcHeight) * this.invTexHeight;
         float u2 = (float)(srcX + srcWidth) * this.invTexWidth;
         float v2 = (float)srcY * this.invTexHeight;
         float fx2 = x + (float)srcWidth;
         float fy2 = y + (float)srcHeight;
         this.vertices[this.idx++] = x;
         this.vertices[this.idx++] = y;
         this.vertices[this.idx++] = this.color;
         this.vertices[this.idx++] = u;
         this.vertices[this.idx++] = v;
         this.vertices[this.idx++] = x;
         this.vertices[this.idx++] = fy2;
         this.vertices[this.idx++] = this.color;
         this.vertices[this.idx++] = u;
         this.vertices[this.idx++] = v2;
         this.vertices[this.idx++] = fx2;
         this.vertices[this.idx++] = fy2;
         this.vertices[this.idx++] = this.color;
         this.vertices[this.idx++] = u2;
         this.vertices[this.idx++] = v2;
         this.vertices[this.idx++] = fx2;
         this.vertices[this.idx++] = y;
         this.vertices[this.idx++] = this.color;
         this.vertices[this.idx++] = u2;
         this.vertices[this.idx++] = v;
      }
   }

   public void draw(Texture texture, float x, float y, float width, float height, float u, float v, float u2, float v2) {
      if (!this.drawing) {
         throw new IllegalStateException("SpriteBatch.begin must be called before draw.");
      } else {
         if (texture != this.lastTexture) {
            this.switchTexture(texture);
         } else if (this.idx == this.vertices.length) {
            this.renderMesh();
         }

         float fx2 = x + width;
         float fy2 = y + height;
         this.vertices[this.idx++] = x;
         this.vertices[this.idx++] = y;
         this.vertices[this.idx++] = this.color;
         this.vertices[this.idx++] = u;
         this.vertices[this.idx++] = v;
         this.vertices[this.idx++] = x;
         this.vertices[this.idx++] = fy2;
         this.vertices[this.idx++] = this.color;
         this.vertices[this.idx++] = u;
         this.vertices[this.idx++] = v2;
         this.vertices[this.idx++] = fx2;
         this.vertices[this.idx++] = fy2;
         this.vertices[this.idx++] = this.color;
         this.vertices[this.idx++] = u2;
         this.vertices[this.idx++] = v2;
         this.vertices[this.idx++] = fx2;
         this.vertices[this.idx++] = y;
         this.vertices[this.idx++] = this.color;
         this.vertices[this.idx++] = u2;
         this.vertices[this.idx++] = v;
      }
   }

   public void draw(Texture texture, float x, float y) {
      if (!this.drawing) {
         throw new IllegalStateException("SpriteBatch.begin must be called before draw.");
      } else {
         if (texture != this.lastTexture) {
            this.switchTexture(texture);
         } else if (this.idx == this.vertices.length) {
            this.renderMesh();
         }

         float fx2 = x + (float)texture.getWidth();
         float fy2 = y + (float)texture.getHeight();
         this.vertices[this.idx++] = x;
         this.vertices[this.idx++] = y;
         this.vertices[this.idx++] = this.color;
         this.vertices[this.idx++] = 0.0F;
         this.vertices[this.idx++] = 1.0F;
         this.vertices[this.idx++] = x;
         this.vertices[this.idx++] = fy2;
         this.vertices[this.idx++] = this.color;
         this.vertices[this.idx++] = 0.0F;
         this.vertices[this.idx++] = 0.0F;
         this.vertices[this.idx++] = fx2;
         this.vertices[this.idx++] = fy2;
         this.vertices[this.idx++] = this.color;
         this.vertices[this.idx++] = 1.0F;
         this.vertices[this.idx++] = 0.0F;
         this.vertices[this.idx++] = fx2;
         this.vertices[this.idx++] = y;
         this.vertices[this.idx++] = this.color;
         this.vertices[this.idx++] = 1.0F;
         this.vertices[this.idx++] = 1.0F;
      }
   }

   public void draw(Texture texture, float x, float y, float width, float height) {
      if (!this.drawing) {
         throw new IllegalStateException("SpriteBatch.begin must be called before draw.");
      } else {
         if (texture != this.lastTexture) {
            this.switchTexture(texture);
         } else if (this.idx == this.vertices.length) {
            this.renderMesh();
         }

         float fx2 = x + width;
         float fy2 = y + height;
         float u = 0.0F;
         float v = 1.0F;
         float u2 = 1.0F;
         float v2 = 0.0F;
         this.vertices[this.idx++] = x;
         this.vertices[this.idx++] = y;
         this.vertices[this.idx++] = this.color;
         this.vertices[this.idx++] = 0.0F;
         this.vertices[this.idx++] = 1.0F;
         this.vertices[this.idx++] = x;
         this.vertices[this.idx++] = fy2;
         this.vertices[this.idx++] = this.color;
         this.vertices[this.idx++] = 0.0F;
         this.vertices[this.idx++] = 0.0F;
         this.vertices[this.idx++] = fx2;
         this.vertices[this.idx++] = fy2;
         this.vertices[this.idx++] = this.color;
         this.vertices[this.idx++] = 1.0F;
         this.vertices[this.idx++] = 0.0F;
         this.vertices[this.idx++] = fx2;
         this.vertices[this.idx++] = y;
         this.vertices[this.idx++] = this.color;
         this.vertices[this.idx++] = 1.0F;
         this.vertices[this.idx++] = 1.0F;
      }
   }

   public void draw(Texture texture, float[] spriteVertices, int offset, int count) {
      if (!this.drawing) {
         throw new IllegalStateException("SpriteBatch.begin must be called before draw.");
      } else {
         if (texture != this.lastTexture) {
            this.switchTexture(texture);
         }

         int remainingVertices = this.vertices.length - this.idx;
         if (remainingVertices == 0) {
            this.renderMesh();
            remainingVertices = this.vertices.length;
         }

         int copyCount = Math.min(remainingVertices, count);
         System.arraycopy(spriteVertices, offset, this.vertices, this.idx, copyCount);
         this.idx += copyCount;

         for(count -= copyCount; count > 0; count -= copyCount) {
            offset += copyCount;
            this.renderMesh();
            copyCount = Math.min(this.vertices.length, count);
            System.arraycopy(spriteVertices, offset, this.vertices, 0, copyCount);
            this.idx += copyCount;
         }

      }
   }

   public void draw(TextureRegion region, float x, float y) {
      this.draw(region, x, y, (float)region.getRegionWidth(), (float)region.getRegionHeight());
   }

   public void draw(TextureRegion region, float x, float y, float width, float height) {
      if (!this.drawing) {
         throw new IllegalStateException("SpriteBatch.begin must be called before draw.");
      } else {
         Texture texture = region.texture;
         if (texture != this.lastTexture) {
            this.switchTexture(texture);
         } else if (this.idx == this.vertices.length) {
            this.renderMesh();
         }

         float fx2 = x + width;
         float fy2 = y + height;
         float u = region.u;
         float v = region.v2;
         float u2 = region.u2;
         float v2 = region.v;
         this.vertices[this.idx++] = x;
         this.vertices[this.idx++] = y;
         this.vertices[this.idx++] = this.color;
         this.vertices[this.idx++] = u;
         this.vertices[this.idx++] = v;
         this.vertices[this.idx++] = x;
         this.vertices[this.idx++] = fy2;
         this.vertices[this.idx++] = this.color;
         this.vertices[this.idx++] = u;
         this.vertices[this.idx++] = v2;
         this.vertices[this.idx++] = fx2;
         this.vertices[this.idx++] = fy2;
         this.vertices[this.idx++] = this.color;
         this.vertices[this.idx++] = u2;
         this.vertices[this.idx++] = v2;
         this.vertices[this.idx++] = fx2;
         this.vertices[this.idx++] = y;
         this.vertices[this.idx++] = this.color;
         this.vertices[this.idx++] = u2;
         this.vertices[this.idx++] = v;
      }
   }

   public void draw(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation) {
      if (!this.drawing) {
         throw new IllegalStateException("SpriteBatch.begin must be called before draw.");
      } else {
         Texture texture = region.texture;
         if (texture != this.lastTexture) {
            this.switchTexture(texture);
         } else if (this.idx == this.vertices.length) {
            this.renderMesh();
         }

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
         this.vertices[this.idx++] = x1;
         this.vertices[this.idx++] = y1;
         this.vertices[this.idx++] = this.color;
         this.vertices[this.idx++] = u;
         this.vertices[this.idx++] = v;
         this.vertices[this.idx++] = x2;
         this.vertices[this.idx++] = y2;
         this.vertices[this.idx++] = this.color;
         this.vertices[this.idx++] = u;
         this.vertices[this.idx++] = v2;
         this.vertices[this.idx++] = x3;
         this.vertices[this.idx++] = y3;
         this.vertices[this.idx++] = this.color;
         this.vertices[this.idx++] = u2;
         this.vertices[this.idx++] = v2;
         this.vertices[this.idx++] = x4;
         this.vertices[this.idx++] = y4;
         this.vertices[this.idx++] = this.color;
         this.vertices[this.idx++] = u2;
         this.vertices[this.idx++] = v;
      }
   }

   public void draw(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation, boolean clockwise) {
      if (!this.drawing) {
         throw new IllegalStateException("SpriteBatch.begin must be called before draw.");
      } else {
         Texture texture = region.texture;
         if (texture != this.lastTexture) {
            this.switchTexture(texture);
         } else if (this.idx == this.vertices.length) {
            this.renderMesh();
         }

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
         float u1;
         float v1;
         if (rotation != 0.0F) {
            u1 = MathUtils.cosDeg(rotation);
            v1 = MathUtils.sinDeg(rotation);
            x1 = u1 * fx - v1 * fy;
            y1 = v1 * fx + u1 * fy;
            x2 = u1 * fx - v1 * fy2;
            y2 = v1 * fx + u1 * fy2;
            x3 = u1 * fx2 - v1 * fy2;
            y3 = v1 * fx2 + u1 * fy2;
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
         float u2;
         float v2;
         float u3;
         float v3;
         float u4;
         float v4;
         if (clockwise) {
            u1 = region.u2;
            v1 = region.v2;
            u2 = region.u;
            v2 = region.v2;
            u3 = region.u;
            v3 = region.v;
            u4 = region.u2;
            v4 = region.v;
         } else {
            u1 = region.u;
            v1 = region.v;
            u2 = region.u2;
            v2 = region.v;
            u3 = region.u2;
            v3 = region.v2;
            u4 = region.u;
            v4 = region.v2;
         }

         this.vertices[this.idx++] = x1;
         this.vertices[this.idx++] = y1;
         this.vertices[this.idx++] = this.color;
         this.vertices[this.idx++] = u1;
         this.vertices[this.idx++] = v1;
         this.vertices[this.idx++] = x2;
         this.vertices[this.idx++] = y2;
         this.vertices[this.idx++] = this.color;
         this.vertices[this.idx++] = u2;
         this.vertices[this.idx++] = v2;
         this.vertices[this.idx++] = x3;
         this.vertices[this.idx++] = y3;
         this.vertices[this.idx++] = this.color;
         this.vertices[this.idx++] = u3;
         this.vertices[this.idx++] = v3;
         this.vertices[this.idx++] = x4;
         this.vertices[this.idx++] = y4;
         this.vertices[this.idx++] = this.color;
         this.vertices[this.idx++] = u4;
         this.vertices[this.idx++] = v4;
      }
   }

   public void flush() {
      this.renderMesh();
   }

   private void renderMesh() {
      if (this.idx != 0) {
         ++this.renderCalls;
         ++this.totalRenderCalls;
         int spritesInBatch = this.idx / 20;
         if (spritesInBatch > this.maxSpritesInBatch) {
            this.maxSpritesInBatch = spritesInBatch;
         }

         this.lastTexture.bind();
         this.mesh.setVertices(this.vertices, 0, this.idx);
         this.mesh.getIndicesBuffer().position(0);
         this.mesh.getIndicesBuffer().limit(spritesInBatch * 6);
         if (this.blendingDisabled) {
            Gdx.gl.glDisable(3042);
         } else {
            Gdx.gl.glEnable(3042);
            if (this.blendSrcFunc != -1) {
               Gdx.gl.glBlendFunc(this.blendSrcFunc, this.blendDstFunc);
            }
         }

         if (Gdx.graphics.isGL20Available()) {
            if (this.customShader != null) {
               this.mesh.render(this.customShader, 4, 0, spritesInBatch * 6);
            } else {
               this.mesh.render(this.shader, 4, 0, spritesInBatch * 6);
            }
         } else {
            this.mesh.render(4, 0, spritesInBatch * 6);
         }

         this.idx = 0;
         ++this.currBufferIdx;
         if (this.currBufferIdx == this.buffers.length) {
            this.currBufferIdx = 0;
         }

         this.mesh = this.buffers[this.currBufferIdx];
      }
   }

   public void disableBlending() {
      if (!this.blendingDisabled) {
         this.renderMesh();
         this.blendingDisabled = true;
      }
   }

   public void enableBlending() {
      if (this.blendingDisabled) {
         this.renderMesh();
         this.blendingDisabled = false;
      }
   }

   public void setBlendFunction(int srcFunc, int dstFunc) {
      this.renderMesh();
      this.blendSrcFunc = srcFunc;
      this.blendDstFunc = dstFunc;
   }

   public void dispose() {
      for(int i = 0; i < this.buffers.length; ++i) {
         this.buffers[i].dispose();
      }

      if (this.ownsShader && this.shader != null) {
         this.shader.dispose();
      }

   }

   public Matrix4 getProjectionMatrix() {
      return this.projectionMatrix;
   }

   public Matrix4 getTransformMatrix() {
      return this.transformMatrix;
   }

   public void setProjectionMatrix(Matrix4 projection) {
      if (this.drawing) {
         this.flush();
      }

      this.projectionMatrix.set(projection);
      if (this.drawing) {
         this.setupMatrices();
      }

   }

   public void setTransformMatrix(Matrix4 transform) {
      if (this.drawing) {
         this.flush();
      }

      this.transformMatrix.set(transform);
      if (this.drawing) {
         this.setupMatrices();
      }

   }

   private void setupMatrices() {
      if (!Gdx.graphics.isGL20Available()) {
         GL10 gl = Gdx.gl10;
         gl.glMatrixMode(5889);
         gl.glLoadMatrixf(this.projectionMatrix.val, 0);
         gl.glMatrixMode(5888);
         gl.glLoadMatrixf(this.transformMatrix.val, 0);
      } else {
         this.combinedMatrix.set(this.projectionMatrix).mul(this.transformMatrix);
         if (this.customShader != null) {
            this.customShader.setUniformMatrix("u_projTrans", this.combinedMatrix);
            this.customShader.setUniformi("u_texture", 0);
         } else {
            this.shader.setUniformMatrix("u_projTrans", this.combinedMatrix);
            this.shader.setUniformi("u_texture", 0);
         }
      }

   }

   private void switchTexture(Texture texture) {
      this.renderMesh();
      this.lastTexture = texture;
      this.invTexWidth = 1.0F / (float)texture.getWidth();
      this.invTexHeight = 1.0F / (float)texture.getHeight();
   }

   public void setShader(ShaderProgram shader) {
      if (this.drawing) {
         this.flush();
         if (this.customShader != null) {
            this.customShader.end();
         } else {
            this.shader.end();
         }
      }

      this.customShader = shader;
      if (this.drawing) {
         if (this.customShader != null) {
            this.customShader.begin();
         } else {
            this.shader.begin();
         }

         this.setupMatrices();
      }

   }

   public boolean isBlendingEnabled() {
      return !this.blendingDisabled;
   }
}
