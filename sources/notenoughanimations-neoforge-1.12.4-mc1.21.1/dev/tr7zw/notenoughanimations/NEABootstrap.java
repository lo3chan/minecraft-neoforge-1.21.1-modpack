package dev.tr7zw.notenoughanimations;

import dev.tr7zw.transition.loader.ModLoaderEventUtil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod("notenoughanimations")
public class NEABootstrap {
   public NEABootstrap() {
      if (FMLEnvironment.dist == Dist.CLIENT) {
         ModLoaderEventUtil.registerClientSetupListener(() -> new NEAnimationsMod().onInitializeClient());
      }
   }
}
