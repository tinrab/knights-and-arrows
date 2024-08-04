package com.badlogic.gdx.scenes.scene2d;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.Cullable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SnapshotArray;

public class Group extends Actor implements Cullable {
   private final SnapshotArray<Actor> children = new SnapshotArray(true, 4, Actor.class);
   private final Matrix3 localTransform = new Matrix3();
   private final Matrix3 worldTransform = new Matrix3();
   private final Matrix4 batchTransform = new Matrix4();
   private final Matrix4 oldBatchTransform = new Matrix4();
   private boolean transform = true;
   private Rectangle cullingArea;
   private final Vector2 point = new Vector2();

   public void act(float delta) {
      super.act(delta);
      Actor[] actors = (Actor[])this.children.begin();
      int i = 0;

      for(int n = this.children.size; i < n; ++i) {
         actors[i].act(delta);
      }

      this.children.end();
   }

   public void draw(SpriteBatch batch, float parentAlpha) {
      if (this.transform) {
         this.applyTransform(batch, this.computeTransform());
      }

      this.drawChildren(batch, parentAlpha);
      if (this.transform) {
         this.resetTransform(batch);
      }

   }

   protected void drawChildren(SpriteBatch batch, float parentAlpha) {
      parentAlpha *= this.color.a;
      SnapshotArray<Actor> children = this.children;
      Actor[] actors = (Actor[])children.begin();
      Rectangle cullingArea = this.cullingArea;
      float cullLeft;
      float cullRight;
      float offsetY;
      if (cullingArea != null) {
         cullLeft = cullingArea.x;
         cullRight = cullLeft + cullingArea.width;
         float cullBottom = cullingArea.y;
         float cullTop = cullBottom + cullingArea.height;
         if (this.transform) {
            int i = 0;

            for(int n = children.size; i < n; ++i) {
               Actor child = actors[i];
               if (child.isVisible()) {
                  float cx = child.x;
                  float cy = child.y;
                  if (cx <= cullRight && cy <= cullTop && cx + child.width >= cullLeft && cy + child.height >= cullBottom) {
                     child.draw(batch, parentAlpha);
                  }
               }
            }

            batch.flush();
         } else {
            float offsetX = this.x;
            offsetY = this.y;
            this.x = 0.0F;
            this.y = 0.0F;
            int i = 0;

            for(int n = children.size; i < n; ++i) {
               Actor child = actors[i];
               if (child.isVisible()) {
                  float cx = child.x;
                  float cy = child.y;
                  if (cx <= cullRight && cy <= cullTop && cx + child.width >= cullLeft && cy + child.height >= cullBottom) {
                     child.x = cx + offsetX;
                     child.y = cy + offsetY;
                     child.draw(batch, parentAlpha);
                     child.x = cx;
                     child.y = cy;
                  }
               }
            }

            this.x = offsetX;
            this.y = offsetY;
         }
      } else if (this.transform) {
         int i = 0;

         for(int n = children.size; i < n; ++i) {
            Actor child = actors[i];
            if (child.isVisible()) {
               child.draw(batch, parentAlpha);
            }
         }

         batch.flush();
      } else {
         cullLeft = this.x;
         cullRight = this.y;
         this.x = 0.0F;
         this.y = 0.0F;
         int i = 0;

         for(int n = children.size; i < n; ++i) {
            Actor child = actors[i];
            if (child.isVisible()) {
               offsetY = child.x;
               float cy = child.y;
               child.x = offsetY + cullLeft;
               child.y = cy + cullRight;
               child.draw(batch, parentAlpha);
               child.x = offsetY;
               child.y = cy;
            }
         }

         this.x = cullLeft;
         this.y = cullRight;
      }

      children.end();
   }

   protected void applyTransform(SpriteBatch batch, Matrix4 transform) {
      this.oldBatchTransform.set(batch.getTransformMatrix());
      batch.setTransformMatrix(transform);
   }

   protected Matrix4 computeTransform() {
      Matrix3 temp = this.worldTransform;
      float originX = this.originX;
      float originY = this.originY;
      float rotation = this.rotation;
      float scaleX = this.scaleX;
      float scaleY = this.scaleY;
      if (originX == 0.0F && originY == 0.0F) {
         this.localTransform.idt();
      } else {
         this.localTransform.setToTranslation(originX, originY);
      }

      if (rotation != 0.0F) {
         this.localTransform.rotate(rotation);
      }

      if (scaleX != 1.0F || scaleY != 1.0F) {
         this.localTransform.scale(scaleX, scaleY);
      }

      if (originX != 0.0F || originY != 0.0F) {
         this.localTransform.translate(-originX, -originY);
      }

      this.localTransform.trn(this.x, this.y);

      Group parentGroup;
      for(parentGroup = this.getParent(); parentGroup != null && !parentGroup.transform; parentGroup = parentGroup.getParent()) {
      }

      if (parentGroup != null) {
         this.worldTransform.set(parentGroup.worldTransform);
         this.worldTransform.mul(this.localTransform);
      } else {
         this.worldTransform.set(this.localTransform);
      }

      this.batchTransform.set(this.worldTransform);
      return this.batchTransform;
   }

   protected void resetTransform(SpriteBatch batch) {
      batch.setTransformMatrix(this.oldBatchTransform);
   }

   public void setCullingArea(Rectangle cullingArea) {
      this.cullingArea = cullingArea;
   }

   public Actor hit(float x, float y, boolean touchable) {
      if (touchable && this.getTouchable() == Touchable.disabled) {
         return null;
      } else {
         Array<Actor> children = this.children;

         for(int i = children.size - 1; i >= 0; --i) {
            Actor child = (Actor)children.get(i);
            if (child.isVisible()) {
               child.parentToLocalCoordinates(this.point.set(x, y));
               Actor hit = child.hit(this.point.x, this.point.y, touchable);
               if (hit != null) {
                  return hit;
               }
            }
         }

         return super.hit(x, y, touchable);
      }
   }

   protected void childrenChanged() {
   }

   public void addActor(Actor actor) {
      actor.remove();
      this.children.add(actor);
      actor.setParent(this);
      actor.setStage(this.getStage());
      this.childrenChanged();
   }

   public void addActorAt(int index, Actor actor) {
      actor.remove();
      if (index >= this.children.size) {
         this.children.add(actor);
      } else {
         this.children.insert(index, actor);
      }

      actor.setParent(this);
      actor.setStage(this.getStage());
      this.childrenChanged();
   }

   public void addActorBefore(Actor actorBefore, Actor actor) {
      actor.remove();
      int index = this.children.indexOf(actorBefore, true);
      this.children.insert(index, actor);
      actor.setParent(this);
      actor.setStage(this.getStage());
      this.childrenChanged();
   }

   public void addActorAfter(Actor actorAfter, Actor actor) {
      actor.remove();
      int index = this.children.indexOf(actorAfter, true);
      if (index == this.children.size) {
         this.children.add(actor);
      } else {
         this.children.insert(index + 1, actor);
      }

      actor.setParent(this);
      actor.setStage(this.getStage());
      this.childrenChanged();
   }

   public boolean removeActor(Actor actor) {
      if (!this.children.removeValue(actor, true)) {
         return false;
      } else {
         Stage stage = this.getStage();
         if (stage != null) {
            stage.unfocus(actor);
         }

         actor.setParent((Group)null);
         actor.setStage((Stage)null);
         this.childrenChanged();
         return true;
      }
   }

   public void clearChildren() {
      Actor[] actors = (Actor[])this.children.begin();
      int i = 0;

      for(int n = this.children.size; i < n; ++i) {
         Actor child = actors[i];
         child.setStage((Stage)null);
         child.setParent((Group)null);
      }

      this.children.end();
      this.children.clear();
      this.childrenChanged();
   }

   public void clear() {
      super.clear();
      this.clearChildren();
   }

   public Actor findActor(String name) {
      Array<Actor> children = this.children;
      int i = 0;

      int n;
      for(n = children.size; i < n; ++i) {
         if (name.equals(((Actor)children.get(i)).getName())) {
            return (Actor)children.get(i);
         }
      }

      i = 0;

      for(n = children.size; i < n; ++i) {
         Actor child = (Actor)children.get(i);
         if (child instanceof Group) {
            Actor actor = ((Group)child).findActor(name);
            if (actor != null) {
               return actor;
            }
         }
      }

      return null;
   }

   protected void setStage(Stage stage) {
      super.setStage(stage);
      Array<Actor> children = this.children;
      int i = 0;

      for(int n = children.size; i < n; ++i) {
         ((Actor)children.get(i)).setStage(stage);
      }

   }

   public boolean swapActor(int first, int second) {
      int maxIndex = this.children.size;
      if (first >= 0 && first < maxIndex) {
         if (second >= 0 && second < maxIndex) {
            this.children.swap(first, second);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean swapActor(Actor first, Actor second) {
      int firstIndex = this.children.indexOf(first, true);
      int secondIndex = this.children.indexOf(second, true);
      if (firstIndex != -1 && secondIndex != -1) {
         this.children.swap(firstIndex, secondIndex);
         return true;
      } else {
         return false;
      }
   }

   public SnapshotArray<Actor> getChildren() {
      return this.children;
   }

   public boolean hasChildren() {
      return this.children.size > 0;
   }

   public void setTransform(boolean transform) {
      this.transform = transform;
   }

   public boolean isTransform() {
      return this.transform;
   }

   public Vector2 localToDescendantCoordinates(Actor descendant, Vector2 localCoords) {
      Group parent = descendant.getParent();
      if (parent == null) {
         throw new IllegalArgumentException("Child is not a descendant: " + descendant);
      } else {
         if (parent != this) {
            this.localToDescendantCoordinates(parent, localCoords);
         }

         descendant.parentToLocalCoordinates(localCoords);
         return localCoords;
      }
   }

   public void print() {
      this.print("");
   }

   private void print(String indent) {
      Actor[] actors = (Actor[])this.children.begin();
      int i = 0;

      for(int n = this.children.size; i < n; ++i) {
         System.out.println(indent + actors[i]);
         if (actors[i] instanceof Group) {
            ((Group)actors[i]).print(indent + "|  ");
         }
      }

      this.children.end();
   }
}
