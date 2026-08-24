package com.aetherteam.cumulus.mixin.mixins.client;

import com.aetherteam.cumulus.client.WorldDisplayHelper;
import net.minecraft.client.gui.screens.GenericMessageScreen;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList.WorldListEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({WorldListEntry.class})
public class WorldListEntryMixin {
   @Final
   @Shadow
   LevelSummary summary;

   @Inject(
      at = {@At("HEAD")},
      method = {"doDeleteWorld()V"}
   )
   public void doDeleteWorld(CallbackInfo ci) {
      if (WorldDisplayHelper.isActive() && WorldDisplayHelper.sameSummaries(this.summary)) {
         WorldDisplayHelper.stopLevel(new ProgressScreen(true));
         WorldDisplayHelper.resetSummary();
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"editWorld()V"}
   )
   public void editWorld(CallbackInfo ci) {
      if (WorldDisplayHelper.isActive() && WorldDisplayHelper.sameSummaries(this.summary)) {
         WorldDisplayHelper.stopLevel(new GenericMessageScreen(Component.translatable("menu.savingLevel")));
      }
   }
}
