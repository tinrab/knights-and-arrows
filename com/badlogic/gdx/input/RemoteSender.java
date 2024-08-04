package com.badlogic.gdx.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import java.io.DataOutputStream;
import java.net.Socket;

public class RemoteSender implements InputProcessor {
   private DataOutputStream out;
   private boolean connected = false;
   public static final int KEY_DOWN = 0;
   public static final int KEY_UP = 1;
   public static final int KEY_TYPED = 2;
   public static final int TOUCH_DOWN = 3;
   public static final int TOUCH_UP = 4;
   public static final int TOUCH_DRAGGED = 5;
   public static final int ACCEL = 6;
   public static final int COMPASS = 7;
   public static final int SIZE = 8;

   public RemoteSender(String ip, int port) {
      try {
         Socket socket = new Socket(ip, port);
         socket.setTcpNoDelay(true);
         socket.setSoTimeout(3000);
         this.out = new DataOutputStream(socket.getOutputStream());
         this.out.writeBoolean(Gdx.input.isPeripheralAvailable(Input.Peripheral.MultitouchScreen));
         this.connected = true;
         Gdx.input.setInputProcessor(this);
      } catch (Exception var4) {
         Gdx.app.log("RemoteSender", "couldn't connect to " + ip + ":" + port);
      }

   }

   public void sendUpdate() {
      synchronized(this) {
         if (!this.connected) {
            return;
         }
      }

      try {
         this.out.writeInt(6);
         this.out.writeFloat(Gdx.input.getAccelerometerX());
         this.out.writeFloat(Gdx.input.getAccelerometerY());
         this.out.writeFloat(Gdx.input.getAccelerometerZ());
         this.out.writeInt(7);
         this.out.writeFloat(Gdx.input.getAzimuth());
         this.out.writeFloat(Gdx.input.getPitch());
         this.out.writeFloat(Gdx.input.getRoll());
         this.out.writeInt(8);
         this.out.writeFloat((float)Gdx.graphics.getWidth());
         this.out.writeFloat((float)Gdx.graphics.getHeight());
      } catch (Throwable var2) {
         this.out = null;
         this.connected = false;
      }

   }

   public boolean keyDown(int keycode) {
      synchronized(this) {
         if (!this.connected) {
            return false;
         }
      }

      try {
         this.out.writeInt(0);
         this.out.writeInt(keycode);
      } catch (Throwable var5) {
         synchronized(this) {
            this.connected = false;
         }
      }

      return false;
   }

   public boolean keyUp(int keycode) {
      synchronized(this) {
         if (!this.connected) {
            return false;
         }
      }

      try {
         this.out.writeInt(1);
         this.out.writeInt(keycode);
      } catch (Throwable var5) {
         synchronized(this) {
            this.connected = false;
         }
      }

      return false;
   }

   public boolean keyTyped(char character) {
      synchronized(this) {
         if (!this.connected) {
            return false;
         }
      }

      try {
         this.out.writeInt(2);
         this.out.writeChar(character);
      } catch (Throwable var5) {
         synchronized(this) {
            this.connected = false;
         }
      }

      return false;
   }

   public boolean touchDown(int x, int y, int pointer, int button) {
      synchronized(this) {
         if (!this.connected) {
            return false;
         }
      }

      try {
         this.out.writeInt(3);
         this.out.writeInt(x);
         this.out.writeInt(y);
         this.out.writeInt(pointer);
      } catch (Throwable var8) {
         synchronized(this) {
            this.connected = false;
         }
      }

      return false;
   }

   public boolean touchUp(int x, int y, int pointer, int button) {
      synchronized(this) {
         if (!this.connected) {
            return false;
         }
      }

      try {
         this.out.writeInt(4);
         this.out.writeInt(x);
         this.out.writeInt(y);
         this.out.writeInt(pointer);
      } catch (Throwable var8) {
         synchronized(this) {
            this.connected = false;
         }
      }

      return false;
   }

   public boolean touchDragged(int x, int y, int pointer) {
      synchronized(this) {
         if (!this.connected) {
            return false;
         }
      }

      try {
         this.out.writeInt(5);
         this.out.writeInt(x);
         this.out.writeInt(y);
         this.out.writeInt(pointer);
      } catch (Throwable var7) {
         synchronized(this) {
            this.connected = false;
         }
      }

      return false;
   }

   public boolean mouseMoved(int x, int y) {
      return false;
   }

   public boolean scrolled(int amount) {
      return false;
   }

   public boolean isConnected() {
      synchronized(this) {
         return this.connected;
      }
   }
}
