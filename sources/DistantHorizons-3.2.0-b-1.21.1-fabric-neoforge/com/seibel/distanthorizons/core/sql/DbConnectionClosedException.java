package com.seibel.distanthorizons.core.sql;

import java.sql.SQLException;

public class DbConnectionClosedException extends SQLException {
   public DbConnectionClosedException() {
      super("The database connection is closed.");
   }

   public DbConnectionClosedException(String message) {
      super(message);
   }

   public DbConnectionClosedException(String message, Throwable cause) {
      super(message, cause);
   }

   public DbConnectionClosedException(Throwable cause) {
      super(cause);
   }

   public static boolean isClosedException(SQLException e) {
      String message = e.getMessage().toLowerCase();
      return message.contains("connection closed")
         || message.contains("pointer is closed")
         || message.contains("stmt pointer is closed")
         || message.contains("database has been closed");
   }
}
