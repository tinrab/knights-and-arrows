package com.badlogic.gdx.graphics.g3d.model;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import java.util.Iterator;

public class Node {
   public String id;
   public Node parent;
   public final Array<Node> children = new Array(2);
   public boolean isAnimated;
   public final Vector3 translation = new Vector3();
   public final Quaternion rotation = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
   public final Vector3 scale = new Vector3(1.0F, 1.0F, 1.0F);
   public final Matrix4 localTransform = new Matrix4();
   public final Matrix4 globalTransform = new Matrix4();
   public Array<NodePart> parts = new Array(2);

   public Matrix4 calculateLocalTransform() {
      if (!this.isAnimated) {
         this.localTransform.idt();
         this.localTransform.translate(this.translation);
         this.localTransform.rotate(this.rotation);
         this.localTransform.scale(this.scale.x, this.scale.y, this.scale.z);
      }

      return this.localTransform;
   }

   public Matrix4 calculateWorldTransform() {
      if (this.parent == null) {
         this.globalTransform.set(this.localTransform);
      } else {
         this.globalTransform.set(this.parent.globalTransform).mul(this.localTransform);
      }

      return this.globalTransform;
   }

   public void calculateTransforms(boolean recursive) {
      this.calculateLocalTransform();
      this.calculateWorldTransform();
      if (recursive) {
         Iterator var3 = this.children.iterator();

         while(var3.hasNext()) {
            Node child = (Node)var3.next();
            child.calculateTransforms(true);
         }
      }

   }

   public void calculateBoneTransforms(boolean recursive) {
      Iterator var3 = this.parts.iterator();

      while(true) {
         NodePart part;
         do {
            do {
               do {
                  if (!var3.hasNext()) {
                     if (recursive) {
                        var3 = this.children.iterator();

                        while(var3.hasNext()) {
                           Node child = (Node)var3.next();
                           child.calculateBoneTransforms(true);
                        }
                     }

                     return;
                  }

                  part = (NodePart)var3.next();
               } while(part.invBoneBindTransforms == null);
            } while(part.bones == null);
         } while(part.invBoneBindTransforms.size != part.bones.length);

         int n = part.invBoneBindTransforms.size;

         for(int i = 0; i < n; ++i) {
            part.bones[i].set(((Node[])part.invBoneBindTransforms.keys)[i].globalTransform).mul(((Matrix4[])part.invBoneBindTransforms.values)[i]);
         }
      }
   }
}
