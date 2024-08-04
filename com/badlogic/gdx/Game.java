package com.badlogic.gdx;

public abstract class Game implements ApplicationListener {
   private Screen screen;

   public void dispose() {
      if (this.screen != null) {
         this.screen.hide();
      }

   }

   public void pause() {
      if (this.screen != null) {
         this.screen.pause();
      }

   }

   public void resume() {
      if (this.screen != null) {
         this.screen.resume();
      }

   }

   public void render() {
      if (this.screen != null) {
         this.screen.render(Gdx.graphics.getDeltaTime());
      }

   }

   public void resize(int width, int height) {
      if (this.screen != null) {
         this.screen.resize(width, height);
      }

   }

   public void setScreen(Screen screen) {
      if (this.screen != null) {
         this.screen.hide();
      }

      this.screen = screen;
      if (this.screen != null) {
         this.screen.show();
         this.screen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
      }

   }

   public Screen getScreen() {
      return this.screen;
   }
}
