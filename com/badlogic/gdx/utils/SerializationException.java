package com.badlogic.gdx.utils;

public class SerializationException extends RuntimeException {
   private StringBuffer trace;

   public SerializationException() {
   }

   public SerializationException(String message, Throwable cause) {
      super(message, cause);
   }

   public SerializationException(String message) {
      super(message);
   }

   public SerializationException(Throwable cause) {
      super("", cause);
   }

   public boolean causedBy(Class type) {
      return this.causedBy(this, type);
   }

   private boolean causedBy(Throwable ex, Class type) {
      Throwable cause = ex.getCause();
      if (cause != null && cause != ex) {
         return type.isAssignableFrom(cause.getClass()) ? true : this.causedBy(cause, type);
      } else {
         return false;
      }
   }

   public String getMessage() {
      if (this.trace == null) {
         return super.getMessage();
      } else {
         StringBuffer buffer = new StringBuffer(512);
         buffer.append(super.getMessage());
         if (buffer.length() > 0) {
            buffer.append('\n');
         }

         buffer.append("Serialization trace:");
         buffer.append(this.trace);
         return buffer.toString();
      }
   }

   public void addTrace(String info) {
      if (info == null) {
         throw new IllegalArgumentException("info cannot be null.");
      } else {
         if (this.trace == null) {
            this.trace = new StringBuffer(512);
         }

         this.trace.append('\n');
         this.trace.append(info);
      }
   }
}
