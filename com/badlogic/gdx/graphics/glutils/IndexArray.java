package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.utils.BufferUtils;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class IndexArray implements IndexData {
   static final IntBuffer tmpHandle = BufferUtils.newIntBuffer(1);
   ShortBuffer buffer;
   ByteBuffer byteBuffer;

   public IndexArray(int maxIndices) {
      this.byteBuffer = BufferUtils.newUnsafeByteBuffer(maxIndices * 2);
      this.buffer = this.byteBuffer.asShortBuffer();
      this.buffer.flip();
      this.byteBuffer.flip();
   }

   public int getNumIndices() {
      return this.buffer.limit();
   }

   public int getNumMaxIndices() {
      return this.buffer.capacity();
   }

   public void setIndices(short[] indices, int offset, int count) {
      this.buffer.clear();
      this.buffer.put(indices, offset, count);
      this.buffer.flip();
      this.byteBuffer.position(0);
      this.byteBuffer.limit(count << 1);
   }

   public ShortBuffer getBuffer() {
      return this.buffer;
   }

   public void bind() {
   }

   public void unbind() {
   }

   public void invalidate() {
   }

   public void dispose() {
      BufferUtils.disposeUnsafeByteBuffer(this.byteBuffer);
   }
}
