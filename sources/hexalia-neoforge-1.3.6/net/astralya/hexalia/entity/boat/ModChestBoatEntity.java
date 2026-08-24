package net.astralya.hexalia.entity.boat;

import net.astralya.hexalia.entity.ModEntities;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class ModChestBoatEntity extends ChestBoat {
   private static final EntityDataAccessor<Integer> DATA_ID_TYPE = SynchedEntityData.defineId(ModChestBoatEntity.class, EntityDataSerializers.INT);

   public ModChestBoatEntity(EntityType<? extends ChestBoat> entityType, Level level) {
      super(entityType, level);
   }

   public ModChestBoatEntity(Level level, double x, double y, double z) {
      this((EntityType<? extends ChestBoat>)ModEntities.MOD_CHEST_BOAT.get(), level);
      this.setPos(x, y, z);
      this.xo = x;
      this.yo = y;
      this.zo = z;
   }

   public Item getDropItem() {
      return switch (this.getModVariant()) {
         case WILLOW -> (Item)ModItems.WILLOW_CHEST_BOAT.get();
         case COTTONWOOD -> (Item)ModItems.COTTONWOOD_CHEST_BOAT.get();
      };
   }

   public void setVariant(ModBoatEntity.Type variant) {
      this.entityData.set(DATA_ID_TYPE, variant.ordinal());
   }

   public ModBoatEntity.Type getModVariant() {
      return ModBoatEntity.Type.byId((Integer)this.entityData.get(DATA_ID_TYPE));
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_ID_TYPE, ModBoatEntity.Type.WILLOW.ordinal());
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putString("Type", this.getModVariant().getSerializedName());
      this.addChestVehicleSaveData(compound, this.registryAccess());
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.readChestVehicleSaveData(compound, this.registryAccess());
      if (compound.contains("Type", 8)) {
         this.setVariant(ModBoatEntity.Type.byName(compound.getString("Type")));
      }
   }
}
