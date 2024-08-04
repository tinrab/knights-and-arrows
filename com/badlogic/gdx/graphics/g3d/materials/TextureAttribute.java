package com.badlogic.gdx.graphics.g3d.materials;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class TextureAttribute extends Material.Attribute {
   public static final String DiffuseAlias = "diffuseTexture";
   public static final long Diffuse = register("diffuseTexture");
   public static final String SpecularAlias = "specularTexture";
   public static final long Specular = register("specularTexture");
   public static final String BumpAlias = "bumpTexture";
   public static final long Bump = register("bumpTexture");
   public static final String NormalAlias = "normalTexture";
   public static final long Normal = register("normalTexture");
   protected static long Mask;
   public final TextureDescriptor textureDescription;

   static {
      Mask = Diffuse | Specular | Bump | Normal;
   }

   public static final boolean is(long mask) {
      return (mask & Mask) != 0L;
   }

   public static TextureAttribute createDiffuse(Texture texture) {
      return new TextureAttribute(Diffuse, texture);
   }

   public static TextureAttribute createSpecular(Texture texture) {
      return new TextureAttribute(Specular, texture);
   }

   public TextureAttribute(long type, TextureDescriptor textureDescription) {
      super(type);
      if (!is(type)) {
         throw new GdxRuntimeException("Invalid type specified");
      } else {
         this.textureDescription = textureDescription;
      }
   }

   public TextureAttribute(long type) {
      this(type, new TextureDescriptor());
   }

   public TextureAttribute(long type, Texture texture) {
      this(type, new TextureDescriptor(texture));
   }

   public TextureAttribute(TextureAttribute copyFrom) {
      this(copyFrom.type, copyFrom.textureDescription);
   }

   public Material.Attribute copy() {
      return new TextureAttribute(this);
   }

   protected boolean equals(Material.Attribute other) {
      return ((TextureAttribute)other).textureDescription.equals(this.textureDescription);
   }
}
