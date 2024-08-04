package com.badlogic.gdx.graphics.g3d.materials;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class ColorAttribute extends Material.Attribute {
   public static final String DiffuseAlias = "diffuseColor";
   public static final long Diffuse = register("diffuseColor");
   public static final String SpecularAlias = "specularColor";
   public static final long Specular = register("specularColor");
   public static final String AmbientAlias = "ambientColor";
   public static final long Ambient = register("ambientColor");
   public static final String EmissiveAlias = "emissiveColor";
   public static final long Emissive = register("emissiveColor");
   protected static long Mask;
   public final Color color;

   static {
      Mask = Ambient | Diffuse | Specular | Emissive;
   }

   public static final boolean is(long mask) {
      return (mask & Mask) != 0L;
   }

   public static final ColorAttribute createDiffuse(Color color) {
      return new ColorAttribute(Diffuse, color);
   }

   public static final ColorAttribute createDiffuse(float r, float g, float b, float a) {
      return new ColorAttribute(Diffuse, r, g, b, a);
   }

   public static final ColorAttribute createSpecular(Color color) {
      return new ColorAttribute(Specular, color);
   }

   public static final ColorAttribute createSpecular(float r, float g, float b, float a) {
      return new ColorAttribute(Specular, r, g, b, a);
   }

   public ColorAttribute(long type) {
      super(type);
      this.color = new Color();
      if (!is(type)) {
         throw new GdxRuntimeException("Invalid type specified");
      }
   }

   public ColorAttribute(long type, Color color) {
      this(type);
      if (color != null) {
         this.color.set(color);
      }

   }

   public ColorAttribute(long type, float r, float g, float b, float a) {
      this(type);
      this.color.set(r, g, b, a);
   }

   public ColorAttribute(ColorAttribute copyFrom) {
      this(copyFrom.type, copyFrom.color);
   }

   public Material.Attribute copy() {
      return new ColorAttribute(this);
   }

   protected boolean equals(Material.Attribute other) {
      return ((ColorAttribute)other).color.equals(this.color);
   }
}
