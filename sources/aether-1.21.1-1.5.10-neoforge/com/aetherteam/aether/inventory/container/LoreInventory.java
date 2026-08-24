package com.aetherteam.aether.inventory.container;

import com.aetherteam.aether.advancement.AetherAdvancementTriggers;
import com.aetherteam.aether.advancement.LoreTrigger;
import com.aetherteam.aether.inventory.menu.LoreBookMenu;
import com.aetherteam.aether.network.packet.serverbound.LoreExistsPacket;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public class LoreInventory extends SimpleContainer {
   public final Player player;
   public LoreBookMenu menu;

   public LoreInventory(Player player) {
      super(1);
      this.player = player;
   }

   public void setItem(int index, ItemStack stack) {
      if (!stack.isEmpty()) {
         if (!this.player.level().isClientSide() || !(this.player instanceof LocalPlayer)) {
            if (this.player instanceof ServerPlayer serverPlayer && this.menu.getLoreEntryExists()) {
               ((LoreTrigger)AetherAdvancementTriggers.LORE_ENTRY.get()).trigger(serverPlayer, stack);
            }
         } else if (this.menu.loreEntryKeyExists(stack)) {
            PacketDistributor.sendToServer(new LoreExistsPacket(this.player.getId(), stack, true), new CustomPacketPayload[0]);
         } else {
            PacketDistributor.sendToServer(new LoreExistsPacket(this.player.getId(), stack, false), new CustomPacketPayload[0]);
         }
      }

      super.setItem(index, stack);
   }

   public void setMenu(LoreBookMenu menu) {
      this.menu = menu;
   }
}
