package dev.latvian.mods.kubejs.core.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.latvian.mods.kubejs.client.ClientAssetPacks;
import dev.latvian.mods.kubejs.client.ClientPlayerKubeEvent;
import dev.latvian.mods.kubejs.client.ClientProperties;
import dev.latvian.mods.kubejs.client.ScheduledClientEvent;
import dev.latvian.mods.kubejs.core.MinecraftClientKJS;
import dev.latvian.mods.kubejs.plugin.builtin.event.ClientEvents;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ScheduledEvents;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Minecraft.GameLoadCookie;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.packs.PackResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@RemapPrefixForJS("kjs$")
@Mixin({Minecraft.class})
public abstract class MinecraftClientMixin implements MinecraftClientKJS {
   @Shadow
   @Nullable
   public LocalPlayer player;

   @Inject(
      method = {"<init>"},
      at = {@At("RETURN")}
   )
   private void kjs$init(CallbackInfo ci) {
      CompletableFuture.runAsync(() -> this.kjs$afterResourcesLoaded(false), this.kjs$self());
   }

   @Inject(
      method = {"createTitle"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void kjs$createTitle(CallbackInfoReturnable<String> ci) {
      String s = ClientProperties.get().windowTitle;
      if (!s.isEmpty()) {
         ci.setReturnValue(s);
      }
   }

   @Shadow
   protected abstract String createTitle();

   @Override
   public String kjs$getTitle() {
      return this.createTitle();
   }

   @ModifyExpressionValue(
      method = {"reloadResourcePacks(ZLnet/minecraft/client/Minecraft$GameLoadCookie;)Ljava/util/concurrent/CompletableFuture;", "<init>"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/server/packs/repository/PackRepository;openAllSelected()Ljava/util/List;"
      )}
   )
   private List<PackResources> kjs$loadPacks(List<PackResources> resources) {
      return ClientAssetPacks.INSTANCE.inject(resources);
   }

   @Inject(
      method = {"startAttack"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/player/LocalPlayer;swing(Lnet/minecraft/world/InteractionHand;)V",
         shift = Shift.AFTER
      )}
   )
   private void kjs$startAttack(CallbackInfoReturnable<Boolean> cir) {
      this.kjs$startAttack0();
   }

   @Inject(
      method = {"handleKeybinds"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/Minecraft;startUseItem()V",
         ordinal = 0,
         shift = Shift.AFTER
      )}
   )
   private void kjs$startUseItem(CallbackInfo ci) {
      this.kjs$startUseItem0();
   }

   @Inject(
      method = {"tick"},
      at = {@At("RETURN")}
   )
   private void kjs$postTickClient(CallbackInfo ci) {
      if (this.kjs$self().level != null && this.kjs$self().player != null) {
         ScheduledClientEvent.EVENTS.tickAll(this.kjs$self().level.getGameTime());
         if (ClientEvents.TICK.hasListeners()) {
            try {
               ClientEvents.TICK.post(ScriptType.CLIENT, new ClientPlayerKubeEvent(this.player));
            } catch (IllegalStateException var3) {
            }
         }
      }
   }

   @Override
   public ScheduledEvents kjs$getScheduledEvents() {
      return ScheduledClientEvent.EVENTS;
   }

   @Inject(
      method = {"reloadResourcePacks(ZLnet/minecraft/client/Minecraft$GameLoadCookie;)Ljava/util/concurrent/CompletableFuture;"},
      at = {@At("TAIL")}
   )
   private void kjs$endResourceReload(boolean bl, GameLoadCookie gameLoadCookie, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
      CompletableFuture.runAsync(() -> this.kjs$afterResourcesLoaded(true), this.kjs$self());
   }
}
