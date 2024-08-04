package com.badlogic.gdx.audio;

import com.badlogic.gdx.utils.Disposable;

public interface Sound extends Disposable {
   long play();

   long play(float var1);

   long play(float var1, float var2, float var3);

   long loop();

   long loop(float var1);

   long loop(float var1, float var2, float var3);

   void stop();

   void dispose();

   void stop(long var1);

   void setLooping(long var1, boolean var3);

   void setPitch(long var1, float var3);

   void setVolume(long var1, float var3);

   void setPan(long var1, float var3, float var4);

   void setPriority(long var1, int var3);
}
