package dh_sqlite.jdbc4;

import dh_sqlite.jdbc3.JDBC3Connection;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLXML;
import java.sql.Statement;
import java.util.Properties;

public class JDBC4Connection extends JDBC3Connection {
   public JDBC4Connection(String url, String fileName, Properties prop) throws SQLException {
      super(url, fileName, prop);
   }

   @Override
   public Statement createStatement(int rst, int rsc, int rsh) throws SQLException {
      this.checkOpen();
      this.checkCursor(rst, rsc, rsh);
      return new JDBC4Statement(this);
   }

   @Override
   public PreparedStatement prepareStatement(String sql, int rst, int rsc, int rsh) throws SQLException {
      this.checkOpen();
      this.checkCursor(rst, rsc, rsh);
      return new JDBC4PreparedStatement(this, sql);
   }

   @Override
   public boolean isClosed() throws SQLException {
      return super.isClosed();
   }

   @Override
   public <T> T unwrap(Class<T> iface) throws ClassCastException {
      return iface.cast(this);
   }

   @Override
   public boolean isWrapperFor(Class<?> iface) {
      return iface.isInstance(this);
   }

   @Override
   public Clob createClob() throws SQLException {
      throw new SQLFeatureNotSupportedException();
   }

   @Override
   public Blob createBlob() throws SQLException {
      throw new SQLFeatureNotSupportedException();
   }

   @Override
   public NClob createNClob() throws SQLException {
      throw new SQLFeatureNotSupportedException();
   }

   @Override
   public SQLXML createSQLXML() throws SQLException {
      throw new SQLFeatureNotSupportedException();
   }

   @Override
   public boolean isValid(int timeout) throws SQLException {
      if (this.isClosed()) {
         return false;
      } else {
         Statement statement = this.createStatement();

         boolean var3;
         try {
            var3 = statement.execute("select 1");
         } finally {
            statement.close();
         }

         return var3;
      }
   }

   @Override
   public void setClientInfo(String name, String value) throws SQLClientInfoException {
   }

   @Override
   public void setClientInfo(Properties properties) throws SQLClientInfoException {
   }

   @Override
   public String getClientInfo(String name) throws SQLException {
      return null;
   }

   @Override
   public Properties getClientInfo() throws SQLException {
      return null;
   }

   @Override
   public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
      return null;
   }
}
