package com.badlogic.gdx.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamUtils {
   public static void copyStream(InputStream input, OutputStream output) throws IOException {
      copyStream(input, output, 8192);
   }

   public static void copyStream(InputStream input, OutputStream output, int bufferSize) throws IOException {
      byte[] buffer = new byte[bufferSize];

      int bytesRead;
      while((bytesRead = input.read(buffer)) != -1) {
         output.write(buffer, 0, bytesRead);
      }

   }
}
