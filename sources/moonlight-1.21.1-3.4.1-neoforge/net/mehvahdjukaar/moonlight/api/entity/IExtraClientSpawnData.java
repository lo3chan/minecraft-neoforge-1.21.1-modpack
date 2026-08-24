package net.mehvahdjukaar.moonlight.api.entity;

import net.minecraft.network.RegistryFriendlyByteBuf;

public interface IExtraClientSpawnData {
   void writeSpawnData(RegistryFriendlyByteBuf var1);

   void readSpawnData(RegistryFriendlyByteBuf var1);
}
