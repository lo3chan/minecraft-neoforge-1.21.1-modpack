package net.blay09.mods.balm.stats;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;

public interface BalmCustomStatRegistrar {
   default ResourceLocation register(String name) {
      return this.register(name, StatFormatter.DEFAULT);
   }

   ResourceLocation register(String var1, StatFormatter var2);

   ResourceLocation register(ResourceLocation var1, StatFormatter var2);
}
