package fuzs.puzzleslib.impl.content.client;

import com.mojang.blaze3d.platform.InputConstants;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.event.v1.gui.AddToastCallback;
import fuzs.puzzleslib.api.client.event.v1.gui.ScreenEvents;
import fuzs.puzzleslib.api.client.event.v1.gui.ScreenMouseEvents;
import fuzs.puzzleslib.api.client.event.v1.gui.ScreenOpeningCallback;
import fuzs.puzzleslib.api.client.gui.v2.screen.ScreenSkipper;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.impl.content.ServerPropertiesHelper;
import java.util.Objects;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.SystemToast.SystemToastId;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState.SelectedGameMode;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;

public class PuzzlesLibClientDevelopment implements ClientModConstructor {
   @Override
   public void onConstructMod() {
      registerEventHandlers();
      if (ModLoaderEnvironment.INSTANCE.getModLoader().isForgeLike()) {
         setupGameOptions(Minecraft.getInstance().options);
      }
   }

   private static void registerEventHandlers() {
      ScreenOpeningCallback.EVENT.register((oldScreen, newScreen) -> {
         if (newScreen.get() instanceof TitleScreen screen) {
            screen.fading = false;
         } else if (newScreen.get() instanceof CreateWorldScreen screen) {
            screen.getUiState().setGameMode(SelectedGameMode.CREATIVE);
            screen.getUiState().setAllowCommands(true);
         }

         return EventResult.PASS;
      });
      ScreenEvents.beforeInit(TitleScreen.class).register((minecraft, screen, screenWidth, screenHeight, widgets) -> {
         if (minecraft.getOverlay() instanceof LoadingOverlay loadingOverlay && loadingOverlay.fadeOutStart != 0L) {
            loadingOverlay.fadeOutStart = 0L;
         }
      });
      AddToastCallback.EVENT
         .register(
            (toastManager, toast) -> toast instanceof SystemToast systemToast && systemToast.getToken() == SystemToastId.UNSECURE_SERVER_WARNING
               ? EventResult.INTERRUPT
               : EventResult.PASS
         );
      ScreenMouseEvents.beforeMouseClick(Screen.class).register((screen, mouseX, mouseY, button) -> {
         for (GuiEventListener guiEventListener : screen.children()) {
            if (guiEventListener instanceof EditBox && guiEventListener.mouseClicked(mouseX, mouseY, button)) {
               screen.setFocused(guiEventListener);
               if (button == 0) {
                  screen.setDragging(true);
               }

               return EventResult.INTERRUPT;
            }
         }

         return EventResult.PASS;
      });
      ScreenMouseEvents.beforeMouseRelease(Screen.class)
         .register(
            (screen, mouseX, mouseY, button) -> {
               screen.setDragging(false);
               return screen.getChildAt(mouseX, mouseY)
                     .filter(EditBox.class::isInstance)
                     .filter(guiEventListener -> guiEventListener.mouseReleased(mouseX, mouseY, button))
                     .isPresent()
                  ? EventResult.INTERRUPT
                  : EventResult.PASS;
            }
         );
      ScreenMouseEvents.beforeMouseDrag(Screen.class)
         .register(
            (screen, mouseX, mouseY, button, dragX, dragY) -> screen.getFocused() instanceof EditBox
                  && screen.isDragging()
                  && button == 0
                  && screen.getFocused().mouseDragged(mouseX, mouseY, button, dragX, dragY)
               ? EventResult.INTERRUPT
               : EventResult.PASS
         );
   }

   @Override
   public void onClientSetup() {
      CreativeModeInventoryScreen.selectedTab = (CreativeModeTab)BuiltInRegistries.CREATIVE_MODE_TAB.getOrThrow(CreativeModeTabs.SEARCH);
      initializeScreenSkipper();
   }

   private static void initializeScreenSkipper() {
      ScreenSkipper.create().setTitleComponent("selectWorld.backupQuestion.experimental").setButtonComponent("selectWorld.backupJoinSkipButton").build();
      ScreenSkipper.create().setTitleComponent("selectWorld.warning.experimental.title").setButtonComponent(CommonComponents.GUI_YES).build();
      ScreenSkipper.create().setTitleComponent("controls.title").setButtonComponent("controls.keybinds").setLastTitleComponent("options.title").build();
   }

   public static void setupGameOptions(Options options) {
      Minecraft minecraft = Minecraft.getInstance();
      Objects.requireNonNull(minecraft, "minecraft is null");
      boolean running = minecraft.running;
      minecraft.running = false;
      initializeGameOptions(options);
      minecraft.running = running;
   }

   public static void initializeGameOptions(Options options) {
      boolean optionsStillMissing = !options.getFile().exists();
      unbindKey(options.keyLoadHotbarActivator, optionsStillMissing);
      unbindKey(options.keySaveHotbarActivator, optionsStillMissing);
      unbindKey(options.keyCommand, optionsStillMissing);
      unbindKey(options.keySocialInteractions, optionsStillMissing);
      unbindKey(options.keyAdvancements, optionsStillMissing);
      unbindKey(options.keyFullscreen, optionsStillMissing);
      if (optionsStillMissing) {
         options.renderDistance().set(16);
         options.framerateLimit().set(60);
         options.narratorHotkey().set(false);
         options.advancedItemTooltips = true;
         options.tutorialStep = TutorialSteps.NONE;
         options.joinedFirstServer = true;
         options.operatorItemsTab().set(true);
         options.realmsNotifications().set(false);
         options.showSubtitles().set(true);
         options.guiScale().set(8);
         options.onboardAccessibility = false;
         options.skipMultiplayerWarning = true;
         options.damageTiltStrength().set(0.0);
         options.lastMpIp = ServerPropertiesHelper.getHostAddress().map(string -> string + ":25565").orElse("");
      }
   }

   private static void unbindKey(KeyMapping keyMapping, boolean optionsStillMissing) {
      keyMapping.defaultKey = InputConstants.UNKNOWN;
      if (optionsStillMissing) {
         keyMapping.setKey(InputConstants.UNKNOWN);
      }
   }
}
