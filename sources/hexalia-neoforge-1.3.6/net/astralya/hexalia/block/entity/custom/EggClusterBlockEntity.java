package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.HexaliaConfig;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class EggClusterBlockEntity extends BlockEntity {
   private static final String TAG_HATCH_TICKS = "HatchTicksRemaining";
   private int hatchTicksRemaining = getHatchDurationTicks();

   public EggClusterBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)ModBlockEntityTypes.EGG_CLUSTER.get(), pos, state);
   }

   public static void tick(Level level, BlockPos pos, BlockState state, EggClusterBlockEntity blockEntity) {
      if (!level.isClientSide) {
         if (blockEntity.hatchTicksRemaining > 0) {
            blockEntity.hatchTicksRemaining--;
            blockEntity.setChanged();
         } else {
            blockEntity.hatch(level, pos);
         }
      }
   }

   private void hatch(Level level, BlockPos pos) {
      ItemStack stack = new ItemStack((ItemLike)ModItems.SILKWORM.get(), 4);
      BlockPos belowPos = pos.below();
      if (level.getBlockEntity(belowPos) instanceof NestingBlockEntity nestingBlock) {
         stack = nestingBlock.insertAll(stack);
      }

      if (!stack.isEmpty()) {
         ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.25, pos.getZ() + 0.5, stack);
         itemEntity.setDefaultPickUpDelay();
         level.addFreshEntity(itemEntity);
      }

      level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
   }

   private static int getHatchDurationTicks() {
      return HexaliaConfig.eggClusterHatchDuration();
   }

   protected void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      tag.putInt("HatchTicksRemaining", this.hatchTicksRemaining);
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      if (tag.contains("HatchTicksRemaining")) {
         this.hatchTicksRemaining = tag.getInt("HatchTicksRemaining");
      } else {
         this.hatchTicksRemaining = getHatchDurationTicks();
      }
   }
}
