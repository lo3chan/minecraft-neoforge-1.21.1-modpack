package dev.tr7zw.waveycapes;

import dev.tr7zw.transition.loader.ModLoaderEventUtil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod("waveycapes")
public class WaveyCapesBootstrap {
   public WaveyCapesBootstrap() {
      if (FMLEnvironment.dist == Dist.CLIENT) {
         ModLoaderEventUtil.registerClientSetupListener(() -> new WaveyCapesMod().init());
      }
   }
}
