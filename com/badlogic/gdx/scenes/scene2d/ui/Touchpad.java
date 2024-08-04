package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Pools;

public class Touchpad extends Widget {
   private Touchpad.TouchpadStyle style;
   boolean touched;
   private float deadzoneRadius;
   private final Circle knobBounds;
   private final Circle touchBounds;
   private final Circle deadzoneBounds;
   private final Vector2 knobPosition;
   private final Vector2 knobPercent;

   public Touchpad(float deadzoneRadius, Skin skin) {
      this(deadzoneRadius, (Touchpad.TouchpadStyle)skin.get(Touchpad.TouchpadStyle.class));
   }

   public Touchpad(float deadzoneRadius, Skin skin, String styleName) {
      this(deadzoneRadius, (Touchpad.TouchpadStyle)skin.get(styleName, Touchpad.TouchpadStyle.class));
   }

   public Touchpad(float deadzoneRadius, Touchpad.TouchpadStyle style) {
      this.knobBounds = new Circle(0.0F, 0.0F, 0.0F);
      this.touchBounds = new Circle(0.0F, 0.0F, 0.0F);
      this.deadzoneBounds = new Circle(0.0F, 0.0F, 0.0F);
      this.knobPosition = new Vector2();
      this.knobPercent = new Vector2();
      if (deadzoneRadius < 0.0F) {
         throw new IllegalArgumentException("deadzoneRadius must be > 0");
      } else {
         this.deadzoneRadius = deadzoneRadius;
         this.knobPosition.set(this.getWidth() / 2.0F, this.getHeight() / 2.0F);
         this.setStyle(style);
         this.setWidth(this.getPrefWidth());
         this.setHeight(this.getPrefHeight());
         this.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
               if (Touchpad.this.touched) {
                  return false;
               } else {
                  Touchpad.this.touched = true;
                  Touchpad.this.calculatePositionAndValue(x, y, false);
                  return true;
               }
            }

            public void touchDragged(InputEvent event, float x, float y, int pointer) {
               Touchpad.this.calculatePositionAndValue(x, y, false);
            }

            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
               Touchpad.this.touched = false;
               Touchpad.this.calculatePositionAndValue(x, y, true);
            }
         });
      }
   }

   void calculatePositionAndValue(float x, float y, boolean isTouchUp) {
      float oldPositionX = this.knobPosition.x;
      float oldPositionY = this.knobPosition.y;
      float oldPercentX = this.knobPercent.x;
      float oldPercentY = this.knobPercent.y;
      float centerX = this.knobBounds.x;
      float centerY = this.knobBounds.y;
      this.knobPosition.set(centerX, centerY);
      this.knobPercent.set(0.0F, 0.0F);
      if (!isTouchUp && !this.deadzoneBounds.contains(x, y)) {
         this.knobPercent.set((x - centerX) / this.knobBounds.radius, (y - centerY) / this.knobBounds.radius);
         float length = this.knobPercent.len();
         if (length > 1.0F) {
            this.knobPercent.scl(1.0F / length);
         }

         if (this.knobBounds.contains(x, y)) {
            this.knobPosition.set(x, y);
         } else {
            this.knobPosition.set(this.knobPercent).nor().scl(this.knobBounds.radius).add(this.knobBounds.x, this.knobBounds.y);
         }
      }

      if (oldPercentX != this.knobPercent.x || oldPercentY != this.knobPercent.y) {
         ChangeListener.ChangeEvent changeEvent = (ChangeListener.ChangeEvent)Pools.obtain(ChangeListener.ChangeEvent.class);
         if (this.fire(changeEvent)) {
            this.knobPercent.set(oldPercentX, oldPercentY);
            this.knobPosition.set(oldPositionX, oldPositionY);
         }

         Pools.free(changeEvent);
      }

   }

   public void setStyle(Touchpad.TouchpadStyle style) {
      if (style == null) {
         throw new IllegalArgumentException("style cannot be null");
      } else {
         this.style = style;
         this.invalidateHierarchy();
      }
   }

   public Touchpad.TouchpadStyle getStyle() {
      return this.style;
   }

   public Actor hit(float x, float y, boolean touchable) {
      return this.touchBounds.contains(x, y) ? this : null;
   }

   public void layout() {
      float halfWidth = this.getWidth() / 2.0F;
      float halfHeight = this.getHeight() / 2.0F;
      float radius = Math.min(halfWidth, halfHeight);
      this.touchBounds.set(halfWidth, halfHeight, radius);
      if (this.style.knob != null) {
         radius -= Math.max(this.style.knob.getMinWidth(), this.style.knob.getMinHeight()) / 2.0F;
      }

      this.knobBounds.set(halfWidth, halfHeight, radius);
      this.deadzoneBounds.set(halfWidth, halfHeight, this.deadzoneRadius);
      this.knobPosition.set(halfWidth, halfHeight);
      this.knobPercent.set(0.0F, 0.0F);
   }

   public void draw(SpriteBatch batch, float parentAlpha) {
      this.validate();
      Color c = this.getColor();
      batch.setColor(c.r, c.g, c.b, c.a * parentAlpha);
      float x = this.getX();
      float y = this.getY();
      float w = this.getWidth();
      float h = this.getHeight();
      Drawable bg = this.style.background;
      if (bg != null) {
         bg.draw(batch, x, y, w, h);
      }

      Drawable knob = this.style.knob;
      if (knob != null) {
         x += this.knobPosition.x - knob.getMinWidth() / 2.0F;
         y += this.knobPosition.y - knob.getMinHeight() / 2.0F;
         knob.draw(batch, x, y, knob.getMinWidth(), knob.getMinHeight());
      }

   }

   public float getPrefWidth() {
      return this.style.background != null ? this.style.background.getMinWidth() : 0.0F;
   }

   public float getPrefHeight() {
      return this.style.background != null ? this.style.background.getMinHeight() : 0.0F;
   }

   public boolean isTouched() {
      return this.touched;
   }

   public void setDeadzone(float deadzoneRadius) {
      if (deadzoneRadius < 0.0F) {
         throw new IllegalArgumentException("deadzoneRadius must be > 0");
      } else {
         this.deadzoneRadius = deadzoneRadius;
         this.invalidate();
      }
   }

   public float getKnobX() {
      return this.knobPosition.x;
   }

   public float getKnobY() {
      return this.knobPosition.y;
   }

   public float getKnobPercentX() {
      return this.knobPercent.x;
   }

   public float getKnobPercentY() {
      return this.knobPercent.y;
   }

   public static class TouchpadStyle {
      public Drawable background;
      public Drawable knob;

      public TouchpadStyle() {
      }

      public TouchpadStyle(Drawable background, Drawable knob) {
         this.background = background;
         this.knob = knob;
      }

      public TouchpadStyle(Touchpad.TouchpadStyle style) {
         this.background = style.background;
         this.knob = style.knob;
      }
   }
}
