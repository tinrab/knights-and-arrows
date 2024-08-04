package com.badlogic.gdx.graphics;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public interface GL20 extends GLCommon {
   int GL_ES_VERSION_2_0 = 1;
   int GL_DEPTH_BUFFER_BIT = 256;
   int GL_STENCIL_BUFFER_BIT = 1024;
   int GL_COLOR_BUFFER_BIT = 16384;
   int GL_FALSE = 0;
   int GL_TRUE = 1;
   int GL_POINTS = 0;
   int GL_LINES = 1;
   int GL_LINE_LOOP = 2;
   int GL_LINE_STRIP = 3;
   int GL_TRIANGLES = 4;
   int GL_TRIANGLE_STRIP = 5;
   int GL_TRIANGLE_FAN = 6;
   int GL_ZERO = 0;
   int GL_ONE = 1;
   int GL_SRC_COLOR = 768;
   int GL_ONE_MINUS_SRC_COLOR = 769;
   int GL_SRC_ALPHA = 770;
   int GL_ONE_MINUS_SRC_ALPHA = 771;
   int GL_DST_ALPHA = 772;
   int GL_ONE_MINUS_DST_ALPHA = 773;
   int GL_DST_COLOR = 774;
   int GL_ONE_MINUS_DST_COLOR = 775;
   int GL_SRC_ALPHA_SATURATE = 776;
   int GL_FUNC_ADD = 32774;
   int GL_BLEND_EQUATION = 32777;
   int GL_BLEND_EQUATION_RGB = 32777;
   int GL_BLEND_EQUATION_ALPHA = 34877;
   int GL_FUNC_SUBTRACT = 32778;
   int GL_FUNC_REVERSE_SUBTRACT = 32779;
   int GL_BLEND_DST_RGB = 32968;
   int GL_BLEND_SRC_RGB = 32969;
   int GL_BLEND_DST_ALPHA = 32970;
   int GL_BLEND_SRC_ALPHA = 32971;
   int GL_CONSTANT_COLOR = 32769;
   int GL_ONE_MINUS_CONSTANT_COLOR = 32770;
   int GL_CONSTANT_ALPHA = 32771;
   int GL_ONE_MINUS_CONSTANT_ALPHA = 32772;
   int GL_BLEND_COLOR = 32773;
   int GL_ARRAY_BUFFER = 34962;
   int GL_ELEMENT_ARRAY_BUFFER = 34963;
   int GL_ARRAY_BUFFER_BINDING = 34964;
   int GL_ELEMENT_ARRAY_BUFFER_BINDING = 34965;
   int GL_STREAM_DRAW = 35040;
   int GL_STATIC_DRAW = 35044;
   int GL_DYNAMIC_DRAW = 35048;
   int GL_BUFFER_SIZE = 34660;
   int GL_BUFFER_USAGE = 34661;
   int GL_CURRENT_VERTEX_ATTRIB = 34342;
   int GL_FRONT = 1028;
   int GL_BACK = 1029;
   int GL_FRONT_AND_BACK = 1032;
   int GL_TEXTURE_2D = 3553;
   int GL_CULL_FACE = 2884;
   int GL_BLEND = 3042;
   int GL_DITHER = 3024;
   int GL_STENCIL_TEST = 2960;
   int GL_DEPTH_TEST = 2929;
   int GL_SCISSOR_TEST = 3089;
   int GL_POLYGON_OFFSET_FILL = 32823;
   int GL_SAMPLE_ALPHA_TO_COVERAGE = 32926;
   int GL_SAMPLE_COVERAGE = 32928;
   int GL_NO_ERROR = 0;
   int GL_INVALID_ENUM = 1280;
   int GL_INVALID_VALUE = 1281;
   int GL_INVALID_OPERATION = 1282;
   int GL_OUT_OF_MEMORY = 1285;
   int GL_CW = 2304;
   int GL_CCW = 2305;
   int GL_LINE_WIDTH = 2849;
   int GL_ALIASED_POINT_SIZE_RANGE = 33901;
   int GL_ALIASED_LINE_WIDTH_RANGE = 33902;
   int GL_CULL_FACE_MODE = 2885;
   int GL_FRONT_FACE = 2886;
   int GL_DEPTH_RANGE = 2928;
   int GL_DEPTH_WRITEMASK = 2930;
   int GL_DEPTH_CLEAR_VALUE = 2931;
   int GL_DEPTH_FUNC = 2932;
   int GL_STENCIL_CLEAR_VALUE = 2961;
   int GL_STENCIL_FUNC = 2962;
   int GL_STENCIL_FAIL = 2964;
   int GL_STENCIL_PASS_DEPTH_FAIL = 2965;
   int GL_STENCIL_PASS_DEPTH_PASS = 2966;
   int GL_STENCIL_REF = 2967;
   int GL_STENCIL_VALUE_MASK = 2963;
   int GL_STENCIL_WRITEMASK = 2968;
   int GL_STENCIL_BACK_FUNC = 34816;
   int GL_STENCIL_BACK_FAIL = 34817;
   int GL_STENCIL_BACK_PASS_DEPTH_FAIL = 34818;
   int GL_STENCIL_BACK_PASS_DEPTH_PASS = 34819;
   int GL_STENCIL_BACK_REF = 36003;
   int GL_STENCIL_BACK_VALUE_MASK = 36004;
   int GL_STENCIL_BACK_WRITEMASK = 36005;
   int GL_VIEWPORT = 2978;
   int GL_SCISSOR_BOX = 3088;
   int GL_COLOR_CLEAR_VALUE = 3106;
   int GL_COLOR_WRITEMASK = 3107;
   int GL_UNPACK_ALIGNMENT = 3317;
   int GL_PACK_ALIGNMENT = 3333;
   int GL_MAX_TEXTURE_SIZE = 3379;
   int GL_MAX_TEXTURE_UNITS = 34018;
   int GL_MAX_VIEWPORT_DIMS = 3386;
   int GL_SUBPIXEL_BITS = 3408;
   int GL_RED_BITS = 3410;
   int GL_GREEN_BITS = 3411;
   int GL_BLUE_BITS = 3412;
   int GL_ALPHA_BITS = 3413;
   int GL_DEPTH_BITS = 3414;
   int GL_STENCIL_BITS = 3415;
   int GL_POLYGON_OFFSET_UNITS = 10752;
   int GL_POLYGON_OFFSET_FACTOR = 32824;
   int GL_TEXTURE_BINDING_2D = 32873;
   int GL_SAMPLE_BUFFERS = 32936;
   int GL_SAMPLES = 32937;
   int GL_SAMPLE_COVERAGE_VALUE = 32938;
   int GL_SAMPLE_COVERAGE_INVERT = 32939;
   int GL_NUM_COMPRESSED_TEXTURE_FORMATS = 34466;
   int GL_COMPRESSED_TEXTURE_FORMATS = 34467;
   int GL_DONT_CARE = 4352;
   int GL_FASTEST = 4353;
   int GL_NICEST = 4354;
   int GL_GENERATE_MIPMAP_HINT = 33170;
   int GL_BYTE = 5120;
   int GL_UNSIGNED_BYTE = 5121;
   int GL_SHORT = 5122;
   int GL_UNSIGNED_SHORT = 5123;
   int GL_INT = 5124;
   int GL_UNSIGNED_INT = 5125;
   int GL_FLOAT = 5126;
   int GL_FIXED = 5132;
   int GL_DEPTH_COMPONENT = 6402;
   int GL_ALPHA = 6406;
   int GL_RGB = 6407;
   int GL_RGBA = 6408;
   int GL_LUMINANCE = 6409;
   int GL_LUMINANCE_ALPHA = 6410;
   int GL_UNSIGNED_SHORT_4_4_4_4 = 32819;
   int GL_UNSIGNED_SHORT_5_5_5_1 = 32820;
   int GL_UNSIGNED_SHORT_5_6_5 = 33635;
   int GL_FRAGMENT_SHADER = 35632;
   int GL_VERTEX_SHADER = 35633;
   int GL_MAX_VERTEX_ATTRIBS = 34921;
   int GL_MAX_VERTEX_UNIFORM_VECTORS = 36347;
   int GL_MAX_VARYING_VECTORS = 36348;
   int GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS = 35661;
   int GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS = 35660;
   int GL_MAX_TEXTURE_IMAGE_UNITS = 34930;
   int GL_MAX_FRAGMENT_UNIFORM_VECTORS = 36349;
   int GL_SHADER_TYPE = 35663;
   int GL_DELETE_STATUS = 35712;
   int GL_LINK_STATUS = 35714;
   int GL_VALIDATE_STATUS = 35715;
   int GL_ATTACHED_SHADERS = 35717;
   int GL_ACTIVE_UNIFORMS = 35718;
   int GL_ACTIVE_UNIFORM_MAX_LENGTH = 35719;
   int GL_ACTIVE_ATTRIBUTES = 35721;
   int GL_ACTIVE_ATTRIBUTE_MAX_LENGTH = 35722;
   int GL_SHADING_LANGUAGE_VERSION = 35724;
   int GL_CURRENT_PROGRAM = 35725;
   int GL_NEVER = 512;
   int GL_LESS = 513;
   int GL_EQUAL = 514;
   int GL_LEQUAL = 515;
   int GL_GREATER = 516;
   int GL_NOTEQUAL = 517;
   int GL_GEQUAL = 518;
   int GL_ALWAYS = 519;
   int GL_KEEP = 7680;
   int GL_REPLACE = 7681;
   int GL_INCR = 7682;
   int GL_DECR = 7683;
   int GL_INVERT = 5386;
   int GL_INCR_WRAP = 34055;
   int GL_DECR_WRAP = 34056;
   int GL_VENDOR = 7936;
   int GL_RENDERER = 7937;
   int GL_VERSION = 7938;
   int GL_EXTENSIONS = 7939;
   int GL_NEAREST = 9728;
   int GL_LINEAR = 9729;
   int GL_NEAREST_MIPMAP_NEAREST = 9984;
   int GL_LINEAR_MIPMAP_NEAREST = 9985;
   int GL_NEAREST_MIPMAP_LINEAR = 9986;
   int GL_LINEAR_MIPMAP_LINEAR = 9987;
   int GL_TEXTURE_MAG_FILTER = 10240;
   int GL_TEXTURE_MIN_FILTER = 10241;
   int GL_TEXTURE_WRAP_S = 10242;
   int GL_TEXTURE_WRAP_T = 10243;
   int GL_TEXTURE = 5890;
   int GL_TEXTURE_CUBE_MAP = 34067;
   int GL_TEXTURE_BINDING_CUBE_MAP = 34068;
   int GL_TEXTURE_CUBE_MAP_POSITIVE_X = 34069;
   int GL_TEXTURE_CUBE_MAP_NEGATIVE_X = 34070;
   int GL_TEXTURE_CUBE_MAP_POSITIVE_Y = 34071;
   int GL_TEXTURE_CUBE_MAP_NEGATIVE_Y = 34072;
   int GL_TEXTURE_CUBE_MAP_POSITIVE_Z = 34073;
   int GL_TEXTURE_CUBE_MAP_NEGATIVE_Z = 34074;
   int GL_MAX_CUBE_MAP_TEXTURE_SIZE = 34076;
   int GL_TEXTURE0 = 33984;
   int GL_TEXTURE1 = 33985;
   int GL_TEXTURE2 = 33986;
   int GL_TEXTURE3 = 33987;
   int GL_TEXTURE4 = 33988;
   int GL_TEXTURE5 = 33989;
   int GL_TEXTURE6 = 33990;
   int GL_TEXTURE7 = 33991;
   int GL_TEXTURE8 = 33992;
   int GL_TEXTURE9 = 33993;
   int GL_TEXTURE10 = 33994;
   int GL_TEXTURE11 = 33995;
   int GL_TEXTURE12 = 33996;
   int GL_TEXTURE13 = 33997;
   int GL_TEXTURE14 = 33998;
   int GL_TEXTURE15 = 33999;
   int GL_TEXTURE16 = 34000;
   int GL_TEXTURE17 = 34001;
   int GL_TEXTURE18 = 34002;
   int GL_TEXTURE19 = 34003;
   int GL_TEXTURE20 = 34004;
   int GL_TEXTURE21 = 34005;
   int GL_TEXTURE22 = 34006;
   int GL_TEXTURE23 = 34007;
   int GL_TEXTURE24 = 34008;
   int GL_TEXTURE25 = 34009;
   int GL_TEXTURE26 = 34010;
   int GL_TEXTURE27 = 34011;
   int GL_TEXTURE28 = 34012;
   int GL_TEXTURE29 = 34013;
   int GL_TEXTURE30 = 34014;
   int GL_TEXTURE31 = 34015;
   int GL_ACTIVE_TEXTURE = 34016;
   int GL_REPEAT = 10497;
   int GL_CLAMP_TO_EDGE = 33071;
   int GL_MIRRORED_REPEAT = 33648;
   int GL_FLOAT_VEC2 = 35664;
   int GL_FLOAT_VEC3 = 35665;
   int GL_FLOAT_VEC4 = 35666;
   int GL_INT_VEC2 = 35667;
   int GL_INT_VEC3 = 35668;
   int GL_INT_VEC4 = 35669;
   int GL_BOOL = 35670;
   int GL_BOOL_VEC2 = 35671;
   int GL_BOOL_VEC3 = 35672;
   int GL_BOOL_VEC4 = 35673;
   int GL_FLOAT_MAT2 = 35674;
   int GL_FLOAT_MAT3 = 35675;
   int GL_FLOAT_MAT4 = 35676;
   int GL_SAMPLER_2D = 35678;
   int GL_SAMPLER_CUBE = 35680;
   int GL_VERTEX_ATTRIB_ARRAY_ENABLED = 34338;
   int GL_VERTEX_ATTRIB_ARRAY_SIZE = 34339;
   int GL_VERTEX_ATTRIB_ARRAY_STRIDE = 34340;
   int GL_VERTEX_ATTRIB_ARRAY_TYPE = 34341;
   int GL_VERTEX_ATTRIB_ARRAY_NORMALIZED = 34922;
   int GL_VERTEX_ATTRIB_ARRAY_POINTER = 34373;
   int GL_VERTEX_ATTRIB_ARRAY_BUFFER_BINDING = 34975;
   int GL_IMPLEMENTATION_COLOR_READ_TYPE = 35738;
   int GL_IMPLEMENTATION_COLOR_READ_FORMAT = 35739;
   int GL_COMPILE_STATUS = 35713;
   int GL_INFO_LOG_LENGTH = 35716;
   int GL_SHADER_SOURCE_LENGTH = 35720;
   int GL_SHADER_COMPILER = 36346;
   int GL_SHADER_BINARY_FORMATS = 36344;
   int GL_NUM_SHADER_BINARY_FORMATS = 36345;
   int GL_LOW_FLOAT = 36336;
   int GL_MEDIUM_FLOAT = 36337;
   int GL_HIGH_FLOAT = 36338;
   int GL_LOW_INT = 36339;
   int GL_MEDIUM_INT = 36340;
   int GL_HIGH_INT = 36341;
   int GL_FRAMEBUFFER = 36160;
   int GL_RENDERBUFFER = 36161;
   int GL_RGBA4 = 32854;
   int GL_RGB5_A1 = 32855;
   int GL_RGB565 = 36194;
   int GL_DEPTH_COMPONENT16 = 33189;
   int GL_STENCIL_INDEX = 6401;
   int GL_STENCIL_INDEX8 = 36168;
   int GL_RENDERBUFFER_WIDTH = 36162;
   int GL_RENDERBUFFER_HEIGHT = 36163;
   int GL_RENDERBUFFER_INTERNAL_FORMAT = 36164;
   int GL_RENDERBUFFER_RED_SIZE = 36176;
   int GL_RENDERBUFFER_GREEN_SIZE = 36177;
   int GL_RENDERBUFFER_BLUE_SIZE = 36178;
   int GL_RENDERBUFFER_ALPHA_SIZE = 36179;
   int GL_RENDERBUFFER_DEPTH_SIZE = 36180;
   int GL_RENDERBUFFER_STENCIL_SIZE = 36181;
   int GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE = 36048;
   int GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME = 36049;
   int GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_LEVEL = 36050;
   int GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_CUBE_MAP_FACE = 36051;
   int GL_COLOR_ATTACHMENT0 = 36064;
   int GL_DEPTH_ATTACHMENT = 36096;
   int GL_STENCIL_ATTACHMENT = 36128;
   int GL_NONE = 0;
   int GL_FRAMEBUFFER_COMPLETE = 36053;
   int GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 36054;
   int GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 36055;
   int GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS = 36057;
   int GL_FRAMEBUFFER_UNSUPPORTED = 36061;
   int GL_FRAMEBUFFER_BINDING = 36006;
   int GL_RENDERBUFFER_BINDING = 36007;
   int GL_MAX_RENDERBUFFER_SIZE = 34024;
   int GL_INVALID_FRAMEBUFFER_OPERATION = 1286;
   int GL_VERTEX_PROGRAM_POINT_SIZE = 34370;
   int GL_COVERAGE_BUFFER_BIT_NV = 32768;

   void glAttachShader(int var1, int var2);

   void glBindAttribLocation(int var1, int var2, String var3);

   void glBindBuffer(int var1, int var2);

   void glBindFramebuffer(int var1, int var2);

   void glBindRenderbuffer(int var1, int var2);

   void glBlendColor(float var1, float var2, float var3, float var4);

   void glBlendEquation(int var1);

   void glBlendEquationSeparate(int var1, int var2);

   void glBlendFuncSeparate(int var1, int var2, int var3, int var4);

   void glBufferData(int var1, int var2, Buffer var3, int var4);

   void glBufferSubData(int var1, int var2, int var3, Buffer var4);

   int glCheckFramebufferStatus(int var1);

   void glCompileShader(int var1);

   int glCreateProgram();

   int glCreateShader(int var1);

   void glDeleteBuffers(int var1, IntBuffer var2);

   void glDeleteFramebuffers(int var1, IntBuffer var2);

   void glDeleteProgram(int var1);

   void glDeleteRenderbuffers(int var1, IntBuffer var2);

   void glDeleteShader(int var1);

   void glDetachShader(int var1, int var2);

   void glDisableVertexAttribArray(int var1);

   void glDrawElements(int var1, int var2, int var3, int var4);

   void glEnableVertexAttribArray(int var1);

   void glFramebufferRenderbuffer(int var1, int var2, int var3, int var4);

   void glFramebufferTexture2D(int var1, int var2, int var3, int var4, int var5);

   void glGenBuffers(int var1, IntBuffer var2);

   void glGenerateMipmap(int var1);

   void glGenFramebuffers(int var1, IntBuffer var2);

   void glGenRenderbuffers(int var1, IntBuffer var2);

   String glGetActiveAttrib(int var1, int var2, IntBuffer var3, Buffer var4);

   String glGetActiveUniform(int var1, int var2, IntBuffer var3, Buffer var4);

   void glGetAttachedShaders(int var1, int var2, Buffer var3, IntBuffer var4);

   int glGetAttribLocation(int var1, String var2);

   void glGetBooleanv(int var1, Buffer var2);

   void glGetBufferParameteriv(int var1, int var2, IntBuffer var3);

   void glGetFloatv(int var1, FloatBuffer var2);

   void glGetFramebufferAttachmentParameteriv(int var1, int var2, int var3, IntBuffer var4);

   void glGetProgramiv(int var1, int var2, IntBuffer var3);

   String glGetProgramInfoLog(int var1);

   void glGetRenderbufferParameteriv(int var1, int var2, IntBuffer var3);

   void glGetShaderiv(int var1, int var2, IntBuffer var3);

   String glGetShaderInfoLog(int var1);

   void glGetShaderPrecisionFormat(int var1, int var2, IntBuffer var3, IntBuffer var4);

   void glGetShaderSource(int var1, int var2, Buffer var3, String var4);

   void glGetTexParameterfv(int var1, int var2, FloatBuffer var3);

   void glGetTexParameteriv(int var1, int var2, IntBuffer var3);

   void glGetUniformfv(int var1, int var2, FloatBuffer var3);

   void glGetUniformiv(int var1, int var2, IntBuffer var3);

   int glGetUniformLocation(int var1, String var2);

   void glGetVertexAttribfv(int var1, int var2, FloatBuffer var3);

   void glGetVertexAttribiv(int var1, int var2, IntBuffer var3);

   void glGetVertexAttribPointerv(int var1, int var2, Buffer var3);

   boolean glIsBuffer(int var1);

   boolean glIsEnabled(int var1);

   boolean glIsFramebuffer(int var1);

   boolean glIsProgram(int var1);

   boolean glIsRenderbuffer(int var1);

   boolean glIsShader(int var1);

   boolean glIsTexture(int var1);

   void glLinkProgram(int var1);

   void glReleaseShaderCompiler();

   void glRenderbufferStorage(int var1, int var2, int var3, int var4);

   void glSampleCoverage(float var1, boolean var2);

   void glShaderBinary(int var1, IntBuffer var2, int var3, Buffer var4, int var5);

   void glShaderSource(int var1, String var2);

   void glStencilFuncSeparate(int var1, int var2, int var3, int var4);

   void glStencilMaskSeparate(int var1, int var2);

   void glStencilOpSeparate(int var1, int var2, int var3, int var4);

   void glTexParameterfv(int var1, int var2, FloatBuffer var3);

   void glTexParameteri(int var1, int var2, int var3);

   void glTexParameteriv(int var1, int var2, IntBuffer var3);

   void glUniform1f(int var1, float var2);

   void glUniform1fv(int var1, int var2, FloatBuffer var3);

   void glUniform1i(int var1, int var2);

   void glUniform1iv(int var1, int var2, IntBuffer var3);

   void glUniform2f(int var1, float var2, float var3);

   void glUniform2fv(int var1, int var2, FloatBuffer var3);

   void glUniform2i(int var1, int var2, int var3);

   void glUniform2iv(int var1, int var2, IntBuffer var3);

   void glUniform3f(int var1, float var2, float var3, float var4);

   void glUniform3fv(int var1, int var2, FloatBuffer var3);

   void glUniform3i(int var1, int var2, int var3, int var4);

   void glUniform3iv(int var1, int var2, IntBuffer var3);

   void glUniform4f(int var1, float var2, float var3, float var4, float var5);

   void glUniform4fv(int var1, int var2, FloatBuffer var3);

   void glUniform4i(int var1, int var2, int var3, int var4, int var5);

   void glUniform4iv(int var1, int var2, IntBuffer var3);

   void glUniformMatrix2fv(int var1, int var2, boolean var3, FloatBuffer var4);

   void glUniformMatrix3fv(int var1, int var2, boolean var3, FloatBuffer var4);

   void glUniformMatrix4fv(int var1, int var2, boolean var3, FloatBuffer var4);

   void glUseProgram(int var1);

   void glValidateProgram(int var1);

   void glVertexAttrib1f(int var1, float var2);

   void glVertexAttrib1fv(int var1, FloatBuffer var2);

   void glVertexAttrib2f(int var1, float var2, float var3);

   void glVertexAttrib2fv(int var1, FloatBuffer var2);

   void glVertexAttrib3f(int var1, float var2, float var3, float var4);

   void glVertexAttrib3fv(int var1, FloatBuffer var2);

   void glVertexAttrib4f(int var1, float var2, float var3, float var4, float var5);

   void glVertexAttrib4fv(int var1, FloatBuffer var2);

   void glVertexAttribPointer(int var1, int var2, int var3, boolean var4, int var5, Buffer var6);

   void glVertexAttribPointer(int var1, int var2, int var3, boolean var4, int var5, int var6);
}
