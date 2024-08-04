package com.badlogic.gdx.utils;

public class PooledLinkedList<T> {
   private PooledLinkedList.Item<T> head;
   private PooledLinkedList.Item<T> tail;
   private PooledLinkedList.Item<T> iter;
   private PooledLinkedList.Item<T> curr;
   private int size = 0;
   private final Pool<PooledLinkedList.Item<T>> pool;

   public PooledLinkedList(int maxPoolSize) {
      this.pool = new Pool<PooledLinkedList.Item<T>>(16, maxPoolSize) {
         protected PooledLinkedList.Item<T> newObject() {
            return new PooledLinkedList.Item();
         }
      };
   }

   public void add(T object) {
      PooledLinkedList.Item<T> item = (PooledLinkedList.Item)this.pool.obtain();
      item.payload = object;
      item.next = null;
      item.prev = null;
      if (this.head == null) {
         this.head = item;
         this.tail = item;
         ++this.size;
      } else {
         item.prev = this.tail;
         this.tail.next = item;
         this.tail = item;
         ++this.size;
      }
   }

   public void iter() {
      this.iter = this.head;
   }

   public T next() {
      if (this.iter == null) {
         return null;
      } else {
         T payload = this.iter.payload;
         this.curr = this.iter;
         this.iter = this.iter.next;
         return payload;
      }
   }

   public void remove() {
      if (this.curr != null) {
         --this.size;
         this.pool.free(this.curr);
         PooledLinkedList.Item<T> c = this.curr;
         PooledLinkedList.Item<T> n = this.curr.next;
         PooledLinkedList.Item<T> p = this.curr.prev;
         this.curr = null;
         if (this.size == 0) {
            this.head = null;
            this.tail = null;
         } else if (c == this.head) {
            n.prev = null;
            this.head = n;
         } else if (c == this.tail) {
            p.next = null;
            this.tail = p;
         } else {
            p.next = n;
            n.prev = p;
         }
      }
   }

   public void clear() {
      this.iter();
      Object var1 = null;

      while(this.next() != null) {
         this.remove();
      }

   }

   static final class Item<T> {
      public T payload;
      public PooledLinkedList.Item<T> next;
      public PooledLinkedList.Item<T> prev;
   }
}
