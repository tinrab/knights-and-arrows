package com.badlogic.gdx.scenes.scene2d;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.SnapshotArray;

public class Stage extends InputAdapter implements Disposable {
   private static final Vector2 actorCoords = new Vector2();
   private static final Vector3 cameraCoords = new Vector3();
   private float viewportX;
   private float viewportY;
   private float viewportWidth;
   private float viewportHeight;
   private float width;
   private float height;
   private float gutterWidth;
   private float gutterHeight;
   private float centerX;
   private float centerY;
   private Camera camera;
   private final SpriteBatch batch;
   private final boolean ownsBatch;
   private Group root;
   private final Vector2 stageCoords;
   private Actor[] pointerOverActors;
   private boolean[] pointerTouched;
   private int[] pointerScreenX;
   private int[] pointerScreenY;
   private int mouseScreenX;
   private int mouseScreenY;
   private Actor mouseOverActor;
   private Actor keyboardFocus;
   private Actor scrollFocus;
   private SnapshotArray<Stage.TouchFocus> touchFocuses;

   public Stage() {
      this((float)Gdx.graphics.getWidth(), (float)Gdx.graphics.getHeight(), false);
   }

   public Stage(float width, float height, boolean keepAspectRatio) {
      this.stageCoords = new Vector2();
      this.pointerOverActors = new Actor[20];
      this.pointerTouched = new boolean[20];
      this.pointerScreenX = new int[20];
      this.pointerScreenY = new int[20];
      this.touchFocuses = new SnapshotArray(true, 4, Stage.TouchFocus.class);
      this.batch = new SpriteBatch();
      this.ownsBatch = true;
      this.initialize(width, height, keepAspectRatio);
   }

   public Stage(float width, float height, boolean keepAspectRatio, SpriteBatch batch) {
      this.stageCoords = new Vector2();
      this.pointerOverActors = new Actor[20];
      this.pointerTouched = new boolean[20];
      this.pointerScreenX = new int[20];
      this.pointerScreenY = new int[20];
      this.touchFocuses = new SnapshotArray(true, 4, Stage.TouchFocus.class);
      this.batch = batch;
      this.ownsBatch = false;
      this.initialize(width, height, keepAspectRatio);
   }

   private void initialize(float width, float height, boolean keepAspectRatio) {
      this.width = width;
      this.height = height;
      this.root = new Group();
      this.root.setStage(this);
      this.camera = new OrthographicCamera();
      this.setViewport(width, height, keepAspectRatio);
   }

   public void setViewport(float width, float height, boolean keepAspectRatio) {
      this.setViewport(width, height, keepAspectRatio, 0.0F, 0.0F, (float)Gdx.graphics.getWidth(), (float)Gdx.graphics.getHeight());
   }

   public void setViewport(float stageWidth, float stageHeight, boolean keepAspectRatio, float viewportX, float viewportY, float viewportWidth, float viewportHeight) {
      this.viewportX = viewportX;
      this.viewportY = viewportY;
      this.viewportWidth = viewportWidth;
      this.viewportHeight = viewportHeight;
      if (keepAspectRatio) {
         float toViewportSpace;
         float toStageSpace;
         float deviceWidth;
         float lengthen;
         if (viewportHeight / viewportWidth < stageHeight / stageWidth) {
            toViewportSpace = viewportHeight / stageHeight;
            toStageSpace = stageHeight / viewportHeight;
            deviceWidth = stageWidth * toViewportSpace;
            lengthen = (viewportWidth - deviceWidth) * toStageSpace;
            this.width = stageWidth + lengthen;
            this.height = stageHeight;
            this.gutterWidth = lengthen / 2.0F;
            this.gutterHeight = 0.0F;
         } else {
            toViewportSpace = viewportWidth / stageWidth;
            toStageSpace = stageWidth / viewportWidth;
            deviceWidth = stageHeight * toViewportSpace;
            lengthen = (viewportHeight - deviceWidth) * toStageSpace;
            this.height = stageHeight + lengthen;
            this.width = stageWidth;
            this.gutterWidth = 0.0F;
            this.gutterHeight = lengthen / 2.0F;
         }
      } else {
         this.width = stageWidth;
         this.height = stageHeight;
         this.gutterWidth = 0.0F;
         this.gutterHeight = 0.0F;
      }

      this.centerX = this.width / 2.0F;
      this.centerY = this.height / 2.0F;
      this.camera.position.set(this.centerX, this.centerY, 0.0F);
      this.camera.viewportWidth = this.width;
      this.camera.viewportHeight = this.height;
   }

   public void draw() {
      this.camera.update();
      if (this.root.isVisible()) {
         this.batch.setProjectionMatrix(this.camera.combined);
         this.batch.begin();
         this.root.draw(this.batch, 1.0F);
         this.batch.end();
      }
   }

   public void act() {
      this.act(Math.min(Gdx.graphics.getDeltaTime(), 0.033333335F));
   }

   public void act(float delta) {
      int pointer = 0;

      for(int n = this.pointerOverActors.length; pointer < n; ++pointer) {
         Actor overLast = this.pointerOverActors[pointer];
         if (!this.pointerTouched[pointer]) {
            if (overLast != null) {
               this.pointerOverActors[pointer] = null;
               this.screenToStageCoordinates(this.stageCoords.set((float)this.pointerScreenX[pointer], (float)this.pointerScreenY[pointer]));
               InputEvent event = (InputEvent)Pools.obtain(InputEvent.class);
               event.setType(InputEvent.Type.exit);
               event.setStage(this);
               event.setStageX(this.stageCoords.x);
               event.setStageY(this.stageCoords.y);
               event.setRelatedActor(overLast);
               event.setPointer(pointer);
               overLast.fire(event);
               Pools.free(event);
            }
         } else {
            this.pointerOverActors[pointer] = this.fireEnterAndExit(overLast, this.pointerScreenX[pointer], this.pointerScreenY[pointer], pointer);
         }
      }

      Application.ApplicationType type = Gdx.app.getType();
      if (type == Application.ApplicationType.Desktop || type == Application.ApplicationType.Applet || type == Application.ApplicationType.WebGL) {
         this.mouseOverActor = this.fireEnterAndExit(this.mouseOverActor, this.mouseScreenX, this.mouseScreenY, -1);
      }

      this.root.act(delta);
   }

   private Actor fireEnterAndExit(Actor overLast, int screenX, int screenY, int pointer) {
      this.screenToStageCoordinates(this.stageCoords.set((float)screenX, (float)screenY));
      Actor over = this.hit(this.stageCoords.x, this.stageCoords.y, true);
      if (over == overLast) {
         return overLast;
      } else {
         InputEvent event = (InputEvent)Pools.obtain(InputEvent.class);
         event.setStage(this);
         event.setStageX(this.stageCoords.x);
         event.setStageY(this.stageCoords.y);
         event.setPointer(pointer);
         if (overLast != null) {
            event.setType(InputEvent.Type.exit);
            event.setRelatedActor(over);
            overLast.fire(event);
         }

         if (over != null) {
            event.setType(InputEvent.Type.enter);
            event.setRelatedActor(overLast);
            over.fire(event);
         }

         Pools.free(event);
         return over;
      }
   }

   public boolean touchDown(int screenX, int screenY, int pointer, int button) {
      this.pointerTouched[pointer] = true;
      this.pointerScreenX[pointer] = screenX;
      this.pointerScreenY[pointer] = screenY;
      this.screenToStageCoordinates(this.stageCoords.set((float)screenX, (float)screenY));
      InputEvent event = (InputEvent)Pools.obtain(InputEvent.class);
      event.setType(InputEvent.Type.touchDown);
      event.setStage(this);
      event.setStageX(this.stageCoords.x);
      event.setStageY(this.stageCoords.y);
      event.setPointer(pointer);
      event.setButton(button);
      Actor target = this.hit(this.stageCoords.x, this.stageCoords.y, true);
      if (target == null) {
         target = this.root;
      }

      ((Actor)target).fire(event);
      boolean handled = event.isHandled();
      Pools.free(event);
      return handled;
   }

   public boolean touchDragged(int screenX, int screenY, int pointer) {
      this.pointerScreenX[pointer] = screenX;
      this.pointerScreenY[pointer] = screenY;
      if (this.touchFocuses.size == 0) {
         return false;
      } else {
         this.screenToStageCoordinates(this.stageCoords.set((float)screenX, (float)screenY));
         InputEvent event = (InputEvent)Pools.obtain(InputEvent.class);
         event.setType(InputEvent.Type.touchDragged);
         event.setStage(this);
         event.setStageX(this.stageCoords.x);
         event.setStageY(this.stageCoords.y);
         event.setPointer(pointer);
         SnapshotArray<Stage.TouchFocus> touchFocuses = this.touchFocuses;
         Stage.TouchFocus[] focuses = (Stage.TouchFocus[])touchFocuses.begin();
         int i = 0;

         for(int n = touchFocuses.size; i < n; ++i) {
            Stage.TouchFocus focus = focuses[i];
            if (focus.pointer == pointer) {
               event.setTarget(focus.target);
               event.setListenerActor(focus.listenerActor);
               if (focus.listener.handle(event)) {
                  event.handle();
               }
            }
         }

         touchFocuses.end();
         boolean handled = event.isHandled();
         Pools.free(event);
         return handled;
      }
   }

   public boolean touchUp(int screenX, int screenY, int pointer, int button) {
      this.pointerTouched[pointer] = false;
      this.pointerScreenX[pointer] = screenX;
      this.pointerScreenY[pointer] = screenY;
      if (this.touchFocuses.size == 0) {
         return false;
      } else {
         this.screenToStageCoordinates(this.stageCoords.set((float)screenX, (float)screenY));
         InputEvent event = (InputEvent)Pools.obtain(InputEvent.class);
         event.setType(InputEvent.Type.touchUp);
         event.setStage(this);
         event.setStageX(this.stageCoords.x);
         event.setStageY(this.stageCoords.y);
         event.setPointer(pointer);
         event.setButton(button);
         SnapshotArray<Stage.TouchFocus> touchFocuses = this.touchFocuses;
         Stage.TouchFocus[] focuses = (Stage.TouchFocus[])touchFocuses.begin();
         int i = 0;

         for(int n = touchFocuses.size; i < n; ++i) {
            Stage.TouchFocus focus = focuses[i];
            if (focus.pointer == pointer && focus.button == button && touchFocuses.removeValue(focus, true)) {
               event.setTarget(focus.target);
               event.setListenerActor(focus.listenerActor);
               if (focus.listener.handle(event)) {
                  event.handle();
               }

               Pools.free(focus);
            }
         }

         touchFocuses.end();
         boolean handled = event.isHandled();
         Pools.free(event);
         return handled;
      }
   }

   public boolean mouseMoved(int screenX, int screenY) {
      this.mouseScreenX = screenX;
      this.mouseScreenY = screenY;
      this.screenToStageCoordinates(this.stageCoords.set((float)screenX, (float)screenY));
      InputEvent event = (InputEvent)Pools.obtain(InputEvent.class);
      event.setStage(this);
      event.setType(InputEvent.Type.mouseMoved);
      event.setStageX(this.stageCoords.x);
      event.setStageY(this.stageCoords.y);
      Actor target = this.hit(this.stageCoords.x, this.stageCoords.y, true);
      if (target == null) {
         target = this.root;
      }

      ((Actor)target).fire(event);
      boolean handled = event.isHandled();
      Pools.free(event);
      return handled;
   }

   public boolean scrolled(int amount) {
      Actor target = this.scrollFocus == null ? this.root : this.scrollFocus;
      this.screenToStageCoordinates(this.stageCoords.set((float)this.mouseScreenX, (float)this.mouseScreenY));
      InputEvent event = (InputEvent)Pools.obtain(InputEvent.class);
      event.setStage(this);
      event.setType(InputEvent.Type.scrolled);
      event.setScrollAmount(amount);
      event.setStageX(this.stageCoords.x);
      event.setStageY(this.stageCoords.y);
      ((Actor)target).fire(event);
      boolean handled = event.isHandled();
      Pools.free(event);
      return handled;
   }

   public boolean keyDown(int keyCode) {
      Actor target = this.keyboardFocus == null ? this.root : this.keyboardFocus;
      InputEvent event = (InputEvent)Pools.obtain(InputEvent.class);
      event.setStage(this);
      event.setType(InputEvent.Type.keyDown);
      event.setKeyCode(keyCode);
      ((Actor)target).fire(event);
      boolean handled = event.isHandled();
      Pools.free(event);
      return handled;
   }

   public boolean keyUp(int keyCode) {
      Actor target = this.keyboardFocus == null ? this.root : this.keyboardFocus;
      InputEvent event = (InputEvent)Pools.obtain(InputEvent.class);
      event.setStage(this);
      event.setType(InputEvent.Type.keyUp);
      event.setKeyCode(keyCode);
      ((Actor)target).fire(event);
      boolean handled = event.isHandled();
      Pools.free(event);
      return handled;
   }

   public boolean keyTyped(char character) {
      Actor target = this.keyboardFocus == null ? this.root : this.keyboardFocus;
      InputEvent event = (InputEvent)Pools.obtain(InputEvent.class);
      event.setStage(this);
      event.setType(InputEvent.Type.keyTyped);
      event.setCharacter(character);
      ((Actor)target).fire(event);
      boolean handled = event.isHandled();
      Pools.free(event);
      return handled;
   }

   public void addTouchFocus(EventListener listener, Actor listenerActor, Actor target, int pointer, int button) {
      Stage.TouchFocus focus = (Stage.TouchFocus)Pools.obtain(Stage.TouchFocus.class);
      focus.listenerActor = listenerActor;
      focus.target = target;
      focus.listener = listener;
      focus.pointer = pointer;
      focus.button = button;
      this.touchFocuses.add(focus);
   }

   public void removeTouchFocus(EventListener listener, Actor listenerActor, Actor target, int pointer, int button) {
      SnapshotArray<Stage.TouchFocus> touchFocuses = this.touchFocuses;

      for(int i = touchFocuses.size - 1; i >= 0; --i) {
         Stage.TouchFocus focus = (Stage.TouchFocus)touchFocuses.get(i);
         if (focus.listener == listener && focus.listenerActor == listenerActor && focus.target == target && focus.pointer == pointer && focus.button == button) {
            touchFocuses.removeIndex(i);
            Pools.free(focus);
         }
      }

   }

   public void cancelTouchFocus() {
      this.cancelTouchFocus((EventListener)null, (Actor)null);
   }

   public void cancelTouchFocus(EventListener listener, Actor actor) {
      InputEvent event = (InputEvent)Pools.obtain(InputEvent.class);
      event.setStage(this);
      event.setType(InputEvent.Type.touchUp);
      event.setStageX(-2.14748365E9F);
      event.setStageY(-2.14748365E9F);
      SnapshotArray<Stage.TouchFocus> touchFocuses = this.touchFocuses;
      Stage.TouchFocus[] items = (Stage.TouchFocus[])touchFocuses.begin();
      int i = 0;

      for(int n = touchFocuses.size; i < n; ++i) {
         Stage.TouchFocus focus = items[i];
         if ((focus.listener != listener || focus.listenerActor != actor) && touchFocuses.removeValue(focus, true)) {
            event.setTarget(focus.target);
            event.setListenerActor(focus.listenerActor);
            event.setPointer(focus.pointer);
            event.setButton(focus.button);
            focus.listener.handle(event);
         }
      }

      touchFocuses.end();
      Pools.free(event);
   }

   public void addActor(Actor actor) {
      this.root.addActor(actor);
   }

   public void addAction(Action action) {
      this.root.addAction(action);
   }

   public Array<Actor> getActors() {
      return this.root.getChildren();
   }

   public boolean addListener(EventListener listener) {
      return this.root.addListener(listener);
   }

   public boolean removeListener(EventListener listener) {
      return this.root.removeListener(listener);
   }

   public boolean addCaptureListener(EventListener listener) {
      return this.root.addCaptureListener(listener);
   }

   public boolean removeCaptureListener(EventListener listener) {
      return this.root.removeCaptureListener(listener);
   }

   public void clear() {
      this.unfocusAll();
      this.root.clear();
   }

   public void unfocusAll() {
      this.scrollFocus = null;
      this.keyboardFocus = null;
      this.cancelTouchFocus();
   }

   public void unfocus(Actor actor) {
      if (this.scrollFocus != null && this.scrollFocus.isDescendantOf(actor)) {
         this.scrollFocus = null;
      }

      if (this.keyboardFocus != null && this.keyboardFocus.isDescendantOf(actor)) {
         this.keyboardFocus = null;
      }

   }

   public void setKeyboardFocus(Actor actor) {
      if (this.keyboardFocus != actor) {
         FocusListener.FocusEvent event = (FocusListener.FocusEvent)Pools.obtain(FocusListener.FocusEvent.class);
         event.setStage(this);
         event.setType(FocusListener.FocusEvent.Type.keyboard);
         Actor oldKeyboardFocus = this.keyboardFocus;
         if (oldKeyboardFocus != null) {
            event.setFocused(false);
            event.setRelatedActor(actor);
            oldKeyboardFocus.fire(event);
         }

         if (!event.isCancelled()) {
            this.keyboardFocus = actor;
            if (actor != null) {
               event.setFocused(true);
               event.setRelatedActor(oldKeyboardFocus);
               actor.fire(event);
               if (event.isCancelled()) {
                  this.setKeyboardFocus(oldKeyboardFocus);
               }
            }
         }

         Pools.free(event);
      }
   }

   public Actor getKeyboardFocus() {
      return this.keyboardFocus;
   }

   public void setScrollFocus(Actor actor) {
      if (this.scrollFocus != actor) {
         FocusListener.FocusEvent event = (FocusListener.FocusEvent)Pools.obtain(FocusListener.FocusEvent.class);
         event.setStage(this);
         event.setType(FocusListener.FocusEvent.Type.scroll);
         Actor oldScrollFocus = this.keyboardFocus;
         if (oldScrollFocus != null) {
            event.setFocused(false);
            event.setRelatedActor(actor);
            oldScrollFocus.fire(event);
         }

         if (!event.isCancelled()) {
            this.scrollFocus = actor;
            if (actor != null) {
               event.setFocused(true);
               event.setRelatedActor(oldScrollFocus);
               actor.fire(event);
               if (event.isCancelled()) {
                  this.setScrollFocus(oldScrollFocus);
               }
            }
         }

         Pools.free(event);
      }
   }

   public Actor getScrollFocus() {
      return this.scrollFocus;
   }

   public float getWidth() {
      return this.width;
   }

   public float getHeight() {
      return this.height;
   }

   public float getGutterWidth() {
      return this.gutterWidth;
   }

   public float getGutterHeight() {
      return this.gutterHeight;
   }

   public SpriteBatch getSpriteBatch() {
      return this.batch;
   }

   public Camera getCamera() {
      return this.camera;
   }

   public void setCamera(Camera camera) {
      this.camera = camera;
   }

   public Group getRoot() {
      return this.root;
   }

   public Actor hit(float stageX, float stageY, boolean touchable) {
      this.root.parentToLocalCoordinates(actorCoords.set(stageX, stageY));
      return this.root.hit(actorCoords.x, actorCoords.y, touchable);
   }

   public Vector2 screenToStageCoordinates(Vector2 screenCoords) {
      this.camera.unproject(cameraCoords.set(screenCoords.x, screenCoords.y, 0.0F), this.viewportX, this.viewportY, this.viewportWidth, this.viewportHeight);
      screenCoords.x = cameraCoords.x;
      screenCoords.y = cameraCoords.y;
      return screenCoords;
   }

   public Vector2 stageToScreenCoordinates(Vector2 stageCoords) {
      this.camera.project(cameraCoords.set(stageCoords.x, stageCoords.y, 0.0F), this.viewportX, this.viewportY, this.viewportWidth, this.viewportHeight);
      stageCoords.x = cameraCoords.x;
      stageCoords.y = this.viewportHeight - cameraCoords.y;
      return stageCoords;
   }

   public Vector2 toScreenCoordinates(Vector2 coords, Matrix4 transformMatrix) {
      ScissorStack.toWindowCoordinates(this.camera, transformMatrix, coords);
      return coords;
   }

   public void dispose() {
      this.clear();
      if (this.ownsBatch) {
         this.batch.dispose();
      }

   }

   public static final class TouchFocus implements Pool.Poolable {
      EventListener listener;
      Actor listenerActor;
      Actor target;
      int pointer;
      int button;

      public void reset() {
         this.listenerActor = null;
         this.listener = null;
      }
   }
}
