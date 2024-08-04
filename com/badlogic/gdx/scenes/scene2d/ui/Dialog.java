package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.ObjectMap;

public class Dialog extends Window {
   public static float fadeDuration = 0.4F;
   Table contentTable;
   Table buttonTable;
   private Skin skin;
   ObjectMap<Actor, Object> values = new ObjectMap();
   boolean cancelHide;
   Actor previousKeyboardFocus;
   Actor previousScrollFocus;
   InputListener ignoreTouchDown = new InputListener() {
      public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
         event.cancel();
         return false;
      }
   };

   public Dialog(String title, Skin skin) {
      super(title, (Window.WindowStyle)skin.get(Window.WindowStyle.class));
      this.skin = skin;
      this.initialize();
   }

   public Dialog(String title, Skin skin, String windowStyleName) {
      super(title, (Window.WindowStyle)skin.get(windowStyleName, Window.WindowStyle.class));
      this.skin = skin;
      this.initialize();
   }

   public Dialog(String title, Window.WindowStyle windowStyle) {
      super(title, windowStyle);
      this.initialize();
   }

   private void initialize() {
      this.setModal(true);
      this.defaults().space(6.0F);
      this.add(this.contentTable = new Table(this.skin)).expand().fill();
      this.row();
      this.add(this.buttonTable = new Table(this.skin));
      this.contentTable.defaults().space(6.0F);
      this.buttonTable.defaults().space(6.0F);
      this.buttonTable.addListener(new ChangeListener() {
         public void changed(ChangeListener.ChangeEvent event, Actor actor) {
            if (Dialog.this.values.containsKey(actor)) {
               while(((Actor)actor).getParent() != Dialog.this.buttonTable) {
                  actor = ((Actor)actor).getParent();
               }

               Dialog.this.result(Dialog.this.values.get(actor));
               if (!Dialog.this.cancelHide) {
                  Dialog.this.hide();
               }

               Dialog.this.cancelHide = false;
            }
         }
      });
      this.addListener(new FocusListener() {
         public void keyboardFocusChanged(FocusListener.FocusEvent event, Actor actor, boolean focused) {
            if (!focused) {
               this.focusChanged(event);
            }

         }

         public void scrollFocusChanged(FocusListener.FocusEvent event, Actor actor, boolean focused) {
            if (!focused) {
               this.focusChanged(event);
            }

         }

         private void focusChanged(FocusListener.FocusEvent event) {
            Stage stage = Dialog.this.getStage();
            if (Dialog.this.isModal && stage != null && stage.getRoot().getChildren().size > 0 && stage.getRoot().getChildren().peek() == Dialog.this) {
               Actor newFocusedActor = event.getRelatedActor();
               if (newFocusedActor == null || !newFocusedActor.isDescendantOf(Dialog.this)) {
                  event.cancel();
               }
            }

         }
      });
   }

   public Table getContentTable() {
      return this.contentTable;
   }

   public Table getButtonTable() {
      return this.buttonTable;
   }

   public Dialog text(String text) {
      if (this.skin == null) {
         throw new IllegalStateException("This method may only be used if the dialog was constructed with a Skin.");
      } else {
         return this.text(text, (Label.LabelStyle)this.skin.get(Label.LabelStyle.class));
      }
   }

   public Dialog text(String text, Label.LabelStyle labelStyle) {
      return this.text(new Label(text, labelStyle));
   }

   public Dialog text(Label label) {
      this.contentTable.add((Actor)label);
      return this;
   }

   public Dialog button(String text) {
      return this.button((String)text, (Object)null);
   }

   public Dialog button(String text, Object object) {
      if (this.skin == null) {
         throw new IllegalStateException("This method may only be used if the dialog was constructed with a Skin.");
      } else {
         return this.button(text, object, (TextButton.TextButtonStyle)this.skin.get(TextButton.TextButtonStyle.class));
      }
   }

   public Dialog button(String text, Object object, TextButton.TextButtonStyle buttonStyle) {
      return this.button((Button)(new TextButton(text, buttonStyle)), object);
   }

   public Dialog button(Button button) {
      return this.button((Button)button, (Object)null);
   }

   public Dialog button(Button button, Object object) {
      this.buttonTable.add((Actor)button);
      this.setObject(button, object);
      return this;
   }

   public Dialog show(Stage stage) {
      this.clearActions();
      this.removeCaptureListener(this.ignoreTouchDown);
      this.previousKeyboardFocus = stage.getKeyboardFocus();
      this.previousScrollFocus = stage.getScrollFocus();
      this.pack();
      this.setPosition((float)Math.round((stage.getWidth() - this.getWidth()) / 2.0F), (float)Math.round((stage.getHeight() - this.getHeight()) / 2.0F));
      stage.addActor(this);
      stage.setKeyboardFocus(this);
      stage.setScrollFocus(this);
      if (fadeDuration > 0.0F) {
         this.getColor().a = 0.0F;
         this.addAction(Actions.fadeIn(fadeDuration, Interpolation.fade));
      }

      return this;
   }

   public void hide() {
      if (fadeDuration > 0.0F) {
         this.addCaptureListener(this.ignoreTouchDown);
         this.addAction(Actions.sequence(Actions.fadeOut(fadeDuration, Interpolation.fade), Actions.removeListener(this.ignoreTouchDown, true), Actions.removeActor()));
      } else {
         this.remove();
      }

   }

   protected void setParent(Group parent) {
      super.setParent(parent);
      if (parent == null) {
         Stage stage = this.getStage();
         if (stage != null) {
            Actor actor = stage.getKeyboardFocus();
            if (actor == this || actor == null) {
               stage.setKeyboardFocus(this.previousKeyboardFocus);
            }

            actor = stage.getScrollFocus();
            if (actor == this || actor == null) {
               stage.setScrollFocus(this.previousScrollFocus);
            }
         }
      }

   }

   public void setObject(Actor actor, Object object) {
      this.values.put(actor, object);
   }

   public Dialog key(final int keycode, final Object object) {
      this.addListener(new InputListener() {
         public boolean keyDown(InputEvent event, int keycode2) {
            if (keycode == keycode2) {
               Dialog.this.result(object);
               if (!Dialog.this.cancelHide) {
                  Dialog.this.hide();
               }

               Dialog.this.cancelHide = false;
            }

            return false;
         }
      });
      return this;
   }

   protected void result(Object object) {
   }

   public void cancel() {
      this.cancelHide = true;
   }
}
