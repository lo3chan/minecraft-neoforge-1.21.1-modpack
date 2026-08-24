package net.diebuddies.mixins.ocean;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({LightTexture.class})
public interface MixinLightTextureAccessor {
   @Accessor("lightTextureLocation")
   ResourceLocation getLightTextureLocation();
}
