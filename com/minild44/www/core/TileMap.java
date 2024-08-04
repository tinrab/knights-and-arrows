package com.minild44.www.core;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.minild44.www.core.entities.Unit;
import com.minild44.www.pathfinding.AStarPathFinder;
import com.minild44.www.pathfinding.Path;
import com.minild44.www.pathfinding.PathFindingContext;
import com.minild44.www.pathfinding.TileBasedMap;
import com.minild44.www.util.Camera;

public abstract class TileMap implements TileBasedMap {
   protected TiledMap map;
   private int tileSize = 32;
   protected OrthogonalTiledMapRenderer mapRenderer;

   public TileMap(String path) {
      this.map = (new TmxMapLoader()).load(path);
      this.mapRenderer = new OrthogonalTiledMapRenderer(this.map, 4.0F);
   }

   public int getWidthInTiles() {
      return (Integer)this.map.getProperties().get("width");
   }

   public int getHeightInTiles() {
      return (Integer)this.map.getProperties().get("height");
   }

   public Path getPath(int sx, int sy, int ex, int ey, int maxDistance, Unit unit) {
      AStarPathFinder finder = new AStarPathFinder(this, maxDistance, true);
      return sx >= 0 && sy >= 0 && ex >= 0 && ey >= 0 ? finder.findPath(unit, sx, sy, ex, ey) : null;
   }

   public TiledMap getMap() {
      return this.map;
   }

   public void pathFinderVisited(int x, int y) {
   }

   public boolean isBlocked(int tx, int ty) {
      TiledMapTileLayer walls = (TiledMapTileLayer)this.map.getLayers().get("collision");
      return walls.getCell(tx, ty) != null;
   }

   public boolean blocked(PathFindingContext context, int tx, int ty) {
      int sx = context.getSourceX();
      int sy = context.getSourceY();
      if (tx != sx && ty != sy) {
         if (this.isBlocked(tx, ty)) {
            return true;
         } else if (sx > tx && sy < ty) {
            return this.isBlocked(sx - 1, sy) || this.isBlocked(sx, sy + 1);
         } else if (sx < tx && sy < ty) {
            return this.isBlocked(sx + 1, sy) || this.isBlocked(sx, sy + 1);
         } else if (sx < tx && sy > ty) {
            return this.isBlocked(sx + 1, sy) || this.isBlocked(sx, sy - 1);
         } else if (sx > tx && sy > ty) {
            return this.isBlocked(sx - 1, sy) || this.isBlocked(sx, sy - 1);
         } else {
            return false;
         }
      } else {
         return this.isBlocked(tx, ty);
      }
   }

   public float getCost(PathFindingContext context, int tx, int ty) {
      float dx = (float)(context.getSourceX() - tx);
      float dy = (float)(context.getSourceY() - ty);
      return dx * dx + dy * dy;
   }

   public boolean canSee(Vector2 v1, Vector2 v2, float maxDistance) {
      if (maxDistance != -1.0F) {
         float dst = v1.dst(v2);
         if (dst > maxDistance) {
            return false;
         }
      }

      Vector2 ray = this.castRay(v1, v2);
      return ray.equals(v2);
   }

   public Vector2 castRay(Vector2 v1, Vector2 v2) {
      Vector2 p1 = new Vector2(v1.x / (float)this.tileSize, v1.y / (float)this.tileSize);
      Vector2 p2 = new Vector2(v2.x / (float)this.tileSize, v2.y / (float)this.tileSize);
      if ((int)p1.x == (int)p2.x && (int)p1.y == (int)p2.y) {
         return v2;
      } else if (!(p1.x < 0.0F) && !(p1.x > (float)this.getWidthInTiles()) && !(p1.y < 0.0F) && !(p1.y > (float)this.getHeightInTiles()) && !(p2.x < 0.0F) && !(p2.x > (float)this.getWidthInTiles()) && !(p2.y < 0.0F) && !(p2.y > (float)this.getHeightInTiles())) {
         int stepX = p2.x > p1.x ? 1 : -1;
         int stepY = p2.y > p1.y ? 1 : -1;
         Vector2 rayDirection = new Vector2(p2.x - p1.x, p2.y - p1.y);
         float ratioX = rayDirection.x / rayDirection.y;
         float ratioY = rayDirection.y / rayDirection.x;
         float deltaY = p2.x - p1.x;
         float deltaX = p2.y - p1.y;
         deltaX = deltaX < 0.0F ? -deltaX : deltaX;
         deltaY = deltaY < 0.0F ? -deltaY : deltaY;
         int testX = (int)p1.x;
         int testY = (int)p1.y;
         float maxX = deltaX * (stepX > 0 ? 1.0F - p1.x % 1.0F : p1.x % 1.0F);
         float maxY = deltaY * (stepY > 0 ? 1.0F - p1.y % 1.0F : p1.y % 1.0F);
         int endTileX = (int)p2.x;
         int endTileY = (int)p2.y;
         Vector2 collisionPoint = new Vector2();

         while(testX != endTileX || testY != endTileY) {
            if (maxX < maxY) {
               maxX += deltaX;
               testX += stepX;
               if (this.isBlocked(testX, testY)) {
                  collisionPoint.x = (float)testX;
                  if (stepX < 0) {
                     collisionPoint.x = (float)((double)collisionPoint.x + 1.0D);
                  }

                  collisionPoint.y = p1.y + ratioY * (collisionPoint.x - p1.x);
                  collisionPoint.x *= (float)this.tileSize;
                  collisionPoint.y *= (float)this.tileSize;
                  return collisionPoint;
               }
            } else {
               maxY += deltaY;
               testY += stepY;
               if (this.isBlocked(testX, testY)) {
                  collisionPoint.y = (float)testY;
                  if (stepY < 0) {
                     collisionPoint.y = (float)((double)collisionPoint.y + 1.0D);
                  }

                  collisionPoint.x = p1.x + ratioX * (collisionPoint.y - p1.y);
                  collisionPoint.x *= (float)this.tileSize;
                  collisionPoint.y *= (float)this.tileSize;
                  return collisionPoint;
               }
            }
         }

         return v2;
      } else {
         return v1;
      }
   }

   public void setView(Camera camera) {
      this.mapRenderer.setView(camera);
   }

   public void renderLayers(String tag, Matrix4 projection) {
      this.renderLayers(tag, projection, Color.WHITE);
   }

   public void renderLayers(String tag, Matrix4 projection, Color color) {
      this.mapRenderer.getSpriteBatch().begin();
      this.mapRenderer.getSpriteBatch().setProjectionMatrix(projection);
      this.mapRenderer.getSpriteBatch().setColor(color);

      for(int i = 0; i < this.map.getLayers().getCount(); ++i) {
         MapLayer layer = this.map.getLayers().get(i);
         if (layer.getProperties().containsKey(tag)) {
            this.mapRenderer.renderTileLayer((TiledMapTileLayer)layer);
         }
      }

      this.mapRenderer.getSpriteBatch().setColor(Color.WHITE);
      this.mapRenderer.getSpriteBatch().end();
   }

   public MapObjects getObjects(String layer) {
      return this.map.getLayers().get(layer).getObjects();
   }

   public void dispose() {
      this.map.dispose();
      this.mapRenderer.dispose();
   }
}
