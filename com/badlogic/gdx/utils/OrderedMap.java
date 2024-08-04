package com.badlogic.gdx.utils;

public class OrderedMap<K, V> extends ObjectMap<K, V> {
   final Array<K> keys;

   public OrderedMap() {
      this.keys = new Array();
   }

   public OrderedMap(int initialCapacity) {
      super(initialCapacity);
      this.keys = new Array(initialCapacity);
   }

   public OrderedMap(int initialCapacity, float loadFactor) {
      super(initialCapacity, loadFactor);
      this.keys = new Array(initialCapacity);
   }

   public V put(K key, V value) {
      if (!this.containsKey(key)) {
         this.keys.add(key);
      }

      return super.put(key, value);
   }

   public V remove(K key) {
      this.keys.removeValue(key, false);
      return super.remove(key);
   }

   public void clear() {
      this.keys.clear();
      super.clear();
   }

   public Array<K> orderedKeys() {
      return this.keys;
   }

   public ObjectMap.Entries<K, V> entries() {
      return new ObjectMap.Entries(this) {
         void advance() {
            ++this.nextIndex;
            this.hasNext = this.nextIndex < this.map.size;
         }

         public ObjectMap.Entry next() {
            this.entry.key = OrderedMap.this.keys.get(this.nextIndex);
            this.entry.value = this.map.get(this.entry.key);
            this.advance();
            return this.entry;
         }

         public void remove() {
            this.map.remove(this.entry.key);
         }
      };
   }

   public ObjectMap.Keys<K> keys() {
      return new ObjectMap.Keys(this) {
         void advance() {
            ++this.nextIndex;
            this.hasNext = this.nextIndex < this.map.size;
         }

         public K next() {
            K key = OrderedMap.this.keys.get(this.nextIndex);
            this.advance();
            return key;
         }

         public void remove() {
            this.map.remove(OrderedMap.this.keys.get(this.nextIndex - 1));
         }
      };
   }

   public ObjectMap.Values<V> values() {
      return new ObjectMap.Values(this) {
         void advance() {
            ++this.nextIndex;
            this.hasNext = this.nextIndex < this.map.size;
         }

         public V next() {
            V value = this.map.get(OrderedMap.this.keys.get(this.nextIndex));
            this.advance();
            return value;
         }

         public void remove() {
            this.map.remove(OrderedMap.this.keys.get(this.nextIndex - 1));
         }
      };
   }

   public String toString() {
      if (this.size == 0) {
         return "{}";
      } else {
         StringBuilder buffer = new StringBuilder(32);
         buffer.append('{');
         Array<K> keys = this.keys;
         int i = 0;

         for(int n = keys.size; i < n; ++i) {
            K key = keys.get(i);
            if (i > 0) {
               buffer.append(", ");
            }

            buffer.append(key);
            buffer.append('=');
            buffer.append(this.get(key));
         }

         buffer.append('}');
         return buffer.toString();
      }
   }
}
