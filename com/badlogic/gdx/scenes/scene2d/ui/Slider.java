package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Pools;

public class Slider extends Widget {
   private Slider.SliderStyle style;
   private float min;
   private float max;
   private float stepSize;
   private float value;
   private float animateFromValue;
   private float sliderPos;
   private final boolean vertical;
   int draggingPointer;
   private float animateDuration;
   private float animateTime;
   private Interpolation animateInterpolation;
   private float[] snapValues;
   private float threshold;

   public Slider(float min, float max, float stepSize, boolean vertical, Skin skin) {
      this(min, max, stepSize, vertical, (Slider.SliderStyle)skin.get("default-" + (vertical ? "vertical" : "horizontal"), Slider.SliderStyle.class));
   }

   public Slider(float min, float max, float stepSize, boolean vertical, Skin skin, String styleName) {
      this(min, max, stepSize, vertical, (Slider.SliderStyle)skin.get(styleName, Slider.SliderStyle.class));
   }

   public Slider(float min, float max, float stepSize, boolean vertical, Slider.SliderStyle style) {
      this.draggingPointer = -1;
      this.animateInterpolation = Interpolation.linear;
      if (min > max) {
         throw new IllegalArgumentException("min must be > max: " + min + " > " + max);
      } else if (stepSize <= 0.0F) {
         throw new IllegalArgumentException("stepSize must be > 0: " + stepSize);
      } else {
         this.setStyle(style);
         this.min = min;
         this.max = max;
         this.stepSize = stepSize;
         this.vertical = vertical;
         this.value = min;
         this.setWidth(this.getPrefWidth());
         this.setHeight(this.getPrefHeight());
         this.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
               if (Slider.this.draggingPointer != -1) {
                  return false;
               } else {
                  Slider.this.draggingPointer = pointer;
                  Slider.this.calculatePositionAndValue(x, y);
                  return true;
               }
            }

            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
               if (pointer == Slider.this.draggingPointer) {
                  Slider.this.draggingPointer = -1;
                  if (!Slider.this.calculatePositionAndValue(x, y)) {
                     ChangeListener.ChangeEvent changeEvent = (ChangeListener.ChangeEvent)Pools.obtain(ChangeListener.ChangeEvent.class);
                     Slider.this.fire(changeEvent);
                     Pools.free(changeEvent);
                  }

               }
            }

            public void touchDragged(InputEvent event, float x, float y, int pointer) {
               Slider.this.calculatePositionAndValue(x, y);
            }
         });
      }
   }

   public void setStyle(Slider.SliderStyle style) {
      if (style == null) {
         throw new IllegalArgumentException("style cannot be null.");
      } else {
         this.style = style;
         this.invalidateHierarchy();
      }
   }

   public Slider.SliderStyle getStyle() {
      return this.style;
   }

   public void act(float delta) {
      super.act(delta);
      this.animateTime -= delta;
   }

   public void draw(SpriteBatch batch, float parentAlpha) {
      Drawable knob = this.style.knob;
      Drawable bg = this.style.background;
      Drawable knobBefore = this.style.knobBefore;
      Drawable knobAfter = this.style.knobAfter;
      Color color = this.getColor();
      float x = this.getX();
      float y = this.getY();
      float width = this.getWidth();
      float height = this.getHeight();
      float knobHeight = knob == null ? 0.0F : knob.getMinHeight();
      float knobWidth = knob == null ? 0.0F : knob.getMinWidth();
      float value = this.getVisualValue();
      batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
      float sliderPosHeight;
      float knobHeightHalf;
      if (this.vertical) {
         bg.draw(batch, x + (float)((int)((width - bg.getMinWidth()) * 0.5F)), y, bg.getMinWidth(), height);
         sliderPosHeight = height - (bg.getTopHeight() + bg.getBottomHeight());
         if (this.min != this.max) {
            this.sliderPos = (value - this.min) / (this.max - this.min) * (sliderPosHeight - knobHeight);
            this.sliderPos = Math.max(0.0F, this.sliderPos);
            this.sliderPos = Math.min(sliderPosHeight - knobHeight, this.sliderPos) + bg.getBottomHeight();
         }

         knobHeightHalf = knobHeight * 0.5F;
         if (knobBefore != null) {
            knobBefore.draw(batch, x + (float)((int)((width - knobBefore.getMinWidth()) * 0.5F)), y, knobBefore.getMinWidth(), (float)((int)(this.sliderPos + knobHeightHalf)));
         }

         if (knobAfter != null) {
            knobAfter.draw(batch, x + (float)((int)((width - knobAfter.getMinWidth()) * 0.5F)), y + (float)((int)(this.sliderPos + knobHeightHalf)), knobAfter.getMinWidth(), height - (float)((int)(this.sliderPos + knobHeightHalf)));
         }

         if (knob != null) {
            knob.draw(batch, x + (float)((int)((width - knobWidth) * 0.5F)), (float)((int)(y + this.sliderPos)), knobWidth, knobHeight);
         }
      } else {
         bg.draw(batch, x, y + (float)((int)((height - bg.getMinHeight()) * 0.5F)), width, bg.getMinHeight());
         sliderPosHeight = width - (bg.getLeftWidth() + bg.getRightWidth());
         if (this.min != this.max) {
            this.sliderPos = (value - this.min) / (this.max - this.min) * (sliderPosHeight - knobWidth);
            this.sliderPos = Math.max(0.0F, this.sliderPos);
            this.sliderPos = Math.min(sliderPosHeight - knobWidth, this.sliderPos) + bg.getLeftWidth();
         }

         knobHeightHalf = knobHeight * 0.5F;
         if (knobBefore != null) {
            knobBefore.draw(batch, x, y + (float)((int)((height - knobBefore.getMinHeight()) * 0.5F)), (float)((int)(this.sliderPos + knobHeightHalf)), knobBefore.getMinHeight());
         }

         if (knobAfter != null) {
            knobAfter.draw(batch, x + (float)((int)(this.sliderPos + knobHeightHalf)), y + (float)((int)((height - knobAfter.getMinHeight()) * 0.5F)), width - (float)((int)(this.sliderPos + knobHeightHalf)), knobAfter.getMinHeight());
         }

         if (knob != null) {
            knob.draw(batch, (float)((int)(x + this.sliderPos)), (float)((int)(y + (height - knobHeight) * 0.5F)), knobWidth, knobHeight);
         }
      }

   }

   boolean calculatePositionAndValue(float x, float y) {
      Drawable knob = this.style.knob;
      Drawable bg = this.style.background;
      float oldPosition = this.sliderPos;
      float value;
      float height;
      float knobHeight;
      if (this.vertical) {
         height = this.getHeight() - bg.getTopHeight() - bg.getBottomHeight();
         knobHeight = knob == null ? 0.0F : knob.getMinHeight();
         this.sliderPos = y - bg.getBottomHeight() - knobHeight * 0.5F;
         value = this.min + (this.max - this.min) * (this.sliderPos / (height - knobHeight));
         this.sliderPos = Math.max(0.0F, this.sliderPos);
         this.sliderPos = Math.min(height - knobHeight, this.sliderPos);
      } else {
         height = this.getWidth() - bg.getLeftWidth() - bg.getRightWidth();
         knobHeight = knob == null ? 0.0F : knob.getMinWidth();
         this.sliderPos = x - bg.getLeftWidth() - knobHeight * 0.5F;
         value = this.min + (this.max - this.min) * (this.sliderPos / (height - knobHeight));
         this.sliderPos = Math.max(0.0F, this.sliderPos);
         this.sliderPos = Math.min(height - knobHeight, this.sliderPos);
      }

      boolean valueSet = this.setValue(value);
      if (value == value) {
         this.sliderPos = oldPosition;
      }

      return valueSet;
   }

   public boolean isDragging() {
      return this.draggingPointer != -1;
   }

   public float getValue() {
      return this.value;
   }

   public float getVisualValue() {
      return this.animateTime > 0.0F ? this.animateInterpolation.apply(this.animateFromValue, this.value, 1.0F - this.animateTime / this.animateDuration) : this.value;
   }

   public boolean setValue(float value) {
      value = this.snap(this.clamp((float)Math.round(value / this.stepSize) * this.stepSize));
      float oldValue = this.value;
      if (value == oldValue) {
         return false;
      } else {
         float oldVisualValue = this.getVisualValue();
         this.value = value;
         ChangeListener.ChangeEvent changeEvent = (ChangeListener.ChangeEvent)Pools.obtain(ChangeListener.ChangeEvent.class);
         boolean cancelled = this.fire(changeEvent);
         if (cancelled) {
            this.value = oldValue;
         } else if (this.animateDuration > 0.0F) {
            this.animateFromValue = oldVisualValue;
            this.animateTime = this.animateDuration;
         }

         Pools.free(changeEvent);
         return !cancelled;
      }
   }

   protected float clamp(float value) {
      return MathUtils.clamp(value, this.min, this.max);
   }

   public void setRange(float min, float max) {
      if (min > max) {
         throw new IllegalArgumentException("min must be <= max");
      } else {
         this.min = min;
         this.max = max;
         if (this.value < min) {
            this.setValue(min);
         } else if (this.value > max) {
            this.setValue(max);
         }

      }
   }

   public void setStepSize(float stepSize) {
      if (stepSize <= 0.0F) {
         throw new IllegalArgumentException("steps must be > 0: " + stepSize);
      } else {
         this.stepSize = stepSize;
      }
   }

   public float getPrefWidth() {
      return this.vertical ? Math.max(this.style.knob == null ? 0.0F : this.style.knob.getMinWidth(), this.style.background.getMinWidth()) : 140.0F;
   }

   public float getPrefHeight() {
      return this.vertical ? 140.0F : Math.max(this.style.knob == null ? 0.0F : this.style.knob.getMinHeight(), this.style.background.getMinHeight());
   }

   public float getMinValue() {
      return this.min;
   }

   public float getMaxValue() {
      return this.max;
   }

   public float getStepSize() {
      return this.stepSize;
   }

   public void setAnimateDuration(float duration) {
      this.animateDuration = duration;
   }

   public void setAnimateInterpolation(Interpolation animateInterpolation) {
      if (animateInterpolation == null) {
         throw new IllegalArgumentException("animateInterpolation cannot be null.");
      } else {
         this.animateInterpolation = animateInterpolation;
      }
   }

   public void setSnapToValues(float[] values, float threshold) {
      this.snapValues = values;
      this.threshold = threshold;
   }

   private float snap(float value) {
      if (this.snapValues == null) {
         return value;
      } else {
         for(int i = 0; i < this.snapValues.length; ++i) {
            if (Math.abs(value - this.snapValues[i]) <= this.threshold) {
               return this.snapValues[i];
            }
         }

         return value;
      }
   }

   public static class SliderStyle {
      public Drawable background;
      public Drawable knob;
      public Drawable knobBefore;
      public Drawable knobAfter;

      public SliderStyle() {
      }

      public SliderStyle(Drawable background, Drawable knob) {
         this.background = background;
         this.knob = knob;
      }

      public SliderStyle(Slider.SliderStyle style) {
         this.background = style.background;
         this.knob = style.knob;
      }
   }
}
