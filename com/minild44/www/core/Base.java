package com.minild44.www.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.minild44.www.Resources;
import com.minild44.www.core.entities.Entity;
import com.minild44.www.util.Camera;

public class Base extends Entity {
   private boolean enemy;
   private float hp = 15000.0F;

   public Base(float x, float y, boolean enemy) {
      this.bounds = new Rectangle(x, y, 32.0F, 32.0F);
      this.enemy = enemy;
   }

   public void update(float delta) {
      if (this.hp <= 0.0F) {
         this.level.end(this.enemy);
      }

   }

   public void render(SpriteBatch batch) {
      batch.setColor(this.getLuminance());
      batch.draw(Resources.getSprite(16, 96), this.bounds.x, this.bounds.y, 32.0F, 32.0F);
      batch.setColor(Color.WHITE);
   }

   public void renderView(Matrix4 view, Camera camera, SpriteBatch batch) {
      if (this.hp > 0.0F) {
         Gdx.gl.glEnable(3042);
         Gdx.gl.glBlendFunc(770, 771);
         Vector2 pos = camera.project(this.bounds.getCenter());
         this.level.sr.begin(ShapeRenderer.ShapeType.Filled);
         this.level.sr.setColor(Color.DARK_GREEN);
         float w = 48.0F;
         float dw = w * this.hp / 15000.0F;
         float dy = -32.0F;
         this.level.sr.rect(pos.x - w / 2.0F, pos.y + dy, dw, 8.0F);
         this.level.sr.setColor(Color.DARK_RED);
         this.level.sr.rect(pos.x - w / 2.0F + dw, pos.y + dy, w - dw, 8.0F);
         this.level.sr.end();
         Gdx.gl.glDisable(3042);
         if (!this.enemy) {
            Vector2 p = camera.project(new Vector2(this.bounds.x, this.bounds.y));
            batch.begin();
            batch.draw(Resources.GUI, p.x + 8.0F, p.y + 48.0F, 16.0F, 16.0F, 80, 112, 16, 16, false, false);
            batch.end();
         }
      }

   }

   public void hurt(float dmg) {
      this.hp -= dmg;
   }

   public boolean isEnemy() {
      return this.enemy;
   }
}
