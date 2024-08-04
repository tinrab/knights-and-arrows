package com.badlogic.gdx.utils.async;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AsyncResult<T> {
   private final Future<T> future;

   AsyncResult(Future<T> future) {
      this.future = future;
   }

   public boolean isDone() {
      return this.future.isDone();
   }

   public T get() {
      try {
         return this.future.get();
      } catch (InterruptedException var2) {
         return null;
      } catch (ExecutionException var3) {
         return null;
      }
   }
}
