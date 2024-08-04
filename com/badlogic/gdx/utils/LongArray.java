package com.badlogic.gdx.utils;

import com.badlogic.gdx.math.MathUtils;
import java.util.Arrays;

public class LongArray {
   public long[] items;
   public int size;
   public boolean ordered;

   public LongArray() {
      this(true, 16);
   }

   public LongArray(int capacity) {
      this(true, capacity);
   }

   public LongArray(boolean ordered, int capacity) {
      this.ordered = ordered;
      this.items = new long[capacity];
   }

   public LongArray(LongArray array) {
      this.ordered = array.ordered;
      this.size = array.size;
      this.items = new long[this.size];
      System.arraycopy(array.items, 0, this.items, 0, this.size);
   }

   public LongArray(long[] array) {
      this(true, array, 0, array.length);
   }

   public LongArray(boolean ordered, long[] array, int startIndex, int count) {
      this(ordered, array.length);
      this.size = count;
      System.arraycopy(array, startIndex, this.items, 0, count);
   }

   public void add(long value) {
      long[] items = this.items;
      if (this.size == items.length) {
         items = this.resize(Math.max(8, (int)((float)this.size * 1.75F)));
      }

      items[this.size++] = value;
   }

   public void addAll(LongArray array) {
      this.addAll((LongArray)array, 0, array.size);
   }

   public void addAll(LongArray array, int offset, int length) {
      if (offset + length > array.size) {
         throw new IllegalArgumentException("offset + length must be <= size: " + offset + " + " + length + " <= " + array.size);
      } else {
         this.addAll(array.items, offset, length);
      }
   }

   public void addAll(long[] array) {
      this.addAll((long[])array, 0, array.length);
   }

   public void addAll(long[] array, int offset, int length) {
      long[] items = this.items;
      int sizeNeeded = this.size + length;
      if (sizeNeeded >= items.length) {
         items = this.resize(Math.max(8, (int)((float)sizeNeeded * 1.75F)));
      }

      System.arraycopy(array, offset, items, this.size, length);
      this.size += length;
   }

   public long get(int index) {
      if (index >= this.size) {
         throw new IndexOutOfBoundsException(String.valueOf(index));
      } else {
         return this.items[index];
      }
   }

   public void set(int index, long value) {
      if (index >= this.size) {
         throw new IndexOutOfBoundsException(String.valueOf(index));
      } else {
         this.items[index] = value;
      }
   }

   public void insert(int index, long value) {
      if (index > this.size) {
         throw new IndexOutOfBoundsException(String.valueOf(index));
      } else {
         long[] items = this.items;
         if (this.size == items.length) {
            items = this.resize(Math.max(8, (int)((float)this.size * 1.75F)));
         }

         if (this.ordered) {
            System.arraycopy(items, index, items, index + 1, this.size - index);
         } else {
            items[this.size] = items[index];
         }

         ++this.size;
         items[index] = value;
      }
   }

   public void swap(int first, int second) {
      if (first >= this.size) {
         throw new IndexOutOfBoundsException(String.valueOf(first));
      } else if (second >= this.size) {
         throw new IndexOutOfBoundsException(String.valueOf(second));
      } else {
         long[] items = this.items;
         long firstValue = items[first];
         items[first] = items[second];
         items[second] = firstValue;
      }
   }

   public boolean contains(long value) {
      int i = this.size - 1;
      long[] items = this.items;

      while(i >= 0) {
         if (items[i--] == value) {
            return true;
         }
      }

      return false;
   }

   public int indexOf(long value) {
      long[] items = this.items;
      int i = 0;

      for(int n = this.size; i < n; ++i) {
         if (items[i] == value) {
            return i;
         }
      }

      return -1;
   }

   public int lastIndexOf(char value) {
      long[] items = this.items;

      for(int i = this.size - 1; i >= 0; --i) {
         if (items[i] == (long)value) {
            return i;
         }
      }

      return -1;
   }

   public boolean removeValue(long value) {
      long[] items = this.items;
      int i = 0;

      for(int n = this.size; i < n; ++i) {
         if (items[i] == value) {
            this.removeIndex(i);
            return true;
         }
      }

      return false;
   }

   public long removeIndex(int index) {
      if (index >= this.size) {
         throw new IndexOutOfBoundsException(String.valueOf(index));
      } else {
         long[] items = this.items;
         long value = items[index];
         --this.size;
         if (this.ordered) {
            System.arraycopy(items, index + 1, items, index, this.size - index);
         } else {
            items[index] = items[this.size];
         }

         return value;
      }
   }

   public boolean removeAll(LongArray array) {
      int size = this.size;
      int startSize = size;
      long[] items = this.items;
      int i = 0;

      for(int n = array.size; i < n; ++i) {
         long item = array.get(i);

         for(int ii = 0; ii < size; ++ii) {
            if (item == items[ii]) {
               this.removeIndex(ii);
               --size;
               break;
            }
         }
      }

      if (size != startSize) {
         return true;
      } else {
         return false;
      }
   }

   public long pop() {
      return this.items[--this.size];
   }

   public long peek() {
      return this.items[this.size - 1];
   }

   public long first() {
      if (this.size == 0) {
         throw new IllegalStateException("Array is empty.");
      } else {
         return this.items[0];
      }
   }

   public void clear() {
      this.size = 0;
   }

   public void shrink() {
      this.resize(this.size);
   }

   public long[] ensureCapacity(int additionalCapacity) {
      int sizeNeeded = this.size + additionalCapacity;
      if (sizeNeeded >= this.items.length) {
         this.resize(Math.max(8, sizeNeeded));
      }

      return this.items;
   }

   protected long[] resize(int newSize) {
      long[] newItems = new long[newSize];
      long[] items = this.items;
      System.arraycopy(items, 0, newItems, 0, Math.min(this.size, newItems.length));
      this.items = newItems;
      return newItems;
   }

   public void sort() {
      Arrays.sort(this.items, 0, this.size);
   }

   public void reverse() {
      int i = 0;
      int lastIndex = this.size - 1;

      for(int n = this.size / 2; i < n; ++i) {
         int ii = lastIndex - i;
         long temp = this.items[i];
         this.items[i] = this.items[ii];
         this.items[ii] = temp;
      }

   }

   public void shuffle() {
      for(int i = this.size - 1; i >= 0; --i) {
         int ii = MathUtils.random(i);
         long temp = this.items[i];
         this.items[i] = this.items[ii];
         this.items[ii] = temp;
      }

   }

   public void truncate(int newSize) {
      if (this.size > newSize) {
         this.size = newSize;
      }

   }

   public long random() {
      return this.size == 0 ? 0L : this.items[MathUtils.random(0, this.size - 1)];
   }

   public long[] toArray() {
      long[] array = new long[this.size];
      System.arraycopy(this.items, 0, array, 0, this.size);
      return array;
   }

   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof LongArray)) {
         return false;
      } else {
         LongArray array = (LongArray)object;
         int n = this.size;
         if (n != array.size) {
            return false;
         } else {
            for(int i = 0; i < n; ++i) {
               if (this.items[i] != array.items[i]) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public String toString() {
      if (this.size == 0) {
         return "[]";
      } else {
         long[] items = this.items;
         StringBuilder buffer = new StringBuilder(32);
         buffer.append('[');
         buffer.append(items[0]);

         for(int i = 1; i < this.size; ++i) {
            buffer.append(", ");
            buffer.append(items[i]);
         }

         buffer.append(']');
         return buffer.toString();
      }
   }

   public String toString(String separator) {
      if (this.size == 0) {
         return "";
      } else {
         long[] items = this.items;
         StringBuilder buffer = new StringBuilder(32);
         buffer.append(items[0]);

         for(int i = 1; i < this.size; ++i) {
            buffer.append(separator);
            buffer.append(items[i]);
         }

         return buffer.toString();
      }
   }
}
