package com.minild44.www;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SplashScreen implements Screen {
   private SpriteBatch batch = new SpriteBatch();
   private byte state = 0;
   private float countdown = 2.0F;

   public void render(float delta) {
      this.batch.begin();
      Resources.FONT.setScale(3.0F);
      BitmapFont.TextBounds tb = Resources.FONT.getBounds("Loading...");
      Resources.FONT.draw(this.batch, "Loading...", (float)(Gdx.graphics.getWidth() / 2) - tb.width / 2.0F, (float)(Gdx.graphics.getHeight() / 2) + tb.height);
      Resources.FONT.setScale(1.0F);
      this.batch.end();
      this.countdown -= delta;
      if (this.state == 1) {
         ++this.state;
         Resources.load();
      }

      if (this.countdown <= 0.0F) {
         Main.INSTANCE.setScreen(new MenuScreen());
      }

      ++this.state;
   }

   public void resize(int width, int height) {
   }

   public void show() {
   }

   public void hide() {
      this.dispose();
   }

   public void pause() {
   }

   public void resume() {
   }

   public void dispose() {
      this.batch.dispose();
   }
}
