package com.badlogic.gdx.maps.tiled;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.XmlReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.zip.DataFormatException;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;

public class AtlasTmxMapLoader extends AsynchronousAssetLoader<TiledMap, AtlasTmxMapLoader.AtlasTiledMapLoaderParameters> {
   protected static final int FLAG_FLIP_HORIZONTALLY = Integer.MIN_VALUE;
   protected static final int FLAG_FLIP_VERTICALLY = 1073741824;
   protected static final int FLAG_FLIP_DIAGONALLY = 536870912;
   protected static final int MASK_CLEAR = -536870912;
   protected XmlReader xml = new XmlReader();
   protected XmlReader.Element root;
   protected boolean yUp;
   protected int mapWidthInPixels;
   protected int mapHeightInPixels;
   protected TiledMap map;
   protected Array<Texture> trackedTextures = new Array();

   public AtlasTmxMapLoader() {
      super(new InternalFileHandleResolver());
   }

   public AtlasTmxMapLoader(FileHandleResolver resolver) {
      super(resolver);
   }

   public TiledMap load(String fileName) {
      return this.load(fileName, new AtlasTmxMapLoader.AtlasTiledMapLoaderParameters());
   }

   public Array<AssetDescriptor> getDependencies(String fileName, AtlasTmxMapLoader.AtlasTiledMapLoaderParameters parameter) {
      Array<AssetDescriptor> dependencies = new Array();
      FileHandle tmxFile = this.resolve(fileName);

      try {
         this.root = this.xml.parse(tmxFile);
         XmlReader.Element properties = this.root.getChildByName("properties");
         if (properties != null) {
            Iterator var7 = properties.getChildrenByName("property").iterator();

            while(var7.hasNext()) {
               XmlReader.Element property = (XmlReader.Element)var7.next();
               String name = property.getAttribute("name");
               String value = property.getAttribute("value");
               if (name.startsWith("atlas")) {
                  FileHandle atlasHandle = getRelativeFileHandle(tmxFile, value);
                  atlasHandle = this.resolve(atlasHandle.path());
                  dependencies.add(new AssetDescriptor(atlasHandle.path(), TextureAtlas.class));
               }
            }
         }

         return dependencies;
      } catch (IOException var11) {
         throw new GdxRuntimeException("Unable to parse .tmx file.");
      }
   }

   public TiledMap load(String fileName, AtlasTmxMapLoader.AtlasTiledMapLoaderParameters parameter) {
      try {
         if (parameter != null) {
            this.yUp = parameter.yUp;
         } else {
            this.yUp = true;
         }

         FileHandle tmxFile = this.resolve(fileName);
         this.root = this.xml.parse(tmxFile);
         ObjectMap<String, TextureAtlas> atlases = new ObjectMap();
         FileHandle atlasFile = this.loadAtlas(this.root, tmxFile);
         if (atlasFile == null) {
            throw new GdxRuntimeException("Couldn't load atlas");
         } else {
            TextureAtlas atlas = new TextureAtlas(atlasFile);
            atlases.put(atlasFile.path(), atlas);
            AtlasTmxMapLoader.AtlasResolver.DirectAtlasResolver atlasResolver = new AtlasTmxMapLoader.AtlasResolver.DirectAtlasResolver(atlases);
            TiledMap map = this.loadMap(this.root, tmxFile, atlasResolver, parameter);
            map.setOwnedResources(atlases.values().toArray());
            this.setTextureFilters(parameter.textureMinFilter, parameter.textureMagFilter);
            return map;
         }
      } catch (IOException var9) {
         throw new GdxRuntimeException("Couldn't load tilemap '" + fileName + "'", var9);
      }
   }

   protected FileHandle loadAtlas(XmlReader.Element root, FileHandle tmxFile) throws IOException {
      XmlReader.Element e = root.getChildByName("properties");
      new Array();
      if (e != null) {
         Iterator var6 = e.getChildrenByName("property").iterator();

         while(var6.hasNext()) {
            XmlReader.Element property = (XmlReader.Element)var6.next();
            String name = property.getAttribute("name", (String)null);
            String value = property.getAttribute("value", (String)null);
            if (name.equals("atlas")) {
               if (value == null) {
                  value = property.getText();
               }

               if (value != null && value.length() != 0) {
                  return getRelativeFileHandle(tmxFile, value);
               }
            }
         }
      }

      return null;
   }

   private void setTextureFilters(Texture.TextureFilter min, Texture.TextureFilter mag) {
      Iterator var4 = this.trackedTextures.iterator();

      while(var4.hasNext()) {
         Texture texture = (Texture)var4.next();
         texture.setFilter(min, mag);
      }

   }

   public void loadAsync(AssetManager manager, String fileName, AtlasTmxMapLoader.AtlasTiledMapLoaderParameters parameter) {
      this.map = null;
      FileHandle tmxFile = this.resolve(fileName);
      if (parameter != null) {
         this.yUp = parameter.yUp;
      } else {
         this.yUp = true;
      }

      try {
         this.map = this.loadMap(this.root, tmxFile, new AtlasTmxMapLoader.AtlasResolver.AssetManagerAtlasResolver(manager), parameter);
      } catch (Exception var6) {
         throw new GdxRuntimeException("Couldn't load tilemap '" + fileName + "'", var6);
      }
   }

   public TiledMap loadSync(AssetManager manager, String fileName, AtlasTmxMapLoader.AtlasTiledMapLoaderParameters parameter) {
      if (parameter != null) {
         this.setTextureFilters(parameter.textureMinFilter, parameter.textureMagFilter);
      }

      return this.map;
   }

   protected TiledMap loadMap(XmlReader.Element root, FileHandle tmxFile, AtlasTmxMapLoader.AtlasResolver resolver, AtlasTmxMapLoader.AtlasTiledMapLoaderParameters parameter) {
      TiledMap map = new TiledMap();
      String mapOrientation = root.getAttribute("orientation", (String)null);
      int mapWidth = root.getIntAttribute("width", 0);
      int mapHeight = root.getIntAttribute("height", 0);
      int tileWidth = root.getIntAttribute("tilewidth", 0);
      int tileHeight = root.getIntAttribute("tileheight", 0);
      String mapBackgroundColor = root.getAttribute("backgroundcolor", (String)null);
      MapProperties mapProperties = map.getProperties();
      if (mapOrientation != null) {
         mapProperties.put("orientation", mapOrientation);
      }

      mapProperties.put("width", mapWidth);
      mapProperties.put("height", mapHeight);
      mapProperties.put("tilewidth", tileWidth);
      mapProperties.put("tileheight", tileHeight);
      if (mapBackgroundColor != null) {
         mapProperties.put("backgroundcolor", mapBackgroundColor);
      }

      this.mapWidthInPixels = mapWidth * tileWidth;
      this.mapHeightInPixels = mapHeight * tileHeight;
      int i = 0;

      for(int j = root.getChildCount(); i < j; ++i) {
         XmlReader.Element element = root.getChild(i);
         String elementName = element.getName();
         if (elementName.equals("properties")) {
            this.loadProperties(map.getProperties(), element);
         } else if (elementName.equals("tileset")) {
            this.loadTileset(map, element, tmxFile, resolver, parameter);
         } else if (elementName.equals("layer")) {
            this.loadTileLayer(map, element);
         } else if (elementName.equals("objectgroup")) {
            this.loadObjectGroup(map, element);
         }
      }

      return map;
   }

   protected void loadTileset(TiledMap map, XmlReader.Element element, FileHandle tmxFile, AtlasTmxMapLoader.AtlasResolver resolver, AtlasTmxMapLoader.AtlasTiledMapLoaderParameters parameter) {
      if (element.getName().equals("tileset")) {
         String name = element.get("name", (String)null);
         int firstgid = element.getIntAttribute("firstgid", 1);
         int tilewidth = element.getIntAttribute("tilewidth", 0);
         int tileheight = element.getIntAttribute("tileheight", 0);
         int spacing = element.getIntAttribute("spacing", 0);
         int margin = element.getIntAttribute("margin", 0);
         String source = element.getAttribute("source", (String)null);
         String imageSource = "";
         int imageWidth = false;
         int imageHeight = false;
         FileHandle image = null;
         int imageWidth;
         int imageHeight;
         if (source != null) {
            FileHandle tsx = getRelativeFileHandle(tmxFile, source);

            try {
               element = this.xml.parse(tsx);
               name = element.get("name", (String)null);
               tilewidth = element.getIntAttribute("tilewidth", 0);
               tileheight = element.getIntAttribute("tileheight", 0);
               spacing = element.getIntAttribute("spacing", 0);
               margin = element.getIntAttribute("margin", 0);
               imageSource = element.getChildByName("image").getAttribute("source");
               imageWidth = element.getChildByName("image").getIntAttribute("width", 0);
               imageHeight = element.getChildByName("image").getIntAttribute("height", 0);
            } catch (IOException var28) {
               throw new GdxRuntimeException("Error parsing external tileset.");
            }
         } else {
            imageSource = element.getChildByName("image").getAttribute("source");
            imageWidth = element.getChildByName("image").getIntAttribute("width", 0);
            imageHeight = element.getChildByName("image").getIntAttribute("height", 0);
         }

         TextureAtlas atlas = null;
         String regionsName = "";
         if (map.getProperties().containsKey("atlas")) {
            FileHandle atlasHandle = getRelativeFileHandle(tmxFile, (String)map.getProperties().get("atlas", String.class));
            atlasHandle = this.resolve(atlasHandle.path());
            atlas = resolver.getAtlas(atlasHandle.path());
            regionsName = atlasHandle.nameWithoutExtension();
            if (parameter != null && parameter.forceTextureFilters) {
               Iterator var21 = atlas.getTextures().iterator();

               while(var21.hasNext()) {
                  Texture texture = (Texture)var21.next();
                  this.trackedTextures.add(texture);
               }
            }
         }

         TiledMapTileSet tileset = new TiledMapTileSet();
         MapProperties props = tileset.getProperties();
         tileset.setName(name);
         props.put("firstgid", firstgid);
         props.put("imagesource", imageSource);
         props.put("imagewidth", imageWidth);
         props.put("imageheight", imageHeight);
         props.put("tilewidth", tilewidth);
         props.put("tileheight", tileheight);
         props.put("margin", margin);
         props.put("spacing", spacing);
         Array<TextureAtlas.AtlasRegion> regions = atlas.findRegions(regionsName);
         Iterator var23 = regions.iterator();

         int tileid;
         while(var23.hasNext()) {
            TextureAtlas.AtlasRegion region = (TextureAtlas.AtlasRegion)var23.next();
            if (region != null) {
               StaticTiledMapTile tile = new StaticTiledMapTile(region);
               if (!this.yUp) {
                  region.flip(false, true);
               }

               tileid = firstgid + region.index;
               tile.setId(tileid);
               tileset.putTile(tileid, tile);
            }
         }

         Array<XmlReader.Element> tileElements = element.getChildrenByName("tile");
         Iterator var37 = tileElements.iterator();

         XmlReader.Element properties;
         while(var37.hasNext()) {
            properties = (XmlReader.Element)var37.next();
            tileid = properties.getIntAttribute("id", 0);
            TiledMapTile tile = tileset.getTile(firstgid + tileid);
            if (tile != null) {
               XmlReader.Element properties = properties.getChildByName("properties");
               if (properties != null) {
                  this.loadProperties(tile.getProperties(), properties);
               }
            }
         }

         properties = element.getChildByName("properties");
         if (properties != null) {
            this.loadProperties(tileset.getProperties(), properties);
         }

         map.getTileSets().addTileSet(tileset);
      }

   }

   protected void loadTileLayer(TiledMap map, XmlReader.Element element) {
      if (element.getName().equals("layer")) {
         String name = element.getAttribute("name", (String)null);
         int width = element.getIntAttribute("width", 0);
         int height = element.getIntAttribute("height", 0);
         int tileWidth = element.getParent().getIntAttribute("tilewidth", 0);
         int tileHeight = element.getParent().getIntAttribute("tileheight", 0);
         boolean visible = element.getIntAttribute("visible", 1) == 1;
         float opacity = element.getFloatAttribute("opacity", 1.0F);
         TiledMapTileLayer layer = new TiledMapTileLayer(width, height, tileWidth, tileHeight);
         layer.setVisible(visible);
         layer.setOpacity(opacity);
         layer.setName(name);
         TiledMapTileSets tilesets = map.getTileSets();
         XmlReader.Element data = element.getChildByName("data");
         String encoding = data.getAttribute("encoding", (String)null);
         String compression = data.getAttribute("compression", (String)null);
         if (encoding == null) {
            throw new GdxRuntimeException("Unsupported encoding (XML) for TMX Layer Data");
         }

         int y;
         boolean flipHorizontally;
         int y;
         int y;
         boolean flipHorizontally;
         if (encoding.equals("csv")) {
            String[] array = data.getText().split(",");

            for(y = 0; y < height; ++y) {
               for(y = 0; y < width; ++y) {
                  y = (int)Long.parseLong(array[y * width + y].trim());
                  boolean flipHorizontally = (y & Integer.MIN_VALUE) != 0;
                  flipHorizontally = (y & 1073741824) != 0;
                  flipHorizontally = (y & 536870912) != 0;
                  y &= 536870911;
                  tilesets.getTile(y);
                  TiledMapTile tile = tilesets.getTile(y);
                  if (tile != null) {
                     TiledMapTileLayer.Cell cell = this.createTileLayerCell(flipHorizontally, flipHorizontally, flipHorizontally);
                     cell.setTile(tile);
                     layer.setCell(y, this.yUp ? height - 1 - y : y, cell);
                  }
               }
            }
         } else {
            if (!encoding.equals("base64")) {
               throw new GdxRuntimeException("Unrecognised encoding (" + encoding + ") for TMX Layer Data");
            }

            byte[] bytes = Base64Coder.decode(data.getText());
            int x;
            boolean flipVertically;
            if (compression == null) {
               y = 0;

               for(y = 0; y < height; ++y) {
                  for(y = 0; y < width; ++y) {
                     x = unsignedByteToInt(bytes[y++]) | unsignedByteToInt(bytes[y++]) << 8 | unsignedByteToInt(bytes[y++]) << 16 | unsignedByteToInt(bytes[y++]) << 24;
                     flipHorizontally = (x & Integer.MIN_VALUE) != 0;
                     flipHorizontally = (x & 1073741824) != 0;
                     flipVertically = (x & 536870912) != 0;
                     x &= 536870911;
                     tilesets.getTile(x);
                     TiledMapTile tile = tilesets.getTile(x);
                     if (tile != null) {
                        TiledMapTileLayer.Cell cell = this.createTileLayerCell(flipHorizontally, flipHorizontally, flipVertically);
                        cell.setTile(tile);
                        layer.setCell(y, this.yUp ? height - 1 - y : y, cell);
                     }
                  }
               }
            } else {
               Inflater zlib;
               byte[] temp;
               int id;
               boolean flipDiagonally;
               TiledMapTile tile;
               TiledMapTileLayer.Cell cell;
               if (compression.equals("gzip")) {
                  zlib = null;

                  GZIPInputStream GZIS;
                  try {
                     GZIS = new GZIPInputStream(new ByteArrayInputStream(bytes), bytes.length);
                  } catch (IOException var26) {
                     throw new GdxRuntimeException("Error Reading TMX Layer Data - IOException: " + var26.getMessage());
                  }

                  temp = new byte[4];

                  for(y = 0; y < height; ++y) {
                     for(x = 0; x < width; ++x) {
                        try {
                           GZIS.read(temp, 0, 4);
                           id = unsignedByteToInt(temp[0]) | unsignedByteToInt(temp[1]) << 8 | unsignedByteToInt(temp[2]) << 16 | unsignedByteToInt(temp[3]) << 24;
                           flipHorizontally = (id & Integer.MIN_VALUE) != 0;
                           flipVertically = (id & 1073741824) != 0;
                           flipDiagonally = (id & 536870912) != 0;
                           id &= 536870911;
                           tilesets.getTile(id);
                           tile = tilesets.getTile(id);
                           if (tile != null) {
                              cell = this.createTileLayerCell(flipHorizontally, flipVertically, flipDiagonally);
                              cell.setTile(tile);
                              layer.setCell(x, this.yUp ? height - 1 - y : y, cell);
                           }
                        } catch (IOException var27) {
                           throw new GdxRuntimeException("Error Reading TMX Layer Data.", var27);
                        }
                     }
                  }
               } else if (compression.equals("zlib")) {
                  zlib = new Inflater();
                  temp = new byte[4];
                  zlib.setInput(bytes, 0, bytes.length);

                  for(y = 0; y < height; ++y) {
                     for(x = 0; x < width; ++x) {
                        try {
                           zlib.inflate(temp, 0, 4);
                           id = unsignedByteToInt(temp[0]) | unsignedByteToInt(temp[1]) << 8 | unsignedByteToInt(temp[2]) << 16 | unsignedByteToInt(temp[3]) << 24;
                           flipHorizontally = (id & Integer.MIN_VALUE) != 0;
                           flipVertically = (id & 1073741824) != 0;
                           flipDiagonally = (id & 536870912) != 0;
                           id &= 536870911;
                           tilesets.getTile(id);
                           tile = tilesets.getTile(id);
                           if (tile != null) {
                              cell = this.createTileLayerCell(flipHorizontally, flipVertically, flipDiagonally);
                              cell.setTile(tile);
                              layer.setCell(x, this.yUp ? height - 1 - y : y, cell);
                           }
                        } catch (DataFormatException var28) {
                           throw new GdxRuntimeException("Error Reading TMX Layer Data.", var28);
                        }
                     }
                  }
               }
            }
         }

         XmlReader.Element properties = element.getChildByName("properties");
         if (properties != null) {
            this.loadProperties(layer.getProperties(), properties);
         }

         map.getLayers().add(layer);
      }

   }

   protected void loadObjectGroup(TiledMap map, XmlReader.Element element) {
      if (element.getName().equals("objectgroup")) {
         String name = element.getAttribute("name", (String)null);
         MapLayer layer = new MapLayer();
         layer.setName(name);
         XmlReader.Element properties = element.getChildByName("properties");
         if (properties != null) {
            this.loadProperties(layer.getProperties(), properties);
         }

         Iterator var7 = element.getChildrenByName("object").iterator();

         while(var7.hasNext()) {
            XmlReader.Element objectElement = (XmlReader.Element)var7.next();
            this.loadObject(layer, objectElement);
         }

         map.getLayers().add(layer);
      }

   }

   protected void loadObject(MapLayer layer, XmlReader.Element element) {
      if (element.getName().equals("object")) {
         MapObject object = null;
         int x = element.getIntAttribute("x", 0);
         int y = this.yUp ? this.mapHeightInPixels - element.getIntAttribute("y", 0) : element.getIntAttribute("y", 0);
         int width = element.getIntAttribute("width", 0);
         int height = element.getIntAttribute("height", 0);
         if (element.getChildCount() > 0) {
            XmlReader.Element child = null;
            String[] points;
            float[] vertices;
            int i;
            String[] point;
            if ((child = element.getChildByName("polygon")) != null) {
               points = child.getAttribute("points").split(" ");
               vertices = new float[points.length * 2];

               for(i = 0; i < points.length; ++i) {
                  point = points[i].split(",");
                  vertices[i * 2] = (float)Integer.parseInt(point[0]);
                  vertices[i * 2 + 1] = (float)Integer.parseInt(point[1]);
                  if (this.yUp) {
                     vertices[i * 2 + 1] *= -1.0F;
                  }
               }

               Polygon polygon = new Polygon(vertices);
               polygon.setPosition((float)x, (float)y);
               object = new PolygonMapObject(polygon);
            } else if ((child = element.getChildByName("polyline")) == null) {
               if (element.getChildByName("ellipse") != null) {
                  object = new EllipseMapObject((float)x, (float)(this.yUp ? y - height : y), (float)width, (float)height);
               }
            } else {
               points = child.getAttribute("points").split(" ");
               vertices = new float[points.length * 2];

               for(i = 0; i < points.length; ++i) {
                  point = points[i].split(",");
                  vertices[i * 2] = (float)Integer.parseInt(point[0]);
                  vertices[i * 2 + 1] = (float)Integer.parseInt(point[1]);
                  if (this.yUp) {
                     vertices[i * 2 + 1] *= -1.0F;
                  }
               }

               Polyline polyline = new Polyline(vertices);
               polyline.setPosition((float)x, (float)y);
               object = new PolylineMapObject(polyline);
            }
         }

         if (object == null) {
            object = new RectangleMapObject((float)x, (float)(this.yUp ? y - height : y), (float)width, (float)height);
         }

         ((MapObject)object).setName(element.getAttribute("name", (String)null));
         String type = element.getAttribute("type", (String)null);
         if (type != null) {
            ((MapObject)object).getProperties().put("type", type);
         }

         int gid = element.getIntAttribute("gid", -1);
         if (gid != -1) {
            ((MapObject)object).getProperties().put("gid", gid);
         }

         ((MapObject)object).getProperties().put("x", x);
         ((MapObject)object).getProperties().put("y", this.yUp ? y - height : y);
         ((MapObject)object).setVisible(element.getIntAttribute("visible", 1) == 1);
         XmlReader.Element properties = element.getChildByName("properties");
         if (properties != null) {
            this.loadProperties(((MapObject)object).getProperties(), properties);
         }

         layer.getObjects().add((MapObject)object);
      }

   }

   protected void loadProperties(MapProperties properties, XmlReader.Element element) {
      String name;
      String value;
      if (element.getName().equals("properties")) {
         for(Iterator var4 = element.getChildrenByName("property").iterator(); var4.hasNext(); properties.put(name, value)) {
            XmlReader.Element property = (XmlReader.Element)var4.next();
            name = property.getAttribute("name", (String)null);
            value = property.getAttribute("value", (String)null);
            if (value == null) {
               value = property.getText();
            }
         }
      }

   }

   protected TiledMapTileLayer.Cell createTileLayerCell(boolean flipHorizontally, boolean flipVertically, boolean flipDiagonally) {
      TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
      if (flipDiagonally) {
         if (flipHorizontally && flipVertically) {
            cell.setFlipHorizontally(true);
            cell.setRotation(this.yUp ? 3 : 1);
         } else if (flipHorizontally) {
            cell.setRotation(this.yUp ? 3 : 1);
         } else if (flipVertically) {
            cell.setRotation(this.yUp ? 1 : 3);
         } else {
            cell.setFlipVertically(true);
            cell.setRotation(this.yUp ? 3 : 1);
         }
      } else {
         cell.setFlipHorizontally(flipHorizontally);
         cell.setFlipVertically(flipVertically);
      }

      return cell;
   }

   public static FileHandle getRelativeFileHandle(FileHandle file, String path) {
      StringTokenizer tokenizer = new StringTokenizer(path, "\\/");
      FileHandle result = file.parent();

      while(tokenizer.hasMoreElements()) {
         String token = tokenizer.nextToken();
         if (token.equals("..")) {
            result = result.parent();
         } else {
            result = result.child(token);
         }
      }

      return result;
   }

   protected static int unsignedByteToInt(byte b) {
      return b & 255;
   }

   private interface AtlasResolver {
      TextureAtlas getAtlas(String var1);

      public static class AssetManagerAtlasResolver implements AtlasTmxMapLoader.AtlasResolver {
         private final AssetManager assetManager;

         public AssetManagerAtlasResolver(AssetManager assetManager) {
            this.assetManager = assetManager;
         }

         public TextureAtlas getAtlas(String name) {
            return (TextureAtlas)this.assetManager.get(name, TextureAtlas.class);
         }
      }

      public static class DirectAtlasResolver implements AtlasTmxMapLoader.AtlasResolver {
         private final ObjectMap<String, TextureAtlas> atlases;

         public DirectAtlasResolver(ObjectMap<String, TextureAtlas> atlases) {
            this.atlases = atlases;
         }

         public TextureAtlas getAtlas(String name) {
            return (TextureAtlas)this.atlases.get(name);
         }
      }
   }

   public static class AtlasTiledMapLoaderParameters extends AssetLoaderParameters<TiledMap> {
      public boolean yUp = true;
      public boolean forceTextureFilters = false;
      public Texture.TextureFilter textureMinFilter;
      public Texture.TextureFilter textureMagFilter;

      public AtlasTiledMapLoaderParameters() {
         this.textureMinFilter = Texture.TextureFilter.Nearest;
         this.textureMagFilter = Texture.TextureFilter.Nearest;
      }
   }
}
