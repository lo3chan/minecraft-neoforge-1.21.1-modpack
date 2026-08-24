package com.aetherteam.cumulus.mixin.mixins.client.accessor;

import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({TitleScreen.class})
public interface TitleScreenAccessor {
   @Accessor("splash")
   SplashRenderer cumulus$getSplash();

   @Accessor("splash")
   void setSplash(SplashRenderer var1);

   @Mutable
   @Accessor("fading")
   void cumulus$setFading(boolean var1);

   @Accessor("fadeInStart")
   void cumulus$setFadeInStart(long var1);
}
