package me.flashyreese.mods.sodiumextra.mixin.cloud;

import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.DimensionSpecialEffects.OverworldEffects;
import net.minecraft.client.renderer.DimensionSpecialEffects.SkyType;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({OverworldEffects.class})
public abstract class MixinDimensionEffectsOverworld extends DimensionSpecialEffects {
   public MixinDimensionEffectsOverworld(float cloudsHeight, boolean alternateSkyColor, SkyType skyType, boolean brightenLighting, boolean darkened) {
      super(cloudsHeight, alternateSkyColor, skyType, brightenLighting, darkened);
   }

   public float getCloudHeight() {
      return SodiumExtraClientMod.options().extraSettings.cloudHeightOverride
         ? SodiumExtraClientMod.options().extraSettings.cloudHeight
         : super.getCloudHeight();
   }
}
