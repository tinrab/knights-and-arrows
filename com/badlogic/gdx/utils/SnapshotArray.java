package com.badlogic.gdx.utils;

import java.util.Comparator;

public class SnapshotArray<T> extends Array<T> {
   private T[] snapshot;
   private T[] recycled;
   private int snapshots;

   public SnapshotArray() {
   }

   public SnapshotArray(Array array) {
      super(array);
   }

   public SnapshotArray(boolean ordered, int capacity, Class<T> arrayType) {
      super(ordered, capacity, arrayType);
   }

   public SnapshotArray(boolean ordered, int capacity) {
      super(ordered, capacity);
   }

   public SnapshotArray(boolean ordered, T[] array, int startIndex, int count) {
      super(ordered, array, startIndex, count);
   }

   public SnapshotArray(Class<T> arrayType) {
      super(arrayType);
   }

   public SnapshotArray(int capacity) {
      super(capacity);
   }

   public SnapshotArray(T[] array) {
      super(array);
   }

   public T[] begin() {
      this.modified();
      this.snapshot = this.items;
      ++this.snapshots;
      return this.items;
   }

   public void end() {
      this.snapshots = Math.max(0, this.snapshots - 1);
      if (this.snapshot != null) {
         if (this.snapshot != this.items && this.snapshots == 0) {
            this.recycled = this.snapshot;
            int i = 0;

            for(int n = this.recycled.length; i < n; ++i) {
               this.recycled[i] = null;
            }
         }

         this.snapshot = null;
      }
   }

   private void modified() {
      if (this.snapshot != null && this.snapshot == this.items) {
         if (this.recycled != null && this.recycled.length >= this.size) {
            System.arraycopy(this.items, 0, this.recycled, 0, this.size);
            this.items = this.recycled;
            this.recycled = null;
         } else {
            this.resize(this.items.length);
         }

      }
   }

   public void set(int index, T value) {
      this.modified();
      super.set(index, value);
   }

   public void insert(int index, T value) {
      this.modified();
      super.insert(index, value);
   }

   public void swap(int first, int second) {
      this.modified();
      super.swap(first, second);
   }

   public boolean removeValue(T value, boolean identity) {
      this.modified();
      return super.removeValue(value, identity);
   }

   public T removeIndex(int index) {
      this.modified();
      return super.removeIndex(index);
   }

   public T pop() {
      this.modified();
      return super.pop();
   }

   public void clear() {
      this.modified();
      super.clear();
   }

   public void sort() {
      this.modified();
      super.sort();
   }

   public void sort(Comparator<T> comparator) {
      this.modified();
      super.sort(comparator);
   }

   public void reverse() {
      this.modified();
      super.reverse();
   }

   public void shuffle() {
      this.modified();
      super.shuffle();
   }

   public void truncate(int newSize) {
      this.modified();
      super.truncate(newSize);
   }
}
