package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class IndexBufferObjectSubData implements IndexData {
   static final IntBuffer tmpHandle = BufferUtils.newIntBuffer(1);
   ShortBuffer buffer;
   ByteBuffer byteBuffer;
   int bufferHandle;
   final boolean isDirect;
   boolean isDirty = true;
   boolean isBound = false;
   final int usage;

   public IndexBufferObjectSubData(boolean isStatic, int maxIndices) {
      this.byteBuffer = BufferUtils.newByteBuffer(maxIndices * 2);
      this.isDirect = true;
      this.usage = isStatic ? '裤' : '裨';
      this.buffer = this.byteBuffer.asShortBuffer();
      this.buffer.flip();
      this.byteBuffer.flip();
      this.bufferHandle = this.createBufferObject();
   }

   public IndexBufferObjectSubData(int maxIndices) {
      this.byteBuffer = BufferUtils.newByteBuffer(maxIndices * 2);
      this.isDirect = true;
      this.usage = 35044;
      this.buffer = this.byteBuffer.asShortBuffer();
      this.buffer.flip();
      this.byteBuffer.flip();
      this.bufferHandle = this.createBufferObject();
   }

   private int createBufferObject() {
      if (Gdx.gl20 != null) {
         Gdx.gl20.glGenBuffers(1, tmpHandle);
         Gdx.gl20.glBindBuffer(34963, tmpHandle.get(0));
         Gdx.gl20.glBufferData(34963, this.byteBuffer.capacity(), (Buffer)null, this.usage);
         Gdx.gl20.glBindBuffer(34963, 0);
         return tmpHandle.get(0);
      } else if (Gdx.gl11 != null) {
         Gdx.gl11.glGenBuffers(1, tmpHandle);
         Gdx.gl11.glBindBuffer(34963, tmpHandle.get(0));
         Gdx.gl11.glBufferData(34963, this.byteBuffer.capacity(), (Buffer)null, this.usage);
         Gdx.gl11.glBindBuffer(34963, 0);
         return tmpHandle.get(0);
      } else {
         return 0;
      }
   }

   public int getNumIndices() {
      return this.buffer.limit();
   }

   public int getNumMaxIndices() {
      return this.buffer.capacity();
   }

   public void setIndices(short[] indices, int offset, int count) {
      this.isDirty = true;
      this.buffer.clear();
      this.buffer.put(indices, offset, count);
      this.buffer.flip();
      this.byteBuffer.position(0);
      this.byteBuffer.limit(count << 1);
      if (this.isBound) {
         if (Gdx.gl11 != null) {
            GL11 gl = Gdx.gl11;
            gl.glBufferSubData(34963, 0, this.byteBuffer.limit(), this.byteBuffer);
         } else if (Gdx.gl11 != null) {
            GL20 gl = Gdx.gl20;
            gl.glBufferSubData(34963, 0, this.byteBuffer.limit(), this.byteBuffer);
         }

         this.isDirty = false;
      }

   }

   public ShortBuffer getBuffer() {
      this.isDirty = true;
      return this.buffer;
   }

   public void bind() {
      if (this.bufferHandle == 0) {
         throw new GdxRuntimeException("buuh");
      } else {
         if (Gdx.gl11 != null) {
            GL11 gl = Gdx.gl11;
            gl.glBindBuffer(34963, this.bufferHandle);
            if (this.isDirty) {
               this.byteBuffer.limit(this.buffer.limit() * 2);
               gl.glBufferSubData(34963, 0, this.byteBuffer.limit(), this.byteBuffer);
               this.isDirty = false;
            }
         } else {
            GL20 gl = Gdx.gl20;
            gl.glBindBuffer(34963, this.bufferHandle);
            if (this.isDirty) {
               this.byteBuffer.limit(this.buffer.limit() * 2);
               gl.glBufferSubData(34963, 0, this.byteBuffer.limit(), this.byteBuffer);
               this.isDirty = false;
            }
         }

         this.isBound = true;
      }
   }

   public void unbind() {
      if (Gdx.gl11 != null) {
         Gdx.gl11.glBindBuffer(34963, 0);
      } else if (Gdx.gl20 != null) {
         Gdx.gl20.glBindBuffer(34963, 0);
      }

      this.isBound = false;
   }

   public void invalidate() {
      this.bufferHandle = this.createBufferObject();
      this.isDirty = true;
   }

   public void dispose() {
      if (Gdx.gl20 != null) {
         tmpHandle.clear();
         tmpHandle.put(this.bufferHandle);
         tmpHandle.flip();
         GL20 gl = Gdx.gl20;
         gl.glBindBuffer(34963, 0);
         gl.glDeleteBuffers(1, tmpHandle);
         this.bufferHandle = 0;
      } else if (Gdx.gl11 != null) {
         tmpHandle.clear();
         tmpHandle.put(this.bufferHandle);
         tmpHandle.flip();
         GL11 gl = Gdx.gl11;
         gl.glBindBuffer(34963, 0);
         gl.glDeleteBuffers(1, tmpHandle);
         this.bufferHandle = 0;
      }

   }
}
