package com.badlogic.gdx.files;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public class FileHandle {
   protected File file;
   protected Files.FileType type;
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$com$badlogic$gdx$Files$FileType;

   protected FileHandle() {
   }

   public FileHandle(String fileName) {
      this.file = new File(fileName);
      this.type = Files.FileType.Absolute;
   }

   public FileHandle(File file) {
      this.file = file;
      this.type = Files.FileType.Absolute;
   }

   protected FileHandle(String fileName, Files.FileType type) {
      this.type = type;
      this.file = new File(fileName);
   }

   protected FileHandle(File file, Files.FileType type) {
      this.file = file;
      this.type = type;
   }

   public String path() {
      return this.file.getPath().replace('\\', '/');
   }

   public String name() {
      return this.file.getName();
   }

   public String extension() {
      String name = this.file.getName();
      int dotIndex = name.lastIndexOf(46);
      return dotIndex == -1 ? "" : name.substring(dotIndex + 1);
   }

   public String nameWithoutExtension() {
      String name = this.file.getName();
      int dotIndex = name.lastIndexOf(46);
      return dotIndex == -1 ? name : name.substring(0, dotIndex);
   }

   public String pathWithoutExtension() {
      String path = this.file.getPath().replace('\\', '/');
      int dotIndex = path.lastIndexOf(46);
      return dotIndex == -1 ? path : path.substring(0, dotIndex);
   }

   public Files.FileType type() {
      return this.type;
   }

   public File file() {
      return this.type == Files.FileType.External ? new File(Gdx.files.getExternalStoragePath(), this.file.getPath()) : this.file;
   }

   public InputStream read() {
      if (this.type != Files.FileType.Classpath && (this.type != Files.FileType.Internal || this.file.exists()) && (this.type != Files.FileType.Local || this.file.exists())) {
         try {
            return new FileInputStream(this.file());
         } catch (Exception var2) {
            if (this.file().isDirectory()) {
               throw new GdxRuntimeException("Cannot open a stream to a directory: " + this.file + " (" + this.type + ")", var2);
            } else {
               throw new GdxRuntimeException("Error reading file: " + this.file + " (" + this.type + ")", var2);
            }
         }
      } else {
         InputStream input = FileHandle.class.getResourceAsStream("/" + this.file.getPath().replace('\\', '/'));
         if (input == null) {
            throw new GdxRuntimeException("File not found: " + this.file + " (" + this.type + ")");
         } else {
            return input;
         }
      }
   }

   public BufferedInputStream read(int bufferSize) {
      return new BufferedInputStream(this.read(), bufferSize);
   }

   public Reader reader() {
      return new InputStreamReader(this.read());
   }

   public Reader reader(String charset) {
      try {
         return new InputStreamReader(this.read(), charset);
      } catch (UnsupportedEncodingException var3) {
         throw new GdxRuntimeException("Error reading file: " + this, var3);
      }
   }

   public BufferedReader reader(int bufferSize) {
      return new BufferedReader(new InputStreamReader(this.read()), bufferSize);
   }

   public BufferedReader reader(int bufferSize, String charset) {
      try {
         return new BufferedReader(new InputStreamReader(this.read(), charset), bufferSize);
      } catch (UnsupportedEncodingException var4) {
         throw new GdxRuntimeException("Error reading file: " + this, var4);
      }
   }

   public String readString() {
      return this.readString((String)null);
   }

   public String readString(String charset) {
      int fileLength = (int)this.length();
      if (fileLength == 0) {
         fileLength = 512;
      }

      StringBuilder output = new StringBuilder(fileLength);
      InputStreamReader reader = null;

      try {
         if (charset == null) {
            reader = new InputStreamReader(this.read());
         } else {
            reader = new InputStreamReader(this.read(), charset);
         }

         char[] buffer = new char[256];

         while(true) {
            int length = reader.read(buffer);
            if (length == -1) {
               return output.toString();
            }

            output.append(buffer, 0, length);
         }
      } catch (IOException var14) {
         throw new GdxRuntimeException("Error reading layout file: " + this, var14);
      } finally {
         try {
            if (reader != null) {
               reader.close();
            }
         } catch (IOException var13) {
         }

      }
   }

   public byte[] readBytes() {
      int length = (int)this.length();
      if (length == 0) {
         length = 512;
      }

      byte[] buffer = new byte[length];
      int position = 0;
      InputStream input = this.read();

      try {
         while(true) {
            int count = input.read(buffer, position, buffer.length - position);
            if (count == -1) {
               break;
            }

            position += count;
            if (position == buffer.length) {
               int b = input.read();
               if (b == -1) {
                  break;
               }

               byte[] newBuffer = new byte[buffer.length * 2];
               System.arraycopy(buffer, 0, newBuffer, 0, position);
               buffer = newBuffer;
               newBuffer[position++] = (byte)b;
            }
         }
      } catch (IOException var15) {
         throw new GdxRuntimeException("Error reading file: " + this, var15);
      } finally {
         try {
            if (input != null) {
               input.close();
            }
         } catch (IOException var14) {
         }

      }

      if (position < buffer.length) {
         byte[] newBuffer = new byte[position];
         System.arraycopy(buffer, 0, newBuffer, 0, position);
         buffer = newBuffer;
      }

      return buffer;
   }

   public int readBytes(byte[] bytes, int offset, int size) {
      InputStream input = this.read();
      int position = 0;

      try {
         while(true) {
            int count = input.read(bytes, offset + position, size - position);
            if (count <= 0) {
               return position - offset;
            }

            position += count;
         }
      } catch (IOException var14) {
         throw new GdxRuntimeException("Error reading file: " + this, var14);
      } finally {
         try {
            if (input != null) {
               input.close();
            }
         } catch (IOException var13) {
         }

      }
   }

   public OutputStream write(boolean append) {
      if (this.type == Files.FileType.Classpath) {
         throw new GdxRuntimeException("Cannot write to a classpath file: " + this.file);
      } else if (this.type == Files.FileType.Internal) {
         throw new GdxRuntimeException("Cannot write to an internal file: " + this.file);
      } else {
         this.parent().mkdirs();

         try {
            return new FileOutputStream(this.file(), append);
         } catch (Exception var3) {
            if (this.file().isDirectory()) {
               throw new GdxRuntimeException("Cannot open a stream to a directory: " + this.file + " (" + this.type + ")", var3);
            } else {
               throw new GdxRuntimeException("Error writing file: " + this.file + " (" + this.type + ")", var3);
            }
         }
      }
   }

   public void write(InputStream input, boolean append) {
      OutputStream output = null;

      try {
         output = this.write(append);
         byte[] buffer = new byte[4096];

         while(true) {
            int length = input.read(buffer);
            if (length == -1) {
               return;
            }

            output.write(buffer, 0, length);
         }
      } catch (Exception var16) {
         throw new GdxRuntimeException("Error stream writing to file: " + this.file + " (" + this.type + ")", var16);
      } finally {
         try {
            if (input != null) {
               input.close();
            }
         } catch (Exception var15) {
         }

         try {
            if (output != null) {
               output.close();
            }
         } catch (Exception var14) {
         }

      }
   }

   public Writer writer(boolean append) {
      return this.writer(append, (String)null);
   }

   public Writer writer(boolean append, String charset) {
      if (this.type == Files.FileType.Classpath) {
         throw new GdxRuntimeException("Cannot write to a classpath file: " + this.file);
      } else if (this.type == Files.FileType.Internal) {
         throw new GdxRuntimeException("Cannot write to an internal file: " + this.file);
      } else {
         this.parent().mkdirs();

         try {
            FileOutputStream output = new FileOutputStream(this.file(), append);
            return charset == null ? new OutputStreamWriter(output) : new OutputStreamWriter(output, charset);
         } catch (IOException var4) {
            if (this.file().isDirectory()) {
               throw new GdxRuntimeException("Cannot open a stream to a directory: " + this.file + " (" + this.type + ")", var4);
            } else {
               throw new GdxRuntimeException("Error writing file: " + this.file + " (" + this.type + ")", var4);
            }
         }
      }
   }

   public void writeString(String string, boolean append) {
      this.writeString(string, append, (String)null);
   }

   public void writeString(String string, boolean append, String charset) {
      Writer writer = null;

      try {
         writer = this.writer(append, charset);
         writer.write(string);
      } catch (Exception var13) {
         throw new GdxRuntimeException("Error writing file: " + this.file + " (" + this.type + ")", var13);
      } finally {
         try {
            if (writer != null) {
               writer.close();
            }
         } catch (Exception var12) {
         }

      }

   }

   public void writeBytes(byte[] bytes, boolean append) {
      OutputStream output = this.write(append);

      try {
         output.write(bytes);
      } catch (IOException var12) {
         throw new GdxRuntimeException("Error writing file: " + this.file + " (" + this.type + ")", var12);
      } finally {
         try {
            output.close();
         } catch (IOException var11) {
         }

      }

   }

   public void writeBytes(byte[] bytes, int offset, int length, boolean append) {
      OutputStream output = this.write(append);

      try {
         output.write(bytes, offset, length);
      } catch (IOException var14) {
         throw new GdxRuntimeException("Error writing file: " + this.file + " (" + this.type + ")", var14);
      } finally {
         try {
            output.close();
         } catch (IOException var13) {
         }

      }

   }

   public FileHandle[] list() {
      if (this.type == Files.FileType.Classpath) {
         throw new GdxRuntimeException("Cannot list a classpath directory: " + this.file);
      } else {
         String[] relativePaths = this.file().list();
         if (relativePaths == null) {
            return new FileHandle[0];
         } else {
            FileHandle[] handles = new FileHandle[relativePaths.length];
            int i = 0;

            for(int n = relativePaths.length; i < n; ++i) {
               handles[i] = this.child(relativePaths[i]);
            }

            return handles;
         }
      }
   }

   public FileHandle[] list(String suffix) {
      if (this.type == Files.FileType.Classpath) {
         throw new GdxRuntimeException("Cannot list a classpath directory: " + this.file);
      } else {
         String[] relativePaths = this.file().list();
         if (relativePaths == null) {
            return new FileHandle[0];
         } else {
            FileHandle[] handles = new FileHandle[relativePaths.length];
            int count = 0;
            int i = 0;

            for(int n = relativePaths.length; i < n; ++i) {
               String path = relativePaths[i];
               if (path.endsWith(suffix)) {
                  handles[count] = this.child(path);
                  ++count;
               }
            }

            if (count < relativePaths.length) {
               FileHandle[] newHandles = new FileHandle[count];
               System.arraycopy(handles, 0, newHandles, 0, count);
               handles = newHandles;
            }

            return handles;
         }
      }
   }

   public boolean isDirectory() {
      return this.type == Files.FileType.Classpath ? false : this.file().isDirectory();
   }

   public FileHandle child(String name) {
      return this.file.getPath().length() == 0 ? new FileHandle(new File(name), this.type) : new FileHandle(new File(this.file, name), this.type);
   }

   public FileHandle sibling(String name) {
      if (this.file.getPath().length() == 0) {
         throw new GdxRuntimeException("Cannot get the sibling of the root.");
      } else {
         return new FileHandle(new File(this.file.getParent(), name), this.type);
      }
   }

   public FileHandle parent() {
      File parent = this.file.getParentFile();
      if (parent == null) {
         if (this.type == Files.FileType.Absolute) {
            parent = new File("/");
         } else {
            parent = new File("");
         }
      }

      return new FileHandle(parent, this.type);
   }

   public void mkdirs() {
      if (this.type == Files.FileType.Classpath) {
         throw new GdxRuntimeException("Cannot mkdirs with a classpath file: " + this.file);
      } else if (this.type == Files.FileType.Internal) {
         throw new GdxRuntimeException("Cannot mkdirs with an internal file: " + this.file);
      } else {
         this.file().mkdirs();
      }
   }

   public boolean exists() {
      switch($SWITCH_TABLE$com$badlogic$gdx$Files$FileType()[this.type.ordinal()]) {
      case 2:
         if (this.file.exists()) {
            return true;
         }
      case 1:
         if (FileHandle.class.getResource("/" + this.file.getPath().replace('\\', '/')) != null) {
            return true;
         }

         return false;
      default:
         return this.file().exists();
      }
   }

   public boolean delete() {
      if (this.type == Files.FileType.Classpath) {
         throw new GdxRuntimeException("Cannot delete a classpath file: " + this.file);
      } else if (this.type == Files.FileType.Internal) {
         throw new GdxRuntimeException("Cannot delete an internal file: " + this.file);
      } else {
         return this.file().delete();
      }
   }

   public boolean deleteDirectory() {
      if (this.type == Files.FileType.Classpath) {
         throw new GdxRuntimeException("Cannot delete a classpath file: " + this.file);
      } else if (this.type == Files.FileType.Internal) {
         throw new GdxRuntimeException("Cannot delete an internal file: " + this.file);
      } else {
         return deleteDirectory(this.file());
      }
   }

   public void emptyDirectory() {
      this.emptyDirectory(false);
   }

   public void emptyDirectory(boolean preserveTree) {
      if (this.type == Files.FileType.Classpath) {
         throw new GdxRuntimeException("Cannot delete a classpath file: " + this.file);
      } else if (this.type == Files.FileType.Internal) {
         throw new GdxRuntimeException("Cannot delete an internal file: " + this.file);
      } else {
         emptyDirectory(this.file(), preserveTree);
      }
   }

   public void copyTo(FileHandle dest) {
      boolean sourceDir = this.isDirectory();
      if (!sourceDir) {
         if (dest.isDirectory()) {
            dest = dest.child(this.name());
         }

         copyFile(this, dest);
      } else {
         if (dest.exists()) {
            if (!dest.isDirectory()) {
               throw new GdxRuntimeException("Destination exists but is not a directory: " + dest);
            }
         } else {
            dest.mkdirs();
            if (!dest.isDirectory()) {
               throw new GdxRuntimeException("Destination directory cannot be created: " + dest);
            }
         }

         if (!sourceDir) {
            dest = dest.child(this.name());
         }

         copyDirectory(this, dest);
      }
   }

   public void moveTo(FileHandle dest) {
      if (this.type == Files.FileType.Classpath) {
         throw new GdxRuntimeException("Cannot move a classpath file: " + this.file);
      } else if (this.type == Files.FileType.Internal) {
         throw new GdxRuntimeException("Cannot move an internal file: " + this.file);
      } else {
         this.copyTo(dest);
         this.delete();
         if (this.exists() && this.isDirectory()) {
            this.deleteDirectory();
         }

      }
   }

   public long length() {
      if (this.type != Files.FileType.Classpath && (this.type != Files.FileType.Internal || this.file.exists())) {
         return this.file().length();
      } else {
         InputStream input = this.read();

         try {
            long var4 = (long)input.available();
            return var4;
         } catch (Exception var13) {
         } finally {
            try {
               input.close();
            } catch (IOException var12) {
            }

         }

         return 0L;
      }
   }

   public long lastModified() {
      return this.file().lastModified();
   }

   public String toString() {
      return this.file.getPath().replace('\\', '/');
   }

   public static FileHandle tempFile(String prefix) {
      try {
         return new FileHandle(File.createTempFile(prefix, (String)null));
      } catch (IOException var2) {
         throw new GdxRuntimeException("Unable to create temp file.", var2);
      }
   }

   public static FileHandle tempDirectory(String prefix) {
      try {
         File file = File.createTempFile(prefix, (String)null);
         if (!file.delete()) {
            throw new IOException("Unable to delete temp file: " + file);
         } else if (!file.mkdir()) {
            throw new IOException("Unable to create temp directory: " + file);
         } else {
            return new FileHandle(file);
         }
      } catch (IOException var2) {
         throw new GdxRuntimeException("Unable to create temp file.", var2);
      }
   }

   private static void emptyDirectory(File file, boolean preserveTree) {
      if (file.exists()) {
         File[] files = file.listFiles();
         if (files != null) {
            int i = 0;

            for(int n = files.length; i < n; ++i) {
               if (!files[i].isDirectory()) {
                  files[i].delete();
               } else if (preserveTree) {
                  emptyDirectory(files[i], true);
               } else {
                  deleteDirectory(files[i]);
               }
            }
         }
      }

   }

   private static boolean deleteDirectory(File file) {
      emptyDirectory(file, false);
      return file.delete();
   }

   private static void copyFile(FileHandle source, FileHandle dest) {
      try {
         dest.write(source.read(), false);
      } catch (Exception var3) {
         throw new GdxRuntimeException("Error copying source file: " + source.file + " (" + source.type + ")\n" + "To destination: " + dest.file + " (" + dest.type + ")", var3);
      }
   }

   private static void copyDirectory(FileHandle sourceDir, FileHandle destDir) {
      destDir.mkdirs();
      FileHandle[] files = sourceDir.list();
      int i = 0;

      for(int n = files.length; i < n; ++i) {
         FileHandle srcFile = files[i];
         FileHandle destFile = destDir.child(srcFile.name());
         if (srcFile.isDirectory()) {
            copyDirectory(srcFile, destFile);
         } else {
            copyFile(srcFile, destFile);
         }
      }

   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$com$badlogic$gdx$Files$FileType() {
      int[] var10000 = $SWITCH_TABLE$com$badlogic$gdx$Files$FileType;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[Files.FileType.values().length];

         try {
            var0[Files.FileType.Absolute.ordinal()] = 4;
         } catch (NoSuchFieldError var5) {
         }

         try {
            var0[Files.FileType.Classpath.ordinal()] = 1;
         } catch (NoSuchFieldError var4) {
         }

         try {
            var0[Files.FileType.External.ordinal()] = 3;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[Files.FileType.Internal.ordinal()] = 2;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[Files.FileType.Local.ordinal()] = 5;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$com$badlogic$gdx$Files$FileType = var0;
         return var0;
      }
   }
}
