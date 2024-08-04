package com.badlogic.gdx.audio;

import com.badlogic.gdx.utils.Disposable;

public interface AudioDevice extends Disposable {
   boolean isMono();

   void writeSamples(short[] var1, int var2, int var3);

   void writeSamples(float[] var1, int var2, int var3);

   int getLatency();

   void dispose();

   void setVolume(float var1);
}
