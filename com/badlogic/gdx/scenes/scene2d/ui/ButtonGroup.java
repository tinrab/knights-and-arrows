package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.utils.Array;

public class ButtonGroup {
   private final Array<Button> buttons = new Array();
   private Array<Button> checkedButtons = new Array(1);
   private int minCheckCount;
   private int maxCheckCount = 1;
   private boolean uncheckLast = true;
   private Button lastChecked;

   public ButtonGroup() {
      this.minCheckCount = 1;
   }

   public ButtonGroup(Button... buttons) {
      this.minCheckCount = 0;
      this.add(buttons);
      this.minCheckCount = 1;
   }

   public void add(Button button) {
      if (button == null) {
         throw new IllegalArgumentException("button cannot be null.");
      } else {
         button.buttonGroup = null;
         boolean shouldCheck = button.isChecked() || this.buttons.size < this.minCheckCount;
         button.setChecked(false);
         button.buttonGroup = this;
         this.buttons.add(button);
         if (shouldCheck) {
            button.setChecked(true);
         }

      }
   }

   public void add(Button... buttons) {
      if (buttons == null) {
         throw new IllegalArgumentException("buttons cannot be null.");
      } else {
         int i = 0;

         for(int n = buttons.length; i < n; ++i) {
            this.add(buttons[i]);
         }

      }
   }

   public void remove(Button button) {
      if (button == null) {
         throw new IllegalArgumentException("button cannot be null.");
      } else {
         button.buttonGroup = null;
         this.buttons.removeValue(button, true);
      }
   }

   public void remove(Button... buttons) {
      if (buttons == null) {
         throw new IllegalArgumentException("buttons cannot be null.");
      } else {
         int i = 0;

         for(int n = buttons.length; i < n; ++i) {
            this.remove(buttons[i]);
         }

      }
   }

   public void setChecked(String text) {
      if (text == null) {
         throw new IllegalArgumentException("text cannot be null.");
      } else {
         int i = 0;

         for(int n = this.buttons.size; i < n; ++i) {
            Button button = (Button)this.buttons.get(i);
            if (button instanceof TextButton && text.contentEquals(((TextButton)button).getText())) {
               button.setChecked(true);
               return;
            }
         }

      }
   }

   protected boolean canCheck(Button button, boolean newState) {
      if (button.isChecked == newState) {
         return false;
      } else {
         if (!newState) {
            if (this.checkedButtons.size <= this.minCheckCount) {
               return false;
            }

            this.checkedButtons.removeValue(button, true);
         } else {
            if (this.maxCheckCount != -1 && this.checkedButtons.size >= this.maxCheckCount) {
               if (!this.uncheckLast) {
                  return false;
               }

               int old = this.minCheckCount;
               this.minCheckCount = 0;
               this.lastChecked.setChecked(false);
               this.minCheckCount = old;
            }

            this.checkedButtons.add(button);
            this.lastChecked = button;
         }

         return true;
      }
   }

   public void uncheckAll() {
      int old = this.minCheckCount;
      this.minCheckCount = 0;
      int i = 0;

      for(int n = this.buttons.size; i < n; ++i) {
         Button button = (Button)this.buttons.get(i);
         button.setChecked(false);
      }

      this.minCheckCount = old;
   }

   public Button getChecked() {
      return this.checkedButtons.size > 0 ? (Button)this.checkedButtons.get(0) : null;
   }

   public Array<Button> getAllChecked() {
      return this.checkedButtons;
   }

   public Array<Button> getButtons() {
      return this.buttons;
   }

   public void setMinCheckCount(int minCheckCount) {
      this.minCheckCount = minCheckCount;
   }

   public void setMaxCheckCount(int maxCheckCount) {
      this.maxCheckCount = maxCheckCount;
   }

   public void setUncheckLast(boolean uncheckLast) {
      this.uncheckLast = uncheckLast;
   }
}
