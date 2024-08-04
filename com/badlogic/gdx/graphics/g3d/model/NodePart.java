package com.badlogic.gdx.graphics.g3d.model;

import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.ArrayMap;

public class NodePart {
   public MeshPart meshPart;
   public Material material;
   public ArrayMap<Node, Matrix4> invBoneBindTransforms;
   public Matrix4[] bones;

   public NodePart() {
   }

   public NodePart(MeshPart meshPart, Material material) {
      this.meshPart = meshPart;
      this.material = material;
   }
}
