package net.blay09.mods.balm.api.compat.trinkets;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface BalmModSupportTrinkets {
   boolean isEquipped(Player var1, Predicate<ItemStack> var2);

   ItemStack findEquipped(Player var1, Predicate<ItemStack> var2);

   List<ItemStack> findAllEquipped(Player var1, Predicate<ItemStack> var2);
}
