package net.blay09.mods.balm.world.item;

import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab.Builder;

public interface BalmCreativeModeTabRegistrar {
   default BalmCreativeModeTabRegistration register(String name, Function<Builder, Builder> builderConsumer) {
      return this.register(name, (id, builder) -> builderConsumer.apply(builder));
   }

   BalmCreativeModeTabRegistration register(String var1, BiFunction<ResourceLocation, Builder, Builder> var2);

   Builder createBuilder();
}
