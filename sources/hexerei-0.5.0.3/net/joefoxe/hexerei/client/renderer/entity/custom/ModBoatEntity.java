package net.joefoxe.hexerei.client.renderer.entity.custom;

import java.util.Arrays;
import java.util.function.Supplier;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class ModBoatEntity extends Boat {
   private static final EntityDataAccessor<Integer> DATA_ID_TYPE = SynchedEntityData.defineId(ModBoatEntity.class, EntityDataSerializers.INT);

   public ModBoatEntity(EntityType<ModBoatEntity> entityEntityType, Level level) {
      super(entityEntityType, level);
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_ID_TYPE, ModBoatEntity.Type.WILLOW.ordinal());
   }

   public ItemStack getPickResult() {
      return new ItemStack(this.getDropItem());
   }

   protected void addAdditionalSaveData(CompoundTag nbt) {
      nbt.putString("model", this.getModel().getName());
   }

   protected void readAdditionalSaveData(CompoundTag nbt) {
      if (nbt.contains("model", 8)) {
         this.entityData.set(DATA_ID_TYPE, ModBoatEntity.Type.byName(nbt.getString("model")).ordinal());
      }
   }

   public void setType(ModBoatEntity.Type pBoatType) {
      this.entityData.set(DATA_ID_TYPE, pBoatType.ordinal());
   }

   public net.minecraft.world.entity.vehicle.Boat.Type getBoatType() {
      return net.minecraft.world.entity.vehicle.Boat.Type.OAK;
   }

   public ModBoatEntity.Type getModBoatType() {
      return ModBoatEntity.Type.byId((Integer)this.entityData.get(DATA_ID_TYPE));
   }

   public Item getDropItem() {
      return switch (ModBoatEntity.Type.byId((Integer)this.entityData.get(DATA_ID_TYPE))) {
         case POLISHED_WILLOW -> (Item)ModItems.POLISHED_WILLOW_BOAT.get();
         case MAHOGANY -> (Item)ModItems.MAHOGANY_BOAT.get();
         case POLISHED_MAHOGANY -> (Item)ModItems.POLISHED_MAHOGANY_BOAT.get();
         default -> (Item)ModItems.WILLOW_BOAT.get();
      };
   }

   public ModBoatEntity withModel(ModBoatEntity.Type type) {
      this.entityData.set(DATA_ID_TYPE, type.ordinal());
      return this;
   }

   public ModBoatEntity.Type getModel() {
      return ModBoatEntity.Type.byId((Integer)this.entityData.get(DATA_ID_TYPE));
   }

   public static enum Type {
      WILLOW("willow", ModBlocks.WILLOW_PLANKS),
      POLISHED_WILLOW("polished_willow", ModBlocks.POLISHED_WILLOW_PLANKS),
      MAHOGANY("mahogany", ModBlocks.MAHOGANY_PLANKS),
      POLISHED_MAHOGANY("polished_mahogany", ModBlocks.POLISHED_MAHOGANY_PLANKS);

      private final String name;
      private final Supplier<Block> supplierPlanks;

      private Type(String name, Supplier<Block> supplierPlanks) {
         this.name = name;
         this.supplierPlanks = supplierPlanks;
      }

      public String getName() {
         return this.name;
      }

      public Block getPlanks() {
         return this.supplierPlanks.get();
      }

      @Override
      public String toString() {
         return this.name;
      }

      public static ModBoatEntity.Type byId(int id) {
         ModBoatEntity.Type[] type = values();
         return type[id >= 0 && id < type.length ? id : 0];
      }

      public static ModBoatEntity.Type byName(String aName) {
         ModBoatEntity.Type[] type = values();
         return Arrays.stream(type).filter(t -> t.getName().equals(aName)).findFirst().orElse(type[0]);
      }
   }
}
