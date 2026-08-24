package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class AegifloraBlockEntity extends BlockEntity {
   private static final String TAG_CHARGES = "Charges";
   private int charges;

   public AegifloraBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)ModBlockEntityTypes.AEGIFLORA.get(), pos, state);
      this.charges = defaultChargesForState(state);
   }

   public boolean canAbsorb() {
      return this.charges > 0;
   }

   public AegifloraBlockEntity.AbsorbOutcome absorbOnce(ServerLevel level) {
      if (this.charges <= 0) {
         return AegifloraBlockEntity.AbsorbOutcome.NONE;
      } else {
         this.charges--;
         BlockState current = this.getBlockState();
         if (this.charges == 1) {
            if (current.is((Block)ModBlocks.AEGIFLORA.get())) {
               level.setBlock(this.worldPosition, ((Block)ModBlocks.WITHERED_AEGIFLORA.get()).defaultBlockState(), 3);
            } else {
               this.setChanged();
               level.sendBlockUpdated(this.worldPosition, current, current, 3);
            }

            return AegifloraBlockEntity.AbsorbOutcome.WITHERED;
         } else if (this.charges <= 0) {
            level.setBlock(this.worldPosition, Blocks.DEAD_BUSH.defaultBlockState(), 3);
            return AegifloraBlockEntity.AbsorbOutcome.DESTROYED;
         } else {
            this.setChanged();
            level.sendBlockUpdated(this.worldPosition, current, current, 3);
            return AegifloraBlockEntity.AbsorbOutcome.NONE;
         }
      }
   }

   private static int defaultChargesForState(BlockState state) {
      return state.is((Block)ModBlocks.WITHERED_AEGIFLORA.get()) ? 1 : 2;
   }

   protected void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      tag.putInt("Charges", this.charges);
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.charges = tag.contains("Charges") ? tag.getInt("Charges") : defaultChargesForState(this.getBlockState());
   }

   public static enum AbsorbOutcome {
      NONE,
      WITHERED,
      DESTROYED;
   }
}
