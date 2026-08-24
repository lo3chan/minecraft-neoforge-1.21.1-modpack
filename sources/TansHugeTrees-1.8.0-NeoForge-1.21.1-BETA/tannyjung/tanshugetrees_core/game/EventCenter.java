package tannyjung.tanshugetrees_core.game;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent.Render.Post;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.level.ChunkEvent.Load;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import tannyjung.tanshugetrees_core.Core;
import tannyjung.tanshugetrees_core.game.world_gen.WorldGenStepEnd;
import tannyjung.tanshugetrees_core.outside.CustomPackOrganizing;
import tannyjung.tanshugetrees_core.outside.TXTFunction;
import tannyjung.tanshugetrees_core.outside.TannyPackManager;
import tannyjung.tanshugetrees_handcode.systems.Commands;
import tannyjung.tanshugetrees_handcode.systems.Overlays;

public class EventCenter {
   @EventBusSubscriber({Dist.CLIENT})
   public static class Client {
      @SubscribeEvent(
         priority = EventPriority.NORMAL
      )
      public static void eventMenu(Post event) {
         Screen screen = event.getScreen();
         GuiGraphics graphic = event.getGuiGraphics();
         int screen_width = event.getScreen().width;
         int screen_height = event.getScreen().height;
         Overlays.eventMenu(screen, graphic, screen_width, screen_height);
      }

      @SubscribeEvent(
         priority = EventPriority.NORMAL
      )
      public static void eventInGame(net.neoforged.neoforge.client.event.RenderGuiEvent.Post event) {
         if (!Minecraft.getInstance().options.hideGui) {
            GuiGraphics graphic = event.getGuiGraphics();
            int screen_width = event.getGuiGraphics().guiWidth();
            int screen_height = event.getGuiGraphics().guiHeight();
            Overlays.eventInGame(graphic, screen_width, screen_height);
            if (Core.developer_mode) {
               OverlayMaker.createText(
                  graphic, screen_width, screen_height, "top-left", 8, 58, 0.75, false, "§9Delayed Command = " + TXTFunction.count_delayed_command
               );
            }
         }
      }
   }

   @EventBusSubscriber
   public static class Server {
      private static boolean first_player_joined = false;

      @SubscribeEvent
      public static void eventWorldAboutToStart(ServerAboutToStartEvent event) {
         String path_world = event.getServer().getWorldPath(new LevelResource(".")).toString();
         Core.path_world_core = path_world + "/data/tannyjung/" + Core.data_structure_version_core;
         Core.path_world_mod = path_world + "/data/" + Core.mod_id;
         Core.DataMigration.run(true);
         Core.restart(null, false, true);
      }

      @SubscribeEvent
      public static void eventWorldStarted(ServerStartedEvent event) {
         ServerLevel level_server = event.getServer().overworld();
         Core.restart(level_server, true, false);
      }

      @SubscribeEvent
      public static void eventWorldStopping(ServerStoppingEvent event) {
         first_player_joined = false;
      }

      @SubscribeEvent
      public static void eventChunkLoaded(Load event) {
         if (event.isNewChunk()) {
            LevelAccessor level_accessor = event.getLevel();
            ServerLevel level_server = (ServerLevel)level_accessor;
            String dimension = GameUtils.Space.getDimensionID(level_server).replace(":", "-");
            ChunkPos chunk_pos = event.getChunk().getPos();
            WorldGenStepEnd.start(dimension, chunk_pos);
         }
      }

      @SubscribeEvent
      public static void eventPlayerJoined(PlayerLoggedInEvent event) {
         Entity entity = event.getEntity();
         ServerLevel level_server = (ServerLevel)entity.level();
         if (!first_player_joined) {
            first_player_joined = true;
            Core.DelayedWork.create(true, 100, () -> {
               CustomPackOrganizing.Error.sendMessage(level_server);
               if (Core.auto_check_update) {
                  Core.thread_main.submit(() -> TannyPackManager.runCheckUpdate(level_server));
               }
            });
         }
      }

      @SubscribeEvent
      public static void eventRegisterCommand(RegisterCommandsEvent event) {
         CommandMaker.BuiltinCommands.registry(event);
         Commands.registry(event);
      }

      @SubscribeEvent
      public static void eventTickServer(net.neoforged.neoforge.event.tick.ServerTickEvent.Post event) {
         if (!Core.global_locking) {
            LevelAccessor level_accessor = event.getServer().overworld();
            ServerLevel level_server = event.getServer().overworld();
            Core.DelayedWork.runTick();
            Core.Loop.loopTick(level_accessor, level_server);
         }
      }
   }
}
