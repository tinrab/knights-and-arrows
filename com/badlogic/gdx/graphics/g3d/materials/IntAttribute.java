package com.badlogic.gdx.graphics.g3d.materials;

public class IntAttribute extends Material.Attribute {
   public static final String CullFaceAlias = "cullface";
   public static final long CullFace = register("cullface");
   public int value;

   public static IntAttribute createCullFace(int value) {
      return new IntAttribute(CullFace, value);
   }

   public IntAttribute(long type) {
      super(type);
   }

   public IntAttribute(long type, int value) {
      super(type);
      this.value = value;
   }

   public Material.Attribute copy() {
      return new IntAttribute(this.type, this.value);
   }

   protected boolean equals(Material.Attribute other) {
      return ((IntAttribute)other).value == this.value;
   }
}
