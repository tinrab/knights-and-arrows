package com.badlogic.gdx.scenes.scene2d;

import com.badlogic.gdx.math.Vector2;

public class InputListener implements EventListener {
   private static final Vector2 tmpCoords = new Vector2();
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$com$badlogic$gdx$scenes$scene2d$InputEvent$Type;

   public boolean handle(Event e) {
      if (!(e instanceof InputEvent)) {
         return false;
      } else {
         InputEvent event = (InputEvent)e;
         switch($SWITCH_TABLE$com$badlogic$gdx$scenes$scene2d$InputEvent$Type()[event.getType().ordinal()]) {
         case 8:
            return this.keyDown(event, event.getKeyCode());
         case 9:
            return this.keyUp(event, event.getKeyCode());
         case 10:
            return this.keyTyped(event, event.getCharacter());
         default:
            event.toCoordinates(event.getListenerActor(), tmpCoords);
            switch($SWITCH_TABLE$com$badlogic$gdx$scenes$scene2d$InputEvent$Type()[event.getType().ordinal()]) {
            case 1:
               return this.touchDown(event, tmpCoords.x, tmpCoords.y, event.getPointer(), event.getButton());
            case 2:
               this.touchUp(event, tmpCoords.x, tmpCoords.y, event.getPointer(), event.getButton());
               return true;
            case 3:
               this.touchDragged(event, tmpCoords.x, tmpCoords.y, event.getPointer());
               return true;
            case 4:
               return this.mouseMoved(event, tmpCoords.x, tmpCoords.y);
            case 5:
               this.enter(event, tmpCoords.x, tmpCoords.y, event.getPointer(), event.getRelatedActor());
               return false;
            case 6:
               this.exit(event, tmpCoords.x, tmpCoords.y, event.getPointer(), event.getRelatedActor());
               return false;
            case 7:
               return this.scrolled(event, tmpCoords.x, tmpCoords.y, event.getScrollAmount());
            default:
               return false;
            }
         }
      }
   }

   public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
      return false;
   }

   public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
   }

   public void touchDragged(InputEvent event, float x, float y, int pointer) {
   }

   public boolean mouseMoved(InputEvent event, float x, float y) {
      return false;
   }

   public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
   }

   public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
   }

   public boolean scrolled(InputEvent event, float x, float y, int amount) {
      return false;
   }

   public boolean keyDown(InputEvent event, int keycode) {
      return false;
   }

   public boolean keyUp(InputEvent event, int keycode) {
      return false;
   }

   public boolean keyTyped(InputEvent event, char character) {
      return false;
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$com$badlogic$gdx$scenes$scene2d$InputEvent$Type() {
      int[] var10000 = $SWITCH_TABLE$com$badlogic$gdx$scenes$scene2d$InputEvent$Type;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[InputEvent.Type.values().length];

         try {
            var0[InputEvent.Type.enter.ordinal()] = 5;
         } catch (NoSuchFieldError var10) {
         }

         try {
            var0[InputEvent.Type.exit.ordinal()] = 6;
         } catch (NoSuchFieldError var9) {
         }

         try {
            var0[InputEvent.Type.keyDown.ordinal()] = 8;
         } catch (NoSuchFieldError var8) {
         }

         try {
            var0[InputEvent.Type.keyTyped.ordinal()] = 10;
         } catch (NoSuchFieldError var7) {
         }

         try {
            var0[InputEvent.Type.keyUp.ordinal()] = 9;
         } catch (NoSuchFieldError var6) {
         }

         try {
            var0[InputEvent.Type.mouseMoved.ordinal()] = 4;
         } catch (NoSuchFieldError var5) {
         }

         try {
            var0[InputEvent.Type.scrolled.ordinal()] = 7;
         } catch (NoSuchFieldError var4) {
         }

         try {
            var0[InputEvent.Type.touchDown.ordinal()] = 1;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[InputEvent.Type.touchDragged.ordinal()] = 3;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[InputEvent.Type.touchUp.ordinal()] = 2;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$com$badlogic$gdx$scenes$scene2d$InputEvent$Type = var0;
         return var0;
      }
   }
}
