package com.badlogic.gdx.graphics.g3d.lights;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.util.Iterator;

public class Lights {
   public final Color ambientLight;
   public Color fog;
   public final Array<DirectionalLight> directionalLights;
   public final Array<PointLight> pointLights;

   public Lights() {
      this.ambientLight = new Color(0.0F, 0.0F, 0.0F, 1.0F);
      this.directionalLights = new Array();
      this.pointLights = new Array();
   }

   public Lights(Color ambient) {
      this.ambientLight = new Color(0.0F, 0.0F, 0.0F, 1.0F);
      this.directionalLights = new Array();
      this.pointLights = new Array();
      this.ambientLight.set(ambient);
   }

   public Lights(float ambientRed, float ambientGreen, float ambientBlue) {
      this.ambientLight = new Color(0.0F, 0.0F, 0.0F, 1.0F);
      this.directionalLights = new Array();
      this.pointLights = new Array();
      this.ambientLight.set(ambientRed, ambientGreen, ambientBlue, 1.0F);
   }

   public Lights(Color ambient, BaseLight... lights) {
      this(ambient);
      this.add(lights);
   }

   public Lights clear() {
      this.ambientLight.set(0.0F, 0.0F, 0.0F, 1.0F);
      this.directionalLights.clear();
      this.pointLights.clear();
      return this;
   }

   public Lights add(BaseLight... lights) {
      BaseLight[] var5 = lights;
      int var4 = lights.length;

      for(int var3 = 0; var3 < var4; ++var3) {
         BaseLight light = var5[var3];
         this.add(light);
      }

      return this;
   }

   public Lights add(Array<BaseLight> lights) {
      Iterator var3 = lights.iterator();

      while(var3.hasNext()) {
         BaseLight light = (BaseLight)var3.next();
         this.add(light);
      }

      return this;
   }

   public Lights add(BaseLight light) {
      if (light instanceof DirectionalLight) {
         this.directionalLights.add((DirectionalLight)light);
      } else {
         if (!(light instanceof PointLight)) {
            throw new GdxRuntimeException("Unknown light type");
         }

         this.pointLights.add((PointLight)light);
      }

      return this;
   }
}
