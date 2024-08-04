package com.badlogic.gdx;

public interface InputProcessor {
   boolean keyDown(int var1);

   boolean keyUp(int var1);

   boolean keyTyped(char var1);

   boolean touchDown(int var1, int var2, int var3, int var4);

   boolean touchUp(int var1, int var2, int var3, int var4);

   boolean touchDragged(int var1, int var2, int var3);

   boolean mouseMoved(int var1, int var2);

   boolean scrolled(int var1);
}
