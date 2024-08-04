package com.minild44.www.core.entities;

import com.badlogic.gdx.math.Rectangle;
import com.minild44.www.core.Level;

public abstract class Spawner {
   private static byte next;
   protected byte id;
   protected int delay;
   protected int max;
   protected Entity target;
   protected float last;
   protected Rectangle bounds;

   public Spawner(int x, int y, int w, int h, int delay, int max, Entity target) {
      byte var10001 = next;
      next = (byte)(var10001 + 1);
      this.id = var10001;
      this.last = (float)delay;
      this.delay = delay;
      this.max = max;
      this.target = target;
      this.bounds = new Rectangle((float)x, (float)y, (float)w, (float)h);
   }

   public abstract void update(float var1, Level var2);
}
