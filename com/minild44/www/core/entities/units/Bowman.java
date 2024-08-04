package com.minild44.www.core.entities.units;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.minild44.www.Resources;
import com.minild44.www.core.entities.Arrow;
import com.minild44.www.core.entities.Entity;
import com.minild44.www.core.entities.Unit;

public class Bowman extends Unit {
   public Bowman() {
      this.bounds.setSize(32.0F, 32.0F);
      this.speed = 2.0F;
      this.stats.maxHp = 100.0F;
      this.stats.hp = 100.0F;
      this.stats.attackRange = 512.0F;
      this.stats.attackRate = 1.0F;
   }

   public void fire(float sx, float sy, Vector2 target) {
      Resources.play(0, this.bounds.x, this.bounds.y, this.level);
      this.level.add((Entity)(new Arrow(sx, sy, target, this)));
   }

   public void render(SpriteBatch batch) {
      batch.setColor(this.getLuminance());
      int frame = this.animation.getCurrentFrame();
      int frame2 = this.fireAnimation.getCurrentFrame();
      switch(this.state) {
      case 1:
         batch.draw(Resources.getSprite(frame << 3, 0), this.bounds.x, this.bounds.y, 32.0F, 32.0F);
         break;
      case 2:
         TextureRegion r = new TextureRegion(Resources.getSprite(frame << 3, 0));
         r.flip(true, false);
         batch.draw(r, this.bounds.x, this.bounds.y, 32.0F, 32.0F);
         break;
      case 3:
         batch.draw(Resources.getSprite((frame << 3) + 24, 0), this.bounds.x, this.bounds.y, 32.0F, 32.0F);
         break;
      case 4:
         batch.draw(Resources.getSprite((frame << 3) + 40, 0), this.bounds.x, this.bounds.y, 32.0F, 32.0F);
         break;
      case 5:
         batch.draw(Resources.getSprite((frame2 << 3) + 56, 0), this.bounds.x, this.bounds.y, 32.0F, 32.0F);
         break;
      case 6:
         TextureRegion re = new TextureRegion(Resources.getSprite((frame2 << 3) + 56, 0));
         re.flip(true, false);
         batch.draw(re, this.bounds.x, this.bounds.y, 32.0F, 32.0F);
         break;
      case 7:
         batch.draw(Resources.getSprite((frame2 << 3) + 72, 0), this.bounds.x, this.bounds.y, 32.0F, 32.0F);
         break;
      case 8:
         batch.draw(Resources.getSprite((frame2 << 3) + 88, 0), this.bounds.x, this.bounds.y, 32.0F, 32.0F);
         break;
      default:
         batch.draw(Resources.getSprite(16, 0), this.bounds.x, this.bounds.y, 32.0F, 32.0F);
      }

      batch.setColor(Color.WHITE);
   }
}
