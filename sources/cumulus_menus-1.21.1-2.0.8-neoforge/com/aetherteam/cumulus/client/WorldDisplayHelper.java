package com.aetherteam.cumulus.client;

import com.aetherteam.cumulus.Cumulus;
import com.aetherteam.cumulus.CumulusConfig;
import com.aetherteam.cumulus.mixin.mixins.client.accessor.MinecraftAccessor;
import com.aetherteam.cumulus.mixin.mixins.common.accessor.MinecraftServerAccessor;
import com.aetherteam.cumulus.network.packets.SetupLevelDisplayPacket;
import com.aetherteam.cumulus.platform.Services;
import com.mojang.blaze3d.systems.TimerQuery;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.SystemToast.SystemToastId;
import net.minecraft.client.gui.screens.GenericMessageScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import org.jetbrains.annotations.Nullable;

public class WorldDisplayHelper {
   public static boolean menuActive = false;
   @Nullable
   private static LevelSummary loadedSummary = null;
   public static final Runnable FAIL_RUN = () -> {
      resetActive();
      resetConfig();
   };

   public static void toggleWorldPreview() {
      if ((Boolean)CumulusConfig.CLIENT.enable_world_preview.get()) {
         enableWorldPreview();
      } else {
         disableWorldPreview();
      }
   }

   public static void enableWorldPreview() {
      Minecraft minecraft = Minecraft.getInstance();
      if (Cumulus.SERVER_INSTANCE != null && !menuActive) {
         Minecraft.getInstance()
            .getToasts()
            .addToast(
               new SystemToast(
                  SystemToastId.WORLD_ACCESS_FAILURE,
                  Component.translatable("cumulus_menus.world_preview.toast.title"),
                  Component.translatable("cumulus_menus.world_preview.toast.description")
               )
            );
         FAIL_RUN.run();
      } else if (minecraft.level == null) {
         loadLevel();
      }
   }

   public static void loadLevel() {
      Minecraft minecraft = Minecraft.getInstance();
      LevelSummary summary = getLevelSummary();
      if (summary != null && minecraft.getLevelSource().levelExists(summary.getLevelId())) {
         setActive();
         minecraft.forceSetScreen(new GenericMessageScreen(Component.translatable("selectWorld.data_read")));
         minecraft.createWorldOpenFlows().openWorld(summary.getLevelId(), FAIL_RUN);
      } else {
         FAIL_RUN.run();
      }
   }

   public static void enterLoadedLevel() {
      Minecraft minecraft = Minecraft.getInstance();
      LevelSummary summary = getLevelSummary();
      if (summary != null && minecraft.getLevelSource().levelExists(summary.getLevelId()) && minecraft.getSingleplayerServer() != null) {
         resetStates();
         minecraft.forceSetScreen(null);
      }
   }

   public static void disableWorldPreview() {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.level != null) {
         stopLevel(new GenericMessageScreen(Component.translatable("menu.savingLevel")));
         setMenu();
      }
   }

   public static void stopLevel(Screen screen) {
      resetStates();
      Minecraft minecraft = Minecraft.getInstance();
      IntegratedServer server = minecraft.getSingleplayerServer();
      if (minecraft.level != null) {
         if (server != null) {
            server.halt(false);
         }

         minecraft.disconnect(Objects.requireNonNullElse(screen, new GenericMessageScreen(Component.translatable("menu.savingLevel"))));
      }
   }

   public static void setMenu() {
      MinecraftAccessor minecraftAccessor = (MinecraftAccessor)Minecraft.getInstance();
      CumulusClient.MENU_HELPER.setShouldFade(false);
      Screen screen = CumulusClient.MENU_HELPER.applyMenu(CumulusClient.MENU_HELPER.getActiveMenu());
      if (screen != null) {
         if (minecraftAccessor.cumulus$getCurrentFrameProfile() != null && !minecraftAccessor.cumulus$getCurrentFrameProfile().isDone()) {
            TimerQuery.getInstance().ifPresent(timer -> minecraftAccessor.cumulus$setCurrentFrameProfile(timer.endProfile()));
         }

         Minecraft.getInstance().forceSetScreen(screen);
      }
   }

   @Nullable
   public static LevelSummary getLevelSummary() {
      if (loadedSummary == null) {
         if (Minecraft.getInstance().getSingleplayerServer() != null) {
            try {
               LevelStorageAccess source = ((MinecraftServerAccessor)Minecraft.getInstance().getSingleplayerServer()).cumulus$getStorageSource();
               loadedSummary = source.getSummary(source.getDataTag());
               return loadedSummary;
            } catch (IOException var1) {
               var1.printStackTrace();
            }
         }

         findLevelSummary();
      }

      return loadedSummary;
   }

   public static void findLevelSummary() {
      Minecraft minecraft = Minecraft.getInstance();
      LevelStorageSource source = minecraft.getLevelSource();

      try {
         List<LevelSummary> summaryList = new ArrayList<>((Collection<? extends LevelSummary>)source.loadLevelSummaries(source.findLevelCandidates()).get());
         Collections.sort(summaryList);
         if (!summaryList.isEmpty()) {
            LevelSummary summary = null;

            for (int i = summaryList.size() - 1; i >= 0; i--) {
               LevelSummary s = summaryList.get(i);
               if (!s.isLocked() && !s.isDisabled()) {
                  LevelStorageAccess access = source.createAccess(s.getLevelId());
                  summary = s;
                  access.close();
               }
            }

            if (summary != null) {
               loadedSummary = summary;
            }
         }
      } catch (InterruptedException | UnsupportedOperationException | IOException | ExecutionException var7) {
         resetActive();
         resetConfig();
         var7.printStackTrace();
      }
   }

   public static boolean sameSummaries(LevelSummary summary) {
      LevelSummary currentSummary = getLevelSummary();
      return currentSummary != null ? currentSummary.getLevelId().equals(summary.getLevelId()) : false;
   }

   public static void resetSummary() {
      loadedSummary = null;
   }

   public static void resetStates() {
      resetPlayerState();
      resetActive();
   }

   public static void resetPlayerState() {
      Minecraft.getInstance().options.hideGui = false;
      Minecraft.getInstance().options.setCameraType(CameraType.FIRST_PERSON);
   }

   public static void resetConfig() {
      CumulusConfig.CLIENT.enable_world_preview.set(false);
      CumulusConfig.CLIENT.enable_world_preview.save();
   }

   public static void resetActive() {
      menuActive = false;
   }

   public static void setActive() {
      menuActive = true;
   }

   public static boolean isActive() {
      return menuActive;
   }

   public static void setupLevelForDisplay() {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.hasSingleplayerServer()) {
         Services.PLATFORM.sendToServer(new SetupLevelDisplayPacket(), new CustomPacketPayload[0]);
      }
   }
}
