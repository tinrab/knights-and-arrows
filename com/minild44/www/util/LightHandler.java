package com.minild44.www.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;
import java.util.List;

public class LightHandler {
   private ShaderProgram shadowMapShader;
   private ShaderProgram shadowRenderShader;
   private TextureRegion shadowMap1D;
   private TextureRegion occluders;
   private TextureRegion post;
   private FrameBuffer shadowMapFBO;
   private FrameBuffer occludersFBO;
   private FrameBuffer postFBO;
   private OrthographicCamera cam;
   public boolean additive = true;
   public boolean softShadows = true;
   private int lightSize;
   private List<Light> lights = new ArrayList();
   private ShadowCasterPass casterPass;

   public LightHandler(int lightSize) {
      this.lightSize = lightSize;
      this.shadowMapShader = new ShaderProgram(Gdx.files.internal("shaders/pass.vs"), Gdx.files.internal("shaders/shadowMap.fs"));
      this.shadowRenderShader = new ShaderProgram(Gdx.files.internal("shaders/pass.vs"), Gdx.files.internal("shaders/shadowRender.fs"));
      this.occludersFBO = new FrameBuffer(Pixmap.Format.RGBA8888, lightSize, lightSize, false);
      this.occluders = new TextureRegion(this.occludersFBO.getColorBufferTexture());
      this.occluders.flip(false, true);
      this.postFBO = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
      this.post = new TextureRegion(this.postFBO.getColorBufferTexture());
      this.post.flip(false, true);
      this.shadowMapFBO = new FrameBuffer(Pixmap.Format.RGBA8888, lightSize, 1, false);
      Texture shadowMapTex = this.shadowMapFBO.getColorBufferTexture();
      shadowMapTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
      shadowMapTex.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
      this.shadowMap1D = new TextureRegion(shadowMapTex);
      this.shadowMap1D.flip(false, true);
      this.cam = new OrthographicCamera((float)Gdx.graphics.getWidth(), (float)Gdx.graphics.getHeight());
      this.cam.setToOrtho(false);
   }

   public void resize(int width, int height) {
      this.cam = new OrthographicCamera((float)width, (float)height);
      this.cam.setToOrtho(false);
   }

   public void add(Light light) {
      this.lights.add(light);
   }

   public void setCasterPass(ShadowCasterPass casterPass) {
      this.casterPass = casterPass;
   }

   public void update(float delta, SpriteBatch batch, OrthographicCamera camera, float levelBrightness) {
      this.postFBO.begin();
      Gdx.gl.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
      Gdx.gl.glClear(16384);
      this.postFBO.end();
      if (this.additive) {
         batch.setBlendFunction(770, 1);
      }

      for(int i = this.lights.size() - 1; i >= 0; --i) {
         Light light = (Light)this.lights.get(i);
         if (light.wasRemoved()) {
            this.lights.remove(i);
         } else {
            light.update(delta);
            if (light.isActive() && camera.frustum.sphereInFrustum(new Vector3(light.getX(), light.getY(), 0.0F), light.fraction * (float)this.lightSize)) {
               this.renderLight(batch, light, camera.combined, levelBrightness);
            }
         }
      }

      if (this.additive) {
         batch.setBlendFunction(770, 771);
      }

   }

   private void renderLight(SpriteBatch batch, Light light, Matrix4 projection, float levelBrightness) {
      float lx = light.getX();
      float ly = light.getY();
      this.occludersFBO.begin();
      Gdx.gl.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
      Gdx.gl.glClear(16384);
      this.cam.setToOrtho(false, (float)this.occludersFBO.getWidth(), (float)this.occludersFBO.getHeight());
      this.cam.translate(lx - (float)this.lightSize / 2.0F, ly - (float)this.lightSize / 2.0F);
      this.cam.update();
      batch.setProjectionMatrix(this.cam.combined);
      batch.setShader((ShaderProgram)null);
      this.casterPass.render(batch, light);
      this.occludersFBO.end();
      this.shadowMapFBO.begin();
      Gdx.gl.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
      Gdx.gl.glClear(16384);
      batch.setShader(this.shadowMapShader);
      batch.begin();
      this.shadowMapShader.setUniformf("resolution", (float)this.lightSize, (float)this.lightSize);
      this.shadowMapShader.setUniformf("fraction", light.fraction);
      this.cam.setToOrtho(false, (float)this.shadowMapFBO.getWidth(), (float)this.shadowMapFBO.getHeight());
      batch.setProjectionMatrix(this.cam.combined);
      batch.draw(this.occluders.getTexture(), 0.0F, 0.0F, (float)this.lightSize, (float)this.shadowMapFBO.getHeight());
      batch.end();
      this.shadowMapFBO.end();
      this.cam.setToOrtho(false);
      this.postFBO.begin();
      batch.setProjectionMatrix(this.cam.combined);
      batch.setShader(this.shadowRenderShader);
      batch.begin();
      this.shadowRenderShader.setUniformf("resolution", (float)this.lightSize, (float)this.lightSize);
      this.shadowRenderShader.setUniformf("softShadows", this.softShadows ? 1.0F : 0.0F);
      this.shadowRenderShader.setUniformf("fraction", light.fraction);
      Color color = new Color(1.0F, 1.0F, 1.0F, 1.0F - levelBrightness);
      color.mul(light.color);
      batch.setColor(color);
      batch.setProjectionMatrix(projection);
      batch.draw(this.shadowMap1D.getTexture(), lx - (float)this.lightSize / 2.0F, ly - (float)this.lightSize / 2.0F, (float)this.lightSize, (float)this.lightSize);
      batch.end();
      this.postFBO.end();
      batch.setColor(Color.WHITE);
   }

   public TextureRegion getLightTexture() {
      return this.post;
   }

   public void dispose() {
      this.shadowMapShader.dispose();
      this.shadowRenderShader.dispose();
      this.occludersFBO.dispose();
      this.shadowMapFBO.dispose();
      this.lights.clear();
   }

   public List<Light> getLights() {
      return this.lights;
   }

   public float getLightSize() {
      return (float)this.lightSize;
   }
}
