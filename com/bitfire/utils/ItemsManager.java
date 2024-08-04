package com.bitfire.utils;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import java.util.Iterator;

public class ItemsManager<T extends Disposable> implements Iterable<T>, Disposable {
   private static final int ItemNotFound = -1;
   private final Array<T> items = new Array();
   protected final Array<Boolean> owned = new Array();

   public void dispose() {
      for(int i = 0; i < this.items.size; ++i) {
         if ((Boolean)this.owned.get(i)) {
            ((Disposable)this.items.get(i)).dispose();
         }
      }

      this.items.clear();
      this.owned.clear();
   }

   public void add(T item, boolean own) {
      if (item != null) {
         this.items.add(item);
         this.owned.add(own);
      }
   }

   public void add(T item) {
      this.add(item, true);
   }

   public T get(int index) {
      return (Disposable)this.items.get(index);
   }

   public int count() {
      return this.items.size;
   }

   public Iterator<T> iterator() {
      return this.items.iterator();
   }

   public void remove(T item) {
      int index = this.items.indexOf(item, true);
      if (index != -1) {
         if ((Boolean)this.owned.get(index)) {
            ((Disposable)this.items.get(index)).dispose();
         }

         this.items.removeIndex(index);
         this.owned.removeIndex(index);
         this.items.removeValue(item, true);
      }
   }
}
