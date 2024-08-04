package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ShortArray;
import java.util.Iterator;

public class MeshBuilder implements MeshPartBuilder {
   private final MeshPartBuilder.VertexInfo vertTmp1 = new MeshPartBuilder.VertexInfo();
   private final MeshPartBuilder.VertexInfo vertTmp2 = new MeshPartBuilder.VertexInfo();
   private final MeshPartBuilder.VertexInfo vertTmp3 = new MeshPartBuilder.VertexInfo();
   private final MeshPartBuilder.VertexInfo vertTmp4 = new MeshPartBuilder.VertexInfo();
   private final MeshPartBuilder.VertexInfo vertTmp5 = new MeshPartBuilder.VertexInfo();
   private final MeshPartBuilder.VertexInfo vertTmp6 = new MeshPartBuilder.VertexInfo();
   private final MeshPartBuilder.VertexInfo vertTmp7 = new MeshPartBuilder.VertexInfo();
   private final MeshPartBuilder.VertexInfo vertTmp8 = new MeshPartBuilder.VertexInfo();
   private final Matrix4 matTmp1 = new Matrix4();
   private final Vector3 tempV1 = new Vector3();
   private final Vector3 tempV2 = new Vector3();
   private final Vector3 tempV3 = new Vector3();
   private final Vector3 tempV4 = new Vector3();
   private final Vector3 tempV5 = new Vector3();
   private final Vector3 tempV6 = new Vector3();
   private final Vector3 tempV7 = new Vector3();
   private final Vector3 tempV8 = new Vector3();
   private VertexAttributes attributes;
   private FloatArray vertices = new FloatArray();
   private ShortArray indices = new ShortArray();
   private int stride;
   private short vindex;
   private int istart;
   private int posOffset;
   private int posSize;
   private int norOffset;
   private int colOffset;
   private int colSize;
   private int cpOffset;
   private int uvOffset;
   private MeshPart part;
   private Array<MeshPart> parts = new Array();
   private final Color color = new Color();
   private boolean colorSet;
   private int primitiveType;
   private float uMin = 0.0F;
   private float uMax = 1.0F;
   private float vMin = 0.0F;
   private float vMax = 1.0F;
   private float[] vertex;
   private static final Pool<Vector3> vectorPool = new Pool<Vector3>() {
      protected Vector3 newObject() {
         return new Vector3();
      }
   };
   private static final Array<Vector3> vectorArray = new Array();

   public static VertexAttributes createAttributes(long usage) {
      Array<VertexAttribute> attrs = new Array();
      if ((usage & 1L) == 1L) {
         attrs.add(new VertexAttribute(1, 3, "a_position"));
      }

      if ((usage & 2L) == 2L) {
         attrs.add(new VertexAttribute(2, 4, "a_color"));
      }

      if ((usage & 8L) == 8L) {
         attrs.add(new VertexAttribute(8, 3, "a_normal"));
      }

      if ((usage & 16L) == 16L) {
         attrs.add(new VertexAttribute(16, 2, "a_texCoord0"));
      }

      VertexAttribute[] attributes = new VertexAttribute[attrs.size];

      for(int i = 0; i < attributes.length; ++i) {
         attributes[i] = (VertexAttribute)attrs.get(i);
      }

      return new VertexAttributes(attributes);
   }

   public void begin(long attributes) {
      this.begin(createAttributes(attributes), 0);
   }

   public void begin(VertexAttributes attributes) {
      this.begin(attributes, 0);
   }

   public void begin(long attributes, int primitiveType) {
      this.begin(createAttributes(attributes), primitiveType);
   }

   public void begin(VertexAttributes attributes, int primitiveType) {
      if (this.attributes != null) {
         throw new RuntimeException("Call end() first");
      } else {
         this.attributes = attributes;
         this.vertices.clear();
         this.indices.clear();
         this.parts.clear();
         this.vindex = 0;
         this.istart = 0;
         this.part = null;
         this.stride = attributes.vertexSize / 4;
         this.vertex = new float[this.stride];
         VertexAttribute a = attributes.findByUsage(1);
         if (a == null) {
            throw new GdxRuntimeException("Cannot build mesh without position attribute");
         } else {
            this.posOffset = a.offset / 4;
            this.posSize = a.numComponents;
            a = attributes.findByUsage(8);
            this.norOffset = a == null ? -1 : a.offset / 4;
            a = attributes.findByUsage(2);
            this.colOffset = a == null ? -1 : a.offset / 4;
            this.colSize = a == null ? 0 : a.numComponents;
            a = attributes.findByUsage(4);
            this.cpOffset = a == null ? -1 : a.offset / 4;
            a = attributes.findByUsage(16);
            this.uvOffset = a == null ? -1 : a.offset / 4;
            this.setColor((Color)null);
            this.primitiveType = primitiveType;
         }
      }
   }

   private void endpart() {
      if (this.part != null) {
         this.part.indexOffset = this.istart;
         this.part.numVertices = this.indices.size - this.istart;
         this.istart = this.indices.size;
         this.part = null;
      }

   }

   public MeshPart part(String id, int primitiveType) {
      if (this.attributes == null) {
         throw new RuntimeException("Call begin() first");
      } else {
         this.endpart();
         this.part = new MeshPart();
         this.part.id = id;
         this.primitiveType = this.part.primitiveType = primitiveType;
         this.parts.add(this.part);
         this.setColor((Color)null);
         return this.part;
      }
   }

   public Mesh end() {
      if (this.attributes == null) {
         throw new RuntimeException("Call begin() first");
      } else {
         this.endpart();
         Mesh mesh = new Mesh(true, this.vertices.size, this.indices.size, this.attributes);
         mesh.setVertices(this.vertices.items, 0, this.vertices.size);
         mesh.setIndices(this.indices.items, 0, this.indices.size);

         MeshPart p;
         for(Iterator var3 = this.parts.iterator(); var3.hasNext(); p.mesh = mesh) {
            p = (MeshPart)var3.next();
         }

         this.parts.clear();
         this.attributes = null;
         this.vertices.clear();
         this.indices.clear();
         return mesh;
      }
   }

   public VertexAttributes getAttributes() {
      return this.attributes;
   }

   public MeshPart getMeshPart() {
      return this.part;
   }

   private Vector3 tmp(float x, float y, float z) {
      Vector3 result = ((Vector3)vectorPool.obtain()).set(x, y, z);
      vectorArray.add(result);
      return result;
   }

   private Vector3 tmp(Vector3 copyFrom) {
      return this.tmp(copyFrom.x, copyFrom.y, copyFrom.z);
   }

   private void cleanup() {
      vectorPool.freeAll(vectorArray);
      vectorArray.clear();
   }

   public void setColor(float r, float g, float b, float a) {
      this.color.set(r, g, b, a);
      this.colorSet = true;
   }

   public void setColor(Color color) {
      if (this.colorSet = color != null) {
         this.color.set(color);
      }

   }

   public short vertex(Vector3 pos, Vector3 nor, Color col, Vector2 uv) {
      if (col == null && this.colorSet) {
         col = this.color;
      }

      if (pos != null) {
         this.vertex[this.posOffset] = pos.x;
         if (this.posSize > 1) {
            this.vertex[this.posOffset + 1] = pos.y;
         }

         if (this.posSize > 2) {
            this.vertex[this.posOffset + 2] = pos.z;
         }
      }

      if (nor != null && this.norOffset >= 0) {
         this.vertex[this.norOffset] = nor.x;
         this.vertex[this.norOffset + 1] = nor.y;
         this.vertex[this.norOffset + 2] = nor.z;
      }

      if (col != null) {
         if (this.colOffset >= 0) {
            this.vertex[this.colOffset] = col.r;
            this.vertex[this.colOffset + 1] = col.g;
            this.vertex[this.colOffset + 2] = col.b;
            if (this.colSize > 3) {
               this.vertex[this.colOffset + 3] = col.a;
            }
         } else if (this.cpOffset > 0) {
            this.vertex[this.cpOffset] = col.toFloatBits();
         }
      }

      if (uv != null && this.uvOffset >= 0) {
         this.vertex[this.uvOffset] = uv.x;
         this.vertex[this.uvOffset + 1] = uv.y;
      }

      this.vertices.addAll(this.vertex);
      short var10002 = this.vindex;
      this.vindex = (short)(var10002 + 1);
      return var10002;
   }

   public short vertex(float[] values) {
      this.vertices.addAll(values);
      this.vindex = (short)(this.vindex + values.length / this.stride);
      return (short)(this.vindex - 1);
   }

   public short vertex(MeshPartBuilder.VertexInfo info) {
      return this.vertex(info.hasPosition ? info.position : null, info.hasNormal ? info.normal : null, info.hasColor ? info.color : null, info.hasUV ? info.uv : null);
   }

   public void index(short value) {
      this.indices.add(value);
   }

   public void index(short value1, short value2) {
      this.indices.ensureCapacity(2);
      this.indices.add(value1);
      this.indices.add(value2);
   }

   public void index(short value1, short value2, short value3) {
      this.indices.ensureCapacity(3);
      this.indices.add(value1);
      this.indices.add(value2);
      this.indices.add(value3);
   }

   public void index(short value1, short value2, short value3, short value4) {
      this.indices.ensureCapacity(4);
      this.indices.add(value1);
      this.indices.add(value2);
      this.indices.add(value3);
      this.indices.add(value4);
   }

   public void index(short value1, short value2, short value3, short value4, short value5, short value6) {
      this.indices.ensureCapacity(6);
      this.indices.add(value1);
      this.indices.add(value2);
      this.indices.add(value3);
      this.indices.add(value4);
      this.indices.add(value5);
      this.indices.add(value6);
   }

   public void index(short value1, short value2, short value3, short value4, short value5, short value6, short value7, short value8) {
      this.indices.ensureCapacity(8);
      this.indices.add(value1);
      this.indices.add(value2);
      this.indices.add(value3);
      this.indices.add(value4);
      this.indices.add(value5);
      this.indices.add(value6);
      this.indices.add(value7);
      this.indices.add(value8);
   }

   public void line(short index1, short index2) {
      if (this.primitiveType != 1) {
         throw new GdxRuntimeException("Incorrect primitive type");
      } else {
         this.index(index1, index2);
      }
   }

   public void line(MeshPartBuilder.VertexInfo p1, MeshPartBuilder.VertexInfo p2) {
      this.line(this.vertex(p1), this.vertex(p2));
   }

   public void line(Vector3 p1, Vector3 p2) {
      this.line(this.vertTmp1.set(p1, (Vector3)null, (Color)null, (Vector2)null), this.vertTmp2.set(p2, (Vector3)null, (Color)null, (Vector2)null));
   }

   public void line(float x1, float y1, float z1, float x2, float y2, float z2) {
      this.line(this.vertTmp1.set((Vector3)null, (Vector3)null, (Color)null, (Vector2)null).setPos(x1, y1, z1), this.vertTmp2.set((Vector3)null, (Vector3)null, (Color)null, (Vector2)null).setPos(x2, y2, z2));
   }

   public void line(Vector3 p1, Color c1, Vector3 p2, Color c2) {
      this.line(this.vertTmp1.set(p1, (Vector3)null, c1, (Vector2)null), this.vertTmp2.set(p2, (Vector3)null, c2, (Vector2)null));
   }

   public void triangle(short index1, short index2, short index3) {
      if (this.primitiveType != 4 && this.primitiveType != 0) {
         if (this.primitiveType != 1) {
            throw new GdxRuntimeException("Incorrect primitive type");
         }

         this.index(index1, index2, index2, index3, index3, index1);
      } else {
         this.index(index1, index2, index3);
      }

   }

   public void triangle(MeshPartBuilder.VertexInfo p1, MeshPartBuilder.VertexInfo p2, MeshPartBuilder.VertexInfo p3) {
      this.triangle(this.vertex(p1), this.vertex(p2), this.vertex(p3));
   }

   public void triangle(Vector3 p1, Vector3 p2, Vector3 p3) {
      this.triangle(this.vertTmp1.set(p1, (Vector3)null, (Color)null, (Vector2)null), this.vertTmp2.set(p2, (Vector3)null, (Color)null, (Vector2)null), this.vertTmp3.set(p3, (Vector3)null, (Color)null, (Vector2)null));
   }

   public void triangle(Vector3 p1, Color c1, Vector3 p2, Color c2, Vector3 p3, Color c3) {
      this.triangle(this.vertTmp1.set(p1, (Vector3)null, c1, (Vector2)null), this.vertTmp2.set(p2, (Vector3)null, c2, (Vector2)null), this.vertTmp3.set(p3, (Vector3)null, c3, (Vector2)null));
   }

   public void rect(short corner00, short corner10, short corner11, short corner01) {
      if (this.primitiveType == 4) {
         this.index(corner00, corner10, corner11, corner11, corner01, corner00);
      } else if (this.primitiveType == 1) {
         this.index(corner00, corner10, corner10, corner11, corner11, corner01, corner01, corner00);
      } else {
         if (this.primitiveType != 0) {
            throw new GdxRuntimeException("Incorrect primitive type");
         }

         this.index(corner00, corner10, corner11, corner01);
      }

   }

   public void rect(MeshPartBuilder.VertexInfo corner00, MeshPartBuilder.VertexInfo corner10, MeshPartBuilder.VertexInfo corner11, MeshPartBuilder.VertexInfo corner01) {
      this.rect(this.vertex(corner00), this.vertex(corner10), this.vertex(corner11), this.vertex(corner01));
   }

   public void rect(Vector3 corner00, Vector3 corner10, Vector3 corner11, Vector3 corner01, Vector3 normal) {
      this.rect(this.vertTmp1.set(corner00, normal, (Color)null, (Vector2)null).setUV(this.uMin, this.vMin), this.vertTmp2.set(corner10, normal, (Color)null, (Vector2)null).setUV(this.uMax, this.vMin), this.vertTmp3.set(corner11, normal, (Color)null, (Vector2)null).setUV(this.uMax, this.vMax), this.vertTmp4.set(corner01, normal, (Color)null, (Vector2)null).setUV(this.uMin, this.vMax));
   }

   public void rect(float x00, float y00, float z00, float x10, float y10, float z10, float x11, float y11, float z11, float x01, float y01, float z01, float normalX, float normalY, float normalZ) {
      this.rect(this.vertTmp1.set((Vector3)null, (Vector3)null, (Color)null, (Vector2)null).setPos(x00, y00, z00).setNor(normalX, normalY, normalZ).setUV(this.uMin, this.vMin), this.vertTmp2.set((Vector3)null, (Vector3)null, (Color)null, (Vector2)null).setPos(x10, y10, z10).setNor(normalX, normalY, normalZ).setUV(this.uMax, this.vMin), this.vertTmp3.set((Vector3)null, (Vector3)null, (Color)null, (Vector2)null).setPos(x11, y11, z11).setNor(normalX, normalY, normalZ).setUV(this.uMax, this.vMax), this.vertTmp4.set((Vector3)null, (Vector3)null, (Color)null, (Vector2)null).setPos(x01, y01, z01).setNor(normalX, normalY, normalZ).setUV(this.uMin, this.vMax));
   }

   public void box(MeshPartBuilder.VertexInfo corner000, MeshPartBuilder.VertexInfo corner010, MeshPartBuilder.VertexInfo corner100, MeshPartBuilder.VertexInfo corner110, MeshPartBuilder.VertexInfo corner001, MeshPartBuilder.VertexInfo corner011, MeshPartBuilder.VertexInfo corner101, MeshPartBuilder.VertexInfo corner111) {
      short i000 = this.vertex(corner000);
      short i100 = this.vertex(corner100);
      short i110 = this.vertex(corner110);
      short i010 = this.vertex(corner010);
      short i001 = this.vertex(corner001);
      short i101 = this.vertex(corner101);
      short i111 = this.vertex(corner111);
      short i011 = this.vertex(corner011);
      this.rect(i000, i100, i110, i010);
      this.rect(i101, i001, i011, i111);
      if (this.primitiveType == 1) {
         this.index(i000, i001, i010, i011, i110, i111, i100, i101);
      } else if (this.primitiveType == 4) {
         this.index(i001, i000, i010, i010, i011, i001);
         this.index(i100, i101, i111, i111, i110, i100);
         this.index(i001, i101, i100, i100, i000, i001);
         this.index(i010, i110, i111, i111, i011, i010);
      } else if (this.primitiveType != 0) {
         throw new GdxRuntimeException("Incorrect primitive type");
      }

   }

   public void box(Vector3 corner000, Vector3 corner010, Vector3 corner100, Vector3 corner110, Vector3 corner001, Vector3 corner011, Vector3 corner101, Vector3 corner111) {
      if (this.norOffset < 0) {
         this.box(this.vertTmp1.set(corner000, (Vector3)null, (Color)null, (Vector2)null), this.vertTmp2.set(corner010, (Vector3)null, (Color)null, (Vector2)null), this.vertTmp3.set(corner100, (Vector3)null, (Color)null, (Vector2)null), this.vertTmp4.set(corner110, (Vector3)null, (Color)null, (Vector2)null), this.vertTmp5.set(corner001, (Vector3)null, (Color)null, (Vector2)null), this.vertTmp6.set(corner011, (Vector3)null, (Color)null, (Vector2)null), this.vertTmp7.set(corner101, (Vector3)null, (Color)null, (Vector2)null), this.vertTmp8.set(corner111, (Vector3)null, (Color)null, (Vector2)null));
      } else {
         Vector3 nor = this.tempV1.set(corner000).lerp(corner110, 0.5F).sub(this.tempV2.set(corner001).lerp(corner111, 0.5F)).nor();
         this.rect(corner000, corner010, corner110, corner100, nor);
         this.rect(corner011, corner001, corner101, corner111, nor.scl(-1.0F));
         nor = this.tempV1.set(corner000).lerp(corner101, 0.5F).sub(this.tempV2.set(corner010).lerp(corner111, 0.5F)).nor();
         this.rect(corner001, corner000, corner100, corner101, nor);
         this.rect(corner010, corner011, corner111, corner110, nor.scl(-1.0F));
         nor = this.tempV1.set(corner000).lerp(corner011, 0.5F).sub(this.tempV2.set(corner100).lerp(corner111, 0.5F)).nor();
         this.rect(corner001, corner011, corner010, corner000, nor);
         this.rect(corner100, corner110, corner111, corner101, nor.scl(-1.0F));
      }

   }

   public void box(Matrix4 transform) {
      this.box(this.tmp(-0.5F, -0.5F, -0.5F).mul(transform), this.tmp(-0.5F, 0.5F, -0.5F).mul(transform), this.tmp(0.5F, -0.5F, -0.5F).mul(transform), this.tmp(0.5F, 0.5F, -0.5F).mul(transform), this.tmp(-0.5F, -0.5F, 0.5F).mul(transform), this.tmp(-0.5F, 0.5F, 0.5F).mul(transform), this.tmp(0.5F, -0.5F, 0.5F).mul(transform), this.tmp(0.5F, 0.5F, 0.5F).mul(transform));
      this.cleanup();
   }

   public void box(float width, float height, float depth) {
      this.box(this.matTmp1.setToScaling(width, height, depth));
   }

   public void box(float x, float y, float z, float width, float height, float depth) {
      this.box(this.matTmp1.setToScaling(width, height, depth).trn(x, y, z));
   }

   public void cylinder(float width, float height, float depth, int divisions) {
      float hw = width * 0.5F;
      float hh = height * 0.5F;
      float hd = depth * 0.5F;
      float step = 6.2831855F / (float)divisions;
      float us = 1.0F / (float)divisions;
      float u = 0.0F;
      float angle = 0.0F;
      MeshPartBuilder.VertexInfo curr1 = this.vertTmp3.set((Vector3)null, (Vector3)null, (Color)null, (Vector2)null);
      curr1.hasUV = curr1.hasPosition = curr1.hasNormal = true;
      MeshPartBuilder.VertexInfo curr2 = this.vertTmp4.set((Vector3)null, (Vector3)null, (Color)null, (Vector2)null);
      curr2.hasUV = curr2.hasPosition = curr2.hasNormal = true;

      for(int i = 0; i <= divisions; ++i) {
         angle = step * (float)i;
         u = 1.0F - us * (float)i;
         curr1.position.set(MathUtils.cos(angle) * hw, 0.0F, MathUtils.sin(angle) * hd);
         curr1.normal.set(curr1.position).nor();
         curr1.position.y = -hh;
         curr1.uv.set(u, 1.0F);
         curr2.position.set(curr1.position);
         curr2.normal.set(curr1.normal);
         curr2.position.y = hh;
         curr2.uv.set(u, 0.0F);
         this.vertex(curr1);
         this.vertex(curr2);
         if (i != 0) {
            this.rect((short)(this.vindex - 3), (short)(this.vindex - 1), (short)(this.vindex - 2), (short)(this.vindex - 4));
         }
      }

   }

   public void cone(float width, float height, float depth, int divisions) {
      float hw = width * 0.5F;
      float hh = height * 0.5F;
      float hd = depth * 0.5F;
      float step = 6.2831855F / (float)divisions;
      float us = 1.0F / (float)divisions;
      float u = 0.0F;
      float angle = 0.0F;
      MeshPartBuilder.VertexInfo curr1 = this.vertTmp3.set((Vector3)null, (Vector3)null, (Color)null, (Vector2)null);
      curr1.hasUV = curr1.hasPosition = curr1.hasNormal = true;
      MeshPartBuilder.VertexInfo curr2 = this.vertTmp4.set((Vector3)null, (Vector3)null, (Color)null, (Vector2)null).setPos(0.0F, hh, 0.0F).setNor(0.0F, 1.0F, 0.0F).setUV(0.5F, 0.0F);
      int base = this.vertex(curr2);

      for(int i = 0; i <= divisions; ++i) {
         angle = step * (float)i;
         u = 1.0F - us * (float)i;
         curr1.position.set(MathUtils.cos(angle) * hw, 0.0F, MathUtils.sin(angle) * hd);
         curr1.normal.set(curr1.position).nor();
         curr1.position.y = -hh;
         curr1.uv.set(u, 1.0F);
         this.vertex(curr1);
         if (i != 0) {
            this.triangle((short)base, (short)(this.vindex - 1), (short)(this.vindex - 2));
         }
      }

   }

   public void sphere(float width, float height, float depth, int divisionsU, int divisionsV) {
      float hw = width * 0.5F;
      float hh = height * 0.5F;
      float hd = depth * 0.5F;
      float stepU = 6.2831855F / (float)divisionsU;
      float stepV = 3.1415927F / (float)divisionsV;
      float us = 1.0F / (float)divisionsU;
      float vs = 1.0F / (float)divisionsV;
      float u = 0.0F;
      float v = 0.0F;
      float angleU = 0.0F;
      float angleV = 0.0F;
      MeshPartBuilder.VertexInfo curr1 = this.vertTmp3.set((Vector3)null, (Vector3)null, (Color)null, (Vector2)null);
      curr1.hasUV = curr1.hasPosition = curr1.hasNormal = true;

      for(int i = 0; i <= divisionsU; ++i) {
         angleU = stepU * (float)i;
         u = 1.0F - us * (float)i;
         this.tempV1.set(MathUtils.cos(angleU) * hw, 0.0F, MathUtils.sin(angleU) * hd);

         for(int j = 0; j <= divisionsV; ++j) {
            angleV = stepV * (float)j;
            v = vs * (float)j;
            float t = MathUtils.sin(angleV);
            curr1.position.set(this.tempV1.x * t, MathUtils.cos(angleV) * hh, this.tempV1.z * t);
            curr1.normal.set(curr1.position).nor();
            curr1.uv.set(u, v);
            this.vertex(curr1);
            if (i != 0 && j != 0) {
               this.index((short)(this.vindex - 2), (short)(this.vindex - 1), (short)(this.vindex - (divisionsV + 2)), (short)(this.vindex - 1), (short)(this.vindex - (divisionsV + 1)), (short)(this.vindex - (divisionsV + 2)));
            }
         }
      }

   }
}
