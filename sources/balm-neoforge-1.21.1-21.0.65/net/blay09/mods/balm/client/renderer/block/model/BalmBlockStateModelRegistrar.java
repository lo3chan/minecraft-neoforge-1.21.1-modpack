package net.blay09.mods.balm.client.renderer.block.model;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;

public interface BalmBlockStateModelRegistrar {
   DeferredBlockStateModel register(ResourceLocation var1);

   default <T> Map<T, DeferredBlockStateModel> registerDiscriminated(T[] values, Function<T, ResourceLocation> identifierFunction) {
      return this.registerDiscriminated(Set.of(values), identifierFunction);
   }

   <T> Map<T, DeferredBlockStateModel> registerDiscriminated(Set<T> var1, Function<T, ResourceLocation> var2);
}
