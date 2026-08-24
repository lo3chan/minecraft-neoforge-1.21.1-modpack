package com.aetherteam.aether.perk.data;

import com.aetherteam.nitrogen.api.users.UserData.Client;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public abstract class ClientPerkData<T> {
   public void applyPerk(UUID uuid, T perk) {
      this.getMap().put(uuid, perk);
   }

   public void removePerk(UUID uuid) {
      this.getMap().remove(uuid);
   }

   public Map<UUID, T> getClientPerkData() {
      return ImmutableMap.copyOf(this.getMap());
   }

   public boolean canSync(Player player) {
      return Client.getClientUser() != null
         && player.level().isClientSide()
         && Minecraft.getInstance().player != null
         && player.getUUID().equals(Minecraft.getInstance().player.getUUID());
   }

   public abstract void syncFromClient(Player var1);

   protected abstract Map<UUID, T> getMap();
}
