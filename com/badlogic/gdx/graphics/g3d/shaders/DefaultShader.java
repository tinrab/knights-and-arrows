package com.badlogic.gdx.graphics.g3d.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.lights.AmbientCubemap;
import com.badlogic.gdx.graphics.g3d.lights.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.lights.Lights;
import com.badlogic.gdx.graphics.g3d.lights.PointLight;
import com.badlogic.gdx.graphics.g3d.materials.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.materials.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.materials.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.materials.IntAttribute;
import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.graphics.g3d.materials.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.util.Iterator;

public class DefaultShader extends BaseShader {
   private static String defaultVertexShader = null;
   private static String defaultFragmentShader = null;
   protected static long implementedFlags;
   public static boolean ignoreUnimplemented;
   public static int defaultCullFace;
   public static int defaultDepthFunc;
   protected final BaseShader.Input u_projTrans;
   protected final BaseShader.Input u_cameraPosition;
   protected final BaseShader.Input u_cameraDirection;
   protected final BaseShader.Input u_cameraUp;
   protected final BaseShader.Input u_worldTrans;
   protected final BaseShader.Input u_normalMatrix;
   protected final BaseShader.Input u_bones;
   protected final BaseShader.Input u_shininess;
   protected final BaseShader.Input u_opacity;
   protected final BaseShader.Input u_diffuseColor;
   protected final BaseShader.Input u_diffuseTexture;
   protected final BaseShader.Input u_specularColor;
   protected final BaseShader.Input u_specularTexture;
   protected final BaseShader.Input u_normalTexture;
   protected final BaseShader.Input u_alphaTest;
   protected final BaseShader.Input u_ambientLight;
   protected final BaseShader.Input u_ambientCubemap;
   protected final BaseShader.Input u_dirLights0color;
   protected final BaseShader.Input u_dirLights0direction;
   protected final BaseShader.Input u_dirLights1color;
   protected final BaseShader.Input u_pointLights0color;
   protected final BaseShader.Input u_pointLights0position;
   protected final BaseShader.Input u_pointLights0intensity;
   protected final BaseShader.Input u_pointLights1color;
   protected final BaseShader.Input u_fogColor;
   protected int dirLightsLoc;
   protected int dirLightsColorOffset;
   protected int dirLightsDirectionOffset;
   protected int dirLightsSize;
   protected int pointLightsLoc;
   protected int pointLightsColorOffset;
   protected int pointLightsPositionOffset;
   protected int pointLightsIntensityOffset;
   protected int pointLightsSize;
   protected boolean lighting;
   protected boolean fog;
   protected final AmbientCubemap ambientCubemap;
   protected final DirectionalLight[] directionalLights;
   protected final PointLight[] pointLights;
   protected final float[] bones;
   protected long materialMask;
   protected long vertexMask;
   protected static final long tangentAttribute = 64L;
   protected static final long binormalAttribute = 128L;
   protected static final long[] blendAttributes;
   private Mesh currentMesh;
   private Matrix3 normalMatrix;
   private Camera camera;
   private static final Matrix4 idtMatrix;
   Material currentMaterial;
   TextureAttribute currentTextureAttribute;
   private final Vector3 tmpV1;

   static {
      implementedFlags = BlendingAttribute.Type | TextureAttribute.Diffuse | ColorAttribute.Diffuse | ColorAttribute.Specular | FloatAttribute.Shininess;
      ignoreUnimplemented = true;
      defaultCullFace = 1029;
      defaultDepthFunc = 515;
      blendAttributes = new long[]{256L, 512L, 1024L, 2048L, 4096L, 8192L, 16384L, 32768L};
      idtMatrix = new Matrix4();
   }

   public static final String getDefaultVertexShader() {
      if (defaultVertexShader == null) {
         defaultVertexShader = Gdx.files.classpath("com/badlogic/gdx/graphics/g3d/shaders/default.vertex.glsl").readString();
      }

      return defaultVertexShader;
   }

   public static final String getDefaultFragmentShader() {
      if (defaultFragmentShader == null) {
         defaultFragmentShader = Gdx.files.classpath("com/badlogic/gdx/graphics/g3d/shaders/default.fragment.glsl").readString();
      }

      return defaultFragmentShader;
   }

   public DefaultShader(Material material, VertexAttributes attributes, boolean lighting, boolean fog, int numDirectional, int numPoint, int numSpot, int numBones) {
      this(getDefaultVertexShader(), getDefaultFragmentShader(), material, attributes, lighting, fog, numDirectional, numPoint, numSpot, numBones);
   }

   public DefaultShader(long materialMask, long vertexMask, boolean lighting, boolean fog, int numDirectional, int numPoint, int numSpot, int numBones) {
      this(getDefaultVertexShader(), getDefaultFragmentShader(), materialMask, vertexMask, lighting, fog, numDirectional, numPoint, numSpot, numBones);
   }

   public DefaultShader(String vertexShader, String fragmentShader, Material material, VertexAttributes attributes, boolean lighting, boolean fog, int numDirectional, int numPoint, int numSpot, int numBones) {
      this(vertexShader, fragmentShader, material.getMask(), getAttributesMask(attributes), lighting, fog, numDirectional, numPoint, numSpot, numBones);
   }

   public DefaultShader(String vertexShader, String fragmentShader, long materialMask, long vertexMask, boolean lighting, boolean fog, int numDirectional, int numPoint, int numSpot, int numBones) {
      this(createPrefix(materialMask, vertexMask, lighting, fog, numDirectional, numPoint, numSpot, numBones), vertexShader, fragmentShader, materialMask, vertexMask, lighting, fog, numDirectional, numPoint, numSpot, numBones);
   }

   public DefaultShader(String prefix, String vertexShader, String fragmentShader, long materialMask, long vertexMask, boolean lighting, boolean fog, int numDirectional, int numPoint, int numSpot, int numBones) {
      this(new ShaderProgram(prefix + vertexShader, prefix + fragmentShader), materialMask, vertexMask, lighting, fog, numDirectional, numPoint, numSpot, numBones);
   }

   public DefaultShader(ShaderProgram shaderProgram, long materialMask, long vertexMask, boolean lighting, boolean fog, int numDirectional, int numPoint, int numSpot, int numBones) {
      this.u_projTrans = this.register(new BaseShader.Input(2, "u_projTrans"));
      this.u_cameraPosition = this.register(new BaseShader.Input(2, "u_cameraPosition"));
      this.u_cameraDirection = this.register(new BaseShader.Input(2, "u_cameraDirection"));
      this.u_cameraUp = this.register(new BaseShader.Input(2, "u_cameraUp"));
      this.u_worldTrans = this.register(new BaseShader.Input(3, "u_worldTrans"));
      this.u_normalMatrix = this.register(new BaseShader.Input(3, "u_normalMatrix", 0L, 8L));
      this.u_bones = this.register(new BaseShader.Input(3, "u_bones"));
      this.u_shininess = this.register(new BaseShader.Input(3, "u_shininess", FloatAttribute.Shininess));
      this.u_opacity = this.register(new BaseShader.Input(3, "u_opacity", BlendingAttribute.Type));
      this.u_diffuseColor = this.register(new BaseShader.Input(3, "u_diffuseColor", ColorAttribute.Diffuse));
      this.u_diffuseTexture = this.register(new BaseShader.Input(3, "u_diffuseTexture", TextureAttribute.Diffuse));
      this.u_specularColor = this.register(new BaseShader.Input(3, "u_specularColor", ColorAttribute.Specular));
      this.u_specularTexture = this.register(new BaseShader.Input(3, "u_specularTexture", TextureAttribute.Specular));
      this.u_normalTexture = this.register(new BaseShader.Input(3, "u_normalTexture", TextureAttribute.Normal));
      this.u_alphaTest = this.register(new BaseShader.Input(3, "u_alphaTest", FloatAttribute.AlphaTest));
      this.u_ambientLight = this.register(new BaseShader.Input(3, "u_ambientLight"));
      this.u_ambientCubemap = this.register(new BaseShader.Input(3, "u_ambientCubemap"));
      this.u_dirLights0color = this.register(new BaseShader.Input(3, "u_dirLights[0].color"));
      this.u_dirLights0direction = this.register(new BaseShader.Input(3, "u_dirLights[0].direction"));
      this.u_dirLights1color = this.register(new BaseShader.Input(3, "u_dirLights[1].color"));
      this.u_pointLights0color = this.register(new BaseShader.Input(3, "u_pointLights[0].color"));
      this.u_pointLights0position = this.register(new BaseShader.Input(3, "u_pointLights[0].position"));
      this.u_pointLights0intensity = this.register(new BaseShader.Input(3, "u_pointLights[0].intensity"));
      this.u_pointLights1color = this.register(new BaseShader.Input(3, "u_pointLights[1].color"));
      this.u_fogColor = this.register(new BaseShader.Input(3, "u_fogColor"));
      this.ambientCubemap = new AmbientCubemap();
      this.normalMatrix = new Matrix3();
      this.tmpV1 = new Vector3();
      this.program = shaderProgram;
      this.lighting = lighting;
      this.fog = fog;
      this.materialMask = materialMask;
      this.vertexMask = vertexMask;
      this.directionalLights = new DirectionalLight[lighting && numDirectional > 0 ? numDirectional : 0];

      int i;
      for(i = 0; i < this.directionalLights.length; ++i) {
         this.directionalLights[i] = new DirectionalLight();
      }

      this.pointLights = new PointLight[lighting && numPoint > 0 ? numPoint : 0];

      for(i = 0; i < this.pointLights.length; ++i) {
         this.pointLights[i] = new PointLight();
      }

      this.bones = new float[numBones > 0 ? numBones * 16 : 0];
      if (!ignoreUnimplemented && (implementedFlags & materialMask) != materialMask) {
         throw new GdxRuntimeException("Some attributes not implemented yet (" + materialMask + ")");
      }
   }

   public void init() {
      ShaderProgram program = this.program;
      this.program = null;
      this.init(program, this.materialMask, this.vertexMask, 0L);
      this.dirLightsLoc = this.u_dirLights0color.location;
      this.dirLightsColorOffset = this.u_dirLights0color.location - this.dirLightsLoc;
      this.dirLightsDirectionOffset = this.u_dirLights0direction.location - this.dirLightsLoc;
      this.dirLightsSize = this.u_dirLights1color.location - this.dirLightsLoc;
      this.pointLightsLoc = this.u_pointLights0color.location;
      this.pointLightsColorOffset = this.u_pointLights0color.location - this.pointLightsLoc;
      this.pointLightsPositionOffset = this.u_pointLights0position.location - this.pointLightsLoc;
      this.pointLightsIntensityOffset = this.u_pointLights0intensity.location - this.pointLightsLoc;
      this.pointLightsSize = this.u_pointLights1color.location - this.pointLightsLoc;
   }

   protected static long getAttributesMask(VertexAttributes attributes) {
      long result = 0L;
      int currentBone = false;
      int n = attributes.size();

      for(int i = 0; i < n; ++i) {
         long a = (long)attributes.get(i).usage;
         if (a == 64L) {
            a = blendAttributes[attributes.get(i).unit];
         } else if (a == 128L) {
            a = 64L;
         } else if (a == 256L) {
            a = 128L;
         }

         result |= a;
      }

      return result;
   }

   private static String createPrefix(long mask, long attributes, boolean lighting, boolean fog, int numDirectional, int numPoint, int numSpot, int numBones) {
      String prefix = "";
      if ((attributes & 2L) == 2L || (attributes & 4L) == 4L) {
         prefix = prefix + "#define colorFlag\n";
      }

      if ((attributes & 8L) == 8L) {
         prefix = prefix + "#define normalFlag\n";
         if (lighting) {
            prefix = prefix + "#define lightingFlag\n";
            prefix = prefix + "#define ambientCubemapFlag\n";
            prefix = prefix + "#define numDirectionalLights " + numDirectional + "\n";
            prefix = prefix + "#define numPointLights " + numPoint + "\n";
            if (fog) {
               prefix = prefix + "#define fogFlag\n";
            }
         }
      }

      for(int i = 0; i < blendAttributes.length; ++i) {
         if ((attributes & blendAttributes[i]) == blendAttributes[i]) {
            prefix = prefix + "#define boneWeight" + i + "Flag\n";
         }
      }

      if ((attributes & 64L) == 64L) {
         prefix = prefix + "#define tangentFlag\n";
      }

      if ((attributes & 128L) == 128L) {
         prefix = prefix + "#define binormalFlag\n";
      }

      if ((mask & BlendingAttribute.Type) == BlendingAttribute.Type) {
         prefix = prefix + "#define blendedFlag\n";
      }

      if ((mask & TextureAttribute.Diffuse) == TextureAttribute.Diffuse) {
         prefix = prefix + "#define diffuseTextureFlag\n";
      }

      if ((mask & TextureAttribute.Normal) == TextureAttribute.Normal) {
         prefix = prefix + "#define normalTextureFlag\n";
      }

      if ((mask & ColorAttribute.Diffuse) == ColorAttribute.Diffuse) {
         prefix = prefix + "#define diffuseColorFlag\n";
      }

      if ((mask & ColorAttribute.Specular) == ColorAttribute.Specular) {
         prefix = prefix + "#define specularColorFlag\n";
      }

      if ((mask & FloatAttribute.Shininess) == FloatAttribute.Shininess) {
         prefix = prefix + "#define shininessFlag\n";
      }

      if ((mask & FloatAttribute.AlphaTest) == FloatAttribute.AlphaTest) {
         prefix = prefix + "#define alphaTestFlag\n";
      }

      if (numBones > 0) {
         prefix = prefix + "#define numBones " + numBones + "\n";
      }

      return prefix;
   }

   public boolean canRender(Renderable renderable) {
      return this.materialMask == renderable.material.getMask() && this.vertexMask == getAttributesMask(renderable.mesh.getVertexAttributes()) && renderable.lights != null == this.lighting && (renderable.lights != null && renderable.lights.fog != null) == this.fog;
   }

   private final boolean can(long flag) {
      return (this.materialMask & flag) == flag;
   }

   public int compareTo(Shader other) {
      if (other == null) {
         return -1;
      } else {
         return other == this ? 0 : 0;
      }
   }

   public boolean equals(Object obj) {
      return obj instanceof DefaultShader ? this.equals((DefaultShader)obj) : false;
   }

   public boolean equals(DefaultShader obj) {
      return obj == this;
   }

   public void begin(Camera camera, RenderContext context) {
      super.begin(camera, context);
      if (defaultDepthFunc == 0) {
         context.setDepthTest(false, 515);
      } else {
         context.setDepthTest(true, defaultDepthFunc);
      }

      float fogDist = 1.09F / camera.far;
      fogDist *= fogDist;
      this.set(this.u_projTrans, camera.combined);
      this.set(this.u_cameraPosition, camera.position.x, camera.position.y, camera.position.z, fogDist);
      this.set(this.u_cameraDirection, camera.direction);
      this.set(this.u_cameraUp, camera.up);
      DirectionalLight[] var7;
      int var6 = (var7 = this.directionalLights).length;

      int var5;
      for(var5 = 0; var5 < var6; ++var5) {
         DirectionalLight dirLight = var7[var5];
         dirLight.set(0.0F, 0.0F, 0.0F, 0.0F, -1.0F, 0.0F);
      }

      PointLight[] var10;
      var6 = (var10 = this.pointLights).length;

      for(var5 = 0; var5 < var6; ++var5) {
         PointLight pointLight = var10[var5];
         pointLight.set(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
      }

      for(int i = 0; i < this.bones.length; ++i) {
         this.bones[i] = idtMatrix.val[i % 16];
      }

   }

   private void setWorldTransform(Matrix4 value) {
      this.set(this.u_worldTrans, value);
      this.set(this.u_normalMatrix, this.normalMatrix.set(value));
   }

   public void render(Renderable renderable) {
      if (!renderable.material.has(BlendingAttribute.Type)) {
         this.context.setBlending(false, 770, 771);
      }

      this.setWorldTransform(renderable.worldTransform);
      this.bindMaterial(renderable);
      if (this.lighting) {
         this.bindLights(renderable);
      }

      if (this.currentMesh != renderable.mesh) {
         if (this.currentMesh != null) {
            this.currentMesh.unbind(this.program);
         }

         renderable.mesh.setAutoBind(false);
         (this.currentMesh = renderable.mesh).bind(this.program);
      }

      if (this.has(this.u_bones)) {
         for(int i = 0; i < this.bones.length; ++i) {
            int idx = i / 16;
            this.bones[i] = renderable.bones != null && idx < renderable.bones.length && renderable.bones[idx] != null ? renderable.bones[idx].val[i % 16] : idtMatrix.val[i % 16];
         }

         this.program.setUniformMatrix4fv(this.u_bones.location, this.bones, 0, this.bones.length);
      }

      super.render(renderable);
   }

   public void end() {
      if (this.currentMesh != null) {
         this.currentMesh.unbind(this.program);
         this.currentMesh = null;
      }

      this.currentTextureAttribute = null;
      this.currentMaterial = null;
      super.end();
   }

   private final void bindMaterial(Renderable renderable) {
      if (this.currentMaterial != renderable.material) {
         int cullFace = defaultCullFace;
         this.currentMaterial = renderable.material;
         Iterator var4 = this.currentMaterial.iterator();

         while(var4.hasNext()) {
            Material.Attribute attr = (Material.Attribute)var4.next();
            long t = attr.type;
            if (BlendingAttribute.is(t)) {
               this.context.setBlending(true, ((BlendingAttribute)attr).sourceFunction, ((BlendingAttribute)attr).destFunction);
               this.set(this.u_opacity, ((BlendingAttribute)attr).opacity);
            } else if (ColorAttribute.is(t)) {
               ColorAttribute col = (ColorAttribute)attr;
               if ((t & ColorAttribute.Diffuse) == ColorAttribute.Diffuse) {
                  this.set(this.u_diffuseColor, col.color);
               } else if ((t & ColorAttribute.Specular) == ColorAttribute.Specular) {
                  this.set(this.u_specularColor, col.color);
               }
            } else if (TextureAttribute.is(t)) {
               TextureAttribute tex = (TextureAttribute)attr;
               if ((t & TextureAttribute.Diffuse) == TextureAttribute.Diffuse && this.has(this.u_diffuseTexture)) {
                  this.bindTextureAttribute(this.u_diffuseTexture.location, tex);
               }

               if ((t & TextureAttribute.Normal) == TextureAttribute.Normal && this.has(this.u_normalTexture)) {
                  this.bindTextureAttribute(this.u_normalTexture.location, tex);
               }
            } else if ((t & FloatAttribute.Shininess) == FloatAttribute.Shininess) {
               this.set(this.u_shininess, ((FloatAttribute)attr).value);
            } else if ((t & IntAttribute.CullFace) == IntAttribute.CullFace) {
               cullFace = ((IntAttribute)attr).value;
            } else if ((t & FloatAttribute.AlphaTest) == FloatAttribute.AlphaTest) {
               this.set(this.u_alphaTest, ((FloatAttribute)attr).value);
            } else if (!ignoreUnimplemented) {
               throw new GdxRuntimeException("Unknown material attribute: " + attr.toString());
            }
         }

         this.context.setCullFace(cullFace);
      }
   }

   private final void bindTextureAttribute(int uniform, TextureAttribute attribute) {
      int unit = this.context.textureBinder.bind(attribute.textureDescription);
      this.program.setUniformi(uniform, unit);
      this.currentTextureAttribute = attribute;
   }

   private final void bindLights(Renderable renderable) {
      Lights lights = renderable.lights;
      Array<DirectionalLight> dirs = lights.directionalLights;
      Array<PointLight> points = lights.pointLights;
      int i;
      if (this.has(this.u_ambientCubemap)) {
         renderable.worldTransform.getTranslation(this.tmpV1);
         this.ambientCubemap.set(lights.ambientLight);

         for(i = this.directionalLights.length; i < dirs.size; ++i) {
            this.ambientCubemap.add(((DirectionalLight)dirs.get(i)).color, ((DirectionalLight)dirs.get(i)).direction);
         }

         for(i = this.pointLights.length; i < points.size; ++i) {
            this.ambientCubemap.add(((PointLight)points.get(i)).color, ((PointLight)points.get(i)).position, this.tmpV1, ((PointLight)points.get(i)).intensity);
         }

         this.ambientCubemap.clamp();
         this.program.setUniform3fv(this.u_ambientCubemap.location, this.ambientCubemap.data, 0, this.ambientCubemap.data.length);
      }

      int idx;
      if (this.dirLightsLoc >= 0) {
         for(i = 0; i < this.directionalLights.length; ++i) {
            if (dirs != null && i < dirs.size) {
               if (this.directionalLights[i].equals((DirectionalLight)dirs.get(i))) {
                  continue;
               }

               this.directionalLights[i].set((DirectionalLight)dirs.get(i));
            } else {
               if (this.directionalLights[i].color.r == 0.0F && this.directionalLights[i].color.g == 0.0F && this.directionalLights[i].color.b == 0.0F) {
                  continue;
               }

               this.directionalLights[i].color.set(0.0F, 0.0F, 0.0F, 1.0F);
            }

            idx = this.dirLightsLoc + i * this.dirLightsSize;
            this.program.setUniformf(idx + this.dirLightsColorOffset, this.directionalLights[i].color.r, this.directionalLights[i].color.g, this.directionalLights[i].color.b);
            this.program.setUniformf(idx + this.dirLightsDirectionOffset, this.directionalLights[i].direction);
         }
      }

      if (this.pointLightsLoc >= 0) {
         for(i = 0; i < this.pointLights.length; ++i) {
            if (points != null && i < points.size) {
               if (this.pointLights[i].equals((PointLight)points.get(i))) {
                  continue;
               }

               this.pointLights[i].set((PointLight)points.get(i));
            } else {
               if (this.pointLights[i].intensity == 0.0F) {
                  continue;
               }

               this.pointLights[i].intensity = 0.0F;
            }

            idx = this.pointLightsLoc + i * this.pointLightsSize;
            this.program.setUniformf(idx + this.pointLightsColorOffset, this.pointLights[i].color.r, this.pointLights[i].color.g, this.pointLights[i].color.b);
            this.program.setUniformf(idx + this.pointLightsPositionOffset, this.pointLights[i].position);
            if (this.pointLightsIntensityOffset >= 0) {
               this.program.setUniformf(idx + this.pointLightsIntensityOffset, this.pointLights[i].intensity);
            }
         }
      }

      if (lights.fog != null) {
         this.program.setUniformf(this.u_fogColor.location, lights.fog);
      }

   }

   public void dispose() {
      this.program.dispose();
   }
}
