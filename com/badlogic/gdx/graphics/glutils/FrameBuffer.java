package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FrameBuffer implements Disposable {
   private static final Map<Application, List<FrameBuffer>> buffers = new HashMap();
   protected Texture colorTexture;
   private static int defaultFramebufferHandle;
   private static boolean defaultFramebufferHandleInitialized = false;
   private int framebufferHandle;
   private int depthbufferHandle;
   protected final int width;
   protected final int height;
   protected final boolean hasDepth;
   protected final Pixmap.Format format;

   public FrameBuffer(Pixmap.Format format, int width, int height, boolean hasDepth) {
      this.width = width;
      this.height = height;
      this.format = format;
      this.hasDepth = hasDepth;
      this.build();
      this.addManagedFrameBuffer(Gdx.app, this);
   }

   protected void setupTexture() {
      this.colorTexture = new Texture(this.width, this.height, this.format);
      this.colorTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
      this.colorTexture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
   }

   private void build() {
      if (!Gdx.graphics.isGL20Available()) {
         throw new GdxRuntimeException("GL2 is required.");
      } else {
         GL20 gl = Gdx.graphics.getGL20();
         IntBuffer handle;
         if (!defaultFramebufferHandleInitialized) {
            defaultFramebufferHandleInitialized = true;
            if (Gdx.app.getType() == Application.ApplicationType.iOS) {
               handle = ByteBuffer.allocateDirect(64).order(ByteOrder.nativeOrder()).asIntBuffer();
               gl.glGetIntegerv(36006, handle);
               defaultFramebufferHandle = handle.get(0);
            } else {
               defaultFramebufferHandle = 0;
            }
         }

         this.setupTexture();
         handle = BufferUtils.newIntBuffer(1);
         gl.glGenFramebuffers(1, handle);
         this.framebufferHandle = handle.get(0);
         if (this.hasDepth) {
            handle.clear();
            gl.glGenRenderbuffers(1, handle);
            this.depthbufferHandle = handle.get(0);
         }

         gl.glBindTexture(3553, this.colorTexture.getTextureObjectHandle());
         if (this.hasDepth) {
            gl.glBindRenderbuffer(36161, this.depthbufferHandle);
            gl.glRenderbufferStorage(36161, 33189, this.colorTexture.getWidth(), this.colorTexture.getHeight());
         }

         gl.glBindFramebuffer(36160, this.framebufferHandle);
         gl.glFramebufferTexture2D(36160, 36064, 3553, this.colorTexture.getTextureObjectHandle(), 0);
         if (this.hasDepth) {
            gl.glFramebufferRenderbuffer(36160, 36096, 36161, this.depthbufferHandle);
         }

         int result = gl.glCheckFramebufferStatus(36160);
         gl.glBindRenderbuffer(36161, 0);
         gl.glBindTexture(3553, 0);
         gl.glBindFramebuffer(36160, defaultFramebufferHandle);
         if (result != 36053) {
            this.colorTexture.dispose();
            if (this.hasDepth) {
               handle.clear();
               handle.put(this.depthbufferHandle);
               handle.flip();
               gl.glDeleteRenderbuffers(1, handle);
            }

            this.colorTexture.dispose();
            handle.clear();
            handle.put(this.framebufferHandle);
            handle.flip();
            gl.glDeleteFramebuffers(1, handle);
            if (result == 36054) {
               throw new IllegalStateException("frame buffer couldn't be constructed: incomplete attachment");
            } else if (result == 36057) {
               throw new IllegalStateException("frame buffer couldn't be constructed: incomplete dimensions");
            } else if (result == 36055) {
               throw new IllegalStateException("frame buffer couldn't be constructed: missing attachment");
            } else if (result == 36061) {
               throw new IllegalStateException("frame buffer couldn't be constructed: unsupported combination of formats");
            } else {
               throw new IllegalStateException("frame buffer couldn't be constructed: unknown error " + result);
            }
         }
      }
   }

   public void dispose() {
      GL20 gl = Gdx.graphics.getGL20();
      IntBuffer handle = BufferUtils.newIntBuffer(1);
      this.colorTexture.dispose();
      if (this.hasDepth) {
         handle.put(this.depthbufferHandle);
         handle.flip();
         gl.glDeleteRenderbuffers(1, handle);
      }

      handle.clear();
      handle.put(this.framebufferHandle);
      handle.flip();
      gl.glDeleteFramebuffers(1, handle);
      if (buffers.get(Gdx.app) != null) {
         ((List)buffers.get(Gdx.app)).remove(this);
      }

   }

   public void begin() {
      Gdx.graphics.getGL20().glViewport(0, 0, this.colorTexture.getWidth(), this.colorTexture.getHeight());
      Gdx.graphics.getGL20().glBindFramebuffer(36160, this.framebufferHandle);
   }

   public void end() {
      Gdx.graphics.getGL20().glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
      Gdx.graphics.getGL20().glBindFramebuffer(36160, defaultFramebufferHandle);
   }

   private void addManagedFrameBuffer(Application app, FrameBuffer frameBuffer) {
      List<FrameBuffer> managedResources = (List)buffers.get(app);
      if (managedResources == null) {
         managedResources = new ArrayList();
      }

      ((List)managedResources).add(frameBuffer);
      buffers.put(app, managedResources);
   }

   public static void invalidateAllFrameBuffers(Application app) {
      if (Gdx.graphics.getGL20() != null) {
         List<FrameBuffer> bufferList = (List)buffers.get(app);
         if (bufferList != null) {
            for(int i = 0; i < bufferList.size(); ++i) {
               ((FrameBuffer)bufferList.get(i)).build();
            }

         }
      }
   }

   public static void clearAllFrameBuffers(Application app) {
      buffers.remove(app);
   }

   public static String getManagedStatus() {
      StringBuilder builder = new StringBuilder();
      int i = false;
      builder.append("Managed buffers/app: { ");
      Iterator var3 = buffers.keySet().iterator();

      while(var3.hasNext()) {
         Application app = (Application)var3.next();
         builder.append(((List)buffers.get(app)).size());
         builder.append(" ");
      }

      builder.append("}");
      return builder.toString();
   }

   public Texture getColorBufferTexture() {
      return this.colorTexture;
   }

   public int getHeight() {
      return this.colorTexture.getHeight();
   }

   public int getWidth() {
      return this.colorTexture.getWidth();
   }
}
