package com.badlogic.gdx.graphics;

public final class VertexAttribute {
   public final int usage;
   public final int numComponents;
   public int offset;
   public String alias;
   public int unit;

   public VertexAttribute(int usage, int numComponents, String alias) {
      this(usage, numComponents, alias, 0);
   }

   public VertexAttribute(int usage, int numComponents, String alias, int index) {
      this.usage = usage;
      this.numComponents = numComponents;
      this.alias = alias;
      this.unit = index;
   }

   public static VertexAttribute Position() {
      return new VertexAttribute(1, 3, "a_position");
   }

   public static VertexAttribute TexCoords(int unit) {
      return new VertexAttribute(16, 2, "a_texCoord" + unit, unit);
   }

   public static VertexAttribute Normal() {
      return new VertexAttribute(8, 3, "a_normal");
   }

   public static VertexAttribute Color() {
      return new VertexAttribute(4, 4, "a_color");
   }

   public static VertexAttribute ColorUnpacked() {
      return new VertexAttribute(2, 4, "a_color");
   }

   public static VertexAttribute Tangent() {
      return new VertexAttribute(128, 3, "a_tangent");
   }

   public static VertexAttribute Binormal() {
      return new VertexAttribute(256, 3, "a_binormal");
   }

   public static VertexAttribute BoneWeight(int unit) {
      return new VertexAttribute(64, 2, "a_boneWeight" + unit, unit);
   }

   public boolean equals(Object obj) {
      return !(obj instanceof VertexAttribute) ? false : this.equals((VertexAttribute)obj);
   }

   public boolean equals(VertexAttribute other) {
      return other != null && this.usage == other.usage && this.numComponents == other.numComponents && this.alias.equals(other.alias) && this.unit == other.unit;
   }
}
