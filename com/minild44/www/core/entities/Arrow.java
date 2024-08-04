package com.minild44.www.core.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.minild44.www.Resources;

public class Arrow extends Projectile {
   public Arrow(float x, float y, Vector2 target, Unit owner) {
      super(x, y, target, owner);
      this.speed = 500.0F;
      this.damage = 10.0F;
   }

   public void render(SpriteBatch batch) {
      batch.setColor(this.getLuminance());
      batch.draw(Resources.getSprite(0, 72), this.bounds.x, this.bounds.y, 0.0F, 0.0F, 32.0F, 32.0F, 1.0F, 1.0F, -this.angle - 135.0F);
      batch.setColor(Color.WHITE);
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
