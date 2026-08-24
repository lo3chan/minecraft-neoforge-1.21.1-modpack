package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.net.KubeJSNet;
import dev.latvian.mods.kubejs.net.SendDataFromServerPayload;
import dev.latvian.mods.kubejs.player.EntityArrayList;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.server.ChangesForChat;
import dev.latvian.mods.kubejs.server.DataExport;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS("kjs$")
public interface MinecraftServerKJS extends WithAttachedData<MinecraftServer>, WithPersistentData, DataSenderKJS, MinecraftEnvironmentKJS, EntityCollectionKJS {
   default MinecraftServer kjs$self() {
      return (MinecraftServer)this;
   }

   ServerLevel kjs$getOverworld();

   @Override
   default Component kjs$getName() {
      return Component.literal(this.kjs$self().name());
   }

   @Override
   default void kjs$tell(Component message) {
      this.kjs$self().sendSystemMessage(message);

      for (ServerPlayer player : this.kjs$self().getPlayerList().getPlayers()) {
         player.kjs$tell(message);
      }
   }

   @Override
   default void kjs$setStatusMessage(Component message) {
      for (ServerPlayer player : this.kjs$self().getPlayerList().getPlayers()) {
         player.kjs$setStatusMessage(message);
      }
   }

   @Info(
      value = "Runs the specified console command.",
      params = {@Param(
         name = "command",
         value = "The console command. Slash at the beginning is optional."
      )}
   )
   @Override
   default void kjs$runCommand(String command) {
      this.kjs$self().getCommands().performPrefixedCommand(this.kjs$self().createCommandSourceStack(), command);
   }

   @Info(
      value = "Runs the specified console command. The command won't output any logs in chat nor console.",
      params = {@Param(
         name = "command",
         value = "The console command. Slash at the beginning is optional."
      )}
   )
   @Override
   default void kjs$runCommandSilent(String command) {
      this.kjs$self().getCommands().performPrefixedCommand(this.kjs$self().createCommandSourceStack().withSuppressedOutput(), command);
   }

   @Override
   default void kjs$setActivePostShader(@Nullable ResourceLocation id) {
      for (ServerPlayer player : this.kjs$self().getPlayerList().getPlayers()) {
         player.kjs$setActivePostShader(id);
      }
   }

   default ServerLevel kjs$getLevel(ResourceLocation dimension) {
      return this.kjs$self().getLevel(ResourceKey.create(Registries.DIMENSION, dimension));
   }

   @Nullable
   default ServerPlayer kjs$getPlayer(PlayerSelector selector) {
      return selector.getPlayer(this.kjs$self());
   }

   @Override
   default Iterable<? extends Entity> kjs$getMcEntities() {
      ArrayList<Entity> list = new ArrayList<>(10);

      for (ServerLevel level : this.kjs$self().getAllLevels()) {
         Iterable<Entity> i = level.getAllEntities();
         if (i instanceof Collection<Entity> c) {
            list.addAll(c);
         } else {
            for (Entity e : i) {
               list.add(e);
            }
         }
      }

      return list;
   }

   @Override
   default List<? extends Player> kjs$getMcPlayers() {
      return this.kjs$self().getPlayerList().getPlayers();
   }

   @Override
   default EntityArrayList kjs$getPlayers() {
      return new EntityArrayList(this.kjs$self().getPlayerList().getPlayers());
   }

   @Nullable
   @Override
   default Entity kjs$getEntityByUUID(UUID id) {
      for (ServerLevel level : this.kjs$self().getAllLevels()) {
         Entity e = (Entity)level.getEntities().get(id);
         if (e != null) {
            return e;
         }
      }

      return null;
   }

   @Nullable
   @Override
   default Entity kjs$getEntityByNetworkID(int id) {
      for (ServerLevel level : this.kjs$self().getAllLevels()) {
         Entity e = (Entity)level.getEntities().get(id);
         if (e != null) {
            return e;
         }
      }

      return null;
   }

   @Nullable
   default AdvancementNode kjs$getAdvancement(ResourceLocation id) {
      return this.kjs$self().getAdvancements().tree().get(id);
   }

   @Override
   default void kjs$sendData(String channel, @Nullable CompoundTag data) {
      KubeJSNet.sendToAllPlayers(new SendDataFromServerPayload(channel, data));
   }

   @HideFromJS
   default void kjs$afterResourcesLoaded(boolean reload) {
      if (reload) {
         DataExport.exportData();
      }

      ConsoleJS.SERVER.stopCapturingErrors();
      if (reload && CommonProperties.get().announceReload && !CommonProperties.get().hideServerScriptErrors) {
         if (ConsoleJS.SERVER.errors.isEmpty()) {
            this.kjs$tell(Component.literal("Reloaded with no KubeJS errors!").withStyle(ChatFormatting.GREEN));
         } else {
            this.kjs$tell(ConsoleJS.SERVER.errorsComponent("/kubejs errors server"));
         }

         if (DevProperties.get().logChangesInChat) {
            ChangesForChat.print(this::kjs$tell);
         }
      }

      ConsoleJS.SERVER.info("Server resource reload complete!");
   }

   default Map<UUID, Map<Integer, ItemStack>> kjs$restoreInventories() {
      throw new NoMixinException();
   }
}
