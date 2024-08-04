package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.SerializationException;
import java.lang.reflect.Method;
import java.util.Iterator;

public class Skin implements Disposable {
   ObjectMap<Class, ObjectMap<String, Object>> resources = new ObjectMap();
   TextureAtlas atlas;

   public Skin() {
   }

   public Skin(FileHandle skinFile) {
      FileHandle atlasFile = skinFile.sibling(skinFile.nameWithoutExtension() + ".atlas");
      if (atlasFile.exists()) {
         this.atlas = new TextureAtlas(atlasFile);
         this.addRegions(this.atlas);
      }

      this.load(skinFile);
   }

   public Skin(FileHandle skinFile, TextureAtlas atlas) {
      this.atlas = atlas;
      this.addRegions(atlas);
      this.load(skinFile);
   }

   public Skin(TextureAtlas atlas) {
      this.atlas = atlas;
      this.addRegions(atlas);
   }

   public void load(FileHandle skinFile) {
      try {
         this.getJsonLoader(skinFile).fromJson(Skin.class, skinFile);
      } catch (SerializationException var3) {
         throw new SerializationException("Error reading file: " + skinFile, var3);
      }
   }

   public void addRegions(TextureAtlas atlas) {
      Array<TextureAtlas.AtlasRegion> regions = atlas.getRegions();
      int i = 0;

      for(int n = regions.size; i < n; ++i) {
         TextureAtlas.AtlasRegion region = (TextureAtlas.AtlasRegion)regions.get(i);
         this.add(region.name, region, TextureRegion.class);
      }

   }

   public void add(String name, Object resource) {
      this.add(name, resource, resource.getClass());
   }

   public void add(String name, Object resource, Class type) {
      if (name == null) {
         throw new IllegalArgumentException("name cannot be null.");
      } else if (resource == null) {
         throw new IllegalArgumentException("resource cannot be null.");
      } else {
         ObjectMap<String, Object> typeResources = (ObjectMap)this.resources.get(type);
         if (typeResources == null) {
            typeResources = new ObjectMap();
            this.resources.put(type, typeResources);
         }

         typeResources.put(name, resource);
      }
   }

   public <T> T get(Class<T> type) {
      return this.get("default", type);
   }

   public <T> T get(String name, Class<T> type) {
      if (name == null) {
         throw new IllegalArgumentException("name cannot be null.");
      } else if (type == null) {
         throw new IllegalArgumentException("type cannot be null.");
      } else if (type == Drawable.class) {
         return this.getDrawable(name);
      } else if (type == TextureRegion.class) {
         return this.getRegion(name);
      } else if (type == NinePatch.class) {
         return this.getPatch(name);
      } else if (type == Sprite.class) {
         return this.getSprite(name);
      } else {
         ObjectMap<String, Object> typeResources = (ObjectMap)this.resources.get(type);
         if (typeResources == null) {
            throw new GdxRuntimeException("No " + type.getName() + " registered with name: " + name);
         } else {
            Object resource = typeResources.get(name);
            if (resource == null) {
               throw new GdxRuntimeException("No " + type.getName() + " registered with name: " + name);
            } else {
               return resource;
            }
         }
      }
   }

   public <T> T optional(String name, Class<T> type) {
      if (name == null) {
         throw new IllegalArgumentException("name cannot be null.");
      } else if (type == null) {
         throw new IllegalArgumentException("type cannot be null.");
      } else {
         ObjectMap<String, Object> typeResources = (ObjectMap)this.resources.get(type);
         return typeResources == null ? null : typeResources.get(name);
      }
   }

   public boolean has(String name, Class type) {
      ObjectMap<String, Object> typeResources = (ObjectMap)this.resources.get(type);
      return typeResources == null ? false : typeResources.containsKey(name);
   }

   public <T> ObjectMap<String, T> getAll(Class<T> type) {
      return (ObjectMap)this.resources.get(type);
   }

   public Color getColor(String name) {
      return (Color)this.get(name, Color.class);
   }

   public BitmapFont getFont(String name) {
      return (BitmapFont)this.get(name, BitmapFont.class);
   }

   public TextureRegion getRegion(String name) {
      TextureRegion region = (TextureRegion)this.optional(name, TextureRegion.class);
      if (region != null) {
         return region;
      } else {
         Texture texture = (Texture)this.optional(name, Texture.class);
         if (texture == null) {
            throw new GdxRuntimeException("No TextureRegion or Texture registered with name: " + name);
         } else {
            region = new TextureRegion(texture);
            this.add(name, region, Texture.class);
            return region;
         }
      }
   }

   public TiledDrawable getTiledDrawable(String name) {
      TiledDrawable tiled = (TiledDrawable)this.optional(name, TiledDrawable.class);
      if (tiled != null) {
         return tiled;
      } else {
         Drawable drawable = (Drawable)this.optional(name, Drawable.class);
         if (tiled != null) {
            if (!(drawable instanceof TiledDrawable)) {
               throw new GdxRuntimeException("Drawable found but is not a TiledDrawable: " + name + ", " + drawable.getClass().getName());
            } else {
               return tiled;
            }
         } else {
            tiled = new TiledDrawable(this.getRegion(name));
            this.add(name, tiled, TiledDrawable.class);
            return tiled;
         }
      }
   }

   public NinePatch getPatch(String name) {
      NinePatch patch = (NinePatch)this.optional(name, NinePatch.class);
      if (patch != null) {
         return patch;
      } else {
         try {
            TextureRegion region = this.getRegion(name);
            if (region instanceof TextureAtlas.AtlasRegion) {
               int[] splits = ((TextureAtlas.AtlasRegion)region).splits;
               if (splits != null) {
                  patch = new NinePatch(region, splits[0], splits[1], splits[2], splits[3]);
                  int[] pads = ((TextureAtlas.AtlasRegion)region).pads;
                  if (pads != null) {
                     patch.setPadding(pads[0], pads[1], pads[2], pads[3]);
                  }
               }
            }

            if (patch == null) {
               patch = new NinePatch(region);
            }

            this.add(name, patch, NinePatch.class);
            return patch;
         } catch (GdxRuntimeException var6) {
            throw new GdxRuntimeException("No NinePatch, TextureRegion, or Texture registered with name: " + name);
         }
      }
   }

   public Sprite getSprite(String name) {
      Sprite sprite = (Sprite)this.optional(name, Sprite.class);
      if (sprite != null) {
         return (Sprite)sprite;
      } else {
         try {
            TextureRegion textureRegion = this.getRegion(name);
            if (textureRegion instanceof TextureAtlas.AtlasRegion) {
               TextureAtlas.AtlasRegion region = (TextureAtlas.AtlasRegion)textureRegion;
               if (region.rotate || region.packedWidth != region.originalWidth || region.packedHeight != region.originalHeight) {
                  sprite = new TextureAtlas.AtlasSprite(region);
               }
            }

            if (sprite == null) {
               sprite = new Sprite(textureRegion);
            }

            this.add(name, sprite, NinePatch.class);
            return (Sprite)sprite;
         } catch (GdxRuntimeException var5) {
            throw new GdxRuntimeException("No NinePatch, TextureRegion, or Texture registered with name: " + name);
         }
      }
   }

   public Drawable getDrawable(String name) {
      Drawable drawable = (Drawable)this.optional(name, Drawable.class);
      if (drawable != null) {
         return drawable;
      } else {
         Drawable drawable = (Drawable)this.optional(name, TiledDrawable.class);
         if (drawable != null) {
            return (Drawable)drawable;
         } else {
            try {
               TextureRegion textureRegion = this.getRegion(name);
               if (textureRegion instanceof TextureAtlas.AtlasRegion) {
                  TextureAtlas.AtlasRegion region = (TextureAtlas.AtlasRegion)textureRegion;
                  if (region.splits != null) {
                     drawable = new NinePatchDrawable(this.getPatch(name));
                  } else if (region.rotate || region.packedWidth != region.originalWidth || region.packedHeight != region.originalHeight) {
                     drawable = new SpriteDrawable(this.getSprite(name));
                  }
               }

               if (drawable == null) {
                  drawable = new TextureRegionDrawable(textureRegion);
               }
            } catch (GdxRuntimeException var5) {
            }

            if (drawable == null) {
               NinePatch patch = (NinePatch)this.optional(name, NinePatch.class);
               if (patch != null) {
                  drawable = new NinePatchDrawable(patch);
               } else {
                  Sprite sprite = (Sprite)this.optional(name, Sprite.class);
                  if (sprite == null) {
                     throw new GdxRuntimeException("No Drawable, NinePatch, TextureRegion, Texture, or Sprite registered with name: " + name);
                  }

                  drawable = new SpriteDrawable(sprite);
               }
            }

            this.add(name, drawable, Drawable.class);
            return (Drawable)drawable;
         }
      }
   }

   public String find(Object resource) {
      if (resource == null) {
         throw new IllegalArgumentException("style cannot be null.");
      } else {
         ObjectMap<String, Object> typeResources = (ObjectMap)this.resources.get(resource.getClass());
         return typeResources == null ? null : (String)typeResources.findKey(resource, true);
      }
   }

   public Drawable newDrawable(String name) {
      return this.newDrawable(this.getDrawable(name));
   }

   public Drawable newDrawable(String name, float r, float g, float b, float a) {
      return this.newDrawable(this.getDrawable(name), new Color(r, g, b, a));
   }

   public Drawable newDrawable(String name, Color tint) {
      return this.newDrawable(this.getDrawable(name), tint);
   }

   public Drawable newDrawable(Drawable drawable) {
      if (drawable instanceof TextureRegionDrawable) {
         return new TextureRegionDrawable((TextureRegionDrawable)drawable);
      } else if (drawable instanceof NinePatchDrawable) {
         return new NinePatchDrawable((NinePatchDrawable)drawable);
      } else if (drawable instanceof SpriteDrawable) {
         return new SpriteDrawable((SpriteDrawable)drawable);
      } else {
         throw new GdxRuntimeException("Unable to copy, unknown drawable type: " + drawable.getClass());
      }
   }

   public Drawable newDrawable(Drawable drawable, float r, float g, float b, float a) {
      return this.newDrawable(drawable, new Color(r, g, b, a));
   }

   public Drawable newDrawable(Drawable drawable, Color tint) {
      Object sprite;
      if (drawable instanceof TextureRegionDrawable) {
         TextureRegion region = ((TextureRegionDrawable)drawable).getRegion();
         if (region instanceof TextureAtlas.AtlasRegion) {
            sprite = new TextureAtlas.AtlasSprite((TextureAtlas.AtlasRegion)region);
         } else {
            sprite = new Sprite(region);
         }

         ((Sprite)sprite).setColor(tint);
         return new SpriteDrawable((Sprite)sprite);
      } else if (drawable instanceof NinePatchDrawable) {
         NinePatchDrawable patchDrawable = new NinePatchDrawable((NinePatchDrawable)drawable);
         patchDrawable.setPatch(new NinePatch(patchDrawable.getPatch(), tint));
         return patchDrawable;
      } else if (drawable instanceof SpriteDrawable) {
         SpriteDrawable spriteDrawable = new SpriteDrawable((SpriteDrawable)drawable);
         Sprite sprite = spriteDrawable.getSprite();
         if (sprite instanceof TextureAtlas.AtlasSprite) {
            sprite = new TextureAtlas.AtlasSprite((TextureAtlas.AtlasSprite)sprite);
         } else {
            sprite = new Sprite(sprite);
         }

         ((Sprite)sprite).setColor(tint);
         spriteDrawable.setSprite((Sprite)sprite);
         return spriteDrawable;
      } else {
         throw new GdxRuntimeException("Unable to copy, unknown drawable type: " + drawable.getClass());
      }
   }

   public void setEnabled(Actor actor, boolean enabled) {
      Method method = findMethod(actor.getClass(), "getStyle");
      if (method != null) {
         Object style;
         try {
            style = method.invoke(actor);
         } catch (Exception var8) {
            return;
         }

         String name = this.find(style);
         if (name != null) {
            name = name.replace("-disabled", "") + (enabled ? "" : "-disabled");
            style = this.get(name, style.getClass());
            method = findMethod(actor.getClass(), "setStyle");
            if (method != null) {
               try {
                  method.invoke(actor, style);
               } catch (Exception var7) {
               }

            }
         }
      }
   }

   public TextureAtlas getAtlas() {
      return this.atlas;
   }

   public void dispose() {
      if (this.atlas != null) {
         this.atlas.dispose();
      }

      Iterator var2 = this.resources.values().iterator();

      while(var2.hasNext()) {
         ObjectMap<String, Object> entry = (ObjectMap)var2.next();
         Iterator var4 = entry.values().iterator();

         while(var4.hasNext()) {
            Object resource = var4.next();
            if (resource instanceof Disposable) {
               ((Disposable)resource).dispose();
            }
         }
      }

   }

   protected Json getJsonLoader(final FileHandle skinFile) {
      Json json = new Json() {
         public <T> T readValue(Class<T> type, Class elementType, JsonValue jsonData) {
            return jsonData.isString() && !CharSequence.class.isAssignableFrom(type) ? Skin.this.get(jsonData.asString(), type) : super.readValue(type, elementType, jsonData);
         }
      };
      json.setTypeName((String)null);
      json.setUsePrototypes(false);
      json.setSerializer(Skin.class, new Json.ReadOnlySerializer<Skin>() {
         public Skin read(Json json, JsonValue typeToValueMap, Class ignored) {
            for(JsonValue valueMap = typeToValueMap.child(); valueMap != null; valueMap = valueMap.next()) {
               try {
                  this.readNamedObjects(json, Class.forName(valueMap.name()), valueMap);
               } catch (ClassNotFoundException var6) {
                  throw new SerializationException(var6);
               }
            }

            return Skin.this;
         }

         private void readNamedObjects(Json json, Class type, JsonValue valueMap) {
            Class addType = type == Skin.TintedDrawable.class ? Drawable.class : type;

            for(JsonValue valueEntry = valueMap.child(); valueEntry != null; valueEntry = valueEntry.next()) {
               Object object = json.readValue(type, valueEntry);
               if (object != null) {
                  try {
                     Skin.this.add(valueEntry.name(), object, addType);
                  } catch (Exception var8) {
                     throw new SerializationException("Error reading " + type.getSimpleName() + ": " + valueEntry.name(), var8);
                  }
               }
            }

         }
      });
      json.setSerializer(BitmapFont.class, new Json.ReadOnlySerializer<BitmapFont>() {
         public BitmapFont read(Json json, JsonValue jsonData, Class type) {
            String path = (String)json.readValue("file", String.class, jsonData);
            FileHandle fontFile = skinFile.parent().child(path);
            if (!fontFile.exists()) {
               fontFile = Gdx.files.internal(path);
            }

            if (!fontFile.exists()) {
               throw new SerializationException("Font file not found: " + fontFile);
            } else {
               String regionName = fontFile.nameWithoutExtension();

               try {
                  TextureRegion region = (TextureRegion)Skin.this.optional(regionName, TextureRegion.class);
                  if (region != null) {
                     return new BitmapFont(fontFile, region, false);
                  } else {
                     FileHandle imageFile = fontFile.parent().child(regionName + ".png");
                     return imageFile.exists() ? new BitmapFont(fontFile, imageFile, false) : new BitmapFont(fontFile, false);
                  }
               } catch (RuntimeException var9) {
                  throw new SerializationException("Error loading bitmap font: " + fontFile, var9);
               }
            }
         }
      });
      json.setSerializer(Color.class, new Json.ReadOnlySerializer<Color>() {
         public Color read(Json json, JsonValue jsonData, Class type) {
            if (jsonData.isString()) {
               return (Color)Skin.this.get(jsonData.asString(), Color.class);
            } else {
               String hex = (String)json.readValue((String)"hex", String.class, (Object)null, jsonData);
               if (hex != null) {
                  return Color.valueOf(hex);
               } else {
                  float r = (Float)json.readValue((String)"r", Float.TYPE, (Object)0.0F, jsonData);
                  float g = (Float)json.readValue((String)"g", Float.TYPE, (Object)0.0F, jsonData);
                  float b = (Float)json.readValue((String)"b", Float.TYPE, (Object)0.0F, jsonData);
                  float a = (Float)json.readValue((String)"a", Float.TYPE, (Object)1.0F, jsonData);
                  return new Color(r, g, b, a);
               }
            }
         }
      });
      json.setSerializer(Skin.TintedDrawable.class, new Json.ReadOnlySerializer() {
         public Object read(Json json, JsonValue jsonData, Class type) {
            String name = (String)json.readValue("name", String.class, jsonData);
            Color color = (Color)json.readValue("color", Color.class, jsonData);
            return Skin.this.newDrawable(name, color);
         }
      });
      return json;
   }

   private static Method findMethod(Class type, String name) {
      Method[] methods = type.getMethods();
      int i = 0;

      for(int n = methods.length; i < n; ++i) {
         Method method = methods[i];
         if (method.getName().equals(name)) {
            return method;
         }
      }

      return null;
   }

   public static class TintedDrawable {
      public String name;
      public Color color;
   }
}
