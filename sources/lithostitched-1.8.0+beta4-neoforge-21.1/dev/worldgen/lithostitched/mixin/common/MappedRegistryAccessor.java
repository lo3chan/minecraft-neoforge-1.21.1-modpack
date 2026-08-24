package dev.worldgen.lithostitched.mixin.common;

import java.util.Map;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Holder.Reference;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({MappedRegistry.class})
public interface MappedRegistryAccessor<T> {
   @Accessor("registrationInfos")
   @Mutable
   Map<ResourceKey<T>, RegistrationInfo> lithostitched$getRegistrationInfos();

   @Accessor("byValue")
   Map<T, Reference<T>> getByValue();
}
