package com.minild44.www.core.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.minild44.www.Resources;

public class Potato extends Entity {
   public Potato(float x, float y) {
      this.bounds.set(x, y, 32.0F, 32.0F);
   }

   public void update(float delta) {
   }

   public void render(SpriteBatch batch) {
      batch.setColor(this.getLuminance());
      batch.draw(Resources.getSprite(0, 104), this.bounds.x, this.bounds.y, 32.0F, 32.0F);
      batch.setColor(Color.WHITE);
   }
}
