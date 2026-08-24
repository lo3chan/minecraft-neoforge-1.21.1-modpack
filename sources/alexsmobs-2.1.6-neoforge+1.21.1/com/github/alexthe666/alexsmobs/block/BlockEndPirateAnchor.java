package com.github.alexthe666.alexsmobs.block;

import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.github.alexthe666.alexsmobs.tileentity.AMTileEntityRegistry;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityEndPirateAnchor;
import com.mojang.serialization.MapCodec;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockEndPirateAnchor extends BaseEntityBlock implements AMSpecialRenderBlock {
   public static final BooleanProperty EASTORWEST = BooleanProperty.create("eastorwest");
   public static final EnumProperty<BlockEndPirateAnchor.PieceType> PIECE = EnumProperty.create("piece", BlockEndPirateAnchor.PieceType.class);
   protected static final VoxelShape FULL_AABB_EW = Block.box(0.0, 0.0, 4.0, 16.0, 16.0, 12.0);
   protected static final VoxelShape FULL_AABB_NS = Block.box(4.0, 0.0, 0.0, 12.0, 16.0, 16.0);
   protected static final VoxelShape CHAIN_AABB = Block.box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);

   protected MapCodec<? extends BaseEntityBlock> codec() {
      return AMPlatform.unsupportedBlockCodec();
   }

   protected BlockEndPirateAnchor() {
      super(Properties.of().mapColor(MapColor.COLOR_BLACK).friction(0.97F).strength(10.0F).lightLevel(i -> 6).sound(SoundType.STONE).noOcclusion());
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(EASTORWEST, false)).setValue(PIECE, BlockEndPirateAnchor.PieceType.ANCHOR)
      );
   }

   public static boolean isClearForPlacement(LevelReader reader, BlockPos center, boolean eastOrWest) {
      for (BlockPos offset : TileEntityEndPirateAnchor.getValidBBPositions(eastOrWest)) {
         BlockPos check = center.offset(offset);
         if (!reader.isEmptyBlock(check) || !reader.getBlockState(check).canBeReplaced()) {
            return false;
         }
      }

      return true;
   }

   public static void placeAnchor(Level level, BlockPos pos, BlockState state) {
      for (BlockPos offset : TileEntityEndPirateAnchor.getValidBBPositions((Boolean)state.getValue(EASTORWEST))) {
         if (!offset.equals(BlockPos.ZERO)) {
            level.setBlock(pos.offset(offset), (BlockState)state.setValue(PIECE, BlockEndPirateAnchor.PieceType.ANCHOR_SIDE), 2);
         }
      }
   }

   public static void removeAnchor(Level level, BlockPos pos, BlockState state) {
      for (BlockPos offset : TileEntityEndPirateAnchor.getValidBBPositions((Boolean)state.getValue(EASTORWEST))) {
         level.setBlock(pos.offset(offset), Blocks.AIR.defaultBlockState(), 67);
      }
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      LevelReader levelreader = context.getLevel();
      BlockPos blockpos = context.getClickedPos();
      BlockPos actualPos = context.getClickedPos().relative(context.getClickedFace().getOpposite());
      BlockPos u = blockpos.above();
      BlockPos d = blockpos.below();
      BlockState clickState = levelreader.getBlockState(actualPos);
      boolean axis = context.getHorizontalDirection().getAxis() == Axis.X;
      if (clickState.getBlock() instanceof BlockEndPirateAnchor) {
         axis = (Boolean)clickState.getValue(EASTORWEST);
      }

      return isClearForPlacement(levelreader, blockpos, axis) ? (BlockState)this.defaultBlockState().setValue(EASTORWEST, axis) : null;
   }

   public boolean isLadder(BlockState state, LevelReader world, BlockPos pos, LivingEntity entity) {
      return state.getValue(PIECE) == BlockEndPirateAnchor.PieceType.CHAIN;
   }

   public boolean isScaffolding(BlockState state, LevelReader world, BlockPos pos, LivingEntity entity) {
      return state.getValue(PIECE) == BlockEndPirateAnchor.PieceType.CHAIN;
   }

   public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
      this.climbChain(state, entity);
   }

   private void climbChain(BlockState state, Entity entity) {
      if (entity instanceof LivingEntity && state.getValue(PIECE) == BlockEndPirateAnchor.PieceType.CHAIN) {
         LivingEntity livingEntity = (LivingEntity)entity;
         if (livingEntity.horizontalCollision && !livingEntity.isInWater()) {
            livingEntity.fallDistance = 0.0F;
            Vec3 motion = livingEntity.getDeltaMovement();
            double d0 = Mth.clamp(motion.x, -0.15000000596046448, 0.15000000596046448);
            double d1 = Mth.clamp(motion.z, -0.15000000596046448, 0.15000000596046448);
            double d2 = 0.3;
            if (d2 < 0.0 && livingEntity.isSuppressingSlidingDownLadder()) {
               d2 = 0.0;
            }

            motion = new Vec3(d0, d2, d1);
            livingEntity.setDeltaMovement(motion);
         }
      }
   }

   public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos p_52780_, boolean p_52781_) {
      if (state.getValue(PIECE) == BlockEndPirateAnchor.PieceType.ANCHOR_SIDE) {
         for (int i = -2; i <= 2; i++) {
            for (int j = -3; j <= 3; j++) {
               for (int k = -2; k <= 2; k++) {
                  BlockPos offsetPos = pos.offset(i, j, k);
                  if (level.getBlockEntity(offsetPos) instanceof TileEntityEndPirateAnchor anchor && !anchor.hasAllAnchorBlocks()) {
                     removeAnchor(level, offsetPos, level.getBlockState(offsetPos));
                     level.destroyBlock(offsetPos, true);
                  }
               }
            }
         }
      }

      if (!this.canSurviveAnchor(state, level, pos)) {
         level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
      }
   }

   public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity player, ItemStack stack) {
      placeAnchor(level, pos, state);
   }

   public boolean canSurviveAnchor(BlockState state, LevelReader world, BlockPos pos) {
      if (state.getValue(PIECE) == BlockEndPirateAnchor.PieceType.ANCHOR) {
         return true;
      } else {
         if (state.getValue(PIECE) == BlockEndPirateAnchor.PieceType.ANCHOR_SIDE) {
            for (int i = -1; i <= 1; i++) {
               for (int j = -3; j <= 0; j++) {
                  for (int k = -1; k <= 1; k++) {
                     BlockPos offsetPos = pos.offset(i, j, k);
                     BlockState anchorState = world.getBlockState(offsetPos);
                     if (anchorState.getBlock() instanceof BlockEndPirateAnchor
                        && anchorState.getValue(PIECE) == BlockEndPirateAnchor.PieceType.ANCHOR
                        && this.isPartOfAnchor(anchorState, world, offsetPos, pos, (Boolean)state.getValue(EASTORWEST))) {
                        return true;
                     }
                  }
               }
            }
         } else if (state.getValue(PIECE) == BlockEndPirateAnchor.PieceType.CHAIN) {
            BlockPos below = pos.below();
            BlockState chainBelow = world.getBlockState(below);
            BlockState chainAbove = world.getBlockState(below);
            return chainBelow.getBlock() instanceof BlockEndPirateAnchor
               && (chainAbove.getBlock() instanceof BlockEndPirateAnchor || chainAbove.getBlock() instanceof BlockEndPirateAnchorWinch);
         }

         return false;
      }
   }

   public boolean isPartOfAnchor(BlockState anchor, LevelReader level, BlockPos center, BlockPos pos, boolean eastOrWest) {
      if ((Boolean)anchor.getValue(EASTORWEST) == eastOrWest) {
         BlockPos offset = pos.subtract(center);
         return TileEntityEndPirateAnchor.getValidBBPositions(eastOrWest).contains(offset);
      } else {
         return false;
      }
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> p_58032_) {
      p_58032_.add(new Property[]{EASTORWEST, PIECE});
   }

   public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
      if (state.getValue(PIECE) == BlockEndPirateAnchor.PieceType.CHAIN) {
         return CHAIN_AABB;
      } else {
         return state.getValue(EASTORWEST) ? FULL_AABB_NS : FULL_AABB_EW;
      }
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return state.getValue(PIECE) == BlockEndPirateAnchor.PieceType.ANCHOR ? new TileEntityEndPirateAnchor(pos, state) : null;
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152180_, BlockState state, BlockEntityType<T> p_152182_) {
      return state.getValue(PIECE) == BlockEndPirateAnchor.PieceType.ANCHOR
         ? createTickerHelper(p_152182_, AMTileEntityRegistry.END_PIRATE_ANCHOR.get(), TileEntityEndPirateAnchor::commonTick)
         : null;
   }

   public RenderShape getRenderShape(BlockState state) {
      return state.getValue(PIECE) == BlockEndPirateAnchor.PieceType.ANCHOR_SIDE ? RenderShape.INVISIBLE : RenderShape.ENTITYBLOCK_ANIMATED;
   }

   public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
      return state.getValue(PIECE) == BlockEndPirateAnchor.PieceType.ANCHOR ? super.getDrops(state, builder) : Collections.emptyList();
   }

   public static enum PieceType implements StringRepresentable {
      ANCHOR,
      ANCHOR_SIDE,
      CHAIN;

      @Override
      public String toString() {
         return this.getSerializedName();
      }

      public String getSerializedName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
