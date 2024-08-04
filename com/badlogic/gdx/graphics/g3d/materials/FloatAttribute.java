package com.badlogic.gdx.graphics.g3d.materials;

public class FloatAttribute extends Material.Attribute {
   public static final String ShininessAlias = "shininess";
   public static final long Shininess = register("shininess");
   public static final String AlphaTestAlias = "alphaTest";
   public static final long AlphaTest = register("alphaTest");
   public float value;

   public static FloatAttribute createShininess(float value) {
      return new FloatAttribute(Shininess, value);
   }

   public static FloatAttribute createAlphaTest(float value) {
      return new FloatAttribute(AlphaTest, value);
   }

   public FloatAttribute(long type) {
      super(type);
   }

   public FloatAttribute(long type, float value) {
      super(type);
      this.value = value;
   }

   public Material.Attribute copy() {
      return new FloatAttribute(this.type, this.value);
   }

   protected boolean equals(Material.Attribute other) {
      return ((FloatAttribute)other).value == this.value;
   }
}
