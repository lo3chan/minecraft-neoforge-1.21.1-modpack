package net.bettercombat;

import com.mojang.logging.LogUtils;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.bettercombat.api.client.AttackRangeExtensions;
import net.bettercombat.client.particle.TrailParticles;
import net.bettercombat.compat.CompatFeatures;
import net.bettercombat.config.FallbackConfig;
import net.bettercombat.config.ServerConfig;
import net.bettercombat.config.ServerConfigWrapper;
import net.bettercombat.config.TrailConfig;
import net.bettercombat.logic.WeaponAttributesFallback;
import net.bettercombat.logic.WeaponRegistry;
import net.minecraft.server.MinecraftServer;
import net.tiny_config.ConfigManager;
import org.slf4j.Logger;

public class BetterCombatMod {
   public static final Logger LOGGER = LogUtils.getLogger();
   public static final String ID = "bettercombat";
   public static ServerConfig config;
   private static FallbackConfig fallbackDefault = FallbackConfig.createDefault();
   public static ConfigManager<FallbackConfig> fallbackConfig = new ConfigManager("fallback_compatibility", fallbackDefault)
      .builder()
      .setDirectory("bettercombat")
      .sanitize(true)
      .build();
   public static ConfigManager<TrailConfig> trailConfig = new ConfigManager("weapon_trails", TrailParticles.defaults())
      .builder()
      .setDirectory("bettercombat")
      .sanitize(true)
      .build();

   public static void init() {
      trailConfig.refresh();
      AutoConfig.register(ServerConfigWrapper.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));
      config = ((ServerConfigWrapper)AutoConfig.getConfigHolder(ServerConfigWrapper.class).getConfig()).server;
      loadFallbackConfig();
      AttackRangeExtensions.register(context -> {
         float multiplier = config.getAttackRangeMultiplierForScale(context.player().getScale());
         return new AttackRangeExtensions.Modifier(multiplier, AttackRangeExtensions.Operation.MULTIPLY);
      });
      CompatFeatures.init();
   }

   public static ServerConfig getConfig() {
      return config;
   }

   private static void loadFallbackConfig() {
      fallbackConfig.load();
      if (fallbackConfig.value == null) {
         fallbackConfig.value = FallbackConfig.createDefault();
      }

      if (((FallbackConfig)fallbackConfig.value).schema_version < fallbackDefault.schema_version) {
         fallbackConfig.value = FallbackConfig.migrate((FallbackConfig)fallbackConfig.value, FallbackConfig.createDefault());
      }

      fallbackConfig.save();
   }

   public static void loadWeaponAttributes(MinecraftServer server) {
      WeaponRegistry.loadAttributes(server.getResourceManager());
      if (config.fallback_compatibility_enabled) {
         WeaponAttributesFallback.initialize();
      }

      WeaponRegistry.encodeRegistry();
   }
}
