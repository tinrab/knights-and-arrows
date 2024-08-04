package com.minild44.www.core.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.minild44.www.Resources;
import com.minild44.www.util.Light;

public class Torch extends Entity {
   private static Color color = new Color(1.0F, 0.8F, 0.2F, 0.5F);

   public Torch(float x, float y) {
      this.bounds.set(x, y, 32.0F, 32.0F);
      this.layer = 1;
   }

   public void onEnterLevel() {
      Color col = Color.random();
      col.a = 0.6F;
      Light light = new Light(this.bounds.x + 12.0F, this.bounds.y + 16.0F, color);
      light.flickerSize = 0.01F;
      light.flickerSpeed = 1.2F;
      light.mask = this.getID();
      this.level.add(light);
      this.level.add("torchfire", this.bounds.x + 13.5F, this.bounds.y + 14.0F);
   }

   public void update(float delta) {
   }

   public void render(SpriteBatch batch) {
      batch.draw(Resources.getSprite(0, 96), this.bounds.x, this.bounds.y, 32.0F, 32.0F);
   }
}
