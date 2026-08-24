package com.aetherteam.aether.client.event.listeners.abilities;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RenderArmEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent.Pre;

public class AccessoryAbilityClientListener {
   public static void listen(IEventBus bus) {
      bus.addListener(AccessoryAbilityClientListener::onRenderPlayer);
      bus.addListener(AccessoryAbilityClientListener::onRenderHand);
   }

   public static void onRenderPlayer(Pre event) {
      Player player = event.getEntity();
      if (!event.isCanceled() && ((AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER)).isWearingInvisibilityCloak()) {
         event.setCanceled(true);
      }
   }

   public static void onRenderHand(RenderArmEvent event) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (!event.isCanceled() && player != null && ((AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER)).isWearingInvisibilityCloak()) {
         event.setCanceled(true);
      }
   }
}
