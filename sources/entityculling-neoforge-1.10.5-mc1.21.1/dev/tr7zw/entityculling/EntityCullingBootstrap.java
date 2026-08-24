package dev.tr7zw.entityculling;

import dev.tr7zw.transition.loader.ModLoaderEventUtil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod("entityculling")
public class EntityCullingBootstrap {
   public EntityCullingBootstrap() {
      if (FMLEnvironment.dist == Dist.CLIENT) {
         ModLoaderEventUtil.registerClientSetupListener(() -> new EntityCullingMod().onInitialize());
      }
   }
}
