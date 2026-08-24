package fuzs.puzzleslib.neoforge.impl;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.impl.PuzzlesLibMod;
import fuzs.puzzleslib.impl.content.PuzzlesLibDevelopment;
import fuzs.puzzleslib.impl.core.context.ModConstructorImpl;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import net.neoforged.fml.common.Mod;

@Mod("puzzleslib")
public class PuzzlesLibNeoForge {
   public PuzzlesLibNeoForge() {
      ModConstructor.construct("puzzleslib", PuzzlesLibMod::new);
      if (ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironmentWithoutDataGeneration("puzzleslib")) {
         ModConstructorImpl.construct(PuzzlesLibMod.id("common/development"), PuzzlesLibDevelopment::new, ProxyImpl.get()::getModConstructorImpl);
      }
   }
}
