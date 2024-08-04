package com.badlogic.gdx.graphics.g3d.materials;

import com.badlogic.gdx.utils.Array;
import java.util.Comparator;
import java.util.Iterator;

public class Material implements Iterable<Material.Attribute>, Comparator<Material.Attribute> {
   private static final Array<String> types = new Array();
   private static int counter = 0;
   public String id;
   protected long mask;
   protected final Array<Material.Attribute> attributes;
   protected boolean sorted;

   protected static final long getAttributeType(String alias) {
      for(int i = 0; i < types.size; ++i) {
         if (((String)types.get(i)).compareTo(alias) == 0) {
            return 1L << i;
         }
      }

      return 0L;
   }

   protected static final String getAttributeAlias(long type) {
      int idx = -1;

      while(type != 0L) {
         ++idx;
         if (idx >= 63 || (type >> idx & 1L) != 0L) {
            break;
         }
      }

      return idx >= 0 && idx < types.size ? (String)types.get(idx) : null;
   }

   protected static final long register(String alias) {
      long result = getAttributeType(alias);
      if (result > 0L) {
         return result;
      } else {
         types.add(alias);
         return 1L << types.size - 1;
      }
   }

   public Material() {
      this("mtl" + ++counter);
   }

   public Material(String id) {
      this.attributes = new Array();
      this.sorted = true;
      this.id = id;
   }

   public Material(Material.Attribute... attributes) {
      this();
      this.set(attributes);
   }

   public Material(String id, Material.Attribute... attributes) {
      this(id);
      this.set(attributes);
   }

   public Material(Array<Material.Attribute> attributes) {
      this();
      this.set(attributes);
   }

   public Material(String id, Array<Material.Attribute> attributes) {
      this(id);
      this.set(attributes);
   }

   public Material(Material copyFrom) {
      this(copyFrom.id, copyFrom);
   }

   public Material(String id, Material copyFrom) {
      this(id);
      Iterator var4 = copyFrom.iterator();

      while(var4.hasNext()) {
         Material.Attribute attr = (Material.Attribute)var4.next();
         this.set(attr.copy());
      }

   }

   private final void enable(long mask) {
      this.mask |= mask;
   }

   private final void disable(long mask) {
      this.mask &= ~mask;
   }

   public final long getMask() {
      return this.mask;
   }

   public final boolean has(long type) {
      return type > 0L && (this.mask & type) == type;
   }

   protected int indexOf(long type) {
      if (this.has(type)) {
         for(int i = 0; i < this.attributes.size; ++i) {
            if (((Material.Attribute)this.attributes.get(i)).type == type) {
               return i;
            }
         }
      }

      return -1;
   }

   public final void set(Material.Attribute attribute) {
      int idx = this.indexOf(attribute.type);
      if (idx < 0) {
         this.enable(attribute.type);
         this.attributes.add(attribute);
         this.sorted = false;
      } else {
         this.attributes.set(idx, attribute);
      }

   }

   public final void set(Material.Attribute... attributes) {
      Material.Attribute[] var5 = attributes;
      int var4 = attributes.length;

      for(int var3 = 0; var3 < var4; ++var3) {
         Material.Attribute attr = var5[var3];
         this.set(attr);
      }

   }

   public final void set(Array<Material.Attribute> attributes) {
      Iterator var3 = attributes.iterator();

      while(var3.hasNext()) {
         Material.Attribute attr = (Material.Attribute)var3.next();
         this.set(attr);
      }

   }

   public final void remove(long mask) {
      for(int i = 0; i < this.attributes.size; ++i) {
         long type = ((Material.Attribute)this.attributes.get(i)).type;
         if ((mask & type) == type) {
            this.attributes.removeIndex(i);
            this.disable(type);
            this.sorted = false;
         }
      }

   }

   public final Material.Attribute get(long type) {
      if (this.has(type)) {
         for(int i = 0; i < this.attributes.size; ++i) {
            if (((Material.Attribute)this.attributes.get(i)).type == type) {
               return (Material.Attribute)this.attributes.get(i);
            }
         }
      }

      return null;
   }

   public final Array<Material.Attribute> get(Array<Material.Attribute> out, long type) {
      for(int i = 0; i < this.attributes.size; ++i) {
         if ((((Material.Attribute)this.attributes.get(i)).type & type) != 0L) {
            out.add((Material.Attribute)this.attributes.get(i));
         }
      }

      return out;
   }

   public final void clear() {
      this.mask = 0L;
      this.attributes.clear();
   }

   public int size() {
      return this.attributes.size;
   }

   public final Material copy() {
      return new Material(this);
   }

   public final int compare(Material.Attribute arg0, Material.Attribute arg1) {
      return (int)(arg0.type - arg1.type);
   }

   public final void sort() {
      if (!this.sorted) {
         this.attributes.sort(this);
         this.sorted = true;
      }

   }

   public final boolean same(Material other) {
      return this.mask == other.mask;
   }

   public final boolean equals(Material other) {
      if (other == null) {
         return false;
      } else if (other == this) {
         return true;
      } else if (!this.same(other)) {
         return false;
      } else {
         this.sort();
         other.sort();

         for(int i = 0; i < this.attributes.size; ++i) {
            if (!((Material.Attribute)this.attributes.get(i)).equals((Material.Attribute)other.attributes.get(i))) {
               return false;
            }
         }

         return true;
      }
   }

   public final boolean equals(Object obj) {
      return obj instanceof Material ? this.equals((Material)obj) : false;
   }

   public final Iterator<Material.Attribute> iterator() {
      return this.attributes.iterator();
   }

   public abstract static class Attribute {
      public final long type;

      protected static long register(String type) {
         return Material.register(type);
      }

      protected Attribute(long type) {
         this.type = type;
      }

      public abstract Material.Attribute copy();

      protected abstract boolean equals(Material.Attribute var1);

      public boolean equals(Object obj) {
         if (obj == null) {
            return false;
         } else if (obj == this) {
            return true;
         } else if (!(obj instanceof Material.Attribute)) {
            return false;
         } else {
            Material.Attribute other = (Material.Attribute)obj;
            return other.type != other.type ? false : this.equals(other);
         }
      }

      public String toString() {
         return Material.getAttributeAlias(this.type);
      }
   }
}
