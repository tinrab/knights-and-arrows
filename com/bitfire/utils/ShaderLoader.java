package com.bitfire.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public final class ShaderLoader {
   public static String BasePath = "";
   public static boolean Pedantic = true;

   public static ShaderProgram fromFile(String vertexFileName, String fragmentFileName) {
      return fromFile(vertexFileName, fragmentFileName, "");
   }

   public static ShaderProgram fromFile(String vertexFileName, String fragmentFileName, String defines) {
      String log = "\"" + vertexFileName + "/" + fragmentFileName + "\"";
      if (defines.length() > 0) {
         log = log + " w/ (" + defines.replace("\n", ", ") + ")";
      }

      log = log + "...";
      Gdx.app.log("ShaderLoader", "Compiling " + log);
      String vpSrc = Gdx.files.internal(BasePath + vertexFileName + ".vertex").readString();
      String fpSrc = Gdx.files.internal(BasePath + fragmentFileName + ".fragment").readString();
      ShaderProgram program = fromString(vpSrc, fpSrc, vertexFileName, fragmentFileName, defines);
      return program;
   }

   public static ShaderProgram fromString(String vertex, String fragment, String vertexName, String fragmentName) {
      return fromString(vertex, fragment, vertexName, fragmentName, "");
   }

   public static ShaderProgram fromString(String vertex, String fragment, String vertexName, String fragmentName, String defines) {
      ShaderProgram.pedantic = Pedantic;
      ShaderProgram shader = new ShaderProgram(defines + "\n" + vertex, defines + "\n" + fragment);
      if (!shader.isCompiled()) {
         Gdx.app.error("ShaderLoader", shader.getLog());
         System.exit(-1);
      }

      return shader;
   }

   private ShaderLoader() {
   }
}
