package com.badlogic.gdx.graphics.g3d.decals;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.SortedIntList;
import java.util.Iterator;

public class DecalBatch implements Disposable {
   private static final int DEFAULT_SIZE = 1000;
   private float[] vertices;
   private Mesh mesh;
   private final SortedIntList<Array<Decal>> groupList;
   private GroupStrategy groupStrategy;
   private final Pool<Array<Decal>> groupPool;
   private final Array<Array<Decal>> usedGroups;

   public DecalBatch() {
      this(1000, new DefaultGroupStrategy());
   }

   public DecalBatch(GroupStrategy groupStrategy) {
      this(1000, groupStrategy);
   }

   public DecalBatch(int size, GroupStrategy groupStrategy) {
      this.groupList = new SortedIntList();
      this.groupPool = new Pool<Array<Decal>>(16) {
         protected Array<Decal> newObject() {
            return new Array(false, 100);
         }
      };
      this.usedGroups = new Array(16);
      this.initialize(size);
      this.setGroupStrategy(groupStrategy);
   }

   public void setGroupStrategy(GroupStrategy groupStrategy) {
      this.groupStrategy = groupStrategy;
   }

   public void initialize(int size) {
      this.vertices = new float[size * 24];
      this.mesh = new Mesh(Mesh.VertexDataType.VertexArray, false, size * 4, size * 6, new VertexAttribute[]{new VertexAttribute(1, 3, "a_position"), new VertexAttribute(4, 4, "a_color"), new VertexAttribute(16, 2, "a_texCoord0")});
      short[] indices = new short[size * 6];
      int v = 0;

      for(int i = 0; i < indices.length; v += 4) {
         indices[i] = (short)v;
         indices[i + 1] = (short)(v + 2);
         indices[i + 2] = (short)(v + 1);
         indices[i + 3] = (short)(v + 1);
         indices[i + 4] = (short)(v + 2);
         indices[i + 5] = (short)(v + 3);
         i += 6;
      }

      this.mesh.setIndices(indices);
   }

   public int getSize() {
      return this.vertices.length / 24;
   }

   public void add(Decal decal) {
      DecalMaterial material = decal.getMaterial();
      int groupIndex = this.groupStrategy.decideGroup(decal);
      Array<Decal> targetGroup = (Array)this.groupList.get(groupIndex);
      if (targetGroup == null) {
         targetGroup = (Array)this.groupPool.obtain();
         targetGroup.clear();
         this.usedGroups.add(targetGroup);
         this.groupList.insert(groupIndex, targetGroup);
      }

      targetGroup.add(decal);
   }

   public void flush() {
      this.render();
      this.clear();
   }

   protected void render() {
      this.groupStrategy.beforeGroups();
      Iterator var2 = this.groupList.iterator();

      while(var2.hasNext()) {
         SortedIntList.Node<Array<Decal>> group = (SortedIntList.Node)var2.next();
         this.groupStrategy.beforeGroup(group.index, (Array)group.value);
         ShaderProgram shader = this.groupStrategy.getGroupShader(group.index);
         this.render(shader, (Array)group.value);
         this.groupStrategy.afterGroup(group.index);
      }

      this.groupStrategy.afterGroups();
   }

   private void render(ShaderProgram shader, Array<Decal> decals) {
      DecalMaterial lastMaterial = null;
      int idx = 0;
      Iterator var6 = decals.iterator();

      while(var6.hasNext()) {
         Decal decal = (Decal)var6.next();
         if (lastMaterial == null || !lastMaterial.equals(decal.getMaterial())) {
            if (idx > 0) {
               this.flush(shader, idx);
               idx = 0;
            }

            decal.material.set();
            lastMaterial = decal.material;
         }

         decal.update();
         System.arraycopy(decal.vertices, 0, this.vertices, idx, decal.vertices.length);
         idx += decal.vertices.length;
         if (idx == this.vertices.length) {
            this.flush(shader, idx);
            idx = 0;
         }
      }

      if (idx > 0) {
         this.flush(shader, idx);
      }

   }

   protected void flush(ShaderProgram shader, int verticesPosition) {
      this.mesh.setVertices(this.vertices, 0, verticesPosition);
      if (shader != null) {
         this.mesh.render(shader, 4, 0, verticesPosition / 4);
      } else {
         this.mesh.render(4, 0, verticesPosition / 4);
      }

   }

   protected void clear() {
      this.groupList.clear();
      this.groupPool.freeAll(this.usedGroups);
      this.usedGroups.clear();
   }

   public void dispose() {
      this.clear();
      this.vertices = null;
      this.mesh.dispose();
   }
}
