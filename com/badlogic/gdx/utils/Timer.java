package com.badlogic.gdx.utils;

import com.badlogic.gdx.Gdx;

public class Timer {
   static final Array<Timer> instances = new Array(1);
   public static final Timer instance;
   private static final int CANCELLED = -1;
   private static final int FOREVER = -2;
   private final Array<Timer.Task> tasks = new Array(false, 8);

   static {
      Thread thread = new Thread("Timer") {
         public void run() {
            while(true) {
               synchronized(Timer.instances) {
                  long timeMillis = System.nanoTime() / 1000000L;
                  long waitMillis = Long.MAX_VALUE;
                  int i = 0;

                  for(int n = Timer.instances.size; i < n; ++i) {
                     try {
                        waitMillis = ((Timer)Timer.instances.get(i)).update(timeMillis, waitMillis);
                     } catch (Throwable var10) {
                        throw new GdxRuntimeException("Task failed: " + ((Timer)Timer.instances.get(i)).getClass().getName(), var10);
                     }
                  }

                  try {
                     if (waitMillis > 0L) {
                        Timer.instances.wait(waitMillis);
                     }
                  } catch (InterruptedException var9) {
                  }
               }
            }
         }
      };
      thread.setDaemon(true);
      thread.start();
      instance = new Timer();
   }

   public Timer() {
      this.start();
   }

   public void postTask(Timer.Task task) {
      this.scheduleTask(task, 0.0F, 0.0F, 0);
   }

   public void scheduleTask(Timer.Task task, float delaySeconds) {
      this.scheduleTask(task, delaySeconds, 0.0F, 0);
   }

   public void scheduleTask(Timer.Task task, float delaySeconds, float intervalSeconds) {
      this.scheduleTask(task, delaySeconds, intervalSeconds, -2);
   }

   public void scheduleTask(Timer.Task task, float delaySeconds, float intervalSeconds, int repeatCount) {
      if (task.repeatCount != -1) {
         throw new IllegalArgumentException("The same task may not be scheduled twice.");
      } else {
         task.executeTimeMillis = System.nanoTime() / 1000000L + (long)(delaySeconds * 1000.0F);
         task.intervalMillis = (long)(intervalSeconds * 1000.0F);
         task.repeatCount = repeatCount;
         synchronized(this.tasks) {
            this.tasks.add(task);
         }

         wake();
      }
   }

   public void stop() {
      synchronized(instances) {
         instances.removeValue(this, true);
      }
   }

   public void start() {
      synchronized(instances) {
         if (!instances.contains(this, true)) {
            instances.add(this);
            wake();
         }
      }
   }

   public void clear() {
      synchronized(this.tasks) {
         int i = 0;

         for(int n = this.tasks.size; i < n; ++i) {
            ((Timer.Task)this.tasks.get(i)).cancel();
         }

         this.tasks.clear();
      }
   }

   long update(long timeMillis, long waitMillis) {
      synchronized(this.tasks) {
         int i = 0;

         for(int n = this.tasks.size; i < n; ++i) {
            Timer.Task task = (Timer.Task)this.tasks.get(i);
            if (task.executeTimeMillis > timeMillis) {
               waitMillis = Math.min(waitMillis, task.executeTimeMillis - timeMillis);
            } else {
               if (task.repeatCount != -1) {
                  if (task.repeatCount == 0) {
                     task.repeatCount = -1;
                  }

                  Gdx.app.postRunnable(task);
               }

               if (task.repeatCount == -1) {
                  this.tasks.removeIndex(i);
                  --i;
                  --n;
               } else {
                  task.executeTimeMillis = timeMillis + task.intervalMillis;
                  waitMillis = Math.min(waitMillis, task.intervalMillis);
                  if (task.repeatCount > 0) {
                     --task.repeatCount;
                  }
               }
            }
         }

         return waitMillis;
      }
   }

   private static void wake() {
      synchronized(instances) {
         instances.notifyAll();
      }
   }

   public static void post(Timer.Task task) {
      instance.postTask(task);
   }

   public static void schedule(Timer.Task task, float delaySeconds) {
      instance.scheduleTask(task, delaySeconds);
   }

   public static void schedule(Timer.Task task, float delaySeconds, float intervalSeconds) {
      instance.scheduleTask(task, delaySeconds, intervalSeconds);
   }

   public static void schedule(Timer.Task task, float delaySeconds, float intervalSeconds, int repeatCount) {
      instance.scheduleTask(task, delaySeconds, intervalSeconds, repeatCount);
   }

   public abstract static class Task implements Runnable {
      long executeTimeMillis;
      long intervalMillis;
      int repeatCount = -1;

      public abstract void run();

      public void cancel() {
         this.executeTimeMillis = 0L;
         this.repeatCount = -1;
      }

      public boolean isScheduled() {
         return this.repeatCount != -1;
      }
   }
}
