package com.badlogic.gdx;

import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

public interface Audio {
   AudioDevice newAudioDevice(int var1, boolean var2);

   AudioRecorder newAudioRecorder(int var1, boolean var2);

   Sound newSound(FileHandle var1);

   Music newMusic(FileHandle var1);
}
