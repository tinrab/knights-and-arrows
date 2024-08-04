package com.badlogic.gdx.graphics.g3d;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.materials.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.materials.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.materials.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.graphics.g3d.materials.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.Animation;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodeAnimation;
import com.badlogic.gdx.graphics.g3d.model.NodeKeyframe;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
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
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.badlogic.gdx.graphics.g3d.utils.TextureProvider;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import java.util.Iterator;

public class Model implements Disposable {
   public final Array<Material> materials;
   public final Array<Node> nodes;
   public final Array<Animation> animations;
   public final Array<Mesh> meshes;
   public final Array<MeshPart> meshParts;
   protected final Array<Disposable> disposables;
   private ObjectMap<NodePart, ArrayMap<String, Matrix4>> nodePartBones;

   public Model() {
      this.materials = new Array();
      this.nodes = new Array();
      this.animations = new Array();
      this.meshes = new Array();
      this.meshParts = new Array();
      this.disposables = new Array();
      this.nodePartBones = new ObjectMap();
   }

   public Model(ModelData modelData) {
      this(modelData, new TextureProvider.FileTextureProvider());
   }

   public Model(ModelData modelData, TextureProvider textureProvider) {
      this.materials = new Array();
      this.nodes = new Array();
      this.animations = new Array();
      this.meshes = new Array();
      this.meshParts = new Array();
      this.disposables = new Array();
      this.nodePartBones = new ObjectMap();
      this.load(modelData, textureProvider);
   }

   private void load(ModelData modelData, TextureProvider textureProvider) {
      this.loadMeshes(modelData.meshes);
      this.loadMaterials(modelData.materials, textureProvider);
      this.loadNodes(modelData.nodes);
      this.loadAnimations(modelData.animations);
      this.calculateTransforms();
   }

   private void loadAnimations(Iterable<ModelAnimation> modelAnimations) {
      Iterator var3 = modelAnimations.iterator();

      label57:
      while(var3.hasNext()) {
         ModelAnimation anim = (ModelAnimation)var3.next();
         Animation animation = new Animation();
         animation.id = anim.id;
         Iterator var6 = anim.nodeAnimations.iterator();

         while(true) {
            ModelNodeAnimation nanim;
            Node node;
            do {
               if (!var6.hasNext()) {
                  if (animation.nodeAnimations.size > 0) {
                     this.animations.add(animation);
                  }
                  continue label57;
               }

               nanim = (ModelNodeAnimation)var6.next();
               node = this.getNode(nanim.nodeId);
            } while(node == null);

            NodeAnimation nodeAnim = new NodeAnimation();
            nodeAnim.node = node;
            Iterator var10 = nanim.keyframes.iterator();

            while(var10.hasNext()) {
               ModelNodeKeyframe kf = (ModelNodeKeyframe)var10.next();
               if (kf.keytime > animation.duration) {
                  animation.duration = kf.keytime;
               }

               NodeKeyframe keyframe = new NodeKeyframe();
               keyframe.keytime = kf.keytime;
               keyframe.rotation.set(kf.rotation == null ? node.rotation : kf.rotation);
               keyframe.scale.set(kf.scale == null ? node.scale : kf.scale);
               keyframe.translation.set(kf.translation == null ? node.translation : kf.translation);
               nodeAnim.keyframes.add(keyframe);
            }

            if (nodeAnim.keyframes.size > 0) {
               animation.nodeAnimations.add(nodeAnim);
            }
         }
      }

   }

   private void loadNodes(Iterable<ModelNode> modelNodes) {
      this.nodePartBones.clear();
      Iterator var3 = modelNodes.iterator();

      while(var3.hasNext()) {
         ModelNode node = (ModelNode)var3.next();
         this.nodes.add(this.loadNode((Node)null, node));
      }

      var3 = this.nodePartBones.entries().iterator();

      while(var3.hasNext()) {
         ObjectMap.Entry<NodePart, ArrayMap<String, Matrix4>> e = (ObjectMap.Entry)var3.next();
         if (((NodePart)e.key).invBoneBindTransforms == null) {
            ((NodePart)e.key).invBoneBindTransforms = new ArrayMap(Node.class, Matrix4.class);
         }

         ((NodePart)e.key).invBoneBindTransforms.clear();
         Iterator var5 = ((ArrayMap)e.value).entries().iterator();

         while(var5.hasNext()) {
            ObjectMap.Entry<String, Matrix4> b = (ObjectMap.Entry)var5.next();
            ((NodePart)e.key).invBoneBindTransforms.put(this.getNode((String)b.key), (new Matrix4((Matrix4)b.value)).inv());
         }
      }

   }

   private Node loadNode(Node parent, ModelNode modelNode) {
      Node node = new Node();
      node.id = modelNode.id;
      node.parent = parent;
      if (modelNode.translation != null) {
         node.translation.set(modelNode.translation);
      }

      if (modelNode.rotation != null) {
         node.rotation.set(modelNode.rotation);
      }

      if (modelNode.scale != null) {
         node.scale.set(modelNode.scale);
      }

      int var5;
      int var6;
      if (modelNode.parts != null) {
         ModelNodePart[] var7;
         var6 = (var7 = modelNode.parts).length;

         for(var5 = 0; var5 < var6; ++var5) {
            ModelNodePart modelNodePart = var7[var5];
            MeshPart meshPart = null;
            Material meshMaterial = null;
            Iterator var11;
            if (modelNodePart.meshPartId != null) {
               var11 = this.meshParts.iterator();

               while(var11.hasNext()) {
                  MeshPart part = (MeshPart)var11.next();
                  if (modelNodePart.meshPartId.equals(part.id)) {
                     meshPart = part;
                     break;
                  }
               }
            }

            if (modelNodePart.materialId != null) {
               var11 = this.materials.iterator();

               while(var11.hasNext()) {
                  Material material = (Material)var11.next();
                  if (modelNodePart.materialId.equals(material.id)) {
                     meshMaterial = material;
                     break;
                  }
               }
            }

            if (meshPart != null && meshMaterial != null) {
               NodePart nodePart = new NodePart();
               nodePart.meshPart = meshPart;
               nodePart.material = meshMaterial;
               node.parts.add(nodePart);
               if (modelNodePart.bones != null) {
                  this.nodePartBones.put(nodePart, modelNodePart.bones);
               }
            }
         }
      }

      if (modelNode.children != null) {
         ModelNode[] var13;
         var6 = (var13 = modelNode.children).length;

         for(var5 = 0; var5 < var6; ++var5) {
            ModelNode child = var13[var5];
            node.children.add(this.loadNode(node, child));
         }
      }

      return node;
   }

   private void loadMeshes(Iterable<ModelMesh> meshes) {
      Iterator var3 = meshes.iterator();

      while(var3.hasNext()) {
         ModelMesh mesh = (ModelMesh)var3.next();
         this.convertMesh(mesh);
      }

   }

   private void convertMesh(ModelMesh modelMesh) {
      int numIndices = 0;
      ModelMeshPart[] var6;
      int var5 = (var6 = modelMesh.parts).length;

      int numVertices;
      for(numVertices = 0; numVertices < var5; ++numVertices) {
         ModelMeshPart part = var6[numVertices];
         numIndices += part.indices.length;
      }

      VertexAttributes attributes = new VertexAttributes(modelMesh.attributes);
      numVertices = modelMesh.vertices.length / (attributes.vertexSize / 4);
      Mesh mesh = new Mesh(true, numVertices, numIndices, attributes);
      this.meshes.add(mesh);
      this.disposables.add(mesh);
      BufferUtils.copy(modelMesh.vertices, mesh.getVerticesBuffer(), modelMesh.vertices.length, 0);
      int offset = 0;
      mesh.getIndicesBuffer().clear();
      ModelMeshPart[] var10;
      int var9 = (var10 = modelMesh.parts).length;

      for(int var8 = 0; var8 < var9; ++var8) {
         ModelMeshPart part = var10[var8];
         MeshPart meshPart = new MeshPart();
         meshPart.id = part.id;
         meshPart.primitiveType = part.primitiveType;
         meshPart.indexOffset = offset;
         meshPart.numVertices = part.indices.length;
         meshPart.mesh = mesh;
         mesh.getIndicesBuffer().put(part.indices);
         offset += meshPart.numVertices;
         this.meshParts.add(meshPart);
      }

      mesh.getIndicesBuffer().position(0);
   }

   private void loadMaterials(Iterable<ModelMaterial> modelMaterials, TextureProvider textureProvider) {
      Iterator var4 = modelMaterials.iterator();

      while(var4.hasNext()) {
         ModelMaterial mtl = (ModelMaterial)var4.next();
         this.materials.add(this.convertMaterial(mtl, textureProvider));
      }

   }

   private Material convertMaterial(ModelMaterial mtl, TextureProvider textureProvider) {
      Material result = new Material();
      result.id = mtl.id;
      if (mtl.ambient != null) {
         result.set((Material.Attribute)(new ColorAttribute(ColorAttribute.Ambient, mtl.ambient)));
      }

      if (mtl.diffuse != null) {
         result.set((Material.Attribute)(new ColorAttribute(ColorAttribute.Diffuse, mtl.diffuse)));
      }

      if (mtl.specular != null) {
         result.set((Material.Attribute)(new ColorAttribute(ColorAttribute.Specular, mtl.specular)));
      }

      if (mtl.emissive != null) {
         result.set((Material.Attribute)(new ColorAttribute(ColorAttribute.Emissive, mtl.emissive)));
      }

      if (mtl.shininess > 0.0F) {
         result.set((Material.Attribute)(new FloatAttribute(FloatAttribute.Shininess, mtl.shininess)));
      }

      if (mtl.opacity != 1.0F) {
         result.set((Material.Attribute)(new BlendingAttribute(770, 771, mtl.opacity)));
      }

      ObjectMap<String, Texture> textures = new ObjectMap();
      if (mtl.textures != null) {
         Iterator var6 = mtl.textures.iterator();

         while(var6.hasNext()) {
            ModelTexture tex = (ModelTexture)var6.next();
            Texture texture;
            if (textures.containsKey(tex.fileName)) {
               texture = (Texture)textures.get(tex.fileName);
            } else {
               texture = textureProvider.load(tex.fileName);
               textures.put(tex.fileName, texture);
               this.disposables.add(texture);
            }

            TextureDescriptor descriptor = new TextureDescriptor(texture);
            descriptor.minFilter = 9729;
            descriptor.magFilter = 9729;
            descriptor.uWrap = 10497;
            descriptor.vWrap = 10497;
            switch(tex.usage) {
            case 2:
               result.set((Material.Attribute)(new TextureAttribute(TextureAttribute.Diffuse, descriptor)));
            case 3:
            case 4:
            case 6:
            default:
               break;
            case 5:
               result.set((Material.Attribute)(new TextureAttribute(TextureAttribute.Specular, descriptor)));
               break;
            case 7:
               result.set((Material.Attribute)(new TextureAttribute(TextureAttribute.Normal, descriptor)));
               break;
            case 8:
               result.set((Material.Attribute)(new TextureAttribute(TextureAttribute.Bump, descriptor)));
            }
         }
      }

      return result;
   }

   public void manageDisposable(Disposable disposable) {
      if (!this.disposables.contains(disposable, true)) {
         this.disposables.add(disposable);
      }

   }

   public Iterable<Disposable> getManagedDisposables() {
      return this.disposables;
   }

   public void dispose() {
      Iterator var2 = this.disposables.iterator();

      while(var2.hasNext()) {
         Disposable disposable = (Disposable)var2.next();
         disposable.dispose();
      }

   }

   public void calculateTransforms() {
      Iterator var2 = this.nodes.iterator();

      Node node;
      while(var2.hasNext()) {
         node = (Node)var2.next();
         node.calculateTransforms(true);
      }

      var2 = this.nodes.iterator();

      while(var2.hasNext()) {
         node = (Node)var2.next();
         node.calculateBoneTransforms(true);
      }

   }

   public BoundingBox calculateBoundingBox(BoundingBox out) {
      out.inf();
      return this.extendBoundingBox(out);
   }

   public BoundingBox extendBoundingBox(BoundingBox out) {
      Iterator var3 = this.nodes.iterator();

      while(var3.hasNext()) {
         Node node = (Node)var3.next();
         this.calculateBoundingBox(out, node);
      }

      return out;
   }

   protected void calculateBoundingBox(BoundingBox out, Node node) {
      Iterator var4 = node.parts.iterator();

      while(var4.hasNext()) {
         NodePart mpm = (NodePart)var4.next();
         mpm.meshPart.mesh.calculateBoundingBox(out, mpm.meshPart.indexOffset, mpm.meshPart.numVertices, node.globalTransform);
      }

      var4 = node.children.iterator();

      while(var4.hasNext()) {
         Node child = (Node)var4.next();
         this.calculateBoundingBox(out, child);
      }

   }

   public Animation getAnimation(String id) {
      Iterator var3 = this.animations.iterator();

      while(var3.hasNext()) {
         Animation anim = (Animation)var3.next();
         if (anim.id.compareTo(id) == 0) {
            return anim;
         }
      }

      return null;
   }

   public Material getMaterial(String id) {
      Iterator var3 = this.materials.iterator();

      while(var3.hasNext()) {
         Material mtl = (Material)var3.next();
         if (mtl.id.compareTo(id) == 0) {
            return mtl;
         }
      }

      return null;
   }

   public Node getNode(String id, boolean recursive) {
      return this.getNode(id, this.nodes, recursive);
   }

   public Node getNode(String id) {
      return this.getNode(id, true);
   }

   protected Node getNode(String id, Iterable<Node> nodes, boolean recursive) {
      Iterator var5 = nodes.iterator();

      while(var5.hasNext()) {
         Node node = (Node)var5.next();
         if (node.id.equals(id)) {
            return node;
         }

         if (recursive) {
            Node n = this.getNode(id, node.children, recursive);
            if (n != null) {
               return n;
            }
         }
      }

      return null;
   }
}
