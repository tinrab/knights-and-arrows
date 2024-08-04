package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.util.Iterator;

public class ModelBuilder {
   private Model model;
   private Node node;
   private Array<MeshBuilder> builders = new Array();

   private MeshBuilder getBuilder(VertexAttributes attributes) {
      Iterator var3 = this.builders.iterator();

      MeshBuilder mb;
      while(var3.hasNext()) {
         mb = (MeshBuilder)var3.next();
         if (mb.getAttributes().equals(attributes)) {
            return mb;
         }
      }

      mb = new MeshBuilder();
      mb.begin(attributes);
      this.builders.add(mb);
      return mb;
   }

   public void begin() {
      if (this.model != null) {
         throw new GdxRuntimeException("Call end() first");
      } else {
         this.node = null;
         this.model = new Model();
         this.builders.clear();
      }
   }

   public Model end() {
      if (this.model == null) {
         throw new GdxRuntimeException("Call begin() first");
      } else {
         Model result = this.model;
         this.endnode();
         this.model = null;
         Iterator var3 = this.builders.iterator();

         while(var3.hasNext()) {
            MeshBuilder mb = (MeshBuilder)var3.next();
            mb.end();
         }

         this.builders.clear();
         rebuildReferences(result);
         return result;
      }
   }

   private void endnode() {
      if (this.node != null) {
         this.node = null;
      }

   }

   protected Node node(Node node) {
      if (this.model == null) {
         throw new GdxRuntimeException("Call begin() first");
      } else {
         this.endnode();
         this.model.nodes.add(node);
         this.node = node;
         return node;
      }
   }

   public Node node() {
      Node node = new Node();
      node.id = "node" + this.model.nodes.size;
      return this.node(node);
   }

   public Node node(String id, Model model) {
      Node node = new Node();
      node.id = id;
      node.children.addAll(model.nodes);
      Iterator var5 = model.getManagedDisposables().iterator();

      while(var5.hasNext()) {
         Disposable disposable = (Disposable)var5.next();
         this.manage(disposable);
      }

      return this.node(node);
   }

   public void manage(Disposable disposable) {
      if (this.model == null) {
         throw new GdxRuntimeException("Call begin() first");
      } else {
         this.model.manageDisposable(disposable);
      }
   }

   public void part(MeshPart meshpart, Material material) {
      if (this.node == null) {
         this.node();
      }

      this.node.parts.add(new NodePart(meshpart, material));
   }

   public MeshPart part(String id, Mesh mesh, int primitiveType, int offset, int size, Material material) {
      MeshPart meshPart = new MeshPart();
      meshPart.id = id;
      meshPart.primitiveType = primitiveType;
      meshPart.mesh = mesh;
      meshPart.indexOffset = offset;
      meshPart.numVertices = size;
      this.part(meshPart, material);
      return meshPart;
   }

   public MeshPart part(String id, Mesh mesh, int primitiveType, Material material) {
      return this.part(id, mesh, primitiveType, 0, mesh.getNumIndices(), material);
   }

   private MeshPartBuilder part(String id, int primitiveType, VertexAttributes attributes, Material material) {
      MeshBuilder builder = this.getBuilder(attributes);
      this.part(builder.part(id, primitiveType), material);
      return builder;
   }

   public MeshPartBuilder part(String id, int primitiveType, long attributes, Material material) {
      return this.part(id, primitiveType, MeshBuilder.createAttributes(attributes), material);
   }

   public Model createBox(float width, float height, float depth, Material material, long attributes) {
      return this.createBox(width, height, depth, 4, material, attributes);
   }

   public Model createBox(float width, float height, float depth, int primitiveType, Material material, long attributes) {
      this.begin();
      this.part("box", primitiveType, attributes, material).box(width, height, depth);
      return this.end();
   }

   public Model createRect(float x00, float y00, float z00, float x10, float y10, float z10, float x11, float y11, float z11, float x01, float y01, float z01, float normalX, float normalY, float normalZ, Material material, long attributes) {
      return this.createRect(x00, y00, z00, x10, y10, z10, x11, y11, z11, x01, y01, z01, normalX, normalY, normalZ, 4, material, attributes);
   }

   public Model createRect(float x00, float y00, float z00, float x10, float y10, float z10, float x11, float y11, float z11, float x01, float y01, float z01, float normalX, float normalY, float normalZ, int primitiveType, Material material, long attributes) {
      this.begin();
      this.part("rect", primitiveType, attributes, material).rect(x00, y00, z00, x10, y10, z10, x11, y11, z11, x01, y01, z01, normalX, normalY, normalZ);
      return this.end();
   }

   public Model createCylinder(float width, float height, float depth, int divisions, Material material, long attributes) {
      return this.createCylinder(width, height, depth, divisions, 4, material, attributes);
   }

   public Model createCylinder(float width, float height, float depth, int divisions, int primitiveType, Material material, long attributes) {
      this.begin();
      this.part("cylinder", primitiveType, attributes, material).cylinder(width, height, depth, divisions);
      return this.end();
   }

   public Model createCone(float width, float height, float depth, int divisions, Material material, long attributes) {
      return this.createCone(width, height, depth, divisions, 4, material, attributes);
   }

   public Model createCone(float width, float height, float depth, int divisions, int primitiveType, Material material, long attributes) {
      this.begin();
      this.part("cone", primitiveType, attributes, material).cone(width, height, depth, divisions);
      return this.end();
   }

   public Model createSphere(float width, float height, float depth, int divisionsU, int divisionsV, Material material, long attributes) {
      return this.createSphere(width, height, depth, divisionsU, divisionsV, 4, material, attributes);
   }

   public Model createSphere(float width, float height, float depth, int divisionsU, int divisionsV, int primitiveType, Material material, long attributes) {
      this.begin();
      this.part("cylinder", primitiveType, attributes, material).sphere(width, height, depth, divisionsU, divisionsV);
      return this.end();
   }

   public static void rebuildReferences(Model model) {
      model.materials.clear();
      model.meshes.clear();
      model.meshParts.clear();
      Iterator var2 = model.nodes.iterator();

      while(var2.hasNext()) {
         Node node = (Node)var2.next();
         rebuildReferences(model, node);
      }

   }

   private static void rebuildReferences(Model model, Node node) {
      Iterator var3 = node.parts.iterator();

      while(var3.hasNext()) {
         NodePart mpm = (NodePart)var3.next();
         if (!model.materials.contains(mpm.material, true)) {
            model.materials.add(mpm.material);
         }

         if (!model.meshParts.contains(mpm.meshPart, true)) {
            model.meshParts.add(mpm.meshPart);
            if (!model.meshes.contains(mpm.meshPart.mesh, true)) {
               model.meshes.add(mpm.meshPart.mesh);
            }

            model.manageDisposable(mpm.meshPart.mesh);
         }
      }

      var3 = node.children.iterator();

      while(var3.hasNext()) {
         Node child = (Node)var3.next();
         rebuildReferences(model, child);
      }

   }

   /** @deprecated */
   @Deprecated
   public static Model createFromMesh(Mesh mesh, int primitiveType, Material material) {
      return createFromMesh(mesh, 0, mesh.getNumIndices(), primitiveType, material);
   }

   /** @deprecated */
   @Deprecated
   public static Model createFromMesh(Mesh mesh, int indexOffset, int vertexCount, int primitiveType, Material material) {
      Model result = new Model();
      MeshPart meshPart = new MeshPart();
      meshPart.id = "part1";
      meshPart.indexOffset = indexOffset;
      meshPart.numVertices = vertexCount;
      meshPart.primitiveType = primitiveType;
      meshPart.mesh = mesh;
      NodePart partMaterial = new NodePart();
      partMaterial.material = material;
      partMaterial.meshPart = meshPart;
      Node node = new Node();
      node.id = "node1";
      node.parts.add(partMaterial);
      result.meshes.add(mesh);
      result.materials.add(material);
      result.nodes.add(node);
      result.meshParts.add(meshPart);
      result.manageDisposable(mesh);
      return result;
   }

   /** @deprecated */
   @Deprecated
   public static Model createFromMesh(float[] vertices, VertexAttribute[] attributes, short[] indices, int primitiveType, Material material) {
      Mesh mesh = new Mesh(false, vertices.length, indices.length, attributes);
      mesh.setVertices(vertices);
      mesh.setIndices(indices);
      return createFromMesh(mesh, 0, indices.length, primitiveType, material);
   }
}
