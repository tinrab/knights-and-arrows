package com.minild44.www.core.entities.units;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.minild44.www.Resources;
import com.minild44.www.core.entities.Entity;
import com.minild44.www.core.entities.GreenWind;
import com.minild44.www.core.entities.Unit;
import com.minild44.www.util.Light;
import java.util.Iterator;

public class Monk extends Unit {
   private float healCountdown;
   private Light light;

   public Monk() {
      this.bounds.setSize(32.0F, 32.0F);
      this.speed = 1.6F;
      this.stats.maxHp = 100.0F;
      this.stats.hp = 100.0F;
      this.stats.attackRange = 320.0F;
      this.stats.attackRate = 1.5F;
   }

   public void onEnterLevel() {
      this.light = new Light(0.0F, 0.0F, new Color(28640358));
      this.light.temp = true;
      this.light.castShadows = false;
      this.level.add(this.light);
   }

   public void fire(float sx, float sy, Vector2 target) {
      Resources.play(3, this.bounds.x, this.bounds.y, this.level);
      this.level.add((Entity)(new GreenWind(sx, sy, target, this)));
   }

   public void update(float delta) {
      super.update(delta);
      this.light.set(this.bounds.x + 16.0F, this.bounds.y + 16.0F);
      this.healCountdown -= delta;
      if (this.healCountdown <= 0.0F) {
         this.healCountdown = 8.0F;
         boolean healed = false;
         Iterator var4 = this.squad.getMembers().iterator();

         while(var4.hasNext()) {
            Unit m = (Unit)var4.next();
            if (m.getID() != this.getID() && m.getStats().hp < m.getStats().maxHp) {
               m.heal(m.getStats().maxHp * 0.2F);
               healed = true;
            }
         }

         if (healed) {
            Resources.play(5, this.bounds.x, this.bounds.y, this.level);
            this.level.add("heal", this.bounds.x + this.bounds.width / 2.0F, this.bounds.y + this.bounds.height / 2.0F);
            this.light.setActive(1.0F);
         }
      }

   }

   public void render(SpriteBatch batch) {
      batch.setColor(this.getLuminance());
      int frame = this.animation.getCurrentFrame();
      int frame2 = this.fireAnimation.getCurrentFrame();
      switch(this.state) {
      case 1:
         batch.draw(Resources.getSprite(frame << 3, 24), this.bounds.x, this.bounds.y, 32.0F, 32.0F);
         break;
      case 2:
         TextureRegion r = new TextureRegion(Resources.getSprite(frame << 3, 24));
         r.flip(true, false);
         batch.draw(r, this.bounds.x, this.bounds.y, 32.0F, 32.0F);
         break;
      case 3:
         batch.draw(Resources.getSprite((frame << 3) + 24, 24), this.bounds.x, this.bounds.y, 32.0F, 32.0F);
         break;
      case 4:
         batch.draw(Resources.getSprite((frame << 3) + 40, 24), this.bounds.x, this.bounds.y, 32.0F, 32.0F);
         break;
      case 5:
         if (frame2 == 0) {
            batch.draw(Resources.getSprite(56, 24), this.bounds.x, this.bounds.y, 32.0F, 32.0F);
         } else {
            batch.draw(Resources.getSprite(64, 24), this.bounds.x, this.bounds.y, 32.0F, 32.0F);
            batch.draw(Resources.getSprite(72, 24), this.bounds.x + 32.0F, this.bounds.y, 32.0F, 32.0F);
         }
         break;
      case 6:
         TextureRegion re;
         if (frame2 == 0) {
            re = new TextureRegion(Resources.getSprite(56, 24));
            re.flip(true, false);
            batch.draw(re, this.bounds.x, this.bounds.y, 32.0F, 32.0F);
         } else {
            re = new TextureRegion(Resources.getSprite(64, 24));
            TextureRegion r2 = new TextureRegion(Resources.getSprite(72, 24));
            re.flip(true, false);
            r2.flip(true, false);
            batch.draw(re, this.bounds.x, this.bounds.y, 32.0F, 32.0F);
            batch.draw(r2, this.bounds.x - 32.0F, this.bounds.y, 32.0F, 32.0F);
         }
         break;
      case 7:
         batch.draw(Resources.getSprite((frame2 << 3) + 80, 24), this.bounds.x, this.bounds.y, 32.0F, 32.0F);
         break;
      case 8:
         batch.draw(Resources.getSprite((frame2 << 3) + 96, 24), this.bounds.x, this.bounds.y, 32.0F, 32.0F);
         break;
      default:
         batch.draw(Resources.getSprite(16, 24), this.bounds.x, this.bounds.y, 32.0F, 32.0F);
      }

      batch.setColor(Color.WHITE);
   }
}
