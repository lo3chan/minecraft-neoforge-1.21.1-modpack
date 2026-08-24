package net.joefoxe.hexerei.block.custom;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.connected.CTDyable;
import net.joefoxe.hexerei.block.connected.Waxed;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.ItemAbility;

public class ConnectingCarpetSlab extends CarpetBlock implements Waxed, CTDyable {
   protected static final VoxelShape SHAPE = Block.box(0.0, -8.0, 0.0, 16.0, -7.0, 16.0);
   public static BooleanProperty WEST = BooleanProperty.create("west");
   public static BooleanProperty EAST = BooleanProperty.create("east");
   public static final EnumProperty<ConnectingCarpetSlab.North> NORTH = EnumProperty.create("north", ConnectingCarpetSlab.North.class);
   public static final EnumProperty<ConnectingCarpetSlab.South> SOUTH = EnumProperty.create("south", ConnectingCarpetSlab.South.class);
   public static final EnumProperty<DyeColor> COLOR = EnumProperty.create("color", DyeColor.class);
   public Block parentBlock;

   public ConnectingCarpetSlab(Properties pProperties, Block parentBlock) {
      super(pProperties.noOcclusion());
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)super.defaultBlockState().setValue(WEST, false)).setValue(EAST, false))
               .setValue(NORTH, ConnectingCarpetSlab.North.NONE))
            .setValue(SOUTH, ConnectingCarpetSlab.South.NONE)
      );
      this.parentBlock = parentBlock;
   }

   public VoxelShape getShape(BlockState p_152917_, BlockGetter p_152918_, BlockPos p_152919_, CollisionContext p_152920_) {
      return SHAPE;
   }

   @Override
   public DyeColor getDyeColor(BlockState blockState) {
      return blockState.hasProperty(COLOR) ? (DyeColor)blockState.getValue(COLOR) : DyeColor.WHITE;
   }

   public List<ItemStack> getDrops(BlockState pState, net.minecraft.world.level.storage.loot.LootParams.Builder pParams) {
      List<ItemStack> drops = super.getDrops(pState, pParams);
      if (!pState.hasProperty(COLOR)) {
         return drops;
      } else {
         List<ItemStack> updated_drops = new ArrayList<>();

         for (ItemStack stack : drops) {
            if (stack.getItem() == ((ConnectingCarpetDyed)ModBlocks.INFUSED_FABRIC_CARPET.get()).asItem()
               || stack.getItem() == ((ConnectingCarpetDyed)ModBlocks.WAXED_INFUSED_FABRIC_CARPET.get()).asItem()) {
               DyeColor color = (DyeColor)pState.getValue(COLOR);
               CompoundTag tag = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
               tag.putString("color", color.getName());
               stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            }

            updated_drops.add(stack);
         }

         return updated_drops;
      }
   }

   public BlockState rotate(BlockState pState, Rotation pRot) {
      boolean east = (Boolean)pState.getValue(EAST);
      boolean west = (Boolean)pState.getValue(WEST);
      ConnectingCarpetSlab.North northState = (ConnectingCarpetSlab.North)pState.getValue(NORTH);
      ConnectingCarpetSlab.South southState = (ConnectingCarpetSlab.South)pState.getValue(SOUTH);
      boolean north = northState == ConnectingCarpetSlab.North.ALL
         || northState == ConnectingCarpetSlab.North.JUST_NORTH
         || northState == ConnectingCarpetSlab.North.NORTH_AND_NORTH_EAST
         || northState == ConnectingCarpetSlab.North.NORTH_AND_NORTH_WEST;
      boolean north_east = northState == ConnectingCarpetSlab.North.ALL
         || northState == ConnectingCarpetSlab.North.JUST_NORTH_EAST
         || northState == ConnectingCarpetSlab.North.NORTH_AND_NORTH_EAST
         || northState == ConnectingCarpetSlab.North.NORTH_EAST_AND_NORTH_WEST;
      boolean north_west = northState == ConnectingCarpetSlab.North.ALL
         || northState == ConnectingCarpetSlab.North.JUST_NORTH_WEST
         || northState == ConnectingCarpetSlab.North.NORTH_AND_NORTH_WEST
         || northState == ConnectingCarpetSlab.North.NORTH_EAST_AND_NORTH_WEST;
      boolean south = southState == ConnectingCarpetSlab.South.ALL
         || southState == ConnectingCarpetSlab.South.JUST_SOUTH
         || southState == ConnectingCarpetSlab.South.SOUTH_AND_SOUTH_EAST
         || southState == ConnectingCarpetSlab.South.SOUTH_AND_SOUTH_WEST;
      boolean south_east = southState == ConnectingCarpetSlab.South.ALL
         || southState == ConnectingCarpetSlab.South.JUST_SOUTH_EAST
         || southState == ConnectingCarpetSlab.South.SOUTH_AND_SOUTH_EAST
         || southState == ConnectingCarpetSlab.South.SOUTH_EAST_AND_SOUTH_WEST;
      boolean south_west = southState == ConnectingCarpetSlab.South.ALL
         || southState == ConnectingCarpetSlab.South.JUST_SOUTH_WEST
         || southState == ConnectingCarpetSlab.South.SOUTH_AND_SOUTH_WEST
         || southState == ConnectingCarpetSlab.South.SOUTH_EAST_AND_SOUTH_WEST;
      switch (pRot) {
         case NONE:
            return pState;
         case CLOCKWISE_90:
            ConnectingCarpetSlab.North northTempxx = ConnectingCarpetSlab.North.NONE;
            ConnectingCarpetSlab.South southTempxx = ConnectingCarpetSlab.South.NONE;
            if (south_east && east && north_east) {
               southTempxx = ConnectingCarpetSlab.South.ALL;
            } else if (!south_east && east && north_east) {
               southTempxx = ConnectingCarpetSlab.South.SOUTH_AND_SOUTH_EAST;
            } else if (south_east && east) {
               southTempxx = ConnectingCarpetSlab.South.SOUTH_AND_SOUTH_WEST;
            } else if (south_east && north_east) {
               southTempxx = ConnectingCarpetSlab.South.SOUTH_EAST_AND_SOUTH_WEST;
            } else if (!south_east && east) {
               southTempxx = ConnectingCarpetSlab.South.JUST_SOUTH;
            } else if (!south_east && north_east) {
               southTempxx = ConnectingCarpetSlab.South.JUST_SOUTH_EAST;
            } else if (south_east) {
               southTempxx = ConnectingCarpetSlab.South.JUST_SOUTH_WEST;
            }

            if (south_west && west && north_west) {
               northTempxx = ConnectingCarpetSlab.North.ALL;
            } else if (!south_west && west && north_west) {
               northTempxx = ConnectingCarpetSlab.North.NORTH_AND_NORTH_EAST;
            } else if (south_west && west) {
               northTempxx = ConnectingCarpetSlab.North.NORTH_AND_NORTH_WEST;
            } else if (south_west && north_west) {
               northTempxx = ConnectingCarpetSlab.North.NORTH_EAST_AND_NORTH_WEST;
            } else if (!south_west && west) {
               northTempxx = ConnectingCarpetSlab.North.JUST_NORTH;
            } else if (!south_west && north_west) {
               northTempxx = ConnectingCarpetSlab.North.JUST_NORTH_EAST;
            } else if (south_west) {
               northTempxx = ConnectingCarpetSlab.North.JUST_NORTH_WEST;
            }

            return (BlockState)((BlockState)((BlockState)((BlockState)pState.setValue(EAST, north)).setValue(WEST, south)).setValue(NORTH, northTempxx))
               .setValue(SOUTH, southTempxx);
         case CLOCKWISE_180:
            ConnectingCarpetSlab.North northTempx = ConnectingCarpetSlab.North.NONE;
            ConnectingCarpetSlab.South southTempx = ConnectingCarpetSlab.South.NONE;
            if (north && north_east && north_west) {
               southTempx = ConnectingCarpetSlab.South.ALL;
            } else if (north && north_west) {
               southTempx = ConnectingCarpetSlab.South.SOUTH_AND_SOUTH_EAST;
            } else if (north && north_east) {
               southTempx = ConnectingCarpetSlab.South.SOUTH_AND_SOUTH_WEST;
            } else if (north_west && north_east) {
               southTempx = ConnectingCarpetSlab.South.SOUTH_EAST_AND_SOUTH_WEST;
            } else if (!north_west && !north_east && north) {
               southTempx = ConnectingCarpetSlab.South.JUST_SOUTH;
            } else if (north_west) {
               southTempx = ConnectingCarpetSlab.South.JUST_SOUTH_EAST;
            } else if (north_east) {
               southTempx = ConnectingCarpetSlab.South.JUST_SOUTH_WEST;
            }

            if (south && south_east && south_west) {
               northTempx = ConnectingCarpetSlab.North.ALL;
            } else if (south && south_west) {
               northTempx = ConnectingCarpetSlab.North.NORTH_AND_NORTH_EAST;
            } else if (south && south_east) {
               northTempx = ConnectingCarpetSlab.North.NORTH_AND_NORTH_WEST;
            } else if (south_west && south_east) {
               northTempx = ConnectingCarpetSlab.North.NORTH_EAST_AND_NORTH_WEST;
            } else if (!south_west && !south_east && south) {
               northTempx = ConnectingCarpetSlab.North.JUST_NORTH;
            } else if (south_west) {
               northTempx = ConnectingCarpetSlab.North.JUST_NORTH_EAST;
            } else if (south_east) {
               northTempx = ConnectingCarpetSlab.North.JUST_NORTH_WEST;
            }

            return (BlockState)((BlockState)((BlockState)((BlockState)pState.setValue(EAST, west)).setValue(WEST, east)).setValue(NORTH, northTempx))
               .setValue(SOUTH, southTempx);
         case COUNTERCLOCKWISE_90:
            ConnectingCarpetSlab.North northTemp = ConnectingCarpetSlab.North.NONE;
            ConnectingCarpetSlab.South southTemp = ConnectingCarpetSlab.South.NONE;
            if (north_west && west && south_west) {
               southTemp = ConnectingCarpetSlab.South.ALL;
            } else if (!north_west && west && south_west) {
               southTemp = ConnectingCarpetSlab.South.SOUTH_AND_SOUTH_EAST;
            } else if (north_west && west) {
               southTemp = ConnectingCarpetSlab.South.SOUTH_AND_SOUTH_WEST;
            } else if (north_west && south_west) {
               southTemp = ConnectingCarpetSlab.South.SOUTH_EAST_AND_SOUTH_WEST;
            } else if (!north_west && west) {
               southTemp = ConnectingCarpetSlab.South.JUST_SOUTH;
            } else if (!north_west && south_west) {
               southTemp = ConnectingCarpetSlab.South.JUST_SOUTH_EAST;
            } else if (north_west) {
               southTemp = ConnectingCarpetSlab.South.JUST_SOUTH_WEST;
            }

            if (north_east && east && south_east) {
               northTemp = ConnectingCarpetSlab.North.ALL;
            } else if (!north_east && east && south_east) {
               northTemp = ConnectingCarpetSlab.North.NORTH_AND_NORTH_EAST;
            } else if (north_east && east) {
               northTemp = ConnectingCarpetSlab.North.NORTH_AND_NORTH_WEST;
            } else if (north_east && south_east) {
               northTemp = ConnectingCarpetSlab.North.NORTH_EAST_AND_NORTH_WEST;
            } else if (!north_east && east) {
               northTemp = ConnectingCarpetSlab.North.JUST_NORTH;
            } else if (!north_east && south_east) {
               northTemp = ConnectingCarpetSlab.North.JUST_NORTH_EAST;
            } else if (north_east) {
               northTemp = ConnectingCarpetSlab.North.JUST_NORTH_WEST;
            }

            return (BlockState)((BlockState)((BlockState)((BlockState)pState.setValue(EAST, south)).setValue(WEST, north)).setValue(NORTH, northTemp))
               .setValue(SOUTH, southTemp);
         default:
            return pState;
      }
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      if (stack.getItem() instanceof DyeItem dyeItem) {
         DyeColor dyecolor = dyeItem.getDyeColor();
         if (this.getDyeColor(state) == dyecolor) {
            return ItemInteractionResult.FAIL;
         } else {
            if (player instanceof ServerPlayer) {
               CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, pos, stack);
            }

            BlockState newBlockstate = (BlockState)level.getBlockState(pos).setValue(COLOR, dyecolor);
            if (state.getBlock() == ModBlocks.INFUSED_FABRIC_CARPET_ORNATE_SLAB.get()) {
               Block.popResource(level, pos, new ItemStack(Items.GOLD_NUGGET));
               newBlockstate = (BlockState)((ConnectingCarpetSlab)ModBlocks.INFUSED_FABRIC_CARPET_SLAB.get()).defaultBlockState().setValue(COLOR, dyecolor);
            }

            level.setBlockAndUpdate(pos, newBlockstate);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, Context.of(player, newBlockstate));
            level.levelEvent(player, 3003, pos, 0);
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
         }
      } else if (stack.getItem() == Items.GOLD_NUGGET) {
         if (state.getBlock() == ModBlocks.INFUSED_FABRIC_CARPET_ORNATE.get()) {
            return ItemInteractionResult.FAIL;
         } else {
            if (player instanceof ServerPlayer) {
               CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, pos, stack);
            }

            BlockState newBlockstate = ((ConnectingCarpetDyed)ModBlocks.INFUSED_FABRIC_CARPET_ORNATE.get()).defaultBlockState();
            if (!player.isCreative()) {
               stack.shrink(1);
            }

            level.setBlockAndUpdate(pos, newBlockstate);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, Context.of(player, newBlockstate));
            level.levelEvent(player, 3004, pos, 0);
            level.playSound(player, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
         }
      } else {
         return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
      }
   }

   public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
      ItemStack stack = this.parentBlock.asItem().getDefaultInstance();
      DyeColor color = this.getDyeColor(state);
      if (color != DyeColor.WHITE) {
         CompoundTag tag = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
         tag.putString("color", color.getName());
         stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
      }

      return stack;
   }

   @Nullable
   public BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
      return this.getUnWaxed(state, context, itemAbility);
   }

   private static boolean canConnect(BlockState state1, BlockState state2) {
      return state1.getBlock() == state2.getBlock() && state1.hasProperty(COLOR) && state2.hasProperty(COLOR)
         ? state1.getValue(COLOR) == state2.getValue(COLOR)
         : false;
   }

   protected BlockState updateCorners(BlockGetter world, BlockPos pos, BlockState state) {
      BlockState bs_north = world.getBlockState(pos.north());
      BlockState bs_north_east = world.getBlockState(pos.north().east());
      BlockState bs_north_west = world.getBlockState(pos.north().west());
      BlockState bs_east = world.getBlockState(pos.east());
      BlockState bs_south = world.getBlockState(pos.south());
      BlockState bs_south_east = world.getBlockState(pos.south().east());
      BlockState bs_south_west = world.getBlockState(pos.south().west());
      BlockState bs_west = world.getBlockState(pos.west());
      ConnectingCarpetSlab.North north = ConnectingCarpetSlab.North.NONE;
      ConnectingCarpetSlab.South south = ConnectingCarpetSlab.South.NONE;
      if (canConnect(state, bs_north)) {
         north = ConnectingCarpetSlab.North.JUST_NORTH;
         if (canConnect(state, bs_north_west) && !canConnect(state, bs_north_east)) {
            north = ConnectingCarpetSlab.North.NORTH_AND_NORTH_WEST;
         }

         if (!canConnect(state, bs_north_west) && canConnect(state, bs_north_east)) {
            north = ConnectingCarpetSlab.North.NORTH_AND_NORTH_EAST;
         }

         if (canConnect(state, bs_north_west) && canConnect(state, bs_north_east)) {
            north = ConnectingCarpetSlab.North.ALL;
         }
      } else {
         if (canConnect(state, bs_north_west) && !canConnect(state, bs_north_east)) {
            north = ConnectingCarpetSlab.North.JUST_NORTH_WEST;
         }

         if (!canConnect(state, bs_north_west) && canConnect(state, bs_north_east)) {
            north = ConnectingCarpetSlab.North.JUST_NORTH_EAST;
         }
      }

      if (canConnect(state, bs_south)) {
         south = ConnectingCarpetSlab.South.JUST_SOUTH;
         if (canConnect(state, bs_south_west) && !canConnect(state, bs_south_east)) {
            south = ConnectingCarpetSlab.South.SOUTH_AND_SOUTH_WEST;
         }

         if (!canConnect(state, bs_south_west) && canConnect(state, bs_south_east)) {
            south = ConnectingCarpetSlab.South.SOUTH_AND_SOUTH_EAST;
         }

         if (canConnect(state, bs_south_west) && canConnect(state, bs_south_east)) {
            south = ConnectingCarpetSlab.South.ALL;
         }
      } else {
         if (canConnect(state, bs_south_west) && !canConnect(state, bs_south_east)) {
            south = ConnectingCarpetSlab.South.JUST_SOUTH_WEST;
         }

         if (!canConnect(state, bs_south_west) && canConnect(state, bs_south_east)) {
            south = ConnectingCarpetSlab.South.JUST_SOUTH_EAST;
         }
      }

      boolean east = canConnect(state, bs_east);
      boolean west = canConnect(state, bs_west);
      return (BlockState)((BlockState)((BlockState)((BlockState)state.setValue(NORTH, north)).setValue(EAST, east)).setValue(SOUTH, south))
         .setValue(WEST, west);
   }

   public RenderShape getRenderShape(BlockState iBlockState) {
      return RenderShape.MODEL;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      BlockGetter iblockreader = context.getLevel();
      ItemStack stack = context.getItemInHand();
      BlockPos pos = context.getClickedPos();
      CompoundTag tag = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
      if (tag.contains("color")) {
         DyeColor color = DyeColor.byName(tag.getString("color"), DyeColor.WHITE);
         return (BlockState)this.updateCorners(iblockreader, pos, super.getStateForPlacement(context)).setValue(COLOR, color);
      } else {
         return this.updateCorners(iblockreader, pos, super.getStateForPlacement(context));
      }
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{WEST, EAST, NORTH, SOUTH, COLOR});
   }

   public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {
      return !state.canSurvive(world, pos) ? Blocks.AIR.defaultBlockState() : this.updateCorners(world, pos, state);
   }

   public static enum North implements StringRepresentable {
      JUST_NORTH,
      NORTH_AND_NORTH_WEST,
      NORTH_AND_NORTH_EAST,
      JUST_NORTH_WEST,
      JUST_NORTH_EAST,
      NORTH_EAST_AND_NORTH_WEST,
      ALL,
      NONE;

      @Override
      public String toString() {
         return this.getSerializedName();
      }

      public String getSerializedName() {
         return switch (this) {
            case JUST_NORTH -> "north";
            case NORTH_AND_NORTH_WEST -> "north_and_north_west";
            case NORTH_AND_NORTH_EAST -> "north_and_north_east";
            case JUST_NORTH_WEST -> "north_west";
            case JUST_NORTH_EAST -> "north_east";
            case NORTH_EAST_AND_NORTH_WEST -> "north_east_and_north_west";
            case ALL -> "all";
            case NONE -> "none";
         };
      }
   }

   public static enum South implements StringRepresentable {
      JUST_SOUTH,
      SOUTH_AND_SOUTH_WEST,
      SOUTH_AND_SOUTH_EAST,
      JUST_SOUTH_WEST,
      JUST_SOUTH_EAST,
      SOUTH_EAST_AND_SOUTH_WEST,
      ALL,
      NONE;

      @Override
      public String toString() {
         return this.getSerializedName();
      }

      public String getSerializedName() {
         return switch (this) {
            case JUST_SOUTH -> "south";
            case SOUTH_AND_SOUTH_WEST -> "south_and_south_west";
            case SOUTH_AND_SOUTH_EAST -> "south_and_south_east";
            case JUST_SOUTH_WEST -> "south_west";
            case JUST_SOUTH_EAST -> "south_east";
            case SOUTH_EAST_AND_SOUTH_WEST -> "south_east_and_south_west";
            case ALL -> "all";
            case NONE -> "none";
         };
      }
   }
}
