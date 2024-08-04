package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.utils.BufferUtils;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class VertexArray implements VertexData {
   final VertexAttributes attributes;
   final FloatBuffer buffer;
   final ByteBuffer byteBuffer;
   boolean isBound;

   public VertexArray(int numVertices, VertexAttribute... attributes) {
      this(numVertices, new VertexAttributes(attributes));
   }

   public VertexArray(int numVertices, VertexAttributes attributes) {
      this.isBound = false;
      this.attributes = attributes;
      this.byteBuffer = BufferUtils.newUnsafeByteBuffer(this.attributes.vertexSize * numVertices);
      this.buffer = this.byteBuffer.asFloatBuffer();
      this.buffer.flip();
      this.byteBuffer.flip();
   }

   public void dispose() {
      BufferUtils.disposeUnsafeByteBuffer(this.byteBuffer);
   }

   public FloatBuffer getBuffer() {
      return this.buffer;
   }

   public int getNumVertices() {
      return this.buffer.limit() * 4 / this.attributes.vertexSize;
   }

   public int getNumMaxVertices() {
      return this.byteBuffer.capacity() / this.attributes.vertexSize;
   }

   public void setVertices(float[] vertices, int offset, int count) {
      BufferUtils.copy(vertices, this.byteBuffer, count, offset);
      this.buffer.position(0);
      this.buffer.limit(count);
   }

   public void bind() {
      GL10 gl = Gdx.gl10;
      int textureUnit = 0;
      int numAttributes = this.attributes.size();
      this.byteBuffer.limit(this.buffer.limit() * 4);

      for(int i = 0; i < numAttributes; ++i) {
         VertexAttribute attribute = this.attributes.get(i);
         switch(attribute.usage) {
         case 1:
            this.byteBuffer.position(attribute.offset);
            gl.glEnableClientState(32884);
            gl.glVertexPointer(attribute.numComponents, 5126, this.attributes.vertexSize, this.byteBuffer);
            break;
         case 2:
         case 4:
            int colorType = 5126;
            if (attribute.usage == 4) {
               colorType = 5121;
            }

            this.byteBuffer.position(attribute.offset);
            gl.glEnableClientState(32886);
            gl.glColorPointer(attribute.numComponents, colorType, this.attributes.vertexSize, this.byteBuffer);
            break;
         case 8:
            this.byteBuffer.position(attribute.offset);
            gl.glEnableClientState(32885);
            gl.glNormalPointer(5126, this.attributes.vertexSize, this.byteBuffer);
            break;
         case 16:
            gl.glClientActiveTexture('蓀' + textureUnit);
            gl.glEnableClientState(32888);
            this.byteBuffer.position(attribute.offset);
            gl.glTexCoordPointer(attribute.numComponents, 5126, this.attributes.vertexSize, this.byteBuffer);
            ++textureUnit;
         }
      }

      this.isBound = true;
   }

   public void unbind() {
      GL10 gl = Gdx.gl10;
      int textureUnit = 0;
      int numAttributes = this.attributes.size();

      for(int i = 0; i < numAttributes; ++i) {
         VertexAttribute attribute = this.attributes.get(i);
         switch(attribute.usage) {
         case 1:
         default:
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
         }
      }

      this.byteBuffer.position(0);
      this.isBound = false;
   }

   public void bind(ShaderProgram shader) {
      GL20 gl = Gdx.gl20;
      int numAttributes = this.attributes.size();
      this.byteBuffer.limit(this.buffer.limit() * 4);

      for(int i = 0; i < numAttributes; ++i) {
         VertexAttribute attribute = this.attributes.get(i);
         shader.enableVertexAttribute(attribute.alias);
         int colorType = 5126;
         boolean normalize = false;
         if (attribute.usage == 4) {
            colorType = 5121;
            normalize = true;
         }

         this.byteBuffer.position(attribute.offset);
         shader.setVertexAttribute(attribute.alias, attribute.numComponents, colorType, normalize, this.attributes.vertexSize, this.byteBuffer);
      }

      this.isBound = true;
   }

   public void unbind(ShaderProgram shader) {
      GL20 gl = Gdx.gl20;
      int numAttributes = this.attributes.size();

      for(int i = 0; i < numAttributes; ++i) {
         VertexAttribute attribute = this.attributes.get(i);
         shader.disableVertexAttribute(attribute.alias);
      }

      this.isBound = false;
   }

   public VertexAttributes getAttributes() {
      return this.attributes;
   }
}
