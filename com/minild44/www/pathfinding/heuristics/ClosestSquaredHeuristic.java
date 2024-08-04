package com.minild44.www.pathfinding.heuristics;

import com.minild44.www.pathfinding.AStarHeuristic;
import com.minild44.www.pathfinding.Mover;
import com.minild44.www.pathfinding.TileBasedMap;

public class ClosestSquaredHeuristic implements AStarHeuristic {
   public float getCost(TileBasedMap map, Mover mover, int x, int y, int tx, int ty) {
      float dx = (float)(tx - x);
      float dy = (float)(ty - y);
      return dx * dx + dy * dy;
   }
}
