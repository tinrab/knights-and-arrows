package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;

public abstract class FocusListener implements EventListener {
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$com$badlogic$gdx$scenes$scene2d$utils$FocusListener$FocusEvent$Type;

   public boolean handle(Event event) {
      if (!(event instanceof FocusListener.FocusEvent)) {
         return false;
      } else {
         FocusListener.FocusEvent focusEvent = (FocusListener.FocusEvent)event;
         switch($SWITCH_TABLE$com$badlogic$gdx$scenes$scene2d$utils$FocusListener$FocusEvent$Type()[focusEvent.getType().ordinal()]) {
         case 1:
            this.keyboardFocusChanged(focusEvent, event.getTarget(), focusEvent.isFocused());
            break;
         case 2:
            this.scrollFocusChanged(focusEvent, event.getTarget(), focusEvent.isFocused());
         }

         return false;
      }
   }

   public void keyboardFocusChanged(FocusListener.FocusEvent event, Actor actor, boolean focused) {
   }

   public void scrollFocusChanged(FocusListener.FocusEvent event, Actor actor, boolean focused) {
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$com$badlogic$gdx$scenes$scene2d$utils$FocusListener$FocusEvent$Type() {
      int[] var10000 = $SWITCH_TABLE$com$badlogic$gdx$scenes$scene2d$utils$FocusListener$FocusEvent$Type;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[FocusListener.FocusEvent.Type.values().length];

         try {
            var0[FocusListener.FocusEvent.Type.keyboard.ordinal()] = 1;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[FocusListener.FocusEvent.Type.scroll.ordinal()] = 2;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$com$badlogic$gdx$scenes$scene2d$utils$FocusListener$FocusEvent$Type = var0;
         return var0;
      }
   }

   public static class FocusEvent extends Event {
      private boolean focused;
      private FocusListener.FocusEvent.Type type;
      private Actor relatedActor;

      public void reset() {
         super.reset();
         this.relatedActor = null;
      }

      public boolean isFocused() {
         return this.focused;
      }

      public void setFocused(boolean focused) {
         this.focused = focused;
      }

      public FocusListener.FocusEvent.Type getType() {
         return this.type;
      }

      public void setType(FocusListener.FocusEvent.Type focusType) {
         this.type = focusType;
      }

      public Actor getRelatedActor() {
         return this.relatedActor;
      }

      public void setRelatedActor(Actor relatedActor) {
         this.relatedActor = relatedActor;
      }

      public static enum Type {
         keyboard,
         scroll;
      }
   }
}
