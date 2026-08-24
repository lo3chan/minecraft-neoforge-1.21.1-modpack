package net.blay09.mods.balm.client.model.item;

import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

public interface BalmItemPropertyRegistrar {
   void register(ItemLike var1, ResourceLocation var2, ClampedItemPropertyFunction var3);

   void registerGeneric(ResourceLocation var1, ClampedItemPropertyFunction var2);
}
