package com.mcwbridges.kikoz.objects;

import com.mcwbridges.kikoz.init.ItemInit;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Bridge_Block extends Block {
   public static final EnumProperty<Bridge_Block.ConnectionStatus> CONNECTION = EnumProperty.create("connection", Bridge_Block.ConnectionStatus.class);
   protected static final VoxelShape BASE = Block.box(0.0, 0.0, 0.0, 15.99, 4.0, 15.99);
   protected static final VoxelShape SIDE_0 = Shapes.or(BASE, Block.box(0.0, 3.0, 13.0, 16.0, 16.0, 16.0));
   protected static final VoxelShape SIDE_90 = Shapes.or(BASE, Block.box(0.0, 3.0, 0.0, 3.0, 16.0, 16.0));
   protected static final VoxelShape SIDE_180 = Shapes.or(BASE, Block.box(0.0, 3.0, 0.0, 16.0, 16.0, 3.0));
   protected static final VoxelShape SIDE_270 = Shapes.or(BASE, Block.box(13.0, 3.0, 0.0, 16.0, 16.0, 16.0));
   protected static final VoxelShape CORNER_0 = Shapes.or(SIDE_180, SIDE_90);
   protected static final VoxelShape CORNER_90 = Shapes.or(SIDE_180, SIDE_270);
   protected static final VoxelShape CORNER_180 = Shapes.or(SIDE_270, SIDE_0);
   protected static final VoxelShape CORNER_270 = Shapes.or(SIDE_0, SIDE_90);
   protected static final VoxelShape MIDDLE_90 = Shapes.or(SIDE_0, SIDE_180);
   protected static final VoxelShape MIDDLE_0 = Shapes.or(SIDE_90, SIDE_270);
   protected static final VoxelShape BASE_COLLISION = Block.box(0.0, 0.0, 0.0, 15.99, 3.0, 15.99);
   protected static final VoxelShape COLLISION_SIDE_0 = Shapes.or(BASE, Block.box(0.0, 2.0, 15.0, 16.0, 25.0, 16.0));
   protected static final VoxelShape COLLISION_SIDE_90 = Shapes.or(BASE, Block.box(0.0, 2.0, 0.0, 1.0, 25.0, 16.0));
   protected static final VoxelShape COLLISION_SIDE_180 = Shapes.or(BASE, Block.box(0.0, 2.0, 0.0, 16.0, 25.0, 1.0));
   protected static final VoxelShape COLLISION_SIDE_270 = Shapes.or(BASE, Block.box(15.0, 2.0, 0.0, 16.0, 25.0, 16.0));
   protected static final VoxelShape COLLISION_CORNER_0 = Shapes.or(COLLISION_SIDE_180, COLLISION_SIDE_90);
   protected static final VoxelShape COLLISION_CORNER_90 = Shapes.or(COLLISION_SIDE_180, COLLISION_SIDE_270);
   protected static final VoxelShape COLLISION_CORNER_180 = Shapes.or(COLLISION_SIDE_270, COLLISION_SIDE_0);
   protected static final VoxelShape COLLISION_CORNER_270 = Shapes.or(COLLISION_SIDE_0, COLLISION_SIDE_90);
   protected static final VoxelShape COLLISION_MIDDLE_90 = Shapes.or(COLLISION_SIDE_0, COLLISION_SIDE_180);
   protected static final VoxelShape COLLISION_MIDDLE_0 = Shapes.or(COLLISION_SIDE_90, COLLISION_SIDE_270);

   public Bridge_Block(Properties prop) {
      super(prop);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(CONNECTION, Bridge_Block.ConnectionStatus.BASE));
   }

   public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext contx) {
      switch ((Bridge_Block.ConnectionStatus)state.getValue(CONNECTION)) {
         case BASE:
            return BASE;
         case MIDDLE_NS:
            return MIDDLE_90;
         case MIDDLE_EW:
            return MIDDLE_0;
         case CORNER_NE:
            return CORNER_270;
         case CORNER_NW:
            return CORNER_180;
         case CORNER_SE:
            return CORNER_0;
         case CORNER_SW:
            return CORNER_90;
         case SIDE_N:
            return SIDE_180;
         case SIDE_E:
            return SIDE_270;
         case SIDE_S:
            return SIDE_0;
         case SIDE_W:
            return SIDE_90;
         default:
            return BASE;
      }
   }

   public VoxelShape getCollisionShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext contx) {
      switch ((Bridge_Block.ConnectionStatus)state.getValue(CONNECTION)) {
         case BASE:
            return BASE;
         case MIDDLE_NS:
            return COLLISION_MIDDLE_90;
         case MIDDLE_EW:
            return COLLISION_MIDDLE_0;
         case CORNER_NE:
            return COLLISION_CORNER_270;
         case CORNER_NW:
            return COLLISION_CORNER_180;
         case CORNER_SE:
            return COLLISION_CORNER_0;
         case CORNER_SW:
            return COLLISION_CORNER_90;
         case SIDE_N:
            return COLLISION_SIDE_180;
         case SIDE_E:
            return COLLISION_SIDE_270;
         case SIDE_S:
            return COLLISION_SIDE_0;
         case SIDE_W:
            return COLLISION_SIDE_90;
         default:
            return BASE_COLLISION;
      }
   }

   private BlockState StairState(BlockState state, LevelAccessor access, BlockPos pos) {
      boolean north = access.getBlockState(pos.north()).getBlock() == this;
      boolean east = access.getBlockState(pos.east()).getBlock() == this;
      boolean south = access.getBlockState(pos.south()).getBlock() == this;
      boolean west = access.getBlockState(pos.west()).getBlock() == this;
      Bridge_Block.ConnectionStatus connection = this.getConnectionStatus(north, east, south, west);
      return (BlockState)state.setValue(CONNECTION, connection);
   }

   private Bridge_Block.ConnectionStatus getConnectionStatus(boolean north, boolean east, boolean south, boolean west) {
      if (!north && !south && !east && !west) {
         return Bridge_Block.ConnectionStatus.MIDDLE_NS;
      } else if (north && !east && south && !west) {
         return Bridge_Block.ConnectionStatus.MIDDLE_EW;
      } else if (!north && east && !south && west) {
         return Bridge_Block.ConnectionStatus.MIDDLE_NS;
      } else if (!north && !east && south && !west) {
         return Bridge_Block.ConnectionStatus.MIDDLE_EW;
      } else if (north && !east && !south && !west) {
         return Bridge_Block.ConnectionStatus.MIDDLE_EW;
      } else if (!north && !east && !south && west) {
         return Bridge_Block.ConnectionStatus.MIDDLE_NS;
      } else if (!north && east && !south && !west) {
         return Bridge_Block.ConnectionStatus.MIDDLE_NS;
      } else if (!north && !east && south && west) {
         return Bridge_Block.ConnectionStatus.CORNER_SW;
      } else if (!north && east && south && !west) {
         return Bridge_Block.ConnectionStatus.CORNER_SE;
      } else if (north && !east && !south && west) {
         return Bridge_Block.ConnectionStatus.CORNER_NW;
      } else if (north && east && !south && !west) {
         return Bridge_Block.ConnectionStatus.CORNER_NE;
      } else if (!north && east && south && west) {
         return Bridge_Block.ConnectionStatus.SIDE_N;
      } else if (north && !east && south && west) {
         return Bridge_Block.ConnectionStatus.SIDE_E;
      } else if (north && east && !south && west) {
         return Bridge_Block.ConnectionStatus.SIDE_S;
      } else {
         return north && east && south && !west ? Bridge_Block.ConnectionStatus.SIDE_W : Bridge_Block.ConnectionStatus.BASE;
      }
   }

   public ItemInteractionResult useItemOn(
      ItemStack itemstack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit
   ) {
      Item item = itemstack.getItem();
      if (item != ItemInit.PLIERS.get() && item != Items.SHEARS) {
         if (itemstack.getItem() instanceof BlockItem) {
            BlockItem blockItem = (BlockItem)itemstack.getItem();
            if (blockItem.getBlock() == this) {
               Direction facing = player.getDirection();
               BlockPos placePos = pos.relative(facing);
               if (level.getBlockState(placePos).isAir()) {
                  level.setBlock(placePos, this.defaultBlockState(), 3);
                  if (!player.getAbilities().instabuild) {
                     itemstack.shrink(1);
                  }

                  level.playSound(null, pos, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                  return ItemInteractionResult.SUCCESS;
               }
            }
         }

         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      } else {
         BlockState newState = (BlockState)state.cycle(CONNECTION);
         level.setBlock(pos, newState, 18);
         return ItemInteractionResult.SUCCESS;
      }
   }

   public void onPlace(BlockState state, Level level, BlockPos pos, BlockState statetwo, boolean bool) {
      if (!statetwo.is(state.getBlock())) {
         level.setBlock(pos, this.StairState(state, level, pos), 2);
      }
   }

   public BlockState updateShape(BlockState state, Direction dir, BlockState facingState, LevelAccessor access, BlockPos pos, BlockPos facingPos) {
      return dir.getAxis().isHorizontal() && facingState.getBlock() instanceof Bridge_Block ? this.StairState(state, access, pos) : state;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{CONNECTION});
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext contx) {
      BlockPos pos = contx.getClickedPos().below();
      LevelAccessor world = contx.getLevel();
      BlockState stateBelow = world.getBlockState(pos);
      return stateBelow.getBlock() instanceof Bridge_Block ? null : this.StairState(super.getStateForPlacement(contx), world, contx.getClickedPos());
   }

   public static enum ConnectionStatus implements StringRepresentable {
      BASE("base"),
      MIDDLE_NS("middle_ns"),
      MIDDLE_EW("middle_ew"),
      CORNER_NE("corner_ne"),
      CORNER_NW("corner_nw"),
      CORNER_SE("corner_se"),
      CORNER_SW("corner_sw"),
      SIDE_N("side_n"),
      SIDE_E("side_e"),
      SIDE_S("side_s"),
      SIDE_W("side_w");

      private final String name;

      private ConnectionStatus(String name) {
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }
   }
}
