package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class MipMapGenerator {
   private static boolean useHWMipMap = true;

   public static void setUseHardwareMipMap(boolean useHWMipMap) {
      MipMapGenerator.useHWMipMap = useHWMipMap;
   }

   public static void generateMipMap(Pixmap pixmap, int textureWidth, int textureHeight, boolean disposePixmap) {
      if (!useHWMipMap) {
         generateMipMapCPU(pixmap, textureWidth, textureHeight, disposePixmap);
      } else {
         if (Gdx.app.getType() == Application.ApplicationType.Android) {
            if (Gdx.graphics.isGL20Available()) {
               generateMipMapGLES20(pixmap, disposePixmap);
            } else {
               generateMipMapCPU(pixmap, textureWidth, textureHeight, disposePixmap);
            }
         } else {
            generateMipMapDesktop(pixmap, textureWidth, textureHeight, disposePixmap);
         }

      }
   }

   private static void generateMipMapGLES20(Pixmap pixmap, boolean disposePixmap) {
      Gdx.gl.glTexImage2D(3553, 0, pixmap.getGLInternalFormat(), pixmap.getWidth(), pixmap.getHeight(), 0, pixmap.getGLFormat(), pixmap.getGLType(), pixmap.getPixels());
      Gdx.gl20.glGenerateMipmap(3553);
      if (disposePixmap) {
         pixmap.dispose();
      }

   }

   private static void generateMipMapDesktop(Pixmap pixmap, int textureWidth, int textureHeight, boolean disposePixmap) {
      if (Gdx.graphics.isGL20Available() && (Gdx.graphics.supportsExtension("GL_ARB_framebuffer_object") || Gdx.graphics.supportsExtension("GL_EXT_framebuffer_object"))) {
         Gdx.gl.glTexImage2D(3553, 0, pixmap.getGLInternalFormat(), pixmap.getWidth(), pixmap.getHeight(), 0, pixmap.getGLFormat(), pixmap.getGLType(), pixmap.getPixels());
         Gdx.gl20.glGenerateMipmap(3553);
         if (disposePixmap) {
            pixmap.dispose();
         }
      } else if (Gdx.graphics.supportsExtension("GL_SGIS_generate_mipmap")) {
         if (Gdx.gl20 == null && textureWidth != textureHeight) {
            throw new GdxRuntimeException("texture width and height must be square when using mipmapping in OpenGL ES 1.x");
         }

         Gdx.gl.glTexParameterf(3553, 33169, 1.0F);
         Gdx.gl.glTexImage2D(3553, 0, pixmap.getGLInternalFormat(), pixmap.getWidth(), pixmap.getHeight(), 0, pixmap.getGLFormat(), pixmap.getGLType(), pixmap.getPixels());
         if (disposePixmap) {
            pixmap.dispose();
         }
      } else {
         generateMipMapCPU(pixmap, textureWidth, textureHeight, disposePixmap);
      }

   }

   private static void generateMipMapCPU(Pixmap pixmap, int textureWidth, int textureHeight, boolean disposePixmap) {
      Gdx.gl.glTexImage2D(3553, 0, pixmap.getGLInternalFormat(), pixmap.getWidth(), pixmap.getHeight(), 0, pixmap.getGLFormat(), pixmap.getGLType(), pixmap.getPixels());
      if (Gdx.gl20 == null && textureWidth != textureHeight) {
         throw new GdxRuntimeException("texture width and height must be square when using mipmapping.");
      } else {
         int width = pixmap.getWidth() / 2;
         int height = pixmap.getHeight() / 2;
         int level = 1;
         Pixmap.Blending blending = Pixmap.getBlending();
         Pixmap.setBlending(Pixmap.Blending.None);

         while(width > 0 && height > 0) {
            Pixmap tmp = new Pixmap(width, height, pixmap.getFormat());
            tmp.drawPixmap(pixmap, 0, 0, pixmap.getWidth(), pixmap.getHeight(), 0, 0, width, height);
            if (level > 1 || disposePixmap) {
               pixmap.dispose();
            }

            pixmap = tmp;
            Gdx.gl.glTexImage2D(3553, level, tmp.getGLInternalFormat(), tmp.getWidth(), tmp.getHeight(), 0, tmp.getGLFormat(), tmp.getGLType(), tmp.getPixels());
            width = tmp.getWidth() / 2;
            height = tmp.getHeight() / 2;
            ++level;
         }

         Pixmap.setBlending(blending);
      }
   }
}
