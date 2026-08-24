package com.aetherteam.cumulus.mixin.mixins.client;

import com.aetherteam.cumulus.CumulusConfig;
import com.aetherteam.cumulus.client.WorldDisplayHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({PauseScreen.class})
public class PauseScreenMixin {
   @Inject(
      at = {@At("HEAD")},
      method = {"onDisconnect()V"},
      cancellable = true
   )
   public void onDisconnectWorldPreview(CallbackInfo ci) {
      if ((Boolean)CumulusConfig.CLIENT.enable_world_preview.get() && Minecraft.getInstance().getSingleplayerServer() != null) {
         WorldDisplayHelper.setActive();
         WorldDisplayHelper.setupLevelForDisplay();
         Player player = Minecraft.getInstance().player;
         if (player != null) {
            player.setXRot(0.0F);
         }

         ci.cancel();
      }
   }
}
