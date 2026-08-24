package com.seibel.distanthorizons.core.util.objects;

public class DataCorruptedException extends Exception {
   public DataCorruptedException(Exception e) {
      super(e.getMessage());
      this.setStackTrace(e.getStackTrace());
      this.addSuppressed(e);
   }

   public DataCorruptedException(String message) {
      super(message);
   }

   public DataCorruptedException(String message, Exception e) {
      super(message);
      this.setStackTrace(e.getStackTrace());
      this.addSuppressed(e);
   }
}
