package dh_sqlite.core;

import dh_sqlite.SQLiteConnection;
import dh_sqlite.SQLiteConnectionConfig;
import dh_sqlite.date.FastDateFormat;
import dh_sqlite.jdbc3.JDBC3Connection;
import dh_sqlite.jdbc4.JDBC4Statement;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;

public abstract class CorePreparedStatement extends JDBC4Statement {
   protected int columnCount;
   protected int paramCount;
   protected int batchQueryCount;

   protected CorePreparedStatement(SQLiteConnection conn, String sql) throws SQLException {
      super(conn);
      this.sql = sql;
      DB db = conn.getDatabase();
      db.prepare(this);
      this.rs.colsMeta = this.pointer.safeRun(DB::column_names);
      this.columnCount = this.pointer.safeRunInt(DB::column_count);
      this.paramCount = this.pointer.safeRunInt(DB::bind_parameter_count);
      this.batchQueryCount = 0;
      this.batch = null;
      this.batchPos = 0;
   }

   @Override
   public int[] executeBatch() throws SQLException {
      return Arrays.stream(this.executeLargeBatch()).mapToInt(l -> (int)l).toArray();
   }

   @Override
   public long[] executeLargeBatch() throws SQLException {
      if (this.batchQueryCount == 0) {
         return new long[0];
      } else {
         if (this.conn instanceof JDBC3Connection) {
            ((JDBC3Connection)this.conn).tryEnforceTransactionMode();
         }

         return this.withConnectionTimeout(() -> {
            long[] var1;
            try {
               var1 = this.conn.getDatabase().executeBatch(this.pointer, this.batchQueryCount, this.batch, this.conn.getAutoCommit());
            } finally {
               this.clearBatch();
            }

            return var1;
         });
      }
   }

   @Override
   public void clearBatch() throws SQLException {
      super.clearBatch();
      this.batchQueryCount = 0;
   }

   protected void batch(int pos, Object value) throws SQLException {
      this.checkOpen();
      if (this.batch == null) {
         this.batch = new Object[this.paramCount];
      }

      this.batch[this.batchPos + pos - 1] = value;
   }

   protected void setDateByMilliseconds(int pos, Long value, Calendar calendar) throws SQLException {
      SQLiteConnectionConfig config = this.conn.getConnectionConfig();
      switch (config.getDateClass()) {
         case TEXT:
            this.batch(pos, FastDateFormat.getInstance(config.getDateStringFormat(), calendar.getTimeZone()).format(new Date(value)));
            break;
         case REAL:
            this.batch(pos, new Double(value.longValue() / 8.64E7 + 2440587.5));
            break;
         default:
            this.batch(pos, new Long(value / config.getDateMultiplier()));
      }
   }
}
