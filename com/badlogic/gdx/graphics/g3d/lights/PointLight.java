package com.badlogic.gdx.graphics.g3d.lights;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class PointLight extends BaseLight {
   public final Vector3 position = new Vector3();
   public float intensity;

   public PointLight set(PointLight copyFrom) {
      return this.set(copyFrom.color, copyFrom.position, copyFrom.intensity);
   }

   public PointLight set(Color color, Vector3 position, float intensity) {
      if (color != null) {
         this.color.set(color);
      }

      if (position != null) {
         this.position.set(position);
      }

      this.intensity = intensity;
      return this;
   }

   public PointLight set(float r, float g, float b, Vector3 position, float intensity) {
      this.color.set(r, g, b, 1.0F);
      if (position != null) {
         this.position.set(position);
      }

      this.intensity = intensity;
      return this;
   }

   public PointLight set(Color color, float x, float y, float z, float intensity) {
      if (color != null) {
         this.color.set(color);
      }

      this.position.set(x, y, z);
      this.intensity = intensity;
      return this;
   }

   public PointLight set(float r, float g, float b, float x, float y, float z, float intensity) {
      this.color.set(r, g, b, 1.0F);
      this.position.set(x, y, z);
      this.intensity = intensity;
      return this;
   }

   public boolean equals(Object obj) {
      return obj instanceof PointLight ? this.equals((PointLight)obj) : false;
   }

   public boolean equals(PointLight other) {
      return other != null && (other == this || this.color.equals(other.color) && this.position.equals(other.position) && this.intensity == other.intensity);
   }
}
