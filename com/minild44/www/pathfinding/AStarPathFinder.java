package com.minild44.www.pathfinding;

import com.minild44.www.pathfinding.heuristics.ClosestHeuristic;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AStarPathFinder implements PathFinder, PathFindingContext {
   private ArrayList closed;
   private AStarPathFinder.PriorityList open;
   private TileBasedMap map;
   private int maxSearchDistance;
   private AStarPathFinder.Node[][] nodes;
   private boolean allowDiagMovement;
   private AStarHeuristic heuristic;
   private AStarPathFinder.Node current;
   private Mover mover;
   private int sourceX;
   private int sourceY;
   private int distance;

   public AStarPathFinder(TileBasedMap map, int maxSearchDistance, boolean allowDiagMovement) {
      this(map, maxSearchDistance, allowDiagMovement, new ClosestHeuristic());
   }

   public AStarPathFinder(TileBasedMap map, int maxSearchDistance, boolean allowDiagMovement, AStarHeuristic heuristic) {
      this.closed = new ArrayList();
      this.open = new AStarPathFinder.PriorityList((AStarPathFinder.PriorityList)null);
      this.heuristic = heuristic;
      this.map = map;
      this.maxSearchDistance = maxSearchDistance;
      this.allowDiagMovement = allowDiagMovement;
      this.nodes = new AStarPathFinder.Node[map.getWidthInTiles()][map.getHeightInTiles()];

      for(int x = 0; x < map.getWidthInTiles(); ++x) {
         for(int y = 0; y < map.getHeightInTiles(); ++y) {
            this.nodes[x][y] = new AStarPathFinder.Node(x, y);
         }
      }

   }

   public Path findPath(Mover mover, int sx, int sy, int tx, int ty) {
      this.current = null;
      this.mover = mover;
      this.sourceX = tx;
      this.sourceY = ty;
      this.distance = 0;
      if (this.map.blocked(this, tx, ty)) {
         return null;
      } else {
         int maxDepth;
         int lx;
         for(maxDepth = 0; maxDepth < this.map.getWidthInTiles(); ++maxDepth) {
            for(lx = 0; lx < this.map.getHeightInTiles(); ++lx) {
               this.nodes[maxDepth][lx].reset();
            }
         }

         this.nodes[sx][sy].cost = 0.0F;
         this.nodes[sx][sy].depth = 0;
         this.closed.clear();
         this.open.clear();
         this.addToOpen(this.nodes[sx][sy]);
         this.nodes[tx][ty].parent = null;
         maxDepth = 0;

         while(maxDepth < this.maxSearchDistance && this.open.size() != 0) {
            lx = sx;
            int ly = sy;
            if (this.current != null) {
               lx = this.current.x;
               ly = this.current.y;
            }

            this.current = this.getFirstInOpen();
            this.distance = this.current.depth;
            if (this.current == this.nodes[tx][ty] && this.isValidLocation(mover, lx, ly, tx, ty)) {
               break;
            }

            this.removeFromOpen(this.current);
            this.addToClosed(this.current);

            for(int x = -1; x < 2; ++x) {
               for(int y = -1; y < 2; ++y) {
                  if ((x != 0 || y != 0) && (this.allowDiagMovement || x == 0 || y == 0)) {
                     int xp = x + this.current.x;
                     int yp = y + this.current.y;
                     if (this.isValidLocation(mover, this.current.x, this.current.y, xp, yp)) {
                        float nextStepCost = this.current.cost + this.getMovementCost(mover, this.current.x, this.current.y, xp, yp);
                        AStarPathFinder.Node neighbour = this.nodes[xp][yp];
                        this.map.pathFinderVisited(xp, yp);
                        if (nextStepCost < neighbour.cost) {
                           if (this.inOpenList(neighbour)) {
                              this.removeFromOpen(neighbour);
                           }

                           if (this.inClosedList(neighbour)) {
                              this.removeFromClosed(neighbour);
                           }
                        }

                        if (!this.inOpenList(neighbour) && !this.inClosedList(neighbour)) {
                           neighbour.cost = nextStepCost;
                           neighbour.heuristic = this.getHeuristicCost(mover, xp, yp, tx, ty);
                           maxDepth = Math.max(maxDepth, neighbour.setParent(this.current));
                           this.addToOpen(neighbour);
                        }
                     }
                  }
               }
            }
         }

         if (this.nodes[tx][ty].parent == null) {
            return null;
         } else {
            Path path = new Path();

            for(AStarPathFinder.Node target = this.nodes[tx][ty]; target != this.nodes[sx][sy]; target = target.parent) {
               path.prependStep((float)target.x, (float)target.y);
            }

            path.prependStep((float)sx, (float)sy);
            return path;
         }
      }
   }

   public int getCurrentX() {
      return this.current == null ? -1 : this.current.x;
   }

   public int getCurrentY() {
      return this.current == null ? -1 : this.current.y;
   }

   protected AStarPathFinder.Node getFirstInOpen() {
      return (AStarPathFinder.Node)this.open.first();
   }

   protected void addToOpen(AStarPathFinder.Node node) {
      node.setOpen(true);
      this.open.add(node);
   }

   protected boolean inOpenList(AStarPathFinder.Node node) {
      return node.isOpen();
   }

   protected void removeFromOpen(AStarPathFinder.Node node) {
      node.setOpen(false);
      this.open.remove(node);
   }

   protected void addToClosed(AStarPathFinder.Node node) {
      node.setClosed(true);
      this.closed.add(node);
   }

   protected boolean inClosedList(AStarPathFinder.Node node) {
      return node.isClosed();
   }

   protected void removeFromClosed(AStarPathFinder.Node node) {
      node.setClosed(false);
      this.closed.remove(node);
   }

   protected boolean isValidLocation(Mover mover, int sx, int sy, int x, int y) {
      boolean invalid = x < 0 || y < 0 || x >= this.map.getWidthInTiles() || y >= this.map.getHeightInTiles();
      if (!invalid && (sx != x || sy != y)) {
         this.mover = mover;
         this.sourceX = sx;
         this.sourceY = sy;
         invalid = this.map.blocked(this, x, y);
      }

      return !invalid;
   }

   public float getMovementCost(Mover mover, int sx, int sy, int tx, int ty) {
      this.mover = mover;
      this.sourceX = sx;
      this.sourceY = sy;
      return this.map.getCost(this, tx, ty);
   }

   public float getHeuristicCost(Mover mover, int x, int y, int tx, int ty) {
      return this.heuristic.getCost(this.map, mover, x, y, tx, ty);
   }

   public Mover getMover() {
      return this.mover;
   }

   public int getSearchDistance() {
      return this.distance;
   }

   public int getSourceX() {
      return this.sourceX;
   }

   public int getSourceY() {
      return this.sourceY;
   }

   private class Node implements Comparable {
      private int x;
      private int y;
      private float cost;
      private AStarPathFinder.Node parent;
      private float heuristic;
      private int depth;
      private boolean open;
      private boolean closed;

      public Node(int x, int y) {
         this.x = x;
         this.y = y;
      }

      public int setParent(AStarPathFinder.Node parent) {
         this.depth = parent.depth + 1;
         this.parent = parent;
         return this.depth;
      }

      public int compareTo(Object other) {
         AStarPathFinder.Node o = (AStarPathFinder.Node)other;
         float f = this.heuristic + this.cost;
         float of = o.heuristic + o.cost;
         if (f < of) {
            return -1;
         } else {
            return f > of ? 1 : 0;
         }
      }

      public void setOpen(boolean open) {
         this.open = open;
      }

      public boolean isOpen() {
         return this.open;
      }

      public void setClosed(boolean closed) {
         this.closed = closed;
      }

      public boolean isClosed() {
         return this.closed;
      }

      public void reset() {
         this.closed = false;
         this.open = false;
         this.cost = 0.0F;
         this.depth = 0;
      }

      public String toString() {
         return "[Node " + this.x + "," + this.y + "]";
      }
   }

   private class PriorityList {
      private List list;

      private PriorityList() {
         this.list = new LinkedList();
      }

      public Object first() {
         return this.list.get(0);
      }

      public void clear() {
         this.list.clear();
      }

      public void add(Object o) {
         for(int i = 0; i < this.list.size(); ++i) {
            if (((Comparable)this.list.get(i)).compareTo(o) > 0) {
               this.list.add(i, o);
               break;
            }
         }

         if (!this.list.contains(o)) {
            this.list.add(o);
         }

      }

      public void remove(Object o) {
         this.list.remove(o);
      }

      public int size() {
         return this.list.size();
      }

      public boolean contains(Object o) {
         return this.list.contains(o);
      }

      public String toString() {
         String temp = "{";

         for(int i = 0; i < this.size(); ++i) {
            temp = temp + this.list.get(i).toString() + ",";
         }

         temp = temp + "}";
         return temp;
      }

      // $FF: synthetic method
      PriorityList(AStarPathFinder.PriorityList var2) {
         this();
      }
   }
}
