package com.alonie.brbe.mixins.accessors;

import net.minecraft.core.Holder.Reference;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({Reference.class})
public interface HolderReferenceAccessor<T> {
   @Accessor("key")
   ResourceKey<T> getKey();
}
