package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PolygonRegion {
   private float[] texCoords;
   private float[] localVertices;
   private final TextureRegion region;

   public PolygonRegion(TextureRegion region, FileHandle file) {
      this.region = region;
      if (file == null) {
         throw new IllegalArgumentException("region cannot be null.");
      } else {
         this.loadPolygonDefinition(file);
      }
   }

   public PolygonRegion(TextureRegion region, float[] vertices) {
      this.region = region;
      EarClippingTriangulator ect = new EarClippingTriangulator();
      List<Vector2> polygonVectors = new ArrayList();

      for(int i = 0; i < vertices.length; i += 2) {
         polygonVectors.add(new Vector2(vertices[i], vertices[i + 1]));
      }

      List<Vector2> triangulatedVectors = ect.computeTriangles(polygonVectors);
      this.localVertices = new float[triangulatedVectors.size() * 2];
      this.texCoords = new float[triangulatedVectors.size() * 2];
      float uvWidth = region.u2 - region.u;
      float uvHeight = region.v2 - region.v;

      for(int i = 0; i < triangulatedVectors.size(); ++i) {
         this.localVertices[i * 2] = ((Vector2)triangulatedVectors.get(i)).x;
         this.localVertices[i * 2 + 1] = ((Vector2)triangulatedVectors.get(i)).y;
         this.texCoords[i * 2] = region.getU() + uvWidth * (this.localVertices[i * 2] / (float)region.getRegionWidth());
         this.texCoords[i * 2 + 1] = region.getV() + uvHeight * (1.0F - this.localVertices[i * 2 + 1] / (float)region.getRegionHeight());
      }

   }

   private void loadPolygonDefinition(FileHandle file) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(file.read()), 64);

      try {
         while(true) {
            String line = reader.readLine();
            if (line == null) {
               return;
            }

            String[] texCoords;
            if (line.startsWith("v")) {
               texCoords = line.substring(1).trim().split(",");
               this.localVertices = new float[texCoords.length];

               for(int i = 0; i < texCoords.length; i += 2) {
                  this.localVertices[i] = Float.parseFloat(texCoords[i]);
                  this.localVertices[i + 1] = Float.parseFloat(texCoords[i + 1]);
               }
            } else if (line.startsWith("u")) {
               texCoords = line.substring(1).trim().split(",");
               float[] localTexCoords = new float[texCoords.length];

               for(int i = 0; i < texCoords.length; i += 2) {
                  localTexCoords[i] = Float.parseFloat(texCoords[i]);
                  localTexCoords[i + 1] = Float.parseFloat(texCoords[i + 1]);
               }

               this.texCoords = this.calculateAtlasTexCoords(localTexCoords);
            }
         }
      } catch (IOException var14) {
         throw new GdxRuntimeException("Error reading polygon shape file: " + file);
      } finally {
         try {
            reader.close();
         } catch (IOException var13) {
         }

      }
   }

   private float[] calculateAtlasTexCoords(float[] localTexCoords) {
      float uvWidth = this.region.u2 - this.region.u;
      float uvHeight = this.region.v2 - this.region.v;

      for(int i = 0; i < localTexCoords.length; i += 2) {
         localTexCoords[i] = this.region.u + localTexCoords[i] * uvWidth;
         localTexCoords[i + 1] = this.region.v + localTexCoords[i + 1] * uvHeight;
      }

      return localTexCoords;
   }

   public float[] getLocalVertices() {
      return this.localVertices;
   }

   public float[] getTextureCoords() {
      return this.texCoords;
   }

   public TextureRegion getRegion() {
      return this.region;
   }
}
