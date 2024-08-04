package com.badlogic.gdx.utils;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.reflect.ArrayReflection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayMap<K, V> {
   public K[] keys;
   public V[] values;
   public int size;
   public boolean ordered;
   private ArrayMap.Entries entries1;
   private ArrayMap.Entries entries2;
   private ArrayMap.Values valuesIter1;
   private ArrayMap.Values valuesIter2;
   private ArrayMap.Keys keysIter1;
   private ArrayMap.Keys keysIter2;

   public ArrayMap() {
      this(true, 16);
   }

   public ArrayMap(int capacity) {
      this(true, capacity);
   }

   public ArrayMap(boolean ordered, int capacity) {
      this.ordered = ordered;
      this.keys = new Object[capacity];
      this.values = new Object[capacity];
   }

   public ArrayMap(boolean ordered, int capacity, Class<K> keyArrayType, Class<V> valueArrayType) {
      this.ordered = ordered;
      this.keys = (Object[])ArrayReflection.newInstance(keyArrayType, capacity);
      this.values = (Object[])ArrayReflection.newInstance(valueArrayType, capacity);
   }

   public ArrayMap(Class<K> keyArrayType, Class<V> valueArrayType) {
      this(false, 16, keyArrayType, valueArrayType);
   }

   public ArrayMap(ArrayMap array) {
      this(array.ordered, array.size, array.keys.getClass().getComponentType(), array.values.getClass().getComponentType());
      this.size = array.size;
      System.arraycopy(array.keys, 0, this.keys, 0, this.size);
      System.arraycopy(array.values, 0, this.values, 0, this.size);
   }

   public void put(K key, V value) {
      if (this.size == this.keys.length) {
         this.resize(Math.max(8, (int)((float)this.size * 1.75F)));
      }

      int index = this.indexOfKey(key);
      if (index == -1) {
         index = this.size++;
      }

      this.keys[index] = key;
      this.values[index] = value;
   }

   public void put(K key, V value, int index) {
      if (this.size == this.keys.length) {
         this.resize(Math.max(8, (int)((float)this.size * 1.75F)));
      }

      int existingIndex = this.indexOfKey(key);
      if (existingIndex != -1) {
         this.removeIndex(existingIndex);
      }

      System.arraycopy(this.keys, index, this.keys, index + 1, this.size - index);
      System.arraycopy(this.values, index, this.values, index + 1, this.size - index);
      this.keys[index] = key;
      this.values[index] = value;
      ++this.size;
   }

   public void putAll(ArrayMap map) {
      this.putAll(map, 0, map.size);
   }

   public void putAll(ArrayMap map, int offset, int length) {
      if (offset + length > map.size) {
         throw new IllegalArgumentException("offset + length must be <= size: " + offset + " + " + length + " <= " + map.size);
      } else {
         int sizeNeeded = this.size + length - offset;
         if (sizeNeeded >= this.keys.length) {
            this.resize(Math.max(8, (int)((float)sizeNeeded * 1.75F)));
         }

         System.arraycopy(map.keys, offset, this.keys, this.size, length);
         System.arraycopy(map.values, offset, this.values, this.size, length);
         this.size += length;
      }
   }

   public V get(K key) {
      Object[] keys = this.keys;
      int i = this.size - 1;
      if (key == null) {
         while(i >= 0) {
            if (keys[i] == key) {
               return this.values[i];
            }

            --i;
         }
      } else {
         while(i >= 0) {
            if (key.equals(keys[i])) {
               return this.values[i];
            }

            --i;
         }
      }

      return null;
   }

   public K getKey(V value, boolean identity) {
      Object[] values = this.values;
      int i = this.size - 1;
      if (!identity && values != null) {
         while(i >= 0) {
            if (values.equals(values[i])) {
               return this.keys[i];
            }

            --i;
         }
      } else {
         while(i >= 0) {
            if (values[i] == values) {
               return this.keys[i];
            }

            --i;
         }
      }

      return null;
   }

   public K getKeyAt(int index) {
      if (index >= this.size) {
         throw new IndexOutOfBoundsException(String.valueOf(index));
      } else {
         return this.keys[index];
      }
   }

   public V getValueAt(int index) {
      if (index >= this.size) {
         throw new IndexOutOfBoundsException(String.valueOf(index));
      } else {
         return this.values[index];
      }
   }

   public K firstKey() {
      if (this.size == 0) {
         throw new IllegalStateException("Map is empty.");
      } else {
         return this.keys[0];
      }
   }

   public V firstValue() {
      if (this.size == 0) {
         throw new IllegalStateException("Map is empty.");
      } else {
         return this.values[0];
      }
   }

   public void setKey(int index, K key) {
      if (index >= this.size) {
         throw new IndexOutOfBoundsException(String.valueOf(index));
      } else {
         this.keys[index] = key;
      }
   }

   public void setValue(int index, V value) {
      if (index >= this.size) {
         throw new IndexOutOfBoundsException(String.valueOf(index));
      } else {
         this.values[index] = value;
      }
   }

   public void insert(int index, K key, V value) {
      if (index > this.size) {
         throw new IndexOutOfBoundsException(String.valueOf(index));
      } else {
         if (this.size == this.keys.length) {
            this.resize(Math.max(8, (int)((float)this.size * 1.75F)));
         }

         if (this.ordered) {
            System.arraycopy(this.keys, index, this.keys, index + 1, this.size - index);
            System.arraycopy(this.values, index, this.values, index + 1, this.size - index);
         } else {
            this.keys[this.size] = this.keys[index];
            this.values[this.size] = this.values[index];
         }

         ++this.size;
         this.keys[index] = key;
         this.values[index] = value;
      }
   }

   public boolean containsKey(K key) {
      Object[] keys = this.keys;
      int i = this.size - 1;
      if (key == null) {
         while(i >= 0) {
            if (keys[i--] == key) {
               return true;
            }
         }
      } else {
         while(i >= 0) {
            if (key.equals(keys[i--])) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean containsValue(V value, boolean identity) {
      Object[] values = this.values;
      int i = this.size - 1;
      if (!identity && value != null) {
         while(i >= 0) {
            if (value.equals(values[i--])) {
               return true;
            }
         }
      } else {
         while(i >= 0) {
            if (values[i--] == value) {
               return true;
            }
         }
      }

      return false;
   }

   public int indexOfKey(K key) {
      Object[] keys = this.keys;
      int i;
      int n;
      if (key == null) {
         i = 0;

         for(n = this.size; i < n; ++i) {
            if (keys[i] == key) {
               return i;
            }
         }
      } else {
         i = 0;

         for(n = this.size; i < n; ++i) {
            if (key.equals(keys[i])) {
               return i;
            }
         }
      }

      return -1;
   }

   public int indexOfValue(V value, boolean identity) {
      Object[] values = this.values;
      int i;
      int n;
      if (!identity && value != null) {
         i = 0;

         for(n = this.size; i < n; ++i) {
            if (value.equals(values[i])) {
               return i;
            }
         }
      } else {
         i = 0;

         for(n = this.size; i < n; ++i) {
            if (values[i] == value) {
               return i;
            }
         }
      }

      return -1;
   }

   public V removeKey(K key) {
      Object[] keys = this.keys;
      int i;
      int n;
      Object value;
      if (key == null) {
         i = 0;

         for(n = this.size; i < n; ++i) {
            if (keys[i] == key) {
               value = this.values[i];
               this.removeIndex(i);
               return value;
            }
         }
      } else {
         i = 0;

         for(n = this.size; i < n; ++i) {
            if (key.equals(keys[i])) {
               value = this.values[i];
               this.removeIndex(i);
               return value;
            }
         }
      }

      return null;
   }

   public boolean removeValue(V value, boolean identity) {
      Object[] values = this.values;
      int i;
      int n;
      if (!identity && value != null) {
         i = 0;

         for(n = this.size; i < n; ++i) {
            if (value.equals(values[i])) {
               this.removeIndex(i);
               return true;
            }
         }
      } else {
         i = 0;

         for(n = this.size; i < n; ++i) {
            if (values[i] == value) {
               this.removeIndex(i);
               return true;
            }
         }
      }

      return false;
   }

   public void removeIndex(int index) {
      if (index >= this.size) {
         throw new IndexOutOfBoundsException(String.valueOf(index));
      } else {
         Object[] keys = this.keys;
         --this.size;
         if (this.ordered) {
            System.arraycopy(keys, index + 1, keys, index, this.size - index);
            System.arraycopy(this.values, index + 1, this.values, index, this.size - index);
         } else {
            keys[index] = keys[this.size];
            this.values[index] = this.values[this.size];
         }

         keys[this.size] = null;
         this.values[this.size] = null;
      }
   }

   public K peekKey() {
      return this.keys[this.size - 1];
   }

   public V peekValue() {
      return this.values[this.size - 1];
   }

   public void clear() {
      Object[] keys = this.keys;
      Object[] values = this.values;
      int i = 0;

      for(int n = this.size; i < n; ++i) {
         keys[i] = null;
         values[i] = null;
      }

      this.size = 0;
   }

   public void shrink() {
      this.resize(this.size);
   }

   public void ensureCapacity(int additionalCapacity) {
      int sizeNeeded = this.size + additionalCapacity;
      if (sizeNeeded >= this.keys.length) {
         this.resize(Math.max(8, sizeNeeded));
      }

   }

   protected void resize(int newSize) {
      Object[] newKeys = (Object[])ArrayReflection.newInstance(this.keys.getClass().getComponentType(), newSize);
      System.arraycopy(this.keys, 0, newKeys, 0, Math.min(this.keys.length, newKeys.length));
      this.keys = newKeys;
      Object[] newValues = (Object[])ArrayReflection.newInstance(this.values.getClass().getComponentType(), newSize);
      System.arraycopy(this.values, 0, newValues, 0, Math.min(this.values.length, newValues.length));
      this.values = newValues;
   }

   public void reverse() {
      int i = 0;
      int lastIndex = this.size - 1;

      for(int n = this.size / 2; i < n; ++i) {
         int ii = lastIndex - i;
         K tempKey = this.keys[i];
         this.keys[i] = this.keys[ii];
         this.keys[ii] = tempKey;
         V tempValue = this.values[i];
         this.values[i] = this.values[ii];
         this.values[ii] = tempValue;
      }

   }

   public void shuffle() {
      for(int i = this.size - 1; i >= 0; --i) {
         int ii = MathUtils.random(i);
         K tempKey = this.keys[i];
         this.keys[i] = this.keys[ii];
         this.keys[ii] = tempKey;
         V tempValue = this.values[i];
         this.values[i] = this.values[ii];
         this.values[ii] = tempValue;
      }

   }

   public void truncate(int newSize) {
      if (this.size > newSize) {
         for(int i = newSize; i < this.size; ++i) {
            this.keys[i] = null;
            this.values[i] = null;
         }

         this.size = newSize;
      }
   }

   public String toString() {
      if (this.size == 0) {
         return "{}";
      } else {
         Object[] keys = this.keys;
         Object[] values = this.values;
         StringBuilder buffer = new StringBuilder(32);
         buffer.append('{');
         buffer.append(keys[0]);
         buffer.append('=');
         buffer.append(values[0]);

         for(int i = 1; i < this.size; ++i) {
            buffer.append(", ");
            buffer.append(keys[i]);
            buffer.append('=');
            buffer.append(values[i]);
         }

         buffer.append('}');
         return buffer.toString();
      }
   }

   public ArrayMap.Entries<K, V> entries() {
      if (this.entries1 == null) {
         this.entries1 = new ArrayMap.Entries(this);
         this.entries2 = new ArrayMap.Entries(this);
      }

      if (!this.entries1.valid) {
         this.entries1.index = 0;
         this.entries1.valid = true;
         this.entries2.valid = false;
         return this.entries1;
      } else {
         this.entries2.index = 0;
         this.entries2.valid = true;
         this.entries1.valid = false;
         return this.entries2;
      }
   }

   public ArrayMap.Values<V> values() {
      if (this.valuesIter1 == null) {
         this.valuesIter1 = new ArrayMap.Values(this);
         this.valuesIter2 = new ArrayMap.Values(this);
      }

      if (!this.valuesIter1.valid) {
         this.valuesIter1.index = 0;
         this.valuesIter1.valid = true;
         this.valuesIter2.valid = false;
         return this.valuesIter1;
      } else {
         this.valuesIter2.index = 0;
         this.valuesIter2.valid = true;
         this.valuesIter1.valid = false;
         return this.valuesIter2;
      }
   }

   public ArrayMap.Keys<K> keys() {
      if (this.keysIter1 == null) {
         this.keysIter1 = new ArrayMap.Keys(this);
         this.keysIter2 = new ArrayMap.Keys(this);
      }

      if (!this.keysIter1.valid) {
         this.keysIter1.index = 0;
         this.keysIter1.valid = true;
         this.keysIter2.valid = false;
         return this.keysIter1;
      } else {
         this.keysIter2.index = 0;
         this.keysIter2.valid = true;
         this.keysIter1.valid = false;
         return this.keysIter2;
      }
   }

   public static class Entries<K, V> implements Iterable<ObjectMap.Entry<K, V>>, Iterator<ObjectMap.Entry<K, V>> {
      private final ArrayMap<K, V> map;
      ObjectMap.Entry<K, V> entry = new ObjectMap.Entry();
      int index;
      boolean valid = true;

      public Entries(ArrayMap<K, V> map) {
         this.map = map;
      }

      public boolean hasNext() {
         return this.index < this.map.size;
      }

      public Iterator<ObjectMap.Entry<K, V>> iterator() {
         return this;
      }

      public ObjectMap.Entry<K, V> next() {
         if (this.index >= this.map.size) {
            throw new NoSuchElementException(String.valueOf(this.index));
         } else if (!this.valid) {
            throw new GdxRuntimeException("#iterator() cannot be used nested.");
         } else {
            this.entry.key = this.map.keys[this.index];
            this.entry.value = this.map.values[this.index++];
            return this.entry;
         }
      }

      public void remove() {
         --this.index;
         this.map.removeIndex(this.index);
      }

      public void reset() {
         this.index = 0;
      }
   }

   public static class Keys<K> implements Iterable<K>, Iterator<K> {
      private final ArrayMap<K, Object> map;
      int index;
      boolean valid = true;

      public Keys(ArrayMap<K, Object> map) {
         this.map = map;
      }

      public boolean hasNext() {
         return this.index < this.map.size;
      }

      public Iterator<K> iterator() {
         return this;
      }

      public K next() {
         if (this.index >= this.map.size) {
            throw new NoSuchElementException(String.valueOf(this.index));
         } else if (!this.valid) {
            throw new GdxRuntimeException("#iterator() cannot be used nested.");
         } else {
            return this.map.keys[this.index++];
         }
      }

      public void remove() {
         --this.index;
         this.map.removeIndex(this.index);
      }

      public void reset() {
         this.index = 0;
      }

      public Array<K> toArray() {
         return new Array(true, this.map.keys, this.index, this.map.size - this.index);
      }
   }

   public static class Values<V> implements Iterable<V>, Iterator<V> {
      private final ArrayMap<Object, V> map;
      int index;
      boolean valid = true;

      public Values(ArrayMap<Object, V> map) {
         this.map = map;
      }

      public boolean hasNext() {
         return this.index < this.map.size;
      }

      public Iterator<V> iterator() {
         return this;
      }

      public V next() {
         if (this.index >= this.map.size) {
            throw new NoSuchElementException(String.valueOf(this.index));
         } else if (!this.valid) {
            throw new GdxRuntimeException("#iterator() cannot be used nested.");
         } else {
            return this.map.values[this.index++];
         }
      }

      public void remove() {
         --this.index;
         this.map.removeIndex(this.index);
      }

      public void reset() {
         this.index = 0;
      }

      public Array<V> toArray() {
         return new Array(true, this.map.values, this.index, this.map.size - this.index);
      }
   }
}
