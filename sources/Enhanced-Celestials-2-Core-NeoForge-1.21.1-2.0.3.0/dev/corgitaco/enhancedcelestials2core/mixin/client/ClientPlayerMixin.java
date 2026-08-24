package dev.corgitaco.enhancedcelestials2core.mixin.client;

import dev.corgitaco.enhancedcelestials2core.client.LunarSoundHandler;
import java.util.List;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AmbientSoundHandler;
import net.minecraft.stats.StatsCounter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LocalPlayer.class})
public class ClientPlayerMixin {
   @Shadow
   @Final
   private List<AmbientSoundHandler> ambientSoundHandlers;

   @Inject(
      method = {"<init>"},
      at = {@At("RETURN")}
   )
   private void addLunarSoundHandler(
      Minecraft mc,
      ClientLevel world,
      ClientPacketListener connection,
      StatsCounter stats,
      ClientRecipeBook recipeBook,
      boolean clientSneakState,
      boolean clientSprintState,
      CallbackInfo ci
   ) {
      this.ambientSoundHandlers.add(new LunarSoundHandler());
   }
}
