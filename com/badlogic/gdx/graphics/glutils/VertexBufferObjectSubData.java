package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class VertexBufferObjectSubData implements VertexData {
   static final IntBuffer tmpHandle = BufferUtils.newIntBuffer(1);
   final VertexAttributes attributes;
   final FloatBuffer buffer;
   final ByteBuffer byteBuffer;
   int bufferHandle;
   final boolean isDirect;
   final boolean isStatic;
   final int usage;
   boolean isDirty = false;
   boolean isBound = false;

   public VertexBufferObjectSubData(boolean isStatic, int numVertices, VertexAttribute... attributes) {
      this.isStatic = isStatic;
      this.attributes = new VertexAttributes(attributes);
      this.byteBuffer = BufferUtils.newByteBuffer(this.attributes.vertexSize * numVertices);
      this.isDirect = true;
      this.usage = isStatic ? '裤' : '裨';
      this.buffer = this.byteBuffer.asFloatBuffer();
      this.bufferHandle = this.createBufferObject();
      this.buffer.flip();
      this.byteBuffer.flip();
   }

   private int createBufferObject() {
      if (Gdx.gl20 != null) {
         Gdx.gl20.glGenBuffers(1, tmpHandle);
         Gdx.gl20.glBindBuffer(34962, tmpHandle.get(0));
         Gdx.gl20.glBufferData(34962, this.byteBuffer.capacity(), (Buffer)null, this.usage);
         Gdx.gl20.glBindBuffer(34962, 0);
      } else {
         Gdx.gl11.glGenBuffers(1, tmpHandle);
         Gdx.gl11.glBindBuffer(34962, tmpHandle.get(0));
         Gdx.gl11.glBufferData(34962, this.byteBuffer.capacity(), (Buffer)null, this.usage);
         Gdx.gl11.glBindBuffer(34962, 0);
      }

      return tmpHandle.get(0);
   }

   public VertexAttributes getAttributes() {
      return this.attributes;
   }

   public int getNumVertices() {
      return this.buffer.limit() * 4 / this.attributes.vertexSize;
   }

   public int getNumMaxVertices() {
      return this.byteBuffer.capacity() / this.attributes.vertexSize;
   }

   public FloatBuffer getBuffer() {
      this.isDirty = true;
      return this.buffer;
   }

   public void setVertices(float[] vertices, int offset, int count) {
      this.isDirty = true;
      if (this.isDirect) {
         BufferUtils.copy(vertices, this.byteBuffer, count, offset);
         this.buffer.position(0);
         this.buffer.limit(count);
      } else {
         this.buffer.clear();
         this.buffer.put(vertices, offset, count);
         this.buffer.flip();
         this.byteBuffer.position(0);
         this.byteBuffer.limit(this.buffer.limit() << 2);
      }

      if (this.isBound) {
         if (Gdx.gl20 != null) {
            GL20 gl = Gdx.gl20;
            gl.glBufferSubData(34962, 0, this.byteBuffer.limit(), this.byteBuffer);
         } else {
            GL11 gl = Gdx.gl11;
            gl.glBufferSubData(34962, 0, this.byteBuffer.limit(), this.byteBuffer);
         }

         this.isDirty = false;
      }

   }

   public void bind() {
      GL11 gl = Gdx.gl11;
      gl.glBindBuffer(34962, this.bufferHandle);
      if (this.isDirty) {
         this.byteBuffer.limit(this.buffer.limit() * 4);
         gl.glBufferSubData(34962, 0, this.byteBuffer.limit(), this.byteBuffer);
         this.isDirty = false;
      }

      int textureUnit = 0;
      int numAttributes = this.attributes.size();

      for(int i = 0; i < numAttributes; ++i) {
         VertexAttribute attribute = this.attributes.get(i);
         switch(attribute.usage) {
         case 1:
            gl.glEnableClientState(32884);
            gl.glVertexPointer(attribute.numComponents, 5126, this.attributes.vertexSize, attribute.offset);
            break;
         case 2:
         case 4:
            int colorType = 5126;
            if (attribute.usage == 4) {
               colorType = 5121;
            }

            gl.glEnableClientState(32886);
            gl.glColorPointer(attribute.numComponents, colorType, this.attributes.vertexSize, attribute.offset);
            break;
         case 8:
            gl.glEnableClientState(32885);
            gl.glNormalPointer(5126, this.attributes.vertexSize, attribute.offset);
            break;
         case 16:
            gl.glClientActiveTexture('蓀' + textureUnit);
            gl.glEnableClientState(32888);
            gl.glTexCoordPointer(attribute.numComponents, 5126, this.attributes.vertexSize, attribute.offset);
            ++textureUnit;
            break;
         default:
            throw new GdxRuntimeException("unkown vertex attribute type: " + attribute.usage);
         }
      }

      this.isBound = true;
   }

   public void bind(ShaderProgram shader) {
      GL20 gl = Gdx.gl20;
      gl.glBindBuffer(34962, this.bufferHandle);
      if (this.isDirty) {
         this.byteBuffer.limit(this.buffer.limit() * 4);
         gl.glBufferSubData(34962, 0, this.byteBuffer.limit(), this.byteBuffer);
         this.isDirty = false;
      }

      int numAttributes = this.attributes.size();

      for(int i = 0; i < numAttributes; ++i) {
         VertexAttribute attribute = this.attributes.get(i);
         shader.enableVertexAttribute(attribute.alias);
         int colorType = 5126;
         boolean normalize = false;
         if (attribute.usage == 4) {
            colorType = 5121;
            normalize = true;
         }

         shader.setVertexAttribute(attribute.alias, attribute.numComponents, colorType, normalize, this.attributes.vertexSize, attribute.offset);
      }

      this.isBound = true;
   }

   public void unbind() {
      GL11 gl = Gdx.gl11;
      int textureUnit = 0;
      int numAttributes = this.attributes.size();

      for(int i = 0; i < numAttributes; ++i) {
         VertexAttribute attribute = this.attributes.get(i);
         switch(attribute.usage) {
         case 1:
            break;
         case 2:
         case 4:
            gl.glDisableClientState(32886);
            break;
         case 8:
            gl.glDisableClientState(32885);
            break;
         case 16:
            gl.glClientActiveTexture('蓀' + textureUnit);
            gl.glDisableClientState(32888);
            ++textureUnit;
            break;
         default:
            throw new GdxRuntimeException("unkown vertex attribute type: " + attribute.usage);
         }
      }

      gl.glBindBuffer(34962, 0);
      this.isBound = false;
   }

   public void unbind(ShaderProgram shader) {
      GL20 gl = Gdx.gl20;
      int numAttributes = this.attributes.size();

      for(int i = 0; i < numAttributes; ++i) {
         VertexAttribute attribute = this.attributes.get(i);
         shader.disableVertexAttribute(attribute.alias);
      }

      gl.glBindBuffer(34962, 0);
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
         gl.glBindBuffer(34962, 0);
         gl.glDeleteBuffers(1, tmpHandle);
         this.bufferHandle = 0;
      } else {
         tmpHandle.clear();
         tmpHandle.put(this.bufferHandle);
         tmpHandle.flip();
         GL11 gl = Gdx.gl11;
         gl.glBindBuffer(34962, 0);
         gl.glDeleteBuffers(1, tmpHandle);
         this.bufferHandle = 0;
      }

   }

   public int getBufferHandle() {
      return this.bufferHandle;
   }
}
