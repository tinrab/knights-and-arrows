package com.badlogic.gdx.utils;

import com.badlogic.gdx.math.MathUtils;
import java.util.NoSuchElementException;

public class IntSet {
   private static final int PRIME1 = -1105259343;
   private static final int PRIME2 = -1262997959;
   private static final int PRIME3 = -825114047;
   private static final int EMPTY = 0;
   public int size;
   int[] keyTable;
   int capacity;
   int stashSize;
   boolean hasZeroValue;
   private float loadFactor;
   private int hashShift;
   private int mask;
   private int threshold;
   private int stashCapacity;
   private int pushIterations;
   private IntSet.IntSetIterator iterator1;
   private IntSet.IntSetIterator iterator2;

   public IntSet() {
      this(32, 0.8F);
   }

   public IntSet(int initialCapacity) {
      this(initialCapacity, 0.8F);
   }

   public IntSet(int initialCapacity, float loadFactor) {
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
         }
      }
   }

   public boolean add(int key) {
      if (key == 0) {
         if (this.hasZeroValue) {
            return false;
         } else {
            this.hasZeroValue = true;
            ++this.size;
            return true;
         }
      } else {
         int[] keyTable = this.keyTable;
         int index1 = key & this.mask;
         int key1 = keyTable[index1];
         if (key1 == key) {
            return false;
         } else {
            int index2 = this.hash2(key);
            int key2 = keyTable[index2];
            if (key2 == key) {
               return false;
            } else {
               int index3 = this.hash3(key);
               int key3 = keyTable[index3];
               if (key3 == key) {
                  return false;
               } else {
                  int i = this.capacity;

                  for(int n = i + this.stashSize; i < n; ++i) {
                     if (keyTable[i] == key) {
                        return false;
                     }
                  }

                  if (key1 == 0) {
                     keyTable[index1] = key;
                     if (this.size++ >= this.threshold) {
                        this.resize(this.capacity << 1);
                     }

                     return true;
                  } else if (key2 == 0) {
                     keyTable[index2] = key;
                     if (this.size++ >= this.threshold) {
                        this.resize(this.capacity << 1);
                     }

                     return true;
                  } else if (key3 == 0) {
                     keyTable[index3] = key;
                     if (this.size++ >= this.threshold) {
                        this.resize(this.capacity << 1);
                     }

                     return true;
                  } else {
                     this.push(key, index1, key1, index2, key2, index3, key3);
                     return true;
                  }
               }
            }
         }
      }
   }

   public void putAll(IntSet set) {
      this.ensureCapacity(set.size);
      IntSet.IntSetIterator iterator = set.iterator();

      while(iterator.hasNext) {
         this.add(iterator.next());
      }

   }

   private void addResize(int key) {
      if (key == 0) {
         this.hasZeroValue = true;
      } else {
         int index1 = key & this.mask;
         int key1 = this.keyTable[index1];
         if (key1 == 0) {
            this.keyTable[index1] = key;
            if (this.size++ >= this.threshold) {
               this.resize(this.capacity << 1);
            }

         } else {
            int index2 = this.hash2(key);
            int key2 = this.keyTable[index2];
            if (key2 == 0) {
               this.keyTable[index2] = key;
               if (this.size++ >= this.threshold) {
                  this.resize(this.capacity << 1);
               }

            } else {
               int index3 = this.hash3(key);
               int key3 = this.keyTable[index3];
               if (key3 == 0) {
                  this.keyTable[index3] = key;
                  if (this.size++ >= this.threshold) {
                     this.resize(this.capacity << 1);
                  }

               } else {
                  this.push(key, index1, key1, index2, key2, index3, key3);
               }
            }
         }
      }
   }

   private void push(int insertKey, int index1, int key1, int index2, int key2, int index3, int key3) {
      int[] keyTable = this.keyTable;
      int mask = this.mask;
      int i = 0;
      int pushIterations = this.pushIterations;

      while(true) {
         int evictedKey;
         switch(MathUtils.random(2)) {
         case 0:
            evictedKey = key1;
            keyTable[index1] = insertKey;
            break;
         case 1:
            evictedKey = key2;
            keyTable[index2] = insertKey;
            break;
         default:
            evictedKey = key3;
            keyTable[index3] = insertKey;
         }

         index1 = evictedKey & mask;
         key1 = keyTable[index1];
         if (key1 == 0) {
            keyTable[index1] = evictedKey;
            if (this.size++ >= this.threshold) {
               this.resize(this.capacity << 1);
            }

            return;
         }

         index2 = this.hash2(evictedKey);
         key2 = keyTable[index2];
         if (key2 == 0) {
            keyTable[index2] = evictedKey;
            if (this.size++ >= this.threshold) {
               this.resize(this.capacity << 1);
            }

            return;
         }

         index3 = this.hash3(evictedKey);
         key3 = keyTable[index3];
         if (key3 == 0) {
            keyTable[index3] = evictedKey;
            if (this.size++ >= this.threshold) {
               this.resize(this.capacity << 1);
            }

            return;
         }

         ++i;
         if (i == pushIterations) {
            this.addStash(evictedKey);
            return;
         }

         insertKey = evictedKey;
      }
   }

   private void addStash(int key) {
      if (this.stashSize == this.stashCapacity) {
         this.resize(this.capacity << 1);
         this.add(key);
      } else {
         int index = this.capacity + this.stashSize;
         this.keyTable[index] = key;
         ++this.stashSize;
         ++this.size;
      }
   }

   public boolean remove(int key) {
      if (key == 0) {
         if (!this.hasZeroValue) {
            return false;
         } else {
            this.hasZeroValue = false;
            --this.size;
            return true;
         }
      } else {
         int index = key & this.mask;
         if (this.keyTable[index] == key) {
            this.keyTable[index] = 0;
            --this.size;
            return true;
         } else {
            index = this.hash2(key);
            if (this.keyTable[index] == key) {
               this.keyTable[index] = 0;
               --this.size;
               return true;
            } else {
               index = this.hash3(key);
               if (this.keyTable[index] == key) {
                  this.keyTable[index] = 0;
                  --this.size;
                  return true;
               } else {
                  return this.removeStash(key);
               }
            }
         }
      }
   }

   boolean removeStash(int key) {
      int[] keyTable = this.keyTable;
      int i = this.capacity;

      for(int n = i + this.stashSize; i < n; ++i) {
         if (keyTable[i] == key) {
            this.removeStashIndex(i);
            --this.size;
            return true;
         }
      }

      return false;
   }

   void removeStashIndex(int index) {
      --this.stashSize;
      int lastIndex = this.capacity + this.stashSize;
      if (index < lastIndex) {
         this.keyTable[index] = this.keyTable[lastIndex];
      }

   }

   public void clear() {
      int[] keyTable = this.keyTable;

      for(int i = this.capacity + this.stashSize; i-- > 0; keyTable[i] = 0) {
      }

      this.size = 0;
      this.stashSize = 0;
      this.hasZeroValue = false;
   }

   public boolean contains(int key) {
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
         if (keyTable[i] == key) {
            return true;
         }
      }

      return false;
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
      this.keyTable = new int[newSize + this.stashCapacity];
      this.size = this.hasZeroValue ? 1 : 0;
      this.stashSize = 0;

      for(int i = 0; i < oldEndIndex; ++i) {
         int key = oldKeyTable[i];
         if (key != 0) {
            this.addResize(key);
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
         int[] keyTable = this.keyTable;
         int i = keyTable.length;
         int key;
         if (this.hasZeroValue) {
            buffer.append("0");
         } else {
            while(i-- > 0) {
               key = keyTable[i];
               if (key != 0) {
                  buffer.append(key);
                  break;
               }
            }
         }

         while(i-- > 0) {
            key = keyTable[i];
            if (key != 0) {
               buffer.append(", ");
               buffer.append(key);
            }
         }

         buffer.append(']');
         return buffer.toString();
      }
   }

   public IntSet.IntSetIterator iterator() {
      if (this.iterator1 == null) {
         this.iterator1 = new IntSet.IntSetIterator(this);
         this.iterator2 = new IntSet.IntSetIterator(this);
      }

      if (!this.iterator1.valid) {
         this.iterator1.reset();
         this.iterator1.valid = true;
         this.iterator2.valid = false;
         return this.iterator1;
      } else {
         this.iterator2.reset();
         this.iterator2.valid = true;
         this.iterator1.valid = false;
         return this.iterator2;
      }
   }

   public static class Entry<V> {
      public int key;
      public V value;

      public String toString() {
         return this.key + "=" + this.value;
      }
   }

   private static class IntSetIterator {
      static final int INDEX_ILLEGAL = -2;
      static final int INDEX_ZERO = -1;
      public boolean hasNext;
      final IntSet set;
      int nextIndex;
      int currentIndex;
      boolean valid = true;

      public IntSetIterator(IntSet set) {
         this.set = set;
         this.reset();
      }

      public void reset() {
         this.currentIndex = -2;
         this.nextIndex = -1;
         if (this.set.hasZeroValue) {
            this.hasNext = true;
         } else {
            this.findNextIndex();
         }

      }

      void findNextIndex() {
         this.hasNext = false;
         int[] keyTable = this.set.keyTable;
         int n = this.set.capacity + this.set.stashSize;

         while(++this.nextIndex < n) {
            if (keyTable[this.nextIndex] != 0) {
               this.hasNext = true;
               break;
            }
         }

      }

      public void remove() {
         if (this.currentIndex == -1 && this.set.hasZeroValue) {
            this.set.hasZeroValue = false;
         } else {
            if (this.currentIndex < 0) {
               throw new IllegalStateException("next must be called before remove.");
            }

            if (this.currentIndex >= this.set.capacity) {
               this.set.removeStashIndex(this.currentIndex);
            } else {
               this.set.keyTable[this.currentIndex] = 0;
            }
         }

         this.currentIndex = -2;
         --this.set.size;
      }

      public int next() {
         if (!this.hasNext) {
            throw new NoSuchElementException();
         } else if (!this.valid) {
            throw new GdxRuntimeException("#iterator() cannot be used nested.");
         } else {
            int key = this.nextIndex == -1 ? 0 : this.set.keyTable[this.nextIndex];
            this.currentIndex = this.nextIndex;
            this.findNextIndex();
            return key;
         }
      }

      public IntArray toArray() {
         IntArray array = new IntArray(true, this.set.size);

         while(this.hasNext) {
            array.add(this.next());
         }

         return array;
      }
   }
}
