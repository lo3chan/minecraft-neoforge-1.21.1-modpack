package net.blay09.mods.balm.neoforge.stats;

import java.util.ArrayList;
import java.util.List;
import net.blay09.mods.balm.api.stats.BalmStats;
import net.blay09.mods.balm.common.NamespaceResolver;
import net.blay09.mods.balm.neoforge.DeferredRegisters;
import net.blay09.mods.balm.neoforge.ModBusEventRegisters;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

public record NeoForgeBalmStats(NamespaceResolver namespaceResolver) implements BalmStats {
   @Override
   public void registerCustomStat(ResourceLocation identifier) {
      DeferredRegister<ResourceLocation> register = DeferredRegisters.get(Registries.CUSTOM_STAT, identifier.getNamespace());
      register.register(identifier.getPath(), () -> identifier);
      this.getActiveRegistrations().customStats.add(identifier);
   }

   private NeoForgeBalmStats.Registrations getActiveRegistrations() {
      return ModBusEventRegisters.getRegistrations(this.namespaceResolver.getDefaultNamespace(), NeoForgeBalmStats.Registrations.class);
   }

   public static class Registrations {
      public final List<ResourceLocation> customStats = new ArrayList<>();

      @SubscribeEvent
      public void commonSetup(FMLCommonSetupEvent event) {
         event.enqueueWork(() -> this.customStats.forEach(it -> Stats.CUSTOM.get(it, StatFormatter.DEFAULT)));
      }
   }
}
