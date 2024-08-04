package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class SplitPane extends WidgetGroup {
   SplitPane.SplitPaneStyle style;
   private Actor firstWidget;
   private Actor secondWidget;
   boolean vertical;
   float splitAmount;
   float minAmount;
   float maxAmount;
   private float oldSplitAmount;
   private Rectangle firstWidgetBounds;
   private Rectangle secondWidgetBounds;
   Rectangle handleBounds;
   private Rectangle firstScissors;
   private Rectangle secondScissors;
   Vector2 lastPoint;
   Vector2 handlePosition;

   public SplitPane(Actor firstWidget, Actor secondWidget, boolean vertical, Skin skin) {
      this(firstWidget, secondWidget, vertical, skin, "default-" + (vertical ? "vertical" : "horizontal"));
   }

   public SplitPane(Actor firstWidget, Actor secondWidget, boolean vertical, Skin skin, String styleName) {
      this(firstWidget, secondWidget, vertical, (SplitPane.SplitPaneStyle)skin.get(styleName, SplitPane.SplitPaneStyle.class));
   }

   public SplitPane(Actor firstWidget, Actor secondWidget, boolean vertical, SplitPane.SplitPaneStyle style) {
      this.splitAmount = 0.5F;
      this.maxAmount = 1.0F;
      this.firstWidgetBounds = new Rectangle();
      this.secondWidgetBounds = new Rectangle();
      this.handleBounds = new Rectangle();
      this.firstScissors = new Rectangle();
      this.secondScissors = new Rectangle();
      this.lastPoint = new Vector2();
      this.handlePosition = new Vector2();
      this.firstWidget = firstWidget;
      this.secondWidget = secondWidget;
      this.vertical = vertical;
      this.setStyle(style);
      this.setFirstWidget(firstWidget);
      this.setSecondWidget(secondWidget);
      this.setWidth(this.getPrefWidth());
      this.setHeight(this.getPrefHeight());
      this.initialize();
   }

   private void initialize() {
      this.addListener(new InputListener() {
         int draggingPointer = -1;

         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            if (this.draggingPointer != -1) {
               return false;
            } else if (pointer == 0 && button != 0) {
               return false;
            } else if (SplitPane.this.handleBounds.contains(x, y)) {
               this.draggingPointer = pointer;
               SplitPane.this.lastPoint.set(x, y);
               SplitPane.this.handlePosition.set(SplitPane.this.handleBounds.x, SplitPane.this.handleBounds.y);
               return true;
            } else {
               return false;
            }
         }

         public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            if (pointer == this.draggingPointer) {
               this.draggingPointer = -1;
            }

         }

         public void touchDragged(InputEvent event, float x, float y, int pointer) {
            if (pointer == this.draggingPointer) {
               Drawable handle = SplitPane.this.style.handle;
               float delta;
               float availWidth;
               float dragX;
               if (!SplitPane.this.vertical) {
                  delta = x - SplitPane.this.lastPoint.x;
                  availWidth = SplitPane.this.getWidth() - handle.getMinWidth();
                  dragX = SplitPane.this.handlePosition.x + delta;
                  SplitPane.this.handlePosition.x = dragX;
                  dragX = Math.max(0.0F, dragX);
                  dragX = Math.min(availWidth, dragX);
                  SplitPane.this.splitAmount = dragX / availWidth;
                  if (SplitPane.this.splitAmount < SplitPane.this.minAmount) {
                     SplitPane.this.splitAmount = SplitPane.this.minAmount;
                  }

                  if (SplitPane.this.splitAmount > SplitPane.this.maxAmount) {
                     SplitPane.this.splitAmount = SplitPane.this.maxAmount;
                  }

                  SplitPane.this.lastPoint.set(x, y);
               } else {
                  delta = y - SplitPane.this.lastPoint.y;
                  availWidth = SplitPane.this.getHeight() - handle.getMinHeight();
                  dragX = SplitPane.this.handlePosition.y + delta;
                  SplitPane.this.handlePosition.y = dragX;
                  dragX = Math.max(0.0F, dragX);
                  dragX = Math.min(availWidth, dragX);
                  SplitPane.this.splitAmount = 1.0F - dragX / availWidth;
                  if (SplitPane.this.splitAmount < SplitPane.this.minAmount) {
                     SplitPane.this.splitAmount = SplitPane.this.minAmount;
                  }

                  if (SplitPane.this.splitAmount > SplitPane.this.maxAmount) {
                     SplitPane.this.splitAmount = SplitPane.this.maxAmount;
                  }

                  SplitPane.this.lastPoint.set(x, y);
               }

               SplitPane.this.invalidate();
            }
         }
      });
   }

   public void setStyle(SplitPane.SplitPaneStyle style) {
      this.style = style;
      this.invalidateHierarchy();
   }

   public SplitPane.SplitPaneStyle getStyle() {
      return this.style;
   }

   public void layout() {
      if (!this.vertical) {
         this.calculateHorizBoundsAndPositions();
      } else {
         this.calculateVertBoundsAndPositions();
      }

      Actor firstWidget = this.firstWidget;
      Rectangle firstWidgetBounds = this.firstWidgetBounds;
      if (firstWidget != null) {
         firstWidget.setX(firstWidgetBounds.x);
         firstWidget.setY(firstWidgetBounds.y);
         if (firstWidget.getWidth() == firstWidgetBounds.width && firstWidget.getHeight() == firstWidgetBounds.height) {
            if (firstWidget instanceof Layout) {
               ((Layout)firstWidget).validate();
            }
         } else {
            firstWidget.setWidth(firstWidgetBounds.width);
            firstWidget.setHeight(firstWidgetBounds.height);
            if (firstWidget instanceof Layout) {
               Layout layout = (Layout)firstWidget;
               layout.invalidate();
               layout.validate();
            }
         }
      }

      Actor secondWidget = this.secondWidget;
      Rectangle secondWidgetBounds = this.secondWidgetBounds;
      if (secondWidget != null) {
         secondWidget.setX(secondWidgetBounds.x);
         secondWidget.setY(secondWidgetBounds.y);
         if (secondWidget.getWidth() == secondWidgetBounds.width && secondWidget.getHeight() == secondWidgetBounds.height) {
            if (secondWidget instanceof Layout) {
               ((Layout)secondWidget).validate();
            }
         } else {
            secondWidget.setWidth(secondWidgetBounds.width);
            secondWidget.setHeight(secondWidgetBounds.height);
            if (secondWidget instanceof Layout) {
               Layout layout = (Layout)secondWidget;
               layout.invalidate();
               layout.validate();
            }
         }
      }

   }

   public float getPrefWidth() {
      float width = this.firstWidget instanceof Layout ? ((Layout)this.firstWidget).getPrefWidth() : this.firstWidget.getWidth();
      width += this.secondWidget instanceof Layout ? ((Layout)this.secondWidget).getPrefWidth() : this.secondWidget.getWidth();
      if (!this.vertical) {
         width += this.style.handle.getMinWidth();
      }

      return width;
   }

   public float getPrefHeight() {
      float height = this.firstWidget instanceof Layout ? ((Layout)this.firstWidget).getPrefHeight() : this.firstWidget.getHeight();
      height += this.secondWidget instanceof Layout ? ((Layout)this.secondWidget).getPrefHeight() : this.secondWidget.getHeight();
      if (this.vertical) {
         height += this.style.handle.getMinHeight();
      }

      return height;
   }

   public float getMinWidth() {
      return 0.0F;
   }

   public float getMinHeight() {
      return 0.0F;
   }

   public void setVertical(boolean vertical) {
      this.vertical = vertical;
   }

   private void calculateHorizBoundsAndPositions() {
      Drawable handle = this.style.handle;
      float height = this.getHeight();
      float availWidth = this.getWidth() - handle.getMinWidth();
      float leftAreaWidth = (float)((int)(availWidth * this.splitAmount));
      float rightAreaWidth = availWidth - leftAreaWidth;
      float handleWidth = handle.getMinWidth();
      this.firstWidgetBounds.set(0.0F, 0.0F, leftAreaWidth, height);
      this.secondWidgetBounds.set(leftAreaWidth + handleWidth, 0.0F, rightAreaWidth, height);
      this.handleBounds.set(leftAreaWidth, 0.0F, handleWidth, height);
   }

   private void calculateVertBoundsAndPositions() {
      Drawable handle = this.style.handle;
      float width = this.getWidth();
      float height = this.getHeight();
      float availHeight = height - handle.getMinHeight();
      float topAreaHeight = (float)((int)(availHeight * this.splitAmount));
      float bottomAreaHeight = availHeight - topAreaHeight;
      float handleHeight = handle.getMinHeight();
      this.firstWidgetBounds.set(0.0F, height - topAreaHeight, width, topAreaHeight);
      this.secondWidgetBounds.set(0.0F, 0.0F, width, bottomAreaHeight);
      this.handleBounds.set(0.0F, bottomAreaHeight, width, handleHeight);
   }

   public void draw(SpriteBatch batch, float parentAlpha) {
      this.validate();
      Color color = this.getColor();
      Drawable handle = this.style.handle;
      this.applyTransform(batch, this.computeTransform());
      Matrix4 transform = batch.getTransformMatrix();
      if (this.firstWidget != null) {
         ScissorStack.calculateScissors(this.getStage().getCamera(), transform, this.firstWidgetBounds, this.firstScissors);
         if (ScissorStack.pushScissors(this.firstScissors)) {
            if (this.firstWidget.isVisible()) {
               this.firstWidget.draw(batch, parentAlpha * color.a);
            }

            batch.flush();
            ScissorStack.popScissors();
         }
      }

      if (this.secondWidget != null) {
         ScissorStack.calculateScissors(this.getStage().getCamera(), transform, this.secondWidgetBounds, this.secondScissors);
         if (ScissorStack.pushScissors(this.secondScissors)) {
            if (this.secondWidget.isVisible()) {
               this.secondWidget.draw(batch, parentAlpha * color.a);
            }

            batch.flush();
            ScissorStack.popScissors();
         }
      }

      batch.setColor(color.r, color.g, color.b, color.a);
      handle.draw(batch, this.handleBounds.x, this.handleBounds.y, this.handleBounds.width, this.handleBounds.height);
      this.resetTransform(batch);
   }

   public void setSplitAmount(float split) {
      this.splitAmount = Math.max(Math.min(this.maxAmount, split), this.minAmount);
      this.invalidate();
   }

   public float getSplit() {
      return this.splitAmount;
   }

   public void setMinSplitAmount(float minAmount) {
      if (minAmount < 0.0F) {
         throw new GdxRuntimeException("minAmount has to be >= 0");
      } else if (minAmount >= this.maxAmount) {
         throw new GdxRuntimeException("minAmount has to be < maxAmount");
      } else {
         this.minAmount = minAmount;
      }
   }

   public void setMaxSplitAmount(float maxAmount) {
      if (maxAmount > 1.0F) {
         throw new GdxRuntimeException("maxAmount has to be >= 0");
      } else if (maxAmount <= this.minAmount) {
         throw new GdxRuntimeException("maxAmount has to be > minAmount");
      } else {
         this.maxAmount = maxAmount;
      }
   }

   public void setFirstWidget(Actor widget) {
      if (this.firstWidget != null) {
         super.removeActor(this.firstWidget);
      }

      this.firstWidget = widget;
      if (widget != null) {
         super.addActor(widget);
      }

      this.invalidate();
   }

   public void setSecondWidget(Actor widget) {
      if (this.secondWidget != null) {
         super.removeActor(this.secondWidget);
      }

      this.secondWidget = widget;
      if (widget != null) {
         super.addActor(widget);
      }

      this.invalidate();
   }

   public void addActor(Actor actor) {
      throw new UnsupportedOperationException("Use ScrollPane#setWidget.");
   }

   public void addActorAt(int index, Actor actor) {
      throw new UnsupportedOperationException("Use ScrollPane#setWidget.");
   }

   public void addActorBefore(Actor actorBefore, Actor actor) {
      throw new UnsupportedOperationException("Use ScrollPane#setWidget.");
   }

   public boolean removeActor(Actor actor) {
      throw new UnsupportedOperationException("Use ScrollPane#setWidget(null).");
   }

   public static class SplitPaneStyle {
      public Drawable handle;

      public SplitPaneStyle() {
      }

      public SplitPaneStyle(Drawable handle) {
         this.handle = handle;
      }

      public SplitPaneStyle(SplitPane.SplitPaneStyle style) {
         this.handle = style.handle;
      }
   }
}
