package com.seibel.distanthorizons.core.sql.repo;

import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.DhChunkPos;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos;
import com.seibel.distanthorizons.core.sql.DbConnectionClosedException;
import com.seibel.distanthorizons.core.sql.dto.BeaconBeamDTO;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public class BeaconBeamRepo extends AbstractDhRepo<DhBlockPos, BeaconBeamDTO> {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private final String insertSqlTemplate = "INSERT INTO "
      + this.getTableName()
      + " (\n   BlockPosX, BlockPosY, BlockPosZ, \n   ColorR, ColorG, ColorB, \n   LastModifiedUnixDateTime, CreatedUnixDateTime) \nVALUES( \n    ?, ?, ?, \n    ?, ?, ?, \n    ?, ? \n);";
   private final String updateSqlTemplate = "UPDATE "
      + this.getTableName()
      + " \nSET \n    ColorR = ?, ColorG = ?, ColorB = ?,  \n    LastModifiedUnixDateTime = ? \nWHERE BlockPosX = ? AND BlockPosY = ? AND BlockPosZ = ?";
   private final String getAllBeamsInRangeTemplate = "SELECT * FROM "
      + this.getTableName()
      + " WHERE ? <= BlockPosX AND BlockPosX <= ? AND ? <= BlockPosZ AND BlockPosZ <= ?";

   public BeaconBeamRepo(String databaseType, File databaseFile) throws SQLException, IOException {
      super(databaseType, databaseFile, BeaconBeamDTO.class);
   }

   @Override
   public String getTableName() {
      return "BeaconBeam";
   }

   @Override
   protected String CreateParameterizedWhereString() {
      return "BlockPosX = ? AND BlockPosY = ? AND BlockPosZ = ?";
   }

   protected int setPreparedStatementWhereClause(PreparedStatement statement, int index, DhBlockPos pos) throws SQLException {
      statement.setInt(index++, pos.getX());
      statement.setInt(index++, pos.getY());
      statement.setInt(index++, pos.getZ());
      return index;
   }

   @Nullable
   public BeaconBeamDTO convertResultSetToDto(ResultSet resultSet) throws ClassCastException, SQLException {
      int posX = resultSet.getInt("BlockPosX");
      int posY = resultSet.getInt("BlockPosY");
      int posZ = resultSet.getInt("BlockPosZ");
      int red = resultSet.getInt("ColorR");
      int green = resultSet.getInt("ColorG");
      int blue = resultSet.getInt("ColorB");
      return new BeaconBeamDTO(new DhBlockPos(posX, posY, posZ), new Color(red, green, blue));
   }

   public PreparedStatement createInsertStatement(BeaconBeamDTO dto) throws SQLException {
      PreparedStatement statement = this.createPreparedStatement(this.insertSqlTemplate);
      if (statement == null) {
         return null;
      } else {
         int i = 1;
         statement.setInt(i++, dto.blockPos.getX());
         statement.setInt(i++, dto.blockPos.getY());
         statement.setInt(i++, dto.blockPos.getZ());
         statement.setInt(i++, dto.color.getRed());
         statement.setInt(i++, dto.color.getGreen());
         statement.setInt(i++, dto.color.getBlue());
         statement.setLong(i++, System.currentTimeMillis());
         statement.setLong(i++, System.currentTimeMillis());
         return statement;
      }
   }

   public PreparedStatement createUpdateStatement(BeaconBeamDTO dto) throws SQLException {
      PreparedStatement statement = this.createPreparedStatement(this.updateSqlTemplate);
      if (statement == null) {
         return null;
      } else {
         int i = 1;
         statement.setInt(i++, dto.color.getRed());
         statement.setInt(i++, dto.color.getGreen());
         statement.setInt(i++, dto.color.getBlue());
         statement.setLong(i++, System.currentTimeMillis());
         statement.setInt(i++, dto.blockPos.getX());
         statement.setInt(i++, dto.blockPos.getY());
         statement.setInt(i++, dto.blockPos.getZ());
         return statement;
      }
   }

   public List<BeaconBeamDTO> getAllBeamsForPos(DhChunkPos chunkPos) {
      int minBlockX = chunkPos.getMinBlockX();
      int minBlockZ = chunkPos.getMinBlockZ();
      int maxBlockX = minBlockX + 16;
      int maxBlockZ = minBlockZ + 16;
      return this.getAllBeamsInBlockPosRange(minBlockX, maxBlockX, minBlockZ, maxBlockZ);
   }

   public ArrayList<BeaconBeamDTO> getAllBeamsForPos(long pos) {
      int minBlockX = DhSectionPos.getMinCornerBlockX(pos);
      int minBlockZ = DhSectionPos.getMinCornerBlockZ(pos);
      int maxBlockX = minBlockX + DhSectionPos.getBlockWidth(pos);
      int maxBlockZ = minBlockZ + DhSectionPos.getBlockWidth(pos);
      return this.getAllBeamsInBlockPosRange(minBlockX, maxBlockX, minBlockZ, maxBlockZ);
   }

   public ArrayList<BeaconBeamDTO> getAllBeamsInBlockPosRange(int minBlockX, int maxBlockX, int minBlockZ, int maxBlockZ) {
      ArrayList<BeaconBeamDTO> beamList = new ArrayList<>();

      try {
         PreparedStatement statement = this.createPreparedStatement(this.getAllBeamsInRangeTemplate);

         ArrayList var20;
         label85: {
            try {
               if (statement == null) {
                  var20 = beamList;
                  break label85;
               }

               int i = 1;
               statement.setInt(i++, minBlockX);
               statement.setInt(i++, maxBlockX);
               statement.setInt(i++, minBlockZ);
               statement.setInt(i++, maxBlockZ);
               ResultSet result = this.query(statement);

               try {
                  while (result != null && result.next()) {
                     beamList.add(this.convertResultSetToDto(result));
                  }
               } catch (Throwable var13) {
                  if (result != null) {
                     try {
                        result.close();
                     } catch (Throwable var12) {
                        var13.addSuppressed(var12);
                     }
                  }

                  throw var13;
               }

               if (result != null) {
                  result.close();
               }
            } catch (Throwable var14) {
               if (statement != null) {
                  try {
                     statement.close();
                  } catch (Throwable var11) {
                     var14.addSuppressed(var11);
                  }
               }

               throw var14;
            }

            if (statement != null) {
               statement.close();
            }

            return beamList;
         }

         if (statement != null) {
            statement.close();
         }

         return var20;
      } catch (Exception var15) {
         if (var15 instanceof SQLException && DbConnectionClosedException.isClosedException((SQLException)var15)) {
            return beamList;
         } else {
            throw new RuntimeException(var15);
         }
      }
   }
}
