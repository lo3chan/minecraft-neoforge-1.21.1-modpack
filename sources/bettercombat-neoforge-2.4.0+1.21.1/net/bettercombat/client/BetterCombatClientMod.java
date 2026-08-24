package net.bettercombat.client;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.bettercombat.client.compat.CompatibilityFlags;
import net.bettercombat.config.ClientConfig;
import net.bettercombat.config.ClientConfigWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManager;

public class BetterCombatClientMod {
   public static boolean ENABLED = false;
   public static ClientConfig config;

   public static void init() {
      AutoConfig.register(ClientConfigWrapper.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));
      config = ((ClientConfigWrapper)AutoConfig.getConfigHolder(ClientConfigWrapper.class).getConfig()).client;
      CompatibilityFlags.initialize();
   }

   public static void loadAnimation() {
      ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
   }
}
