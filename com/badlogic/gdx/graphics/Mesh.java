package com.badlogic.gdx.graphics;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.IndexArray;
import com.badlogic.gdx.graphics.glutils.IndexBufferObject;
import com.badlogic.gdx.graphics.glutils.IndexBufferObjectSubData;
import com.badlogic.gdx.graphics.glutils.IndexData;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.VertexArray;
import com.badlogic.gdx.graphics.glutils.VertexBufferObject;
import com.badlogic.gdx.graphics.glutils.VertexBufferObjectSubData;
import com.badlogic.gdx.graphics.glutils.VertexData;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Mesh implements Disposable {
   static final Map<Application, List<Mesh>> meshes = new HashMap();
   public static boolean forceVBO = false;
   final VertexData vertices;
   final IndexData indices;
   boolean autoBind = true;
   final boolean isVertexArray;
   private final Vector3 tmpV = new Vector3();

   public Mesh(boolean isStatic, int maxVertices, int maxIndices, VertexAttribute... attributes) {
      if (Gdx.gl20 == null && Gdx.gl11 == null && !forceVBO) {
         this.vertices = new VertexArray(maxVertices, attributes);
         this.indices = new IndexArray(maxIndices);
         this.isVertexArray = true;
      } else {
         this.vertices = new VertexBufferObject(isStatic, maxVertices, attributes);
         this.indices = new IndexBufferObject(isStatic, maxIndices);
         this.isVertexArray = false;
      }

      addManagedMesh(Gdx.app, this);
   }

   public Mesh(boolean isStatic, int maxVertices, int maxIndices, VertexAttributes attributes) {
      if (Gdx.gl20 == null && Gdx.gl11 == null && !forceVBO) {
         this.vertices = new VertexArray(maxVertices, attributes);
         this.indices = new IndexArray(maxIndices);
         this.isVertexArray = true;
      } else {
         this.vertices = new VertexBufferObject(isStatic, maxVertices, attributes);
         this.indices = new IndexBufferObject(isStatic, maxIndices);
         this.isVertexArray = false;
      }

      addManagedMesh(Gdx.app, this);
   }

   public Mesh(boolean staticVertices, boolean staticIndices, int maxVertices, int maxIndices, VertexAttributes attributes) {
      if (Gdx.gl20 == null && Gdx.gl11 == null && !forceVBO) {
         this.vertices = new VertexArray(maxVertices, attributes);
         this.indices = new IndexArray(maxIndices);
         this.isVertexArray = true;
      } else {
         this.vertices = new VertexBufferObject(staticVertices, maxVertices, attributes);
         this.indices = new IndexBufferObject(staticIndices, maxIndices);
         this.isVertexArray = false;
      }

      addManagedMesh(Gdx.app, this);
   }

   public Mesh(Mesh.VertexDataType type, boolean isStatic, int maxVertices, int maxIndices, VertexAttribute... attributes) {
      if (type != Mesh.VertexDataType.VertexBufferObject && !forceVBO) {
         if (type == Mesh.VertexDataType.VertexBufferObjectSubData) {
            this.vertices = new VertexBufferObjectSubData(isStatic, maxVertices, attributes);
            this.indices = new IndexBufferObjectSubData(isStatic, maxIndices);
            this.isVertexArray = false;
         } else {
            this.vertices = new VertexArray(maxVertices, attributes);
            this.indices = new IndexArray(maxIndices);
            this.isVertexArray = true;
         }
      } else {
         this.vertices = new VertexBufferObject(isStatic, maxVertices, attributes);
         this.indices = new IndexBufferObject(isStatic, maxIndices);
         this.isVertexArray = false;
      }

      addManagedMesh(Gdx.app, this);
   }

   public static Mesh create(boolean isStatic, Mesh base, Matrix4[] transformations) {
      VertexAttribute posAttr = base.getVertexAttribute(1);
      int offset = posAttr.offset / 4;
      int numComponents = posAttr.numComponents;
      int numVertices = base.getNumVertices();
      int vertexSize = base.getVertexSize() / 4;
      int baseSize = numVertices * vertexSize;
      int numIndices = base.getNumIndices();
      float[] vertices = new float[numVertices * vertexSize * transformations.length];
      short[] indices = new short[numIndices * transformations.length];
      base.getIndices(indices);

      for(int i = 0; i < transformations.length; ++i) {
         base.getVertices(0, baseSize, vertices, baseSize * i);
         transform(transformations[i], vertices, vertexSize, offset, numComponents, numVertices * i, numVertices);
         if (i > 0) {
            for(int j = 0; j < numIndices; ++j) {
               indices[numIndices * i + j] = (short)(indices[j] + numVertices * i);
            }
         }
      }

      Mesh result = new Mesh(isStatic, vertices.length / vertexSize, indices.length, base.getVertexAttributes());
      result.setVertices(vertices);
      result.setIndices(indices);
      return result;
   }

   public static Mesh create(boolean isStatic, Mesh[] meshes) {
      return create(isStatic, (Mesh[])meshes, (Matrix4[])null);
   }

   public static Mesh create(boolean isStatic, Mesh[] meshes, Matrix4[] transformations) {
      if (transformations != null && transformations.length < meshes.length) {
         throw new IllegalArgumentException("Not enough transformations specified");
      } else {
         VertexAttributes attributes = meshes[0].getVertexAttributes();
         int vertCount = meshes[0].getNumVertices();
         int idxCount = meshes[0].getNumIndices();

         for(int i = 1; i < meshes.length; ++i) {
            if (!meshes[i].getVertexAttributes().equals(attributes)) {
               throw new IllegalArgumentException("Inconsistent VertexAttributes");
            }

            vertCount += meshes[i].getNumVertices();
            idxCount += meshes[i].getNumIndices();
         }

         VertexAttribute posAttr = meshes[0].getVertexAttribute(1);
         int offset = posAttr.offset / 4;
         int numComponents = posAttr.numComponents;
         int vertexSize = attributes.vertexSize / 4;
         float[] vertices = new float[vertCount * vertexSize];
         short[] indices = new short[idxCount];
         meshes[0].getVertices(vertices);
         meshes[0].getIndices(indices);
         int voffset = meshes[0].getNumVertices() * vertexSize;
         int ioffset = meshes[0].getNumIndices();

         for(int i = 1; i < meshes.length; ++i) {
            Mesh mesh = meshes[i];
            int vsize = mesh.getNumVertices() * vertexSize;
            int isize = mesh.getNumIndices();
            mesh.getVertices(0, vsize, vertices, voffset);
            if (transformations != null) {
               transform(transformations[i], vertices, vertexSize, offset, numComponents, voffset / vertexSize, vsize / vertexSize);
            }

            mesh.getIndices(indices, ioffset);

            for(byte j = 0; j < isize; ++i) {
               indices[ioffset + j] = (short)(indices[ioffset + j] + voffset);
            }

            voffset += vsize;
            ioffset += isize;
         }

         Mesh result = new Mesh(isStatic, vertices.length / vertexSize, indices.length, attributes);
         result.setVertices(vertices);
         result.setIndices(indices);
         return result;
      }
   }

   public Mesh setVertices(float[] vertices) {
      this.vertices.setVertices(vertices, 0, vertices.length);
      return this;
   }

   public Mesh setVertices(float[] vertices, int offset, int count) {
      this.vertices.setVertices(vertices, offset, count);
      return this;
   }

   public void getVertices(float[] vertices) {
      this.getVertices(0, -1, vertices);
   }

   public void getVertices(int srcOffset, float[] vertices) {
      this.getVertices(srcOffset, -1, vertices);
   }

   public void getVertices(int srcOffset, int count, float[] vertices) {
      this.getVertices(srcOffset, count, vertices, 0);
   }

   public void getVertices(int srcOffset, int count, float[] vertices, int destOffset) {
      int max = this.getNumVertices() * this.getVertexSize() / 4;
      if (count == -1) {
         count = max - srcOffset;
         if (count > vertices.length - destOffset) {
            count = vertices.length - destOffset;
         }
      }

      if (srcOffset >= 0 && count > 0 && srcOffset + count <= max && destOffset >= 0 && destOffset < vertices.length) {
         if (vertices.length - destOffset < count) {
            throw new IllegalArgumentException("not enough room in vertices array, has " + vertices.length + " floats, needs " + count);
         } else {
            int pos = this.getVerticesBuffer().position();
            this.getVerticesBuffer().position(srcOffset);
            this.getVerticesBuffer().get(vertices, destOffset, count);
            this.getVerticesBuffer().position(pos);
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public Mesh setIndices(short[] indices) {
      this.indices.setIndices(indices, 0, indices.length);
      return this;
   }

   public Mesh setIndices(short[] indices, int offset, int count) {
      this.indices.setIndices(indices, offset, count);
      return this;
   }

   public void getIndices(short[] indices) {
      this.getIndices(indices, 0);
   }

   public void getIndices(short[] indices, int destOffset) {
      if (indices.length - destOffset < this.getNumIndices()) {
         throw new IllegalArgumentException("not enough room in indices array, has " + indices.length + " floats, needs " + this.getNumIndices());
      } else {
         int pos = this.getIndicesBuffer().position();
         this.getIndicesBuffer().position(0);
         this.getIndicesBuffer().get(indices, destOffset, this.getNumIndices());
         this.getIndicesBuffer().position(pos);
      }
   }

   public int getNumIndices() {
      return this.indices.getNumIndices();
   }

   public int getNumVertices() {
      return this.vertices.getNumVertices();
   }

   public int getMaxVertices() {
      return this.vertices.getNumMaxVertices();
   }

   public int getMaxIndices() {
      return this.indices.getNumMaxIndices();
   }

   public int getVertexSize() {
      return this.vertices.getAttributes().vertexSize;
   }

   public void setAutoBind(boolean autoBind) {
      this.autoBind = autoBind;
   }

   public void bind() {
      if (Gdx.graphics.isGL20Available()) {
         throw new IllegalStateException("can't use this render method with OpenGL ES 2.0");
      } else {
         this.vertices.bind();
         if (!this.isVertexArray && this.indices.getNumIndices() > 0) {
            this.indices.bind();
         }

      }
   }

   public void unbind() {
      if (Gdx.graphics.isGL20Available()) {
         throw new IllegalStateException("can't use this render method with OpenGL ES 2.0");
      } else {
         this.vertices.unbind();
         if (!this.isVertexArray && this.indices.getNumIndices() > 0) {
            this.indices.unbind();
         }

      }
   }

   public void bind(ShaderProgram shader) {
      if (!Gdx.graphics.isGL20Available()) {
         throw new IllegalStateException("can't use this render method with OpenGL ES 1.x");
      } else {
         this.vertices.bind(shader);
         if (this.indices.getNumIndices() > 0) {
            this.indices.bind();
         }

      }
   }

   public void unbind(ShaderProgram shader) {
      if (!Gdx.graphics.isGL20Available()) {
         throw new IllegalStateException("can't use this render method with OpenGL ES 1.x");
      } else {
         this.vertices.unbind(shader);
         if (this.indices.getNumIndices() > 0) {
            this.indices.unbind();
         }

      }
   }

   public void render(int primitiveType) {
      this.render(primitiveType, 0, this.indices.getNumMaxIndices() > 0 ? this.getNumIndices() : this.getNumVertices());
   }

   public void render(int primitiveType, int offset, int count) {
      if (Gdx.graphics.isGL20Available()) {
         throw new IllegalStateException("can't use this render method with OpenGL ES 2.0");
      } else if (count != 0) {
         if (this.autoBind) {
            this.bind();
         }

         if (this.isVertexArray) {
            if (this.indices.getNumIndices() > 0) {
               ShortBuffer buffer = this.indices.getBuffer();
               int oldPosition = buffer.position();
               int oldLimit = buffer.limit();
               buffer.position(offset);
               buffer.limit(offset + count);
               Gdx.gl10.glDrawElements(primitiveType, count, 5123, buffer);
               buffer.position(oldPosition);
               buffer.limit(oldLimit);
            } else {
               Gdx.gl10.glDrawArrays(primitiveType, offset, count);
            }
         } else if (this.indices.getNumIndices() > 0) {
            Gdx.gl11.glDrawElements(primitiveType, count, 5123, offset * 2);
         } else {
            Gdx.gl11.glDrawArrays(primitiveType, offset, count);
         }

         if (this.autoBind) {
            this.unbind();
         }

      }
   }

   public void render(ShaderProgram shader, int primitiveType) {
      this.render(shader, primitiveType, 0, this.indices.getNumMaxIndices() > 0 ? this.getNumIndices() : this.getNumVertices());
   }

   public void render(ShaderProgram shader, int primitiveType, int offset, int count) {
      if (!Gdx.graphics.isGL20Available()) {
         throw new IllegalStateException("can't use this render method with OpenGL ES 1.x");
      } else if (count != 0) {
         if (this.autoBind) {
            this.bind(shader);
         }

         if (this.isVertexArray) {
            if (this.indices.getNumIndices() > 0) {
               ShortBuffer buffer = this.indices.getBuffer();
               int oldPosition = buffer.position();
               int oldLimit = buffer.limit();
               buffer.position(offset);
               buffer.limit(offset + count);
               Gdx.gl20.glDrawElements(primitiveType, count, 5123, buffer);
               buffer.position(oldPosition);
               buffer.limit(oldLimit);
            } else {
               Gdx.gl20.glDrawArrays(primitiveType, offset, count);
            }
         } else if (this.indices.getNumIndices() > 0) {
            Gdx.gl20.glDrawElements(primitiveType, count, 5123, offset * 2);
         } else {
            Gdx.gl20.glDrawArrays(primitiveType, offset, count);
         }

         if (this.autoBind) {
            this.unbind(shader);
         }

      }
   }

   public void dispose() {
      if (meshes.get(Gdx.app) != null) {
         ((List)meshes.get(Gdx.app)).remove(this);
      }

      this.vertices.dispose();
      this.indices.dispose();
   }

   public VertexAttribute getVertexAttribute(int usage) {
      VertexAttributes attributes = this.vertices.getAttributes();
      int len = attributes.size();

      for(int i = 0; i < len; ++i) {
         if (attributes.get(i).usage == usage) {
            return attributes.get(i);
         }
      }

      return null;
   }

   public VertexAttributes getVertexAttributes() {
      return this.vertices.getAttributes();
   }

   public FloatBuffer getVerticesBuffer() {
      return this.vertices.getBuffer();
   }

   public BoundingBox calculateBoundingBox() {
      BoundingBox bbox = new BoundingBox();
      this.calculateBoundingBox(bbox);
      return bbox;
   }

   public void calculateBoundingBox(BoundingBox bbox) {
      int numVertices = this.getNumVertices();
      if (numVertices == 0) {
         throw new GdxRuntimeException("No vertices defined");
      } else {
         FloatBuffer verts = this.vertices.getBuffer();
         bbox.inf();
         VertexAttribute posAttrib = this.getVertexAttribute(1);
         int offset = posAttrib.offset / 4;
         int vertexSize = this.vertices.getAttributes().vertexSize / 4;
         int idx = offset;
         int i;
         switch(posAttrib.numComponents) {
         case 1:
            for(i = 0; i < numVertices; ++i) {
               bbox.ext(verts.get(idx), 0.0F, 0.0F);
               idx += vertexSize;
            }

            return;
         case 2:
            for(i = 0; i < numVertices; ++i) {
               bbox.ext(verts.get(idx), verts.get(idx + 1), 0.0F);
               idx += vertexSize;
            }

            return;
         case 3:
            for(i = 0; i < numVertices; ++i) {
               bbox.ext(verts.get(idx), verts.get(idx + 1), verts.get(idx + 2));
               idx += vertexSize;
            }
         }

      }
   }

   public BoundingBox calculateBoundingBox(BoundingBox out, int offset, int count) {
      return this.extendBoundingBox(out.inf(), offset, count);
   }

   public BoundingBox calculateBoundingBox(BoundingBox out, int offset, int count, Matrix4 transform) {
      return this.extendBoundingBox(out.inf(), offset, count, transform);
   }

   public BoundingBox extendBoundingBox(BoundingBox out, int offset, int count) {
      return this.extendBoundingBox(out, offset, count, (Matrix4)null);
   }

   public BoundingBox extendBoundingBox(BoundingBox out, int offset, int count, Matrix4 transform) {
      int numIndices = this.getNumIndices();
      if (offset >= 0 && count >= 1 && offset + count <= numIndices) {
         FloatBuffer verts = this.vertices.getBuffer();
         ShortBuffer index = this.indices.getBuffer();
         VertexAttribute posAttrib = this.getVertexAttribute(1);
         int posoff = posAttrib.offset / 4;
         int vertexSize = this.vertices.getAttributes().vertexSize / 4;
         int end = offset + count;
         int i;
         int idx;
         switch(posAttrib.numComponents) {
         case 1:
            for(i = offset; i < end; ++i) {
               idx = index.get(i) * vertexSize + posoff;
               this.tmpV.set(verts.get(idx), 0.0F, 0.0F);
               if (transform != null) {
                  this.tmpV.mul(transform);
               }

               out.ext(this.tmpV);
            }

            return out;
         case 2:
            for(i = offset; i < end; ++i) {
               idx = index.get(i) * vertexSize + posoff;
               this.tmpV.set(verts.get(idx), verts.get(idx + 1), 0.0F);
               if (transform != null) {
                  this.tmpV.mul(transform);
               }

               out.ext(this.tmpV);
            }

            return out;
         case 3:
            for(i = offset; i < end; ++i) {
               idx = index.get(i) * vertexSize + posoff;
               this.tmpV.set(verts.get(idx), verts.get(idx + 1), verts.get(idx + 2));
               if (transform != null) {
                  this.tmpV.mul(transform);
               }

               out.ext(this.tmpV);
            }
         }

         return out;
      } else {
         throw new GdxRuntimeException("Not enough indices");
      }
   }

   public ShortBuffer getIndicesBuffer() {
      return this.indices.getBuffer();
   }

   private static void addManagedMesh(Application app, Mesh mesh) {
      List<Mesh> managedResources = (List)meshes.get(app);
      if (managedResources == null) {
         managedResources = new ArrayList();
      }

      ((List)managedResources).add(mesh);
      meshes.put(app, managedResources);
   }

   public static void invalidateAllMeshes(Application app) {
      List<Mesh> meshesList = (List)meshes.get(app);
      if (meshesList != null) {
         for(int i = 0; i < meshesList.size(); ++i) {
            if (((Mesh)meshesList.get(i)).vertices instanceof VertexBufferObject) {
               ((VertexBufferObject)((Mesh)meshesList.get(i)).vertices).invalidate();
            }

            ((Mesh)meshesList.get(i)).indices.invalidate();
         }

      }
   }

   public static void clearAllMeshes(Application app) {
      meshes.remove(app);
   }

   public static String getManagedStatus() {
      StringBuilder builder = new StringBuilder();
      int i = false;
      builder.append("Managed meshes/app: { ");
      Iterator var3 = meshes.keySet().iterator();

      while(var3.hasNext()) {
         Application app = (Application)var3.next();
         builder.append(((List)meshes.get(app)).size());
         builder.append(" ");
      }

      builder.append("}");
      return builder.toString();
   }

   public void scale(float scaleX, float scaleY, float scaleZ) {
      float[] vertices;
      VertexAttribute posAttr = this.getVertexAttribute(1);
      int offset = posAttr.offset / 4;
      int numComponents = posAttr.numComponents;
      int numVertices = this.getNumVertices();
      int vertexSize = this.getVertexSize() / 4;
      vertices = new float[numVertices * vertexSize];
      this.getVertices(vertices);
      int idx = offset;
      int i;
      label32:
      switch(numComponents) {
      case 1:
         i = 0;

         while(true) {
            if (i >= numVertices) {
               break label32;
            }

            vertices[idx] *= scaleX;
            idx += vertexSize;
            ++i;
         }
      case 2:
         i = 0;

         while(true) {
            if (i >= numVertices) {
               break label32;
            }

            vertices[idx] *= scaleX;
            vertices[idx + 1] *= scaleY;
            idx += vertexSize;
            ++i;
         }
      case 3:
         for(i = 0; i < numVertices; ++i) {
            vertices[idx] *= scaleX;
            vertices[idx + 1] *= scaleY;
            vertices[idx + 2] *= scaleZ;
            idx += vertexSize;
         }
      }

      this.setVertices(vertices);
   }

   public void transform(Matrix4 matrix) {
      this.transform(matrix, 0, this.getNumVertices());
   }

   protected void transform(Matrix4 matrix, int start, int count) {
      VertexAttribute posAttr = this.getVertexAttribute(1);
      int offset = posAttr.offset / 4;
      int vertexSize = this.getVertexSize() / 4;
      int numComponents = posAttr.numComponents;
      int numVertices = this.getNumVertices();
      float[] vertices = new float[numVertices * vertexSize];
      this.getVertices(0, vertices.length, vertices);
      transform(matrix, vertices, vertexSize, offset, numComponents, start, count);
      this.setVertices(vertices, 0, vertices.length);
   }

   public static void transform(Matrix4 matrix, float[] vertices, int vertexSize, int offset, int dimensions, int start, int count) {
      if (offset >= 0 && dimensions >= 1 && offset + dimensions <= vertexSize) {
         if (start >= 0 && count >= 1 && (start + count) * vertexSize <= vertices.length) {
            Vector3 tmp = new Vector3();
            int idx = offset + start * vertexSize;
            int i;
            switch(dimensions) {
            case 1:
               for(i = 0; i < count; ++i) {
                  tmp.set(vertices[idx], 0.0F, 0.0F).mul(matrix);
                  vertices[idx] = tmp.x;
                  idx += vertexSize;
               }

               return;
            case 2:
               for(i = 0; i < count; ++i) {
                  tmp.set(vertices[idx], vertices[idx + 1], 0.0F).mul(matrix);
                  vertices[idx] = tmp.x;
                  vertices[idx + 1] = tmp.y;
                  idx += vertexSize;
               }

               return;
            case 3:
               for(i = 0; i < count; ++i) {
                  tmp.set(vertices[idx], vertices[idx + 1], vertices[idx + 2]).mul(matrix);
                  vertices[idx] = tmp.x;
                  vertices[idx + 1] = tmp.y;
                  vertices[idx + 2] = tmp.z;
                  idx += vertexSize;
               }
            }

         } else {
            throw new IndexOutOfBoundsException("start = " + start + ", count = " + count + ", vertexSize = " + vertexSize + ", length = " + vertices.length);
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void transformUV(Matrix3 matrix) {
      this.transformUV(matrix, 0, this.getNumVertices());
   }

   protected void transformUV(Matrix3 matrix, int start, int count) {
      VertexAttribute posAttr = this.getVertexAttribute(16);
      int offset = posAttr.offset / 4;
      int vertexSize = this.getVertexSize() / 4;
      int numVertices = this.getNumVertices();
      float[] vertices = new float[numVertices * vertexSize];
      this.getVertices(0, vertices.length, vertices);
      transformUV(matrix, vertices, vertexSize, offset, start, count);
      this.setVertices(vertices, 0, vertices.length);
   }

   public static void transformUV(Matrix3 matrix, float[] vertices, int vertexSize, int offset, int start, int count) {
      if (start >= 0 && count >= 1 && (start + count) * vertexSize <= vertices.length) {
         Vector2 tmp = new Vector2();
         int idx = offset + start * vertexSize;

         for(int i = 0; i < count; ++i) {
            tmp.set(vertices[idx], vertices[idx + 1]).mul(matrix);
            vertices[idx] = tmp.x;
            vertices[idx + 1] = tmp.y;
            idx += vertexSize;
         }

      } else {
         throw new IndexOutOfBoundsException("start = " + start + ", count = " + count + ", vertexSize = " + vertexSize + ", length = " + vertices.length);
      }
   }

   public Mesh copy(boolean isStatic, boolean removeDuplicates, int[] usage) {
      int vertexSize = this.getVertexSize() / 4;
      int numVertices = this.getNumVertices();
      float[] vertices = new float[numVertices * vertexSize];
      this.getVertices(0, vertices.length, vertices);
      short[] checks = null;
      VertexAttribute[] attrs = null;
      int newVertexSize = 0;
      int numIndices;
      int size;
      int i;
      if (usage != null) {
         numIndices = 0;
         int as = 0;

         int idx;
         for(idx = 0; idx < usage.length; ++idx) {
            if (this.getVertexAttribute(usage[idx]) != null) {
               numIndices += this.getVertexAttribute(usage[idx]).numComponents;
               ++as;
            }
         }

         if (numIndices > 0) {
            attrs = new VertexAttribute[as];
            checks = new short[numIndices];
            idx = -1;
            size = -1;

            for(i = 0; i < usage.length; ++i) {
               VertexAttribute a = this.getVertexAttribute(usage[i]);
               if (a != null) {
                  for(int j = 0; j < a.numComponents; ++j) {
                     ++idx;
                     checks[idx] = (short)(a.offset + j);
                  }

                  ++size;
                  attrs[size] = new VertexAttribute(a.usage, a.numComponents, a.alias);
                  newVertexSize += a.numComponents;
               }
            }
         }
      }

      if (checks == null) {
         checks = new short[vertexSize];

         for(short i = 0; i < vertexSize; checks[i] = i++) {
         }

         newVertexSize = vertexSize;
      }

      numIndices = this.getNumIndices();
      short[] indices = null;
      if (numIndices > 0) {
         indices = new short[numIndices];
         this.getIndices(indices);
         if (removeDuplicates || newVertexSize != vertexSize) {
            float[] tmp = new float[vertices.length];
            size = 0;

            for(i = 0; i < numIndices; ++i) {
               int idx1 = indices[i] * vertexSize;
               short newIndex = -1;
               int idx2;
               if (removeDuplicates) {
                  for(short j = 0; j < size && newIndex < 0; ++j) {
                     idx2 = j * newVertexSize;
                     boolean found = true;

                     for(int k = 0; k < checks.length && found; ++k) {
                        if (tmp[idx2 + k] != vertices[idx1 + checks[k]]) {
                           found = false;
                        }
                     }

                     if (found) {
                        newIndex = j;
                     }
                  }
               }

               if (newIndex > 0) {
                  indices[i] = newIndex;
               } else {
                  int idx = size * newVertexSize;

                  for(idx2 = 0; idx2 < checks.length; ++idx2) {
                     tmp[idx + idx2] = vertices[idx1 + checks[idx2]];
                  }

                  indices[i] = (short)size;
                  ++size;
               }
            }

            vertices = tmp;
            numVertices = size;
         }
      }

      Mesh result;
      if (attrs == null) {
         result = new Mesh(isStatic, numVertices, indices == null ? 0 : indices.length, this.getVertexAttributes());
      } else {
         result = new Mesh(isStatic, numVertices, indices == null ? 0 : indices.length, attrs);
      }

      result.setVertices(vertices, 0, numVertices * newVertexSize);
      result.setIndices(indices);
      return result;
   }

   public Mesh copy(boolean isStatic) {
      return this.copy(isStatic, false, (int[])null);
   }

   public static Mesh createFullScreenQuad() {
      float[] verts = new float[20];
      int i = 0;
      int var3 = i + 1;
      verts[i] = -1.0F;
      verts[var3++] = -1.0F;
      verts[var3++] = 0.0F;
      verts[var3++] = 0.0F;
      verts[var3++] = 0.0F;
      verts[var3++] = 1.0F;
      verts[var3++] = -1.0F;
      verts[var3++] = 0.0F;
      verts[var3++] = 1.0F;
      verts[var3++] = 0.0F;
      verts[var3++] = 1.0F;
      verts[var3++] = 1.0F;
      verts[var3++] = 0.0F;
      verts[var3++] = 1.0F;
      verts[var3++] = 1.0F;
      verts[var3++] = -1.0F;
      verts[var3++] = 1.0F;
      verts[var3++] = 0.0F;
      verts[var3++] = 0.0F;
      verts[var3++] = 1.0F;
      Mesh mesh = new Mesh(true, 4, 0, new VertexAttribute[]{new VertexAttribute(1, 3, "a_position"), new VertexAttribute(16, 2, "a_texCoord0")});
      mesh.setVertices(verts);
      return mesh;
   }

   public static enum VertexDataType {
      VertexArray,
      VertexBufferObject,
      VertexBufferObjectSubData;
   }
}
