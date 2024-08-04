package com.badlogic.gdx.graphics.g3d.loader;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMaterial;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMesh;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMeshPart;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNode;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNodePart;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.FloatArray;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

public class ObjLoader extends ModelLoader<ObjLoader.ObjLoaderParameters> {
   final FloatArray verts;
   final FloatArray norms;
   final FloatArray uvs;
   final ArrayList<ObjLoader.Group> groups;

   public ObjLoader() {
      this((FileHandleResolver)null);
   }

   public ObjLoader(FileHandleResolver resolver) {
      super(resolver);
      this.verts = new FloatArray(300);
      this.norms = new FloatArray(300);
      this.uvs = new FloatArray(200);
      this.groups = new ArrayList(10);
   }

   /** @deprecated */
   public Model loadObj(FileHandle file) {
      return this.loadModel(file);
   }

   /** @deprecated */
   public Model loadObj(FileHandle file, boolean flipV) {
      return this.loadModel(file, flipV);
   }

   public Model loadModel(FileHandle fileHandle, boolean flipV) {
      return this.loadModel(fileHandle, new ObjLoader.ObjLoaderParameters(flipV));
   }

   public ModelData loadModelData(FileHandle file, ObjLoader.ObjLoaderParameters parameters) {
      return this.loadModelData(file, parameters == null ? false : parameters.flipV);
   }

   protected ModelData loadModelData(FileHandle file, boolean flipV) {
      MtlLoader mtl = new MtlLoader();
      ObjLoader.Group activeGroup = new ObjLoader.Group("default");
      this.groups.add(activeGroup);
      BufferedReader reader = new BufferedReader(new InputStreamReader(file.read()), 4096);
      int id = 0;

      int i;
      try {
         String line;
         while((line = reader.readLine()) != null) {
            String[] tokens = line.split("\\s+");
            if (tokens.length < 1) {
               break;
            }

            char firstChar;
            if (tokens[0].length() != 0 && (firstChar = tokens[0].toLowerCase().charAt(0)) != '#') {
               if (firstChar == 'v') {
                  if (tokens[0].length() == 1) {
                     this.verts.add(Float.parseFloat(tokens[1]));
                     this.verts.add(Float.parseFloat(tokens[2]));
                     this.verts.add(Float.parseFloat(tokens[3]));
                  } else if (tokens[0].charAt(1) == 'n') {
                     this.norms.add(Float.parseFloat(tokens[1]));
                     this.norms.add(Float.parseFloat(tokens[2]));
                     this.norms.add(Float.parseFloat(tokens[3]));
                  } else if (tokens[0].charAt(1) == 't') {
                     this.uvs.add(Float.parseFloat(tokens[1]));
                     this.uvs.add(flipV ? 1.0F - Float.parseFloat(tokens[2]) : Float.parseFloat(tokens[2]));
                  }
               } else if (firstChar == 'f') {
                  ArrayList<Integer> faces = activeGroup.faces;

                  for(i = 1; i < tokens.length - 2; --i) {
                     String[] parts = tokens[1].split("/");
                     faces.add(this.getIndex(parts[0], this.verts.size));
                     if (parts.length > 2) {
                        if (i == 1) {
                           activeGroup.hasNorms = true;
                        }

                        faces.add(this.getIndex(parts[2], this.norms.size));
                     }

                     if (parts.length > 1 && parts[1].length() > 0) {
                        if (i == 1) {
                           activeGroup.hasUVs = true;
                        }

                        faces.add(this.getIndex(parts[1], this.uvs.size));
                     }

                     ++i;
                     parts = tokens[i].split("/");
                     faces.add(this.getIndex(parts[0], this.verts.size));
                     if (parts.length > 2) {
                        faces.add(this.getIndex(parts[2], this.norms.size));
                     }

                     if (parts.length > 1 && parts[1].length() > 0) {
                        faces.add(this.getIndex(parts[1], this.uvs.size));
                     }

                     ++i;
                     parts = tokens[i].split("/");
                     faces.add(this.getIndex(parts[0], this.verts.size));
                     if (parts.length > 2) {
                        faces.add(this.getIndex(parts[2], this.norms.size));
                     }

                     if (parts.length > 1 && parts[1].length() > 0) {
                        faces.add(this.getIndex(parts[1], this.uvs.size));
                     }

                     ++activeGroup.numFaces;
                  }
               } else if (firstChar != 'o' && firstChar != 'g') {
                  if (tokens[0].equals("mtllib")) {
                     mtl.load(file.parent().child(tokens[1]));
                  } else if (tokens[0].equals("usemtl")) {
                     if (tokens.length == 1) {
                        activeGroup.materialName = "default";
                     } else {
                        activeGroup.materialName = tokens[1];
                     }
                  }
               } else if (tokens.length > 1) {
                  activeGroup = this.setActiveGroup(tokens[1]);
               } else {
                  activeGroup = this.setActiveGroup("default");
               }
            }
         }

         reader.close();
      } catch (IOException var31) {
         return null;
      }

      int numGroups;
      for(numGroups = 0; numGroups < this.groups.size(); ++numGroups) {
         if (((ObjLoader.Group)this.groups.get(numGroups)).numFaces < 1) {
            this.groups.remove(numGroups);
            --numGroups;
         }
      }

      if (this.groups.size() < 1) {
         return null;
      } else {
         numGroups = this.groups.size();
         ModelData data = new ModelData();

         for(i = 0; i < numGroups; ++i) {
            ObjLoader.Group group = (ObjLoader.Group)this.groups.get(i);
            ArrayList<Integer> faces = group.faces;
            int numElements = faces.size();
            int numFaces = group.numFaces;
            boolean hasNorms = group.hasNorms;
            boolean hasUVs = group.hasUVs;
            float[] finalVerts = new float[numFaces * 3 * (3 + (hasNorms ? 3 : 0) + (hasUVs ? 2 : 0))];
            int numIndices = 0;
            int var21 = 0;

            int i;
            while(numIndices < numElements) {
               i = (Integer)faces.get(numIndices++) * 3;
               finalVerts[var21++] = this.verts.get(i++);
               finalVerts[var21++] = this.verts.get(i++);
               finalVerts[var21++] = this.verts.get(i);
               int uvIndex;
               if (hasNorms) {
                  uvIndex = (Integer)faces.get(numIndices++) * 3;
                  finalVerts[var21++] = this.norms.get(uvIndex++);
                  finalVerts[var21++] = this.norms.get(uvIndex++);
                  finalVerts[var21++] = this.norms.get(uvIndex);
               }

               if (hasUVs) {
                  uvIndex = (Integer)faces.get(numIndices++) * 2;
                  finalVerts[var21++] = this.uvs.get(uvIndex++);
                  finalVerts[var21++] = this.uvs.get(uvIndex);
               }
            }

            numIndices = numFaces * 3 >= 32767 ? 0 : numFaces * 3;
            short[] finalIndices = new short[numIndices];
            if (numIndices > 0) {
               for(i = 0; i < numIndices; ++i) {
                  finalIndices[i] = (short)i;
               }
            }

            ArrayList<VertexAttribute> attributes = new ArrayList();
            attributes.add(new VertexAttribute(1, 3, "a_position"));
            if (hasNorms) {
               attributes.add(new VertexAttribute(8, 3, "a_normal"));
            }

            if (hasUVs) {
               attributes.add(new VertexAttribute(16, 2, "a_texCoord0"));
            }

            StringBuilder var10000 = new StringBuilder("node");
            ++id;
            String nodeId = var10000.append(id).toString();
            String meshId = "mesh" + id;
            String partId = "part" + id;
            ModelNode node = new ModelNode();
            node.id = nodeId;
            node.meshId = meshId;
            node.scale = new Vector3(1.0F, 1.0F, 1.0F);
            node.translation = new Vector3();
            node.rotation = new Quaternion();
            ModelNodePart pm = new ModelNodePart();
            pm.meshPartId = partId;
            pm.materialId = group.materialName;
            node.parts = new ModelNodePart[]{pm};
            ModelMeshPart part = new ModelMeshPart();
            part.id = partId;
            part.indices = finalIndices;
            part.primitiveType = 4;
            ModelMesh mesh = new ModelMesh();
            mesh.id = meshId;
            mesh.attributes = (VertexAttribute[])attributes.toArray(new VertexAttribute[attributes.size()]);
            mesh.vertices = finalVerts;
            mesh.parts = new ModelMeshPart[]{part};
            data.nodes.add(node);
            data.meshes.add(mesh);
            ModelMaterial mm = mtl.getMaterial(group.materialName);
            data.materials.add(mm);
         }

         if (this.verts.size > 0) {
            this.verts.clear();
         }

         if (this.norms.size > 0) {
            this.norms.clear();
         }

         if (this.uvs.size > 0) {
            this.uvs.clear();
         }

         if (this.groups.size() > 0) {
            this.groups.clear();
         }

         return data;
      }
   }

   private ObjLoader.Group setActiveGroup(String name) {
      Iterator var3 = this.groups.iterator();

      ObjLoader.Group group;
      while(var3.hasNext()) {
         group = (ObjLoader.Group)var3.next();
         if (group.name.equals(name)) {
            return group;
         }
      }

      group = new ObjLoader.Group(name);
      this.groups.add(group);
      return group;
   }

   private int getIndex(String index, int size) {
      if (index != null && index.length() != 0) {
         int idx = Integer.parseInt(index);
         return idx < 0 ? size + idx : idx - 1;
      } else {
         return 0;
      }
   }

   private class Group {
      final String name;
      String materialName;
      ArrayList<Integer> faces;
      int numFaces;
      boolean hasNorms;
      boolean hasUVs;
      Material mat;

      Group(String name) {
         this.name = name;
         this.faces = new ArrayList(200);
         this.numFaces = 0;
         this.mat = new Material("");
         this.materialName = "default";
      }
   }

   public static class ObjLoaderParameters extends AssetLoaderParameters<Model> {
      public boolean flipV;

      public ObjLoaderParameters() {
      }

      public ObjLoaderParameters(boolean flipV) {
         this.flipV = flipV;
      }
   }
}
