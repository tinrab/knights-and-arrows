package com.minild44.www;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import java.awt.Component;
import javax.swing.JOptionPane;
import org.lwjgl.opengl.GLContext;

public class Main extends Game {
   public static Input INPUT = new Input();
   public static Main INSTANCE = new Main();
   public static float VOLUME = 1.0F;

   public void create() {
      if (!GLContext.getCapabilities().OpenGL20) {
         JOptionPane.showMessageDialog((Component)null, "OpenGL20 not available", "ERROR", 0);
         System.exit(0);
      }

      Resources.FONT = new BitmapFont(Gdx.files.internal("fonts/basic.fnt"), false);
      ShaderProgram.pedantic = false;
      this.setScreen(new SplashScreen());
   }

   public void dispose() {
      super.dispose();
      Resources.dispose();
   }

   public static void main(String[] args) {
      LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
      cfg.title = "Knights & Arrows";
      cfg.useGL20 = true;
      cfg.width = 1280;
      cfg.height = 720;
      cfg.vSyncEnabled = true;
      cfg.backgroundFPS = 60;
      cfg.foregroundFPS = 60;
      new LwjglApplication(INSTANCE, cfg);
   }
}
