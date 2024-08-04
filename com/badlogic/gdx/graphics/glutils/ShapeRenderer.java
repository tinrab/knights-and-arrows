package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class ShapeRenderer {
   ImmediateModeRenderer renderer;
   boolean matrixDirty;
   Matrix4 projView;
   Matrix4 transform;
   Matrix4 combined;
   Matrix4 tmp;
   Color color;
   ShapeRenderer.ShapeType currType;

   public ShapeRenderer() {
      this(5000);
   }

   public ShapeRenderer(int maxVertices) {
      this.matrixDirty = false;
      this.projView = new Matrix4();
      this.transform = new Matrix4();
      this.combined = new Matrix4();
      this.tmp = new Matrix4();
      this.color = new Color(1.0F, 1.0F, 1.0F, 1.0F);
      this.currType = null;
      if (Gdx.graphics.isGL20Available()) {
         this.renderer = new ImmediateModeRenderer20(maxVertices, false, true, 0);
      } else {
         this.renderer = new ImmediateModeRenderer10(maxVertices);
      }

      this.projView.setToOrtho2D(0.0F, 0.0F, (float)Gdx.graphics.getWidth(), (float)Gdx.graphics.getHeight());
      this.matrixDirty = true;
   }

   public void setColor(Color color) {
      this.color.set(color);
   }

   public void setColor(float r, float g, float b, float a) {
      this.color.set(r, g, b, a);
   }

   public void setProjectionMatrix(Matrix4 matrix) {
      this.projView.set(matrix);
      this.matrixDirty = true;
   }

   public Matrix4 getProjectionMatrix() {
      return this.projView;
   }

   public void setTransformMatrix(Matrix4 matrix) {
      this.transform.set(matrix);
      this.matrixDirty = true;
   }

   public Matrix4 getTransformMatrix() {
      return this.transform;
   }

   public void identity() {
      this.transform.idt();
      this.matrixDirty = true;
   }

   public void translate(float x, float y, float z) {
      this.transform.translate(x, y, z);
      this.matrixDirty = true;
   }

   public void rotate(float axisX, float axisY, float axisZ, float angle) {
      this.transform.rotate(axisX, axisY, axisZ, angle);
      this.matrixDirty = true;
   }

   public void scale(float scaleX, float scaleY, float scaleZ) {
      this.transform.scale(scaleX, scaleY, scaleZ);
      this.matrixDirty = true;
   }

   public void begin(ShapeRenderer.ShapeType type) {
      if (this.currType != null) {
         throw new GdxRuntimeException("Call end() before beginning a new shape batch");
      } else {
         this.currType = type;
         if (this.matrixDirty) {
            this.combined.set(this.projView);
            Matrix4.mul(this.combined.val, this.transform.val);
            this.matrixDirty = false;
         }

         this.renderer.begin(this.combined, this.currType.getGlType());
      }
   }

   public void point(float x, float y, float z) {
      if (this.currType != ShapeRenderer.ShapeType.Point) {
         throw new GdxRuntimeException("Must call begin(ShapeType.Point)");
      } else {
         this.checkDirty();
         this.checkFlush(1);
         this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
         this.renderer.vertex(x, y, z);
      }
   }

   public void line(float x, float y, float z, float x2, float y2, float z2) {
      if (this.currType != ShapeRenderer.ShapeType.Line) {
         throw new GdxRuntimeException("Must call begin(ShapeType.Line)");
      } else {
         this.checkDirty();
         this.checkFlush(2);
         this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
         this.renderer.vertex(x, y, z);
         this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
         this.renderer.vertex(x2, y2, z2);
      }
   }

   public void line(float x, float y, float x2, float y2) {
      if (this.currType != ShapeRenderer.ShapeType.Line) {
         throw new GdxRuntimeException("Must call begin(ShapeType.Line)");
      } else {
         this.checkDirty();
         this.checkFlush(2);
         this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
         this.renderer.vertex(x, y, 0.0F);
         this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
         this.renderer.vertex(x2, y2, 0.0F);
      }
   }

   public void line(float x, float y, float x2, float y2, Color c1, Color c2) {
      if (this.currType != ShapeRenderer.ShapeType.Line) {
         throw new GdxRuntimeException("Must call begin(ShapeType.Line)");
      } else {
         this.checkDirty();
         this.checkFlush(2);
         this.renderer.color(c1.r, c1.g, c1.b, c1.a);
         this.renderer.vertex(x, y, 0.0F);
         this.renderer.color(c2.r, c2.g, c2.b, c2.a);
         this.renderer.vertex(x2, y2, 0.0F);
      }
   }

   public void curve(float x1, float y1, float cx1, float cy1, float cx2, float cy2, float x2, float y2, int segments) {
      if (this.currType != ShapeRenderer.ShapeType.Line) {
         throw new GdxRuntimeException("Must call begin(ShapeType.Line)");
      } else {
         this.checkDirty();
         this.checkFlush(segments * 2 + 2);
         float subdiv_step = 1.0F / (float)segments;
         float subdiv_step2 = subdiv_step * subdiv_step;
         float subdiv_step3 = subdiv_step * subdiv_step * subdiv_step;
         float pre1 = 3.0F * subdiv_step;
         float pre2 = 3.0F * subdiv_step2;
         float pre4 = 6.0F * subdiv_step2;
         float pre5 = 6.0F * subdiv_step3;
         float tmp1x = x1 - cx1 * 2.0F + cx2;
         float tmp1y = y1 - cy1 * 2.0F + cy2;
         float tmp2x = (cx1 - cx2) * 3.0F - x1 + x2;
         float tmp2y = (cy1 - cy2) * 3.0F - y1 + y2;
         float fx = x1;
         float fy = y1;
         float dfx = (cx1 - x1) * pre1 + tmp1x * pre2 + tmp2x * subdiv_step3;
         float dfy = (cy1 - y1) * pre1 + tmp1y * pre2 + tmp2y * subdiv_step3;
         float ddfx = tmp1x * pre4 + tmp2x * pre5;
         float ddfy = tmp1y * pre4 + tmp2y * pre5;
         float dddfx = tmp2x * pre5;
         float dddfy = tmp2y * pre5;

         while(segments-- > 0) {
            this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
            this.renderer.vertex(fx, fy, 0.0F);
            fx += dfx;
            fy += dfy;
            dfx += ddfx;
            dfy += ddfy;
            ddfx += dddfx;
            ddfy += dddfy;
            this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
            this.renderer.vertex(fx, fy, 0.0F);
         }

         this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
         this.renderer.vertex(fx, fy, 0.0F);
         this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
         this.renderer.vertex(x2, y2, 0.0F);
      }
   }

   public void triangle(float x1, float y1, float x2, float y2, float x3, float y3) {
      if (this.currType != ShapeRenderer.ShapeType.Filled && this.currType != ShapeRenderer.ShapeType.Line) {
         throw new GdxRuntimeException("Must call begin(ShapeType.Filled) or begin(ShapeType.Line)");
      } else {
         this.checkDirty();
         this.checkFlush(6);
         if (this.currType == ShapeRenderer.ShapeType.Line) {
            this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
            this.renderer.vertex(x1, y1, 0.0F);
            this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
            this.renderer.vertex(x2, y2, 0.0F);
            this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
            this.renderer.vertex(x2, y2, 0.0F);
            this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
            this.renderer.vertex(x3, y3, 0.0F);
            this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
            this.renderer.vertex(x3, y3, 0.0F);
            this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
            this.renderer.vertex(x1, y1, 0.0F);
         } else {
            this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
            this.renderer.vertex(x1, y1, 0.0F);
            this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
            this.renderer.vertex(x2, y2, 0.0F);
            this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
            this.renderer.vertex(x3, y3, 0.0F);
         }

      }
   }

   public void rect(float x, float y, float width, float height) {
      if (this.currType != ShapeRenderer.ShapeType.Filled && this.currType != ShapeRenderer.ShapeType.Line) {
         throw new GdxRuntimeException("Must call begin(ShapeType.Filled) or begin(ShapeType.Line)");
      } else {
         this.checkDirty();
         this.checkFlush(8);
         if (this.currType == ShapeRenderer.ShapeType.Line) {
            this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
            this.renderer.vertex(x, y, 0.0F);
            this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
            this.renderer.vertex(x + width, y, 0.0F);
            this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
            this.renderer.vertex(x + width, y, 0.0F);
            this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
            this.renderer.vertex(x + width, y + height, 0.0F);
            this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
            this.renderer.vertex(x + width, y + height, 0.0F);
            this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
            this.renderer.vertex(x, y + height, 0.0F);
            this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
            this.renderer.vertex(x, y + height, 0.0F);
            this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
            this.renderer.vertex(x, y, 0.0F);
         } else {
            this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
            this.renderer.vertex(x, y, 0.0F);
            this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
            this.renderer.vertex(x + width, y, 0.0F);
            this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
            this.renderer.vertex(x + width, y + height, 0.0F);
            this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
            this.renderer.vertex(x + width, y + height, 0.0F);
            this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
            this.renderer.vertex(x, y + height, 0.0F);
            this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
            this.renderer.vertex(x, y, 0.0F);
         }

      }
   }

   public void rect(float x, float y, float width, float height, Color col1, Color col2, Color col3, Color col4) {
      if (this.currType != ShapeRenderer.ShapeType.Filled && this.currType != ShapeRenderer.ShapeType.Line) {
         throw new GdxRuntimeException("Must call begin(ShapeType.Filled) or begin(ShapeType.Line)");
      } else {
         this.checkDirty();
         this.checkFlush(8);
         if (this.currType == ShapeRenderer.ShapeType.Line) {
            this.renderer.color(col1.r, col1.g, col1.b, col1.a);
            this.renderer.vertex(x, y, 0.0F);
            this.renderer.color(col2.r, col2.g, col2.b, col2.a);
            this.renderer.vertex(x + width, y, 0.0F);
            this.renderer.color(col2.r, col2.g, col2.b, col2.a);
            this.renderer.vertex(x + width, y, 0.0F);
            this.renderer.color(col3.r, col3.g, col3.b, col3.a);
            this.renderer.vertex(x + width, y + height, 0.0F);
            this.renderer.color(col3.r, col3.g, col3.b, col3.a);
            this.renderer.vertex(x + width, y + height, 0.0F);
            this.renderer.color(col4.r, col4.g, col4.b, col4.a);
            this.renderer.vertex(x, y + height, 0.0F);
            this.renderer.color(col4.r, col4.g, col4.b, col4.a);
            this.renderer.vertex(x, y + height, 0.0F);
            this.renderer.color(col1.r, col1.g, col1.b, col1.a);
            this.renderer.vertex(x, y, 0.0F);
         } else {
            this.renderer.color(col1.r, col1.g, col1.b, col1.a);
            this.renderer.vertex(x, y, 0.0F);
            this.renderer.color(col2.r, col2.g, col2.b, col2.a);
            this.renderer.vertex(x + width, y, 0.0F);
            this.renderer.color(col3.r, col3.g, col3.b, col3.a);
            this.renderer.vertex(x + width, y + height, 0.0F);
            this.renderer.color(col3.r, col3.g, col3.b, col3.a);
            this.renderer.vertex(x + width, y + height, 0.0F);
            this.renderer.color(col4.r, col4.g, col4.b, col4.a);
            this.renderer.vertex(x, y + height, 0.0F);
            this.renderer.color(col1.r, col1.g, col1.b, col1.a);
            this.renderer.vertex(x, y, 0.0F);
         }

      }
   }

   public void box(float x, float y, float z, float width, float height, float depth) {
      if (this.currType != ShapeRenderer.ShapeType.Line) {
         throw new GdxRuntimeException("Must call begin(ShapeType.Line)");
      } else {
         this.checkDirty();
         this.checkFlush(16);
         depth = -depth;
         this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
         this.renderer.vertex(x, y, z);
         this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
         this.renderer.vertex(x + width, y, z);
         this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
         this.renderer.vertex(x + width, y, z);
         this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
         this.renderer.vertex(x + width, y, z + depth);
         this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
         this.renderer.vertex(x + width, y, z + depth);
         this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
         this.renderer.vertex(x, y, z + depth);
         this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
         this.renderer.vertex(x, y, z + depth);
         this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
         this.renderer.vertex(x, y, z);
         this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
         this.renderer.vertex(x, y, z);
         this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
         this.renderer.vertex(x, y + height, z);
         this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
         this.renderer.vertex(x, y + height, z);
         this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
         this.renderer.vertex(x + width, y + height, z);
         this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
         this.renderer.vertex(x + width, y + height, z);
         this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
         this.renderer.vertex(x + width, y + height, z + depth);
         this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
         this.renderer.vertex(x + width, y + height, z + depth);
         this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
         this.renderer.vertex(x, y + height, z + depth);
         this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
         this.renderer.vertex(x, y + height, z + depth);
         this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
         this.renderer.vertex(x, y + height, z);
         this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
         this.renderer.vertex(x + width, y, z);
         this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
         this.renderer.vertex(x + width, y + height, z);
         this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
         this.renderer.vertex(x + width, y, z + depth);
         this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
         this.renderer.vertex(x + width, y + height, z + depth);
         this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
         this.renderer.vertex(x, y, z + depth);
         this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
         this.renderer.vertex(x, y + height, z + depth);
      }
   }

   public void x(float x, float y, float radius) {
      if (this.currType != ShapeRenderer.ShapeType.Line) {
         throw new GdxRuntimeException("Must call begin(ShapeType.Line)");
      } else {
         this.line(x - radius, y - radius, x + radius, y + radius);
         this.line(x - radius, y + radius, x + radius, y - radius);
      }
   }

   public void circle(float x, float y, float radius) {
      this.circle(x, y, radius, Math.max(1, (int)(6.0F * (float)Math.cbrt((double)radius))));
   }

   public void circle(float x, float y, float radius, int segments) {
      if (segments <= 0) {
         throw new IllegalArgumentException("segments must be > 0.");
      } else if (this.currType != ShapeRenderer.ShapeType.Filled && this.currType != ShapeRenderer.ShapeType.Line) {
         throw new GdxRuntimeException("Must call begin(ShapeType.Filled) or begin(ShapeType.Line)");
      } else {
         this.checkDirty();
         float angle = 6.283185F / (float)segments;
         float cos = MathUtils.cos(angle);
         float sin = MathUtils.sin(angle);
         float cx = radius;
         float cy = 0.0F;
         int i;
         float temp;
         if (this.currType == ShapeRenderer.ShapeType.Line) {
            this.checkFlush(segments * 2 + 2);

            for(i = 0; i < segments; ++i) {
               this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
               this.renderer.vertex(x + cx, y + cy, 0.0F);
               temp = cx;
               cx = cos * cx - sin * cy;
               cy = sin * temp + cos * cy;
               this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
               this.renderer.vertex(x + cx, y + cy, 0.0F);
            }

            this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
            this.renderer.vertex(x + cx, y + cy, 0.0F);
         } else {
            this.checkFlush(segments * 3 + 3);
            --segments;

            for(i = 0; i < segments; ++i) {
               this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
               this.renderer.vertex(x, y, 0.0F);
               this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
               this.renderer.vertex(x + cx, y + cy, 0.0F);
               temp = cx;
               cx = cos * cx - sin * cy;
               cy = sin * temp + cos * cy;
               this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
               this.renderer.vertex(x + cx, y + cy, 0.0F);
            }

            this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
            this.renderer.vertex(x, y, 0.0F);
            this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
            this.renderer.vertex(x + cx, y + cy, 0.0F);
         }

         cy = 0.0F;
         this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
         this.renderer.vertex(x + radius, y + cy, 0.0F);
      }
   }

   public void ellipse(float x, float y, float width, float height) {
      this.ellipse(x, y, width, height, Math.max(1, (int)(12.0F * (float)Math.cbrt((double)Math.max(width * 0.5F, height * 0.5F)))));
   }

   public void ellipse(float x, float y, float width, float height, int segments) {
      if (segments <= 0) {
         throw new IllegalArgumentException("segments must be > 0.");
      } else if (this.currType != ShapeRenderer.ShapeType.Filled && this.currType != ShapeRenderer.ShapeType.Line) {
         throw new GdxRuntimeException("Must call begin(ShapeType.Filled) or begin(ShapeType.Line)");
      } else {
         this.checkDirty();
         this.checkFlush(segments * 3);
         float angle = 6.283185F / (float)segments;
         float cx = x + width / 2.0F;
         float cy = y + height / 2.0F;
         int i;
         if (this.currType == ShapeRenderer.ShapeType.Line) {
            for(i = 0; i < segments; ++i) {
               this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
               this.renderer.vertex(cx + width * 0.5F * MathUtils.cos((float)i * angle), cy + height * 0.5F * MathUtils.sin((float)i * angle), 0.0F);
               this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
               this.renderer.vertex(cx + width * 0.5F * MathUtils.cos((float)(i + 1) * angle), cy + height * 0.5F * MathUtils.sin((float)(i + 1) * angle), 0.0F);
            }
         } else {
            for(i = 0; i < segments; ++i) {
               this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
               this.renderer.vertex(cx + width * 0.5F * MathUtils.cos((float)i * angle), cy + height * 0.5F * MathUtils.sin((float)i * angle), 0.0F);
               this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
               this.renderer.vertex(cx, cy, 0.0F);
               this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
               this.renderer.vertex(cx + width * 0.5F * MathUtils.cos((float)(i + 1) * angle), cy + height * 0.5F * MathUtils.sin((float)(i + 1) * angle), 0.0F);
            }
         }

      }
   }

   public void cone(float x, float y, float z, float radius, float height) {
      this.cone(x, y, z, radius, height, Math.max(1, (int)(4.0F * (float)Math.sqrt((double)radius))));
   }

   public void cone(float x, float y, float z, float radius, float height, int segments) {
      if (segments <= 0) {
         throw new IllegalArgumentException("segments must be > 0.");
      } else if (this.currType != ShapeRenderer.ShapeType.Filled && this.currType != ShapeRenderer.ShapeType.Line) {
         throw new GdxRuntimeException("Must call begin(ShapeType.Filled) or begin(ShapeType.Line)");
      } else {
         this.checkDirty();
         this.checkFlush(segments * 4 + 2);
         float angle = 6.283185F / (float)segments;
         float cos = MathUtils.cos(angle);
         float sin = MathUtils.sin(angle);
         float cx = radius;
         float cy = 0.0F;
         int i;
         float temp;
         if (this.currType == ShapeRenderer.ShapeType.Line) {
            for(i = 0; i < segments; ++i) {
               this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
               this.renderer.vertex(x + cx, y + cy, z);
               this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
               this.renderer.vertex(x, y, z + height);
               this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
               this.renderer.vertex(x + cx, y + cy, z);
               temp = cx;
               cx = cos * cx - sin * cy;
               cy = sin * temp + cos * cy;
               this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
               this.renderer.vertex(x + cx, y + cy, z);
            }

            this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
            this.renderer.vertex(x + cx, y + cy, z);
         } else {
            --segments;

            for(i = 0; i < segments; ++i) {
               this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
               this.renderer.vertex(x, y, z);
               this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
               this.renderer.vertex(x + cx, y + cy, z);
               temp = cx;
               float temp2 = cy;
               cx = cos * cx - sin * cy;
               cy = sin * temp + cos * cy;
               this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
               this.renderer.vertex(x + cx, y + cy, z);
               this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
               this.renderer.vertex(x + temp, y + temp2, z);
               this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
               this.renderer.vertex(x + cx, y + cy, z);
               this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
               this.renderer.vertex(x, y, z + height);
            }

            this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
            this.renderer.vertex(x, y, z);
            this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
            this.renderer.vertex(x + cx, y + cy, z);
         }

         cy = 0.0F;
         this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
         this.renderer.vertex(x + radius, y + cy, z);
      }
   }

   public void polygon(float[] vertices) {
      this.polygon(vertices, 0, vertices.length);
   }

   public void polygon(float[] vertices, int offset, int count) {
      if (this.currType != ShapeRenderer.ShapeType.Line) {
         throw new GdxRuntimeException("Must call begin(ShapeType.Line)");
      } else if (count < 6) {
         throw new IllegalArgumentException("Polygons must contain at least 3 points.");
      } else if (count % 2 != 0) {
         throw new IllegalArgumentException("Polygons must have a pair number of vertices.");
      } else {
         this.checkDirty();
         this.checkFlush(count);
         float firstX = vertices[0];
         float firstY = vertices[1];
         int i = offset;

         for(int n = offset + count; i < n; i += 2) {
            float x1 = vertices[i];
            float y1 = vertices[i + 1];
            float x2;
            float y2;
            if (i + 2 >= count) {
               x2 = firstX;
               y2 = firstY;
            } else {
               x2 = vertices[i + 2];
               y2 = vertices[i + 3];
            }

            this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
            this.renderer.vertex(x1, y1, 0.0F);
            this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
            this.renderer.vertex(x2, y2, 0.0F);
         }

      }
   }

   public void polyline(float[] vertices) {
      this.polyline(vertices, 0, vertices.length);
   }

   public void polyline(float[] vertices, int offset, int count) {
      if (this.currType != ShapeRenderer.ShapeType.Line) {
         throw new GdxRuntimeException("Must call begin(ShapeType.Line)");
      } else if (count < 4) {
         throw new IllegalArgumentException("Polylines must contain at least 2 points.");
      } else if (count % 2 != 0) {
         throw new IllegalArgumentException("Polylines must have a pair number of vertices.");
      } else {
         this.checkDirty();
         this.checkFlush(count);
         int i = offset;

         for(int n = offset + count - 2; i < n; i += 2) {
            float x1 = vertices[i];
            float y1 = vertices[i + 1];
            float x2 = vertices[i + 2];
            float y2 = vertices[i + 3];
            this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
            this.renderer.vertex(x1, y1, 0.0F);
            this.renderer.color(this.color.r, this.color.g, this.color.b, this.color.a);
            this.renderer.vertex(x2, y2, 0.0F);
         }

      }
   }

   private void checkDirty() {
      if (this.matrixDirty) {
         ShapeRenderer.ShapeType type = this.currType;
         this.end();
         this.begin(type);
      }
   }

   private void checkFlush(int newVertices) {
      if (this.renderer.getMaxVertices() - this.renderer.getNumVertices() < newVertices) {
         ShapeRenderer.ShapeType type = this.currType;
         this.end();
         this.begin(type);
      }
   }

   public void end() {
      this.renderer.end();
      this.currType = null;
   }

   public void flush() {
      ShapeRenderer.ShapeType type = this.currType;
      this.end();
      this.begin(type);
   }

   public ShapeRenderer.ShapeType getCurrentType() {
      return this.currType;
   }

   public void dispose() {
      this.renderer.dispose();
   }

   public static enum ShapeType {
      Point(0),
      Line(1),
      Filled(4);

      private final int glType;

      private ShapeType(int glType) {
         this.glType = glType;
      }

      public int getGlType() {
         return this.glType;
      }
   }
}
