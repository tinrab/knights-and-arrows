package com.badlogic.gdx.graphics;

import java.nio.Buffer;
import java.nio.IntBuffer;

public interface GLCommon {
   int GL_GENERATE_MIPMAP = 33169;
   int GL_TEXTURE_MAX_ANISOTROPY_EXT = 34046;
   int GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT = 34047;

   void glActiveTexture(int var1);

   void glBindTexture(int var1, int var2);

   void glBlendFunc(int var1, int var2);

   void glClear(int var1);

   void glClearColor(float var1, float var2, float var3, float var4);

   void glClearDepthf(float var1);

   void glClearStencil(int var1);

   void glColorMask(boolean var1, boolean var2, boolean var3, boolean var4);

   void glCompressedTexImage2D(int var1, int var2, int var3, int var4, int var5, int var6, int var7, Buffer var8);

   void glCompressedTexSubImage2D(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, Buffer var9);

   void glCopyTexImage2D(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8);

   void glCopyTexSubImage2D(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8);

   void glCullFace(int var1);

   void glDeleteTextures(int var1, IntBuffer var2);

   void glDepthFunc(int var1);

   void glDepthMask(boolean var1);

   void glDepthRangef(float var1, float var2);

   void glDisable(int var1);

   void glDrawArrays(int var1, int var2, int var3);

   void glDrawElements(int var1, int var2, int var3, Buffer var4);

   void glEnable(int var1);

   void glFinish();

   void glFlush();

   void glFrontFace(int var1);

   void glGenTextures(int var1, IntBuffer var2);

   int glGetError();

   void glGetIntegerv(int var1, IntBuffer var2);

   String glGetString(int var1);

   void glHint(int var1, int var2);

   void glLineWidth(float var1);

   void glPixelStorei(int var1, int var2);

   void glPolygonOffset(float var1, float var2);

   void glReadPixels(int var1, int var2, int var3, int var4, int var5, int var6, Buffer var7);

   void glScissor(int var1, int var2, int var3, int var4);

   void glStencilFunc(int var1, int var2, int var3);

   void glStencilMask(int var1);

   void glStencilOp(int var1, int var2, int var3);

   void glTexImage2D(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, Buffer var9);

   void glTexParameterf(int var1, int var2, float var3);

   void glTexSubImage2D(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, Buffer var9);

   void glViewport(int var1, int var2, int var3, int var4);
}
