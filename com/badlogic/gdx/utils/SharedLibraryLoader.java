package com.badlogic.gdx.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class SharedLibraryLoader {
   public static boolean isWindows = System.getProperty("os.name").contains("Windows");
   public static boolean isLinux = System.getProperty("os.name").contains("Linux");
   public static boolean isMac = System.getProperty("os.name").contains("Mac");
   public static boolean isIos = false;
   public static boolean isAndroid = false;
   public static boolean is64Bit = System.getProperty("os.arch").equals("amd64");
   private static HashSet<String> loadedLibraries;
   private String nativesJar;

   static {
      String vm = System.getProperty("java.vm.name");
      if (vm != null && vm.contains("Dalvik")) {
         isAndroid = true;
         isWindows = false;
         isLinux = false;
         isMac = false;
         is64Bit = false;
      }

      if (!isAndroid && !isWindows && !isLinux && !isMac) {
         isIos = true;
         is64Bit = false;
      }

      loadedLibraries = new HashSet();
   }

   public SharedLibraryLoader() {
   }

   public SharedLibraryLoader(String nativesJar) {
      this.nativesJar = nativesJar;
   }

   public String crc(InputStream input) {
      if (input == null) {
         throw new IllegalArgumentException("input cannot be null.");
      } else {
         CRC32 crc = new CRC32();
         byte[] buffer = new byte[4096];

         try {
            while(true) {
               int length = input.read(buffer);
               if (length == -1) {
                  break;
               }

               crc.update(buffer, 0, length);
            }
         } catch (Exception var7) {
            try {
               input.close();
            } catch (Exception var6) {
            }
         }

         return Long.toString(crc.getValue());
      }
   }

   public String mapLibraryName(String libraryName) {
      if (isWindows) {
         return libraryName + (is64Bit ? "64.dll" : ".dll");
      } else if (isLinux) {
         return "lib" + libraryName + (is64Bit ? "64.so" : ".so");
      } else {
         return isMac ? "lib" + libraryName + ".dylib" : libraryName;
      }
   }

   public synchronized void load(String libraryName) {
      if (!isIos) {
         libraryName = this.mapLibraryName(libraryName);
         if (!loadedLibraries.contains(libraryName)) {
            try {
               if (isAndroid) {
                  System.loadLibrary(libraryName);
               } else {
                  System.load(this.extractFile(libraryName, (String)null).getAbsolutePath());
               }
            } catch (Throwable var3) {
               throw new GdxRuntimeException("Couldn't load shared library '" + libraryName + "' for target: " + System.getProperty("os.name") + (is64Bit ? ", 64-bit" : ", 32-bit"), var3);
            }

            loadedLibraries.add(libraryName);
         }
      }
   }

   private InputStream readFile(String path) {
      if (this.nativesJar == null) {
         InputStream input = SharedLibraryLoader.class.getResourceAsStream("/" + path);
         if (input == null) {
            throw new GdxRuntimeException("Unable to read file for extraction: " + path);
         } else {
            return input;
         }
      } else {
         try {
            ZipFile file = new ZipFile(this.nativesJar);
            ZipEntry entry = file.getEntry(path);
            if (entry == null) {
               throw new GdxRuntimeException("Couldn't find '" + path + "' in JAR: " + this.nativesJar);
            } else {
               return file.getInputStream(entry);
            }
         } catch (IOException var4) {
            throw new GdxRuntimeException("Error reading '" + path + "' in JAR: " + this.nativesJar, var4);
         }
      }
   }

   public File extractFile(String sourcePath, String dirName) throws IOException {
      File extractedDir;
      try {
         String sourceCrc = this.crc(this.readFile(sourcePath));
         if (dirName == null) {
            dirName = sourceCrc;
         }

         extractedDir = new File(System.getProperty("java.io.tmpdir") + "/libgdx" + System.getProperty("user.name") + "/" + dirName);
         File extractedFile = new File(extractedDir, (new File(sourcePath)).getName());
         String extractedCrc = null;
         if (extractedFile.exists()) {
            try {
               extractedCrc = this.crc(new FileInputStream(extractedFile));
            } catch (FileNotFoundException var11) {
            }
         }

         if (extractedCrc == null || !extractedCrc.equals(sourceCrc)) {
            try {
               InputStream input = this.readFile(sourcePath);
               extractedDir.mkdirs();
               FileOutputStream output = new FileOutputStream(extractedFile);
               byte[] buffer = new byte[4096];

               while(true) {
                  int length = input.read(buffer);
                  if (length == -1) {
                     input.close();
                     output.close();
                     break;
                  }

                  output.write(buffer, 0, length);
               }
            } catch (IOException var12) {
               throw new GdxRuntimeException("Error extracting file: " + sourcePath, var12);
            }
         }

         return extractedFile;
      } catch (RuntimeException var13) {
         extractedDir = new File(System.getProperty("java.library.path"), sourcePath);
         if (extractedDir.exists()) {
            return extractedDir;
         } else {
            throw var13;
         }
      }
   }
}
