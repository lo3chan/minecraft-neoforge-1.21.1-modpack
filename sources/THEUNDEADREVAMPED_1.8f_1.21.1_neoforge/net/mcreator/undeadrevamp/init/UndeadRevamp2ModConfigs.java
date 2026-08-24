package net.mcreator.undeadrevamp.init;

import net.mcreator.undeadrevamp.configuration.MobsabilityConfiguration;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;

@EventBusSubscriber(
   modid = "undead_revamp2",
   bus = Bus.MOD
)
public class UndeadRevamp2ModConfigs {
   @SubscribeEvent
   public static void register(FMLConstructModEvent event) {
      event.enqueueWork(
         () -> ((ModContainer)ModList.get().getModContainerById("undead_revamp2").get())
            .registerConfig(Type.COMMON, MobsabilityConfiguration.SPEC, "Undead_mobs.toml")
      );
   }
}
