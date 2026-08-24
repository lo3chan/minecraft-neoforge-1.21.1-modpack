package com.aetherteam.cumulus.api;

import com.aetherteam.cumulus.CumulusConfig;
import com.aetherteam.cumulus.mixin.mixins.client.accessor.ScreenAccessor;
import com.aetherteam.cumulus.mixin.mixins.client.accessor.SplashRendererAccessor;
import com.aetherteam.cumulus.mixin.mixins.client.accessor.TitleScreenAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.sounds.Music;
import org.jetbrains.annotations.Nullable;

public class MenuHelper {
   @Nullable
   private Menu activeMenu = Menus.MINECRAFT;
   @Nullable
   private TitleScreen fallbackTitleScreen = null;
   @Nullable
   private String lastSplash = null;
   private boolean shouldFade = true;

   @Nullable
   public Menu getActiveMenu() {
      return this.activeMenu;
   }

   public void setActiveMenu(@Nullable Menu activeMenu) {
      this.activeMenu = activeMenu;
   }

   @Nullable
   public TitleScreen applyMenu(Menu menu) {
      if ((Boolean)CumulusConfig.CLIENT.enable_menu_api.get()) {
         this.setActiveMenu(menu);
         TitleScreen screen = this.checkFallbackScreen(menu, menu.screen());
         if (this.shouldFade()) {
            TitleScreenAccessor defaultMenuAccessor = (TitleScreenAccessor)screen;
            defaultMenuAccessor.cumulus$setFading(true);
            defaultMenuAccessor.cumulus$setFadeInStart(0L);
         }

         ScreenAccessor.cumulus$setCubeMap(menu.panorama());
         ScreenAccessor.cumulus$setPanorama(new PanoramaRenderer(menu.panorama()));
         if (this.getLastSplash() != null) {
            this.migrateSplash(this.getLastSplash(), screen);
         }

         menu.apply().run();
         return screen;
      } else {
         return this.getFallbackTitleScreen();
      }
   }

   private TitleScreen checkFallbackScreen(Menu menu, TitleScreen screen) {
      if ((screen.getClass() == TitleScreen.class || menu == Menus.MINECRAFT) && this.getFallbackTitleScreen() != null) {
         screen = this.getFallbackTitleScreen();
      }

      return screen;
   }

   public void clearActiveMenu() {
      this.activeMenu = Menus.MINECRAFT;
   }

   @Nullable
   public TitleScreen getActiveScreen() {
      return this.getActiveMenu() != null ? this.getActiveMenu().screen() : null;
   }

   @Nullable
   public Music getActiveMusic() {
      return this.getActiveMenu() != null ? this.getActiveMenu().music() : null;
   }

   @Nullable
   public TitleScreen getFallbackTitleScreen() {
      return this.fallbackTitleScreen;
   }

   public void setFallbackTitleScreen(@Nullable TitleScreen fallbackTitleScreen) {
      this.fallbackTitleScreen = fallbackTitleScreen;
   }

   @Nullable
   public String getLastSplash() {
      return this.lastSplash;
   }

   public void setLastSplash(@Nullable String lastSplash) {
      this.lastSplash = lastSplash;
   }

   public void migrateSplash(String originalSplash, TitleScreen newScreen) {
      TitleScreenAccessor newScreenAccessor = (TitleScreenAccessor)newScreen;
      if (newScreenAccessor.cumulus$getSplash() == null) {
         newScreenAccessor.setSplash(Minecraft.getInstance().getSplashManager().getSplash());
      }

      SplashRendererAccessor splashRendererAccessor = (SplashRendererAccessor)newScreenAccessor.cumulus$getSplash();
      splashRendererAccessor.cumulus$setSplash(originalSplash);
   }

   public void setCustomSplash(TitleScreen screen, Predicate<Calendar> condition, String splash) {
      TitleScreenAccessor screenAccessor = (TitleScreenAccessor)screen;
      SplashRendererAccessor splashRendererAccessor = (SplashRendererAccessor)screenAccessor.cumulus$getSplash();
      if (splashRendererAccessor != null) {
         Calendar calendar = Calendar.getInstance();
         calendar.setTime(new Date());
         if (condition.test(calendar)) {
            splashRendererAccessor.cumulus$setSplash(splash);
         }
      }
   }

   public boolean doesScreenMatchMenu(TitleScreen titleScreen) {
      boolean matches = false;

      for (Screen screen : Menus.getMenuScreens()) {
         if (titleScreen.getClass().equals(screen.getClass())) {
            matches = true;
            break;
         }
      }

      return matches;
   }

   public boolean shouldFade() {
      return this.shouldFade;
   }

   public void setShouldFade(boolean shouldFade) {
      this.shouldFade = shouldFade;
   }
}
