package com.badlogic.gdx.graphics;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public interface GL11 extends GL10 {
   int GL_OES_VERSION_1_0 = 1;
   int GL_MAX_ELEMENTS_VERTICES = 33000;
   int GL_MAX_ELEMENTS_INDICES = 33001;
   int GL_POLYGON_SMOOTH_HINT = 3155;
   int GL_VERSION_ES_CM_1_0 = 1;
   int GL_VERSION_ES_CL_1_0 = 1;
   int GL_VERSION_ES_CM_1_1 = 1;
   int GL_VERSION_ES_CL_1_1 = 1;
   int GL_CLIP_PLANE0 = 12288;
   int GL_CLIP_PLANE1 = 12289;
   int GL_CLIP_PLANE2 = 12290;
   int GL_CLIP_PLANE3 = 12291;
   int GL_CLIP_PLANE4 = 12292;
   int GL_CLIP_PLANE5 = 12293;
   int GL_CURRENT_COLOR = 2816;
   int GL_CURRENT_NORMAL = 2818;
   int GL_CURRENT_TEXTURE_COORDS = 2819;
   int GL_POINT_SIZE = 2833;
   int GL_POINT_SIZE_MIN = 33062;
   int GL_POINT_SIZE_MAX = 33063;
   int GL_POINT_FADE_THRESHOLD_SIZE = 33064;
   int GL_POINT_DISTANCE_ATTENUATION = 33065;
   int GL_LINE_WIDTH = 2849;
   int GL_CULL_FACE_MODE = 2885;
   int GL_FRONT_FACE = 2886;
   int GL_SHADE_MODEL = 2900;
   int GL_DEPTH_RANGE = 2928;
   int GL_DEPTH_WRITEMASK = 2930;
   int GL_DEPTH_CLEAR_VALUE = 2931;
   int GL_DEPTH_FUNC = 2932;
   int GL_STENCIL_CLEAR_VALUE = 2961;
   int GL_STENCIL_FUNC = 2962;
   int GL_STENCIL_VALUE_MASK = 2963;
   int GL_STENCIL_FAIL = 2964;
   int GL_STENCIL_PASS_DEPTH_FAIL = 2965;
   int GL_STENCIL_PASS_DEPTH_PASS = 2966;
   int GL_STENCIL_REF = 2967;
   int GL_STENCIL_WRITEMASK = 2968;
   int GL_MATRIX_MODE = 2976;
   int GL_VIEWPORT = 2978;
   int GL_MODELVIEW_STACK_DEPTH = 2979;
   int GL_PROJECTION_STACK_DEPTH = 2980;
   int GL_TEXTURE_STACK_DEPTH = 2981;
   int GL_MODELVIEW_MATRIX = 2982;
   int GL_PROJECTION_MATRIX = 2983;
   int GL_TEXTURE_MATRIX = 2984;
   int GL_ALPHA_TEST_FUNC = 3009;
   int GL_ALPHA_TEST_REF = 3010;
   int GL_BLEND_DST = 3040;
   int GL_BLEND_SRC = 3041;
   int GL_LOGIC_OP_MODE = 3056;
   int GL_SCISSOR_BOX = 3088;
   int GL_COLOR_CLEAR_VALUE = 3106;
   int GL_COLOR_WRITEMASK = 3107;
   int GL_MAX_CLIP_PLANES = 3378;
   int GL_POLYGON_OFFSET_UNITS = 10752;
   int GL_POLYGON_OFFSET_FACTOR = 32824;
   int GL_TEXTURE_BINDING_2D = 32873;
   int GL_VERTEX_ARRAY_SIZE = 32890;
   int GL_VERTEX_ARRAY_TYPE = 32891;
   int GL_VERTEX_ARRAY_STRIDE = 32892;
   int GL_NORMAL_ARRAY_TYPE = 32894;
   int GL_NORMAL_ARRAY_STRIDE = 32895;
   int GL_COLOR_ARRAY_SIZE = 32897;
   int GL_COLOR_ARRAY_TYPE = 32898;
   int GL_COLOR_ARRAY_STRIDE = 32899;
   int GL_TEXTURE_COORD_ARRAY_SIZE = 32904;
   int GL_TEXTURE_COORD_ARRAY_TYPE = 32905;
   int GL_TEXTURE_COORD_ARRAY_STRIDE = 32906;
   int GL_VERTEX_ARRAY_POINTER = 32910;
   int GL_NORMAL_ARRAY_POINTER = 32911;
   int GL_COLOR_ARRAY_POINTER = 32912;
   int GL_TEXTURE_COORD_ARRAY_POINTER = 32914;
   int GL_SAMPLE_BUFFERS = 32936;
   int GL_SAMPLES = 32937;
   int GL_SAMPLE_COVERAGE_VALUE = 32938;
   int GL_SAMPLE_COVERAGE_INVERT = 32939;
   int GL_GENERATE_MIPMAP_HINT = 33170;
   int GL_GENERATE_MIPMAP = 33169;
   int GL_ACTIVE_TEXTURE = 34016;
   int GL_CLIENT_ACTIVE_TEXTURE = 34017;
   int GL_ARRAY_BUFFER = 34962;
   int GL_ELEMENT_ARRAY_BUFFER = 34963;
   int GL_ARRAY_BUFFER_BINDING = 34964;
   int GL_ELEMENT_ARRAY_BUFFER_BINDING = 34965;
   int GL_VERTEX_ARRAY_BUFFER_BINDING = 34966;
   int GL_NORMAL_ARRAY_BUFFER_BINDING = 34967;
   int GL_COLOR_ARRAY_BUFFER_BINDING = 34968;
   int GL_TEXTURE_COORD_ARRAY_BUFFER_BINDING = 34970;
   int GL_STATIC_DRAW = 35044;
   int GL_DYNAMIC_DRAW = 35048;
   int GL_BUFFER_SIZE = 34660;
   int GL_BUFFER_USAGE = 34661;
   int GL_SUBTRACT = 34023;
   int GL_COMBINE = 34160;
   int GL_COMBINE_RGB = 34161;
   int GL_COMBINE_ALPHA = 34162;
   int GL_RGB_SCALE = 34163;
   int GL_ADD_SIGNED = 34164;
   int GL_INTERPOLATE = 34165;
   int GL_CONSTANT = 34166;
   int GL_PRIMARY_COLOR = 34167;
   int GL_PREVIOUS = 34168;
   int GL_OPERAND0_RGB = 34192;
   int GL_OPERAND1_RGB = 34193;
   int GL_OPERAND2_RGB = 34194;
   int GL_OPERAND0_ALPHA = 34200;
   int GL_OPERAND1_ALPHA = 34201;
   int GL_OPERAND2_ALPHA = 34202;
   int GL_ALPHA_SCALE = 3356;
   int GL_SRC0_RGB = 34176;
   int GL_SRC1_RGB = 34177;
   int GL_SRC2_RGB = 34178;
   int GL_SRC0_ALPHA = 34184;
   int GL_SRC1_ALPHA = 34185;
   int GL_SRC2_ALPHA = 34186;
   int GL_DOT3_RGB = 34478;
   int GL_DOT3_RGBA = 34479;
   int GL_POINT_SIZE_ARRAY_OES = 35740;
   int GL_POINT_SIZE_ARRAY_TYPE_OES = 35210;
   int GL_POINT_SIZE_ARRAY_STRIDE_OES = 35211;
   int GL_POINT_SIZE_ARRAY_POINTER_OES = 35212;
   int GL_POINT_SIZE_ARRAY_BUFFER_BINDING_OES = 35743;
   int GL_POINT_SPRITE_OES = 34913;
   int GL_COORD_REPLACE_OES = 34914;
   int GL_OES_point_size_array = 1;
   int GL_OES_point_sprite = 1;

   void glClipPlanef(int var1, float[] var2, int var3);

   void glClipPlanef(int var1, FloatBuffer var2);

   void glGetClipPlanef(int var1, float[] var2, int var3);

   void glGetClipPlanef(int var1, FloatBuffer var2);

   void glGetFloatv(int var1, float[] var2, int var3);

   void glGetFloatv(int var1, FloatBuffer var2);

   void glGetLightfv(int var1, int var2, float[] var3, int var4);

   void glGetLightfv(int var1, int var2, FloatBuffer var3);

   void glGetMaterialfv(int var1, int var2, float[] var3, int var4);

   void glGetMaterialfv(int var1, int var2, FloatBuffer var3);

   void glGetTexParameterfv(int var1, int var2, float[] var3, int var4);

   void glGetTexParameterfv(int var1, int var2, FloatBuffer var3);

   void glPointParameterf(int var1, float var2);

   void glPointParameterfv(int var1, float[] var2, int var3);

   void glPointParameterfv(int var1, FloatBuffer var2);

   void glTexParameterfv(int var1, int var2, float[] var3, int var4);

   void glTexParameterfv(int var1, int var2, FloatBuffer var3);

   void glBindBuffer(int var1, int var2);

   void glBufferData(int var1, int var2, Buffer var3, int var4);

   void glBufferSubData(int var1, int var2, int var3, Buffer var4);

   void glColor4ub(byte var1, byte var2, byte var3, byte var4);

   void glDeleteBuffers(int var1, int[] var2, int var3);

   void glDeleteBuffers(int var1, IntBuffer var2);

   void glGetBooleanv(int var1, boolean[] var2, int var3);

   void glGetBooleanv(int var1, IntBuffer var2);

   void glGetBufferParameteriv(int var1, int var2, int[] var3, int var4);

   void glGetBufferParameteriv(int var1, int var2, IntBuffer var3);

   void glGenBuffers(int var1, int[] var2, int var3);

   void glGenBuffers(int var1, IntBuffer var2);

   void glGetPointerv(int var1, Buffer[] var2);

   void glGetTexEnviv(int var1, int var2, int[] var3, int var4);

   void glGetTexEnviv(int var1, int var2, IntBuffer var3);

   void glGetTexParameteriv(int var1, int var2, int[] var3, int var4);

   void glGetTexParameteriv(int var1, int var2, IntBuffer var3);

   boolean glIsBuffer(int var1);

   boolean glIsEnabled(int var1);

   boolean glIsTexture(int var1);

   void glTexEnvi(int var1, int var2, int var3);

   void glTexEnviv(int var1, int var2, int[] var3, int var4);

   void glTexEnviv(int var1, int var2, IntBuffer var3);

   void glTexParameteri(int var1, int var2, int var3);

   void glTexParameteriv(int var1, int var2, int[] var3, int var4);

   void glTexParameteriv(int var1, int var2, IntBuffer var3);

   void glPointSizePointerOES(int var1, int var2, Buffer var3);

   void glVertexPointer(int var1, int var2, int var3, int var4);

   void glColorPointer(int var1, int var2, int var3, int var4);

   void glNormalPointer(int var1, int var2, int var3);

   void glTexCoordPointer(int var1, int var2, int var3, int var4);

   void glDrawElements(int var1, int var2, int var3, int var4);
}
