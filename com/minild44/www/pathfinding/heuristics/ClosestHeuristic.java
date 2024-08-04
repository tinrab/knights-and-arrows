package com.minild44.www.pathfinding.heuristics;

import com.minild44.www.pathfinding.AStarHeuristic;
import com.minild44.www.pathfinding.Mover;
import com.minild44.www.pathfinding.TileBasedMap;

public class ClosestHeuristic implements AStarHeuristic {
   public float getCost(TileBasedMap map, Mover mover, int x, int y, int tx, int ty) {
      float dx = (float)(tx - x);
      float dy = (float)(ty - y);
      float result = (float)Math.sqrt((double)(dx * dx + dy * dy));
      return result;
   }
}
