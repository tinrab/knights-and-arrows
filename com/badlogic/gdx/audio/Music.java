package com.badlogic.gdx.audio;

import com.badlogic.gdx.utils.Disposable;

public interface Music extends Disposable {
   void play();

   void pause();

   void stop();

   boolean isPlaying();

   void setLooping(boolean var1);

   boolean isLooping();

   void setVolume(float var1);

   float getVolume();

   void setPan(float var1, float var2);

   float getPosition();

   void dispose();

   void setOnCompletionListener(Music.OnCompletionListener var1);

   public interface OnCompletionListener {
      void onCompletion(Music var1);
   }
}
