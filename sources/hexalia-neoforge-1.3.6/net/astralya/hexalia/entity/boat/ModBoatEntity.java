package net.astralya.hexalia.entity.boat;

import java.util.function.Function;
import java.util.function.IntFunction;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.entity.ModEntities;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.ByIdMap.OutOfBoundsStrategy;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class ModBoatEntity extends Boat {
   private static final EntityDataAccessor<Integer> DATA_ID_TYPE = SynchedEntityData.defineId(ModBoatEntity.class, EntityDataSerializers.INT);

   public ModBoatEntity(EntityType<? extends Boat> entityType, Level level) {
      super(entityType, level);
   }

   public ModBoatEntity(Level level, double x, double y, double z) {
      this((EntityType<? extends Boat>)ModEntities.MOD_BOAT.get(), level);
      this.setPos(x, y, z);
      this.xo = x;
      this.yo = y;
      this.zo = z;
   }

   public Item getDropItem() {
      return switch (this.getModVariant()) {
         case WILLOW -> (Item)ModItems.WILLOW_BOAT.get();
         case COTTONWOOD -> (Item)ModItems.COTTONWOOD_BOAT.get();
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
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      if (compound.contains("Type", 8)) {
         this.setVariant(ModBoatEntity.Type.byName(compound.getString("Type")));
      }
   }

   public static enum Type implements StringRepresentable {
      WILLOW((Block)ModBlocks.WILLOW_PLANKS.get(), "willow"),
      COTTONWOOD((Block)ModBlocks.COTTONWOOD_PLANKS.get(), "cottonwood");

      private static final Function<String, ModBoatEntity.Type> BY_NAME = StringRepresentable.createNameLookup(values(), Function.identity());
      private static final IntFunction<ModBoatEntity.Type> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), OutOfBoundsStrategy.ZERO);
      private final Block planks;
      private final String name;

      private Type(Block planks, String name) {
         this.planks = planks;
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }

      public String getName() {
         return this.name;
      }

      public Block getPlanks() {
         return this.planks;
      }

      @Override
      public String toString() {
         return this.name;
      }

      public static ModBoatEntity.Type byId(int id) {
         return BY_ID.apply(id);
      }

      public static ModBoatEntity.Type byName(String name) {
         ModBoatEntity.Type type = BY_NAME.apply(name);
         return type == null ? WILLOW : type;
      }
   }
}
