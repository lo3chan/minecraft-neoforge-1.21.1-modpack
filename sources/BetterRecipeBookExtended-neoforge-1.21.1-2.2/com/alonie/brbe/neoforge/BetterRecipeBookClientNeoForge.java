package com.alonie.brbe.neoforge;

import com.alonie.brbe.BetterRecipeBook;
import com.alonie.brbe.brewingstand.neoforge.PlatformPotionUtilImpl;
import com.alonie.brbe.compat.OverlayHider;
import com.alonie.brbe.compat.emi.EmiCompat;
import com.alonie.brbe.compat.rei.ReiCompat;
import com.alonie.brbe.impl.hud.EmiHudHider;
import com.alonie.brbe.impl.hud.JeiHudHider;
import com.alonie.brbe.impl.hud.ReiHudHider;
import com.alonie.brbe.loaders.PotionLoader;
import com.alonie.brbe.util.TopLayerOverlayRenderer;
import com.alonie.recipebookispain_extended.RecipeBookIsPain;
import com.alonie.recipebookispain_extended.neoforge.NeoForgePlatform;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ScreenEvent.Init.Post;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent.Load;
import net.neoforged.neoforge.event.level.LevelEvent.Unload;

public class BetterRecipeBookClientNeoForge {
   private static final Set<Screen> registeredScreens = Collections.newSetFromMap(new WeakHashMap<>());

   public static void init(IEventBus modEventBus) {
      modEventBus.addListener(RegisterKeyMappingsEvent.class, event -> event.register(BetterRecipeBook.PIN_MAPPING));
      PlatformPotionUtilImpl.init();
      NeoForge.EVENT_BUS.addListener(Load.class, event -> {
         if (event.getLevel().isClientSide() && event.getLevel() instanceof ClientLevel clientLevel) {
            PotionLoader.load(clientLevel);
         }
      });
      NeoForge.EVENT_BUS.addListener(Unload.class, event -> {
         if (event.getLevel().isClientSide()) {
            PotionLoader.clear();
         }
      });
      OverlayHider.register(new JeiHudHider());
      OverlayHider.register(new ReiHudHider());
      OverlayHider.register(new EmiHudHider());
      RecipeBookIsPain.PLATFORM = new NeoForgePlatform();
      RecipeBookIsPain.isOwOLoaded = RecipeBookIsPain.PLATFORM.isModLoaded("owo");
      RecipeBookIsPain.LOGGER.info("[RBIP] NeoForge platform initialized");
      ReiCompat.register();
      EmiCompat.register();
      RecipeBookIsPain.ensureInitialized();
      RecipeBookIsPain.LOGGER.info(RecipeBookIsPain.diagnostic());
      NeoForge.EVENT_BUS.addListener(Post.class, event -> {
         Screen screen = event.getScreen();
         if (screen != null) {
            registeredScreens.remove(screen);
            OverlayHider.setOverlaysHidden(BetterRecipeBook.config.hideReiJeiOverlay);
         }
      });
      NeoForge.EVENT_BUS
         .addListener(
            net.neoforged.neoforge.client.event.ClientTickEvent.Post.class,
            event -> {
               Minecraft client = Minecraft.getInstance();
               Screen screen = client.screen;
               if (BetterRecipeBook.config.hideReiJeiOverlay && screen != null) {
                  OverlayHider.ensureJeiOverlayHidden();
               }

               if (screen != null && !registeredScreens.contains(screen) && TopLayerOverlayRenderer.hasOverlay(screen)) {
                  registeredScreens.add(screen);
                  NeoForge.EVENT_BUS
                     .addListener(
                        net.neoforged.neoforge.client.event.ScreenEvent.Render.Post.class,
                        renderEvent -> {
                           if (renderEvent.getScreen() == screen) {
                              TopLayerOverlayRenderer.render(
                                 screen, renderEvent.getGuiGraphics(), renderEvent.getMouseX(), renderEvent.getMouseY(), renderEvent.getPartialTick()
                              );
                           }
                        }
                     );
               }
            }
         );
   }
}
