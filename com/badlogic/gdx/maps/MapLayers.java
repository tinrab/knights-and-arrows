package com.badlogic.gdx.maps;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import java.util.Iterator;

public class MapLayers implements Iterable<MapLayer> {
   private Array<MapLayer> layers = new Array();

   public MapLayer get(int index) {
      return (MapLayer)this.layers.get(index);
   }

   public MapLayer get(String name) {
      Iterator var3 = this.layers.iterator();

      while(var3.hasNext()) {
         MapLayer layer = (MapLayer)var3.next();
         if (name.equals(layer.getName())) {
            return layer;
         }
      }

      return null;
   }

   public int getCount() {
      return this.layers.size;
   }

   public void add(MapLayer layer) {
      this.layers.add(layer);
   }

   public void remove(int index) {
      this.layers.removeIndex(index);
   }

   public void remove(MapLayer layer) {
      this.layers.removeValue(layer, true);
   }

   public <T extends MapLayer> Array<T> getByType(Class<T> type) {
      return this.getByType(type, new Array());
   }

   public <T extends MapLayer> Array<T> getByType(Class<T> type, Array<T> fill) {
      fill.clear();
      Iterator var4 = this.layers.iterator();

      while(var4.hasNext()) {
         MapLayer layer = (MapLayer)var4.next();
         if (ClassReflection.isInstance(type, layer)) {
            fill.add(layer);
         }
      }

      return fill;
   }

   public Iterator<MapLayer> iterator() {
      return this.layers.iterator();
   }
}
