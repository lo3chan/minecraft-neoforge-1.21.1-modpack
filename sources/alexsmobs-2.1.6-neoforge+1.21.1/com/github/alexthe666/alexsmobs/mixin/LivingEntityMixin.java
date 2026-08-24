package com.github.alexthe666.alexsmobs.mixin;

import com.github.alexthe666.alexsmobs.citadel.server.entity.ICitadelDataEntity;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.IEntitySaveDataAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LivingEntity.class})
public abstract class LivingEntityMixin extends Entity implements ICitadelDataEntity, IEntitySaveDataAccessor {
   private static final EntityDataAccessor<CompoundTag> ALEXSMOBS_CITADEL_DATA = SynchedEntityData.defineId(
      LivingEntity.class, EntityDataSerializers.COMPOUND_TAG
   );

   protected LivingEntityMixin(EntityType<? extends Entity> entityType, Level world) {
      super(entityType, world);
   }

   @Inject(
      at = {@At("TAIL")},
      method = {"Lnet/minecraft/world/entity/LivingEntity;defineSynchedData(Lnet/minecraft/network/syncher/SynchedEntityData$Builder;)V"}
   )
   private void alexsmobs_registerCitadelData(Builder builder, CallbackInfo ci) {
      builder.define(ALEXSMOBS_CITADEL_DATA, new CompoundTag());
   }

   @Inject(
      at = {@At("TAIL")},
      method = {"Lnet/minecraft/world/entity/LivingEntity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"}
   )
   private void alexsmobs_writeCitadelData(CompoundTag compoundNBT, CallbackInfo ci) {
      CompoundTag citadelDat = this.getCitadelEntityData();
      if (citadelDat != null) {
         compoundNBT.put("CitadelData", citadelDat);
      }
   }

   @Inject(
      at = {@At("TAIL")},
      method = {"Lnet/minecraft/world/entity/LivingEntity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"}
   )
   private void alexsmobs_readCitadelData(CompoundTag compoundNBT, CallbackInfo ci) {
      if (compoundNBT.contains("CitadelData")) {
         this.setCitadelEntityData(AMCompat.getCompound(compoundNBT, "CitadelData"));
      }
   }

   @Override
   public void am_writeSaveData(CompoundTag tag) {
      this.addAdditionalSaveData(tag);
   }

   @Override
   public void am_readSaveData(CompoundTag tag) {
      this.readAdditionalSaveData(tag);
   }

   @Override
   public CompoundTag getCitadelEntityData() {
      return (CompoundTag)this.entityData.get(ALEXSMOBS_CITADEL_DATA);
   }

   @Override
   public void setCitadelEntityData(CompoundTag nbt) {
      this.entityData.set(ALEXSMOBS_CITADEL_DATA, nbt);
   }
}
