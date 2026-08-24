package com.aetherteam.aether.blockentity;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.DataComponentMap.Builder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntity.DataComponentInput;
import net.minecraft.world.level.block.state.BlockState;

public class SunAltarBlockEntity extends BlockEntity implements Nameable {
   @Nullable
   private Component name;

   public SunAltarBlockEntity(BlockPos pos, BlockState blockState) {
      super((BlockEntityType)AetherBlockEntityTypes.SUN_ALTAR.get(), pos, blockState);
   }

   @Nullable
   public Component getName() {
      return (Component)(this.name != null ? this.name : Component.translatable("menu.aether.sun_altar"));
   }

   public void setCustomName(@Nullable Component name) {
      this.name = name;
   }

   @Nullable
   public Component getCustomName() {
      return this.name;
   }

   protected void applyImplicitComponents(DataComponentInput componentInput) {
      super.applyImplicitComponents(componentInput);
      this.name = (Component)componentInput.get(DataComponents.CUSTOM_NAME);
   }

   protected void collectImplicitComponents(Builder components) {
      super.collectImplicitComponents(components);
      components.set(DataComponents.CUSTOM_NAME, this.name);
   }

   public void removeComponentsFromTag(CompoundTag tag) {
      tag.remove("CustomName");
   }

   public CompoundTag getUpdateTag(Provider lookupProvider) {
      return this.saveWithoutMetadata(lookupProvider);
   }

   public void handleUpdateTag(CompoundTag tag, Provider lookupProvider) {
      this.loadAdditional(tag, lookupProvider);
   }

   protected void saveAdditional(CompoundTag tag, Provider lookupProvider) {
      super.saveAdditional(tag, lookupProvider);
      if (this.hasCustomName()) {
         tag.putString("CustomName", Serializer.toJson(this.name, lookupProvider));
      }
   }

   public void loadAdditional(CompoundTag tag, Provider lookupProvider) {
      super.loadAdditional(tag, lookupProvider);
      if (tag.contains("CustomName", 8)) {
         this.name = Serializer.fromJson(tag.getString("CustomName"), lookupProvider);
      }
   }

   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet, Provider lookupProvider) {
      CompoundTag compound = packet.getTag();
      this.handleUpdateTag(compound, lookupProvider);
   }
}
