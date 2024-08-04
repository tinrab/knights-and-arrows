package com.badlogic.gdx.graphics.g3d.loader;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMaterial;
import com.badlogic.gdx.graphics.g3d.model.data.ModelTexture;
import com.badlogic.gdx.utils.Array;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

class MtlLoader {
   public ArrayList<ModelMaterial> materials = new ArrayList();

   public void load(FileHandle file) {
      String curMatName = "default";
      Color difcolor = Color.WHITE;
      Color speccolor = Color.WHITE;
      float opacity = 1.0F;
      float shininess = 0.0F;
      String texFilename = null;
      if (file != null && file.exists()) {
         BufferedReader reader = new BufferedReader(new InputStreamReader(file.read()), 4096);

         try {
            String line;
            while((line = reader.readLine()) != null) {
               if (line.length() > 0 && line.charAt(0) == '\t') {
                  line = line.substring(1).trim();
               }

               String[] tokens = line.split("\\s+");
               if (tokens[0].length() != 0 && tokens[0].charAt(0) != '#') {
                  String key = tokens[0].toLowerCase();
                  if (key.equals("newmtl")) {
                     ModelMaterial mat = new ModelMaterial();
                     mat.id = curMatName;
                     mat.diffuse = new Color(difcolor);
                     mat.specular = new Color(speccolor);
                     mat.opacity = opacity;
                     mat.shininess = shininess;
                     if (texFilename != null) {
                        ModelTexture tex = new ModelTexture();
                        tex.usage = 2;
                        tex.fileName = new String(texFilename);
                        if (mat.textures == null) {
                           mat.textures = new Array(1);
                        }

                        mat.textures.add(tex);
                     }

                     this.materials.add(mat);
                     if (tokens.length > 1) {
                        curMatName = tokens[1];
                        curMatName = curMatName.replace('.', '_');
                     } else {
                        curMatName = "default";
                     }

                     difcolor = Color.WHITE;
                     speccolor = Color.WHITE;
                     opacity = 1.0F;
                     shininess = 0.0F;
                  } else if (!key.equals("kd") && !key.equals("ks")) {
                     if (!key.equals("tr") && !key.equals("d")) {
                        if (key.equals("ns")) {
                           shininess = Float.parseFloat(tokens[1]);
                        } else if (key.equals("map_kd")) {
                           texFilename = file.parent().child(tokens[1]).path();
                        }
                     } else {
                        opacity = Float.parseFloat(tokens[1]);
                     }
                  } else {
                     float r = Float.parseFloat(tokens[1]);
                     float g = Float.parseFloat(tokens[2]);
                     float b = Float.parseFloat(tokens[3]);
                     float a = 1.0F;
                     if (tokens.length > 4) {
                        a = Float.parseFloat(tokens[4]);
                     }

                     if (tokens[0].toLowerCase().equals("kd")) {
                        difcolor = new Color();
                        difcolor.set(r, g, b, a);
                     } else {
                        speccolor = new Color();
                        speccolor.set(r, g, b, a);
                     }
                  }
               }
            }

            reader.close();
         } catch (IOException var16) {
            return;
         }

         ModelMaterial mat = new ModelMaterial();
         mat.id = curMatName;
         mat.diffuse = new Color(difcolor);
         mat.specular = new Color(speccolor);
         mat.opacity = opacity;
         mat.shininess = shininess;
         if (texFilename != null) {
            ModelTexture tex = new ModelTexture();
            tex.usage = 2;
            tex.fileName = new String(texFilename);
            if (mat.textures == null) {
               mat.textures = new Array(1);
            }

            mat.textures.add(tex);
         }

         this.materials.add(mat);
      }
   }

   public ModelMaterial getMaterial(String name) {
      Iterator var3 = this.materials.iterator();

      ModelMaterial m;
      while(var3.hasNext()) {
         m = (ModelMaterial)var3.next();
         if (m.id.equals(name)) {
            return m;
         }
      }

      m = new ModelMaterial();
      m.id = name;
      m.diffuse = new Color(Color.WHITE);
      this.materials.add(m);
      return m;
   }
}
