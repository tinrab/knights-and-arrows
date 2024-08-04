package com.badlogic.gdx.utils;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.reflect.ArrayReflection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Array<T> implements Iterable<T> {
   public T[] items;
   public int size;
   public boolean ordered;
   private Array.ArrayIterator iterator1;
   private Array.ArrayIterator iterator2;
   private Predicate.PredicateIterable<T> predicateIterable;

   public Array() {
      this(true, 16);
   }

   public Array(int capacity) {
      this(true, capacity);
   }

   public Array(boolean ordered, int capacity) {
      this.ordered = ordered;
      this.items = new Object[capacity];
   }

   public Array(boolean ordered, int capacity, Class<T> arrayType) {
      this.ordered = ordered;
      this.items = (Object[])ArrayReflection.newInstance(arrayType, capacity);
   }

   public Array(Class<T> arrayType) {
      this(true, 16, arrayType);
   }

   public Array(Array<? extends T> array) {
      this(array.ordered, array.size, array.items.getClass().getComponentType());
      this.size = array.size;
      System.arraycopy(array.items, 0, this.items, 0, this.size);
   }

   public Array(T[] array) {
      this(true, array, 0, array.length);
   }

   public Array(boolean ordered, T[] array, int start, int count) {
      this(ordered, array.length, array.getClass().getComponentType());
      this.size = count;
      System.arraycopy(array, 0, this.items, 0, this.size);
   }

   public void add(T value) {
      Object[] items = this.items;
      if (this.size == items.length) {
         items = this.resize(Math.max(8, (int)((float)this.size * 1.75F)));
      }

      items[this.size++] = value;
   }

   public void addAll(Array<? extends T> array) {
      this.addAll((Array)array, 0, array.size);
   }

   public void addAll(Array<? extends T> array, int offset, int length) {
      if (offset + length > array.size) {
         throw new IllegalArgumentException("offset + length must be <= size: " + offset + " + " + length + " <= " + array.size);
      } else {
         this.addAll(array.items, offset, length);
      }
   }

   public void addAll(T[] array) {
      this.addAll((Object[])array, 0, array.length);
   }

   public void addAll(T[] array, int offset, int length) {
      Object[] items = this.items;
      int sizeNeeded = this.size + length;
      if (sizeNeeded > items.length) {
         items = this.resize(Math.max(8, (int)((float)sizeNeeded * 1.75F)));
      }

      System.arraycopy(array, offset, items, this.size, length);
      this.size += length;
   }

   public T get(int index) {
      if (index >= this.size) {
         throw new IndexOutOfBoundsException(String.valueOf(index));
      } else {
         return this.items[index];
      }
   }

   public void set(int index, T value) {
      if (index >= this.size) {
         throw new IndexOutOfBoundsException(String.valueOf(index));
      } else {
         this.items[index] = value;
      }
   }

   public void insert(int index, T value) {
      if (index > this.size) {
         throw new IndexOutOfBoundsException(String.valueOf(index));
      } else {
         Object[] items = this.items;
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
         Object[] items = this.items;
         T firstValue = items[first];
         items[first] = items[second];
         items[second] = firstValue;
      }
   }

   public boolean contains(T value, boolean identity) {
      Object[] items = this.items;
      int i = this.size - 1;
      if (!identity && value != null) {
         while(i >= 0) {
            if (value.equals(items[i--])) {
               return true;
            }
         }
      } else {
         while(i >= 0) {
            if (items[i--] == value) {
               return true;
            }
         }
      }

      return false;
   }

   public int indexOf(T value, boolean identity) {
      Object[] items = this.items;
      int i;
      int n;
      if (!identity && value != null) {
         i = 0;

         for(n = this.size; i < n; ++i) {
            if (value.equals(items[i])) {
               return i;
            }
         }
      } else {
         i = 0;

         for(n = this.size; i < n; ++i) {
            if (items[i] == value) {
               return i;
            }
         }
      }

      return -1;
   }

   public int lastIndexOf(T value, boolean identity) {
      Object[] items = this.items;
      int i;
      if (!identity && value != null) {
         for(i = this.size - 1; i >= 0; --i) {
            if (value.equals(items[i])) {
               return i;
            }
         }
      } else {
         for(i = this.size - 1; i >= 0; --i) {
            if (items[i] == value) {
               return i;
            }
         }
      }

      return -1;
   }

   public boolean removeValue(T value, boolean identity) {
      Object[] items = this.items;
      int i;
      int n;
      if (!identity && value != null) {
         i = 0;

         for(n = this.size; i < n; ++i) {
            if (value.equals(items[i])) {
               this.removeIndex(i);
               return true;
            }
         }
      } else {
         i = 0;

         for(n = this.size; i < n; ++i) {
            if (items[i] == value) {
               this.removeIndex(i);
               return true;
            }
         }
      }

      return false;
   }

   public T removeIndex(int index) {
      if (index >= this.size) {
         throw new IndexOutOfBoundsException(String.valueOf(index));
      } else {
         Object[] items = this.items;
         T value = items[index];
         --this.size;
         if (this.ordered) {
            System.arraycopy(items, index + 1, items, index, this.size - index);
         } else {
            items[index] = items[this.size];
         }

         items[this.size] = null;
         return value;
      }
   }

   public boolean removeAll(Array<? extends T> array, boolean identity) {
      int size = this.size;
      int startSize = size;
      Object[] items = this.items;
      int i;
      int n;
      Object item;
      int ii;
      if (identity) {
         i = 0;

         for(n = array.size; i < n; ++i) {
            item = array.get(i);

            for(ii = 0; ii < size; ++ii) {
               if (item == items[ii]) {
                  this.removeIndex(ii);
                  --size;
                  break;
               }
            }
         }
      } else {
         i = 0;

         for(n = array.size; i < n; ++i) {
            item = array.get(i);

            for(ii = 0; ii < size; ++ii) {
               if (item.equals(items[ii])) {
                  this.removeIndex(ii);
                  --size;
                  break;
               }
            }
         }
      }

      return size != startSize;
   }

   public T pop() {
      --this.size;
      T item = this.items[this.size];
      this.items[this.size] = null;
      return item;
   }

   public T peek() {
      return this.items[this.size - 1];
   }

   public T first() {
      if (this.size == 0) {
         throw new IllegalStateException("Array is empty.");
      } else {
         return this.items[0];
      }
   }

   public void clear() {
      Object[] items = this.items;
      int i = 0;

      for(int n = this.size; i < n; ++i) {
         items[i] = null;
      }

      this.size = 0;
   }

   public void shrink() {
      this.resize(this.size);
   }

   public T[] ensureCapacity(int additionalCapacity) {
      int sizeNeeded = this.size + additionalCapacity;
      if (sizeNeeded >= this.items.length) {
         this.resize(Math.max(8, sizeNeeded));
      }

      return this.items;
   }

   protected T[] resize(int newSize) {
      Object[] items = this.items;
      Object[] newItems = (Object[])ArrayReflection.newInstance(items.getClass().getComponentType(), newSize);
      System.arraycopy(items, 0, newItems, 0, Math.min(this.size, newItems.length));
      this.items = newItems;
      return newItems;
   }

   public void sort() {
      Sort.instance().sort(this.items, 0, this.size);
   }

   public void sort(Comparator<T> comparator) {
      Sort.instance().sort(this.items, comparator, 0, this.size);
   }

   public void reverse() {
      int i = 0;
      int lastIndex = this.size - 1;

      for(int n = this.size / 2; i < n; ++i) {
         int ii = lastIndex - i;
         T temp = this.items[i];
         this.items[i] = this.items[ii];
         this.items[ii] = temp;
      }

   }

   public void shuffle() {
      for(int i = this.size - 1; i >= 0; --i) {
         int ii = MathUtils.random(i);
         T temp = this.items[i];
         this.items[i] = this.items[ii];
         this.items[ii] = temp;
      }

   }

   public Iterator<T> iterator() {
      if (this.iterator1 == null) {
         this.iterator1 = new Array.ArrayIterator(this);
         this.iterator2 = new Array.ArrayIterator(this);
      }

      if (!this.iterator1.valid) {
         this.iterator1.index = 0;
         this.iterator1.valid = true;
         this.iterator2.valid = false;
         return this.iterator1;
      } else {
         this.iterator2.index = 0;
         this.iterator2.valid = true;
         this.iterator1.valid = false;
         return this.iterator2;
      }
   }

   public Iterable<T> select(Predicate<T> predicate) {
      if (this.predicateIterable == null) {
         this.predicateIterable = new Predicate.PredicateIterable(this, predicate);
      } else {
         this.predicateIterable.set(this, predicate);
      }

      return this.predicateIterable;
   }

   public void truncate(int newSize) {
      if (this.size > newSize) {
         for(int i = newSize; i < this.size; ++i) {
            this.items[i] = null;
         }

         this.size = newSize;
      }
   }

   public T random() {
      return this.size == 0 ? null : this.items[MathUtils.random(0, this.size - 1)];
   }

   public T[] toArray() {
      return this.toArray(this.items.getClass().getComponentType());
   }

   public <V> V[] toArray(Class<V> type) {
      Object[] result = (Object[])ArrayReflection.newInstance(type, this.size);
      System.arraycopy(this.items, 0, result, 0, this.size);
      return result;
   }

   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof Array)) {
         return false;
      } else {
         Array array = (Array)object;
         int n = this.size;
         if (n != array.size) {
            return false;
         } else {
            Object[] items1 = this.items;
            Object[] items2 = array.items;
            int i = 0;

            while(true) {
               if (i >= n) {
                  return true;
               }

               Object o1 = items1[i];
               Object o2 = items2[i];
               if (o1 == null) {
                  if (o2 != null) {
                     break;
                  }
               } else if (!o1.equals(o2)) {
                  break;
               }

               ++i;
            }

            return false;
         }
      }
   }

   public String toString() {
      if (this.size == 0) {
         return "[]";
      } else {
         Object[] items = this.items;
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
         Object[] items = this.items;
         StringBuilder buffer = new StringBuilder(32);
         buffer.append(items[0]);

         for(int i = 1; i < this.size; ++i) {
            buffer.append(separator);
            buffer.append(items[i]);
         }

         return buffer.toString();
      }
   }

   public static class ArrayIterator<T> implements Iterator<T> {
      private final Array<T> array;
      int index;
      boolean valid = true;

      public ArrayIterator(Array<T> array) {
         this.array = array;
      }

      public boolean hasNext() {
         return this.index < this.array.size;
      }

      public T next() {
         if (this.index >= this.array.size) {
            throw new NoSuchElementException(String.valueOf(this.index));
         } else if (!this.valid) {
            throw new GdxRuntimeException("#iterator() cannot be used nested.");
         } else {
            return this.array.items[this.index++];
         }
      }

      public void remove() {
         --this.index;
         this.array.removeIndex(this.index);
      }

      public void reset() {
         this.index = 0;
      }
   }
}
