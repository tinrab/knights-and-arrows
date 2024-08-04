package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.SnapshotArray;

public class VerticalGroup extends WidgetGroup {
   private float prefWidth;
   private float prefHeight;
   private boolean sizeInvalid = true;
   private int alignment;
   private boolean reverse;

   public VerticalGroup() {
      this.setTouchable(Touchable.childrenOnly);
   }

   public void setAlignment(int alignment) {
      this.alignment = alignment;
   }

   public void setReverse(boolean reverse) {
      this.reverse = reverse;
   }

   public void invalidate() {
      super.invalidate();
      this.sizeInvalid = true;
   }

   private void computeSize() {
      this.sizeInvalid = false;
      this.prefWidth = 0.0F;
      this.prefHeight = 0.0F;
      SnapshotArray<Actor> children = this.getChildren();
      int i = 0;

      for(int n = children.size; i < n; ++i) {
         Actor child = (Actor)children.get(i);
         if (child instanceof Layout) {
            Layout layout = (Layout)child;
            this.prefWidth = Math.max(this.prefWidth, layout.getPrefWidth());
            this.prefHeight += layout.getPrefHeight();
         } else {
            this.prefWidth = Math.max(this.prefWidth, child.getWidth());
            this.prefHeight += child.getHeight();
         }
      }

   }

   public void layout() {
      float groupWidth = this.getWidth();
      float y = this.reverse ? 0.0F : this.getHeight();
      float dir = (float)(this.reverse ? 1 : -1);
      SnapshotArray<Actor> children = this.getChildren();
      int i = 0;

      for(int n = children.size; i < n; ++i) {
         Actor child = (Actor)children.get(i);
         float width;
         float height;
         if (child instanceof Layout) {
            Layout layout = (Layout)child;
            width = layout.getPrefWidth();
            height = layout.getPrefHeight();
         } else {
            width = child.getWidth();
            height = child.getHeight();
         }

         float x;
         if ((this.alignment & 8) != 0) {
            x = 0.0F;
         } else if ((this.alignment & 16) != 0) {
            x = groupWidth - width;
         } else {
            x = (groupWidth - width) / 2.0F;
         }

         if (!this.reverse) {
            y += height * dir;
         }

         child.setBounds(x, y, width, height);
         if (this.reverse) {
            y += height * dir;
         }
      }

   }

   public float getPrefWidth() {
      if (this.sizeInvalid) {
         this.computeSize();
      }

      return this.prefWidth;
   }

   public float getPrefHeight() {
      if (this.sizeInvalid) {
         this.computeSize();
      }

      return this.prefHeight;
   }
}
