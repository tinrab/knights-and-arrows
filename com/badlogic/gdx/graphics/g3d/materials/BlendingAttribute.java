package com.badlogic.gdx.graphics.g3d.materials;

public class BlendingAttribute extends Material.Attribute {
   public static final String Alias = "blended";
   public static final long Type = register("blended");
   public int sourceFunction;
   public int destFunction;
   public float opacity;

   public static final boolean is(long mask) {
      return (mask & Type) == mask;
   }

   public BlendingAttribute() {
      this((BlendingAttribute)null);
   }

   public BlendingAttribute(int sourceFunc, int destFunc, float opacity) {
      super(Type);
      this.opacity = 1.0F;
      this.sourceFunction = sourceFunc;
      this.destFunction = destFunc;
      this.opacity = opacity;
   }

   public BlendingAttribute(int sourceFunc, int destFunc) {
      this(sourceFunc, destFunc, 1.0F);
   }

   public BlendingAttribute(BlendingAttribute copyFrom) {
      this(copyFrom == null ? 770 : copyFrom.sourceFunction, copyFrom == null ? 771 : copyFrom.destFunction, copyFrom == null ? 1.0F : copyFrom.opacity);
   }

   public BlendingAttribute copy() {
      return new BlendingAttribute(this);
   }

   protected boolean equals(Material.Attribute other) {
      return ((BlendingAttribute)other).sourceFunction == this.sourceFunction && ((BlendingAttribute)other).destFunction == this.destFunction;
   }
}
