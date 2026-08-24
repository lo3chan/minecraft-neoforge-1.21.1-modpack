package dev.latvian.mods.kubejs.block.entity;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

public interface BlockEntityAttachment {
   default Object getWrappedObject() {
      return this;
   }

   @Nullable
   default <CAP, SRC> CAP getCapability(BlockCapability<CAP, SRC> capability) {
      return null;
   }

   @Nullable
   default Tag serialize(Provider registries) {
      return this.getWrappedObject() instanceof INBTSerializable<?> s ? s.serializeNBT(registries) : null;
   }

   default void deserialize(Provider registries, @Nullable Tag tag) {
      if (tag != null && this.getWrappedObject() instanceof INBTSerializable s) {
         s.deserializeNBT(registries, tag);
      }
   }

   default void onRemove(ServerLevel level, KubeBlockEntity blockEntity, BlockState newState) {
   }

   default void serverTick() {
   }
}
