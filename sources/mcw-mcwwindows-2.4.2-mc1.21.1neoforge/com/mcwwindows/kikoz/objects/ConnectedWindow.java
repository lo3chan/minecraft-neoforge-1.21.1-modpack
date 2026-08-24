package com.mcwwindows.kikoz.objects;

import com.mcwwindows.kikoz.init.ItemInit;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ConnectedWindow extends Block {
   public static final DirectionProperty FACING = DirectionProperty.create("facing", new Direction[]{Direction.NORTH, Direction.EAST});
   public static final EnumProperty<ConnectedWindow.ConnectionStatus> PART = EnumProperty.create("part", ConnectedWindow.ConnectionStatus.class);
   protected static final VoxelShape EE = box(0.0, 0.0, 7.0, 16.0, 16.0, 9.0);
   protected static final VoxelShape NN = box(7.0, 0.0, 0.0, 9.0, 16.0, 16.0);
   private boolean wasInteractedWith = false;

   public ConnectedWindow(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH))
            .setValue(PART, ConnectedWindow.ConnectionStatus.BASE)
      );
   }

   public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
      return ((Direction)state.getValue(FACING)).getAxis() == Axis.X ? NN : EE;
   }

   protected BlockState WindowState(BlockState state, LevelAccessor level, BlockPos pos) {
      boolean above = level.getBlockState(pos.above()).getBlock() == this;
      boolean below = level.getBlockState(pos.below()).getBlock() == this;
      boolean north = level.getBlockState(pos.north()).getBlock() == this;
      boolean east = level.getBlockState(pos.east()).getBlock() == this;
      boolean south = level.getBlockState(pos.south()).getBlock() == this;
      boolean west = level.getBlockState(pos.west()).getBlock() == this;
      switch ((Direction)state.getValue(FACING)) {
         case NORTH:
            if (!above && below) {
               if (east && west) {
                  return (BlockState)state.setValue(PART, ConnectedWindow.ConnectionStatus.TOP_M);
               } else if (!east && west) {
                  return (BlockState)state.setValue(PART, ConnectedWindow.ConnectionStatus.TOP_L);
               } else {
                  if (east && !west) {
                     return (BlockState)state.setValue(PART, ConnectedWindow.ConnectionStatus.TOP_R);
                  }

                  return (BlockState)state.setValue(PART, ConnectedWindow.ConnectionStatus.TOP);
               }
            } else if (above && below) {
               if (east && west) {
                  return (BlockState)state.setValue(PART, ConnectedWindow.ConnectionStatus.MID_M);
               } else if (!east && west) {
                  return (BlockState)state.setValue(PART, ConnectedWindow.ConnectionStatus.MID_L);
               } else {
                  if (east && !west) {
                     return (BlockState)state.setValue(PART, ConnectedWindow.ConnectionStatus.MID_R);
                  }

                  return (BlockState)state.setValue(PART, ConnectedWindow.ConnectionStatus.MIDDLE);
               }
            } else if (above && !below) {
               if (east && west) {
                  return (BlockState)state.setValue(PART, ConnectedWindow.ConnectionStatus.BOT_M);
               } else if (!east && west) {
                  return (BlockState)state.setValue(PART, ConnectedWindow.ConnectionStatus.BOT_L);
               } else {
                  if (east && !west) {
                     return (BlockState)state.setValue(PART, ConnectedWindow.ConnectionStatus.BOT_R);
                  }

                  return (BlockState)state.setValue(PART, ConnectedWindow.ConnectionStatus.BOTTOM);
               }
            } else if (!above && !below) {
               if (east && west) {
                  return (BlockState)state.setValue(PART, ConnectedWindow.ConnectionStatus.SINGLE_M);
               } else if (!east && west) {
                  return (BlockState)state.setValue(PART, ConnectedWindow.ConnectionStatus.SINGLE_L);
               } else {
                  if (east && !west) {
                     return (BlockState)state.setValue(PART, ConnectedWindow.ConnectionStatus.SINGLE_R);
                  }

                  return (BlockState)state.setValue(PART, ConnectedWindow.ConnectionStatus.BASE);
               }
            }
         case EAST:
            if (!above && below) {
               if (north && south) {
                  return (BlockState)state.setValue(PART, ConnectedWindow.ConnectionStatus.TOP_M);
               } else if (!north && south) {
                  return (BlockState)state.setValue(PART, ConnectedWindow.ConnectionStatus.TOP_R);
               } else {
                  if (north && !south) {
                     return (BlockState)state.setValue(PART, ConnectedWindow.ConnectionStatus.TOP_L);
                  }

                  return (BlockState)state.setValue(PART, ConnectedWindow.ConnectionStatus.TOP);
               }
            } else if (above && below) {
               if (north && south) {
                  return (BlockState)state.setValue(PART, ConnectedWindow.ConnectionStatus.MID_M);
               } else if (!north && south) {
                  return (BlockState)state.setValue(PART, ConnectedWindow.ConnectionStatus.MID_R);
               } else {
                  if (north && !south) {
                     return (BlockState)state.setValue(PART, ConnectedWindow.ConnectionStatus.MID_L);
                  }

                  return (BlockState)state.setValue(PART, ConnectedWindow.ConnectionStatus.MIDDLE);
               }
            } else if (above && !below) {
               if (north && south) {
                  return (BlockState)state.setValue(PART, ConnectedWindow.ConnectionStatus.BOT_M);
               } else if (!north && south) {
                  return (BlockState)state.setValue(PART, ConnectedWindow.ConnectionStatus.BOT_R);
               } else {
                  if (north && !south) {
                     return (BlockState)state.setValue(PART, ConnectedWindow.ConnectionStatus.BOT_L);
                  }

                  return (BlockState)state.setValue(PART, ConnectedWindow.ConnectionStatus.BOTTOM);
               }
            } else if (!above && !below) {
               if (north && south) {
                  return (BlockState)state.setValue(PART, ConnectedWindow.ConnectionStatus.SINGLE_M);
               } else if (!north && south) {
                  return (BlockState)state.setValue(PART, ConnectedWindow.ConnectionStatus.SINGLE_R);
               } else {
                  if (north && !south) {
                     return (BlockState)state.setValue(PART, ConnectedWindow.ConnectionStatus.SINGLE_L);
                  }

                  return (BlockState)state.setValue(PART, ConnectedWindow.ConnectionStatus.BASE);
               }
            }
         default:
            return (BlockState)state.setValue(PART, ConnectedWindow.ConnectionStatus.BASE);
      }
   }

   public ItemInteractionResult useItemOn(
      ItemStack itemstack, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit
   ) {
      Item item = itemstack.getItem();
      if (item != ItemInit.HAMMER.get() && item != Items.SHEARS) {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      } else {
         BlockState newState = (BlockState)state.cycle(PART);
         worldIn.setBlockAndUpdate(pos, newState);
         this.setWasInteractedWith(true, worldIn, pos);
         return ItemInteractionResult.SUCCESS;
      }
   }

   public void setWasInteractedWith(boolean interacted, Level level, BlockPos pos) {
      this.wasInteractedWith = interacted;
   }

   public void onPlace(BlockState state, Level level, BlockPos pos, BlockState statetwo, boolean bool) {
      if (!statetwo.is(state.getBlock())) {
         level.setBlock(pos, this.WindowState(state, level, pos), 2);
         this.wasInteractedWith = false;
      }
   }

   public BlockState updateShape(BlockState state, Direction dir, BlockState statetwo, LevelAccessor access, BlockPos pos, BlockPos postwo) {
      return this.wasInteractedWith ? state : this.WindowState(state, access, pos);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{PART, FACING});
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext contx) {
      Direction facingDirection = contx.getHorizontalDirection();
      LevelAccessor world = contx.getLevel();
      if (facingDirection == Direction.WEST) {
         facingDirection = Direction.EAST;
      } else if (facingDirection == Direction.SOUTH) {
         facingDirection = Direction.NORTH;
      }

      return (BlockState)this.WindowState(super.getStateForPlacement(contx), world, contx.getClickedPos()).setValue(FACING, facingDirection);
   }

   public static enum ConnectionStatus implements StringRepresentable {
      BASE("base"),
      TOP("top"),
      MIDDLE("middle"),
      BOTTOM("bottom"),
      TOP_L("top_l"),
      TOP_M("top_m"),
      TOP_R("top_r"),
      MID_L("mid_l"),
      MID_M("mid_m"),
      MID_R("mid_r"),
      BOT_L("bot_l"),
      BOT_M("bot_m"),
      BOT_R("bot_r"),
      SINGLE_L("single_l"),
      SINGLE_M("single_m"),
      SINGLE_R("single_r");

      private final String name;

      private ConnectionStatus(String name) {
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }
   }
}
