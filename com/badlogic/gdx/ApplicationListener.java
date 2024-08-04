package com.badlogic.gdx;

public interface ApplicationListener {
   void create();

   void resize(int var1, int var2);

   void render();

   void pause();

   void resume();

   void dispose();
}
