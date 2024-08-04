package com.minild44.www;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class EndScreen implements Screen {
   private Stage stage;
   private SpriteBatch batch;
   private boolean won;
   private int kills;
   private float time;

   public EndScreen(boolean won, int kills, float time) {
      this.won = won;
      this.kills = kills;
      this.time = time;
   }

   public void render(float delta) {
      Gdx.gl.glClear(16384);
      Gdx.gl.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
      Gdx.input.setInputProcessor(this.stage);
      this.stage.act(delta);
      this.stage.draw();
      Resources.FONT.setScale(4.0F);
      this.batch.begin();
      String text = this.won ? "You won!" : "You lost!";
      BitmapFont.TextBounds tb = Resources.FONT.getBounds(text);
      Resources.FONT.draw(this.batch, text, (float)(Gdx.graphics.getWidth() / 2) - tb.width / 2.0F, (float)Gdx.graphics.getHeight() * 0.8F);
      Resources.FONT.setScale(2.0F);
      String text2 = "Kills: " + this.kills;
      tb = Resources.FONT.getBounds(text2);
      Resources.FONT.draw(this.batch, text2, (float)(Gdx.graphics.getWidth() / 2) - tb.width / 2.0F, (float)Gdx.graphics.getHeight() * 0.6F);
      int mili = (int)(this.time * 1000.0F);
      Date date = new Date((long)mili);
      DateFormat format = new SimpleDateFormat("mm:ss:SS");
      String dateFormatted = format.format(date);
      String text3 = "Time: " + dateFormatted;
      tb = Resources.FONT.getBounds(text3);
      Resources.FONT.draw(this.batch, text3, (float)(Gdx.graphics.getWidth() / 2) - tb.width / 2.0F, (float)Gdx.graphics.getHeight() * 0.5F);
      this.batch.end();
      Resources.FONT.setScale(1.0F);
   }

   public void resize(int width, int height) {
      this.stage = new Stage((float)width, (float)height, true, this.batch);
      this.stage.clear();
      TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
      style.up = Resources.BUTTONS.getDrawable("button_off");
      style.over = Resources.BUTTONS.getDrawable("button_on");
      style.down = Resources.BUTTONS.getDrawable("button_active");
      style.font = Resources.FONT;
      style.font.setScale(2.0F);
      TextButton returnButton = new TextButton("Return", style);
      returnButton.setSize(256.0F, 96.0F);
      returnButton.setPosition((float)(Gdx.graphics.getWidth() / 2 - 128), (float)(Gdx.graphics.getHeight() / 2 - 256));
      returnButton.addListener(new InputListener() {
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            return true;
         }

         public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            if (button == 0) {
               Resources.GUISOUNDS[0].play(Main.VOLUME);
               EndScreen.this.returnToMenu();
            }

            super.touchUp(event, x, y, pointer, button);
         }
      });
      this.stage.addActor(returnButton);
   }

   private void returnToMenu() {
      Main.INSTANCE.setScreen(new MenuScreen());
   }

   public void show() {
      this.batch = new SpriteBatch();
      this.stage = new Stage();
   }

   public void hide() {
      this.dispose();
   }

   public void pause() {
   }

   public void resume() {
   }

   public void dispose() {
      this.stage.dispose();
      this.batch.dispose();
   }
}
