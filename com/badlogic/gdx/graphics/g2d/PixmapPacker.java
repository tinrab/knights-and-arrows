package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import java.util.Iterator;

public class PixmapPacker implements Disposable {
   final int pageWidth;
   final int pageHeight;
   final Pixmap.Format pageFormat;
   final int padding;
   final boolean duplicateBorder;
   final Array<PixmapPacker.Page> pages = new Array();
   PixmapPacker.Page currPage;
   boolean disposed;

   public PixmapPacker(int width, int height, Pixmap.Format format, int padding, boolean duplicateBorder) {
      this.pageWidth = width;
      this.pageHeight = height;
      this.pageFormat = format;
      this.padding = padding;
      this.duplicateBorder = duplicateBorder;
      this.newPage();
   }

   public synchronized Rectangle pack(String name, Pixmap image) {
      if (this.disposed) {
         return null;
      } else if (this.getRect(name) != null) {
         throw new RuntimeException("Key with name '" + name + "' is already in map");
      } else {
         int borderPixels = this.padding + (this.duplicateBorder ? 1 : 0);
         borderPixels <<= 1;
         Rectangle rect = new Rectangle(0.0F, 0.0F, (float)(image.getWidth() + borderPixels), (float)(image.getHeight() + borderPixels));
         if (!(rect.getWidth() > (float)this.pageWidth) && !(rect.getHeight() > (float)this.pageHeight)) {
            PixmapPacker.Node node = this.insert(this.currPage.root, rect);
            if (node == null) {
               this.newPage();
               return this.pack(name, image);
            } else {
               node.leaveName = name;
               rect = new Rectangle(node.rect);
               rect.width -= (float)borderPixels;
               rect.height -= (float)borderPixels;
               borderPixels >>= 1;
               rect.x += (float)borderPixels;
               rect.y += (float)borderPixels;
               this.currPage.rects.put(name, rect);
               Pixmap.Blending blending = Pixmap.getBlending();
               Pixmap.setBlending(Pixmap.Blending.None);
               this.currPage.image.drawPixmap(image, (int)rect.x, (int)rect.y);
               if (this.duplicateBorder) {
                  int imageWidth = image.getWidth();
                  int imageHeight = image.getHeight();
                  this.currPage.image.drawPixmap(image, 0, 0, 1, 1, (int)rect.x - 1, (int)rect.y - 1, 1, 1);
                  this.currPage.image.drawPixmap(image, imageWidth - 1, 0, 1, 1, (int)rect.x + (int)rect.width, (int)rect.y - 1, 1, 1);
                  this.currPage.image.drawPixmap(image, 0, imageHeight - 1, 1, 1, (int)rect.x - 1, (int)rect.y + (int)rect.height, 1, 1);
                  this.currPage.image.drawPixmap(image, imageWidth - 1, imageHeight - 1, 1, 1, (int)rect.x + (int)rect.width, (int)rect.y + (int)rect.height, 1, 1);
                  this.currPage.image.drawPixmap(image, 0, 0, imageWidth, 1, (int)rect.x, (int)rect.y - 1, (int)rect.width, 1);
                  this.currPage.image.drawPixmap(image, 0, imageHeight - 1, imageWidth, 1, (int)rect.x, (int)rect.y + (int)rect.height, (int)rect.width, 1);
                  this.currPage.image.drawPixmap(image, 0, 0, 1, imageHeight, (int)rect.x - 1, (int)rect.y, 1, (int)rect.height);
                  this.currPage.image.drawPixmap(image, imageWidth - 1, 0, 1, imageHeight, (int)rect.x + (int)rect.width, (int)rect.y, 1, (int)rect.height);
               }

               Pixmap.setBlending(blending);
               this.currPage.addedRects.add(name);
               return rect;
            }
         } else {
            throw new GdxRuntimeException("page size for '" + name + "' to small");
         }
      }
   }

   private void newPage() {
      PixmapPacker.Page page = new PixmapPacker.Page();
      page.image = new Pixmap(this.pageWidth, this.pageHeight, this.pageFormat);
      page.root = new PixmapPacker.Node(0, 0, this.pageWidth, this.pageHeight, (PixmapPacker.Node)null, (PixmapPacker.Node)null, (String)null);
      page.rects = new OrderedMap();
      this.pages.add(page);
      this.currPage = page;
   }

   private PixmapPacker.Node insert(PixmapPacker.Node node, Rectangle rect) {
      if (node.leaveName == null && node.leftChild != null && node.rightChild != null) {
         PixmapPacker.Node newNode = null;
         newNode = this.insert(node.leftChild, rect);
         if (newNode == null) {
            newNode = this.insert(node.rightChild, rect);
         }

         return newNode;
      } else if (node.leaveName != null) {
         return null;
      } else if (node.rect.width == rect.width && node.rect.height == rect.height) {
         return node;
      } else if (!(node.rect.width < rect.width) && !(node.rect.height < rect.height)) {
         node.leftChild = new PixmapPacker.Node();
         node.rightChild = new PixmapPacker.Node();
         int deltaWidth = (int)node.rect.width - (int)rect.width;
         int deltaHeight = (int)node.rect.height - (int)rect.height;
         if (deltaWidth > deltaHeight) {
            node.leftChild.rect.x = node.rect.x;
            node.leftChild.rect.y = node.rect.y;
            node.leftChild.rect.width = rect.width;
            node.leftChild.rect.height = node.rect.height;
            node.rightChild.rect.x = node.rect.x + rect.width;
            node.rightChild.rect.y = node.rect.y;
            node.rightChild.rect.width = node.rect.width - rect.width;
            node.rightChild.rect.height = node.rect.height;
         } else {
            node.leftChild.rect.x = node.rect.x;
            node.leftChild.rect.y = node.rect.y;
            node.leftChild.rect.width = node.rect.width;
            node.leftChild.rect.height = rect.height;
            node.rightChild.rect.x = node.rect.x;
            node.rightChild.rect.y = node.rect.y + rect.height;
            node.rightChild.rect.width = node.rect.width;
            node.rightChild.rect.height = node.rect.height - rect.height;
         }

         return this.insert(node.leftChild, rect);
      } else {
         return null;
      }
   }

   public Array<PixmapPacker.Page> getPages() {
      return this.pages;
   }

   public synchronized Rectangle getRect(String name) {
      Iterator var3 = this.pages.iterator();

      while(var3.hasNext()) {
         PixmapPacker.Page page = (PixmapPacker.Page)var3.next();
         Rectangle rect = (Rectangle)page.rects.get(name);
         if (rect != null) {
            return rect;
         }
      }

      return null;
   }

   public synchronized PixmapPacker.Page getPage(String name) {
      Iterator var3 = this.pages.iterator();

      while(var3.hasNext()) {
         PixmapPacker.Page page = (PixmapPacker.Page)var3.next();
         Rectangle rect = (Rectangle)page.rects.get(name);
         if (rect != null) {
            return page;
         }
      }

      return null;
   }

   public synchronized void dispose() {
      Iterator var2 = this.pages.iterator();

      while(var2.hasNext()) {
         PixmapPacker.Page page = (PixmapPacker.Page)var2.next();
         page.image.dispose();
      }

      this.disposed = true;
   }

   public synchronized TextureAtlas generateTextureAtlas(Texture.TextureFilter minFilter, Texture.TextureFilter magFilter, boolean useMipMaps) {
      TextureAtlas atlas = new TextureAtlas();
      Iterator var6 = this.pages.iterator();

      while(true) {
         PixmapPacker.Page page;
         do {
            if (!var6.hasNext()) {
               return atlas;
            }

            page = (PixmapPacker.Page)var6.next();
         } while(page.rects.size == 0);

         Texture texture = new Texture(new PixmapPacker.ManagedPixmapTextureData(page.image, page.image.getFormat(), useMipMaps)) {
            public void dispose() {
               super.dispose();
               this.getTextureData().consumePixmap().dispose();
            }
         };
         texture.setFilter(minFilter, magFilter);
         ObjectMap.Keys<String> names = page.rects.keys();
         Iterator var10 = names.iterator();

         while(var10.hasNext()) {
            String name = (String)var10.next();
            Rectangle rect = (Rectangle)page.rects.get(name);
            TextureRegion region = new TextureRegion(texture, (int)rect.x, (int)rect.y, (int)rect.width, (int)rect.height);
            atlas.addRegion(name, region);
         }
      }
   }

   public synchronized void updateTextureAtlas(TextureAtlas atlas, Texture.TextureFilter minFilter, Texture.TextureFilter magFilter, boolean useMipMaps) {
      Iterator var6 = this.pages.iterator();

      while(true) {
         PixmapPacker.Page page;
         String name;
         Iterator var8;
         Rectangle rect;
         TextureRegion region;
         do {
            label35:
            do {
               do {
                  if (!var6.hasNext()) {
                     return;
                  }

                  page = (PixmapPacker.Page)var6.next();
                  if (page.texture == null) {
                     continue label35;
                  }
               } while(page.addedRects.size <= 0);

               page.texture.load(page.texture.getTextureData());
               var8 = page.addedRects.iterator();

               while(var8.hasNext()) {
                  name = (String)var8.next();
                  rect = (Rectangle)page.rects.get(name);
                  region = new TextureRegion(page.texture, (int)rect.x, (int)rect.y, (int)rect.width, (int)rect.height);
                  atlas.addRegion(name, region);
               }

               page.addedRects.clear();
               return;
            } while(page.rects.size == 0);
         } while(page.addedRects.size <= 0);

         page.texture = new Texture(new PixmapPacker.ManagedPixmapTextureData(page.image, page.image.getFormat(), useMipMaps)) {
            public void dispose() {
               super.dispose();
               this.getTextureData().consumePixmap().dispose();
            }
         };
         page.texture.setFilter(minFilter, magFilter);
         var8 = page.addedRects.iterator();

         while(var8.hasNext()) {
            name = (String)var8.next();
            rect = (Rectangle)page.rects.get(name);
            region = new TextureRegion(page.texture, (int)rect.x, (int)rect.y, (int)rect.width, (int)rect.height);
            atlas.addRegion(name, region);
         }

         page.addedRects.clear();
      }
   }

   public int getPageWidth() {
      return this.pageWidth;
   }

   public int getPageHeight() {
      return this.pageHeight;
   }

   public int getPadding() {
      return this.padding;
   }

   public boolean duplicateBoarder() {
      return this.duplicateBorder;
   }

   public class ManagedPixmapTextureData extends PixmapTextureData {
      public ManagedPixmapTextureData(Pixmap pixmap, Pixmap.Format format, boolean useMipMaps) {
         super(pixmap, format, useMipMaps, false);
      }

      public boolean isManaged() {
         return true;
      }
   }

   static final class Node {
      public PixmapPacker.Node leftChild;
      public PixmapPacker.Node rightChild;
      public Rectangle rect;
      public String leaveName;

      public Node(int x, int y, int width, int height, PixmapPacker.Node leftChild, PixmapPacker.Node rightChild, String leaveName) {
         this.rect = new Rectangle((float)x, (float)y, (float)width, (float)height);
         this.leftChild = leftChild;
         this.rightChild = rightChild;
         this.leaveName = leaveName;
      }

      public Node() {
         this.rect = new Rectangle();
      }
   }

   public class Page {
      PixmapPacker.Node root;
      OrderedMap<String, Rectangle> rects;
      Pixmap image;
      Texture texture;
      Array<String> addedRects = new Array();

      public Pixmap getPixmap() {
         return this.image;
      }
   }
}
