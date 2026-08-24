package dh_sqlite.jdbc4;

import dh_sqlite.SQLiteConnection;
import dh_sqlite.jdbc3.JDBC3Statement;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBC4Statement extends JDBC3Statement implements Statement {
   private boolean closed = false;
   boolean closeOnCompletion;

   public JDBC4Statement(SQLiteConnection conn) {
      super(conn);
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
   public void close() throws SQLException {
      super.close();
      this.closed = true;
   }

   @Override
   public boolean isClosed() {
      return this.closed;
   }

   @Override
   public void closeOnCompletion() throws SQLException {
      if (this.closed) {
         throw new SQLException("statement is closed");
      } else {
         this.closeOnCompletion = true;
      }
   }

   @Override
   public boolean isCloseOnCompletion() throws SQLException {
      if (this.closed) {
         throw new SQLException("statement is closed");
      } else {
         return this.closeOnCompletion;
      }
   }

   @Override
   public void setPoolable(boolean poolable) throws SQLException {
   }

   @Override
   public boolean isPoolable() throws SQLException {
      return false;
   }
}
