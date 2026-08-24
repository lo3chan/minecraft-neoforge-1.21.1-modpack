package net.irisshaders.iris.mixin.statelisteners;

import com.mojang.blaze3d.platform.GlStateManager.BooleanState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({BooleanState.class})
public interface BooleanStateAccessor {
   @Accessor("enabled")
   boolean isEnabled();
}
