package net.irisshaders.iris.mixin.forge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(
   targets = {"com/portingdeadmods/cable_facades/events/GameClientEvents$2"},
   remap = true
)
public class MixinGameClientEvents {
}
