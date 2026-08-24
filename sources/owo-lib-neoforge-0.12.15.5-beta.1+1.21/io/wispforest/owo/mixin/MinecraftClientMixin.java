package io.wispforest.owo.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(
   value = {Minecraft.class},
   priority = 0
)
public class MinecraftClientMixin {
}
