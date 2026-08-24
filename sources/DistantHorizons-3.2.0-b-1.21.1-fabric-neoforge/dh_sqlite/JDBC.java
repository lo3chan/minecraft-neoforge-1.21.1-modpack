package dh_sqlite;

import dh_sqlite.jdbc4.JDBC4Connection;
import dh_sqlite.util.Logger;
import dh_sqlite.util.LoggerFactory;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;

public class JDBC implements Driver {
   private static final Logger logger = LoggerFactory.getLogger(JDBC.class);
   public static final String PREFIX = "jdbc:dh_sqlite:";

   @Override
   public int getMajorVersion() {
      return SQLiteJDBCLoader.getMajorVersion();
   }

   @Override
   public int getMinorVersion() {
      return SQLiteJDBCLoader.getMinorVersion();
   }

   @Override
   public boolean jdbcCompliant() {
      return false;
   }

   @Override
   public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
      return null;
   }

   @Override
   public boolean acceptsURL(String url) {
      return isValidURL(url);
   }

   public static boolean isValidURL(String url) {
      return url != null && url.toLowerCase().startsWith("jdbc:dh_sqlite:");
   }

   @Override
   public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
      return SQLiteConfig.getDriverPropertyInfo();
   }

   @Override
   public Connection connect(String url, Properties info) throws SQLException {
      return createConnection(url, info);
   }

   static String extractAddress(String url) {
      return url.substring("jdbc:dh_sqlite:".length());
   }

   public static SQLiteConnection createConnection(String url, Properties prop) throws SQLException {
      if (!isValidURL(url)) {
         return null;
      } else {
         url = url.trim();
         return new JDBC4Connection(url, extractAddress(url), prop);
      }
   }

   static {
      try {
         DriverManager.registerDriver(new JDBC());
      } catch (SQLException var1) {
         logger.error(() -> "Could not register driver", var1);
      }
   }
}
