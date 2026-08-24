package dev.architectury.neoforge;

import dev.architectury.event.EventHandler;
import dev.architectury.networking.SpawnEntityPacket;
import dev.architectury.registry.level.biome.forge.BiomeModificationsImpl;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import net.neoforged.fml.common.Mod;

@Mod("architectury")
public class ArchitecturyNeoForge {
   public ArchitecturyNeoForge() {
      EventHandler.init();
      BiomeModificationsImpl.init();
      EnvExecutor.runInEnv(Env.CLIENT, () -> SpawnEntityPacket.Client::register);
      EnvExecutor.runInEnv(Env.SERVER, () -> SpawnEntityPacket::register);
   }
}
