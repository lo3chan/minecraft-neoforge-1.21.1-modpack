package com.seibel.distanthorizons.core.sql.repo;

import com.seibel.distanthorizons.core.api.internal.ClientApi;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.sql.DatabaseUpdater;
import com.seibel.distanthorizons.core.sql.DbConnectionClosedException;
import com.seibel.distanthorizons.core.sql.DbCorruptedException;
import com.seibel.distanthorizons.core.sql.dto.IBaseDTO;
import com.seibel.distanthorizons.core.sql.repo.phantoms.AutoClosableTrackingWrapper;
import com.seibel.distanthorizons.core.util.KeyedLockContainer;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractDhRepo<TKey, TDTO extends IBaseDTO<TKey>> implements AutoCloseable {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   public static final String DEFAULT_DATABASE_TYPE = "jdbc:dh_sqlite";
   public static final int TIMEOUT_SECONDS = 0;
   private static final ConcurrentHashMap<String, Connection> CONNECTIONS_BY_CONNECTION_STRING = new ConcurrentHashMap<>();
   private static final ConcurrentHashMap<AbstractDhRepo<?, ?>, String> ACTIVE_CONNECTION_STRINGS_BY_REPO = new ConcurrentHashMap<>();
   private static final Set<String> CORRUPTED_DB_PATHS = Collections.newSetFromMap(new ConcurrentHashMap<>());
   private final String connectionString;
   private final Connection connection;
   public final String databaseType;
   public final File databaseFile;
   public final Set<AutoClosableTrackingWrapper> openClosables = ConcurrentHashMap.newKeySet();
   public final Class<? extends TDTO> dtoClass;
   protected final KeyedLockContainer<TKey> saveLockContainer = new KeyedLockContainer<>();
   private final AtomicBoolean databaseCorruptedRef = new AtomicBoolean(false);
   private String selectSqlTemplate = null;
   private String existsSqlTemplate = null;
   private String deleteSqlTemplate = null;

   public AbstractDhRepo(String databaseType, File databaseFile, Class<? extends TDTO> dtoClass) throws SQLException, IOException {
      this.databaseType = databaseType;
      this.databaseFile = databaseFile;
      this.dtoClass = dtoClass;

      try {
         Class.forName("dh_sqlite.JDBC");
      } catch (ClassNotFoundException var7) {
         throw new RuntimeException(var7);
      }

      if (!databaseFile.exists()) {
         File parentFolder = databaseFile.getParentFile();
         if (parentFolder != null && !parentFolder.exists() && !parentFolder.mkdirs()) {
            throw new IOException("Unable to create the necessary parent folders for the database file at location [" + databaseFile.getPath() + "].");
         }

         if (!databaseFile.exists()) {
            try {
               boolean e = databaseFile.createNewFile();
            } catch (IOException var6) {
               throw new IOException(
                  "Unable to create database file at location [" + databaseFile.getPath() + "] due to error: [" + var6.getMessage() + "]", var6
               );
            }
         }
      }

      if (!databaseFile.canRead()) {
         throw new IOException(
            "Unable to read database file at location [" + databaseFile.getPath() + "], please make sure the folder and file has the correct permissions."
         );
      } else if (!databaseFile.canWrite()) {
         throw new IOException(
            "Unable to write database file at location [" + databaseFile.getPath() + "], please make sure the folder and file aren't set to read-only."
         );
      } else {
         this.connectionString = this.databaseType + ":" + this.databaseFile.getPath();
         this.connection = CONNECTIONS_BY_CONNECTION_STRING.computeIfAbsent(this.connectionString, connectionString -> {
            try {
               return DriverManager.getConnection(connectionString);
            } catch (SQLException var2) {
               LOGGER.error("Unable to connect to database with the connection string: [" + connectionString + "]");
               return null;
            }
         });
         if (this.connection == null) {
            throw new SQLException("Unable to get repo with connection string [" + this.connectionString + "]");
         } else {
            ACTIVE_CONNECTION_STRINGS_BY_REPO.put(this, this.connectionString);
            DatabaseUpdater.runAutoUpdateScripts(this);
         }
      }
   }

   public TDTO getByKey(TKey primaryKey) {
      try {
         PreparedStatement statement = this.createSelectStatementByKey(primaryKey);

         IBaseDTO var4;
         label89: {
            try {
               ResultSet resultSet = this.query(statement);

               label91: {
                  try {
                     if (resultSet == null || !resultSet.next()) {
                        var4 = null;
                        break label91;
                     }

                     var4 = this.convertResultSetToDto(resultSet);
                  } catch (Throwable var8) {
                     if (resultSet != null) {
                        try {
                           resultSet.close();
                        } catch (Throwable var7) {
                           var8.addSuppressed(var7);
                        }
                     }

                     throw var8;
                  }

                  if (resultSet != null) {
                     resultSet.close();
                  }
                  break label89;
               }

               if (resultSet != null) {
                  resultSet.close();
               }
            } catch (Throwable var9) {
               if (statement != null) {
                  try {
                     statement.close();
                  } catch (Throwable var6) {
                     var9.addSuppressed(var6);
                  }
               }

               throw var9;
            }

            if (statement != null) {
               statement.close();
            }

            return (TDTO)var4;
         }

         if (statement != null) {
            statement.close();
         }

         return (TDTO)var4;
      } catch (IOException | SQLException var10) {
         if (!(var10 instanceof SQLException) || !DbConnectionClosedException.isClosedException(var10)) {
            LOGGER.warn(
               "Unexpected issue deserializing DTO ["
                  + this.dtoClass.getSimpleName()
                  + "] with primary key ["
                  + primaryKey
                  + "]. Error: ["
                  + var10.getMessage()
                  + "].",
               var10
            );
         }

         return null;
      }
   }

   public void save(TDTO dto) {
      ReentrantLock saveLock = this.saveLockContainer.getLockForPos(dto.getKey());

      try {
         saveLock.lock();
         if (this.existsWithKey(dto.getKey())) {
            this.update(dto);
         } else {
            this.insert(dto);
         }
      } finally {
         saveLock.unlock();
      }
   }

   private void insert(TDTO dto) {
      try {
         PreparedStatement statement = this.createInsertStatement(dto);

         try {
            ResultSet result = this.query(statement);
            if (result != null) {
               result.close();
            }
         } catch (Throwable var6) {
            if (statement != null) {
               try {
                  statement.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (statement != null) {
            statement.close();
         }
      } catch (DbConnectionClosedException var7) {
      } catch (SQLException var8) {
         String message = "Unexpected DTO insert error: [" + var8.getMessage() + "].";
         LOGGER.error(message);
         throw new RuntimeException(message, var8);
      }
   }

   private void update(TDTO dto) {
      try {
         PreparedStatement statement = this.createUpdateStatement(dto);

         try {
            ResultSet result = this.query(statement);
            if (result != null) {
               result.close();
            }
         } catch (Throwable var6) {
            if (statement != null) {
               try {
                  statement.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (statement != null) {
            statement.close();
         }
      } catch (DbConnectionClosedException var7) {
      } catch (SQLException var8) {
         String message = "Unexpected DTO update error: [" + var8.getMessage() + "].";
         LOGGER.error(message);
         throw new RuntimeException(message, var8);
      }
   }

   public void delete(TDTO dto) {
      this.deleteWithKey(dto.getKey());
   }

   public void deleteWithKey(TKey key) {
      try {
         PreparedStatement statement = this.createDeleteStatementByKey(key);

         try {
            ResultSet result = this.query(statement);
            if (result != null) {
               result.close();
            }
         } catch (Throwable var6) {
            if (statement != null) {
               try {
                  statement.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (statement != null) {
            statement.close();
         }
      } catch (SQLException var7) {
         throw new RuntimeException(var7);
      }
   }

   public void deleteAll() {
      String sql = "DELETE FROM " + this.getTableName();

      try {
         PreparedStatement statement = this.createPreparedStatement(sql);

         try {
            ResultSet result = this.query(statement);
            if (result != null) {
               result.close();
            }
         } catch (Throwable var6) {
            if (statement != null) {
               try {
                  statement.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (statement != null) {
            statement.close();
         }
      } catch (SQLException var7) {
         throw new RuntimeException(var7);
      }
   }

   public boolean exists(TDTO dto) {
      return this.existsWithKey(dto.getKey());
   }

   public boolean existsWithKey(TKey key) {
      try {
         PreparedStatement statement = this.createExistsStatementByKey(key);

         boolean var4;
         try {
            ResultSet result = this.query(statement);

            try {
               var4 = result != null && result.getInt("existingCount") != 0;
            } catch (Throwable var8) {
               if (result != null) {
                  try {
                     result.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }
               }

               throw var8;
            }

            if (result != null) {
               result.close();
            }
         } catch (Throwable var9) {
            if (statement != null) {
               try {
                  statement.close();
               } catch (Throwable var6) {
                  var9.addSuppressed(var6);
               }
            }

            throw var9;
         }

         if (statement != null) {
            statement.close();
         }

         return var4;
      } catch (SQLException var10) {
         return false;
      }
   }

   @Nullable
   public Map<String, Object> queryDictionaryFirst(String sql) {
      try {
         List<Map<String, Object>> objectList = this.queryDictionary(sql);
         return !objectList.isEmpty() ? objectList.get(0) : null;
      } catch (DbConnectionClosedException var3) {
         return null;
      }
   }

   private List<Map<String, Object>> queryDictionary(String sql) throws RuntimeException, DbConnectionClosedException {
      try {
         Statement statement = this.connection.createStatement();

         List var5;
         try {
            statement.setQueryTimeout(0);
            boolean resultSetPresent = statement.execute(sql);
            ResultSet resultSet = AutoClosableTrackingWrapper.wrap(ResultSet.class, statement.getResultSet(), this.openClosables);

            try {
               var5 = this.convertResultSetToDictionaryList(resultSet, resultSetPresent);
            } catch (Throwable var9) {
               if (resultSet != null) {
                  try {
                     resultSet.close();
                  } catch (Throwable var8) {
                     var9.addSuppressed(var8);
                  }
               }

               throw var9;
            }

            if (resultSet != null) {
               resultSet.close();
            }
         } catch (Throwable var10) {
            if (statement != null) {
               try {
                  statement.close();
               } catch (Throwable var7) {
                  var10.addSuppressed(var7);
               }
            }

            throw var10;
         }

         if (statement != null) {
            statement.close();
         }

         return var5;
      } catch (SQLException var11) {
         if (DbConnectionClosedException.isClosedException(var11)) {
            return new ArrayList<>();
         } else {
            String message = "Unexpected Query error: [" + var11.getMessage() + "], for script: [" + sql + "].";
            LOGGER.error(message, var11);
            throw new RuntimeException(message, var11);
         }
      }
   }

   @Nullable
   public ResultSet query(@Nullable PreparedStatement statement) throws RuntimeException {
      if (this.databaseCorruptedRef.get()) {
         return null;
      } else if (statement == null) {
         return null;
      } else {
         try {
            statement.setQueryTimeout(0);
            boolean resultSetPresent = statement.execute();
            if (resultSetPresent) {
               ResultSet resultSet = statement.getResultSet();
               return AutoClosableTrackingWrapper.wrap(ResultSet.class, resultSet, this.openClosables);
            } else {
               return null;
            }
         } catch (SQLException var6) {
            SQLException e = var6;
            if (DbConnectionClosedException.isClosedException(var6)) {
               return null;
            } else if (DbCorruptedException.isCorruptedException(var6)) {
               synchronized (this) {
                  if (!this.databaseCorruptedRef.getAndSet(true) && CORRUPTED_DB_PATHS.add(this.databaseFile.getPath())) {
                     LOGGER.error(
                        "DH database file at ["
                           + this.databaseFile.getPath()
                           + "] is corrupted. \nAll operations to this DB are disabled, DH may behave strangely if you continue playing. \nPlease leave the world and delete the corrupted database file to fix. \nError: ["
                           + e.getMessage()
                           + "]",
                        e
                     );
                     ClientApi.INSTANCE
                        .showChatMessageNextFrame(
                           "§4§lDH database is corrupted.§r\nDH will behave strangely if your continue playing. \nPlease leave the world and delete the corrupted database file at: \n§e"
                              + this.databaseFile.getPath()
                              + "§r"
                              + "\nto resolve the issue. \n"
                        );
                  }

                  return null;
               }
            } else {
               String message = "Unexpected Query error: [" + var6.getMessage() + "], for prepared statement: [" + statement + "].";
               LOGGER.error(message);
               throw new RuntimeException(message, var6);
            }
         }
      }
   }

   @Nullable
   public PreparedStatement createPreparedStatement(String sql) throws RuntimeException {
      try {
         PreparedStatement statement = this.connection.prepareStatement(sql);
         statement.setQueryTimeout(0);
         return AutoClosableTrackingWrapper.wrap(PreparedStatement.class, statement, this.openClosables);
      } catch (SQLException var4) {
         if (DbConnectionClosedException.isClosedException(var4)) {
            return null;
         } else {
            String message = "Unexpected error: [" + var4.getMessage() + "], preparing statement: [" + sql + "].";
            LOGGER.error(message);
            throw new RuntimeException(message, var4);
         }
      }
   }

   public Connection getConnection() {
      return this.connection;
   }

   public boolean isConnected() {
      try {
         return this.connection != null && this.connection.isClosed();
      } catch (SQLException var2) {
         return false;
      }
   }

   public static void closeAllConnections() {
      LOGGER.info("Closing all [" + ACTIVE_CONNECTION_STRINGS_BY_REPO.size() + "] database connections...");

      for (String connectionString : ACTIVE_CONNECTION_STRINGS_BY_REPO.values()) {
         try {
            Connection connection = CONNECTIONS_BY_CONNECTION_STRING.remove(connectionString);
            if (connection != null && !connection.isClosed()) {
               LOGGER.info("Closing database connection: [" + connectionString + "]");
               connection.close();
            }
         } catch (SQLException var3) {
            LOGGER.error("Unable to close the connection [" + connectionString + "], error: [" + var3.getMessage() + "]");
         }
      }

      CORRUPTED_DB_PATHS.clear();
   }

   @Override
   public void close() {
      try {
         ACTIVE_CONNECTION_STRINGS_BY_REPO.remove(this);
         if (!ACTIVE_CONNECTION_STRINGS_BY_REPO.containsValue(this.connectionString)) {
            if (this.connection != null) {
               CONNECTIONS_BY_CONNECTION_STRING.remove(this.connectionString);
               int openClosableCount = this.openClosables.size();
               if (openClosableCount != 0) {
                  LOGGER.warn(
                     "["
                        + openClosableCount
                        + "] objects not closed for repo ["
                        + this.getClass().getSimpleName()
                        + "]-["
                        + this.getTableName()
                        + "] with connection: ["
                        + this.connectionString
                        + "]. A memory leak may be present and closing this connection may take longer than normal."
                  );
                  StringBuilder stringBuilder = new StringBuilder();
                  stringBuilder.append("Unclosed objects: \n");
                  HashMap<String, AtomicInteger> unclosedObjectCountsByString = this.getUnclosedObjectStringsAndCounts();

                  for (String objString : unclosedObjectCountsByString.keySet()) {
                     AtomicInteger countRef = unclosedObjectCountsByString.get(objString);
                     if (countRef != null) {
                        stringBuilder.append("[" + countRef.get() + "] - [" + objString + "] \n");
                     }
                  }

                  LOGGER.warn(stringBuilder.toString());
               }

               if (!this.connection.isClosed()) {
                  LOGGER.info("Closing database connection: [" + this.connectionString + "]...");
                  this.connection.close();
                  LOGGER.info("Finished closing database connection: [" + this.connectionString + "]");
               }
            }

            ACTIVE_CONNECTION_STRINGS_BY_REPO.remove(this);
         }
      } catch (SQLException var7) {
         LOGGER.error("Unable to close the connection [" + this.connectionString + "], error: [" + var7.getMessage() + "]");
      }
   }

   private List<Map<String, Object>> convertResultSetToDictionaryList(ResultSet resultSet, boolean resultSetPresent) throws SQLException {
      if (resultSetPresent) {
         List<Map<String, Object>> resultList = convertResultSetToDictionaryList(resultSet);
         resultSet.close();
         return resultList;
      } else {
         if (resultSet != null) {
            resultSet.close();
         }

         return new ArrayList<>();
      }
   }

   private static List<Map<String, Object>> convertResultSetToDictionaryList(ResultSet resultSet) throws SQLException {
      List<Map<String, Object>> list = new ArrayList<>();
      ResultSetMetaData resultMetaData = resultSet.getMetaData();
      int resultColumnCount = resultMetaData.getColumnCount();

      while (resultSet.next()) {
         HashMap<String, Object> object = new HashMap<>();

         for (int columnIndex = 1; columnIndex <= resultColumnCount; columnIndex++) {
            String columnName = resultMetaData.getColumnName(columnIndex);
            if (columnName == null || columnName.isEmpty()) {
               throw new RuntimeException(
                  "SQL result set is missing a column name for column [" + resultMetaData.getTableName(columnIndex) + "." + columnIndex + "]."
               );
            }

            String columnType = resultMetaData.getColumnTypeName(columnIndex).toUpperCase();
            Object columnValue;
            switch (columnType) {
               case "BIGINT":
                  columnValue = resultSet.getLong(columnIndex);
                  break;
               case "SMALLINT":
                  columnValue = resultSet.getShort(columnIndex);
                  break;
               case "TINYINT":
                  columnValue = resultSet.getByte(columnIndex);
                  break;
               default:
                  columnValue = resultSet.getObject(columnIndex);
            }

            object.put(columnName, columnValue);
         }

         list.add(object);
      }

      return list;
   }

   public HashMap<String, AtomicInteger> getUnclosedObjectStringsAndCounts() {
      HashMap<String, AtomicInteger> closableCountsByToString = new HashMap<>();

      for (AutoClosableTrackingWrapper closableWrapper : this.openClosables) {
         String str = closableWrapper.wrappedClosable.getClass().getSimpleName();
         if (closableWrapper.wrappedClosable instanceof ResultSet) {
            str = str + " @ " + closableWrapper.wrappedClosable.toString();
         } else if (closableWrapper.wrappedClosable instanceof PreparedStatement) {
            String sql = closableWrapper.wrappedClosable.toString();
            int parametersIndex = sql.indexOf("\n parameters=");
            if (parametersIndex != -1) {
               sql = sql.substring(0, parametersIndex);
            }

            str = str + " @ " + sql;
         } else {
            str = str + " @ " + closableWrapper.wrappedClosable.toString();
         }

         closableCountsByToString.compute(str, (stringVal, countRef) -> {
            if (countRef == null) {
               countRef = new AtomicInteger(0);
            }

            countRef.incrementAndGet();
            return (AtomicInteger)countRef;
         });
      }

      return closableCountsByToString;
   }

   public abstract String getTableName();

   @Nullable
   public abstract TDTO convertResultSetToDto(ResultSet resultSet) throws ClassCastException, IOException, SQLException;

   protected abstract String CreateParameterizedWhereString();

   protected void setPreparedStatementWhereClause(PreparedStatement statement, TKey key) throws SQLException {
      this.setPreparedStatementWhereClause(statement, 1, key);
   }

   protected abstract int setPreparedStatementWhereClause(PreparedStatement preparedStatement, int i, TKey object) throws SQLException;

   public PreparedStatement createSelectStatementByKey(TKey key) throws SQLException {
      if (this.selectSqlTemplate == null) {
         this.selectSqlTemplate = "SELECT * FROM " + this.getTableName() + " WHERE " + this.CreateParameterizedWhereString();
      }

      PreparedStatement statement = this.createPreparedStatement(this.selectSqlTemplate);
      if (statement == null) {
         return null;
      } else {
         this.setPreparedStatementWhereClause(statement, key);
         return statement;
      }
   }

   public PreparedStatement createExistsStatementByKey(TKey key) throws SQLException {
      if (this.existsSqlTemplate == null) {
         this.existsSqlTemplate = "SELECT EXISTS(SELECT 1 FROM "
            + this.getTableName()
            + " WHERE "
            + this.CreateParameterizedWhereString()
            + ") as 'existingCount'";
      }

      PreparedStatement statement = this.createPreparedStatement(this.existsSqlTemplate);
      if (statement == null) {
         return null;
      } else {
         this.setPreparedStatementWhereClause(statement, key);
         return statement;
      }
   }

   public PreparedStatement createDeleteStatementByKey(TKey key) throws SQLException {
      if (this.deleteSqlTemplate == null) {
         this.deleteSqlTemplate = "DELETE FROM " + this.getTableName() + " WHERE " + this.CreateParameterizedWhereString();
      }

      PreparedStatement statement = this.createPreparedStatement(this.deleteSqlTemplate);
      if (statement == null) {
         return null;
      } else {
         this.setPreparedStatementWhereClause(statement, key);
         return statement;
      }
   }

   @Nullable
   public abstract PreparedStatement createInsertStatement(TDTO iBaseDTO) throws SQLException;

   @Nullable
   public abstract PreparedStatement createUpdateStatement(TDTO iBaseDTO) throws SQLException;
}
