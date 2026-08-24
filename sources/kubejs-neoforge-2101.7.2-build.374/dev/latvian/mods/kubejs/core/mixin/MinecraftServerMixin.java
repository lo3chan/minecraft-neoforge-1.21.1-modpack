package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.MinecraftServerKJS;
import dev.latvian.mods.kubejs.gui.chest.CustomChestMenu;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.plugin.builtin.event.ServerEvents;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.server.ScheduledServerEvent;
import dev.latvian.mods.kubejs.server.ServerKubeEvent;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import dev.latvian.mods.kubejs.util.AttachedData;
import dev.latvian.mods.kubejs.util.ScheduledEvents;
import dev.latvian.mods.rhino.util.RemapForJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess.Frozen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@RemapPrefixForJS("kjs$")
@Mixin({MinecraftServer.class})
public abstract class MinecraftServerMixin implements MinecraftServerKJS {
   @Unique
   private final CompoundTag kjs$persistentData = new CompoundTag();
   @Unique
   private ServerLevel kjs$overworld;
   @Unique
   private AttachedData<MinecraftServer> kjs$attachedData;
   @Unique
   private final Map<UUID, Map<Integer, ItemStack>> kjs$restoreInventories = new HashMap<>(1);

   @Shadow
   protected abstract boolean initServer() throws IOException;

   @Shadow
   public abstract void invalidateStatus();

   @Inject(
      method = {"<init>"},
      at = {@At("RETURN")}
   )
   private void kjs$init(CallbackInfo ci) {
      CompletableFuture.runAsync(() -> this.kjs$afterResourcesLoaded(false), this.kjs$self());
   }

   @Shadow
   public abstract Frozen registryAccess();

   @Override
   public CompoundTag kjs$getPersistentData() {
      return this.kjs$persistentData;
   }

   @Override
   public AttachedData<MinecraftServer> kjs$getData() {
      if (this.kjs$attachedData == null) {
         this.kjs$attachedData = new AttachedData<>(this.kjs$self());
         KubeJSPlugins.forEachPlugin(this.kjs$attachedData, KubeJSPlugin::attachServerData);
      }

      return this.kjs$attachedData;
   }

   @Override
   public ServerLevel kjs$getOverworld() {
      if (this.kjs$overworld == null) {
         this.kjs$overworld = this.kjs$self().overworld();
      }

      return this.kjs$overworld;
   }

   @Inject(
      method = {"tickServer"},
      at = {@At("RETURN")}
   )
   private void kjs$postTickServer(BooleanSupplier booleanSupplier, CallbackInfo ci) {
      ScheduledServerEvent.EVENTS.tickAll(this.kjs$getOverworld().getGameTime());
      if (!this.kjs$restoreInventories.isEmpty()) {
         for (ServerPlayer player : this.kjs$self().getPlayerList().getPlayers()) {
            Map<Integer, ItemStack> map = this.kjs$restoreInventories.get(player.getUUID());
            if (map != null && player.isAlive() && !player.hasDisconnected() && !(player.containerMenu instanceof CustomChestMenu)) {
               this.kjs$restoreInventories.remove(player.getUUID());
               NonNullList<ItemStack> playerItems = player.getInventory().items;

               for (int i = 0; i < playerItems.size(); i++) {
                  playerItems.set(i, map.getOrDefault(i, ItemStack.EMPTY));
               }
            }
         }
      }

      if (ServerEvents.TICK.hasListeners()) {
         ServerEvents.TICK.post(ScriptType.SERVER, new ServerKubeEvent(this.kjs$self()));
      }
   }

   @Override
   public ScheduledEvents kjs$getScheduledEvents() {
      return ScheduledServerEvent.EVENTS;
   }

   @Override
   public Map<UUID, Map<Integer, ItemStack>> kjs$restoreInventories() {
      return this.kjs$restoreInventories;
   }

   @Shadow
   @RemapForJS("isDedicated")
   public abstract boolean isDedicatedServer();

   @Shadow
   @RemapForJS("stop")
   public abstract void stopServer();

   @Inject(
      method = {"reloadResources"},
      at = {@At("TAIL")}
   )
   private void kjs$endResourceReload(Collection<String> collection, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
      CompletableFuture.runAsync(() -> this.kjs$afterResourcesLoaded(true), this.kjs$self());
   }

   @Redirect(
      method = {"lambda$reloadResources$29"},
      at = @At(
         value = "NEW",
         target = "(Lnet/minecraft/server/packs/PackType;Ljava/util/List;)Lnet/minecraft/server/packs/resources/MultiPackResourceManager;"
      )
   )
   private MultiPackResourceManager kjs$modifyResourceReload(PackType type, List<PackResources> original) {
      return new MultiPackResourceManager(type, ServerScriptManager.createPackResources(original));
   }
}
