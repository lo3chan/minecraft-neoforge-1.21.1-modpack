package com.aetherteam.aether.mixin.mixins.client;

import com.aetherteam.aether.client.event.hooks.GuiHooks;
import com.aetherteam.aether.entity.AetherBossMob;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent.BossEventProgress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin({BossHealthOverlay.class})
public class BossHealthOverlayMixin {
   @ModifyVariable(
      at = @At("STORE"),
      method = {"render(Lnet/minecraft/client/gui/GuiGraphics;)V"},
      index = 7
   )
   private BossEventProgress event(BossEventProgress event) {
      if (Minecraft.getInstance().level != null
         && GuiHooks.BOSS_EVENTS.containsKey(event.getBossEvent().getId())
         && Minecraft.getInstance().level.getEntity(GuiHooks.BOSS_EVENTS.get(event.getBossEvent().getId())) instanceof AetherBossMob) {
         event.setCanceled(true);
      }

      return event;
   }
}
