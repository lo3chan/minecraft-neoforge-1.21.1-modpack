package com.aetherteam.cumulus.mixin.mixins.client;

import com.aetherteam.cumulus.client.WorldDisplayHelper;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({TitleScreen.class})
public class TitleScreenMixin {
   @ModifyReturnValue(
      at = {@At("RETURN")},
      method = {"isPauseScreen()Z"}
   )
   public boolean isPauseScreen(boolean original) {
      return original || WorldDisplayHelper.isActive();
   }
}
