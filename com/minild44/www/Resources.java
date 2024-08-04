package com.minild44.www;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.minild44.www.core.Level;
import java.util.ArrayList;
import java.util.List;

public final class Resources {
   public static TextureRegion[][] SPRITES;
   public static Texture ACTIONS;
   public static Texture GUI;
   public static Texture SPLASH;
   public static BitmapFont FONT;
   public static Skin BUTTONS;
   public static Sound[] SOUNDS = new Sound[11];
   public static Sound[] GUISOUNDS = new Sound[3];
   public static List<String> NAMES = new ArrayList();

   public static void load() {
      SPRITES = TextureRegion.split(new Texture(internal("images/sprites.png")), 8, 8);
      ACTIONS = new Texture(internal("images/actions.png"));
      GUI = new Texture(internal("images/gui.png"));
      SPLASH = new Texture(internal("images/splash.png"));
      String[] names = internal("names").readString().split("\n");
      String[] var4 = names;
      int var3 = names.length;

      for(int var2 = 0; var2 < var3; ++var2) {
         String name = var4[var2];
         if (name.length() > 0) {
            NAMES.add(name);
         }
      }

      SOUNDS[0] = Gdx.audio.newSound(internal("sounds/bow.wav"));
      SOUNDS[1] = Gdx.audio.newSound(internal("sounds/sword.wav"));
      SOUNDS[2] = Gdx.audio.newSound(internal("sounds/magic.wav"));
      SOUNDS[3] = Gdx.audio.newSound(internal("sounds/wind.wav"));
      SOUNDS[4] = Gdx.audio.newSound(internal("sounds/hit.wav"));
      SOUNDS[5] = Gdx.audio.newSound(internal("sounds/heal.wav"));
      SOUNDS[6] = Gdx.audio.newSound(internal("sounds/boom.wav"));
      SOUNDS[7] = Gdx.audio.newSound(internal("sounds/explosion.mp3"));
      SOUNDS[8] = Gdx.audio.newSound(internal("sounds/hit2.wav"));
      SOUNDS[9] = Gdx.audio.newSound(internal("sounds/hit3.wav"));
      SOUNDS[10] = Gdx.audio.newSound(internal("sounds/fire.wav"));
      GUISOUNDS[0] = Gdx.audio.newSound(internal("sounds/select.wav"));
      GUISOUNDS[1] = Gdx.audio.newSound(internal("sounds/true.wav"));
      GUISOUNDS[2] = Gdx.audio.newSound(internal("sounds/false.wav"));
      BUTTONS = new Skin();
      BUTTONS.addRegions(new TextureAtlas(internal("images/buttons.pack")));
   }

   public static TextureRegion getSprite(int x, int y) {
      return SPRITES[y >> 3][x >> 3];
   }

   public static TextureRegion get(Texture texture, int x, int y, int w, int h) {
      TextureRegion region = new TextureRegion(texture);
      region.setRegion(x, y, w, h);
      return region;
   }

   public static void dispose() {
      ACTIONS.dispose();
      SPLASH.dispose();
      GUI.dispose();
      BUTTONS.dispose();
      Sound[] var3;
      int var2 = (var3 = SOUNDS).length;

      Sound s;
      int var1;
      for(var1 = 0; var1 < var2; ++var1) {
         s = var3[var1];
         if (s != null) {
            s.dispose();
         }
      }

      var2 = (var3 = GUISOUNDS).length;

      for(var1 = 0; var1 < var2; ++var1) {
         s = var3[var1];
         if (s != null) {
            s.dispose();
         }
      }

   }

   public static FileHandle internal(String path) {
      return Gdx.files.internal(path);
   }

   public static void play(int i, float x, float y, Level level) {
      float dst = (new Vector2(level.getCamera().position.x, level.getCamera().position.y)).dst(x, y);
      float d = (float)Math.sqrt((double)(Gdx.graphics.getWidth() * Gdx.graphics.getWidth() + Gdx.graphics.getHeight() * Gdx.graphics.getHeight())) / 2.0F;
      dst = (d - dst) / d;
      if (dst < 0.0F) {
         dst = 0.0F;
      }

      long id = SOUNDS[i].play(0.0F);
      float dx = (x - level.getCamera().position.x) / (float)Gdx.graphics.getWidth() / 2.0F;
      dst = MathUtils.clamp(dst, 0.0F, Main.VOLUME);
      SOUNDS[i].setPan(id, dx, dst);
   }
}
