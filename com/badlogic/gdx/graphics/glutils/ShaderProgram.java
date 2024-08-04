package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectMap;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ShaderProgram implements Disposable {
   public static final String POSITION_ATTRIBUTE = "a_position";
   public static final String NORMAL_ATTRIBUTE = "a_normal";
   public static final String COLOR_ATTRIBUTE = "a_color";
   public static final String TEXCOORD_ATTRIBUTE = "a_texCoord";
   public static final String TANGENT_ATTRIBUTE = "a_tangent";
   public static final String BINORMAL_ATTRIBUTE = "a_binormal";
   public static boolean pedantic = true;
   private static final ObjectMap<Application, List<ShaderProgram>> shaders = new ObjectMap();
   private String log;
   private boolean isCompiled;
   private final ObjectIntMap<String> uniforms;
   private final ObjectIntMap<String> uniformTypes;
   private final ObjectIntMap<String> uniformSizes;
   private String[] uniformNames;
   private final ObjectIntMap<String> attributes;
   private final ObjectIntMap<String> attributeTypes;
   private final ObjectIntMap<String> attributeSizes;
   private String[] attributeNames;
   private int program;
   private int vertexShaderHandle;
   private int fragmentShaderHandle;
   private final FloatBuffer matrix;
   private final String vertexShaderSource;
   private final String fragmentShaderSource;
   private boolean invalidated;
   private ByteBuffer buffer;
   private FloatBuffer floatBuffer;
   private IntBuffer intBuffer;
   private int refCount;
   static final IntBuffer intbuf = BufferUtils.newIntBuffer(1);
   IntBuffer params;
   IntBuffer type;

   public ShaderProgram(String vertexShader, String fragmentShader) {
      this.log = "";
      this.uniforms = new ObjectIntMap();
      this.uniformTypes = new ObjectIntMap();
      this.uniformSizes = new ObjectIntMap();
      this.attributes = new ObjectIntMap();
      this.attributeTypes = new ObjectIntMap();
      this.attributeSizes = new ObjectIntMap();
      this.buffer = null;
      this.floatBuffer = null;
      this.intBuffer = null;
      this.refCount = 0;
      this.params = BufferUtils.newIntBuffer(1);
      this.type = BufferUtils.newIntBuffer(1);
      if (vertexShader == null) {
         throw new IllegalArgumentException("vertex shader must not be null");
      } else if (fragmentShader == null) {
         throw new IllegalArgumentException("fragment shader must not be null");
      } else {
         this.vertexShaderSource = vertexShader;
         this.fragmentShaderSource = fragmentShader;
         this.matrix = BufferUtils.newFloatBuffer(16);
         this.compileShaders(vertexShader, fragmentShader);
         if (this.isCompiled()) {
            this.fetchAttributes();
            this.fetchUniforms();
            this.addManagedShader(Gdx.app, this);
         }

      }
   }

   public ShaderProgram(FileHandle vertexShader, FileHandle fragmentShader) {
      this(vertexShader.readString(), fragmentShader.readString());
   }

   private void compileShaders(String vertexShader, String fragmentShader) {
      this.vertexShaderHandle = this.loadShader(35633, vertexShader);
      this.fragmentShaderHandle = this.loadShader(35632, fragmentShader);
      if (this.vertexShaderHandle != -1 && this.fragmentShaderHandle != -1) {
         this.program = this.linkProgram();
         if (this.program == -1) {
            this.isCompiled = false;
         } else {
            this.isCompiled = true;
         }
      } else {
         this.isCompiled = false;
      }
   }

   private int loadShader(int type, String source) {
      GL20 gl = Gdx.graphics.getGL20();
      IntBuffer intbuf = BufferUtils.newIntBuffer(1);
      int shader = gl.glCreateShader(type);
      if (shader == 0) {
         return -1;
      } else {
         gl.glShaderSource(shader, source);
         gl.glCompileShader(shader);
         gl.glGetShaderiv(shader, 35713, intbuf);
         int compiled = intbuf.get(0);
         if (compiled == 0) {
            String infoLog = gl.glGetShaderInfoLog(shader);
            this.log = this.log + infoLog;
            return -1;
         } else {
            return shader;
         }
      }
   }

   private int linkProgram() {
      GL20 gl = Gdx.graphics.getGL20();
      int program = gl.glCreateProgram();
      if (program == 0) {
         return -1;
      } else {
         gl.glAttachShader(program, this.vertexShaderHandle);
         gl.glAttachShader(program, this.fragmentShaderHandle);
         gl.glLinkProgram(program);
         ByteBuffer tmp = ByteBuffer.allocateDirect(4);
         tmp.order(ByteOrder.nativeOrder());
         IntBuffer intbuf = tmp.asIntBuffer();
         gl.glGetProgramiv(program, 35714, intbuf);
         int linked = intbuf.get(0);
         if (linked == 0) {
            this.log = Gdx.gl20.glGetProgramInfoLog(program);
            return -1;
         } else {
            return program;
         }
      }
   }

   public String getLog() {
      if (this.isCompiled) {
         this.log = Gdx.gl20.glGetProgramInfoLog(this.program);
         return this.log;
      } else {
         return this.log;
      }
   }

   public boolean isCompiled() {
      return this.isCompiled;
   }

   private int fetchAttributeLocation(String name) {
      GL20 gl = Gdx.graphics.getGL20();
      int location;
      if ((location = this.attributes.get(name, -2)) == -2) {
         location = gl.glGetAttribLocation(this.program, name);
         this.attributes.put(name, location);
      }

      return location;
   }

   private int fetchUniformLocation(String name) {
      return this.fetchUniformLocation(name, pedantic);
   }

   public int fetchUniformLocation(String name, boolean pedantic) {
      GL20 gl = Gdx.graphics.getGL20();
      int location;
      if ((location = this.uniforms.get(name, -2)) == -2) {
         location = gl.glGetUniformLocation(this.program, name);
         if (location == -1 && pedantic) {
            throw new IllegalArgumentException("no uniform with name '" + name + "' in shader");
         }

         this.uniforms.put(name, location);
      }

      return location;
   }

   public void setUniformi(String name, int value) {
      GL20 gl = Gdx.graphics.getGL20();
      this.checkManaged();
      int location = this.fetchUniformLocation(name);
      gl.glUniform1i(location, value);
   }

   public void setUniformi(int location, int value) {
      GL20 gl = Gdx.graphics.getGL20();
      this.checkManaged();
      gl.glUniform1i(location, value);
   }

   public void setUniformi(String name, int value1, int value2) {
      GL20 gl = Gdx.graphics.getGL20();
      this.checkManaged();
      int location = this.fetchUniformLocation(name);
      gl.glUniform2i(location, value1, value2);
   }

   public void setUniformi(int location, int value1, int value2) {
      GL20 gl = Gdx.graphics.getGL20();
      this.checkManaged();
      gl.glUniform2i(location, value1, value2);
   }

   public void setUniformi(String name, int value1, int value2, int value3) {
      GL20 gl = Gdx.graphics.getGL20();
      this.checkManaged();
      int location = this.fetchUniformLocation(name);
      gl.glUniform3i(location, value1, value2, value3);
   }

   public void setUniformi(int location, int value1, int value2, int value3) {
      GL20 gl = Gdx.graphics.getGL20();
      this.checkManaged();
      gl.glUniform3i(location, value1, value2, value3);
   }

   public void setUniformi(String name, int value1, int value2, int value3, int value4) {
      GL20 gl = Gdx.graphics.getGL20();
      this.checkManaged();
      int location = this.fetchUniformLocation(name);
      gl.glUniform4i(location, value1, value2, value3, value4);
   }

   public void setUniformi(int location, int value1, int value2, int value3, int value4) {
      GL20 gl = Gdx.graphics.getGL20();
      this.checkManaged();
      gl.glUniform4i(location, value1, value2, value3, value4);
   }

   public void setUniformf(String name, float value) {
      GL20 gl = Gdx.graphics.getGL20();
      this.checkManaged();
      int location = this.fetchUniformLocation(name);
      gl.glUniform1f(location, value);
   }

   public void setUniformf(int location, float value) {
      GL20 gl = Gdx.graphics.getGL20();
      this.checkManaged();
      gl.glUniform1f(location, value);
   }

   public void setUniformf(String name, float value1, float value2) {
      GL20 gl = Gdx.graphics.getGL20();
      this.checkManaged();
      int location = this.fetchUniformLocation(name);
      gl.glUniform2f(location, value1, value2);
   }

   public void setUniformf(int location, float value1, float value2) {
      GL20 gl = Gdx.graphics.getGL20();
      this.checkManaged();
      gl.glUniform2f(location, value1, value2);
   }

   public void setUniformf(String name, float value1, float value2, float value3) {
      GL20 gl = Gdx.graphics.getGL20();
      this.checkManaged();
      int location = this.fetchUniformLocation(name);
      gl.glUniform3f(location, value1, value2, value3);
   }

   public void setUniformf(int location, float value1, float value2, float value3) {
      GL20 gl = Gdx.graphics.getGL20();
      this.checkManaged();
      gl.glUniform3f(location, value1, value2, value3);
   }

   public void setUniformf(String name, float value1, float value2, float value3, float value4) {
      GL20 gl = Gdx.graphics.getGL20();
      this.checkManaged();
      int location = this.fetchUniformLocation(name);
      gl.glUniform4f(location, value1, value2, value3, value4);
   }

   public void setUniformf(int location, float value1, float value2, float value3, float value4) {
      GL20 gl = Gdx.graphics.getGL20();
      this.checkManaged();
      gl.glUniform4f(location, value1, value2, value3, value4);
   }

   public void setUniform1fv(String name, float[] values, int offset, int length) {
      GL20 gl = Gdx.graphics.getGL20();
      this.checkManaged();
      int location = this.fetchUniformLocation(name);
      this.ensureBufferCapacity(length << 2);
      this.floatBuffer.clear();
      BufferUtils.copy(values, this.floatBuffer, length, offset);
      gl.glUniform1fv(location, length, this.floatBuffer);
   }

   public void setUniform1fv(int location, float[] values, int offset, int length) {
      GL20 gl = Gdx.graphics.getGL20();
      this.checkManaged();
      this.ensureBufferCapacity(length << 2);
      this.floatBuffer.clear();
      BufferUtils.copy(values, this.floatBuffer, length, offset);
      gl.glUniform1fv(location, length, this.floatBuffer);
   }

   public void setUniform2fv(String name, float[] values, int offset, int length) {
      GL20 gl = Gdx.graphics.getGL20();
      this.checkManaged();
      int location = this.fetchUniformLocation(name);
      this.ensureBufferCapacity(length << 2);
      this.floatBuffer.clear();
      BufferUtils.copy(values, this.floatBuffer, length, offset);
      gl.glUniform2fv(location, length / 2, this.floatBuffer);
   }

   public void setUniform2fv(int location, float[] values, int offset, int length) {
      GL20 gl = Gdx.graphics.getGL20();
      this.checkManaged();
      this.ensureBufferCapacity(length << 2);
      this.floatBuffer.clear();
      BufferUtils.copy(values, this.floatBuffer, length, offset);
      gl.glUniform2fv(location, length / 2, this.floatBuffer);
   }

   public void setUniform3fv(String name, float[] values, int offset, int length) {
      GL20 gl = Gdx.graphics.getGL20();
      this.checkManaged();
      int location = this.fetchUniformLocation(name);
      this.ensureBufferCapacity(length << 2);
      this.floatBuffer.clear();
      BufferUtils.copy(values, this.floatBuffer, length, offset);
      gl.glUniform3fv(location, length / 3, this.floatBuffer);
   }

   public void setUniform3fv(int location, float[] values, int offset, int length) {
      GL20 gl = Gdx.graphics.getGL20();
      this.checkManaged();
      this.ensureBufferCapacity(length << 2);
      this.floatBuffer.clear();
      BufferUtils.copy(values, this.floatBuffer, length, offset);
      gl.glUniform3fv(location, length / 3, this.floatBuffer);
   }

   public void setUniform4fv(String name, float[] values, int offset, int length) {
      GL20 gl = Gdx.graphics.getGL20();
      this.checkManaged();
      int location = this.fetchUniformLocation(name);
      this.ensureBufferCapacity(length << 2);
      this.floatBuffer.clear();
      BufferUtils.copy(values, this.floatBuffer, length, offset);
      gl.glUniform4fv(location, length / 4, this.floatBuffer);
   }

   public void setUniform4fv(int location, float[] values, int offset, int length) {
      GL20 gl = Gdx.graphics.getGL20();
      this.checkManaged();
      this.ensureBufferCapacity(length << 2);
      this.floatBuffer.clear();
      BufferUtils.copy(values, this.floatBuffer, length, offset);
      gl.glUniform4fv(location, length / 4, this.floatBuffer);
   }

   public void setUniformMatrix(String name, Matrix4 matrix) {
      this.setUniformMatrix(name, matrix, false);
   }

   public void setUniformMatrix(String name, Matrix4 matrix, boolean transpose) {
      GL20 gl = Gdx.graphics.getGL20();
      this.checkManaged();
      int location = this.fetchUniformLocation(name);
      this.matrix.clear();
      BufferUtils.copy(matrix.val, this.matrix, matrix.val.length, 0);
      gl.glUniformMatrix4fv(location, 1, transpose, this.matrix);
   }

   public void setUniformMatrix(int location, Matrix4 matrix) {
      this.setUniformMatrix(location, matrix, false);
   }

   public void setUniformMatrix(int location, Matrix4 matrix, boolean transpose) {
      GL20 gl = Gdx.graphics.getGL20();
      this.checkManaged();
      this.matrix.clear();
      BufferUtils.copy(matrix.val, this.matrix, matrix.val.length, 0);
      gl.glUniformMatrix4fv(location, 1, transpose, this.matrix);
   }

   public void setUniformMatrix(String name, Matrix3 matrix) {
      this.setUniformMatrix(name, matrix, false);
   }

   public void setUniformMatrix(String name, Matrix3 matrix, boolean transpose) {
      GL20 gl = Gdx.graphics.getGL20();
      this.checkManaged();
      int location = this.fetchUniformLocation(name);
      float[] vals = matrix.getValues();
      this.matrix.clear();
      BufferUtils.copy(vals, this.matrix, vals.length, 0);
      gl.glUniformMatrix3fv(location, 1, transpose, this.matrix);
   }

   public void setUniformMatrix(int location, Matrix3 matrix) {
      this.setUniformMatrix(location, matrix, false);
   }

   public void setUniformMatrix(int location, Matrix3 matrix, boolean transpose) {
      GL20 gl = Gdx.graphics.getGL20();
      this.checkManaged();
      float[] vals = matrix.getValues();
      this.matrix.clear();
      BufferUtils.copy(vals, this.matrix, vals.length, 0);
      gl.glUniformMatrix3fv(location, 1, transpose, this.matrix);
   }

   public void setUniformMatrix3fv(String name, FloatBuffer buffer, int count, boolean transpose) {
      GL20 gl = Gdx.graphics.getGL20();
      this.checkManaged();
      buffer.position(0);
      int location = this.fetchUniformLocation(name);
      gl.glUniformMatrix3fv(location, count, transpose, buffer);
   }

   public void setUniformMatrix4fv(String name, FloatBuffer buffer, int count, boolean transpose) {
      GL20 gl = Gdx.graphics.getGL20();
      this.checkManaged();
      buffer.position(0);
      int location = this.fetchUniformLocation(name);
      gl.glUniformMatrix4fv(location, count, transpose, buffer);
   }

   public void setUniformMatrix4fv(int location, float[] values, int offset, int length) {
      GL20 gl = Gdx.graphics.getGL20();
      this.checkManaged();
      this.ensureBufferCapacity(length << 2);
      this.floatBuffer.clear();
      BufferUtils.copy(values, this.floatBuffer, length, offset);
      gl.glUniformMatrix4fv(location, length / 16, false, this.floatBuffer);
   }

   public void setUniformMatrix4fv(String name, float[] values, int offset, int length) {
      GL20 gl = Gdx.graphics.getGL20();
      this.checkManaged();
      this.ensureBufferCapacity(length << 2);
      this.floatBuffer.clear();
      BufferUtils.copy(values, this.floatBuffer, length, offset);
      int location = this.fetchUniformLocation(name);
      gl.glUniformMatrix4fv(location, length / 16, false, this.floatBuffer);
   }

   public void setUniformf(String name, Vector2 values) {
      this.setUniformf(name, values.x, values.y);
   }

   public void setUniformf(int location, Vector2 values) {
      this.setUniformf(location, values.x, values.y);
   }

   public void setUniformf(String name, Vector3 values) {
      this.setUniformf(name, values.x, values.y, values.z);
   }

   public void setUniformf(int location, Vector3 values) {
      this.setUniformf(location, values.x, values.y, values.z);
   }

   public void setUniformf(String name, Color values) {
      this.setUniformf(name, values.r, values.g, values.b, values.a);
   }

   public void setUniformf(int location, Color values) {
      this.setUniformf(location, values.r, values.g, values.b, values.a);
   }

   public void setVertexAttribute(String name, int size, int type, boolean normalize, int stride, Buffer buffer) {
      GL20 gl = Gdx.graphics.getGL20();
      this.checkManaged();
      int location = this.fetchAttributeLocation(name);
      if (location != -1) {
         gl.glVertexAttribPointer(location, size, type, normalize, stride, buffer);
      }
   }

   public void setVertexAttribute(int location, int size, int type, boolean normalize, int stride, Buffer buffer) {
      GL20 gl = Gdx.graphics.getGL20();
      this.checkManaged();
      gl.glVertexAttribPointer(location, size, type, normalize, stride, buffer);
   }

   public void setVertexAttribute(String name, int size, int type, boolean normalize, int stride, int offset) {
      GL20 gl = Gdx.graphics.getGL20();
      this.checkManaged();
      int location = this.fetchAttributeLocation(name);
      if (location != -1) {
         gl.glVertexAttribPointer(location, size, type, normalize, stride, offset);
      }
   }

   public void setVertexAttribute(int location, int size, int type, boolean normalize, int stride, int offset) {
      GL20 gl = Gdx.graphics.getGL20();
      this.checkManaged();
      gl.glVertexAttribPointer(location, size, type, normalize, stride, offset);
   }

   public void begin() {
      GL20 gl = Gdx.graphics.getGL20();
      this.checkManaged();
      gl.glUseProgram(this.program);
   }

   public void end() {
      GL20 gl = Gdx.graphics.getGL20();
      gl.glUseProgram(0);
   }

   public void dispose() {
      GL20 gl = Gdx.graphics.getGL20();
      gl.glUseProgram(0);
      gl.glDeleteShader(this.vertexShaderHandle);
      gl.glDeleteShader(this.fragmentShaderHandle);
      gl.glDeleteProgram(this.program);
      if (shaders.get(Gdx.app) != null) {
         ((List)shaders.get(Gdx.app)).remove(this);
      }

   }

   public void disableVertexAttribute(String name) {
      GL20 gl = Gdx.graphics.getGL20();
      this.checkManaged();
      int location = this.fetchAttributeLocation(name);
      if (location != -1) {
         gl.glDisableVertexAttribArray(location);
      }
   }

   public void disableVertexAttribute(int location) {
      GL20 gl = Gdx.graphics.getGL20();
      this.checkManaged();
      gl.glDisableVertexAttribArray(location);
   }

   public void enableVertexAttribute(String name) {
      GL20 gl = Gdx.graphics.getGL20();
      this.checkManaged();
      int location = this.fetchAttributeLocation(name);
      if (location != -1) {
         gl.glEnableVertexAttribArray(location);
      }
   }

   public void enableVertexAttribute(int location) {
      GL20 gl = Gdx.graphics.getGL20();
      this.checkManaged();
      gl.glEnableVertexAttribArray(location);
   }

   private void checkManaged() {
      if (this.invalidated) {
         this.compileShaders(this.vertexShaderSource, this.fragmentShaderSource);
         this.invalidated = false;
      }

   }

   private void addManagedShader(Application app, ShaderProgram shaderProgram) {
      List<ShaderProgram> managedResources = (List)shaders.get(app);
      if (managedResources == null) {
         managedResources = new ArrayList();
      }

      ((List)managedResources).add(shaderProgram);
      shaders.put(app, managedResources);
   }

   public static void invalidateAllShaderPrograms(Application app) {
      if (Gdx.graphics.getGL20() != null) {
         List<ShaderProgram> shaderList = (List)shaders.get(app);
         if (shaderList != null) {
            for(int i = 0; i < shaderList.size(); ++i) {
               ((ShaderProgram)shaderList.get(i)).invalidated = true;
               ((ShaderProgram)shaderList.get(i)).checkManaged();
            }

         }
      }
   }

   public static void clearAllShaderPrograms(Application app) {
      shaders.remove(app);
   }

   public static String getManagedStatus() {
      StringBuilder builder = new StringBuilder();
      int i = false;
      builder.append("Managed shaders/app: { ");
      Iterator var3 = shaders.keys().iterator();

      while(var3.hasNext()) {
         Application app = (Application)var3.next();
         builder.append(((List)shaders.get(app)).size());
         builder.append(" ");
      }

      builder.append("}");
      return builder.toString();
   }

   public void setAttributef(String name, float value1, float value2, float value3, float value4) {
      GL20 gl = Gdx.graphics.getGL20();
      int location = this.fetchAttributeLocation(name);
      gl.glVertexAttrib4f(location, value1, value2, value3, value4);
   }

   private void ensureBufferCapacity(int numBytes) {
      if (this.buffer == null || this.buffer.capacity() < numBytes) {
         this.buffer = BufferUtils.newByteBuffer(numBytes);
         this.floatBuffer = this.buffer.asFloatBuffer();
         this.intBuffer = this.buffer.asIntBuffer();
      }

   }

   private void fetchUniforms() {
      this.params.clear();
      Gdx.gl20.glGetProgramiv(this.program, 35718, this.params);
      int numUniforms = this.params.get(0);
      this.uniformNames = new String[numUniforms];

      for(int i = 0; i < numUniforms; ++i) {
         this.params.clear();
         this.params.put(0, 1);
         this.type.clear();
         String name = Gdx.gl20.glGetActiveUniform(this.program, i, this.params, this.type);
         int location = Gdx.gl20.glGetUniformLocation(this.program, name);
         this.uniforms.put(name, location);
         this.uniformTypes.put(name, this.type.get(0));
         this.uniformSizes.put(name, this.params.get(0));
         this.uniformNames[i] = name;
      }

   }

   private void fetchAttributes() {
      this.params.clear();
      Gdx.gl20.glGetProgramiv(this.program, 35721, this.params);
      int numAttributes = this.params.get(0);
      this.attributeNames = new String[numAttributes];

      for(int i = 0; i < numAttributes; ++i) {
         this.params.clear();
         this.params.put(0, 1);
         this.type.clear();
         String name = Gdx.gl20.glGetActiveAttrib(this.program, i, this.params, this.type);
         int location = Gdx.gl20.glGetAttribLocation(this.program, name);
         this.attributes.put(name, location);
         this.attributeTypes.put(name, this.type.get(0));
         this.attributeSizes.put(name, this.params.get(0));
         this.attributeNames[i] = name;
      }

   }

   public boolean hasAttribute(String name) {
      return this.attributes.containsKey(name);
   }

   public int getAttributeType(String name) {
      int type = this.attributeTypes.get(name, -1);
      return type == -1 ? 0 : type;
   }

   public int getAttributeLocation(String name) {
      int location = this.attributes.get(name, -1);
      return location == -1 ? -1 : location;
   }

   public int getAttributeSize(String name) {
      int size = this.attributeSizes.get(name, -1);
      return size == -1 ? 0 : size;
   }

   public boolean hasUniform(String name) {
      return this.uniforms.containsKey(name);
   }

   public int getUniformType(String name) {
      int type = this.uniformTypes.get(name, -1);
      return type == -1 ? 0 : type;
   }

   public int getUniformLocation(String name) {
      int location = this.uniforms.get(name, -1);
      return location == -1 ? -1 : location;
   }

   public int getUniformSize(String name) {
      int size = this.uniformSizes.get(name, -1);
      return size == -1 ? 0 : size;
   }

   public String[] getAttributes() {
      return this.attributeNames;
   }

   public String[] getUniforms() {
      return this.uniformNames;
   }
}
