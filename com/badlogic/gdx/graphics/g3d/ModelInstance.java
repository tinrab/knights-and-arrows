package com.badlogic.gdx.graphics.g3d;

import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.graphics.g3d.model.Animation;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodeAnimation;
import com.badlogic.gdx.graphics.g3d.model.NodeKeyframe;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;
import java.util.Iterator;

public class ModelInstance implements RenderableProvider {
   public final Array<Material> materials;
   public final Array<Node> nodes;
   public final Array<Animation> animations;
   public final Model model;
   public Matrix4 transform;
   public Object userData;
   private ObjectMap<NodePart, ArrayMap<Node, Matrix4>> nodePartBones;

   public ModelInstance(Model model) {
      this((Model)model, (String[])null);
   }

   public ModelInstance(Model model, String nodeId, boolean mergeTransform) {
      this(model, (Matrix4)null, nodeId, false, false, mergeTransform);
   }

   public ModelInstance(Model model, Matrix4 transform, String nodeId, boolean mergeTransform) {
      this(model, transform, nodeId, false, false, mergeTransform);
   }

   public ModelInstance(Model model, String nodeId, boolean parentTransform, boolean mergeTransform) {
      this(model, (Matrix4)null, nodeId, true, parentTransform, mergeTransform);
   }

   public ModelInstance(Model model, Matrix4 transform, String nodeId, boolean parentTransform, boolean mergeTransform) {
      this(model, transform, nodeId, true, parentTransform, mergeTransform);
   }

   public ModelInstance(Model model, String nodeId, boolean recursive, boolean parentTransform, boolean mergeTransform) {
      this(model, (Matrix4)null, nodeId, recursive, parentTransform, mergeTransform);
   }

   public ModelInstance(Model model, Matrix4 transform, String nodeId, boolean recursive, boolean parentTransform, boolean mergeTransform) {
      this.materials = new Array();
      this.nodes = new Array();
      this.animations = new Array();
      this.nodePartBones = new ObjectMap();
      this.model = model;
      this.transform = transform == null ? new Matrix4() : transform;
      this.nodePartBones.clear();
      Node node = model.getNode(nodeId, recursive);
      Node copy;
      this.nodes.add(copy = this.copyNode((Node)null, node));
      if (mergeTransform) {
         this.transform.mul(parentTransform ? node.globalTransform : node.localTransform);
         copy.translation.set(0.0F, 0.0F, 0.0F);
         copy.rotation.idt();
         copy.scale.set(1.0F, 1.0F, 1.0F);
      } else if (parentTransform && copy.parent != null) {
         this.transform.mul(node.parent.globalTransform);
      }

      this.setBones();
      this.copyAnimations(model.animations);
      this.calculateTransforms();
   }

   public ModelInstance(Model model, String... rootNodeIds) {
      this(model, (Matrix4)null, (String[])rootNodeIds);
   }

   public ModelInstance(Model model, Matrix4 transform, String... rootNodeIds) {
      this.materials = new Array();
      this.nodes = new Array();
      this.animations = new Array();
      this.nodePartBones = new ObjectMap();
      this.model = model;
      this.transform = transform == null ? new Matrix4() : transform;
      if (rootNodeIds == null) {
         this.copyNodes(model.nodes);
      } else {
         this.copyNodes(model.nodes, rootNodeIds);
      }

      this.copyAnimations(model.animations);
      this.calculateTransforms();
   }

   public ModelInstance(Model model, Array<String> rootNodeIds) {
      this(model, (Matrix4)null, (Array)rootNodeIds);
   }

   public ModelInstance(Model model, Matrix4 transform, Array<String> rootNodeIds) {
      this.materials = new Array();
      this.nodes = new Array();
      this.animations = new Array();
      this.nodePartBones = new ObjectMap();
      this.model = model;
      this.transform = transform == null ? new Matrix4() : transform;
      this.copyNodes(model.nodes, rootNodeIds);
      this.copyAnimations(model.animations);
      this.calculateTransforms();
   }

   public ModelInstance(Model model, Vector3 position) {
      this(model);
      this.transform.setToTranslation(position);
   }

   public ModelInstance(Model model, float x, float y, float z) {
      this(model);
      this.transform.setToTranslation(x, y, z);
   }

   public ModelInstance(Model model, Matrix4 transform) {
      this(model, transform, (String[])null);
   }

   public ModelInstance(ModelInstance copyFrom) {
      this(copyFrom, copyFrom.transform.cpy());
   }

   public ModelInstance(ModelInstance copyFrom, Matrix4 transform) {
      this.materials = new Array();
      this.nodes = new Array();
      this.animations = new Array();
      this.nodePartBones = new ObjectMap();
      this.model = copyFrom.model;
      this.transform = transform == null ? new Matrix4() : transform;
      this.copyNodes(copyFrom.nodes);
      this.copyAnimations(copyFrom.animations);
      this.calculateTransforms();
   }

   public ModelInstance copy() {
      return new ModelInstance(this);
   }

   private void copyNodes(Array<Node> nodes) {
      this.nodePartBones.clear();
      Iterator var3 = nodes.iterator();

      while(var3.hasNext()) {
         Node node = (Node)var3.next();
         this.nodes.add(this.copyNode((Node)null, node));
      }

      this.setBones();
   }

   private void copyNodes(Array<Node> nodes, String... nodeIds) {
      this.nodePartBones.clear();
      Iterator var4 = nodes.iterator();

      while(true) {
         while(var4.hasNext()) {
            Node node = (Node)var4.next();
            String[] var8 = nodeIds;
            int var7 = nodeIds.length;

            for(int var6 = 0; var6 < var7; ++var6) {
               String nodeId = var8[var6];
               if (nodeId.equals(node.id)) {
                  this.nodes.add(this.copyNode((Node)null, node));
                  break;
               }
            }
         }

         this.setBones();
         return;
      }
   }

   private void copyNodes(Array<Node> nodes, Array<String> nodeIds) {
      this.nodePartBones.clear();
      Iterator var4 = nodes.iterator();

      while(true) {
         while(var4.hasNext()) {
            Node node = (Node)var4.next();
            Iterator var6 = nodeIds.iterator();

            while(var6.hasNext()) {
               String nodeId = (String)var6.next();
               if (nodeId.equals(node.id)) {
                  this.nodes.add(this.copyNode((Node)null, node));
                  break;
               }
            }
         }

         this.setBones();
         return;
      }
   }

   private void setBones() {
      Iterator var2 = this.nodePartBones.entries().iterator();

      while(var2.hasNext()) {
         ObjectMap.Entry<NodePart, ArrayMap<Node, Matrix4>> e = (ObjectMap.Entry)var2.next();
         if (((NodePart)e.key).invBoneBindTransforms == null) {
            ((NodePart)e.key).invBoneBindTransforms = new ArrayMap(true, ((ArrayMap)e.value).size, Node.class, Matrix4.class);
         }

         ((NodePart)e.key).invBoneBindTransforms.clear();
         Iterator var4 = ((ArrayMap)e.value).entries().iterator();

         while(var4.hasNext()) {
            ObjectMap.Entry<Node, Matrix4> b = (ObjectMap.Entry)var4.next();
            ((NodePart)e.key).invBoneBindTransforms.put(this.getNode(((Node)b.key).id), (Matrix4)b.value);
         }

         ((NodePart)e.key).bones = new Matrix4[((ArrayMap)e.value).size];

         for(int i = 0; i < ((NodePart)e.key).bones.length; ++i) {
            ((NodePart)e.key).bones[i] = new Matrix4();
         }
      }

   }

   private Node copyNode(Node parent, Node node) {
      Node copy = new Node();
      copy.id = node.id;
      copy.parent = parent;
      copy.translation.set(node.translation);
      copy.rotation.set(node.rotation);
      copy.scale.set(node.scale);
      copy.localTransform.set(node.localTransform);
      copy.globalTransform.set(node.globalTransform);
      Iterator var5 = node.parts.iterator();

      while(var5.hasNext()) {
         NodePart nodePart = (NodePart)var5.next();
         copy.parts.add(this.copyNodePart(nodePart));
      }

      var5 = node.children.iterator();

      while(var5.hasNext()) {
         Node child = (Node)var5.next();
         copy.children.add(this.copyNode(copy, child));
      }

      return copy;
   }

   private NodePart copyNodePart(NodePart nodePart) {
      NodePart copy = new NodePart();
      copy.meshPart = new MeshPart();
      copy.meshPart.id = nodePart.meshPart.id;
      copy.meshPart.indexOffset = nodePart.meshPart.indexOffset;
      copy.meshPart.numVertices = nodePart.meshPart.numVertices;
      copy.meshPart.primitiveType = nodePart.meshPart.primitiveType;
      copy.meshPart.mesh = nodePart.meshPart.mesh;
      if (nodePart.invBoneBindTransforms != null) {
         this.nodePartBones.put(copy, nodePart.invBoneBindTransforms);
      }

      int index = this.materials.indexOf(nodePart.material, false);
      if (index < 0) {
         this.materials.add(copy.material = nodePart.material.copy());
      } else {
         copy.material = (Material)this.materials.get(index);
      }

      return copy;
   }

   private void copyAnimations(Iterable<Animation> source) {
      Iterator var3 = source.iterator();

      label38:
      while(var3.hasNext()) {
         Animation anim = (Animation)var3.next();
         Animation animation = new Animation();
         animation.id = anim.id;
         animation.duration = anim.duration;
         Iterator var6 = anim.nodeAnimations.iterator();

         while(true) {
            NodeAnimation nanim;
            Node node;
            do {
               if (!var6.hasNext()) {
                  if (animation.nodeAnimations.size > 0) {
                     this.animations.add(animation);
                  }
                  continue label38;
               }

               nanim = (NodeAnimation)var6.next();
               node = this.getNode(nanim.node.id);
            } while(node == null);

            NodeAnimation nodeAnim = new NodeAnimation();
            nodeAnim.node = node;
            Iterator var10 = nanim.keyframes.iterator();

            while(var10.hasNext()) {
               NodeKeyframe kf = (NodeKeyframe)var10.next();
               NodeKeyframe keyframe = new NodeKeyframe();
               keyframe.keytime = kf.keytime;
               keyframe.rotation.set(kf.rotation);
               keyframe.scale.set(kf.scale);
               keyframe.translation.set(kf.translation);
               nodeAnim.keyframes.add(keyframe);
            }

            if (nodeAnim.keyframes.size > 0) {
               animation.nodeAnimations.add(nodeAnim);
            }
         }
      }

   }

   public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
      Iterator var4 = this.nodes.iterator();

      while(var4.hasNext()) {
         Node node = (Node)var4.next();
         this.getRenderables(node, renderables, pool);
      }

   }

   protected void getRenderables(Node node, Array<Renderable> renderables, Pool<Renderable> pool) {
      Iterator var5;
      if (node.parts.size > 0) {
         var5 = node.parts.iterator();

         while(var5.hasNext()) {
            NodePart nodePart = (NodePart)var5.next();
            Renderable renderable = (Renderable)pool.obtain();
            renderable.material = nodePart.material;
            renderable.mesh = nodePart.meshPart.mesh;
            renderable.meshPartOffset = nodePart.meshPart.indexOffset;
            renderable.meshPartSize = nodePart.meshPart.numVertices;
            renderable.primitiveType = nodePart.meshPart.primitiveType;
            renderable.bones = nodePart.bones;
            if (nodePart.bones == null && this.transform != null) {
               renderable.worldTransform.set(this.transform).mul(node.globalTransform);
            } else if (this.transform != null) {
               renderable.worldTransform.set(this.transform);
            } else {
               renderable.worldTransform.idt();
            }

            renderable.userData = this.userData;
            renderables.add(renderable);
         }
      }

      var5 = node.children.iterator();

      while(var5.hasNext()) {
         Node child = (Node)var5.next();
         this.getRenderables(child, renderables, pool);
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
