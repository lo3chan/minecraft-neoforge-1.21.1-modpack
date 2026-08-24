package dev.latvian.mods.kubejs.server;

import dev.latvian.mods.kubejs.command.CommandRegistryKubeEvent;
import dev.latvian.mods.kubejs.command.KubeJSCommands;
import dev.latvian.mods.kubejs.gui.chest.CustomChestMenu;
import dev.latvian.mods.kubejs.level.SimpleLevelKubeEvent;
import dev.latvian.mods.kubejs.plugin.builtin.event.LevelEvents;
import dev.latvian.mods.kubejs.plugin.builtin.event.ServerEvents;
import dev.latvian.mods.kubejs.script.PlatformWrapper;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.kubejs.web.LocalWebServer;
import dev.latvian.mods.kubejs.web.WebServerProperties;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.CommandEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent.Pre;
import net.neoforged.neoforge.event.level.LevelEvent.Load;
import net.neoforged.neoforge.event.level.LevelEvent.Save;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

@EventBusSubscriber(
   modid = "kubejs"
)
public class KubeJSServerEventHandler {
   private static final LevelResource PERSISTENT_DATA = new LevelResource("kubejs_persistent_data.nbt");

   @SubscribeEvent
   public static void registerCommands(RegisterCommandsEvent event) {
      KubeJSCommands.register(event.getDispatcher());
      if (ServerEvents.COMMAND_REGISTRY.hasListeners()) {
         ServerEvents.COMMAND_REGISTRY
            .post(ScriptType.SERVER, new CommandRegistryKubeEvent(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection()));
      }
   }

   @SubscribeEvent
   public static void serverBeforeStart(ServerAboutToStartEvent event) {
      MinecraftServer server = event.getServer();
      if (FMLEnvironment.dist == Dist.DEDICATED_SERVER
         && !PlatformWrapper.isGeneratingData()
         && WebServerProperties.get().enabled
         && !WebServerProperties.get().publicAddress.isEmpty()) {
         LocalWebServer.start(server, false);
      }

      Path p = server.getWorldPath(PERSISTENT_DATA);
      if (Files.exists(p)) {
         try {
            CompoundTag tag = NbtIo.readCompressed(p, NbtAccounter.unlimitedHeap());
            if (tag != null) {
               CompoundTag t = tag.getCompound("__restore_inventories");
               if (!t.isEmpty()) {
                  tag.remove("__restore_inventories");
                  Map<UUID, Map<Integer, ItemStack>> playerMap = server.kjs$restoreInventories();

                  for (String key : t.getAllKeys()) {
                     ListTag list = t.getList(key, 10);
                     Map<Integer, ItemStack> map = playerMap.computeIfAbsent(UUID.fromString(key), k -> new HashMap<>());

                     for (Tag tag2 : list) {
                        short slot = ((CompoundTag)tag2).getShort("Slot");
                        Optional<ItemStack> stack = ItemStack.parse(server.registryAccess(), tag2);
                        stack.ifPresent(itemStack -> map.put(Integer.valueOf(slot), itemStack));
                     }
                  }
               }

               server.kjs$getPersistentData().merge(tag);
            }
         } catch (Exception var14) {
            var14.printStackTrace();
         }
      }
   }

   @SubscribeEvent
   public static void serverStarting(ServerStartingEvent event) {
      ServerEvents.LOADED.post(ScriptType.SERVER, new ServerKubeEvent(event.getServer()));
   }

   @SubscribeEvent
   public static void serverStopping(ServerStoppingEvent event) {
      ServerEvents.UNLOADED.post(ScriptType.SERVER, new ServerKubeEvent(event.getServer()));
   }

   @SubscribeEvent
   public static void serverStopped(ServerStoppedEvent event) {
      RegistryAccessContainer.current = RegistryAccessContainer.BUILTIN;
   }

   @SubscribeEvent
   public static void serverLevelLoaded(Load event) {
      if (event.getLevel() instanceof ServerLevel level && LevelEvents.LOADED.hasListeners(level.dimension())) {
         LevelEvents.LOADED.post(new SimpleLevelKubeEvent(level), level.dimension());
      }
   }

   @SubscribeEvent
   public static void serverLevelSaved(Save event) {
      if (event.getLevel() instanceof ServerLevel level && LevelEvents.SAVED.hasListeners(level.dimension())) {
         LevelEvents.SAVED.post(new SimpleLevelKubeEvent(level), level.dimension());
      }

      if (event.getLevel() instanceof ServerLevel level && level.dimension() == Level.OVERWORLD) {
         CompoundTag serverData = level.getServer().kjs$getPersistentData().copy();
         Path p = level.getServer().getWorldPath(PERSISTENT_DATA);
         Map<UUID, Map<Integer, ItemStack>> playerMap = level.getServer().kjs$restoreInventories();
         if (!playerMap.isEmpty()) {
            CompoundTag nbt = new CompoundTag();

            for (Entry<UUID, Map<Integer, ItemStack>> entry : playerMap.entrySet()) {
               ListTag list = new ListTag();

               for (Entry<Integer, ItemStack> entry2 : entry.getValue().entrySet()) {
                  CompoundTag tag = new CompoundTag();
                  tag.putShort("Slot", entry2.getKey().shortValue());
                  entry2.getValue().save(level.registryAccess(), tag);
                  list.add(tag);
               }

               nbt.put(entry.getKey().toString(), list);
            }

            serverData.put("__restore_inventories", nbt);
         }

         Util.ioPool().execute(() -> {
            try {
               NbtIo.writeCompressed(serverData, p);
            } catch (Exception var3x) {
               var3x.printStackTrace();
            }
         });
      }
   }

   @SubscribeEvent
   public static void command(CommandEvent event) {
      if (ServerEvents.COMMAND.hasListeners()) {
         CommandKubeEvent e = new CommandKubeEvent(event);
         if (ServerEvents.COMMAND.hasListeners(e.getCommandName())) {
            ServerEvents.COMMAND.post(e, e.getCommandName()).applyCancel(event);
         }
      }
   }

   @SubscribeEvent
   public static void addReloadListeners(AddReloadListenerEvent event) {
      event.addListener(new KubeJSReloadListener(event.getServerResources()));
   }

   @SubscribeEvent
   public static void preventPickupDuringChestGUI(Pre event) {
      if (event.getPlayer() instanceof ServerPlayer player && player.isAlive() && !player.hasDisconnected() && player.containerMenu instanceof CustomChestMenu) {
         event.setCanPickup(TriState.FALSE);
      }
   }
}
