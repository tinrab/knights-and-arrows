package com.minild44.www.core.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.minild44.www.Resources;

public class Hit extends Projectile {
   public Hit(float x, float y, Vector2 target, Unit owner) {
      super(x, y, target, owner);
      this.speed = 600.0F;
      this.damage = 15.0F;
   }

   public void render(SpriteBatch batch) {
      batch.draw(Resources.getSprite(8, 72), this.bounds.x, this.bounds.y, 0.0F, 0.0F, 32.0F, 32.0F, 1.0F, 1.0F, -this.angle - 135.0F);
   }

   public void onHitLevel() {
      Resources.play(4, this.bounds.x, this.bounds.y, this.level);
      this.level.add("arrowhit", this.bounds.x, this.bounds.y);
   }

   public void onHitUnit(Unit u) {
      Resources.play(4, this.bounds.x, this.bounds.y, this.level);
      u.hurt(this.damage);
      this.level.add("arrowhit", this.bounds.x, this.bounds.y);
   }
}
