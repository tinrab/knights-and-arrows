package com.minild44.www.core.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.minild44.www.core.Level;
import com.minild44.www.util.Light;
import java.util.Iterator;

public abstract class Entity implements Comparable<Entity> {
   private static int next;
   private int id;
   protected Level level;
   private boolean removed;
   protected Rectangle bounds = new Rectangle();
   protected byte layer;

   public Entity() {
      this.id = ++next;
   }

   public int getID() {
      return this.id;
   }

   public int compareTo(Entity o) {
      if (this.layer < o.layer) {
         return 1;
      } else {
         return this.layer > o.layer ? -1 : 0;
      }
   }

   public abstract void update(float var1);

   public abstract void render(SpriteBatch var1);

   public void remove() {
      this.removed = true;
   }

   public boolean wasRemoved() {
      return this.removed;
   }

   public void enter(Level level) {
      this.level = level;
      this.onEnterLevel();
   }

   public void onEnterLevel() {
   }

   public void setPosition(float x, float y) {
      this.bounds.setPosition(x, y);
   }

   public void setBounds(float x, float y, float width, float height) {
      this.bounds.set(x, y, width, height);
   }

   public Rectangle getBounds() {
      return this.bounds;
   }

   protected Color getLuminance() {
      Color tint = new Color(this.level.getBrightness(), this.level.getBrightness(), this.level.getBrightness(), 1.0F);
      Iterator var3 = this.level.getLights().getLights().iterator();

      while(var3.hasNext()) {
         Light light = (Light)var3.next();
         if (light.isActive()) {
            Vector2 c = this.bounds.getCenter();
            Vector2 l = new Vector2(light.getX(), light.getY());
            Vector2 sub = c.cpy().sub(l);
            sub.nor();
            l.add(sub.scl(32.0F));
            float r = light.fraction * this.level.getLights().getLightSize() / 2.0F;
            float dst = c.dst(l);
            if (this.level.canSee(c, l, r)) {
               tint.add(light.color.cpy().mul(1.0F - dst / r));
            }
         }
      }

      tint.clamp();
      tint.a = 1.0F;
      return tint;
   }

   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else {
         return this.id == ((Entity)obj).id;
      }
   }

   public void dispose() {
   }
}
