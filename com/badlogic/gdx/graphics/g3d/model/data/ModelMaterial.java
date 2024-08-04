package com.badlogic.gdx.graphics.g3d.model.data;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

public class ModelMaterial {
   public String id;
   public ModelMaterial.MaterialType type;
   public Color ambient;
   public Color diffuse;
   public Color specular;
   public Color emissive;
   public float shininess;
   public float opacity = 1.0F;
   public Array<ModelTexture> textures;

   public static enum MaterialType {
      Lambert,
      Phong;
   }
}
