package com.badlogic.gdx;

import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.TimeUtils;

public class InputProcessorQueue implements InputProcessor {
   private static final int KEY_DOWN = 0;
   private static final int KEY_UP = 1;
   private static final int KEY_TYPED = 2;
   private static final int TOUCH_DOWN = 3;
   private static final int TOUCH_UP = 4;
   private static final int TOUCH_DRAGGED = 5;
   private static final int MOUSE_MOVED = 6;
   private static final int SCROLLED = 7;
   private InputProcessor processor;
   private final IntArray queue = new IntArray();
   private final IntArray processingQueue = new IntArray();
   private long currentEventTime;

   public InputProcessorQueue() {
   }

   public InputProcessorQueue(InputProcessor processor) {
      this.processor = processor;
   }

   public void setProcessor(InputProcessor processor) {
      this.processor = processor;
   }

   public InputProcessor getProcessor() {
      return this.processor;
   }

   public void drain() {
      IntArray q = this.processingQueue;
      synchronized(this) {
         if (this.processor == null) {
            this.queue.clear();
            return;
         }

         q.addAll(this.queue);
         this.queue.clear();
      }

      int i = 0;
      int n = q.size;

      while(i < n) {
         this.currentEventTime = (long)q.get(i++) << 32 | (long)q.get(i++) & 4294967295L;
         switch(q.get(i++)) {
         case 0:
            this.processor.keyDown(q.get(i++));
            break;
         case 1:
            this.processor.keyUp(q.get(i++));
            break;
         case 2:
            this.processor.keyTyped((char)q.get(i++));
            break;
         case 3:
            this.processor.touchDown(q.get(i++), q.get(i++), q.get(i++), q.get(i++));
            break;
         case 4:
            this.processor.touchUp(q.get(i++), q.get(i++), q.get(i++), q.get(i++));
            break;
         case 5:
            this.processor.touchDragged(q.get(i++), q.get(i++), q.get(i++));
            break;
         case 6:
            this.processor.mouseMoved(q.get(i++), q.get(i++));
            break;
         case 7:
            this.processor.scrolled(q.get(i++));
         }
      }

      q.clear();
   }

   private void queueTime() {
      long time = TimeUtils.nanoTime();
      this.queue.add((int)(time >> 32));
      this.queue.add((int)time);
   }

   public synchronized boolean keyDown(int keycode) {
      this.queueTime();
      this.queue.add(0);
      this.queue.add(keycode);
      return false;
   }

   public synchronized boolean keyUp(int keycode) {
      this.queueTime();
      this.queue.add(1);
      this.queue.add(keycode);
      return false;
   }

   public synchronized boolean keyTyped(char character) {
      this.queueTime();
      this.queue.add(2);
      this.queue.add(character);
      return false;
   }

   public synchronized boolean touchDown(int screenX, int screenY, int pointer, int button) {
      this.queueTime();
      this.queue.add(3);
      this.queue.add(screenX);
      this.queue.add(screenY);
      this.queue.add(pointer);
      this.queue.add(button);
      return false;
   }

   public synchronized boolean touchUp(int screenX, int screenY, int pointer, int button) {
      this.queueTime();
      this.queue.add(4);
      this.queue.add(screenX);
      this.queue.add(screenY);
      this.queue.add(pointer);
      this.queue.add(button);
      return false;
   }

   public synchronized boolean touchDragged(int screenX, int screenY, int pointer) {
      this.queueTime();
      this.queue.add(5);
      this.queue.add(screenX);
      this.queue.add(screenY);
      this.queue.add(pointer);
      return false;
   }

   public synchronized boolean mouseMoved(int screenX, int screenY) {
      this.queueTime();
      this.queue.add(6);
      this.queue.add(screenX);
      this.queue.add(screenY);
      return false;
   }

   public synchronized boolean scrolled(int amount) {
      this.queueTime();
      this.queue.add(7);
      this.queue.add(amount);
      return false;
   }

   public long getCurrentEventTime() {
      return this.currentEventTime;
   }
}
