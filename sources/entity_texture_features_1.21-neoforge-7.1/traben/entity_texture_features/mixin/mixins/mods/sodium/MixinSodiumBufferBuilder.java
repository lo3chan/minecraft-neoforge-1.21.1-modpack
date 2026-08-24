package traben.entity_texture_features.mixin.mixins.mods.sodium;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import traben.entity_texture_features.mixin.CancelTarget;

@Pseudo
@Mixin({CancelTarget.class})
public class MixinSodiumBufferBuilder {
}
