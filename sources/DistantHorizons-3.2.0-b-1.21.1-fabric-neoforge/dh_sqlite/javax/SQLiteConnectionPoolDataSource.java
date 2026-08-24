package dh_sqlite.javax;

import dh_sqlite.SQLiteConfig;
import dh_sqlite.SQLiteDataSource;
import java.sql.SQLException;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;

public class SQLiteConnectionPoolDataSource extends SQLiteDataSource implements ConnectionPoolDataSource {
   public SQLiteConnectionPoolDataSource() {
   }

   public SQLiteConnectionPoolDataSource(SQLiteConfig config) {
      super(config);
   }

   @Override
   public PooledConnection getPooledConnection() throws SQLException {
      return this.getPooledConnection(null, null);
   }

   @Override
   public PooledConnection getPooledConnection(String user, String password) throws SQLException {
      return new SQLitePooledConnection(this.getConnection(user, password));
   }
}
