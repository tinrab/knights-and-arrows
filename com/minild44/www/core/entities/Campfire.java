package com.minild44.www.core.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.minild44.www.Resources;
import com.minild44.www.util.Light;

public class Campfire extends Entity {
   public Campfire(float x, float y) {
      this.bounds.set(x, y, 32.0F, 32.0F);
   }

   public void onEnterLevel() {
      Light light = new Light(this.bounds.x + 16.0F, this.bounds.y + 16.0F, new Color(-5373816));
      light.mask = this.getID();
      light.flickerSize = 0.01F;
      light.flickerSpeed = 1.5F;
      this.level.add(light);
      this.level.add("fire", this.bounds.x + 16.0F, this.bounds.y + 16.0F);
   }

   public void update(float delta) {
   }

   public void render(SpriteBatch batch) {
      batch.draw(Resources.getSprite(8, 96), this.bounds.x, this.bounds.y, 32.0F, 32.0F);
   }
}
