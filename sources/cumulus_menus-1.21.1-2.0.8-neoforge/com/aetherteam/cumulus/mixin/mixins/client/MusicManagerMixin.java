package com.aetherteam.cumulus.mixin.mixins.client;

import com.aetherteam.cumulus.client.CumulusClient;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({MusicManager.class})
public class MusicManagerMixin {
   @WrapOperation(
      method = {"tick()V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/Minecraft;getSituationalMusic()Lnet/minecraft/sounds/Music;"
      )}
   )
   public Music injected(Minecraft instance, Operation<Music> original) {
      Music music = (Music)original.call(new Object[]{instance});
      return music == Musics.MENU && CumulusClient.MENU_HELPER.getActiveMusic() != null ? CumulusClient.MENU_HELPER.getActiveMusic() : music;
   }
}
