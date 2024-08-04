package com.esotericsoftware.tablelayout;

public abstract class Value {
   public static final Value zero = new Value.CellValue() {
      public float get(Cell cell) {
         return 0.0F;
      }

      public float get(Object table) {
         return 0.0F;
      }
   };
   public static Value minWidth = new Value.CellValue() {
      public float get(Cell cell) {
         if (cell == null) {
            throw new RuntimeException("minWidth can only be set on a cell property.");
         } else {
            Object widget = cell.widget;
            return widget == null ? 0.0F : Toolkit.instance.getMinWidth(widget);
         }
      }
   };
   public static Value minHeight = new Value.CellValue() {
      public float get(Cell cell) {
         if (cell == null) {
            throw new RuntimeException("minHeight can only be set on a cell property.");
         } else {
            Object widget = cell.widget;
            return widget == null ? 0.0F : Toolkit.instance.getMinHeight(widget);
         }
      }
   };
   public static Value prefWidth = new Value.CellValue() {
      public float get(Cell cell) {
         if (cell == null) {
            throw new RuntimeException("prefWidth can only be set on a cell property.");
         } else {
            Object widget = cell.widget;
            return widget == null ? 0.0F : Toolkit.instance.getPrefWidth(widget);
         }
      }
   };
   public static Value prefHeight = new Value.CellValue() {
      public float get(Cell cell) {
         if (cell == null) {
            throw new RuntimeException("prefHeight can only be set on a cell property.");
         } else {
            Object widget = cell.widget;
            return widget == null ? 0.0F : Toolkit.instance.getPrefHeight(widget);
         }
      }
   };
   public static Value maxWidth = new Value.CellValue() {
      public float get(Cell cell) {
         if (cell == null) {
            throw new RuntimeException("maxWidth can only be set on a cell property.");
         } else {
            Object widget = cell.widget;
            return widget == null ? 0.0F : Toolkit.instance.getMaxWidth(widget);
         }
      }
   };
   public static Value maxHeight = new Value.CellValue() {
      public float get(Cell cell) {
         if (cell == null) {
            throw new RuntimeException("maxHeight can only be set on a cell property.");
         } else {
            Object widget = cell.widget;
            return widget == null ? 0.0F : Toolkit.instance.getMaxHeight(widget);
         }
      }
   };

   public abstract float get(Object var1);

   public abstract float get(Cell var1);

   public float width(Object table) {
      return Toolkit.instance.width(this.get(table));
   }

   public float height(Object table) {
      return Toolkit.instance.height(this.get(table));
   }

   public float width(Cell cell) {
      return Toolkit.instance.width(this.get(cell));
   }

   public float height(Cell cell) {
      return Toolkit.instance.height(this.get(cell));
   }

   public static Value percentWidth(final float percent) {
      return new Value.TableValue() {
         public float get(Object table) {
            return Toolkit.instance.getWidth(table) * percent;
         }
      };
   }

   public static Value percentHeight(final float percent) {
      return new Value.TableValue() {
         public float get(Object table) {
            return Toolkit.instance.getHeight(table) * percent;
         }
      };
   }

   public static Value percentWidth(final float percent, final Object widget) {
      return new Value() {
         public float get(Cell cell) {
            return Toolkit.instance.getWidth(widget) * percent;
         }

         public float get(Object table) {
            return Toolkit.instance.getWidth(widget) * percent;
         }
      };
   }

   public static Value percentHeight(final float percent, final Object widget) {
      return new Value.TableValue() {
         public float get(Object table) {
            return Toolkit.instance.getHeight(widget) * percent;
         }
      };
   }

   public abstract static class CellValue extends Value {
      public float get(Object table) {
         throw new UnsupportedOperationException("This value can only be used for a cell property.");
      }
   }

   public static class FixedValue extends Value {
      private float value;

      public FixedValue(float value) {
         this.value = value;
      }

      public void set(float value) {
         this.value = value;
      }

      public float get(Object table) {
         return this.value;
      }

      public float get(Cell cell) {
         return this.value;
      }
   }

   public abstract static class TableValue extends Value {
      public float get(Cell cell) {
         return this.get(cell.getLayout().getTable());
      }
   }
}
