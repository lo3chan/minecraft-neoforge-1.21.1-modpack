package io.wispforest.owo.mixin.registry;

import net.minecraft.core.Holder.Reference;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({Reference.class})
public interface ReferenceAccessor<T> {
   @Invoker("bindKey")
   void owo$setRegistryKey(ResourceKey<T> var1);

   @Invoker("bindValue")
   void owo$setValue(T var1);
}
