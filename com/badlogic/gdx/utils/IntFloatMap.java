package com.badlogic.gdx.utils;

import com.badlogic.gdx.math.MathUtils;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class IntFloatMap {
   private static final int PRIME1 = -1105259343;
   private static final int PRIME2 = -1262997959;
   private static final int PRIME3 = -825114047;
   private static final int EMPTY = 0;
   public int size;
   int[] keyTable;
   float[] valueTable;
   int capacity;
   int stashSize;
   float zeroValue;
   boolean hasZeroValue;
   private float loadFactor;
   private int hashShift;
   private int mask;
   private int threshold;
   private int stashCapacity;
   private int pushIterations;
   private IntFloatMap.Entries entries1;
   private IntFloatMap.Entries entries2;
   private IntFloatMap.Values values1;
   private IntFloatMap.Values values2;
   private IntFloatMap.Keys keys1;
   private IntFloatMap.Keys keys2;

   public IntFloatMap() {
      this(32, 0.8F);
   }

   public IntFloatMap(int initialCapacity) {
      this(initialCapacity, 0.8F);
   }

   public IntFloatMap(int initialCapacity, float loadFactor) {
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
            this.keyTable = new int[this.capacity + this.stashCapacity];
            this.valueTable = new float[this.keyTable.length];
         }
      }
   }

   public void put(int key, float value) {
      if (key == 0) {
         this.zeroValue = value;
         if (!this.hasZeroValue) {
            this.hasZeroValue = true;
            ++this.size;
         }

      } else {
         int[] keyTable = this.keyTable;
         int index1 = key & this.mask;
         int key1 = keyTable[index1];
         if (key == key1) {
            this.valueTable[index1] = value;
         } else {
            int index2 = this.hash2(key);
            int key2 = keyTable[index2];
            if (key == key2) {
               this.valueTable[index2] = value;
            } else {
               int index3 = this.hash3(key);
               int key3 = keyTable[index3];
               if (key == key3) {
                  this.valueTable[index3] = value;
               } else {
                  int i = this.capacity;

                  for(int n = i + this.stashSize; i < n; ++i) {
                     if (key == keyTable[i]) {
                        this.valueTable[i] = value;
                        return;
                     }
                  }

                  if (key1 == 0) {
                     keyTable[index1] = key;
                     this.valueTable[index1] = value;
                     if (this.size++ >= this.threshold) {
                        this.resize(this.capacity << 1);
                     }

                  } else if (key2 == 0) {
                     keyTable[index2] = key;
                     this.valueTable[index2] = value;
                     if (this.size++ >= this.threshold) {
                        this.resize(this.capacity << 1);
                     }

                  } else if (key3 == 0) {
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

   public void putAll(IntFloatMap map) {
      Iterator var3 = map.entries().iterator();

      while(var3.hasNext()) {
         IntFloatMap.Entry entry = (IntFloatMap.Entry)var3.next();
         this.put(entry.key, entry.value);
      }

   }

   private void putResize(int key, float value) {
      if (key == 0) {
         this.zeroValue = value;
         this.hasZeroValue = true;
      } else {
         int index1 = key & this.mask;
         int key1 = this.keyTable[index1];
         if (key1 == 0) {
            this.keyTable[index1] = key;
            this.valueTable[index1] = value;
            if (this.size++ >= this.threshold) {
               this.resize(this.capacity << 1);
            }

         } else {
            int index2 = this.hash2(key);
            int key2 = this.keyTable[index2];
            if (key2 == 0) {
               this.keyTable[index2] = key;
               this.valueTable[index2] = value;
               if (this.size++ >= this.threshold) {
                  this.resize(this.capacity << 1);
               }

            } else {
               int index3 = this.hash3(key);
               int key3 = this.keyTable[index3];
               if (key3 == 0) {
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
   }

   private void push(int insertKey, float insertValue, int index1, int key1, int index2, int key2, int index3, int key3) {
      int[] keyTable = this.keyTable;
      float[] valueTable = this.valueTable;
      int mask = this.mask;
      int i = 0;
      int pushIterations = this.pushIterations;

      while(true) {
         int evictedKey;
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

         index1 = evictedKey & mask;
         key1 = keyTable[index1];
         if (key1 == 0) {
            keyTable[index1] = evictedKey;
            valueTable[index1] = evictedValue;
            if (this.size++ >= this.threshold) {
               this.resize(this.capacity << 1);
            }

            return;
         }

         index2 = this.hash2(evictedKey);
         key2 = keyTable[index2];
         if (key2 == 0) {
            keyTable[index2] = evictedKey;
            valueTable[index2] = evictedValue;
            if (this.size++ >= this.threshold) {
               this.resize(this.capacity << 1);
            }

            return;
         }

         index3 = this.hash3(evictedKey);
         key3 = keyTable[index3];
         if (key3 == 0) {
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

   private void putStash(int key, float value) {
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

   public float get(int key, float defaultValue) {
      if (key == 0) {
         return this.zeroValue;
      } else {
         int index = key & this.mask;
         if (this.keyTable[index] != key) {
            index = this.hash2(key);
            if (this.keyTable[index] != key) {
               index = this.hash3(key);
               if (this.keyTable[index] != key) {
                  return this.getStash(key, defaultValue);
               }
            }
         }

         return this.valueTable[index];
      }
   }

   private float getStash(int key, float defaultValue) {
      int[] keyTable = this.keyTable;
      int i = this.capacity;

      for(int n = i + this.stashSize; i < n; ++i) {
         if (key == keyTable[i]) {
            return this.valueTable[i];
         }
      }

      return defaultValue;
   }

   public float getAndIncrement(int key, float defaultValue, float increment) {
      int index = key & this.mask;
      if (key != this.keyTable[index]) {
         index = this.hash2(key);
         if (key != this.keyTable[index]) {
            index = this.hash3(key);
            if (key != this.keyTable[index]) {
               return this.getAndIncrementStash(key, defaultValue, increment);
            }
         }
      }

      float value = this.valueTable[index];
      this.valueTable[index] = value + increment;
      return value;
   }

   private float getAndIncrementStash(int key, float defaultValue, float increment) {
      int[] keyTable = this.keyTable;
      int i = this.capacity;

      for(int n = i + this.stashSize; i < n; ++i) {
         if (key == keyTable[i]) {
            float value = this.valueTable[i];
            this.valueTable[i] = value + increment;
            return value;
         }
      }

      this.put(key, defaultValue + increment);
      return defaultValue;
   }

   public float remove(int key, float defaultValue) {
      if (key == 0) {
         if (!this.hasZeroValue) {
            return defaultValue;
         } else {
            this.hasZeroValue = false;
            --this.size;
            return this.zeroValue;
         }
      } else {
         int index = key & this.mask;
         float oldValue;
         if (key == this.keyTable[index]) {
            this.keyTable[index] = 0;
            oldValue = this.valueTable[index];
            --this.size;
            return oldValue;
         } else {
            index = this.hash2(key);
            if (key == this.keyTable[index]) {
               this.keyTable[index] = 0;
               oldValue = this.valueTable[index];
               --this.size;
               return oldValue;
            } else {
               index = this.hash3(key);
               if (key == this.keyTable[index]) {
                  this.keyTable[index] = 0;
                  oldValue = this.valueTable[index];
                  --this.size;
                  return oldValue;
               } else {
                  return this.removeStash(key, defaultValue);
               }
            }
         }
      }
   }

   float removeStash(int key, float defaultValue) {
      int[] keyTable = this.keyTable;
      int i = this.capacity;

      for(int n = i + this.stashSize; i < n; ++i) {
         if (key == keyTable[i]) {
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
      int[] keyTable = this.keyTable;

      for(int i = this.capacity + this.stashSize; i-- > 0; keyTable[i] = 0) {
      }

      this.size = 0;
      this.stashSize = 0;
   }

   public boolean containsValue(float value) {
      if (this.hasZeroValue && this.zeroValue == value) {
         return true;
      } else {
         float[] valueTable = this.valueTable;
         int i = this.capacity + this.stashSize;

         while(i-- > 0) {
            if (valueTable[i] == value) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean containsValue(float value, float epsilon) {
      if (this.hasZeroValue && this.zeroValue == value) {
         return true;
      } else {
         float[] valueTable = this.valueTable;
         int i = this.capacity + this.stashSize;

         while(i-- > 0) {
            if (Math.abs(valueTable[i] - value) <= epsilon) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean containsKey(int key) {
      if (key == 0) {
         return this.hasZeroValue;
      } else {
         int index = key & this.mask;
         if (this.keyTable[index] != key) {
            index = this.hash2(key);
            if (this.keyTable[index] != key) {
               index = this.hash3(key);
               if (this.keyTable[index] != key) {
                  return this.containsKeyStash(key);
               }
            }
         }

         return true;
      }
   }

   private boolean containsKeyStash(int key) {
      int[] keyTable = this.keyTable;
      int i = this.capacity;

      for(int n = i + this.stashSize; i < n; ++i) {
         if (key == keyTable[i]) {
            return true;
         }
      }

      return false;
   }

   public int findKey(float value, int notFound) {
      if (this.hasZeroValue && this.zeroValue == value) {
         return 0;
      } else {
         float[] valueTable = this.valueTable;
         int i = this.capacity + this.stashSize;

         while(i-- > 0) {
            if (valueTable[i] == value) {
               return this.keyTable[i];
            }
         }

         return notFound;
      }
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
      int[] oldKeyTable = this.keyTable;
      float[] oldValueTable = this.valueTable;
      this.keyTable = new int[newSize + this.stashCapacity];
      this.valueTable = new float[newSize + this.stashCapacity];
      this.size = this.hasZeroValue ? 1 : 0;
      this.stashSize = 0;

      for(int i = 0; i < oldEndIndex; ++i) {
         int key = oldKeyTable[i];
         if (key != 0) {
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
         int[] keyTable = this.keyTable;
         float[] valueTable = this.valueTable;
         int i = keyTable.length;
         int key;
         if (this.hasZeroValue) {
            buffer.append("0=");
            buffer.append(this.zeroValue);
         } else {
            while(i-- > 0) {
               key = keyTable[i];
               if (key != 0) {
                  buffer.append(key);
                  buffer.append('=');
                  buffer.append(valueTable[i]);
                  break;
               }
            }
         }

         while(i-- > 0) {
            key = keyTable[i];
            if (key != 0) {
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

   public IntFloatMap.Entries entries() {
      if (this.entries1 == null) {
         this.entries1 = new IntFloatMap.Entries(this);
         this.entries2 = new IntFloatMap.Entries(this);
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

   public IntFloatMap.Values values() {
      if (this.values1 == null) {
         this.values1 = new IntFloatMap.Values(this);
         this.values2 = new IntFloatMap.Values(this);
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

   public IntFloatMap.Keys keys() {
      if (this.keys1 == null) {
         this.keys1 = new IntFloatMap.Keys(this);
         this.keys2 = new IntFloatMap.Keys(this);
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

   public static class Entries extends IntFloatMap.MapIterator implements Iterable<IntFloatMap.Entry>, Iterator<IntFloatMap.Entry> {
      private IntFloatMap.Entry entry = new IntFloatMap.Entry();

      public Entries(IntFloatMap map) {
         super(map);
      }

      public IntFloatMap.Entry next() {
         if (!this.hasNext) {
            throw new NoSuchElementException();
         } else if (!this.valid) {
            throw new GdxRuntimeException("#iterator() cannot be used nested.");
         } else {
            int[] keyTable = this.map.keyTable;
            if (this.nextIndex == -1) {
               this.entry.key = 0;
               this.entry.value = this.map.zeroValue;
            } else {
               this.entry.key = keyTable[this.nextIndex];
               this.entry.value = this.map.valueTable[this.nextIndex];
            }

            this.currentIndex = this.nextIndex;
            this.findNextIndex();
            return this.entry;
         }
      }

      public boolean hasNext() {
         return this.hasNext;
      }

      public Iterator<IntFloatMap.Entry> iterator() {
         return this;
      }
   }

   public static class Entry<K> {
      public int key;
      public float value;

      public String toString() {
         return this.key + "=" + this.value;
      }
   }

   public static class Keys extends IntFloatMap.MapIterator {
      public Keys(IntFloatMap map) {
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
            int key = this.nextIndex == -1 ? 0 : this.map.keyTable[this.nextIndex];
            this.currentIndex = this.nextIndex;
            this.findNextIndex();
            return key;
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

   private static class MapIterator<K> {
      static final int INDEX_ILLEGAL = -2;
      static final int INDEX_ZERO = -1;
      public boolean hasNext;
      final IntFloatMap map;
      int nextIndex;
      int currentIndex;
      boolean valid = true;

      public MapIterator(IntFloatMap map) {
         this.map = map;
         this.reset();
      }

      public void reset() {
         this.currentIndex = -2;
         this.nextIndex = -1;
         if (this.map.hasZeroValue) {
            this.hasNext = true;
         } else {
            this.findNextIndex();
         }

      }

      void findNextIndex() {
         this.hasNext = false;
         int[] keyTable = this.map.keyTable;
         int n = this.map.capacity + this.map.stashSize;

         while(++this.nextIndex < n) {
            if (keyTable[this.nextIndex] != 0) {
               this.hasNext = true;
               break;
            }
         }

      }

      public void remove() {
         if (this.currentIndex == -1 && this.map.hasZeroValue) {
            this.map.hasZeroValue = false;
         } else {
            if (this.currentIndex < 0) {
               throw new IllegalStateException("next must be called before remove.");
            }

            if (this.currentIndex >= this.map.capacity) {
               this.map.removeStashIndex(this.currentIndex);
            } else {
               this.map.keyTable[this.currentIndex] = 0;
            }
         }

         this.currentIndex = -2;
         --this.map.size;
      }
   }

   public static class Values extends IntFloatMap.MapIterator<Object> {
      public Values(IntFloatMap map) {
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
            float value;
            if (this.nextIndex == -1) {
               value = this.map.zeroValue;
            } else {
               value = this.map.valueTable[this.nextIndex];
            }

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
