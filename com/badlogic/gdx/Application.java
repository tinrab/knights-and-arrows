package com.badlogic.gdx;

import com.badlogic.gdx.utils.Clipboard;

public interface Application {
   int LOG_NONE = 0;
   int LOG_DEBUG = 3;
   int LOG_INFO = 2;
   int LOG_ERROR = 1;

   ApplicationListener getApplicationListener();

   Graphics getGraphics();

   Audio getAudio();

   Input getInput();

   Files getFiles();

   Net getNet();

   void log(String var1, String var2);

   void log(String var1, String var2, Exception var3);

   void error(String var1, String var2);

   void error(String var1, String var2, Throwable var3);

   void debug(String var1, String var2);

   void debug(String var1, String var2, Throwable var3);

   void setLogLevel(int var1);

   Application.ApplicationType getType();

   int getVersion();

   long getJavaHeap();

   long getNativeHeap();

   Preferences getPreferences(String var1);

   Clipboard getClipboard();

   void postRunnable(Runnable var1);

   void exit();

   void addLifecycleListener(LifecycleListener var1);

   void removeLifecycleListener(LifecycleListener var1);

   public static enum ApplicationType {
      Android,
      Desktop,
      Applet,
      WebGL,
      iOS;
   }
}
