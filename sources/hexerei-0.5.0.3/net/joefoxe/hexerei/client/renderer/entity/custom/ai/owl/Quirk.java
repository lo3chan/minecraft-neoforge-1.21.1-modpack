package net.joefoxe.hexerei.client.renderer.entity.custom.ai.owl;

import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.minecraft.nbt.CompoundTag;

public interface Quirk {
   default void tick(OwlEntity owl) {
      if (owl.level().isClientSide) {
         this.clientTick(owl);
      } else {
         this.serverTick(owl);
      }
   }

   void clientTick(OwlEntity var1);

   void serverTick(OwlEntity var1);

   String getName();

   void write(CompoundTag var1);

   void read(CompoundTag var1);
}
