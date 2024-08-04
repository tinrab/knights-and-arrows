package com.badlogic.gdx;

import com.badlogic.gdx.files.FileHandle;

public interface Files {
   FileHandle getFileHandle(String var1, Files.FileType var2);

   FileHandle classpath(String var1);

   FileHandle internal(String var1);

   FileHandle external(String var1);

   FileHandle absolute(String var1);

   FileHandle local(String var1);

   String getExternalStoragePath();

   boolean isExternalStorageAvailable();

   String getLocalStoragePath();

   boolean isLocalStorageAvailable();

   public static enum FileType {
      Classpath,
      Internal,
      External,
      Absolute,
      Local;
   }
}
