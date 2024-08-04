package com.badlogic.gdx.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class RemoteInput implements Runnable, Input {
   public static int DEFAULT_PORT = 8190;
   private ServerSocket serverSocket;
   private float[] accel;
   private float[] compass;
   private boolean multiTouch;
   private float remoteWidth;
   private float remoteHeight;
   private boolean connected;
   private RemoteInput.RemoteInputListener listener;
   Set<Integer> keys;
   int[] touchX;
   int[] touchY;
   boolean[] isTouched;
   boolean justTouched;
   InputProcessor processor;
   private final int port;
   public final String[] ips;

   public RemoteInput() {
      this(DEFAULT_PORT);
   }

   public RemoteInput(RemoteInput.RemoteInputListener listener) {
      this(DEFAULT_PORT, listener);
   }

   public RemoteInput(int port) {
      this(port, (RemoteInput.RemoteInputListener)null);
   }

   public RemoteInput(int port, RemoteInput.RemoteInputListener listener) {
      this.accel = new float[3];
      this.compass = new float[3];
      this.multiTouch = false;
      this.remoteWidth = 0.0F;
      this.remoteHeight = 0.0F;
      this.connected = false;
      this.keys = new HashSet();
      this.touchX = new int[20];
      this.touchY = new int[20];
      this.isTouched = new boolean[20];
      this.justTouched = false;
      this.processor = null;
      this.listener = listener;

      try {
         this.port = port;
         this.serverSocket = new ServerSocket(port);
         Thread thread = new Thread(this);
         thread.setDaemon(true);
         thread.start();
         InetAddress[] allByName = InetAddress.getAllByName(InetAddress.getLocalHost().getHostName());
         this.ips = new String[allByName.length];

         for(int i = 0; i < allByName.length; ++i) {
            this.ips[i] = allByName[i].getHostAddress();
         }

      } catch (Exception var6) {
         throw new GdxRuntimeException("Couldn't open listening socket at port '" + port + "'", var6);
      }
   }

   public void run() {
      while(true) {
         try {
            this.connected = false;
            if (this.listener != null) {
               this.listener.onDisconnected();
            }

            System.out.println("listening, port " + this.port);
            Socket socket = null;
            socket = this.serverSocket.accept();
            socket.setTcpNoDelay(true);
            socket.setSoTimeout(3000);
            this.connected = true;
            if (this.listener != null) {
               this.listener.onConnected();
            }

            DataInputStream in = new DataInputStream(socket.getInputStream());
            this.multiTouch = in.readBoolean();

            while(true) {
               int event = in.readInt();
               RemoteInput.KeyEvent keyEvent = null;
               RemoteInput.TouchEvent touchEvent = null;
               switch(event) {
               case 0:
                  keyEvent = new RemoteInput.KeyEvent();
                  keyEvent.keyCode = in.readInt();
                  keyEvent.type = 0;
                  break;
               case 1:
                  keyEvent = new RemoteInput.KeyEvent();
                  keyEvent.keyCode = in.readInt();
                  keyEvent.type = 1;
                  break;
               case 2:
                  keyEvent = new RemoteInput.KeyEvent();
                  keyEvent.keyChar = in.readChar();
                  keyEvent.type = 2;
                  break;
               case 3:
                  touchEvent = new RemoteInput.TouchEvent();
                  touchEvent.x = (int)((float)in.readInt() / this.remoteWidth * (float)Gdx.graphics.getWidth());
                  touchEvent.y = (int)((float)in.readInt() / this.remoteHeight * (float)Gdx.graphics.getHeight());
                  touchEvent.pointer = in.readInt();
                  touchEvent.type = 0;
                  break;
               case 4:
                  touchEvent = new RemoteInput.TouchEvent();
                  touchEvent.x = (int)((float)in.readInt() / this.remoteWidth * (float)Gdx.graphics.getWidth());
                  touchEvent.y = (int)((float)in.readInt() / this.remoteHeight * (float)Gdx.graphics.getHeight());
                  touchEvent.pointer = in.readInt();
                  touchEvent.type = 1;
                  break;
               case 5:
                  touchEvent = new RemoteInput.TouchEvent();
                  touchEvent.x = (int)((float)in.readInt() / this.remoteWidth * (float)Gdx.graphics.getWidth());
                  touchEvent.y = (int)((float)in.readInt() / this.remoteHeight * (float)Gdx.graphics.getHeight());
                  touchEvent.pointer = in.readInt();
                  touchEvent.type = 2;
                  break;
               case 6:
                  this.accel[0] = in.readFloat();
                  this.accel[1] = in.readFloat();
                  this.accel[2] = in.readFloat();
                  break;
               case 7:
                  this.compass[0] = in.readFloat();
                  this.compass[1] = in.readFloat();
                  this.compass[2] = in.readFloat();
                  break;
               case 8:
                  this.remoteWidth = in.readFloat();
                  this.remoteHeight = in.readFloat();
               }

               Gdx.app.postRunnable(new RemoteInput.EventTrigger(touchEvent, keyEvent));
            }
         } catch (IOException var6) {
            var6.printStackTrace();
         }
      }
   }

   public boolean isConnected() {
      return this.connected;
   }

   public float getAccelerometerX() {
      return this.accel[0];
   }

   public float getAccelerometerY() {
      return this.accel[1];
   }

   public float getAccelerometerZ() {
      return this.accel[2];
   }

   public int getX() {
      return this.touchX[0];
   }

   public int getX(int pointer) {
      return this.touchX[pointer];
   }

   public int getY() {
      return this.touchY[0];
   }

   public int getY(int pointer) {
      return this.touchY[pointer];
   }

   public boolean isTouched() {
      return this.isTouched[0];
   }

   public boolean justTouched() {
      return this.justTouched;
   }

   public boolean isTouched(int pointer) {
      return this.isTouched[pointer];
   }

   public boolean isButtonPressed(int button) {
      if (button != 0) {
         return false;
      } else {
         for(int i = 0; i < this.isTouched.length; ++i) {
            if (this.isTouched[i]) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean isKeyPressed(int key) {
      return this.keys.contains(key);
   }

   public void getTextInput(Input.TextInputListener listener, String title, String text) {
      Gdx.app.getInput().getTextInput(listener, title, text);
   }

   public void getPlaceholderTextInput(Input.TextInputListener listener, String title, String placeholder) {
      Gdx.app.getInput().getPlaceholderTextInput(listener, title, placeholder);
   }

   public void setOnscreenKeyboardVisible(boolean visible) {
   }

   public void vibrate(int milliseconds) {
   }

   public void vibrate(long[] pattern, int repeat) {
   }

   public void cancelVibrate() {
   }

   public float getAzimuth() {
      return this.compass[0];
   }

   public float getPitch() {
      return this.compass[1];
   }

   public float getRoll() {
      return this.compass[2];
   }

   public void setCatchBackKey(boolean catchBack) {
   }

   public void setInputProcessor(InputProcessor processor) {
      this.processor = processor;
   }

   public InputProcessor getInputProcessor() {
      return this.processor;
   }

   public String[] getIPs() {
      return this.ips;
   }

   public boolean isPeripheralAvailable(Input.Peripheral peripheral) {
      if (peripheral == Input.Peripheral.Accelerometer) {
         return true;
      } else if (peripheral == Input.Peripheral.Compass) {
         return true;
      } else {
         return peripheral == Input.Peripheral.MultitouchScreen ? this.multiTouch : false;
      }
   }

   public int getRotation() {
      return 0;
   }

   public Input.Orientation getNativeOrientation() {
      return Input.Orientation.Landscape;
   }

   public void setCursorCatched(boolean catched) {
   }

   public boolean isCursorCatched() {
      return false;
   }

   public int getDeltaX() {
      return 0;
   }

   public int getDeltaX(int pointer) {
      return 0;
   }

   public int getDeltaY() {
      return 0;
   }

   public int getDeltaY(int pointer) {
      return 0;
   }

   public void setCursorPosition(int x, int y) {
   }

   public void setCatchMenuKey(boolean catchMenu) {
   }

   public long getCurrentEventTime() {
      return 0L;
   }

   public void getRotationMatrix(float[] matrix) {
   }

   class EventTrigger implements Runnable {
      RemoteInput.TouchEvent touchEvent;
      RemoteInput.KeyEvent keyEvent;

      public EventTrigger(RemoteInput.TouchEvent touchEvent, RemoteInput.KeyEvent keyEvent) {
         this.touchEvent = touchEvent;
         this.keyEvent = keyEvent;
      }

      public void run() {
         RemoteInput.this.justTouched = false;
         if (RemoteInput.this.processor != null) {
            if (this.touchEvent != null) {
               RemoteInput.this.touchX[this.touchEvent.pointer] = this.touchEvent.x;
               RemoteInput.this.touchY[this.touchEvent.pointer] = this.touchEvent.y;
               switch(this.touchEvent.type) {
               case 0:
                  RemoteInput.this.processor.touchDown(this.touchEvent.x, this.touchEvent.y, this.touchEvent.pointer, 0);
                  RemoteInput.this.isTouched[this.touchEvent.pointer] = true;
                  RemoteInput.this.justTouched = true;
                  break;
               case 1:
                  RemoteInput.this.processor.touchUp(this.touchEvent.x, this.touchEvent.y, this.touchEvent.pointer, 0);
                  RemoteInput.this.isTouched[this.touchEvent.pointer] = false;
                  break;
               case 2:
                  RemoteInput.this.processor.touchDragged(this.touchEvent.x, this.touchEvent.y, this.touchEvent.pointer);
               }
            }

            if (this.keyEvent != null) {
               switch(this.keyEvent.type) {
               case 0:
                  RemoteInput.this.processor.keyDown(this.keyEvent.keyCode);
                  RemoteInput.this.keys.add(this.keyEvent.keyCode);
                  break;
               case 1:
                  RemoteInput.this.processor.keyUp(this.keyEvent.keyCode);
                  RemoteInput.this.keys.remove(this.keyEvent.keyCode);
                  break;
               case 2:
                  RemoteInput.this.processor.keyTyped(this.keyEvent.keyChar);
               }
            }
         } else {
            if (this.touchEvent != null) {
               RemoteInput.this.touchX[this.touchEvent.pointer] = this.touchEvent.x;
               RemoteInput.this.touchY[this.touchEvent.pointer] = this.touchEvent.y;
               if (this.touchEvent.type == 0) {
                  RemoteInput.this.isTouched[this.touchEvent.pointer] = true;
                  RemoteInput.this.justTouched = true;
               }

               if (this.touchEvent.type == 1) {
                  RemoteInput.this.isTouched[this.touchEvent.pointer] = false;
               }
            }

            if (this.keyEvent != null) {
               if (this.keyEvent.type == 0) {
                  RemoteInput.this.keys.add(this.keyEvent.keyCode);
               }

               if (this.keyEvent.type == 1) {
                  RemoteInput.this.keys.remove(this.keyEvent.keyCode);
               }
            }
         }

      }
   }

   class KeyEvent {
      static final int KEY_DOWN = 0;
      static final int KEY_UP = 1;
      static final int KEY_TYPED = 2;
      long timeStamp;
      int type;
      int keyCode;
      char keyChar;
   }

   public interface RemoteInputListener {
      void onConnected();

      void onDisconnected();
   }

   class TouchEvent {
      static final int TOUCH_DOWN = 0;
      static final int TOUCH_UP = 1;
      static final int TOUCH_DRAGGED = 2;
      long timeStamp;
      int type;
      int x;
      int y;
      int pointer;
   }
}
