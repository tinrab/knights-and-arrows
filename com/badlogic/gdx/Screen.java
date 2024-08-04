package com.badlogic.gdx;

public interface Screen {
   void render(float var1);

   void resize(int var1, int var2);

   void show();

   void hide();

   void pause();

   void resume();

   void dispose();
}
