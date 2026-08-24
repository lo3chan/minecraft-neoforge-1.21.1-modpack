package traben.entity_model_features;

import net.minecraft.client.gui.screens.Screen;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import traben.entity_texture_features.ETF;

@Mod("entity_model_features")
public class EMFInit {
   public EMFInit() {
      if (FMLEnvironment.dist.isClient()) {
         try {
            ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> this::createScreen);
         } catch (NoClassDefFoundError var2) {
            System.out.println("[Entity Model Features]: Mod config broken, download latest neoforge version");
         }

         EMF.init();
      }
   }

   Screen createScreen(ModContainer arg, Screen arg2) {
      return ETF.getConfigScreen(arg2);
   }
}
