package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.Cullable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;

public class ScrollPane extends WidgetGroup {
   private ScrollPane.ScrollPaneStyle style;
   private Actor widget;
   final Rectangle hScrollBounds;
   final Rectangle vScrollBounds;
   final Rectangle hKnobBounds;
   final Rectangle vKnobBounds;
   private final Rectangle widgetAreaBounds;
   private final Rectangle widgetCullingArea;
   private final Rectangle scissorBounds;
   private ActorGestureListener flickScrollListener;
   boolean scrollX;
   boolean scrollY;
   float amountX;
   float amountY;
   float visualAmountX;
   float visualAmountY;
   float maxX;
   float maxY;
   boolean touchScrollH;
   boolean touchScrollV;
   final Vector2 lastPoint;
   float areaWidth;
   float areaHeight;
   private boolean fadeScrollBars;
   private boolean smoothScrolling;
   float fadeAlpha;
   float fadeAlphaSeconds;
   float fadeDelay;
   float fadeDelaySeconds;
   boolean cancelTouchFocus;
   boolean flickScroll;
   float velocityX;
   float velocityY;
   float flingTimer;
   private boolean overscrollX;
   private boolean overscrollY;
   float flingTime;
   private float overscrollDistance;
   private float overscrollSpeedMin;
   private float overscrollSpeedMax;
   private boolean forceScrollX;
   private boolean forceScrollY;
   private boolean disableX;
   private boolean disableY;
   private boolean clamp;
   private boolean scrollbarsOnTop;
   int draggingPointer;

   public ScrollPane(Actor widget) {
      this(widget, new ScrollPane.ScrollPaneStyle());
   }

   public ScrollPane(Actor widget, Skin skin) {
      this(widget, (ScrollPane.ScrollPaneStyle)skin.get(ScrollPane.ScrollPaneStyle.class));
   }

   public ScrollPane(Actor widget, Skin skin, String styleName) {
      this(widget, (ScrollPane.ScrollPaneStyle)skin.get(styleName, ScrollPane.ScrollPaneStyle.class));
   }

   public ScrollPane(Actor widget, ScrollPane.ScrollPaneStyle style) {
      this.hScrollBounds = new Rectangle();
      this.vScrollBounds = new Rectangle();
      this.hKnobBounds = new Rectangle();
      this.vKnobBounds = new Rectangle();
      this.widgetAreaBounds = new Rectangle();
      this.widgetCullingArea = new Rectangle();
      this.scissorBounds = new Rectangle();
      this.lastPoint = new Vector2();
      this.fadeScrollBars = true;
      this.smoothScrolling = true;
      this.fadeAlphaSeconds = 1.0F;
      this.fadeDelaySeconds = 1.0F;
      this.cancelTouchFocus = true;
      this.flickScroll = true;
      this.overscrollX = true;
      this.overscrollY = true;
      this.flingTime = 1.0F;
      this.overscrollDistance = 50.0F;
      this.overscrollSpeedMin = 30.0F;
      this.overscrollSpeedMax = 200.0F;
      this.clamp = true;
      this.draggingPointer = -1;
      if (style == null) {
         throw new IllegalArgumentException("style cannot be null.");
      } else {
         this.style = style;
         this.setWidget(widget);
         this.setWidth(150.0F);
         this.setHeight(150.0F);
         this.addCaptureListener(new InputListener() {
            private float handlePosition;

            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
               if (ScrollPane.this.draggingPointer != -1) {
                  return false;
               } else if (pointer == 0 && button != 0) {
                  return false;
               } else {
                  ScrollPane.this.getStage().setScrollFocus(ScrollPane.this);
                  if (!ScrollPane.this.flickScroll) {
                     ScrollPane.this.resetFade();
                  }

                  if (ScrollPane.this.fadeAlpha == 0.0F) {
                     return false;
                  } else if (ScrollPane.this.scrollX && ScrollPane.this.hScrollBounds.contains(x, y)) {
                     event.stop();
                     ScrollPane.this.resetFade();
                     if (ScrollPane.this.hKnobBounds.contains(x, y)) {
                        ScrollPane.this.lastPoint.set(x, y);
                        this.handlePosition = ScrollPane.this.hKnobBounds.x;
                        ScrollPane.this.touchScrollH = true;
                        ScrollPane.this.draggingPointer = pointer;
                        return true;
                     } else {
                        ScrollPane.this.setScrollX(ScrollPane.this.amountX + Math.max(ScrollPane.this.areaWidth * 0.9F, ScrollPane.this.maxX * 0.1F) * (float)(x < ScrollPane.this.hKnobBounds.x ? -1 : 1));
                        return true;
                     }
                  } else if (ScrollPane.this.scrollY && ScrollPane.this.vScrollBounds.contains(x, y)) {
                     event.stop();
                     ScrollPane.this.resetFade();
                     if (ScrollPane.this.vKnobBounds.contains(x, y)) {
                        ScrollPane.this.lastPoint.set(x, y);
                        this.handlePosition = ScrollPane.this.vKnobBounds.y;
                        ScrollPane.this.touchScrollV = true;
                        ScrollPane.this.draggingPointer = pointer;
                        return true;
                     } else {
                        ScrollPane.this.setScrollY(ScrollPane.this.amountY + Math.max(ScrollPane.this.areaHeight * 0.9F, ScrollPane.this.maxY * 0.1F) * (float)(y < ScrollPane.this.vKnobBounds.y ? 1 : -1));
                        return true;
                     }
                  } else {
                     return false;
                  }
               }
            }

            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
               if (pointer == ScrollPane.this.draggingPointer) {
                  ScrollPane.this.draggingPointer = -1;
                  ScrollPane.this.touchScrollH = false;
                  ScrollPane.this.touchScrollV = false;
               }
            }

            public void touchDragged(InputEvent event, float x, float y, int pointer) {
               if (pointer == ScrollPane.this.draggingPointer) {
                  float delta;
                  float scrollV;
                  float total;
                  if (ScrollPane.this.touchScrollH) {
                     delta = x - ScrollPane.this.lastPoint.x;
                     scrollV = this.handlePosition + delta;
                     this.handlePosition = scrollV;
                     scrollV = Math.max(ScrollPane.this.hScrollBounds.x, scrollV);
                     scrollV = Math.min(ScrollPane.this.hScrollBounds.x + ScrollPane.this.hScrollBounds.width - ScrollPane.this.hKnobBounds.width, scrollV);
                     total = ScrollPane.this.hScrollBounds.width - ScrollPane.this.hKnobBounds.width;
                     if (total != 0.0F) {
                        ScrollPane.this.setScrollPercentX((scrollV - ScrollPane.this.hScrollBounds.x) / total);
                     }

                     ScrollPane.this.lastPoint.set(x, y);
                  } else if (ScrollPane.this.touchScrollV) {
                     delta = y - ScrollPane.this.lastPoint.y;
                     scrollV = this.handlePosition + delta;
                     this.handlePosition = scrollV;
                     scrollV = Math.max(ScrollPane.this.vScrollBounds.y, scrollV);
                     scrollV = Math.min(ScrollPane.this.vScrollBounds.y + ScrollPane.this.vScrollBounds.height - ScrollPane.this.vKnobBounds.height, scrollV);
                     total = ScrollPane.this.vScrollBounds.height - ScrollPane.this.vKnobBounds.height;
                     if (total != 0.0F) {
                        ScrollPane.this.setScrollPercentY(1.0F - (scrollV - ScrollPane.this.vScrollBounds.y) / total);
                     }

                     ScrollPane.this.lastPoint.set(x, y);
                  }

               }
            }

            public boolean mouseMoved(InputEvent event, float x, float y) {
               if (!ScrollPane.this.flickScroll) {
                  ScrollPane.this.resetFade();
               }

               return false;
            }
         });
         this.flickScrollListener = new ActorGestureListener() {
            public void pan(InputEvent event, float x, float y, float deltaX, float deltaY) {
               ScrollPane.this.resetFade();
               ScrollPane var10000 = ScrollPane.this;
               var10000.amountX -= deltaX;
               var10000 = ScrollPane.this;
               var10000.amountY += deltaY;
               ScrollPane.this.clamp();
               ScrollPane.this.cancelTouchFocusedChild(event);
            }

            public void fling(InputEvent event, float x, float y, int button) {
               if (Math.abs(x) > 150.0F) {
                  ScrollPane.this.flingTimer = ScrollPane.this.flingTime;
                  ScrollPane.this.velocityX = x;
                  ScrollPane.this.cancelTouchFocusedChild(event);
               }

               if (Math.abs(y) > 150.0F) {
                  ScrollPane.this.flingTimer = ScrollPane.this.flingTime;
                  ScrollPane.this.velocityY = -y;
                  ScrollPane.this.cancelTouchFocusedChild(event);
               }

            }

            public boolean handle(Event event) {
               if (super.handle(event)) {
                  if (((InputEvent)event).getType() == InputEvent.Type.touchDown) {
                     ScrollPane.this.flingTimer = 0.0F;
                  }

                  return true;
               } else {
                  return false;
               }
            }
         };
         this.addListener(this.flickScrollListener);
         this.addListener(new InputListener() {
            public boolean scrolled(InputEvent event, float x, float y, int amount) {
               ScrollPane.this.resetFade();
               if (ScrollPane.this.scrollY) {
                  ScrollPane.this.setScrollY(ScrollPane.this.amountY + Math.max(ScrollPane.this.areaHeight * 0.9F, ScrollPane.this.maxY * 0.1F) / 4.0F * (float)amount);
               } else if (ScrollPane.this.scrollX) {
                  ScrollPane.this.setScrollX(ScrollPane.this.amountX + Math.max(ScrollPane.this.areaWidth * 0.9F, ScrollPane.this.maxX * 0.1F) / 4.0F * (float)amount);
               }

               return true;
            }
         });
      }
   }

   void resetFade() {
      this.fadeAlpha = this.fadeAlphaSeconds;
      this.fadeDelay = this.fadeDelaySeconds;
   }

   void cancelTouchFocusedChild(InputEvent event) {
      if (this.cancelTouchFocus) {
         Stage stage = this.getStage();
         if (stage != null) {
            stage.cancelTouchFocus(this.flickScrollListener, this);
         }

      }
   }

   void clamp() {
      if (this.clamp) {
         this.scrollX(this.overscrollX ? MathUtils.clamp(this.amountX, -this.overscrollDistance, this.maxX + this.overscrollDistance) : MathUtils.clamp(this.amountX, 0.0F, this.maxX));
         this.scrollY(this.overscrollY ? MathUtils.clamp(this.amountY, -this.overscrollDistance, this.maxY + this.overscrollDistance) : MathUtils.clamp(this.amountY, 0.0F, this.maxY));
      }
   }

   public void setStyle(ScrollPane.ScrollPaneStyle style) {
      if (style == null) {
         throw new IllegalArgumentException("style cannot be null.");
      } else {
         this.style = style;
         this.invalidateHierarchy();
      }
   }

   public ScrollPane.ScrollPaneStyle getStyle() {
      return this.style;
   }

   public void act(float delta) {
      super.act(delta);
      boolean panning = this.flickScrollListener.getGestureDetector().isPanning();
      if (this.fadeAlpha > 0.0F && this.fadeScrollBars && !panning && !this.touchScrollH && !this.touchScrollV) {
         this.fadeDelay -= delta;
         if (this.fadeDelay <= 0.0F) {
            this.fadeAlpha = Math.max(0.0F, this.fadeAlpha - delta);
         }
      }

      if (this.flingTimer > 0.0F) {
         this.resetFade();
         float alpha = this.flingTimer / this.flingTime;
         this.amountX -= this.velocityX * alpha * delta;
         this.amountY -= this.velocityY * alpha * delta;
         this.clamp();
         if (this.amountX == -this.overscrollDistance) {
            this.velocityX = 0.0F;
         }

         if (this.amountX >= this.maxX + this.overscrollDistance) {
            this.velocityX = 0.0F;
         }

         if (this.amountY == -this.overscrollDistance) {
            this.velocityY = 0.0F;
         }

         if (this.amountY >= this.maxY + this.overscrollDistance) {
            this.velocityY = 0.0F;
         }

         this.flingTimer -= delta;
         if (this.flingTimer <= 0.0F) {
            this.velocityX = 0.0F;
            this.velocityY = 0.0F;
         }
      }

      if (this.smoothScrolling && this.flingTimer <= 0.0F && !this.touchScrollH && !this.touchScrollV && !panning) {
         if (this.visualAmountX != this.amountX) {
            if (this.visualAmountX < this.amountX) {
               this.visualScrollX(Math.min(this.amountX, this.visualAmountX + Math.max(150.0F * delta, (this.amountX - this.visualAmountX) * 5.0F * delta)));
            } else {
               this.visualScrollX(Math.max(this.amountX, this.visualAmountX - Math.max(150.0F * delta, (this.visualAmountX - this.amountX) * 5.0F * delta)));
            }
         }

         if (this.visualAmountY != this.amountY) {
            if (this.visualAmountY < this.amountY) {
               this.visualScrollY(Math.min(this.amountY, this.visualAmountY + Math.max(150.0F * delta, (this.amountY - this.visualAmountY) * 5.0F * delta)));
            } else {
               this.visualScrollY(Math.max(this.amountY, this.visualAmountY - Math.max(150.0F * delta, (this.visualAmountY - this.amountY) * 5.0F * delta)));
            }
         }
      } else {
         if (this.visualAmountX != this.amountX) {
            this.visualScrollX(this.amountX);
         }

         if (this.visualAmountY != this.amountY) {
            this.visualScrollY(this.amountY);
         }
      }

      if (!panning) {
         if (this.overscrollX && this.scrollX) {
            if (this.amountX < 0.0F) {
               this.resetFade();
               this.amountX += (this.overscrollSpeedMin + (this.overscrollSpeedMax - this.overscrollSpeedMin) * -this.amountX / this.overscrollDistance) * delta;
               if (this.amountX > 0.0F) {
                  this.scrollX(0.0F);
               }
            } else if (this.amountX > this.maxX) {
               this.resetFade();
               this.amountX -= (this.overscrollSpeedMin + (this.overscrollSpeedMax - this.overscrollSpeedMin) * -(this.maxX - this.amountX) / this.overscrollDistance) * delta;
               if (this.amountX < this.maxX) {
                  this.scrollX(this.maxX);
               }
            }
         }

         if (this.overscrollY && this.scrollY) {
            if (this.amountY < 0.0F) {
               this.resetFade();
               this.amountY += (this.overscrollSpeedMin + (this.overscrollSpeedMax - this.overscrollSpeedMin) * -this.amountY / this.overscrollDistance) * delta;
               if (this.amountY > 0.0F) {
                  this.scrollY(0.0F);
               }
            } else if (this.amountY > this.maxY) {
               this.resetFade();
               this.amountY -= (this.overscrollSpeedMin + (this.overscrollSpeedMax - this.overscrollSpeedMin) * -(this.maxY - this.amountY) / this.overscrollDistance) * delta;
               if (this.amountY < this.maxY) {
                  this.scrollY(this.maxY);
               }
            }
         }
      }

   }

   public void layout() {
      Drawable bg = this.style.background;
      Drawable hScrollKnob = this.style.hScrollKnob;
      Drawable vScrollKnob = this.style.vScrollKnob;
      float bgLeftWidth = 0.0F;
      float bgRightWidth = 0.0F;
      float bgTopHeight = 0.0F;
      float bgBottomHeight = 0.0F;
      if (bg != null) {
         bgLeftWidth = bg.getLeftWidth();
         bgRightWidth = bg.getRightWidth();
         bgTopHeight = bg.getTopHeight();
         bgBottomHeight = bg.getBottomHeight();
      }

      float width = this.getWidth();
      float height = this.getHeight();
      float scrollbarHeight = 0.0F;
      if (hScrollKnob != null) {
         scrollbarHeight = hScrollKnob.getMinHeight();
      }

      if (this.style.hScroll != null) {
         scrollbarHeight = Math.max(scrollbarHeight, this.style.hScroll.getMinHeight());
      }

      float scrollbarWidth = 0.0F;
      if (vScrollKnob != null) {
         scrollbarWidth = vScrollKnob.getMinWidth();
      }

      if (this.style.vScroll != null) {
         scrollbarWidth = Math.max(scrollbarWidth, this.style.vScroll.getMinWidth());
      }

      this.areaWidth = width - bgLeftWidth - bgRightWidth;
      this.areaHeight = height - bgTopHeight - bgBottomHeight;
      if (this.widget != null) {
         float widgetWidth;
         float widgetHeight;
         if (this.widget instanceof Layout) {
            Layout layout = (Layout)this.widget;
            widgetWidth = layout.getPrefWidth();
            widgetHeight = layout.getPrefHeight();
         } else {
            widgetWidth = this.widget.getWidth();
            widgetHeight = this.widget.getHeight();
         }

         this.scrollX = this.forceScrollX || widgetWidth > this.areaWidth && !this.disableX;
         this.scrollY = this.forceScrollY || widgetHeight > this.areaHeight && !this.disableY;
         boolean fade = this.fadeScrollBars;
         if (!fade) {
            if (this.scrollY) {
               this.areaWidth -= scrollbarWidth;
               if (!this.scrollX && widgetWidth > this.areaWidth && !this.disableX) {
                  this.scrollX = true;
               }
            }

            if (this.scrollX) {
               this.areaHeight -= scrollbarHeight;
               if (!this.scrollY && widgetHeight > this.areaHeight && !this.disableY) {
                  this.scrollY = true;
                  this.areaWidth -= scrollbarWidth;
               }
            }
         }

         this.widgetAreaBounds.set(bgLeftWidth, bgBottomHeight, this.areaWidth, this.areaHeight);
         if (fade) {
            if (this.scrollX) {
               this.areaHeight -= scrollbarHeight;
            }

            if (this.scrollY) {
               this.areaWidth -= scrollbarWidth;
            }
         } else {
            Rectangle var10000;
            if (this.scrollbarsOnTop) {
               if (this.scrollX) {
                  var10000 = this.widgetAreaBounds;
                  var10000.height += scrollbarHeight;
               }

               if (this.scrollY) {
                  var10000 = this.widgetAreaBounds;
                  var10000.width += scrollbarWidth;
               }
            } else if (this.scrollX) {
               var10000 = this.widgetAreaBounds;
               var10000.y += scrollbarHeight;
            }
         }

         widgetWidth = this.disableX ? width : Math.max(this.areaWidth, widgetWidth);
         widgetHeight = this.disableY ? height : Math.max(this.areaHeight, widgetHeight);
         this.maxX = widgetWidth - this.areaWidth;
         this.maxY = widgetHeight - this.areaHeight;
         if (fade) {
            if (this.scrollX) {
               this.maxY -= scrollbarHeight;
            }

            if (this.scrollY) {
               this.maxX -= scrollbarWidth;
            }
         }

         this.scrollX(MathUtils.clamp(this.amountX, 0.0F, this.maxX));
         this.scrollY(MathUtils.clamp(this.amountY, 0.0F, this.maxY));
         float vScrollWidth;
         if (this.scrollX) {
            if (hScrollKnob != null) {
               vScrollWidth = this.style.hScroll != null ? this.style.hScroll.getMinHeight() : hScrollKnob.getMinHeight();
               this.hScrollBounds.set(bgLeftWidth, bgBottomHeight, this.areaWidth, vScrollWidth);
               this.hKnobBounds.width = Math.max(hScrollKnob.getMinWidth(), (float)((int)(this.hScrollBounds.width * this.areaWidth / widgetWidth)));
               this.hKnobBounds.height = hScrollKnob.getMinHeight();
               this.hKnobBounds.x = this.hScrollBounds.x + (float)((int)((this.hScrollBounds.width - this.hKnobBounds.width) * this.getScrollPercentX()));
               this.hKnobBounds.y = this.hScrollBounds.y;
            } else {
               this.hScrollBounds.set(0.0F, 0.0F, 0.0F, 0.0F);
               this.hKnobBounds.set(0.0F, 0.0F, 0.0F, 0.0F);
            }
         }

         if (this.scrollY) {
            if (vScrollKnob != null) {
               vScrollWidth = this.style.vScroll != null ? this.style.vScroll.getMinWidth() : vScrollKnob.getMinWidth();
               this.vScrollBounds.set(width - bgRightWidth - vScrollWidth, height - bgTopHeight - this.areaHeight, vScrollWidth, this.areaHeight);
               this.vKnobBounds.width = vScrollKnob.getMinWidth();
               this.vKnobBounds.height = Math.max(vScrollKnob.getMinHeight(), (float)((int)(this.vScrollBounds.height * this.areaHeight / widgetHeight)));
               this.vKnobBounds.x = width - bgRightWidth - vScrollKnob.getMinWidth();
               this.vKnobBounds.y = this.vScrollBounds.y + (float)((int)((this.vScrollBounds.height - this.vKnobBounds.height) * (1.0F - this.getScrollPercentY())));
            } else {
               this.vScrollBounds.set(0.0F, 0.0F, 0.0F, 0.0F);
               this.vKnobBounds.set(0.0F, 0.0F, 0.0F, 0.0F);
            }
         }

         if (this.widget.getWidth() == widgetWidth && this.widget.getHeight() == widgetHeight) {
            if (this.widget instanceof Layout) {
               ((Layout)this.widget).validate();
            }
         } else {
            this.widget.setWidth(widgetWidth);
            this.widget.setHeight(widgetHeight);
            if (this.widget instanceof Layout) {
               Layout layout = (Layout)this.widget;
               layout.invalidate();
               layout.validate();
            }
         }

      }
   }

   public void draw(SpriteBatch batch, float parentAlpha) {
      if (this.widget != null) {
         this.validate();
         this.applyTransform(batch, this.computeTransform());
         if (this.scrollX) {
            this.hKnobBounds.x = this.hScrollBounds.x + (float)((int)((this.hScrollBounds.width - this.hKnobBounds.width) * this.getScrollPercentX()));
         }

         if (this.scrollY) {
            this.vKnobBounds.y = this.vScrollBounds.y + (float)((int)((this.vScrollBounds.height - this.vKnobBounds.height) * (1.0F - this.getScrollPercentY())));
         }

         float y = this.widgetAreaBounds.y;
         if (!this.scrollY) {
            y -= (float)((int)this.maxY);
         } else {
            y -= (float)((int)(this.maxY - this.visualAmountY));
         }

         float scrollbarHeight;
         if (!this.fadeScrollBars && this.scrollbarsOnTop && this.scrollX) {
            scrollbarHeight = 0.0F;
            if (this.style.hScrollKnob != null) {
               scrollbarHeight = this.style.hScrollKnob.getMinHeight();
            }

            if (this.style.hScroll != null) {
               scrollbarHeight = Math.max(scrollbarHeight, this.style.hScroll.getMinHeight());
            }

            y += scrollbarHeight;
         }

         scrollbarHeight = this.widgetAreaBounds.x;
         if (this.scrollX) {
            scrollbarHeight -= (float)((int)this.visualAmountX);
         }

         this.widget.setPosition(scrollbarHeight, y);
         if (this.widget instanceof Cullable) {
            this.widgetCullingArea.x = -this.widget.getX() + this.widgetAreaBounds.x;
            this.widgetCullingArea.y = -this.widget.getY() + this.widgetAreaBounds.y;
            this.widgetCullingArea.width = this.widgetAreaBounds.width;
            this.widgetCullingArea.height = this.widgetAreaBounds.height;
            ((Cullable)this.widget).setCullingArea(this.widgetCullingArea);
         }

         ScissorStack.calculateScissors(this.getStage().getCamera(), batch.getTransformMatrix(), this.widgetAreaBounds, this.scissorBounds);
         Color color = this.getColor();
         batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
         if (this.style.background != null) {
            this.style.background.draw(batch, 0.0F, 0.0F, this.getWidth(), this.getHeight());
         }

         batch.flush();
         if (ScissorStack.pushScissors(this.scissorBounds)) {
            this.drawChildren(batch, parentAlpha);
            ScissorStack.popScissors();
         }

         batch.setColor(color.r, color.g, color.b, color.a * parentAlpha * Interpolation.fade.apply(this.fadeAlpha / this.fadeAlphaSeconds));
         if (this.scrollX && this.scrollY && this.style.corner != null) {
            this.style.corner.draw(batch, this.hScrollBounds.x + this.hScrollBounds.width, this.hScrollBounds.y, this.vScrollBounds.width, this.vScrollBounds.y);
         }

         if (this.scrollX) {
            if (this.style.hScroll != null) {
               this.style.hScroll.draw(batch, this.hScrollBounds.x, this.hScrollBounds.y, this.hScrollBounds.width, this.hScrollBounds.height);
            }

            if (this.style.hScrollKnob != null) {
               this.style.hScrollKnob.draw(batch, this.hKnobBounds.x, this.hKnobBounds.y, this.hKnobBounds.width, this.hKnobBounds.height);
            }
         }

         if (this.scrollY) {
            if (this.style.vScroll != null) {
               this.style.vScroll.draw(batch, this.vScrollBounds.x, this.vScrollBounds.y, this.vScrollBounds.width, this.vScrollBounds.height);
            }

            if (this.style.vScrollKnob != null) {
               this.style.vScrollKnob.draw(batch, this.vKnobBounds.x, this.vKnobBounds.y, this.vKnobBounds.width, this.vKnobBounds.height);
            }
         }

         this.resetTransform(batch);
      }
   }

   public float getPrefWidth() {
      if (this.widget instanceof Layout) {
         float width = ((Layout)this.widget).getPrefWidth();
         if (this.style.background != null) {
            width += this.style.background.getLeftWidth() + this.style.background.getRightWidth();
         }

         return width;
      } else {
         return 150.0F;
      }
   }

   public float getPrefHeight() {
      if (this.widget instanceof Layout) {
         float height = ((Layout)this.widget).getPrefHeight();
         if (this.style.background != null) {
            height += this.style.background.getTopHeight() + this.style.background.getBottomHeight();
         }

         return height;
      } else {
         return 150.0F;
      }
   }

   public float getMinWidth() {
      return 0.0F;
   }

   public float getMinHeight() {
      return 0.0F;
   }

   public void setWidget(Actor widget) {
      if (widget == this) {
         throw new IllegalArgumentException("widget cannot be same object");
      } else {
         if (this.widget != null) {
            super.removeActor(this.widget);
         }

         this.widget = widget;
         if (widget != null) {
            super.addActor(widget);
         }

      }
   }

   public Actor getWidget() {
      return this.widget;
   }

   /** @deprecated */
   public void addActor(Actor actor) {
      throw new UnsupportedOperationException("Use ScrollPane#setWidget.");
   }

   /** @deprecated */
   public void addActorAt(int index, Actor actor) {
      throw new UnsupportedOperationException("Use ScrollPane#setWidget.");
   }

   /** @deprecated */
   public void addActorBefore(Actor actorBefore, Actor actor) {
      throw new UnsupportedOperationException("Use ScrollPane#setWidget.");
   }

   /** @deprecated */
   public void addActorAfter(Actor actorAfter, Actor actor) {
      throw new UnsupportedOperationException("Use ScrollPane#setWidget.");
   }

   public boolean removeActor(Actor actor) {
      if (actor != this.widget) {
         return false;
      } else {
         this.setWidget((Actor)null);
         return true;
      }
   }

   public Actor hit(float x, float y, boolean touchable) {
      if (!(x < 0.0F) && !(x >= this.getWidth()) && !(y < 0.0F) && !(y >= this.getHeight())) {
         if (this.scrollX && this.hScrollBounds.contains(x, y)) {
            return this;
         } else {
            return (Actor)(this.scrollY && this.vScrollBounds.contains(x, y) ? this : super.hit(x, y, touchable));
         }
      } else {
         return null;
      }
   }

   protected void scrollX(float pixelsX) {
      this.amountX = pixelsX;
   }

   protected void scrollY(float pixelsY) {
      this.amountY = pixelsY;
   }

   protected void visualScrollX(float pixelsX) {
      this.visualAmountX = pixelsX;
   }

   protected void visualScrollY(float pixelsY) {
      this.visualAmountY = pixelsY;
   }

   public void setScrollX(float pixels) {
      this.scrollX(MathUtils.clamp(pixels, 0.0F, this.maxX));
   }

   public float getScrollX() {
      return this.amountX;
   }

   public void setScrollY(float pixels) {
      this.scrollY(MathUtils.clamp(pixels, 0.0F, this.maxY));
   }

   public float getScrollY() {
      return this.amountY;
   }

   public void updateVisualScroll() {
      this.visualAmountX = this.amountX;
      this.visualAmountY = this.amountY;
   }

   public float getVisualScrollX() {
      return !this.scrollX ? 0.0F : this.visualAmountX;
   }

   public float getVisualScrollY() {
      return !this.scrollY ? 0.0F : this.visualAmountY;
   }

   public float getScrollPercentX() {
      return MathUtils.clamp(this.amountX / this.maxX, 0.0F, 1.0F);
   }

   public void setScrollPercentX(float percentX) {
      this.scrollX(this.maxX * MathUtils.clamp(percentX, 0.0F, 1.0F));
   }

   public float getScrollPercentY() {
      return MathUtils.clamp(this.amountY / this.maxY, 0.0F, 1.0F);
   }

   public void setScrollPercentY(float percentY) {
      this.scrollY(this.maxY * MathUtils.clamp(percentY, 0.0F, 1.0F));
   }

   public void setFlickScroll(boolean flickScroll) {
      if (this.flickScroll != flickScroll) {
         this.flickScroll = flickScroll;
         if (flickScroll) {
            this.addListener(this.flickScrollListener);
         } else {
            this.removeListener(this.flickScrollListener);
         }

         this.invalidate();
      }
   }

   public void scrollTo(float x, float y, float width, float height) {
      float amountX = this.amountX;
      if (x + width > amountX + this.areaWidth) {
         amountX = x + width - this.areaWidth;
      }

      if (x < amountX) {
         amountX = x;
      }

      this.scrollX(MathUtils.clamp(amountX, 0.0F, this.maxX));
      float amountY = this.amountY;
      if (amountY > this.maxY - y - height + this.areaHeight) {
         amountY = this.maxY - y - height + this.areaHeight;
      }

      if (amountY < this.maxY - y) {
         amountY = this.maxY - y;
      }

      this.scrollY(MathUtils.clamp(amountY, 0.0F, this.maxY));
   }

   public void scrollToCenter(float x, float y, float width, float height) {
      float amountX = this.amountX;
      if (x + width > amountX + this.areaWidth) {
         amountX = x + width - this.areaWidth;
      }

      if (x < amountX) {
         amountX = x;
      }

      this.scrollX(MathUtils.clamp(amountX, 0.0F, this.maxX));
      float amountY = this.amountY;
      float centerY = this.maxY - y + this.areaHeight / 2.0F - height / 2.0F;
      if (amountY < centerY - this.areaHeight / 4.0F || amountY > centerY + this.areaHeight / 4.0F) {
         amountY = centerY;
      }

      this.scrollY(MathUtils.clamp(amountY, 0.0F, this.maxY));
   }

   public float getMaxX() {
      return this.maxX;
   }

   public float getMaxY() {
      return this.maxY;
   }

   public float getScrollBarHeight() {
      return this.style.hScrollKnob != null && this.scrollX ? this.style.hScrollKnob.getMinHeight() : 0.0F;
   }

   public float getScrollBarWidth() {
      return this.style.vScrollKnob != null && this.scrollY ? this.style.vScrollKnob.getMinWidth() : 0.0F;
   }

   public boolean isScrollX() {
      return this.scrollX;
   }

   public boolean isScrollY() {
      return this.scrollY;
   }

   public void setScrollingDisabled(boolean x, boolean y) {
      this.disableX = x;
      this.disableY = y;
   }

   public boolean isDragging() {
      return this.draggingPointer != -1;
   }

   public boolean isPanning() {
      return this.flickScrollListener.getGestureDetector().isPanning();
   }

   public boolean isFlinging() {
      return this.flingTimer > 0.0F;
   }

   public void setVelocityX(float velocityX) {
      this.velocityX = velocityX;
   }

   public float getVelocityX() {
      if (this.flingTimer <= 0.0F) {
         return 0.0F;
      } else {
         float alpha = this.flingTimer / this.flingTime;
         alpha = alpha * alpha * alpha;
         return this.velocityX * alpha * alpha * alpha;
      }
   }

   public void setVelocityY(float velocityY) {
      this.velocityY = velocityY;
   }

   public float getVelocityY() {
      return this.velocityY;
   }

   public void setOverscroll(boolean overscrollX, boolean overscrollY) {
      this.overscrollX = overscrollX;
      this.overscrollY = overscrollY;
   }

   public void setupOverscroll(float distance, float speedMin, float speedMax) {
      this.overscrollDistance = distance;
      this.overscrollSpeedMin = speedMin;
      this.overscrollSpeedMax = speedMax;
   }

   public void setForceScroll(boolean x, boolean y) {
      this.forceScrollX = x;
      this.forceScrollY = y;
   }

   public boolean isForceScrollX() {
      return this.forceScrollX;
   }

   public boolean isForceScrollY() {
      return this.forceScrollY;
   }

   public void setFlingTime(float flingTime) {
      this.flingTime = flingTime;
   }

   public void setClamp(boolean clamp) {
      this.clamp = clamp;
   }

   public void setFadeScrollBars(boolean fadeScrollBars) {
      if (this.fadeScrollBars != fadeScrollBars) {
         this.fadeScrollBars = fadeScrollBars;
         if (!fadeScrollBars) {
            this.fadeAlpha = this.fadeAlphaSeconds;
         }

         this.invalidate();
      }
   }

   public void setupFadeScrollBars(float fadeAlphaSeconds, float fadeDelaySeconds) {
      this.fadeAlphaSeconds = fadeAlphaSeconds;
      this.fadeDelaySeconds = fadeDelaySeconds;
   }

   public void setSmoothScrolling(boolean smoothScrolling) {
      this.smoothScrolling = smoothScrolling;
   }

   public void setScrollbarsOnTop(boolean scrollbarsOnTop) {
      this.scrollbarsOnTop = scrollbarsOnTop;
      this.invalidate();
   }

   public void setCancelTouchFocus(boolean cancelTouchFocus) {
      this.cancelTouchFocus = cancelTouchFocus;
   }

   public static class ScrollPaneStyle {
      public Drawable background;
      public Drawable corner;
      public Drawable hScroll;
      public Drawable hScrollKnob;
      public Drawable vScroll;
      public Drawable vScrollKnob;

      public ScrollPaneStyle() {
      }

      public ScrollPaneStyle(Drawable background, Drawable hScroll, Drawable hScrollKnob, Drawable vScroll, Drawable vScrollKnob) {
         this.background = background;
         this.hScroll = hScroll;
         this.hScrollKnob = hScrollKnob;
         this.vScroll = vScroll;
         this.vScrollKnob = vScrollKnob;
      }

      public ScrollPaneStyle(ScrollPane.ScrollPaneStyle style) {
         this.background = style.background;
         this.hScroll = style.hScroll;
         this.hScrollKnob = style.hScrollKnob;
         this.vScroll = style.vScroll;
         this.vScrollKnob = style.vScrollKnob;
      }
   }
}
