package com.seibel.distanthorizons.core.sql;

import java.sql.SQLException;

public class DbCorruptedException extends SQLException {
   public DbCorruptedException(String message) {
      super(message);
   }

   public DbCorruptedException(String message, Throwable cause) {
      super(message, cause);
   }

   public DbCorruptedException(Throwable cause) {
      super(cause);
   }

   public static boolean isCorruptedException(SQLException e) {
      String message = e.getMessage().toLowerCase();
      return message.contains("sqlite_corrupt") || message.contains("malformed");
   }
}
