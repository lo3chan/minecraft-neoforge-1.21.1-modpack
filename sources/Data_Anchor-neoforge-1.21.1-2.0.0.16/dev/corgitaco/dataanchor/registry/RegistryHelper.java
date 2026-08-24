package dev.corgitaco.dataanchor.registry;

import com.mojang.serialization.Codec;
import dev.corgitaco.dataanchor.util.ServiceUtil;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public interface RegistryHelper {
   RegistryHelper INSTANCE = ServiceUtil.load(RegistryHelper.class);

   <T> Supplier<T> register(Registry<T> var1, ResourceLocation var2, Supplier<T> var3);

   <T> Supplier<Registry<T>> createSimpleBuiltin(ResourceKey<Registry<T>> var1);

   <T> void registerDatapackRegistry(ResourceKey<Registry<T>> var1, Supplier<Codec<T>> var2);
}
