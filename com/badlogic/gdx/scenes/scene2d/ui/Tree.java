package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;

public class Tree extends WidgetGroup {
   Tree.TreeStyle style;
   final Array<Tree.Node> rootNodes;
   final Array<Tree.Node> selectedNodes;
   float ySpacing;
   float iconSpacing;
   float padding;
   float indentSpacing;
   private float leftColumnWidth;
   private float prefWidth;
   private float prefHeight;
   private boolean sizeInvalid;
   boolean multiSelect;
   boolean toggleSelect;
   private Tree.Node foundNode;
   Tree.Node overNode;
   private ClickListener clickListener;

   public Tree(Skin skin) {
      this((Tree.TreeStyle)skin.get(Tree.TreeStyle.class));
   }

   public Tree(Skin skin, String styleName) {
      this((Tree.TreeStyle)skin.get(styleName, Tree.TreeStyle.class));
   }

   public Tree(Tree.TreeStyle style) {
      this.rootNodes = new Array();
      this.selectedNodes = new Array();
      this.ySpacing = 4.0F;
      this.iconSpacing = 2.0F;
      this.padding = 0.0F;
      this.sizeInvalid = true;
      this.multiSelect = true;
      this.toggleSelect = true;
      this.setStyle(style);
      this.initialize();
   }

   private void initialize() {
      this.addListener(this.clickListener = new ClickListener() {
         public void clicked(InputEvent event, float x, float y) {
            Tree.Node node = Tree.this.getNodeAt(y);
            if (node != null) {
               if (node == Tree.this.getNodeAt(this.getTouchDownY())) {
                  float rowX;
                  if (!Tree.this.multiSelect || Tree.this.selectedNodes.size <= 0 || !Gdx.input.isKeyPressed(59) && !Gdx.input.isKeyPressed(60)) {
                     if (Tree.this.multiSelect && (Gdx.input.isKeyPressed(129) || Gdx.input.isKeyPressed(130))) {
                        if (!node.isSelectable()) {
                           return;
                        }
                     } else {
                        if (node.children.size > 0) {
                           rowX = node.actor.getX();
                           if (node.icon != null) {
                              rowX -= Tree.this.iconSpacing + node.icon.getMinWidth();
                           }

                           if (x < rowX) {
                              node.setExpanded(!node.expanded);
                              return;
                           }
                        }

                        if (!node.isSelectable()) {
                           return;
                        }

                        boolean unselect = Tree.this.toggleSelect && Tree.this.selectedNodes.size == 1 && Tree.this.selectedNodes.contains(node, true);
                        Tree.this.selectedNodes.clear();
                        if (unselect) {
                           Tree.this.fireChangeEvent();
                           return;
                        }
                     }

                     if (!Tree.this.selectedNodes.removeValue(node, true)) {
                        Tree.this.selectedNodes.add(node);
                     }

                     Tree.this.fireChangeEvent();
                  } else {
                     rowX = ((Tree.Node)Tree.this.selectedNodes.first()).actor.getY();
                     float high = node.actor.getY();
                     if (!Gdx.input.isKeyPressed(129) && !Gdx.input.isKeyPressed(130)) {
                        Tree.this.selectedNodes.clear();
                     }

                     if (rowX > high) {
                        Tree.this.selectNodes(Tree.this.rootNodes, high, rowX);
                     } else {
                        Tree.this.selectNodes(Tree.this.rootNodes, rowX, high);
                     }

                     Tree.this.fireChangeEvent();
                  }
               }
            }
         }

         public boolean mouseMoved(InputEvent event, float x, float y) {
            Tree.this.setOverNode(Tree.this.getNodeAt(y));
            return false;
         }

         public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
            super.exit(event, x, y, pointer, toActor);
            if (toActor == null || !toActor.isDescendantOf(Tree.this)) {
               Tree.this.setOverNode((Tree.Node)null);
            }

         }
      });
   }

   public void setStyle(Tree.TreeStyle style) {
      this.style = style;
      this.indentSpacing = Math.max(style.plus.getMinWidth(), style.minus.getMinWidth()) + this.iconSpacing;
   }

   public void add(Tree.Node node) {
      this.insert(this.rootNodes.size, node);
   }

   public void insert(int index, Tree.Node node) {
      this.remove(node);
      node.parent = null;
      this.rootNodes.insert(index, node);
      node.addToTree(this);
      this.invalidateHierarchy();
   }

   public void remove(Tree.Node node) {
      if (node.parent != null) {
         node.parent.remove(node);
      } else {
         this.rootNodes.removeValue(node, true);
         node.removeFromTree(this);
         this.invalidateHierarchy();
      }
   }

   public void clearChildren() {
      super.clearChildren();
      this.rootNodes.clear();
      this.selectedNodes.clear();
      this.setOverNode((Tree.Node)null);
      this.fireChangeEvent();
   }

   void fireChangeEvent() {
      ChangeListener.ChangeEvent changeEvent = (ChangeListener.ChangeEvent)Pools.obtain(ChangeListener.ChangeEvent.class);
      this.fire(changeEvent);
      Pools.free(changeEvent);
   }

   public Array<Tree.Node> getNodes() {
      return this.rootNodes;
   }

   public void invalidate() {
      super.invalidate();
      this.sizeInvalid = true;
   }

   private void computeSize() {
      this.sizeInvalid = false;
      this.prefWidth = this.style.plus.getMinWidth();
      this.prefWidth = Math.max(this.prefWidth, this.style.minus.getMinWidth());
      this.prefHeight = this.getHeight();
      this.leftColumnWidth = 0.0F;
      this.computeSize(this.rootNodes, this.indentSpacing);
      this.leftColumnWidth += this.iconSpacing + this.padding;
      this.prefWidth += this.leftColumnWidth + this.padding;
      this.prefHeight = this.getHeight() - this.prefHeight;
   }

   private void computeSize(Array<Tree.Node> nodes, float indent) {
      float ySpacing = this.ySpacing;
      int i = 0;

      for(int n = nodes.size; i < n; ++i) {
         Tree.Node node = (Tree.Node)nodes.get(i);
         float rowWidth = indent + this.iconSpacing;
         Actor actor = node.actor;
         if (actor instanceof Layout) {
            Layout layout = (Layout)actor;
            rowWidth += layout.getPrefWidth();
            node.height = layout.getPrefHeight();
            layout.pack();
         } else {
            rowWidth += actor.getWidth();
            node.height = actor.getHeight();
         }

         if (node.icon != null) {
            rowWidth += this.iconSpacing * 2.0F + node.icon.getMinWidth();
            node.height = Math.max(node.height, node.icon.getMinHeight());
         }

         this.prefWidth = Math.max(this.prefWidth, rowWidth);
         this.prefHeight -= node.height + ySpacing;
         if (node.expanded) {
            this.computeSize(node.children, indent + this.indentSpacing);
         }
      }

   }

   public void layout() {
      if (this.sizeInvalid) {
         this.computeSize();
      }

      this.layout(this.rootNodes, this.leftColumnWidth + this.indentSpacing + this.iconSpacing, this.getHeight() - this.ySpacing / 2.0F);
   }

   private float layout(Array<Tree.Node> nodes, float indent, float y) {
      float ySpacing = this.ySpacing;
      Drawable plus = this.style.plus;
      Drawable minus = this.style.minus;
      int i = 0;

      for(int n = nodes.size; i < n; ++i) {
         Tree.Node node = (Tree.Node)nodes.get(i);
         Actor actor = node.actor;
         float x = indent;
         if (node.icon != null) {
            x = indent + node.icon.getMinWidth();
         }

         y -= node.height;
         node.actor.setPosition(x, y);
         y -= ySpacing;
         if (node.expanded) {
            y = this.layout(node.children, indent + this.indentSpacing, y);
         }
      }

      return y;
   }

   public void draw(SpriteBatch batch, float parentAlpha) {
      Color color = this.getColor();
      if (this.style.background != null) {
         batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
         this.style.background.draw(batch, this.getX(), this.getY(), this.getWidth(), this.getHeight());
         batch.setColor(Color.WHITE);
      }

      this.draw(batch, this.rootNodes, this.leftColumnWidth);
      super.draw(batch, parentAlpha);
   }

   private void draw(SpriteBatch batch, Array<Tree.Node> nodes, float indent) {
      Drawable plus = this.style.plus;
      Drawable minus = this.style.minus;
      float x = this.getX();
      float y = this.getY();
      int i = 0;

      for(int n = nodes.size; i < n; ++i) {
         Tree.Node node = (Tree.Node)nodes.get(i);
         Actor actor = node.actor;
         if (this.selectedNodes.contains(node, true) && this.style.selection != null) {
            this.style.selection.draw(batch, x, y + actor.getY() - this.ySpacing / 2.0F, this.getWidth(), node.height + this.ySpacing);
         } else if (node == this.overNode && this.style.over != null) {
            this.style.over.draw(batch, x, y + actor.getY() - this.ySpacing / 2.0F, this.getWidth(), node.height + this.ySpacing);
         }

         if (node.icon != null) {
            float iconY = actor.getY() + (float)Math.round((node.height - node.icon.getMinHeight()) / 2.0F);
            batch.setColor(actor.getColor());
            node.icon.draw(batch, x + node.actor.getX() - this.iconSpacing - node.icon.getMinWidth(), y + iconY, node.icon.getMinWidth(), node.icon.getMinHeight());
            batch.setColor(Color.WHITE);
         }

         if (node.children.size != 0) {
            Drawable expandIcon = node.expanded ? minus : plus;
            float iconY = actor.getY() + (float)Math.round((node.height - expandIcon.getMinHeight()) / 2.0F);
            expandIcon.draw(batch, x + indent - this.iconSpacing, y + iconY, expandIcon.getMinWidth(), expandIcon.getMinHeight());
            if (node.expanded) {
               this.draw(batch, node.children, indent + this.indentSpacing);
            }
         }
      }

   }

   public Tree.Node getNodeAt(float y) {
      this.foundNode = null;
      this.getNodeAt(this.rootNodes, y, this.getHeight());
      return this.foundNode;
   }

   private float getNodeAt(Array<Tree.Node> nodes, float y, float rowY) {
      int i = 0;

      for(int n = nodes.size; i < n; ++i) {
         Tree.Node node = (Tree.Node)nodes.get(i);
         if (y >= rowY - node.height - this.ySpacing && y < rowY) {
            this.foundNode = node;
            return -1.0F;
         }

         rowY -= node.height + this.ySpacing;
         if (node.expanded) {
            rowY = this.getNodeAt(node.children, y, rowY);
            if (rowY == -1.0F) {
               return -1.0F;
            }
         }
      }

      return rowY;
   }

   void selectNodes(Array<Tree.Node> nodes, float low, float high) {
      float ySpacing = this.ySpacing;
      int i = 0;

      for(int n = nodes.size; i < n; ++i) {
         Tree.Node node = (Tree.Node)nodes.get(i);
         if (node.actor.getY() < low) {
            break;
         }

         if (node.isSelectable()) {
            if (node.actor.getY() <= high) {
               this.selectedNodes.add(node);
            }

            if (node.expanded) {
               this.selectNodes(node.children, low, high);
            }
         }
      }

   }

   public Array<Tree.Node> getSelection() {
      return this.selectedNodes;
   }

   public void setSelection(Tree.Node node) {
      this.selectedNodes.clear();
      this.selectedNodes.add(node);
      this.fireChangeEvent();
   }

   public void setSelection(Array<Tree.Node> nodes) {
      this.selectedNodes.clear();
      this.selectedNodes.addAll(nodes);
      this.fireChangeEvent();
   }

   public void addSelection(Tree.Node node) {
      this.selectedNodes.add(node);
      this.fireChangeEvent();
   }

   public void clearSelection() {
      this.selectedNodes.clear();
      this.fireChangeEvent();
   }

   public Tree.TreeStyle getStyle() {
      return this.style;
   }

   public Array<Tree.Node> getRootNodes() {
      return this.rootNodes;
   }

   public Tree.Node getOverNode() {
      return this.overNode;
   }

   public void setOverNode(Tree.Node overNode) {
      this.overNode = overNode;
   }

   public void setPadding(float padding) {
      this.padding = padding;
   }

   public void setYSpacing(float ySpacing) {
      this.ySpacing = ySpacing;
   }

   public void setIconSpacing(float iconSpacing) {
      this.iconSpacing = iconSpacing;
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

   public void findExpandedObjects(Array objects) {
      findExpandedObjects(this.rootNodes, objects);
   }

   public void restoreExpandedObjects(Array objects) {
      int i = 0;

      for(int n = objects.size; i < n; ++i) {
         Tree.Node node = this.findNode(objects.get(i));
         if (node != null) {
            node.setExpanded(true);
            node.expandTo();
         }
      }

   }

   static boolean findExpandedObjects(Array<Tree.Node> nodes, Array objects) {
      boolean expanded = false;
      int i = 0;

      for(int n = nodes.size; i < n; ++i) {
         Tree.Node node = (Tree.Node)nodes.get(i);
         if (node.expanded && !findExpandedObjects(node.children, objects)) {
            objects.add(node.object);
         }
      }

      return expanded;
   }

   public Tree.Node findNode(Object object) {
      if (object == null) {
         throw new IllegalArgumentException("object cannot be null.");
      } else {
         return findNode(this.rootNodes, object);
      }
   }

   static Tree.Node findNode(Array<Tree.Node> nodes, Object object) {
      int i = 0;

      int n;
      Tree.Node node;
      for(n = nodes.size; i < n; ++i) {
         node = (Tree.Node)nodes.get(i);
         if (object.equals(node.object)) {
            return node;
         }
      }

      i = 0;

      for(n = nodes.size; i < n; ++i) {
         node = (Tree.Node)nodes.get(i);
         Tree.Node found = findNode(node.children, object);
         if (found != null) {
            return found;
         }
      }

      return null;
   }

   public void collapseAll() {
      collapseAll(this.rootNodes);
   }

   static void collapseAll(Array<Tree.Node> nodes) {
      int i = 0;

      for(int n = nodes.size; i < n; ++i) {
         Tree.Node node = (Tree.Node)nodes.get(i);
         node.setExpanded(false);
         collapseAll(node.children);
      }

   }

   public void expandAll() {
      expandAll(this.rootNodes);
   }

   static void expandAll(Array<Tree.Node> nodes) {
      int i = 0;

      for(int n = nodes.size; i < n; ++i) {
         ((Tree.Node)nodes.get(i)).expandAll();
      }

   }

   public ClickListener getClickListener() {
      return this.clickListener;
   }

   public void setMultiSelect(boolean multiSelect) {
      this.multiSelect = multiSelect;
   }

   public void setToggleSelect(boolean toggleSelect) {
      this.toggleSelect = toggleSelect;
   }

   public static class Node {
      Actor actor;
      Tree.Node parent;
      final Array<Tree.Node> children = new Array(0);
      boolean selectable = true;
      boolean expanded;
      Drawable icon;
      float height;
      Object object;

      public Node(Actor actor) {
         if (actor == null) {
            throw new IllegalArgumentException("actor cannot be null.");
         } else {
            this.actor = actor;
         }
      }

      public void setExpanded(boolean expanded) {
         if (expanded != this.expanded) {
            this.expanded = expanded;
            if (this.children.size != 0) {
               Tree tree = this.getTree();
               if (tree != null) {
                  int i;
                  int n;
                  if (expanded) {
                     i = 0;

                     for(n = this.children.size; i < n; ++i) {
                        ((Tree.Node)this.children.get(i)).addToTree(tree);
                     }
                  } else {
                     i = 0;

                     for(n = this.children.size; i < n; ++i) {
                        ((Tree.Node)this.children.get(i)).removeFromTree(tree);
                     }
                  }

                  tree.invalidateHierarchy();
               }
            }
         }
      }

      protected void addToTree(Tree tree) {
         tree.addActor(this.actor);
         if (this.expanded) {
            int i = 0;

            for(int n = this.children.size; i < n; ++i) {
               ((Tree.Node)this.children.get(i)).addToTree(tree);
            }

         }
      }

      protected void removeFromTree(Tree tree) {
         tree.removeActor(this.actor);
         if (this.expanded) {
            int i = 0;

            for(int n = this.children.size; i < n; ++i) {
               ((Tree.Node)this.children.get(i)).removeFromTree(tree);
            }

         }
      }

      public void add(Tree.Node node) {
         this.insert(this.children.size, node);
      }

      public void addAll(Array<Tree.Node> nodes) {
         int i = 0;

         for(int n = nodes.size; i < n; ++i) {
            this.insert(this.children.size, (Tree.Node)nodes.get(i));
         }

      }

      public void insert(int index, Tree.Node node) {
         node.parent = this;
         this.children.insert(index, node);
         if (this.expanded) {
            Tree tree = this.getTree();
            if (tree != null) {
               int i = 0;

               for(int n = this.children.size; i < n; ++i) {
                  ((Tree.Node)this.children.get(i)).addToTree(tree);
               }

            }
         }
      }

      public void remove() {
         Tree tree = this.getTree();
         if (tree != null) {
            tree.remove(this);
         }
      }

      public void remove(Tree.Node node) {
         this.children.removeValue(node, true);
         if (this.expanded) {
            Tree tree = this.getTree();
            if (tree != null) {
               node.removeFromTree(tree);
               if (this.children.size == 0) {
                  this.expanded = false;
               }

            }
         }
      }

      public void removeAll() {
         Tree tree = this.getTree();
         if (tree != null) {
            int i = 0;

            for(int n = this.children.size; i < n; ++i) {
               ((Tree.Node)this.children.get(i)).removeFromTree(tree);
            }
         }

         this.children.clear();
      }

      public Tree getTree() {
         Group parent = this.actor.getParent();
         return !(parent instanceof Tree) ? null : (Tree)parent;
      }

      public Actor getActor() {
         return this.actor;
      }

      public boolean isExpanded() {
         return this.expanded;
      }

      public Array<Tree.Node> getChildren() {
         return this.children;
      }

      public Tree.Node getParent() {
         return this.parent;
      }

      public void setIcon(Drawable icon) {
         this.icon = icon;
      }

      public Object getObject() {
         return this.object;
      }

      public void setObject(Object object) {
         this.object = object;
      }

      public Drawable getIcon() {
         return this.icon;
      }

      public Tree.Node findNode(Object object) {
         if (object == null) {
            throw new IllegalArgumentException("object cannot be null.");
         } else {
            return object.equals(this.object) ? this : Tree.findNode(this.children, object);
         }
      }

      public void collapseAll() {
         this.setExpanded(false);
         Tree.collapseAll(this.children);
      }

      public void expandAll() {
         this.setExpanded(true);
         if (this.children.size > 0) {
            Tree.expandAll(this.children);
         }

      }

      public void expandTo() {
         for(Tree.Node node = this.parent; node != null; node = node.parent) {
            node.setExpanded(true);
         }

      }

      public boolean isSelectable() {
         return this.selectable;
      }

      public void setSelectable(boolean selectable) {
         this.selectable = selectable;
      }
   }

   public static class TreeStyle {
      public Drawable plus;
      public Drawable minus;
      public Drawable over;
      public Drawable selection;
      public Drawable background;

      public TreeStyle() {
      }

      public TreeStyle(Drawable plus, Drawable minus, Drawable selection) {
         this.plus = plus;
         this.minus = minus;
         this.selection = selection;
      }

      public TreeStyle(Tree.TreeStyle style) {
         this.plus = style.plus;
         this.minus = style.minus;
         this.selection = style.selection;
      }
   }
}
