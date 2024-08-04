package com.badlogic.gdx.utils;

public class BinaryHeap<T extends BinaryHeap.Node> {
   public int size;
   private BinaryHeap.Node[] nodes;
   private final boolean isMaxHeap;

   public BinaryHeap() {
      this(16, false);
   }

   public BinaryHeap(int capacity, boolean isMaxHeap) {
      this.size = 0;
      this.isMaxHeap = isMaxHeap;
      this.nodes = new BinaryHeap.Node[capacity];
   }

   public T add(T node) {
      if (this.size == this.nodes.length) {
         BinaryHeap.Node[] newNodes = new BinaryHeap.Node[this.size << 1];
         System.arraycopy(this.nodes, 0, newNodes, 0, this.size);
         this.nodes = newNodes;
      }

      node.index = this.size;
      this.nodes[this.size] = node;
      this.up(this.size++);
      return node;
   }

   public BinaryHeap.Node peek() {
      if (this.size == 0) {
         throw new IllegalStateException("The heap is empty.");
      } else {
         return this.nodes[0];
      }
   }

   public T pop() {
      BinaryHeap.Node[] nodes = this.nodes;
      BinaryHeap.Node popped = nodes[0];
      nodes[0] = nodes[--this.size];
      nodes[this.size] = null;
      if (this.size > 0) {
         this.down(0);
      }

      return popped;
   }

   public void setValue(T node, float value) {
      float oldValue = node.value;
      node.value = value;
      if (value < oldValue ^ this.isMaxHeap) {
         this.up(node.index);
      } else {
         this.down(node.index);
      }

   }

   private void up(int index) {
      BinaryHeap.Node[] nodes = this.nodes;
      BinaryHeap.Node node = nodes[index];

      int parentIndex;
      for(float value = node.value; index > 0; index = parentIndex) {
         parentIndex = index - 1 >> 1;
         BinaryHeap.Node parent = nodes[parentIndex];
         if (!(value < parent.value ^ this.isMaxHeap)) {
            break;
         }

         nodes[index] = parent;
         parent.index = index;
      }

      nodes[index] = node;
      node.index = index;
   }

   private void down(int index) {
      BinaryHeap.Node[] nodes = this.nodes;
      int size = this.size;
      BinaryHeap.Node node = nodes[index];
      float value = node.value;

      while(true) {
         int leftIndex = 1 + (index << 1);
         if (leftIndex >= size) {
            break;
         }

         int rightIndex = leftIndex + 1;
         BinaryHeap.Node leftNode = nodes[leftIndex];
         float leftValue = leftNode.value;
         BinaryHeap.Node rightNode;
         float rightValue;
         if (rightIndex >= size) {
            rightNode = null;
            rightValue = this.isMaxHeap ? Float.MIN_VALUE : Float.MAX_VALUE;
         } else {
            rightNode = nodes[rightIndex];
            rightValue = rightNode.value;
         }

         if (leftValue < rightValue ^ this.isMaxHeap) {
            if (leftValue == value || leftValue > value ^ this.isMaxHeap) {
               break;
            }

            nodes[index] = leftNode;
            leftNode.index = index;
            index = leftIndex;
         } else {
            if (rightValue == value || rightValue > value ^ this.isMaxHeap) {
               break;
            }

            nodes[index] = rightNode;
            rightNode.index = index;
            index = rightIndex;
         }
      }

      nodes[index] = node;
      node.index = index;
   }

   public String toString() {
      if (this.size == 0) {
         return "[]";
      } else {
         Object[] nodes = this.nodes;
         StringBuilder buffer = new StringBuilder(32);
         buffer.append('[');
         buffer.append((Object)nodes[0]);

         for(int i = 1; i < this.size; ++i) {
            buffer.append(", ");
            buffer.append((Object)nodes[i]);
         }

         buffer.append(']');
         return buffer.toString();
      }
   }

   public static class Node {
      float value;
      int index;

      public Node(float value) {
         this.value = value;
      }

      public float getValue() {
         return this.value;
      }
   }
}
