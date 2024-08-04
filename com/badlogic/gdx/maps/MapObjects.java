package com.badlogic.gdx.maps;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import java.util.Iterator;

public class MapObjects implements Iterable<MapObject> {
   private Array<MapObject> objects = new Array();

   public MapObject get(int index) {
      return (MapObject)this.objects.get(index);
   }

   public MapObject get(String name) {
      Iterator var3 = this.objects.iterator();

      while(var3.hasNext()) {
         MapObject object = (MapObject)var3.next();
         if (name.equals(object.getName())) {
            return object;
         }
      }

      return null;
   }

   public void add(MapObject object) {
      this.objects.add(object);
   }

   public void remove(int index) {
      this.objects.removeIndex(index);
   }

   public void remove(MapObject object) {
      this.objects.removeValue(object, true);
   }

   public int getCount() {
      return this.objects.size;
   }

   public <T extends MapObject> Array<T> getByType(Class<T> type) {
      return this.getByType(type, new Array());
   }

   public <T extends MapObject> Array<T> getByType(Class<T> type, Array<T> fill) {
      fill.clear();
      Iterator var4 = this.objects.iterator();

      while(var4.hasNext()) {
         MapObject object = (MapObject)var4.next();
         if (ClassReflection.isInstance(type, object)) {
            fill.add(object);
         }
      }

      return fill;
   }

   public Iterator<MapObject> iterator() {
      return this.objects.iterator();
   }
}
