package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.nio.FloatBuffer;

public class ImmediateModeRenderer10 implements ImmediateModeRenderer {
   private int primitiveType;
   private float[] positions;
   private FloatBuffer positionsBuffer;
   private float[] colors;
   private FloatBuffer colorsBuffer;
   private float[] normals;
   private FloatBuffer normalsBuffer;
   private float[] texCoords;
   private FloatBuffer texCoordsBuffer;
   private int idxPos;
   private int idxCols;
   private int idxNors;
   private int idxTexCoords;
   private boolean hasCols;
   private boolean hasNors;
   private boolean hasTexCoords;
   private final int maxVertices;
   private int numVertices;

   public ImmediateModeRenderer10() {
      this(2000);
   }

   public ImmediateModeRenderer10(int maxVertices) {
      this.idxPos = 0;
      this.idxCols = 0;
      this.idxNors = 0;
      this.idxTexCoords = 0;
      this.maxVertices = maxVertices;
      if (Gdx.graphics.isGL20Available()) {
         throw new GdxRuntimeException("ImmediateModeRenderer can only be used with OpenGL ES 1.0/1.1");
      } else {
         this.positions = new float[3 * maxVertices];
         this.positionsBuffer = BufferUtils.newFloatBuffer(3 * maxVertices);
         this.colors = new float[4 * maxVertices];
         this.colorsBuffer = BufferUtils.newFloatBuffer(4 * maxVertices);
         this.normals = new float[3 * maxVertices];
         this.normalsBuffer = BufferUtils.newFloatBuffer(3 * maxVertices);
         this.texCoords = new float[2 * maxVertices];
         this.texCoordsBuffer = BufferUtils.newFloatBuffer(2 * maxVertices);
      }
   }

   public void begin(Matrix4 projModelView, int primitiveType) {
      GL10 gl = Gdx.gl10;
      gl.glMatrixMode(5889);
      gl.glLoadMatrixf(projModelView.val, 0);
      gl.glMatrixMode(5888);
      gl.glLoadIdentity();
      this.begin(primitiveType);
   }

   public void begin(int primitiveType) {
      this.primitiveType = primitiveType;
      this.numVertices = 0;
      this.idxPos = 0;
      this.idxCols = 0;
      this.idxNors = 0;
      this.idxTexCoords = 0;
      this.hasCols = false;
      this.hasNors = false;
      this.hasTexCoords = false;
   }

   public void color(float r, float g, float b, float a) {
      this.colors[this.idxCols] = r;
      this.colors[this.idxCols + 1] = g;
      this.colors[this.idxCols + 2] = b;
      this.colors[this.idxCols + 3] = a;
      this.hasCols = true;
   }

   public void normal(float x, float y, float z) {
      this.normals[this.idxNors] = x;
      this.normals[this.idxNors + 1] = y;
      this.normals[this.idxNors + 2] = z;
      this.hasNors = true;
   }

   public void texCoord(float u, float v) {
      this.texCoords[this.idxTexCoords] = u;
      this.texCoords[this.idxTexCoords + 1] = v;
      this.hasTexCoords = true;
   }

   public void vertex(float x, float y, float z) {
      this.positions[this.idxPos++] = x;
      this.positions[this.idxPos++] = y;
      this.positions[this.idxPos++] = z;
      if (this.hasCols) {
         this.idxCols += 4;
      }

      if (this.hasNors) {
         this.idxNors += 3;
      }

      if (this.hasTexCoords) {
         this.idxTexCoords += 2;
      }

      ++this.numVertices;
   }

   public int getNumVertices() {
      return this.numVertices;
   }

   public int getMaxVertices() {
      return this.maxVertices;
   }

   public void end() {
      if (this.idxPos != 0) {
         GL10 gl = Gdx.gl10;
         gl.glEnableClientState(32884);
         this.positionsBuffer.clear();
         BufferUtils.copy(this.positions, this.positionsBuffer, this.idxPos, 0);
         gl.glVertexPointer(3, 5126, 0, this.positionsBuffer);
         if (this.hasCols) {
            gl.glEnableClientState(32886);
            this.colorsBuffer.clear();
            BufferUtils.copy(this.colors, this.colorsBuffer, this.idxCols, 0);
            gl.glColorPointer(4, 5126, 0, this.colorsBuffer);
         }

         if (this.hasNors) {
            gl.glEnableClientState(32885);
            this.normalsBuffer.clear();
            BufferUtils.copy(this.normals, this.normalsBuffer, this.idxNors, 0);
            gl.glNormalPointer(5126, 0, this.normalsBuffer);
         }

         if (this.hasTexCoords) {
            gl.glClientActiveTexture(33984);
            gl.glEnableClientState(32888);
            this.texCoordsBuffer.clear();
            BufferUtils.copy(this.texCoords, this.texCoordsBuffer, this.idxTexCoords, 0);
            gl.glTexCoordPointer(2, 5126, 0, this.texCoordsBuffer);
         }

         gl.glDrawArrays(this.primitiveType, 0, this.idxPos / 3);
         if (this.hasCols) {
            gl.glDisableClientState(32886);
         }

         if (this.hasNors) {
            gl.glDisableClientState(32885);
         }

         if (this.hasTexCoords) {
            gl.glDisableClientState(32888);
         }

      }
   }

   public void vertex(Vector3 point) {
      this.vertex(point.x, point.y, point.z);
   }

   public void dispose() {
   }
}
