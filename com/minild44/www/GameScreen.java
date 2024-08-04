package com.minild44.www;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.minild44.www.core.Level;
import com.minild44.www.core.gui.GuiHandler;
import com.minild44.www.util.Camera;
import org.lwjgl.opengl.Display;

public class GameScreen implements Screen {
   private Level level;
   private SpriteBatch batch;
   private Matrix4 screenView;
   private String path;
   private GuiHandler gui;
   private boolean paused;

   public GameScreen(String path) {
      this.path = path;
   }

   public void render(float delta) {
      if (Input.isKeyPressed(131)) {
         Main.INSTANCE.setScreen(new MenuScreen());
         Resources.SOUNDS[10].stop();
      }

      if (!this.paused) {
         this.paused = Input.isKeyPressed(44) || !Display.isActive();
      } else {
         this.paused = !Input.isKeyPressed(44);
      }

      if (Input.isKeyPressed(81)) {
         Main.VOLUME += 0.1F;
         Main.VOLUME = MathUtils.clamp(Main.VOLUME, 0.0F, 1.0F);
         Resources.GUISOUNDS[1].play(Main.VOLUME);
      } else if (Input.isKeyPressed(69)) {
         Main.VOLUME -= 0.1F;
         Main.VOLUME = MathUtils.clamp(Main.VOLUME, 0.0F, 1.0F);
         Resources.GUISOUNDS[1].play(Main.VOLUME);
      }

      Gdx.gl.glClear(16384);
      Gdx.gl.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
      Input.update();
      Camera camera = this.level.getCamera();
      if (!this.paused) {
         if (Input.isButtonDown(1)) {
            camera.translate((float)Input.getMouseDX(), (float)Input.getMouseDY());
         }

         camera.clampTo(0.0F, 0.0F, (float)(this.level.getWidthInTiles() << 5), (float)(this.level.getHeightInTiles() << 5));
         camera.transform(delta);
         camera.update();
      }

      this.level.updateAndRender(delta, this.screenView, this.batch, this.paused);
      this.batch.setProjectionMatrix(this.screenView);
      this.gui.updateAndRender(delta, this.batch, camera, this.paused);
      if (this.paused) {
         this.batch.begin();
         this.batch.draw(Resources.GUI, 0.0F, 0.0F, (float)Gdx.graphics.getWidth(), (float)Gdx.graphics.getHeight(), 127, 127, 1, 1, false, false);
         Resources.FONT.setScale(4.0F);
         String text = "Paused";
         BitmapFont.TextBounds tb = Resources.FONT.getBounds(text);
         Resources.FONT.draw(this.batch, text, (float)(Gdx.graphics.getWidth() / 2) - tb.width / 2.0F, (float)(Gdx.graphics.getHeight() / 2) + tb.height);
         Resources.FONT.setScale(1.0F);
         this.batch.end();
      }

   }

   public void resize(int width, int height) {
      this.gui.resize(width, height);
      this.level.resize(width, height);
      this.screenView.setToOrtho2D(0.0F, 0.0F, (float)Gdx.graphics.getWidth(), (float)Gdx.graphics.getHeight());
   }

   public void show() {
      Resources.FONT.setScale(1.0F);
      this.level = new Level(this.path);
      this.batch = new SpriteBatch();
      this.screenView = new Matrix4();
      this.screenView.setToOrtho2D(0.0F, 0.0F, (float)Gdx.graphics.getWidth(), (float)Gdx.graphics.getHeight());
      this.gui = new GuiHandler(this.level);
   }

   public void hide() {
      this.dispose();
   }

   public void pause() {
      this.paused = true;
   }

   public void resume() {
      this.paused = false;
   }

   public void dispose() {
      this.level.dispose();
      this.batch.dispose();
      this.gui.dispose();
   }
}
