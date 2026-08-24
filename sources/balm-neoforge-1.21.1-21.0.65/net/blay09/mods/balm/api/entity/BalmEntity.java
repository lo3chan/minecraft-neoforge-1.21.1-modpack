package net.blay09.mods.balm.api.entity;

import net.minecraft.nbt.CompoundTag;

public interface BalmEntity {
   CompoundTag getFabricBalmData();

   void setFabricBalmData(CompoundTag var1);

   CompoundTag getForgeBalmData();

   void setForgeBalmData(CompoundTag var1);

   CompoundTag getNeoForgeBalmData();

   void setNeoForgeBalmData(CompoundTag var1);
}
