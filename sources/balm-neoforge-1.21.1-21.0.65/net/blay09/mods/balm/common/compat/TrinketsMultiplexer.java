package net.blay09.mods.balm.common.compat;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import net.blay09.mods.balm.api.compat.trinkets.BalmModSupportTrinkets;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class TrinketsMultiplexer implements BalmModSupportTrinkets {
   private final List<BalmModSupportTrinkets> providers;

   public TrinketsMultiplexer(List<BalmModSupportTrinkets> providers) {
      this.providers = providers;
   }

   @Override
   public boolean isEquipped(Player player, Predicate<ItemStack> predicate) {
      return this.providers.stream().anyMatch(provider -> provider.isEquipped(player, predicate));
   }

   @Override
   public ItemStack findEquipped(Player player, Predicate<ItemStack> predicate) {
      return this.providers
         .stream()
         .map(provider -> provider.findEquipped(player, predicate))
         .filter(stack -> !stack.isEmpty())
         .findFirst()
         .orElse(ItemStack.EMPTY);
   }

   @Override
   public List<ItemStack> findAllEquipped(Player player, Predicate<ItemStack> predicate) {
      return this.providers.stream().map(provider -> provider.findAllEquipped(player, predicate)).flatMap(Collection::stream).toList();
   }
}
