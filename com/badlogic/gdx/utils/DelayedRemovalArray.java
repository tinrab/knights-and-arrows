package com.badlogic.gdx.utils;

import java.util.Comparator;

public class DelayedRemovalArray<T> extends Array<T> {
   private boolean iterating;
   private IntArray remove = new IntArray(0);

   public DelayedRemovalArray() {
   }

   public DelayedRemovalArray(Array array) {
      super(array);
   }

   public DelayedRemovalArray(boolean ordered, int capacity, Class<T> arrayType) {
      super(ordered, capacity, arrayType);
   }

   public DelayedRemovalArray(boolean ordered, int capacity) {
      super(ordered, capacity);
   }

   public DelayedRemovalArray(boolean ordered, T[] array, int startIndex, int count) {
      super(ordered, array, startIndex, count);
   }

   public DelayedRemovalArray(Class<T> arrayType) {
      super(arrayType);
   }

   public DelayedRemovalArray(int capacity) {
      super(capacity);
   }

   public DelayedRemovalArray(T[] array) {
      super(array);
   }

   public void begin() {
      this.iterating = true;
   }

   public void end() {
      this.iterating = false;
      int i = 0;

      for(int n = this.remove.size; i < n; ++i) {
         this.removeIndex(this.remove.pop());
      }

   }

   private void remove(int index) {
      int i = 0;

      for(int n = this.remove.size; i < n; ++i) {
         int removeIndex = this.remove.get(i);
         if (index == removeIndex) {
            return;
         }

         if (index < removeIndex) {
            this.remove.insert(i, index);
            return;
         }
      }

      this.remove.add(index);
   }

   public boolean removeValue(T value, boolean identity) {
      if (this.iterating) {
         int index = this.indexOf(value, identity);
         if (index == -1) {
            return false;
         } else {
            this.remove(index);
            return true;
         }
      } else {
         return super.removeValue(value, identity);
      }
   }

   public T removeIndex(int index) {
      if (this.iterating) {
         this.remove(index);
         return this.get(index);
      } else {
         return super.removeIndex(index);
      }
   }

   public void set(int index, T value) {
      if (this.iterating) {
         throw new IllegalStateException("Invalid between begin/end.");
      } else {
         super.set(index, value);
      }
   }

   public void insert(int index, T value) {
      if (this.iterating) {
         throw new IllegalStateException("Invalid between begin/end.");
      } else {
         super.insert(index, value);
      }
   }

   public void swap(int first, int second) {
      if (this.iterating) {
         throw new IllegalStateException("Invalid between begin/end.");
      } else {
         super.swap(first, second);
      }
   }

   public T pop() {
      if (this.iterating) {
         throw new IllegalStateException("Invalid between begin/end.");
      } else {
         return super.pop();
      }
   }

   public void clear() {
      if (this.iterating) {
         throw new IllegalStateException("Invalid between begin/end.");
      } else {
         super.clear();
      }
   }

   public void sort() {
      if (this.iterating) {
         throw new IllegalStateException("Invalid between begin/end.");
      } else {
         super.sort();
      }
   }

   public void sort(Comparator<T> comparator) {
      if (this.iterating) {
         throw new IllegalStateException("Invalid between begin/end.");
      } else {
         super.sort(comparator);
      }
   }

   public void reverse() {
      if (this.iterating) {
         throw new IllegalStateException("Invalid between begin/end.");
      } else {
         super.reverse();
      }
   }

   public void shuffle() {
      if (this.iterating) {
         throw new IllegalStateException("Invalid between begin/end.");
      } else {
         super.shuffle();
      }
   }

   public void truncate(int newSize) {
      if (this.iterating) {
         throw new IllegalStateException("Invalid between begin/end.");
      } else {
         super.truncate(newSize);
      }
   }
}
