package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class Window extends Table {
   private static final Vector2 tmpPosition = new Vector2();
   private static final Vector2 tmpSize = new Vector2();
   private Window.WindowStyle style;
   private String title;
   private BitmapFontCache titleCache;
   boolean isMovable;
   boolean isModal;
   final Vector2 dragOffset;
   boolean dragging;
   private int titleAlignment;
   boolean keepWithinStage;

   public Window(String title, Skin skin) {
      this(title, (Window.WindowStyle)skin.get(Window.WindowStyle.class));
      this.setSkin(skin);
   }

   public Window(String title, Skin skin, String styleName) {
      this(title, (Window.WindowStyle)skin.get(styleName, Window.WindowStyle.class));
      this.setSkin(skin);
   }

   public Window(String title, Window.WindowStyle style) {
      this.isMovable = true;
      this.dragOffset = new Vector2();
      this.titleAlignment = 1;
      this.keepWithinStage = true;
      if (title == null) {
         throw new IllegalArgumentException("title cannot be null.");
      } else {
         this.title = title;
         this.setTouchable(Touchable.enabled);
         this.setClip(true);
         this.setStyle(style);
         this.setWidth(150.0F);
         this.setHeight(150.0F);
         this.setTitle(title);
         this.addCaptureListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
               Window.this.toFront();
               return false;
            }
         });
         this.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
               if (button == 0) {
                  Window.this.dragging = Window.this.isMovable && Window.this.getHeight() - y <= Window.this.getPadTop() && y < Window.this.getHeight() && x > 0.0F && x < Window.this.getWidth();
                  Window.this.dragOffset.set(x, y);
               }

               return Window.this.dragging || Window.this.isModal;
            }

            public void touchDragged(InputEvent event, float x, float y, int pointer) {
               if (Window.this.dragging) {
                  Window.this.translate(x - Window.this.dragOffset.x, y - Window.this.dragOffset.y);
               }
            }

            public boolean mouseMoved(InputEvent event, float x, float y) {
               return Window.this.isModal;
            }

            public boolean scrolled(InputEvent event, float x, float y, int amount) {
               return Window.this.isModal;
            }

            public boolean keyDown(InputEvent event, int keycode) {
               return Window.this.isModal;
            }

            public boolean keyUp(InputEvent event, int keycode) {
               return Window.this.isModal;
            }

            public boolean keyTyped(InputEvent event, char character) {
               return Window.this.isModal;
            }
         });
      }
   }

   public void setStyle(Window.WindowStyle style) {
      if (style == null) {
         throw new IllegalArgumentException("style cannot be null.");
      } else {
         this.style = style;
         this.setBackground(style.background);
         this.titleCache = new BitmapFontCache(style.titleFont);
         this.titleCache.setColor(style.titleFontColor);
         if (this.title != null) {
            this.setTitle(this.title);
         }

         this.invalidateHierarchy();
      }
   }

   public Window.WindowStyle getStyle() {
      return this.style;
   }

   public void draw(SpriteBatch batch, float parentAlpha) {
      Stage stage = this.getStage();
      if (this.keepWithinStage && this.getParent() == stage.getRoot()) {
         float parentWidth = stage.getWidth();
         float parentHeight = stage.getHeight();
         if (this.getX() < 0.0F) {
            this.setX(0.0F);
         }

         if (this.getRight() > parentWidth) {
            this.setX(parentWidth - this.getWidth());
         }

         if (this.getY() < 0.0F) {
            this.setY(0.0F);
         }

         if (this.getTop() > parentHeight) {
            this.setY(parentHeight - this.getHeight());
         }
      }

      super.draw(batch, parentAlpha);
   }

   protected void drawBackground(SpriteBatch batch, float parentAlpha) {
      if (this.style.stageBackground != null) {
         Color color = this.getColor();
         batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
         Stage stage = this.getStage();
         this.stageToLocalCoordinates(tmpPosition.set(0.0F, 0.0F));
         this.stageToLocalCoordinates(tmpSize.set(stage.getWidth(), stage.getHeight()));
         this.style.stageBackground.draw(batch, this.getX() + tmpPosition.x, this.getY() + tmpPosition.y, this.getX() + tmpSize.x, this.getY() + tmpSize.y);
      }

      super.drawBackground(batch, parentAlpha);
      float x = this.getX();
      float y = this.getY() + this.getHeight();
      BitmapFont.TextBounds bounds = this.titleCache.getBounds();
      if ((this.titleAlignment & 8) != 0) {
         x += this.getPadLeft();
      } else if ((this.titleAlignment & 16) != 0) {
         x += this.getWidth() - bounds.width - this.getPadRight();
      } else {
         x += (this.getWidth() - bounds.width) / 2.0F;
      }

      if ((this.titleAlignment & 2) == 0) {
         if ((this.titleAlignment & 4) != 0) {
            y -= this.getPadTop() - bounds.height;
         } else {
            y -= (this.getPadTop() - bounds.height) / 2.0F;
         }
      }

      this.titleCache.setColor(Color.tmp.set(this.getColor()).mul(this.style.titleFontColor));
      this.titleCache.setPosition((float)((int)x), (float)((int)y));
      this.titleCache.draw(batch, parentAlpha);
   }

   public Actor hit(float x, float y, boolean touchable) {
      Actor hit = super.hit(x, y, touchable);
      return (Actor)(hit != null || !this.isModal || touchable && this.getTouchable() != Touchable.enabled ? hit : this);
   }

   public void setTitle(String title) {
      this.title = title;
      this.titleCache.setMultiLineText(title, 0.0F, 0.0F);
   }

   public String getTitle() {
      return this.title;
   }

   public void setTitleAlignment(int titleAlignment) {
      this.titleAlignment = titleAlignment;
   }

   public void setMovable(boolean isMovable) {
      this.isMovable = isMovable;
   }

   public void setModal(boolean isModal) {
      this.isModal = isModal;
   }

   public void setKeepWithinStage(boolean keepWithinStage) {
      this.keepWithinStage = keepWithinStage;
   }

   public boolean isDragging() {
      return this.dragging;
   }

   public float getPrefWidth() {
      return Math.max(super.getPrefWidth(), this.titleCache.getBounds().width + this.getPadLeft() + this.getPadRight());
   }

   public static class WindowStyle {
      public Drawable background;
      public BitmapFont titleFont;
      public Color titleFontColor = new Color(1.0F, 1.0F, 1.0F, 1.0F);
      public Drawable stageBackground;

      public WindowStyle() {
      }

      public WindowStyle(BitmapFont titleFont, Color titleFontColor, Drawable background) {
         this.background = background;
         this.titleFont = titleFont;
         this.titleFontColor.set(titleFontColor);
      }

      public WindowStyle(Window.WindowStyle style) {
         this.background = style.background;
         this.titleFont = style.titleFont;
         this.titleFontColor = new Color(style.titleFontColor);
      }
   }
}
