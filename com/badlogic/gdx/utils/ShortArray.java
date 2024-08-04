package com.badlogic.gdx.utils;

import com.badlogic.gdx.math.MathUtils;
import java.util.Arrays;

public class ShortArray {
   public short[] items;
   public int size;
   public boolean ordered;

   public ShortArray() {
      this(true, 16);
   }

   public ShortArray(int capacity) {
      this(true, capacity);
   }

   public ShortArray(boolean ordered, int capacity) {
      this.ordered = ordered;
      this.items = new short[capacity];
   }

   public ShortArray(ShortArray array) {
      this.ordered = array.ordered;
      this.size = array.size;
      this.items = new short[this.size];
      System.arraycopy(array.items, 0, this.items, 0, this.size);
   }

   public ShortArray(short[] array) {
      this(true, array, 0, array.length);
   }

   public ShortArray(boolean ordered, short[] array, int startIndex, int count) {
      this(ordered, array.length);
      this.size = count;
      System.arraycopy(array, startIndex, this.items, 0, count);
   }

   public void add(short value) {
      short[] items = this.items;
      if (this.size == items.length) {
         items = this.resize(Math.max(8, (int)((float)this.size * 1.75F)));
      }

      items[this.size++] = value;
   }

   public void addAll(ShortArray array) {
      this.addAll((ShortArray)array, 0, array.size);
   }

   public void addAll(ShortArray array, int offset, int length) {
      if (offset + length > array.size) {
         throw new IllegalArgumentException("offset + length must be <= size: " + offset + " + " + length + " <= " + array.size);
      } else {
         this.addAll(array.items, offset, length);
      }
   }

   public void addAll(short[] array) {
      this.addAll((short[])array, 0, array.length);
   }

   public void addAll(short[] array, int offset, int length) {
      short[] items = this.items;
      int sizeNeeded = this.size + length;
      if (sizeNeeded >= items.length) {
         items = this.resize(Math.max(8, (int)((float)sizeNeeded * 1.75F)));
      }

      System.arraycopy(array, offset, items, this.size, length);
      this.size += length;
   }

   public short get(int index) {
      if (index >= this.size) {
         throw new IndexOutOfBoundsException(String.valueOf(index));
      } else {
         return this.items[index];
      }
   }

   public void set(int index, short value) {
      if (index >= this.size) {
         throw new IndexOutOfBoundsException(String.valueOf(index));
      } else {
         this.items[index] = value;
      }
   }

   public void insert(int index, short value) {
      if (index > this.size) {
         throw new IndexOutOfBoundsException(String.valueOf(index));
      } else {
         short[] items = this.items;
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
         short[] items = this.items;
         short firstValue = items[first];
         items[first] = items[second];
         items[second] = firstValue;
      }
   }

   public boolean contains(short value) {
      int i = this.size - 1;
      short[] items = this.items;

      while(i >= 0) {
         if (items[i--] == value) {
            return true;
         }
      }

      return false;
   }

   public int indexOf(short value) {
      short[] items = this.items;
      int i = 0;

      for(int n = this.size; i < n; ++i) {
         if (items[i] == value) {
            return i;
         }
      }

      return -1;
   }

   public int lastIndexOf(char value) {
      short[] items = this.items;

      for(int i = this.size - 1; i >= 0; --i) {
         if (items[i] == value) {
            return i;
         }
      }

      return -1;
   }

   public boolean removeValue(short value) {
      short[] items = this.items;
      int i = 0;

      for(int n = this.size; i < n; ++i) {
         if (items[i] == value) {
            this.removeIndex(i);
            return true;
         }
      }

      return false;
   }

   public short removeIndex(int index) {
      if (index >= this.size) {
         throw new IndexOutOfBoundsException(String.valueOf(index));
      } else {
         short[] items = this.items;
         short value = items[index];
         --this.size;
         if (this.ordered) {
            System.arraycopy(items, index + 1, items, index, this.size - index);
         } else {
            items[index] = items[this.size];
         }

         return value;
      }
   }

   public boolean removeAll(ShortArray array) {
      int size = this.size;
      int startSize = size;
      short[] items = this.items;
      int i = 0;

      for(int n = array.size; i < n; ++i) {
         short item = array.get(i);

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

   public short pop() {
      return this.items[--this.size];
   }

   public short peek() {
      return this.items[this.size - 1];
   }

   public short first() {
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

   public short[] ensureCapacity(int additionalCapacity) {
      int sizeNeeded = this.size + additionalCapacity;
      if (sizeNeeded >= this.items.length) {
         this.resize(Math.max(8, sizeNeeded));
      }

      return this.items;
   }

   protected short[] resize(int newSize) {
      short[] newItems = new short[newSize];
      short[] items = this.items;
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
         short temp = this.items[i];
         this.items[i] = this.items[ii];
         this.items[ii] = temp;
      }

   }

   public void shuffle() {
      for(int i = this.size - 1; i >= 0; --i) {
         int ii = MathUtils.random(i);
         short temp = this.items[i];
         this.items[i] = this.items[ii];
         this.items[ii] = temp;
      }

   }

   public void truncate(int newSize) {
      if (this.size > newSize) {
         this.size = newSize;
      }

   }

   public short random() {
      return this.size == 0 ? 0 : this.items[MathUtils.random(0, this.size - 1)];
   }

   public short[] toArray() {
      short[] array = new short[this.size];
      System.arraycopy(this.items, 0, array, 0, this.size);
      return array;
   }

   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof ShortArray)) {
         return false;
      } else {
         ShortArray array = (ShortArray)object;
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
         short[] items = this.items;
         StringBuilder buffer = new StringBuilder(32);
         buffer.append('[');
         buffer.append((int)items[0]);

         for(int i = 1; i < this.size; ++i) {
            buffer.append(", ");
            buffer.append((int)items[i]);
         }

         buffer.append(']');
         return buffer.toString();
      }
   }

   public String toString(String separator) {
      if (this.size == 0) {
         return "";
      } else {
         short[] items = this.items;
         StringBuilder buffer = new StringBuilder(32);
         buffer.append((int)items[0]);

         for(int i = 1; i < this.size; ++i) {
            buffer.append(separator);
            buffer.append((int)items[i]);
         }

         return buffer.toString();
      }
   }
}
