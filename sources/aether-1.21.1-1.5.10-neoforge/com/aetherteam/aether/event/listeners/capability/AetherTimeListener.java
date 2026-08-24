package com.aetherteam.aether.event.listeners.capability;

import com.aetherteam.aether.event.hooks.CapabilityHooks;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;

public class AetherTimeListener {
   public static void listen(IEventBus bus) {
      bus.addListener(AetherTimeListener::onLogin);
      bus.addListener(AetherTimeListener::onChangeDimension);
      bus.addListener(AetherTimeListener::onPlayerRespawn);
   }

   public static void onLogin(PlayerLoggedInEvent event) {
      Player player = event.getEntity();
      CapabilityHooks.AetherTimeHooks.login(player);
   }

   public static void onChangeDimension(PlayerChangedDimensionEvent event) {
      Player player = event.getEntity();
      CapabilityHooks.AetherTimeHooks.changeDimension(player);
   }

   public static void onPlayerRespawn(PlayerRespawnEvent event) {
      Player player = event.getEntity();
      CapabilityHooks.AetherTimeHooks.respawn(player);
   }
}
