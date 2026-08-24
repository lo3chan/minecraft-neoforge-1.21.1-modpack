package com.seibel.distanthorizons.core.sql;

import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.sql.dto.IBaseDTO;
import com.seibel.distanthorizons.core.sql.repo.AbstractDhRepo;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public class DatabaseUpdater {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   public static final String SCHEMA_TABLE_NAME = "Schema";
   public static final String UPDATE_SCRIPT_BATCH_SEPARATOR = "--batch--";
   public static final String UPDATE_SCRIPT_NO_TRANSACTION_FLAG = "--No Transactions--";
   private static final String SQL_SCRIPT_RESOURCE_FOLDER = "sqlScripts/";
   private static final String SQL_SCRIPT_LIST_FILE = "sqlScripts/scriptList.txt";

   public static <TKey, TDTO extends IBaseDTO<TKey>> void runAutoUpdateScripts(AbstractDhRepo<TKey, TDTO> repo) throws SQLException {
      ArrayList<DatabaseUpdater.SqlScript> scriptList;
      try {
         scriptList = getAutoUpdateScripts();
      } catch (IOException var16) {
         LOGGER.error("Get auto update SQL scripts failed. Error: " + var16.getMessage(), var16);
         return;
      }

      Map<String, Object> schemaTableExistsResult = repo.queryDictionaryFirst(
         "SELECT COUNT(name) as 'tableCount' FROM sqlite_master WHERE type='table' AND name='Schema';"
      );
      if (schemaTableExistsResult == null || (Integer)schemaTableExistsResult.get("tableCount") == 0) {
         String createBaseSchemaTable = "CREATE TABLE Schema ( \n    SchemaVersionId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, \n    ScriptName TEXT NOT NULL UNIQUE, \n    AppliedDateTime DATETIME NOT NULL default CURRENT_TIMESTAMP \n)";
         repo.queryDictionaryFirst(createBaseSchemaTable);
      }

      for (DatabaseUpdater.SqlScript resource : scriptList) {
         Map<String, Object> scriptAlreadyRunResult = repo.queryDictionaryFirst(
            "SELECT EXISTS(SELECT 1 FROM Schema WHERE ScriptName='" + resource.name + "') as 'existingCount';"
         );
         if (scriptAlreadyRunResult != null && (Integer)scriptAlreadyRunResult.get("existingCount") == 0) {
            LOGGER.info("Running SQL update script: [" + resource.name + "], for repo: [" + repo.databaseFile + "]");
            int sqlIndex = 0;

            try {
               String[] fileUpdateSqlArray = resource.queryString.split("--batch--");
               boolean transactScript = !resource.queryString.contains("--No Transactions--");
               Connection connection = repo.getConnection();
               connection.setAutoCommit(!transactScript);

               try {
                  Statement statement = connection.createStatement();

                  try {
                     statement.setQueryTimeout(0);

                     for (String updateSql : fileUpdateSqlArray) {
                        statement.execute(updateSql);
                     }

                     if (transactScript) {
                        connection.commit();
                     }
                  } catch (Throwable var17) {
                     if (statement != null) {
                        try {
                           statement.close();
                        } catch (Throwable var15) {
                           var17.addSuppressed(var15);
                        }
                     }

                     throw var17;
                  }

                  if (statement != null) {
                     statement.close();
                  }
               } catch (SQLException var18) {
                  LOGGER.error(
                     "Unexpected SQL Error: ["
                        + var18.getMessage()
                        + "] \nreturned for auto update script: ["
                        + resource.name
                        + "], \nquery: ["
                        + fileUpdateSqlArray[sqlIndex]
                        + "]. \nChanges should have been rolled back.",
                     new SQLException()
                  );
                  if (transactScript) {
                     connection.rollback();
                  }

                  throw var18;
               }

               if (transactScript) {
                  connection.setAutoCommit(true);
               }
            } catch (RuntimeException var19) {
               LOGGER.error(
                  "Unexpected error running database update script ["
                     + resource.name
                     + "] on database ["
                     + repo.databaseFile
                     + "], stopping database update. Database reading/writing may fail if you continue. \nError: ["
                     + var19.getMessage()
                     + "]. \nSql Script:["
                     + resource.queryString
                     + "]",
                  var19
               );
               throw var19;
            }

            repo.queryDictionaryFirst("INSERT INTO Schema (ScriptName) VALUES('" + resource.name + "');");
         }
      }
   }

   private static ArrayList<DatabaseUpdater.SqlScript> getAutoUpdateScripts() throws NullPointerException, IOException {
      ClassLoader loader = DatabaseUpdater.class.getClassLoader();
      InputStream scriptListInputStream = loader.getResourceAsStream("sqlScripts/scriptList.txt");

      String scriptListString;
      try {
         if (scriptListInputStream == null) {
            throw new NullPointerException("Failed to find the SQL Script list file [sqlScripts/scriptList.txt], no auto update scripts can be run.");
         }

         Scanner scanner = new Scanner(scriptListInputStream).useDelimiter("\\A");

         try {
            scriptListString = scanner.hasNext() ? scanner.next() : "";
         } catch (Throwable var17) {
            if (scanner != null) {
               try {
                  scanner.close();
               } catch (Throwable var15) {
                  var17.addSuppressed(var15);
               }
            }

            throw var17;
         }

         if (scanner != null) {
            scanner.close();
         }
      } catch (Throwable var19) {
         if (scriptListInputStream != null) {
            try {
               scriptListInputStream.close();
            } catch (Throwable var14) {
               var19.addSuppressed(var14);
            }
         }

         throw var19;
      }

      if (scriptListInputStream != null) {
         scriptListInputStream.close();
      }

      ArrayList<DatabaseUpdater.SqlScript> scriptList = new ArrayList<>();
      String[] sqlScriptNames = scriptListString.split("\n");

      for (String scriptName : sqlScriptNames) {
         scriptName = scriptName.trim();
         if (!scriptName.isEmpty()) {
            scriptName = "sqlScripts/" + scriptName.trim();
            InputStream scriptInputStream = loader.getResourceAsStream(scriptName);

            try {
               if (scriptInputStream == null) {
                  throw new NullPointerException("Failed to find the SQL Script file [" + scriptName + "], no auto update scripts can be run.");
               }

               Scanner fileScanner = new Scanner(scriptInputStream).useDelimiter("\\A");

               try {
                  scriptListString = fileScanner.hasNext() ? fileScanner.next() : "";
               } catch (Throwable var16) {
                  if (fileScanner != null) {
                     try {
                        fileScanner.close();
                     } catch (Throwable var13) {
                        var16.addSuppressed(var13);
                     }
                  }

                  throw var16;
               }

               if (fileScanner != null) {
                  fileScanner.close();
               }

               scriptList.add(new DatabaseUpdater.SqlScript(scriptName, scriptListString));
            } catch (Throwable var18) {
               if (scriptInputStream != null) {
                  try {
                     scriptInputStream.close();
                  } catch (Throwable var12) {
                     var18.addSuppressed(var12);
                  }
               }

               throw var18;
            }

            if (scriptInputStream != null) {
               scriptInputStream.close();
            }
         }
      }

      return scriptList;
   }

   public static int getAutoUpdateScriptCount() throws NullPointerException, IOException {
      return getAutoUpdateScripts().size();
   }

   private static class SqlScript {
      public String name;
      public String queryString;

      public SqlScript(String name, String queryString) {
         this.name = name;
         this.queryString = queryString;
      }
   }
}
