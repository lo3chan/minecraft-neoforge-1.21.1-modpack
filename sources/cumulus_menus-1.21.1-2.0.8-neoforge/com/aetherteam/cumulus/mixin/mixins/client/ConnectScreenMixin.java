package com.aetherteam.cumulus.mixin.mixins.client;

import com.aetherteam.cumulus.client.WorldDisplayHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.GenericMessageScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.TransferState;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ConnectScreen.class})
public class ConnectScreenMixin {
   @Inject(
      at = {@At("HEAD")},
      method = {"startConnecting(Lnet/minecraft/client/gui/screens/Screen;Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/multiplayer/resolver/ServerAddress;Lnet/minecraft/client/multiplayer/ServerData;ZLnet/minecraft/client/multiplayer/TransferState;)V"}
   )
   private static void startConnecting(
      Screen screen, Minecraft minecraft, ServerAddress serverAddress, ServerData serverData, boolean isQuickPlay, TransferState transferState, CallbackInfo ci
   ) {
      if (WorldDisplayHelper.isActive()) {
         WorldDisplayHelper.stopLevel(new GenericMessageScreen(Component.translatable("menu.savingLevel")));
      }
   }
}
