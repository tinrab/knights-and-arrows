package com.badlogic.gdx.graphics;

public final class VertexAttributes {
   private final VertexAttribute[] attributes;
   public final int vertexSize;
   private long mask = -1L;

   public VertexAttributes(VertexAttribute... attributes) {
      if (attributes.length == 0) {
         throw new IllegalArgumentException("attributes must be >= 1");
      } else {
         VertexAttribute[] list = new VertexAttribute[attributes.length];

         for(int i = 0; i < attributes.length; ++i) {
            list[i] = attributes[i];
         }

         this.attributes = list;
         this.checkValidity();
         this.vertexSize = this.calculateOffsets();
      }
   }

   public int getOffset(int usage) {
      VertexAttribute vertexAttribute = this.findByUsage(usage);
      return vertexAttribute == null ? 0 : vertexAttribute.offset / 4;
   }

   public VertexAttribute findByUsage(int usage) {
      int len = this.size();

      for(int i = 0; i < len; ++i) {
         if (this.get(i).usage == usage) {
            return this.get(i);
         }
      }

      return null;
   }

   private int calculateOffsets() {
      int count = 0;

      for(int i = 0; i < this.attributes.length; ++i) {
         VertexAttribute attribute = this.attributes[i];
         attribute.offset = count;
         if (attribute.usage == 4) {
            count += 4;
         } else {
            count += 4 * attribute.numComponents;
         }
      }

      return count;
   }

   private void checkValidity() {
      boolean pos = false;
      boolean cols = false;
      boolean nors = false;

      for(int i = 0; i < this.attributes.length; ++i) {
         VertexAttribute attribute = this.attributes[i];
         if (attribute.usage == 1) {
            if (pos) {
               throw new IllegalArgumentException("two position attributes were specified");
            }

            pos = true;
         }

         if (attribute.usage == 8 && nors) {
            throw new IllegalArgumentException("two normal attributes were specified");
         }

         if (attribute.usage == 2 || attribute.usage == 4) {
            if (attribute.numComponents != 4) {
               throw new IllegalArgumentException("color attribute must have 4 components");
            }

            if (cols) {
               throw new IllegalArgumentException("two color attributes were specified");
            }

            cols = true;
         }
      }

      if (!pos) {
         throw new IllegalArgumentException("no position attribute was specified");
      }
   }

   public int size() {
      return this.attributes.length;
   }

   public VertexAttribute get(int index) {
      return this.attributes[index];
   }

   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("[");

      for(int i = 0; i < this.attributes.length; ++i) {
         builder.append("(");
         builder.append(this.attributes[i].alias);
         builder.append(", ");
         builder.append(this.attributes[i].usage);
         builder.append(", ");
         builder.append(this.attributes[i].numComponents);
         builder.append(", ");
         builder.append(this.attributes[i].offset);
         builder.append(")");
         builder.append("\n");
      }

      builder.append("]");
      return builder.toString();
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof VertexAttributes)) {
         return false;
      } else {
         VertexAttributes other = (VertexAttributes)obj;
         if (this.attributes.length != other.size()) {
            return false;
         } else {
            for(int i = 0; i < this.attributes.length; ++i) {
               if (!this.attributes[i].equals(other.attributes[i])) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public long getMask() {
      if (this.mask == -1L) {
         long result = 0L;

         for(int i = 0; i < this.attributes.length; ++i) {
            result |= (long)this.attributes[i].usage;
         }

         this.mask = result;
      }

      return this.mask;
   }

   public static final class Usage {
      public static final int Position = 1;
      public static final int Color = 2;
      public static final int ColorPacked = 4;
      public static final int Normal = 8;
      public static final int TextureCoordinates = 16;
      public static final int Generic = 32;
      public static final int BoneWeight = 64;
      public static final int Tangent = 128;
      public static final int BiNormal = 256;
   }
}
