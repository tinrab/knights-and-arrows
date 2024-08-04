package com.minild44.www.core.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.minild44.www.Resources;
import com.minild44.www.util.Light;

public class Magic extends Projectile {
   private Light light;

   public Magic(float sx, float sy, Vector2 target, Unit owner) {
      super(sx, sy, target, owner);
      this.speed = 500.0F;
      this.light = new Light(sx, sy, new Color(-549257421));
      this.light.mask = this.getID();
      this.light.fraction = 0.5F;
      this.light.castShadows = false;
      this.damage = 10.0F;
   }

   public void onEnterLevel() {
      this.level.add(this.light);
   }

   public void update(float delta) {
      super.update(delta);
      this.light.set(this.bounds.x, this.bounds.y);
   }

   public void render(SpriteBatch batch) {
      batch.draw(Resources.getSprite(this.owner.getSquad().isEnemy() ? 40 : 16, 72), this.bounds.x, this.bounds.y, 0.0F, 0.0F, 32.0F, 32.0F, 1.0F, 1.0F, -this.angle - 135.0F);
   }

   public void onHitLevel() {
      Resources.play(8, this.bounds.x, this.bounds.y, this.level);
      this.light.remove();
      this.level.add("magichit", this.bounds.x, this.bounds.y);
   }

   public void onHitUnit(Unit u) {
      Resources.play(8, this.bounds.x, this.bounds.y, this.level);
      u.hurt(this.damage);
      this.light.remove();
      this.level.add("magichit", this.bounds.x, this.bounds.y);
   }
}
