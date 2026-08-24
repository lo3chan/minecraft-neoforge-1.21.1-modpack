package com.seibel.distanthorizons.core.sql.repo;

import com.seibel.distanthorizons.api.enums.worldGeneration.EDhApiWorldGenerationStep;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.sql.dto.FullDataSourceV1DTO;
import com.seibel.distanthorizons.coreapi.util.StringUtil;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

public class FullDataSourceV1Repo extends AbstractDhRepo<Long, FullDataSourceV1DTO> {
   public static final String TABLE_NAME = "Legacy_FullData_V1";
   private final String insertSqlTemplate = "INSERT INTO "
      + this.getTableName()
      + "\n  (DhSectionPos, \nChecksum, DataVersion, DataDetailLevel, WorldGenStep, DataType, BinaryDataFormatVersion, \nData) \n   VALUES( \n    ? \n   ,? ,? ,? ,? ,? ,? \n   ,? \n);";
   private final String updateSqlTemplate = "UPDATE "
      + this.getTableName()
      + " \nSET \n    Checksum = ? \n   ,DataVersion = ? \n   ,DataDetailLevel = ? \n   ,WorldGenStep = ? \n   ,DataType = ? \n   ,BinaryDataFormatVersion = ? \n   ,Data = ? \n   ,LastModifiedDateTime = CURRENT_TIMESTAMP \nWHERE DhSectionPos = ?";
   private final String getMigrationPositionsSqlTemplate = "SELECT DhSectionPos FROM " + this.getTableName() + " WHERE MigrationFailed <> 1 LIMIT ?;";
   private final String getUnusedPositionSqlTemplate = "SELECT DhSectionPos FROM "
      + this.getTableName()
      + " WHERE DataDetailLevel <> 0 OR DataType <> 'CompleteFullDataSource' LIMIT ?";

   public FullDataSourceV1Repo(String databaseType, File databaseFile) throws SQLException, IOException {
      super(databaseType, databaseFile, FullDataSourceV1DTO.class);
   }

   @Override
   public String getTableName() {
      return "Legacy_FullData_V1";
   }

   @Override
   protected String CreateParameterizedWhereString() {
      return "DhSectionPos = ?";
   }

   protected int setPreparedStatementWhereClause(PreparedStatement statement, int index, Long pos) throws SQLException {
      statement.setString(index++, serializeSectionPos(pos));
      return index;
   }

   @Nullable
   public FullDataSourceV1DTO convertResultSetToDto(ResultSet resultSet) throws ClassCastException, SQLException {
      String posString = resultSet.getString("DhSectionPos");
      Long pos = deserializeSectionPos(posString);
      int checksum = resultSet.getInt("Checksum");
      byte dataDetailLevel = resultSet.getByte("DataDetailLevel");
      String worldGenStepString = resultSet.getString("WorldGenStep");
      EDhApiWorldGenerationStep worldGenStep = EDhApiWorldGenerationStep.fromName(worldGenStepString);
      String dataType = resultSet.getString("DataType");
      byte binaryDataFormatVersion = resultSet.getByte("BinaryDataFormatVersion");
      byte[] dataByteArray = resultSet.getBytes("Data");
      return new FullDataSourceV1DTO(pos, checksum, dataDetailLevel, worldGenStep, dataType, binaryDataFormatVersion, dataByteArray);
   }

   public PreparedStatement createInsertStatement(FullDataSourceV1DTO dto) throws SQLException {
      PreparedStatement statement = this.createPreparedStatement(this.insertSqlTemplate);
      if (statement == null) {
         return null;
      } else {
         int i = 1;
         statement.setString(i++, serializeSectionPos(dto.pos));
         statement.setInt(i++, dto.checksum);
         statement.setInt(i++, 0);
         statement.setByte(i++, dto.dataDetailLevel);
         statement.setObject(i++, dto.worldGenStep);
         statement.setString(i++, dto.dataType);
         statement.setByte(i++, dto.binaryDataFormatVersion);
         statement.setObject(i++, dto.dataArray);
         return statement;
      }
   }

   public PreparedStatement createUpdateStatement(FullDataSourceV1DTO dto) throws SQLException {
      PreparedStatement statement = this.createPreparedStatement(this.updateSqlTemplate);
      if (statement == null) {
         return null;
      } else {
         int i = 1;
         statement.setInt(i++, dto.checksum);
         statement.setInt(i++, 0);
         statement.setByte(i++, dto.dataDetailLevel);
         statement.setObject(i++, dto.worldGenStep);
         statement.setString(i++, dto.dataType);
         statement.setByte(i++, dto.binaryDataFormatVersion);
         statement.setObject(i++, dto.dataArray);
         statement.setString(i++, serializeSectionPos(dto.pos));
         return statement;
      }
   }

   public long getMigrationCount() {
      Map<String, Object> resultMap = this.queryDictionaryFirst("select COUNT(*) as itemCount from " + this.getTableName() + " where MigrationFailed <> 1");
      if (resultMap == null) {
         return 0L;
      } else {
         Number resultNumber = (Number)resultMap.get("itemCount");
         return resultNumber.longValue();
      }
   }

   public LongArrayList getPositionsToMigrate(int returnCount) {
      LongArrayList posList = new LongArrayList();

      try {
         PreparedStatement statement = this.createPreparedStatement(this.getMigrationPositionsSqlTemplate);

         LongArrayList var14;
         label80: {
            try {
               if (statement == null) {
                  var14 = posList;
                  break label80;
               }

               int i = 1;
               statement.setInt(i++, returnCount);
               ResultSet result = this.query(statement);

               try {
                  while (result != null && result.next()) {
                     String posString = result.getString("DhSectionPos");
                     Long sectionPos = deserializeSectionPos(posString);
                     if (sectionPos != null) {
                        posList.add(sectionPos);
                     }
                  }
               } catch (Throwable var10) {
                  if (result != null) {
                     try {
                        result.close();
                     } catch (Throwable var9) {
                        var10.addSuppressed(var9);
                     }
                  }

                  throw var10;
               }

               if (result != null) {
                  result.close();
               }
            } catch (Throwable var11) {
               if (statement != null) {
                  try {
                     statement.close();
                  } catch (Throwable var8) {
                     var11.addSuppressed(var8);
                  }
               }

               throw var11;
            }

            if (statement != null) {
               statement.close();
            }

            return posList;
         }

         if (statement != null) {
            statement.close();
         }

         return var14;
      } catch (Exception var12) {
         throw new RuntimeException(var12);
      }
   }

   public void markMigrationFailed(long pos) {
      String sql = "UPDATE " + this.getTableName() + " \nSET MigrationFailed = 1 \nWHERE DhSectionPos = '" + serializeSectionPos(pos) + "'";
      this.queryDictionaryFirst(sql);
   }

   public long getUnusedDataSourceCount() {
      Map<String, Object> resultMap = this.queryDictionaryFirst(
         "select Count(*) as unusedCount from " + this.getTableName() + " where DataDetailLevel <> 0 or DataType <> 'CompleteFullDataSource'"
      );
      if (resultMap != null) {
         Number resultNumber = (Number)resultMap.get("unusedCount");
         return resultNumber.longValue();
      } else {
         return 0L;
      }
   }

   public ArrayList<String> getUnusedDataSourcePositionStringList(int limit) {
      ArrayList<String> deletePosList = new ArrayList<>();

      try {
         PreparedStatement statement = this.createPreparedStatement(this.getUnusedPositionSqlTemplate);

         ArrayList var14;
         label77: {
            try {
               if (statement == null) {
                  var14 = deletePosList;
                  break label77;
               }

               int i = 1;
               statement.setInt(i++, limit);
               ResultSet result = this.query(statement);

               try {
                  while (result != null && result.next()) {
                     String posString = result.getString("DhSectionPos");
                     deletePosList.add("'" + posString + "'");
                  }
               } catch (Throwable var10) {
                  if (result != null) {
                     try {
                        result.close();
                     } catch (Throwable var9) {
                        var10.addSuppressed(var9);
                     }
                  }

                  throw var10;
               }

               if (result != null) {
                  result.close();
               }
            } catch (Throwable var11) {
               if (statement != null) {
                  try {
                     statement.close();
                  } catch (Throwable var8) {
                     var11.addSuppressed(var8);
                  }
               }

               throw var11;
            }

            if (statement != null) {
               statement.close();
            }

            return deletePosList;
         }

         if (statement != null) {
            statement.close();
         }

         return var14;
      } catch (SQLException var12) {
         throw new RuntimeException(var12);
      }
   }

   public void deleteUnusedLegacyData(ArrayList<String> deletePosList) {
      String sectionPosCsv = StringUtil.join(",", deletePosList);
      this.queryDictionaryFirst("delete from " + this.getTableName() + " where DhSectionPos in (" + sectionPosCsv + ")");
   }

   private static String serializeSectionPos(long pos) {
      return "[" + DhSectionPos.getDetailLevel(pos) + ',' + DhSectionPos.getX(pos) + ',' + DhSectionPos.getZ(pos) + ']';
   }

   @Nullable
   private static Long deserializeSectionPos(String value) {
      if (value.charAt(0) == '[' && value.charAt(value.length() - 1) == ']') {
         String[] split = value.substring(1, value.length() - 1).split(",");
         return split.length != 3 ? null : DhSectionPos.encode(Byte.parseByte(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
      } else {
         return null;
      }
   }
}
