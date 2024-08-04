package com.badlogic.gdx.utils;

import com.badlogic.gdx.math.MathUtils;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class IdentityMap<K, V> {
   private static final int PRIME1 = -1105259343;
   private static final int PRIME2 = -1262997959;
   private static final int PRIME3 = -825114047;
   public int size;
   K[] keyTable;
   V[] valueTable;
   int capacity;
   int stashSize;
   private float loadFactor;
   private int hashShift;
   private int mask;
   private int threshold;
   private int stashCapacity;
   private int pushIterations;
   private IdentityMap.Entries entries1;
   private IdentityMap.Entries entries2;
   private IdentityMap.Values values1;
   private IdentityMap.Values values2;
   private IdentityMap.Keys keys1;
   private IdentityMap.Keys keys2;

   public IdentityMap() {
      this(32, 0.8F);
   }

   public IdentityMap(int initialCapacity) {
      this(initialCapacity, 0.8F);
   }

   public IdentityMap(int initialCapacity, float loadFactor) {
      if (initialCapacity < 0) {
         throw new IllegalArgumentException("initialCapacity must be >= 0: " + initialCapacity);
      } else if (this.capacity > 1073741824) {
         throw new IllegalArgumentException("initialCapacity is too large: " + initialCapacity);
      } else {
         this.capacity = MathUtils.nextPowerOfTwo(initialCapacity);
         if (loadFactor <= 0.0F) {
            throw new IllegalArgumentException("loadFactor must be > 0: " + loadFactor);
         } else {
            this.loadFactor = loadFactor;
            this.threshold = (int)((float)this.capacity * loadFactor);
            this.mask = this.capacity - 1;
            this.hashShift = 31 - Integer.numberOfTrailingZeros(this.capacity);
            this.stashCapacity = Math.max(3, (int)Math.ceil(Math.log((double)this.capacity)) * 2);
            this.pushIterations = Math.max(Math.min(this.capacity, 8), (int)Math.sqrt((double)this.capacity) / 8);
            this.keyTable = new Object[this.capacity + this.stashCapacity];
            this.valueTable = new Object[this.keyTable.length];
         }
      }
   }

   public V put(K key, V value) {
      if (key == null) {
         throw new IllegalArgumentException("key cannot be null.");
      } else {
         Object[] keyTable = this.keyTable;
         int hashCode = System.identityHashCode(key);
         int index1 = hashCode & this.mask;
         K key1 = keyTable[index1];
         if (key1 == key) {
            V oldValue = this.valueTable[index1];
            this.valueTable[index1] = value;
            return oldValue;
         } else {
            int index2 = this.hash2(hashCode);
            K key2 = keyTable[index2];
            if (key2 == key) {
               V oldValue = this.valueTable[index2];
               this.valueTable[index2] = value;
               return oldValue;
            } else {
               int index3 = this.hash3(hashCode);
               K key3 = keyTable[index3];
               if (key3 == key) {
                  V oldValue = this.valueTable[index3];
                  this.valueTable[index3] = value;
                  return oldValue;
               } else {
                  int i = this.capacity;

                  for(int n = i + this.stashSize; i < n; ++i) {
                     if (keyTable[i] == key) {
                        V oldValue = this.valueTable[i];
                        this.valueTable[i] = value;
                        return oldValue;
                     }
                  }

                  if (key1 == null) {
                     keyTable[index1] = key;
                     this.valueTable[index1] = value;
                     if (this.size++ >= this.threshold) {
                        this.resize(this.capacity << 1);
                     }

                     return null;
                  } else if (key2 == null) {
                     keyTable[index2] = key;
                     this.valueTable[index2] = value;
                     if (this.size++ >= this.threshold) {
                        this.resize(this.capacity << 1);
                     }

                     return null;
                  } else if (key3 == null) {
                     keyTable[index3] = key;
                     this.valueTable[index3] = value;
                     if (this.size++ >= this.threshold) {
                        this.resize(this.capacity << 1);
                     }

                     return null;
                  } else {
                     this.push(key, value, index1, key1, index2, key2, index3, key3);
                     return null;
                  }
               }
            }
         }
      }
   }

   private void putResize(K key, V value) {
      int hashCode = System.identityHashCode(key);
      int index1 = hashCode & this.mask;
      K key1 = this.keyTable[index1];
      if (key1 == null) {
         this.keyTable[index1] = key;
         this.valueTable[index1] = value;
         if (this.size++ >= this.threshold) {
            this.resize(this.capacity << 1);
         }

      } else {
         int index2 = this.hash2(hashCode);
         K key2 = this.keyTable[index2];
         if (key2 == null) {
            this.keyTable[index2] = key;
            this.valueTable[index2] = value;
            if (this.size++ >= this.threshold) {
               this.resize(this.capacity << 1);
            }

         } else {
            int index3 = this.hash3(hashCode);
            K key3 = this.keyTable[index3];
            if (key3 == null) {
               this.keyTable[index3] = key;
               this.valueTable[index3] = value;
               if (this.size++ >= this.threshold) {
                  this.resize(this.capacity << 1);
               }

            } else {
               this.push(key, value, index1, key1, index2, key2, index3, key3);
            }
         }
      }
   }

   private void push(K insertKey, V insertValue, int index1, K key1, int index2, K key2, int index3, K key3) {
      Object[] keyTable = this.keyTable;
      Object[] valueTable = this.valueTable;
      int mask = this.mask;
      int i = 0;
      int pushIterations = this.pushIterations;

      while(true) {
         Object evictedKey;
         Object evictedValue;
         switch(MathUtils.random(2)) {
         case 0:
            evictedKey = key1;
            evictedValue = valueTable[index1];
            keyTable[index1] = insertKey;
            valueTable[index1] = insertValue;
            break;
         case 1:
            evictedKey = key2;
            evictedValue = valueTable[index2];
            keyTable[index2] = insertKey;
            valueTable[index2] = insertValue;
            break;
         default:
            evictedKey = key3;
            evictedValue = valueTable[index3];
            keyTable[index3] = insertKey;
            valueTable[index3] = insertValue;
         }

         int hashCode = System.identityHashCode(evictedKey);
         index1 = hashCode & mask;
         key1 = keyTable[index1];
         if (key1 == null) {
            keyTable[index1] = evictedKey;
            valueTable[index1] = evictedValue;
            if (this.size++ >= this.threshold) {
               this.resize(this.capacity << 1);
            }

            return;
         }

         index2 = this.hash2(hashCode);
         key2 = keyTable[index2];
         if (key2 == null) {
            keyTable[index2] = evictedKey;
            valueTable[index2] = evictedValue;
            if (this.size++ >= this.threshold) {
               this.resize(this.capacity << 1);
            }

            return;
         }

         index3 = this.hash3(hashCode);
         key3 = keyTable[index3];
         if (key3 == null) {
            keyTable[index3] = evictedKey;
            valueTable[index3] = evictedValue;
            if (this.size++ >= this.threshold) {
               this.resize(this.capacity << 1);
            }

            return;
         }

         ++i;
         if (i == pushIterations) {
            this.putStash(evictedKey, evictedValue);
            return;
         }

         insertKey = evictedKey;
         insertValue = evictedValue;
      }
   }

   private void putStash(K key, V value) {
      if (this.stashSize == this.stashCapacity) {
         this.resize(this.capacity << 1);
         this.put(key, value);
      } else {
         int index = this.capacity + this.stashSize;
         this.keyTable[index] = key;
         this.valueTable[index] = value;
         ++this.stashSize;
         ++this.size;
      }
   }

   public V get(K key) {
      int hashCode = System.identityHashCode(key);
      int index = hashCode & this.mask;
      if (key != this.keyTable[index]) {
         index = this.hash2(hashCode);
         if (key != this.keyTable[index]) {
            index = this.hash3(hashCode);
            if (key != this.keyTable[index]) {
               return this.getStash(key, (Object)null);
            }
         }
      }

      return this.valueTable[index];
   }

   public V get(K key, V defaultValue) {
      int hashCode = System.identityHashCode(key);
      int index = hashCode & this.mask;
      if (key != this.keyTable[index]) {
         index = this.hash2(hashCode);
         if (key != this.keyTable[index]) {
            index = this.hash3(hashCode);
            if (key != this.keyTable[index]) {
               return this.getStash(key, defaultValue);
            }
         }
      }

      return this.valueTable[index];
   }

   private V getStash(K key, V defaultValue) {
      Object[] keyTable = this.keyTable;
      int i = this.capacity;

      for(int n = i + this.stashSize; i < n; ++i) {
         if (keyTable[i] == key) {
            return this.valueTable[i];
         }
      }

      return defaultValue;
   }

   public V remove(K key) {
      int hashCode = System.identityHashCode(key);
      int index = hashCode & this.mask;
      Object oldValue;
      if (this.keyTable[index] == key) {
         this.keyTable[index] = null;
         oldValue = this.valueTable[index];
         this.valueTable[index] = null;
         --this.size;
         return oldValue;
      } else {
         index = this.hash2(hashCode);
         if (this.keyTable[index] == key) {
            this.keyTable[index] = null;
            oldValue = this.valueTable[index];
            this.valueTable[index] = null;
            --this.size;
            return oldValue;
         } else {
            index = this.hash3(hashCode);
            if (this.keyTable[index] == key) {
               this.keyTable[index] = null;
               oldValue = this.valueTable[index];
               this.valueTable[index] = null;
               --this.size;
               return oldValue;
            } else {
               return this.removeStash(key);
            }
         }
      }
   }

   V removeStash(K key) {
      Object[] keyTable = this.keyTable;
      int i = this.capacity;

      for(int n = i + this.stashSize; i < n; ++i) {
         if (keyTable[i] == key) {
            V oldValue = this.valueTable[i];
            this.removeStashIndex(i);
            --this.size;
            return oldValue;
         }
      }

      return null;
   }

   void removeStashIndex(int index) {
      --this.stashSize;
      int lastIndex = this.capacity + this.stashSize;
      if (index < lastIndex) {
         this.keyTable[index] = this.keyTable[lastIndex];
         this.valueTable[index] = this.valueTable[lastIndex];
         this.valueTable[lastIndex] = null;
      } else {
         this.valueTable[index] = null;
      }

   }

   public void clear() {
      Object[] keyTable = this.keyTable;
      Object[] valueTable = this.valueTable;

      for(int i = this.capacity + this.stashSize; i-- > 0; valueTable[i] = null) {
         keyTable[i] = null;
      }

      this.size = 0;
      this.stashSize = 0;
   }

   public boolean containsValue(Object value, boolean identity) {
      Object[] valueTable = this.valueTable;
      if (value == null) {
         Object[] keyTable = this.keyTable;
         int i = this.capacity + this.stashSize;

         while(i-- > 0) {
            if (keyTable[i] != null && valueTable[i] == null) {
               return true;
            }
         }
      } else {
         int i;
         if (identity) {
            i = this.capacity + this.stashSize;

            while(i-- > 0) {
               if (valueTable[i] == value) {
                  return true;
               }
            }
         } else {
            i = this.capacity + this.stashSize;

            while(i-- > 0) {
               if (value.equals(valueTable[i])) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public boolean containsKey(K key) {
      int hashCode = System.identityHashCode(key);
      int index = hashCode & this.mask;
      if (key != this.keyTable[index]) {
         index = this.hash2(hashCode);
         if (key != this.keyTable[index]) {
            index = this.hash3(hashCode);
            if (key != this.keyTable[index]) {
               return this.containsKeyStash(key);
            }
         }
      }

      return true;
   }

   private boolean containsKeyStash(K key) {
      Object[] keyTable = this.keyTable;
      int i = this.capacity;

      for(int n = i + this.stashSize; i < n; ++i) {
         if (keyTable[i] == key) {
            return true;
         }
      }

      return false;
   }

   public K findKey(Object value, boolean identity) {
      Object[] valueTable = this.valueTable;
      if (value == null) {
         Object[] keyTable = this.keyTable;
         int i = this.capacity + this.stashSize;

         while(i-- > 0) {
            if (keyTable[i] != null && valueTable[i] == null) {
               return keyTable[i];
            }
         }
      } else {
         int i;
         if (identity) {
            i = this.capacity + this.stashSize;

            while(i-- > 0) {
               if (valueTable[i] == value) {
                  return this.keyTable[i];
               }
            }
         } else {
            i = this.capacity + this.stashSize;

            while(i-- > 0) {
               if (value.equals(valueTable[i])) {
                  return this.keyTable[i];
               }
            }
         }
      }

      return null;
   }

   public void ensureCapacity(int additionalCapacity) {
      int sizeNeeded = this.size + additionalCapacity;
      if (sizeNeeded >= this.threshold) {
         this.resize(MathUtils.nextPowerOfTwo((int)((float)sizeNeeded / this.loadFactor)));
      }

   }

   private void resize(int newSize) {
      int oldEndIndex = this.capacity + this.stashSize;
      this.capacity = newSize;
      this.threshold = (int)((float)newSize * this.loadFactor);
      this.mask = newSize - 1;
      this.hashShift = 31 - Integer.numberOfTrailingZeros(newSize);
      this.stashCapacity = Math.max(3, (int)Math.ceil(Math.log((double)newSize)) * 2);
      this.pushIterations = Math.max(Math.min(newSize, 8), (int)Math.sqrt((double)newSize) / 8);
      Object[] oldKeyTable = this.keyTable;
      Object[] oldValueTable = this.valueTable;
      this.keyTable = new Object[newSize + this.stashCapacity];
      this.valueTable = new Object[newSize + this.stashCapacity];
      this.size = 0;
      this.stashSize = 0;

      for(int i = 0; i < oldEndIndex; ++i) {
         K key = oldKeyTable[i];
         if (key != null) {
            this.putResize(key, oldValueTable[i]);
         }
      }

   }

   private int hash2(int h) {
      h *= -1262997959;
      return (h ^ h >>> this.hashShift) & this.mask;
   }

   private int hash3(int h) {
      h *= -825114047;
      return (h ^ h >>> this.hashShift) & this.mask;
   }

   public String toString() {
      if (this.size == 0) {
         return "[]";
      } else {
         StringBuilder buffer = new StringBuilder(32);
         buffer.append('[');
         Object[] keyTable = this.keyTable;
         Object[] valueTable = this.valueTable;
         int i = keyTable.length;

         Object key;
         while(i-- > 0) {
            key = keyTable[i];
            if (key != null) {
               buffer.append(key);
               buffer.append('=');
               buffer.append(valueTable[i]);
               break;
            }
         }

         while(i-- > 0) {
            key = keyTable[i];
            if (key != null) {
               buffer.append(", ");
               buffer.append(key);
               buffer.append('=');
               buffer.append(valueTable[i]);
            }
         }

         buffer.append(']');
         return buffer.toString();
      }
   }

   public IdentityMap.Entries<K, V> entries() {
      if (this.entries1 == null) {
         this.entries1 = new IdentityMap.Entries(this);
         this.entries2 = new IdentityMap.Entries(this);
      }

      if (!this.entries1.valid) {
         this.entries1.reset();
         this.entries1.valid = true;
         this.entries2.valid = false;
         return this.entries1;
      } else {
         this.entries2.reset();
         this.entries2.valid = true;
         this.entries1.valid = false;
         return this.entries2;
      }
   }

   public IdentityMap.Values<V> values() {
      if (this.values1 == null) {
         this.values1 = new IdentityMap.Values(this);
         this.values2 = new IdentityMap.Values(this);
      }

      if (!this.values1.valid) {
         this.values1.reset();
         this.values1.valid = true;
         this.values2.valid = false;
         return this.values1;
      } else {
         this.values2.reset();
         this.values2.valid = true;
         this.values1.valid = false;
         return this.values2;
      }
   }

   public IdentityMap.Keys<K> keys() {
      if (this.keys1 == null) {
         this.keys1 = new IdentityMap.Keys(this);
         this.keys2 = new IdentityMap.Keys(this);
      }

      if (!this.keys1.valid) {
         this.keys1.reset();
         this.keys1.valid = true;
         this.keys2.valid = false;
         return this.keys1;
      } else {
         this.keys2.reset();
         this.keys2.valid = true;
         this.keys1.valid = false;
         return this.keys2;
      }
   }

   public static class Entries<K, V> extends IdentityMap.MapIterator<K, V> implements Iterable<IdentityMap.Entry<K, V>>, Iterator<IdentityMap.Entry<K, V>> {
      private IdentityMap.Entry<K, V> entry = new IdentityMap.Entry();

      public Entries(IdentityMap<K, V> map) {
         super(map);
      }

      public IdentityMap.Entry<K, V> next() {
         if (!this.hasNext) {
            throw new NoSuchElementException();
         } else if (!this.valid) {
            throw new GdxRuntimeException("#iterator() cannot be used nested.");
         } else {
            Object[] keyTable = this.map.keyTable;
            this.entry.key = keyTable[this.nextIndex];
            this.entry.value = this.map.valueTable[this.nextIndex];
            this.currentIndex = this.nextIndex;
            this.findNextIndex();
            return this.entry;
         }
      }

      public boolean hasNext() {
         return this.hasNext;
      }

      public Iterator<IdentityMap.Entry<K, V>> iterator() {
         return this;
      }
   }

   public static class Entry<K, V> {
      public K key;
      public V value;

      public String toString() {
         return this.key + "=" + this.value;
      }
   }

   public static class Keys<K> extends IdentityMap.MapIterator<K, Object> implements Iterable<K>, Iterator<K> {
      public Keys(IdentityMap<K, ?> map) {
         super(map);
      }

      public boolean hasNext() {
         return this.hasNext;
      }

      public K next() {
         if (!this.hasNext) {
            throw new NoSuchElementException();
         } else if (!this.valid) {
            throw new GdxRuntimeException("#iterator() cannot be used nested.");
         } else {
            K key = this.map.keyTable[this.nextIndex];
            this.currentIndex = this.nextIndex;
            this.findNextIndex();
            return key;
         }
      }

      public Iterator<K> iterator() {
         return this;
      }

      public Array<K> toArray() {
         Array array = new Array(true, this.map.size);

         while(this.hasNext) {
            array.add(this.next());
         }

         return array;
      }
   }

   private static class MapIterator<K, V> {
      public boolean hasNext;
      final IdentityMap<K, V> map;
      int nextIndex;
      int currentIndex;
      boolean valid = true;

      public MapIterator(IdentityMap<K, V> map) {
         this.map = map;
         this.reset();
      }

      public void reset() {
         this.currentIndex = -1;
         this.nextIndex = -1;
         this.findNextIndex();
      }

      void findNextIndex() {
         this.hasNext = false;
         Object[] keyTable = this.map.keyTable;
         int n = this.map.capacity + this.map.stashSize;

         while(++this.nextIndex < n) {
            if (keyTable[this.nextIndex] != null) {
               this.hasNext = true;
               break;
            }
         }

      }

      public void remove() {
         if (this.currentIndex < 0) {
            throw new IllegalStateException("next must be called before remove.");
         } else {
            if (this.currentIndex >= this.map.capacity) {
               this.map.removeStashIndex(this.currentIndex);
            } else {
               this.map.keyTable[this.currentIndex] = null;
               this.map.valueTable[this.currentIndex] = null;
            }

            this.currentIndex = -1;
            --this.map.size;
         }
      }
   }

   public static class Values<V> extends IdentityMap.MapIterator<Object, V> implements Iterable<V>, Iterator<V> {
      public Values(IdentityMap<?, V> map) {
         super(map);
      }

      public boolean hasNext() {
         return this.hasNext;
      }

      public V next() {
         if (!this.hasNext) {
            throw new NoSuchElementException();
         } else if (!this.valid) {
            throw new GdxRuntimeException("#iterator() cannot be used nested.");
         } else {
            V value = this.map.valueTable[this.nextIndex];
            this.currentIndex = this.nextIndex;
            this.findNextIndex();
            return value;
         }
      }

      public Iterator<V> iterator() {
         return this;
      }

      public Array<V> toArray() {
         Array array = new Array(true, this.map.size);

         while(this.hasNext) {
            array.add(this.next());
         }

         return array;
      }

      public void toArray(Array<V> array) {
         while(this.hasNext) {
            array.add(this.next());
         }

      }
   }
}
