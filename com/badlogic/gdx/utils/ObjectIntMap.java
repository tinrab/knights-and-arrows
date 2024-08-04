package com.badlogic.gdx.utils;

import com.badlogic.gdx.math.MathUtils;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ObjectIntMap<K> {
   private static final int PRIME1 = -1105259343;
   private static final int PRIME2 = -1262997959;
   private static final int PRIME3 = -825114047;
   public int size;
   K[] keyTable;
   int[] valueTable;
   int capacity;
   int stashSize;
   private float loadFactor;
   private int hashShift;
   private int mask;
   private int threshold;
   private int stashCapacity;
   private int pushIterations;
   private ObjectIntMap.Entries entries1;
   private ObjectIntMap.Entries entries2;
   private ObjectIntMap.Values values1;
   private ObjectIntMap.Values values2;
   private ObjectIntMap.Keys keys1;
   private ObjectIntMap.Keys keys2;

   public ObjectIntMap() {
      this(32, 0.8F);
   }

   public ObjectIntMap(int initialCapacity) {
      this(initialCapacity, 0.8F);
   }

   public ObjectIntMap(int initialCapacity, float loadFactor) {
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
            this.valueTable = new int[this.keyTable.length];
         }
      }
   }

   public void put(K key, int value) {
      if (key == null) {
         throw new IllegalArgumentException("key cannot be null.");
      } else {
         Object[] keyTable = this.keyTable;
         int hashCode = key.hashCode();
         int index1 = hashCode & this.mask;
         K key1 = keyTable[index1];
         if (key.equals(key1)) {
            this.valueTable[index1] = value;
         } else {
            int index2 = this.hash2(hashCode);
            K key2 = keyTable[index2];
            if (key.equals(key2)) {
               this.valueTable[index2] = value;
            } else {
               int index3 = this.hash3(hashCode);
               K key3 = keyTable[index3];
               if (key.equals(key3)) {
                  this.valueTable[index3] = value;
               } else {
                  int i = this.capacity;

                  for(int n = i + this.stashSize; i < n; ++i) {
                     if (key.equals(keyTable[i])) {
                        this.valueTable[i] = value;
                        return;
                     }
                  }

                  if (key1 == null) {
                     keyTable[index1] = key;
                     this.valueTable[index1] = value;
                     if (this.size++ >= this.threshold) {
                        this.resize(this.capacity << 1);
                     }

                  } else if (key2 == null) {
                     keyTable[index2] = key;
                     this.valueTable[index2] = value;
                     if (this.size++ >= this.threshold) {
                        this.resize(this.capacity << 1);
                     }

                  } else if (key3 == null) {
                     keyTable[index3] = key;
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
      }
   }

   public void putAll(ObjectIntMap<K> map) {
      Iterator var3 = map.entries().iterator();

      while(var3.hasNext()) {
         ObjectIntMap.Entry<K> entry = (ObjectIntMap.Entry)var3.next();
         this.put(entry.key, entry.value);
      }

   }

   private void putResize(K key, int value) {
      int hashCode = key.hashCode();
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

   private void push(K insertKey, int insertValue, int index1, K key1, int index2, K key2, int index3, K key3) {
      Object[] keyTable = this.keyTable;
      int[] valueTable = this.valueTable;
      int mask = this.mask;
      int i = 0;
      int pushIterations = this.pushIterations;

      while(true) {
         Object evictedKey;
         int evictedValue;
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

         int hashCode = evictedKey.hashCode();
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

   private void putStash(K key, int value) {
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

   public int get(K key, int defaultValue) {
      int hashCode = key.hashCode();
      int index = hashCode & this.mask;
      if (!key.equals(this.keyTable[index])) {
         index = this.hash2(hashCode);
         if (!key.equals(this.keyTable[index])) {
            index = this.hash3(hashCode);
            if (!key.equals(this.keyTable[index])) {
               return this.getStash(key, defaultValue);
            }
         }
      }

      return this.valueTable[index];
   }

   private int getStash(K key, int defaultValue) {
      Object[] keyTable = this.keyTable;
      int i = this.capacity;

      for(int n = i + this.stashSize; i < n; ++i) {
         if (key.equals(keyTable[i])) {
            return this.valueTable[i];
         }
      }

      return defaultValue;
   }

   public int getAndIncrement(K key, int defaultValue, int increment) {
      int hashCode = key.hashCode();
      int index = hashCode & this.mask;
      if (!key.equals(this.keyTable[index])) {
         index = this.hash2(hashCode);
         if (!key.equals(this.keyTable[index])) {
            index = this.hash3(hashCode);
            if (!key.equals(this.keyTable[index])) {
               return this.getAndIncrementStash(key, defaultValue, increment);
            }
         }
      }

      int value = this.valueTable[index];
      this.valueTable[index] = value + increment;
      return value;
   }

   private int getAndIncrementStash(K key, int defaultValue, int increment) {
      Object[] keyTable = this.keyTable;
      int i = this.capacity;

      for(int n = i + this.stashSize; i < n; ++i) {
         if (key.equals(keyTable[i])) {
            int value = this.valueTable[i];
            this.valueTable[i] = value + increment;
            return value;
         }
      }

      this.put(key, defaultValue + increment);
      return defaultValue;
   }

   public int remove(K key, int defaultValue) {
      int hashCode = key.hashCode();
      int index = hashCode & this.mask;
      int oldValue;
      if (key.equals(this.keyTable[index])) {
         this.keyTable[index] = null;
         oldValue = this.valueTable[index];
         --this.size;
         return oldValue;
      } else {
         index = this.hash2(hashCode);
         if (key.equals(this.keyTable[index])) {
            this.keyTable[index] = null;
            oldValue = this.valueTable[index];
            --this.size;
            return oldValue;
         } else {
            index = this.hash3(hashCode);
            if (key.equals(this.keyTable[index])) {
               this.keyTable[index] = null;
               oldValue = this.valueTable[index];
               --this.size;
               return oldValue;
            } else {
               return this.removeStash(key, defaultValue);
            }
         }
      }
   }

   int removeStash(K key, int defaultValue) {
      Object[] keyTable = this.keyTable;
      int i = this.capacity;

      for(int n = i + this.stashSize; i < n; ++i) {
         if (key.equals(keyTable[i])) {
            int oldValue = this.valueTable[i];
            this.removeStashIndex(i);
            --this.size;
            return oldValue;
         }
      }

      return defaultValue;
   }

   void removeStashIndex(int index) {
      --this.stashSize;
      int lastIndex = this.capacity + this.stashSize;
      if (index < lastIndex) {
         this.keyTable[index] = this.keyTable[lastIndex];
         this.valueTable[index] = this.valueTable[lastIndex];
      }

   }

   public void clear() {
      Object[] keyTable = this.keyTable;

      for(int i = this.capacity + this.stashSize; i-- > 0; keyTable[i] = null) {
      }

      this.size = 0;
      this.stashSize = 0;
   }

   public boolean containsValue(int value) {
      int[] valueTable = this.valueTable;
      int i = this.capacity + this.stashSize;

      while(i-- > 0) {
         if (valueTable[i] == value) {
            return true;
         }
      }

      return false;
   }

   public boolean containsKey(K key) {
      int hashCode = key.hashCode();
      int index = hashCode & this.mask;
      if (!key.equals(this.keyTable[index])) {
         index = this.hash2(hashCode);
         if (!key.equals(this.keyTable[index])) {
            index = this.hash3(hashCode);
            if (!key.equals(this.keyTable[index])) {
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
         if (key.equals(keyTable[i])) {
            return true;
         }
      }

      return false;
   }

   public K findKey(int value) {
      int[] valueTable = this.valueTable;
      int i = this.capacity + this.stashSize;

      while(i-- > 0) {
         if (valueTable[i] == value) {
            return this.keyTable[i];
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
      int[] oldValueTable = this.valueTable;
      this.keyTable = new Object[newSize + this.stashCapacity];
      this.valueTable = new int[newSize + this.stashCapacity];
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
         return "{}";
      } else {
         StringBuilder buffer = new StringBuilder(32);
         buffer.append('{');
         Object[] keyTable = this.keyTable;
         int[] valueTable = this.valueTable;
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

         buffer.append('}');
         return buffer.toString();
      }
   }

   public ObjectIntMap.Entries<K> entries() {
      if (this.entries1 == null) {
         this.entries1 = new ObjectIntMap.Entries(this);
         this.entries2 = new ObjectIntMap.Entries(this);
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

   public ObjectIntMap.Values values() {
      if (this.values1 == null) {
         this.values1 = new ObjectIntMap.Values(this);
         this.values2 = new ObjectIntMap.Values(this);
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

   public ObjectIntMap.Keys<K> keys() {
      if (this.keys1 == null) {
         this.keys1 = new ObjectIntMap.Keys(this);
         this.keys2 = new ObjectIntMap.Keys(this);
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

   public static class Entries<K> extends ObjectIntMap.MapIterator<K> implements Iterable<ObjectIntMap.Entry<K>>, Iterator<ObjectIntMap.Entry<K>> {
      private ObjectIntMap.Entry<K> entry = new ObjectIntMap.Entry();

      public Entries(ObjectIntMap<K> map) {
         super(map);
      }

      public ObjectIntMap.Entry<K> next() {
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

      public Iterator<ObjectIntMap.Entry<K>> iterator() {
         return this;
      }
   }

   public static class Entry<K> {
      public K key;
      public int value;

      public String toString() {
         return this.key + "=" + this.value;
      }
   }

   public static class Keys<K> extends ObjectIntMap.MapIterator<K> implements Iterable<K>, Iterator<K> {
      public Keys(ObjectIntMap<K> map) {
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

   private static class MapIterator<K> {
      public boolean hasNext;
      final ObjectIntMap<K> map;
      int nextIndex;
      int currentIndex;
      boolean valid = true;

      public MapIterator(ObjectIntMap<K> map) {
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
            }

            this.currentIndex = -1;
            --this.map.size;
         }
      }
   }

   public static class Values extends ObjectIntMap.MapIterator<Object> {
      public Values(ObjectIntMap<?> map) {
         super(map);
      }

      public boolean hasNext() {
         return this.hasNext;
      }

      public int next() {
         if (!this.hasNext) {
            throw new NoSuchElementException();
         } else if (!this.valid) {
            throw new GdxRuntimeException("#iterator() cannot be used nested.");
         } else {
            int value = this.map.valueTable[this.nextIndex];
            this.currentIndex = this.nextIndex;
            this.findNextIndex();
            return value;
         }
      }

      public IntArray toArray() {
         IntArray array = new IntArray(true, this.map.size);

         while(this.hasNext) {
            array.add(this.next());
         }

         return array;
      }
   }
}
