package com.minild44.www.core.entities.units;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.minild44.www.Resources;
import com.minild44.www.core.entities.Entity;
import com.minild44.www.core.entities.Magic;
import com.minild44.www.core.entities.Unit;

public class Magician extends Unit {
   public Magician() {
      this.bounds.setSize(32.0F, 32.0F);
      this.speed = 2.0F;
      this.stats.maxHp = 100.0F;
      this.stats.hp = 100.0F;
      this.stats.attackRange = 512.0F;
      this.stats.attackRate = 1.0F;
   }

   public void fire(float sx, float sy, Vector2 target) {
      Resources.play(2, this.bounds.x, this.bounds.y, this.level);
      this.level.add((Entity)(new Magic(sx, sy, target, this)));
   }

   public void render(SpriteBatch batch) {
      batch.setColor(this.getLuminance());
      int frame = this.animation.getCurrentFrame();
      int frame2 = this.fireAnimation.getCurrentFrame();
      switch(this.state) {
      case 1:
         batch.draw(Resources.getSprite(frame << 3, 16), this.bounds.x, this.bounds.y, 32.0F, 32.0F);
         break;
      case 2:
         TextureRegion r = new TextureRegion(Resources.getSprite(frame << 3, 16));
         r.flip(true, false);
         batch.draw(r, this.bounds.x, this.bounds.y, 32.0F, 32.0F);
         break;
      case 3:
         batch.draw(Resources.getSprite((frame << 3) + 24, 16), this.bounds.x, this.bounds.y, 32.0F, 32.0F);
         break;
      case 4:
         batch.draw(Resources.getSprite((frame << 3) + 40, 16), this.bounds.x, this.bounds.y, 32.0F, 32.0F);
         break;
      case 5:
         if (frame2 == 0) {
            batch.draw(Resources.getSprite(8, 16), this.bounds.x, this.bounds.y, 32.0F, 32.0F);
         } else {
            batch.draw(Resources.getSprite(56, 16), this.bounds.x, this.bounds.y, 32.0F, 32.0F);
            batch.draw(Resources.getSprite(64, 16), this.bounds.x + 32.0F, this.bounds.y, 32.0F, 32.0F);
         }
         break;
      case 6:
         TextureRegion re;
         if (frame2 == 0) {
            re = new TextureRegion(Resources.getSprite(8, 16));
            re.flip(true, false);
            batch.draw(re, this.bounds.x, this.bounds.y, 32.0F, 32.0F);
         } else {
            re = new TextureRegion(Resources.getSprite(56, 16));
            TextureRegion r2 = new TextureRegion(Resources.getSprite(64, 16));
            re.flip(true, false);
            r2.flip(true, false);
            batch.draw(re, this.bounds.x, this.bounds.y, 32.0F, 32.0F);
            batch.draw(r2, this.bounds.x - 32.0F, this.bounds.y, 32.0F, 32.0F);
         }
         break;
      case 7:
         if (frame2 == 0) {
            batch.draw(Resources.getSprite(40, 16), this.bounds.x, this.bounds.y, 32.0F, 32.0F);
         } else {
            batch.draw(Resources.getSprite(80, 16), this.bounds.x, this.bounds.y, 32.0F, 32.0F);
         }
         break;
      case 8:
         if (frame2 == 0) {
            batch.draw(Resources.getSprite(16, 16), this.bounds.x, this.bounds.y, 32.0F, 32.0F);
         } else {
            batch.draw(Resources.getSprite(72, 16), this.bounds.x, this.bounds.y, 32.0F, 32.0F);
         }
         break;
      default:
         batch.draw(Resources.getSprite(16, 16), this.bounds.x, this.bounds.y, 32.0F, 32.0F);
      }

      batch.setColor(Color.WHITE);
   }
}
