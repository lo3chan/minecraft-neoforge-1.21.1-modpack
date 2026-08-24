package com.aetherteam.aether.event.listeners.capability;

import com.aetherteam.aether.event.hooks.CapabilityHooks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.Clone;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Post;

public class AetherPlayerListener {
   public static void listen(IEventBus bus) {
      bus.addListener(AetherPlayerListener::onPlayerLogin);
      bus.addListener(AetherPlayerListener::onPlayerLogout);
      bus.addListener(AetherPlayerListener::onPlayerJoinLevel);
      bus.addListener(AetherPlayerListener::onPlayerUpdate);
      bus.addListener(EventPriority.LOWEST, AetherPlayerListener::onPlayerClone);
      bus.addListener(AetherPlayerListener::onPlayerChangeDimension);
   }

   public static void onPlayerLogin(PlayerLoggedInEvent event) {
      Player player = event.getEntity();
      CapabilityHooks.AetherPlayerHooks.login(player);
   }

   public static void onPlayerLogout(PlayerLoggedOutEvent event) {
      Player player = event.getEntity();
      CapabilityHooks.AetherPlayerHooks.logout(player);
   }

   public static void onPlayerJoinLevel(EntityJoinLevelEvent event) {
      Entity entity = event.getEntity();
      CapabilityHooks.AetherPlayerHooks.joinLevel(entity);
   }

   public static void onPlayerUpdate(Post event) {
      if (event.getEntity() instanceof LivingEntity livingEntity) {
         CapabilityHooks.AetherPlayerHooks.update(livingEntity);
      }
   }

   public static void onPlayerClone(Clone event) {
      CapabilityHooks.AetherPlayerHooks.clone(event.getEntity(), event.isWasDeath());
   }

   public static void onPlayerChangeDimension(PlayerChangedDimensionEvent event) {
      Player player = event.getEntity();
      CapabilityHooks.AetherPlayerHooks.changeDimension(player);
   }
}
