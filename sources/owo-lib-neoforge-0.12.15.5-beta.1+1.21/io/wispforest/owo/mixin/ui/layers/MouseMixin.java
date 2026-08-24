package io.wispforest.owo.mixin.ui.layers;

import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({MouseHandler.class})
public class MouseMixin {
   @Shadow
   private int activeButton;
}
