package dev.worldgen.lithostitched.mixin.common;

import net.minecraft.core.Holder.Reference;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({Reference.class})
public interface HolderReferenceAccessor<T> {
   @Accessor("value")
   void setValue(T var1);
}
