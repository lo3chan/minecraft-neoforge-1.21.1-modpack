package net.cibernet.alchemancy.blocks;

import com.mojang.serialization.MapCodec;
import net.cibernet.alchemancy.blocks.blockentities.SculkBudBlockEntity;
import net.cibernet.alchemancy.registries.AlchemancyBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SculkBehaviour;
import net.minecraft.world.level.block.SculkSpreader;
import net.minecraft.world.level.block.SculkSpreader.ChargeCursor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class SculkBudBlock extends BaseEntityBlock implements SculkBehaviour {
   public static final MapCodec<SculkBudBlock> CODEC = simpleCodec(SculkBudBlock::new);
   private final IntProvider xpRange = ConstantInt.of(5);
   protected static final VoxelShape AABB = Block.box(4.0, 0.0, 4.0, 12.0, 10.0, 12.0);

   public SculkBudBlock(Properties properties) {
      super(properties);
   }

   public int getExpDrop(BlockState state, LevelAccessor level, BlockPos pos, @Nullable BlockEntity blockEntity, @Nullable Entity breaker, ItemStack tool) {
      return this.xpRange.sample(level.getRandom());
   }

   protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
      BlockPos blockpos = pos.below();
      return canSupportRigidBlock(level, blockpos) || canSupportCenter(level, blockpos, Direction.UP);
   }

   protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return AABB;
   }

   protected RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new SculkBudBlockEntity(pos, state);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
      return level.isClientSide()
         ? null
         : createTickerHelper(blockEntityType, (BlockEntityType)AlchemancyBlockEntities.SCULK_BUD.get(), SculkBudBlockEntity::serverTick);
   }

   public int attemptUseCharge(ChargeCursor cursor, LevelAccessor level, BlockPos pos, RandomSource random, SculkSpreader spreader, boolean shouldConvertBlocks) {
      return shouldConvertBlocks && this.attemptPlaceSculk(spreader, level, pos.below(), random) ? cursor.getCharge() - 1 : 0;
   }

   private boolean attemptPlaceSculk(SculkSpreader spreader, LevelAccessor level, BlockPos pos, RandomSource random) {
      TagKey<Block> tagkey = spreader.replaceableBlocks();
      BlockState blockstate1 = level.getBlockState(pos);
      if (blockstate1.is(tagkey)) {
         BlockState blockstate2 = Blocks.SCULK.defaultBlockState();
         level.setBlock(pos, blockstate2, 3);
         Block.pushEntitiesUp(blockstate1, blockstate2, level, pos);
         level.playSound(null, pos, SoundEvents.SCULK_BLOCK_SPREAD, SoundSource.BLOCKS, 1.0F, 1.0F);

         for (Direction direction2 : Direction.values()) {
            BlockPos blockpos1 = pos.relative(direction2);
            BlockState blockstate3 = level.getBlockState(blockpos1);
            if (blockstate3.is(this)) {
               this.onDischarged(level, blockstate3, blockpos1, random);
            }
         }

         return true;
      } else {
         return false;
      }
   }
}
