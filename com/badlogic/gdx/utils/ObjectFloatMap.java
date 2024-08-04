package com.badlogic.gdx.utils;

import com.badlogic.gdx.math.MathUtils;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ObjectFloatMap<K> {
   private static final int PRIME1 = -1105259343;
   private static final int PRIME2 = -1262997959;
   private static final int PRIME3 = -825114047;
   public int size;
   K[] keyTable;
   float[] valueTable;
   int capacity;
   int stashSize;
   private float loadFactor;
   private int hashShift;
   private int mask;
   private int threshold;
   private int stashCapacity;
   private int pushIterations;
   private ObjectFloatMap.Entries entries1;
   private ObjectFloatMap.Entries entries2;
   private ObjectFloatMap.Values values1;
   private ObjectFloatMap.Values values2;
   private ObjectFloatMap.Keys keys1;
   private ObjectFloatMap.Keys keys2;

   public ObjectFloatMap() {
      this(32, 0.8F);
   }

   public ObjectFloatMap(int initialCapacity) {
      this(initialCapacity, 0.8F);
   }

   public ObjectFloatMap(int initialCapacity, float loadFactor) {
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
            this.valueTable = new float[this.keyTable.length];
         }
      }
   }

   public void put(K key, float value) {
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

   public void putAll(ObjectFloatMap<K> map) {
      Iterator var3 = map.entries().iterator();

      while(var3.hasNext()) {
         ObjectFloatMap.Entry<K> entry = (ObjectFloatMap.Entry)var3.next();
         this.put(entry.key, entry.value);
      }

   }

   private void putResize(K key, float value) {
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

   private void push(K insertKey, float insertValue, int index1, K key1, int index2, K key2, int index3, K key3) {
      Object[] keyTable = this.keyTable;
      float[] valueTable = this.valueTable;
      int mask = this.mask;
      int i = 0;
      int pushIterations = this.pushIterations;

      while(true) {
         Object evictedKey;
         float evictedValue;
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

   private void putStash(K key, float value) {
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

   public float get(K key, float defaultValue) {
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

   private float getStash(K key, float defaultValue) {
      Object[] keyTable = this.keyTable;
      int i = this.capacity;

      for(int n = i + this.stashSize; i < n; ++i) {
         if (key.equals(keyTable[i])) {
            return this.valueTable[i];
         }
      }

      return defaultValue;
   }

   public float getAndIncrement(K key, float defaultValue, float increment) {
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

      float value = this.valueTable[index];
      this.valueTable[index] = value + increment;
      return value;
   }

   private float getAndIncrementStash(K key, float defaultValue, float increment) {
      Object[] keyTable = this.keyTable;
      int i = this.capacity;

      for(int n = i + this.stashSize; i < n; ++i) {
         if (key.equals(keyTable[i])) {
            float value = this.valueTable[i];
            this.valueTable[i] = value + increment;
            return value;
         }
      }

      this.put(key, defaultValue + increment);
      return defaultValue;
   }

   public float remove(K key, float defaultValue) {
      int hashCode = key.hashCode();
      int index = hashCode & this.mask;
      float oldValue;
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

   float removeStash(K key, float defaultValue) {
      Object[] keyTable = this.keyTable;
      int i = this.capacity;

      for(int n = i + this.stashSize; i < n; ++i) {
         if (key.equals(keyTable[i])) {
            float oldValue = this.valueTable[i];
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

   public boolean containsValue(float value) {
      float[] valueTable = this.valueTable;
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

   public K findKey(float value) {
      float[] valueTable = this.valueTable;
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
      float[] oldValueTable = this.valueTable;
      this.keyTable = new Object[newSize + this.stashCapacity];
      this.valueTable = new float[newSize + this.stashCapacity];
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
         float[] valueTable = this.valueTable;
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

   public ObjectFloatMap.Entries<K> entries() {
      if (this.entries1 == null) {
         this.entries1 = new ObjectFloatMap.Entries(this);
         this.entries2 = new ObjectFloatMap.Entries(this);
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

   public ObjectFloatMap.Values values() {
      if (this.values1 == null) {
         this.values1 = new ObjectFloatMap.Values(this);
         this.values2 = new ObjectFloatMap.Values(this);
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

   public ObjectFloatMap.Keys<K> keys() {
      if (this.keys1 == null) {
         this.keys1 = new ObjectFloatMap.Keys(this);
         this.keys2 = new ObjectFloatMap.Keys(this);
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

   public static class Entries<K> extends ObjectFloatMap.MapIterator<K> implements Iterable<ObjectFloatMap.Entry<K>>, Iterator<ObjectFloatMap.Entry<K>> {
      private ObjectFloatMap.Entry<K> entry = new ObjectFloatMap.Entry();

      public Entries(ObjectFloatMap<K> map) {
         super(map);
      }

      public ObjectFloatMap.Entry<K> next() {
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

      public Iterator<ObjectFloatMap.Entry<K>> iterator() {
         return this;
      }
   }

   public static class Entry<K> {
      public K key;
      public float value;

      public String toString() {
         return this.key + "=" + this.value;
      }
   }

   public static class Keys<K> extends ObjectFloatMap.MapIterator<K> implements Iterable<K>, Iterator<K> {
      public Keys(ObjectFloatMap<K> map) {
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
      final ObjectFloatMap<K> map;
      int nextIndex;
      int currentIndex;
      boolean valid = true;

      public MapIterator(ObjectFloatMap<K> map) {
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

   public static class Values extends ObjectFloatMap.MapIterator<Object> {
      public Values(ObjectFloatMap<?> map) {
         super(map);
      }

      public boolean hasNext() {
         return this.hasNext;
      }

      public float next() {
         if (!this.hasNext) {
            throw new NoSuchElementException();
         } else if (!this.valid) {
            throw new GdxRuntimeException("#iterator() cannot be used nested.");
         } else {
            float value = this.map.valueTable[this.nextIndex];
            this.currentIndex = this.nextIndex;
            this.findNextIndex();
            return value;
         }
      }

      public FloatArray toArray() {
         FloatArray array = new FloatArray(true, this.map.size);

         while(this.hasNext) {
            array.add(this.next());
         }

         return array;
      }
   }
}
