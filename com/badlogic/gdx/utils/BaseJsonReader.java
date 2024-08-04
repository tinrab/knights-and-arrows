package com.badlogic.gdx.utils;

import com.badlogic.gdx.files.FileHandle;
import java.io.InputStream;

public interface BaseJsonReader {
   JsonValue parse(InputStream var1);

   JsonValue parse(FileHandle var1);
}
