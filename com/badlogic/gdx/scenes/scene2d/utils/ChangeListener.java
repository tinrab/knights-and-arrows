package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;

public abstract class ChangeListener implements EventListener {
   public boolean handle(Event event) {
      if (!(event instanceof ChangeListener.ChangeEvent)) {
         return false;
      } else {
         this.changed((ChangeListener.ChangeEvent)event, event.getTarget());
         return false;
      }
   }

   public abstract void changed(ChangeListener.ChangeEvent var1, Actor var2);

   public static class ChangeEvent extends Event {
   }
}
