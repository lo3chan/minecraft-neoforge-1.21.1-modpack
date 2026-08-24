package dev.shadowsoffire.placebo.mixin.client;

import dev.shadowsoffire.placebo.util.DrawsOnLeft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(
   value = {AbstractContainerScreen.class},
   remap = false
)
public class AbstractContainerScreenMixin implements DrawsOnLeft {
}
