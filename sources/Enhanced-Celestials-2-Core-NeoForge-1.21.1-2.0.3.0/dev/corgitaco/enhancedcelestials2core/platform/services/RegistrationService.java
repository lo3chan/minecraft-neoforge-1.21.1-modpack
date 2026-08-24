package dev.corgitaco.enhancedcelestials2core.platform.services;

import com.mojang.serialization.Codec;
import dev.corgitaco.enhancedcelestials2core.platform.Services;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public interface RegistrationService {
   RegistrationService INSTANCE = Services.load(RegistrationService.class);

   <T> Registry<T> createSimpleBuiltin(ResourceKey<Registry<T>> var1);

   <T> Supplier<T> register(Registry<T> var1, String var2, String var3, Supplier<T> var4);

   <T> void registerDatapackRegistry(ResourceKey<Registry<T>> var1, Supplier<Codec<T>> var2);
}
