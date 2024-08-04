package com.minild44.www.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class TwoPassShader {
   private Mesh screen;
   private FrameBuffer firstFBO;
   private FrameBuffer secondFBO;
   private TextureRegion first;
   private TextureRegion second;
   private ShaderProgram shader;

   public TwoPassShader(FileHandle vs, FileHandle fs) {
      this.create(new ShaderProgram(vs, fs));
   }

   public TwoPassShader(ShaderProgram shader) {
      this.create(shader);
   }

   private void create(ShaderProgram shader) {
      this.screen = Mesh.createFullScreenQuad();
      this.firstFBO = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
      this.first = new TextureRegion(this.firstFBO.getColorBufferTexture());
      this.first.flip(false, true);
      this.first.getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
      this.secondFBO = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
      this.second = new TextureRegion(this.secondFBO.getColorBufferTexture());
      this.second.flip(false, true);
      this.second.getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
      this.shader = shader;
      this.screen.setAutoBind(false);
   }

   public void beginFirst() {
      this.firstFBO.begin();
      Gdx.gl.glClear(16384);
      Gdx.gl.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
   }

   public void endFirst() {
      this.firstFBO.end();
   }

   public void beginSecond() {
      this.secondFBO.begin();
      Gdx.gl.glClear(16384);
      Gdx.gl.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
   }

   public void endSecond() {
      this.secondFBO.end();
   }

   public void setFirst(TextureRegion region) {
      this.first = region;
   }

   public void setSecond(TextureRegion region) {
      this.second = region;
   }

   public TextureRegion getFirst() {
      return this.first;
   }

   public TextureRegion getSecond() {
      return this.second;
   }

   public void renderToScreen(SpriteBatch batch, float mergeFactor) {
      batch.enableBlending();
      batch.setBlendFunction(770, 771);
      batch.begin();
      batch.setShader(this.shader);
      this.second.getTexture().bind(1);
      this.shader.setUniformi("u_second", 1);
      this.shader.setUniformf("factor", mergeFactor);
      Gdx.gl.glActiveTexture(33984);
      batch.draw(this.first, 0.0F, 0.0F, (float)Gdx.graphics.getWidth(), (float)Gdx.graphics.getHeight());
      batch.setShader((ShaderProgram)null);
      batch.end();
   }

   public void dispose() {
      this.firstFBO.dispose();
      this.secondFBO.dispose();
      this.shader.dispose();
      this.screen.dispose();
   }
}
