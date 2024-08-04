package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class DragAndDrop {
   static final Vector2 tmpVector = new Vector2();
   DragAndDrop.Source source;
   DragAndDrop.Payload payload;
   Actor dragActor;
   DragAndDrop.Target target;
   boolean isValidTarget;
   Array<DragAndDrop.Target> targets = new Array();
   ObjectMap<DragAndDrop.Source, DragListener> sourceListeners = new ObjectMap();
   private float tapSquareSize = 8.0F;
   private int button;
   float dragActorX = 14.0F;
   float dragActorY = -20.0F;
   long dragStartTime;
   int dragTime = 250;
   int activePointer = -1;

   public void addSource(final DragAndDrop.Source source) {
      DragListener listener = new DragListener() {
         public void dragStart(InputEvent event, float x, float y, int pointer) {
            if (DragAndDrop.this.activePointer != -1) {
               event.stop();
            } else {
               DragAndDrop.this.activePointer = pointer;
               DragAndDrop.this.dragStartTime = System.currentTimeMillis();
               DragAndDrop.this.payload = source.dragStart(event, this.getTouchDownX(), this.getTouchDownY(), pointer);
               event.stop();
            }
         }

         public void drag(InputEvent event, float x, float y, int pointer) {
            if (DragAndDrop.this.payload != null) {
               if (pointer == DragAndDrop.this.activePointer) {
                  Stage stage = event.getStage();
                  Touchable dragActorTouchable = null;
                  if (DragAndDrop.this.dragActor != null) {
                     dragActorTouchable = DragAndDrop.this.dragActor.getTouchable();
                     DragAndDrop.this.dragActor.setTouchable(Touchable.disabled);
                  }

                  DragAndDrop.Target newTarget = null;
                  DragAndDrop.this.isValidTarget = false;
                  Actor hit = event.getStage().hit(event.getStageX(), event.getStageY(), true);
                  if (hit == null) {
                     hit = event.getStage().hit(event.getStageX(), event.getStageY(), false);
                  }

                  if (hit != null) {
                     int i = 0;

                     for(int n = DragAndDrop.this.targets.size; i < n; ++i) {
                        DragAndDrop.Target target = (DragAndDrop.Target)DragAndDrop.this.targets.get(i);
                        if (target.actor.isAscendantOf(hit)) {
                           newTarget = target;
                           target.actor.stageToLocalCoordinates(DragAndDrop.tmpVector.set(event.getStageX(), event.getStageY()));
                           DragAndDrop.this.isValidTarget = target.drag(source, DragAndDrop.this.payload, DragAndDrop.tmpVector.x, DragAndDrop.tmpVector.y, pointer);
                           break;
                        }
                     }
                  }

                  if (newTarget != DragAndDrop.this.target) {
                     if (DragAndDrop.this.target != null) {
                        DragAndDrop.this.target.reset(source, DragAndDrop.this.payload);
                     }

                     DragAndDrop.this.target = newTarget;
                  }

                  if (DragAndDrop.this.dragActor != null) {
                     DragAndDrop.this.dragActor.setTouchable(dragActorTouchable);
                  }

                  Actor actor = null;
                  if (DragAndDrop.this.target != null) {
                     actor = DragAndDrop.this.isValidTarget ? DragAndDrop.this.payload.validDragActor : DragAndDrop.this.payload.invalidDragActor;
                  }

                  if (actor == null) {
                     actor = DragAndDrop.this.payload.dragActor;
                  }

                  if (actor != null) {
                     if (DragAndDrop.this.dragActor != actor) {
                        if (DragAndDrop.this.dragActor != null) {
                           DragAndDrop.this.dragActor.remove();
                        }

                        DragAndDrop.this.dragActor = actor;
                        stage.addActor(actor);
                     }

                     float actorX = event.getStageX() + DragAndDrop.this.dragActorX;
                     float actorY = event.getStageY() + DragAndDrop.this.dragActorY - actor.getHeight();
                     if (actorX < 0.0F) {
                        actorX = 0.0F;
                     }

                     if (actorY < 0.0F) {
                        actorY = 0.0F;
                     }

                     if (actorX + actor.getWidth() > stage.getWidth()) {
                        actorX = stage.getWidth() - actor.getWidth();
                     }

                     if (actorY + actor.getHeight() > stage.getHeight()) {
                        actorY = stage.getHeight() - actor.getHeight();
                     }

                     actor.setPosition(actorX, actorY);
                  }
               }
            }
         }

         public void dragStop(InputEvent event, float x, float y, int pointer) {
            if (pointer == DragAndDrop.this.activePointer) {
               DragAndDrop.this.activePointer = -1;
               if (DragAndDrop.this.payload != null) {
                  if (System.currentTimeMillis() - DragAndDrop.this.dragStartTime < (long)DragAndDrop.this.dragTime) {
                     DragAndDrop.this.isValidTarget = false;
                  }

                  if (DragAndDrop.this.dragActor != null) {
                     DragAndDrop.this.dragActor.remove();
                  }

                  if (DragAndDrop.this.isValidTarget) {
                     DragAndDrop.this.target.actor.stageToLocalCoordinates(DragAndDrop.tmpVector.set(event.getStageX(), event.getStageY()));
                     DragAndDrop.this.target.drop(source, DragAndDrop.this.payload, DragAndDrop.tmpVector.x, DragAndDrop.tmpVector.y, pointer);
                  }

                  source.dragStop(event, x, y, pointer, DragAndDrop.this.isValidTarget ? DragAndDrop.this.target : null);
                  if (DragAndDrop.this.target != null) {
                     DragAndDrop.this.target.reset(source, DragAndDrop.this.payload);
                  }

                  DragAndDrop.this.source = null;
                  DragAndDrop.this.payload = null;
                  DragAndDrop.this.target = null;
                  DragAndDrop.this.isValidTarget = false;
                  DragAndDrop.this.dragActor = null;
               }
            }
         }
      };
      listener.setTapSquareSize(this.tapSquareSize);
      listener.setButton(this.button);
      source.actor.addCaptureListener(listener);
      this.sourceListeners.put(source, listener);
   }

   public void removeSource(DragAndDrop.Source source) {
      DragListener dragListener = (DragListener)this.sourceListeners.remove(source);
      source.actor.removeCaptureListener(dragListener);
   }

   public void addTarget(DragAndDrop.Target target) {
      this.targets.add(target);
   }

   public void removeTarget(DragAndDrop.Target target) {
      this.targets.removeValue(target, true);
   }

   public void setTapSquareSize(float halfTapSquareSize) {
      this.tapSquareSize = halfTapSquareSize;
   }

   public void setButton(int button) {
      this.button = button;
   }

   public void setDragActorPosition(float dragActorX, float dragActorY) {
      this.dragActorX = dragActorX;
      this.dragActorY = dragActorY;
   }

   public boolean isDragging() {
      return this.payload != null;
   }

   public Actor getDragActor() {
      return this.dragActor;
   }

   public void setDragTime(int dragMillis) {
      this.dragTime = dragMillis;
   }

   public static class Payload {
      Actor dragActor;
      Actor validDragActor;
      Actor invalidDragActor;
      Object object;

      public void setDragActor(Actor dragActor) {
         this.dragActor = dragActor;
      }

      public Actor getDragActor() {
         return this.dragActor;
      }

      public void setValidDragActor(Actor validDragActor) {
         this.validDragActor = validDragActor;
      }

      public Actor getValidDragActor() {
         return this.validDragActor;
      }

      public void setInvalidDragActor(Actor invalidDragActor) {
         this.invalidDragActor = invalidDragActor;
      }

      public Actor getInvalidDragActor() {
         return this.invalidDragActor;
      }

      public Object getObject() {
         return this.object;
      }

      public void setObject(Object object) {
         this.object = object;
      }
   }

   public abstract static class Source {
      final Actor actor;

      public Source(Actor actor) {
         if (actor == null) {
            throw new IllegalArgumentException("actor cannot be null.");
         } else {
            this.actor = actor;
         }
      }

      public abstract DragAndDrop.Payload dragStart(InputEvent var1, float var2, float var3, int var4);

      public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Target target) {
      }

      public Actor getActor() {
         return this.actor;
      }
   }

   public abstract static class Target {
      final Actor actor;

      public Target(Actor actor) {
         if (actor == null) {
            throw new IllegalArgumentException("actor cannot be null.");
         } else {
            this.actor = actor;
            Stage stage = actor.getStage();
            if (stage != null && actor == stage.getRoot()) {
               throw new IllegalArgumentException("The stage root cannot be a drag and drop target.");
            }
         }
      }

      public abstract boolean drag(DragAndDrop.Source var1, DragAndDrop.Payload var2, float var3, float var4, int var5);

      public void reset(DragAndDrop.Source source, DragAndDrop.Payload payload) {
      }

      public abstract void drop(DragAndDrop.Source var1, DragAndDrop.Payload var2, float var3, float var4, int var5);

      public Actor getActor() {
         return this.actor;
      }
   }
}
