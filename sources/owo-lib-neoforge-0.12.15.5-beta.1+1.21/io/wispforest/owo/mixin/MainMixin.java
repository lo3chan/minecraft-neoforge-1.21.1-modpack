package io.wispforest.owo.mixin;

import net.minecraft.server.Main;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(
   value = {Main.class},
   priority = 0
)
public class MainMixin {
}
