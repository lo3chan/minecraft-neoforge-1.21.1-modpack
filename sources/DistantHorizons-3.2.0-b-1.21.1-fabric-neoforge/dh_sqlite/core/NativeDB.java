package dh_sqlite.core;

import dh_sqlite.BusyHandler;
import dh_sqlite.Collation;
import dh_sqlite.Function;
import dh_sqlite.ProgressHandler;
import dh_sqlite.SQLiteConfig;
import dh_sqlite.SQLiteJDBCLoader;
import dh_sqlite.util.Logger;
import dh_sqlite.util.LoggerFactory;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.MessageFormat;

public final class NativeDB extends DB {
   private static final Logger logger = LoggerFactory.getLogger(NativeDB.class);
   private static final int DEFAULT_BACKUP_BUSY_SLEEP_TIME_MILLIS = 100;
   private static final int DEFAULT_BACKUP_NUM_BUSY_BEFORE_FAIL = 3;
   private static final int DEFAULT_PAGES_PER_BACKUP_STEP = 100;
   private long pointer = 0L;
   private static boolean isLoaded;
   private static boolean loadSucceeded;
   private long busyHandler = 0L;
   private long commitListener = 0L;
   private long updateListener = 0L;
   private long progressHandler;

   public NativeDB(String url, String fileName, SQLiteConfig config) throws SQLException {
      super(url, fileName, config);
   }

   public static boolean load() throws Exception {
      if (isLoaded) {
         return loadSucceeded;
      } else {
         try {
            loadSucceeded = SQLiteJDBCLoader.initialize();
         } finally {
            isLoaded = true;
         }

         return loadSucceeded;
      }
   }

   @Override
   protected synchronized void _open(String file, int openFlags) throws SQLException {
      this._open_utf8(stringToUtf8ByteArray(file), openFlags);
   }

   synchronized native void _open_utf8(byte[] bs, int i) throws SQLException;

   @Override
   protected synchronized native void _close() throws SQLException;

   @Override
   public synchronized int _exec(String sql) throws SQLException {
      logger.trace(() -> MessageFormat.format("DriverManager [{0}] [SQLite EXEC] {1}", Thread.currentThread().getName(), sql));
      return this._exec_utf8(stringToUtf8ByteArray(sql));
   }

   synchronized native int _exec_utf8(byte[] bs) throws SQLException;

   @Override
   public synchronized native int shared_cache(boolean bl);

   @Override
   public synchronized native int enable_load_extension(boolean bl);

   @Override
   public native void interrupt();

   @Override
   public synchronized native void busy_timeout(int i);

   @Override
   public synchronized native void busy_handler(BusyHandler busyHandler);

   @Override
   protected synchronized SafeStmtPtr prepare(String sql) throws SQLException {
      logger.trace(() -> MessageFormat.format("DriverManager [{0}] [SQLite EXEC] {1}", Thread.currentThread().getName(), sql));
      return new SafeStmtPtr(this, this.prepare_utf8(stringToUtf8ByteArray(sql)));
   }

   synchronized native long prepare_utf8(byte[] bs) throws SQLException;

   @Override
   synchronized String errmsg() {
      return utf8ByteBufferToString(this.errmsg_utf8());
   }

   synchronized native ByteBuffer errmsg_utf8();

   @Override
   public synchronized String libversion() {
      return utf8ByteBufferToString(this.libversion_utf8());
   }

   native ByteBuffer libversion_utf8();

   @Override
   public synchronized native long changes();

   @Override
   public synchronized native long total_changes();

   @Override
   protected synchronized native int finalize(long l);

   @Override
   public synchronized native int step(long l);

   @Override
   public synchronized native int reset(long l);

   @Override
   public synchronized native int clear_bindings(long l);

   @Override
   synchronized native int bind_parameter_count(long l);

   @Override
   public synchronized native int column_count(long l);

   @Override
   public synchronized native int column_type(long l, int i);

   @Override
   public synchronized String column_decltype(long stmt, int col) {
      return utf8ByteBufferToString(this.column_decltype_utf8(stmt, col));
   }

   synchronized native ByteBuffer column_decltype_utf8(long l, int i);

   @Override
   public synchronized String column_table_name(long stmt, int col) {
      return utf8ByteBufferToString(this.column_table_name_utf8(stmt, col));
   }

   synchronized native ByteBuffer column_table_name_utf8(long l, int i);

   @Override
   public synchronized String column_name(long stmt, int col) {
      return utf8ByteBufferToString(this.column_name_utf8(stmt, col));
   }

   synchronized native ByteBuffer column_name_utf8(long l, int i);

   @Override
   public synchronized String column_text(long stmt, int col) {
      return utf8ByteBufferToString(this.column_text_utf8(stmt, col));
   }

   synchronized native ByteBuffer column_text_utf8(long l, int i);

   @Override
   public synchronized native byte[] column_blob(long l, int i);

   @Override
   public synchronized native double column_double(long l, int i);

   @Override
   public synchronized native long column_long(long l, int i);

   @Override
   public synchronized native int column_int(long l, int i);

   @Override
   synchronized native int bind_null(long l, int i);

   @Override
   synchronized native int bind_int(long l, int i, int j);

   @Override
   synchronized native int bind_long(long l, int i, long m);

   @Override
   synchronized native int bind_double(long l, int i, double d);

   @Override
   synchronized int bind_text(long stmt, int pos, String v) {
      return this.bind_text_utf8(stmt, pos, stringToUtf8ByteArray(v));
   }

   synchronized native int bind_text_utf8(long l, int i, byte[] bs);

   @Override
   synchronized native int bind_blob(long l, int i, byte[] bs);

   @Override
   public synchronized native void result_null(long l);

   @Override
   public synchronized void result_text(long context, String val) {
      this.result_text_utf8(context, stringToUtf8ByteArray(val));
   }

   synchronized native void result_text_utf8(long l, byte[] bs);

   @Override
   public synchronized native void result_blob(long l, byte[] bs);

   @Override
   public synchronized native void result_double(long l, double d);

   @Override
   public synchronized native void result_long(long l, long m);

   @Override
   public synchronized native void result_int(long l, int i);

   @Override
   public synchronized void result_error(long context, String err) {
      this.result_error_utf8(context, stringToUtf8ByteArray(err));
   }

   synchronized native void result_error_utf8(long l, byte[] bs);

   @Override
   public synchronized String value_text(Function f, int arg) {
      return utf8ByteBufferToString(this.value_text_utf8(f, arg));
   }

   synchronized native ByteBuffer value_text_utf8(Function function, int i);

   @Override
   public synchronized native byte[] value_blob(Function function, int i);

   @Override
   public synchronized native double value_double(Function function, int i);

   @Override
   public synchronized native long value_long(Function function, int i);

   @Override
   public synchronized native int value_int(Function function, int i);

   @Override
   public synchronized native int value_type(Function function, int i);

   @Override
   public synchronized int create_function(String name, Function func, int nArgs, int flags) throws SQLException {
      return this.create_function_utf8(this.nameToUtf8ByteArray("function", name), func, nArgs, flags);
   }

   synchronized native int create_function_utf8(byte[] bs, Function function, int i, int j);

   @Override
   public synchronized int destroy_function(String name) throws SQLException {
      return this.destroy_function_utf8(this.nameToUtf8ByteArray("function", name));
   }

   synchronized native int destroy_function_utf8(byte[] bs);

   @Override
   public synchronized int create_collation(String name, Collation coll) throws SQLException {
      return this.create_collation_utf8(this.nameToUtf8ByteArray("collation", name), coll);
   }

   synchronized native int create_collation_utf8(byte[] bs, Collation collation);

   @Override
   public synchronized int destroy_collation(String name) throws SQLException {
      return this.destroy_collation_utf8(this.nameToUtf8ByteArray("collation", name));
   }

   synchronized native int destroy_collation_utf8(byte[] bs);

   @Override
   public synchronized native int limit(int i, int j) throws SQLException;

   private byte[] nameToUtf8ByteArray(String nameType, String name) throws SQLException {
      byte[] nameUtf8 = stringToUtf8ByteArray(name);
      if (name != null && !"".equals(name) && nameUtf8.length <= 255) {
         return nameUtf8;
      } else {
         throw new SQLException("invalid " + nameType + " name: '" + name + "'");
      }
   }

   @Override
   public int backup(String dbName, String destFileName, DB.ProgressObserver observer) throws SQLException {
      return this.backup(stringToUtf8ByteArray(dbName), stringToUtf8ByteArray(destFileName), observer, 100, 3, 100);
   }

   @Override
   public int backup(String dbName, String destFileName, DB.ProgressObserver observer, int sleepTimeMillis, int nTimeouts, int pagesPerStep) throws SQLException {
      return this.backup(stringToUtf8ByteArray(dbName), stringToUtf8ByteArray(destFileName), observer, sleepTimeMillis, nTimeouts, pagesPerStep);
   }

   synchronized native int backup(byte[] bs, byte[] cs, DB.ProgressObserver progressObserver, int i, int j, int k) throws SQLException;

   @Override
   public synchronized int restore(String dbName, String sourceFileName, DB.ProgressObserver observer) throws SQLException {
      return this.restore(dbName, sourceFileName, observer, 100, 3, 100);
   }

   @Override
   public synchronized int restore(String dbName, String sourceFileName, DB.ProgressObserver observer, int sleepTimeMillis, int nTimeouts, int pagesPerStep) throws SQLException {
      return this.restore(stringToUtf8ByteArray(dbName), stringToUtf8ByteArray(sourceFileName), observer, sleepTimeMillis, nTimeouts, pagesPerStep);
   }

   synchronized native int restore(byte[] bs, byte[] cs, DB.ProgressObserver progressObserver, int i, int j, int k) throws SQLException;

   @Override
   synchronized native boolean[][] column_metadata(long l);

   @Override
   synchronized native void set_commit_listener(boolean bl);

   @Override
   synchronized native void set_update_listener(boolean bl);

   static void throwex(String msg) throws SQLException {
      throw new SQLException(msg);
   }

   static byte[] stringToUtf8ByteArray(String str) {
      return str == null ? null : str.getBytes(StandardCharsets.UTF_8);
   }

   static String utf8ByteBufferToString(ByteBuffer buffer) {
      if (buffer == null) {
         return null;
      } else {
         byte[] buff = new byte[buffer.remaining()];
         buffer.get(buff);
         return new String(buff, StandardCharsets.UTF_8);
      }
   }

   @Override
   public synchronized native void register_progress_handler(int i, ProgressHandler progressHandler) throws SQLException;

   @Override
   public synchronized native void clear_progress_handler() throws SQLException;

   long getBusyHandler() {
      return this.busyHandler;
   }

   long getCommitListener() {
      return this.commitListener;
   }

   long getUpdateListener() {
      return this.updateListener;
   }

   long getProgressHandler() {
      return this.progressHandler;
   }

   @Override
   public synchronized native byte[] serialize(String string) throws SQLException;

   @Override
   public synchronized native void deserialize(String string, byte[] bs) throws SQLException;

   static {
      if ("The Android Project".equals(System.getProperty("java.vm.vendor"))) {
         System.loadLibrary("sqlitejdbc");
         isLoaded = true;
         loadSucceeded = true;
      } else {
         isLoaded = false;
         loadSucceeded = false;
      }
   }
}
