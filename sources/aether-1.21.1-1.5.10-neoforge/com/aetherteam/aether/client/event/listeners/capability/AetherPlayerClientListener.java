package com.aetherteam.aether.client.event.listeners.capability;

import com.aetherteam.aether.client.event.hooks.CapabilityClientHooks;
import net.minecraft.client.player.Input;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.client.event.InputEvent.Key;
import net.neoforged.neoforge.client.event.InputEvent.MouseButton.Post;

public class AetherPlayerClientListener {
   public static void listen(IEventBus bus) {
      bus.addListener(AetherPlayerClientListener::onMove);
      bus.addListener(AetherPlayerClientListener::onClick);
      bus.addListener(AetherPlayerClientListener::onPress);
   }

   public static void onMove(MovementInputUpdateEvent event) {
      Player player = event.getEntity();
      Input input = event.getInput();
      CapabilityClientHooks.AetherPlayerHooks.movementInput(player, input);
   }

   public static void onClick(Post event) {
      int button = event.getButton();
      CapabilityClientHooks.AetherPlayerHooks.mouseInput(button);
   }

   public static void onPress(Key event) {
      int key = event.getKey();
      CapabilityClientHooks.AetherPlayerHooks.keyInput(key);
   }
}
