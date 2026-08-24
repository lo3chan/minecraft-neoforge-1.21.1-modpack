package net.mehvahdjukaar.amendments.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({BrewingStandBlockEntity.class})
public abstract class BrewingStandMixin extends BlockEntity {
   protected BrewingStandMixin(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
      super(pType, pWorldPosition, pBlockState);
   }

   @Inject(
      method = {"doBrew"},
      at = {@At("TAIL")}
   )
   private static void amendments$ensureModelRefresh(Level level, BlockPos pos, NonNullList<ItemStack> items, CallbackInfo ci) {
      if (level != null) {
         BlockState state = level.getBlockState(pos);
         level.sendBlockUpdated(pos, state, state, 3);
      }
   }

   @Inject(
      method = {"loadAdditional"},
      at = {@At("TAIL")}
   )
   public void amendments$refreshModel(CompoundTag tag, Provider registries, CallbackInfo ci) {
      if (this.level != null && this.level.isClientSide) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
      }
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag(Provider registries) {
      return this.saveWithoutMetadata(registries);
   }
}
