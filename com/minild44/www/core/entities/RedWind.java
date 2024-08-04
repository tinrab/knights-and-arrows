package com.minild44.www.core.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.minild44.www.Resources;

public class RedWind extends Projectile {
   public RedWind(float sx, float sy, Vector2 target, Unit owner) {
      super(sx, sy, target, owner);
      this.speed = 400.0F;
      this.damage = 5.0F;
   }

   public void render(SpriteBatch batch) {
      batch.setColor(this.getLuminance());
      batch.draw(Resources.getSprite(32, 72), this.bounds.x, this.bounds.y, 0.0F, 0.0F, 32.0F, 32.0F, 1.0F, 1.0F, -this.angle - 135.0F);
      batch.setColor(Color.WHITE);
   }

   public void onHitLevel() {
      this.level.add("wind", this.bounds.x, this.bounds.y);
   }

   public void onHitUnit(Unit u) {
      u.hurt(this.damage);
      this.level.add("wind", this.bounds.x, this.bounds.y);
   }
}
