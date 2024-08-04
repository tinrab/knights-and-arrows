package com.badlogic.gdx.utils;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class AtomicQueue<T> {
   private final AtomicInteger writeIndex = new AtomicInteger();
   private final AtomicInteger readIndex = new AtomicInteger();
   private final AtomicReferenceArray<T> queue;

   public AtomicQueue(int capacity) {
      this.queue = new AtomicReferenceArray(capacity);
   }

   private int next(int idx) {
      return idx + 1 & this.queue.length() - 1;
   }

   public boolean put(T value) {
      int write = this.writeIndex.get();
      int read = this.readIndex.get();
      int next = this.next(write);
      if (next == read) {
         return false;
      } else {
         this.queue.set(write, value);
         this.writeIndex.set(next);
         return true;
      }
   }

   public T poll() {
      int read = this.readIndex.get();
      int write = this.writeIndex.get();
      if (read == write) {
         return null;
      } else {
         T value = this.queue.get(read);
         this.readIndex.set(this.next(read));
         return value;
      }
   }
}
