package com.aetherteam.aether.world.structurepiece.silverdungeon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class SilverDungeonBuilder {
   private static final int CHEST_ROOM = 1;
   private static final int STAIRS = 2;
   private static final int FINAL_STAIRS = 4;
   private static final int STAIRS_MIDDLE = 8;
   private static final int STAIRS_TOP = 16;
   private static final int NORTH_DOOR = 32;
   private static final int WEST_DOOR = 64;
   private static final int VISITED = 128;
   private final RandomSource random;
   private final SilverProcessorSettings processors;
   private final int[][][] grid;
   private final int width;
   private final int height;
   private final int length;

   public SilverDungeonBuilder(RandomSource random, int x, int y, int z, SilverProcessorSettings processors) {
      this.random = random;
      this.processors = processors;
      this.grid = new int[x][y][z];
      this.width = x;
      this.height = y;
      this.length = z;
      this.populateGrid();
   }

   private void populateGrid() {
      int finalStairsX = this.random.nextInt(this.width);
      this.grid[finalStairsX][0][0] = 4;
      this.grid[finalStairsX][1][0] = 8;
      this.grid[finalStairsX][2][0] = 16;
      int firstStairsX = this.random.nextInt(this.width);
      this.grid[firstStairsX][0][1] = 2;
      this.grid[firstStairsX][1][1] = 16;
      int secondStairsX = this.random.nextInt(this.width);
      this.grid[secondStairsX][1][2] = 2;
      this.grid[secondStairsX][2][2] = 16;

      for (int y = 0; y < this.height; y++) {
         this.traverseRooms(1, y, 1, 0);

         for (int z = 0; z < this.length; z++) {
            for (int x = 0; x < this.width; x++) {
               if ((this.grid[x][y][z] & 31) == 0 && this.random.nextInt(3) != 0) {
                  this.grid[x][y][z] = this.grid[x][y][z] | 1;
               }
            }
         }
      }
   }

   private boolean traverseRooms(int x, int y, int z, int typesToAvoid) {
      if (x >= 0 && x < this.width && z >= 0 && z < this.length) {
         int room = this.grid[x][y][z];
         if ((room & typesToAvoid) > 0) {
            return false;
         } else if ((room & 128) == 128) {
            return this.random.nextInt(3) == 0;
         } else {
            this.grid[x][y][z] = this.grid[x][y][z] | 128;
            int blacklist = this.setNeighborBlacklist(room);
            List<Direction> directions = new ArrayList<>(4);
            Collections.addAll(directions, Direction.NORTH, Direction.WEST, Direction.SOUTH, Direction.EAST);

            for (int i = directions.size(); i > 0; i--) {
               int index = this.random.nextInt(i);
               switch ((Direction)directions.remove(index)) {
                  case NORTH:
                     if (this.traverseRooms(x, y, z - 1, blacklist)) {
                        this.grid[x][y][z] = this.grid[x][y][z] | 32;
                     }
                     break;
                  case SOUTH:
                     if (this.traverseRooms(x, y, z + 1, blacklist)) {
                        this.grid[x][y][z + 1] = this.grid[x][y][z + 1] | 32;
                     }
                     break;
                  case WEST:
                     if (this.traverseRooms(x - 1, y, z, blacklist)) {
                        this.grid[x][y][z] = this.grid[x][y][z] | 64;
                     }
                     break;
                  case EAST:
                     if (this.traverseRooms(x + 1, y, z, blacklist)) {
                        this.grid[x + 1][y][z] = this.grid[x + 1][y][z] | 64;
                     }
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private int setNeighborBlacklist(int roomType) {
      int blacklist = 12;
      if ((roomType & 16) == 16) {
         blacklist |= 2;
      }

      if ((roomType & 2) == 2) {
         blacklist |= 16;
      }

      return blacklist;
   }

   public void assembleDungeon(
      StructurePiecesBuilder builder, StructureTemplateManager templateManager, BlockPos startPos, Rotation rotation, Direction direction
   ) {
      startPos = startPos.offset(direction.getStepZ() * 5 - direction.getStepX(), 5, -direction.getStepX() * 5 - direction.getStepZ());
      MutableBlockPos offset = new MutableBlockPos();
      Rotation sideways = rotation.getRotated(Rotation.CLOCKWISE_90);

      for (int y = this.height - 1; y >= 0; y--) {
         offset.setY(startPos.getY() + y * 5);

         for (int z = 0; z < this.length; z++) {
            for (int x = 0; x < this.width; x++) {
               int xOffset = startPos.getX() + direction.getStepZ() * x * 7 + direction.getStepX() * z * 7;
               int zOffset = startPos.getZ() + direction.getStepZ() * z * 7 - direction.getStepX() * x * 7;
               offset.set(xOffset, offset.getY(), zOffset);
               int room = this.grid[x][y][z];
               builder.addPiece(
                  new SilverFloorPiece(
                     templateManager,
                     "floor",
                     offset.offset(direction.getStepX() + direction.getStepZ(), -1, direction.getStepZ() - direction.getStepX()),
                     rotation,
                     this.processors.floorSettings()
                  )
               );
               builder.addPiece(
                  new SilverTemplePiece(
                     templateManager,
                     (room & 32) == 32 ? "door" : "wall",
                     offset.offset(direction.getStepZ(), 0, -direction.getStepX()),
                     rotation,
                     this.processors.roomSettings()
                  )
               );
               builder.addPiece(
                  new SilverTemplePiece(
                     templateManager, (room & 64) == 64 ? "door" : "wall", offset.relative(direction), sideways, this.processors.roomSettings()
                  )
               );
               if ((room & 4) == 4) {
                  builder.addPiece(new SilverDungeonRoom(templateManager, "tall_staircase", offset.offset(2, 0, 2), rotation, this.processors.roomSettings()));
                  builder.addPiece(
                     new SilverTemplePiece(
                        templateManager,
                        "boss_door",
                        offset.offset(direction.getStepZ() * 3, 0, -direction.getStepX() * 3),
                        rotation,
                        this.processors.roomSettings()
                     )
                  );
               } else if ((room & 2) == 2) {
                  builder.addPiece(new SilverDungeonRoom(templateManager, "staircase", offset.offset(2, 0, 2), rotation, this.processors.roomSettings()));
               } else if ((room & 1) == 1) {
                  builder.addPiece(new SilverDungeonRoom(templateManager, "chest_room", offset.offset(3, 0, 3), rotation, this.processors.roomSettings()));
               }
            }
         }
      }
   }
}
