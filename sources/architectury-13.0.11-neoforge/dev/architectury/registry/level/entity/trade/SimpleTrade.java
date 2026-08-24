package dev.architectury.registry.level.entity.trade;

import java.util.Optional;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import org.jetbrains.annotations.Nullable;

public record SimpleTrade(ItemCost primaryPrice, Optional<ItemCost> secondaryPrice, ItemStack sale, int maxTrades, int experiencePoints, float priceMultiplier)
   implements ItemListing {
   @Nullable
   public MerchantOffer getOffer(Entity entity, RandomSource random) {
      return new MerchantOffer(this.primaryPrice, this.secondaryPrice, this.sale, this.maxTrades, this.experiencePoints, this.priceMultiplier);
   }
}
