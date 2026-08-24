package net.bettercombat.neoforge;

import net.bettercombat.BetterCombatMod;
import net.bettercombat.mixin.player.PlayerEntityAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.entity.living.LivingSwapItemsEvent.Hands;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

@EventBusSubscriber(
   modid = "bettercombat",
   bus = Bus.GAME
)
public class NeoForgeEvents {
   @SubscribeEvent
   public static void register(ServerAboutToStartEvent event) {
      BetterCombatMod.loadWeaponAttributes(event.getServer());
   }

   @SubscribeEvent
   public static void onHandSwap(Hands event) {
      if (event.getEntity() instanceof Player player) {
         ItemStack offHandStack = (ItemStack)((PlayerEntityAccessor)player).getInventory().offhand.get(0);
         event.setItemSwappedToOffHand(player.getMainHandItem());
         event.setItemSwappedToMainHand(offHandStack);
      }
   }
}
