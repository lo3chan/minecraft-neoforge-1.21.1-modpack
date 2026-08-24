package traben.entity_texture_features;

import net.minecraft.client.gui.screens.Screen;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod("entity_texture_features")
public class ETFInit {
   public ETFInit() {
      if (FMLEnvironment.dist.isClient()) {
         try {
            ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> this::createScreen);
         } catch (NoClassDefFoundError var2) {
            System.out.println("[Entity Texture Features]: Mod config broken, download latest neoforge version");
         }

         ETF.start();
      }
   }

   Screen createScreen(ModContainer arg, Screen arg2) {
      return ETF.getConfigScreen(arg2);
   }
}
