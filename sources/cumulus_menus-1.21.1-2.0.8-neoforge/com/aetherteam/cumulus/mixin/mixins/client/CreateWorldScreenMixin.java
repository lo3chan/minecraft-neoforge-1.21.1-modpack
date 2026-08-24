package com.aetherteam.cumulus.mixin.mixins.client;

import com.aetherteam.cumulus.Cumulus;
import com.aetherteam.cumulus.client.WorldDisplayHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.GenericMessageScreen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({CreateWorldScreen.class})
public class CreateWorldScreenMixin {
   @Inject(
      at = {@At("HEAD")},
      method = {"onCreate()V"}
   )
   private void onCreate(CallbackInfo ci) {
      if (WorldDisplayHelper.isActive()) {
         Minecraft minecraft = Minecraft.getInstance();
         WorldDisplayHelper.stopLevel(new GenericMessageScreen(Component.translatable("menu.savingLevel")));
         minecraft.managedBlock(() -> Cumulus.SERVER_INSTANCE == null);
         WorldDisplayHelper.resetSummary();
      }
   }
}
