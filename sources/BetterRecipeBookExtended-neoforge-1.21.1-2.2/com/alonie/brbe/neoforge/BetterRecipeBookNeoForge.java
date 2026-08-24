package com.alonie.brbe.neoforge;

import com.alonie.brbe.BetterRecipeBook;
import com.alonie.brbe.brewingstand.neoforge.PlatformPotionUtilImpl;
import com.alonie.brbe.config.Config;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod("brbe")
public final class BetterRecipeBookNeoForge {
   public BetterRecipeBookNeoForge(IEventBus modEventBus) {
      PlatformPotionUtilImpl.init();
      BetterRecipeBook.init();
      if (FMLEnvironment.dist == Dist.CLIENT) {
         BetterRecipeBookClientNeoForge.init(modEventBus);
         ModContainer container = (ModContainer)ModList.get().getModContainerById("brbe").get();
         IConfigScreenFactory factory = (modContainer, parent) -> (Screen)AutoConfig.getConfigScreen(Config.class, parent).get();
         container.registerExtensionPoint(IConfigScreenFactory.class, factory);
      }
   }
}
