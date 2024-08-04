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
import com.badlogic.gdx.utils.NumberUtils;

public class PolygonSpriteBatch {
   private Mesh mesh;
   private Mesh[] buffers;
   private Texture lastTexture;
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
   public int maxVerticesInBatch;
   private ShaderProgram customShader;

   public PolygonSpriteBatch() {
      this(4000);
   }

   public PolygonSpriteBatch(int size) {
      this(size, (ShaderProgram)null);
   }

   public PolygonSpriteBatch(int size, ShaderProgram defaultShader) {
      this(size, 1, defaultShader);
   }

   public PolygonSpriteBatch(int size, int buffers) {
      this(size, buffers, (ShaderProgram)null);
   }

   public PolygonSpriteBatch(int size, int buffers, ShaderProgram defaultShader) {
      this.lastTexture = null;
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
      this.maxVerticesInBatch = 0;
      this.customShader = null;
      this.buffers = new Mesh[buffers];

      for(int i = 0; i < buffers; ++i) {
         this.buffers[i] = new Mesh(Mesh.VertexDataType.VertexArray, false, size, 0, new VertexAttribute[]{new VertexAttribute(1, 2, "a_position"), new VertexAttribute(4, 4, "a_color"), new VertexAttribute(16, 2, "a_texCoord0")});
      }

      this.projectionMatrix.setToOrtho2D(0.0F, 0.0F, (float)Gdx.graphics.getWidth(), (float)Gdx.graphics.getHeight());
      this.vertices = new float[size * 5];
      this.mesh = this.buffers[0];
      if (Gdx.graphics.isGL20Available() && defaultShader == null) {
         this.shader = createDefaultShader();
         this.ownsShader = true;
      } else {
         this.shader = defaultShader;
      }

   }

   public static ShaderProgram createDefaultShader() {
      String vertexShader = "attribute vec4 a_position;\nattribute vec4 a_color;\nattribute vec2 a_texCoord0;\nuniform mat4 u_projectionViewMatrix;\nvarying vec4 v_color;\nvarying vec2 v_texCoords;\n\nvoid main()\n{\n   v_color = a_color;\n   v_texCoords = a_texCoord0;\n   gl_Position =  u_projectionViewMatrix * a_position;\n}\n";
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
         throw new IllegalStateException("you have to call PolygonSpriteBatch.end() first");
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
         throw new IllegalStateException("PolygonSpriteBatch.begin must be called before end.");
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

   public void draw(PolygonRegion region, float x, float y) {
      this.draw(region, x, y, (float)region.getRegion().getRegionWidth(), (float)region.getRegion().getRegionHeight());
   }

   public void draw(PolygonRegion region, float x, float y, float width, float height) {
      if (!this.drawing) {
         throw new IllegalStateException("PolygonSpriteBatch.begin must be called before draw.");
      } else {
         Texture texture = region.getRegion().texture;
         if (texture != this.lastTexture) {
            this.switchTexture(texture);
         }

         float[] localVertices = region.getLocalVertices();
         float[] texCoords = region.getTextureCoords();
         if (this.idx + localVertices.length > this.vertices.length) {
            this.renderMesh();
         }

         float sX = width / (float)region.getRegion().getRegionWidth();
         float sY = height / (float)region.getRegion().getRegionHeight();

         for(int i = 0; i < localVertices.length; i += 2) {
            this.vertices[this.idx++] = localVertices[i] * sX + x;
            this.vertices[this.idx++] = localVertices[i + 1] * sY + y;
            this.vertices[this.idx++] = this.color;
            this.vertices[this.idx++] = texCoords[i];
            this.vertices[this.idx++] = texCoords[i + 1];
         }

      }
   }

   public void draw(PolygonRegion region, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation) {
      if (!this.drawing) {
         throw new IllegalStateException("PolygonSpriteBatch.begin must be called before draw.");
      } else {
         Texture texture = region.getRegion().texture;
         if (texture != this.lastTexture) {
            this.switchTexture(texture);
         }

         float[] localVertices = region.getLocalVertices();
         float[] texCoords = region.getTextureCoords();
         if (this.idx + localVertices.length > this.vertices.length) {
            this.renderMesh();
         }

         float worldOriginX = x + originX;
         float worldOriginY = y + originY;
         float sX = width / (float)region.getRegion().getRegionWidth();
         float sY = height / (float)region.getRegion().getRegionHeight();
         float cos = MathUtils.cosDeg(rotation);
         float sin = MathUtils.sinDeg(rotation);

         for(int i = 0; i < localVertices.length; i += 2) {
            float fx = localVertices[i] * sX;
            float fy = localVertices[i + 1] * sY;
            fx -= originX;
            fy -= originY;
            if (scaleX != 1.0F || scaleY != 1.0F) {
               fx *= scaleX;
               fy *= scaleY;
            }

            float rx = cos * fx - sin * fy;
            float ry = sin * fx + cos * fy;
            rx += worldOriginX;
            ry += worldOriginY;
            this.vertices[this.idx++] = rx;
            this.vertices[this.idx++] = ry;
            this.vertices[this.idx++] = this.color;
            this.vertices[this.idx++] = texCoords[i];
            this.vertices[this.idx++] = texCoords[i + 1];
         }

      }
   }

   public void draw(PolygonRegion region, float[] spriteVertices, int offset, int length) {
      if (!this.drawing) {
         throw new IllegalStateException("PolygonSpriteBatch.begin must be called before draw.");
      } else {
         Texture texture = region.getRegion().texture;
         if (texture != this.lastTexture) {
            this.switchTexture(texture);
         }

         if (this.idx + length > this.vertices.length) {
            this.renderMesh();
         }

         if (length <= this.vertices.length) {
            System.arraycopy(spriteVertices, offset, this.vertices, this.idx, length);
            this.idx += length;
         }

      }
   }

   public void flush() {
      this.renderMesh();
   }

   private void renderMesh() {
      if (this.idx != 0) {
         ++this.renderCalls;
         ++this.totalRenderCalls;
         int verticesInBatch = this.idx / 5;
         if (verticesInBatch > this.maxVerticesInBatch) {
            this.maxVerticesInBatch = verticesInBatch;
         }

         this.lastTexture.bind();
         this.mesh.setVertices(this.vertices, 0, this.idx);
         if (this.blendingDisabled) {
            Gdx.gl.glDisable(3042);
         } else {
            Gdx.gl.glEnable(3042);
            Gdx.gl.glBlendFunc(this.blendSrcFunc, this.blendDstFunc);
         }

         if (Gdx.graphics.isGL20Available()) {
            if (this.customShader != null) {
               this.mesh.render(this.customShader, 4, 0, verticesInBatch);
            } else {
               this.mesh.render(this.shader, 4, 0, verticesInBatch);
            }
         } else {
            this.mesh.render(4, 0, verticesInBatch);
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
      this.renderMesh();
      this.blendingDisabled = true;
   }

   public void enableBlending() {
      this.renderMesh();
      this.blendingDisabled = false;
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
            this.customShader.setUniformMatrix("u_proj", this.projectionMatrix);
            this.customShader.setUniformMatrix("u_trans", this.transformMatrix);
            this.customShader.setUniformMatrix("u_projTrans", this.combinedMatrix);
            this.customShader.setUniformi("u_texture", 0);
         } else {
            this.shader.setUniformMatrix("u_projectionViewMatrix", this.combinedMatrix);
            this.shader.setUniformi("u_texture", 0);
         }
      }

   }

   private void switchTexture(Texture texture) {
      this.renderMesh();
      this.lastTexture = texture;
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
