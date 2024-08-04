package com.minild44.www.pathfinding.heuristics;

import com.minild44.www.pathfinding.AStarHeuristic;
import com.minild44.www.pathfinding.Mover;
import com.minild44.www.pathfinding.TileBasedMap;

public class ManhattanHeuristic implements AStarHeuristic {
   private int minimumCost;

   public ManhattanHeuristic(int minimumCost) {
      this.minimumCost = minimumCost;
   }

   public float getCost(TileBasedMap map, Mover mover, int x, int y, int tx, int ty) {
      return (float)(this.minimumCost * (Math.abs(x - tx) + Math.abs(y - ty)));
   }
}
