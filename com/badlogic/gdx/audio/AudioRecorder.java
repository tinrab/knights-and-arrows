package com.badlogic.gdx.audio;

import com.badlogic.gdx.utils.Disposable;

public interface AudioRecorder extends Disposable {
   void read(short[] var1, int var2, int var3);

   void dispose();
}
