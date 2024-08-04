package com.badlogic.gdx.maps;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

public class MapObject {
   private String name = "";
   private float opacity = 1.0F;
   private boolean visible = true;
   private MapProperties properties = new MapProperties();
   private Color color;

   public MapObject() {
      this.color = Color.WHITE.cpy();
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public Color getColor() {
      return this.color;
   }

   public void setColor(Color color) {
      this.color = color;
   }

   public float getOpacity() {
      return this.opacity;
   }

   public void setOpacity(float opacity) {
      this.opacity = opacity;
   }

   public boolean isVisible() {
      return this.visible;
   }

   public void setVisible(boolean visible) {
      this.visible = visible;
   }

   public MapProperties getProperties() {
      return this.properties;
   }

   public Rectangle getBounds() {
      float x = Float.valueOf(this.properties.get("x").toString());
      float y = Float.valueOf(this.properties.get("y").toString());
      float w = Float.valueOf(this.properties.get("width").toString());
      float h = Float.valueOf(this.properties.get("height").toString());
      return new Rectangle(x * 4.0F, y * 4.0F, w * 4.0F, h * 4.0F);
   }
}
