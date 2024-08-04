package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Clipboard;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;

public class TextField extends Widget {
   private static final char BACKSPACE = '\b';
   private static final char ENTER_DESKTOP = '\r';
   private static final char ENTER_ANDROID = '\n';
   private static final char TAB = '\t';
   private static final char DELETE = '\u007f';
   private static final char BULLET = '\u0095';
   private static final Vector2 tmp1 = new Vector2();
   private static final Vector2 tmp2 = new Vector2();
   private static final Vector2 tmp3 = new Vector2();
   static boolean isMac = System.getProperty("os.name").contains("Mac");
   TextField.TextFieldStyle style;
   String text;
   String messageText;
   private CharSequence displayText;
   int cursor;
   private Clipboard clipboard;
   TextField.TextFieldListener listener;
   TextField.TextFieldFilter filter;
   TextField.OnscreenKeyboard keyboard;
   boolean focusTraversal;
   boolean disabled;
   boolean onlyFontChars;
   private boolean passwordMode;
   private StringBuilder passwordBuffer;
   private final Rectangle fieldBounds;
   private final BitmapFont.TextBounds textBounds;
   private final Rectangle scissor;
   float renderOffset;
   float textOffset;
   private int visibleTextStart;
   private int visibleTextEnd;
   private final FloatArray glyphAdvances;
   final FloatArray glyphPositions;
   boolean cursorOn;
   private float blinkTime;
   long lastBlink;
   boolean hasSelection;
   int selectionStart;
   private float selectionX;
   private float selectionWidth;
   private char passwordCharacter;
   InputListener inputListener;
   TextField.KeyRepeatTask keyRepeatTask;
   float keyRepeatInitialTime;
   float keyRepeatTime;
   boolean rightAligned;
   int maxLength;

   public TextField(String text, Skin skin) {
      this(text, (TextField.TextFieldStyle)skin.get(TextField.TextFieldStyle.class));
   }

   public TextField(String text, Skin skin, String styleName) {
      this(text, (TextField.TextFieldStyle)skin.get(styleName, TextField.TextFieldStyle.class));
   }

   public TextField(String text, TextField.TextFieldStyle style) {
      this.keyboard = new TextField.DefaultOnscreenKeyboard();
      this.focusTraversal = true;
      this.onlyFontChars = true;
      this.fieldBounds = new Rectangle();
      this.textBounds = new BitmapFont.TextBounds();
      this.scissor = new Rectangle();
      this.glyphAdvances = new FloatArray();
      this.glyphPositions = new FloatArray();
      this.cursorOn = true;
      this.blinkTime = 0.32F;
      this.passwordCharacter = 149;
      this.keyRepeatTask = new TextField.KeyRepeatTask();
      this.keyRepeatInitialTime = 0.4F;
      this.keyRepeatTime = 0.1F;
      this.maxLength = 0;
      this.setStyle(style);
      this.clipboard = Gdx.app.getClipboard();
      this.setText(text);
      this.setWidth(this.getPrefWidth());
      this.setHeight(this.getPrefHeight());
      this.initialize();
   }

   private void initialize() {
      this.addListener(this.inputListener = new ClickListener() {
         public void clicked(InputEvent event, float x, float y) {
            if (this.getTapCount() > 1) {
               TextField.this.setSelection(0, TextField.this.text.length());
            }

         }

         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            if (!super.touchDown(event, x, y, pointer, button)) {
               return false;
            } else if (pointer == 0 && button != 0) {
               return false;
            } else if (TextField.this.disabled) {
               return true;
            } else {
               TextField.this.clearSelection();
               this.setCursorPosition(x);
               TextField.this.selectionStart = TextField.this.cursor;
               Stage stage = TextField.this.getStage();
               if (stage != null) {
                  stage.setKeyboardFocus(TextField.this);
               }

               TextField.this.keyboard.show(true);
               return true;
            }
         }

         public void touchDragged(InputEvent event, float x, float y, int pointer) {
            super.touchDragged(event, x, y, pointer);
            TextField.this.lastBlink = 0L;
            TextField.this.cursorOn = false;
            this.setCursorPosition(x);
            TextField.this.hasSelection = true;
         }

         private void setCursorPosition(float x) {
            TextField.this.lastBlink = 0L;
            TextField.this.cursorOn = false;
            x -= TextField.this.renderOffset + TextField.this.textOffset;

            for(int i = 0; i < TextField.this.glyphPositions.size; ++i) {
               if (TextField.this.glyphPositions.items[i] > x) {
                  TextField.this.cursor = Math.max(0, i - 1);
                  return;
               }
            }

            TextField.this.cursor = Math.max(0, TextField.this.glyphPositions.size - 1);
         }

         public boolean keyDown(InputEvent event, int keycode) {
            if (TextField.this.disabled) {
               return false;
            } else {
               BitmapFont font = TextField.this.style.font;
               TextField.this.lastBlink = 0L;
               TextField.this.cursorOn = false;
               Stage stage = TextField.this.getStage();
               if (stage != null && stage.getKeyboardFocus() == TextField.this) {
                  boolean repeat = false;
                  boolean ctrl;
                  if (TextField.isMac) {
                     ctrl = Gdx.input.isKeyPressed(63);
                  } else {
                     ctrl = Gdx.input.isKeyPressed(129) || Gdx.input.isKeyPressed(130);
                  }

                  if (ctrl) {
                     if (keycode == 50) {
                        TextField.this.paste();
                        return true;
                     }

                     if (keycode == 31 || keycode == 133) {
                        TextField.this.copy();
                        return true;
                     }

                     if (keycode == 52 || keycode == 67) {
                        TextField.this.cut();
                        return true;
                     }
                  }

                  char c;
                  char cx;
                  int length;
                  if (!Gdx.input.isKeyPressed(59) && !Gdx.input.isKeyPressed(60)) {
                     if (keycode == 21) {
                        while(TextField.this.cursor-- > 1 && ctrl) {
                           c = TextField.this.text.charAt(TextField.this.cursor - 1);
                           if ((c < 'A' || c > 'Z') && (c < 'a' || c > 'z') && (c < '0' || c > '9')) {
                              break;
                           }
                        }

                        TextField.this.clearSelection();
                        repeat = true;
                     }

                     if (keycode == 22) {
                        length = TextField.this.text.length();

                        while(++TextField.this.cursor < length && ctrl) {
                           cx = TextField.this.text.charAt(TextField.this.cursor - 1);
                           if ((cx < 'A' || cx > 'Z') && (cx < 'a' || cx > 'z') && (cx < '0' || cx > '9')) {
                              break;
                           }
                        }

                        TextField.this.clearSelection();
                        repeat = true;
                     }

                     if (keycode == 3) {
                        TextField.this.cursor = 0;
                        TextField.this.clearSelection();
                     }

                     if (keycode == 132) {
                        TextField.this.cursor = TextField.this.text.length();
                        TextField.this.clearSelection();
                     }

                     TextField.this.cursor = Math.max(0, TextField.this.cursor);
                     TextField.this.cursor = Math.min(TextField.this.text.length(), TextField.this.cursor);
                  } else {
                     if (keycode == 133) {
                        TextField.this.paste();
                     }

                     if (keycode == 112 && TextField.this.hasSelection) {
                        TextField.this.copy();
                        TextField.this.delete();
                     }

                     if (keycode == 21) {
                        if (!TextField.this.hasSelection) {
                           TextField.this.selectionStart = TextField.this.cursor;
                           TextField.this.hasSelection = true;
                        }

                        while(--TextField.this.cursor > 0 && ctrl) {
                           c = TextField.this.text.charAt(TextField.this.cursor);
                           if ((c < 'A' || c > 'Z') && (c < 'a' || c > 'z') && (c < '0' || c > '9')) {
                              break;
                           }
                        }

                        repeat = true;
                     }

                     if (keycode == 22) {
                        if (!TextField.this.hasSelection) {
                           TextField.this.selectionStart = TextField.this.cursor;
                           TextField.this.hasSelection = true;
                        }

                        length = TextField.this.text.length();

                        while(++TextField.this.cursor < length && ctrl) {
                           cx = TextField.this.text.charAt(TextField.this.cursor - 1);
                           if ((cx < 'A' || cx > 'Z') && (cx < 'a' || cx > 'z') && (cx < '0' || cx > '9')) {
                              break;
                           }
                        }

                        repeat = true;
                     }

                     if (keycode == 3) {
                        if (!TextField.this.hasSelection) {
                           TextField.this.selectionStart = TextField.this.cursor;
                           TextField.this.hasSelection = true;
                        }

                        TextField.this.cursor = 0;
                     }

                     if (keycode == 132) {
                        if (!TextField.this.hasSelection) {
                           TextField.this.selectionStart = TextField.this.cursor;
                           TextField.this.hasSelection = true;
                        }

                        TextField.this.cursor = TextField.this.text.length();
                     }

                     TextField.this.cursor = Math.max(0, TextField.this.cursor);
                     TextField.this.cursor = Math.min(TextField.this.text.length(), TextField.this.cursor);
                  }

                  if (repeat && (!TextField.this.keyRepeatTask.isScheduled() || TextField.this.keyRepeatTask.keycode != keycode)) {
                     TextField.this.keyRepeatTask.keycode = keycode;
                     TextField.this.keyRepeatTask.cancel();
                     Timer.schedule(TextField.this.keyRepeatTask, TextField.this.keyRepeatInitialTime, TextField.this.keyRepeatTime);
                  }

                  return true;
               } else {
                  return false;
               }
            }
         }

         public boolean keyUp(InputEvent event, int keycode) {
            if (TextField.this.disabled) {
               return false;
            } else {
               TextField.this.keyRepeatTask.cancel();
               return true;
            }
         }

         public boolean keyTyped(InputEvent event, char character) {
            if (TextField.this.disabled) {
               return false;
            } else {
               BitmapFont font = TextField.this.style.font;
               Stage stage = TextField.this.getStage();
               if (stage != null && stage.getKeyboardFocus() == TextField.this) {
                  if (character == '\b') {
                     if (TextField.this.cursor > 0 || TextField.this.hasSelection) {
                        if (!TextField.this.hasSelection) {
                           TextField.this.text = TextField.this.text.substring(0, TextField.this.cursor - 1) + TextField.this.text.substring(TextField.this.cursor);
                           TextField.this.updateDisplayText();
                           --TextField.this.cursor;
                           TextField.this.renderOffset = 0.0F;
                        } else {
                           TextField.this.delete();
                        }
                     }
                  } else if (character == 127) {
                     if (TextField.this.cursor < TextField.this.text.length() || TextField.this.hasSelection) {
                        if (!TextField.this.hasSelection) {
                           TextField.this.text = TextField.this.text.substring(0, TextField.this.cursor) + TextField.this.text.substring(TextField.this.cursor + 1);
                           TextField.this.updateDisplayText();
                        } else {
                           TextField.this.delete();
                        }
                     }
                  } else if ((character == '\t' || character == '\n') && TextField.this.focusTraversal) {
                     TextField.this.next(Gdx.input.isKeyPressed(59) || Gdx.input.isKeyPressed(60));
                  } else if (font.containsCharacter(character)) {
                     if (character != '\r' && character != '\n' && TextField.this.filter != null && !TextField.this.filter.acceptChar(TextField.this, character)) {
                        return true;
                     }

                     if (TextField.this.maxLength > 0 && TextField.this.text.length() + 1 > TextField.this.maxLength) {
                        return true;
                     }

                     if (!TextField.this.hasSelection) {
                        TextField.this.text = TextField.this.text.substring(0, TextField.this.cursor) + character + TextField.this.text.substring(TextField.this.cursor, TextField.this.text.length());
                        TextField.this.updateDisplayText();
                        ++TextField.this.cursor;
                     } else {
                        int minIndex = Math.min(TextField.this.cursor, TextField.this.selectionStart);
                        int maxIndex = Math.max(TextField.this.cursor, TextField.this.selectionStart);
                        TextField.this.text = (minIndex > 0 ? TextField.this.text.substring(0, minIndex) : "") + (maxIndex < TextField.this.text.length() ? TextField.this.text.substring(maxIndex, TextField.this.text.length()) : "");
                        TextField.this.cursor = minIndex;
                        TextField.this.text = TextField.this.text.substring(0, TextField.this.cursor) + character + TextField.this.text.substring(TextField.this.cursor, TextField.this.text.length());
                        TextField.this.updateDisplayText();
                        ++TextField.this.cursor;
                        TextField.this.clearSelection();
                     }
                  }

                  if (TextField.this.listener != null) {
                     TextField.this.listener.keyTyped(TextField.this, character);
                  }

                  return true;
               } else {
                  return false;
               }
            }
         }
      });
   }

   public void setMaxLength(int maxLength) {
      this.maxLength = maxLength;
   }

   public int getMaxLength() {
      return this.maxLength;
   }

   public void setOnlyFontChars(boolean onlyFontChars) {
      this.onlyFontChars = onlyFontChars;
   }

   public void setStyle(TextField.TextFieldStyle style) {
      if (style == null) {
         throw new IllegalArgumentException("style cannot be null.");
      } else {
         this.style = style;
         this.invalidateHierarchy();
      }
   }

   public void setPasswordCharacter(char passwordCharacter) {
      this.passwordCharacter = passwordCharacter;
      if (this.passwordMode) {
         this.updateDisplayText();
      }

   }

   public TextField.TextFieldStyle getStyle() {
      return this.style;
   }

   private void calculateOffsets() {
      float visibleWidth = this.getWidth();
      if (this.style.background != null) {
         visibleWidth -= this.style.background.getLeftWidth() + this.style.background.getRightWidth();
      }

      float position = this.glyphPositions.get(this.cursor);
      float distance = position - Math.abs(this.renderOffset);
      if (distance <= 0.0F) {
         if (this.cursor > 0) {
            this.renderOffset = -this.glyphPositions.get(this.cursor - 1);
         } else {
            this.renderOffset = 0.0F;
         }
      } else if (distance > visibleWidth) {
         this.renderOffset -= distance - visibleWidth;
      }

      this.visibleTextStart = 0;
      this.textOffset = 0.0F;
      float start = Math.abs(this.renderOffset);
      int len = this.glyphPositions.size;
      float startPos = 0.0F;

      int minIndex;
      for(minIndex = 0; minIndex < len; ++minIndex) {
         if (this.glyphPositions.items[minIndex] >= start) {
            this.visibleTextStart = minIndex;
            startPos = this.glyphPositions.items[minIndex];
            this.textOffset = startPos - start;
            break;
         }
      }

      for(this.visibleTextEnd = Math.min(this.displayText.length(), this.cursor + 1); this.visibleTextEnd <= this.displayText.length() && !(this.glyphPositions.items[this.visibleTextEnd] - startPos > visibleWidth); ++this.visibleTextEnd) {
      }

      this.visibleTextEnd = Math.max(0, this.visibleTextEnd - 1);
      if (this.hasSelection) {
         minIndex = Math.min(this.cursor, this.selectionStart);
         int maxIndex = Math.max(this.cursor, this.selectionStart);
         float minX = Math.max(this.glyphPositions.get(minIndex), startPos);
         float maxX = Math.min(this.glyphPositions.get(maxIndex), this.glyphPositions.get(this.visibleTextEnd));
         this.selectionX = minX;
         this.selectionWidth = maxX - minX;
      }

      if (this.rightAligned) {
         this.textOffset = visibleWidth - (this.glyphPositions.items[this.visibleTextEnd] - startPos);
         if (this.hasSelection) {
            this.selectionX += this.textOffset;
         }
      }

   }

   public void draw(SpriteBatch batch, float parentAlpha) {
      Stage stage = this.getStage();
      boolean focused = stage != null && stage.getKeyboardFocus() == this;
      BitmapFont font = this.style.font;
      Color fontColor = this.disabled && this.style.disabledFontColor != null ? this.style.disabledFontColor : (focused && this.style.focusedFontColor != null ? this.style.focusedFontColor : this.style.fontColor);
      Drawable selection = this.style.selection;
      Drawable cursorPatch = this.style.cursor;
      Drawable background = this.disabled && this.style.disabledBackground != null ? this.style.disabledBackground : (focused && this.style.focusedBackground != null ? this.style.focusedBackground : this.style.background);
      Color color = this.getColor();
      float x = this.getX();
      float y = this.getY();
      float width = this.getWidth();
      float height = this.getHeight();
      float textY = this.textBounds.height / 2.0F + font.getDescent();
      batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
      float bgLeftWidth = 0.0F;
      float yOffset;
      if (background != null) {
         background.draw(batch, x, y, width, height);
         bgLeftWidth = background.getLeftWidth();
         yOffset = background.getBottomHeight();
         textY = (float)((int)(textY + (height - background.getTopHeight() - yOffset) / 2.0F + yOffset));
      } else {
         textY = (float)((int)(textY + height / 2.0F));
      }

      this.calculateOffsets();
      if (focused && this.hasSelection && selection != null) {
         selection.draw(batch, x + this.selectionX + bgLeftWidth + this.renderOffset, y + textY - this.textBounds.height - font.getDescent(), this.selectionWidth, this.textBounds.height + font.getDescent() / 2.0F);
      }

      yOffset = font.isFlipped() ? -this.textBounds.height : 0.0F;
      if (this.displayText.length() == 0) {
         if (!focused && this.messageText != null) {
            if (this.style.messageFontColor != null) {
               font.setColor(this.style.messageFontColor.r, this.style.messageFontColor.g, this.style.messageFontColor.b, this.style.messageFontColor.a * parentAlpha);
            } else {
               font.setColor(0.7F, 0.7F, 0.7F, parentAlpha);
            }

            BitmapFont messageFont = this.style.messageFont != null ? this.style.messageFont : font;
            messageFont.draw(batch, this.messageText, x + bgLeftWidth, y + textY + yOffset);
         }
      } else {
         font.setColor(fontColor.r, fontColor.g, fontColor.b, fontColor.a * parentAlpha);
         font.draw(batch, this.displayText, x + bgLeftWidth + this.textOffset, y + textY + yOffset, this.visibleTextStart, this.visibleTextEnd);
      }

      if (focused && !this.disabled) {
         this.blink();
         if (this.cursorOn && cursorPatch != null) {
            cursorPatch.draw(batch, x + bgLeftWidth + this.textOffset + this.glyphPositions.get(this.cursor) - this.glyphPositions.items[this.visibleTextStart] - 1.0F, y + textY - this.textBounds.height - font.getDescent(), cursorPatch.getMinWidth(), this.textBounds.height + font.getDescent() / 2.0F);
         }
      }

   }

   void updateDisplayText() {
      StringBuilder buffer = new StringBuilder();

      for(int i = 0; i < this.text.length(); ++i) {
         char c = this.text.charAt(i);
         buffer.append(this.style.font.containsCharacter(c) ? c : ' ');
      }

      String text = buffer.toString();
      if (this.passwordMode && this.style.font.containsCharacter(this.passwordCharacter)) {
         if (this.passwordBuffer == null) {
            this.passwordBuffer = new StringBuilder(text.length());
         }

         if (this.passwordBuffer.length() > text.length()) {
            this.passwordBuffer.setLength(text.length());
         } else {
            int i = this.passwordBuffer.length();

            for(int n = text.length(); i < n; ++i) {
               this.passwordBuffer.append(this.passwordCharacter);
            }
         }

         this.displayText = this.passwordBuffer;
      } else {
         this.displayText = text;
      }

      this.style.font.computeGlyphAdvancesAndPositions(this.displayText, this.glyphAdvances, this.glyphPositions);
      if (this.selectionStart > text.length()) {
         this.selectionStart = text.length();
      }

   }

   private void blink() {
      long time = TimeUtils.nanoTime();
      if ((float)(time - this.lastBlink) / 1.0E9F > this.blinkTime) {
         this.cursorOn = !this.cursorOn;
         this.lastBlink = time;
      }

   }

   public void copy() {
      if (this.hasSelection) {
         int minIndex = Math.min(this.cursor, this.selectionStart);
         int maxIndex = Math.max(this.cursor, this.selectionStart);
         this.clipboard.setContents(this.text.substring(minIndex, maxIndex));
      }

   }

   public void cut() {
      if (this.hasSelection) {
         this.copy();
         this.delete();
      }

   }

   void paste() {
      String content = this.clipboard.getContents();
      if (content != null) {
         StringBuilder buffer = new StringBuilder();

         int minIndex;
         for(minIndex = 0; minIndex < content.length() && (this.maxLength <= 0 || this.text.length() + buffer.length() + 1 <= this.maxLength); ++minIndex) {
            char c = content.charAt(minIndex);
            if (this.style.font.containsCharacter(c) && (this.filter == null || this.filter.acceptChar(this, c))) {
               buffer.append(c);
            }
         }

         content = buffer.toString();
         if (!this.hasSelection) {
            this.text = this.text.substring(0, this.cursor) + content + this.text.substring(this.cursor, this.text.length());
            this.updateDisplayText();
            this.cursor += content.length();
         } else {
            minIndex = Math.min(this.cursor, this.selectionStart);
            int maxIndex = Math.max(this.cursor, this.selectionStart);
            this.text = (minIndex > 0 ? this.text.substring(0, minIndex) : "") + (maxIndex < this.text.length() ? this.text.substring(maxIndex, this.text.length()) : "");
            this.cursor = minIndex;
            this.text = this.text.substring(0, this.cursor) + content + this.text.substring(this.cursor, this.text.length());
            this.updateDisplayText();
            this.cursor = minIndex + content.length();
            this.clearSelection();
         }
      }

   }

   void delete() {
      int minIndex = Math.min(this.cursor, this.selectionStart);
      int maxIndex = Math.max(this.cursor, this.selectionStart);
      this.text = (minIndex > 0 ? this.text.substring(0, minIndex) : "") + (maxIndex < this.text.length() ? this.text.substring(maxIndex, this.text.length()) : "");
      this.updateDisplayText();
      this.cursor = minIndex;
      this.clearSelection();
   }

   public void next(boolean up) {
      Stage stage = this.getStage();
      if (stage != null) {
         this.getParent().localToStageCoordinates(tmp1.set(this.getX(), this.getY()));
         TextField textField = this.findNextTextField(stage.getActors(), (TextField)null, tmp2, tmp1, up);
         if (textField == null) {
            if (up) {
               tmp1.set(Float.MIN_VALUE, Float.MIN_VALUE);
            } else {
               tmp1.set(Float.MAX_VALUE, Float.MAX_VALUE);
            }

            textField = this.findNextTextField(this.getStage().getActors(), (TextField)null, tmp2, tmp1, up);
         }

         if (textField != null) {
            stage.setKeyboardFocus(textField);
         } else {
            Gdx.input.setOnscreenKeyboardVisible(false);
         }

      }
   }

   private TextField findNextTextField(Array<Actor> actors, TextField best, Vector2 bestCoords, Vector2 currentCoords, boolean up) {
      int i = 0;

      for(int n = actors.size; i < n; ++i) {
         Actor actor = (Actor)actors.get(i);
         if (actor != this) {
            if (actor instanceof TextField) {
               TextField textField = (TextField)actor;
               if (!textField.isDisabled() && textField.focusTraversal) {
                  Vector2 actorCoords = actor.getParent().localToStageCoordinates(tmp3.set(actor.getX(), actor.getY()));
                  if ((actorCoords.y < currentCoords.y || actorCoords.y == currentCoords.y && actorCoords.x > currentCoords.x) ^ up && (best == null || (actorCoords.y > bestCoords.y || actorCoords.y == bestCoords.y && actorCoords.x < bestCoords.x) ^ up)) {
                     best = (TextField)actor;
                     bestCoords.set(actorCoords);
                  }
               }
            } else if (actor instanceof Group) {
               best = this.findNextTextField(((Group)actor).getChildren(), best, bestCoords, currentCoords, up);
            }
         }
      }

      return best;
   }

   public void setTextFieldListener(TextField.TextFieldListener listener) {
      this.listener = listener;
   }

   public void setTextFieldFilter(TextField.TextFieldFilter filter) {
      this.filter = filter;
   }

   public void setFocusTraversal(boolean focusTraversal) {
      this.focusTraversal = focusTraversal;
   }

   public String getMessageText() {
      return this.messageText;
   }

   public void setMessageText(String messageText) {
      this.messageText = messageText;
   }

   public void setText(String text) {
      if (text == null) {
         throw new IllegalArgumentException("text cannot be null.");
      } else {
         BitmapFont font = this.style.font;
         StringBuilder buffer = new StringBuilder();

         for(int i = 0; i < text.length() && (this.maxLength <= 0 || buffer.length() + 1 <= this.maxLength); ++i) {
            char c = text.charAt(i);
            if ((!this.onlyFontChars || this.style.font.containsCharacter(c)) && (this.filter == null || this.filter.acceptChar(this, c))) {
               buffer.append(c);
            }
         }

         this.text = buffer.toString();
         this.updateDisplayText();
         this.cursor = 0;
         this.clearSelection();
         this.textBounds.set(font.getBounds(this.displayText));
         BitmapFont.TextBounds var10000 = this.textBounds;
         var10000.height -= font.getDescent() * 2.0F;
         font.computeGlyphAdvancesAndPositions(this.displayText, this.glyphAdvances, this.glyphPositions);
      }
   }

   public String getText() {
      return this.text;
   }

   public void setSelection(int selectionStart, int selectionEnd) {
      if (selectionStart < 0) {
         throw new IllegalArgumentException("selectionStart must be >= 0");
      } else if (selectionEnd < 0) {
         throw new IllegalArgumentException("selectionEnd must be >= 0");
      } else {
         selectionStart = Math.min(this.text.length(), selectionStart);
         selectionEnd = Math.min(this.text.length(), selectionEnd);
         if (selectionEnd == selectionStart) {
            this.clearSelection();
         } else {
            if (selectionEnd < selectionStart) {
               int temp = selectionEnd;
               selectionEnd = selectionStart;
               selectionStart = temp;
            }

            this.hasSelection = true;
            this.selectionStart = selectionStart;
            this.cursor = selectionEnd;
         }
      }
   }

   public void selectAll() {
      this.setSelection(0, this.text.length());
   }

   public void clearSelection() {
      this.hasSelection = false;
   }

   public void setCursorPosition(int cursorPosition) {
      if (cursorPosition < 0) {
         throw new IllegalArgumentException("cursorPosition must be >= 0");
      } else {
         this.clearSelection();
         this.cursor = Math.min(cursorPosition, this.text.length());
      }
   }

   public int getCursorPosition() {
      return this.cursor;
   }

   public TextField.OnscreenKeyboard getOnscreenKeyboard() {
      return this.keyboard;
   }

   public void setOnscreenKeyboard(TextField.OnscreenKeyboard keyboard) {
      this.keyboard = keyboard;
   }

   public void setClipboard(Clipboard clipboard) {
      this.clipboard = clipboard;
   }

   public float getPrefWidth() {
      return 150.0F;
   }

   public float getPrefHeight() {
      float prefHeight = this.textBounds.height;
      if (this.style.background != null) {
         prefHeight = Math.max(prefHeight + this.style.background.getBottomHeight() + this.style.background.getTopHeight(), this.style.background.getMinHeight());
      }

      return prefHeight;
   }

   public void setRightAligned(boolean rightAligned) {
      this.rightAligned = rightAligned;
   }

   public void setPasswordMode(boolean passwordMode) {
      this.passwordMode = passwordMode;
      this.updateDisplayText();
   }

   public void setBlinkTime(float blinkTime) {
      this.blinkTime = blinkTime;
   }

   public void setDisabled(boolean disabled) {
      this.disabled = disabled;
   }

   public boolean isDisabled() {
      return this.disabled;
   }

   public boolean isPasswordMode() {
      return this.passwordMode;
   }

   public TextField.TextFieldFilter getTextFieldFilter() {
      return this.filter;
   }

   public static class DefaultOnscreenKeyboard implements TextField.OnscreenKeyboard {
      public void show(boolean visible) {
         Gdx.input.setOnscreenKeyboardVisible(visible);
      }
   }

   class KeyRepeatTask extends Timer.Task {
      int keycode;

      public void run() {
         TextField.this.inputListener.keyDown((InputEvent)null, this.keycode);
      }
   }

   public interface OnscreenKeyboard {
      void show(boolean var1);
   }

   public interface TextFieldFilter {
      boolean acceptChar(TextField var1, char var2);

      public static class DigitsOnlyFilter implements TextField.TextFieldFilter {
         public boolean acceptChar(TextField textField, char key) {
            return Character.isDigit(key);
         }
      }
   }

   public interface TextFieldListener {
      void keyTyped(TextField var1, char var2);
   }

   public static class TextFieldStyle {
      public BitmapFont font;
      public Color fontColor;
      public Color focusedFontColor;
      public Color disabledFontColor;
      public Drawable background;
      public Drawable focusedBackground;
      public Drawable disabledBackground;
      public Drawable cursor;
      public Drawable selection;
      public BitmapFont messageFont;
      public Color messageFontColor;

      public TextFieldStyle() {
      }

      public TextFieldStyle(BitmapFont font, Color fontColor, Drawable cursor, Drawable selection, Drawable background) {
         this.background = background;
         this.cursor = cursor;
         this.font = font;
         this.fontColor = fontColor;
         this.selection = selection;
      }

      public TextFieldStyle(TextField.TextFieldStyle style) {
         this.messageFont = style.messageFont;
         if (style.messageFontColor != null) {
            this.messageFontColor = new Color(style.messageFontColor);
         }

         this.background = style.background;
         this.focusedBackground = style.focusedBackground;
         this.disabledBackground = style.disabledBackground;
         this.cursor = style.cursor;
         this.font = style.font;
         if (style.fontColor != null) {
            this.fontColor = new Color(style.fontColor);
         }

         if (style.focusedFontColor != null) {
            this.focusedFontColor = new Color(style.focusedFontColor);
         }

         if (style.disabledFontColor != null) {
            this.disabledFontColor = new Color(style.disabledFontColor);
         }

         this.selection = style.selection;
      }
   }
}
