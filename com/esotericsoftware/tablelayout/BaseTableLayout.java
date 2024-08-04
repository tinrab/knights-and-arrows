package com.esotericsoftware.tablelayout;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseTableLayout<C, T extends C, L extends BaseTableLayout, K extends Toolkit<C, T, L>> {
   public static final int CENTER = 1;
   public static final int TOP = 2;
   public static final int BOTTOM = 4;
   public static final int LEFT = 8;
   public static final int RIGHT = 16;
   K toolkit;
   T table;
   private int columns;
   private int rows;
   private final ArrayList<Cell> cells = new ArrayList(4);
   private final Cell cellDefaults;
   private final ArrayList<Cell> columnDefaults = new ArrayList(2);
   private Cell rowDefaults;
   private boolean sizeInvalid = true;
   private float[] columnMinWidth;
   private float[] rowMinHeight;
   private float[] columnPrefWidth;
   private float[] rowPrefHeight;
   private float tableMinWidth;
   private float tableMinHeight;
   private float tablePrefWidth;
   private float tablePrefHeight;
   private float[] columnWidth;
   private float[] rowHeight;
   private float[] expandWidth;
   private float[] expandHeight;
   private float[] columnWeightedWidth;
   private float[] rowWeightedHeight;
   Value padTop;
   Value padLeft;
   Value padBottom;
   Value padRight;
   int align = 1;
   BaseTableLayout.Debug debug;

   public BaseTableLayout(K toolkit) {
      this.debug = BaseTableLayout.Debug.none;
      this.toolkit = toolkit;
      this.cellDefaults = toolkit.obtainCell(this);
      this.cellDefaults.defaults();
   }

   public void invalidate() {
      this.sizeInvalid = true;
   }

   public abstract void invalidateHierarchy();

   public Cell<C> add(C widget) {
      Cell cell = this.toolkit.obtainCell(this);
      cell.widget = widget;
      Cell columnCell;
      if (this.cells.size() > 0) {
         columnCell = (Cell)this.cells.get(this.cells.size() - 1);
         if (!columnCell.endRow) {
            cell.column = columnCell.column + columnCell.colspan;
            cell.row = columnCell.row;
         } else {
            cell.column = 0;
            cell.row = columnCell.row + 1;
         }

         if (cell.row > 0) {
            label42:
            for(int i = this.cells.size() - 1; i >= 0; --i) {
               Cell other = (Cell)this.cells.get(i);
               int column = other.column;

               for(int nn = column + other.colspan; column < nn; ++column) {
                  if (column == cell.column) {
                     cell.cellAboveIndex = i;
                     break label42;
                  }
               }
            }
         }
      } else {
         cell.column = 0;
         cell.row = 0;
      }

      this.cells.add(cell);
      cell.set(this.cellDefaults);
      if (cell.column < this.columnDefaults.size()) {
         columnCell = (Cell)this.columnDefaults.get(cell.column);
         if (columnCell != null) {
            cell.merge(columnCell);
         }
      }

      cell.merge(this.rowDefaults);
      if (widget != null) {
         this.toolkit.addChild(this.table, widget);
      }

      return cell;
   }

   public Cell row() {
      if (this.cells.size() > 0) {
         this.endRow();
         this.invalidate();
      }

      if (this.rowDefaults != null) {
         this.toolkit.freeCell(this.rowDefaults);
      }

      this.rowDefaults = this.toolkit.obtainCell(this);
      this.rowDefaults.clear();
      return this.rowDefaults;
   }

   private void endRow() {
      int rowColumns = 0;

      for(int i = this.cells.size() - 1; i >= 0; --i) {
         Cell cell = (Cell)this.cells.get(i);
         if (cell.endRow) {
            break;
         }

         rowColumns += cell.colspan;
      }

      this.columns = Math.max(this.columns, rowColumns);
      ++this.rows;
      ((Cell)this.cells.get(this.cells.size() - 1)).endRow = true;
   }

   public Cell columnDefaults(int column) {
      Cell cell = this.columnDefaults.size() > column ? (Cell)this.columnDefaults.get(column) : null;
      if (cell == null) {
         cell = this.toolkit.obtainCell(this);
         cell.clear();
         if (column >= this.columnDefaults.size()) {
            for(int i = this.columnDefaults.size(); i < column; ++i) {
               this.columnDefaults.add((Object)null);
            }

            this.columnDefaults.add(cell);
         } else {
            this.columnDefaults.set(column, cell);
         }
      }

      return cell;
   }

   public void reset() {
      this.clear();
      this.padTop = null;
      this.padLeft = null;
      this.padBottom = null;
      this.padRight = null;
      this.align = 1;
      if (this.debug != BaseTableLayout.Debug.none) {
         this.toolkit.clearDebugRectangles(this);
      }

      this.debug = BaseTableLayout.Debug.none;
      this.cellDefaults.defaults();
      int i = 0;

      for(int n = this.columnDefaults.size(); i < n; ++i) {
         Cell columnCell = (Cell)this.columnDefaults.get(i);
         if (columnCell != null) {
            this.toolkit.freeCell(columnCell);
         }
      }

      this.columnDefaults.clear();
   }

   public void clear() {
      for(int i = this.cells.size() - 1; i >= 0; --i) {
         Cell cell = (Cell)this.cells.get(i);
         Object widget = cell.widget;
         if (widget != null) {
            this.toolkit.removeChild(this.table, widget);
         }

         this.toolkit.freeCell(cell);
      }

      this.cells.clear();
      this.rows = 0;
      this.columns = 0;
      if (this.rowDefaults != null) {
         this.toolkit.freeCell(this.rowDefaults);
      }

      this.rowDefaults = null;
      this.invalidate();
   }

   public Cell getCell(C widget) {
      int i = 0;

      for(int n = this.cells.size(); i < n; ++i) {
         Cell c = (Cell)this.cells.get(i);
         if (c.widget == widget) {
            return c;
         }
      }

      return null;
   }

   public List<Cell> getCells() {
      return this.cells;
   }

   public void setToolkit(K toolkit) {
      this.toolkit = toolkit;
   }

   public T getTable() {
      return this.table;
   }

   public void setTable(T table) {
      this.table = table;
   }

   public float getMinWidth() {
      if (this.sizeInvalid) {
         this.computeSize();
      }

      return this.tableMinWidth;
   }

   public float getMinHeight() {
      if (this.sizeInvalid) {
         this.computeSize();
      }

      return this.tableMinHeight;
   }

   public float getPrefWidth() {
      if (this.sizeInvalid) {
         this.computeSize();
      }

      return this.tablePrefWidth;
   }

   public float getPrefHeight() {
      if (this.sizeInvalid) {
         this.computeSize();
      }

      return this.tablePrefHeight;
   }

   public Cell defaults() {
      return this.cellDefaults;
   }

   public L pad(Value pad) {
      this.padTop = pad;
      this.padLeft = pad;
      this.padBottom = pad;
      this.padRight = pad;
      this.sizeInvalid = true;
      return this;
   }

   public L pad(Value top, Value left, Value bottom, Value right) {
      this.padTop = top;
      this.padLeft = left;
      this.padBottom = bottom;
      this.padRight = right;
      this.sizeInvalid = true;
      return this;
   }

   public L padTop(Value padTop) {
      this.padTop = padTop;
      this.sizeInvalid = true;
      return this;
   }

   public L padLeft(Value padLeft) {
      this.padLeft = padLeft;
      this.sizeInvalid = true;
      return this;
   }

   public L padBottom(Value padBottom) {
      this.padBottom = padBottom;
      this.sizeInvalid = true;
      return this;
   }

   public L padRight(Value padRight) {
      this.padRight = padRight;
      this.sizeInvalid = true;
      return this;
   }

   public L pad(float pad) {
      this.padTop = new Value.FixedValue(pad);
      this.padLeft = new Value.FixedValue(pad);
      this.padBottom = new Value.FixedValue(pad);
      this.padRight = new Value.FixedValue(pad);
      this.sizeInvalid = true;
      return this;
   }

   public L pad(float top, float left, float bottom, float right) {
      this.padTop = new Value.FixedValue(top);
      this.padLeft = new Value.FixedValue(left);
      this.padBottom = new Value.FixedValue(bottom);
      this.padRight = new Value.FixedValue(right);
      this.sizeInvalid = true;
      return this;
   }

   public L padTop(float padTop) {
      this.padTop = new Value.FixedValue(padTop);
      this.sizeInvalid = true;
      return this;
   }

   public L padLeft(float padLeft) {
      this.padLeft = new Value.FixedValue(padLeft);
      this.sizeInvalid = true;
      return this;
   }

   public L padBottom(float padBottom) {
      this.padBottom = new Value.FixedValue(padBottom);
      this.sizeInvalid = true;
      return this;
   }

   public L padRight(float padRight) {
      this.padRight = new Value.FixedValue(padRight);
      this.sizeInvalid = true;
      return this;
   }

   public L align(int align) {
      this.align = align;
      return this;
   }

   public L center() {
      this.align = 1;
      return this;
   }

   public L top() {
      this.align |= 2;
      this.align &= -5;
      return this;
   }

   public L left() {
      this.align |= 8;
      this.align &= -17;
      return this;
   }

   public L bottom() {
      this.align |= 4;
      this.align &= -3;
      return this;
   }

   public L right() {
      this.align |= 16;
      this.align &= -9;
      return this;
   }

   public L debug() {
      this.debug = BaseTableLayout.Debug.all;
      this.invalidate();
      return this;
   }

   public L debugTable() {
      this.debug = BaseTableLayout.Debug.table;
      this.invalidate();
      return this;
   }

   public L debugCell() {
      this.debug = BaseTableLayout.Debug.cell;
      this.invalidate();
      return this;
   }

   public L debugWidget() {
      this.debug = BaseTableLayout.Debug.widget;
      this.invalidate();
      return this;
   }

   public L debug(BaseTableLayout.Debug debug) {
      this.debug = debug;
      if (debug == BaseTableLayout.Debug.none) {
         this.toolkit.clearDebugRectangles(this);
      } else {
         this.invalidate();
      }

      return this;
   }

   public BaseTableLayout.Debug getDebug() {
      return this.debug;
   }

   public Value getPadTopValue() {
      return this.padTop;
   }

   public float getPadTop() {
      return this.padTop == null ? 0.0F : this.padTop.height(this.table);
   }

   public Value getPadLeftValue() {
      return this.padLeft;
   }

   public float getPadLeft() {
      return this.padLeft == null ? 0.0F : this.padLeft.width(this.table);
   }

   public Value getPadBottomValue() {
      return this.padBottom;
   }

   public float getPadBottom() {
      return this.padBottom == null ? 0.0F : this.padBottom.height(this.table);
   }

   public Value getPadRightValue() {
      return this.padRight;
   }

   public float getPadRight() {
      return this.padRight == null ? 0.0F : this.padRight.width(this.table);
   }

   public int getAlign() {
      return this.align;
   }

   public int getRow(float y) {
      int row = 0;
      y += this.h(this.padTop);
      int i = 0;
      int n = this.cells.size();
      if (n == 0) {
         return -1;
      } else if (n == 1) {
         return 0;
      } else {
         Cell c;
         if (((Cell)this.cells.get(0)).widgetY < ((Cell)this.cells.get(1)).widgetY) {
            while(i < n) {
               c = (Cell)this.cells.get(i++);
               if (!c.getIgnore()) {
                  if (c.widgetY + c.computedPadTop > y) {
                     break;
                  }

                  if (c.endRow) {
                     ++row;
                  }
               }
            }

            return row - 1;
         } else {
            while(i < n) {
               c = (Cell)this.cells.get(i++);
               if (!c.getIgnore()) {
                  if (c.widgetY + c.computedPadTop < y) {
                     break;
                  }

                  if (c.endRow) {
                     ++row;
                  }
               }
            }

            return row;
         }
      }
   }

   private float[] ensureSize(float[] array, int size) {
      if (array != null && array.length >= size) {
         int i = 0;

         for(int n = array.length; i < n; ++i) {
            array[i] = 0.0F;
         }

         return array;
      } else {
         return new float[size];
      }
   }

   private float w(Value value) {
      return value == null ? 0.0F : value.width(this.table);
   }

   private float h(Value value) {
      return value == null ? 0.0F : value.height(this.table);
   }

   private float w(Value value, Cell cell) {
      return value == null ? 0.0F : value.width(cell);
   }

   private float h(Value value, Cell cell) {
      return value == null ? 0.0F : value.height(cell);
   }

   private void computeSize() {
      this.sizeInvalid = false;
      Toolkit toolkit = this.toolkit;
      ArrayList<Cell> cells = this.cells;
      if (cells.size() > 0 && !((Cell)cells.get(cells.size() - 1)).endRow) {
         this.endRow();
      }

      this.columnMinWidth = this.ensureSize(this.columnMinWidth, this.columns);
      this.rowMinHeight = this.ensureSize(this.rowMinHeight, this.rows);
      this.columnPrefWidth = this.ensureSize(this.columnPrefWidth, this.columns);
      this.rowPrefHeight = this.ensureSize(this.rowPrefHeight, this.rows);
      this.columnWidth = this.ensureSize(this.columnWidth, this.columns);
      this.rowHeight = this.ensureSize(this.rowHeight, this.rows);
      this.expandWidth = this.ensureSize(this.expandWidth, this.columns);
      this.expandHeight = this.ensureSize(this.expandHeight, this.rows);
      float spaceRightLast = 0.0F;
      int i = 0;

      int n;
      Cell c;
      float prefWidth;
      float prefHeight;
      float spannedMinWidth;
      float vpadding;
      float totalExpandWidth;
      float extraMinWidth;
      float extraPrefWidth;
      float uniformPrefHeight;
      for(n = cells.size(); i < n; ++i) {
         c = (Cell)cells.get(i);
         if (!c.ignore) {
            if (c.expandY != 0 && this.expandHeight[c.row] == 0.0F) {
               this.expandHeight[c.row] = (float)c.expandY;
            }

            if (c.colspan == 1 && c.expandX != 0 && this.expandWidth[c.column] == 0.0F) {
               this.expandWidth[c.column] = (float)c.expandX;
            }

            c.computedPadLeft = this.w(c.padLeft, c) + (c.column == 0 ? 0.0F : Math.max(0.0F, this.w(c.spaceLeft, c) - spaceRightLast));
            c.computedPadTop = this.h(c.padTop, c);
            if (c.cellAboveIndex != -1) {
               Cell above = (Cell)cells.get(c.cellAboveIndex);
               c.computedPadTop += Math.max(0.0F, this.h(c.spaceTop, c) - this.h(above.spaceBottom, above));
            }

            uniformPrefHeight = this.w(c.spaceRight, c);
            c.computedPadRight = this.w(c.padRight, c) + (c.column + c.colspan == this.columns ? 0.0F : uniformPrefHeight);
            c.computedPadBottom = this.h(c.padBottom, c) + (c.row == this.rows - 1 ? 0.0F : this.h(c.spaceBottom, c));
            spaceRightLast = uniformPrefHeight;
            prefWidth = c.prefWidth.get(c);
            prefHeight = c.prefHeight.get(c);
            spannedMinWidth = c.minWidth.get(c);
            vpadding = c.minHeight.get(c);
            totalExpandWidth = c.maxWidth.get(c);
            extraMinWidth = c.maxHeight.get(c);
            if (prefWidth < spannedMinWidth) {
               prefWidth = spannedMinWidth;
            }

            if (prefHeight < vpadding) {
               prefHeight = vpadding;
            }

            if (totalExpandWidth > 0.0F && prefWidth > totalExpandWidth) {
               prefWidth = totalExpandWidth;
            }

            if (extraMinWidth > 0.0F && prefHeight > extraMinWidth) {
               prefHeight = extraMinWidth;
            }

            if (c.colspan == 1) {
               extraPrefWidth = c.computedPadLeft + c.computedPadRight;
               this.columnPrefWidth[c.column] = Math.max(this.columnPrefWidth[c.column], prefWidth + extraPrefWidth);
               this.columnMinWidth[c.column] = Math.max(this.columnMinWidth[c.column], spannedMinWidth + extraPrefWidth);
            }

            extraPrefWidth = c.computedPadTop + c.computedPadBottom;
            this.rowPrefHeight[c.row] = Math.max(this.rowPrefHeight[c.row], prefHeight + extraPrefWidth);
            this.rowMinHeight[c.row] = Math.max(this.rowMinHeight[c.row], vpadding + extraPrefWidth);
         }
      }

      i = 0;

      int i;
      label236:
      for(n = cells.size(); i < n; ++i) {
         c = (Cell)cells.get(i);
         if (!c.ignore && c.expandX != 0) {
            int column = c.column;

            for(i = column + c.colspan; column < i; ++column) {
               if (this.expandWidth[column] != 0.0F) {
                  continue label236;
               }
            }

            column = c.column;

            for(i = column + c.colspan; column < i; ++column) {
               this.expandWidth[column] = (float)c.expandX;
            }
         }
      }

      i = 0;

      for(n = cells.size(); i < n; ++i) {
         c = (Cell)cells.get(i);
         if (!c.ignore && c.colspan != 1) {
            uniformPrefHeight = c.minWidth.get(c);
            prefWidth = c.prefWidth.get(c);
            prefHeight = c.maxWidth.get(c);
            if (prefWidth < uniformPrefHeight) {
               prefWidth = uniformPrefHeight;
            }

            if (prefHeight > 0.0F && prefWidth > prefHeight) {
               prefWidth = prefHeight;
            }

            spannedMinWidth = -(c.computedPadLeft + c.computedPadRight);
            vpadding = spannedMinWidth;
            int column = c.column;

            int column;
            for(column = column + c.colspan; column < column; ++column) {
               spannedMinWidth += this.columnMinWidth[column];
               vpadding += this.columnPrefWidth[column];
            }

            totalExpandWidth = 0.0F;
            column = c.column;

            for(int nn = column + c.colspan; column < nn; ++column) {
               totalExpandWidth += this.expandWidth[column];
            }

            extraMinWidth = Math.max(0.0F, uniformPrefHeight - spannedMinWidth);
            extraPrefWidth = Math.max(0.0F, prefWidth - vpadding);
            int column = c.column;

            for(int nn = column + c.colspan; column < nn; ++column) {
               float ratio = totalExpandWidth == 0.0F ? 1.0F / (float)c.colspan : this.expandWidth[column] / totalExpandWidth;
               float[] var10000 = this.columnMinWidth;
               var10000[column] += extraMinWidth * ratio;
               var10000 = this.columnPrefWidth;
               var10000[column] += extraPrefWidth * ratio;
            }
         }
      }

      float uniformMinWidth = 0.0F;
      float uniformMinHeight = 0.0F;
      float uniformPrefWidth = 0.0F;
      uniformPrefHeight = 0.0F;
      i = 0;

      int n;
      Cell c;
      for(n = cells.size(); i < n; ++i) {
         c = (Cell)cells.get(i);
         if (!c.ignore) {
            if (c.uniformX == Boolean.TRUE && c.colspan == 1) {
               vpadding = c.computedPadLeft + c.computedPadRight;
               uniformMinWidth = Math.max(uniformMinWidth, this.columnMinWidth[c.column] - vpadding);
               uniformPrefWidth = Math.max(uniformPrefWidth, this.columnPrefWidth[c.column] - vpadding);
            }

            if (c.uniformY == Boolean.TRUE) {
               vpadding = c.computedPadTop + c.computedPadBottom;
               uniformMinHeight = Math.max(uniformMinHeight, this.rowMinHeight[c.row] - vpadding);
               uniformPrefHeight = Math.max(uniformPrefHeight, this.rowPrefHeight[c.row] - vpadding);
            }
         }
      }

      if (uniformPrefWidth > 0.0F || uniformPrefHeight > 0.0F) {
         i = 0;

         for(n = cells.size(); i < n; ++i) {
            c = (Cell)cells.get(i);
            if (!c.ignore) {
               if (uniformPrefWidth > 0.0F && c.uniformX == Boolean.TRUE && c.colspan == 1) {
                  vpadding = c.computedPadLeft + c.computedPadRight;
                  this.columnMinWidth[c.column] = uniformMinWidth + vpadding;
                  this.columnPrefWidth[c.column] = uniformPrefWidth + vpadding;
               }

               if (uniformPrefHeight > 0.0F && c.uniformY == Boolean.TRUE) {
                  vpadding = c.computedPadTop + c.computedPadBottom;
                  this.rowMinHeight[c.row] = uniformMinHeight + vpadding;
                  this.rowPrefHeight[c.row] = uniformPrefHeight + vpadding;
               }
            }
         }
      }

      this.tableMinWidth = 0.0F;
      this.tableMinHeight = 0.0F;
      this.tablePrefWidth = 0.0F;
      this.tablePrefHeight = 0.0F;

      for(i = 0; i < this.columns; ++i) {
         this.tableMinWidth += this.columnMinWidth[i];
         this.tablePrefWidth += this.columnPrefWidth[i];
      }

      for(i = 0; i < this.rows; ++i) {
         this.tableMinHeight += this.rowMinHeight[i];
         this.tablePrefHeight += Math.max(this.rowMinHeight[i], this.rowPrefHeight[i]);
      }

      prefWidth = this.w(this.padLeft) + this.w(this.padRight);
      prefHeight = this.h(this.padTop) + this.h(this.padBottom);
      this.tableMinWidth += prefWidth;
      this.tableMinHeight += prefHeight;
      this.tablePrefWidth = Math.max(this.tablePrefWidth + prefWidth, this.tableMinWidth);
      this.tablePrefHeight = Math.max(this.tablePrefHeight + prefHeight, this.tableMinHeight);
   }

   public void layout(float layoutX, float layoutY, float layoutWidth, float layoutHeight) {
      Toolkit toolkit = this.toolkit;
      ArrayList<Cell> cells = this.cells;
      if (this.sizeInvalid) {
         this.computeSize();
      }

      float padLeft = this.w(this.padLeft);
      float hpadding = padLeft + this.w(this.padRight);
      float padTop = this.h(this.padTop);
      float vpadding = padTop + this.h(this.padBottom);
      float totalExpandWidth = 0.0F;
      float totalExpandHeight = 0.0F;

      int i;
      for(i = 0; i < this.columns; ++i) {
         totalExpandWidth += this.expandWidth[i];
      }

      for(i = 0; i < this.rows; ++i) {
         totalExpandHeight += this.expandHeight[i];
      }

      float totalGrowWidth = this.tablePrefWidth - this.tableMinWidth;
      float tableWidth;
      float tableHeight;
      float[] columnWeightedWidth;
      if (totalGrowWidth == 0.0F) {
         columnWeightedWidth = this.columnMinWidth;
      } else {
         float extraWidth = Math.min(totalGrowWidth, Math.max(0.0F, layoutWidth - this.tableMinWidth));
         columnWeightedWidth = this.columnWeightedWidth = this.ensureSize(this.columnWeightedWidth, this.columns);

         for(int i = 0; i < this.columns; ++i) {
            tableWidth = this.columnPrefWidth[i] - this.columnMinWidth[i];
            tableHeight = tableWidth / totalGrowWidth;
            columnWeightedWidth[i] = this.columnMinWidth[i] + extraWidth * tableHeight;
         }
      }

      float totalGrowHeight = this.tablePrefHeight - this.tableMinHeight;
      float x;
      float y;
      float[] rowWeightedHeight;
      int i;
      if (totalGrowHeight == 0.0F) {
         rowWeightedHeight = this.rowMinHeight;
      } else {
         rowWeightedHeight = this.rowWeightedHeight = this.ensureSize(this.rowWeightedHeight, this.rows);
         tableWidth = Math.min(totalGrowHeight, Math.max(0.0F, layoutHeight - this.tableMinHeight));

         for(i = 0; i < this.rows; ++i) {
            x = this.rowPrefHeight[i] - this.rowMinHeight[i];
            y = x / totalGrowHeight;
            rowWeightedHeight[i] = this.rowMinHeight[i] + tableWidth * y;
         }
      }

      int i = 0;

      int column;
      int nn;
      float spannedCellWidth;
      float maxHeight;
      Cell c;
      float currentX;
      float currentY;
      for(i = cells.size(); i < i; ++i) {
         c = (Cell)cells.get(i);
         if (!c.ignore) {
            y = 0.0F;
            column = c.column;

            for(nn = column + c.colspan; column < nn; ++column) {
               y += columnWeightedWidth[column];
            }

            currentX = rowWeightedHeight[c.row];
            currentY = c.prefWidth.get(c);
            float prefHeight = c.prefHeight.get(c);
            float minWidth = c.minWidth.get(c);
            float minHeight = c.minHeight.get(c);
            spannedCellWidth = c.maxWidth.get(c);
            maxHeight = c.maxHeight.get(c);
            if (currentY < minWidth) {
               currentY = minWidth;
            }

            if (prefHeight < minHeight) {
               prefHeight = minHeight;
            }

            if (spannedCellWidth > 0.0F && currentY > spannedCellWidth) {
               currentY = spannedCellWidth;
            }

            if (maxHeight > 0.0F && prefHeight > maxHeight) {
               prefHeight = maxHeight;
            }

            c.widgetWidth = Math.min(y - c.computedPadLeft - c.computedPadRight, currentY);
            c.widgetHeight = Math.min(currentX - c.computedPadTop - c.computedPadBottom, prefHeight);
            if (c.colspan == 1) {
               this.columnWidth[c.column] = Math.max(this.columnWidth[c.column], y);
            }

            this.rowHeight[c.row] = Math.max(this.rowHeight[c.row], currentX);
         }
      }

      float[] var10000;
      int i;
      int i;
      if (totalExpandWidth > 0.0F) {
         tableWidth = layoutWidth - hpadding;

         for(i = 0; i < this.columns; ++i) {
            tableWidth -= this.columnWidth[i];
         }

         tableHeight = 0.0F;
         i = 0;

         for(i = 0; i < this.columns; ++i) {
            if (this.expandWidth[i] != 0.0F) {
               currentX = tableWidth * this.expandWidth[i] / totalExpandWidth;
               var10000 = this.columnWidth;
               var10000[i] += currentX;
               tableHeight += currentX;
               i = i;
            }
         }

         var10000 = this.columnWidth;
         var10000[i] += tableWidth - tableHeight;
      }

      if (totalExpandHeight > 0.0F) {
         tableWidth = layoutHeight - vpadding;

         for(i = 0; i < this.rows; ++i) {
            tableWidth -= this.rowHeight[i];
         }

         tableHeight = 0.0F;
         i = 0;

         for(i = 0; i < this.rows; ++i) {
            if (this.expandHeight[i] != 0.0F) {
               currentX = tableWidth * this.expandHeight[i] / totalExpandHeight;
               var10000 = this.rowHeight;
               var10000[i] += currentX;
               tableHeight += currentX;
               i = i;
            }
         }

         var10000 = this.rowHeight;
         var10000[i] += tableWidth - tableHeight;
      }

      i = 0;

      for(i = cells.size(); i < i; ++i) {
         c = (Cell)cells.get(i);
         if (!c.ignore && c.colspan != 1) {
            y = 0.0F;
            column = c.column;

            for(nn = column + c.colspan; column < nn; ++column) {
               y += columnWeightedWidth[column] - this.columnWidth[column];
            }

            y -= Math.max(0.0F, c.computedPadLeft + c.computedPadRight);
            y /= (float)c.colspan;
            if (y > 0.0F) {
               column = c.column;

               for(nn = column + c.colspan; column < nn; ++column) {
                  var10000 = this.columnWidth;
                  var10000[column] += y;
               }
            }
         }
      }

      tableWidth = hpadding;
      tableHeight = vpadding;

      for(i = 0; i < this.columns; ++i) {
         tableWidth += this.columnWidth[i];
      }

      for(i = 0; i < this.rows; ++i) {
         tableHeight += this.rowHeight[i];
      }

      x = layoutX + padLeft;
      if ((this.align & 16) != 0) {
         x += layoutWidth - tableWidth;
      } else if ((this.align & 8) == 0) {
         x += (layoutWidth - tableWidth) / 2.0F;
      }

      y = layoutY + padTop;
      if ((this.align & 4) != 0) {
         y += layoutHeight - tableHeight;
      } else if ((this.align & 2) == 0) {
         y += (layoutHeight - tableHeight) / 2.0F;
      }

      currentX = x;
      currentY = y;
      int i = 0;

      int nn;
      int n;
      Cell c;
      int column;
      for(n = cells.size(); i < n; ++i) {
         c = (Cell)cells.get(i);
         if (!c.ignore) {
            spannedCellWidth = 0.0F;
            column = c.column;

            for(nn = column + c.colspan; column < nn; ++column) {
               spannedCellWidth += this.columnWidth[column];
            }

            spannedCellWidth -= c.computedPadLeft + c.computedPadRight;
            currentX += c.computedPadLeft;
            if (c.fillX > 0.0F) {
               c.widgetWidth = spannedCellWidth * c.fillX;
               maxHeight = c.maxWidth.get(c);
               if (maxHeight > 0.0F) {
                  c.widgetWidth = Math.min(c.widgetWidth, maxHeight);
               }
            }

            if (c.fillY > 0.0F) {
               c.widgetHeight = this.rowHeight[c.row] * c.fillY - c.computedPadTop - c.computedPadBottom;
               maxHeight = c.maxHeight.get(c);
               if (maxHeight > 0.0F) {
                  c.widgetHeight = Math.min(c.widgetHeight, maxHeight);
               }
            }

            if ((c.align & 8) != 0) {
               c.widgetX = currentX;
            } else if ((c.align & 16) != 0) {
               c.widgetX = currentX + spannedCellWidth - c.widgetWidth;
            } else {
               c.widgetX = currentX + (spannedCellWidth - c.widgetWidth) / 2.0F;
            }

            if ((c.align & 2) != 0) {
               c.widgetY = currentY + c.computedPadTop;
            } else if ((c.align & 4) != 0) {
               c.widgetY = currentY + this.rowHeight[c.row] - c.widgetHeight - c.computedPadBottom;
            } else {
               c.widgetY = currentY + (this.rowHeight[c.row] - c.widgetHeight + c.computedPadTop - c.computedPadBottom) / 2.0F;
            }

            if (c.endRow) {
               currentX = x;
               currentY += this.rowHeight[c.row];
            } else {
               currentX += spannedCellWidth + c.computedPadRight;
            }
         }
      }

      if (this.debug != BaseTableLayout.Debug.none) {
         toolkit.clearDebugRectangles(this);
         currentX = x;
         currentY = y;
         if (this.debug == BaseTableLayout.Debug.table || this.debug == BaseTableLayout.Debug.all) {
            toolkit.addDebugRectangle(this, BaseTableLayout.Debug.table, layoutX, layoutY, layoutWidth, layoutHeight);
            toolkit.addDebugRectangle(this, BaseTableLayout.Debug.table, x, y, tableWidth - hpadding, tableHeight - vpadding);
         }

         i = 0;

         for(n = cells.size(); i < n; ++i) {
            c = (Cell)cells.get(i);
            if (!c.ignore) {
               if (this.debug == BaseTableLayout.Debug.widget || this.debug == BaseTableLayout.Debug.all) {
                  toolkit.addDebugRectangle(this, BaseTableLayout.Debug.widget, c.widgetX, c.widgetY, c.widgetWidth, c.widgetHeight);
               }

               spannedCellWidth = 0.0F;
               column = c.column;

               for(nn = column + c.colspan; column < nn; ++column) {
                  spannedCellWidth += this.columnWidth[column];
               }

               spannedCellWidth -= c.computedPadLeft + c.computedPadRight;
               currentX += c.computedPadLeft;
               if (this.debug == BaseTableLayout.Debug.cell || this.debug == BaseTableLayout.Debug.all) {
                  toolkit.addDebugRectangle(this, BaseTableLayout.Debug.cell, currentX, currentY + c.computedPadTop, spannedCellWidth, this.rowHeight[c.row] - c.computedPadTop - c.computedPadBottom);
               }

               if (c.endRow) {
                  currentX = x;
                  currentY += this.rowHeight[c.row];
               } else {
                  currentX += spannedCellWidth + c.computedPadRight;
               }
            }
         }

      }
   }

   public static enum Debug {
      none,
      all,
      table,
      cell,
      widget;
   }
}
