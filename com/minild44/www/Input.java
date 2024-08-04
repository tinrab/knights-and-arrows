package com.minild44.www;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.minild44.www.util.Camera;

public class Input implements InputProcessor {
   private static boolean[] keys = new boolean[256];
   private static boolean[] pressedKeys = new boolean[256];
   private static boolean[] buttons = new boolean[3];
   private static boolean[] pressedButtons = new boolean[3];
   private static byte[] buttonsUp = new byte[3];
   private static int mx;
   private static int my;
   private static int mpx;
   private static int mpy;
   private static int mdx;
   private static int mdy;
   private static int mw;

   public static int getMouseDX() {
      return mdx;
   }

   public static int getMouseDY() {
      return mdy;
   }

   public static boolean isMouseDragging() {
      return mdx != 0 && mdy != 0;
   }

   public static boolean hasMouseMoved() {
      return mdx != 0 || mdy != 0;
   }

   public static boolean isKeyDown(int keycode) {
      return keys[keycode];
   }

   public static boolean isKeyPressed(int keycode) {
      if (pressedKeys[keycode]) {
         pressedKeys[keycode] = false;
         return true;
      } else {
         return false;
      }
   }

   public static boolean isButtonDown(int button) {
      return buttons[button];
   }

   public static boolean isButtonPressed(int button) {
      if (pressedButtons[button]) {
         pressedButtons[button] = false;
         return true;
      } else {
         return false;
      }
   }

   public static boolean isButtonPressedAndReleased(int button) {
      if (buttonsUp[button] == 2) {
         buttonsUp[button] = 0;
         return true;
      } else {
         return false;
      }
   }

   public static int getMouseX() {
      return mx;
   }

   public static int getMouseY() {
      return Gdx.graphics.getHeight() - my - 1;
   }

   public static Vector2 getMousePosition(boolean yDown) {
      return new Vector2((float)mx, (float)(yDown ? getMouseY() : my));
   }

   public static int getMouseWheel() {
      int n = mw;
      mw = 0;
      return n;
   }

   public boolean keyDown(int keycode) {
      keys[keycode] = true;
      pressedKeys[keycode] = true;
      return false;
   }

   public boolean keyUp(int keycode) {
      keys[keycode] = false;
      pressedKeys[keycode] = false;
      return false;
   }

   public static void update() {
      mdx = mpx - getMouseX();
      mdy = mpy - getMouseY();
      mpx = getMouseX();
      mpy = getMouseY();
   }

   public boolean keyTyped(char character) {
      return false;
   }

   public boolean touchDown(int screenX, int screenY, int pointer, int button) {
      mx = screenX;
      my = screenY;
      buttons[button] = true;
      pressedButtons[button] = true;
      buttonsUp[button] = 1;
      return false;
   }

   public boolean touchUp(int screenX, int screenY, int pointer, int button) {
      mx = screenX;
      my = screenY;
      buttons[button] = false;
      pressedButtons[button] = false;
      if (buttonsUp[button] == 1) {
         buttonsUp[button] = 2;
      }

      return false;
   }

   public boolean touchDragged(int screenX, int screenY, int pointer) {
      mx = screenX;
      my = screenY;
      buttonsUp[0] = 0;
      buttonsUp[1] = 0;
      buttonsUp[2] = 0;
      return false;
   }

   public boolean mouseMoved(int screenX, int screenY) {
      mx = screenX;
      my = screenY;
      buttonsUp[0] = 0;
      buttonsUp[1] = 0;
      buttonsUp[2] = 0;
      return false;
   }

   public boolean scrolled(int amount) {
      mw = amount;
      buttonsUp[0] = 0;
      buttonsUp[1] = 0;
      buttonsUp[2] = 0;
      return false;
   }

   public static Vector2 unprojectMouse(Camera camera) {
      return camera.unproject(getMousePosition(false));
   }

   public static void restUps() {
      buttonsUp[0] = 0;
      buttonsUp[1] = 0;
      buttonsUp[2] = 0;
   }
}
