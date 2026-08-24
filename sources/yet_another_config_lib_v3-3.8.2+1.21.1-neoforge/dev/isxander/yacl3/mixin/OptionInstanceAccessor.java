package dev.isxander.yacl3.mixin;

import net.minecraft.client.OptionInstance;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Internal
@Mixin({OptionInstance.class})
public interface OptionInstanceAccessor<T> {
   @Accessor
   T getInitialValue();
}
