package com.aetherteam.cumulus.client.event.hooks;

import com.aetherteam.cumulus.CumulusConfig;
import com.aetherteam.cumulus.api.MenuHelper;
import com.aetherteam.cumulus.api.Menus;
import com.aetherteam.cumulus.client.CumulusClient;
import com.aetherteam.cumulus.client.WorldDisplayHelper;
import com.aetherteam.cumulus.client.gui.screen.DynamicMenuButton;
import com.aetherteam.cumulus.client.gui.screen.MenuSelectionScreen;
import com.aetherteam.cumulus.mixin.mixins.client.accessor.SplashRendererAccessor;
import com.aetherteam.cumulus.mixin.mixins.client.accessor.TitleScreenAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Button.Builder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.Nullable;

public class MenuHooks {
   public static void setLastSplash(Screen screen, MenuHelper menuHelper) {
      if (screen instanceof TitleScreen titleScreen) {
         TitleScreenAccessor screenAccessor = (TitleScreenAccessor)titleScreen;
         SplashRendererAccessor splashRendererAccessor = (SplashRendererAccessor)screenAccessor.cumulus$getSplash();
         if (splashRendererAccessor != null) {
            menuHelper.setLastSplash(splashRendererAccessor.cumulus$getSplash());
         }
      }
   }

   public static void trackFallbacks(Screen screen) {
      if (screen instanceof TitleScreen titleScreen) {
         if ((Boolean)CumulusConfig.CLIENT.enable_menu_api.get()) {
            if (!CumulusClient.MENU_HELPER.doesScreenMatchMenu(titleScreen) || screen.getClass() == TitleScreen.class) {
               CumulusClient.MENU_HELPER.setFallbackTitleScreen(titleScreen);
            }
         } else if (screen.getClass() == TitleScreen.class) {
            CumulusClient.MENU_HELPER.setFallbackTitleScreen(titleScreen);
         }
      }
   }

   @Nullable
   public static Screen setupCustomMenu(Screen screen, MenuHelper menuHelper) {
      return screen instanceof TitleScreen && CumulusConfig.CLIENT.enable_menu_api.get()
         ? menuHelper.applyMenu(Menus.get(ResourceLocation.parse((String)CumulusConfig.CLIENT.active_menu.get())))
         : null;
   }

   public static void resetFade(MenuHelper menuHelper) {
      menuHelper.setShouldFade(false);
   }

   @Nullable
   public static Button setupMenuScreenButton(Screen screen) {
      return CumulusConfig.CLIENT.enable_menu_api.get() && CumulusConfig.CLIENT.enable_menu_list_button.get() && screen instanceof TitleScreen
         ? Button.builder(
               Component.translatable("gui.cumulus_menus.button.menu_list"), pressed -> Minecraft.getInstance().setScreen(new MenuSelectionScreen(screen))
            )
            .bounds(screen.width - 62, 4, 58, 20)
            .build()
         : null;
   }

   @Nullable
   public static Button setupToggleWorldButton(Screen screen) {
      if (screen instanceof TitleScreen) {
         DynamicMenuButton dynamicMenuButton = new DynamicMenuButton(
            new Builder(Component.translatable("gui.cumulus_menus.menu.button.world_preview"), pressed -> {
               CumulusConfig.CLIENT.enable_world_preview.set(!(Boolean)CumulusConfig.CLIENT.enable_world_preview.get());
               CumulusConfig.CLIENT.enable_world_preview.save();
               WorldDisplayHelper.toggleWorldPreview();
            }).bounds(screen.width - 24 - getButtonOffset(), 4, 20, 20).tooltip(Tooltip.create(Component.translatable("gui.cumulus_menus.menu.preview")))
         );
         dynamicMenuButton.setDisplayConfigs(CumulusConfig.CLIENT.enable_world_preview_button);
         return dynamicMenuButton;
      } else {
         return null;
      }
   }

   @Nullable
   public static Button setupQuickLoadButton(Screen screen) {
      if (screen instanceof TitleScreen) {
         DynamicMenuButton dynamicMenuButton = new DynamicMenuButton(
            new Builder(Component.translatable("gui.cumulus_menus.menu.button.quick_load"), pressed -> {
               WorldDisplayHelper.enterLoadedLevel();
               Minecraft.getInstance().getMusicManager().stopPlaying();
               Minecraft.getInstance().getSoundManager().stop();
               Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }).bounds(screen.width - 24 - getButtonOffset(), 4, 20, 20).tooltip(Tooltip.create(Component.translatable("gui.cumulus_menus.menu.load")))
         );
         dynamicMenuButton.setOffsetConfigs(CumulusConfig.CLIENT.enable_world_preview_button);
         dynamicMenuButton.setDisplayConfigs(CumulusConfig.CLIENT.enable_world_preview, CumulusConfig.CLIENT.enable_quick_load_button);
         return dynamicMenuButton;
      } else {
         return null;
      }
   }

   private static int getButtonOffset() {
      return CumulusConfig.CLIENT.enable_menu_api.get() && CumulusConfig.CLIENT.enable_menu_list_button.get() ? 62 : 0;
   }
}
