package com.seibel.distanthorizons.core.sql.repo;

import com.seibel.distanthorizons.api.enums.config.EDhApiDataCompressionMode;
import com.seibel.distanthorizons.core.enums.EDhDirection;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.sql.DbConnectionClosedException;
import com.seibel.distanthorizons.core.sql.dto.FullDataSourceV2DTO;
import com.seibel.distanthorizons.core.util.BoolUtil;
import com.seibel.distanthorizons.core.util.ListUtil;
import com.seibel.distanthorizons.core.util.objects.dataStreams.DhDataInputStream;
import com.seibel.distanthorizons.core.util.objects.pooling.PhantomArrayList.PhantomArrayListCheckout;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

public class FullDataSourceV2Repo extends AbstractDhRepo<Long, FullDataSourceV2DTO> {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private final String insertSqlTemplate = "INSERT INTO "
      + this.getTableName()
      + " (\n   DetailLevel, PosX, PosZ, \n   MinY, DataChecksum, \n   Data, ColumnGenerationStep, ColumnWorldCompressionMode, Mapping, \n   NorthAdjData, SouthAdjData, EastAdjData, WestAdjData, \n   DataFormatVersion, CompressionMode, ApplyToParent, ApplyToChildren, \n   LastModifiedUnixDateTime, CreatedUnixDateTime) \nVALUES( \n    ?, ?, ?, \n    ?, ?, \n    ?, ?, ?, ?, \n    ?, ?, ?, ?, \n    ?, ?, ?, ?, \n    ?, ? \n);";
   private final String getAdjForDirectionSqlTemplate = "SELECT \n   DataChecksum, \n   ColumnGenerationStep, ColumnWorldCompressionMode, Mapping, \n   DataFormatVersion, CompressionMode, ApplyToParent, ApplyToChildren, \n   LastModifiedUnixDateTime, CreatedUnixDateTime, \n   DIRECTION_ENUM as AdjData \nFROM "
      + this.getTableName()
      + "\n   WHERE DetailLevel = ? AND PosX = ? AND PosZ = ?; \n";
   private final String getAdjForNorthDirTemplate = this.getAdjForDirectionSqlTemplate.replace("DIRECTION_ENUM", "NorthAdjData");
   private final String getAdjForSouthDirTemplate = this.getAdjForDirectionSqlTemplate.replace("DIRECTION_ENUM", "SouthAdjData");
   private final String getAdjForEastDirTemplate = this.getAdjForDirectionSqlTemplate.replace("DIRECTION_ENUM", "EastAdjData");
   private final String getAdjForWestDirTemplate = this.getAdjForDirectionSqlTemplate.replace("DIRECTION_ENUM", "WestAdjData");
   private final String setApplyToParentSql = "UPDATE " + this.getTableName() + " \nSET ApplyToParent = ? \nWHERE DetailLevel = ? AND PosX = ? AND PosZ = ?";
   private final String setApplyToChildrenSql = "UPDATE "
      + this.getTableName()
      + " \nSET ApplyToChildren = ? \nWHERE DetailLevel = ? AND PosX = ? AND PosZ = ?";
   private final String getParentPositionsToUpdateSql = "SELECT DetailLevel, PosX, PosZ,    abs((PosX << (6 + DetailLevel)) - ?) + abs((PosZ << (6 + DetailLevel)) - ?) AS Distance FROM "
      + this.getTableName()
      + " WHERE ApplyToParent = 1 ORDER BY Distance ASC LIMIT ?; ";
   private final String getChildPositionsToUpdateSql = "SELECT DetailLevel, PosX, PosZ,    abs((PosX << (6 + DetailLevel)) - ?) + abs((PosZ << (6 + DetailLevel)) - ?) AS Distance FROM "
      + this.getTableName()
      + " WHERE ApplyToChildren = 1 ORDER BY Distance ASC LIMIT ?; ";
   private final String getColumnGenerationStepSql = "select ColumnGenerationStep, CompressionMode from "
      + this.getTableName()
      + " WHERE DetailLevel = ? AND PosX = ? AND PosZ = ?";
   private final String getTimestampForPosSql = "SELECT LastModifiedUnixDateTime FROM "
      + this.getTableName()
      + " WHERE DetailLevel = ? AND PosX = ? AND PosZ = ?;";
   private final String getTimestampForRangeSql = "SELECT PosX, PosZ, LastModifiedUnixDateTime FROM "
      + this.getTableName()
      + " WHERE DetailLevel = ? AND PosX BETWEEN ? AND ? AND PosZ BETWEEN ? AND ?;";
   private final String getAllPositionsSql = "select DetailLevel, PosX, PosZ from " + this.getTableName() + "; ";
   private final String getDataSizeInBytesSql = "select LENGTH(Data) as dataSize from "
      + this.getTableName()
      + " WHERE DetailLevel = ? AND PosX = ? AND PosZ = ?";
   private final String getTotalDataSizeInBytesSql = "select SUM(LENGTH(Data)) as dataSize from " + this.getTableName() + "; ";

   public FullDataSourceV2Repo(String databaseType, File databaseFile) throws SQLException, IOException {
      super(databaseType, databaseFile, FullDataSourceV2DTO.class);
   }

   @Override
   public String getTableName() {
      return "FullData";
   }

   @Override
   protected String CreateParameterizedWhereString() {
      return "DetailLevel = ? AND PosX = ? AND PosZ = ?";
   }

   protected int setPreparedStatementWhereClause(PreparedStatement statement, int index, Long pos) throws SQLException {
      int detailLevel = DhSectionPos.getDetailLevel(pos) - 6;
      statement.setInt(index++, detailLevel);
      statement.setInt(index++, DhSectionPos.getX(pos));
      statement.setInt(index++, DhSectionPos.getZ(pos));
      return index;
   }

   @Nullable
   public FullDataSourceV2DTO convertResultSetToDto(ResultSet resultSet) throws ClassCastException, IOException, SQLException {
      return this.convertResultSetToDto(resultSet, true);
   }

   public FullDataSourceV2DTO convertResultSetToDto(ResultSet resultSet, boolean includeAdjacent) throws ClassCastException, IOException, SQLException {
      byte detailLevel = resultSet.getByte("DetailLevel");
      byte sectionDetailLevel = (byte)(detailLevel + 6);
      int posX = resultSet.getInt("PosX");
      int posZ = resultSet.getInt("PosZ");
      long pos = DhSectionPos.encode(sectionDetailLevel, posX, posZ);
      int dataChecksum = resultSet.getInt("DataChecksum");
      byte dataFormatVersion = resultSet.getByte("DataFormatVersion");
      byte compressionModeValue = resultSet.getByte("CompressionMode");
      boolean applyToParent = resultSet.getInt("ApplyToParent") == 1;
      boolean applyToChildren = resultSet.getInt("ApplyToChildren") == 1;
      long lastModifiedUnixDateTime = resultSet.getLong("LastModifiedUnixDateTime");
      long createdUnixDateTime = resultSet.getLong("CreatedUnixDateTime");
      FullDataSourceV2DTO dto = FullDataSourceV2DTO.CreateEmptyDataSourceForDecoding();
      dto.compressedDataByteArray = putAllBytes(resultSet.getBinaryStream("Data"), dto.compressedDataByteArray);
      dto.compressedColumnGenStepByteArray = putAllBytes(resultSet.getBinaryStream("ColumnGenerationStep"), dto.compressedColumnGenStepByteArray);
      dto.compressedWorldCompressionModeByteArray = putAllBytes(
         resultSet.getBinaryStream("ColumnWorldCompressionMode"), dto.compressedWorldCompressionModeByteArray
      );
      dto.compressedMappingByteArray = putAllBytes(resultSet.getBinaryStream("Mapping"), dto.compressedMappingByteArray);
      if (includeAdjacent) {
         dto.compressedNorthAdjDataByteArray = putAllBytes(resultSet.getBinaryStream("NorthAdjData"), dto.compressedNorthAdjDataByteArray);
         dto.compressedSouthAdjDataByteArray = putAllBytes(resultSet.getBinaryStream("SouthAdjData"), dto.compressedSouthAdjDataByteArray);
         dto.compressedEastAdjDataByteArray = putAllBytes(resultSet.getBinaryStream("EastAdjData"), dto.compressedEastAdjDataByteArray);
         dto.compressedWestAdjDataByteArray = putAllBytes(resultSet.getBinaryStream("WestAdjData"), dto.compressedWestAdjDataByteArray);
      }

      dto.pos = pos;
      dto.dataChecksum = dataChecksum;
      dto.dataFormatVersion = dataFormatVersion;
      dto.compressionModeValue = compressionModeValue;
      dto.lastModifiedUnixDateTime = lastModifiedUnixDateTime;
      dto.createdUnixDateTime = createdUnixDateTime;
      dto.applyToParent = applyToParent;
      dto.applyToChildren = applyToChildren;
      return dto;
   }

   @Nullable
   public FullDataSourceV2DTO convertResultSetToAdjDto(long pos, ResultSet resultSet) throws ClassCastException, IOException, SQLException {
      int dataChecksum = resultSet.getInt("DataChecksum");
      byte dataFormatVersion = resultSet.getByte("DataFormatVersion");
      byte compressionModeValue = resultSet.getByte("CompressionMode");
      boolean applyToParent = resultSet.getInt("ApplyToParent") == 1;
      boolean applyToChildren = resultSet.getInt("ApplyToChildren") == 1;
      long lastModifiedUnixDateTime = resultSet.getLong("LastModifiedUnixDateTime");
      long createdUnixDateTime = resultSet.getLong("CreatedUnixDateTime");
      FullDataSourceV2DTO dto = FullDataSourceV2DTO.CreateEmptyDataSourceForDecoding();
      dto.compressedDataByteArray = putAllBytes(resultSet.getBinaryStream("AdjData"), dto.compressedDataByteArray);
      dto.compressedColumnGenStepByteArray = putAllBytes(resultSet.getBinaryStream("ColumnGenerationStep"), dto.compressedColumnGenStepByteArray);
      dto.compressedWorldCompressionModeByteArray = putAllBytes(
         resultSet.getBinaryStream("ColumnWorldCompressionMode"), dto.compressedWorldCompressionModeByteArray
      );
      dto.compressedMappingByteArray = putAllBytes(resultSet.getBinaryStream("Mapping"), dto.compressedMappingByteArray);
      dto.pos = pos;
      dto.dataChecksum = dataChecksum;
      dto.dataFormatVersion = dataFormatVersion;
      dto.compressionModeValue = compressionModeValue;
      dto.lastModifiedUnixDateTime = lastModifiedUnixDateTime;
      dto.createdUnixDateTime = createdUnixDateTime;
      dto.applyToParent = applyToParent;
      dto.applyToChildren = applyToChildren;
      return dto;
   }

   public PreparedStatement createInsertStatement(FullDataSourceV2DTO dto) throws SQLException {
      PreparedStatement statement = this.createPreparedStatement(this.insertSqlTemplate);
      if (statement == null) {
         return null;
      } else {
         int i = 1;
         statement.setInt(i++, DhSectionPos.getDetailLevel(dto.pos) - 6);
         statement.setInt(i++, DhSectionPos.getX(dto.pos));
         statement.setInt(i++, DhSectionPos.getZ(dto.pos));
         statement.setInt(i++, 0);
         statement.setInt(i++, dto.dataChecksum);
         statement.setBinaryStream(i++, new ByteArrayInputStream(dto.compressedDataByteArray.elements()), dto.compressedDataByteArray.size());
         statement.setBinaryStream(i++, new ByteArrayInputStream(dto.compressedColumnGenStepByteArray.elements()), dto.compressedColumnGenStepByteArray.size());
         statement.setBinaryStream(
            i++, new ByteArrayInputStream(dto.compressedWorldCompressionModeByteArray.elements()), dto.compressedWorldCompressionModeByteArray.size()
         );
         statement.setBinaryStream(i++, new ByteArrayInputStream(dto.compressedMappingByteArray.elements()), dto.compressedMappingByteArray.size());
         statement.setBinaryStream(i++, new ByteArrayInputStream(dto.compressedNorthAdjDataByteArray.elements()), dto.compressedNorthAdjDataByteArray.size());
         statement.setBinaryStream(i++, new ByteArrayInputStream(dto.compressedSouthAdjDataByteArray.elements()), dto.compressedSouthAdjDataByteArray.size());
         statement.setBinaryStream(i++, new ByteArrayInputStream(dto.compressedEastAdjDataByteArray.elements()), dto.compressedEastAdjDataByteArray.size());
         statement.setBinaryStream(i++, new ByteArrayInputStream(dto.compressedWestAdjDataByteArray.elements()), dto.compressedWestAdjDataByteArray.size());
         statement.setByte(i++, dto.dataFormatVersion);
         statement.setByte(i++, dto.compressionModeValue);
         statement.setBoolean(i++, BoolUtil.falseIfNull(dto.applyToParent));
         statement.setBoolean(i++, BoolUtil.falseIfNull(dto.applyToChildren));
         statement.setLong(i++, System.currentTimeMillis());
         statement.setLong(i++, System.currentTimeMillis());
         return statement;
      }
   }

   public PreparedStatement createUpdateStatement(FullDataSourceV2DTO dto) throws SQLException {
      String updateSqlTemplate = ("UPDATE "
            + this.getTableName()
            + " \nSET \n   DataChecksum = ? \n   ,Data = ? \n   ,ColumnGenerationStep = ? \n   ,ColumnWorldCompressionMode = ? \n   ,Mapping = ? \n   ,NorthAdjData = ?, SouthAdjData = ?, EastAdjData = ?, WestAdjData = ? \n   ,DataFormatVersion = ? \n   ,CompressionMode = ? \n"
            + (dto.applyToParent != null ? "   ,ApplyToParent = ? \n" : "")
            + (dto.applyToChildren != null ? "   ,ApplyToChildren = ? \n" : "")
            + "   ,LastModifiedUnixDateTime = ? \n   ,CreatedUnixDateTime = ? \nWHERE DetailLevel = ? AND PosX = ? AND PosZ = ?")
         .intern();
      PreparedStatement statement = this.createPreparedStatement(updateSqlTemplate);
      if (statement == null) {
         return null;
      } else {
         int i = 1;
         statement.setInt(i++, dto.dataChecksum);
         statement.setBinaryStream(i++, new ByteArrayInputStream(dto.compressedDataByteArray.elements()), dto.compressedDataByteArray.size());
         statement.setBinaryStream(i++, new ByteArrayInputStream(dto.compressedColumnGenStepByteArray.elements()), dto.compressedColumnGenStepByteArray.size());
         statement.setBinaryStream(
            i++, new ByteArrayInputStream(dto.compressedWorldCompressionModeByteArray.elements()), dto.compressedWorldCompressionModeByteArray.size()
         );
         statement.setBinaryStream(i++, new ByteArrayInputStream(dto.compressedMappingByteArray.elements()), dto.compressedMappingByteArray.size());
         statement.setBinaryStream(i++, new ByteArrayInputStream(dto.compressedNorthAdjDataByteArray.elements()), dto.compressedNorthAdjDataByteArray.size());
         statement.setBinaryStream(i++, new ByteArrayInputStream(dto.compressedSouthAdjDataByteArray.elements()), dto.compressedSouthAdjDataByteArray.size());
         statement.setBinaryStream(i++, new ByteArrayInputStream(dto.compressedEastAdjDataByteArray.elements()), dto.compressedEastAdjDataByteArray.size());
         statement.setBinaryStream(i++, new ByteArrayInputStream(dto.compressedWestAdjDataByteArray.elements()), dto.compressedWestAdjDataByteArray.size());
         statement.setByte(i++, dto.dataFormatVersion);
         statement.setByte(i++, dto.compressionModeValue);
         if (dto.applyToParent != null) {
            statement.setBoolean(i++, dto.applyToParent);
         }

         if (dto.applyToChildren != null) {
            statement.setBoolean(i++, dto.applyToChildren);
         }

         statement.setLong(i++, System.currentTimeMillis());
         statement.setLong(i++, dto.createdUnixDateTime);
         statement.setInt(i++, DhSectionPos.getDetailLevel(dto.pos) - 6);
         statement.setInt(i++, DhSectionPos.getX(dto.pos));
         statement.setInt(i++, DhSectionPos.getZ(dto.pos));
         return statement;
      }
   }

   public FullDataSourceV2DTO getAdjByPosAndDirection(long pos, EDhDirection direction) {
      String sql;
      switch (direction) {
         case NORTH:
            sql = this.getAdjForNorthDirTemplate;
            break;
         case SOUTH:
            sql = this.getAdjForSouthDirTemplate;
            break;
         case EAST:
            sql = this.getAdjForEastDirTemplate;
            break;
         case WEST:
            sql = this.getAdjForWestDirTemplate;
            break;
         default:
            throw new IllegalArgumentException();
      }

      try {
         PreparedStatement statement = this.createPreparedStatement(sql);

         Object var18;
         label112: {
            FullDataSourceV2DTO var8;
            label113: {
               try {
                  if (statement == null) {
                     var18 = null;
                     break label112;
                  }

                  int i = 1;
                  statement.setInt(i++, DhSectionPos.getDetailLevel(pos) - 6);
                  statement.setInt(i++, DhSectionPos.getX(pos));
                  statement.setInt(i++, DhSectionPos.getZ(pos));
                  ResultSet resultSet = this.query(statement);

                  label115: {
                     try {
                        if (resultSet == null || !resultSet.next()) {
                           var8 = null;
                           break label115;
                        }

                        var8 = this.convertResultSetToAdjDto(pos, resultSet);
                     } catch (Throwable var12) {
                        if (resultSet != null) {
                           try {
                              resultSet.close();
                           } catch (Throwable var11) {
                              var12.addSuppressed(var11);
                           }
                        }

                        throw var12;
                     }

                     if (resultSet != null) {
                        resultSet.close();
                     }
                     break label113;
                  }

                  if (resultSet != null) {
                     resultSet.close();
                  }
               } catch (Throwable var13) {
                  if (statement != null) {
                     try {
                        statement.close();
                     } catch (Throwable var10) {
                        var13.addSuppressed(var10);
                     }
                  }

                  throw var13;
               }

               if (statement != null) {
                  statement.close();
               }

               return var8;
            }

            if (statement != null) {
               statement.close();
            }

            return var8;
         }

         if (statement != null) {
            statement.close();
         }

         return (FullDataSourceV2DTO)var18;
      } catch (IOException | SQLException var14) {
         if (!(var14 instanceof SQLException) || !DbConnectionClosedException.isClosedException(var14)) {
            LOGGER.warn(
               "Unexpected issue deserializing DTO ["
                  + this.dtoClass.getSimpleName()
                  + "] with pos ["
                  + DhSectionPos.toString(pos)
                  + "] and direction ["
                  + direction
                  + "]. Error: ["
                  + var14.getMessage()
                  + "].",
               var14
            );
         }

         return null;
      }
   }

   public void setApplyToParent(long pos, boolean applyToParent) {
      this.setApplyToFlag(pos, applyToParent, true);
   }

   public void setApplyToChild(long pos, boolean applyToChild) {
      this.setApplyToFlag(pos, applyToChild, false);
   }

   private void setApplyToFlag(long pos, boolean applyFlag, boolean applyToParent) {
      String sql = applyToParent ? this.setApplyToParentSql : this.setApplyToChildrenSql;

      try {
         PreparedStatement statement = this.createPreparedStatement(sql);

         label57: {
            try {
               if (statement == null) {
                  break label57;
               }

               int i = 1;
               statement.setBoolean(i++, applyFlag);
               int detailLevel = DhSectionPos.getDetailLevel(pos) - 6;
               statement.setInt(i++, detailLevel);
               statement.setInt(i++, DhSectionPos.getX(pos));
               statement.setInt(i++, DhSectionPos.getZ(pos));
               ResultSet result = this.query(statement);
               if (result != null) {
                  result.close();
               }
            } catch (Throwable var11) {
               if (statement != null) {
                  try {
                     statement.close();
                  } catch (Throwable var10) {
                     var11.addSuppressed(var10);
                  }
               }

               throw var11;
            }

            if (statement != null) {
               statement.close();
            }

            return;
         }

         if (statement != null) {
            statement.close();
         }
      } catch (SQLException var12) {
         throw new RuntimeException(var12);
      }
   }

   public LongArrayList getPositionsToUpdate(int targetBlockPosX, int targetBlockPosZ, int returnCount) {
      return this.getPositionsToUpdate(targetBlockPosX, targetBlockPosZ, returnCount, true);
   }

   public LongArrayList getChildPositionsToUpdate(int targetBlockPosX, int targetBlockPosZ, int returnCount) {
      return this.getPositionsToUpdate(targetBlockPosX, targetBlockPosZ, returnCount, false);
   }

   private LongArrayList getPositionsToUpdate(int targetBlockPosX, int targetBlockPosZ, int returnCount, boolean getParentUpdates) {
      LongArrayList list = new LongArrayList();
      String sql = getParentUpdates ? this.getParentPositionsToUpdateSql : this.getChildPositionsToUpdateSql;

      try {
         PreparedStatement statement = this.createPreparedStatement(sql);

         LongArrayList var24;
         label85: {
            LongArrayList var25;
            try {
               if (statement == null) {
                  var24 = list;
                  break label85;
               }

               int i = 1;
               statement.setInt(i++, targetBlockPosX);
               statement.setInt(i++, targetBlockPosZ);
               statement.setInt(i++, returnCount);
               ResultSet result = this.query(statement);

               try {
                  while (result != null && result.next()) {
                     byte detailLevel = result.getByte("DetailLevel");
                     byte sectionDetailLevel = (byte)(detailLevel + 6);
                     int posX = result.getInt("PosX");
                     int posZ = result.getInt("PosZ");
                     long pos = DhSectionPos.encode(sectionDetailLevel, posX, posZ);
                     list.add(pos);
                  }
               } catch (Throwable var18) {
                  if (result != null) {
                     try {
                        result.close();
                     } catch (Throwable var17) {
                        var18.addSuppressed(var17);
                     }
                  }

                  throw var18;
               }

               if (result != null) {
                  result.close();
               }

               var25 = list;
            } catch (Throwable var19) {
               if (statement != null) {
                  try {
                     statement.close();
                  } catch (Throwable var16) {
                     var19.addSuppressed(var16);
                  }
               }

               throw var19;
            }

            if (statement != null) {
               statement.close();
            }

            return var25;
         }

         if (statement != null) {
            statement.close();
         }

         return var24;
      } catch (SQLException var20) {
         throw new RuntimeException(var20);
      }
   }

   public void getColumnGenerationStepForPos(long pos, ByteArrayList outputByteArray) {
      try {
         PreparedStatement statement = this.createPreparedStatement(this.getColumnGenerationStepSql);

         label141: {
            label142: {
               try {
                  ResultSet result;
                  label144: {
                     if (statement == null) {
                        break label141;
                     }

                     int detailLevel = DhSectionPos.getDetailLevel(pos) - 6;
                     int i = 1;
                     statement.setInt(i++, detailLevel);
                     statement.setInt(i++, DhSectionPos.getX(pos));
                     statement.setInt(i++, DhSectionPos.getZ(pos));
                     result = this.query(statement);

                     try {
                        if (result == null || !result.next()) {
                           break label144;
                        }

                        byte compressionModeEnumValue = result.getByte("CompressionMode");
                        EDhApiDataCompressionMode compressionModeEnum = EDhApiDataCompressionMode.getFromValue(compressionModeEnumValue);

                        try {
                           ByteArrayList byteArrayList = new ByteArrayList();
                           putAllBytes(result.getBinaryStream("ColumnGenerationStep"), byteArrayList);
                           PhantomArrayListCheckout checkout = FullDataSourceV2DTO.ARRAY_LIST_POOL.checkoutByteArrays(1);

                           try {
                              DhDataInputStream compressedIn = DhDataInputStream.create(byteArrayList, compressionModeEnum, checkout);

                              try {
                                 putAllBytes(compressedIn, outputByteArray);
                              } catch (Throwable var19) {
                                 if (compressedIn != null) {
                                    try {
                                       compressedIn.close();
                                    } catch (Throwable var18) {
                                       var19.addSuppressed(var18);
                                    }
                                 }

                                 throw var19;
                              }

                              if (compressedIn != null) {
                                 compressedIn.close();
                              }
                           } catch (Throwable var20) {
                              if (checkout != null) {
                                 try {
                                    checkout.close();
                                 } catch (Throwable var17) {
                                    var20.addSuppressed(var17);
                                 }
                              }

                              throw var20;
                           }

                           if (checkout != null) {
                              checkout.close();
                           }
                        } catch (IOException var21) {
                           LOGGER.warn(
                              "Decompression issue when getting column gen steps for pos: [" + DhSectionPos.toString(pos) + "], deleting corrupted data.",
                              var21
                           );
                           this.deleteWithKey(pos);
                           ListUtil.clearAndSetSize(outputByteArray, 4096);
                        }
                     } catch (Throwable var22) {
                        if (result != null) {
                           try {
                              result.close();
                           } catch (Throwable var16) {
                              var22.addSuppressed(var16);
                           }
                        }

                        throw var22;
                     }

                     if (result != null) {
                        result.close();
                     }
                     break label142;
                  }

                  if (result != null) {
                     result.close();
                  }
               } catch (Throwable var23) {
                  if (statement != null) {
                     try {
                        statement.close();
                     } catch (Throwable var15) {
                        var23.addSuppressed(var15);
                     }
                  }

                  throw var23;
               }

               if (statement != null) {
                  statement.close();
               }

               return;
            }

            if (statement != null) {
               statement.close();
            }

            return;
         }

         if (statement != null) {
            statement.close();
         }
      } catch (SQLException var24) {
         if (!DbConnectionClosedException.isClosedException(var24)) {
            throw new RuntimeException(var24);
         }
      }
   }

   @Nullable
   public Long getTimestampForPos(long pos) {
      try {
         PreparedStatement preparedStatement = this.createPreparedStatement(this.getTimestampForPosSql);

         Object var17;
         label100: {
            Long var6;
            label101: {
               try {
                  if (preparedStatement == null) {
                     var17 = null;
                     break label100;
                  }

                  int i = 1;
                  preparedStatement.setInt(i++, DhSectionPos.getDetailLevel(pos) - 6);
                  preparedStatement.setInt(i++, DhSectionPos.getX(pos));
                  preparedStatement.setInt(i++, DhSectionPos.getZ(pos));
                  ResultSet result = this.query(preparedStatement);

                  label103: {
                     try {
                        if (result == null || !result.next()) {
                           var6 = null;
                           break label103;
                        }

                        var6 = result.getLong("LastModifiedUnixDateTime");
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
                     break label101;
                  }

                  if (result != null) {
                     result.close();
                  }
               } catch (Throwable var11) {
                  if (preparedStatement != null) {
                     try {
                        preparedStatement.close();
                     } catch (Throwable var8) {
                        var11.addSuppressed(var8);
                     }
                  }

                  throw var11;
               }

               if (preparedStatement != null) {
                  preparedStatement.close();
               }

               return var6;
            }

            if (preparedStatement != null) {
               preparedStatement.close();
            }

            return var6;
         }

         if (preparedStatement != null) {
            preparedStatement.close();
         }

         return (Long)var17;
      } catch (DbConnectionClosedException var12) {
         return null;
      } catch (SQLException var13) {
         throw new RuntimeException(var13);
      }
   }

   public Map<Long, Long> getTimestampsForRange(byte detailLevel, int startPosX, int startPosZ, int endPosX, int endPosZ) {
      try {
         PreparedStatement preparedStatement = this.createPreparedStatement(this.getTimestampForRangeSql);

         HashMap var24;
         label84: {
            HashMap var25;
            try {
               if (preparedStatement == null) {
                  var24 = new HashMap();
                  break label84;
               }

               int i = 1;
               preparedStatement.setInt(i++, detailLevel - 6);
               preparedStatement.setInt(i++, startPosX);
               preparedStatement.setInt(i++, endPosX - 1);
               preparedStatement.setInt(i++, startPosZ);
               preparedStatement.setInt(i++, endPosZ - 1);
               ResultSet result = this.query(preparedStatement);

               try {
                  HashMap<Long, Long> returnMap = new HashMap<>();

                  while (result != null && result.next()) {
                     long key = DhSectionPos.encode(detailLevel, result.getInt("PosX"), result.getInt("PosZ"));
                     long value = result.getLong("LastModifiedUnixDateTime");
                     returnMap.put(key, value);
                  }

                  var25 = returnMap;
               } catch (Throwable var16) {
                  if (result != null) {
                     try {
                        result.close();
                     } catch (Throwable var15) {
                        var16.addSuppressed(var15);
                     }
                  }

                  throw var16;
               }

               if (result != null) {
                  result.close();
               }
            } catch (Throwable var17) {
               if (preparedStatement != null) {
                  try {
                     preparedStatement.close();
                  } catch (Throwable var14) {
                     var17.addSuppressed(var14);
                  }
               }

               throw var17;
            }

            if (preparedStatement != null) {
               preparedStatement.close();
            }

            return var25;
         }

         if (preparedStatement != null) {
            preparedStatement.close();
         }

         return var24;
      } catch (SQLException var18) {
         throw new RuntimeException(var18);
      }
   }

   public LongArrayList getAllPositions() {
      LongArrayList list = new LongArrayList();

      try {
         PreparedStatement statement = this.createPreparedStatement(this.getAllPositionsSql);

         LongArrayList var15;
         label79: {
            LongArrayList var16;
            try {
               if (statement == null) {
                  var15 = list;
                  break label79;
               }

               ResultSet result = this.query(statement);

               try {
                  while (true) {
                     if (result == null || !result.next()) {
                        var16 = list;
                        break;
                     }

                     byte detailLevel = result.getByte("DetailLevel");
                     byte sectionDetailLevel = (byte)(detailLevel + 6);
                     int posX = result.getInt("PosX");
                     int posZ = result.getInt("PosZ");
                     long pos = DhSectionPos.encode(sectionDetailLevel, posX, posZ);
                     list.add(pos);
                  }
               } catch (Throwable var12) {
                  if (result != null) {
                     try {
                        result.close();
                     } catch (Throwable var11) {
                        var12.addSuppressed(var11);
                     }
                  }

                  throw var12;
               }

               if (result != null) {
                  result.close();
               }
            } catch (Throwable var13) {
               if (statement != null) {
                  try {
                     statement.close();
                  } catch (Throwable var10) {
                     var13.addSuppressed(var10);
                  }
               }

               throw var13;
            }

            if (statement != null) {
               statement.close();
            }

            return var16;
         }

         if (statement != null) {
            statement.close();
         }

         return var15;
      } catch (SQLException var14) {
         throw new RuntimeException(var14);
      }
   }

   public long getDataSizeInBytes(long pos) {
      int detailLevel = DhSectionPos.getDetailLevel(pos) - 6;

      try {
         PreparedStatement statement = this.createPreparedStatement(this.getDataSizeInBytesSql);

         long var17;
         label97: {
            long var7;
            label98: {
               try {
                  if (statement == null) {
                     var17 = 0L;
                     break label97;
                  }

                  int i = 1;
                  statement.setInt(i++, detailLevel);
                  statement.setInt(i++, DhSectionPos.getX(pos));
                  statement.setInt(i++, DhSectionPos.getZ(pos));
                  ResultSet result = this.query(statement);

                  label100: {
                     try {
                        if (result == null || !result.next()) {
                           var7 = 0L;
                           break label100;
                        }

                        var7 = result.getLong("dataSize");
                     } catch (Throwable var11) {
                        if (result != null) {
                           try {
                              result.close();
                           } catch (Throwable var10) {
                              var11.addSuppressed(var10);
                           }
                        }

                        throw var11;
                     }

                     if (result != null) {
                        result.close();
                     }
                     break label98;
                  }

                  if (result != null) {
                     result.close();
                  }
               } catch (Throwable var12) {
                  if (statement != null) {
                     try {
                        statement.close();
                     } catch (Throwable var9) {
                        var12.addSuppressed(var9);
                     }
                  }

                  throw var12;
               }

               if (statement != null) {
                  statement.close();
               }

               return var7;
            }

            if (statement != null) {
               statement.close();
            }

            return var7;
         }

         if (statement != null) {
            statement.close();
         }

         return var17;
      } catch (SQLException var13) {
         throw new RuntimeException(var13);
      }
   }

   public long getTotalDataSizeInBytes() {
      try {
         PreparedStatement statement = this.createPreparedStatement(this.getTotalDataSizeInBytesSql);

         long var3;
         label80: {
            try {
               ResultSet result = this.query(statement);

               label82: {
                  try {
                     if (result == null || !result.next()) {
                        var3 = 0L;
                        break label82;
                     }

                     var3 = result.getLong("dataSize");
                  } catch (Throwable var7) {
                     if (result != null) {
                        try {
                           result.close();
                        } catch (Throwable var6) {
                           var7.addSuppressed(var6);
                        }
                     }

                     throw var7;
                  }

                  if (result != null) {
                     result.close();
                  }
                  break label80;
               }

               if (result != null) {
                  result.close();
               }
            } catch (Throwable var8) {
               if (statement != null) {
                  try {
                     statement.close();
                  } catch (Throwable var5) {
                     var8.addSuppressed(var5);
                  }
               }

               throw var8;
            }

            if (statement != null) {
               statement.close();
            }

            return var3;
         }

         if (statement != null) {
            statement.close();
         }

         return var3;
      } catch (SQLException var9) {
         throw new RuntimeException(var9);
      }
   }

   private static ByteArrayList putAllBytes(@Nullable InputStream inputStream, @Nullable ByteArrayList existingArrayList) throws IOException {
      if (existingArrayList == null) {
         existingArrayList = new ByteArrayList(64);
      } else {
         existingArrayList.clear();
      }

      try {
         if (inputStream != null) {
            for (int nextByte = inputStream.read(); nextByte != -1; nextByte = inputStream.read()) {
               existingArrayList.add((byte)nextByte);
            }
         }
      } catch (EOFException var3) {
      }

      return existingArrayList;
   }
}
