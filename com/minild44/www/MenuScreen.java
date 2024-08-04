package com.minild44.www;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

public class MenuScreen implements Screen {
   private Stage stage1;
   private Stage stage2;
   private Skin skin;
   private TextureAtlas atlas;
   private SpriteBatch batch;
   private TextureRegionDrawable mapSelectTexture;
   private byte state = 0;
   private Array<MenuScreen.MapData> maps = new Array();

   public void render(float delta) {
      Gdx.gl.glClear(16384);
      Gdx.gl.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
      switch(this.state) {
      case 0:
         Gdx.input.setInputProcessor(this.stage1);
         this.stage1.act(delta);
         this.stage1.draw();
         this.batch.begin();
         int w = 1024;
         int h = 384;
         this.batch.draw(Resources.SPLASH, (float)(Gdx.graphics.getWidth() / 2 - w / 2), (float)(Gdx.graphics.getHeight() / 2 - h / 6), (float)w, (float)h);
         Resources.FONT.setScale(1.0F);
         BitmapFont.TextBounds tb = Resources.FONT.getBounds("Scrambled by @PaidGEEK");
         Resources.FONT.draw(this.batch, "Scrambled by @PaidGEEK", (float)(Gdx.graphics.getWidth() / 2) - tb.width / 2.0F, tb.height + 24.0F);
         Resources.FONT.draw(this.batch, "Volume +/-", 16.0F, tb.height + 16.0F);
         Resources.FONT.setScale(2.0F);
         this.batch.end();
         break;
      case 1:
         Gdx.input.setInputProcessor(this.stage2);
         this.stage2.act(delta);
         this.stage2.draw();
         this.batch.begin();
         BitmapFont.TextBounds tb2 = Resources.FONT.getBounds("Select a level to play.");
         Resources.FONT.draw(this.batch, "Select a level to play.", (float)(Gdx.graphics.getWidth() / 2) - tb2.width / 2.0F, (float)Gdx.graphics.getHeight() * 0.3F + tb2.height);
         this.batch.end();
      }

   }

   public void resize(int width, int height) {
      this.stage1 = new Stage((float)width, (float)height, true, this.batch);
      this.stage2 = new Stage((float)width, (float)height, true, this.batch);
      TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
      style.up = this.skin.getDrawable("button_off");
      style.over = this.skin.getDrawable("button_on");
      style.down = this.skin.getDrawable("button_active");
      style.font = Resources.FONT;
      style.font.setScale(2.0F);
      TextButton startButton = new TextButton("Start", style);
      startButton.setSize(256.0F, 96.0F);
      startButton.setPosition((float)(Gdx.graphics.getWidth() / 2 - 128), (float)(Gdx.graphics.getHeight() / 2 - 160));
      startButton.addListener(new InputListener() {
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            return true;
         }

         public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            if (button == 0) {
               Resources.GUISOUNDS[0].play(Main.VOLUME);
               MenuScreen.this.state = 1;
            }

            super.touchUp(event, x, y, pointer, button);
         }
      });
      this.stage1.addActor(startButton);
      TextButton exitButton = new TextButton("Exit", style);
      exitButton.setSize(256.0F, 96.0F);
      exitButton.setPosition((float)(Gdx.graphics.getWidth() / 2 - 128), (float)(Gdx.graphics.getHeight() / 2 - 272));
      exitButton.addListener(new InputListener() {
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            return true;
         }

         public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            if (button == 0) {
               Gdx.app.exit();
            }

            super.touchUp(event, x, y, pointer, button);
         }
      });
      this.stage1.addActor(exitButton);
      TextButton backButton = new TextButton("Back", style);
      backButton.setSize(256.0F, 96.0F);
      backButton.setPosition((float)(Gdx.graphics.getWidth() / 2 - 128), (float)(Gdx.graphics.getHeight() / 2 - 272));
      backButton.addListener(new InputListener() {
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            return true;
         }

         public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            if (button == 0) {
               Resources.GUISOUNDS[0].play(Main.VOLUME);
               MenuScreen.this.state = 0;
            }

            super.touchUp(event, x, y, pointer, button);
         }
      });
      this.stage2.addActor(backButton);
      float x = (float)(Gdx.graphics.getWidth() / 2 - 480);
      float y = (float)Gdx.graphics.getHeight() * 0.45F;

      for(final int i = 0; i < this.maps.size; ++i) {
         ImageButton.ImageButtonStyle imgStyle = new ImageButton.ImageButtonStyle();
         imgStyle.up = new TextureRegionDrawable(new TextureRegion(((MenuScreen.MapData)this.maps.get(i)).tex));
         imgStyle.over = this.mapSelectTexture;
         ImageButton mapSelect = new ImageButton(imgStyle);
         mapSelect.setSize(256.0F, 256.0F);
         mapSelect.setPosition(x, y);
         x += 320.0F;
         mapSelect.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
               return true;
            }

            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
               if (button == 0) {
                  Resources.GUISOUNDS[0].play(Main.VOLUME);
                  Main.INSTANCE.setScreen(new GameScreen(((MenuScreen.MapData)MenuScreen.this.maps.get(i)).path));
               }

               super.touchUp(event, x, y, pointer, button);
            }
         });
         this.stage2.addActor(mapSelect);
      }

   }

   public void show() {
      this.batch = new SpriteBatch();
      this.stage1 = new Stage();
      this.stage2 = new Stage();
      this.atlas = new TextureAtlas(Resources.internal("images/buttons.pack"));
      this.skin = new Skin();
      this.skin.addRegions(this.atlas);
      String[] names = new String[]{"backyard", "desert", "snow"};
      String[] var5 = names;
      int var4 = names.length;

      for(int var3 = 0; var3 < var4; ++var3) {
         String map = var5[var3];
         this.maps.add(new MenuScreen.MapData("maps/" + map + ".tmx", "maps/" + map + ".png"));
      }

      this.mapSelectTexture = new TextureRegionDrawable(new TextureRegion(new Texture(Resources.internal("maps/mapselect.png"))));
   }

   public void hide() {
      this.dispose();
   }

   public void pause() {
   }

   public void resume() {
   }

   public void dispose() {
      this.stage1.dispose();
      this.stage2.dispose();
      this.skin.dispose();
      this.batch.dispose();
      this.atlas.dispose();
   }

   private class MapData {
      public String path;
      public Texture tex;

      public MapData(String path, String thumbPath) {
         this.path = path;
         this.tex = new Texture(Resources.internal(thumbPath));
      }
   }
}
