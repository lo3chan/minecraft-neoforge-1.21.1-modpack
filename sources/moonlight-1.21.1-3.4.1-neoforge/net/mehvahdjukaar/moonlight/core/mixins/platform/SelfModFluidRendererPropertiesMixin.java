package net.mehvahdjukaar.moonlight.core.mixins.platform;

import net.mehvahdjukaar.moonlight.api.client.ModFluidRenderProperties;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ModFluidRenderProperties.class})
public abstract class SelfModFluidRendererPropertiesMixin implements IClientFluidTypeExtensions {
}
