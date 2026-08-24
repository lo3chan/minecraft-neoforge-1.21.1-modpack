package net.joefoxe.hexerei.block.connected.behavior;

import net.joefoxe.hexerei.block.connected.CTSpriteShiftEntry;
import net.joefoxe.hexerei.block.connected.ConnectedTextureBehaviour;
import net.joefoxe.hexerei.block.custom.ConnectingCarpetDyed;
import net.joefoxe.hexerei.block.custom.ConnectingCarpetStairs;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CarpetStairsCTBehaviour extends ConnectedTextureBehaviour.Base {
   protected CTSpriteShiftEntry topShift;
   protected CTSpriteShiftEntry layerShift;

   public CarpetStairsCTBehaviour(CTSpriteShiftEntry layerShift) {
      this(layerShift, null);
   }

   private boolean posEquals(BlockPos pos, BlockPos pos2) {
      return pos.getX() == pos2.getX() && pos.getY() == pos2.getY() && pos.getZ() == pos2.getZ();
   }

   public static boolean checkLeft(BlockState stateIn, BlockPos currentPos, BlockAndTintGetter worldIn) {
      if (stateIn.hasProperty(StairBlock.FACING)) {
         if (stateIn.getValue(StairBlock.FACING) == Direction.NORTH) {
            return worldIn.getBlockState(currentPos.west()).getBlock() instanceof ConnectingCarpetStairs;
         }

         if (stateIn.getValue(StairBlock.FACING) == Direction.EAST) {
            return worldIn.getBlockState(currentPos.north()).getBlock() instanceof ConnectingCarpetStairs;
         }

         if (stateIn.getValue(StairBlock.FACING) == Direction.SOUTH) {
            return worldIn.getBlockState(currentPos.east()).getBlock() instanceof ConnectingCarpetStairs;
         }

         if (stateIn.getValue(StairBlock.FACING) == Direction.WEST) {
            return worldIn.getBlockState(currentPos.south()).getBlock() instanceof ConnectingCarpetStairs;
         }
      }

      return false;
   }

   public boolean checkRight(BlockState stateIn, BlockPos currentPos, BlockAndTintGetter worldIn) {
      if (stateIn.hasProperty(StairBlock.FACING)) {
         if (stateIn.getValue(StairBlock.FACING) == Direction.NORTH) {
            return worldIn.getBlockState(currentPos.east()).getBlock() instanceof ConnectingCarpetStairs;
         }

         if (stateIn.getValue(StairBlock.FACING) == Direction.EAST) {
            return worldIn.getBlockState(currentPos.south()).getBlock() instanceof ConnectingCarpetStairs;
         }

         if (stateIn.getValue(StairBlock.FACING) == Direction.SOUTH) {
            return worldIn.getBlockState(currentPos.west()).getBlock() instanceof ConnectingCarpetStairs;
         }

         if (stateIn.getValue(StairBlock.FACING) == Direction.WEST) {
            return worldIn.getBlockState(currentPos.north()).getBlock() instanceof ConnectingCarpetStairs;
         }
      }

      return false;
   }

   @Override
   public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos, Direction face) {
      BlockState below = reader.getBlockState(pos.below());
      if (below.hasProperty(StairBlock.FACING)) {
         Direction facing = (Direction)state.getValue(StairBlock.FACING);
         if (face != facing.getClockWise() && face != facing.getCounterClockWise()) {
            if (face == facing) {
               Axis side_axis = facing.getClockWise().getAxis();
               Direction ne = facing.getCounterClockWise(side_axis);
               Direction sw = facing.getClockWise(side_axis);
               Direction tempFacing = facing != Direction.EAST && facing != Direction.NORTH ? sw : ne;
               Axis up_axis = tempFacing.getCounterClockWise(side_axis).getAxis();
               if (this.posEquals(otherPos, pos.relative(tempFacing))) {
                  BlockState otherCarpet = reader.getBlockState(pos.relative(facing).above());
                  if (otherCarpet.getBlock() == state.getBlock()) {
                     return this.hasColor(state, otherCarpet) && otherCarpet.getValue(StairBlock.FACING) == facing;
                  }
               } else if (this.posEquals(otherPos, pos.relative(facing.getClockWise().getOpposite()))) {
                  BlockState otherCarpet = reader.getBlockState(pos.relative(facing.getClockWise()));
                  if (otherCarpet.getBlock() == state.getBlock()) {
                     return this.hasColor(state, otherCarpet) && otherCarpet.getValue(StairBlock.FACING) == facing;
                  }
               } else if (this.posEquals(otherPos, pos.relative(facing.getCounterClockWise().getOpposite()))) {
                  BlockState otherCarpet = reader.getBlockState(pos.relative(facing.getCounterClockWise()));
                  if (otherCarpet.getBlock() == state.getBlock()) {
                     return this.hasColor(state, otherCarpet) && otherCarpet.getValue(StairBlock.FACING) == facing;
                  }
               } else if (this.posEquals(otherPos, pos.relative(facing.getCounterClockWise(side_axis)).relative(facing.getClockWise().getOpposite()))) {
                  BlockState otherCarpet = reader.getBlockState(pos.relative(facing).relative(facing.getClockWise()).above());
                  if (otherCarpet.getBlock() == state.getBlock()) {
                     return this.hasColor(state, otherCarpet) && otherCarpet.getValue(StairBlock.FACING) == facing;
                  }
               } else if (this.posEquals(otherPos, pos.relative(facing.getCounterClockWise(side_axis)).relative(facing.getCounterClockWise().getOpposite()))) {
                  BlockState otherCarpet = reader.getBlockState(pos.relative(facing).relative(facing.getCounterClockWise()).above());
                  if (otherCarpet.getBlock() == state.getBlock()) {
                     return this.hasColor(state, otherCarpet) && otherCarpet.getValue(StairBlock.FACING) == facing;
                  }
               } else if (this.posEquals(
                  otherPos, pos.relative(facing.getOpposite().getCounterClockWise(side_axis)).relative(facing.getClockWise().getOpposite())
               )) {
                  BlockState otherCarpet = reader.getBlockState(pos.relative(facing.getOpposite()).relative(facing.getClockWise()).below());
                  if (otherCarpet.getBlock() == state.getBlock()) {
                     return this.hasColor(state, otherCarpet) && otherCarpet.getValue(StairBlock.FACING) == facing;
                  }
               } else if (this.posEquals(
                  otherPos, pos.relative(facing.getOpposite().getCounterClockWise(side_axis)).relative(facing.getCounterClockWise().getOpposite())
               )) {
                  BlockState otherCarpet = reader.getBlockState(pos.relative(facing.getOpposite()).relative(facing.getCounterClockWise()).below());
                  if (otherCarpet.getBlock() == state.getBlock()) {
                     return this.hasColor(state, otherCarpet) && otherCarpet.getValue(StairBlock.FACING) == facing;
                  }
               } else if (this.posEquals(otherPos, pos.relative(facing.getOpposite().getCounterClockWise(side_axis)))) {
                  BlockState otherCarpet = reader.getBlockState(pos.relative(facing.getOpposite()).below());
                  if (otherCarpet.getBlock() == state.getBlock()) {
                     return this.hasColor(state, otherCarpet) && otherCarpet.getValue(StairBlock.FACING) == facing;
                  }
               }
            } else if (face == facing.getOpposite()) {
               if (facing == Direction.NORTH || facing == Direction.SOUTH) {
                  if (this.posEquals(otherPos, pos.relative(Direction.UP))) {
                     BlockState otherCarpet = reader.getBlockState(pos.relative(facing).above());
                     if (otherCarpet.getBlock() == state.getBlock()) {
                        return this.hasColor(state, otherCarpet) && otherCarpet.getValue(StairBlock.FACING) == facing;
                     }
                  }

                  if (this.posEquals(otherPos, pos.relative(Direction.DOWN))) {
                     BlockState otherCarpet = reader.getBlockState(pos.relative(facing.getOpposite()).below());
                     if (otherCarpet.getBlock() == state.getBlock()) {
                        return this.hasColor(state, otherCarpet) && otherCarpet.getValue(StairBlock.FACING) == facing;
                     }
                  }

                  if (this.posEquals(otherPos, pos.relative(Direction.WEST))) {
                     BlockState otherCarpet = reader.getBlockState(pos.relative(Direction.WEST));
                     if (otherCarpet.getBlock() == state.getBlock()) {
                        return this.hasColor(state, otherCarpet) && otherCarpet.getValue(StairBlock.FACING) == facing;
                     }
                  }

                  if (this.posEquals(otherPos, pos.relative(Direction.EAST))) {
                     BlockState otherCarpet = reader.getBlockState(pos.relative(Direction.EAST));
                     if (otherCarpet.getBlock() == state.getBlock()) {
                        return this.hasColor(state, otherCarpet) && otherCarpet.getValue(StairBlock.FACING) == facing;
                     }
                  }

                  if (this.posEquals(otherPos, pos.relative(Direction.UP).relative(Direction.EAST))) {
                     BlockState otherCarpet = reader.getBlockState(pos.relative(Direction.EAST).relative(facing).above());
                     if (otherCarpet.getBlock() == state.getBlock()) {
                        return this.hasColor(state, otherCarpet) && otherCarpet.getValue(StairBlock.FACING) == facing;
                     }
                  }

                  if (this.posEquals(otherPos, pos.relative(Direction.DOWN).relative(Direction.EAST))) {
                     BlockState otherCarpet = reader.getBlockState(pos.relative(Direction.EAST).relative(facing.getOpposite()).below());
                     if (otherCarpet.getBlock() == state.getBlock()) {
                        return this.hasColor(state, otherCarpet) && otherCarpet.getValue(StairBlock.FACING) == facing;
                     }
                  }

                  if (this.posEquals(otherPos, pos.relative(Direction.UP).relative(Direction.WEST))) {
                     BlockState otherCarpet = reader.getBlockState(pos.relative(Direction.WEST).relative(facing).above());
                     if (otherCarpet.getBlock() == state.getBlock()) {
                        return this.hasColor(state, otherCarpet) && otherCarpet.getValue(StairBlock.FACING) == facing;
                     }
                  }

                  if (this.posEquals(otherPos, pos.relative(Direction.DOWN).relative(Direction.WEST))) {
                     BlockState otherCarpet = reader.getBlockState(pos.relative(Direction.WEST).relative(facing.getOpposite()).below());
                     if (otherCarpet.getBlock() == state.getBlock()) {
                        return this.hasColor(state, otherCarpet) && otherCarpet.getValue(StairBlock.FACING) == facing;
                     }
                  }
               }

               if (facing == Direction.EAST || facing == Direction.WEST) {
                  if (this.posEquals(otherPos, pos.relative(Direction.UP))) {
                     BlockState otherCarpet = reader.getBlockState(pos.relative(facing).above());
                     if (otherCarpet.getBlock() == state.getBlock()) {
                        return this.hasColor(state, otherCarpet) && otherCarpet.getValue(StairBlock.FACING) == facing;
                     }
                  }

                  if (this.posEquals(otherPos, pos.relative(Direction.DOWN))) {
                     BlockState otherCarpet = reader.getBlockState(pos.relative(facing.getOpposite()).below());
                     if (otherCarpet.getBlock() == state.getBlock()) {
                        return this.hasColor(state, otherCarpet) && otherCarpet.getValue(StairBlock.FACING) == facing;
                     }
                  }

                  if (this.posEquals(otherPos, pos.relative(Direction.SOUTH))) {
                     BlockState otherCarpet = reader.getBlockState(pos.relative(Direction.SOUTH));
                     if (otherCarpet.getBlock() == state.getBlock()) {
                        return this.hasColor(state, otherCarpet) && otherCarpet.getValue(StairBlock.FACING) == facing;
                     }
                  }

                  if (this.posEquals(otherPos, pos.relative(Direction.NORTH))) {
                     BlockState otherCarpet = reader.getBlockState(pos.relative(Direction.NORTH));
                     if (otherCarpet.getBlock() == state.getBlock()) {
                        return this.hasColor(state, otherCarpet) && otherCarpet.getValue(StairBlock.FACING) == facing;
                     }
                  }

                  if (this.posEquals(otherPos, pos.relative(Direction.UP).relative(Direction.SOUTH))) {
                     BlockState otherCarpet = reader.getBlockState(pos.relative(Direction.SOUTH).relative(facing).above());
                     if (otherCarpet.getBlock() == state.getBlock()) {
                        return this.hasColor(state, otherCarpet) && otherCarpet.getValue(StairBlock.FACING) == facing;
                     }
                  }

                  if (this.posEquals(otherPos, pos.relative(Direction.DOWN).relative(Direction.SOUTH))) {
                     BlockState otherCarpet = reader.getBlockState(pos.relative(Direction.SOUTH).relative(facing.getOpposite()).below());
                     if (otherCarpet.getBlock() == state.getBlock()) {
                        return this.hasColor(state, otherCarpet) && otherCarpet.getValue(StairBlock.FACING) == facing;
                     }
                  }

                  if (this.posEquals(otherPos, pos.relative(Direction.UP).relative(Direction.NORTH))) {
                     BlockState otherCarpet = reader.getBlockState(pos.relative(Direction.NORTH).relative(facing).above());
                     if (otherCarpet.getBlock() == state.getBlock()) {
                        return this.hasColor(state, otherCarpet) && otherCarpet.getValue(StairBlock.FACING) == facing;
                     }
                  }

                  if (this.posEquals(otherPos, pos.relative(Direction.DOWN).relative(Direction.NORTH))) {
                     BlockState otherCarpet = reader.getBlockState(pos.relative(Direction.NORTH).relative(facing.getOpposite()).below());
                     if (otherCarpet.getBlock() == state.getBlock()) {
                        return this.hasColor(state, otherCarpet) && otherCarpet.getValue(StairBlock.FACING) == facing;
                     }
                  }
               }
            } else if (face == Direction.UP) {
               if (this.posEquals(otherPos, pos.relative(facing))) {
                  BlockState otherCarpet = reader.getBlockState(pos.relative(facing).above());
                  if (otherCarpet.getBlock() == state.getBlock()) {
                     return this.hasColor(state, otherCarpet) && otherCarpet.getValue(StairBlock.FACING) == facing;
                  }
               } else if (this.posEquals(otherPos, pos.relative(facing.getClockWise()))) {
                  BlockState otherCarpet = reader.getBlockState(pos.relative(facing.getClockWise()));
                  if (otherCarpet.getBlock() == state.getBlock()) {
                     return this.hasColor(state, otherCarpet) && otherCarpet.getValue(StairBlock.FACING) == facing;
                  }
               } else if (this.posEquals(otherPos, pos.relative(facing.getCounterClockWise()))) {
                  BlockState otherCarpet = reader.getBlockState(pos.relative(facing.getCounterClockWise()));
                  if (otherCarpet.getBlock() == state.getBlock()) {
                     return this.hasColor(state, otherCarpet) && otherCarpet.getValue(StairBlock.FACING) == facing;
                  }
               } else if (this.posEquals(otherPos, pos.relative(facing).relative(facing.getClockWise()))) {
                  BlockState otherCarpet = reader.getBlockState(pos.relative(facing).relative(facing.getClockWise()).above());
                  if (otherCarpet.getBlock() == state.getBlock()) {
                     return this.hasColor(state, otherCarpet) && otherCarpet.getValue(StairBlock.FACING) == facing;
                  }
               } else if (this.posEquals(otherPos, pos.relative(facing).relative(facing.getCounterClockWise()))) {
                  BlockState otherCarpet = reader.getBlockState(pos.relative(facing).relative(facing.getCounterClockWise()).above());
                  if (otherCarpet.getBlock() == state.getBlock()) {
                     return this.hasColor(state, otherCarpet) && otherCarpet.getValue(StairBlock.FACING) == facing;
                  }
               } else if (this.posEquals(otherPos, pos.relative(facing.getOpposite()).relative(facing.getClockWise()))) {
                  BlockState otherCarpet = reader.getBlockState(pos.relative(facing.getOpposite()).relative(facing.getClockWise()).below());
                  if (otherCarpet.getBlock() == state.getBlock()) {
                     return this.hasColor(state, otherCarpet) && otherCarpet.getValue(StairBlock.FACING) == facing;
                  }
               } else if (this.posEquals(otherPos, pos.relative(facing.getOpposite()).relative(facing.getCounterClockWise()))) {
                  BlockState otherCarpet = reader.getBlockState(pos.relative(facing.getOpposite()).relative(facing.getCounterClockWise()).below());
                  if (otherCarpet.getBlock() == state.getBlock()) {
                     return this.hasColor(state, otherCarpet) && otherCarpet.getValue(StairBlock.FACING) == facing;
                  }
               } else if (this.posEquals(otherPos, pos.relative(facing.getOpposite()))) {
                  BlockState otherCarpet = reader.getBlockState(pos.relative(facing.getOpposite()).below());
                  if (otherCarpet.getBlock() == state.getBlock()) {
                     return this.hasColor(state, otherCarpet) && otherCarpet.getValue(StairBlock.FACING) == facing;
                  }
               }
            } else if (face == Direction.DOWN) {
               if (this.posEquals(otherPos, pos.relative(facing.getOpposite()))) {
                  BlockState otherCarpet = reader.getBlockState(pos.relative(facing).above());
                  if (otherCarpet.getBlock() == state.getBlock()) {
                     return this.hasColor(state, otherCarpet) && otherCarpet.getValue(StairBlock.FACING) == facing;
                  }
               } else if (this.posEquals(otherPos, pos.relative(facing.getClockWise().getOpposite()))) {
                  BlockState otherCarpet = reader.getBlockState(pos.relative(facing.getClockWise()));
                  if (otherCarpet.getBlock() == state.getBlock()) {
                     return this.hasColor(state, otherCarpet) && otherCarpet.getValue(StairBlock.FACING) == facing;
                  }
               } else if (this.posEquals(otherPos, pos.relative(facing.getCounterClockWise().getOpposite()))) {
                  BlockState otherCarpet = reader.getBlockState(pos.relative(facing.getCounterClockWise()));
                  if (otherCarpet.getBlock() == state.getBlock()) {
                     return this.hasColor(state, otherCarpet) && otherCarpet.getValue(StairBlock.FACING) == facing;
                  }
               } else if (this.posEquals(otherPos, pos.relative(facing).relative(facing.getClockWise()))) {
                  BlockState otherCarpet = reader.getBlockState(pos.relative(facing.getOpposite()).relative(facing.getCounterClockWise()).below());
                  if (otherCarpet.getBlock() == state.getBlock()) {
                     return this.hasColor(state, otherCarpet) && otherCarpet.getValue(StairBlock.FACING) == facing;
                  }
               } else if (this.posEquals(otherPos, pos.relative(facing).relative(facing.getCounterClockWise()))) {
                  BlockState otherCarpet = reader.getBlockState(pos.relative(facing.getOpposite()).relative(facing.getClockWise()).below());
                  if (otherCarpet.getBlock() == state.getBlock()) {
                     return this.hasColor(state, otherCarpet) && otherCarpet.getValue(StairBlock.FACING) == facing;
                  }
               } else if (this.posEquals(otherPos, pos.relative(facing.getOpposite()).relative(facing.getClockWise()))) {
                  BlockState otherCarpet = reader.getBlockState(pos.relative(facing).relative(facing.getCounterClockWise()).above());
                  if (otherCarpet.getBlock() == state.getBlock()) {
                     return this.hasColor(state, otherCarpet) && otherCarpet.getValue(StairBlock.FACING) == facing;
                  }
               } else if (this.posEquals(otherPos, pos.relative(facing.getOpposite()).relative(facing.getCounterClockWise()))) {
                  BlockState otherCarpet = reader.getBlockState(pos.relative(facing).relative(facing.getClockWise()).above());
                  if (otherCarpet.getBlock() == state.getBlock()) {
                     return this.hasColor(state, otherCarpet) && otherCarpet.getValue(StairBlock.FACING) == facing;
                  }
               } else if (this.posEquals(otherPos, pos.relative(facing))) {
                  BlockState otherCarpet = reader.getBlockState(pos.relative(facing.getOpposite()).below());
                  if (otherCarpet.getBlock() == state.getBlock()) {
                     return this.hasColor(state, otherCarpet) && otherCarpet.getValue(StairBlock.FACING) == facing;
                  }
               }
            }
         } else if (this.posEquals(otherPos, pos.above())) {
            BlockState otherCarpet = reader.getBlockState(pos.relative(facing).above());
            if (otherCarpet.getBlock() == state.getBlock()) {
               return this.hasColor(state, otherCarpet) && otherCarpet.getValue(StairBlock.FACING) == facing;
            }
         } else if (this.posEquals(otherPos, pos.below())) {
            BlockState otherCarpet = reader.getBlockState(pos.relative(facing.getOpposite()).below());
            if (otherCarpet.getBlock() == state.getBlock()) {
               return this.hasColor(state, otherCarpet) && otherCarpet.getValue(StairBlock.FACING) == facing;
            }
         }
      }

      return false;
   }

   private boolean hasColor(BlockState state1, BlockState state2) {
      return state1.hasProperty(ConnectingCarpetDyed.COLOR) && state2.hasProperty(ConnectingCarpetDyed.COLOR)
         ? state1.getValue(ConnectingCarpetDyed.COLOR) == state2.getValue(ConnectingCarpetDyed.COLOR)
         : false;
   }

   public CarpetStairsCTBehaviour(CTSpriteShiftEntry layerShift, CTSpriteShiftEntry topShift) {
      this.layerShift = layerShift;
      this.topShift = topShift;
   }

   @Override
   public CTSpriteShiftEntry getShift(BlockState state, Direction direction, @Nullable TextureAtlasSprite sprite) {
      return direction.getAxis().isHorizontal() ? this.layerShift : this.topShift;
   }
}
