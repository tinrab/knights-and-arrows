package com.badlogic.gdx.utils;

public class SortedIntList<E> implements Iterable<SortedIntList.Node<E>> {
   private SortedIntList.NodePool<E> nodePool = new SortedIntList.NodePool();
   private SortedIntList<E>.Iterator iterator;
   int size = 0;
   SortedIntList.Node<E> first;

   public E insert(int index, E value) {
      if (this.first != null) {
         SortedIntList.Node c;
         for(c = this.first; c.n != null && c.n.index <= index; c = c.n) {
         }

         if (index > c.index) {
            c.n = this.nodePool.obtain(c, c.n, value, index);
            if (c.n.n != null) {
               c.n.n.p = c.n;
            }

            ++this.size;
         } else if (index < c.index) {
            SortedIntList.Node<E> newFirst = this.nodePool.obtain((SortedIntList.Node)null, this.first, value, index);
            this.first.p = newFirst;
            this.first = newFirst;
            ++this.size;
         } else {
            c.value = value;
         }
      } else {
         this.first = this.nodePool.obtain((SortedIntList.Node)null, (SortedIntList.Node)null, value, index);
         ++this.size;
      }

      return null;
   }

   public E get(int index) {
      E match = null;
      if (this.first != null) {
         SortedIntList.Node c;
         for(c = this.first; c.n != null && c.index < index; c = c.n) {
         }

         if (c.index == index) {
            match = c.value;
         }
      }

      return match;
   }

   public void clear() {
      while(this.first != null) {
         this.nodePool.free(this.first);
         this.first = this.first.n;
      }

      this.size = 0;
   }

   public int size() {
      return this.size;
   }

   public java.util.Iterator<SortedIntList.Node<E>> iterator() {
      if (this.iterator == null) {
         this.iterator = new SortedIntList.Iterator();
      }

      return this.iterator.reset();
   }

   class Iterator implements java.util.Iterator<SortedIntList.Node<E>> {
      private SortedIntList.Node<E> position;
      private SortedIntList.Node<E> previousPosition;

      public boolean hasNext() {
         return this.position != null;
      }

      public SortedIntList.Node<E> next() {
         this.previousPosition = this.position;
         this.position = this.position.n;
         return this.previousPosition;
      }

      public void remove() {
         if (this.previousPosition != null) {
            if (this.previousPosition == SortedIntList.this.first) {
               SortedIntList.this.first = this.position;
            } else {
               this.previousPosition.p.n = this.position;
               if (this.position != null) {
                  this.position.p = this.previousPosition.p;
               }
            }

            --SortedIntList.this.size;
         }

      }

      public SortedIntList<E>.Iterator reset() {
         this.position = SortedIntList.this.first;
         this.previousPosition = null;
         return this;
      }
   }

   public static class Node<E> {
      protected SortedIntList.Node<E> p;
      protected SortedIntList.Node<E> n;
      public E value;
      public int index;
   }

   static class NodePool<E> extends Pool<SortedIntList.Node<E>> {
      protected SortedIntList.Node<E> newObject() {
         return new SortedIntList.Node();
      }

      public SortedIntList.Node<E> obtain(SortedIntList.Node<E> p, SortedIntList.Node<E> n, E value, int index) {
         SortedIntList.Node<E> newNode = (SortedIntList.Node)super.obtain();
         newNode.p = p;
         newNode.n = n;
         newNode.value = value;
         newNode.index = index;
         return newNode;
      }
   }
}
