package com.mcwroofs.kikoz.objects.roofs;

import com.mcwroofs.kikoz.init.ItemInit;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RoofTopNew extends Block {
   private static final VoxelShape BASE = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
   protected static final VoxelShape OCCLUSION = Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
   private static final EnumProperty<RoofTopNew.RoofPart> PART = EnumProperty.create("part", RoofTopNew.RoofPart.class);

   public RoofTopNew(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(PART, RoofTopNew.RoofPart.SWITCHED_0));
   }

   public void onBroken(Level worldIn, BlockPos pos) {
      worldIn.levelEvent(1029, pos, 0);
   }

   public VoxelShape getOcclusionShape(BlockState state, BlockGetter getter, BlockPos pos) {
      return OCCLUSION;
   }

   protected boolean useShapeForLightOcclusion(BlockState p_56395_) {
      return true;
   }

   public VoxelShape getShape(BlockState state, BlockGetter blockReader, BlockPos pos, CollisionContext selectionContext) {
      return BASE;
   }

   private BlockState StairState(BlockState state, LevelAccessor access, BlockPos pos) {
      boolean north = access.getBlockState(pos.north()).getBlock() == this;
      boolean east = access.getBlockState(pos.east()).getBlock() == this;
      boolean south = access.getBlockState(pos.south()).getBlock() == this;
      boolean west = access.getBlockState(pos.west()).getBlock() == this;
      RoofTopNew.RoofPart connection = this.getRoofPart(north, east, south, west);
      return (BlockState)state.setValue(PART, connection);
   }

   private RoofTopNew.RoofPart getRoofPart(boolean north, boolean east, boolean south, boolean west) {
      if (!north && !south && east && west) {
         return RoofTopNew.RoofPart.SWITCHED_90;
      } else if (north && south && east && west) {
         return RoofTopNew.RoofPart.FOUR_WAY;
      } else if (north && !south && !east && west) {
         return RoofTopNew.RoofPart.TOP_OUTER_0;
      } else if (north && !south && east && !west) {
         return RoofTopNew.RoofPart.TOP_OUTER_90;
      } else if (!north && south && east && !west) {
         return RoofTopNew.RoofPart.TOP_OUTER_180;
      } else if (!north && south && !east && west) {
         return RoofTopNew.RoofPart.TOP_OUTER_270;
      } else if (north && !south && !east && !west) {
         return RoofTopNew.RoofPart.TOP_END_0;
      } else if (!north && !south && east && !west) {
         return RoofTopNew.RoofPart.TOP_END_90;
      } else if (!north && south && !east && !west) {
         return RoofTopNew.RoofPart.TOP_END_180;
      } else if (!north && !south && !east && west) {
         return RoofTopNew.RoofPart.TOP_END_270;
      } else if (north && south && east && !west) {
         return RoofTopNew.RoofPart.THREE_WAY_0;
      } else if (!north && south && east && west) {
         return RoofTopNew.RoofPart.THREE_WAY_90;
      } else if (north && south && !east && west) {
         return RoofTopNew.RoofPart.THREE_WAY_180;
      } else {
         return north && !south && east && west ? RoofTopNew.RoofPart.THREE_WAY_270 : RoofTopNew.RoofPart.SWITCHED_0;
      }
   }

   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   public BlockState updateShape(BlockState state, Direction dir, BlockState statetwo, LevelAccessor access, BlockPos pos, BlockPos postwo) {
      return this.StairState(state, access, pos);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext contx) {
      BlockPos pos = contx.getClickedPos().below();
      LevelAccessor world = contx.getLevel();
      BlockState stateBelow = world.getBlockState(pos);
      return stateBelow.getBlock() instanceof RoofTopNew ? null : this.StairState(super.getStateForPlacement(contx), world, contx.getClickedPos());
   }

   public void onPlace(BlockState state, Level level, BlockPos pos, BlockState statetwo, boolean bool) {
      if (!statetwo.is(state.getBlock())) {
         level.setBlock(pos, this.StairState(state, level, pos), 2);
      }
   }

   public ItemInteractionResult useItemOn(
      ItemStack itemstack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit
   ) {
      Item item = itemstack.getItem();
      RoofTopNew.RoofPart connection = (RoofTopNew.RoofPart)state.getValue(PART);
      if (item == ItemInit.ROOFING_HAMMER.get()) {
         RoofTopNew.RoofPart newConnection = RoofTopNew.RoofPart.PYRAMID;
         switch (connection) {
            case PYRAMID:
               newConnection = RoofTopNew.RoofPart.SWITCHED_0;
               break;
            case SWITCHED_0:
               newConnection = RoofTopNew.RoofPart.SWITCHED_90;
               break;
            case SWITCHED_90:
               newConnection = RoofTopNew.RoofPart.FOUR_WAY;
               break;
            case FOUR_WAY:
               newConnection = RoofTopNew.RoofPart.TOP_END_0;
               break;
            case TOP_END_0:
               newConnection = RoofTopNew.RoofPart.TOP_END_90;
               break;
            case TOP_END_90:
               newConnection = RoofTopNew.RoofPart.TOP_END_180;
               break;
            case TOP_END_180:
               newConnection = RoofTopNew.RoofPart.TOP_END_270;
               break;
            case TOP_END_270:
               newConnection = RoofTopNew.RoofPart.TOP_OUTER_0;
               break;
            case TOP_OUTER_0:
               newConnection = RoofTopNew.RoofPart.TOP_OUTER_90;
               break;
            case TOP_OUTER_90:
               newConnection = RoofTopNew.RoofPart.TOP_OUTER_180;
               break;
            case TOP_OUTER_180:
               newConnection = RoofTopNew.RoofPart.TOP_OUTER_270;
               break;
            case TOP_OUTER_270:
               newConnection = RoofTopNew.RoofPart.THREE_WAY_0;
               break;
            case THREE_WAY_0:
               newConnection = RoofTopNew.RoofPart.THREE_WAY_90;
               break;
            case THREE_WAY_90:
               newConnection = RoofTopNew.RoofPart.THREE_WAY_180;
               break;
            case THREE_WAY_180:
               newConnection = RoofTopNew.RoofPart.THREE_WAY_270;
               break;
            case THREE_WAY_270:
               newConnection = RoofTopNew.RoofPart.PYRAMID;
         }

         level.setBlock(pos, (BlockState)state.setValue(PART, newConnection), 4);
         return ItemInteractionResult.SUCCESS;
      } else {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{PART});
   }

   public void placeAt(Level world, BlockPos pos, int intos) {
      world.setBlock(pos, this.defaultBlockState(), intos);
   }

   public static enum RoofPart implements StringRepresentable {
      PYRAMID("pyramid"),
      SWITCHED_0("switched_0"),
      SWITCHED_90("switched_90"),
      FOUR_WAY("four_way"),
      TOP_END_0("top_end_0"),
      TOP_END_90("top_end_90"),
      TOP_END_180("top_end_180"),
      TOP_END_270("top_end_270"),
      TOP_OUTER_0("top_outer_0"),
      TOP_OUTER_90("top_outer_90"),
      TOP_OUTER_180("top_outer_180"),
      TOP_OUTER_270("top_outer_270"),
      THREE_WAY_0("three_way_0"),
      THREE_WAY_90("three_way_90"),
      THREE_WAY_180("three_way_180"),
      THREE_WAY_270("three_way_270");

      private final String name;

      private RoofPart(final String name) {
         this.name = name;
      }

      public String getName() {
         return this.name;
      }

      public String getString() {
         return this.name;
      }

      public String getSerializedName() {
         return this.name;
      }
   }
}
