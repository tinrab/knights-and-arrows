package com.badlogic.gdx.graphics.g3d.loader;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.model.data.ModelAnimation;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMaterial;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMesh;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMeshPart;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNode;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNodeAnimation;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNodeKeyframe;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNodePart;
import com.badlogic.gdx.graphics.g3d.model.data.ModelTexture;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.BaseJsonReader;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonValue;
import java.util.Iterator;

public class G3dModelLoader extends ModelLoader<AssetLoaderParameters<Model>> {
   public static final short VERSION_HI = 0;
   public static final short VERSION_LO = 1;
   protected final BaseJsonReader reader;
   private final Quaternion tempQ;

   public G3dModelLoader(BaseJsonReader reader) {
      this(reader, (FileHandleResolver)null);
   }

   public G3dModelLoader(BaseJsonReader reader, FileHandleResolver resolver) {
      super(resolver);
      this.tempQ = new Quaternion();
      this.reader = reader;
   }

   public ModelData loadModelData(FileHandle fileHandle, AssetLoaderParameters<Model> parameters) {
      return this.parseModel(fileHandle);
   }

   public ModelData parseModel(FileHandle handle) {
      JsonValue json = this.reader.parse(handle);
      ModelData model = new ModelData();
      JsonValue version = json.require("version");
      model.version[0] = (short)version.getInt(0);
      model.version[1] = (short)version.getInt(1);
      if (model.version[0] == 0 && model.version[1] == 1) {
         model.id = json.getString("id", "");
         this.parseMeshes(model, json);
         this.parseMaterials(model, json, handle.parent().path());
         this.parseNodes(model, json);
         this.parseAnimations(model, json);
         return model;
      } else {
         throw new GdxRuntimeException("Model version not supported");
      }
   }

   private void parseMeshes(ModelData model, JsonValue json) {
      JsonValue meshes = json.require("meshes");
      model.meshes.ensureCapacity(meshes.size());

      for(JsonValue mesh = meshes.child(); mesh != null; mesh = mesh.next()) {
         ModelMesh jsonMesh = new ModelMesh();
         String id = mesh.getString("id", "");
         jsonMesh.id = id;
         JsonValue attributes = mesh.require("attributes");
         jsonMesh.attributes = this.parseAttributes(attributes);
         JsonValue vertices = mesh.require("vertices");
         float[] verts = new float[vertices.size()];
         int j = 0;

         JsonValue meshParts;
         for(meshParts = vertices.child(); meshParts != null; ++j) {
            verts[j] = meshParts.asFloat();
            meshParts = meshParts.next();
         }

         jsonMesh.vertices = verts;
         meshParts = mesh.require("parts");
         Array<ModelMeshPart> parts = new Array();

         for(JsonValue meshPart = meshParts.child(); meshPart != null; meshPart = meshPart.next()) {
            ModelMeshPart jsonPart = new ModelMeshPart();
            String partId = meshPart.getString("id", (String)null);
            if (id == null) {
               throw new GdxRuntimeException("Not id given for mesh part");
            }

            Iterator var17 = parts.iterator();

            while(var17.hasNext()) {
               ModelMeshPart other = (ModelMeshPart)var17.next();
               if (other.id.equals(partId)) {
                  throw new GdxRuntimeException("Mesh part with id '" + partId + "' already in defined");
               }
            }

            jsonPart.id = partId;
            String type = meshPart.getString("type", (String)null);
            if (type == null) {
               throw new GdxRuntimeException("No primitive type given for mesh part '" + partId + "'");
            }

            jsonPart.primitiveType = this.parseType(type);
            JsonValue indices = meshPart.require("indices");
            short[] partIndices = new short[indices.size()];
            int k = 0;

            for(JsonValue value = indices.child(); value != null; ++k) {
               partIndices[k] = (short)value.asInt();
               value = value.next();
            }

            jsonPart.indices = partIndices;
            parts.add(jsonPart);
         }

         jsonMesh.parts = (ModelMeshPart[])parts.toArray(ModelMeshPart.class);
         model.meshes.add(jsonMesh);
      }

   }

   private int parseType(String type) {
      if (type.equals("TRIANGLES")) {
         return 4;
      } else if (type.equals("LINES")) {
         return 1;
      } else if (type.equals("POINTS")) {
         return 0;
      } else if (type.equals("TRIANGLE_STRIP")) {
         return 5;
      } else if (type.equals("LINE_STRIP")) {
         return 3;
      } else {
         throw new GdxRuntimeException("Unknown primitive type '" + type + "', should be one of triangle, trianglestrip, line, linestrip, lineloop or point");
      }
   }

   private VertexAttribute[] parseAttributes(JsonValue attributes) {
      Array<VertexAttribute> vertexAttributes = new Array();
      int unit = 0;
      int blendWeightCount = 0;

      for(JsonValue value = attributes.child(); value != null; value = value.next()) {
         String attribute = value.asString();
         if (attribute.equals("POSITION")) {
            vertexAttributes.add(VertexAttribute.Position());
         } else if (attribute.equals("NORMAL")) {
            vertexAttributes.add(VertexAttribute.Normal());
         } else if (attribute.equals("COLOR")) {
            vertexAttributes.add(VertexAttribute.ColorUnpacked());
         } else if (attribute.equals("COLORPACKED")) {
            vertexAttributes.add(VertexAttribute.Color());
         } else if (attribute.equals("TANGENT")) {
            vertexAttributes.add(VertexAttribute.Tangent());
         } else if (attribute.equals("BINORMAL")) {
            vertexAttributes.add(VertexAttribute.Binormal());
         } else if (attribute.startsWith("TEXCOORD")) {
            vertexAttributes.add(VertexAttribute.TexCoords(unit++));
         } else {
            if (!attribute.startsWith("BLENDWEIGHT")) {
               throw new GdxRuntimeException("Unknown vertex attribute '" + attribute + "', should be one of position, normal, uv, tangent or binormal");
            }

            vertexAttributes.add(VertexAttribute.BoneWeight(blendWeightCount++));
         }
      }

      return (VertexAttribute[])vertexAttributes.toArray(VertexAttribute.class);
   }

   private void parseMaterials(ModelData model, JsonValue json, String materialDir) {
      JsonValue materials = json.get("materials");
      if (materials != null) {
         model.materials.ensureCapacity(materials.size());

         for(JsonValue material = materials.child(); material != null; material = material.next()) {
            ModelMaterial jsonMaterial = new ModelMaterial();
            String id = material.getString("id", (String)null);
            if (id == null) {
               throw new GdxRuntimeException("Material needs an id.");
            }

            jsonMaterial.id = id;
            JsonValue diffuse = material.get("diffuse");
            if (diffuse != null) {
               jsonMaterial.diffuse = this.parseColor(diffuse);
            }

            JsonValue ambient = material.get("ambient");
            if (ambient != null) {
               jsonMaterial.ambient = this.parseColor(ambient);
            }

            JsonValue emissive = material.get("emissive");
            if (emissive != null) {
               jsonMaterial.emissive = this.parseColor(emissive);
            }

            JsonValue specular = material.get("specular");
            if (specular != null) {
               jsonMaterial.specular = this.parseColor(specular);
            }

            jsonMaterial.shininess = material.getFloat("shininess", 0.0F);
            jsonMaterial.opacity = material.getFloat("opacity", 1.0F);
            JsonValue textures = material.get("textures");
            if (textures != null) {
               for(JsonValue texture = textures.child(); texture != null; texture = texture.next()) {
                  ModelTexture jsonTexture = new ModelTexture();
                  String textureId = texture.getString("id", (String)null);
                  if (textureId == null) {
                     throw new GdxRuntimeException("Texture has no id.");
                  }

                  jsonTexture.id = textureId;
                  String fileName = texture.getString("filename", (String)null);
                  if (fileName == null) {
                     throw new GdxRuntimeException("Texture needs filename.");
                  }

                  jsonTexture.fileName = materialDir + (materialDir.length() != 0 && !materialDir.endsWith("/") ? "/" : "") + fileName;
                  jsonTexture.uvTranslation = this.readVector2(texture.get("uvTranslation"), 0.0F, 0.0F);
                  jsonTexture.uvScaling = this.readVector2(texture.get("uvScaling"), 1.0F, 1.0F);
                  String textureType = texture.getString("type", (String)null);
                  if (textureType == null) {
                     throw new GdxRuntimeException("Texture needs type.");
                  }

                  jsonTexture.usage = this.parseTextureUsage(textureType);
                  if (jsonMaterial.textures == null) {
                     jsonMaterial.textures = new Array();
                  }

                  jsonMaterial.textures.add(jsonTexture);
               }
            }

            model.materials.add(jsonMaterial);
         }
      }

   }

   private int parseTextureUsage(String value) {
      if (value.equalsIgnoreCase("AMBIENT")) {
         return 4;
      } else if (value.equalsIgnoreCase("BUMP")) {
         return 8;
      } else if (value.equalsIgnoreCase("DIFFUSE")) {
         return 2;
      } else if (value.equalsIgnoreCase("EMISSIVE")) {
         return 3;
      } else if (value.equalsIgnoreCase("NONE")) {
         return 1;
      } else if (value.equalsIgnoreCase("NORMAL")) {
         return 7;
      } else if (value.equalsIgnoreCase("REFLECTION")) {
         return 10;
      } else if (value.equalsIgnoreCase("SHININESS")) {
         return 6;
      } else if (value.equalsIgnoreCase("SPECULAR")) {
         return 5;
      } else {
         return value.equalsIgnoreCase("TRANSPARENCY") ? 9 : 0;
      }
   }

   private Color parseColor(JsonValue colorArray) {
      if (colorArray.size >= 3) {
         return new Color(colorArray.getFloat(0), colorArray.getFloat(1), colorArray.getFloat(2), 1.0F);
      } else {
         throw new GdxRuntimeException("Expected Color values <> than three.");
      }
   }

   private Vector2 readVector2(JsonValue vectorArray, float x, float y) {
      if (vectorArray == null) {
         return new Vector2(x, y);
      } else if (vectorArray.size == 2) {
         return new Vector2(vectorArray.getFloat(0), vectorArray.getFloat(1));
      } else {
         throw new GdxRuntimeException("Expected Vector2 values <> than two.");
      }
   }

   private Array<ModelNode> parseNodes(ModelData model, JsonValue json) {
      JsonValue nodes = json.get("nodes");
      if (nodes == null) {
         throw new GdxRuntimeException("At least one node is required.");
      } else {
         model.nodes.ensureCapacity(nodes.size());

         for(JsonValue node = nodes.child(); node != null; node = node.next()) {
            model.nodes.add(this.parseNodesRecursively(node));
         }

         return model.nodes;
      }
   }

   private ModelNode parseNodesRecursively(JsonValue json) {
      ModelNode jsonNode = new ModelNode();
      String id = json.getString("id", (String)null);
      if (id == null) {
         throw new GdxRuntimeException("Node id missing.");
      } else {
         jsonNode.id = id;
         JsonValue translation = json.get("translation");
         if (translation != null && translation.size() != 3) {
            throw new GdxRuntimeException("Node translation incomplete");
         } else {
            jsonNode.translation = translation == null ? null : new Vector3(translation.getFloat(0), translation.getFloat(1), translation.getFloat(2));
            JsonValue rotation = json.get("rotation");
            if (rotation != null && rotation.size() != 4) {
               throw new GdxRuntimeException("Node rotation incomplete");
            } else {
               jsonNode.rotation = rotation == null ? null : new Quaternion(rotation.getFloat(0), rotation.getFloat(1), rotation.getFloat(2), rotation.getFloat(3));
               JsonValue scale = json.get("scale");
               if (scale != null && scale.size() != 3) {
                  throw new GdxRuntimeException("Node scale incomplete");
               } else {
                  jsonNode.scale = scale == null ? null : new Vector3(scale.getFloat(0), scale.getFloat(1), scale.getFloat(2));
                  String meshId = json.getString("mesh", (String)null);
                  if (meshId != null) {
                     jsonNode.meshId = meshId;
                  }

                  JsonValue materials = json.get("parts");
                  if (materials != null) {
                     jsonNode.parts = new ModelNodePart[materials.size()];
                     int i = 0;

                     for(JsonValue material = materials.child(); material != null; ++i) {
                        ModelNodePart nodePart = new ModelNodePart();
                        String meshPartId = material.getString("meshpartid", (String)null);
                        String materialId = material.getString("materialid", (String)null);
                        if (meshPartId == null || materialId == null) {
                           throw new GdxRuntimeException("Node " + id + " part is missing meshPartId or materialId");
                        }

                        nodePart.materialId = materialId;
                        nodePart.meshPartId = meshPartId;
                        JsonValue bones = material.get("bones");
                        if (bones != null) {
                           nodePart.bones = new ArrayMap(true, bones.size(), String.class, Matrix4.class);
                           int j = 0;

                           for(JsonValue bone = bones.child(); bone != null; ++j) {
                              String nodeId = bone.getString("node", (String)null);
                              if (nodeId == null) {
                                 throw new GdxRuntimeException("Bone node ID missing");
                              }

                              Matrix4 transform = new Matrix4();
                              JsonValue val = bone.get("translation");
                              if (val != null && val.size() >= 3) {
                                 transform.translate(val.getFloat(0), val.getFloat(1), val.getFloat(2));
                              }

                              val = bone.get("rotation");
                              if (val != null && val.size() >= 4) {
                                 transform.rotate(this.tempQ.set(val.getFloat(0), val.getFloat(1), val.getFloat(2), val.getFloat(3)));
                              }

                              val = bone.get("scale");
                              if (val != null && val.size() >= 3) {
                                 transform.scale(val.getFloat(0), val.getFloat(1), val.getFloat(2));
                              }

                              nodePart.bones.put(nodeId, transform);
                              bone = bone.next();
                           }
                        }

                        jsonNode.parts[i] = nodePart;
                        material = material.next();
                     }
                  }

                  JsonValue children = json.get("children");
                  if (children != null) {
                     jsonNode.children = new ModelNode[children.size()];
                     int i = 0;

                     for(JsonValue child = children.child(); child != null; ++i) {
                        jsonNode.children[i] = this.parseNodesRecursively(child);
                        child = child.next();
                     }
                  }

                  return jsonNode;
               }
            }
         }
      }
   }

   private void parseAnimations(ModelData model, JsonValue json) {
      JsonValue animations = json.get("animations");
      if (animations != null) {
         model.animations.ensureCapacity(animations.size());

         for(JsonValue anim = animations.child(); anim != null; anim = anim.next()) {
            JsonValue nodes = anim.get("bones");
            if (nodes != null) {
               ModelAnimation animation = new ModelAnimation();
               model.animations.add(animation);
               animation.nodeAnimations.ensureCapacity(nodes.size());
               animation.id = anim.getString("id");

               for(JsonValue node = nodes.child(); node != null; node = node.next()) {
                  JsonValue keyframes = node.get("keyframes");
                  ModelNodeAnimation nodeAnim = new ModelNodeAnimation();
                  animation.nodeAnimations.add(nodeAnim);
                  nodeAnim.nodeId = node.getString("boneId");
                  nodeAnim.keyframes.ensureCapacity(keyframes.size());

                  for(JsonValue keyframe = keyframes.child(); keyframe != null; keyframe = keyframe.next()) {
                     ModelNodeKeyframe kf = new ModelNodeKeyframe();
                     nodeAnim.keyframes.add(kf);
                     kf.keytime = keyframe.getFloat("keytime") / 1000.0F;
                     JsonValue translation = keyframe.get("translation");
                     if (translation != null && translation.size() == 3) {
                        kf.translation = new Vector3(translation.getFloat(0), translation.getFloat(1), translation.getFloat(2));
                     }

                     JsonValue rotation = keyframe.get("rotation");
                     if (rotation != null && rotation.size() == 4) {
                        kf.rotation = new Quaternion(rotation.getFloat(0), rotation.getFloat(1), rotation.getFloat(2), rotation.getFloat(3));
                     }

                     JsonValue scale = keyframe.get("scale");
                     if (scale != null && scale.size() == 3) {
                        kf.scale = new Vector3(scale.getFloat(0), scale.getFloat(1), scale.getFloat(2));
                     }
                  }
               }
            }
         }

      }
   }
}
