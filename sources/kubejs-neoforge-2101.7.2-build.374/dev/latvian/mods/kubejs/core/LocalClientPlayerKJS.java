package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.client.KubeSessionData;
import dev.latvian.mods.kubejs.client.NotificationToast;
import dev.latvian.mods.kubejs.net.SendDataFromClientPayload;
import dev.latvian.mods.kubejs.player.PlayerStatsJS;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.kubejs.typings.ThisIs;
import dev.latvian.mods.kubejs.util.NotificationToastData;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS("kjs$")
public interface LocalClientPlayerKJS extends ClientPlayerKJS {
   @HideFromJS
   default LocalPlayer kjs$self() {
      return (LocalPlayer)this;
   }

   default Minecraft kjs$getMinecraft() {
      return Minecraft.getInstance();
   }

   @Info(
      value = "Runs the specified console command client-side with the player's permission level.",
      params = {@Param(
         name = "command",
         value = "The console command. Slash at the beginning is optional."
      )}
   )
   @Override
   default void kjs$runCommand(String command) {
      this.kjs$self().connection.sendCommand(command);
   }

   @Info(
      value = "Runs the specified console command client-side with the player's permission level. The command won't output any logs in chat nor console.",
      params = {@Param(
         name = "command",
         value = "The console command. Slash at the beginning is optional."
      )}
   )
   @Override
   default void kjs$runCommandSilent(String command) {
      this.kjs$self().connection.sendCommand(command);
   }

   @ThisIs(
      classNames = {"net.minecraft.client.player.LocalPlayer"}
   )
   @Info("Checks, whether the entity is a reference to yourself - that is - the client player you are controlling.")
   @Override
   default boolean kjs$isSelf() {
      return true;
   }

   @Override
   default void kjs$sendData(String channel, @Nullable CompoundTag data) {
      if (!channel.isEmpty()) {
         PacketDistributor.sendToServer(new SendDataFromClientPayload(channel, data), new CustomPacketPayload[0]);
      }
   }

   @Override
   default PlayerStatsJS kjs$getStats() {
      return new PlayerStatsJS(this.kjs$self(), this.kjs$self().getStats());
   }

   @Info("Checks, whether the player is currently mining a block.")
   @Override
   default boolean kjs$isMiningBlock() {
      return Minecraft.getInstance().gameMode.isDestroying();
   }

   @Override
   default void kjs$notify(NotificationToastData notification) {
      Minecraft mc = Minecraft.getInstance();
      mc.getToasts().addToast(new NotificationToast(mc, notification));
   }

   @Override
   default void kjs$setActivePostShader(@Nullable ResourceLocation id) {
      KubeSessionData sessionData = KubeSessionData.of(this.kjs$self().connection);
      if (sessionData != null) {
         sessionData.activePostShader = id;
         Minecraft mc = this.kjs$getMinecraft();
         mc.gameRenderer.checkEntityPostEffect(mc.options.getCameraType().isFirstPerson() ? mc.getCameraEntity() : null);
      }
   }
}
