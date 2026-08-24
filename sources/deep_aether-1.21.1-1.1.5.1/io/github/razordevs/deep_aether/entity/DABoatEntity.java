package io.github.razordevs.deep_aether.entity;

import io.github.razordevs.deep_aether.init.DAEntities;
import io.github.razordevs.deep_aether.init.DAItems;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class DABoatEntity extends Boat {
   private static final EntityDataAccessor<Integer> WOOD_TYPE = SynchedEntityData.defineId(DABoatEntity.class, EntityDataSerializers.INT);

   public DABoatEntity(EntityType<? extends Boat> type, Level level) {
      super(type, level);
      this.blocksBuilding = true;
   }

   public DABoatEntity(Level level, double x, double y, double z) {
      this((EntityType<? extends Boat>)DAEntities.BOAT.get(), level);
      this.setPos(x, y, z);
      this.xo = x;
      this.yo = y;
      this.zo = z;
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(WOOD_TYPE, 0);
   }

   protected void readAdditionalSaveData(CompoundTag pCompound) {
      if (pCompound.contains("Type", 8)) {
         this.setWoodType(DABoatEntity.Type.byName(pCompound.getString("Type")));
      }
   }

   protected void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putString("Type", this.getWoodType().getName());
   }

   public DABoatEntity.Type getWoodType() {
      return DABoatEntity.Type.byId((Integer)this.entityData.get(WOOD_TYPE));
   }

   public void setWoodType(DABoatEntity.Type type) {
      this.entityData.set(WOOD_TYPE, type.ordinal());
   }

   public Item getDropItem() {
      return this.getWoodType().getItem().get();
   }

   public static enum Type {
      ROSEROOT("roseroot", DAItems.ROSEROOT_BOAT, DAItems.ROSEROOT_CHEST_BOAT),
      CONBERRY("conberry", DAItems.CONBERRY_BOAT, DAItems.CONBERRY_CHEST_BOAT),
      CRUDEROOT("cruderoot", DAItems.CRUDEROOT_BOAT, DAItems.CRUDEROOT_CHEST_BOAT),
      YAGROOT("yagroot", DAItems.YAGROOT_BOAT, DAItems.YAGROOT_CHEST_BOAT),
      SUNROOT("sunroot", DAItems.SUNROOT_BOAT, DAItems.SUNROOT_CHEST_BOAT);

      private final String name;
      private final Supplier<Item> item;
      private final Supplier<Item> chestItem;

      private Type(String name, Supplier<Item> boatItem, Supplier<Item> chestBoatItem) {
         this.name = name;
         this.item = boatItem;
         this.chestItem = chestBoatItem;
      }

      public ResourceLocation getTexture(boolean hasChest) {
         return hasChest
            ? ResourceLocation.fromNamespaceAndPath("deep_aether", "textures/entity/chest_boat/" + this.name + ".png")
            : ResourceLocation.fromNamespaceAndPath("deep_aether", "textures/entity/boat/" + this.name + ".png");
      }

      public String getModelLocation() {
         return "boat/" + this.name;
      }

      public String getChestModelLocation() {
         return "chest_boat/" + this.name;
      }

      public String getName() {
         return this.name;
      }

      public Supplier<Item> getItem() {
         return this.item;
      }

      public Supplier<Item> getChestItem() {
         return this.chestItem;
      }

      public static DABoatEntity.Type byId(int id) {
         DABoatEntity.Type[] values = values();
         if (id < 0 || id >= values.length) {
            id = 0;
         }

         return values[id];
      }

      public static DABoatEntity.Type byName(String name) {
         DABoatEntity.Type[] values = values();

         for (DABoatEntity.Type value : values) {
            if (value.getName().equals(name)) {
               return value;
            }
         }

         return values[0];
      }
   }
}
