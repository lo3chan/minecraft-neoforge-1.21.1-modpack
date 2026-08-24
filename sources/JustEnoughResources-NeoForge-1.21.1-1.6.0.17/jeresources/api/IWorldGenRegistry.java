package jeresources.api;

import jeresources.api.distributions.DistributionBase;
import jeresources.api.drop.LootDrop;
import jeresources.api.restrictions.Restriction;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface IWorldGenRegistry {
   void register(@NotNull ItemStack var1, @NotNull ItemStack var2, DistributionBase var3, Restriction var4, boolean var5, LootDrop... var6);

   void register(@NotNull ItemStack var1, DistributionBase var2, Restriction var3, boolean var4, LootDrop... var5);

   void register(@NotNull ItemStack var1, @NotNull ItemStack var2, DistributionBase var3, Restriction var4, LootDrop... var5);

   void register(@NotNull ItemStack var1, DistributionBase var2, Restriction var3, LootDrop... var4);

   void register(@NotNull ItemStack var1, @NotNull ItemStack var2, DistributionBase var3, boolean var4, LootDrop... var5);

   void register(@NotNull ItemStack var1, DistributionBase var2, boolean var3, LootDrop... var4);

   void register(@NotNull ItemStack var1, @NotNull ItemStack var2, DistributionBase var3, LootDrop... var4);

   void register(@NotNull ItemStack var1, DistributionBase var2, LootDrop... var3);

   void registerDrops(@NotNull ItemStack var1, LootDrop... var2);
}
