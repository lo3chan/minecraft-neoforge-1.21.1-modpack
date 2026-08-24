package net.mehvahdjukaar.amendments.mixins;

import net.mehvahdjukaar.amendments.common.ExtendedHangingSign;
import net.mehvahdjukaar.amendments.common.tile.HangingSignTileExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({HangingSignBlockEntity.class})
public abstract class HangingSignBlockEntityMixin extends BlockEntity implements ExtendedHangingSign {
   @Unique
   private final HangingSignTileExtension amendments$extension = new HangingSignTileExtension(this.getBlockState());

   protected HangingSignBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
      super(blockEntityType, blockPos, blockState);
   }

   public AABB getRenderBoundingBox() {
      return new AABB(this.worldPosition).inflate(0.5);
   }

   protected void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      this.amendments$extension.saveAdditional(tag, registries);
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.amendments$extension.load(tag, registries);
   }

   @Override
   public HangingSignTileExtension amendments$getExtension() {
      return this.amendments$extension;
   }
}
