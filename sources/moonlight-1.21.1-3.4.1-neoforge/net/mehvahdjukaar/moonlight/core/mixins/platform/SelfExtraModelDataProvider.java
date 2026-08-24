package net.mehvahdjukaar.moonlight.core.mixins.platform;

import net.mehvahdjukaar.moonlight.api.client.model.ExtraModelData;
import net.mehvahdjukaar.moonlight.api.client.model.IExtraModelDataProvider;
import net.mehvahdjukaar.moonlight.api.client.model.platform.ExtraModelDataImpl;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.extensions.IBlockEntityExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin({IExtraModelDataProvider.class})
public interface SelfExtraModelDataProvider extends IBlockEntityExtension, IExtraModelDataProvider {
   @Overwrite
   @Override
   default void requestModelReload() {
      BlockEntity be = (BlockEntity)this;
      be.requestModelDataUpdate();
      Level level = be.getLevel();
      if (level != null && level.isClientSide) {
         level.sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), 2);
      }
   }

   default ModelData getModelData() {
      return this.getExtraModelData() instanceof ExtraModelDataImpl(ModelData var6) ? var6 : ModelData.EMPTY;
   }

   default void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, Provider registries) {
      BlockEntity be = (BlockEntity)this;
      Level level = be.getLevel();
      if (level != null && level.isClientSide) {
         ExtraModelData oldData = this.getExtraModelData();
         CompoundTag tag = pkt.getTag();
         if (!tag.isEmpty()) {
            super.onDataPacket(net, pkt, registries);
            this.afterDataPacket(oldData);
         }
      }
   }
}
