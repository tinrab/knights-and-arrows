package com.badlogic.gdx.scenes.scene2d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.Pools;

public class Actor {
   private Stage stage;
   private Group parent;
   private final DelayedRemovalArray<EventListener> listeners = new DelayedRemovalArray(0);
   private final DelayedRemovalArray<EventListener> captureListeners = new DelayedRemovalArray(0);
   private final Array<Action> actions = new Array(0);
   private String name;
   private Touchable touchable;
   private boolean visible;
   float x;
   float y;
   float width;
   float height;
   float originX;
   float originY;
   float scaleX;
   float scaleY;
   float rotation;
   final Color color;

   public Actor() {
      this.touchable = Touchable.enabled;
      this.visible = true;
      this.scaleX = 1.0F;
      this.scaleY = 1.0F;
      this.color = new Color(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public void draw(SpriteBatch batch, float parentAlpha) {
   }

   public void act(float delta) {
      for(int i = 0; i < this.actions.size; ++i) {
         Action action = (Action)this.actions.get(i);
         if (action.act(delta) && i < this.actions.size) {
            this.actions.removeIndex(i);
            action.setActor((Actor)null);
            --i;
         }
      }

   }

   public boolean fire(Event event) {
      if (event.getStage() == null) {
         event.setStage(this.getStage());
      }

      event.setTarget(this);
      Array<Group> ancestors = (Array)Pools.obtain(Array.class);

      for(Group parent = this.getParent(); parent != null; parent = parent.getParent()) {
         ancestors.add(parent);
      }

      try {
         int i;
         boolean var7;
         for(i = ancestors.size - 1; i >= 0; --i) {
            Group currentTarget = (Group)ancestors.get(i);
            currentTarget.notify(event, true);
            if (event.isStopped()) {
               var7 = event.isCancelled();
               return var7;
            }
         }

         this.notify(event, true);
         if (event.isStopped()) {
            var7 = event.isCancelled();
            return var7;
         } else {
            this.notify(event, false);
            if (!event.getBubbles()) {
               var7 = event.isCancelled();
               return var7;
            } else if (event.isStopped()) {
               var7 = event.isCancelled();
               return var7;
            } else {
               i = 0;

               for(int n = ancestors.size; i < n; ++i) {
                  ((Group)ancestors.get(i)).notify(event, false);
                  if (event.isStopped()) {
                     var7 = event.isCancelled();
                     return var7;
                  }
               }

               var7 = event.isCancelled();
               return var7;
            }
         }
      } finally {
         ancestors.clear();
         Pools.free(ancestors);
      }
   }

   public boolean notify(Event event, boolean capture) {
      if (event.getTarget() == null) {
         throw new IllegalArgumentException("The event target cannot be null.");
      } else {
         DelayedRemovalArray<EventListener> listeners = capture ? this.captureListeners : this.listeners;
         if (listeners.size == 0) {
            return event.isCancelled();
         } else {
            event.setListenerActor(this);
            event.setCapture(capture);
            if (event.getStage() == null) {
               event.setStage(this.stage);
            }

            listeners.begin();
            int i = 0;

            for(int n = listeners.size; i < n; ++i) {
               EventListener listener = (EventListener)listeners.get(i);
               if (listener.handle(event)) {
                  event.handle();
                  if (event instanceof InputEvent) {
                     InputEvent inputEvent = (InputEvent)event;
                     if (inputEvent.getType() == InputEvent.Type.touchDown) {
                        event.getStage().addTouchFocus(listener, this, inputEvent.getTarget(), inputEvent.getPointer(), inputEvent.getButton());
                     }
                  }
               }
            }

            listeners.end();
            return event.isCancelled();
         }
      }
   }

   public Actor hit(float x, float y, boolean touchable) {
      if (touchable && this.touchable != Touchable.enabled) {
         return null;
      } else {
         return x >= 0.0F && x < this.width && y >= 0.0F && y < this.height ? this : null;
      }
   }

   public boolean remove() {
      return this.parent != null ? this.parent.removeActor(this) : false;
   }

   public boolean addListener(EventListener listener) {
      if (!this.listeners.contains(listener, true)) {
         this.listeners.add(listener);
         return true;
      } else {
         return false;
      }
   }

   public boolean removeListener(EventListener listener) {
      return this.listeners.removeValue(listener, true);
   }

   public Array<EventListener> getListeners() {
      return this.listeners;
   }

   public boolean addCaptureListener(EventListener listener) {
      if (!this.captureListeners.contains(listener, true)) {
         this.captureListeners.add(listener);
      }

      return true;
   }

   public boolean removeCaptureListener(EventListener listener) {
      return this.captureListeners.removeValue(listener, true);
   }

   public Array<EventListener> getCaptureListeners() {
      return this.captureListeners;
   }

   public void addAction(Action action) {
      action.setActor(this);
      this.actions.add(action);
   }

   public void removeAction(Action action) {
      if (this.actions.removeValue(action, true)) {
         action.setActor((Actor)null);
      }

   }

   public Array<Action> getActions() {
      return this.actions;
   }

   public void clearActions() {
      for(int i = this.actions.size - 1; i >= 0; --i) {
         ((Action)this.actions.get(i)).setActor((Actor)null);
      }

      this.actions.clear();
   }

   public void clearListeners() {
      this.listeners.clear();
      this.captureListeners.clear();
   }

   public void clear() {
      this.clearActions();
      this.clearListeners();
   }

   public Stage getStage() {
      return this.stage;
   }

   protected void setStage(Stage stage) {
      this.stage = stage;
   }

   public boolean isDescendantOf(Actor actor) {
      if (actor == null) {
         throw new IllegalArgumentException("actor cannot be null.");
      } else {
         for(Object parent = this; parent != null; parent = ((Actor)parent).parent) {
            if (parent == actor) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean isAscendantOf(Actor actor) {
      if (actor == null) {
         throw new IllegalArgumentException("actor cannot be null.");
      } else {
         while(actor != null) {
            if (actor == this) {
               return true;
            }

            actor = ((Actor)actor).parent;
         }

         return false;
      }
   }

   public boolean hasParent() {
      return this.parent != null;
   }

   public Group getParent() {
      return this.parent;
   }

   protected void setParent(Group parent) {
      this.parent = parent;
   }

   public Touchable getTouchable() {
      return this.touchable;
   }

   public void setTouchable(Touchable touchable) {
      this.touchable = touchable;
   }

   public boolean isVisible() {
      return this.visible;
   }

   public void setVisible(boolean visible) {
      this.visible = visible;
   }

   public float getX() {
      return this.x;
   }

   public void setX(float x) {
      this.x = x;
   }

   public float getY() {
      return this.y;
   }

   public void setY(float y) {
      this.y = y;
   }

   public void setPosition(float x, float y) {
      this.x = x;
      this.y = y;
   }

   public void translate(float x, float y) {
      this.x += x;
      this.y += y;
   }

   public float getWidth() {
      return this.width;
   }

   public void setWidth(float width) {
      this.width = width;
   }

   public float getHeight() {
      return this.height;
   }

   public void setHeight(float height) {
      this.height = height;
   }

   public float getTop() {
      return this.y + this.height;
   }

   public float getRight() {
      return this.x + this.width;
   }

   public void setSize(float width, float height) {
      this.width = width;
      this.height = height;
   }

   public void size(float size) {
      this.width += size;
      this.height += size;
   }

   public void size(float width, float height) {
      this.width += width;
      this.height += height;
   }

   public void setBounds(float x, float y, float width, float height) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
   }

   public float getOriginX() {
      return this.originX;
   }

   public void setOriginX(float originX) {
      this.originX = originX;
   }

   public float getOriginY() {
      return this.originY;
   }

   public void setOriginY(float originY) {
      this.originY = originY;
   }

   public void setOrigin(float originX, float originY) {
      this.originX = originX;
      this.originY = originY;
   }

   public float getScaleX() {
      return this.scaleX;
   }

   public void setScaleX(float scaleX) {
      this.scaleX = scaleX;
   }

   public float getScaleY() {
      return this.scaleY;
   }

   public void setScaleY(float scaleY) {
      this.scaleY = scaleY;
   }

   public void setScale(float scale) {
      this.scaleX = scale;
      this.scaleY = scale;
   }

   public void setScale(float scaleX, float scaleY) {
      this.scaleX = scaleX;
      this.scaleY = scaleY;
   }

   public void scale(float scale) {
      this.scaleX += scale;
      this.scaleY += scale;
   }

   public void scale(float scaleX, float scaleY) {
      this.scaleX += scaleX;
      this.scaleY += scaleY;
   }

   public float getRotation() {
      return this.rotation;
   }

   public void setRotation(float degrees) {
      this.rotation = degrees;
   }

   public void rotate(float amountInDegrees) {
      this.rotation += amountInDegrees;
   }

   public void setColor(Color color) {
      this.color.set(color);
   }

   public void setColor(float r, float g, float b, float a) {
      this.color.set(r, g, b, a);
   }

   public Color getColor() {
      return this.color;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void toFront() {
      this.setZIndex(Integer.MAX_VALUE);
   }

   public void toBack() {
      this.setZIndex(0);
   }

   public void setZIndex(int index) {
      if (index < 0) {
         throw new IllegalArgumentException("ZIndex cannot be < 0.");
      } else {
         Group parent = this.parent;
         if (parent != null) {
            Array<Actor> children = parent.getChildren();
            if (children.size != 1) {
               if (children.removeValue(this, true)) {
                  if (index >= children.size) {
                     children.add(this);
                  } else {
                     children.insert(index, this);
                  }

               }
            }
         }
      }
   }

   public int getZIndex() {
      Group parent = this.parent;
      return parent == null ? -1 : parent.getChildren().indexOf(this, true);
   }

   public boolean clipBegin() {
      return this.clipBegin(this.x, this.y, this.width, this.height);
   }

   public boolean clipBegin(float x, float y, float width, float height) {
      Rectangle tableBounds = Rectangle.tmp;
      tableBounds.x = x;
      tableBounds.y = y;
      tableBounds.width = width;
      tableBounds.height = height;
      Stage stage = this.stage;
      Rectangle scissorBounds = (Rectangle)Pools.obtain(Rectangle.class);
      ScissorStack.calculateScissors(stage.getCamera(), stage.getSpriteBatch().getTransformMatrix(), tableBounds, scissorBounds);
      if (ScissorStack.pushScissors(scissorBounds)) {
         return true;
      } else {
         Pools.free(scissorBounds);
         return false;
      }
   }

   public void clipEnd() {
      Pools.free(ScissorStack.popScissors());
   }

   public Vector2 screenToLocalCoordinates(Vector2 screenCoords) {
      Stage stage = this.stage;
      return stage == null ? screenCoords : this.stageToLocalCoordinates(stage.screenToStageCoordinates(screenCoords));
   }

   public Vector2 stageToLocalCoordinates(Vector2 stageCoords) {
      if (this.parent == null) {
         return stageCoords;
      } else {
         this.parent.stageToLocalCoordinates(stageCoords);
         this.parentToLocalCoordinates(stageCoords);
         return stageCoords;
      }
   }

   public Vector2 localToStageCoordinates(Vector2 localCoords) {
      return this.localToAscendantCoordinates((Actor)null, localCoords);
   }

   public Vector2 localToParentCoordinates(Vector2 localCoords) {
      float rotation = -this.rotation;
      float scaleX = this.scaleX;
      float scaleY = this.scaleY;
      float x = this.x;
      float y = this.y;
      float originX;
      float originY;
      if (rotation == 0.0F) {
         if (scaleX == 1.0F && scaleY == 1.0F) {
            localCoords.x += x;
            localCoords.y += y;
         } else {
            originX = this.originX;
            originY = this.originY;
            localCoords.x = (localCoords.x - originX) * scaleX + originX + x;
            localCoords.y = (localCoords.y - originY) * scaleY + originY + y;
         }
      } else {
         originX = (float)Math.cos((double)(rotation * 0.017453292F));
         originY = (float)Math.sin((double)(rotation * 0.017453292F));
         float originX = this.originX;
         float originY = this.originY;
         float tox = localCoords.x - originX;
         float toy = localCoords.y - originY;
         localCoords.x = (tox * originX + toy * originY) * scaleX + originX + x;
         localCoords.y = (tox * -originY + toy * originX) * scaleY + originY + y;
      }

      return localCoords;
   }

   public Vector2 localToAscendantCoordinates(Actor ascendant, Vector2 localCoords) {
      Object actor = this;

      while(((Actor)actor).parent != null) {
         ((Actor)actor).localToParentCoordinates(localCoords);
         actor = ((Actor)actor).parent;
         if (actor == ascendant) {
            break;
         }
      }

      return localCoords;
   }

   public Vector2 parentToLocalCoordinates(Vector2 parentCoords) {
      float rotation = this.rotation;
      float scaleX = this.scaleX;
      float scaleY = this.scaleY;
      float childX = this.x;
      float childY = this.y;
      float originX;
      float originY;
      if (rotation == 0.0F) {
         if (scaleX == 1.0F && scaleY == 1.0F) {
            parentCoords.x -= childX;
            parentCoords.y -= childY;
         } else {
            originX = this.originX;
            originY = this.originY;
            parentCoords.x = (parentCoords.x - childX - originX) / scaleX + originX;
            parentCoords.y = (parentCoords.y - childY - originY) / scaleY + originY;
         }
      } else {
         originX = (float)Math.cos((double)(rotation * 0.017453292F));
         originY = (float)Math.sin((double)(rotation * 0.017453292F));
         float originX = this.originX;
         float originY = this.originY;
         float tox = parentCoords.x - childX - originX;
         float toy = parentCoords.y - childY - originY;
         parentCoords.x = (tox * originX + toy * originY) / scaleX + originX;
         parentCoords.y = (tox * -originY + toy * originX) / scaleY + originY;
      }

      return parentCoords;
   }

   public String toString() {
      String name = this.name;
      if (name == null) {
         name = this.getClass().getName();
         int dotIndex = name.lastIndexOf(46);
         if (dotIndex != -1) {
            name = name.substring(dotIndex + 1);
         }
      }

      return name;
   }
}
